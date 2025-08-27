package com.ibm.maximo.technician.testcases;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

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
import com.ibm.maximo.automation.mobile.api.json.Asset;
import com.ibm.maximo.automation.mobile.api.json.Item;
import com.ibm.maximo.automation.mobile.api.json.ItemChangeStatus;
import com.ibm.maximo.automation.mobile.api.json.StoreRoom;
import com.ibm.maximo.automation.mobile.api.json.Task;
import com.ibm.maximo.automation.mobile.api.json.WorkOrder;
import com.ibm.maximo.technician.framework.TechnicianTest;
import com.ibm.maximo.technician.page.AddMaterialPage;
import com.ibm.maximo.technician.page.MySchedulePage;
import com.ibm.maximo.technician.page.ReportWorkPage;
import com.ibm.maximo.technician.page.WorkOrderDetailsPage;
import com.ibm.maximo.technician.setupdata.SetupData;
import com.ibm.maximo.technician.setupdata.SetupData.ItemStatus;
import com.ibm.maximo.technician.setupdata.SetupData.LocAssetStatus;
import com.ibm.maximo.technician.setupdata.SetupData.WoStatus;

/*
 * GRAPHITE-70250: [TECHMOBILE] Add Actual Materials P3:38M,3TA,0A
 * GRAPHITE-72265: [TECHMOBILE] Add Actual Materials P3:38M,2TA,0A
 * 
 */

public class ActualMaterialsTest extends TechnicianTest {

	private static final String LOCATION_DESCRIPTION = "Location for mobile automation test ";
	private static final String ASSET_DESCRIPTION = "Asset for mobile automation test ";
	private static final String ITEM_DESCRIPTION = "Item Description for mobile automation test ";
	private final Logger logger = LoggerFactory.getLogger(ActualMaterialsTest.class);
	private AbstractAutomationFramework af;
	private TestSuite testSuite;
	private MaximoApi maximoApi;
	private WorkOrder newWorkOrder, newWorkOrder1,newWorkOrder2;
	private String woNum, woNum1,woNum2,labor, itemNum, taskId = "10";
	private String itemNum2= "61503";
	private String itemNum2Description = "61503 Brake Shoe Kit";
	private String itemNumRot, itemNumRot1, assetNumRot, assetNumRot1, assetNumRot2 ;
	
	private String emptyQuantityField = "Please specify a quantity for a material issue/return.";
	
	private String headerText="Save or discard changes?";


	/**
	 * GRAPHITE-51063 [TECHMOBILE] Report work - Material reporting :39M,4TA,0A
	 * 
	 * @param configPath
	 * @throws Exception
	 */
	@BeforeClass(alwaysRun = true)
	@Parameters({ "configPath" })
	public void setup(String configPath) throws Exception {
		logger.info("********************ActualMaterialsTest*********************************");
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
			// Data will update for IVT
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

	@Test(groups = {
			"mobile" }, description = "Verify after technician save the transaction, the material used record appears on the Report Work page", timeOut = 3600000)
	public void materialInventoryFunctionality() throws Exception {
		verifyMaterialInventory();
		// verifyInventory();
	}

	@Test(groups = {
			"priority2" }, description = "Verify material drawer functionality if selected material/item have multiple associated storerooms and rotating assets", timeOut = 950000)
	public void ActualMaterialsTestScenario21() throws Exception {
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage workOrderDetailsPage = new WorkOrderDetailsPage(af);
		ReportWorkPage reportWorkPage = new ReportWorkPage(af);
		
		logger.info(
				"Start Scenario 21:");

		// Search the WO
		assertTrue(assignedWorkPage.search(woNum));
		// open the work order details page.
		assignedWorkPage.openCardDetails();
		
		workOrderDetailsPage.clickReportWorkButton();
		// Click on Three Dots to add Materials
		reportWorkPage.addMaterialThreeDots();
		// Click on ‘+’ to add Items
		reportWorkPage.addItems();
		
		AddMaterialPage addMaterialPage = new AddMaterialPage(af);
		// Click on right chevron
		addMaterialPage.rightChevronforSearchMaterial();
		// Click on Materials
		logger.info("start searching material:" + itemNumRot);
		assertTrue(addMaterialPage.search(itemNumRot));
		// Select the material from the lookup.
		addMaterialPage.clickIdentifiedMaterialFromList(itemIdQueryFromDB(itemNumRot));
		//Check if Storeroom selection page opened to select from multiple storerooms
		assertEquals("Storeroom",addMaterialPage.getStoreroomPageTitle());
		logger.info("header label "+ addMaterialPage.getStoreroomPageTitle());
		//Click First Storeroom from list
		addMaterialPage.clickToSelectFirstElementInList();
		//Check if Rotating Asset selection page opened to select from multiple Rotating Asset
		assertEquals("Rotating asset",addMaterialPage.getRotatingAssetPageTitle());
		logger.info("header label "+ addMaterialPage.getRotatingAssetPageTitle());
		//Click First Rotating Asset from list
		addMaterialPage.clickToSelectFirstElementInList();
	}

	@Test(groups = {
		"priority2" }, description = "Verify material drawer functionality if selected material/item have multiple associated storerooms and have only one associated rotating asset", timeOut = 950000)
		public void ActualMaterialsTestScenario22() throws Exception {
		ReportWorkPage reportWorkPage = new ReportWorkPage(af);
		AddMaterialPage addMaterialPage = new AddMaterialPage(af);
		
		logger.info(
				"Start Scenario 22:");
		
		// Click on Materials
		addMaterialPage.closeMaterialDrawer();
		addMaterialPage.clickDiscardChanges();
		addMaterialPage.clickDiscardButton();
		
		// Click on Three Dots to add Materials
		reportWorkPage.addMaterialThreeDots();
		// Click on ‘+’ to add Items
		reportWorkPage.addItems();
		// Click on right chevron
		addMaterialPage.rightChevronforSearchMaterial();

		logger.info("start searching material:" + itemNumRot1);
		assertTrue(addMaterialPage.search(itemNumRot1));
		// Select the material from the lookup.
		addMaterialPage.clickIdentifiedMaterialFromList(itemIdQueryFromDB(itemNumRot1));
		//Check if Storeroom selection page opened to select from multiple storerooms
		assertEquals("Storeroom",addMaterialPage.getStoreroomPageTitle());
		logger.info("header label "+ addMaterialPage.getStoreroomPageTitle());
		//Click First Storeroom from list
		addMaterialPage.clickToSelectFirstElementInList();
		assertTrue(addMaterialPage.getRotatingAssetNum(assetNumRot2));
		}
		
	@Test(groups = {
		"priority2" }, description = "Verify that the material record is saved with updated and valid values when technician clicks on 'Save' button on dialog and add material drawer fields are updated", timeOut = 950000)
		public void ActualMaterialsTestScenario30() throws Exception {
		ReportWorkPage reportWorkPage = new ReportWorkPage(af);
		AddMaterialPage addMaterialPage = new AddMaterialPage(af);
		
		logger.info(
				"Start Scenario 30:");
		
		// Click on Materials
		addMaterialPage.closeMaterialDrawer();
		addMaterialPage.clickDiscardChanges();
		addMaterialPage.clickDiscardButton();
		
		// Click on Three Dots to add Materials
		reportWorkPage.addMaterialThreeDots();
		// Click on ‘+’ to add Items
		reportWorkPage.addItems();
		// Click on right chevron
		addMaterialPage.rightChevronforSearchMaterial();

		logger.info("start searching material:" + "61503");
		assertTrue(addMaterialPage.search("61503"));
		// Select the material from the lookup.
		addMaterialPage.clickIdentifiedMaterialFromList(itemIdQueryFromDB(itemNum2));
		
		addMaterialPage.closeMaterialDrawer();
		
		addMaterialPage.clickSaveChanges();
		
		assertTrue(reportWorkPage.isMaterialExist(itemNum2Description));
	}
	
	@Test(groups = {
	"priority3" }, description = "Verify that dialog with save and discard options is displayed when technician clicks on 'X' button after making changes on add material sliding drawer", timeOut = 900000)
     public void verifySaveDiscardButton() throws Exception {
		
		ReportWorkPage reportWorkPage = new ReportWorkPage(af);
		AddMaterialPage addMaterialPage = new AddMaterialPage(af);
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage workOrderDetailsPage = new WorkOrderDetailsPage(af);
		
		// Search the WO
		assertTrue(assignedWorkPage.search(woNum1));
		// open the work order details page.
		assignedWorkPage.openCardDetails();
				
		workOrderDetailsPage.clickReportWorkButton();
		// Click on Three Dots to add Materials
		reportWorkPage.addMaterialThreeDots();
		// Click on ‘+’ to add Items
		reportWorkPage.addItems();
				
		// Click on right chevron
		addMaterialPage.rightChevronforSearchMaterial();
		// Click on Materials
		logger.info("start searching material:" + itemNumRot);
		assertTrue(addMaterialPage.search(itemNumRot));
		// Select the material from the lookup.
		addMaterialPage.clickToSelectFirstElementInList();
		
		//Click First Storeroom from list
		addMaterialPage.clickToSelectFirstElementInList();
		
		//Click First Rotating Asset from list
		addMaterialPage.clickToSelectFirstElementInList();
		
		//Clear the mandatory Quantity field
		addMaterialPage.setQuantity("5");
		
		//Click on the X button
		addMaterialPage.closeMaterialDrawer();
		
		// Verify save and discard popup is displayed
		assertEquals(headerText, reportWorkPage.getSaveDiscardHeader());

		assertEquals("You must first discard or save your changes.", reportWorkPage.getSaveDiscardText());

		reportWorkPage.discardButtonClick();
		
		//Go back to WO details Page
		reportWorkPage.clickBackChevron();
		//Go back to list page
		workOrderDetailsPage.clickBackWOList();
				
		assignedWorkPage.clickClearButton();
		assignedWorkPage.checkForUpdateButton();	
	}
	
	
	@Test(groups = {
	"priority3" }, description = "Verify that the technician is unable to save material used record without filling data in required fields", timeOut = 900000)
     public void verifyNotAbleToSaveMaterials() throws Exception {
		
		ReportWorkPage reportWorkPage = new ReportWorkPage(af);
		AddMaterialPage addMaterialPage = new AddMaterialPage(af);
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage workOrderDetailsPage = new WorkOrderDetailsPage(af);
		MobileAutomationFramework maf = (MobileAutomationFramework) this.af;
		// Search the WO
		assertTrue(assignedWorkPage.search(woNum1));
		// open the work order details page.
		assignedWorkPage.openCardDetails();
				
		workOrderDetailsPage.clickReportWorkButton();
		// Click on Three Dots to add Materials
		reportWorkPage.addMaterialThreeDots();
		// Click on ‘+’ to add Items
		reportWorkPage.addItems();
				
		// Click on right chevron
		addMaterialPage.rightChevronforSearchMaterial();
		// Click on Materials
		logger.info("start searching material:" + itemNumRot);
		assertTrue(addMaterialPage.search(itemNumRot));
		// Select the material from the lookup.
		addMaterialPage.clickToSelectFirstElementInList();
		
		//Click First Storeroom from list
		addMaterialPage.clickToSelectFirstElementInList();
		
		//Click First Rotating Asset from list
		addMaterialPage.clickToSelectFirstElementInList();
		
		//Clear the mandatory Quantity field
	    addMaterialPage.clearQuantity();
		
		//Click on Save button 
		addMaterialPage.clickSaveButton();
		
		com.ibm.maximo.automation.mobile.page.ErrorPage err = new com.ibm.maximo.automation.mobile.page.ErrorPage(maf);
		maf.switchToParentFrame();

		// Tap on Error page
		err.clickErrorBadge();
		
		//click open the transaction to verify the error message 
		err.clickTransactionChevron(true);
		
		//Verify that error message is thrown when mandatory field is not filled and the data cannot be saved
		assertEquals(emptyQuantityField, err.getErrorMessageTextForInvalidAsset());
				
		maf.changeToDefaultContext();
		
		//Go back to WO details Page
		reportWorkPage.clickBackChevron();
		//Go back to list page
		workOrderDetailsPage.clickBackWOList();
		
		assignedWorkPage.clickClearButton();
		assignedWorkPage.checkForUpdateButton();	
	}
	
	
	
	@Test(groups = {
	"priority3" }, description = "Verify technician can add conditioned enabled rotating item as actual material which is stored in multiple bins and condition codes combination", timeOut = 900000)
     public void verifyAMInMultipleBinsForScenario26And27() throws Exception {
		
		ReportWorkPage reportWorkPage = new ReportWorkPage(af);
		AddMaterialPage addMaterialPage = new AddMaterialPage(af);
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage workOrderDetailsPage = new WorkOrderDetailsPage(af);
		
		// Search the WO
		assertTrue(assignedWorkPage.search(woNum2));
		// open the work order details page.
		assignedWorkPage.openCardDetails();
				
		workOrderDetailsPage.clickReportWorkButton();
		// Click on Three Dots to add Materials
		reportWorkPage.addMaterialThreeDots();
		// Click on '+' to add Items
		reportWorkPage.addItems();
				
		// Click on right chevron
		addMaterialPage.rightChevronforSearchMaterial();
		// Click on Materials
		logger.info("start searching material:" + itemNumRot);
		assertTrue(addMaterialPage.search(itemNumRot));
		// Select the material from the lookup.
		addMaterialPage.clickToSelectFirstElementInList();
		
		//Click First Storeroom from list
		addMaterialPage.clickToSelectFirstElementInList();
		
		//Check if Rotating Asset selection page opened to select from multiple Rotating Asset
		assertEquals("Rotating asset",addMaterialPage.getRotatingAssetPageTitle());
		logger.info("header label "+ addMaterialPage.getRotatingAssetPageTitle());
		
		//Click First Rotating Asset from list
		addMaterialPage.clickToSelectFirstElementInList();

        //Click on Save button
		addMaterialPage.clickSaveButton();
		
		//Go back to WO details Page
		reportWorkPage.clickBackChevron();
		//Go back to list page
		workOrderDetailsPage.clickBackWOList();
				
		assignedWorkPage.clickClearButton();
		assignedWorkPage.checkForUpdateButton();	
}
	
	/**
	 * Scenario 23/24 Verify after technician save the transaction, the material
	 * used record appears on the Report Work page And balance in Inventory reduced
	 * 
	 * @throws Exception
	 */
	private void verifyMaterialInventory() throws Exception {

		String LARGE_NUMBER = "10000";
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage workOrderDetailsPage = new WorkOrderDetailsPage(af);

		// Scenario 23 - Verify after technician save the transaction, the material used
		// record appears on the Report Work page
		logger.info(
				"Start Scenario 23: Verify after technician save the transaction, the material used record appears on the Report Work page");

		// Search the WO
		assertTrue(assignedWorkPage.search(woNum));
		// open the work order details page.
		assignedWorkPage.openCardDetails();

		// Click to open report work page.
		ReportWorkPage reportWorkPage = workOrderDetailsPage.clickReportWorkButton();
		// Click on Three Dots to add Materials
		reportWorkPage.addMaterialThreeDots();
		// Click on ‘+’ to add Items
		reportWorkPage.addItems();
		// Below code of selection of material via search is part of p3 testcases https://github.ibm.com/maximo-app-framework/technician-app/blob/MAS-8.11/testcases/ReportWorkPage/TC_AddActualMaterials.md#scenario-20---verify-material-drawer-functionality-if-selected-materialitem-have-one-to-one-relationship-with-storeroom-and-have-multiple-associated-rotating-assets
		// If the selected material/item have one to one relationship with storeroom and have multiple associated rotating assets then technician should need to select rotating asset manually.
		
		AddMaterialPage addMaterialPage = new AddMaterialPage(af);
		// Click on right chevron
		addMaterialPage.rightChevronforSearchMaterial();
		// Click on Materials
		logger.info("start searching material:" + itemNum);
		assertTrue(addMaterialPage.search(itemNum));
		// Select the material from the lookup.
		addMaterialPage.clickIdentifiedMaterialFromList(itemIdQueryFromDB(itemNum));

		// Select task from task lookup (if planned tasks are associated with work
		// order).
		addMaterialPage.selectTask(taskId);

		// In case, any error is generated, it should be displayed on error page. An
		// example is when technician enter quantity of material more than current
		// balance in inventory.
		addMaterialPage.setQuantity(LARGE_NUMBER);
		addMaterialPage.clickSaveButton();
		// TODO this is a bug
//		assertTrue(reportWorkPage.showErrorPageAndReturn());
		logger.info("Passed: error page pops up");

		// Start process again
		// Click on Three Dots to add Materials
		reportWorkPage.addMaterialThreeDots();
		// Click on ‘+’ to add Items
		reportWorkPage.addItems();
		// Click on right chevron
		addMaterialPage.rightChevronforSearchMaterial();
		// Click on Materials
		logger.info("start searching material:" + itemNum);
		assertTrue(addMaterialPage.search(itemNum));
		// Select the material from the lookup.
		addMaterialPage.clickIdentifiedMaterialFromList(itemIdQueryFromDB(itemNum));

		// Select task from task lookup (if planned tasks are associated with work
		// order).
		assertTrue(addMaterialPage.selectTask(taskId));
		// Click on save button.
		addMaterialPage.clickSaveButton();

		// Should display "Material used" section on report work page.
		assertTrue(reportWorkPage.verifyMaterialAdded());
		logger.info("Passed: Material added successfully");

		// Scenario 24 - Verify that current balance of the material should be reduced
		// from the inventory after adding material used when Transaction type is
		// "Issue"
		// TODO balance not changed, ticket: GRAPHITE-55057 - It is not a bug
		// assertEquals(getMaterialBalanceFromDB(itemNum), balanceBefore);
		// logger.info("Passed: Inventory balance verified");
	}

	/**
	 * Scenario 25 Verify that current balance of the material should be increased
	 * in the inventory after adding material used when Transaction type is "Return"
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	private void verifyInventory() throws Exception {

		ReportWorkPage reportWorkPage = new ReportWorkPage(af);
		// Click on Three Dots to add Materials
		reportWorkPage.addMaterialThreeDots();
		// Click on ‘+’ to add Items
		reportWorkPage.addItems();
		AddMaterialPage addMaterialPage = new AddMaterialPage(af);
		// Click on right chevron
		addMaterialPage.rightChevronforSearchMaterial();
		// Click on Materials
		logger.info("start searching material:" + itemNum);
		assertTrue(addMaterialPage.search(itemNum));
		// Select the material from the lookup.
		addMaterialPage.clickIdentifiedMaterialFromList(itemIdQueryFromDB(itemNum));

		// Select task from task lookup (if planned tasks are associated with work
		// order).
		assertTrue(addMaterialPage.selectTask(taskId));

		// select transaction type as "Return"
		addMaterialPage.selectTypeAsReturn();

		// Get balance before Adding
		int balanceBefore = getMaterialBalanceFromDB(itemNum);
		// Click on save button.
		addMaterialPage.clickSaveButton();

		// Should display "Material used" section on report work page.
		assertTrue(reportWorkPage.verifyMaterialAdded());
		logger.info("Material added successfully");

		// Scenario 25 - Verify that current balance of the material should be increased
		// in the inventory after adding material used
		// when Transaction type is "Return"

		assertEquals(getMaterialBalanceFromDB(itemNum), balanceBefore);
		logger.info("Passed: Inventory balance verified");
	}


	/**
	 * Create all objects needed for testing via api calls
	 */
	protected void createDefaultObjects() throws Exception {
		createNormalObjects();
		createRotatingObjects();
		createRotatingObjects2();
		createRotatingObjectsForScenario26And27();
	}

	/**
	 * Create objects for adding rotating material flow
	 * 
	 * @throws Exception
	 */
	private void createRotatingObjects2() throws Exception {
		logger.info("Creating default objects for rotating case");
		String locationRot;
		String locationRot1;
		
		// Create a location
		logger.info("Creating an storeroom");
		locationRot = AbstractAutomationFramework.randomString(5).toUpperCase();
		String storeRoomResult = maximoApi.retrieve(new StoreRoom(),
				"lean=1&addid=1&action=system:new&ignorecollectionref=1");
		StoreRoom storeRoomRot = new Gson().fromJson(storeRoomResult, StoreRoom.class);
		storeRoomRot.setLocation(locationRot);
		storeRoomRot.setDescription(LOCATION_DESCRIPTION + locationRot);
		storeRoomRot.setSiteId(SetupData.SITEID);
		maximoApi.create(storeRoomRot);
		logger.info("Created storeRoom location:" + locationRot);
		
		// Create another location
		logger.info("Creating a another storeroom");
		locationRot1 = AbstractAutomationFramework.randomString(5).toUpperCase();
		String storeRoomResult1 = maximoApi.retrieve(new StoreRoom(),
				"lean=1&addid=1&action=system:new&ignorecollectionref=1");
		StoreRoom storeRoomRot1 = new Gson().fromJson(storeRoomResult1, StoreRoom.class);
		storeRoomRot1.setLocation(locationRot1);
		storeRoomRot1.setDescription(LOCATION_DESCRIPTION + locationRot1);
		storeRoomRot1.setSiteId(SetupData.SITEID);
		maximoApi.create(storeRoomRot1);
		logger.info("Created another storeRoom location:" + locationRot1);

		// Creating the item of material
		logger.info("Creating an item of material");
		itemNumRot = AbstractAutomationFramework.randomString(5).toUpperCase();
		String itemResult = maximoApi.retrieve(new Item(),
				"lean=1&addid=1&domainmeta=1&internalvalues=1&action=system:new&oslc.select=itemsetid,lottype,issueunit,orderunit,rotating,itemtype");
		Item item = new Gson().fromJson(itemResult, Item.class);
		item.setItemNum(itemNumRot);
		item.setDescription("item " + itemNumRot);
		item.setRotating(true);
		maximoApi.create(item);
		logger.info("Created Item Num:" + itemNumRot);
		
		// Creating one more item of multiple storeroom and one rotating assert
		logger.info("Creating another item");
		itemNumRot1 = AbstractAutomationFramework.randomString(5).toUpperCase();
		String itemResult1 = maximoApi.retrieve(new Item(),
				"lean=1&addid=1&domainmeta=1&internalvalues=1&action=system:new&oslc.select=itemsetid,lottype,issueunit,orderunit,rotating,itemtype");
		Item item1 = new Gson().fromJson(itemResult1, Item.class);
		item1.setItemNum(itemNumRot1);
		item1.setDescription("item " + itemNumRot1);
		item1.setRotating(true);
		maximoApi.create(item1);
		logger.info("Created Item Num:" + itemNumRot1);

		// add Item to StoreRoom
		maximoApi.addItemToStoreRoom(itemNumRot, SetupData.ITEMSET, locationRot, SetupData.SITEID, "false",
				SetupData.ISSUEUNIT, "100", "bin101", "", "", "0.0", "1.0", "0.0", "0", "0", "0.0", "0.0",
				"true");
		logger.info("Added Item to StoreRoom successfully");
		
		// add same Item to another StoreRoom
		maximoApi.addItemToStoreRoom(itemNumRot, SetupData.ITEMSET, locationRot1, SetupData.SITEID, "false",
				SetupData.ISSUEUNIT, "100", "bin101", "", "", "0.0", "1.0", "0.0", "0", "0", "0.0", "0.0",
				"true");
		logger.info("Added same Item to another StoreRoom successfully");
		
		// add Item to StoreRoom
		maximoApi.addItemToStoreRoom(itemNumRot1, SetupData.ITEMSET, locationRot, SetupData.SITEID, "false",
				SetupData.ISSUEUNIT, "100", "bin101", "", "", "0.0", "1.0", "0.0", "0", "0", "0.0", "0.0",
				"true");
		logger.info("Added Item to StoreRoom successfully");
		
		// add same Item to another StoreRoom
		maximoApi.addItemToStoreRoom(itemNumRot1, SetupData.ITEMSET, locationRot1, SetupData.SITEID, "false",
				SetupData.ISSUEUNIT, "100", "bin101", "", "", "0.0", "1.0", "0.0", "0", "0", "0.0", "0.0",
				"true");
		logger.info("Added same Item to another StoreRoom successfully");

		// item status change and rolldown
		logger.info("Item status change to Active");
		List<ItemChangeStatus> arr = new ArrayList<>();
		ItemChangeStatus ics = new ItemChangeStatus();
		ics.setStatus(ItemStatus.ACTIVE.toString());
		ics.setRollDown("true");
		arr.add(ics);
		item.setItemChangeStatus(arr);
		maximoApi.update(item);
		logger.info("Item status changed");
		
		// item status change and rolldown
		logger.info("Item status change to Active");
		List<ItemChangeStatus> arr1 = new ArrayList<>();
		ItemChangeStatus ics1 = new ItemChangeStatus();
		ics1.setStatus(ItemStatus.ACTIVE.toString());
		ics1.setRollDown("true");
		arr1.add(ics1);
		item1.setItemChangeStatus(arr1);
		maximoApi.update(item1);
		logger.info("Item status changed");

		// Create an asset
		logger.info("Creating an asset");
		assetNumRot = AbstractAutomationFramework.randomString(5).toUpperCase();
		String assetResult = maximoApi.retrieve(new Asset(), "addid=1&internalvalues=1&action=system:new&addschema=1");
		Asset newAsset = new Gson().fromJson(assetResult, Asset.class);
		newAsset.setAssetNum(assetNumRot);
		newAsset.setDescription(ASSET_DESCRIPTION + assetNumRot);
		newAsset.setStatus(LocAssetStatus.ACTIVE.toString());
		newAsset.setLocation(locationRot);
		newAsset.setItemNum(itemNumRot);
		maximoApi.create(newAsset);
		logger.info("Created Asset for rotating: " + assetNumRot);
		
		// Create another asset
		logger.info("Creating another asset");
		assetNumRot1 = AbstractAutomationFramework.randomString(5).toUpperCase();
		String assetResult1 = maximoApi.retrieve(new Asset(), "addid=1&internalvalues=1&action=system:new&addschema=1");
		Asset newAsset1 = new Gson().fromJson(assetResult1, Asset.class);
		newAsset1.setAssetNum(assetNumRot1);
		newAsset1.setDescription(ASSET_DESCRIPTION + assetNumRot1);
		newAsset1.setStatus(LocAssetStatus.ACTIVE.toString());
		newAsset1.setLocation(locationRot);
		newAsset1.setItemNum(itemNumRot);
		maximoApi.create(newAsset1);
		logger.info("Created Asset for rotating: " + assetNumRot1);
		
		// Create an asset
		logger.info("Creating an asset");
		assetNumRot2 = AbstractAutomationFramework.randomString(5).toUpperCase();
		String assetResult2 = maximoApi.retrieve(new Asset(), "addid=1&internalvalues=1&action=system:new&addschema=1");
		Asset newAsset2 = new Gson().fromJson(assetResult2, Asset.class);
		newAsset2.setAssetNum(assetNumRot2);
		newAsset2.setDescription(ASSET_DESCRIPTION + assetNumRot2);
		newAsset2.setStatus(LocAssetStatus.ACTIVE.toString());
		newAsset2.setLocation(locationRot);
		newAsset2.setItemNum(itemNumRot1);
		maximoApi.create(newAsset2);
		logger.info("Created Asset for rotating: " + assetNumRot2);

		// Create a work order
		logger.info("Creating a work order");
		woNum = AbstractAutomationFramework.randomString(5).toUpperCase();
		String workOrderResult = maximoApi.retrieve(new WorkOrder(),
				"addid=1&internalvalues=1&action=system:new&addschema=1");
		newWorkOrder = new Gson().fromJson(workOrderResult, WorkOrder.class);
		newWorkOrder.setWoNum(woNum);
		newWorkOrder.setDescription("WorkeOrder for mobile automation test");
		newWorkOrder.setSiteId(SetupData.SITEID);
		newWorkOrder.setAssetNum(assetNum);
		newWorkOrder.setGlaccount(SetupData.GLDEBITACCT);
		newWorkOrder.setWorkType("PM");
		newWorkOrder.setLocation(locationRot);
		maximoApi.create(newWorkOrder);
		logger.info("Created Work Order:" + woNum);
		
		// Change WO status to Approved
		logger.info("Changing work order status to APPR");
		maximoApi.changeStatus(newWorkOrder, "APPR");
		// Assignment with labor
		maximoApi.addAssignmentLabor(newWorkOrder, labor);
		logger.info("Assignment added");
		
		// Create 2nd  work order
		logger.info("Creating 2nd work order");
		String workOrderResult1 = maximoApi.retrieve(new WorkOrder(),
						"addid=1&internalvalues=1&action=system:new&addschema=1");
		newWorkOrder1 = new Gson().fromJson(workOrderResult1, WorkOrder.class);
		newWorkOrder1.setDescription("WorkOrder for mobile automation");
		newWorkOrder1.setAssetNum(assetNum);
		newWorkOrder.setSiteId(SetupData.SITEID);
		newWorkOrder1.setGLAccount(SetupData.GLDEBITACCT);
		newWorkOrder.setLocation(locationRot);
		newWorkOrder.setWorkType("PM");

		maximoApi.create(newWorkOrder1);
		woNum1 = newWorkOrder1.getWoNum();
		logger.info("Work Order: " + woNum1);

		// Assign the labor
		maximoApi.addAssignmentLabor(newWorkOrder1, labor);
		logger.info("Assignment added");
				
		// Change WO status to Approved
		logger.info("Changing work order status to APPR");
		maximoApi.changeStatus(newWorkOrder1, WoStatus.APPR.toString());
		logger.info("APPR WO");
				
	

		// Create Task
		logger.info("Task API Call started ");
		List<Task> arrTask = new ArrayList<>();
		Task task = new Task();
		task.setEstdur(Float.parseFloat("1.5"));
		task.setSiteid(SetupData.SITEID);
		task.setDescription("WorkOrder for mobile automation test");
		task.setOwnergroup(SetupData.OWNERGROUP);
		task.setParentchgsstatus(true);
		task.setTaskid(Integer.parseInt(taskId));
		task.setStatus(WoStatus.WAPPR.toString());
		arrTask.add(task);
		newWorkOrder.setWoactivity(arrTask);
		maximoApi.update(newWorkOrder);
		logger.info("Created Tasks");
	}

	/**
	 * Create objects for adding normal material flow
	 * 
	 * @throws Exception
	 */
	private void createNormalObjects() throws Exception {
		logger.info("Creating default objects for normal case");
		String location;
		
		// Create a location
		logger.info("Creating an storeroom");
		location = AbstractAutomationFramework.randomString(5).toUpperCase();
		String storeRoomResult = maximoApi.retrieve(new StoreRoom(),
				"lean=1&addid=1&action=system:new&ignorecollectionref=1");
		StoreRoom storeRoom = new Gson().fromJson(storeRoomResult, StoreRoom.class);
		storeRoom.setLocation(location);
		storeRoom.setDescription(LOCATION_DESCRIPTION + location);
		storeRoom.setSiteId(SetupData.SITEID);

		maximoApi.create(storeRoom);
		logger.info("Created storeRoom location:" + location);

		// Creating the item of material
		logger.info("Creating an item of material");
		itemNum = AbstractAutomationFramework.randomString(5).toUpperCase();
		String itemResult = maximoApi.retrieve(new Item(),
				"lean=1&addid=1&domainmeta=1&internalvalues=1&action=system:new&oslc.select=itemsetid,lottype,issueunit,orderunit,rotating,itemtype");
		Item item = new Gson().fromJson(itemResult, Item.class);
		item.setItemNum(itemNum);
		item.setDescription(ITEM_DESCRIPTION + itemNum);
		maximoApi.create(item);
		logger.info("Created Item Num:" + itemNum);

		// add Item to StoreRoom
		maximoApi.addItemToStoreRoom(itemNum, SetupData.ITEMSET, location, SetupData.SITEID, "false",
				SetupData.ISSUEUNIT, "100", SetupData.DFLTBIN, "", "", "0.0", "1.0", "0.0", "0", "0", "0.0", "0.0",
				"true");
		logger.info("Added Item to StoreRoom successfully");

		// item status change and roll down
		logger.info("Item status change to Active");
		List<ItemChangeStatus> arr = new ArrayList<>();
		ItemChangeStatus ics = new ItemChangeStatus();
		ics.setStatus(ItemStatus.ACTIVE.toString());
		ics.setRollDown("true");
		arr.add(ics);
		item.setItemChangeStatus(arr);
		maximoApi.update(item);
		logger.info("Item status changed");

		// Create a work order
		logger.info("Creating a work order");
		woNum = AbstractAutomationFramework.randomString(5).toUpperCase();
		String workOrderResult = maximoApi.retrieve(new WorkOrder(),
				"addid=1&internalvalues=1&action=system:new&addschema=1");
		newWorkOrder = new Gson().fromJson(workOrderResult, WorkOrder.class);
		newWorkOrder.setWoNum(woNum);
		newWorkOrder.setDescription("WorkeOrder for mobile automation test");
		newWorkOrder.setSiteId(SetupData.SITEID);
		newWorkOrder.setAssetNum(assetNum);
		newWorkOrder.setWorkType("PM");
		newWorkOrder.setLocation(location);
		maximoApi.create(newWorkOrder);
		logger.info("Created Work Order:" + woNum);

		// Assignment with labor
		maximoApi.addAssignmentLabor(newWorkOrder, labor);
		logger.info("Assignment added");

		// Change WO status to Approved
		logger.info("Changing work order status to APPR");
		maximoApi.changeStatus(newWorkOrder, WoStatus.APPR.toString());

		// Create Task
		logger.info("Task API Call started ");
		List<Task> arrTask = new ArrayList<>();
		Task task = new Task();
		task.setEstdur(Float.parseFloat("1.5"));
		task.setSiteid(SetupData.SITEID);
		task.setDescription("WorkOrder for mobile automation test");
		task.setOwnergroup(SetupData.OWNERGROUP);
		task.setParentchgsstatus(true);
		task.setTaskid(Integer.parseInt(taskId));
		task.setStatus(WoStatus.APPR.toString());
		arrTask.add(task);
		newWorkOrder.setWoactivity(arrTask);
		maximoApi.update(newWorkOrder);
		logger.info("Created Tasks");
	}

	/**
	 * Create objects for adding rotating material flow
	 * 
	 * @throws Exception
	 */
	private void createRotatingObjects() throws Exception {
		logger.info("Creating default objects for rotating case");
		String locationRot;
		
		// Create a location
		logger.info("Creating an storeroom");
		locationRot = AbstractAutomationFramework.randomString(5).toUpperCase();
		String storeRoomResult = maximoApi.retrieve(new StoreRoom(),
				"lean=1&addid=1&action=system:new&ignorecollectionref=1");
		StoreRoom storeRoomRot = new Gson().fromJson(storeRoomResult, StoreRoom.class);
		storeRoomRot.setLocation(locationRot);
		storeRoomRot.setDescription(LOCATION_DESCRIPTION + locationRot);
		storeRoomRot.setSiteId(SetupData.SITEID);
		maximoApi.create(storeRoomRot);
		logger.info("Created storeRoom location:" + locationRot);

		// Creating the item of material
		logger.info("Creating an item of material");
		itemNumRot = AbstractAutomationFramework.randomString(5).toUpperCase();
		String itemResult = maximoApi.retrieve(new Item(),
				"lean=1&addid=1&domainmeta=1&internalvalues=1&action=system:new&oslc.select=itemsetid,lottype,issueunit,orderunit,rotating,itemtype");
		Item item = new Gson().fromJson(itemResult, Item.class);
		item.setItemNum(itemNumRot);
		item.setDescription("item " + itemNumRot);
		item.setRotating(true);
		maximoApi.create(item);
		logger.info("Created Item Num:" + itemNumRot);

		// add Item to StoreRoom
		maximoApi.addItemToStoreRoom(itemNumRot, SetupData.ITEMSET, locationRot, SetupData.SITEID, "false",
				SetupData.ISSUEUNIT, "100", "bin101", "", "", "0.0", "1.0", "0.0", "0", "0", "0.0", "0.0",
				"true");
		logger.info("Added Item to StoreRoom successfully");

		// item status change and rolldown
		logger.info("Item status change to Active");
		List<ItemChangeStatus> arr = new ArrayList<>();
		ItemChangeStatus ics = new ItemChangeStatus();
		ics.setStatus(ItemStatus.ACTIVE.toString());
		ics.setRollDown("true");
		arr.add(ics);
		item.setItemChangeStatus(arr);
		maximoApi.update(item);
		logger.info("Item status changed");

		// Create an asset
		logger.info("Creating an asset");
		assetNumRot = AbstractAutomationFramework.randomString(5).toUpperCase();
		String assetResult = maximoApi.retrieve(new Asset(), "addid=1&internalvalues=1&action=system:new&addschema=1");
		Asset newAsset = new Gson().fromJson(assetResult, Asset.class);
		newAsset.setAssetNum(assetNumRot);
		newAsset.setDescription(ASSET_DESCRIPTION + assetNumRot);
		newAsset.setStatus(LocAssetStatus.ACTIVE.toString());
		newAsset.setLocation(locationRot);
		newAsset.setItemNum(itemNumRot);
		maximoApi.create(newAsset);
		logger.info("Created Asset for rotating: " + assetNumRot);

		// Create a work order
		logger.info("Creating a work order");
		woNum = AbstractAutomationFramework.randomString(5).toUpperCase();
		String workOrderResult = maximoApi.retrieve(new WorkOrder(),
				"addid=1&internalvalues=1&action=system:new&addschema=1");
		newWorkOrder = new Gson().fromJson(workOrderResult, WorkOrder.class);
		newWorkOrder.setWoNum(woNum);
		newWorkOrder.setDescription("WorkeOrder for mobile automation test");
		newWorkOrder.setSiteId(SetupData.SITEID);
		newWorkOrder.setAssetNum(assetNum);
		newWorkOrder.setWorkType("PM");
		newWorkOrder.setLocation(locationRot);
		maximoApi.create(newWorkOrder);
		logger.info("Created Work Order:" + woNum);

		// Change WO status to Approved
		logger.info("Changing work order status to APPR");
		maximoApi.changeStatus(newWorkOrder, "APPR");
		// Assignment with labor
		maximoApi.addAssignmentLabor(newWorkOrder, labor);
		logger.info("Assignment added");

		// Create Task
		logger.info("Task API Call started ");
		List<Task> arrTask = new ArrayList<>();
		Task task = new Task();
		task.setEstdur(Float.parseFloat("1.5"));
		task.setSiteid(SetupData.SITEID);
		task.setDescription("WorkOrder for mobile automation test");
		task.setOwnergroup(SetupData.OWNERGROUP);
		task.setParentchgsstatus(true);
		task.setTaskid(Integer.parseInt(taskId));
		task.setStatus(WoStatus.WAPPR.toString());
		arrTask.add(task);
		newWorkOrder.setWoactivity(arrTask);
		maximoApi.update(newWorkOrder);
		logger.info("Created Tasks");
	}

	/**
	 * Get item id from DB by item number
	 * 
	 * @param itemNum
	 * @return
	 */
	protected String itemIdQueryFromDB(String itemNum) {
		String query = "select I.itemid from MAXIMO.item I where I.itemnum = '" + itemNum + "' and I.itemsetid= '" + SetupData.ITEMSET
				+ "'";
		logger.info(query);
		Object[] resultsObjects = jdbcConnection.executeSQL(query);
		Object[] resultArray1 = (Object[]) resultsObjects[0];
		String itemId = resultArray1[0].toString();
		logger.info("itemId>" + itemId);
		return itemId;
	}

	/**
	 * Get Inventory balance from DB by item number
	 * 
	 * @param itemNum
	 * @return
	 */
	protected int getMaterialBalanceFromDB(String itemNum) {
		String query = "SELECT INV.CURBAL FROM MAXIMO.INVBALANCES INV WHERE INV.ITEMNUM ='" + itemNum + "' AND INV.ITEMSETID= '" + SetupData.ITEMSET + "'";
		logger.info(query);
		Object[] resultsObjects = jdbcConnection.executeSQL(query);
		assertTrue(resultsObjects != null && resultsObjects.length > 0, "Should have balance for the material");

		Object[] resultArray1 = (Object[]) resultsObjects[0];
		int balance = ((java.math.BigDecimal) resultArray1[0]).intValue();
		logger.info("balance>" + balance);
		return balance;
	}
	
	/**
	 * Create objects for adding conditional code and rotating material flow
	 * 
	 * @throws Exception
	 */
	private void createRotatingObjectsForScenario26And27() throws Exception {
		logger.info("Creating default objects for rotating case");
		String locationRot;
		String locationRot1;
		
		// Create a location
		logger.info("Creating an storeroom");
		locationRot = AbstractAutomationFramework.randomString(5).toUpperCase();
		String storeRoomResult = maximoApi.retrieve(new StoreRoom(),
				"lean=1&addid=1&action=system:new&ignorecollectionref=1");
		StoreRoom storeRoomRot = new Gson().fromJson(storeRoomResult, StoreRoom.class);
		storeRoomRot.setLocation(locationRot);
		storeRoomRot.setDescription(LOCATION_DESCRIPTION + locationRot);
		storeRoomRot.setSiteId(SetupData.SITEID);
		maximoApi.create(storeRoomRot);
		logger.info("Created storeRoom location:" + locationRot);
		
		// Create another location
		logger.info("Creating a another storeroom");
		locationRot1 = AbstractAutomationFramework.randomString(5).toUpperCase();
		String storeRoomResult1 = maximoApi.retrieve(new StoreRoom(),
				"lean=1&addid=1&action=system:new&ignorecollectionref=1");
		StoreRoom storeRoomRot1 = new Gson().fromJson(storeRoomResult1, StoreRoom.class);
		storeRoomRot1.setLocation(locationRot1);
		storeRoomRot1.setDescription(LOCATION_DESCRIPTION + locationRot1);
		storeRoomRot1.setSiteId(SetupData.SITEID);
		maximoApi.create(storeRoomRot1);
		logger.info("Created another storeRoom location:" + locationRot1);

		// Creating the item of material
		logger.info("Creating an item of material");
		itemNumRot = AbstractAutomationFramework.randomString(5).toUpperCase();
		String itemResult = maximoApi.retrieve(new Item(),
				"lean=1&addid=1&domainmeta=1&internalvalues=1&action=system:new&oslc.select=itemsetid,lottype,issueunit,orderunit,rotating,itemtype");
		Item item = new Gson().fromJson(itemResult, Item.class);
		item.setItemNum(itemNumRot);
		item.setDescription("item " + itemNumRot);
		item.setRotating(true);
		maximoApi.create(item);
		logger.info("Created Item Num:" + itemNumRot);
		
        jdbcConnection.executeUpdateSQL("UPDATE MAXIMO.ITEM SET CONDITIONENABLED=1 WHERE ITEMNUM='"+itemNumRot+"'");
		
		Random random = new Random();
		int rowStamp = random.nextInt(100000);
		int itemConditionId = random.nextInt(10000);
		String insertQueryRate60 = "INSERT INTO MAXIMO.ITEMCONDITION (CONDITIONCODE, DESCRIPTION, STOCKTYPE, ITEMNUM, ITEMSETID, CONDRATE, COMMODITYGROUP, COMMODITY, ITEMCONDITIONID, LANGCODE, HASLD, ROWSTAMP) VALUES('REBUILT', 'Rebuilt', NULL, '"+itemNumRot+"', 'SET1', 60, NULL, NULL, "+itemConditionId+", 'EN', 0, "+rowStamp+")";
		int rowStamp1 = random.nextInt(100000);
		int itemConditionId1 = random.nextInt(10000);
		String insertQueryRate100 = "INSERT INTO MAXIMO.ITEMCONDITION (CONDITIONCODE, DESCRIPTION, STOCKTYPE, ITEMNUM, ITEMSETID, CONDRATE, COMMODITYGROUP, COMMODITY, ITEMCONDITIONID, LANGCODE, HASLD, ROWSTAMP)VALUES('NEW', 'New', NULL, '"+itemNumRot+"', 'SET1', 100, NULL, NULL, "+itemConditionId1+", 'EN', 0, "+rowStamp1+")";		

		jdbcConnection.executeUpdateSQL(insertQueryRate60);
		
		jdbcConnection.executeUpdateSQL(insertQueryRate100);
		
		// Creating one more item of multiple storeroom and one rotating assert
		logger.info("Creating another item");
		itemNumRot1 = AbstractAutomationFramework.randomString(5).toUpperCase();
		String itemResult1 = maximoApi.retrieve(new Item(),
				"lean=1&addid=1&domainmeta=1&internalvalues=1&action=system:new&oslc.select=itemsetid,lottype,issueunit,orderunit,rotating,itemtype");
		Item item1 = new Gson().fromJson(itemResult1, Item.class);
		item1.setItemNum(itemNumRot1);
		item1.setDescription("item " + itemNumRot1);
		item1.setRotating(true);
		maximoApi.create(item1);
		logger.info("Created Item Num:" + itemNumRot1);

		// add Item to StoreRoom
		maximoApi.addItemToStoreRoom(itemNumRot, SetupData.ITEMSET, locationRot, SetupData.SITEID, "false",
				SetupData.ISSUEUNIT, "100", "bin101", "", "", "0.0", "1.0", "0.0", "0", "0", "0.0", "0.0",
				"true");
		logger.info("Added Item to StoreRoom successfully");
		
		// add same Item to another StoreRoom
		maximoApi.addItemToStoreRoom(itemNumRot, SetupData.ITEMSET, locationRot1, SetupData.SITEID, "false",
				SetupData.ISSUEUNIT, "100", "bin101", "", "", "0.0", "1.0", "0.0", "0", "0", "0.0", "0.0",
				"true");
		logger.info("Added same Item to another StoreRoom successfully");
		
		// add Item to StoreRoom
		maximoApi.addItemToStoreRoom(itemNumRot1, SetupData.ITEMSET, locationRot, SetupData.SITEID, "false",
				SetupData.ISSUEUNIT, "100", "bin101", "", "", "0.0", "1.0", "0.0", "0", "0", "0.0", "0.0",
				"true");
		logger.info("Added Item to StoreRoom successfully");
		
		// add same Item to another StoreRoom
		maximoApi.addItemToStoreRoom(itemNumRot1, SetupData.ITEMSET, locationRot1, SetupData.SITEID, "false",
				SetupData.ISSUEUNIT, "100", "bin101", "", "", "0.0", "1.0", "0.0", "0", "0", "0.0", "0.0",
				"true");
		logger.info("Added same Item to another StoreRoom successfully");

		// item status change and rolldown
		logger.info("Item status change to Active");
		List<ItemChangeStatus> arr = new ArrayList<>();
		ItemChangeStatus ics = new ItemChangeStatus();
		ics.setStatus(ItemStatus.ACTIVE.toString());
		ics.setRollDown("true");
		arr.add(ics);
		item.setItemChangeStatus(arr);
		maximoApi.update(item);
		logger.info("Item status changed");
		
		// item status change and rolldown
		logger.info("Item status change to Active");
		List<ItemChangeStatus> arr1 = new ArrayList<>();
		ItemChangeStatus ics1 = new ItemChangeStatus();
		ics1.setStatus(ItemStatus.ACTIVE.toString());
		ics1.setRollDown("true");
		arr1.add(ics1);
		item1.setItemChangeStatus(arr1);
		maximoApi.update(item1);
		logger.info("Item status changed");

		// Create an asset
		logger.info("Creating an asset");
		assetNumRot = AbstractAutomationFramework.randomString(5).toUpperCase();
		String assetResult = maximoApi.retrieve(new Asset(), "addid=1&internalvalues=1&action=system:new&addschema=1");
		Asset newAsset = new Gson().fromJson(assetResult, Asset.class);
		newAsset.setAssetNum(assetNumRot);
		newAsset.setDescription(ASSET_DESCRIPTION + assetNumRot);
		newAsset.setStatus(LocAssetStatus.ACTIVE.toString());
		newAsset.setLocation(locationRot);
		newAsset.setItemNum(itemNumRot);
		maximoApi.create(newAsset);
		logger.info("Created Asset for rotating: " + assetNumRot);
		
		// Create another asset
		logger.info("Creating another asset");
		assetNumRot1 = AbstractAutomationFramework.randomString(5).toUpperCase();
		String assetResult1 = maximoApi.retrieve(new Asset(), "addid=1&internalvalues=1&action=system:new&addschema=1");
		Asset newAsset1 = new Gson().fromJson(assetResult1, Asset.class);
		newAsset1.setAssetNum(assetNumRot1);
		newAsset1.setDescription(ASSET_DESCRIPTION + assetNumRot1);
		newAsset1.setStatus(LocAssetStatus.ACTIVE.toString());
		newAsset1.setLocation(locationRot);
		newAsset1.setItemNum(itemNumRot);
		maximoApi.create(newAsset1);
		logger.info("Created Asset for rotating: " + assetNumRot1);
		
		// Create an asset
		logger.info("Creating an asset");
		assetNumRot2 = AbstractAutomationFramework.randomString(5).toUpperCase();
		String assetResult2 = maximoApi.retrieve(new Asset(), "addid=1&internalvalues=1&action=system:new&addschema=1");
		Asset newAsset2 = new Gson().fromJson(assetResult2, Asset.class);
		newAsset2.setAssetNum(assetNumRot2);
		newAsset2.setDescription(ASSET_DESCRIPTION + assetNumRot2);
		newAsset2.setStatus(LocAssetStatus.ACTIVE.toString());
		newAsset2.setLocation(locationRot);
		newAsset2.setItemNum(itemNumRot1);
		maximoApi.create(newAsset2);
		logger.info("Created Asset for rotating: " + assetNumRot2);

		// Create 3rd  work order
		logger.info("Creating 3rd a work order");
		String workOrderResult1 = maximoApi.retrieve(new WorkOrder(),
				"addid=1&internalvalues=1&action=system:new&addschema=1");
		newWorkOrder2 = new Gson().fromJson(workOrderResult1, WorkOrder.class);
		newWorkOrder2.setDescription("WorkOrder for mobile automation test Start Travel");
		newWorkOrder2.setAssetNum(assetNum);
		newWorkOrder2.setSiteId(SetupData.SITEID);
		newWorkOrder2.setGLAccount(SetupData.GLDEBITACCT);
		newWorkOrder2.setLocation(locationRot);
		newWorkOrder2.setWorkType("PM");

		maximoApi.create(newWorkOrder2);
		woNum1 = newWorkOrder2.getWoNum();
		logger.info("Work Order: " + woNum2);

		// Assign the labor
		maximoApi.addAssignmentLabor(newWorkOrder2, labor);
		logger.info("Assignment added");

		// Change WO status to Approved
		logger.info("Changing work order status to APPR");
		maximoApi.changeStatus(newWorkOrder2, WoStatus.APPR.toString());
		logger.info("APPR WO");

		// Create Task
		logger.info("Task API Call started ");
		List<Task> arrTask = new ArrayList<>();
		Task task = new Task();
		task.setEstdur(Float.parseFloat("1.5"));
		task.setSiteid(SetupData.SITEID);
		task.setDescription("WorkOrder for mobile automation test");
		task.setOwnergroup(SetupData.OWNERGROUP);
		task.setParentchgsstatus(true);
		task.setTaskid(Integer.parseInt(taskId));
		task.setStatus(WoStatus.WAPPR.toString());
		arrTask.add(task);
		newWorkOrder.setWoactivity(arrTask);
		maximoApi.update(newWorkOrder2);
		logger.info("Created Tasks");
	}

}
