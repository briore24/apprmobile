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
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.maximo.automation.mobile.AbstractAutomationFramework;
import com.ibm.maximo.automation.mobile.FrameworkFactory;
import com.ibm.maximo.automation.mobile.api.MaximoApi;
import com.ibm.maximo.automation.mobile.api.json.Domain;
import com.ibm.maximo.automation.mobile.api.json.SynonymDomainValue;
import com.ibm.maximo.automation.mobile.api.json.Task;
import com.ibm.maximo.automation.mobile.api.json.WoTaskRelation;
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
public class TaskFlowControlTestCaseOne extends TechnicianTest {
	private final Logger logger = LoggerFactory.getLogger(TaskFlowControlTestCaseOne.class);
	private AbstractAutomationFramework af;
	private TestSuite testSuite;
	private MaximoApi maximoApi;
	private String CUSTOMCOMP = "CUSTOMCOMP";
	private String woNum1, woNum4, woNum5, labor, masServer;
	private String taskWrapper = "q439v_items_datalistWrapper";
	private int taskNum;
	private String statusWrapper = "z4q5w_items_datalistWrapper";
	private List<Task> arr, arr1;

	@BeforeClass(alwaysRun = true)
	@Parameters({ "configPath" })
	public void setup(String configPath) throws Exception {
		logger.info("********************TaskFlowControlTestCaseOne*********************************");
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
		jdbcConnection.executeUpdateSQL("UPDATE WORKORDER SET STATUS = 'COMP' WHERE STATUS= 'CUSTOMCOMP'");
		logOut(af);
		// Delete 'CUSTOMCOMP' via DB query
		jdbcConnection.executeUpdateSQL("DELETE FROM MAXIMO.SYNONYMDOMAIN WHERE SYNONYMDOMAIN.VALUE ='CUSTOMCOMP'");
		checkStartTimerInprg("0");
		
		if (testSuite != null) {
			testSuite.teardown();
		}
	}

	@Test(groups = {
			"mobile" }, description = "Flow controlled WO and tasks with no predecessor when WORKTYPE.STARTSTATUS is not filled in", timeOut = 15000000)
	public void flowControl1() throws Exception {
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage woDetailsPage = new WorkOrderDetailsPage(af);
		ReportWorkPage reportWorkPage = new ReportWorkPage(af);
		TaskPage taskPage = new TaskPage(af);

		logger.info("Search work order woNum1>" + woNum1);
		assignedWorkPage.search(woNum1);
		logger.info("Click search");

		assignedWorkPage.clickStartWorkButton();
		logger.info("Click start work button");

		woDetailsPage.clickBackChevronToListPage();
		logger.info("Back chevron clicked");

		assertEquals("In progress", assignedWorkPage.verifyChangedStatus());
		logger.info("Status has verified");

		assignedWorkPage.openWorkOrderDetails();
		logger.info("Open Details page");

		woDetailsPage.clickTaskButton();
		logger.info("Click Task Button");

		assertEquals("Approved", taskPage.verifyTaskStatus());
		logger.info("Approved status verification for first Task");

		taskPage.taskChevron(taskWrapper, 0);
		taskPage.taskChevron(taskWrapper, 1);
		assertEquals("Approved", taskPage.verifyTaskStatus());
		logger.info("Approved status verification for second Task");

		// navigate back to work order details page
		taskPage.clickBackChevron();

		woDetailsPage.openStatusList();

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

		for (int i = 0; i < 1; i++) {
			taskPage.clickDoneTaskButton(i);
		}

		taskPage.clickBackChevron();
		logger.info("Click Back chevron of Task");

		woDetailsPage.clickTaskButton();

		taskPage.completeTask("pb_mv[1]");
		logger.info("Complete Task 2");

		taskPage.clickBackChevron();
		logger.info("Click Back chevron of Task after task 2 complete");

		woDetailsPage.clickTaskButton();

		taskPage.taskChevron(taskWrapper, 0);
		assertEquals("Completed", taskPage.verifyTaskStatus());
		logger.info("Task 1 completed");

		taskPage.taskChevron(taskWrapper, 1);
		assertEquals("Completed", taskPage.verifyTaskStatus());
		logger.info("Task 2 completed");

		taskPage.clickBackChevron();
		logger.info("Task Page Back chevron clicked");

		String workOrderHeader = woDetailsPage.getTitle(woDetailsPage.workOrderHeaderText);
		assertEquals("Work order", workOrderHeader);

		assertEquals("Completed", woDetailsPage.verifyChangedStatus());
		logger.info("Verify the status of work order is changed to 'Custom Completed'");

		woDetailsPage.clickBackChevronToListPage();
	}

	@Test(groups = {
			"mobile" }, enabled = true, description = "Normal WO with tasks and predecessor with WORKTYPE.STARTSTATUS filled in", timeOut = 1500000)
	public void flowControl4() throws Exception {
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage woDetailsPage = new WorkOrderDetailsPage(af);
		TaskPage taskPage = new TaskPage(af);
		ReportWorkPage reportPage = new ReportWorkPage(af);

		logger.info("Search work order woNum4>" + woNum4);

		assignedWorkPage.clickClearButton();
		assignedWorkPage.search(woNum4);

		assignedWorkPage.clickStartWorkButton();
		logger.info("Click start work button");

		woDetailsPage.clickBackChevron();
		logger.info("Back chevron clicked");

		assertEquals("In progress", assignedWorkPage.verifyChangedStatus());
		logger.info("Status has verified");

		assignedWorkPage.openWorkOrderDetails();
		logger.info("Open Details page");

		woDetailsPage.clickTaskButton();

		assertEquals("In progress", taskPage.verifyTaskStatus());
		taskPage.taskChevron(taskWrapper, 0);
		taskPage.taskChevron(taskWrapper, 1);
		assertEquals("In progress", taskPage.verifyTaskStatus());

		taskPage.clickBackChevron();
		logger.info("Back to work order details page");

		woDetailsPage.clickReportWorkButton();
		logger.info("Report work button clicked");

		reportPage.completeReportWorkButton();
		logger.info("Complete Work button is enabled and clicked");

		assignedWorkPage.clickClearButton();
		logger.info("Back chevron  from details");
	}

	@Test(groups = {
			"mobile" }, enabled = true, description = "Normal WO with tasks and predecessor with WORKTYPE.STARTSTATUS filled in", timeOut = 800000)
	public void flowControl5() throws Exception {
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage woDetailsPage = new WorkOrderDetailsPage(af);
		TaskPage taskPage = new TaskPage(af);
		ReportWorkPage reportPage = new ReportWorkPage(af);

		logger.info("Search work order woNum5>" + woNum5);

		assignedWorkPage.search(woNum5);
		assignedWorkPage.clickStartWorkButton();
		logger.info("Click start work button");

		woDetailsPage.clickBackChevron();
		logger.info("Back chevron clicked");

		assertEquals("In progress", assignedWorkPage.verifyChangedStatus());
		logger.info("Status has verified");

		assignedWorkPage.openWorkOrderDetails();
		logger.info("Open Details page");

		woDetailsPage.clickTaskButton();
		logger.info("Task Button clicked");

		assertEquals("In progress", taskPage.verifyTaskStatus());
		taskPage.taskChevron(taskWrapper, 0);
		taskPage.taskChevron(taskWrapper, 1);
		assertEquals("In progress", taskPage.verifyTaskStatus());

		taskPage.clickBackChevron();
		logger.info("Back to work order details page");

		woDetailsPage.clickReportWorkButton();
		logger.info("Report work button clicked");

		reportPage.completeReportWorkButton();
		logger.info("Complete Work button is enabled and clicked");

	}

	/**
	 * Method to create work orders, set organization settings, set system property
	 * and create custom status
	 * 
	 * @throws Exception
	 */
	protected void createDefaultObjects() throws Exception {
		systemPropertiesOfAuth("1");
		updateSystemPropertiesForAuth("0");
		createFlowControlWO();
		createDefaultWithoutPredWO();
		createDefaultWithPredWO();
		logger.info("Creating custom status");
		createCustomStatus();
		logger.info("Setting organization settings");
		setWorkTypeStatus(SetupData.WorkType.CM.toString(), WoStatus.APPR.toString(), WoStatus.COMP.toString());
		logger.info("Setting system settings");
		checkStartTimerInprg("1");
		logger.info("Setting system property");
		updateSystemProperties();
		logger.info("Creating work orders with tasks");

	}
	
	// Generated by WCA for GP
	/**
	 * Updates the system properties for the authentication.
	 *
	 * @param value the value to set
	 * @throws Exception if an error occurs
	 */
	void updateSystemPropertiesForAuth(String value) throws Exception {
		logger.info("Set the value of mxe.int.enableosauth to "+value);
		maximoApi.setProperty("mxe.int.enableosauth", "COMMON", null, value);
		logger.info("Property set successfully for mxe.int.enableosauth to "+value);
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
		domainvalue.add(new SynonymDomainValue(null, WoStatus.COMP.toString(), CUSTOMCOMP, "WOSTATUS|" + CUSTOMCOMP,
				"Custom Completed", false, null, null, "AddChange"));
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
					"addid=1&internalvalues=1&oslc.where=worktype=%22"+SetupData.WorkType.EM.toString()+"%22%20and%20orgid=%22" + SetupData.ORGID
							+ "%22%20and%20woclass=%22WORKORDER%22&oslc.select=*");
		} else if (SetupData.WorkType.CM.toString().equalsIgnoreCase(workType)) {
			logger.info("Set Start Status as APPR and Complete Status as COMP for CM worktype");
			workTypeResult = maximoApi.retrieve(new WorkType(),
					"addid=1&internalvalues=1&oslc.where=worktype=%22"+SetupData.WorkType.CM.toString()+"%22%20and%20orgid=%22" + SetupData.ORGID
							+ "%22%20and%20woclass=%22WORKORDER%22&oslc.select=*");
		} else if (SetupData.WorkType.CP.toString().equalsIgnoreCase(workType)) {
			logger.info("Set Start Status as WSCH and Complete Status as COMP for CP worktype");
			workTypeResult = maximoApi.retrieve(new WorkType(),
					"addid=1&internalvalues=1&oslc.where=worktype=%22"+SetupData.WorkType.CP.toString()+"%22%20and%20orgid=%22" + SetupData.ORGID
							+ "%22%20and%20woclass=%22WORKORDER%22&oslc.select=*");
		} else if (SetupData.WorkType.PM.toString().equalsIgnoreCase(workType)) {
			logger.info("Set Start Status as INPRG and Complete Status as CUSTOMCOMP for PM worktype");
			workTypeResult = maximoApi.retrieve(new WorkType(),
					"addid=1&internalvalues=1&oslc.where=worktype=%22"+SetupData.WorkType.PM.toString()+"%22%20and%20orgid=%22" + SetupData.ORGID
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
	 * Method to create a normal work order and tasks with predecessor and
	 * WORKTYPE.STARTSTATUS filled in as APPR and WORKTYPE.COMPLETESTATUS filled in
	 * as COMP
	 * 
	 * @throws Exception
	 */
	public void createDefaultWithoutPredWO() throws Exception {
		// Create work order4
		String workOrderResult = maximoApi.retrieve(new WorkOrder(),
				"addid=1&internalvalues=1&action=system:new&addschema=1");
		WorkOrder newWorkOrder = new Gson().fromJson(workOrderResult, WorkOrder.class);
		newWorkOrder.setDescription("WO without flowcontrol with predecessor");
		newWorkOrder.setSiteId(SetupData.SITEID);
		newWorkOrder.setWorkType(SetupData.WorkType.CM.toString());
		newWorkOrder.setGLAccount(SetupData.GLDEBITACCT);
		maximoApi.create(newWorkOrder);
		woNum4 = newWorkOrder.getWoNum();
		logger.info("woNum4: " + woNum4);

		// Create task1
		arr1 = new ArrayList<>();
		Task task1 = new Task();
		task1.setEstdur(Float.parseFloat("1.5"));
		task1.setSiteid(SetupData.SITEID);
		task1.setOwnergroup(SetupData.OWNERGROUP);
		task1.setParentchgsstatus(true);
		// taskNum = AbstractAutomationFramework.randomInt();
		task1.setTaskid(Integer.parseInt("10"));
		String woNumTask1 = task1.getReferenceWo();
		arr1.add(task1);
		// newWorkOrder.setWoactivity(arr1);
		maximoApi.update(newWorkOrder);
		logger.info("taskNum1: " + 10);

		// Create task2
		Task task2 = new Task();
		task2.setEstdur(Float.parseFloat("1.5"));
		task2.setSiteid(SetupData.SITEID);
		task2.setOwnergroup(SetupData.OWNERGROUP);
		task2.setParentchgsstatus(true);
		// taskNum1 = AbstractAutomationFramework.randomInt();
		task2.setTaskid(Integer.parseInt("20"));
		// task2.setPredessorWos(String.valueOf(taskNum));
		String woNumTask2 = task2.getReferenceWo();
		arr1.add(task2);
		newWorkOrder.setWoactivity(arr1);
		maximoApi.update(newWorkOrder);
		logger.info("taskNum2: " + 20);

		// Assign the labor
		maximoApi.addAssignmentLabor(newWorkOrder, labor);
		logger.info("Assignment added");

		// Change WO status to Approved
		logger.info("Changing work order status to APPR");
		maximoApi.changeStatus(newWorkOrder, WoStatus.APPR.toString());
		woNum = woNum4;

		// TODO : EAM below code will not work , ivt15 Env below code will work
		String result = maximoApi.retrieve(new WorkOrder(),
				"&collectioncount=1&ignorecollectionref=1&relativeuri=1&lean=1&internalvalues=1&addid=1&internalvalues=1&oslc.select=woNum,woactivity&oslc.where=wonum="
						+ woNum);

		@SuppressWarnings("deprecation")
		JsonParser jsonParser = new JsonParser();

		@SuppressWarnings("deprecation")
		JsonObject teste = jsonParser.parse(result).getAsJsonObject();

		newWorkOrder = new Gson().fromJson(teste.get("member").getAsJsonArray().get(0).toString(), WorkOrder.class);

		for (Task temp : newWorkOrder.getWoactivity()) {
			temp.set_id(temp.get_id());
			if (temp.getTaskid() == 10) {
				woNumTask1 = temp.getReferenceWo();
			} else {
				woNumTask2 = temp.getReferenceWo();
			}
		}
	}

	/**
	 * Method to create flow controlled work order and tasks when
	 * WORKTYPE.STARTSTATUS is not provided
	 * 
	 * @throws Exception
	 */
	public void createFlowControlWO() throws Exception {
		// Create wonum1
		woNum1 = AbstractAutomationFramework.randomString(5).toUpperCase();
		String workOrderResult = maximoApi.retrieve(new WorkOrder(),
				"addid=1&internalvalues=1&action=system:new&addschema=1");
		WorkOrder newWorkOrder = new Gson().fromJson(workOrderResult, WorkOrder.class);
		newWorkOrder.setWoNum(woNum1);
		newWorkOrder.setDescription("WO with flowcontrol");
		newWorkOrder.setSiteId(SetupData.SITEID);
		newWorkOrder.setGLAccount(SetupData.GLDEBITACCT);
		newWorkOrder.setFlowControlled(true);
		maximoApi.create(newWorkOrder);
		newWorkOrder.setWorkType(SetupData.WorkType.CM.toString());

		// Create task1
		arr = new ArrayList<>();
		Task task1 = new Task();
		task1.setEstdur(Float.parseFloat("1.5"));
		task1.setSiteid(SetupData.SITEID);
		task1.setOwnergroup(SetupData.OWNERGROUP);
		task1.setParentchgsstatus(true);
		taskNum = AbstractAutomationFramework.randomInt();
		task1.setTaskid(taskNum);
		arr.add(task1);
		// newWorkOrder.setWoactivity(arr);
		maximoApi.update(newWorkOrder);
		logger.info("taskNum1: " + taskNum);

		// Create task2
		List<Task> arr1 = new ArrayList<>();
		Task task2 = new Task();
		task2.setEstdur(Float.parseFloat("1.5"));
		task2.setSiteid(SetupData.SITEID);
		task2.setOwnergroup(SetupData.OWNERGROUP);
		task2.setParentchgsstatus(true);
		taskNum = AbstractAutomationFramework.randomInt();
		task2.setTaskid(taskNum);
		arr.add(task2);
		newWorkOrder.setWoactivity(arr);
		maximoApi.update(newWorkOrder);
		logger.info("taskNum2: " + taskNum);

		// Assign the labor
		maximoApi.addAssignmentLabor(newWorkOrder, labor);
		logger.info("Assignment added");

		// Change WO status to Approved
		logger.info("Changing work order status to APPR");
		maximoApi.changeStatus(newWorkOrder, WoStatus.APPR.toString());
	}

	/**
	 * Method to create a normal work order and tasks with predecessor and
	 * WORKTYPE.STARTSTATUS filled in as APPR and WORKTYPE.COMPLETESTATUS filled in
	 * as COMP
	 * 
	 * @throws Exception
	 */
	public void createDefaultWithPredWO() throws Exception {
		// Create work order5
		String workOrderResult = maximoApi.retrieve(new WorkOrder(),
				"addid=1&internalvalues=1&action=system:new&addschema=1");
		WorkOrder newWorkOrder = new Gson().fromJson(workOrderResult, WorkOrder.class);

		newWorkOrder.setDescription("WO without flowcontrol with predecessor");
		newWorkOrder.setSiteId(SetupData.SITEID);
		newWorkOrder.setWorkType(SetupData.WorkType.CM.toString());
		newWorkOrder.setGLAccount(SetupData.GLDEBITACCT);
		maximoApi.create(newWorkOrder);
		woNum5 = newWorkOrder.getWoNum();
		logger.info("woNum5: " + woNum5);

		// Create task1
		arr1 = new ArrayList<>();
		Task task1 = new Task();
		task1.setEstdur(Float.parseFloat("1.5"));
		task1.setSiteid(SetupData.SITEID);
		task1.setOwnergroup(SetupData.OWNERGROUP);
		task1.setParentchgsstatus(true);
		// taskNum = AbstractAutomationFramework.randomInt();
		task1.setTaskid(Integer.parseInt("10"));
		String woNumTask1 = task1.getReferenceWo();
		arr1.add(task1);
		// newWorkOrder.setWoactivity(arr1);
		maximoApi.update(newWorkOrder);
		logger.info("taskNum1: " + 10);

		// Create task2
		Task task2 = new Task();
		task2.setEstdur(Float.parseFloat("1.5"));
		task2.setSiteid(SetupData.SITEID);
		task2.setOwnergroup(SetupData.OWNERGROUP);
		task2.setParentchgsstatus(true);
		// taskNum1 = AbstractAutomationFramework.randomInt();
		task2.setTaskid(Integer.parseInt("20"));
		// task2.setPredessorWos(String.valueOf(taskNum));
		String woNumTask2 = task2.getReferenceWo();
		arr1.add(task2);
		newWorkOrder.setWoactivity(arr1);
		maximoApi.update(newWorkOrder);
		logger.info("taskNum2: " + 20);

		// Assign the labor
		maximoApi.addAssignmentLabor(newWorkOrder, labor);
		logger.info("Assignment added");

		// Change WO status to Approved
		logger.info("Changing work order status to APPR");
		maximoApi.changeStatus(newWorkOrder, WoStatus.APPR.toString());
		woNum = woNum4;

		String result = maximoApi.retrieve(new WorkOrder(),
				"&collectioncount=1&ignorecollectionref=1&relativeuri=1&lean=1&internalvalues=1&addid=1&internalvalues=1&oslc.select=woNum,woactivity&oslc.where=wonum="
						+ woNum);

		@SuppressWarnings("deprecation")
		JsonParser jsonParser = new JsonParser();

		@SuppressWarnings("deprecation")
		JsonObject teste = jsonParser.parse(result).getAsJsonObject();

		newWorkOrder = new Gson().fromJson(teste.get("member").getAsJsonArray().get(0).toString(), WorkOrder.class);

		for (Task temp : newWorkOrder.getWoactivity()) {
			temp.set_id(temp.get_id());
			if (temp.getTaskid() == 10) {
				woNumTask1 = temp.getReferenceWo();
			} else {
				woNumTask2 = temp.getReferenceWo();
			}
		}

		// Setting Task10 as Predecessor of Task20
		logger.info("Predecessor API Call started ");
		WoTaskRelation newWoTaskRelation = new WoTaskRelation();
		newWoTaskRelation
				.setLocationUrl(maximoApi.getMaximoServer() + "/api/os/" + WoTaskRelation.RESOURCE + "?lean=1");
		newWoTaskRelation.setWoNum(woNumTask2);
		newWoTaskRelation.setPredWoNum(woNum);
		newWoTaskRelation.setPredRefWoNum(woNumTask1);
		newWoTaskRelation.setPredTaskId("10");
		newWoTaskRelation.setRelType(SetupData.RELTYPE);
		newWoTaskRelation.setSiteID(SetupData.SITEID);
		newWoTaskRelation.setOrgID(SetupData.ORGID);
		maximoApi.create(newWoTaskRelation);
	}
}