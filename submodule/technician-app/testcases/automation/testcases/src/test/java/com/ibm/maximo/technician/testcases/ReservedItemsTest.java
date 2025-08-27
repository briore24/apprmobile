package com.ibm.maximo.technician.testcases;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertFalse;
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
import com.ibm.maximo.automation.mobile.api.json.AddItemToStoreroom;
import com.ibm.maximo.automation.mobile.api.json.Item;
import com.ibm.maximo.automation.mobile.api.json.ItemChangeStatus;
import com.ibm.maximo.automation.mobile.api.json.StoreRoom;
import com.ibm.maximo.automation.mobile.api.json.WorkOrder;
import com.ibm.maximo.automation.mobile.api.json.WpMaterial;
import com.ibm.maximo.automation.mobile.page.login.LoginPage;
import com.ibm.maximo.technician.framework.TechnicianTest;
import com.ibm.maximo.technician.page.MaterialsAndToolsPage;
import com.ibm.maximo.technician.page.MySchedulePage;
import com.ibm.maximo.technician.page.ReportWorkPage;
import com.ibm.maximo.technician.page.WorkOrderDetailsPage;
import com.ibm.maximo.technician.setupdata.SetupData;
import com.ibm.maximo.technician.setupdata.SetupData.ItemStatus;
import com.ibm.maximo.technician.setupdata.SetupData.WoStatus;
import com.ibm.maximo.technician.setupdata.SetupData.WorkType;

/*GRAPHITE-51074:[TECHMOBILE] Get Reserved Items and report them as Actuals :14M,3TA,0A*/
public class ReservedItemsTest extends TechnicianTest {
	private final Logger logger = LoggerFactory.getLogger(ReservedItemsTest.class);
	private AbstractAutomationFramework af;
	private TestSuite testSuite;
	private MaximoApi maximoApi;
	private WorkOrder newWorkOrder, newWorkOrder1, newWorkOrder2;
	private String woNum, woNum1, header,labor, labor1, location, location1, woNum2;
	private String inventoryCount1, inventoryCount2;
	private String itemNum, itemNum1;
	public static LoginPage lp;

	@BeforeClass(alwaysRun = true)
	@Parameters({ "configPath" })
	public void setup(String configPath) throws Exception {
		this.af = FrameworkFactory.get();
		Properties properties = new Properties();
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(configPath));
			properties.load(in);
			labor = properties.getProperty("system.username");
			labor1 = properties.getProperty("app.username1");
			maximoApi = new MaximoApi();
			maximoApi.setMaximoServer(properties.getProperty("system.maximoServerUrl"),
					properties.getProperty("system.maximoAPIKey"));
			createDefaultObjects();
			defaultInformationOfDefaultSite(SetupData.SITEID,labor1);
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
		maximoApi.changeStatus(newWorkOrder1, WoStatus.COMP.toString());
		maximoApi.changeStatus(newWorkOrder2, WoStatus.COMP.toString());
		if (testSuite != null) {
			testSuite.teardown();
		}
	}

	@Test(groups = {
			"mobile" }, description = "Verify all selected reserved items are reported as actuals in \"Materials used\" section on \"Report work\" page when technician clicks on \"Get selected\"", timeOut = 900000)
	public void GetReservedTestFromMaterialsAndToolsPage() throws Exception {
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage woDetails = new WorkOrderDetailsPage(af);
		MaterialsAndToolsPage materialsAndTools = new MaterialsAndToolsPage(af);
		assertTrue(assignedWorkPage.search(woNum), "Fail : Search Work Order is not displayed");
		// Navigate to work order details page
		assignedWorkPage.openCardDetails();
		// Wait for start work button enabled
		woDetails.startWorkButtonEnabled();
		// click on planned materials and tools touchpoint
		woDetails.clickMaterialAndToolTouchpoint();
		// Click on the 3 dots
		woDetails.clickMaterialAndToolMenu();
		// Click on Get Reserved items
		materialsAndTools.clickGetReservedItems();
		// Select the item by clicking on the checkbox
		materialsAndTools.checkboxClickOnGetReservedItems();
		// click on the get Selected button on top after checkbox selection
		materialsAndTools.clickGetSelected();
		// verify the get reserved items added
		assertEquals(materialsAndTools.verifyGetReservedItemsAddedInMaterials(), itemNum + " " + "item" + " " + itemNum,
				"Fail");
		// Logout
		logOut(af);
		// Relogin to enter different credentials for next test
		reLoginWithDifferentCredentials();
	}

	@Test( dependsOnMethods = { "GetReservedTestFromMaterialsAndToolsPage" }, groups = { "mobile" }, description = "Verify that \"Get reserved items >\" button under 3 dot menu in Materials used section on Report work page is either disabled or hidden when technician do not have permission for reporting reserved items as actuals", timeOut = 300000)
	public void newLogin() throws Exception {
		// create a work order
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage workOrderDetailsPage = new WorkOrderDetailsPage(af);
		ReportWorkPage reportWorkPage = new ReportWorkPage(af);
		// Search the WO
		assertTrue(assignedWorkPage.search(woNum1), "Fail : Search Work Order is not displayed");
		// Navigate to work order details page
		assignedWorkPage.openCardDetails();
		// Wait for start work button enabled
		workOrderDetailsPage.startWorkButtonEnabled();
		// Click on Report Page touch point
		workOrderDetailsPage.clickReportWorkButton();
		// Click on 3 dots at Material used section
		reportWorkPage.clickOn3Dotsbutton();
		// Verify that the get Reserved items it Disabled
		assertFalse(reportWorkPage.verifyGetReservedItemsDisabled(),"Get ReservedItems is enabled");
	}

	@Test( dependsOnMethods = { "newLogin" }, groups = { "mobile" }, description = "Verify that \"Get reserved items >\" button under 3 dot menu in planned materials and tools drawer on WO details page is either disabled or hidden when technician do not have permission for reporting reserved items as actuals", timeOut = 300000)
	public void newLogin2() throws Exception {
		ReportWorkPage reportWorkPage = new ReportWorkPage(af);
		WorkOrderDetailsPage woDetails = new WorkOrderDetailsPage(af);
		MaterialsAndToolsPage materialsAndTools = new MaterialsAndToolsPage(af);
		// Go back to the work order detail page
		reportWorkPage.clickBackChevron();
		// open the materials and tools touchpoint
		woDetails.clickMaterialAndToolTouchpoint();
		// Click on the 3 dots
		woDetails.clickMaterialAndToolMenu();
		// Verify that the get Reserved items it Disabled
		assertFalse(materialsAndTools.verifyGetReservedItemsDisabled(),"Get ReservedItems is enabled");
	}

	@Test(groups = {
			"priority2" }, description = "Verify all selected reserved items when reported as actuals then inventory count should get updated", timeOut = 920000)
	public void GetReservedInventoryCountTest() throws Exception {
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage woDetails = new WorkOrderDetailsPage(af);
		MaterialsAndToolsPage materialsAndTools = new MaterialsAndToolsPage(af);
		ReportWorkPage reportWorkPage = new ReportWorkPage(af);
		assertTrue(assignedWorkPage.search(woNum2), "Fail : Search Work Order is not displayed");
		// Navigate to work order details page
		assignedWorkPage.openCardDetails();
		// Wait for start work button enabled
		woDetails.startWorkButtonEnabled();
		// click on planned materials and tools touchpoint
		woDetails.clickMaterialAndToolTouchpoint();
		// Click on the 3 dots
		woDetails.clickMaterialAndToolMenu();
		// Click on Get Reserved items
		materialsAndTools.clickGetReservedItems();

	    String inventoryQuery1 = "SELECT CURBAL FROM MAXIMO.INVBALANCES WHERE ITEMNUM='"+ itemNum1 + "'";
		Object[] woResult1 = jdbcConnection.executeSQL(inventoryQuery1);
		Object[] woResultArray1 = (Object[]) woResult1[0];
		inventoryCount1 = woResultArray1[0].toString();
		logger.info("Initial Count" + " " + inventoryCount1);
		// Select the item by clicking on the checkbox
		materialsAndTools.checkboxClickOnGetReservedItems();
		// click on the get Selected button on top after checkbox selection
		materialsAndTools.clickGetSelected();
		reportWorkPage.clickBackChevron();
		// Wait for start work button enabled
		woDetails.startWorkButtonEnabled();
		// click on planned materials and tools touchpoint
		woDetails.clickMaterialAndToolTouchpoint();
		// Click on the 3 dots
		woDetails.clickMaterialAndToolMenu();
		// Click on Get Reserved items
		materialsAndTools.clickGetReservedItems();

		String inventoryQuery = "SELECT CURBAL FROM MAXIMO.INVBALANCES WHERE ITEMNUM='"+ itemNum1 + "'";
		Object[] woResult = jdbcConnection.executeSQL(inventoryQuery);
		Object[] woResultArray = (Object[]) woResult[0];
		inventoryCount2 = woResultArray[0].toString();
		logger.info("Count after Get Selected" + " " + inventoryCount2);

		assertEquals((int) Double.parseDouble(inventoryCount1) - 1, (int) Double.parseDouble(inventoryCount2));
	}

	
	@Test(groups = {
	"priority3" }, description = "Verify save is disabled for items on materials page", timeOut = 920000)
	public void verifyMaterialsTest() throws Exception {
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage woDetails = new WorkOrderDetailsPage(af);
		MaterialsAndToolsPage materialsAndTools = new MaterialsAndToolsPage(af);
		ReportWorkPage reportWorkPage = new ReportWorkPage(af);
		assertTrue(assignedWorkPage.search(woNum2), "Fail : Search Work Order is not displayed");
		// Navigate to work order details page
		assignedWorkPage.openCardDetails();
		// Wait for start work button enabled
		woDetails.startWorkButtonEnabled();
		// click on planned materials and tools touchpoint
		woDetails.clickMaterialAndToolTouchpoint();
		// Click on the 3 dots
		woDetails.clickMaterialAndToolMenu();
		// Click on Get Reserved items
		materialsAndTools.clickGetReservedItems();
		// Select the item by clicking on the checkbox
		materialsAndTools.checkboxClickOnGetReservedItems();
		// click on the get Selected button on top after checkbox selection
		materialsAndTools.clickGetSelected();
		// Verify Report work page header value
		header = materialsAndTools.getTitle(materialsAndTools.itemsPageHeader);
		assertEquals("Items", header);
		logger.info("Items page Header verified");
		assertTrue(materialsAndTools.verifySaveButtonIsDisabled(),"Save button is disabled");
	}
	
	protected void createDefaultObjects() throws Exception {
		logger.info("Creating default objects");
		// Create a work order
		String workOrderResult = maximoApi.retrieve(new WorkOrder(),
				"addid=1&internalvalues=1&action=system:new&addschema=1");
		newWorkOrder = new Gson().fromJson(workOrderResult, WorkOrder.class);
		newWorkOrder.setDescription("Get Reserved Items for Scenario8");
		newWorkOrder.setSiteId(SetupData.SITEID);
		newWorkOrder.setGLAccount(SetupData.GLDEBITACCT);
		// newWorkOrder.setFlowControlled(true);
		maximoApi.create(newWorkOrder);
		woNum = newWorkOrder.getWoNum();
		logger.info("Work Order: " + woNum);
		// Assign the labor
		maximoApi.addAssignmentLabor(newWorkOrder, labor);
		logger.info("Assignment added");

		// Create Second Work Order
		logger.info("Creating second work order");
		String workOrderResult1 = maximoApi.retrieve(new WorkOrder(),
				"addid=1&internalvalues=1&action=system:new&addschema=1");
		newWorkOrder1 = new Gson().fromJson(workOrderResult1, WorkOrder.class);
		newWorkOrder1.setDescription("WorkOrder for mobile automation test");
		newWorkOrder1.setSiteId(SetupData.SITEID);
		newWorkOrder1.setWorkType(WorkType.PM.toString());
		maximoApi.create(newWorkOrder1);
		woNum1 = newWorkOrder1.getWoNum();
		logger.info("Work Order 2: {}" + woNum1);

		// Assign the labor
		maximoApi.addAssignmentLabor(newWorkOrder1, labor1);
		logger.info("Assignment added");

		// Change WO status to Approved
		logger.info("Changing work order status to APPR");
		maximoApi.changeStatus(newWorkOrder1, WoStatus.APPR.toString());

		// Create Third work order
		String workOrderResult2 = maximoApi.retrieve(new WorkOrder(),
				"addid=1&internalvalues=1&action=system:new&addschema=1");
		newWorkOrder2 = new Gson().fromJson(workOrderResult2, WorkOrder.class);
		newWorkOrder2.setDescription("Get Reserved Items for Scenario8");
		newWorkOrder2.setSiteId(SetupData.SITEID);
		newWorkOrder2.setGLAccount(SetupData.GLDEBITACCT);
		maximoApi.create(newWorkOrder2);
		woNum2 = newWorkOrder2.getWoNum();
		logger.info("Work Order 3: " + woNum2);
		// Assign the labor
		maximoApi.addAssignmentLabor(newWorkOrder2, labor);
		logger.info("Assignment added");

		// Creating the item
		logger.info("Creating an item");
		itemNum = AbstractAutomationFramework.randomString(5).toUpperCase();
		String itemResult = maximoApi.retrieve(new Item(),
				"lean=1&addid=1&domainmeta=1&internalvalues=1&action=system:new&oslc.select=itemsetid,lottype,issueunit,orderunit,rotating,itemtype");
		Item item = new Gson().fromJson(itemResult, Item.class);
		item.setItemNum(itemNum);
		item.setDescription("item " + itemNum);
		maximoApi.create(item);
		logger.info("ItemNum::" + itemNum);
		// Creating storeroom
		logger.info("Creating a store room");
		location = AbstractAutomationFramework.randomString(5).toUpperCase();
		String storeRoomResult = maximoApi.retrieve(new StoreRoom(),
				"addid=1&internalvalues=1&action=system:new&oslc.select=itemnum,siteid,description,itemsetid,type,itemtype,status,itemid&addschema=1");
		StoreRoom storeRoom = new Gson().fromJson(storeRoomResult, StoreRoom.class);
		storeRoom.setDescription("StoreRoom " + location);
		storeRoom.setLocation(location);
		maximoApi.create(storeRoom);
		logger.info("storeRoom location::" + location);
		// Add Tool to StoreRoom
		maximoApi.addItemToStoreRoom(itemNum, SetupData.ITEMSET, location, SetupData.SITEID, "false",
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

		// Creating the item
		logger.info("Creating another item");
		itemNum1 = AbstractAutomationFramework.randomString(5).toUpperCase();
		String itemResult1 = maximoApi.retrieve(new Item(),
				"lean=1&addid=1&domainmeta=1&internalvalues=1&action=system:new&oslc.select=itemsetid,lottype,issueunit,orderunit,rotating,itemtype");
		Item item1 = new Gson().fromJson(itemResult1, Item.class);
		item1.setItemNum(itemNum1);
		item1.setDescription("item " + itemNum1);
		item1.setRotating(true);
		maximoApi.create(item1);
		logger.info("ItemNum::" + itemNum1);
		// Creating another storeroom
		logger.info("Creating a store room");
		location1 = AbstractAutomationFramework.randomString(5).toUpperCase();
		String storeRoomResult1 = maximoApi.retrieve(new StoreRoom(),
				"addid=1&internalvalues=1&action=system:new&oslc.select=itemnum,siteid,description,itemsetid,type,itemtype,status,itemid&addschema=1");
		StoreRoom storeRoom1 = new Gson().fromJson(storeRoomResult1, StoreRoom.class);
		storeRoom1.setDescription("StoreRoom " + location1);
		storeRoom1.setLocation(location1);
		maximoApi.create(storeRoom1);
		logger.info("storeRoom location::" + location1);
		
		AddItemToStoreroom addItemToStoreroom1 = new AddItemToStoreroom();

		addItemToStoreroom1.setItemNum(itemNum1);
		addItemToStoreroom1.setItemSetID(SetupData.ITEMSET);
		addItemToStoreroom1.setStoreroom(location1);
		addItemToStoreroom1.setSiteID(SetupData.SITEID);
		addItemToStoreroom1.setTool(false);
		addItemToStoreroom1.setIssueUnit(SetupData.ISSUEUNIT);
		addItemToStoreroom1.setCurBal(100);
		addItemToStoreroom1.setDfltbin(SetupData.DFLTBIN);
		addItemToStoreroom1.setCostType("");
		addItemToStoreroom1.setLotNum("");
		addItemToStoreroom1.setDeliveryTime(0.0);
		addItemToStoreroom1.setOrderQty(2.0);
		addItemToStoreroom1.setMinLevel(0.0);
		addItemToStoreroom1.setReOrder(0);
		addItemToStoreroom1.setCcf(0);
		addItemToStoreroom1.setStdCost(0.0);
		addItemToStoreroom1.setAvgCost(0.0);
		addItemToStoreroom1.setSaveNow(true);

		addItemToStoreroom1.setLocationUrl(maximoApi.getMaximoServer() + "/api/service/location");

		String addAnItemToStoreroom1 = "addAnItemToStoreroom";

		maximoApi.updateByService(addItemToStoreroom1, addAnItemToStoreroom1);

		logger.info("added Item to StoreRoom successfully");
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

		// Adding item to Workorder
		logger.info("Adding wpmaterial");
		WpMaterial wpmaterial = new WpMaterial();
		wpmaterial.setItemNum(item.getItemNum());
		wpmaterial.setLocation(location);
		wpmaterial.setItemQty(1);
		wpmaterial.setRate(1);
		List<WpMaterial> WpMaterialArray = new ArrayList<WpMaterial>();
		WpMaterialArray.add(wpmaterial);
		newWorkOrder.setWpMaterial(WpMaterialArray);
		logger.info("Item updated to WO ");
		maximoApi.update(newWorkOrder);
		// Change WO status to Approved
		logger.info("Changing work order status to APPR");
		maximoApi.changeStatus(newWorkOrder, WoStatus.APPR.toString());
		logger.info("WO Status Changed");

		// Adding item1 to Workorder 3
		logger.info("Adding wpmaterial");
		WpMaterial wpmaterial1 = new WpMaterial();
		wpmaterial1.setItemNum(item1.getItemNum());
		wpmaterial1.setLocation(location1);
		wpmaterial1.setItemQty(1);
		wpmaterial1.setRate(1);
		List<WpMaterial> WpMaterialArray1 = new ArrayList<WpMaterial>();
		WpMaterialArray1.add(wpmaterial1);
		newWorkOrder2.setWpMaterial(WpMaterialArray1);
		logger.info("Item updated to WO ");
		maximoApi.update(newWorkOrder2);
		// Change WO status to Approved
		logger.info("Changing work order status to APPR");
		maximoApi.changeStatus(newWorkOrder2, WoStatus.APPR.toString());
		logger.info("WO Status Changed");
	}
	//UPDATE MAXIMO.MAXUSER SET MAXIMO.MAXUSER.DEFSITE = '' WHERE MAXIMO.MAXUSER.PERSONID = 'WIE8L0R'
	public void defaultInformationOfDefaultSite(String site, String labor) {
		jdbcConnection.executeUpdateSQL("UPDATE MAXIMO.MAXUSER SET MAXIMO.MAXUSER.DEFSITE = '" + site
				+ "' WHERE MAXIMO.MAXUSER.PERSONID = '" + labor + "'");
	}
}