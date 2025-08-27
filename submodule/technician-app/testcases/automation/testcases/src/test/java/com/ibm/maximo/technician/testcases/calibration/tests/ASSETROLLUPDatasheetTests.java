package com.ibm.maximo.technician.testcases.calibration.tests;

import static org.testng.Assert.assertEquals;

import java.lang.reflect.Method;

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
import com.ibm.maximo.technician.testcases.calibration.support.constants.DatasheetConstants;

public class ASSETROLLUPDatasheetTests extends DatasheetTests {

	private final Logger logger = LoggerFactory.getLogger(ASSETROLLUPDatasheetTests.class);

	@BeforeClass(alwaysRun = true)
	@Parameters({ "configPath" })
	public void setup(String configPath) throws Exception {
		super.setup(configPath, DatasheetConstants.MODEL_ASSETROLLUP);
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

	@Test(groups = {"mobile"}, description = "Verify asset functions listed", timeOut = 100 * 1000)
	@Parameters({ "configPath" })
	public void verifyAssetFunctionsPresent(String configPath) throws Exception {
		Reporter.log("Test Case: Should have multiple assets visible when selecting datasheet");
		logger.info("Test Case: Should have multiple assets visible when selecting datasheet");

		String targetModel = this.getTargetModel();
		String targetWonum = this.getTargetWonum();
		String targetDatasheetName = this.getDatasheetName();

		logger.info("Properties configured for this set:");
		logger.info(" * targetModel = " + targetModel);
		logger.info(" * targetWonum = " + targetWonum);
		logger.info(" * targetDatasheetName = " + targetDatasheetName);

		Reporter.log("Preparing Interface Handlers");
		logger.info("Preparing Interface Handlers");

		AbstractAutomationFramework af = this.getAf();
		AssetFunctionPage assetFunctionPage = new AssetFunctionPage(af);
		
		assertEquals(assetFunctionPage.getPageTitle(), AssetFunctionPage.assetFunctionPageTitleName);
		
		String[] assetFunctionsList = {
				DatasheetConstants.MODEL_EUIWO1,
				"Discrete",
				DatasheetConstants.MODEL_SUMNSF,
				DatasheetConstants.MODEL_URVOWO,
				DatasheetConstants.MODEL_SUMIWOOPP
			};
		
		assetFunctionPage.assertAssetFunctionsPresent("", assetFunctionsList, " Asset Function");
		
		takeScreenshot(targetModel + "asset_functions_list", af);
		
		logger.info("Done!");
	}
}
