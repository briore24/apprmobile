package com.ibm.maximo.technician.testcases.calibration.tests;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.google.gson.Gson;
import com.ibm.maximo.automation.mobile.AbstractAutomationFramework;
import com.ibm.maximo.automation.mobile.FrameworkFactory;
import com.ibm.maximo.automation.mobile.api.MaximoApi;
import com.ibm.maximo.automation.mobile.api.json.Asset;
import com.ibm.maximo.automation.mobile.api.json.ItemChangeStatus;
import com.ibm.maximo.automation.mobile.api.json.Location;
import com.ibm.maximo.automation.mobile.api.json.StoreRoom;
import com.ibm.maximo.automation.mobile.api.json.ToolItem;
import com.ibm.maximo.automation.mobile.api.json.WorkOrder;
import com.ibm.maximo.automation.mobile.api.json.WpTool;
import com.ibm.maximo.technician.framework.TechnicianTest;
import com.ibm.maximo.technician.page.AddToolPage;
import com.ibm.maximo.technician.page.MySchedulePage;
import com.ibm.maximo.technician.page.ReportWorkPage;
import com.ibm.maximo.technician.page.WorkOrderDetailsPage;
import com.ibm.maximo.technician.setupdata.SetupData;
import com.ibm.maximo.technician.setupdata.SetupData.ItemStatus;
import com.ibm.maximo.technician.setupdata.SetupData.LocAssetStatus;
import com.ibm.maximo.technician.setupdata.SetupData.WoStatus;
import com.ibm.maximo.technician.setupdata.SetupData.WorkType;
import com.ibm.maximo.technician.testcases.TestSuite;
import com.ibm.maximo.technician.testcases.calibration.page.AsFoundValuesPage;
import com.ibm.maximo.technician.testcases.calibration.page.AsLeftValuesPage;
import com.ibm.maximo.technician.testcases.calibration.page.DatasheetListPage;
import com.ibm.maximo.technician.testcases.calibration.support.constants.DatasheetConstants;
import com.ibm.maximo.technician.testcases.calibration.page.AssetFunctionPage;


/*
 * MAXMOA-5466:  Technician Calibration - TS016799669: Actual Tools Entry should be deletable, viewable for calibration work order based on MAXVAR
 */
public class CalibrationToolEditableAndViewable extends DatasheetTests {

	private final Logger logger = LoggerFactory.getLogger(CalibrationToolEditableAndViewable.class);
	private static String calPointAsFoundPASSData = "PASS", calPointAsFoundErrorData = "AsFoundError";
	private AbstractAutomationFramework af;
	private MaximoApi maximoApi;
	private TestSuite testSuite;
	private WorkOrder newWorkOrder;
	private String woNum;
	private String assetNum, locationNum, labor;
	private String assetDescription = "TestAuto Calibration Asset";
	private String LOCATION_DESCRIPTION = "INVALID_LOCATION_1";
	private String woNum2, itemnum, location,locationRot;
	private String woNumRot, itemnumRot, assetNumRot;

	@BeforeClass(alwaysRun = true)
	@Parameters({ "configPath" })
	public void setup(String configPath) throws Exception {
		logger.info("********************CalibrationCheckTest*********************************");
		this.af = FrameworkFactory.get();
		Properties properties = new Properties();
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(configPath));
			properties.load(in);
			labor = properties.getProperty("system.username");
			maximoApi = new MaximoApi();
			maximoApi.setMaximoServer(properties.getProperty("system.maximoServerUrl"),
					properties.getProperty("system.maximoAPIKey"));
			
			// Required: Define target model
			this.setTargetModel(DatasheetConstants.MODEL_SUMIWO);
			
			createDefaultObjects(configPath);
			createObjectsForRotatingTool();
		} catch (Exception e) {
			e.printStackTrace();
			throw new SkipException("Skipped Test case due to an error while api setup...!!!");
		}

		login(af);
		updateData();
	}

	@Test(groups = {
			"mobile" }, description = "Verify calibration touchpoint is visible and fill datasheets to complete workorder", timeOut = 95000000)
	public void verifyCalibrationToolAdded() throws Exception {
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage assignedWorkOrderDetailsPage = new WorkOrderDetailsPage(af);
		ReportWorkPage reportWork = new ReportWorkPage(af);

		// Search the WO
		assignedWorkPage.checkForUpdateButton();
		assertEquals(true, assignedWorkPage.search(woNum));

		// Navigate to work order details page
		assignedWorkPage.openCardDetails();

		// Navigate to Report work page
		assignedWorkOrderDetailsPage.clickReportWorkButton();
		AddToolPage addToolPage = reportWork.clickAddToolButton();
		addToolPage.tillPageLoaded();
		
		addToolPage.tillPageLoaded();

		// Select the rotating tool which is having the rotating asset.
		addToolPage.selectCalTool(itemnumRot, itemIdQueryFromDB(itemnumRot));

		// technician should be able see the rotating asset field in tool slider
		logger.info("start to verify the asset populated...");
		 assertEquals(assetNumRot, addToolPage.getCalAssetNumber());
		logger.info("passed: tool added after click save button");

		addToolPage.clickCalSaveButton();
		logger.info("save button clicked");
		addToolPage.tillPageLoaded();

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

	protected void createDefaultObjects(String configPath) throws Exception {
		try {

			// Create a location
			logger.info("Creating a location");
			locationNum = AbstractAutomationFramework.randomString(5).toUpperCase();
			Location newlocation = new Location();

			newlocation.setLocationId(locationNum);
			newlocation.setDescription(LOCATION_DESCRIPTION);
			newlocation.setSiteId(SetupData.SITEID);
			assertEquals(maximoApi.create(newlocation), SetupData.CreatedSuccess);
			logger.info("location: {}" + locationNum);

			// Create an asset
			logger.info("Creating an asset");
			assetNum = AbstractAutomationFramework.randomString(5).toUpperCase();
			String assetResult = maximoApi.retrieve(new Asset(),
					"addid=1&internalvalues=1&action=system:new&addschema=1");

			Asset newAsset = new Gson().fromJson(assetResult, Asset.class);

			newAsset.setAssetNum(assetNum);
			newAsset.setDescription(assetDescription);
			newAsset.setSiteId(SetupData.SITEID);
			newAsset.setLocation(locationNum);
			newAsset.setIscalibration(true);
			newAsset.setStatus(SetupData.LocAssetStatus.ACTIVE.toString());

			assertEquals(maximoApi.create(newAsset), SetupData.CreatedSuccess);
			logger.info("Asset: {}" + assetNum);

			// Create a work order
			logger.info("Creating a work order");
			woNum = AbstractAutomationFramework.randomString(5).toUpperCase();
			String workOrderResult = maximoApi.retrieve(new WorkOrder(),
					"addid=1&internalvalues=1&action=system:new&addschema=1");
			newWorkOrder = new Gson().fromJson(workOrderResult, WorkOrder.class);
			newWorkOrder.setWoNum(woNum);
			newWorkOrder.setDescription("WorkeOrder for mobile calibration automation test");

			newWorkOrder.setSiteId(SetupData.SITEID);
			newWorkOrder.setAssetNum(assetNum);
			newWorkOrder.setWorkType(WorkType.PM.toString());
			newWorkOrder.setLocation(locationNum);
			newWorkOrder.setGlaccount(SetupData.GLDEBITACCT);
			maximoApi.create(newWorkOrder);
			logger.info("Work Order: {}" + woNum);

			this.datasheetFactory
				.createDataSheet(newWorkOrder, labor, this.getTargetModel(), jdbcConnection);

			// Assignment labor to the WO
			maximoApi.addAssignmentLabor(newWorkOrder, labor);
			logger.info("Assignment added");

			// Change WO status to Approved
			logger.info("Changing work order status to APPR");
			maximoApi.changeStatus(newWorkOrder, WoStatus.APPR.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	/**
	 * For Scenario 19
	 * 
	 * @throws Exception
	 */
	private void createObjectsForRotatingTool() throws Exception {

		
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


	}

	// Assisted by watsonx Code Assistant 
	/**
	 * This function retrieves the item ID from the database based on the tool number.
	 * @param {string} toolNum - The tool number to query.
	 * @returns {string} The item ID retrieved from the database.
	 */
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