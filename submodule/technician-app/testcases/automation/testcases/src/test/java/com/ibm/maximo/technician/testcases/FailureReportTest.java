package com.ibm.maximo.technician.testcases;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;

import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.google.gson.Gson;
import com.ibm.maximo.automation.mobile.AbstractAutomationFramework;
import com.ibm.maximo.automation.mobile.FrameworkFactory;
import com.ibm.maximo.automation.mobile.MobileAutomationFramework;
import com.ibm.maximo.automation.mobile.api.MaximoApi;
import com.ibm.maximo.automation.mobile.api.json.WorkOrder;
import com.ibm.maximo.components.TextInputComponent;
import com.ibm.maximo.technician.framework.TechnicianTest;
import com.ibm.maximo.technician.page.FailureReportingPage;
import com.ibm.maximo.technician.page.MySchedulePage;
import com.ibm.maximo.technician.page.ReportWorkPage;
import com.ibm.maximo.technician.page.WorkOrderDetailsPage;
import com.ibm.maximo.technician.setupdata.SetupData;
import com.ibm.maximo.technician.setupdata.SetupData.*;

/*
 * GRAPHITE-51061: Report work - Failure reporting
   GRAPHITE-65683: Report work- Failure reporting
   GRAPHITE-65540: [TECHMOBILE]- Failure Reporting P2 Scenarios
   MASR-794 : Provide an Editable Field for Failure Date and Time on Failure reporting under Report Work Page for My Schedule App
 */

public class FailureReportTest extends TechnicianTest {
	private final Logger logger = LoggerFactory.getLogger(FailureReportTest.class);
	private AbstractAutomationFramework af;
	private TestSuite testSuite;
	private MaximoApi maximoApi;
	private WorkOrder newWorkOrder;
	private static final String WORKORDER_DESCRIPTION = "Work Order Description ";
	private String woNum, timezone, labor, todayDate1, startTime, deviceType;
	private int startHrs, startMin;
	private String problemHeadertext = "Problem";
	private String causeHeaderText = "Cause";
	private String remedyHeaderText = "Remedy";
	private String detailsHeaderText = "Details";
	private String descriptionText= "Mobile Test Priority 3: Description for new test is added in the work order in the Report work page for failure Report";

	@BeforeClass(alwaysRun = true)
	@Parameters({ "configPath" })
	public void setup(String configPath) throws Exception {
		logger.info("********************FailureReportTest*********************************");
		this.af = FrameworkFactory.get();
		Properties properties = new Properties();
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(configPath));
			properties.load(in);
			labor = properties.getProperty("system.username");
			timezone = properties.getProperty("capabilities.browserstack.timezone") != null ? properties.getProperty("capabilities.browserstack.timezone") : "UTC";
			deviceType = properties.getProperty("system.deviceType");
			maximoApi = new MaximoApi();
			maximoApi.setMaximoServer(properties.getProperty("system.maximoServerUrl"),
					properties.getProperty("system.maximoAPIKey"));
			// timezone is set in default information of user
			defaultInformationOfUserTimeZone(timezone, labor);
			createDefaultObjects();
		} catch (Exception e) {
			e.printStackTrace();
		}
		login(af);
		if (masServer.equals("true")) {
			// FVT SetupData , IVT might be same as sandbox or different
			updateData();
		}
	}

	@AfterClass(alwaysRun = true)
	public void teardown() throws Exception {
		logOut(af);
		// Change WO status to Completed
		logger.info("Changing work order status to COMP");
		maximoApi.changeStatus(newWorkOrder, WoStatus.COMP.toString());
		logger.info("Delete FAILURECODE via DB query>>"+"DELETE FROM MAXIMO.FAILURELIST WHERE FAILURECODE='"+failureCodeClass+"' OR FAILURECODE='"+failureCodeProblem+"' OR FAILURECODE='"+failureCodeCause+"' OR FAILURECODE='"+failureCodeRemedy+"'");
		// Delete FAILURECODE via DB query
		jdbcConnection.executeUpdateSQL("DELETE FROM MAXIMO.FAILURELIST WHERE FAILURECODE='"+failureCodeClass+"' OR FAILURECODE='"+failureCodeProblem+"' OR FAILURECODE='"+failureCodeCause+"' OR FAILURECODE='"+failureCodeRemedy+"'");
		if (testSuite != null) {
			testSuite.teardown();
		}
	}

	@Test(groups = { "mobile", "desktop" }, description = "Verify technician should not be able to select remedy, cause, problem without selecting the first field failure class", timeOut = 900000)
	public void addFailureReport() throws Exception {
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage woDetails = new WorkOrderDetailsPage(af);
		ReportWorkPage reportWorkPage = new ReportWorkPage(af);
		FailureReportingPage failureReporting = new FailureReportingPage(af);

		// Search the work order
		assertEquals(true, assignedWorkPage.search(woNum));
		// Navigate to work order details page
		assignedWorkPage.openCardDetails();
		// Click on Report Work touch-point
		woDetails.clickReportWorkButton();
		// Click on edit icon for failure reporting
		reportWorkPage.edit();
		assertFalse(failureReporting.isProblemChevronNotExist(),
				"Problem chevron cannot be selected as it is not available");
		assertFalse(failureReporting.isCauseChevronNotExist(),
				"Cause chevron cannot be selected as it is not available");
		assertFalse(failureReporting.isRemedyChevronNotExist(),
				"Remedy chevron cannot be selected as it is not available");
		assertTrue(failureReporting.isFailureDateAndTimeChevronExist(),
				"Failure date and time chevron is not displayed");
		failureReporting.save();
	}

	@Test(groups = {
			"mobile", "desktop" }, description = "Verify user is able to perform failure reporting functionality on Report work page", timeOut = 9800000)
	public void modifyFailureReport() throws Exception {
		ReportWorkPage reportWorkPage = new ReportWorkPage(af);
		FailureReportingPage failureReporting = new FailureReportingPage(af);

		reportWorkPage.edit();
		// Choose Failure class
		failureReporting.openFailureClassList();

		String failureCodeClassXapth = "//*[@id='m35ek']//p[text()='" + descriptionFailureCodeClass + "']";
		String findIndexString = af
				.waitForElementToBePresent(By.xpath(failureCodeClassXapth), af.DEFAULT_TIMEOUT_MS * 5)
				.getAttribute("id");
		String[] findIndexStringSplit = findIndexString.split("\\[");
		String index = findIndexStringSplit[1].replaceAll("\\]", "");

		failureReporting.chooseFailureClassValue(Integer.parseInt(index));
		// Choose Problem
		failureReporting.chooseProblemValue(0);
		// Choose Cause
		failureReporting.chooseCauseValue(0);
		// Choose Remedy
		failureReporting.chooseRemedyValue(0);
		// Add Summary
		failureReporting.addSummary("Mobile Test 1: Summary");
		// Add Description
		failureReporting.addDescription("Mobile Test 1: Description");
		// Failure DateAndTime 
		failureReporting.backButton();

		//skip for ios app - Edit Failure code date and time test due to open bug in framework MAXMOA-954 & MAXMOA-957
		// Remove this if condition once MAXMOA-954 & MAXMOA-957 bug fixes
		if (!deviceType.equalsIgnoreCase("ios")) {
			logger.info("Edit Failure code date and time");
			failureReporting.clickFailureDateAndTime();
			enterDateAndTime();
		}
		// Click on Save button
		failureReporting.save();
		logger.info("Verify that when technician clears out or changes problem code then child fields are cleared out");
		// Click on the edit button
		reportWorkPage.edit();
		// Clear Problem value
		failureReporting.closeProblemValue();
		// Assert Cause value is removed
		assertFalse(failureReporting.checkChildFieldIsRemoved("nzw8m[2]"), "Is Cause value cleared");
		// Assert Remedy value is removed
		assertFalse(failureReporting.checkChildFieldIsRemoved("nzw8m[3]"), "Is Remedy value cleared");
		// Click on Save
		failureReporting.save();
		logger.info("Verify after saving, latest failure report data is displayed on the Report work page");
		// Assert updated failure report
		assertEquals(descriptionFailureCodeClass, reportWorkPage.getFailureClass());
		logger.info("Verify that technician can modify the existing failure Summary");
		// Click on edit icon for failure reporting
		reportWorkPage.edit();
		// close failure class
		failureReporting.closeFailureClass();
		// Choose Failure class
		failureReporting.openFailureClassList();
		failureReporting.chooseFailureClassValue(Integer.parseInt(index));
		// Choose Problem
		failureReporting.chooseProblemValue(0);
		// Choose Cause
		failureReporting.chooseCauseValue(0);
		// Choose Remedy
		failureReporting.chooseRemedyValue(0);
		// Add new summary
		failureReporting.addSummary("Mobile Test 2: Summary can be modified");
		// Click on Save button
		failureReporting.save();
		logger.info("Verify after saving, latest failure report data is displayed on the Report work page");
		// Assert updated failure report
		assertEquals(descriptionFailureCodeClass, reportWorkPage.getFailureClass());

		// https://github.ibm.com/maximo-app-framework/technician-app/blob/MAS-9.1/testcases/ReportWorkPage/TC_ReportworkPageFailureReporting.md#scenario-19---verify-that-technician-can-update-causes-and-remedy-for-the-existing-failure-code
		logger.info("Verify that when technician clears out or changes Cause code then child fields are cleared out");
		// Click on the edit button
		reportWorkPage.edit();
		// Clear Cause value
		failureReporting.closeCauseValue();
		// Assert Cause value is removed
		assertTrue(failureReporting.checkChildFieldIsRemoved(failureReporting.crossProblemButton), "Is Cause value cleared");		
		// Assert Cause value is removed
		assertFalse(failureReporting.checkChildFieldIsRemoved(failureReporting.crossCauseButton), "Is Cause value cleared");
		// Assert Remedy value is removed
		assertFalse(failureReporting.checkChildFieldIsRemoved(failureReporting.crossRemedyButton), "Is Remedy value cleared");
		// Click on Save
		failureReporting.save();
		logger.info("Verify after saving, latest failure report data is displayed on the Report work page");
		// Assert updated failure report
		assertEquals(descriptionFailureCodeClass, reportWorkPage.getFailureClass());
		logger.info("Verify that technician can modify the existing failure Summary");
		// Click on edit icon for failure reporting
		reportWorkPage.edit();
		// close failure class
		failureReporting.closeFailureClass();
		// Choose Failure class
		failureReporting.openFailureClassList();
		failureReporting.chooseFailureClassValue(Integer.parseInt(index));
		// Choose Problem
		failureReporting.chooseProblemValue(0);
		// Choose Cause
		failureReporting.chooseCauseValue(0);
		// Choose Remedy
		failureReporting.chooseRemedyValue(0);
		// Add new summary
		failureReporting.addSummary("Mobile Test 2: Summary can be modified");
		// Click on Save button
		failureReporting.save();
		logger.info("Verify after saving, latest failure report data is displayed on the Report work page");
		// Assert updated failure report
		assertEquals(descriptionFailureCodeClass, reportWorkPage.getFailureClass());
		
		logger.info("Verify that when technician clears out or changes Remeday code");
		// Click on the edit button
		reportWorkPage.edit();
		// Clear Cause value
		failureReporting.closeRemedyValue();
		// Assert Cause value is displayed
		assertTrue(failureReporting.checkChildFieldIsRemoved(failureReporting.crossCauseButton), "Is Cause value displayed");
		// Assert Remedy value is removed
		assertFalse(failureReporting.checkChildFieldIsRemoved(failureReporting.crossRemedyButton), "Is Remedy value cleared");
		// Click on Save
		failureReporting.save();
		logger.info("Verify after saving, latest failure report data is displayed on the Report work page");
		// Assert updated failure report
		assertEquals(descriptionFailureCodeClass, reportWorkPage.getFailureClass());
		logger.info("Verify that technician can modify the existing failure Summary");
		// Click on edit icon for failure reporting
		reportWorkPage.edit();
		// close failure class
		failureReporting.closeFailureClass();
		// Choose Failure class
		failureReporting.openFailureClassList();
		failureReporting.chooseFailureClassValue(Integer.parseInt(index));
		// Choose Problem
		failureReporting.chooseProblemValue(0);
		// Choose Cause
		failureReporting.chooseCauseValue(0);
		// Choose Remedy
		failureReporting.chooseRemedyValue(0);
		// Add new summary
		failureReporting.addSummary("Mobile Test 2: Summary can be modified");
		// Click on Save button
		failureReporting.save();
		logger.info("Verify after saving, latest failure report data is displayed on the Report work page");
		// Assert updated failure report
		assertEquals(descriptionFailureCodeClass, reportWorkPage.getFailureClass());
	}

	@Test(groups = {
			"priority2" }, description = " Verify that technician can select and save a problem code from a value list (only problem codes associated to selected failure class are displayed)", timeOut = 90000000)
	public void verifyFailureClass() throws Exception {
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage woDetails = new WorkOrderDetailsPage(af);
		ReportWorkPage reportWorkPage = new ReportWorkPage(af);
		FailureReportingPage failureReporting = new FailureReportingPage(af);

		// Search the work order
		assertEquals(true, assignedWorkPage.search(woNum));
		// Navigate to work order details page
		assignedWorkPage.openWorkOrderDetails();
		// Click on Report Work touch-point
		woDetails.clickReportWorkButton();
		// Click on edit icon for failure reporting
		reportWorkPage.edit();
		
		failureReporting.openFailureClassList();

		String failureCodeClassXapth = "//*[@id='m35ek']//p[text()='" + descriptionFailureCodeClass + "']";
		String findIndexString = af
				.waitForElementToBePresent(By.xpath(failureCodeClassXapth), af.DEFAULT_TIMEOUT_MS * 5)
				.getAttribute("id");
		String[] findIndexStringSplit = findIndexString.split("\\[");
		String index = findIndexStringSplit[1].replaceAll("\\]", "");

		failureReporting.chooseFailureClassValue(Integer.parseInt(index));
		
		// verify title of page
		String problemPageHeader = failureReporting.getTitle(failureReporting.PageHeader);
		assertEquals(problemHeadertext, problemPageHeader);
		failureReporting.backButton();
		assertTrue(failureReporting.checkCrossIconDisplayed(0), "Failure class is selected");
	}

	@Test(groups = {
			"priority2" }, description = "Verify that technician can select and save cause code from value list (only causes associated to selected failure class and problem code are displayed)", timeOut = 9000000)
	public void verifyProblemValue() throws Exception {
		FailureReportingPage failureReporting = new FailureReportingPage(af);
		failureReporting.openProblemList();
		failureReporting.chooseProblemValue(0);
		// verify title of page
		String causePageHeader = failureReporting.getTitle(failureReporting.PageHeader);
		assertEquals(causeHeaderText, causePageHeader);
		failureReporting.backButton();
		assertTrue(failureReporting.checkCrossIconDisplayed(1), "Problem class is selected");
	}

	@Test(groups = {
			"priority2" }, description = "Verify that technician can select and save cause code from value list (only causes associated to selected failure class and problem code are displayed)", timeOut = 9000000)
	public void verifyValueCause() throws Exception {

		FailureReportingPage failureReporting = new FailureReportingPage(af);
		failureReporting.openCauseList();
		failureReporting.chooseCauseValue(0);
		// verify title of page
		String remedyPageHeader = failureReporting.getTitle(failureReporting.PageHeader);
		assertEquals(remedyHeaderText, remedyPageHeader);
		failureReporting.backButton();
		assertTrue(failureReporting.checkCrossIconDisplayed(2), "Cause class is selected");
	}

	@Test(groups = {
			"priority2" }, description = "Verify that technician can select and save remedy code from a value list (only remedy codes associated to selected failure class, problem and cause codes are displayed)", timeOut = 9000000)
	public void verifyWRemedyCode() throws Exception {

		FailureReportingPage failureReporting = new FailureReportingPage(af);
		failureReporting.openRemedyList();
		failureReporting.chooseRemedyValue(0);
		// verify title of page
		String detailsPageHeader = failureReporting.getTitle(failureReporting.PageHeader);
		assertEquals(detailsHeaderText, detailsPageHeader);
		failureReporting.backButton();
		assertTrue(failureReporting.checkCrossIconDisplayed(3), "Remedy class is selected");
	}
	
	@Test(groups = {
			"priority3" }, description = "Verify that the remarks and description are saved when clicked on save button at top right corner", timeOut = 9000000)
	public void verifyWRemarksDescription() throws Exception {

		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage woDetails = new WorkOrderDetailsPage(af);
		ReportWorkPage reportWorkPage = new ReportWorkPage(af);
		FailureReportingPage failureReporting = new FailureReportingPage(af);

		// Search the work order
		assertEquals(true, assignedWorkPage.search(woNum));
		
		// Navigate to work order details page
		assignedWorkPage.openWorkOrderDetails();
		
		// Click on Report Work touch-point
		woDetails.clickReportWorkButton();
		
		// Click on edit icon for failure reporting
		reportWorkPage.edit();
		
		// Click on details icon 
		failureReporting.detailsField();
		
		// Verify title of the page
		String detailsPageHeader = failureReporting.getTitle(failureReporting.PageHeader);
		assertEquals(detailsHeaderText, detailsPageHeader);

		// Add summary 
		failureReporting.addSummary("Mobile Test 3: Summary is added");
		
		// Add description
		failureReporting.addDescription(descriptionText);
		
		// Save the details 
		failureReporting.save();
	}
	

// Generated by WCA for GP
	/**
	 * This method creates the default objects required for the unit tests.
	 * 
	 * @throws Exception If an error occurs.
	 */
	protected void createDefaultObjects() throws Exception {
		logger.info("Creating default objects");
		failurecodeHierarchy();
		createFailureCodeReturnFailureList(failureCodeCause1, descriptionCause1,
				failurelistQueryFromDB(failureCodeProblem), "CAUSE");
		createFailureCodeReturnFailureList(failureCodeRemedy1, descriptionRemedy1,
				failurelistQueryFromDB(failureCodeCause), "REMEDY");
		createFailureCodeReturnFailureList(failureCodeRemedy1+'1', descriptionRemedy1+'1',
				failurelistQueryFromDB(failureCodeCause1), "REMEDY");
		// Create a workorder
		logger.info("Creating a work order");
		String workOrderResult = maximoApi.retrieve(new WorkOrder(),
				"addid=1&internalvalues=1&action=system:new&addschema=1");
		newWorkOrder = new Gson().fromJson(workOrderResult, WorkOrder.class);
		newWorkOrder.setDescription(WORKORDER_DESCRIPTION);
		newWorkOrder.setSiteId(SetupData.SITEID);
		newWorkOrder.setAssetNum(assetNum);
		newWorkOrder.setWorkType(WorkType.PM.toString());
		maximoApi.create(newWorkOrder);
		woNum = newWorkOrder.getWoNum();
		logger.info("Work Order: {}" + woNum);

		// Change WO status to Approved
		logger.info("Changing work order status to APPR");
		maximoApi.changeStatus(newWorkOrder, WoStatus.APPR.toString());

		// Assign the labor
		maximoApi.addAssignmentLabor(newWorkOrder, labor);
		logger.info("Assignment added");

	}
	
	protected void enterDateAndTime() throws Exception {
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
		String currentMonth = currentDate[0];

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
			
		// Date in 'MM/DD/YY' format
		todayDate1 = currentMonth + "/" + currentDay + "/" + currentYear;
		logger.info("Today Date: " + todayDate1);

		// Verify date on failure date and time drawer is an auto-filled value
		TextInputComponent startDateAdd = af.instantiateComponent(TextInputComponent.class, "yd6d6_date");
		assertEquals(startDateAdd.getContents(),todayDate1);

		// Enter time on failure date and time drawer
		((MobileAutomationFramework) af).setTime(By.id("yd6d6_time"), startHrs, startMin, AMOrPM); // setTime true means AM and false means PM per 
		logger.info("Time added");
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
}
