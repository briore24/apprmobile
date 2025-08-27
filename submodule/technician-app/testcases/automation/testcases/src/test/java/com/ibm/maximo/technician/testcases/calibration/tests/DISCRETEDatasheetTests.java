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

public class DISCRETEDatasheetTests extends DatasheetTests {
	
	@BeforeClass(alwaysRun = true)
	@Parameters({ "configPath" })
	public void setup(String configPath) throws Exception {
		this.setNoAdjustmentTest(true);
		
		super.setup(
			configPath,
			DatasheetConstants.MODEL_DISCRETE
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
		super.verifyNonRepetableNoAdjustmentConfig(true);
	}
	
}
