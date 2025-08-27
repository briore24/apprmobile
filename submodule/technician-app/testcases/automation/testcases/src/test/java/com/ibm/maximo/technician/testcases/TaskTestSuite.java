package com.ibm.maximo.technician.testcases;

import static org.testng.Assert.assertEquals;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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
import com.ibm.maximo.automation.mobile.api.json.Asset;
import com.ibm.maximo.automation.mobile.api.json.Location;
import com.ibm.maximo.automation.mobile.api.json.Task;
import com.ibm.maximo.automation.mobile.api.json.WorkOrder;
import com.ibm.maximo.technician.framework.TechnicianTest;
import com.ibm.maximo.technician.page.MySchedulePage;
import com.ibm.maximo.technician.page.ReportWorkPage;
import com.ibm.maximo.technician.page.TaskPage;
import com.ibm.maximo.technician.page.WorkOrderDetailsPage;
import com.ibm.maximo.technician.setupdata.SetupData;
import com.ibm.maximo.technician.setupdata.SetupData.*;
/*
 * GRAPHITE-47692: Eli should be able to mark as complete tasks in a work order
 * GRAPHITE-50897: Display Asset/Location on Task when different than Parent WO
 * GRAPHITE-50846: Eli should go to the report work page after finishing the last task
 */
 
public class TaskTestSuite extends TechnicianTest {
	private final Logger logger = LoggerFactory.getLogger(TaskTestSuite.class);
	private AbstractAutomationFramework af;
	private TestSuite testSuite;
	private WorkOrder newWorkOrder;
	private MaximoApi maximoApi;
	private String assetNum, woNum, labor, location, assetNum1, location1, maximoStytemProp;
	private static final String ASSET_DESCRIPTION = "Asset Description ";
	private static final String WORKORDER_DESCRIPTION = "Work Order Description ";
	private static final String LOCATION_DESCRIPTION = "Location for mobile automation test";
	
	@BeforeClass(alwaysRun = true)
	@Parameters({ "configPath" })
	public void setup(String configPath) throws Exception {
		logger.info("********************TaskTestSuite*********************************");
		this.af = FrameworkFactory.get();
		Properties properties = new Properties();
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(configPath));
			properties.load(in);
			labor = properties.getProperty("system.username");
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
		logOut(af);
		// Change WO status to Completed
		logger.info("Changing work order status to COMP");
		maximoApi.changeStatus(newWorkOrder, WoStatus.COMP.toString());
		if (testSuite != null) {
			testSuite.teardown();
		}
	}

	@Test(groups = { "mobile" }, description = "Verify Task functionality", timeOut = 900000)
	public void taskFunctionality() throws Exception {
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage workOrderDetailsPage = new WorkOrderDetailsPage(af);
		TaskPage taskPage = new TaskPage(af);
		ReportWorkPage reprotWorkPage = new ReportWorkPage(af);
		// Scenario 1: Verify different asset and location on Task page
		logger.info("Scenario 1: Verify different asset and location on Task page");
		
		// Search the WO
		assignedWorkPage.search(woNum);
		// Navigate to Work Order Details page
		assignedWorkPage.openWorkOrderDetails();
		// Open Task Page
		workOrderDetailsPage.clickTaskButton();
		
		// Verify asset details on Task page are different from WO asset details
		assertEquals(assetNum1, taskPage.getAssetNum());
		logger.info("asset details on Task page are different from WO asset details");
		
		// Verify location details on Task page are different from WO location details
		assertEquals(location1, taskPage.getLocationNum());
		logger.info("location details on Task page are different from WO location details");
	
		// Verify asset icon
		assertEquals("carbon:asset", taskPage.getAssetIcon());
		logger.info("Verify asset icon");
		
		// Verify location icon
		assertEquals("carbon:location", taskPage.getLocationIcon());	
		logger.info("Verify location icon");	
		
		// Scenario 2: Verify user is able to mark tasks as Complete in a work order
		logger.info("Scenario 2: Verify user is able to mark tasks as Complete in a work order");
		
		// Verify that blue checkmark icon is displayed for the task when it is incomplete
		assertEquals("carbon:checkmark", taskPage.getBlueCheckmarkOnTaskPage(0));
		logger.info("blue checkmark icon is displayed for the task when it is incomplete");
		
		// Click on Change Status drawer
		taskPage.taskStatusChange();
		logger.info("Change Status drawer clicked");
		
		// Verify that technician marks a task completed
		taskPage.selectCompleteTaskStatus();
		logger.info("Task marked as completed");
		
		// Click on save button
		taskPage.saveStatusChange();
		
		// Verify that green checkmark icon is displayed for the task when technician marks that incomplete task as completed
		assertEquals("Carbon:checkmark--outline", taskPage.getGreenCheckmarkOnTaskPage(0));
		
		taskPage.clickBackChevron();
		
		workOrderDetailsPage.clickTaskButton();
		
		//click done button
		taskPage.clickDoneButton();
		
		String maximoStytemProp = getSystemProps().toString();
		
		if(maximoStytemProp.equals("1")) {
			// Verify that the page should navigate to the report work page after the last task is completed and maximo.mobile.gotoreportwork should be 1
			assertEquals(true, reprotWorkPage.checkCurrentPage());
		}else {
			assertEquals(true, workOrderDetailsPage.checkCurrentPage());
		}
	}
	
	protected String getSystemProps() throws Exception {
		logger.info("Getting maximo system property : maximo.mobile.gotoreportwork");
		String resultsObjects = maximoApi.getSystemProps("maximo.mobile.gotoreportwork");  
//		JSONObject jsonObject = new JSONObject(resultsObjects.toString());
//		String maximoSysProp = jsonObject.getString("maximo.mobile.gotoreportwork");
		logger.info("maximo system property: {}", resultsObjects);
		return resultsObjects;
	}

	@Test(groups = {
			"priority2" }, description = "Verify that the technician cannot view and open attachments associated with task in tasks list page", timeOut = 900000)
	public void taskListAttachmentVerification() throws Exception {
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage workOrderDetailsPage = new WorkOrderDetailsPage(af);
		TaskPage taskPage = new TaskPage(af);
		logger.info(
				"Scenario 15 : Verify that the technician cannot view and open attachments associated with task in tasks list page");
		assignedWorkPage.search(woNum);
		assignedWorkPage.openWorkOrderDetails();
		logger.info("opened WO");
		workOrderDetailsPage.clickTaskButton();
		logger.info("finding attachments");
		assertEquals(false, taskPage.isAttachmentExists());
		taskPage.clickBackChevron();
		logger.info("No attachment found!");
		workOrderDetailsPage.clickBackChevron();
	}

	protected void createDefaultObjects() throws Exception {
		logger.info("Creating default objects");

		// Create a location for WO
		logger.info("Creating a location");
		location = AbstractAutomationFramework.randomString(5).toUpperCase();
		Location newlocation = new Location();
		newlocation.setLocationId(location);
		newlocation.setDescription(LOCATION_DESCRIPTION);
		newlocation.setSiteId(SetupData.SITEID);
		newlocation.setType(SetupData.LocType.OPERATING.toString());
		maximoApi.create(newlocation);
		logger.info("Location: {}" + location);
		
		// Create an asset for WO
		logger.info("Creating an asset");
		assetNum = AbstractAutomationFramework.randomString(5).toUpperCase();
		String assetResult = maximoApi.retrieve(new Asset(), "addid=1&internalvalues=1&action=system:new&addschema=1");
		Asset newAsset = new Gson().fromJson(assetResult, Asset.class);
		newAsset.setAssetNum(assetNum);
		newAsset.setDescription(ASSET_DESCRIPTION + assetNum);
		newAsset.setStatus(SetupData.LocAssetStatus.ACTIVE.toString());
		newAsset.setSiteId(SetupData.SITEID);
		newAsset.setLocation(location);
		maximoApi.create(newAsset);
		logger.info("Asset: {assetNum} " + assetNum);
		
		// Create a workorder
		logger.info("Creating a work order");
		woNum = AbstractAutomationFramework.randomString(5).toUpperCase();
		String workOrderResult = maximoApi.retrieve(new WorkOrder(),
				"addid=1&internalvalues=1&action=system:new&addschema=1");
		newWorkOrder = new Gson().fromJson(workOrderResult, WorkOrder.class);
		newWorkOrder.setWoNum(woNum);
		newWorkOrder.setDescription(WORKORDER_DESCRIPTION + woNum);
		newWorkOrder.setSiteId(SetupData.SITEID);
		newWorkOrder.setAssetNum(assetNum);
		newWorkOrder.setLocation(location);
		newWorkOrder.setWorkType(WorkType.PM.toString());
		maximoApi.create(newWorkOrder);
		logger.info("Work Order: {}" + woNum);

		// Change WO status to Approved
		logger.info("Changing work order status to APPR");
		maximoApi.changeStatus(newWorkOrder, WoStatus.APPR.toString());
		
		// Assign the labor
		maximoApi.addAssignmentLabor(newWorkOrder, labor);
		logger.info("Assignment added");
						
		// Create a location for Task
		logger.info("Creating a location1 for Task");
		location1 = AbstractAutomationFramework.randomString(5).toUpperCase();
		Location newlocation1 = new Location();
		newlocation1.setLocationId(location1);
		newlocation1.setDescription(LOCATION_DESCRIPTION);
		newlocation1.setSiteId(SetupData.SITEID);
		newlocation1.setType(SetupData.LocType.OPERATING.toString());
		maximoApi.create(newlocation1);
		logger.info("Task Location: {}" + location1);
		
		// Create an asset for Task
		logger.info("Creating an asset for Task");
		assetNum1 = AbstractAutomationFramework.randomString(5).toUpperCase();
		String assetResult1 = maximoApi.retrieve(new Asset(), "addid=1&internalvalues=1&action=system:new&addschema=1");
		Asset newAsset1 = new Gson().fromJson(assetResult1, Asset.class);
		newAsset1.setAssetNum(assetNum1);
		newAsset1.setDescription(ASSET_DESCRIPTION + assetNum1);
		newAsset1.setStatus(SetupData.LocAssetStatus.ACTIVE.toString());
		newAsset1.setSiteId(SetupData.SITEID);
		newAsset1.setLocation(location1);
		maximoApi.create(newAsset1);
		logger.info("Task Asset: {}" + assetNum1);
	
		// Create Task
		logger.info("Task API Call started ");
		List<Task> arr = new ArrayList<>();
		Task task = new Task();
		task.setEstdur(Float.parseFloat("1.5"));
		task.setSiteid(SetupData.SITEID);
		task.setDescription("WorkOrder for mobile automation test");
		task.setOwnergroup(SetupData.OWNERGROUP);
		task.setParentchgsstatus(true);
		task.setTaskid(Integer.parseInt("89087"));
		task.setStatus(WoStatus.WAPPR.toString());
		task.setAssetnum(assetNum1);
		task.setLocation(location1);
		arr.add(task);
		newWorkOrder.setWoactivity(arr);
		maximoApi.update(newWorkOrder);
		logger.info("Tasks added");
			
		//setting system properties
		logger.info("Setting system property");
		updateSystemProperties();
	}
	
	/**
	 * Method to update the maximo.mobile.gotoreportwork property to 0
	 * 
	 * @throws Exception
	 */
	protected void updateSystemProperties() throws Exception {
		logger.info("Set the value of maximo.mobile.gotoreportwork to 1");
		String value = String.valueOf(1);
		maximoApi.setProperty("maximo.mobile.gotoreportwork", "COMMON", null, value);
	}
}