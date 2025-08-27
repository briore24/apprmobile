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
import com.ibm.maximo.automation.mobile.api.json.AssetSpare;
import com.ibm.maximo.automation.mobile.api.json.Item;
import com.ibm.maximo.automation.mobile.api.json.ItemChangeStatus;
import com.ibm.maximo.automation.mobile.api.json.SpareParts;
import com.ibm.maximo.automation.mobile.api.json.StoreRoom;
import com.ibm.maximo.automation.mobile.api.json.WorkOrder;
import com.ibm.maximo.technician.framework.TechnicianTest;
import com.ibm.maximo.technician.page.MySchedulePage;
import com.ibm.maximo.technician.page.ReportWorkPage;
import com.ibm.maximo.technician.page.WorkOrderDetailsPage;
import com.ibm.maximo.technician.setupdata.SetupData;
import com.ibm.maximo.technician.setupdata.SetupData.ItemStatus;
import com.ibm.maximo.technician.setupdata.SetupData.WoStatus;

/*
 * MAXMOA-8089 :Test Case & Automation update - Reserve Material Page
 * Scenario 3 : testcases/ReportWorkPage/SelectAssetSpareParts.md
 * */

public class SelectAssetSparePartTest extends TechnicianTest {

	private final Logger logger = LoggerFactory.getLogger(SelectAssetSparePartTest.class);
	private AbstractAutomationFramework af;
	private TestSuite testSuite;
	private MaximoApi maximoApi;
	private String woNum, labor, selectSparePartsHeader, itemNum, location;
	private WorkOrder newWorkOrder;
	private boolean apiCodeSuccess = false;

	@BeforeClass(alwaysRun = true)
	@Parameters({ "configPath" })
	public void setup(String configPath) throws Exception {
		logger.info("********************SelectAssetSparePartTest*********************************");
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

	@Test(groups = { "mobile",
			"desktop" }, description = "Verify contents, Verify single/multiple non-Rotating items can be added", timeOut = 900000)

	public void selectSpareAssetPart() throws Exception {
		
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
		//Click on select asset spare part 
		reportWorkPage.clickSelectAssetSparePart();
		// Verify select spare part header 
		selectSparePartsHeader = reportWorkPage.getTitle(reportWorkPage.getSelectAssetSparePartHeader);
		assertEquals("Select spare parts", selectSparePartsHeader);
		logger.info("Select spare parts header verified");
		//click on the checkbox
		reportWorkPage.clickOnNonRotatingItemCheckbox();
		//Click on Confirm selection
		reportWorkPage.clickOnConfirmSelectionBtnOnSelectSparePartPage();
		// verify spare parts added
		assertEquals(reportWorkPage.verifyNonRotatingItemsAdded(), itemNum + " " + "item" + " " + itemNum, "Fail");
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
			logger.info("Creating item");
			itemNum = AbstractAutomationFramework.randomString(5).toUpperCase();
			String itemResult1 = maximoApi.retrieve(new Item(),
					"lean=1&addid=1&domainmeta=1&internalvalues=1&action=system:new&oslc.select=itemsetid,lottype,issueunit,orderunit,rotating,itemtype");
			Item item1 = new Gson().fromJson(itemResult1, Item.class);
			item1.setItemNum(itemNum);
			item1.setDescription("item " + itemNum);
			//item1.setSparePartAutoAdd(true);
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
			newAsset.setDescription("Asset for spare part");
			newAsset.setSiteId(SetupData.SITEID);
			maximoApi.create(newAsset);
			logger.info("Asset: " + assetNum);
						
			// Create new asset with Spare Parts
			logger.info("Creating new asset with Spare Parts");
			SpareParts spareParts1 = new SpareParts();
			spareParts1.setIssuedQty(0.0);
			spareParts1.setItemNum(itemNum);
			spareParts1.setQuantity(1.0);
			spareParts1.setItemSetId(SetupData.ITEMSET);
			spareParts1.setOrgId(SetupData.ORGID);
			spareParts1.setHasld(false);
			spareParts1.setLangCode(SetupData.LANGCODE);
			spareParts1.setSiteId(SetupData.SITEID);
			List<SpareParts> sparePartList = new ArrayList<>();
			sparePartList.add(spareParts1);
			logger.info("Spare Parts List: " + new Gson().toJson(sparePartList));

			String assetNum = AbstractAutomationFramework.randomString(5).toUpperCase();

			AssetSpare assetsparepart = new AssetSpare();
			assetsparepart.setSparePart(sparePartList);
			assetsparepart.setAssetNum(assetNum);
			assetsparepart.setDescription("Asset with Spare Parts");
			assetsparepart.setSiteId(SetupData.SITEID);
			maximoApi.create(assetsparepart);
			logger.info("Asset with Spare parts Created " + assetNum);
			
			// Create a work order
			String workOrderResult = maximoApi.retrieve(new WorkOrder(),
					"addid=1&internalvalues=1&action=system:new&addschema=1");
			newWorkOrder = new Gson().fromJson(workOrderResult, WorkOrder.class);
			newWorkOrder.setDescription("Automation for select asset spare parts");
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
