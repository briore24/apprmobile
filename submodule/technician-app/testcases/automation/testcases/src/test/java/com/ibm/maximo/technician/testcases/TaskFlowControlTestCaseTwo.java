package com.ibm.maximo.technician.testcases;

import static org.testng.Assert.assertEquals;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONObject;
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
import com.ibm.maximo.automation.mobile.api.json.Domain;
import com.ibm.maximo.automation.mobile.api.json.SynonymDomainValue;
import com.ibm.maximo.automation.mobile.api.json.Task;
import com.ibm.maximo.automation.mobile.api.json.WorkOrder;
import com.ibm.maximo.automation.mobile.api.json.WorkType;
import com.ibm.maximo.components.DataListComponent;
import com.ibm.maximo.components.DataListItemComponent;
import com.ibm.maximo.technician.framework.TechnicianTest;
import com.ibm.maximo.technician.page.MySchedulePage;
import com.ibm.maximo.technician.page.ReportWorkPage;
import com.ibm.maximo.technician.page.TaskPage;
import com.ibm.maximo.technician.page.WorkOrderDetailsPage;
import com.ibm.maximo.technician.setupdata.SetupData;
import com.ibm.maximo.technician.setupdata.SetupData.*;

/*
 * GRAPHITE-51080: Work order under Flow control functionality
 * predecessor is working on ivt15 environment only
 */
public class TaskFlowControlTestCaseTwo extends TechnicianTest {
	private final Logger logger = LoggerFactory.getLogger(TaskFlowControlTestCaseTwo.class);
	private AbstractAutomationFramework af;
	private TestSuite testSuite;
	private MaximoApi maximoApi;
	private WorkOrder newWorkOrder;
	private String CUSTOMCOMP = "CUSTOMCOMP";
	private String woNum2, labor, masServer;
	private String statusWrapper = "z4q5w_items_datalistWrapper";
	private int taskNum;

	@BeforeClass(alwaysRun = true)
	@Parameters({ "configPath" })
	public void setup(String configPath) throws Exception {
		logger.info("********************TaskFlowControlTestCaseTwo*********************************");
		this.af = FrameworkFactory.get();
		Properties properties = new Properties();
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(configPath));
			properties.load(in);
			labor = properties.getProperty("system.username");
			masServer = properties.getProperty("system.masServer");
			maximoApi = new MaximoApi();
			maximoApi.setMaximoServer(properties.getProperty("system.maximoServerUrl"),
					properties.getProperty("system.maximoAPIKey"));
			createDefaultObjects();
		} catch (Exception e) {
			e.printStackTrace();
		}
		login(af);
	}

	@AfterClass(alwaysRun = true)
	public void teardown() throws Exception {
		// If test failed from UI then WO is not completed then will be completed via DB query
		jdbcConnection.executeUpdateSQL("UPDATE WORKORDER SET STATUS = 'COMP' WHERE WONUM = '" + woNum2 + "'");
		logOut(af);
		// Delete 'CUSTOMCOMP' via DB query
		jdbcConnection.executeUpdateSQL("DELETE FROM MAXIMO.SYNONYMDOMAIN WHERE SYNONYMDOMAIN.VALUE ='CUSTOMCOMP'");
		checkStartTimerInprg("0");

		if (testSuite != null) {
			testSuite.teardown();
		}
	}

	@Test(groups = {
			"mobile" }, enabled = true, description = "Flow controlled WO and tasks when WORKTYPE.COMPLETESTATUS is set to Custom Completed status", timeOut = 1500000)
	public void flowControl2() throws Exception {
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage woDetailsPage = new WorkOrderDetailsPage(af);
		TaskPage taskPage = new TaskPage(af);
		ReportWorkPage reportWorkPage = new ReportWorkPage(af);

		logger.info("Search work order woNum2>" + woNum2);

		assignedWorkPage.search(woNum2);
		assignedWorkPage.clickStartWorkButton();
		woDetailsPage.clickBackChevronToListPage();
		assertEquals("In progress", assignedWorkPage.verifyChangedStatus());
		logger.info("Status has verified");

		assignedWorkPage.openWorkOrderDetails();

		logger.info("Verify status of task should be 'Approved'");
		woDetailsPage.clickTaskButton();

		// navigate back to work order details page
		taskPage.clickBackChevron();
		logger.info("Click Back chevron of Task");

		woDetailsPage.openStatusList();
		logger.info("Open status list");

		DataListComponent list = af.instantiateComponent(DataListComponent.class, statusWrapper);
		List<DataListItemComponent> items = list.getChildren();

		for (DataListItemComponent status : items) {
			logger.info("status", status.getText());
			assertEquals(true, status.getText() != "Completed");
		}
		logger.info("Parent workorder dosen't have status completed");

		woDetailsPage.closeStatusChangeDialog();
		logger.info("changes status dialog clicked");

		woDetailsPage.clickReportWorkButton();
		logger.info("Report work button is clicked");

		assertEquals(false, reportWorkPage.verifySaveButtonEnabled());
		logger.info("complete work button is disabled");

		// EAM env
		if (masServer.equals("false")) {
			reportWorkPage.clickBackChevron();
			logger.info("Back to work order details page");
		} else { // MAS or FVT
			goToMySchedule(af);
			assignedWorkPage.openWorkOrderDetails();
		}

		woDetailsPage.clickTaskButton();
		logger.info("Task Button clicked");

		logger.info("Complete all the tasks");
		taskPage.completeTask("pb_mv[0]");
		logger.info("Complete Task 1");

		taskPage.clickBackChevron();
		logger.info("Click Back chevron of Task");

		woDetailsPage.clickTaskButton();

		taskPage.completeTask("pb_mv[1]");
		logger.info("Complete Task 2");

		taskPage.clickBackChevron();
		logger.info("Click Back chevron of Task after task 2 complete");

		woDetailsPage.clickTaskButton();

		// Verify that green checkmark icon is displayed for the task when technician
		// marks that incomplete task as completed
		assertEquals("Carbon:checkmark--outline", taskPage.getGreenCheckmarkOnTaskPage(0));
		logger.info("Task 1 completed");

		// Verify that green checkmark icon is displayed for the task when technician
		// marks that incomplete task as completed
		assertEquals("Carbon:checkmark--outline", taskPage.getGreenCheckmarkOnTaskPage(1));
		logger.info("Task 2 completed");

		taskPage.clickBackChevron();

		// Verify technician is navigated to WO details page
		String workOrderHeader = woDetailsPage.getTitle(woDetailsPage.workOrderHeaderText);
		assertEquals("Work order", workOrderHeader);

		woDetailsPage.clickBackChevronToListPage();
	}

	/**
	 * Method to create work orders, set organization settings, set system property
	 * and create custom status
	 * 
	 * @throws Exception
	 */
	protected void createDefaultObjects() throws Exception {
		logger.info("Creating custom status");
		createCustomStatus();
		logger.info("Creating work orders with tasks");
		createFlowControlCustomStatusWO();
		logger.info("Setting organization settings");
		logger.info("****************************PM***************CUSTOMCOMP************************************");
		setWorkTypeStatus(SetupData.WorkType.PM.toString(), WoStatus.INPRG.toString(), CUSTOMCOMP);
		logger.info("**********************************CM********COMP********************************************");
		setWorkTypeStatus(SetupData.WorkType.CM.toString(), WoStatus.APPR.toString(), WoStatus.COMP.toString());
		logger.info("**********************************CP********COMP********************************************");
		setWorkTypeStatus(SetupData.WorkType.CP.toString(), WoStatus.WSCH.toString(), WoStatus.COMP.toString());
		logger.info("Setting system settings");
		checkStartTimerInprg("1");
		logger.info("Setting system property");
		updateSystemProperties();
	}

	/**
	 * Method to create custom status
	 * 
	 * @throws Exception
	 */
	public void createCustomStatus() throws Exception {
		logger.info("Custom status inside method");

		String domainResultString = maximoApi.retrieve(new Domain(),
				"addid=1&internalvalues=1&oslc.where=domainid=%22WOSTATUS%22");

		logger.info("domainResult:" + domainResultString);
		JSONObject domainResultJson = new JSONObject(domainResultString);
		JSONArray memberJson = domainResultJson.getJSONArray("member");
		JSONObject hrefJson = memberJson.getJSONObject(0);
		String hrefString = hrefJson.getString("href");

		logger.info("locationURL: " + hrefString);

		Domain sd = new Domain();

		List<SynonymDomainValue> domainvalue = new ArrayList<SynonymDomainValue>();
		domainvalue.add(new SynonymDomainValue("WOSTATUS", WoStatus.COMP.toString(), CUSTOMCOMP,
				"WOSTATUS|" + CUSTOMCOMP, "Custom Completed", false, null, null, "AddChange"));
		sd.setLocationUrl(hrefString);
		sd.setSynonymDomainValue(domainvalue);

		maximoApi.update(sd);
		logger.info("WOSTATUS updated");
		logger.info("Custom status created successfully");
	}

	/**
	 * Method to set start status and complete status of worktype
	 * 
	 * @throws Exception
	 */
	public void setWorkTypeStatus(String workType, String startStatus, String compStatus) throws Exception {
		String workTypeResult = null;
		logger.info("Worktype >" + workType);
		if (SetupData.WorkType.EM.toString().equalsIgnoreCase(workType)) {
			logger.info("Set Start Status as APPR and Complete Status as CUSTOMCOMP for EM worktype");
			workTypeResult = maximoApi.retrieve(new WorkType(),
					"addid=1&internalvalues=1&oslc.where=worktype=%22" + SetupData.WorkType.EM.toString()
							+ "%22%20and%20orgid=%22" + SetupData.ORGID
							+ "%22%20and%20woclass=%22WORKORDER%22&oslc.select=*");
		} else if (SetupData.WorkType.CM.toString().equalsIgnoreCase(workType)) {
			logger.info("Set Start Status as APPR and Complete Status as COMP for CM worktype");
			workTypeResult = maximoApi.retrieve(new WorkType(),
					"addid=1&internalvalues=1&oslc.where=worktype=%22" + SetupData.WorkType.CM.toString()
							+ "%22%20and%20orgid=%22" + SetupData.ORGID
							+ "%22%20and%20woclass=%22WORKORDER%22&oslc.select=*");
		} else if (SetupData.WorkType.CP.toString().equalsIgnoreCase(workType)) {
			logger.info("Set Start Status as WSCH and Complete Status as COMP for CP worktype");
			workTypeResult = maximoApi.retrieve(new WorkType(),
					"addid=1&internalvalues=1&oslc.where=worktype=%22" + SetupData.WorkType.CP.toString()
							+ "%22%20and%20orgid=%22" + SetupData.ORGID
							+ "%22%20and%20woclass=%22WORKORDER%22&oslc.select=*");
		} else if (SetupData.WorkType.PM.toString().equalsIgnoreCase(workType)) {
			logger.info("Set Start Status as INPRG and Complete Status as CUSTOMCOMP for PM worktype");
			workTypeResult = maximoApi.retrieve(new WorkType(),
					"addid=1&internalvalues=1&oslc.where=worktype=%22" + SetupData.WorkType.PM.toString()
							+ "%22%20and%20orgid=%22" + SetupData.ORGID
							+ "%22%20and%20woclass=%22WORKORDER%22&oslc.select=*");
		}

		logger.info("Worktype settings API >" + workTypeResult);
		JSONObject jsonObject = new JSONObject(workTypeResult);
		JSONArray jsonArray = jsonObject.getJSONArray("member");
		jsonObject = new JSONObject(jsonArray.get(0).toString());
		String locationUrl = jsonObject.getString("href");

		WorkType newWorkType = new Gson().fromJson(jsonArray.get(0).toString(), WorkType.class);
		newWorkType.setLocationUrl(locationUrl);
		newWorkType.setWorkType(workType);
		newWorkType.setOrgId(SetupData.ORGID);
		newWorkType.setWoClass("WORKORDER");
		newWorkType.setStartStatus(startStatus);
		newWorkType.setCompleteStatus(compStatus);
		maximoApi.update(newWorkType);
		logger.info("Worktype status set successfully");
	}

	/**
	 * Method to click checkbox of "Automatically change work order status to INPRG
	 * when a user starts a labor timer"
	 * 
	 * @throws Exception
	 */
	public void checkStartTimerInprg(String value) {
		logger.info("Automatically change work order status to INPRG when a user starts a labor timer");
		jdbcConnection.executeUpdateSQL("UPDATE MAXIMO.MAXVARS SET MAXIMO.MAXVARS.VARVALUE = '" + value
				+ "' WHERE MAXIMO.MAXVARS.VARNAME = 'STARTTIMERINPRG'");
	}

	/**
	 * Method to update the maximo.mobile.gotoreportwork property to 0
	 * 
	 * @throws Exception
	 */
	protected void updateSystemProperties() throws Exception {
		logger.info("Set the value of maximo.mobile.gotoreportwork to 0");
		String value = String.valueOf(0);
		maximoApi.setProperty("maximo.mobile.gotoreportwork", "COMMON", null, value);
		maximoApi.setProperty("maximo.mobile.usetimer", "COMMON", null, "1");
		logger.info("Property set successfully");
	}

	/**
	 * Method to create flow controlled work order and tasks with no predecessor
	 * WORKTYPE.STARTSTATUS filled in as APPR and WORKTYPE.COMPLETESTATUS filled in
	 * as CUSTOMCOMP
	 * 
	 * @throws Exception
	 */
	public void createFlowControlCustomStatusWO() throws Exception {
		// Create work order2

		String workOrderResult = maximoApi.retrieve(new WorkOrder(),
				"addid=1&internalvalues=1&action=system:new&addschema=1");
		newWorkOrder = new Gson().fromJson(workOrderResult, WorkOrder.class);
		newWorkOrder.setDescription("WO with flowcontrol and custom status");
		newWorkOrder.setSiteId(SetupData.SITEID);
		newWorkOrder.setWorkType(SetupData.WorkType.EM.toString());
		newWorkOrder.setGLAccount(SetupData.GLDEBITACCT);
		newWorkOrder.setFlowControlled(true);
		maximoApi.create(newWorkOrder);
		woNum2 = newWorkOrder.getWoNum();
		logger.info("woNum2: " + woNum2);

		// Create task1
		List<Task> arr = new ArrayList<>();
		Task task1 = new Task();
		task1.setEstdur(Float.parseFloat("1.5"));
		task1.setSiteid(SetupData.SITEID);
		task1.setOwnergroup(SetupData.OWNERGROUP);
		task1.setParentchgsstatus(true);
		taskNum = AbstractAutomationFramework.randomIntRange(1, 1000);
		task1.setTaskid(taskNum);
		arr.add(task1);
		newWorkOrder.setWoactivity(arr);
		maximoApi.update(newWorkOrder);
		logger.info("taskNum1: " + taskNum);

		// Create task2
		List<Task> arr1 = new ArrayList<>();
		Task task2 = new Task();
		task2.setEstdur(Float.parseFloat("1.5"));
		task2.setSiteid(SetupData.SITEID);
		task2.setOwnergroup(SetupData.OWNERGROUP);
		task2.setParentchgsstatus(true);
		taskNum = AbstractAutomationFramework.randomIntRange(1, 1000);
		task2.setTaskid(taskNum);
		arr1.add(task2);
		newWorkOrder.setWoactivity(arr1);
		maximoApi.update(newWorkOrder);
		logger.info("taskNum2: " + taskNum);

		// Assign the labor
		maximoApi.addAssignmentLabor(newWorkOrder, labor);
		logger.info("Assign the labor");

		// Change WO status to Approved
		maximoApi.changeStatus(newWorkOrder, WoStatus.APPR.toString());
		logger.info("Changing work order status to APPR");
	}
}