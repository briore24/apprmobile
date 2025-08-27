package com.ibm.maximo.technician.testcases.calibration.tests;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

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
import com.ibm.maximo.technician.testcases.calibration.page.AsFoundValuesPage;
import com.ibm.maximo.technician.testcases.calibration.page.AsLeftValuesPage;
import com.ibm.maximo.technician.testcases.calibration.page.DatasheetListPage;
import com.ibm.maximo.technician.testcases.calibration.page.FunctionCheckAsFoundPage;
import com.ibm.maximo.technician.testcases.calibration.page.FunctionCheckAsLeftPage;
import com.ibm.maximo.technician.testcases.calibration.page.AssetFunctionPage;

public class FunctionCheckTest extends TechnicianTest {

	private final Logger logger = LoggerFactory.getLogger(FunctionCheckTest.class);
	private static String functionCheckPassStatus = "PASS", functionCheckFailStatus = "FAIL";
	private AbstractAutomationFramework af;
	private MaximoApi maximoApi;
	private TestSuite testSuite;
	private WorkOrder newWorkOrder;
	private String assetNum, locationNum, labor;
	private String assetDescription = "TestAuto Calibration Asset";
	private String LOCATION_DESCRIPTION = "INVALID_LOCATION_1";
	private String functionChecksAssetFunctionName = "Function Checks";

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
			updateData();
		} catch (Exception e) {
			e.printStackTrace();
			throw new SkipException(
					"Skipped Test case due to an error while api setup or due to an error while data sheet creation...!!!");
		}
	}

	@Test(groups = { "mobile" }, description = "Verify PASS status for Function check data sheet", timeOut = 9500000)
	public void verifyFunctionCheckPassStatus() throws Exception {
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage assignedWorkOrderDetailsPage = new WorkOrderDetailsPage(af);
		DatasheetListPage dataSheetListPageObj = new DatasheetListPage(af);
		AssetFunctionPage assetFunctionPageObj = new AssetFunctionPage(af);
		FunctionCheckAsFoundPage functionCheckAsFoundPageObj = new FunctionCheckAsFoundPage(af);
		FunctionCheckAsLeftPage functionCheckAsLeftPageObj = new FunctionCheckAsLeftPage(af);
		SoftAssert softAssert = new SoftAssert();

		logger.info("Verify Status for Function Checks...!!!!!");
		// Search the WO
		assertEquals(true, assignedWorkPage.search(System.getProperty("Multi_AF_WONum")));

		// Navigate to work order details page
		assignedWorkPage.openCardDetails();
		assignedWorkOrderDetailsPage.clickBackChevronToListPage();
		assignedWorkPage.openCardDetails();

		// Verify Calibration icon
		assertTrue(assignedWorkOrderDetailsPage.isCalibrationIconPresent());
		logger.info("Is Calibration Icon present? : " + assignedWorkOrderDetailsPage.isCalibrationIconPresent());
		Reporter.log("Is Calibration Icon present? : " + assignedWorkOrderDetailsPage.isCalibrationIconPresent());

		// Refresh to get Badge on Calibration icon
		assignedWorkOrderDetailsPage.clickCalibrationIcon();
		dataSheetListPageObj.clickBackArrowBtn();
		assignedWorkOrderDetailsPage.clickBackChevronToListPage();
		assignedWorkPage.openCardDetails();

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
		assertTrue(assetFunctionPageObj.isAssetFunctionPresent(functionChecksAssetFunctionName),
				"Verify Function Checks asset function is present");
		logger.info(functionChecksAssetFunctionName + " asset function is displayed.");

		// Navigate to As found values page
		logger.info("Navigate to As Found value page of Function Checks");
		assetFunctionPageObj.clickAssetFunctionAsFoundArrowBtn(functionChecksAssetFunctionName);
		assertEquals(functionCheckAsFoundPageObj.getPageTitle(), FunctionCheckAsFoundPage.functionCheckAsFoundPageTitle,
				"Verify As found values page title.");
		assertEquals(functionCheckAsFoundPageObj.getAssetFunctionName(), functionChecksAssetFunctionName,
				"Verify Function Checks asset function name is displayed.");
		logger.info("Function Checks asset function is displayed on As found values page");
		Reporter.log("Function Checks asset function is displayed on As found values page");

		// Select All function checks as PASS
		functionCheckAsFoundPageObj.selectAllAsFoundPass();

		// Save As found values
		logger.info("Save As found values");
		Reporter.log("Save As found values");
		functionCheckAsFoundPageObj.saveAsFoundValues();

		// Verify As found asset function status display PASS
		softAssert.assertEquals(functionCheckAsFoundPageObj.getAssetFunctionStatus(), functionCheckPassStatus,
				"Verify Asset function status on As found page after save all PASS = " + functionCheckPassStatus);
		logger.info("Asset function status on As found page display " + functionCheckPassStatus);
		Reporter.log("Asset function status on As found page display " + functionCheckPassStatus);

		// Navigate back to Asset Function page
		functionCheckAsFoundPageObj.clickBackArrowBtn();
		softAssert.assertEquals(assetFunctionPageObj.getAssetFunctionAsFoundStatus(functionChecksAssetFunctionName),
				functionCheckPassStatus, "Verify As found status on Asset Function page");
		takeScreenshot("FunctionChecks_AssetFunctionPage_AsFound_PASS", af);

		// Navigate to As left values page
		assetFunctionPageObj.clickAssetFunctionAsLeftArrowBtn(functionChecksAssetFunctionName);
		assertEquals(functionCheckAsLeftPageObj.getPageTitle(), FunctionCheckAsLeftPage.functionCheckAsLeftPageTitle,
				"Verify As left values page title.");
		assertEquals(functionCheckAsLeftPageObj.getAssetFunctionName(), functionChecksAssetFunctionName,
				"Verify Function Checks asset function name is displayed.");
		logger.info("Function Checks asset function is displayed on As left values page");
		Reporter.log("Function Checks asset function is displayed on As left values page");

		// Select All function checks as PASS
		functionCheckAsLeftPageObj.selectAllAsLeftPass();

		// Save As left values
		logger.info("Save As left values");
		Reporter.log("Save As left values");
		functionCheckAsLeftPageObj.saveAsLeftValues();

		// Verify As left asset function status display PASS
		softAssert.assertEquals(functionCheckAsLeftPageObj.getAssetFunctionStatus(), functionCheckPassStatus,
				"Verify Asset function status on As left page after save all PASS = " + functionCheckPassStatus);
		logger.info("Asset function status on As left page display " + functionCheckPassStatus);
		Reporter.log("Asset function status on As left page display " + functionCheckPassStatus);

		// Navigate back to Asset Function page
		functionCheckAsLeftPageObj.clickBackArrowBtn();
		softAssert.assertEquals(assetFunctionPageObj.getAssetFunctionAsLeftStatus(functionChecksAssetFunctionName),
				functionCheckPassStatus, " Verify As left status on Asset Function page");
		takeScreenshot("FunctionChecks_AssetFunctionPage_AsLeft_PASS", af);

		// Select As found FAIL
		assetFunctionPageObj.clickAssetFunctionAsFoundArrowBtn(functionChecksAssetFunctionName);
		softAssert.assertEquals(functionCheckAsFoundPageObj.getAssetFunctionStatus(), functionCheckPassStatus,
				"Verify Asset function status on As found page before save PASS = " + functionCheckPassStatus);
		logger.info("Select one of the As found function check as FAIL");
		Reporter.log("Select one of the As found function check as FAIL");
		functionCheckAsFoundPageObj.selectFunctionCheckAsFoundFailBtn((int) (Math.random() * 3));

		// Save As found values
		logger.info("Save As found values");
		Reporter.log("Save As found values");
		functionCheckAsFoundPageObj.saveAsFoundValues();

		// Verify As found asset function status display FAIL
		softAssert.assertEquals(functionCheckAsFoundPageObj.getAssetFunctionStatus(), functionCheckFailStatus,
				"Verify Asset function status on As found page after save FAIL = " + functionCheckFailStatus);
		logger.info("Asset function status on As found page display " + functionCheckFailStatus);
		Reporter.log("Asset function status on As found page display " + functionCheckFailStatus);

		// Navigate back to Asset Function page
		functionCheckAsFoundPageObj.clickBackArrowBtn();
		softAssert.assertEquals(assetFunctionPageObj.getAssetFunctionAsFoundStatus(functionChecksAssetFunctionName),
				functionCheckFailStatus, "Verify As found status on Asset Function page");
		takeScreenshot("FunctionChecks_AssetFunctionPage_AsFound_FAIL", af);

		// Select As left FAIL
		assetFunctionPageObj.clickAssetFunctionAsLeftArrowBtn(functionChecksAssetFunctionName);
		softAssert.assertEquals(functionCheckAsLeftPageObj.getAssetFunctionStatus(), functionCheckPassStatus,
				"Verify Asset function status on As left page before save PASS = " + functionCheckPassStatus);
		logger.info("Select one of the As left function check as FAIL");
		Reporter.log("Select one of the As left function check as FAIL");
		functionCheckAsLeftPageObj.selectFunctionCheckAsLeftFailBtn((int) (Math.random() * 3));

		// Save As left values
		logger.info("Save As left values");
		Reporter.log("Save As left values");
		functionCheckAsLeftPageObj.saveAsLeftValues();

		// Verify As left asset function status display PASS
		softAssert.assertEquals(functionCheckAsLeftPageObj.getAssetFunctionStatus(), functionCheckFailStatus,
				"Verify Asset function status on As left page after save FAIL = " + functionCheckFailStatus);
		logger.info("Asset function status on As left page display " + functionCheckFailStatus);
		Reporter.log("Asset function status on As left page display " + functionCheckFailStatus);

		// Navigate back to Asset Function page
		functionCheckAsLeftPageObj.clickBackArrowBtn();
		softAssert.assertEquals(assetFunctionPageObj.getAssetFunctionAsLeftStatus(functionChecksAssetFunctionName),
				functionCheckFailStatus, " Verify As left status on Asset Function page");
		takeScreenshot("FunctionChecks_AssetFunctionPage_AsLeft_FAIL", af);

		// Navigate back to Work order list page
		assetFunctionPageObj.clickBackArrowBtn();
		dataSheetListPageObj.clickBackArrowBtn();
		assignedWorkOrderDetailsPage.clickBackWOList();
		assignedWorkPage.clickClearButton();
		
		softAssert.assertAll();
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
