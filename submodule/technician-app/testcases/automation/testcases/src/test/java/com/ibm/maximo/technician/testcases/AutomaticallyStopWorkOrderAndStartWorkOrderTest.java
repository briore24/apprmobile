package com.ibm.maximo.technician.testcases;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
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
import com.ibm.maximo.automation.mobile.page.login.LoginPage;
import com.ibm.maximo.components.ButtonComponent;
import com.ibm.maximo.components.LabelComponent;
import com.ibm.maximo.technician.framework.TechnicianTest;
import com.ibm.maximo.technician.page.MySchedulePage;
import com.ibm.maximo.technician.page.ReportWorkPage;
import com.ibm.maximo.technician.page.WorkOrderDetailsPage;
import com.ibm.maximo.automation.mobile.api.json.ServiceAddress;
import com.ibm.maximo.automation.mobile.api.json.WoServiceAddress;
import com.google.gson.Gson;
import com.ibm.maximo.technician.setupdata.SetupData;
import com.ibm.maximo.technician.setupdata.SetupData.*;

/*
 * MASR-1289 : Role Based App Technician : Automatically stop Timer on WO where timer is already running when user click start timer on another WO
 */

public class AutomaticallyStopWorkOrderAndStartWorkOrderTest extends TechnicianTest {
	private final Logger logger = LoggerFactory.getLogger(AutomaticallyStopWorkOrderAndStartWorkOrderTest.class);
	private AbstractAutomationFramework af;
	private TestSuite testSuite;
	private MaximoApi maximoApi;
	public static LoginPage lp;
	private WorkOrder newWorkOrder;
	private WorkOrder newWorkOrder2;
	private String assetNum, woNum, woNum2, labor, seq1WoNum , seq2WoNum;
	private static final String ASSET_DESCRIPTION = "Asset for mobile automation test";
	private static final String WO_DESCRIPTION = "AutoStopStarWorkOrder",laborHeaderText = "Labor";

	@BeforeClass(alwaysRun = true)
	@Parameters({ "configPath" })
	public void setup(String configPath) throws Exception {
		logger.info("********************AutomaticallyStopWorkOrderAndStartWorkOrderTest*********************************");
		this.af = FrameworkFactory.get();
		Properties properties = new Properties();
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(configPath));
			properties.load(in);
			labor = properties.getProperty("system.username");
			maximoApi = new MaximoApi();
			maximoApi.setMaximoServer(properties.getProperty("system.maximoServerUrl"),
					properties.getProperty("system.maximoAPIKey"));
			createDefaultObjects();
		} catch (Exception e) {
			e.printStackTrace();
		}
		login(af);
	}

	@AfterClass(alwaysRun = true)
	public void teardown() throws Exception {
		//logOut(af);
		// Change WO status to Completed
		logger.info("Changing work order status to COMP");
		maximoApi.changeStatus(newWorkOrder, WoStatus.COMP.toString());
		maximoApi.changeStatus(newWorkOrder2, WoStatus.COMP.toString());				
		if (testSuite != null) {
			testSuite.teardown();
		}
	}

	@Test(groups = { "mobile", "desktop" } , description = "Automatically stop Timer on WO where timer is already running when user click start timer on another WO then 1st WO will be stop and 2nd WO will be started", timeOut = 9000000)
	public void automaticallyStopWorkOrderAndStartWorkOrderTest() throws Exception {

		// Open work order list page
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage woDetailsPage = new WorkOrderDetailsPage(af);
		
		// Time is required in web automation as data sync
		if(testType.equalsIgnoreCase("desktop")) {
			logger.info("Time is required in web automation as data sync");
			Thread.sleep(15000);
			assignedWorkPage.checkForUpdateButton();
			Thread.sleep(15000);
		}
		
		// Search the work order
		assertTrue(assignedWorkPage.search(WO_DESCRIPTION), "Fail : Search Work Order is not displayed");
		// 2 record is display woNum and woNum2
		assertEquals(assignedWorkPage.getSearchResult(), "2 records");
		seq1WoNum =	af.instantiateComponent(LabelComponent.class,"n942w[0]").getValue().substring(0,4);
		logger.info("Work Order: seq1WoNum >>>" + seq1WoNum);
		seq2WoNum =	af.instantiateComponent(LabelComponent.class,"n942w[1]").getValue().substring(0,4);
		logger.info("Work Order: seq2WoNum >>>" + seq2WoNum);
				
		// Start the work order on first WO of list
		assignedWorkPage.clickStartWorkButton();
		logger.info("Clicked on Work Order Start");
		// Check Barcode Scanner
		if (woDetailsPage.isScanDialogExists()) {
			woDetailsPage.scanBarcode();
			logger.info("Scanner Skipped");
		}
		// Check if redirected to Work Order Details Page
		assertEquals("Work order", woDetailsPage.getInfo());
		// Check for the Stop button on WO Details Page
		assertEquals("carbon:stop", woDetailsPage.getStopWorkButtonText());
		// Back to Order List Page
		woDetailsPage.clickBackWOList();
		
		// 2 record is display woNum and woNum2
		assertEquals(assignedWorkPage.getSearchResult(), "2 records");
		// Click Second Start WO from list page
		assignedWorkPage.clickSecondStartWorkButton();
		
		// Verify message Do you want to stop timer? Select Yes to stop the timer for work order woNum workOrderDescription and start the timer for work order woNum workOrderDescription.
		assertEquals("Do you want to stop timer?", af.instantiateComponent(ButtonComponent.class, "dialogLabel1").getText());
		assertEquals("Select Yes to stop the timer for work order PM "+seq1WoNum+" "+WO_DESCRIPTION+" and start the timer for work order PM "+seq2WoNum+" "+WO_DESCRIPTION+".", af.instantiateComponent(ButtonComponent.class, "dialogLabel2").getText());		
		  
		// Confirmation Message need to click Yes
		woDetailsPage.clickButton("confirmDialog_button_group_confirmDialog_primary_button");
		
		// Click Send Button
		woDetailsPage.clickButton("woConfirmLabTimeOnSchedule_button_group_woConfirmLabTimeOnSchedule_primary_button");

		// Check if redirected to Work Order Details Page
		assertEquals("Work order", woDetailsPage.getInfo());
		
		// Check for the Stop button on WO Details Page
		assertEquals("carbon:stop", woDetailsPage.getStopWorkButtonText());
		
		// Click Back Chevron
		woDetailsPage.clickBackChevron();
		// Clear Search Result
		//assignedWorkPage.clickClearButton();
	}
	
	@Test(dependsOnMethods = { "automaticallyStopWorkOrderAndStartWorkOrderTest" }, groups = { "mobile", "desktop" }, description = "Automatically stop Timer on WO where timer is already running when user click start timer on another WO then 1st WO will be stop and 2nd WO will be started", timeOut = 9000000)
	public void automaticallyStopWorkOrderAndStartWorkOrderTest2() throws Exception {

		// Open work order list page
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage woDetailsPage = new WorkOrderDetailsPage(af);

		// 2 record is display woNum and taskWo
		assertEquals(assignedWorkPage.getSearchResult(), "2 records");
		// Start the work order from 1st WO in list
		assignedWorkPage.clickStartWorkButton();
		
		// Verify message Do you want to stop timer? Select Yes to stop the timer for work order woNum workOrderDescription and start the timer for work order woNum workOrderDescription.
		assertEquals("Do you want to stop timer?", af.instantiateComponent(ButtonComponent.class, "dialogLabel1").getText());
		assertEquals("Select Yes to stop the timer for work order PM "+seq2WoNum+" "+WO_DESCRIPTION+" and start the timer for work order PM "+seq1WoNum+" "+WO_DESCRIPTION+".", af.instantiateComponent(ButtonComponent.class, "dialogLabel2").getText());		
		  
		// Confirmation Message need to click Yes
		woDetailsPage.clickButton("confirmDialog_button_group_confirmDialog_primary_button");
		
		// Click delete entry Button on Labor Approval dialog
		woDetailsPage.clickButton("woConfirmLabTimeOnSchedule_button_group_woConfirmLabTimeOnSchedule_tertiary_button");
		
		// Check for the Stop button on WO Details Page
		assertEquals("carbon:stop", woDetailsPage.getStopWorkButtonText());
		
		// Click Back Chevron
		woDetailsPage.clickBackChevron();

	}
	
	@Test(dependsOnMethods = { "automaticallyStopWorkOrderAndStartWorkOrderTest2" }, groups = { "mobile", "desktop" }, description = "Automatically stop Timer on WO where timer is already running when user click start timer on another WO but 1st WO will be stop but 2nd WO will not be started.", timeOut = 9000000)
	public void automaticallyStopWorkOrderAndAnotherWorkOrderNotStartedTest() throws Exception {

		// Open work order list page
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage woDetailsPage = new WorkOrderDetailsPage(af);
		ReportWorkPage reportWork = new ReportWorkPage(af);
		
		// 2 record is display woNum and woNum2
		assertEquals(assignedWorkPage.getSearchResult(), "2 records");
		// Click Second Start WO from list page
		assignedWorkPage.clickSecondStartWorkButton();
		
		// Verify message Do you want to stop timer? Select Yes to stop the timer for work order woNum workOrderDescription and start the timer for work order woNum workOrderDescription.
		assertEquals("Do you want to stop timer?", af.instantiateComponent(ButtonComponent.class, "dialogLabel1").getText());
		assertEquals("Select Yes to stop the timer for work order PM "+seq1WoNum+" "+WO_DESCRIPTION+" and start the timer for work order PM "+seq2WoNum+" "+WO_DESCRIPTION+".", af.instantiateComponent(ButtonComponent.class, "dialogLabel2").getText());		

		// Confirmation Message need to click Yes
		woDetailsPage.clickButton("confirmDialog_button_group_confirmDialog_primary_button");
		
		// Click edit labor Button on Labor Approval dialog
		woDetailsPage.clickButton("woConfirmLabTimeOnSchedule_button_group_woConfirmLabTimeOnSchedule_secondary_button");

		// Check if redirected to Report page
		// verify Report page title value
		String dynamicTitle = woDetailsPage.getTitle(woDetailsPage.reportWorkPageTitle);
		String expectedTitle = " Report work";
		assertTrue(dynamicTitle.contains(expectedTitle));
		
		// Verify header of the page
		String laborHeader = reportWork.getTitle(reportWork.laborPageHeader);
		assertEquals(laborHeaderText, laborHeader);
		
		// Click on Delete labor entry from edit labor page
		woDetailsPage.clickButton("wp_a6");
				
		woDetailsPage.clickButton(woDetailsPage.reportWorkBreadcrumb);
		af.waitForElementToNotBePresent(By.id(reportWork.laborPageHeader), 2000);
		Thread.sleep(15000); // Edit labor is taking too much time
		// 2 record is display woNum and woNum2
		assertEquals(assignedWorkPage.getSearchResult(), "2 records");
		// if isStartWorkRecordExists is true means both WO are having Start WO button
		assertTrue(assignedWorkPage.isStartWorkRecordExists());
		// Clear Search Result
		assignedWorkPage.clickClearButton();		
	}
	
	protected void createDefaultObjects() throws Exception {
		logger.info("Creating default objects");
		// Update System Properties
		logger.info("System Properties maximo.mobile.allowmultipletimers Set to 0");
		updateSystemProperties("0");

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

		// Assignment with labor
		maximoApi.addAssignmentLabor(newWorkOrder, labor);
		logger.info("Assignment added");

		// Create Work Order 2
		logger.info("Creating a work order 2");
		String workOrderResult2 = maximoApi.retrieve(new WorkOrder(),
				"addid=1&internalvalues=1&action=system:new&addschema=1");
		newWorkOrder2 = new Gson().fromJson(workOrderResult2, WorkOrder.class);
		newWorkOrder2.setDescription(WO_DESCRIPTION);
		newWorkOrder2.setAssetNum(assetNum);
		newWorkOrder2.setWorkType(WorkType.PM.toString());
		newWorkOrder2.setGLAccount(SetupData.GLDEBITACCT);
		maximoApi.create(newWorkOrder2);
		woNum2 = newWorkOrder2.getWoNum();
		logger.info("Work Order: {}" + woNum2);

		// Change WO status to Approved
		logger.info("Changing work order status to APPR");
		maximoApi.changeStatus(newWorkOrder2, WoStatus.APPR.toString());

		// Assignment with labor maxadmin
		maximoApi.addAssignmentLabor(newWorkOrder2, labor);
		logger.info("Assignment added");

	}

	protected void updateSystemProperties(String value) throws Exception {	
		maximoApi.setProperty("maximo.mobile.allowmultipletimers", "COMMON", null, value);
		logger.info("System Properties Set Successfully");
	}

}
