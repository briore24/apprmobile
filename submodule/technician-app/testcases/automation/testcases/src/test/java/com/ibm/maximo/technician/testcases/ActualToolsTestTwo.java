package com.ibm.maximo.technician.testcases;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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
import com.ibm.maximo.automation.mobile.api.MaximoApi;
import com.ibm.maximo.automation.mobile.api.json.Asset;
import com.ibm.maximo.automation.mobile.api.json.ItemChangeStatus;
import com.ibm.maximo.automation.mobile.api.json.StoreRoom;
import com.ibm.maximo.automation.mobile.api.json.ToolItem;
import com.ibm.maximo.automation.mobile.api.json.WorkOrder;
import com.ibm.maximo.automation.mobile.api.json.WpTool;
import com.ibm.maximo.components.LabelComponent;
import com.ibm.maximo.technician.framework.TechnicianTest;
import com.ibm.maximo.technician.page.AddMaterialPage;
import com.ibm.maximo.technician.page.AddToolPage;
import com.ibm.maximo.technician.page.MySchedulePage;
import com.ibm.maximo.technician.page.ReportWorkPage;
import com.ibm.maximo.technician.page.WorkOrderDetailsPage;
import com.ibm.maximo.technician.setupdata.SetupData;
import com.ibm.maximo.technician.setupdata.SetupData.ItemStatus;
import com.ibm.maximo.technician.setupdata.SetupData.LocAssetStatus;
import com.ibm.maximo.technician.setupdata.SetupData.WoStatus;
import com.ibm.maximo.technician.setupdata.SetupData.LocType;

/*
 * GRAPHITE-51064:  Report work - Tools as actuals
 * GRAPHITE-65557: [TECHMOBILE] Add Tools Used :31M,1TA,1A
 * GRAPHITE-71929:[TECHMOBILE] Add Tools Used :31M,0TA,3A,P3
 *  GRAPHITE-71928:[TECHMOBILE] Add Tools Used :31M,0TA,2A,P3
 * 
 */
public class ActualToolsTestTwo extends TechnicianTest {
	private final Logger logger = LoggerFactory.getLogger(ActualToolsTestTwo.class);
	private AbstractAutomationFramework af;
	private TestSuite testSuite;
	private MaximoApi maximoApi;
	private WorkOrder newWorkOrder;
	private String labor;
	private String  itemnum, location,woNum1,location1,locationRot,locationRot1,location3;
	private String woNumRot, itemnumRot, assetNumRot;

	@BeforeClass(alwaysRun = true)
	@Parameters({ "configPath" })
	public void setup(String configPath) throws Exception {
		logger.info("********************ActualToolsTest*********************************");
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

	@Test(groups = {
			"priority3" }, description = "Verify Discrad Button,MultipleStoreroom with Normal Tool,MultipleStoreroom with Rotating tool", timeOut = 30000000)
	public void verifyActualToolsFlow() throws Exception {
		verifyDiscradButton();
		
		verifyNormalToolForMultipleStoreRoom();

		verifyRotatingToolForMultipleStoreRoom();

	}
	



		public void verifyDiscradButton() throws Exception {


		
		// Open WO list page
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage workOrderDetailsPage = new WorkOrderDetailsPage(af);
		ReportWorkPage reportPage = new ReportWorkPage(af);
		AddMaterialPage addMaterialPage =  new AddMaterialPage(af);
		// Search the WO
		assertTrue(assignedWorkPage.search(woNum1));
		// Navigate to work order details page
		assignedWorkPage.openWorkOrderDetails();

		// click "Report work" Icon, and "Report work" page loaded
		ReportWorkPage reportWorkPage = workOrderDetailsPage.clickReportWorkButton();
		// click Tools used plus icon and "Add tool" page loaded
		AddToolPage addToolPage = reportWorkPage.clickAddToolButton();

		// verify save button is disabled
		assertFalse(addToolPage.isSaveButtonEnabled());
		logger.info("passed:Initial save button disabled");

		// select tool added just now
		addToolPage.toolsDrawerClose();

		


		// appears on the Report Work view
		reportWorkPage.clickAddToolButton();
		// click save button
		addToolPage.selectTool(itemnum, itemIdQueryFromDB(itemnum));
		addToolPage.isMultipleStoreRoomPresent(location,location1);

	
		addToolPage.toolsDrawerClose();
		addMaterialPage.ClicOnToolDiscrad();
	
		logger.info("passed: tool added after click save button");
		
		reportPage.clickBackChevron();
		logger.info("Complete Work button is enabled and clicked");

		workOrderDetailsPage.clickBackChevron();
		logger.info("Back chevron  from details");
		assignedWorkPage.clickClearButton();
				

		
	}

	private void verifyRotatingToolForMultipleStoreRoom() throws Exception {

		

		// Search the WO
		ReportWorkPage reportPage = new ReportWorkPage(af);
		MySchedulePage assignedWorkPage = new MySchedulePage(af);

		assignedWorkPage.search(woNumRot);
		assignedWorkPage.openWorkOrderDetails();

		// click "Report work" Icon, and "Report work" page loaded
		WorkOrderDetailsPage workOrderDetailsPage = new WorkOrderDetailsPage(af);
		ReportWorkPage reportWorkPage = workOrderDetailsPage.clickReportWorkButton();
		// click Tools used plus icon and "Add tool" page loaded
		AddToolPage addToolPage = reportWorkPage.clickAddToolButton();


		// Select the rotating tool which is having the rotating asset.
		addToolPage.selectTool(itemnumRot, itemIdQueryFromDB(itemnumRot));
		addToolPage.isMultipleStoreRoomPresent(locationRot,locationRot1);

		addToolPage.clickSaveButton();
		logger.info("save button clicked");
		addToolPage.tillPageLoaded();
		
		assertTrue(reportWorkPage.verifyToolAdded());
		logger.info("passed: tool added after click save button");
		
		reportPage.completeReportWorkButton();
		logger.info("Complete Work button is enabled and clicked");

		assignedWorkPage.clickClearButton();
		logger.info("Back chevron  from details");
	}
	private void verifyNormalToolForMultipleStoreRoom() throws Exception {
		

		// Open WO list page
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage workOrderDetailsPage = new WorkOrderDetailsPage(af);
		ReportWorkPage reportPage = new ReportWorkPage(af);
		// Search the WO
		
		assertTrue(assignedWorkPage.search(woNum1));
		// Navigate to work order details page
		assignedWorkPage.openWorkOrderDetails();

		// click "Report work" Icon, and "Report work" page loaded
		ReportWorkPage reportWorkPage = workOrderDetailsPage.clickReportWorkButton();
		// click Tools used plus icon and "Add tool" page loaded
		AddToolPage addToolPage = reportWorkPage.clickAddToolButton();

		// verify save button is disabled
		assertFalse(addToolPage.isSaveButtonEnabled());
		logger.info("passed:Initial save button disabled");

		// select tool added just now
		addToolPage.selectTool(itemnum, itemIdQueryFromDB(itemnum));
		addToolPage.isMultipleStoreRoomPresent(location,location1);
		


		// click save button
		addToolPage.clickSaveButton();
		logger.info("save button clicked");
		addToolPage.tillPageLoaded();

		assertTrue(reportWorkPage.verifyToolAdded());
		logger.info("passed: tool added after click save button");
		
		
		reportPage.completeReportWorkButton();
		logger.info("Complete Work button is enabled and clicked");

		assignedWorkPage.clickClearButton();
		logger.info("Back chevron  from details");
	}


	protected void createDefaultObjects() throws Exception {

		logger.info("Creating default objects");
		createObjectsForNormalToolForMultipleStoreRoom();
		createObjectsForRotatingToolForMultipleStoreRoom();

	}

	/**
	 * Scenario 16/18
	 * 
	 * @throws Exception
	 */
	private void createObjectsForNormalToolForMultipleStoreRoom() throws Exception {
		String assetNum;

		////// Creating a tool
		logger.info("Creating a tool");
		itemnum = AbstractAutomationFramework.randomString(5).toUpperCase();

		String toolItemResult = maximoApi.retrieve(new ToolItem(),
				"addid=1&internalvalues=1&action=system:new&oslc.select=itemnum,siteid,description,itemsetid,rotating,lottype,itemtype,status,itemid&addschema=1");
		ToolItem toolItem = new Gson().fromJson(toolItemResult, ToolItem.class);
		toolItem.setItemNum(itemnum);
		toolItem.setDescription(SetupData.TOOL_DESCRIPTION);
		maximoApi.create(toolItem);
		logger.info("ToolItem: " + itemnum);

		// item status change and rolldown
		logger.info("Changing Tool Status");
		List<ItemChangeStatus> arr = new ArrayList<>();
		ItemChangeStatus itemChangeStatus = new ItemChangeStatus();
		itemChangeStatus.setStatus(ItemStatus.ACTIVE.toString());
		itemChangeStatus.setRollDown("true");
		arr.add(itemChangeStatus);
		toolItem.setItemChangeStatus(arr);
		maximoApi.update(toolItem);
		logger.info("Tool API status changed");

		// Creating storeroom
		logger.info("Creating a store room");

		location = AbstractAutomationFramework.randomString(5).toUpperCase();

		StoreRoom storeRoom = new StoreRoom();
		storeRoom.setType(LocType.STOREROOM.toString());
		storeRoom.setDescription("StoreRoom " + location);
		storeRoom.setSiteId(SetupData.SITEID);
		storeRoom.setLocation(location);
		maximoApi.create(storeRoom);
		logger.info("storeRoom location::" + location);

		maximoApi.addItemToStoreRoom(itemnum, SetupData.ITEMSET, location, SetupData.SITEID, "false",
				SetupData.ISSUEUNIT, "100", SetupData.DFLTBIN, "", "", "0.0", "1.0", "0.0", "0", "0", "0.0", "0.0",
				"true");
		logger.info("added ToolItem to StoreRoom successfully");
		
		///////// Creating storeroom new
		logger.info("Creating a store room");

		location1 = AbstractAutomationFramework.randomString(5).toUpperCase();

		StoreRoom storeRoom1 = new StoreRoom();
		storeRoom1.setType(LocType.STOREROOM.toString());
		storeRoom1.setDescription("StoreRoom " + location1);
		storeRoom1.setSiteId(SetupData.SITEID);
		storeRoom1.setLocation(location1);
		maximoApi.create(storeRoom1);
		logger.info("storeRoom location::" + location1);

		maximoApi.addItemToStoreRoom(itemnum, SetupData.ITEMSET, location1, SetupData.SITEID, "false",
				SetupData.ISSUEUNIT, "100", SetupData.DFLTBIN, "", "", "0.0", "1.0", "0.0", "0", "0", "0.0", "0.0",
				"true");
		logger.info("added ToolItem to StoreRoom successfully");

		// Add tool to store room
		// Create an asset
		logger.info("Creating an asset");
		assetNum = AbstractAutomationFramework.randomString(5).toUpperCase();
		String assetResult = maximoApi.retrieve(new Asset(), "addid=1&internalvalues=1&action=system:new&addschema=1");
		Asset newAsset = new Gson().fromJson(assetResult, Asset.class);
		newAsset.setAssetNum(assetNum);
		newAsset.setSiteId(SetupData.SITEID);
		maximoApi.create(newAsset);
		logger.info("Asset: " + assetNum);

		// Create a work order
		logger.info("Creating a work order");
		woNum1 = AbstractAutomationFramework.randomString(5).toUpperCase();
		String workOrderResult = maximoApi.retrieve(new WorkOrder(),
				"addid=1&internalvalues=1&action=system:new&addschema=1");
		WorkOrder workOrder1 = new Gson().fromJson(workOrderResult, WorkOrder.class);
		workOrder1.setWoNum(woNum1);
		workOrder1.setDescription("WorkOrder for mobile automation test " + woNum1);
		workOrder1.setSiteId(SetupData.SITEID);
		maximoApi.create(workOrder1);
		logger.info("Work Order: " + woNum1);
		// Assign the labor
		maximoApi.addAssignmentLabor(workOrder1, labor);
		logger.info("Assignment added");
		
		// Change WO status to Approved
		logger.info("Changing work order status to APPR");
		maximoApi.changeStatus(workOrder1, WoStatus.APPR.toString());

	
	}

	

	private void createObjectsForRotatingToolForMultipleStoreRoom() throws Exception {

		
		logger.info("Creating default objects for rotating starts");
		// Creating storeroom
		logger.info("Creating a store room");
		locationRot = AbstractAutomationFramework.randomString(5).toUpperCase();
		String storeRoomResult = maximoApi.retrieve(new StoreRoom(),
				"addid=1&internalvalues=1&action=system:new&oslc.select=itemnum,siteid,description,itemsetid,type,itemtype,status,itemid&addschema=1");
		StoreRoom storeRoom = new Gson().fromJson(storeRoomResult, StoreRoom.class);
		storeRoom.setDescription("StoreRoom " + locationRot);
		storeRoom.setLocation(locationRot);
		maximoApi.create(storeRoom);
		logger.info("storeRoom location::" + locationRot);

		// Creating a tool
		logger.info("Creating a tool");
		itemnumRot = AbstractAutomationFramework.randomString(5).toUpperCase();

		String toolItemResult = maximoApi.retrieve(new ToolItem(),
				"addid=1&internalvalues=1&action=system:new&oslc.select=itemnum,siteid,description,itemsetid,rotating,lottype,itemtype,status,itemid&addschema=1");
		ToolItem toolItem = new Gson().fromJson(toolItemResult, ToolItem.class);
		toolItem.setItemNum(itemnumRot);
		toolItem.setRotating("true");
		toolItem.setDescription(SetupData.TOOL_DESCRIPTION);
		maximoApi.create(toolItem);
		logger.info("ToolItem for rotating: " + itemnumRot);

		// item status change and rolldown
		logger.info("Changing Tool Status");
		List<ItemChangeStatus> arr = new ArrayList<>();
		ItemChangeStatus itemChangeStatus = new ItemChangeStatus();
		itemChangeStatus.setStatus(ItemStatus.ACTIVE.toString());
		itemChangeStatus.setRollDown("true");
		arr.add(itemChangeStatus);
		toolItem.setItemChangeStatus(arr);
		maximoApi.update(toolItem);
		logger.info("Tool API status changed");

		// Add Tool to StoreRoom
		maximoApi.addItemToStoreRoom(itemnumRot, SetupData.ITEMSET, locationRot, SetupData.SITEID, "false",
				SetupData.ISSUEUNIT, "100", SetupData.DFLTBIN, "", "", "0.0", "1.0", "0.0", "0", "0", "0.0", "0.0",
				"true");
		logger.info("added Tool to StoreRoom successfully");

		// Create an asset
		logger.info("Creating an asset");
		assetNumRot = AbstractAutomationFramework.randomString(5).toUpperCase();
		String assetResult = maximoApi.retrieve(new Asset(), "addid=1&internalvalues=1&action=system:new&addschema=1");

		Asset newAsset = new Gson().fromJson(assetResult, Asset.class);
		newAsset.setAssetNum(assetNumRot);
		newAsset.setDescription("Asset :" + assetNumRot);
		newAsset.setStatus(LocAssetStatus.ACTIVE.toString());
		newAsset.setLocation(locationRot);
		newAsset.setItemNum(itemnumRot);

		maximoApi.create(newAsset);
		logger.info("Asset for rotating: " + assetNumRot);

		// Create a work order
		logger.info("Creating a work order");
		String workOrderResult = maximoApi.retrieve(new WorkOrder(),
				"addid=1&internalvalues=1&action=system:new&addschema=1");
		newWorkOrder = new Gson().fromJson(workOrderResult, WorkOrder.class);
		newWorkOrder.setDescription("WorkOrder test");
		newWorkOrder.setAssetNum(assetNumRot);
		maximoApi.create(newWorkOrder);
		woNumRot = newWorkOrder.getWoNum();
		logger.info("Work Order for rotating: " + woNumRot);

		// Assign the labor
		maximoApi.addAssignmentLabor(newWorkOrder, labor);
		logger.info("Assignment added");

		// Adding Tool to WorkOrder
		logger.info("Adding wptool");
		WpTool wptool = new WpTool();
		wptool.setItemNum(toolItem.getItemNum());
		wptool.setItemQty(1);
		wptool.setRate(1);
		wptool.setReservereq(true);
		List<WpTool> wpToolArray = new ArrayList<WpTool>();
		wpToolArray.add(wptool);
		newWorkOrder.setWpTool(wpToolArray);
		maximoApi.update(newWorkOrder);
		// Creating storeroom another
		logger.info("Creating a store room");
		locationRot1 = AbstractAutomationFramework.randomString(5).toUpperCase();
		String storeRoomResult1 = maximoApi.retrieve(new StoreRoom(),
				"addid=1&internalvalues=1&action=system:new&oslc.select=itemnum,siteid,description,itemsetid,type,itemtype,status,itemid&addschema=1");
		StoreRoom storeRoom1 = new Gson().fromJson(storeRoomResult1, StoreRoom.class);
		storeRoom1.setDescription("StoreRoom " + locationRot1);
		storeRoom1.setLocation(locationRot1);
		maximoApi.create(storeRoom1);
		logger.info("storeRoom location::" + locationRot1);
		// Add Tool to StoreRoom
		maximoApi.addItemToStoreRoom(itemnumRot, SetupData.ITEMSET, locationRot1, SetupData.SITEID, "false",
				SetupData.ISSUEUNIT, "100", SetupData.DFLTBIN, "", "", "0.0", "1.0", "0.0", "0", "0", "0.0", "0.0",
				"true");
		logger.info("added Tool to StoreRoom successfully");
		// Change WO status to Approved
		logger.info("Changing work order status to APPR");
		maximoApi.changeStatus(newWorkOrder, WoStatus.APPR.toString());
		logger.info("WO Status Changed");

	}
	
	
	protected String itemIdQueryFromDB(String toolNum) {
		String query = "select I.itemid from MAXIMO.item I where I.itemnum = '" + toolNum + "' and I.itemsetid= '"
				+ SetupData.ITEMSET + "'";
		logger.info(query);
		Object[] resultsObjects = jdbcConnection.executeSQL(query);
		Object[] resultArray1 = (Object[]) resultsObjects[0];
		String itemId = resultArray1[0].toString();
		logger.info("itemId from db>" + itemId);
		return itemId;
	}
}
