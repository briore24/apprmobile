package com.ibm.maximo.technician.testcases.calibration.tests;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.ibm.maximo.automation.mobile.AbstractAutomationFramework;
import com.ibm.maximo.automation.mobile.FrameworkFactory;
import com.ibm.maximo.automation.mobile.MobileAutomationFramework;
import com.ibm.maximo.technician.framework.TechnicianTest;
import com.ibm.maximo.technician.page.MySchedulePage;
import com.ibm.maximo.technician.page.WorkOrderDetailsPage;
import com.ibm.maximo.technician.testcases.TestSuite;

/*
 * GRAPHITE-50896: Keep the WO number visible after pressing the "Edit" button inside a Work Order on the App
 */

public class ViewCalWorkOrderTest extends TechnicianTest {
	private final Logger logger = LoggerFactory.getLogger(ViewCalWorkOrderTest.class);
	private AbstractAutomationFramework af;
	private TestSuite testSuite;

	@BeforeClass(alwaysRun = true)
	@Parameters({ "configPath" })
	public void setup(String configPath) throws Exception {
		logger.info("********************View Calibrated Work Order Test*********************************");		
		this.af = FrameworkFactory.get();
		login(af);
	}

	@AfterClass(alwaysRun = true)
	public void teardown() throws Exception {
		logOut(af);
		if (testSuite != null) {
			testSuite.teardown();
		}
	}

	@Test(groups = {
			"mobile" }, description = "Verify user is able to view the details of Calibrated Work Order", timeOut = 500000)
	@Parameters({"calibrationWONum"})
	public void viewCalWorkOrderDetails(String calibrationWONum) throws Exception {
		MobileAutomationFramework maf = (MobileAutomationFramework) this.af;
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage assignedWorkOrderDetailsPage = new WorkOrderDetailsPage(af);
		
		logger.info("Calibreation WO Num : " + calibrationWONum);
		Reporter.log("Calibreation WO Num : " + calibrationWONum);
		
		assertTrue(assignedWorkPage.search(calibrationWONum));

		// Click on Chevron Icon on Work Order
		assignedWorkPage.openWorkOrderDetails();

		// click on Edit Work Order
		assignedWorkOrderDetailsPage.editWorkOrderDetails();
		// verify Work Order page title value
		String dynamicTitle = assignedWorkOrderDetailsPage.getTitle(assignedWorkOrderDetailsPage.woEditPageTitle);
		String expectedTitle = calibrationWONum + " Edit work order";
		assertEquals(expectedTitle, dynamicTitle);
		assignedWorkOrderDetailsPage.clickButton(assignedWorkOrderDetailsPage.workOrderBreadcrumb);
		
		maf.scrollPage(1, 2);
		
		assertTrue(assignedWorkOrderDetailsPage.isCalibrationIconPresent());
		logger.info("Is Calibration Icon present? : " + assignedWorkOrderDetailsPage.isCalibrationIconPresent());
		Reporter.log("Is Calibration Icon present? : " + assignedWorkOrderDetailsPage.isCalibrationIconPresent());
		assertTrue(assignedWorkOrderDetailsPage.isCalibreationIconBadgePresent());
		logger.info("Is Badge present on Calibration Icon? : " + assignedWorkOrderDetailsPage.isCalibreationIconBadgePresent());
		Reporter.log("Is Badge present on Calibration Icon? : " + assignedWorkOrderDetailsPage.isCalibreationIconBadgePresent());
		logger.info("Calibration Badge Value : " + assignedWorkOrderDetailsPage.getCalibrationBadgeValue());
		Reporter.log("Calibration Badge Value : " + assignedWorkOrderDetailsPage.getCalibrationBadgeValue());
		
	}
}
