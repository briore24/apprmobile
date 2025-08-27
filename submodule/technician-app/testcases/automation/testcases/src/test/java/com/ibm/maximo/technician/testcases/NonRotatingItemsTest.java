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
import com.ibm.maximo.automation.mobile.api.json.AddItemToStoreroom;
import com.ibm.maximo.automation.mobile.api.json.Asset;
import com.ibm.maximo.automation.mobile.api.json.Item;
import com.ibm.maximo.automation.mobile.api.json.ItemChangeStatus;
import com.ibm.maximo.automation.mobile.api.json.StoreRoom;
import com.ibm.maximo.automation.mobile.api.json.WorkOrder;
import com.ibm.maximo.automation.mobile.api.json.WpMaterial;
import com.ibm.maximo.technician.framework.TechnicianTest;
import com.ibm.maximo.technician.page.MySchedulePage;
import com.ibm.maximo.technician.page.ReportWorkPage;
import com.ibm.maximo.technician.page.WorkOrderDetailsPage;
import com.ibm.maximo.technician.setupdata.SetupData;
import com.ibm.maximo.technician.setupdata.SetupData.ItemStatus;
import com.ibm.maximo.technician.setupdata.SetupData.WoStatus;
import com.ibm.maximo.technician.setupdata.SetupData.WorkType;

/*
 * MAXMOA-6179 :Test Case & Automation update - Reserve Material Page
 * Scenario 12,13 : testcases/WorkorderDetailsPage/TC_GetReservedItemsWODetailsReportWork.md
 * */

public class NonRotatingItemsTest extends TechnicianTest{
	
	private final Logger logger = LoggerFactory.getLogger(NonRotatingItemsTest.class);
	private AbstractAutomationFramework af;
	private TestSuite testSuite;
	private MaximoApi maximoApi;
	private String woNum, labor,nonRotatingHeader,headerItemNo,itemNum,location,woNum2;
	private WorkOrder newWorkOrder,newWorkOrder2;
	private static final String WO_DESCRIPTION = "TEST Automation";
	private boolean apiCodeSuccess = false;
	
	@BeforeClass(alwaysRun = true)
	@Parameters({ "configPath" })
	public void setup(String configPath) throws Exception {
		logger.info("********************NonRotatingItemsTest*********************************");
		this.af = FrameworkFactory.get();
		Properties properties = new Properties();
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(configPath));
			properties.load(in);
			labor = properties.getProperty("system.username");
			maximoApi = new MaximoApi();
			maximoApi.setMaximoServer(properties.getProperty("system.maximoServerUrl"),
					properties.getProperty("system.maximoAPIKey"));
			masServer = properties.getProperty("system.masServer");
			createDefaultObjects();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!apiCodeSuccess) {
			logger.info("stopped framework and quit as API failed = " + apiCodeSuccess);
			FrameworkFactory.stopAll();
		} else {
			login(af);
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
	"mobile","desktop" }, description = "Verify contents, Verify single/multiple non-Rotating items can be added", timeOut = 900000)
		
		public void nonRotatingItemsAddFromReportWorkPage() throws Exception {
		
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage woDetails = new WorkOrderDetailsPage(af);	
        ReportWorkPage reportWorkPage = new ReportWorkPage(af);
		
		// Search the WO
		assertTrue(assignedWorkPage.search(woNum));
		// Click on selected workorder to open Work Order Details Page
		assignedWorkPage.openWorkOrderDetails();	
		// Click on Report Work touch-point
		woDetails.clickReportWorkButton();	
		//Go to materials used section and click on "Get reserved items"
		reportWorkPage.addMaterialThreeDots();
		// Click on Get Reserved items
		reportWorkPage.clickGetReservedItems();
		// Verify non-rotating header on Get Reserved page
		nonRotatingHeader = reportWorkPage.getTitle(reportWorkPage.getReservednonRotatingItemsPageHeader);
		assertEquals("Non-rotating items", nonRotatingHeader);
		logger.info("Non-rotating items header verified");
		//verify non-rotating item 
		headerItemNo=reportWorkPage.getTitle(reportWorkPage.getNonRotatingItemNo);
		assertEquals(itemNum + " " + "item" + " " + itemNum, headerItemNo);
		// Select the item by clicking on the checkbox
		reportWorkPage.clickOnNonRotatingItemCheckbox();
		//Click on the confirm selection button
		reportWorkPage.clickOnConfirmSelectionBtn();
		// verify non rotating items added
		assertEquals(reportWorkPage.verifyNonRotatingItemsAdded(), itemNum + " " + "item" + " " + itemNum,
						"Fail");
		reportWorkPage.clickBackChevron();
		woDetails.clickBackWOList();
		assignedWorkPage.clickClearButton();	
		
	}
	
	
	protected void createDefaultObjects() throws Exception {
		try {
		logger.info("Creating default objects");
		
		// Creating storeroom
		logger.info("Creating an storeroom");
		location = AbstractAutomationFramework.randomString(5).toUpperCase();
		String storeRoomResult = maximoApi.retrieve(new StoreRoom(),
				"lean=1&addid=1&action=system:new&ignorecollectionref=1");
		StoreRoom storeRoom = new Gson().fromJson(storeRoomResult, StoreRoom.class);
		storeRoom.setDescription("StoreRoom " + location);
		storeRoom.setLocation(location);

		maximoApi.create(storeRoom);
		logger.info("storeRoom location::" + location);

		// Creating the item
		logger.info("Creating another item");
		itemNum = AbstractAutomationFramework.randomString(5).toUpperCase();
		String itemResult1 = maximoApi.retrieve(new Item(),
				"lean=1&addid=1&domainmeta=1&internalvalues=1&action=system:new&oslc.select=itemsetid,lottype,issueunit,orderunit,rotating,itemtype");
		Item item1 = new Gson().fromJson(itemResult1, Item.class);
		item1.setItemNum(itemNum);
		item1.setDescription("item " + itemNum);
		maximoApi.create(item1);
		logger.info("ItemNum::" + itemNum);

		AddItemToStoreroom addItemToStoreroom1 = new AddItemToStoreroom();

		addItemToStoreroom1.setItemNum(itemNum);
		addItemToStoreroom1.setItemSetID(SetupData.ITEMSET);
		addItemToStoreroom1.setStoreroom(location);
		addItemToStoreroom1.setSiteID(SetupData.SITEID);
		addItemToStoreroom1.setTool(false);
		addItemToStoreroom1.setIssueUnit(SetupData.ISSUEUNIT);
		addItemToStoreroom1.setCurBal(100);
		addItemToStoreroom1.setDfltbin(SetupData.DFLTBIN);
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
		String workOrderResult = maximoApi.retrieve(new WorkOrder(),
				"addid=1&internalvalues=1&action=system:new&addschema=1");
		newWorkOrder = new Gson().fromJson(workOrderResult, WorkOrder.class);
		newWorkOrder.setDescription("Get Reserved Items for Non-rotating items");
		newWorkOrder.setSiteId(SetupData.SITEID);
		newWorkOrder.setAssetNum(assetNum);
		newWorkOrder.setGLAccount(SetupData.GLDEBITACCT);
		// newWorkOrder.setFlowControlled(true);
		maximoApi.create(newWorkOrder);
		woNum = newWorkOrder.getWoNum();
		logger.info("Work Order: " + woNum);

		// Assign the labor
		maximoApi.addAssignmentLabor(newWorkOrder, labor);
		logger.info("Assignment added");

		// Adding item to Workorder
		logger.info("Adding wpmaterial");
		WpMaterial wpmaterial = new WpMaterial();
		wpmaterial.setItemNum(item1.getItemNum());
		wpmaterial.setLocation(location);
		wpmaterial.setItemQty(10);
		wpmaterial.setRate(1);
		List<WpMaterial> WpMaterialArray = new ArrayList<WpMaterial>();
		WpMaterialArray.add(wpmaterial);
		newWorkOrder.setWpMaterial(WpMaterialArray);
		logger.info("Item updated to WO ");
		maximoApi.update(newWorkOrder);
		// Change WO status to Approved
		logger.info("Changing work order status to APPR");
		maximoApi.changeStatus(newWorkOrder, WoStatus.APPR.toString());
		
		 apiCodeSuccess = true;
       } catch (AssertionError e) {
  	    logger.info(" SkipException apiCodeSuccess = " + apiCodeSuccess);
        apiCodeSuccess = false;
       throw new Exception("Test Setup API Failed,Stopping execution.");
       }

}
}
