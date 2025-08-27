package com.ibm.maximo.technician.testcases;

import static org.testng.Assert.*;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import com.ibm.maximo.automation.mobile.api.json.*;
import com.ibm.maximo.components.ButtonComponent;
import com.ibm.maximo.technician.setupdata.SetupData.WorkType;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
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
import com.ibm.maximo.automation.mobile.DesktopAutomationFramework;
import com.ibm.maximo.automation.mobile.api.MaximoApi;
import com.ibm.maximo.technician.framework.TechnicianTest;
import com.ibm.maximo.technician.page.CreateWorkOrderPage;
import com.ibm.maximo.technician.page.FollowUpWorkPage;
import com.ibm.maximo.technician.page.MultipleAssetsAndLocationsPage;
import com.ibm.maximo.technician.page.MySchedulePage;
import com.ibm.maximo.technician.page.WorkOrderDetailsPage;
import com.ibm.maximo.technician.setupdata.SetupData;
import com.ibm.maximo.technician.setupdata.SetupData.*;

/*
 * MASR-789:Technician (RBA) - Multiple Assets, Locations requires an Add/Delete button
 */

public class AddDeleteMultiAssetLocationInFollowUpWorkTest extends TechnicianTest {
	private final Logger logger = LoggerFactory.getLogger(AddDeleteMultiAssetLocationInFollowUpWorkTest.class);
	private AbstractAutomationFramework af;
	private TestSuite testSuite;
	private MaximoApi maximoApi;
	private WorkOrder newWorkOrder;
	private static final String LOCATION_DESCRIPTION = "Location for mobile automation test";
	private String assetNum, assetNum2, assetNum3, labor, locationNum, locationNum2, locationNum3;
	private static final String WORKORDER_DESCRIPTION = "Work Order for Automation Test";
	private String count = "1";
	private String woNum,createdFollowUpWoNum;
	private String createdFollowUpWOdescription;
	private boolean checkMultiAssetLocationview = true;
	private String firstDeleteIconMultiAssetLocation = "ze32d[0]", secDeleteIconMultiAssetLocation= "ze32d[1]", secUndoIconMultiAssetLocation= "xzr76[1]";

	@BeforeClass(alwaysRun = true)
	@Parameters({ "configPath" })
	public void setup(String configPath) throws Exception {
		logger.info("********************AddDeleteMultiAssetLocationInFollowUpWorkTest*********************************");
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

	@Test(groups = { "mobile" , "desktop"}, description = "Verify new follow-up work order with add/delete parent asset location which is multiassetlocation and search follow up WO and check added multi asset locations", timeOut = 800000)
	public void createFollowUpWorkOrderWithMultiAssetLocation() throws Exception {

		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage woDetailsPage = new WorkOrderDetailsPage(af);
		FollowUpWorkPage followUpWorkPage = new FollowUpWorkPage(af);

		assignedWorkPage.search(woNum);
		assignedWorkPage.openWorkOrderDetails();
		// Test data created with 3 multi asset and location with WO. So, count check is 3
		assertEquals("3", woDetailsPage.multiAssetsAndLocationsCountDisplay("3"));
		woDetailsPage.clickFollowUpWorkButton();
		String followUpTitle = followUpWorkPage.getTitle(followUpWorkPage.followUpPageTitle);
		logger.info(followUpTitle);
		assertEquals("Follow-up work", followUpTitle);
		followUpWorkPage.clickPlusButton();
		String createFollowUpTitle = woDetailsPage.getTitle(followUpWorkPage.CreateFollowUpPageTitle);
		assertEquals("Create follow-up WO", createFollowUpTitle);
		logger.info(createFollowUpTitle);
		assertEquals("Asset and location", followUpWorkPage.assetLocationText());
		if (testType.equalsIgnoreCase("desktop")) {
			DesktopAutomationFramework daf = (DesktopAutomationFramework) this.af;
			daf.scrollToBottom();
		}
		else {
			MobileAutomationFramework maf = (MobileAutomationFramework) this.af;
			maf.scrollDown();
		}
		assertEquals("Parent assets and locations", followUpWorkPage.parentAssetLocationText());
		
		Set<String> multiAssetLocSet = new HashSet<String>();
		multiAssetLocSet.add(assetNum2);multiAssetLocSet.add(assetNum3);multiAssetLocSet.add(locationNum3);
		
		List<WebElement> ele = af.waitForElementToBePresent(By.cssSelector("input[id^='v_d_4']"), 2000).findElements(By.cssSelector("input[id^='v_d_4']"));
		
		for(WebElement i:ele){
			String value = i.getText();logger.info("value >>>>"+value);
			boolean test = multiAssetLocSet.contains(value);
			checkMultiAssetLocationview = checkMultiAssetLocationview && test;
			logger.info("checkMultiAssetLocationview >>>>"+checkMultiAssetLocationview);
		}
		
		if(checkMultiAssetLocationview) {
			af.instantiateComponent(ButtonComponent.class, firstDeleteIconMultiAssetLocation).click();// Delete first asset/location from parent asset and location which is multiassetlocation
			af.instantiateComponent(ButtonComponent.class, secDeleteIconMultiAssetLocation).click();// Delete second asset/location from parent asset and location which is multiassetlocation
			af.instantiateComponent(ButtonComponent.class, secUndoIconMultiAssetLocation).click();// Undo second asset/location from parent asset and location which is multiassetlocation			
		}
		followUpWorkPage.clickCreateFollowUp();
		Thread.sleep(10000);// Lots of time is taken to save and back to follow up page
		assertEquals("Related work orders", followUpWorkPage.relatedWorkOrdersText());
		assertEquals("Related Tickets", followUpWorkPage.relatedTokenHeaderText());
		Thread.sleep(1000);
		createdFollowUpWOdescription = followUpWorkPage.followUpDescription();
		assertTrue(createdFollowUpWOdescription.contains(WORKORDER_DESCRIPTION));
		createdFollowUpWoNum = createdFollowUpWOdescription.split(" ")[0];logger.info("createdFollowUpWoNum >>>>"+createdFollowUpWoNum);
		followUpWorkPage.backButton();
		woDetailsPage.clickBackChevron();
		assignedWorkPage.clickClearButton();
		assignedWorkPage.checkForUpdateButton();
		assignedWorkPage.search(woNum);
		assignedWorkPage.openWorkOrderDetails();
		assertEquals(count, woDetailsPage.followUpCountDisplay(count));
		// Test data created with 3 multi asset and location with WO. So, count check is 3
		assertEquals("3", woDetailsPage.multiAssetsAndLocationsCountDisplay("3"));
		woDetailsPage.clickBackChevron();
		assignedWorkPage.clickClearButton();
		
		// Now check created follow up WO with multi asset and location
		CreateWorkOrderPage createWO = new CreateWorkOrderPage(af);
		// Open Assigned Work from dropdown
		createWO.openNativeDropdown("Work created by me");
		
		assertTrue(assignedWorkPage.search(createdFollowUpWoNum), "Fail : Search Work Order is not displayed");
		assignedWorkPage.openWorkOrderDetails();
		assertEquals(count, woDetailsPage.followUpCountDisplay(count));
		//Follow up WO created with 2 multi asset and location with WO. So, count check is 2
		assertEquals("2", woDetailsPage.multiAssetsAndLocationsCountDisplay("2"));
		woDetailsPage.clickFollowUpWorkButton();
		followUpTitle = followUpWorkPage.getTitle(followUpWorkPage.followUpPageTitle);
		logger.info(followUpTitle);
		assertEquals("Follow-up work", followUpTitle);
		Thread.sleep(1000);
		String createdFollowUpWOdescription2 = followUpWorkPage.followUpDescription();
		logger.info("createdFollowUpWOdescription2 >>>>"+createdFollowUpWOdescription2);
		assertTrue(createdFollowUpWOdescription2.contains(woNum));
		followUpWorkPage.backButton();
		Thread.sleep(1000);
		woDetailsPage.multiAssetsAndLocationsChevron();
		MultipleAssetsAndLocationsPage multiALpage = new MultipleAssetsAndLocationsPage(af);
		multiALpage.search(assetNum2);
		multiALpage.search(assetNum3);
		multiALpage.clickBackChevron();
		woDetailsPage.clickBackChevronToListPage();
		assignedWorkPage.clickClearButton();
		// Open Assigned Work from dropdown
		createWO.openNativeDropdown("Assigned work");
	}

	protected void createDefaultObjects() throws Exception {
		logger.info("Creating default objects");
		// Create a location
		logger.info("Creating a location");
		locationNum = AbstractAutomationFramework.randomString(5).toUpperCase();
		Location newlocation = new Location();
		newlocation.setLocationId(locationNum);
		newlocation.setDescription(LOCATION_DESCRIPTION+locationNum);
		newlocation.setSiteId(SetupData.SITEID);
		newlocation.setType(SetupData.LocType.OPERATING.toString());
		maximoApi.create(newlocation);
		logger.info("location: {}" + locationNum);

		// Create an Asset
		logger.info("Creating an asset");
		assetNum = AbstractAutomationFramework.randomString(5).toUpperCase();
		String assetResult = maximoApi.retrieve(new Asset(), "addid=1&internalvalues=1&action=system:new&addschema=1");
		Asset newAsset = new Gson().fromJson(assetResult, Asset.class);
		newAsset.setAssetNum(assetNum);
		newAsset.setDescription(SetupData.ASSET_DESCRIPTION + assetNum);
		newAsset.setLocation(locationNum);
		newAsset.setSiteId(SetupData.SITEID);
		newAsset.setStatus(SetupData.LocAssetStatus.ACTIVE.toString());
		maximoApi.create(newAsset);
		logger.info("Asset: {}" + assetNum);

		// Create First Work Order
		logger.info("Creating work order");
		String workOrderResult = maximoApi.retrieve(new WorkOrder(),
				"addid=1&internalvalues=1&action=system:new&addschema=1");
		newWorkOrder = new Gson().fromJson(workOrderResult, WorkOrder.class);
		newWorkOrder.setDescription(WORKORDER_DESCRIPTION);
		newWorkOrder.setSiteId(SetupData.SITEID);
		newWorkOrder.setWoPriority(1);
		newWorkOrder.setWorkType(WorkType.PM.toString());
		newWorkOrder.setGLAccount(SetupData.GLDEBITACCT);
		newWorkOrder.setAssetNum(assetNum);
		maximoApi.create(newWorkOrder);
		woNum = newWorkOrder.getWoNum();
		logger.info("Work Order 1: {}" + woNum);
		
		// Create Multiple Assets
		List<MultipleAssetsLocationsCIs> amalc = new ArrayList<MultipleAssetsLocationsCIs>();
		
		// Create a location
		logger.info("Creating a location");
		locationNum2 = AbstractAutomationFramework.randomString(5).toUpperCase();
		Location newlocation2 = new Location();
		newlocation2.setLocationId(locationNum2);
		newlocation2.setDescription(LOCATION_DESCRIPTION+locationNum2);
		newlocation2.setSiteId(SetupData.SITEID);
		newlocation2.setType(SetupData.LocType.OPERATING.toString());
		maximoApi.create(newlocation2);
		logger.info("location2: {}" + locationNum2);

		// Create an Asset
		logger.info("Creating an asset");
		assetNum2 = AbstractAutomationFramework.randomString(5).toUpperCase();
		String assetResult2 = maximoApi.retrieve(new Asset(), "addid=1&internalvalues=1&action=system:new&addschema=1");
		Asset newAsset2 = new Gson().fromJson(assetResult2, Asset.class);
		newAsset2.setAssetNum(assetNum2);
		newAsset2.setDescription(SetupData.ASSET_DESCRIPTION + assetNum2);
		newAsset2.setLocation(locationNum2);
		newAsset2.setSiteId(SetupData.SITEID);
		newAsset2.setStatus(SetupData.LocAssetStatus.ACTIVE.toString());
		maximoApi.create(newAsset2);
		logger.info("Asset2: {}" + assetNum2);
		
		MultipleAssetsLocationsCIs firstAssetLoc = new MultipleAssetsLocationsCIs();
		firstAssetLoc.setIsprimary(false);
		firstAssetLoc.setAssetNum(assetNum2);
		
		// Create an Asset
		logger.info("Creating an asset");
		assetNum3 = AbstractAutomationFramework.randomString(5).toUpperCase();
		String assetResult3 = maximoApi.retrieve(new Asset(), "addid=1&internalvalues=1&action=system:new&addschema=1");
		Asset newAsset3 = new Gson().fromJson(assetResult3, Asset.class);
		newAsset3.setAssetNum(assetNum3);
		newAsset3.setDescription(SetupData.ASSET_DESCRIPTION + assetNum3);
		newAsset3.setSiteId(SetupData.SITEID);
		newAsset3.setStatus(SetupData.LocAssetStatus.ACTIVE.toString());
		maximoApi.create(newAsset3);
		logger.info("Asset3: {}" + assetNum3);
		
		MultipleAssetsLocationsCIs SecAsset = new MultipleAssetsLocationsCIs();
		SecAsset.setIsprimary(false);
		SecAsset.setAssetNum(assetNum3);
		
		// Create a location
		logger.info("Creating a location");
		locationNum3 = AbstractAutomationFramework.randomString(5).toUpperCase();
		Location newlocation3 = new Location();
		newlocation3.setLocationId(locationNum3);
		newlocation3.setDescription(LOCATION_DESCRIPTION+locationNum3);
		newlocation3.setSiteId(SetupData.SITEID);
		newlocation3.setType(SetupData.LocType.OPERATING.toString());
		maximoApi.create(newlocation3);
		logger.info("location3: {}" + locationNum3);
		
		MultipleAssetsLocationsCIs ThirdLoc = new MultipleAssetsLocationsCIs();
		ThirdLoc.setIsprimary(false);
		ThirdLoc.setLocation(locationNum3);
		
		amalc.add(firstAssetLoc);
		amalc.add(SecAsset);
		amalc.add(ThirdLoc);
		newWorkOrder.setMultiassetlocci(amalc);
		maximoApi.update(newWorkOrder);
		logger.info("Multiple Assets added");

		// Change WO status to Approved
		logger.info("Changing work order status to APPR");
		maximoApi.changeStatus(newWorkOrder, WoStatus.APPR.toString());

		// Assign the labor
		maximoApi.addAssignmentLabor(newWorkOrder, labor);
		logger.info("Assignment added");
	}
}
