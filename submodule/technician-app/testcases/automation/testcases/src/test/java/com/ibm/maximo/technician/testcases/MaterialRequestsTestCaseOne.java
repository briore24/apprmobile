package com.ibm.maximo.technician.testcases;

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
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.google.gson.Gson;
import com.ibm.maximo.automation.mobile.AbstractAutomationFramework;
import com.ibm.maximo.automation.mobile.FrameworkFactory;
import com.ibm.maximo.automation.mobile.api.MaximoApi;
import com.ibm.maximo.automation.mobile.api.json.Asset;
import com.ibm.maximo.automation.mobile.api.json.Item;
import com.ibm.maximo.automation.mobile.api.json.ItemChangeStatus;

import com.ibm.maximo.automation.mobile.api.json.StoreRoom;
import com.ibm.maximo.automation.mobile.api.json.WorkOrder;
import com.ibm.maximo.components.DataListComponent;
import com.ibm.maximo.components.DataListItemComponent;
import com.ibm.maximo.technician.framework.TechnicianTest;
import com.ibm.maximo.technician.page.AddMaterialPage;
import com.ibm.maximo.technician.page.MySchedulePage;
import com.ibm.maximo.technician.page.WorkOrderDetailsPage;
import com.ibm.maximo.technician.setupdata.SetupData;
import com.ibm.maximo.technician.setupdata.SetupData.WoStatus;
import com.ibm.maximo.technician.setupdata.SetupData.ItemStatus;

/*GRAPHITE-70311:- [TECHMOBILE] Planned Materials Tools Request Materials*/
public class MaterialRequestsTestCaseOne extends TechnicianTest {
	private final Logger logger = LoggerFactory.getLogger(MaterialRequestsTestCaseOne.class);
	private AbstractAutomationFramework af;
	private TestSuite testSuite;
	private MaximoApi maximoApi;
	private WorkOrder newWorkOrder;
	private String woNum, labor, location, location1;
	private String itemNum, itemId;
	private int year = 2030, month = 11, date = 10;
	private String locationDesc;
	private String locationDescOne;
	private String itemDesc;

	@BeforeClass(alwaysRun = true)
	@Parameters({ "configPath" })
	public void setup(String configPath) throws Exception {
		logger.info("********************MaterialRequestsTestCaseOne*********************************");
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
			itemIdQueryFromDB();

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

	/*
	 * Scenario 17 - Verify storeroom lookup is displayed to select storeroom if
	 * multiple storerooms are associated with selected item
	 * 
	 * Scenario 19 - Verify save/checkmark button is enabled only when all the
	 * mandatory fields are provided on "Add item" sliding drawer
	 * 
	 * Scenario 23 - Verify user can add multiple material request for same material
	 * from the material request section by clicking on "+" button
	 * 
	 * Scenario 25 - Verify various fields of MR and MR Line record when technician
	 * saves/sends the Material request successfully
	 */
	@Test(groups = {
			"priority3" }, description = "Verify save/checkmark button is enabled only when all the mandatory fields are provided on \"Add item\" sliding drawer", timeOut = 1500000)

	public void materialRequestTestFromWorkOrderDetailsPage() throws Exception {
		// Open work order details
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage workOrderDetailsPage = new WorkOrderDetailsPage(af);
		AddMaterialPage addMaterialPage = new AddMaterialPage(af);
		// Search the WO
		assertTrue(assignedWorkPage.search(woNum), "Fail : Search Work Order is not displayed");
		assignedWorkPage.openWorkOrderDetails();
		// click on planned materials and tools touchpoint
		workOrderDetailsPage.clickMaterialAndToolTouchpoint();
		// click on materials and tools menu
		workOrderDetailsPage.clickMaterialAndToolMenu();
		logger.info("Click Material and Tool Menu");
		// click on shopping cart button
		workOrderDetailsPage.clickShoppingCart();
		logger.info("Click Shopping cart");
		// verify material request page opens
		assertTrue(workOrderDetailsPage.addMaterialsDisplayed(), "Fail : Material request is not displayed");

		searchAndSeclectItem(workOrderDetailsPage, addMaterialPage);

		assertEquals("Storeroom", addMaterialPage.verifyStoreroomPageTitle());
		logger.info("header label " + addMaterialPage.verifyStoreroomPageTitle());
		// Click First Storeroom from list
		addMaterialPage.clickToSelectFirstElementInStoreRoom();
		assertEquals("Item", addMaterialPage.verifyItemValue());
		logger.info("Item Value is verified");
		assertEquals("Quantity", addMaterialPage.getQuantityLabel());
		logger.info("Quantity Label verified");
		assertEquals(1, Integer.parseInt(addMaterialPage.getQuantityValue()));
		logger.info("Quantity Value verified");

		// save Add Item
		workOrderDetailsPage.clickSaveAddItem();
		searchAndSeclectItem(workOrderDetailsPage, addMaterialPage);
		// Click First Storeroom from list
		addMaterialPage.clickToSelectFirstElementInStoreRoom();
		// save Add Item
		workOrderDetailsPage.clickSaveAddItem();

		logger.info("Verify save button is enabled and clicked");
		// Enter Required date under Required details of Material request
		workOrderDetailsPage.requiredDateUnderRequiredDetailsOfMaterialRequest(year, month, date);
		// Priorty
		assertEquals(workOrderDetailsPage.getPriorityMaterialRequestValue(), "1", "Fail : Priority is not set as 1");
		// Drop to
		workOrderDetailsPage.dropToMaterialRequest("Delhi,India");

		// Send Material Request
		workOrderDetailsPage.sendMaterialRequest();
		
		// Scenario 23,25 start

		
		 workOrderDetailsPage.firstChevronOfRequestedMaterialStatus();
		 logger.info("First chevron clicked");
		  
		 addMaterialPage.waitDataRepresnt(); logger.info("Wait done");
		 
		 // Verify length of material list
		 
		 DataListComponent list = af.instantiateComponent(DataListComponent.class,"yxbyd_items_datalistWrapper");
		 
		 List<DataListItemComponent> items = list.getChildren();
		 
		 logger.info("Items Length:-> " + items); 
		 assertTrue(items.size() > 1,"More then one Request are available on Material list");
		 
		 workOrderDetailsPage.clickAddItemOpenDrawerButton();
		 
		 assertEquals("Quantity", addMaterialPage.getQuantityLabel());
		 logger.info("Quantity Label verified");
		 
		 assertEquals(1, Integer.parseInt(addMaterialPage.getQuantityValue()));
		 logger.info("Quantity Value verified");
		 
		 assertEquals("Order unit", addMaterialPage.getOrderUnitLabel());
		 logger.info("Order Unit Label verified");
		 
		 assertEquals("EACH", addMaterialPage.getOrderUnitValue());
		 logger.info("Order Unit Value verified");
		 
		 workOrderDetailsPage.clickAddItemCloseDrawerButton();
		 
		 assertEquals("1", workOrderDetailsPage.getPriorityMaterialRequestValue());
		 logger.info("Priority Value verified");
		 
		 // Uncomment once issue is resolved - https://jsw.ibm.com/browse/MAXMOA-1091
		 //assertEquals("Delhi,India", workOrderDetailsPage.getDropToMaterialRequest());
		// logger.info("WorkOrder Drop Material Request is verified");
		  
		 workOrderDetailsPage.getRequiredDateUnderRequiredDetailsOfMaterialRequest(
		 year, month, date); logger.info("Required Date value is verified");
		 
		 addMaterialPage.backFromMaterials();
		 
		 workOrderDetailsPage.verifySaveAndDiscardDialog();
		 
		 workOrderDetailsPage.clickDiscardButton();
		 
		 String mrnum = workOrderDetailsPage.clickRequestedMaterialListChevron(woNum,itemNum, location);
				 
		workOrderDetailsPage.verifyItemOnMaterialList(woNum, mrnum, itemNum,itemDesc, location, locationDesc, "1");
				 
		logger.info("check assert", workOrderDetailsPage.verifyItemOnMaterialList(woNum, mrnum, itemNum,itemDesc, location, locationDesc, "1"));
				 
		addMaterialPage.backFromMaterials();
				 
		workOrderDetailsPage.verifySaveAndDiscardDialog();
				 
		workOrderDetailsPage.clickDiscardButton();
		 
		addMaterialPage.waitSlidingDrawaerReapper();
		 
		// Scenario 23,25 end

		workOrderDetailsPage.clickSlidingDrawerCloseButton();

		workOrderDetailsPage.clickBackChevron();
		logger.info("Back to workorder list page");

	}

	public void searchAndSeclectItem(WorkOrderDetailsPage workOrderDetailsPage, AddMaterialPage addMaterialPage)
			throws Exception {

		workOrderDetailsPage.clickAddMaterialIcon();
		logger.info("Click Material Icon");
		// click on Item chevron
		workOrderDetailsPage.clickItemChevron();
		logger.info("Click Item Chevron");
		// search itemNum
		workOrderDetailsPage.searchForItemAndSelectItem(itemNum, itemId);
		logger.info("Search for Item and Select Item");

	}

	protected void createDefaultObjects() throws Exception {
		logger.info("Creating default objects");

		// Create an asset
		logger.info("Creating an asset");
		assetNum = AbstractAutomationFramework.randomString(5).toUpperCase();
		String assetResult = maximoApi.retrieve(new Asset(), "addid=1&internalvalues=1&action=system:new&addschema=1");
		Asset newAsset = new Gson().fromJson(assetResult, Asset.class);
		newAsset.setAssetNum(assetNum);
		newAsset.setDescription("Asset for mobile automation test");
		maximoApi.create(newAsset);
		logger.info("Asset: " + assetNum);

		// Create a work order
		logger.info("Creating a work order");
		String workOrderResult = maximoApi.retrieve(new WorkOrder(),
				"addid=1&internalvalues=1&action=system:new&addschema=1");
		newWorkOrder = new Gson().fromJson(workOrderResult, WorkOrder.class);
		newWorkOrder.setDescription("WorkOrder for mobile automation test");
		newWorkOrder.setAssetNum(assetNum);

		maximoApi.create(newWorkOrder);
		woNum = newWorkOrder.getWoNum();
		logger.info("Work Order: " + woNum);

		// Assign the labor
		maximoApi.addAssignmentLabor(newWorkOrder, labor);
		logger.info("Assignment added");

		// Creating storeroom
		logger.info("Creating an storeroom");
		location = AbstractAutomationFramework.randomString(5).toUpperCase();
		String storeRoomResult = maximoApi.retrieve(new StoreRoom(),
				"lean=1&addid=1&action=system:new&ignorecollectionref=1");
		StoreRoom storeRoom = new Gson().fromJson(storeRoomResult, StoreRoom.class);
		locationDesc = "StoreRoom " + location;
		storeRoom.setDescription(locationDesc);
		storeRoom.setLocation(location);

		maximoApi.create(storeRoom);
		logger.info("storeRoom location::" + location);

		// Creating storeroom
		logger.info("Creating an storeroom");
		location1 = AbstractAutomationFramework.randomString(5).toUpperCase();
		String storeRoomResult1 = maximoApi.retrieve(new StoreRoom(),
				"lean=1&addid=1&action=system:new&ignorecollectionref=1");
		StoreRoom storeRoom1 = new Gson().fromJson(storeRoomResult1, StoreRoom.class);
		locationDescOne = "StoreRoom " + location1;
		storeRoom1.setDescription(locationDescOne);
		storeRoom1.setLocation(location1);

		maximoApi.create(storeRoom1);
		logger.info("storeRoom location::" + location1);

		// Creating the item
		logger.info("Creating an item");
		itemNum = AbstractAutomationFramework.randomString(5).toUpperCase();
		String itemResult = maximoApi.retrieve(new Item(),
				"lean=1&addid=1&domainmeta=1&internalvalues=1&action=system:new&oslc.select=itemsetid,lottype,issueunit,orderunit,rotating,itemtype");
		Item item = new Gson().fromJson(itemResult, Item.class);
		item.setItemNum(itemNum);
		itemDesc = "item " + itemNum;
		item.setDescription(itemDesc);
		maximoApi.create(item);
		logger.info("ItemNum::" + itemNum);

		// add Item to StoreRoom
		maximoApi.addItemToStoreRoom(itemNum, SetupData.ITEMSET, location, SetupData.SITEID, "false",
				SetupData.ISSUEUNIT, "100", SetupData.DFLTBIN, "", "", "0.0", "1.0", "0.0", "0", "0", "0.0", "0.0",
				"true");
		logger.info("added Item to StoreRoom successfully");

		// add Item to 2nd StoreRoom
		maximoApi.addItemToStoreRoom(itemNum, SetupData.ITEMSET, location1, SetupData.SITEID, "false",
				SetupData.ISSUEUNIT, "100", SetupData.DFLTBIN, "", "", "0.0", "1.0", "0.0", "0", "0", "0.0", "0.0",
				"true");
		logger.info("added Item to StoreRoom successfully");

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

		// Change WO status to Approved
		logger.info("Changing work order status to APPR");
		maximoApi.changeStatus(newWorkOrder, WoStatus.APPR.toString());
		logger.info("APPR WO");

	}

	protected String itemIdQueryFromDB() {
		logger.info("select itemid from MAXIMO.ITEM where itemnum = '" + itemNum + "' and itemsetid= '"
				+ SetupData.ITEMSET + "'");
		Object[] resultsObjects = jdbcConnection.executeSQL("select itemid from MAXIMO.ITEM where itemnum = '" + itemNum
				+ "' and itemsetid= '" + SetupData.ITEMSET + "'");
		Object[] resultArray1 = (Object[]) resultsObjects[0];
		itemId = resultArray1[0].toString();
		logger.info("itemId>" + itemId);
		return itemId;
	}

}
