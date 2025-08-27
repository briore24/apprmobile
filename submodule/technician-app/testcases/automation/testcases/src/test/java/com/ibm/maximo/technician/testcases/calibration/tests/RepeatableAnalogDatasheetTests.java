package com.ibm.maximo.technician.testcases.calibration.tests;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

import java.nio.file.Path;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Reporter;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

import com.ibm.maximo.automation.mobile.AbstractAutomationFramework;
import com.ibm.maximo.technician.testcases.calibration.page.AssetFunctionPage;
import com.ibm.maximo.technician.testcases.calibration.page.CalibrationPointsRepeatablePage;
import com.ibm.maximo.technician.testcases.calibration.support.constants.DatasheetConstants;
import com.ibm.maximo.technician.testcases.calibration.support.constants.ValidationConstants;
import com.ibm.maximo.technician.testcases.calibration.support.objects.maximo.PluscDsInstr;

public class RepeatableAnalogDatasheetTests extends DatasheetTests {

	private final Logger logger = LoggerFactory.getLogger(RepeatableAnalogDatasheetTests.class);

	@Ignore
	@Test(groups = {"mobile"}, description = "Should have Environmental Conditions when Datasheet is configured", timeOut = 9500000)
	public void shouldHaveEnvironmentalConditions_whenDatasheetIsConfigured() throws Exception {
		// TODO: GRAPHITE-78045 test: implement test case
	}
	
	/* ------------------------------------------------------------------ */
	/*                                                                    */
	/* Supporting methods                                                 */
	/*                                                                    */
	/* ------------------------------------------------------------------ */

	@SuppressWarnings({ "static-access" })
	public void calculateToleranceBoundsWithValidationTestcase(
		String condition,
		String expectedStatus,
		String validationTestCase,
		Boolean ignoreTolError,
		Path validationSetPath
	) throws Exception {

		logger.info("Test: Calculate Tolerance Bounds for Repeatable Datasheet with Validation Set");
		Reporter.log("Test: Calculate tolerance Bounds for Repeatable Datasheet with Validation Set");
		
		// ... Step 1: Arranging target variables ...

		String targetModel = this.getTargetModel();
		String targetWonum = this.getTargetWonum();
		String targetDatasheetName = this.getDatasheetName();

		logger.info("Properties configured for this set:");
		logger.info(" * targetModel = " + targetModel);
		logger.info(" * targetWonum = " + targetWonum);
		logger.info(" * targetDatasheetName = " + targetDatasheetName);
		logger.info(" * condition = " + condition);
		logger.info(" * expectedStatus = " + expectedStatus);
		logger.info(" * validationTestCase = " + validationTestCase);
		logger.info(" * ignoreTolError = " + ignoreTolError);
		logger.info(" * validationSetPath = " + validationSetPath);

		// ... Step 2: Preparing Interface Handlers ...
		
		logger.info("Preparing Interface Handlers");
		AbstractAutomationFramework af = this.getAf();
		AssetFunctionPage dataSheetPageObj = new AssetFunctionPage(af);
		CalibrationPointsRepeatablePage repeatablePage = new CalibrationPointsRepeatablePage(af); 
		
		// ... Step 3: From starting page, navigate to calibration page ...

		String pageTitle = dataSheetPageObj.getPageTitle();
		assertEquals(pageTitle, AssetFunctionPage.assetFunctionPageTitleName, "Verify Asset Functions List page title.");

		boolean valuesEntered = false;
		logger.info("Navigate to calibration page");
		if (condition == DatasheetConstants.CONDITION_ASFOUND) {
			valuesEntered = dataSheetPageObj.getAsFoundStatus().equals(expectedStatus);
			dataSheetPageObj.clickAsFoundNextArrowBtn();
			assertEquals(
				repeatablePage.getPageTitle(),
				DatasheetConstants.PAGE_TITLE_ASFOUND,
				String.format(
					"Page title '%s' should be equal to '%s'.",
					repeatablePage.getPageTitle(),
					DatasheetConstants.PAGE_TITLE_ASFOUND
				)
			);
		} else {
			valuesEntered = dataSheetPageObj.getAsLeftStatus().equals(expectedStatus);
			dataSheetPageObj.clickAsLeftNextArrowBtn();
			assertEquals(
				repeatablePage.getPageTitle(),
				DatasheetConstants.PAGE_TITLE_ASLEFT,
				String.format(
					"Page title '%s' should be equal to '%s'.",
					repeatablePage.getPageTitle(),
					DatasheetConstants.PAGE_TITLE_ASLEFT
				)
			);
		}

		// ... Step 4: Enter Calibration Point Values based on Validation Set ...

		if(!valuesEntered) {
			Reporter.log("Enter Calibration Point values");
			if (condition == DatasheetConstants.CONDITION_ASFOUND) {
				repeatablePage.enterRepeatableCalibrationPointValue(
						/* path= */ validationSetPath.toString(),
						/* testDataType= */ validationTestCase,
						/* repeatablePointValue= */ getRepeatValue() == null ? ValidationConstants.DEFAULT_REPEAT_VALUE : String.valueOf(getRepeatValue())
						);
			} else {
				repeatablePage.enterValuesAsLeft(
						/* path= */ validationSetPath.toString(),
						/* testDataType= */ validationTestCase,
						/* repeatablePointValue= */ getRepeatValue() == null ? ValidationConstants.DEFAULT_REPEAT_VALUE : String.valueOf(getRepeatValue())
						);
			}
			Reporter.log("Save values");
			repeatablePage.save();
		} else {
			logger.info("Calibration point values already entered by previous test. Going back");
		}

		// ... Step 5: Assert datasheet status ...

		// Navigate to Asset Function page
		repeatablePage.goBack();
		
		/** Validate no-adjustment state
		 * If as left values were entered manually OR
		 * As found status is not PASS
		 */
		if(this.isNoAdjustmentTest()) {
			if(condition == DatasheetConstants.CONDITION_ASLEFT
				|| !dataSheetPageObj.getAsFoundStatus().equals(ValidationConstants.STATUS_PASS))
				assertFalse(dataSheetPageObj.isNoAdjustmentBtnEnabled());
		}

		// Verify the Status
		String datsheetStatus = condition == DatasheetConstants.CONDITION_ASFOUND
			? dataSheetPageObj.getAsFoundStatus()
			: dataSheetPageObj.getAsLeftStatus();

		assertEquals(
			datsheetStatus,
			expectedStatus,
			String.format(
				"Datasheet status '%s' should be equal to '%s'.",
				datsheetStatus,
				expectedStatus
			)
		);

		takeScreenshot(targetModel + "_" + condition + "_status", af);
		
		// ... Step 6: Verify standard deviation limits if applicable
		dataSheetPageObj.assertStandardDeviationLimit(repeatablePage, dataSheetPageObj, getPluscDsInstrList());
		
		// ... Step 7: Assert tolerance bounds are calculated according to Validation Set ...

		logger.info("Verify Tolerances");
		Reporter.log("Verify Tolerances");
		
		if (condition == DatasheetConstants.CONDITION_ASFOUND) {
			dataSheetPageObj.clickAsFoundNextArrowBtn();
		} else {
			dataSheetPageObj.clickAsLeftNextArrowBtn();
		}

		repeatablePage.validateTolerances(validationSetPath.toString(), validationTestCase);
		takeScreenshot("tolerance_verification", af);
		
		// ... Step 7: Navigate back to origin ...

		// Navigate back to Work order list page
		logger.info("Navigate back to work order list");
		Reporter.log("Navigate back to work order list");
		repeatablePage.goBack();

		logger.info("Test is finished.");
	}
	
}
