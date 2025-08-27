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

public class EUIRPTSTDDEVAnalogDatasheetTests extends RepeatableAnalogDatasheetTests{
	@BeforeClass(alwaysRun = true)
	@Parameters({ "configPath" })
	public void setup(String configPath) throws Exception {
		super.setup(
			configPath,
			DatasheetConstants.MODEL_EUIRPTSTDDEV
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

	@Test(groups = {"mobile"}, description = "Should calculate Tolerance Bounds and standard deviation when passing testcase is provided in AsFound Page", timeOut = TimeoutConstants.ONE_HOUR_WAIT)
	public void shouldCalculateToleranceBoundsAndStdDev_whenPassingTestcaseIsProvided_inAsFoundPage() throws Exception {
		this.calculateToleranceBoundsWithValidationTestcase(
			DatasheetConstants.CONDITION_ASFOUND,
			DatasheetConstants.STATUS_PASS,
			ValidationConstants.TESTCASE_PASS,
			ValidationConstants.IGNORE_TOL_ERROR,
			this.getValidationSetPath()
		);
	}
	
	@Test(groups = {"mobile"}, description = "Should calculate Tolerance Bounds and standard deviation when passing testcase is provided in AsLeft Page", timeOut = TimeoutConstants.ONE_HOUR_WAIT)
	public void shouldCalculateToleranceBoundsAndStdDev_whenPassingTestcaseIsProvided_inAsLeftPage() throws Exception {
		this.calculateToleranceBoundsWithValidationTestcase(
			/* condition */ DatasheetConstants.CONDITION_ASLEFT,
			/* expectedStatus */ DatasheetConstants.STATUS_PASS,
			/* validationTestCase */ ValidationConstants.TESTCASE_PASS,
			/* validationTestCase */ ValidationConstants.IGNORE_TOL_ERROR,
			/* ignoreTolError */ this.getValidationSetPath()
		);
	}

	@Test(groups = {"mobile"}, description = "Should calculate Tolerance Errors and standard deviation when failing test case is provided in AsFound Page", timeOut = TimeoutConstants.ONE_HOUR_WAIT)
	public void shouldCalculateToleranceErrorsAndStdDev_whenFailingTestcaseIsProvided_inAsFoundPage() throws Exception {
		this.calculateToleranceBoundsWithValidationTestcase(
			/* condition */ DatasheetConstants.CONDITION_ASFOUND,
			/* expectedStatus */ DatasheetConstants.STATUS_ADJREQD,
			/* validationTestCase */ ValidationConstants.TESTCASE_FAIL_AS_FOUND,
			/* validationTestCase */ ValidationConstants.NOT_IGNORE_TOL_ERROR,
			/* ignoreTolError */ this.getValidationSetPath()
		);
	}

	@Test(groups = {"mobile"}, description = "Should calculate Tolerance Errors and standard deviation when failing test case is provided in AsLeft Page", timeOut = TimeoutConstants.ONE_HOUR_WAIT)
	public void shouldCalculateToleranceErrorsAndStdDev_whenFailingTestcaseIsProvided_inAsLeftPage() throws Exception {
		this.calculateToleranceBoundsWithValidationTestcase(
			/* condition */ DatasheetConstants.CONDITION_ASLEFT,
			/* expectedStatus */ DatasheetConstants.STATUS_ADJREQD,
			/* validationTestCase */ ValidationConstants.TESTCASE_FAIL_AS_LEFT,
			/* validationTestCase */ ValidationConstants.NOT_IGNORE_TOL_ERROR,
			/* ignoreTolError */ this.getValidationSetPath()
		);
	}
	
	
	@Test(groups = {"mobile"}, description = "Should calculate Tolerance Errors and standard deviation when failing test case is provided in AsFound Page  - Fail status because of standard deviation error", timeOut = TimeoutConstants.ONE_HOUR_WAIT)
	public void shouldCalculateToleranceErrorsAndStdDev_whenFailingTestcaseIsProvided_inAsFoundPage_StdDevError() throws Exception {
		this.calculateToleranceBoundsWithValidationTestcase(
			/* condition */ DatasheetConstants.CONDITION_ASFOUND,
			/* expectedStatus */ DatasheetConstants.STATUS_FAIL,
			/* validationTestCase */ ValidationConstants.TESTCASE_FAIL_AS_FOUND_STD_DEV,
			/* validationTestCase */ ValidationConstants.NOT_IGNORE_TOL_ERROR,
			/* ignoreTolError */ this.getValidationSetPath()
		);
	}
	
	
	@Test(groups = {"mobile"}, description = "Should calculate Tolerance Errors and standard deviation when failing test case is provided in AsLeft Page - Fail status because of standard deviation error", timeOut = TimeoutConstants.ONE_HOUR_WAIT)
	public void shouldCalculateToleranceErrorsAndStdDev_whenFailingTestcaseIsProvided_inAsLeftPage_StdDevError() throws Exception {
		this.calculateToleranceBoundsWithValidationTestcase(
			/* condition */ DatasheetConstants.CONDITION_ASLEFT,
			/* expectedStatus */ DatasheetConstants.STATUS_FAIL,
			/* validationTestCase */ ValidationConstants.TESTCASE_FAIL_AS_LEFT_STD_DEV,
			/* validationTestCase */ ValidationConstants.NOT_IGNORE_TOL_ERROR,
			/* ignoreTolError */ this.getValidationSetPath()
		);
	}

}
