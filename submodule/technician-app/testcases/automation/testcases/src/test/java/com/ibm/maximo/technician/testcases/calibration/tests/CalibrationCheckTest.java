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
import com.ibm.maximo.technician.page.MySchedulePage;
import com.ibm.maximo.technician.page.ReportWorkPage;
import com.ibm.maximo.technician.page.WorkOrderDetailsPage;
import com.ibm.maximo.technician.setupdata.SetupData;
import com.ibm.maximo.technician.setupdata.SetupData.WoStatus;
import com.ibm.maximo.technician.setupdata.SetupData.WorkType;
import com.ibm.maximo.technician.testcases.TestSuite;
import com.ibm.maximo.technician.testcases.calibration.page.AsFoundValuesPage;
import com.ibm.maximo.technician.testcases.calibration.page.AsLeftValuesPage;
import com.ibm.maximo.technician.testcases.calibration.page.AssetFunctionPage;
import com.ibm.maximo.technician.testcases.calibration.page.DatasheetListPage;
import com.ibm.maximo.technician.testcases.calibration.support.constants.DatasheetConstants;
import com.ibm.maximo.technician.testcases.calibration.support.constants.ValidationConstants;

public class CalibrationCheckTest extends DatasheetTests {

	private final Logger logger = LoggerFactory.getLogger(CalibrationCheckTest.class);
	private AbstractAutomationFramework af;
	private MaximoApi maximoApi;
	private TestSuite testSuite;
	private WorkOrder newWorkOrder;
	private String woNum;
	private String assetNum, locationNum, labor;
	private String assetDescription = "TestAuto Calibration Asset";
	private String LOCATION_DESCRIPTION = "INVALID_LOCATION_1";
	
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
		} catch (Exception e) {
			e.printStackTrace();
			throw new SkipException("Skipped Test case due to an error while api setup...!!!");
		}

		login(af);
		updateData();
	}

	@Test(groups = {
			"mobile" }, description = "Verify calibration touchpoint is visible and fill datasheets to complete workorder", timeOut = 95000000)
	public void verifyCalibrationTouchpoint() throws Exception {
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage assignedWorkOrderDetailsPage = new WorkOrderDetailsPage(af);
		DatasheetListPage dataSheetListPageObj = new DatasheetListPage(af);
		AssetFunctionPage dataSheetPageObj = new AssetFunctionPage(af);
		AsFoundValuesPage asFoundValuesPageObj = new AsFoundValuesPage(af);
		AsLeftValuesPage asLeftValuesPageObj = new AsLeftValuesPage(af);
		WorkOrderDetailsPage woDetails = new WorkOrderDetailsPage(af);
		ReportWorkPage reportWork = new ReportWorkPage(af);

		String validationSetPathStr = this.getValidationSetPath().toString();
		String validationTestCase = ValidationConstants.TESTCASE_PASS;
		
		// Search the WO
		assignedWorkPage.checkForUpdateButton();
		assertEquals(true, assignedWorkPage.search(woNum));

		// Navigate to work order details page
		assignedWorkPage.openCardDetails();
		assignedWorkOrderDetailsPage.clickBackChevronToListPage();

		// Click on Start work button
		assignedWorkPage.clickStartWorkButton();

		// Check Barcode Scanner
		if (woDetails.isScanDialogExists()) {
			woDetails.scanBarcode();
			logger.info("Scanner Skipped");
		}

		// Verify stop button is displayed
		assertEquals("carbon:stop", woDetails.getStopWorkButtonText());

		// Click stop work button
		woDetails.clickStopWorkTimerButtonIcon();

		// Verify labor approval page displays
		assertEquals("Labor approval?", woDetails.getLaborApprovalText());

		woDetails.clickLaborApproval();
		reportWork.completeReportWorkButton();

		// Verify incomplete data sheets notification
		assertEquals("Incomplete data sheets", dataSheetPageObj.dataSheetwarningHeader());
		assertEquals("Required data sheets must be completed to finalize calibration.",
				dataSheetPageObj.verifyWarningMessage());
		dataSheetPageObj.clickDataSheet();
		assertEquals("Associated data sheets", dataSheetPageObj.verifyDataSheetTitle());

		dataSheetListPageObj.clickBackArrowBtn();

		// Verify the badge on Calibration icon
		boolean isCalibrationIconBadgePresent = assignedWorkOrderDetailsPage.isCalibreationIconBadgePresent();
		String calibrationBadgeValue = assignedWorkOrderDetailsPage.getCalibrationBadgeValue();
		assertTrue(isCalibrationIconBadgePresent);
		logger.info("Is Badge present on Calibration Icon? : " + isCalibrationIconBadgePresent);
		Reporter.log("Is Badge present on Calibration Icon? : " + isCalibrationIconBadgePresent);
		logger.info("Calibration Badge Value : " + calibrationBadgeValue);
		Reporter.log("Calibration Badge Value : " + calibrationBadgeValue);

		// Navigate to Calibration Datasheet list page
		logger.info("Navigate to Datasheet list page");
		assignedWorkOrderDetailsPage.clickCalibrationIcon();
		assertEquals(dataSheetListPageObj.getPageSubtitle(), "Associated data sheets",
				"Verify Datasheet list page sub-title.");
		assertEquals(dataSheetListPageObj.getDatasheetName(), this.getDatasheetName(),
				"Verify created Datasheet Name");

		// Navigate to Data sheet page
		logger.info("Navigate to Datasheet page");
		dataSheetListPageObj.clickNavigationToDatasheet();

		// Navigate to As found values page
		logger.info("Navigate to As Found value page");
		dataSheetPageObj.clickAsFoundNextArrowBtn();
		assertEquals(asFoundValuesPageObj.getPageTitle(), "As found values", "Verify As found values page title.");

		// Enter As found values
		logger.info("Enter Calibration Point values");
		Reporter.log("Enter Calibration Point values");
		asFoundValuesPageObj.enterCalibrationPointValues(validationSetPathStr, validationTestCase);

		// Save As found values
		logger.info("Save As found values");
		Reporter.log("Save As found values");
		asFoundValuesPageObj.saveAsFoundValues();

		// Navigate to Asset Function page
		asFoundValuesPageObj.clickBackArrowBtn();

		logger.info("Navigate to As left value page");
		dataSheetPageObj.clickAsLeftNextArrowBtn();
		assertEquals(asLeftValuesPageObj.getPageTitle(), "As left values", "Verify As left values page title.");

		// Enter As Found values
		logger.info("Enter Calibration Point values");
		Reporter.log("Enter Calibration Point values");
		asLeftValuesPageObj.enterCalibrationPointValues(validationSetPathStr, validationTestCase);

		// Save As Found values
		logger.info("Save As Left values");
		Reporter.log("Save As Left values");
		asLeftValuesPageObj.saveAsLeftValues();

		// Navigate to Asset Function page
		asLeftValuesPageObj.clickBackArrowBtn();
		dataSheetPageObj.clickBackArrowBtn();
		dataSheetListPageObj.clickBackArrowBtn();
		woDetails.clickStartWorkTimerButtonIcon();
		woDetails.clickStopWorkTimerButtonIcon();
		assertEquals("Labor approval?", woDetails.getLaborApprovalText());
		woDetails.clickLaborApproval();
		reportWork.completeReportWorkButton();

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
}