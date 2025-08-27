package com.ibm.maximo.technician.testcases.calibration.tests;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
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
import com.ibm.maximo.automation.mobile.api.json.Location;
import com.ibm.maximo.automation.mobile.api.json.WorkOrder;
import com.ibm.maximo.technician.framework.TechnicianTest;
import com.ibm.maximo.technician.page.MySchedulePage;
import com.ibm.maximo.technician.page.WorkOrderDetailsPage;
import com.ibm.maximo.technician.setupdata.SetupData;
import com.ibm.maximo.technician.setupdata.SetupData.WoStatus;
import com.ibm.maximo.technician.setupdata.SetupData.WorkType;
import com.ibm.maximo.technician.testcases.TestSuite;
import com.ibm.maximo.technician.testcases.calibration.page.AssetFunctionPage;
import com.ibm.maximo.technician.testcases.calibration.page.DatasheetListPage;
import com.ibm.maximo.technician.testcases.calibration.page.DynamicCheckAsFoundPage;
import com.ibm.maximo.technician.testcases.calibration.page.DynamicCheckAsLeftPage;

public class DynamicCheckTest extends TechnicianTest {

	private final Logger logger = LoggerFactory.getLogger(DynamicCheckTest.class);
	private AbstractAutomationFramework af;
	private MaximoApi maximoApi;
	private TestSuite testSuite;
	private WorkOrder newWorkOrder;
	private String assetNum, locationNum, labor;
	private String assetDescription = "TestAuto Calibration Asset";
	private String LOCATION_DESCRIPTION = "INVALID_LOCATION_1";
	private String dynamicChecksAssetFunctionName = "Dynamic Checks";

	@BeforeClass(alwaysRun = true)
	@Parameters({ "configPath" })
	public void setup(String configPath) throws Exception {
		logger.info("********************VerifyToleranceTest*********************************");
		this.af = FrameworkFactory.get();
		Properties properties = new Properties();
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(configPath));
			properties.load(in);
			labor = properties.getProperty("system.username");
			maximoApi = new MaximoApi();
			maximoApi.setMaximoServer(properties.getProperty("system.maximoServerUrl"),
					properties.getProperty("system.maximoAPIKey"));

			if (System.getProperty("Multi_AF_DatasheetName") == null && System.getProperty("Multi_AF_WONum") == null) {
				createDefaultObjects(configPath);
			} else {
				logger.info("Multi Asset function data sheet and Work order are already created...!!!!!");
			}
			login(af);
		} catch (Exception e) {
			e.printStackTrace();
			throw new SkipException(
					"Skipped Test case due to an error while api setup or due to an error while data sheet creation...!!!");
		}
	}

	@Test(groups = { "mobile" }, description = "Save Dynamic Checks data", timeOut = 9500000)
	public void saveDynamicCheckData() throws Exception {
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage assignedWorkOrderDetailsPage = new WorkOrderDetailsPage(af);
		DatasheetListPage dataSheetListPageObj = new DatasheetListPage(af);
		AssetFunctionPage assetFunctionPageObj = new AssetFunctionPage(af);
		DynamicCheckAsFoundPage dynamicCheckAsFoundPageObj = new DynamicCheckAsFoundPage(af);
		DynamicCheckAsLeftPage dynamicCheckAsLeftPageObj = new DynamicCheckAsLeftPage(af);

		logger.info("Dynamic Check test...!!!!!");
		// Search the WO
		assignedWorkPage.checkForUpdateButton();
		assertEquals(true, assignedWorkPage.search(System.getProperty("Multi_AF_WONum")));

		// Navigate to work order details page
		assignedWorkPage.openCardDetails();

		// Verify Calibration icon
		assertTrue(assignedWorkOrderDetailsPage.isCalibrationIconPresent());
		logger.info("Is Calibration Icon present? : " + assignedWorkOrderDetailsPage.isCalibrationIconPresent());
		Reporter.log("Is Calibration Icon present? : " + assignedWorkOrderDetailsPage.isCalibrationIconPresent());

		// Refresh to get Badge on Calibration icon
		// assignedWorkOrderDetailsPage.clickCalibrationIcon();
		// dataSheetListPageObj.clickBackArrowBtn();
		// assignedWorkOrderDetailsPage.clickBackChevronToListPage();
		// assignedWorkPage.openCardDetails();

		// Verify the badge on Calibration icon
		assertTrue(assignedWorkOrderDetailsPage.isCalibreationIconBadgePresent());
		logger.info("Is Badge present on Calibration Icon? : "
				+ assignedWorkOrderDetailsPage.isCalibreationIconBadgePresent());
		Reporter.log("Is Badge present on Calibration Icon? : "
				+ assignedWorkOrderDetailsPage.isCalibreationIconBadgePresent());
		logger.info("Calibration Badge Value : " + assignedWorkOrderDetailsPage.getCalibrationBadgeValue());
		Reporter.log("Calibration Badge Value : " + assignedWorkOrderDetailsPage.getCalibrationBadgeValue());

		// Navigate to Calibration Data sheet list page
		logger.info("Navigate to Data sheet list page");
		assignedWorkOrderDetailsPage.clickCalibrationIcon();
		assertEquals(dataSheetListPageObj.getPageSubtitle(), DatasheetListPage.datasheetListPageTitleName,
				"Verify Datasheet list page sub-title.");
		assertEquals(dataSheetListPageObj.getDatasheetName(), System.getProperty("Multi_AF_DatasheetName"),
				"Verify created Datasheet Name");

		// Navigate to Data sheet page
		logger.info("Navigate to Datasheet page");
		dataSheetListPageObj.clickNavigationToDatasheet();
		assertEquals(assetFunctionPageObj.getPageTitle(), AssetFunctionPage.assetFunctionPageTitleName,
				"Verify Datasheet page title.");
		logger.info("Asset function page is displayed");
		Reporter.log("Asset function page is displayed");

		// Verify Function checks asset function is present
		assertTrue(assetFunctionPageObj.isAssetFunctionPresent(dynamicChecksAssetFunctionName),
				"Verify Function Checks asset function is present");
		logger.info(dynamicChecksAssetFunctionName + " asset function is displayed.");

		// Navigate to As found values page
		logger.info("Navigate to As found value page of Dynamic Checks");
		assetFunctionPageObj.clickAssetFunctionAsFoundArrowBtn(dynamicChecksAssetFunctionName);
		assertEquals(dynamicCheckAsFoundPageObj.getPageTitle(), DynamicCheckAsFoundPage.dynamicCheckAsFoundPageTitle,
				"Verify As found values page title.");
		assertEquals(dynamicCheckAsFoundPageObj.getAssetFunctionName(), dynamicChecksAssetFunctionName,
				"Verify Dynamic Checks asset function name is displayed.");
		logger.info("Dynamic Checks asset function is displayed on As found values page");
		Reporter.log("Dynamic Checks asset function is displayed on As found values page");

		// Enter Values and select unit
		dynamicCheckAsFoundPageObj.enterValueAndSelectUnit();

		// Save As found data
		dynamicCheckAsFoundPageObj.saveAsFoundValues();

		// Navigate back to Asset function page
		dynamicCheckAsFoundPageObj.clickBackArrowBtn();

		// Navigate to As found values page
		assetFunctionPageObj.clickAssetFunctionAsFoundArrowBtn(dynamicChecksAssetFunctionName);

		// verify the saved values
		dynamicCheckAsFoundPageObj.verifySavedValues();
		logger.info("As found saved values verified");
		Reporter.log("As found saved values verified");

		// Navigate back to Asset function page
		dynamicCheckAsFoundPageObj.clickBackArrowBtn();

		// Navigate to As left values page
		logger.info("Navigate to As left value page of Dynamic Checks");
		assetFunctionPageObj.clickAssetFunctionAsLeftArrowBtn(dynamicChecksAssetFunctionName);
		assertEquals(dynamicCheckAsLeftPageObj.getPageTitle(), DynamicCheckAsLeftPage.dynamicCheckAsLeftPageTitle,
				"Verify As left values page title.");
		assertEquals(dynamicCheckAsLeftPageObj.getAssetFunctionName(), dynamicChecksAssetFunctionName,
				"Verify Dynamic Checks asset function name is displayed.");
		logger.info("Dynamic Checks asset function is displayed on As left values page");
		Reporter.log("Dynamic Checks asset function is displayed on As left values page");

		// Enter Values and select unit
		dynamicCheckAsLeftPageObj.enterValueAndSelectUnit();

		// Save As left data
		dynamicCheckAsLeftPageObj.saveAsFoundValues();

		// Navigate back to Asset function page
		dynamicCheckAsLeftPageObj.clickBackArrowBtn();

		// Navigate to As left values page
		assetFunctionPageObj.clickAssetFunctionAsLeftArrowBtn(dynamicChecksAssetFunctionName);

		// verify the saved values
		dynamicCheckAsLeftPageObj.verifySavedValues();
		logger.info("As left saved values verified");
		Reporter.log("As left saved values verified");

		// Navigate back to Asset function page
		dynamicCheckAsLeftPageObj.clickBackArrowBtn();

		// Navigate back to Work order list page
		assetFunctionPageObj.clickBackArrowBtn();
		dataSheetListPageObj.clickBackArrowBtn();
		assignedWorkOrderDetailsPage.clickBackWOList();
		assignedWorkPage.clickClearButton();

	}

	@AfterMethod(alwaysRun = true)
	public void userLogout() throws Exception {
		logOut(af);
	}

	@AfterClass(alwaysRun = true)
	public void teardown() throws Exception {
		// logOut(af);
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
			maximoApi.create(newWorkOrder);
			logger.info("Work Order: {}" + woNum);

			System.setProperty("Multi_AF_WONum", woNum);
			// Create Multi Asset function data sheet and assign to WO
			new CreateDataSheet().createMultiAFDataSheet(configPath, jdbcConnection, assetNum, locationNum, woNum,
					SetupData.SITEID, SetupData.ORGID);

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
}
