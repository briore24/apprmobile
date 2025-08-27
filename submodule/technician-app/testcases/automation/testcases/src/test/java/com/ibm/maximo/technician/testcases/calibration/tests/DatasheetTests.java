package com.ibm.maximo.technician.testcases.calibration.tests;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Reporter;
import org.testng.SkipException;

import com.google.gson.Gson;
import com.ibm.maximo.automation.mobile.AbstractAutomationFramework;
import com.ibm.maximo.automation.mobile.FrameworkFactory;
import com.ibm.maximo.automation.mobile.api.MaximoApi;
import com.ibm.maximo.automation.mobile.api.json.Asset;
import com.ibm.maximo.automation.mobile.api.json.PlusCWODS;
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
import com.ibm.maximo.technician.testcases.calibration.page.AssetFunctionPage;
import com.ibm.maximo.technician.testcases.calibration.page.CalibrationPointsRepeatablePage;
import com.ibm.maximo.technician.testcases.calibration.page.DatasheetListPage;
import com.ibm.maximo.technician.testcases.calibration.page.EnvironmentalConditionPage;
import com.ibm.maximo.technician.testcases.calibration.page.RemarksPage;
import com.ibm.maximo.technician.testcases.calibration.support.constants.BaseConstants;
import com.ibm.maximo.technician.testcases.calibration.support.constants.DatasheetConstants;
import com.ibm.maximo.technician.testcases.calibration.support.constants.ValidationConstants;
import com.ibm.maximo.technician.testcases.calibration.support.factories.DatasheetFactory;
import com.ibm.maximo.technician.testcases.calibration.support.objects.maximo.PluscDsInstr;

public class DatasheetTests extends TechnicianTest {

	private final Logger logger = LoggerFactory.getLogger(DatasheetTests.class);

	// Framework
	private AbstractAutomationFramework af;
	private MaximoApi maximoApi;
	private TestSuite testSuite;
	private Properties properties;

	// Traits
	private Path validationSetPath;
	
	// Target Datasheet data
	private String targetModel = null;
	private WorkOrder newWorkOrder;
	private String targetWonum = AbstractAutomationFramework.randomString(5).toUpperCase();

	// Additional Datasheet information
	private String assetDescription = "TestAuto Calibration Asset";
	private String assetNum = AbstractAutomationFramework.randomString(5).toUpperCase();
	private String datasheetName = "DEF_" + AbstractAutomationFramework.randomString(5).toUpperCase();
	private String labor = null;
	private String locationDescription = "INVALID_LOCATION_1";
	private String locationNum = AbstractAutomationFramework.randomString(5).toUpperCase();
	
	protected DatasheetFactory datasheetFactory = new DatasheetFactory(datasheetName);
	private boolean noAdjustmentTest = false;
	
	private boolean shouldLogBackIn = false;
	
	private Integer repeatValue;
	
	private List<PluscDsInstr> pluscDsInstrList;

	// Setup, login and navigate to datasheet page.
	public void setup(String configPath, String targetModel) throws Exception {
		setup(configPath, targetModel, true, true, true);
	}
	
	// Setup, optionally login, optionally update data and optionally navigate to datasheet page.
	public void setup(String configPath, String targetModel, boolean login, boolean updateData, boolean toDataSheetPage) throws Exception {
		logger.info("Setting up environment...");

		// Required: define datasheet model
		this.setTargetModel(targetModel);

		// Get instance of AutomationFramework class
		this.setAf(FrameworkFactory.get());

		// Load properties into class
		this.setProperties(configPath);

		// Reading from configuration properties
		String labor = properties.getProperty("system.username");
		String serverUrl = properties.getProperty("system.maximoServerUrl");
		String apiKey = properties.getProperty("system.maximoAPIKey");
		
		this.setLabor(labor);
		this.setMaximoApi(serverUrl, apiKey);
		
		// Populate environment
		this.initializeTestData();

		logger.info("Setup is finished.");

		if(login) {
			logger.info("Logging in...");
			this.login();
			//Update record data
			
			if(updateData)
				this.updateData();
			
			if(toDataSheetPage)
				this.navigateFromScheduleToDataSheetPage();
		}
	}

	public void teardown() throws Exception {
		this.logout();
		if(newWorkOrder != null) {
			logger.info("Attempt change work order '{}' status to completed", newWorkOrder.getWoNum());
			maximoApi.changeStatus(newWorkOrder, WoStatus.COMP.toString());
		}
		
		logger.info("Tearing down...");
		if (testSuite != null) {
			testSuite.teardown();
		}
		logger.info("Tearing down is finished.");
	}

	public void beforeMethod(String configPath, Method method) throws Exception {
		logger.info("******************** Test " + method.getName() + " is starting... *********************************");
		logger.info("Config Path: " + configPath);
		
		if(shouldLogBackIn)
		{
			shouldLogBackIn = false;
			this.logout();
			this.login();
			this.navigateFromScheduleToDataSheetPage();
		}
		
		if(this.noAdjustmentTest) {
			AssetFunctionPage assetFunctionPage = new AssetFunctionPage(this.af);
			if(assetFunctionPage.isNoAdjustmentBtnSelected())
				assetFunctionPage.clickNoAdjustmentBtn();
		}
	}

	public void afterMethod() throws Exception {
		logger.info("******************** Test is finished. *********************************");
		
		AssetFunctionPage assetFunctionPage = new AssetFunctionPage(af);
		if(!assetFunctionPage.isAssetFunctionPage())
			shouldLogBackIn = true;
	}

	public void initializeTestData() throws Exception {
		
		logger.info("Populating environment with data before test starts");
		
		Properties properties = this.getProperties();
		String assetNum = this.getAssetNum();
		String assetDescription = this.getAssetDescription();
		String locationNum = this.getLocationNum();
		String locationDescription = this.getLocationDescription();
		String targetWonum = this.getTargetWonum();
		String targetModel = this.getTargetModel();
		String datasheetName = this.getDatasheetName();
		String labor = this.getLabor();

		/** MAXMOA-6177 - Commenting the below part for location creation till further investigation
		 * since it is causing failure in FVT.
		*/  
		
		// ... Create a location ...
		// logger.info("Creating a location...");
		// Location newlocation = new Location();
		// newlocation.setLocationId(locationNum);
		// newlocation.setDescription(locationDescription);
		// logger.info(locationNum + " " + locationDescription);
		// newlocation.setSiteId(SetupData.SITEID);
		// assertEquals(maximoApi.create(newlocation), SetupData.CreatedSuccess);
		// logger.info("Location created: " + locationNum);

		// ... Create an asset ...
		logger.info("Creating an asset...");
		String assetResult = maximoApi.retrieve(new Asset(), "addid=1&internalvalues=1&action=system:new&addschema=1");
		Asset newAsset = new Gson().fromJson(assetResult, Asset.class);
		newAsset.setAssetNum(assetNum);
		newAsset.setDescription(assetDescription);
		// newAsset.setLocation(locationNum); // commented due to MAXMOA-6177
		newAsset.setIscalibration(true);
		newAsset.setSiteId(SetupData.SITEID);
		newAsset.setStatus(SetupData.LocAssetStatus.ACTIVE.toString());
		assertEquals(maximoApi.create(newAsset), SetupData.CreatedSuccess);
		logger.info("Asset created: " + assetNum);

		// ... Create work order ...
		logger.info("Creating Work Order...");
		String workOrderResult = maximoApi.retrieve(new WorkOrder(), "addid=1&internalvalues=1&action=system:new&addschema=1");
		newWorkOrder = new Gson().fromJson(workOrderResult, WorkOrder.class);
		newWorkOrder.setWoNum(targetWonum);
		newWorkOrder.setDescription("WorkeOrder for mobile calibration automation test");
		newWorkOrder.setSiteId(SetupData.SITEID);
		newWorkOrder.setAssetNum(assetNum);
		newWorkOrder.setWorkType(WorkType.PM.toString());
		//newWorkOrder.setLocation(locationNum); // commented due to MAXMOA-6177
		assertEquals(maximoApi.create(newWorkOrder), SetupData.CreatedSuccess);
		logger.info("Work Order created: " + targetWonum);

		// ... Create Datasheet ...
		logger.info("Creating Datasheet...");

		this.datasheetFactory.setEnableNoAdjustment(this.noAdjustmentTest);
		this.datasheetFactory
			.createDataSheet(newWorkOrder, labor, targetModel, jdbcConnection);
		setRepeatValue(datasheetFactory.getRepeatValue());
		setPluscDsInstrList(datasheetFactory.getPluscDsInstrList());
		
		logger.info("Datasheet created: "+ datasheetName);
		
		List<PlusCWODS> plusCWODSarr = new ArrayList<PlusCWODS>();
		plusCWODSarr.add(new PlusCWODS(datasheetName));
		
		logger.info(String.format("Assigning datasheet '%s' to work order '%s'. ", datasheetName, targetWonum));
		newWorkOrder.setPlusCWODS(plusCWODSarr);
		maximoApi.update(newWorkOrder);
		
		// Assignment labor to the WO
		logger.info(String.format("Assigning labor '%s' to work order '%s'. ", labor, targetWonum));
		maximoApi.addAssignmentLabor(newWorkOrder, labor);

		// Change WO status to Approved
		logger.info(String.format("Updating Work Order '%s' status to 'APPR'.", targetWonum));
		maximoApi.changeStatus(newWorkOrder, WoStatus.APPR.toString());
		
		logger.info("Finished populating environment with data.");
	}
	
	public void login() throws Exception {
		this.login(this.getAf());
	}
	
	public void logout() throws Exception {
		this.logOut(this.getAf());
	}

	/* ------------------------------------------------------------------ */
	/*                                                                    */
	/* Actions                                                            */
	/*                                                                    */
	/* ------------------------------------------------------------------ */
	
	protected void navigateFromCalpointToSchedule(String condition) throws Exception {
		
		AbstractAutomationFramework af = this.getAf();
		
		AsFoundValuesPage asFoundValuesPageObj = new AsFoundValuesPage(af);
		AsLeftValuesPage asLeftValuesPageObj = new AsLeftValuesPage(af);
		
		// Navigate back to Work order list page
		Reporter.log("Navigate back to origin");
		logger.info("Navigate back to origin");
		
		if (condition == DatasheetConstants.CONDITION_ASFOUND) {
			asFoundValuesPageObj.clickBackArrowBtn();
		} else if (condition == DatasheetConstants.CONDITION_ASLEFT) {
			asLeftValuesPageObj.clickBackArrowBtn();
		}
		
		navigateFromDataSheetPageToOrigin();
	}
	
	protected void navigateFromScheduleToDataSheetPage() throws Exception {
		AbstractAutomationFramework af = this.getAf();
		MySchedulePage mySchedulePage = new MySchedulePage(af);
		WorkOrderDetailsPage workOrderDetailsPage = new WorkOrderDetailsPage(af);
		DatasheetListPage dataSheetListPageObj = new DatasheetListPage(af);
		AssetFunctionPage assetFunctionPage = new AssetFunctionPage(af);

		// ... Step 3: Go to calibration function ...
		mySchedulePage.checkForUpdateButton();
		mySchedulePage.search(targetWonum);
		mySchedulePage.openCardDetails();
		workOrderDetailsPage.clickCalibrationIcon();
		
		String pageSubTitle = dataSheetListPageObj.getPageSubtitle();
		assertEquals(pageSubTitle, DatasheetListPage.datasheetListPageTitleName, "Verify Datasheet list page sub-title.");
		assertEquals(dataSheetListPageObj.getDatasheetName(), this.datasheetName, "Verify Datasheet Name.");
		dataSheetListPageObj.clickNavigationToDatasheet();
		assertEquals(assetFunctionPage.getPageTitle(), AssetFunctionPage.assetFunctionPageTitleName);
	}
	
	@SuppressWarnings("static-access")
	protected void navigateFromDataSheetPageToOrigin() throws Exception {
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage assignedWorkOrderDetailsPage = new WorkOrderDetailsPage(af);
		DatasheetListPage dataSheetListPageObj = new DatasheetListPage(af);
		AssetFunctionPage assetFunctionPage = new AssetFunctionPage(af);
		
		assetFunctionPage.clickBackArrowBtn();
		dataSheetListPageObj.clickBackArrowBtn();
		assignedWorkOrderDetailsPage.clickBackWOList();
		assignedWorkPage.clickClearButton();
	}

	protected void navigateFromScheduleToCalpoint(String targetAssetFunctionName, String condition) throws Exception {
		
		AbstractAutomationFramework af = this.getAf();
		AssetFunctionPage assetFunctionPage = new AssetFunctionPage(af);

		// ... Step 3: Go to calibration function ...
		navigateFromScheduleToDataSheetPage();
		
		if (condition == DatasheetConstants.CONDITION_ASFOUND) {
			assetFunctionPage.clickAssetFunctionAsFoundArrowBtn(targetAssetFunctionName);
		} else if (condition == DatasheetConstants.CONDITION_ASLEFT) {
			assetFunctionPage.clickAssetFunctionAsLeftArrowBtn(targetAssetFunctionName);
		}
	}
	
	public void saveAndVerifyEnvironmentalConditions() throws Exception {
		String envCondiTempValue = "25.0", envCondiHumidValue = "31.0", envCondiBaroPressureValue = "15.0";
		
		// Arranging target variables ...

		String targetModel = this.getTargetModel();
		String targetWonum = this.getTargetWonum();
		String targetDatasheetName = this.getDatasheetName();

		logger.info("Properties configured for this set:");
		logger.info(" * targetModel = " + targetModel);
		logger.info(" * targetWonum = " + targetWonum);
		logger.info(" * targetDatasheetName = " + targetDatasheetName);

		// Preparing Interface Handlers ...

		logger.info("Preparing Interface Handlers");
		AbstractAutomationFramework af = this.getAf();
		AssetFunctionPage dataSheetPageObj = new AssetFunctionPage(af);
		EnvironmentalConditionPage envCondiPageObj = new EnvironmentalConditionPage(af);
		
		// Navigate to Environmental Condition page
		logger.info("Navigate to Environmental Condition page.");
		dataSheetPageObj.clickEnvConditionSection();

		// Enter Temperature with random temperature unit
		String selectedTempUnit = envCondiPageObj.enterTemperatureWithRandomUnit(envCondiTempValue);
		// Enter Humidity with random unit
		String selectedHumidUnit = envCondiPageObj.enterHumidityWithRandomUnit(envCondiHumidValue);
		// Enter Barometric Pressure with random unit
		String selectedBarometricPressureUnit = envCondiPageObj.enterBaroPressureWithRandomUnit(envCondiBaroPressureValue);

		// Click Save button
		logger.info("Save the Environmental Conditions.");
		envCondiPageObj.clickSaveBtn();

		// Close Environmental Condition dialog page
		logger.info("Re-open Environmental Conditions dialog page.");
		dataSheetPageObj.clickEnvConditionSection();

		// Verify the data on Environmental Conditions dialog page after saving
		envCondiPageObj.verifySelectedInputAndUnits(envCondiTempValue, selectedTempUnit, 
				envCondiHumidValue, selectedHumidUnit, envCondiBaroPressureValue, selectedBarometricPressureUnit);
		
		takeScreenshot("Saved Environmental Condition values", af);

		envCondiPageObj.clickCloseBtn();
	}
	
	public void shouldHaveEnvironmentalConditions_whenDatasheetIsConfigured() throws Exception {
		AbstractAutomationFramework af = this.getAf();
		AssetFunctionPage dataSheetPageObj = new AssetFunctionPage(af);
		
		assertEquals(dataSheetPageObj.getEnvConditionSectionLabel(), "Environmental conditions",
				"Verify Environmental Condition section is present");
		
		logger.info("Verified Environmental Condition section is present. Datasheet Name: {}", this.getDatasheetName());
	}
	
	public void verifyNonRepetableNoAdjustmentConfig(boolean isDiscrete) throws Exception {
		if(!this.noAdjustmentTest)
			throw new SkipException("Skipping this test as No Adjustment was not configured!");
		
		AssetFunctionPage dataSheetPageObj = new AssetFunctionPage(af);
		AsFoundValuesPage asFoundValuesPageObj = new AsFoundValuesPage(af);
		AsLeftValuesPage asLeftValuesPageObj = new AsLeftValuesPage(af);
		asFoundValuesPageObj.setDiscreteType(isDiscrete);
		asLeftValuesPageObj.setDiscreteType(isDiscrete);
		
		String validationTestCase = ValidationConstants.TESTCASE_PASS;
		String validationSetPathStr = this.getValidationSetPath().toString();

		logger.info("No Adjustment for Asset Function");
		assertTrue(dataSheetPageObj.isNoAdjustmentBtnPresent(), "Verify No Adjustment button is present");
		
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

		// Navigate back to Asset Function page
		asFoundValuesPageObj.clickBackArrowBtn();
		
		// Verify the As Found Status
		assertEquals(dataSheetPageObj.getAsFoundStatus(), DatasheetConstants.STATUS_PASS, "Verify As Found status as PASS");

		// Click No Adjustment Button
		dataSheetPageObj.clickNoAdjustmentBtn();
		
		// Verify As left values copied
		logger.info("Verify as left values copied and read only");
		dataSheetPageObj.clickAsLeftNextArrowBtn();
		asLeftValuesPageObj.verifyNoAdjInlineNotification();
		asLeftValuesPageObj.verifyCalibrationPointValues(validationSetPathStr, validationTestCase, true);
		asLeftValuesPageObj.clickBackArrowBtn();

		logger.info("Verify as found values copied and read only");
		dataSheetPageObj.clickAsFoundNextArrowBtn();
		asFoundValuesPageObj.verifyNoAdjInlineNotification();
		asFoundValuesPageObj.verifyCalibrationPointValues(validationSetPathStr, validationTestCase, true);
		asFoundValuesPageObj.clickBackArrowBtn();
		
		// Click No Adjustment button again and verify as left values cleared
		logger.info("Click no adjustment again and verify as left values cleared");
		dataSheetPageObj.clickNoAdjustmentBtn();
		dataSheetPageObj.clickAsLeftNextArrowBtn();
		
		asLeftValuesPageObj.assertCalibrationPointsEmpty(validationSetPathStr, validationTestCase);
		asLeftValuesPageObj.clickBackArrowBtn();
		
		assertTrue(dataSheetPageObj.isNoAdjustmentBtnEnabled(), "Verify no adjustment is clickable");
		
		// Enter values for as left manually and check no adjustment disabled
		logger.info("Manually enter value for As Left");
		dataSheetPageObj.clickAsLeftNextArrowBtn();
		asLeftValuesPageObj.enterCalibrationPointValuesUptoLength(validationSetPathStr, validationTestCase, 1);
		asLeftValuesPageObj.saveAsLeftValues();
		asLeftValuesPageObj.clickBackArrowBtn();
		assertFalse(dataSheetPageObj.isNoAdjustmentBtnEnabled(), "Verify no adjustment is not clickable");
		
		logger.info("Clear entered As Left value");
		dataSheetPageObj.clickAsLeftNextArrowBtn();
		asLeftValuesPageObj.clearCalibrationPointValuesUptoLength(validationSetPathStr, validationTestCase, 1);
		asLeftValuesPageObj.saveAsLeftValues();
		asLeftValuesPageObj.clickBackArrowBtn();
		assertTrue(dataSheetPageObj.isNoAdjustmentBtnEnabled(), "Verify no adjustment is clickable");
		
		// get status other than PASS for As Found
		logger.info("Get FAIL status for As Found");
		dataSheetPageObj.clickAsFoundNextArrowBtn();
		asFoundValuesPageObj.enterCalibrationPointValues(validationSetPathStr, ValidationConstants.TESTCASE_FAIL_AS_FOUND);
		asFoundValuesPageObj.saveAsFoundValues();
		asFoundValuesPageObj.clickBackArrowBtn();
		assertFalse(dataSheetPageObj.isNoAdjustmentBtnEnabled(), "Verify no adjustment is not clickable");
		
	}
	
	public void verifyRepetableNoAdjustmentConfig() throws Exception {
		if(!this.noAdjustmentTest)
			throw new SkipException("Skipping this test as No Adjustment was not configured!");
		
		AssetFunctionPage dataSheetPageObj = new AssetFunctionPage(af);
		CalibrationPointsRepeatablePage asFoundValuesPageObj = new CalibrationPointsRepeatablePage(af);
		CalibrationPointsRepeatablePage asLeftValuesPageObj = new CalibrationPointsRepeatablePage(af);
		
		String validationTestCase = ValidationConstants.TESTCASE_PASS;
		String validationSetPathStr = this.getValidationSetPath().toString();
		
		logger.info("No Adjustment for Asset Function");
		assertTrue(dataSheetPageObj.isNoAdjustmentBtnPresent(), "Verify No Adjustment button is present");
		
		// Navigate to As found values page
		logger.info("Navigate to As Found value page");
		dataSheetPageObj.clickAsFoundNextArrowBtn();
		assertEquals(asFoundValuesPageObj.getPageTitle(), "As found values", "Verify As found values page title.");
		
		// Enter As found values
		logger.info("Enter Calibration Point values");
		Reporter.log("Enter Calibration Point values");
		asFoundValuesPageObj.enterRepeatableCalibrationPointValue(validationSetPathStr, validationTestCase, ValidationConstants.DEFAULT_REPEAT_VALUE);
		
		// Save As found values
		logger.info("Save As found values");
		Reporter.log("Save As found values");
		asFoundValuesPageObj.save();
		
		// Navigate back to Asset Function page
		asFoundValuesPageObj.goBack();
		
		// Verify the As Found Status
		assertEquals(dataSheetPageObj.getAsFoundStatus(), DatasheetConstants.STATUS_PASS, "Verify As Found status as PASS");
		
		// Click No Adjustment Button and veriy statuses
		dataSheetPageObj.clickNoAdjustmentBtn();
		
		// Click No Adjustment button again and verify as left status blank
		dataSheetPageObj.clickNoAdjustmentBtn();
		dataSheetPageObj.clickAsLeftNextArrowBtn();
		
		asLeftValuesPageObj.assertRepeatableCalPointsEmpty();
		asLeftValuesPageObj.goBack();
		
		assertTrue(dataSheetPageObj.isNoAdjustmentBtnEnabled(), "Verify no adjustment is clickable");
		
	}
	
	public void verifyRemarkColumnConfig() throws Exception {
		AbstractAutomationFramework af = this.getAf();
		AssetFunctionPage dataSheetPageObj = new AssetFunctionPage(af);
		RemarksPage remarksPageObj = new RemarksPage(af);
		
		String remark = "Remark for calibration automation test";
		String updatedRemark = "Updated remark for calibration automation test";

		assertEquals(dataSheetPageObj.getRemarksSectionLabel(), "Remarks");
		
		dataSheetPageObj.clickRemarksSection();
		remarksPageObj.validateHeader();
		
		remarksPageObj.enterAndValidateRemark(remark);
		dataSheetPageObj.clickRemarksSection();
		remarksPageObj.enterAndValidateRemark(updatedRemark);
		dataSheetPageObj.clickRemarksSection();
		
		remarksPageObj.clearAndValidateRemarkEmpty();
	}
	
	/* ------------------------------------------------------------------ */
	/*                                                                    */
	/* Getters and Setters                                                */
	/*                                                                    */
	/* ------------------------------------------------------------------ */

	protected AbstractAutomationFramework getAf() {
		return af;
	}

	protected void setAf(AbstractAutomationFramework af) {
		this.af = af;
	}

	protected void setLabor(String labor) {
		this.labor = labor;
	}
	
	protected void setMaximoApi(String serverUrl, String apiKey) {
		MaximoApi maximoApi = new MaximoApi();
		maximoApi.setMaximoServer(serverUrl, apiKey);
		this.maximoApi = maximoApi;
	}

	protected void setProperties(String configPath) throws IOException {
		InputStream inputStream = new BufferedInputStream(new FileInputStream(configPath));
		Properties properties = new Properties();
		properties.load(inputStream);
		this.properties = properties;
	}

	protected Properties getProperties() {
		return this.properties;
	}
	
	protected String getLocationDescription() {
		return locationDescription;
	}

	protected void setLocationDescription(String locationDescription) {
		this.locationDescription = locationDescription;
	}
	
	protected String getLocationNum() {
		return this.locationNum;
	}

	protected void setLocationNum(String locationNum) {
		this.locationNum = locationNum;
	}

	protected String getAssetNum() {
		return assetNum;
	}

	protected void setAssetNum(String assetNum) {
		this.assetNum = assetNum;
	}

	private String getAssetDescription() {
		return assetDescription;
	}

	protected void setAssetDescription(String assetDescription) {
		this.assetDescription = assetDescription;
	}

	protected String getTargetWonum() {
		return targetWonum;
	}

	protected void setTargetWonum(String targetWonum) {
		this.targetWonum = targetWonum;
	}

	protected String getDatasheetName() {
		return datasheetName;
	}
	
	protected void setDatasheetName(String datasheetName) {
		this.datasheetName = datasheetName;
	}

	protected String getTargetModel() {
		return targetModel;
	}

	protected void setTargetModel(String targetModel) {
		this.targetModel = targetModel;
		// Load Datasheet Validation Set
		// Should generate path "./src/test/java/com/ibm/maximo/technician/testcases/calibration/support/data/datasheets/EUAWO/EUAWO--datasheet.validation.json"
		Path validationSetPath = Paths.get(BaseConstants.SUPPORT_DATA_DATASHEETS_PATH + "/" + targetModel + "/" + targetModel + "-datasheet.validation.json");
		this.setValidationSetPath(validationSetPath);
	}

	protected String getLabor() {
		return labor;
	}
	
	protected Path getValidationSetPath() {
		return validationSetPath;
	}

	protected void setValidationSetPath(Path validationSetPath) {
		this.validationSetPath = validationSetPath;
	}

	public boolean isNoAdjustmentTest() {
		return noAdjustmentTest;
	}

	public void setNoAdjustmentTest(boolean noAdjustmentTest) {
		this.noAdjustmentTest = noAdjustmentTest;
	}

	public Integer getRepeatValue() {
		return repeatValue;
	}

	public void setRepeatValue(Integer repeatValue) {
		this.repeatValue = repeatValue;
	}

	public List<PluscDsInstr> getPluscDsInstrList() {
		return pluscDsInstrList;
	}

	public void setPluscDsInstrList(List<PluscDsInstr> pluscDsInstrList) {
		this.pluscDsInstrList = pluscDsInstrList;
	}
	
}
