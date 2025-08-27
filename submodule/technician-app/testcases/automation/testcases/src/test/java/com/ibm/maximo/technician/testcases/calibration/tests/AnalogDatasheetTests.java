package com.ibm.maximo.technician.testcases.calibration.tests;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.nio.file.Path;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Reporter;
import org.testng.SkipException;

import com.ibm.maximo.automation.mobile.AbstractAutomationFramework;
import com.ibm.maximo.technician.page.WorkOrderDetailsPage;
import com.ibm.maximo.technician.testcases.calibration.page.AsFoundValuesPage;
import com.ibm.maximo.technician.testcases.calibration.page.AsLeftValuesPage;
import com.ibm.maximo.technician.testcases.calibration.page.AssetFunctionPage;
import com.ibm.maximo.technician.testcases.calibration.page.CalibrationBaseTest;
import com.ibm.maximo.technician.testcases.calibration.support.constants.DatasheetConstants;
import com.ibm.maximo.technician.testcases.calibration.support.constants.ValidationConstants;
import com.ibm.maximo.technician.page.AssetDetailsPage;

public class AnalogDatasheetTests extends DatasheetTests {

	private final Logger logger = LoggerFactory.getLogger(AnalogDatasheetTests.class);
	
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

		logger.info("(begin) Calculate tolerance Bounds with Validation Set");
		Reporter.log("(begin) Calculate tolerance Bounds with Validation Set");
		
		// ... Step 1: Arranging target variables ...

		AbstractAutomationFramework af = this.getAf();
		AssetFunctionPage dataSheetPageObj = new AssetFunctionPage(af);
		AsFoundValuesPage asFoundValuesPageObj = new AsFoundValuesPage(af);
		AsLeftValuesPage asLeftValuesPageObj = new AsLeftValuesPage(af);
		
		String targetModel = this.getTargetModel();
		String targetWonum = this.getTargetWonum();
		String targetDatasheetName = this.getDatasheetName();

		logger.info("Properties configured for this set:");
		logger.info(" * targetModel = " + targetModel);
		logger.info(" * targetWonum = " + targetWonum);
		logger.info(" * targetDatasheetName = " + targetDatasheetName);

		String pageTitle = dataSheetPageObj.getPageTitle();
		assertEquals(pageTitle, AssetFunctionPage.assetFunctionPageTitleName, "Verify Asset Functions List page title.");
		
		logger.info("Navigate to calibration page");
		if (condition == DatasheetConstants.CONDITION_ASFOUND) {
			
			dataSheetPageObj.clickAsFoundNextArrowBtn();

			String calibrationPageTitle = asFoundValuesPageObj.getPageTitle();
			assertEquals(calibrationPageTitle, AsFoundValuesPage.asFoundValuesPageTitleName, "Verify page title.");

			// ... Step 4: Enter Calibration Point Values based on Validation Set ...
			logger.info("Enter Calibration Point values");
			Reporter.log("Enter Calibration Point values");	
			asFoundValuesPageObj.enterCalibrationPointValues(validationSetPath.toString(), validationTestCase);

			//.. Step 5: check if add calibration point is available or not.
			logger.info("Add calibration point tile is available");
			Reporter.log("Add calibration point tile is available");	
			asFoundValuesPageObj.checkAddCalpointAvailable();
			

			logger.info("Save values");
			Reporter.log("Save values");
			asFoundValuesPageObj.saveAsFoundValues();

			// ... Step 6: Assert datasheet status ...
			
			// Navigate to Asset Function page
			asFoundValuesPageObj.clickBackArrowBtn();
	
			// Verify the Status
			String datsheetStatus = dataSheetPageObj.getAsFoundStatus();
			assertEquals(datsheetStatus, expectedStatus , "Verify status as " + expectedStatus);
			takeScreenshot(targetModel + "_" + condition + "_status", af);
			
			// ... Step 7: Assert tolerance bounds are calculated according to Validation Set ...
	
			logger.info("Verify Tolerances");
			Reporter.log("Verify Tolerances");
			dataSheetPageObj.clickAsFoundNextArrowBtn();
			asFoundValuesPageObj.validateTolerances(validationSetPath.toString(), validationTestCase, condition);
			takeScreenshot("Tolerance_verification", af);
			
			// ... Step 8: Navigate back to origin ...
	
			// Navigate back to Work order list page
			logger.info("Navigate back to origin");
			Reporter.log("Navigate back to origin");
			
			asFoundValuesPageObj.clickBackArrowBtn();

		} else if (condition == DatasheetConstants.CONDITION_ASLEFT) {
			
			dataSheetPageObj.clickAsLeftNextArrowBtn();

			String calibrationPageTitle = asLeftValuesPageObj.getPageTitle();
			assertEquals(calibrationPageTitle, AsLeftValuesPage.asLeftValuePageTitleName, "Verify page title.");

			// ... Step 4: Enter Calibration Point Values based on Validation Set ...
			logger.info("Enter Calibration Point values");
			Reporter.log("Enter Calibration Point values");	
			asLeftValuesPageObj.enterCalibrationPointValues(validationSetPath.toString(), validationTestCase);

			//.. Step 5: check if add calibration point is available or not.
			logger.info("Add calibration point tile is available");
			Reporter.log("Add calibration point tile is available");	
			asFoundValuesPageObj.checkAddCalpointAvailable();

			logger.info("Save values");
			Reporter.log("Save values");
			asLeftValuesPageObj.saveAsLeftValues();

			// ... Step 6: Assert datasheet status ...

			// Navigate to Asset Function page
			asLeftValuesPageObj.clickBackArrowBtn();

			// Verify the Status
			String datsheetStatus = dataSheetPageObj.getAsLeftStatus();
			assertEquals(datsheetStatus, expectedStatus , "Verify status as " + expectedStatus);
			takeScreenshot(targetModel + "_" + condition + "_status", af);

			// ... Step 7: Assert tolerance bounds are calculated according to Validation Set ...

			logger.info("Verify Tolerances");
			Reporter.log("Verify Tolerances");
			dataSheetPageObj.clickAsLeftNextArrowBtn();
			asLeftValuesPageObj.validateTolerances(validationSetPath.toString(), validationTestCase, condition);
			takeScreenshot("Tolerance_verification", af);

			// ... Step 8: Navigate back to origin ...

			// Navigate back to Work order list page
			logger.info("Navigate back to origin");
			Reporter.log("Navigate back to origin");
			
			asLeftValuesPageObj.clickBackArrowBtn();

		} else { 

			Boolean isValidCondition = Arrays.asList(DatasheetConstants.CONDITIONS).contains(condition); 
			assertTrue(isValidCondition, "Should have condition defined as either As Found or As Left.");

		}
		
		logger.info("(end) Calculate tolerance Bounds with Validation Set");
	}
	
	public void verifyToleranceWarningsAndErrors(
			String condition,
			Path validationSetPath,
			boolean enterAllPoints
	)	throws Exception {
		logger.info("(begin) Verify tolerance warnings and errors test");
		Reporter.log("(begin) Verify tolerance warnings and errors test");
		
		Boolean isValidCondition = Arrays.asList(DatasheetConstants.CONDITIONS).contains(condition); 
		assertTrue(isValidCondition, "Should have condition defined as either As Found or As Left.");
		
		// ... Step 1: Arranging target variables ...

		AbstractAutomationFramework af = this.getAf();
		AssetFunctionPage dataSheetPageObj = new AssetFunctionPage(af);
		
		CalibrationBaseTest calPointsPageObj = condition.equals(DatasheetConstants.CONDITION_ASFOUND) 
				? new AsFoundValuesPage(af) : new AsLeftValuesPage(af);
		
		String targetModel = this.getTargetModel();
		String targetWonum = this.getTargetWonum();
		String targetDatasheetName = this.getDatasheetName();

		logger.info("Properties configured for this set:");
		logger.info(" * targetModel = " + targetModel);
		logger.info(" * targetWonum = " + targetWonum);
		logger.info(" * targetDatasheetName = " + targetDatasheetName);

		String pageTitle = dataSheetPageObj.getPageTitle();
		assertEquals(pageTitle, AssetFunctionPage.assetFunctionPageTitleName, "Verify Asset Functions List page title.");

		// ... Step 2: Navigate to calibration page ...
		
		logger.info("Navigate to calibration page");
		
		if(condition == DatasheetConstants.CONDITION_ASFOUND) 
			dataSheetPageObj.clickAsFoundNextArrowBtn();
		else
			dataSheetPageObj.clickAsLeftNextArrowBtn();
		
		// ... Step 3: Enter data that will violate tolerance and verify warning message
		logger.info("Enter data to violate tolerance limits...");
		calPointsPageObj.enterCalibrationPointValuesUptoLength(
				validationSetPath.toString(), ValidationConstants.TESTCASE_OUT_OF_TOLERANCE, enterAllPoints ? -1 : 1
			);

		assertTrue(calPointsPageObj.verifyToleranceExceedMessage(condition));
		assertTrue(calPointsPageObj.verifyToleranceExceedIcon(condition));

		takeScreenshot(targetModel + "_Tolerance_Exceeded_Warning", af);

		// ... Step 4: Enter data that will violate input range and verify error message
		logger.info("Enter data to violate input range...");
		calPointsPageObj.enterCalibrationPointValuesUptoLength(
				validationSetPath.toString(), ValidationConstants.TESTCASE_OUT_OF_RANGE, enterAllPoints ? -1 : 1
			);

		assertTrue(calPointsPageObj.verifyInputOutOfRangeMessage());
		takeScreenshot(targetModel + "_Out_Of_Range_Message", af);

		// ... Step 5: Attempt to save and verify that it fails
		logger.info("Attempt to save (should fail!)...");
		if(calPointsPageObj instanceof AsFoundValuesPage) {
			assertFalse(((AsFoundValuesPage) calPointsPageObj).saveAndReturnStatus(false, 5), "Verify save failed");
			((AsFoundValuesPage) calPointsPageObj).clickBackArrowBtn();
		}
		else {
			assertFalse(((AsLeftValuesPage) calPointsPageObj).saveAndReturnStatus(false, 5), "Verify save failed");
			((AsLeftValuesPage) calPointsPageObj).clickBackArrowBtn();
		}
		
		calPointsPageObj.clickDiscardSaveBtn(condition);
		
		logger.info("(end) Verify tolerance warnings and errors test");
	}
	
	public void verifyCalibrationSections(String woNum, WorkOrderDetailsPage assignedWorkOrderDetailsPage) {
		AbstractAutomationFramework af = this.getAf();
		AssetDetailsPage assetDetailsPage = new AssetDetailsPage(af);
		logger.info("calibration history present");
		try {
			assertEquals(assetDetailsPage.isCalibrationHistoryPresent(), true);

			logger.info("Navigate to calibration history page.");
			assetDetailsPage.clickCalibrationHistoryTile();

			assertEquals(assetDetailsPage.getCalibrationHisWoListSectionLabel(), "Calibration history",
					"Verify Calibration History section is present");
			takeScreenshot("Calibration history", af);
			assertEquals(assetDetailsPage.getWorkorderList(), woNum, "Verify workorder list is displayed.");
			assetDetailsPage.clickWoListItem();
			takeScreenshot("Calibration details", af);
			assertEquals(assetDetailsPage.getCalibrationDetailsSectionLabel(), "Calibration details",
					"Verify Calibration details section is present");
			// Navigate back to tech app to workorder details page
			assetDetailsPage.clickButton(assetDetailsPage.calibrationDetailBreadcrumb);
			assetDetailsPage.clickButton(assetDetailsPage.calibrationHistoryCloseButton);
			assetDetailsPage.clickButton(assetDetailsPage.assetDetailBreadcrumb);
			logger.info("Navigation back to work order details page in Technician app done correctly.");
			// Back to WO list view
			assignedWorkOrderDetailsPage.clickBackChevronToListPage();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new SkipException("Skipping this test due to Asset is not found in Asset Application.");
		}
	}
	
	public void verifyAssetStatusSections(String woNum, WorkOrderDetailsPage assignedWorkOrderDetailsPage) {
		AbstractAutomationFramework af = this.getAf();
		AssetDetailsPage assetDetailsPage = new AssetDetailsPage(af);
		logger.info("Asset status history present");
		try {
			assertEquals(assetDetailsPage.isAssetStatusHistoryPresent(), true);

			logger.info("Navigate to Asset status history page.");
			assetDetailsPage.clickAssetStatusHistoryTile();

			assertEquals(assetDetailsPage.getAssetStatusHisWoListSectionLabel(), "Asset status history",
					"Verify Asset status History section is present");
			takeScreenshot("Asset status history", af);
			//assertEquals(assetDetailsPage.getWorkorderList(), woNum, "Verify workorder list is displayed.");
			assetDetailsPage.clickAssetStatusListItem();
			takeScreenshot("Asset status details", af);
			assertEquals(assetDetailsPage.getAssetStatusDetailsSectionLabel(), "Asset status details",
					"Verify Asset status details section is present");
			// Navigate back to tech app to workorder details page
			assetDetailsPage.clickButton(assetDetailsPage.assetStatusDetailBreadcrumb);
			assetDetailsPage.clickButton(assetDetailsPage.assetStatusHistoryCloseButton);
			assetDetailsPage.clickButton(assetDetailsPage.assetDetailBreadcrumb);
			logger.info("Navigation back to work order details page in Technician app done correctly.");
			// Back to WO list view
			assignedWorkOrderDetailsPage.clickBackChevronToListPage();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new SkipException("Skipping this test due to Asset is not found in Asset Application.");
		}
	}
	
	public void navigateBackToWoDetails(boolean assetChevron, WorkOrderDetailsPage assignedWorkOrderDetailsPage) {
		logger.info("asset is not available, going back to WO list page");
		try {
			assignedWorkOrderDetailsPage.clickBackChevronToListPage();
			assertTrue(assetChevron, "asset Chevron is not Displayed");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new SkipException("Skipping this test due to Asset is not found in Asset Application.");
		}
	}
	
	
}
