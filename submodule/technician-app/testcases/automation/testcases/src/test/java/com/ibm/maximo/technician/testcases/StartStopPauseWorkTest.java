package com.ibm.maximo.technician.testcases;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.ibm.maximo.automation.mobile.AbstractAutomationFramework;
import com.ibm.maximo.automation.mobile.FrameworkFactory;
import com.ibm.maximo.automation.mobile.api.MaximoApi;
import com.ibm.maximo.automation.mobile.api.json.Asset;
import com.ibm.maximo.automation.mobile.api.json.WorkOrder;
import com.ibm.maximo.technician.framework.TechnicianTest;
import com.ibm.maximo.technician.page.MySchedulePage;
import com.ibm.maximo.technician.page.ReportWorkPage;
import com.ibm.maximo.technician.page.WorkOrderDetailsPage;
import com.google.gson.Gson;
import com.ibm.maximo.technician.setupdata.SetupData;
import com.ibm.maximo.technician.setupdata.SetupData.*;

/*
 * GRAPHITE-69630: [TECHMOBILE] Start Pause Stop Work WO List And Details
 *  GRAPHITE-65556 : TECHMOBILE] Start Pause Stop Work WO List And Details (Part 1)
 *  GRAPHITE-72436:[TECHMOBILE] Start Pause Stop Work WO List And Details:38M,0TA,1A
 *  MAXMOA-3823:[TECHMOBILE]Enable Technician to Delete their Timer
 */

public class StartStopPauseWorkTest extends TechnicianTest {
	private final Logger logger = LoggerFactory.getLogger(StartStopPauseWorkTest.class);
	private AbstractAutomationFramework af;
	private TestSuite testSuite;
	private MaximoApi maximoApi;
	private WorkOrder newWorkOrder,newWorkOrder2,newWorkOrder1;
	private String assetNum, woNum, labor, header, reportHeader,labor2,assetNum1,woNum1;
	String expectedDetailsPageTitle = "Work order";
	String expectedWOStatusDetailsPage = "In progress";
	String expectedWOStatusListPage = "Approved";
	private static final String ASSET_DESCRIPTION = "Asset for mobile automation test";
	private static final String WO_DESCRIPTION = "WorkOrder for mobile automation test";

	@BeforeClass(alwaysRun = true)
	@Parameters({ "configPath" })
	public void setup(String configPath) throws Exception {
		logger.info("********************StartStopPauseWorkTest*********************************");
		this.af = FrameworkFactory.get();
		Properties properties = new Properties();
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(configPath));
			properties.load(in);
			labor = properties.getProperty("system.username");
			labor2 = properties.getProperty("app.username1");
			maximoApi = new MaximoApi();
			maximoApi.setMaximoServer(properties.getProperty("system.maximoServerUrl"),
					properties.getProperty("system.maximoAPIKey"));
			createDefaultObjects();
			createDefaultObjectsForMultipleLabor();
		} catch (Exception e) {
			e.printStackTrace();
		}
		login(af);
	}

	@AfterClass(alwaysRun = true)
	public void teardown() throws Exception {
		logOut(af);
		logger.info("Changing work order status to COMP");
		maximoApi.changeStatus(newWorkOrder, WoStatus.COMP.toString());
		maximoApi.changeStatus(newWorkOrder1, WoStatus.COMP.toString());
		maximoApi.changeStatus(newWorkOrder2, WoStatus.COMP.toString());
		updateSystemPropertiesForMultiplelabor("1");
		updateSystemProperties("0");
		if (testSuite != null) {
			testSuite.teardown();
		}
	}
	@Test(groups = { "mobile" }, description = "Verify user is able to view delete button after starting the work order", timeOut = 900000)
	public void startWorkOrderLabor() throws Exception {

		// Open work order list page
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage woDetailsPage = new WorkOrderDetailsPage(af);
		ReportWorkPage reportWork = new ReportWorkPage(af);
	
		// Search the work order
		assertTrue(assignedWorkPage.search(woNum), "Fail : Search Work Order is not displayed");
		// Click on selected workorder to open Work Order Details Page
		assignedWorkPage.openWorkOrderDetails();
		// Check if redirected to Work Order Details Page
		assertEquals("Work order", woDetailsPage.getInfo());
		woDetailsPage.acceptWorkorderonDetailsPage();

		// Navigate to Report work page
		woDetailsPage.clickReportWorkButton();
		// Verify there is no labor transaction present in report work page
		assertEquals("No labor time reported.",reportWork.getNoLaborText());
		// Navigate back to WO details page
		reportWork.clickBackChevron();
		// Click start work button
		woDetailsPage.clickStartWorkButton();
		logger.info("start work button clicked ");
		// Navigate to Report work page
		woDetailsPage.clickReportWorkButton();
		// Click on edit icon of the transaction 
		reportWork.clickEditIcon();
		logger.info("edit button clicked");
		// Click on the delete icon
		reportWork.clickDeleteIcon();
		logger.info("delete button clicked ");
		// Verify no transaction is saved after clicking delete icon
		assertEquals("No labor time reported.",reportWork.getNoLaborText());
		// Click on back chevron and navigate to WO details page
		reportWork.clickBackChevron();
		// Verify after clicking delete button only start WO button displays
		assertTrue(woDetailsPage.StartWorkTimerButtonIcon(), "Start work timer button is displayed");
		// Click on start Work order 
		woDetailsPage.clickStartWorkButton();
		// Click Pause button
		woDetailsPage.clickPauseBUtton();
		// Verify the header text
		assertEquals("Labor approval?", woDetailsPage.getLaborApprovalText());
		// Click on delete icon
		woDetailsPage.deleteButtonLaborApprovalPage();
		// Verify stop and pause work orders are not visible and start work order is displayed
		assertTrue(woDetailsPage.StartWorkTimerButtonIcon(), "Start work timer button is displayed");
	}
	
	@Test(groups = {
			"priority2" }, description = " Verify that Stop and Pause button are displayed when technician taps on Start button on work order list and work order details pages", timeOut = 90000000)
	public void scenario1() throws Exception {

		// Open work order list page
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage woDetailsPage = new WorkOrderDetailsPage(af);
		// Search the work order
		assertTrue(assignedWorkPage.search(woNum), "Fail : Search Work Order is not displayed");
		// Verify status of Work order is Approved
		assertEquals(expectedWOStatusListPage, assignedWorkPage.verifyChangedStatus());
		// Verify start work button exists
		assignedWorkPage.isStartWorkButtonExists();
		// Click on start work button
		assignedWorkPage.clickStartWorkButton();
		// Check Barcode Scanner
		if (woDetailsPage.isScanDialogExists()) {
			woDetailsPage.scanBarcode();
			logger.info("Scanner Skipped");
		}
		// Verify stop button is displayed
		assertEquals("carbon:stop", woDetailsPage.getStopWorkButtonText());
		// Verify user navigates to WO details page
		String woDetialsPageTitle = woDetailsPage.getTitle(woDetailsPage.infoLocator);
		assertEquals(expectedDetailsPageTitle, woDetialsPageTitle);
		// Verify status of the work order is changed to "In Progress"
		assertEquals(expectedWOStatusDetailsPage, woDetailsPage.verifyChangedStatus());
	}

	@Test(dependsOnMethods = { "scenario1" }, groups = {
			"priority2" }, description = "Scenario 6 - Verify labor transaction is saved and technician stays at work order details page when technician clicks/taps on pause button from work order details page", timeOut = 90000000)
	public void scenario2() throws Exception {

		// Open work order list page
		WorkOrderDetailsPage woDetailsPage = new WorkOrderDetailsPage(af);
		ReportWorkPage reportWork = new ReportWorkPage(af);
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		ReportWorkPage reportWorkPage = new ReportWorkPage(af);
		
		// Click on pause button on detail page
		woDetailsPage.clickPauseBUtton();
		// Verify technician remains on WO details page after clicking pause work
		String woDetialsPageTitle = woDetailsPage.getTitle(woDetailsPage.infoLocator);
		assertEquals(expectedDetailsPageTitle, woDetialsPageTitle);
		// Navigate to report work page
		woDetailsPage.clickReportWorkButton();
		// verify header of the report work page
		header = woDetailsPage.getTitle(woDetailsPage.reportWorkPageTitle);
		reportHeader = woNum + " Report work";
		assertEquals(header, reportHeader);
		logger.info("Report work Page header verified");
		// Expand the labor chevron
		reportWork.laborChevronExpand();
		// Verify the labor record exists
		assertTrue(reportWork.isLaborRecordExists(),"Labor record exists on report work page");
		reportWorkPage.isStartDateTimeExist();
		logger.info("Start Date and Time exist");
		reportWorkPage.isEndDateTimeExist();
		logger.info("End Date and Time exist");
		reportWorkPage.isLaborTransactionTypeExist();
		logger.info("Labor Transaction type exist");
		reportWorkPage.isRegularHourExist();
		logger.info("Regular Hour exist");
		reportWorkPage.clickBackChevron();
		woDetailsPage.clickBackChevron();
		assignedWorkPage.clickClearButton();	
		
	}

	@Test(groups = {
			"priority3" }, description = " Verify that a technician can start timer/labor transaction when other technician(s) has started timers/labor transactions and \"maximo.mobile.allowmultipletimers\" system property is 0", timeOut = 90000000)
	public void verifyStartByMultipleLabor() throws Exception {

		// Open work order list page
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage woDetailsPage = new WorkOrderDetailsPage(af);
		// Search the work order
		assertTrue(assignedWorkPage.search(woNum1), "Fail : Search Work Order is not displayed");
		// Verify status of Work order is Approved
		assertEquals(expectedWOStatusListPage, assignedWorkPage.verifyChangedStatus());
		// Verify start work button exists
		assertTrue(assignedWorkPage.isStartWorkButtonExists());
		// Click on start work button
		assignedWorkPage.clickStartWorkButton();
		// Check Barcode Scanner
		if (woDetailsPage.isScanDialogExists()) {
			woDetailsPage.scanBarcode();
			logger.info("Scanner Skipped");
		}
		// Verify stop button is displayed
		assertEquals("carbon:stop", woDetailsPage.getStopWorkButtonText());
		// Verify user navigates to WO details page
		
		logOut(af);
		// Relogin to enter different credentials
		reLoginWithDifferentCredentials();
		// Open work order list page
		
		// Search the work order
		assertTrue(assignedWorkPage.search(woNum1), "Fail : Search Work Order is not displayed");
		// Verify status of Work order is Approved
		assertEquals(expectedWOStatusDetailsPage, assignedWorkPage.verifyChangedStatus());
		// Verify start work button exists
		assertTrue(assignedWorkPage.isStartWorkButtonExists());
		// Click on start work button
		assignedWorkPage.clickStartWorkButton();
		// Check Barcode Scanner
		if (woDetailsPage.isScanDialogExists()) {
			woDetailsPage.scanBarcode();
			logger.info("Scanner Skipped");
		}
		// Verify stop button is displayed
		assertEquals("carbon:stop", woDetailsPage.getStopWorkButtonText());
		// Verify user navigates to WO details page
		
	}

	// Generated by WCA for GP
	/**
	 * Create default objects
	 * 
	 * @throws Exception
	 */
	protected void createDefaultObjects() throws Exception {
		logger.info("Creating default objects");
		updateSystemProperties("1");
		logger.info("System property set");
		setSystemSettings();
		logger.info("System property set");

		// Create an asset
		logger.info("Creating an asset");
		assetNum = AbstractAutomationFramework.randomString(5).toUpperCase();
		String assetResult = maximoApi.retrieve(new Asset(), "addid=1&internalvalues=1&action=system:new&addschema=1");
		Asset newAsset = new Gson().fromJson(assetResult, Asset.class);
		newAsset.setAssetNum(assetNum);
		newAsset.setDescription(ASSET_DESCRIPTION);
		maximoApi.create(newAsset);
		logger.info("Asset: {}" + assetNum);

		// Create a workorder
		logger.info("Creating a work order");
		String workOrderResult = maximoApi.retrieve(new WorkOrder(),
				"addid=1&internalvalues=1&action=system:new&addschema=1");
		newWorkOrder = new Gson().fromJson(workOrderResult, WorkOrder.class);
		newWorkOrder.setDescription(WO_DESCRIPTION);
		newWorkOrder.setAssetNum(assetNum);
		newWorkOrder.setWorkType(WorkType.PM.toString());
		newWorkOrder.setGLAccount(SetupData.GLDEBITACCT);
		maximoApi.create(newWorkOrder);
		woNum = newWorkOrder.getWoNum();
		logger.info("Work Order: {}" + woNum);

		// Change WO status to Approved
		logger.info("Changing work order status to APPR");
		maximoApi.changeStatus(newWorkOrder, WoStatus.APPR.toString());

		// Assignment with labor wilson
		maximoApi.addAssignmentLabor(newWorkOrder, labor);
		logger.info("Assignment added");
	}

	
	// Generated by WCA for GP
		/**
		 * Create default objects
		 * 
		 * @throws Exception
		 */
		protected void createDefaultObjectsForMultipleLabor() throws Exception {
			logger.info("Creating default objects for multiple labor");
			updateSystemPropertiesForMultiplelabor("0");
			logger.info("System property set");
			setSystemSettings();
			logger.info("System property set");

			// Create an asset
			logger.info("Creating an asset");
			assetNum1 = AbstractAutomationFramework.randomString(5).toUpperCase();
			String assetResult = maximoApi.retrieve(new Asset(), "addid=1&internalvalues=1&action=system:new&addschema=1");
			Asset newAsset = new Gson().fromJson(assetResult, Asset.class);
			newAsset.setAssetNum(assetNum1);
			newAsset.setDescription(ASSET_DESCRIPTION);
			maximoApi.create(newAsset);
			logger.info("Asset1: {}" + assetNum1);

			// Create a workorder
			logger.info("Creating a work order");
			String workOrderResult = maximoApi.retrieve(new WorkOrder(),
					"addid=1&internalvalues=1&action=system:new&addschema=1");
			newWorkOrder1 = new Gson().fromJson(workOrderResult, WorkOrder.class);
			newWorkOrder1.setDescription(WO_DESCRIPTION);
			newWorkOrder1.setAssetNum(assetNum1);
			newWorkOrder1.setWorkType(WorkType.PM.toString());
			newWorkOrder1.setGLAccount(SetupData.GLDEBITACCT);
			maximoApi.create(newWorkOrder1);
			woNum1 = newWorkOrder1.getWoNum();
			logger.info("Work Order: {}" + woNum1);


			// Assignment with labor wilson
			maximoApi.addAssignmentLabor(newWorkOrder1, labor);
			logger.info("Assignment added");

			// Assignment with labor wilson
			maximoApi.addAssignmentLabor(newWorkOrder1, labor2);
			logger.info("Assignment added");
			// Change WO status to Approved
			logger.info("Changing work order status to APPR");
			maximoApi.changeStatus(newWorkOrder1, WoStatus.APPR.toString());

		}
	// Generated by WCA for GP
	/**
	 * Update system properties
	 * 
	 * @throws Exception
	 */
	protected void updateSystemProperties(String value) throws Exception {
		jdbcConnection.executeUpdateSQL("UPDATE MAXIMO.MAXPROP SET MAXIMO.MAXPROP.LIVEREFRESH = '"+value+"' WHERE MAXIMO.MAXPROP.PROPNAME='maximo.mobile.usetimer'");
		jdbcConnection.executeUpdateSQL("UPDATE MAXIMO.MAXPROP SET MAXIMO.MAXPROP.ONLINECHANGES = '"+value+"' WHERE MAXIMO.MAXPROP.PROPNAME='maximo.mobile.usetimer'");
		logger.info("System Properties Set Successfully "+value);
	}
	// Generated by WCA for GP
	/**
	 * Update system properties
	 * 
	 * @throws Exception
	 */
	protected void updateSystemPropertiesForMultiplelabor(String value) throws Exception {
		jdbcConnection.executeUpdateSQL("UPDATE MAXIMO.MAXPROP SET MAXIMO.MAXPROP.LIVEREFRESH = '"+value+"' WHERE MAXIMO.MAXPROP.PROPNAME='maximo.mobile.allowmultipletimersr'");
		jdbcConnection.executeUpdateSQL("UPDATE MAXIMO.MAXPROP SET MAXIMO.MAXPROP.ONLINECHANGES = '"+value+"' WHERE MAXIMO.MAXPROP.PROPNAME='maximo.mobile.allowmultipletimersr'");
		logger.info("System Properties Set Successfully "+value);
	}
	
	// Generated by WCA for GP
	/**
	 * Set system settings
	 */
	public void setSystemSettings() {
		logger.info("Uncheck/De-select confirm calculatedbytimer in organization's systemsettings");
		logger.info("Automatically change work order status to INPRG when a user starts a labor timer");
		jdbcConnection.executeUpdateSQL("update MAXIMO.maxvars M set M.varvalue = '1' where M.varname = 'STARTTIMERINPRG'");
		jdbcConnection.executeUpdateSQL("update MAXIMO.maxvars M set M.varvalue = '0' where M.varname = 'CONFIRMLABTRANS'");
	}
}
