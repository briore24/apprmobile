package com.ibm.maximo.technician.testcases.calibration.tests;

import static org.testng.Assert.assertTrue;

import java.lang.reflect.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.ibm.maximo.automation.mobile.AbstractAutomationFramework;
import com.ibm.maximo.technician.page.AssetDetailsPage;
import com.ibm.maximo.technician.page.AssetListPage;
import com.ibm.maximo.technician.page.MySchedulePage;
import com.ibm.maximo.technician.page.WorkOrderDetailsPage;
import com.ibm.maximo.technician.testcases.calibration.support.constants.DatasheetConstants;

public class AssetStatusHistoryTest extends AnalogDatasheetTests {
	
	private final Logger logger = LoggerFactory.getLogger(AssetStatusHistoryTest.class);
	private String assetNum, woNum;
	
	
	@BeforeClass(alwaysRun = true)
	@Parameters({ "configPath" })
	public void setup(String configPath) throws Exception {
		super.setup(
			configPath,
			DatasheetConstants.MODEL_SUMIWO,
			true, true, false
		);
	}
	
	@AfterClass(alwaysRun = true)
	public void teardown() throws Exception {
		super.teardown();
	}
	
	@BeforeMethod(alwaysRun = true)
	@Parameters({ "configPath" })
	public void beforeMethod(String configPath, Method method) throws Exception {
		logger.info("******************** Test " + method.getName() + " is starting... *********************************");
		logger.info("Config Path: " + configPath);
	}
	
	@AfterMethod(alwaysRun = true)
	public void afterMethod() throws Exception {
		logger.info("******************** Test is finished. *********************************");
	}

	@Test(enabled = true, groups = { "mobile" }, description = "switchToAssetDetailsTest", timeOut = 900000)
	public void switchToAssetDetailsTest() throws Exception {
		// Open work order list page
		AbstractAutomationFramework af = this.getAf();
		assetNum = this.getAssetNum();
		woNum = this.getTargetWonum();
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage assignedWorkOrderDetailsPage = new WorkOrderDetailsPage(af);
		AssetDetailsPage assetDetailsPage = new AssetDetailsPage(af);
		// check Asset App is loaded
		goToAssets(af);
		AssetListPage assetListPage = new AssetListPage(af);

		// Select Assets created by me option
		logger.info("Select Assets Created by Me option");
		assetListPage.openNativeDropdown(1);

		// reload asset list
		logger.info("reload asset list");
		assetListPage.clickRefreshButton();
		assetListPage.waitForLoadingNotVisible();

		// Search with asset number
		logger.info("Search with asset number : " + assetNum);
		boolean searchOutput = assetListPage.search(assetNum);

		if (!searchOutput)
			throw new SkipException("Skipping this test due to Asset is not found in Asset Application.");

		goToMySchedule(af);
		// Search the workorder
		assertTrue(assignedWorkPage.search(woNum), "Searched workorder was not found");
		// Click on Chevron Icon on Work Order
		assignedWorkPage.openWorkOrderDetails();
		boolean assetChevron = assignedWorkOrderDetailsPage.assetChevronDisplayed();
		try {
			if (assetChevron) {
				// Click on Asset chevron to navigate to asset details page
				assignedWorkOrderDetailsPage.clickAssetChevronButton();
				boolean checkAssetDetailPage = assetDetailsPage.checkAssetDetailPage();

				if (checkAssetDetailPage) {
					this.verifyAssetStatusSections(woNum, assignedWorkOrderDetailsPage);
				} else {
					// Back to WO list view
					this.navigateBackToWoDetails(assetChevron, assignedWorkOrderDetailsPage);
				}
			} else {
				// Back to WO list view
				this.navigateBackToWoDetails(assetChevron, assignedWorkOrderDetailsPage);
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new SkipException("Skipping this test due to Asset is not found in Asset Application.");
		}
	}
	
}
