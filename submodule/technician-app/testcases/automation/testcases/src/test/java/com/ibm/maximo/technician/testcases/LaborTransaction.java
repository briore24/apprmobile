package com.ibm.maximo.technician.testcases;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.google.gson.Gson;
import com.ibm.maximo.automation.mobile.AbstractAutomationFramework;
import com.ibm.maximo.automation.mobile.FrameworkFactory;
import com.ibm.maximo.automation.mobile.api.MaximoApi;
import com.ibm.maximo.automation.mobile.api.json.Task;
import com.ibm.maximo.automation.mobile.api.json.WorkOrder;
import com.ibm.maximo.components.TextInputComponent;
import com.ibm.maximo.components.TimeInputComponent;
import com.ibm.maximo.technician.framework.TechnicianTest;
import com.ibm.maximo.technician.page.CreateWorkOrderPage;
import com.ibm.maximo.technician.page.MySchedulePage;
import com.ibm.maximo.technician.page.ReportWorkPage;
import com.ibm.maximo.technician.page.TaskPage;
import com.ibm.maximo.technician.page.WorkOrderDetailsPage;
import com.ibm.maximo.technician.setupdata.SetupData;
import com.ibm.maximo.technician.setupdata.SetupData.*;

/*
 * GRAPHITE-51062: Report work - Add Labor transaction
 * GRAPHITE-64951: Add/Edit Labor Transaction: 30M,1TA,1A
 * MAXMOA-5972: Method to verify labor values exists
 * GRAPHITE-78401: check person name (it should not be person id) in labor details of report page
 */
public class LaborTransaction extends TechnicianTest {
	private final Logger logger = LoggerFactory.getLogger(LaborTransaction.class);
	private AbstractAutomationFramework af;
	private TestSuite testSuite;
	private MaximoApi maximoApi;
	private String woNum, timezone, labor, header, reportHeader, todayDate1, todayDate2, masServer;
	private WorkOrder newWorkOrder;
	private String laborHeaderText = "Labor";
	private String taskPageHeaderText = "Task";
	private int startHrs, startMin;
	private String regularHours = "0", regularMins = "1", startTime, endTime;
	private String laborName, craft, skill, laborType1;
	private static final String taskTitleOne = "Task-1 For Mobile Automation";
	private static final String taskTitleTwo = "Task-2 For Mobile Automation";
	private int startYear = 2024, startMonth = 1, startDate = 1;
	private int taskIdOne = 1;
	private int taskIdTwo = 2;
	private String chevronId="bam2r_items_datalistWrapper";
	private boolean apiCodeSuccess = false;
	private String headerText="Save or discard changes?";

	@BeforeClass(alwaysRun = true)
	@Parameters({ "configPath" })
	public void setup(String configPath) throws Exception {
		logger.info("********************LaborTransaction*********************************");
		this.af = FrameworkFactory.get();
		Properties properties = new Properties();
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(configPath));
			properties.load(in);
			timezone = properties.getProperty("capabilities.browserstack.timezone") != null ? properties.getProperty("capabilities.browserstack.timezone") : "UTC";
			labor = properties.getProperty("system.username");
			maximoApi = new MaximoApi();
			maximoApi.setMaximoServer(properties.getProperty("system.maximoServerUrl"),
					properties.getProperty("system.maximoAPIKey"));
			masServer = properties.getProperty("system.masServer");
			// timezone is set in default information of user
			defaultInformationOfUserTimeZone(timezone, labor);
			// EAM for user - wilson , FVT for user - username9 from MobileSetup4MAS.data
			skill = SetupData.SKILL;
			laborName = SetupData.LABORNAME;
			// EAM env
			if (masServer.equals("false")) {
				// sandbox
				craft = "Electrician";
				laborType1 = "Waiting for materials";

			} else {
				// FVT SetupData , IVT might be same as sandbox or different
				craft = "Mobile Automation Craft";
				laborType1 = "Waiting for materials";
			}
			createDefaultObjects();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!apiCodeSuccess) {
			logger.info("stopped framework and quit as API failed = " + apiCodeSuccess);
			FrameworkFactory.stopAll();
		} else {
			login(af);
			if (masServer.equals("true")) {
				// Data will update for IVT
				updateData();
			}
		}
	}

	@AfterClass(alwaysRun = true)
	public void teardown() throws Exception {
		logOut(af);
		// timezone is blank in default information of user
		defaultInformationOfUserTimeZone("",labor);
		// Change WO status to Completed
		logger.info("Changing work order status to COMP");
		maximoApi.changeStatus(newWorkOrder, WoStatus.COMP.toString());
		if (testSuite != null) {
			testSuite.teardown();
		}
	}


	@Test(groups = {
			"mobile" }, description = "Verify labor transaction functionality on Report work page", timeOut = 8000000)
	public void laborTransaction() throws Exception {
		// Search the work order
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		assignedWorkPage.search(woNum);

		// Navigate to WO details page
		assignedWorkPage.openWorkOrderDetails();

		// Start the work order
		WorkOrderDetailsPage woDetails = new WorkOrderDetailsPage(af);
		woDetails.clickStartWorkButton();

		// Navigate to Report work page
		woDetails.clickReportWorkButton();

		// Verify confirm dialog is not displayed when technician clicks on "X" button
		// without making any changes on labor sliding drawer
		ReportWorkPage reportWork = new ReportWorkPage(af);
		reportWork.clickPlusLaborButton();
		reportWork.closeLaborDrawer();

		// Verify Report work page header value
		header = woDetails.getTitle(woDetails.reportWorkPageTitle);
		reportHeader = woNum + " Report work";
		assertEquals(header, reportHeader);
		logger.info("Report work Page header verified");

		reportWork.clickPlusLaborButton();
		logger.info("+ Button clicked");

		SimpleDateFormat formatDate = new SimpleDateFormat("MM/dd/yyyy HH:mm z");
		// "SimpleDateFormat" class initialize with object
		// "formatDate" this class acceptes the format of
		// date and time as ""MM/dd/yyyy" and "HH:mm:ss z""
		// "z" use for print the time zone
		Date date = new Date();
		// initialize "Date" class
		formatDate.setTimeZone(TimeZone.getTimeZone(timezone));
		// converting to UTC or format the Date as UTC
		String myDate = formatDate.format(date);
		// print formatted date and time
		// Output : 11/20/2020 12:42 IST
		logger.info("myDate : " + myDate);
		// getting the current year
		String[] currentDate = myDate.split("/");
		String[] StrYear = currentDate[2].split(" ");
		String currentYear = StrYear[0];
		logger.info("currentYear : " + currentYear);
		// Getting the current day
		String currentDay = currentDate[1];
		logger.info("currentDay : " + currentDay);
		// Getting the current month
		String currentMonth = (currentDate[0].startsWith("0"))? (currentDate[0].substring(1,2)):currentDate[0] ;
		String _24HourTime = StrYear[1];
		logger.info("_24HourTime : " + _24HourTime);
		SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
		SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
		Date _24HourDt = _24HourSDF.parse(_24HourTime);

		String time = _12HourSDF.format(_24HourDt); // 10:15 PM
		logger.info("time : " + time);
		String[] currentTimeSplit = time.split(":");
		String currentHr = ((currentTimeSplit[0]).charAt(0) == '0')
				? (currentTimeSplit[0]).substring(1, (currentTimeSplit[0]).length())
				: currentTimeSplit[0];
		logger.info("currentHr : " + currentHr);
		String currentAMOrPM = (currentTimeSplit[1]).substring((currentTimeSplit[1]).length() - 2);
		logger.info("currentAMOrPM : " + currentAMOrPM);
		boolean AMOrPM = (currentAMOrPM.equalsIgnoreCase("AM"))? true : false ;
		startHrs =  Integer.parseInt(currentHr);
		startMin = Integer.parseInt(currentHr);
		String StrstartMin = (startMin >= 0 && startMin <= 9)? String.format("%02d", startMin): Integer.toString(startMin);
		startTime = currentHr+":"+StrstartMin+" "+currentAMOrPM;
		
		int totalMin = startMin+Integer.parseInt(regularMins);
		String expectedMins = (totalMin >= 0 && totalMin <= 9)? String.format("%02d", totalMin):
			((totalMin<60)? Integer.toString(totalMin): "00" );
		String Hr = (expectedMins == "00")? Integer.toString(startHrs+1) : currentHr;
		endTime = Hr+":"+expectedMins+" "+currentAMOrPM;
		
		// Date in 'MM/DD/YY' format
		todayDate1 = currentMonth + "/" + currentDay + "/" + currentYear.substring(2,4);
		logger.info("Start date on add labor drawer: " + todayDate1);

		// Verify Start date on add labor drawer is an auto-filled value
		TextInputComponent startDateAdd = af.instantiateComponent(TextInputComponent.class, "startDate");
		assertEquals(startDateAdd.getContents(),todayDate1);

		// Enter labor start time on add labor drawer
		reportWork.startTime(startHrs, startMin, AMOrPM); // setTime true means AM and false means PM per 
		logger.info("Time added");

		// Enter labor hours on add labor drawer
		reportWork.regularHours(regularHours, regularMins);
		logger.info("hours added");

		// Verify if two values of Start date and time, Hours, End date and time are
		// provided then third value is calculated accordingly
		TextInputComponent endDateAdd = af.instantiateComponent(TextInputComponent.class, "endDate");
		logger.info("End date on add labor drawer: " + endDateAdd.getContents());
		reportWork.isDateExist();

		TimeInputComponent endTimeAdd = af.instantiateComponent(TimeInputComponent.class, "endTime");
		logger.info("End Time on add labor drawer: " + endTimeAdd.getTimeInput());
		reportWork.isTimeExist();

		// Verify labor record is saved with updated values when technician clicks on
		// "Save" button on labor drawer
		reportWork.changeLaborType(WoStatus.WMATL);
		reportWork.saveLabor();
		reportWork.clickBackChevron();
		woDetails.clickReportWorkButton();
		// Verify grouped labor name
		logger.info("Grouped Labor name: " + laborName);
		assertEquals(laborName, reportWork.getLaborName());

		// Click chevron to expand labor record
		reportWork.laborChevron("bam2r_items_datalistWrapper");

		// Date in 'MMMM D, YYYY' format
		SimpleDateFormat formatDate1 = new SimpleDateFormat("dd/MMMM/yyyy");
		Date date1 = new Date();
		formatDate1.setTimeZone(TimeZone.getTimeZone(timezone));
		// converting to UTC or format the Date as UTC
		String myDate1 = formatDate1.format(date1);
		logger.info("myDate1 : " + myDate1);
		// getting the current year
		String[] splitDate = myDate1.split("/");
		String currentYear1 = splitDate[2];
		logger.info("currentYear1 : " + currentYear1);
		// Getting the current day
		String currentDay1 = splitDate[0];
		logger.info("currentDay1 : " + currentDay1);
		// Getting the current month
		String currentMonth1 = (splitDate[1].startsWith("0"))? (splitDate[1].substring(1,2)):splitDate[1] ;
		logger.info("currentMonth1 : " + currentMonth1);		
		todayDate2 = currentMonth1 + " " + currentDay1 + ", " + currentYear1.substring(2,4);

		// Verify updated values on saved labor record
		logger.info("Start date on saved labor record:" + todayDate2);
		assertEquals(reportWork.getLaborStartDate(), todayDate2);

		logger.info("End date on saved labor record:" + todayDate2);
		assertEquals(reportWork.getLaborEndDate(), todayDate2);

		logger.info("Start time on saved labor record: " + startTime);
		assertEquals(reportWork.getLaborStartTime().toUpperCase(),startTime);

		logger.info("End time on saved labor record: " + endTime);
		assertEquals(reportWork.getLaborEndTime().toUpperCase(),endTime);

		logger.info("Labor type on saved labor record: " + laborType1);
		assertEquals(laborType1, reportWork.getLaborType());

		String actualEstimated = reportWork.getLaborHours();
		logger.info("Duration on saved labor record: " + actualEstimated);
		assertEquals(actualEstimated, regularMins + " minute");

		// Get start time of started labor transaction
		String startTime = reportWork.getStartTime().toUpperCase();
		logger.info("Start Time of started labor transaction: " + startTime);

		// Verify availability of edit pencil icon button for started labor time record
		reportWork.editLabor();

		// Verify clicking on edit pencil button opens sliding drawer to modify started
		// labor time record
		logger.info("Labor name on edit labor drawer: " + laborName);
		assertEquals(laborName, reportWork.getLaborNameOnDrawer()); 

		TextInputComponent startDateEdit = af.instantiateComponent(TextInputComponent.class, "startDate");
		logger.info("Start date on edit labor drawer: " + todayDate1);
		assertEquals(todayDate1, startDateEdit.getContents());

		TimeInputComponent startTimeEdit = af.instantiateComponent(TimeInputComponent.class, "startTime");
		logger.info("Start time on edit labor drawer: " + todayDate1);
		assertEquals(startTime, startTimeEdit.getTimeInput());

		logger.info("Craft on edit labor drawer: " + craft);
		assertEquals(craft, reportWork.getLaborCraftOnDrawer());

		logger.info("Skill level on edit labor drawer: " + skill);

		assertEquals(skill, reportWork.getLaborSkillOnDrawer());
	}

	@Test(groups = {
			"priority2" }, description = "Verify technician can select a task from task lookup when single or multiple planned tasks are added to work order", timeOut = 8000000)
	public void selectTask() throws Exception {

		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		ReportWorkPage reportWork = new ReportWorkPage(af);
		WorkOrderDetailsPage woDetails = new WorkOrderDetailsPage(af);
		CreateWorkOrderPage createWO= new CreateWorkOrderPage(af);
		TaskPage taskPage = new TaskPage(af);

		assignedWorkPage.search(woNum);

		// Navigate to WO details page
		assignedWorkPage.openWorkOrderDetails();

		// Start the work order
		woDetails.clickStartWorkButton();

		// Navigate to Report work page
		woDetails.clickReportWorkButton();

		// verify header of the page
		header = woDetails.getTitle(woDetails.reportWorkPageTitle);
		reportHeader = woNum + " Report work";
		assertEquals(header, reportHeader);
		logger.info("Report work Page header verified");

		// click on X button
		reportWork.clickPlusLaborButton();

		// Verify header of the page
		String laborHeader = reportWork.getTitle(reportWork.laborPageHeader);
		assertEquals(laborHeaderText, laborHeader);

		// Click on Task button
		reportWork.clickTaskButton();
		logger.info("task button clicked");

		// Verify header of the page
		String taskPageHeader = reportWork.getTitle(taskPage.taskPageHeader);
		assertEquals(taskPageHeaderText, taskPageHeader);

		// Verify tasks are present
		assertEquals(taskTitleOne, taskPage.verifyTaskContent(taskIdOne));
		assertEquals(taskTitleTwo, taskPage.verifyTaskContent(taskIdTwo));

		taskPage.taskPageBackButton();
		taskPage.crossButtonLaborPage();
		woDetails.clickButton(woDetails.reportWorkBreadcrumb);
		woDetails.clickBackWOList();

	}
	@Test(groups = {
	"priority2" }, description = "Verify labor transaction functionality on Report work page", timeOut = 8000000)
	public void verifyLaborFullName() throws Exception {

		// Search the work order
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage workOrderDetailsPage = new WorkOrderDetailsPage(af);
		ReportWorkPage reportWork = new ReportWorkPage(af);

		// record appears on the Report Work page
		logger.info(
				"Scenario 9 - Verify Add labor, actual materials and tools functionality for task work order on report work page");

		// Search the task work order
		logger.info("Search with taskWoNum");
		assignedWorkPage.search(woNum);
		// and click chevron to open task work order details page.
		assignedWorkPage.openCardDetails();

		// Click to open report work page.
		ReportWorkPage reportWorkPage = workOrderDetailsPage.clickReportWorkButton();

		reportWorkPage.clickPlusLaborButton();
		String laborname1 = reportWorkPage.getLaborName(labor, getLaborIdFromDB(labor));
		reportWorkPage.saveLaborSelection();
		String verilaborNameOndrawer=reportWork.getLaborNameOnDrawer();
		
		//verify full name on drawer
		assertEquals(laborname1,verilaborNameOndrawer);
		
		reportWorkPage.saveLabor();
		String labornameOnreportpage=reportWork.getLaborName();
		//verify full name on report page
		assertEquals(laborname1,labornameOnreportpage);
		reportWorkPage.clickBackChevron();
		workOrderDetailsPage.clickBackWOList();
}
	
	@Test(groups = {
			"mobile","desktop","priority2" }, description = "Verify technician can select multiple labors from report work page", timeOut = 8000000)
public void verifyMltipleLabor() throws Exception {

	MySchedulePage assignedWorkPage = new MySchedulePage(af);
	ReportWorkPage reportWork = new ReportWorkPage(af);
	WorkOrderDetailsPage woDetails = new WorkOrderDetailsPage(af);

	assignedWorkPage.search(woNum);

	// Navigate to WO details page
	assignedWorkPage.openWorkOrderDetails();

	// Navigate to Report work page
	woDetails.clickReportWorkButton();

	// verify header of the page
	header = woDetails.getTitle(woDetails.reportWorkPageTitle);
	reportHeader = woNum + " Report work";
	assertEquals(header, reportHeader);
	logger.info("Report work Page header verified");

	// click on + icon of labor on report work page
	reportWork.clickPlusLaborButton();

	// Verify header of the page
	String laborHeader = reportWork.getTitle(reportWork.laborPageHeader);
	assertEquals(laborHeaderText, laborHeader);
	
	// Select new labor from labor selection list
	reportWork.selectNewLabor();
	
	reportWork.saveLaborSelection();
	
	// Verify after selection labor exists
	assertTrue(reportWork.isLaborExist());
	
	// Save the selected labors
	reportWork.saveLabor();
	
	// Click chevron to expand first labor record
	reportWork.firstLaborChevron(chevronId);
	// click on chevron to close first labor record
	reportWork.firstLaborChevron(chevronId);
	// Click on chevron to expand second labor record
	reportWork.secondLaborChevron(chevronId);
	reportWork.clickBackChevron();
	woDetails.clickBackWOList();	
}

	@Test(groups = {
			"priority3" }, description = "Verify that dialog with save and discard options is displayed when technician clicks on \"X\" button after making changes on labor sliding drawer", timeOut = 8000000)
	public void saveDiscardDialog() throws Exception {

		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		ReportWorkPage reportWork = new ReportWorkPage(af);
		WorkOrderDetailsPage woDetails = new WorkOrderDetailsPage(af);

		assignedWorkPage.search(woNum);

		// Navigate to WO details page
		assignedWorkPage.openWorkOrderDetails();

		// Navigate to Report work page
		woDetails.clickReportWorkButton();

		// verify header of the page
		header = woDetails.getTitle(woDetails.reportWorkPageTitle);
		reportHeader = woNum + " Report work";
		assertEquals(header, reportHeader);
		logger.info("Report work Page header verified");

		// click on X button
		reportWork.clickPlusLaborButton();

		// Verify header of the page
		String laborHeader = reportWork.getTitle(reportWork.laborPageHeader);
		assertEquals(laborHeaderText, laborHeader);

		// Click on change date
		reportWork.enterDate(startYear, startMonth, startDate);

		// Click back button
		reportWork.closeLaborDrawer();

		// Verify save and discard popup is displayed
		assertEquals(headerText, reportWork.getSaveDiscardHeader());

		assertEquals("You must first discard or save your changes.", reportWork.getSaveDiscardText());

		reportWork.discardButtonClick();

		// click on X button
		reportWork.clickPlusLaborButton();

		logger.info("Craft on edit labor drawer: " + craft);
		assertEquals(craft, reportWork.getLaborCraftOnDrawer());

		logger.info("Skill level on edit labor drawer: " + skill);

		assertEquals(skill, reportWork.getLaborSkillOnDrawer());
	}


	protected void createDefaultObjects() throws Exception {
		try {
			logger.info("Creating default objects");

			// Create work order
			logger.info("Creating a work order");
			String workOrderResult = maximoApi.retrieve(new WorkOrder(),
					"addid=1&internalvalues=1&action=system:new&addschema=1");
			newWorkOrder = new Gson().fromJson(workOrderResult, WorkOrder.class);
			newWorkOrder.setDescription("WO for mobile automation test");
			newWorkOrder.setSiteId(SetupData.SITEID);
			newWorkOrder.setGLAccount(SetupData.GLDEBITACCT);
			assertEquals(maximoApi.create(newWorkOrder), SetupData.CreatedSuccess);
			woNum = newWorkOrder.getWoNum();
			logger.info("Work Order: " + woNum);
			// Create Task 1
			logger.info("Task API Call started ");
			List<Task> arr = new ArrayList<>();
			Task task = new Task();
			task.setDescription(taskTitleOne);
			task.setTaskid(Integer.parseInt("10"));
			task.setStatus(WoStatus.WAPPR.toString());
			arr.add(task);
			newWorkOrder.setWoactivity(arr);
			maximoApi.update(newWorkOrder);
			logger.info("Tasks added");

			// Create Task 2
			logger.info("Task API Call started ");
			List<Task> arr1 = new ArrayList<>();
			Task task1 = new Task();
			task1.setDescription(taskTitleTwo);
			task1.setTaskid(Integer.parseInt("11"));
			task1.setStatus(WoStatus.WAPPR.toString());
			arr1.add(task1);
			newWorkOrder.setWoactivity(arr1);
			maximoApi.update(newWorkOrder);
			logger.info("Tasks added");

			// Change WO status to Approved
			logger.info("Changing work order status to APPR");
			assertEquals(maximoApi.changeStatus(newWorkOrder, WoStatus.APPR.toString()), SetupData.NoContentSuccess);

			// Assign the labor
			assertEquals(maximoApi.addAssignmentLabor(newWorkOrder, labor), SetupData.NoContentSuccess);
			logger.info("Assignment added");

			apiCodeSuccess = true;
		} catch (AssertionError e) {
			logger.info(" SkipException apiCodeSuccess = " + apiCodeSuccess);
			apiCodeSuccess = false;
			throw new Exception("Test Setup API Failed,Stopping execution.");
		}
	}

	// Generated by WCA for GP
	/**
	 * Sets the system properties of the Default Information timezone.
	 *
	 * @param timezone the value to set , labor the value to set
	 */
	public void defaultInformationOfUserTimeZone(String timezone, String labor) {
		jdbcConnection.executeUpdateSQL("UPDATE MAXIMO.PERSON SET MAXIMO.PERSON.TIMEZONE = '" + timezone
				+ "' WHERE MAXIMO.PERSON.PERSONID = '" + labor + "'");
	}
	/**
	 * Get labor Id by labor Code via database
	 * 
	 * @param laborCode
	 * @return
	 */
	protected String getLaborIdFromDB(String laborCode) {
		String sqlString = "SELECT L.LABORID FROM MAXIMO.LABOR L WHERE L.LABORCODE='" + laborCode.toUpperCase()+ "'";
		logger.info(sqlString);
		Object[] resultsObjects = jdbcConnection.executeSQL(sqlString);
		Object[] resultArray1 = (Object[]) resultsObjects[0];
		String laborId = resultArray1[0].toString();
		logger.info("labor id from db>" + laborId);
		return laborId;
	};
}
