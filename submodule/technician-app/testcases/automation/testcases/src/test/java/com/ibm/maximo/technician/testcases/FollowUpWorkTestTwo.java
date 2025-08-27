package com.ibm.maximo.technician.testcases;

import static org.testng.Assert.*;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.ibm.maximo.automation.mobile.api.json.*;
import com.ibm.maximo.automation.mobile.page.ErrorPage;
import com.ibm.maximo.technician.setupdata.SetupData.WorkType;
import org.openqa.selenium.By;
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
import com.ibm.maximo.technician.framework.TechnicianTest;
import com.ibm.maximo.technician.page.CreateWorkOrderPage;
import com.ibm.maximo.technician.page.EditWorkOrderPage;
import com.ibm.maximo.technician.page.FollowUpWorkPage;
import com.ibm.maximo.technician.page.MySchedulePage;
import com.ibm.maximo.technician.page.WorkOrderDetailsPage;
import com.ibm.maximo.technician.setupdata.SetupData;
import com.ibm.maximo.technician.setupdata.SetupData.*;

/*
 * GRAPHITE-51068:  Follow-up work order
 * GRAPHITE-63881:  Follow_Up Work order details
 * GRAPHITE-65682
 * GRAPHITE-70282
 */

public class FollowUpWorkTestTwo extends TechnicianTest {
	private final Logger logger = LoggerFactory.getLogger(FollowUpWorkTestTwo.class);
	private AbstractAutomationFramework af;
	private TestSuite testSuite;
	private MaximoApi maximoApi;
	private WorkOrder newWorkOrder, newWorkOrder1, newWorkOrder2;
	private static final String LOCATION_DESCRIPTION = "Location for mobile automation test";
	private String assetNum, labor, locationNum, srNum, locationNum8, locationNum9, assetNum8, assetNum9,dateType;
	private String descriptionStr = "Follow Up Record created";
	private static final String WORKORDER_DESCRIPTION = "Work Order for Automation Test";
	private String woNum, woNum1, woNum2;
	private String serviceReqDescriptionStr = "Testing for workorder ticket";
	private String newDescriptionStr = "Added new description";
	private int startYear = 2022, finishYear = 2030, startMonth = 8, startDate = 10, hour = 10, minute = 10;
	private String longDescriptionStr = "Long Description Added";
	private String priority = "14";
	private String Number = "1";
	private String priorityUpdated = "99";

	@BeforeClass(alwaysRun = true)
	@Parameters({ "configPath" })
	public void setup(String configPath) throws Exception {
		logger.info("********************FollowUpWorkTestTwo*********************************");
		this.af = FrameworkFactory.get();
		Properties properties = new Properties();
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(configPath));
			properties.load(in);
			labor = properties.getProperty("system.username");
			maximoApi = new MaximoApi();
			maximoApi.setMaximoServer(properties.getProperty("system.maximoServerUrl"),
					properties.getProperty("system.maximoAPIKey"));
			failurecodeHierarchy();
			createDefaultObjects();
			createDefaultObjects4();
		} catch (Exception e) {
			e.printStackTrace();
		}
		login(af);
		if (masServer.equals("true")) {
			// FVT SetupData , IVT might be same as sandbox or different
			updateData();
		}
	}

	@AfterClass(alwaysRun = true)
	public void teardown() throws Exception {
		logOut(af);
		// Change WO status to Completed
		logger.info("Changing work order status to COMP");
		maximoApi.changeStatus(newWorkOrder, WoStatus.COMP.toString());
		maximoApi.changeStatus(newWorkOrder1, WoStatus.COMP.toString());
		logger.info("Delete FAILURECODE via DB query>>" + "DELETE FROM MAXIMO.FAILURELIST WHERE FAILURECODE='"
				+ failureCodeClass + "' OR FAILURECODE='" + failureCodeProblem + "' OR FAILURECODE='" + failureCodeCause
				+ "' OR FAILURECODE='" + failureCodeRemedy + "'");
		// Delete FAILURECODE via DB query
		jdbcConnection.executeUpdateSQL("DELETE FROM MAXIMO.FAILURELIST WHERE FAILURECODE='" + failureCodeClass
				+ "' OR FAILURECODE='" + failureCodeProblem + "' OR FAILURECODE='" + failureCodeCause
				+ "' OR FAILURECODE='" + failureCodeRemedy + "'");
		if (testSuite != null) {
			testSuite.teardown();
		}
	}

	@Test(groups = { "priority2" }, description = "Verify various fields values of new follow-up work order when technician saves the follow-up work order successfully", timeOut = 900000)
	public void verifyFieldsAndSaveInFollowUpWorkOrder() throws Exception {

		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage woDetailsPage = new WorkOrderDetailsPage(af);
		FollowUpWorkPage followUpWorkPage = new FollowUpWorkPage(af);
		EditWorkOrderPage editWODetailsPage = new EditWorkOrderPage(af);

		assignedWorkPage.search(woNum);
		assignedWorkPage.openWorkOrderDetails();
		woDetailsPage.clickFollowUpWorkButton();

		String followUpTitle = followUpWorkPage.getTitle(followUpWorkPage.followUpPageTitle);
		logger.info(followUpTitle);
		assertEquals("Follow-up work", followUpTitle);
		followUpWorkPage.clickPlusButton();
		String createFollowUpTitle = woDetailsPage.getTitle(followUpWorkPage.CreateFollowUpPageTitle);
		assertEquals("Create follow-up WO", createFollowUpTitle);
		logger.info(createFollowUpTitle);

		// Enter Description in create WO
		editWODetailsPage.enterDescription(descriptionStr);

		// Enter Long description
		followUpWorkPage.enterLongDescription(longDescriptionStr);

		// Enter priority
		followUpWorkPage.priorityEnter("1");

	

		 dateType=dateConfiguration(); 
			if(dateType.contains("SCHEDULE") && dateType.contains("TARGET") ) {
				// Select Schedule start date and time
				followUpWorkPage.scheduledStartDateAndTime(startYear, startMonth, startDate, hour, minute);
				// Select Schedule finish date and time
				followUpWorkPage.scheduledFinishDateAndTime(finishYear, startMonth, startDate, hour, minute);
				// Select Schedule finish date and time
				followUpWorkPage.targetFinishDateAndTime(finishYear, startMonth, startDate, hour, minute);

				followUpWorkPage.targetStartDateAndTime(startYear, startMonth, startDate, hour, minute);

				}	else if(dateType.contains("TARGET") ) {
				
					followUpWorkPage.targetFinishDateAndTime(finishYear, startMonth, startDate, hour, minute);

					followUpWorkPage.targetStartDateAndTime(startYear, startMonth, startDate, hour, minute);
					} else {
						followUpWorkPage.scheduledStartDateAndTime(startYear, startMonth, startDate, hour, minute);
						// Select Schedule finish date and time
						followUpWorkPage.scheduledFinishDateAndTime(finishYear, startMonth, startDate, hour, minute);
					}
		

		// Click chevron to select work type
		followUpWorkPage.changeWorkType(SetupData.WorkType.CM.toString());
		assertEquals("Asset and location", followUpWorkPage.assetLocationText());
		
		// Verify that the asset record is found and selected
		assertTrue(followUpWorkPage.searchForAssets(assetNum));

		// Verify that correct location record is found
		assertTrue(followUpWorkPage.searchForLocation(locationNum));

		// Enter Estimated Time
		followUpWorkPage.estimatedDuration(Number, Number);

		followUpWorkPage.clickCreateFollowUp();
		followUpWorkPage.backButton();
		// Back to Order List Page
		woDetailsPage.clickBackWOList();
	}

	@Test(groups = { "priority2" }, description = "Verify that on the \"Create follow-up work order\" page, Technician can view and edit asset and location associated to the work order under Asset and Location section", timeOut = 900000)
	public void verifyAssetnLocationAndSaveInFollowUpWorkOrder() throws Exception {

		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage woDetailsPage = new WorkOrderDetailsPage(af);
		FollowUpWorkPage followUpWorkPage = new FollowUpWorkPage(af);

		
		assignedWorkPage.search(woNum);
		assignedWorkPage.openWorkOrderDetails();
		woDetailsPage.clickFollowUpWorkButton();

		String followUpTitle = followUpWorkPage.getTitle(followUpWorkPage.followUpPageTitle);
		logger.info(followUpTitle);
		assertEquals("Follow-up work", followUpTitle);
		followUpWorkPage.clickPlusButton();
		String createFollowUpTitle = woDetailsPage.getTitle(followUpWorkPage.CreateFollowUpPageTitle);
		assertEquals("Create follow-up WO", createFollowUpTitle);
		logger.info(createFollowUpTitle);
		assertEquals("Asset and location", followUpWorkPage.assetLocationText());

		// Verify that the asset record is found and selected
		assertTrue(followUpWorkPage.searchForAssets(assetNum));

		// Verify that correct location record is found
		assertTrue(followUpWorkPage.searchForLocation(locationNum));

		followUpWorkPage.clickCreateFollowUp();
		followUpWorkPage.backButton();
		// Back to Order List Page
		woDetailsPage.clickBackWOList();
		assignedWorkPage.clickClearButton();
	}

	@Test(groups = { "priority3" }, description = "Verify system message while updating work order with an asset which is not in the selected location(with GL account)", timeOut = 900000)
	public void createWorkOrderWithOneLocationOneAssetandChange() throws Exception {
		MobileAutomationFramework maf = (MobileAutomationFramework) this.af;
		CreateWorkOrderPage createWO = new CreateWorkOrderPage(maf);
		WorkOrderDetailsPage woDetailsPage = new WorkOrderDetailsPage(af);
		FollowUpWorkPage followUpWorkPage = new FollowUpWorkPage(af);

		// Click on 9 dots(Navigator menu)
		followUpWorkPage.clickNavigatorMenu();
		logger.info("Clicking Back Chevron from list page");

		// Click on plus icon on Navigator page
		createWO.getplusiconClick();
		logger.info("Clicking Back Chevron from list page");

		// Click on Create Work Order
		createWO.selectCreateWO();

		// Enter Description in create WO
		createWO.insertDescriptionOfWorkOrder(descriptionStr);
		logger.info("Clicking Back Chevron from list page");

		// Enter Long description
		createWO.enterLongDescription(longDescriptionStr);
		logger.info("Clicking Back Chevron from list page");

		maf.scrollPage(1, 2);

		// Enter Asset
		createWO.assetEnter(assetNum8);
		logger.info("Enter Asset");

		// Enter priority
		createWO.priorityEnter(priority);
		logger.info("Enter priority");

		// Verify location is populating automatically
		assertEquals(createWO.getLocationTextWODetailsPage(), locationNum8);
		logger.info("Verify location is populating automatically");

		// Click on Create workorder button
		createWO.clickWorkOrderCreate();
		logger.info("Enter Estimated Time");

		// Click on FollowUpWork Button
		woDetailsPage.clickFollowUpWorkButton();

		// Click on FollowUpWork Plus Button
		followUpWorkPage.clickPlusButton();
		maf.scrollPage(1, 2);

		// Add asset
		followUpWorkPage.clearAndAddassetNumber(assetNum9);
		followUpWorkPage.locationNumberClick();
		followUpWorkPage.yesClickButton();

		// verify location detail
		assertEquals(followUpWorkPage.getLocationTextWODetailsPageOnfollowUpWO(), locationNum8);
		logger.info("Verify location is populating automatically");
		followUpWorkPage.clickCreateFollowUp();
		followUpWorkPage.checkplusButtonAvailbility();

		createWO.clickBackButton(maf);
		logger.info("Navigate to list page");
		createWO.clickBackButton(maf);
		logger.info("Navigate to list page");

		createWO.checkSearchButtonAvailbility();
		logger.info("Verify search button is available");
	}

	@Test(groups = { "priority3" }, description = " Verify asset failure code and asset priority is updated in follow-up work order failure code and asset/location priority, when an asset is added with associated location having location failure code and location priority", timeOut = 900000)
	public void scenario3() throws Exception {

		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage woDetailsPage = new WorkOrderDetailsPage(af);
		FollowUpWorkPage followUpWorkPage = new FollowUpWorkPage(af);
		EditWorkOrderPage editWODetailsPage = new EditWorkOrderPage(af);

		// Search the work order
		assignedWorkPage.search(woNum);

		// Open WO details page
		assignedWorkPage.openWorkOrderDetails();

		// Click on follow up work button
		woDetailsPage.clickFollowUpWorkButton();

		// Verify page title
		String followUpTitle = followUpWorkPage.getTitle(followUpWorkPage.followUpPageTitle);
		logger.info(followUpTitle);
		assertEquals("Follow-up work", followUpTitle);
		followUpWorkPage.clickPlusButton();
		String createFollowUpTitle = woDetailsPage.getTitle(followUpWorkPage.CreateFollowUpPageTitle);
		assertEquals("Create follow-up WO", createFollowUpTitle);
		logger.info(createFollowUpTitle);

		// Verify Overview title
		assertEquals("Overview", followUpWorkPage.overviewLabel());

		// Verify Asset and Location title
		assertEquals("Asset and location", followUpWorkPage.assetLocationText());

		// Click create follow up button
		followUpWorkPage.clickCreateFollowUp();

		// Click back button to WO details page
		followUpWorkPage.backButton();

		// Click back button to WO List page
		woDetailsPage.clickBackChevron();

		// Click WO Clear button
		assignedWorkPage.clickClearButton();

		// Click check for updates button
		assignedWorkPage.checkForUpdateButton();

		// Search for Work order
		assignedWorkPage.search(woNum);

		// Click on WO Details page
		assignedWorkPage.openWorkOrderDetails();

		woDetailsPage.clickFollowUpWorkButton();
		followUpWorkPage.clickOpenChevron();
		woDetailsPage.editWorkOrderDetails();
		editWODetailsPage.enterDescription(newDescriptionStr + woNum);

		editWODetailsPage.enterPriority(priorityUpdated);

		// Verify that the asset record is found and selected
		assertTrue(followUpWorkPage.searchForAssets(assetNum));

		// Verify location is auto-populated
		assertTrue(followUpWorkPage.locationDisplayed());

		editWODetailsPage.saveWO();
		woDetailsPage.clickBackChevronToListPage();
		assignedWorkPage.clickClearButton();
		logger.info("clear clicked");
	}

	protected void createDefaultObjects() throws Exception {
		logger.info("Creating default objects");
		// Create a location
		logger.info("Creating a location");
		locationNum = AbstractAutomationFramework.randomString(5).toUpperCase();
		Location newlocation = new Location();
		newlocation.setLocationId(locationNum);
		newlocation.setDescription(LOCATION_DESCRIPTION);
		newlocation.setSiteId(SetupData.SITEID);
		maximoApi.create(newlocation);
		logger.info("location: {}" + locationNum);

		// Create an Asset
		logger.info("Creating an asset");
		assetNum = AbstractAutomationFramework.randomString(5).toUpperCase();
		String assetResult = maximoApi.retrieve(new Asset(), "addid=1&internalvalues=1&action=system:new&addschema=1");
		Asset newAsset = new Gson().fromJson(assetResult, Asset.class);
		newAsset.setAssetNum(assetNum);
		newAsset.setLocation(locationNum);
		newAsset.setSiteId(SetupData.SITEID);
		newAsset.setStatus(SetupData.LocAssetStatus.ACTIVE.toString());
		maximoApi.create(newAsset);
		logger.info("Asset: {}" + assetNum);

		// Create First Work Order
		logger.info("Creating work order");
		String workOrderResult = maximoApi.retrieve(new WorkOrder(),
				"addid=1&internalvalues=1&action=system:new&addschema=1");
		newWorkOrder = new Gson().fromJson(workOrderResult, WorkOrder.class);
		newWorkOrder.setDescription(WORKORDER_DESCRIPTION);
		newWorkOrder.setSiteId(SetupData.SITEID);
		newWorkOrder.setFailureCode(failureCodeClass);
		newWorkOrder.setWorkType(WorkType.PM.toString());
		maximoApi.create(newWorkOrder);
		woNum = newWorkOrder.getWoNum();
		logger.info("Work Order 1: {}" + woNum);

		// Change WO status to Approved
		logger.info("Changing work order status to APPR");
		maximoApi.changeStatus(newWorkOrder, WoStatus.APPR.toString());

		// Assign the labor
		maximoApi.addAssignmentLabor(newWorkOrder, labor);
		logger.info("Assignment added");

	}

	// Generated by WCA for GP
	/**
	 * Create default objects for testing
	 * 
	 * @throws Exception
	 */
	protected void createDefaultObjects4() throws Exception {

		// Create a location
		logger.info("Creating a location");
		locationNum8 = AbstractAutomationFramework.randomString(5).toUpperCase();
		Location newlocation = new Location();

		newlocation.setLocationId(locationNum8);
		newlocation.setDescription("Loc Desc");
		newlocation.setSiteId(SetupData.SITEID);
		newlocation.setGlaccount(SetupData.GLDEBITACCT);
		newlocation.setFailurecode(failureCodeClass);
		newlocation.setType(SetupData.LocType.OPERATING.toString());
		newlocation.setLocpriority("1");
		newlocation.setStatus(SetupData.LocAssetStatus.ACTIVE.toString());

		maximoApi.create(newlocation);
		logger.info("location8: {}" + locationNum8);

		// Create an asset
		logger.info("Creating an asset");
		assetNum8 = AbstractAutomationFramework.randomString(5).toUpperCase();
		String assetResult = maximoApi.retrieve(new Asset(), "addid=1&internalvalues=1&action=system:new&addschema=1");

		Asset newAsset = new Gson().fromJson(assetResult, Asset.class);

		newAsset.setAssetNum(assetNum8);
		newAsset.setDescription("asset description");
		newAsset.setSiteId(SetupData.SITEID);
		newAsset.setLocation(locationNum8);
		newAsset.setStatus(SetupData.LocAssetStatus.ACTIVE.toString());

		maximoApi.create(newAsset);
		logger.info("Asset8: {}" + assetNum8);

		// Create a location
		logger.info("Creating a location");
		locationNum9 = AbstractAutomationFramework.randomString(5).toUpperCase();
		Location newlocation1 = new Location();

		newlocation1.setLocationId(locationNum9);
		newlocation1.setDescription("Loc Desc");
		newlocation1.setSiteId(SetupData.SITEID);
		newlocation1.setGlaccount(SetupData.GLDEBITACCT);
		newlocation1.setFailurecode(failureCodeClass);
		newlocation1.setType(SetupData.LocType.OPERATING.toString());
		newlocation1.setLocpriority("2");
		newlocation1.setStatus(SetupData.LocAssetStatus.ACTIVE.toString());

		maximoApi.create(newlocation1);
		logger.info("location9: {}" + locationNum9);

		// Create an asset
		logger.info("Creating an asset");
		assetNum9 = AbstractAutomationFramework.randomString(5).toUpperCase();
		String assetResult1 = maximoApi.retrieve(new Asset(), "addid=1&internalvalues=1&action=system:new&addschema=1");

		Asset newAsset1 = new Gson().fromJson(assetResult1, Asset.class);

		newAsset1.setAssetNum(assetNum9);
		newAsset1.setDescription("asset description");
		newAsset1.setSiteId(SetupData.SITEID);
		newAsset1.setLocation(locationNum9);
		newAsset1.setStatus(SetupData.LocAssetStatus.ACTIVE.toString());

		maximoApi.create(newAsset1);
		logger.info("Asset9: {}" + assetNum9);
	}
	/**
	 * Get WOSchedulingDates properties from DB
	 * 
	 * @param itemNum
	 * @return
	 */
	private String dateConfiguration() {
		String query = "SELECT MAXPROP.PROPNAME, MAXPROP.DESCRIPTION, MAXPROPVALUE.PROPVALUE\n"
				+ "FROM MAXPROP, MAXPROPVALUE WHERE MAXPROP.PROPNAME = MAXPROPVALUE.PROPNAME AND MAXPROP.PROPNAME= 'maximo.mobile.WOSchedulingDates'" ;
				
		logger.info(query);
		Object[] resultsObjects = jdbcConnection.executeSQL(query);
		Object[] resultArray1 = (Object[]) resultsObjects[0];
		String dateType1 = resultArray1[2].toString();
		logger.info("dateType1>" + dateType1);
		return dateType1;
	}
}
