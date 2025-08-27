package com.ibm.maximo.technician.testcases;

import static org.testng.Assert.assertEquals;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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
import com.ibm.maximo.automation.mobile.api.json.Asset;
import com.ibm.maximo.automation.mobile.api.json.Location;
import com.ibm.maximo.automation.mobile.api.json.Task;
import com.ibm.maximo.automation.mobile.api.json.WorkOrder;
import com.ibm.maximo.technician.framework.TechnicianTest;
import com.ibm.maximo.technician.page.CreateWorkOrderPage;
import com.ibm.maximo.technician.page.MySchedulePage;
import com.ibm.maximo.technician.page.TaskPage;
import com.ibm.maximo.technician.page.WorkOrderDetailsPage;
import com.ibm.maximo.technician.setupdata.SetupData;
import com.ibm.maximo.technician.setupdata.SetupData.WoStatus;
import com.ibm.maximo.technician.setupdata.SetupData.WorkType;

/*
 * GRAPHITE-53319: Review Task Page Designs
   GRAPHITE-70312: Task List 
 */

public class TaskPageTest extends TechnicianTest {
	private final Logger logger = LoggerFactory.getLogger(TaskPageTest.class);
	private AbstractAutomationFramework af;
	private TestSuite testSuite;
	private MaximoApi maximoApi;
	private WorkOrder newWorkOrder;
	private String assetNum, woNum, labor, location, assetNum1, location1;
	private static final String TASK_TITLE = "Task For Mobile Automation";
	private static final String TASK_LONG_DESCRIPTION = "This is Task Page Design"
			+ "automation test to verify icons and functionalities on task page"
			+ "with asset icon location icon description"
			+ "and button with button description and icon name";
	private static final String ASSET_DESCRIPTION = "Asset Description ";
	private static final String WORKORDER_DESCRIPTION = "Work Order Description ";
	private static final String LOCATION_DESCRIPTION = "Location for mobile automation test";
	
	@BeforeClass(alwaysRun = true)
	@Parameters({ "configPath" })
	public void setup(String configPath) throws Exception {
		logger.info("********************TaskPageTest*********************************");
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
				
				
		// Search the WO
		assignedWorkPage.search(woNum);
		// Navigate to Work Order Details page
		assignedWorkPage.openWorkOrderDetails();
		logger.info("Work Order Details Page Opened");
		// Open Task Page
		workOrderDetailsPage.clickTaskButton();
		logger.info("Task Icon Clicked");
				
		//Verify task page title is present
		assertEquals(woNum+" Tasks", taskPage.getTaskPageTitle());
		logger.info("Task Page Titled Found");
				
		//Verify Task page title is present at the top
		logger.info("Scenario 1: Verify Task page title is present at the top");
		
		//Verify Asset and location icons are present
			
		// Verify asset icon
		assertEquals("carbon:asset", taskPage.getAssetIcon());
		logger.info("Asset Icon are present");
				
		// Verify location icon
		assertEquals("carbon:location", taskPage.getLocationIcon());
		logger.info("Location icon are present");
				
       //Verify Long description present
		logger.info("Scenario 26 - Verify that the 'Long description' of added task in work order is displayed under task on tasks list page");
        assertEquals(true, taskPage.getTaskLongDescription() != null || taskPage.getTaskLongDescription() != " ");
        logger.info("Long Description are present");
        
       //Navigate back to workorder details page
        taskPage.clickBackChevron();
      //Navigate back to workorder list page 
		workOrderDetailsPage.clickBackChevron();
	}
	
	@Test(groups = { "priority3" }, description = "Verify Task functionality", timeOut = 9000000)
	public void taskFunctionality1() throws Exception {
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage workOrderDetailsPage = new WorkOrderDetailsPage(af);
		TaskPage taskPage = new TaskPage(af);
		CreateWorkOrderPage createWO = new CreateWorkOrderPage(af);
						
		// Search the WO
		assignedWorkPage.search(woNum);
		
		// Navigate to Work Order Details page
		assignedWorkPage.openWorkOrderDetails();
		logger.info("Work Order Details Page Opened");
		String workOrderHeader = workOrderDetailsPage.getTitle(workOrderDetailsPage.workOrderHeaderText);
		assertEquals("Work order", workOrderHeader);		
		workOrderDetailsPage.taskBadgeExists();
		logger.info("task badge exists");
		int expectedTaskcount = workOrderDetailsPage.getTaskCountDisplay();
		workOrderDetailsPage.placeholderLongDescriptionExists();
		logger.info("placeholder text (-) is displayed in Details section when there is no long description added to work order");
		
		// Open Task Page
		workOrderDetailsPage.clickTaskButton();
		
		// Verify long description icon exists
		taskPage.longDecsriptionIconExists();
		
		// Click on long description icon 
		taskPage.clickLongDescriptionIcon();
		
		// Click on back button from long description page
		taskPage.longDescriptionBackButton();
		
		assertEquals(TASK_LONG_DESCRIPTION,taskPage.getTextWOLongDescription());
		logger.info("Long description of the task is displayed in a large dialog/flyout when technician clicks on maximize icon/button under task on tasks list page");
		
		assertEquals(assetNum1, taskPage.getAssetNum());
		logger.info("asset details on Task page are different from WO asset details");
		
		// Verify location details on Task page are different from WO location details
		assertEquals(location1, taskPage.getLocationNum());
		logger.info("location details on Task page are different from WO location details");
	
		taskPage.clickTaskStatus();
		logger.info("task status change");	
		
		taskPage.selectCompleteTaskStatus();
		logger.info("status completed");	
		
		// Click on save button
		taskPage.saveStatusChange();
		logger.info("saved");	
		
		// click back button 
		taskPage.backButtonTaskPage();
		String workOrderHeader1 = workOrderDetailsPage.getTitle(workOrderDetailsPage.workOrderHeaderText);
		assertEquals("Work order", workOrderHeader1);	
		assertEquals(expectedTaskcount,workOrderDetailsPage.getTaskCountDisplay());
			
	}
	
	protected String getSystemProps() throws Exception {
		logger.info("Getting maximo system property : maximo.mobile.gotoreportwork");
		String resultsObjects = maximoApi.getSystemProps("maximo.mobile.gotoreportwork");  
		JSONObject jsonObject = new JSONObject(resultsObjects.toString());
		String maximoSysProp = jsonObject.getString("maximo.mobile.gotoreportwork");
		logger.info("maximo system property: {}", maximoSysProp);
		return maximoSysProp;
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
		String workOrderResult = maximoApi.retrieve(new WorkOrder(),
				"addid=1&internalvalues=1&action=system:new&addschema=1");
		newWorkOrder = new Gson().fromJson(workOrderResult, WorkOrder.class);
		newWorkOrder.setDescription(WORKORDER_DESCRIPTION + woNum);
		newWorkOrder.setSiteId(SetupData.SITEID);
		newWorkOrder.setAssetNum(assetNum);
		newWorkOrder.setLocation(location);
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
	
		// Create Task 1
		logger.info("Task API Call started ");
		List<Task> arr = new ArrayList<>();
		Task task = new Task();
		task.setDescription(TASK_TITLE);
		task.setTaskid(Integer.parseInt("10"));
		task.setSiteid(SetupData.SITEID);
		task.setStatus(WoStatus.WAPPR.toString());
		task.setOwnergroup(SetupData.OWNERGROUP);
		task.setParentchgsstatus(true);
		task.setAssetnum(assetNum1);
		task.setLocation(location1);
		task.setLongDescription(TASK_LONG_DESCRIPTION);
		arr.add(task);
		
		// Create 2nd Task
		logger.info("Task two 2222 API Call started ");
		Task task1 = new Task();
		task1.setEstdur(Float.parseFloat("1.5"));
		task1.setSiteid(SetupData.SITEID);
		task1.setDescription(WORKORDER_DESCRIPTION);
		task1.setOwnergroup(SetupData.OWNERGROUP);
		task1.setParentchgsstatus(true);
		task1.setTaskid(20);
		task1.setStatus(WoStatus.WAPPR.toString());
		arr.add(task1);

		// Create 3nd Task
		int i = 30;
		while (i < 70) {
			Task task2 = new Task();
			task2.setEstdur(Float.parseFloat("1.5"));
			task2.setSiteid(SetupData.SITEID);
			task2.setDescription(WORKORDER_DESCRIPTION);
			task2.setOwnergroup(SetupData.OWNERGROUP);
			task2.setParentchgsstatus(true);
			task2.setTaskid(i);
			task2.setStatus(WoStatus.WAPPR.toString());
			arr.add(task2);
			logger.info("Tasks added " + i);
			i += 10;
		}

		while (i < 90) {
			Task task2 = new Task();
			task2.setEstdur(Float.parseFloat("1.5"));
			task2.setSiteid(SetupData.SITEID);
			task2.setDescription(WORKORDER_DESCRIPTION);
			task2.setOwnergroup(SetupData.OWNERGROUP);
			task2.setParentchgsstatus(true);
			task2.setTaskid(i);
			task2.setStatus(WoStatus.CLOSE.toString());
			arr.add(task2);
			logger.info("Tasks added " + i);
			i += 10;
		}

		while (i < 110) {
			Task task2 = new Task();
			task2.setEstdur(Float.parseFloat("1.5"));
			task2.setSiteid(SetupData.SITEID);
			task2.setDescription(WORKORDER_DESCRIPTION);
			task2.setOwnergroup(SetupData.OWNERGROUP);
			task2.setParentchgsstatus(true);
			task2.setTaskid(i);
			task2.setStatus(WoStatus.CAN.toString());
			arr.add(task2);
			logger.info("Tasks added " + i);
			i += 10;
		}
		newWorkOrder.setWoactivity(arr);
		maximoApi.update(newWorkOrder);
	}
}
