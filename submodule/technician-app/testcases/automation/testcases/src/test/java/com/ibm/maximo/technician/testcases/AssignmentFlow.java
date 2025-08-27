package com.ibm.maximo.technician.testcases;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.google.gson.Gson;
import com.ibm.maximo.automation.mobile.AbstractAutomationFramework;
import com.ibm.maximo.automation.mobile.FrameworkFactory;
import com.ibm.maximo.automation.mobile.MobileAutomationFramework;
import com.ibm.maximo.automation.mobile.api.MaximoApi;
import com.ibm.maximo.automation.mobile.api.json.Asset;
import com.ibm.maximo.automation.mobile.api.json.ServiceAddress;
import com.ibm.maximo.automation.mobile.api.json.WoServiceAddress;
import com.ibm.maximo.automation.mobile.api.json.WorkOrder;
import com.ibm.maximo.automation.mobile.common.AppSwitcher;
import com.ibm.maximo.automation.mobile.common.AppSwitcher.App;
import com.ibm.maximo.technician.framework.TechnicianTest;
import com.ibm.maximo.technician.page.CreateWorkOrderPage;
import com.ibm.maximo.technician.page.MySchedulePage;
import com.ibm.maximo.technician.page.WorkOrderDetailsPage;
import com.ibm.maximo.technician.setupdata.SetupData;
import com.ibm.maximo.technician.setupdata.SetupData.WoStatus;
import com.ibm.maximo.technician.setupdata.SetupData.WorkType;

/*
 * GRAPHITE-53319: Eli should be able to view start work (with timer) when travel is complete and when the configuration is set to track labor hours
 * GRAPHITE-70254 : [TECHMOBILE] Start Stop Travel.
 *
 */

public class AssignmentFlow extends TechnicianTest {
	private final static Logger logger = LoggerFactory.getLogger(AssignmentFlow.class);
	private AbstractAutomationFramework af;
	private TestSuite testSuite;
	private MaximoApi maximoApi;
	private String assetNum, woNum, labor;
	private WorkOrder newWorkOrder;
	private WorkOrder newWorkOrder1,newWorkOrder2,newWorkOrder3,newWorkOrder4;
	private String woNum1,woNum2,woNum3,woNum4;


	@BeforeClass(alwaysRun = true)
	@Parameters({ "configPath" })
	public void setup(String configPath) throws Exception {
		logger.info("********************ChangeStatusWOTest*********************************");
		this.af = FrameworkFactory.get();
		Properties properties = new Properties();
		try {
			ObjectStructureEnableAssignmentFlow("Insert");
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

		logOut(af);
		ObjectStructureEnableAssignmentFlow("Delete");
		// Change WO status to Completed
		logger.info("Changing work order status to COMP");
		maximoApi.changeStatus(newWorkOrder, WoStatus.COMP.toString());
		maximoApi.changeStatus(newWorkOrder1, WoStatus.COMP.toString());
		maximoApi.changeStatus(newWorkOrder2, WoStatus.COMP.toString());
		maximoApi.changeStatus(newWorkOrder3, WoStatus.COMP.toString());
		maximoApi.changeStatus(newWorkOrder4, WoStatus.COMP.toString());
		resetSystemProperty();
		if (testSuite != null) {
			testSuite.teardown();
		}
	}

				@Test(groups = {
						"mobile" }, description = "Verify user is able to view Reject Button and perform reject Action on work order list page", timeOut = 900000)
				public void rejectWorkOrderFromListlPage() throws Exception {

					// Open work order list page
					MySchedulePage assignedWorkPage = new MySchedulePage(af);
					WorkOrderDetailsPage woDetailsPage = new WorkOrderDetailsPage(af);
					// Search the work order
					assertTrue(assignedWorkPage.search(woNum), "Fail : Search Work Order is not displayed");
					// Click on selected workorder to open Work Order Details Page
					woDetailsPage.rejectWorkorder();

					assertFalse(assignedWorkPage.search(woNum), "Fail : Search Work Order is not displayed");

				}

				@Test(groups = {
				"mobile" }, description = "Verify user is able to view Accept Button and perform reject Action on work order list page", timeOut = 900000)
			public void acceptWorkorderFromListPage() throws Exception {

			// Open work order list page
			MySchedulePage assignedWorkPage = new MySchedulePage(af);
			WorkOrderDetailsPage woDetailsPage = new WorkOrderDetailsPage(af);
			// Search the work order
			assertTrue(assignedWorkPage.search(woNum2), "Fail : Search Work Order is not displayed");
			// Click on selected workorder to open Work Order Details Page

			woDetailsPage.acceptWorkorder();
			//
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
			WorkOrderDetailsPage.clickBackWOList();
			// Clear Search Result


			//Clear Search Result
			assignedWorkPage.clickClearButton();


			}

			@Test(groups = {
			"mobile" }, description = "Verify user is able to view Reject Button and perform reject Action on work order Dateil page", timeOut = 900000)
		public void rejectWorkOrderFromDetailPage() throws Exception {

		// Open work order list page
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage woDetailsPage = new WorkOrderDetailsPage(af);
		// Search the work order
		assertTrue(assignedWorkPage.search(woNum3), "Fail : Search Work Order is not displayed");
		// Click on selected workorder to open Work Order Details Page
		assignedWorkPage.openWorkOrderDetails();
		woDetailsPage.rejectWorkorderonDetailsPage();
		WorkOrderDetailsPage.clickBackWOList();

		assertFalse(assignedWorkPage.search(woNum), "Fail : Search Work Order is not displayed");

		}
			@Test(groups = {
			"mobile" }, description = "Verify user is able to view Accept Button and perform Accept Action on work order Dateil page", timeOut = 900000)
		public void acceptWorkorderFromDetailsPage() throws Exception {

		// Open work order list page
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage woDetailsPage = new WorkOrderDetailsPage(af);
		// Search the work order
		assertTrue(assignedWorkPage.search(woNum4), "Fail : Search Work Order is not displayed");
		// Click on selected workorder to open Work Order Details Page
		assignedWorkPage.openWorkOrderDetails();
		assertFalse(woDetailsPage.isEditWorkOrderExist(), "Edit work order icon not exist");
		assertFalse(woDetailsPage.isReportButtonExist(), "Edit work order icon not exist");
		woDetailsPage.acceptWorkorderonDetailsPage();
		WorkOrderDetailsPage.clickBackWOList();
		//
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
		WorkOrderDetailsPage.clickBackWOList();
		// Clear Search Result

		// Clear Search Result
		assignedWorkPage.clickClearButton();
			}

			@Test(groups = {
			"priority2" }, description = "Verify user is able to view Accept and Reject button on map Page", timeOut = 900000)
		public void verifyAcceptrejectOnMapPage() throws Exception {

		// Open work order list page
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage woDetailsPage = new WorkOrderDetailsPage(af);
		AppSwitcher appSwitcher = new AppSwitcher(af);

		//Verify "Map" opens with "Assigned work" filter
		logger.info("Verify Map opens with Assigned work filter");
		appSwitcher.switchApp(App.Map);
		assertEquals("Assigned work", assignedWorkPage.getAssignedWorkFilterOnMapPage());
		assertEquals(true, assignedWorkPage.verifySearchInputOnMap());
		assignedWorkPage.verifyAcceptrejectOnMapPage(woNum4);
		// Searc.h the work order

			}



	protected void createDefaultObjects() throws Exception {
		logger.info("Creating default objects");
		systemPropertiesOfStatusforphysicalsignature("1");
		updateSystemProperties();
		logger.info("setting system properties");

		// Create an asset
		logger.info("Creating an asset");
		assetNum = AbstractAutomationFramework.randomString(5).toUpperCase();
		String assetResult = maximoApi.retrieve(new Asset(), "addid=1&internalvalues=1&action=system:new&addschema=1");
		Asset newAsset = new Gson().fromJson(assetResult, Asset.class);
		newAsset.setAssetNum(assetNum);
		newAsset.setSiteId(SetupData.SITEID);
		maximoApi.create(newAsset);
		logger.info("Asset: " + assetNum);

		// Create a work order
		logger.info("Creating a work order");
		woNum = AbstractAutomationFramework.randomString(5).toUpperCase();
		String workOrderResult = maximoApi.retrieve(new WorkOrder(),
				"addid=1&internalvalues=1&action=system:new&addschema=1");
		newWorkOrder = new Gson().fromJson(workOrderResult, WorkOrder.class);
		newWorkOrder.setWoNum(woNum);
		newWorkOrder.setDescription("WorkOrder for mobile automation test");
		newWorkOrder.setWorkType(WorkType.PM.toString());
		newWorkOrder.setSiteId(SetupData.SITEID);
		newWorkOrder.setAssetNum(assetNum);
		maximoApi.create(newWorkOrder);
		logger.info("Work Order: " + woNum);

		// Change WO status to Approved
		logger.info("Changing work order status to APPR");
		maximoApi.changeStatus(newWorkOrder, WoStatus.APPR.toString());
		// Assignment with labor maxadmin
		maximoApi.addAssignmentLabor(newWorkOrder, labor);
		logger.info("Assignment added");

		// Create a work order 2
		logger.info("Creating a work order2");
		woNum2 = AbstractAutomationFramework.randomString(5).toUpperCase();
		String workOrderResult2 = maximoApi.retrieve(new WorkOrder(),
				"addid=1&internalvalues=1&action=system:new&addschema=1");
		newWorkOrder2 = new Gson().fromJson(workOrderResult2, WorkOrder.class);
		newWorkOrder2.setWoNum(woNum2);
		newWorkOrder2.setDescription("WorkOrder for mobile automation test");
		newWorkOrder2.setWorkType(WorkType.PM.toString());
		newWorkOrder2.setSiteId(SetupData.SITEID);
		newWorkOrder2.setAssetNum(assetNum);
		maximoApi.create(newWorkOrder2);
		logger.info("Work Order: " + woNum2);

		// Change WO status to Approved
		logger.info("Changing work order status to APPR");
		maximoApi.changeStatus(newWorkOrder2, WoStatus.APPR.toString());
		// Assignment with labor maxadmin
		maximoApi.addAssignmentLabor(newWorkOrder2, labor);
		logger.info("Assignment added");

		// Create a work order 3
		logger.info("Creating a work order 3");
		woNum3 = AbstractAutomationFramework.randomString(5).toUpperCase();
		String workOrderResult3 = maximoApi.retrieve(new WorkOrder(),
				"addid=1&internalvalues=1&action=system:new&addschema=1");
		newWorkOrder3 = new Gson().fromJson(workOrderResult3, WorkOrder.class);
		newWorkOrder3.setWoNum(woNum3);
		newWorkOrder3.setDescription("WorkOrder for mobile automation test");
		newWorkOrder3.setWorkType(WorkType.PM.toString());
		newWorkOrder3.setSiteId(SetupData.SITEID);
		newWorkOrder3.setAssetNum(assetNum);
		maximoApi.create(newWorkOrder3);
		logger.info("Work Order: " + woNum3);

		// Change WO status to Approved
		logger.info("Changing work order status to APPR");
		maximoApi.changeStatus(newWorkOrder3, WoStatus.APPR.toString());
		// Assignment with labor maxadmin
		maximoApi.addAssignmentLabor(newWorkOrder3, labor);
		logger.info("Assignment added");


		// Create a work order 4
		logger.info("Creating a work order 4");
		woNum4 = AbstractAutomationFramework.randomString(5).toUpperCase();
		String workOrderResult4 = maximoApi.retrieve(new WorkOrder(),
				"addid=1&internalvalues=1&action=system:new&addschema=1");
		newWorkOrder4 = new Gson().fromJson(workOrderResult4, WorkOrder.class);
		newWorkOrder4.setWoNum(woNum4);
		newWorkOrder4.setDescription("WorkOrder for mobile automation test");
		newWorkOrder4.setWorkType(WorkType.PM.toString());
		newWorkOrder4.setSiteId(SetupData.SITEID);
		newWorkOrder4.setAssetNum(assetNum);
		maximoApi.create(newWorkOrder4);
		logger.info("Work Order: " + woNum4);

		// Change WO status to Approved
		logger.info("Changing work order status to APPR");
		maximoApi.changeStatus(newWorkOrder4, WoStatus.APPR.toString());
		// Assignment with labor maxadmin
		maximoApi.addAssignmentLabor(newWorkOrder4, labor);
		logger.info("Assignment added");



	}

	protected void updateSystemProperties() throws Exception {
		// Set System Properties
		maximoApi.setProperty("maximo.mobile.statusforphysicalsignature", "COMMON", null, WoStatus.WAPPR.toString());
		logger.info("Properties Set Successfully");
	}

	protected void resetSystemProperty() throws Exception {
		logger.info("Set the value of maximo.mobile.statusforphysicalsignature to original value");
		maximoApi.setProperty("maximo.mobile.statusforphysicalsignature", "COMMON", null, "");
		logger.info("Properties set successfully");
	}





}