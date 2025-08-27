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

public class SUMIWOAnalogDatasheetTests extends AnalogDatasheetTests {
	
	@BeforeClass(alwaysRun = true)
	@Parameters({ "configPath" })
	public void setup(String configPath) throws Exception {
		this.setNoAdjustmentTest(true);
		
		super.setup(
			configPath,
			DatasheetConstants.MODEL_SUMIWO
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
	
	@Test(groups = {"mobile"}, description = "Verify no adjustment feature", timeOut = TimeoutConstants.ONE_HOUR_WAIT, priority = 1)
	public void verifyNoAdjustmentConfig() throws Exception {
		super.verifyNonRepetableNoAdjustmentConfig(false);
	}
	
	
	@Test(groups = {"mobile"}, description = "Should calculate Tolerance Bounds when passing testcase is provided in AsFound Page", timeOut = TimeoutConstants.ONE_HOUR_WAIT, priority = 2)
	public void shouldCalculateToleranceBounds_whenPassingTestcaseIsProvided_inAsFoundPage() throws Exception {
		this.calculateToleranceBoundsWithValidationTestcase(
			/* condition */ DatasheetConstants.CONDITION_ASFOUND,
			/* expectedStatus */ DatasheetConstants.STATUS_PASS,
			/* validationTestCase */ ValidationConstants.TESTCASE_PASS,
			/* validationTestCase */ ValidationConstants.IGNORE_TOL_ERROR,
			/* ignoreTolError */ this.getValidationSetPath()
		);
	}

	@Test(groups = {"mobile"}, description = "Should calculate Tolerance Bounds when passing testcase is provided in AsLeft Page", timeOut = TimeoutConstants.ONE_HOUR_WAIT, priority = 3)
	public void shouldCalculateToleranceBounds_whenPassingTestcaseIsProvided_inAsLeftPage() throws Exception {
		this.calculateToleranceBoundsWithValidationTestcase(
			/* condition */ DatasheetConstants.CONDITION_ASLEFT,
			/* expectedStatus */ DatasheetConstants.STATUS_PASS,
			/* validationTestCase */ ValidationConstants.TESTCASE_PASS,
			/* validationTestCase */ ValidationConstants.IGNORE_TOL_ERROR,
			/* ignoreTolError */ this.getValidationSetPath()
		);
	}

	@Test(groups = {"mobile"}, description = "Should calculate Tolerance Errors when failing test case is provided in AsFound Page", timeOut = TimeoutConstants.ONE_HOUR_WAIT, priority = 4)
	public void shouldCalculateToleranceErrors_whenFailingTestcaseIsProvided_inAsFoundPage() throws Exception {
		this.calculateToleranceBoundsWithValidationTestcase(
			/* condition */ DatasheetConstants.CONDITION_ASFOUND,
			/* expectedStatus */ DatasheetConstants.STATUS_ACTION,
			/* validationTestCase */ ValidationConstants.TESTCASE_FAIL_AS_FOUND,
			/* validationTestCase */ ValidationConstants.NOT_IGNORE_TOL_ERROR,
			/* ignoreTolError */ this.getValidationSetPath()
		);
	}

	@Test(enabled = false, groups = {"mobile"}, description = "Should calculate Tolerance Errors when failing test case is provided in AsLeft Page", timeOut = TimeoutConstants.ONE_HOUR_WAIT, priority = 5)
	public void shouldCalculateToleranceErrors_whenFailingTestcaseIsProvided_inAsLeftPage() throws Exception {
		this.calculateToleranceBoundsWithValidationTestcase(
			/* condition */ DatasheetConstants.CONDITION_ASLEFT,
			/* expectedStatus */ DatasheetConstants.STATUS_ACTION,
			/* validationTestCase */ ValidationConstants.TESTCASE_FAIL_AS_LEFT,
			/* validationTestCase */ ValidationConstants.NOT_IGNORE_TOL_ERROR,
			/* ignoreTolError */ this.getValidationSetPath()
		);
	}
	
	@Test(enabled = false, groups = {"mobile"}, description = "Verify tolerance warning and errors displayed in as found page", timeOut = TimeoutConstants.ONE_HOUR_WAIT, priority = 6)
	public void shouldDisplayErrorsAndWarnings_inAsFoundPage() throws Exception {
		this.verifyToleranceWarningsAndErrors(
			/* condition */ DatasheetConstants.CONDITION_ASFOUND,
			/* ignoreTolError */ this.getValidationSetPath(),
			/* enter all points? */ false
		);
	}
	
	@Test(enabled = false, groups = {"mobile"}, description = "Verify tolerance warning and errors displayed in as left page", timeOut = TimeoutConstants.ONE_HOUR_WAIT, priority = 7)
	public void shouldDisplayErrorsAndWarnings_inAsLeftPage() throws Exception {
		this.verifyToleranceWarningsAndErrors(
			/* condition */ DatasheetConstants.CONDITION_ASLEFT,
			/* ignoreTolError */ this.getValidationSetPath(),
			/* enter all points? */ false
		);
	}
	
	@Test(groups = {"mobile"}, description = "Save and verify Environmental conditions", timeOut = 9500000, priority = 8)
	public void saveAndVerifyEnvironmentalConditions() throws Exception {
		super.saveAndVerifyEnvironmentalConditions();
	}
	
	@Test(groups = {"mobile"}, description = "Save and verify remarks column", timeOut = TimeoutConstants.ONE_HOUR_WAIT, priority = 9)
	public void verifyRemarkColumnConfig() throws Exception {
		super.verifyRemarkColumnConfig();
	}
}
