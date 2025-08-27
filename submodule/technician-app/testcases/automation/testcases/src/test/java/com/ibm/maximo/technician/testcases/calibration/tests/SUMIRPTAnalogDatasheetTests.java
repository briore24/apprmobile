package com.ibm.maximo.technician.testcases.calibration.tests;

import java.lang.reflect.Method;

import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.ibm.maximo.technician.testcases.calibration.support.constants.DatasheetConstants;
import com.ibm.maximo.technician.testcases.calibration.support.constants.TimeoutConstants;
import com.ibm.maximo.technician.testcases.calibration.support.constants.ValidationConstants;

public class SUMIRPTAnalogDatasheetTests extends RepeatableAnalogDatasheetTests {
	
	@BeforeClass(alwaysRun = true)
	@Parameters({ "configPath" })
	public void setup(String configPath) throws Exception {
		this.setNoAdjustmentTest(true);
		
		super.setup(
			configPath,
			DatasheetConstants.MODEL_SUMIRPT
		);
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

	@Test(groups = {"mobile"}, description = "Verify No Adjustment for Repeatable page", timeOut = TimeoutConstants.ONE_HOUR_WAIT, priority = 1)
	public void verifyNoAdjustmentConfig() throws Exception {
		super.verifyRepetableNoAdjustmentConfig();
	}
	
	@Test(groups = {"mobile"}, description = "Should calculate Tolerance Bounds when passing testcase is provided in AsFound Page", timeOut = TimeoutConstants.ONE_HOUR_WAIT, priority = 2)
	public void shouldCalculateToleranceBounds_whenPassingTestcaseIsProvided_inAsFoundPage() throws Exception {
		this.calculateToleranceBoundsWithValidationTestcase(
			DatasheetConstants.CONDITION_ASFOUND,
			DatasheetConstants.STATUS_PASS,
			ValidationConstants.TESTCASE_PASS,
			ValidationConstants.IGNORE_TOL_ERROR,
			this.getValidationSetPath()
		);
	}

	@Test(groups = {"mobile"}, description = "Should calculate Tolerance Errors when failing test case is provided in AsFound Page", timeOut = TimeoutConstants.ONE_HOUR_WAIT, priority = 3)
	public void shouldCalculateToleranceBounds_whenPassingTestcaseIsProvided_inAsLeftPage() throws Exception {
		this.calculateToleranceBoundsWithValidationTestcase(
			DatasheetConstants.CONDITION_ASLEFT,
			DatasheetConstants.STATUS_PASS,
			ValidationConstants.TESTCASE_PASS,
			ValidationConstants.IGNORE_TOL_ERROR,
			this.getValidationSetPath()
		);
	}
}
