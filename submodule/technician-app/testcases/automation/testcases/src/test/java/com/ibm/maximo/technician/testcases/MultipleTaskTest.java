package com.ibm.maximo.technician.testcases;

import static org.junit.Assert.assertEquals;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

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
import com.ibm.maximo.technician.page.MySchedulePage;
import com.ibm.maximo.technician.page.TaskPage;
import com.ibm.maximo.technician.page.WorkOrderDetailsPage;
/*
 * GRAPHITE-51059: [TECHMOBILE] View and complete tasks - No Flow Control :46M,6TA,1A
 */
import com.ibm.maximo.technician.setupdata.SetupData;
import com.ibm.maximo.technician.setupdata.SetupData.WoStatus;
import com.ibm.maximo.technician.setupdata.SetupData.WorkType;

public class MultipleTaskTest extends TechnicianTest {
	private final Logger logger = LoggerFactory.getLogger(TaskTestSuite.class);
	private AbstractAutomationFramework af;
	private TestSuite testSuite;
	private MaximoApi maximoApi;
	private WorkOrder newWorkOrder;
	private String assetNum, woNum, labor, location, assetNum1, location1;
	private static final String LOCATION_DESCRIPTION = "Location for mobile automation test";
	private static final String WORKORDER_DESCRIPTION = "WorkOrder for mobile automation test";
	private static final String ASSET_DESCRIPTION = "Asset Description ";

	@BeforeClass(alwaysRun = true)
	@Parameters({ "configPath" })
	public void setup(String configPath) throws Exception {
		logger.info("********************MultipleTaskTest*********************************");
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
		if (testSuite != null) {
			testSuite.teardown();
		}
	}

	@Test(groups = { "mobile" }, description = "Verify Task functionality", timeOut = 1000000)
	public void taskFunctionality() throws Exception {
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage workOrderDetailsPage = new WorkOrderDetailsPage(af);
		TaskPage taskPage = new TaskPage(af);

		// Search the WO
		assertEquals(true, assignedWorkPage.search(woNum));
		logger.info("Search the workorder");
		// Navigate to Work Order Details page
		assignedWorkPage.openWorkOrderDetails();
		logger.info("Open wo details page");
		// Open Task Page

		int taskcount = workOrderDetailsPage.getTaskCountDisplay();
		logger.info("TASK COUNT: " + taskcount);

		/*
		 * NOTE: both clickTaskButton will be intercepted by other element, but works
		 * normally
		 */
		workOrderDetailsPage.clickTaskButton();
		logger.info("click on task button");

		logger.info("Scenario 7 - Verify that first incomplete task is expanded by default");
		// Asserting task 10 is expanded using workorder parent num and task id
		assertTrue(taskPage.isTaskExpanded(woNum, 10));
		logger.info("Task 10 is expanded");

		logger.info(
				"Scenario 39 part1- Verify the UI elements on task list page when work order is not flow controlled");

		assertEquals(woNum + " Tasks", taskPage.getTaskPageTitle());
		logger.info("Title verified");

		assertEquals(assetNum1, taskPage.getAssetNum());
		logger.info("Asset Number verified");

		assertEquals("Asset Description " + assetNum1, taskPage.getTaskAssetDescription());
		logger.info("Asset Description verified");

		assertEquals("Location for mobile automation test", taskPage.getTaskLocationDescription());
		logger.info("Location Description verified");

		assertEquals("carbon:asset", taskPage.getAssetIcon());
		logger.info("Asset Icon present");

		assertEquals("10.", taskPage.getTaskTitle(0));
		logger.info("Task title  verified");

		assertEquals("Waiting on approval", taskPage.verifyTaskStatus());
		logger.info("Task status verified");

		assertEquals("Carbon:chevron--up", taskPage.taskExpandIconValue(woNum, 10));
		logger.info("expand icon verified");

		taskPage.clickTaskExpandIcon(woNum, 10);
		assertEquals("Carbon:chevron--down", taskPage.taskExpandIconValue(woNum, 10));
		logger.info("expand icon verified again");

		assertTrue(taskPage.verifyCompleteButtonSecondary(1));
		logger.info("second task secondary color button verified");

		taskPage.clickTaskExpandIcon(woNum, 10);

		logger.info("Scenario 25 - Verify that all planned tasks which are either in cancelled or closed"
				+ " status are not displayed in task list and tasks in other statuses are displayed\n" + "");

		assertFalse(taskPage.isTaskExists(9));
		logger.info("Not showing task 10");
		assertFalse(taskPage.isTaskExists(8));
		logger.info("Not showing task 9");
		assertFalse(taskPage.isTaskExists(7));
		logger.info("Not showing task 8");
		assertFalse(taskPage.isTaskExists(6));
		logger.info("Not showing task 7");

		logger.info("Not showing task that are CLOSED or CANCELED");

		// Click on Change Status drawer
		taskPage.clickTaskStatus();

		// Verify that technician marks a task completed
		taskPage.selectCompleteTaskStatus();

		// Click on save button
		taskPage.saveStatusChange();
		
		logger.info("Scenario 8 - Verify that when technician marks a task completed,"
				+ " the next incomplete task is expanded automatically");

		// Asserting task 20 is expanded using workorder parent num and task id
		assertTrue(taskPage.isTaskExpanded(woNum, 20));
		logger.info("Task 20 is now expanded instead of 10");

		// Go back to WO Details
		taskPage.clickBackChevron();

		logger.info("Scenario 13 - Verify the badge count value on task touch-point"
				+ " when technician marks an incomplete task as completed in tasks list");

		assertTrue(taskcount > workOrderDetailsPage.getTaskCountDisplay());
		logger.info("Task number updated when complete one of the tasks");

		workOrderDetailsPage.clickTaskButton();
		logger.info("click on task button");

		logger.info(
				"Scenario 39 part2- Verify the UI elements on task list page when work order is not flow controlled");

		assertEquals("carbon:checkmark", taskPage.getBlueCheckmarkOnTaskPage(1));
		assertEquals("Carbon:checkmark--outline", taskPage.getGreenCheckmarkOnTaskPage(0));
		logger.info("Blue and Green checkmark verified");

		assertFalse(taskPage.verifyDoneButton());
		logger.info("Done button not enabled");

		assertTrue(taskPage.verifyCompleteButtonPrimary(1));
		logger.info("second task primary color button verified");

		logger.info("Setting all tasks as done");
		taskPage.clickDoneTaskButton(1);
		taskPage.clickDoneTaskButton(3);
		taskPage.clickDoneTaskButton(2);
		taskPage.clickDoneTaskButton(4);
		taskPage.clickDoneTaskButton(5);
		assertTrue(taskPage.verifyDoneButton());
		logger.info("Done button now enabled");

		logger.info("Testing if all tasks can be expanded");
		/*
		 * NOTE: first click will be intercepted by header but works normally
		 */
		taskPage.clickTaskExpandIcon(woNum, 20);
		taskPage.clickTaskExpandIcon(woNum, 10);
		taskPage.clickTaskExpandIcon(woNum, 30);
		taskPage.clickTaskExpandIcon(woNum, 40);
		taskPage.clickTaskExpandIcon(woNum, 60);
		taskPage.clickTaskExpandIcon(woNum, 50);
		logger.info("Test passed");

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
		maximoApi.create(newlocation);
		logger.info("location: {}" + location);

		// Create an asset for WO
		logger.info("Creating an asset");
		assetNum = AbstractAutomationFramework.randomString(5).toUpperCase();
		String assetResult = maximoApi.retrieve(new Asset(), "addid=1&internalvalues=1&action=system:new&addschema=1");
		Asset newAsset = new Gson().fromJson(assetResult, Asset.class);
		newAsset.setAssetNum(assetNum);
		newAsset.setDescription(ASSET_DESCRIPTION + assetNum);
		newAsset.setLocation(location);
		newAsset.setSiteId(SetupData.SITEID);
		newAsset.setStatus(SetupData.LocAssetStatus.ACTIVE.toString());
		maximoApi.create(newAsset);
		logger.info("Asset: {}" + assetNum);

		// Create a work order
		logger.info("Creating work order");
		String workOrderResult = maximoApi.retrieve(new WorkOrder(),
				"addid=1&internalvalues=1&action=system:new&addschema=1");
		newWorkOrder = new Gson().fromJson(workOrderResult, WorkOrder.class);
		newWorkOrder.setDescription(WORKORDER_DESCRIPTION);
		newWorkOrder.setSiteId(SetupData.SITEID);
		newWorkOrder.setWorkType(WorkType.PM.toString());
		newWorkOrder.setAssetNum(assetNum);
		newWorkOrder.setLocation(location);
		maximoApi.create(newWorkOrder);
		woNum = newWorkOrder.getWoNum();
		logger.info("Work Order: {}" + woNum);

		// Assign the labor
		maximoApi.addAssignmentLabor(newWorkOrder, labor);
		logger.info("Assignment added");

		// Change WO status to Approved
		logger.info("Changing work order status to APPR");
		maximoApi.changeStatus(newWorkOrder, WoStatus.APPR.toString());

		logger.info("Create a location for Task");
		location1 = AbstractAutomationFramework.randomString(5).toUpperCase();
		Location newlocation1 = new Location();
		newlocation1.setLocationId(location1);
		newlocation1.setDescription(LOCATION_DESCRIPTION);
		newlocation1.setSiteId(SetupData.SITEID);
		maximoApi.create(newlocation1);
		logger.info("location: {}" + location1);

		logger.info("Create an asset for Task");
		assetNum1 = AbstractAutomationFramework.randomString(5).toUpperCase();
		String assetResult1 = maximoApi.retrieve(new Asset(), "addid=1&internalvalues=1&action=system:new&addschema=1");
		Asset newAsset1 = new Gson().fromJson(assetResult1, Asset.class);
		newAsset1.setAssetNum(assetNum1);
		newAsset1.setLocation(location1);
		newAsset1.setDescription(ASSET_DESCRIPTION + assetNum1);
		newAsset1.setSiteId(SetupData.SITEID);
		newAsset1.setStatus(SetupData.LocAssetStatus.ACTIVE.toString());
		maximoApi.create(newAsset1);
		logger.info("Asset: {}" + assetNum1);

		// Create 1st Task
		logger.info("Task API Call started ");
		List<Task> arr = new ArrayList<>();
		Task task = new Task();
		task.setEstdur(Float.parseFloat("1.5"));
		task.setSiteid(SetupData.SITEID);
		task.setDescription(WORKORDER_DESCRIPTION);
		task.setOwnergroup(SetupData.OWNERGROUP);
		task.setParentchgsstatus(true);
		task.setTaskid(10);
		task.setStatus(WoStatus.WAPPR.toString());
		task.setAssetnum(assetNum1);
		task.setLocation(location1);
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