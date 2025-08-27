package com.ibm.maximo.technician.testcases.calibration.tests;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.ibm.maximo.automation.mobile.AbstractAutomationFramework;
import com.ibm.maximo.technician.testcases.calibration.page.AssetFunctionPage;
import com.ibm.maximo.technician.testcases.calibration.page.CalibrationBaseTest;
import com.ibm.maximo.technician.testcases.calibration.page.DatasheetListPage;
import com.ibm.maximo.technician.testcases.calibration.support.constants.DatasheetConstants;
import com.ibm.maximo.technician.testcases.calibration.support.constants.TimeoutConstants;

public class MULTIASSETDatasheetTests extends DatasheetTests {

	private final Logger logger = LoggerFactory.getLogger(MULTIASSETDatasheetTests.class);
	private String[] nonCalculatedStatuses = {
		DatasheetConstants.STATUS_EMPTY,
		DatasheetConstants.STATUS_MISSING,
		DatasheetConstants.STATUS_BROKEN
	};

	@BeforeClass(alwaysRun = true)
	@Parameters({ "configPath" })
	public void setup(String configPath) throws Exception {
		super.setup(configPath, DatasheetConstants.MODEL_MULTIASSET);
		
		String targetModel = this.getTargetModel();
		String targetWonum = this.getTargetWonum();
		String targetDatasheetName = this.getDatasheetName();
		
		logger.info("Properties configured for this test:");
		logger.info(" * targetModel = " + targetModel);
		logger.info(" * targetWonum = " + targetWonum);
		logger.info(" * targetDatasheetName = " + targetDatasheetName);
	}

	@AfterClass(alwaysRun = true)
	public void teardown() throws Exception {
		super.teardown();
	}

	@BeforeMethod(alwaysRun = true)
	@Parameters({ "configPath" })
	public void beforeMethod(String configPath, Method method) throws Exception {
		super.beforeMethod(configPath, method);
	}

	@AfterMethod(alwaysRun = true)
	public void afterMethod() throws Exception {
		super.afterMethod();
	}
	
	@Test(groups = { "mobile" }, description = "Should calculate Tolerance Bounds when passing testcase is provided in AsFound Page", timeOut = TimeoutConstants.ONE_HOUR_WAIT)
	public void A_shouldHaveMultipleAssetsVisible_whenSelectingDatasheet() throws Exception {

		Reporter.log("Test Case: Should have multiple assets visible when selecting datasheet");
		logger.info("Test Case: Should have multiple assets visible when selecting datasheet");

		// ... Step 1: Arranging target variables ...
		String targetModel = this.getTargetModel();
		String targetWonum = this.getTargetWonum();
		String targetDatasheetName = this.getDatasheetName();

		logger.info("Properties configured for this set:");
		logger.info(" * targetModel = " + targetModel);
		logger.info(" * targetWonum = " + targetWonum);
		logger.info(" * targetDatasheetName = " + targetDatasheetName);

		// ... Step 2: Preparing Interface Handlers ...
		Reporter.log("Preparing Interface Handlers");
		logger.info("Preparing Interface Handlers");

		AbstractAutomationFramework af = this.getAf();
		AssetFunctionPage assetFunctionPage = new AssetFunctionPage(af);

		Reporter.log("Should have datasheet name in the title");
		takeScreenshot(targetModel + "_step_datasheet_list", af);

		// ... Step 4: Navigate to Asset Functions (assetfunctions.xml) ...
		Reporter.log("Navigate to Asset Functions List page");
		logger.info("Navigate to Asset Functions List page");

		String assetFunctiontitle = assetFunctionPage.getPageTitle();

		// Assert: Should have 'Analog Asset Function' with 5 points
		// Assert: Should have 'Discrete Asset' with 2 points
		boolean isSUMIWOAfPresent = assetFunctionPage.isAssetFunctionPresent(targetModel + " SUMIWO Asset Function");
		boolean isSUMNSFAfPresent = assetFunctionPage.isAssetFunctionPresent(targetModel + " SUMNSF Asset Function");
		boolean isDiscreteAfPresent = assetFunctionPage
				.isAssetFunctionPresent(targetModel + " Discrete Asset Function");

		Reporter.log("Is SUMIWO Asset Function present on screen? " + (isSUMIWOAfPresent ? "YES" : "NO"));
		Reporter.log("Is SUMNSF Asset Function present on screen? " + (isSUMNSFAfPresent ? "YES" : "NO"));
		Reporter.log("Is Discrete Asset Function present on screen? " + (isDiscreteAfPresent ? "YES" : "NO"));
		takeScreenshot(targetModel + "_step4_assetfunctions_list", af);

		assertEquals(assetFunctiontitle, AssetFunctionPage.assetFunctionPageTitleName);
		assertTrue(isSUMIWOAfPresent);
		assertTrue(isSUMNSFAfPresent);
		assertTrue(isDiscreteAfPresent);

		logger.info("Done!");
	}
	
	@Test(groups = { "mobile" }, description = "Should calculate Tolerance Bounds when passing testcase is provided in AsFound Page", timeOut = TimeoutConstants.ONE_HOUR_WAIT)
	public void check_StatusRollUp_inDatasheet() throws Exception {
		String[] assetFunctionTypes = {DatasheetConstants.MODEL_SUMIWO, DatasheetConstants.MODEL_SUMNSF, "Discrete"};
		
		// Scenario 1
		logger.info("**Check asset rollup scenario 1 - combination of BROKEN and MISSING**");
		retrieveStatusForAssetFunctionConditions(assetFunctionTypes[0], DatasheetConstants.STATUS_BROKEN, DatasheetConstants.STATUS_BROKEN);
		retrieveStatusForAssetFunctionConditions(assetFunctionTypes[1], DatasheetConstants.STATUS_BROKEN, DatasheetConstants.STATUS_BROKEN);
		retrieveStatusForAssetFunctionConditions(assetFunctionTypes[2], DatasheetConstants.STATUS_MISSING, DatasheetConstants.STATUS_BROKEN);
		Reporter.log("**Check asset rollup scenario 1 - combination of BROKEN and MISSING**");
		takeScreenshot("AF_Status_1", this.getAf());
		// assert statuses
		assertStatusForDatasheet(DatasheetConstants.STATUS_MISSING, DatasheetConstants.STATUS_BROKEN);
		
		// Reset third AF 
		retrieveStatusForAssetFunctionConditions(assetFunctionTypes[2], DatasheetConstants.STATUS_EMPTY, DatasheetConstants.STATUS_EMPTY);
		assertStatusForDatasheet(DatasheetConstants.STATUS_BROKEN, DatasheetConstants.STATUS_BROKEN);
		
		// Scenario 2
		logger.info("**Check asset rollup scenario 2 - combination with BROKEN and calculated statuses**");
		retrieveStatusForAssetFunctionConditions(assetFunctionTypes[0], DatasheetConstants.STATUS_BROKEN, DatasheetConstants.STATUS_PASS);
		retrieveStatusForAssetFunctionConditions(assetFunctionTypes[1], DatasheetConstants.STATUS_ACTION, DatasheetConstants.STATUS_PASS);
		retrieveStatusForAssetFunctionConditions(assetFunctionTypes[2], DatasheetConstants.STATUS_PASS, DatasheetConstants.STATUS_FAIL);
		Reporter.log("**Check asset rollup scenario 2 - combination with BROKEN and calculated statuses**");
		takeScreenshot("AF_Status_2", this.getAf());
		// assert statuses
		assertStatusForDatasheet(DatasheetConstants.STATUS_BROKEN, DatasheetConstants.STATUS_FAIL);
		
		// Scenario 3
		logger.info("**Check asset rollup scenario 3 - combination with calculated statuses**");
		retrieveStatusForAssetFunctionConditions(assetFunctionTypes[0], DatasheetConstants.STATUS_LIMITEDUSE, DatasheetConstants.STATUS_ACTION);
		retrieveStatusForAssetFunctionConditions(assetFunctionTypes[1], DatasheetConstants.STATUS_ADJREQD, DatasheetConstants.STATUS_PASS);
		retrieveStatusForAssetFunctionConditions(assetFunctionTypes[2], DatasheetConstants.STATUS_PASS, DatasheetConstants.STATUS_PASS);
		Reporter.log("**Check asset rollup scenario 3 - combination with calculated statuses**");
		takeScreenshot("AF_Status_3", this.getAf());
		// assert statuses
		assertStatusForDatasheet(DatasheetConstants.STATUS_LIMITEDUSE, DatasheetConstants.STATUS_ACTION);
	}
	
	/**
	 * 
	 * @param condition - AsFound or AsLeft
	 * @param assetFunctionType - Model type of Asset function e.g. SUMIWO, SUMNSF
	 * @param status - status to select. If it's the empty status, will reset the status.
	 * 
	 * @throws Exception
	 */
	public void selectStatusForAssetFunction(String condition, String assetFunctionType, String status) throws Exception {
		AbstractAutomationFramework af = this.getAf();
		AssetFunctionPage assetFunctionPage = new AssetFunctionPage(af);
		
		boolean resetStatus = (status == DatasheetConstants.STATUS_EMPTY);
		
		String assetFunctionName = String.format("%s %s Asset Function", this.getTargetModel(), assetFunctionType);
		
		String currentAssetFunctionStatus = assetFunctionPage.getAssetFunctionStatus(condition, assetFunctionName);
		logger.info("Asset function: " + assetFunctionName);	
		
		if(currentAssetFunctionStatus.equals(status)) {
			logger.info("Current asset function status is already as required");	
			return;
		}
		
		if(!resetStatus) {
			logger.info(String.format("Selecting asset function status %s for condition %s", status, condition));
			assetFunctionPage.selectAssetFunctionStatus(condition, assetFunctionName, status);
		} else {
			logger.info(String.format("Resetting asset function status for condition %s", condition));
			assetFunctionPage.resetAssetFunctionStatus(condition, assetFunctionName);
		}
		
		assertEquals(assetFunctionPage.getAssetFunctionStatus(condition, assetFunctionName), status, "Verify status selection successful");
	}
	
	/**
	 * 
	 * @param condition - AsFound or AsLeft
	 * @param assetFunctionType - Model type of Asset function e.g. SUMIWO, SUMNSF
	 * @param assetFunctionStatus - status to retrieve
	 * @throws Exception
	 */
	public void retrieveStatusForAssetFunction(String condition, String assetFunctionType, String assetFunctionStatus) throws Exception {
		logger.info("**Get status {} for {}**", assetFunctionStatus, assetFunctionType);
		Reporter.log(String.format("**Get status %s for %s**", assetFunctionStatus, assetFunctionType));
		
		if(Arrays.asList(nonCalculatedStatuses).contains(assetFunctionStatus)) {
			selectStatusForAssetFunction(condition, assetFunctionType, assetFunctionStatus);
			return;
		}
		
		String assetFunctionName = String.format("%s %s Asset Function", this.getTargetModel(), assetFunctionType);
		
		String validationSetPathStr = this.getValidationSetPath().toString();
		
		String assetFuncValidationTestCase = assetFunctionType + "_" + assetFunctionStatus;

		AbstractAutomationFramework af = this.getAf();
		AssetFunctionPage assetFunctionPage = new AssetFunctionPage(af);
		
		String currentAssetFunctionStatus = assetFunctionPage.getAssetFunctionStatus(condition, assetFunctionName);
		if(currentAssetFunctionStatus.equals(assetFunctionStatus)) {
			logger.info("Current asset function status is already as required");
			return;
		}
		
		// currently we have a non calculated status, then enter all points in order to change status
		boolean enterAllPoints = (Arrays.asList(nonCalculatedStatuses).contains(currentAssetFunctionStatus));
		
		assetFunctionPage.clickAssetFunctionNextArrowBtn(condition, assetFunctionName);
		
		Reporter.log("Enter Calibration Point values");
		logger.info("Enter Calibration Point values");

		CalibrationBaseTest pageObj = CalibrationBaseTest.getPageObj(af, condition);
		pageObj.setDiscreteType( assetFunctionName.contains("Discrete") );
		
		pageObj.enterCalibrationPointValuesUptoLength(validationSetPathStr, assetFuncValidationTestCase, enterAllPoints ? -1 : 1);
		pageObj.saveValues();
		pageObj.clickBackArrowBtn();
		
		assertEquals(assetFunctionPage.getAssetFunctionStatus(condition, assetFunctionName), assetFunctionStatus);
		
		assetFunctionPage.clickAssetFunctionNextArrowBtn(condition, assetFunctionName);
		
		pageObj.verifyTolerances(validationSetPathStr, assetFuncValidationTestCase, condition);

		pageObj.clickBackArrowBtn();

		logger.info("Done!");	
	}
	
	public void retrieveStatusForAssetFunctionConditions(String assetFunctionType, String statusAsFound, String statusAsLeft) throws Exception {
		retrieveStatusForAssetFunction(DatasheetConstants.CONDITION_ASFOUND, assetFunctionType, statusAsFound);
		retrieveStatusForAssetFunction(DatasheetConstants.CONDITION_ASLEFT, assetFunctionType, statusAsLeft);
	}
	
	public void assertStatusForDatasheet(String statusAsFound, String statusAsLeft) throws Exception {
		AbstractAutomationFramework af = this.getAf();
		DatasheetListPage datasheetListPage = new DatasheetListPage(af);
		AssetFunctionPage assetFunctionPage = new AssetFunctionPage(af);
		
		assetFunctionPage.clickBackArrowBtn();
		assertEquals(datasheetListPage.getAsFoundStatus(), statusAsFound);
		assertEquals(datasheetListPage.getAsLeftStatus(), statusAsLeft);
		takeScreenshot(String.format("DS_Status_%s_%s", statusAsFound, statusAsLeft), this.getAf());
		datasheetListPage.clickNavigationToDatasheet();
	}
}
