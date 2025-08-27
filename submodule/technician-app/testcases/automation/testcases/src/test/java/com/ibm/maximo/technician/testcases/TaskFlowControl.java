package com.ibm.maximo.technician.testcases;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

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
import com.ibm.maximo.automation.mobile.api.json.Asset;
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
public class TaskFlowControl extends TechnicianTest {
	private final Logger logger = LoggerFactory.getLogger(TaskFlowControl.class);
	private AbstractAutomationFramework af;
	private TestSuite testSuite;
	private MaximoApi maximoApi;
	private String woNum3, labor, masServer;
	private String taskDesc31, taskLongDesc31, taskDesc32, taskLongDesc32;
	private String taskWrapper = "q439v_items_datalistWrapper";
	private String statusWrapper = "z4q5w_items_datalistWrapper";
	private String taskMessage = "Complete task 10 first.";
	private String completeTask10Mark="pb_mv[0]";
	private String completeTask20Mark="pb_mv[1]";
	private static final String ASSET_DESCRIPTION = "Asset for mobile automation test";
	
	@BeforeClass(alwaysRun = true)
	@Parameters({ "configPath" })
	public void setup(String configPath) throws Exception {
		logger.info("********************TaskFlowControl*********************************");
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
		checkStartTimerInprg("0");
		logOut(af);
		if (testSuite != null) {
			testSuite.teardown();
		}
	}



	@Test(groups = {
			"mobile" }, enabled = true, description = "Flow controlled WO and tasks with predecessor when WORKTYPE.STARTSTATUS is filled in as 'APPR'", timeOut = 900000)
	public void flowControl3() throws Exception {
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage woDetailsPage = new WorkOrderDetailsPage(af);
		TaskPage taskPage = new TaskPage(af);
		ReportWorkPage reportWorkPage = new ReportWorkPage(af);

		logger.info("Search work order woNum3>" + woNum3);
		assignedWorkPage.search(woNum3);
		assignedWorkPage.clickStartWorkButton();
		// Check Barcode Scanner
		if (woDetailsPage.isScanDialogExists()) {
			woDetailsPage.scanBarcode();
			logger.info("Scanner Skipped");
		}
		woDetailsPage.clickBackChevron();
		assertEquals("In progress", assignedWorkPage.verifyChangedStatus());
		logger.info("Status has verified");
		
		assignedWorkPage.openWorkOrderDetails();
		woDetailsPage.clickTaskButton();

		
		// Complete task1 and verify the task status
		taskPage.completeTask("pb_mv[0]");
		
		taskPage.clickBackChevron();
		
		logger.info("Out from Task page");
		
         woDetailsPage.openStatusList();
         logger.info("Open status list");
		
		DataListComponent list = af.instantiateComponent(DataListComponent.class, statusWrapper);
		List<DataListItemComponent> items = list.getChildren();
		
		for (DataListItemComponent status : items) {
			logger.info("status",status.getText());
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
		

		taskPage.taskChevron(taskWrapper, 1);
		assertEquals("Completed", taskPage.verifyTaskStatus());
		logger.info(" Verify Task Status is completed");
		
		taskPage.clickBackChevron();
		logger.info("Navigating back to details page");

		// Verify technician is navigated to WO details page
		String workOrderHeader = woDetailsPage.getTitle(woDetailsPage.workOrderHeaderText);
		assertEquals("Work order", workOrderHeader);
		woDetailsPage.clickBackChevron();
	}

	@Test(groups = {
			"priority2" }, enabled = true, description = "Verify that dependent tasks are locked and collapsed when parent task is yet to be completed and work order is flow controlled'", timeOut = 1500000)
	public void flowControl4() throws Exception {
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage woDetailsPage = new WorkOrderDetailsPage(af);
		TaskPage taskPage = new TaskPage(af);

		assignedWorkPage.search(woNum3);
		logger.info("Search work order woNum3>" + woNum3);
		assignedWorkPage.clickStartWorkButton();
		logger.info("Click on Start Work Order Button");
		
		// Check Barcode Scanner
		if (woDetailsPage.isScanDialogExists()) {
			woDetailsPage.scanBarcode();
			logger.info("Scanner Skipped");
		}

		woDetailsPage.clickTaskButton();
		logger.info("click on task button");

		assertEquals(true, taskPage.isApprovedPresent());
		logger.info("Verify isApproved Button is visible");

		taskPage.isCompleteTaskPresent(completeTask10Mark);
		logger.info("Verify Complete task Blue tick mark is visible");

		assertEquals(true, taskPage.verifyLockIcon());
		logger.info("Verify Lock Icon is visible");

		taskPage.clickOnSecondTask();
		logger.info("Task 20 is expanded");

		assertTrue(taskMessage.equals(taskPage.getLockedTaskText()));
		logger.info("Verify task message 'Complete task 10 first'");

		taskPage.completeTask(completeTask10Mark);
		logger.info("Click on Complete task 10");

		taskPage.isCompleteTaskPresent(completeTask20Mark);
		logger.info("Verify task 20 Complete blue tick mark present ");

		assertEquals(true, taskPage.isApprovedPresent());
		logger.info("Verify task 20 Apporove button is displayed ");

		assertEquals(true, taskPage.isGreenTickPresent());
		logger.info("Verify task 10 Green Tick is displayed ");

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
		logger.info("Creating work orders with tasks");
		createFlowControlPredWO();
		logger.info("Setting system settings");
		checkStartTimerInprg("1");
		logger.info("Setting system property");
		updateSystemProperties();
		setWorkTypeStatus(SetupData.WorkType.CM.toString(), WoStatus.APPR.toString(), WoStatus.COMP.toString());
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
					"addid=1&internalvalues=1&oslc.where=worktype=%22"+SetupData.WorkType.EM.toString()+"%22%20and%20orgid=%22"+SetupData.ORGID+"%22%20and%20woclass=%22WORKORDER%22&oslc.select=*");
		} else if (SetupData.WorkType.CM.toString().equalsIgnoreCase(workType)) {
			logger.info("Set Start Status as APPR and Complete Status as COMP for CM worktype");
			workTypeResult = maximoApi.retrieve(new WorkType(),
					"addid=1&internalvalues=1&oslc.where=worktype=%22"+SetupData.WorkType.CM.toString()+"%22%20and%20orgid=%22"+SetupData.ORGID+"%22%20and%20woclass=%22WORKORDER%22&oslc.select=*");
		} else if (SetupData.WorkType.CP.toString().equalsIgnoreCase(workType)) {
			logger.info("Set Start Status as WSCH and Complete Status as COMP for CP worktype");
			workTypeResult = maximoApi.retrieve(new WorkType(),
					"addid=1&internalvalues=1&oslc.where=worktype=%22"+SetupData.WorkType.CP.toString()+"%22%20and%20orgid=%22"+SetupData.ORGID+"%22%20and%20woclass=%22WORKORDER%22&oslc.select=*");
		} else if (SetupData.WorkType.PM.toString().equalsIgnoreCase(workType)) {
			logger.info("Set Start Status as INPRG and Complete Status as CUSTOMCOMP for PM worktype");
			workTypeResult = maximoApi.retrieve(new WorkType(),
					"addid=1&internalvalues=1&oslc.where=worktype=%22"+SetupData.WorkType.PM.toString()+"%22%20and%20orgid=%22"+SetupData.ORGID+"%22%20and%20woclass=%22WORKORDER%22&oslc.select=*");
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
		logger.info("Property set successfully");
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
	 * Method to create flow controlled work order and tasks with predecessor and
	 * WORKTYPE.STARTSTATUS filled in as APPR and WORKTYPE.COMPLETESTATUS filled in
	 * as COMP
	 * 
	 * @throws Exception
	 */
	public void createFlowControlPredWO() throws Exception {
		// Create an asset
		logger.info("Creating an asset");
		assetNum = AbstractAutomationFramework.randomString(5).toUpperCase();
		String assetResult = maximoApi.retrieve(new Asset(), "addid=1&internalvalues=1&action=system:new&addschema=1");
		Asset newAsset = new Gson().fromJson(assetResult, Asset.class);
		newAsset.setAssetNum(assetNum);
		newAsset.setDescription(ASSET_DESCRIPTION);
		maximoApi.create(newAsset);
		logger.info("Asset: {}" + assetNum);
				
		// Create work order3
		String workOrderResult = maximoApi.retrieve(new WorkOrder(),
				"addid=1&internalvalues=1&action=system:new&addschema=1");
		WorkOrder newWorkOrder = new Gson().fromJson(workOrderResult, WorkOrder.class);
		newWorkOrder.setDescription("WO with flowcontrol and predecessor");
		newWorkOrder.setSiteId(SetupData.SITEID);
		newWorkOrder.setWorkType(SetupData.WorkType.CM.toString());
		newWorkOrder.setGLAccount(SetupData.GLDEBITACCT);
		newWorkOrder.setAssetNum(assetNum);
		newWorkOrder.setFlowControlled(true);
		maximoApi.create(newWorkOrder);
		woNum3 = newWorkOrder.getWoNum();
		logger.info("woNum3: " + woNum3);

		// Create task1
		List<Task> arr = new ArrayList<>();
		Task task1 = new Task();
		task1.setEstdur(Float.parseFloat("1.5"));
		task1.setSiteid(SetupData.SITEID);
		task1.setOwnergroup(SetupData.OWNERGROUP);
		taskDesc31 = AbstractAutomationFramework.randomString(8).toUpperCase();
		task1.setDescription(taskDesc31);
		taskLongDesc31 = AbstractAutomationFramework.randomString(10).toUpperCase();
		task1.setLongDescription(taskLongDesc31);
		task1.setParentchgsstatus(true);
		task1.setTaskid(Integer.parseInt("10"));
		task1.setStatus(WoStatus.WAPPR.toString());
		String woNumTask1 = task1.getReferenceWo();
		arr.add(task1);
		newWorkOrder.setWoactivity(arr);
		maximoApi.update(newWorkOrder);
		logger.info("taskNum1: " + 10);

		// Create task2
		List<Task> arr1 = new ArrayList<>();
		Task task2 = new Task();
		task2.setEstdur(Float.parseFloat("1.5"));
		task2.setSiteid(SetupData.SITEID);
		task2.setOwnergroup(SetupData.OWNERGROUP);
		taskDesc32 = AbstractAutomationFramework.randomString(10).toUpperCase();
		task2.setDescription(taskDesc32);
		taskLongDesc32 = AbstractAutomationFramework.randomString(10).toUpperCase();
		task2.setLongDescription(taskLongDesc32);
		task2.setParentchgsstatus(true);
//		taskNum1 = AbstractAutomationFramework.randomInt();
//		task2.setTaskid(taskNum1);
		task2.setTaskid(Integer.parseInt("20"));
		task2.setStatus(WoStatus.WAPPR.toString());
		String woNumTask2 = task2.getReferenceWo();
		// task2.setPredessorWos(String.valueOf(taskNum));
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
		woNum = woNum3;

		String result = maximoApi.retrieve(new WorkOrder(),
				"&collectioncount=1&ignorecollectionref=1&relativeuri=1&lean=1&internalvalues=1&addid=1&internalvalues=1&oslc.select=woNum,woactivity&oslc.where=wonum="
						+ woNum);
		logger.info("Predecessor API Call setup " + result);
		@SuppressWarnings("deprecation")
		JsonParser jsonParser = new JsonParser();

		@SuppressWarnings("deprecation")
		JsonObject teste = jsonParser.parse(result).getAsJsonObject();

		newWorkOrder = new Gson().fromJson(teste.get("member").getAsJsonArray().get(0).toString(), WorkOrder.class);

		for (Task temp : newWorkOrder.getWoactivity()) {
			temp.set_id(temp.get_id());
			if (temp.getTaskid() == 10) {
				woNumTask1 = temp.getReferenceWo();
				logger.info("woNumTask1 from woactivity" + woNumTask1);
			} else {
				woNumTask2 = temp.getReferenceWo();
				logger.info("woNumTask2 from woactivity" + woNumTask2);
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