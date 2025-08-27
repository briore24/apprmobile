package com.ibm.maximo.technician.testcases;

import static org.testng.Assert.assertEquals;
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
import com.ibm.maximo.technician.framework.TechnicianTest;
import com.ibm.maximo.technician.page.DataUpdatePage;
import com.ibm.maximo.technician.page.MySchedulePage;
import com.ibm.maximo.technician.page.ReportWorkPage;
import com.ibm.maximo.technician.page.WorkOrderDetailsPage;
import com.ibm.maximo.technician.setupdata.SetupData;
import com.ibm.maximo.technician.setupdata.SetupData.WoStatus;
import com.ibm.maximo.technician.setupdata.SetupData.WorkType;

/*
 * GRAPHITE-53319: Eli should be able to view start work (with timer) when travel is complete and when the configuration is set to track labor hours
 * GRAPHITE-70254 : [TECHMOBILE] Start Stop Travel.
 *
 */

public class StartWorkWithTimer extends TechnicianTest {
	private final Logger logger = LoggerFactory.getLogger(StartWorkWithTimer.class);
	private AbstractAutomationFramework af;
	private TestSuite testSuite;
	private MaximoApi maximoApi;
	private String assetNum, woNum, labor;
	private WorkOrder newWorkOrder;
	private String woType = WorkType.PM.toString();
	private WorkOrder newWorkOrder1;
	private String woNum1;
	private String timezone;
	private static final String ASSET_DESCRIPTION = "Asset for mobile automation test";
	private static final String WO_DESCRIPTION = "WorkeOrder for mobile automation test";

	@BeforeClass(alwaysRun = true)
	@Parameters({ "configPath" })
	public void setup(String configPath) throws Exception {
		logger.info("********************StartWorkWithTimer*********************************");
		this.af = FrameworkFactory.get();
		Properties properties = new Properties();
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(configPath));
			properties.load(in);
			labor = properties.getProperty("system.username");
			timezone = properties.getProperty("capabilities.browserstack.timezone") != null ? properties.getProperty("capabilities.browserstack.timezone") : "UTC";
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
		this.updateSystemProperties(0);
		 //Change WO status to Completed
		 setSystemSettings(null,labor);
		logger.info("Changing work order status to COMP");
		maximoApi.changeStatus(newWorkOrder, WoStatus.COMP.toString());
		if (testSuite != null) {
			testSuite.teardown();
		}
	}

	@Test(groups = {
			"mobile" }, description = "Verify user is able to view start work order button with timer icon", timeOut = 900000)
	public void startWorkOrder() throws Exception {

		// Open work order list page
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage woDetailsPage = new WorkOrderDetailsPage(af);
		ReportWorkPage reportWork = new ReportWorkPage(af);
		MobileAutomationFramework maf = (MobileAutomationFramework)af;
		DataUpdatePage dataUpdatePage =  new DataUpdatePage(maf);
		
		// Search the work order
		assertTrue(assignedWorkPage.search(woNum), "Fail : Search Work Order is not displayed");
		// Click on selected workorder to open Work Order Details Page
		assignedWorkPage.openWorkOrderDetails();
		// Check if redirected to Work Order Details Page
		assertEquals("Work order", woDetailsPage.getInfo());
		logger.info("Current Page : " + woDetailsPage.getInfo());
		// Check for the Start Work button with timer on WO Details Page
		assertEquals("maximo:start-work", woDetailsPage.getStartWorkTimerButtonIcon());
		logger.info("START BUTTON ICON ON WO DETAILS PAGE : " + woDetailsPage.getStartWorkTimerButtonIcon());
		// Click Back to WO List Page
		woDetailsPage.clickBackWOList();
		// Start the work order
		assignedWorkPage.clickStartWorkButton();
		logger.info("Clicked on Work Order Start");
		// Check Barcode Scanner
		if (woDetailsPage.isScanDialogExists()) {
			woDetailsPage.scanBarcode();
			logger.info("Scanner Skipped");
		}
		// Check if redirected to Work Order Details Page
		assertEquals("Work order", woDetailsPage.getInfo());
		logger.info("Current Page : " + woDetailsPage.getInfo());
		
		// Verify stop button is displayed
		assertEquals("carbon:stop", woDetailsPage.getStopWorkButtonText());
		
		Thread.sleep(60000);// Time elapsed of 1 minute between start and stop workorder button = "1"

		// Stop the work order with timer
		woDetailsPage.clickStopWorkTimerButtonIcon();
		
		woDetailsPage.clickSendLaborButton();
		
		// Click chevron to expand first labor record
		reportWork.firstLaborChevron("bam2r_items_datalistWrapper"); // Verify first labor reported hour
		logger.info("First Labor Reported Actual: we are waiting for 1 mins by using thread.sleeep() and sometime it's Showing 2 mins due to time clock '0.59' secs  " + "1");
		assertEquals(true, reportWork.getActualLaborhours().contains("1 minutes") || reportWork.getActualLaborhours().contains("2 minutes") );
		
		reportWork.completeReportWorkButton();
		logger.info("Complete Work button is enabled and clicked");
		
		dataUpdatePage.openDataUpdatePage();
		assertTrue(dataUpdatePage.isDataUpdatePage());
		dataUpdatePage.waitSyncBadgeDisappear();
		assertTrue(dataUpdatePage.noSynchronizationInProgress());
	}
	
	//Scenario:- Verify technician can click on start travel button to start labor transaction of work type as "Travel time"

	@Test(groups = {
			"priority3" }, description = "Verify technician can click on start travel button to start labor transaction of work type as \"Travel time", timeOut = 9000000)
	public void workTypeverifcation() throws Exception {
		logger.info("Verify that Start travel button is displayed when property mxe.mobile.travel.prompt is set to 1");
		// Open work order list page
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage woDetailsPage = new WorkOrderDetailsPage(af);
		// Search the work order
		assertEquals(true, assignedWorkPage.search(woNum1));
		// Check for the Start travel button on the searched work order
		assertEquals("carbon:road", assignedWorkPage.getStartTravelButtonText());
		logger.info("Get start travel button");
		// Click Start Travel
		assignedWorkPage.clickStartTravelButton();
		logger.info("Clicked start travel button");
		// Check if Redirected to Work ORder Details page
		assertEquals("Work order", woDetailsPage.getInfo());

		// Click Back Chevron
		woDetailsPage.clickBackChevron();
		logger.info("Back to work order details page");

		assertEquals("carbon:stop", assignedWorkPage.getStopTravelButtonText());
		logger.info("Stop Travel button is available on the screen");

		assignedWorkPage.clickStopTravelButton();
		logger.info("Click On stop travel button");

		assignedWorkPage.isPausePopupExists();
		logger.info("Labor Transaction pop up exist");

		assignedWorkPage.isLaborTransactionTypeExist(); 
		logger.info("Labor Transaction Type exist");

		assertEquals("Travel time", assignedWorkPage.getLaborTransactionTypeVal()); 
		logger.info("Labor Transaction started with work type as Travel Time");

		assignedWorkPage.clickSlidingDrawerButton();
		logger.info("Close sliding drawer button");
	}

	protected void createDefaultObjects() throws Exception {
		logger.info("Creating default objects");
//		 Update System Properties
		this.updateSystemProperties(1);
		setSystemSettings(timezone, labor);
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

		// Create a Service Address
		logger.info("Creating a Service Address");
		String serAddResult = maximoApi.retrieve(new ServiceAddress(),
				"addid=1&internalvalues=1&action=system:new&addschema=1");
		ServiceAddress newSerAdd = new Gson().fromJson(serAddResult, ServiceAddress.class);
		newSerAdd.setLatitudeY(SetupData.LATITUDEY);
		newSerAdd.setLongitudeX(SetupData.LONGITUDEX);
		newSerAdd.setDescription(SetupData.ADDRESSTITLE);
		maximoApi.create(newSerAdd);
		logger.info("Service Address Created :" + newSerAdd.getAddressCode());

		// Link Service Address
		logger.info("Link Service Address with Work Order");
		List<WoServiceAddress> serviceAddressList = new ArrayList<WoServiceAddress>();
		WoServiceAddress newWoServiceAddress = new WoServiceAddress();
		newWoServiceAddress.setsAddressCode(newSerAdd.getAddressCode());
		serviceAddressList.add(newWoServiceAddress);
		logger.info("Service Address Linked to Work Order");

		// Create a work order
		logger.info("Creating a work order");
		String workOrderResult1 = maximoApi.retrieve(new WorkOrder(),
				"addid=1&internalvalues=1&action=system:new&addschema=1");
		newWorkOrder1 = new Gson().fromJson(workOrderResult1, WorkOrder.class);
		newWorkOrder1.setDescription("WorkOrder for mobile automation test Start Travel");
		newWorkOrder1.setAssetNum(assetNum);
		newWorkOrder1.setGLAccount(SetupData.GLDEBITACCT);
		newWorkOrder1.setWoServiceAddress(serviceAddressList);

		maximoApi.create(newWorkOrder1);
		woNum1 = newWorkOrder1.getWoNum();
		logger.info("Work Order: " + woNum1);

		// Assign the labor
		maximoApi.addAssignmentLabor(newWorkOrder1, labor);
		logger.info("Assignment added");

		// Change WO status to Approved
		logger.info("Changing work order status to APPR");
		maximoApi.changeStatus(newWorkOrder1, WoStatus.APPR.toString());
		logger.info("APPR WO");

	}

	protected void updateSystemProperties(int propertyVal) throws Exception {
//		 Set System Properties
		
		if(propertyVal == 1) {
			maximoApi.setProperty("maximo.mobile.usetimer", "COMMON", null, "1");
			maximoApi.setProperty("mxe.mobile.travel.radius", "COMMON", null, "1");
			maximoApi.setProperty("mxe.mobile.travel.prompt", "COMMON", null, "1");
			logger.info("System Properties Set Successfully");
		}else {
			maximoApi.setProperty("maximo.mobile.usetimer", "COMMON", null, "0");
			maximoApi.setProperty("mxe.mobile.travel.radius", "COMMON", null, "0");
			maximoApi.setProperty("mxe.mobile.travel.prompt", "COMMON", null, "0");
			logger.info("System Properties reverted Successfully");
		}
		
	}

	// Generated by WCA for GP
	/**
	 * Set system settings
	 */
	public void setSystemSettings(String timezone, String labor) {
		jdbcConnection.executeUpdateSQL("UPDATE MAXIMO.PERSON SET MAXIMO.PERSON.TIMEZONE = '" + timezone
				+ "' WHERE MAXIMO.PERSON.PERSONID = '" + labor + "'");
		logger.info("Set also timezone");
	}
}
