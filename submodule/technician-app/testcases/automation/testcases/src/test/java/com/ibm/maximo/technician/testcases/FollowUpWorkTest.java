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

public class FollowUpWorkTest extends TechnicianTest {
	private final Logger logger = LoggerFactory.getLogger(FollowUpWorkTest.class);
	private AbstractAutomationFramework af;
	private TestSuite testSuite;
	private MaximoApi maximoApi;
	private WorkOrder newWorkOrder, newWorkOrder1, newWorkOrder2;
	private static final String LOCATION_DESCRIPTION = "Location for mobile automation test";
	private String assetNum, labor, locationNum, srNum, locationNum8, locationNum9, assetNum8, assetNum9;
	private String descriptionStr = "Follow Up Record created";
	private static final String WORKORDER_DESCRIPTION = "Work Order for Automation Test";
	private String count = "2";
	private String relatedRecord = "Related";
	private String noRelateRecord = "No related records.";
	private String woNum, woNum1, woNum2;
	private String serviceReqDescriptionStr = "Testing for workorder ticket";
	private String newDescriptionStr = "Added new description";
	private int startYear = 2022, finishYear = 2030, startMonth = 8, startDate = 10, hour = 10, minute = 10;
	private String longDescriptionStr = "Long Description Added";
	private String priority = "14";
	private String Number = "1";
	private String priorityUpdated = "99";

	private String invalidStatus = "BMXAA0090E - Asset ABCD is not a valid asset, or its status is not an operating status.";

	@BeforeClass(alwaysRun = true)
	@Parameters({ "configPath" })
	public void setup(String configPath) throws Exception {
		logger.info("********************FollowUpWorkTest*********************************");
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

	@Test(groups = { "mobile" }, description = "Verify a toast message on creating follow up, Verify that new follow-up work order is added to \"Related work orders\" section, Verify that on the \"Create follow-up work order\" page, Technician can add/edit asset location,  Verify technician is navigated back to WO Details page and badge count is updated with new value", timeOut = 600000)
	public void addFollowUpWorkOrder() throws Exception {

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
		editWODetailsPage.enterDescription(descriptionStr);
		assertEquals("Asset and location", followUpWorkPage.assetLocationText());
		// Verify that the asset record is found and selected
		assertTrue(followUpWorkPage.searchForAssets(assetNum));
		// Verify that correct location record is found
		assertTrue(followUpWorkPage.searchForLocation(locationNum));
		followUpWorkPage.clickCreateFollowUp();
		Thread.sleep(4000);
		assertEquals("Follow-up work created", followUpWorkPage.toastDisplayed());
		assertEquals("Related work orders", followUpWorkPage.relatedWorkOrdersText());
		assertEquals("Service requests", followUpWorkPage.relatedTokenHeaderText());
		Thread.sleep(1000);
		assertEquals(srNum, followUpWorkPage.relatedSRTokenNum());
		assertEquals(descriptionStr, followUpWorkPage.followUpDescription());
		Thread.sleep(1000);
		followUpWorkPage.backButton();
		woDetailsPage.clickBackChevron();
		assignedWorkPage.clickClearButton();
		assignedWorkPage.checkForUpdateButton();
		assignedWorkPage.search(woNum);
		assignedWorkPage.openWorkOrderDetails();
		assertEquals(count, woDetailsPage.followUpCountDisplay(count));
		woDetailsPage.clickBackChevron();
		assignedWorkPage.clickClearButton();
	}

	@Test(groups = {
			"mobile" }, description = "Verify Follow Up WO Details Originating Record Work Order is Present", timeOut = 600000)
	public void checkFollowUpDetails() throws Exception {
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage woDetailsPage = new WorkOrderDetailsPage(af);
		FollowUpWorkPage followUpWorkPage = new FollowUpWorkPage(af);

		// Search Work Order and Open Work Order Details
		assignedWorkPage.search(woNum);
		assignedWorkPage.openWorkOrderDetails();
		logger.info("Current Page : " + woDetailsPage.getInfo());

		// Click on Follow Up Work Button on Work Order Details Page
		woDetailsPage.clickFollowUpWorkButton();
		String followUpTitle = followUpWorkPage.getTitle(followUpWorkPage.followUpPageTitle);
		logger.info("Current Page : " + followUpTitle);

		// Click on Newly Created Follow Up Work Chevron Button
		followUpWorkPage.clickOpenChevron();

		// Click on Follow Up Work Button on Follow Up Work Order Details Page
		woDetailsPage.clickFollowUpWorkButton();

		// Check Originating Work Order Record is Present on Follow Up Work Page
		logger.info("Check Originating Work Order Record is Present on Follow Up Work Page");
		logger.info(followUpWorkPage.originatingRecordText());
		assertEquals("Originating record", followUpWorkPage.originatingRecordText());

		// Description of Originating Work Order
		String description = ((woNum).toString() + " " + WORKORDER_DESCRIPTION);

		// Check originating record description is equals to Original Work Order
		// Description
		logger.info("Check originating record description is equals to Original Work Order Description");
		logger.info("Originating Record Description : " + description);
		logger.info("Label Description :" + followUpWorkPage.followUpDescription());
		assertEquals(description, followUpWorkPage.followUpDescription());
		goToMySchedule(af);
		assignedWorkPage.clickClearButton();
	}

	@Test(groups = {
			"mobile" }, description = "Verify contents of \"Follow-up work\" page when follow-up/related work orders is not present but Service request is associated with the work order", timeOut = 9000000)
	public void verifyContentsOfFollowUpWO() throws Exception {
		WorkOrderDetailsPage woDetails = new WorkOrderDetailsPage(af);
		FollowUpWorkPage followUpWorkPage = new FollowUpWorkPage(af);
		MySchedulePage assignedWorkPage = new MySchedulePage(af);

		assignedWorkPage.search(woNum1);
		assignedWorkPage.openWorkOrderDetails();
		// Click on Follow up work button
		woDetails.clickFollowUpWorkButton();
		// Verify Header Follow up Work
		String followUpTitle = followUpWorkPage.getTitle(followUpWorkPage.followUpPageTitle);
		logger.info(followUpTitle);
		assertEquals("Follow-up work", followUpTitle);
		// Verify Create follow up work
		assertTrue(af.isElementExists(By.id(followUpWorkPage.btnPlusLocatorIconToCreateFollowUpWO)));
		// Verify Done button
		assertTrue(af.isElementExists(By.id(followUpWorkPage.btnDoneIcon)));
		// Verify No Related records found on Related work orders section when not added
		assertEquals(noRelateRecord, followUpWorkPage.getDescForNoRelatedRecordsFound());
		// Verify content of service request
		assertEquals("Service requests", followUpWorkPage.relatedTokenHeaderText());
		assertEquals(srNum, followUpWorkPage.relatedSRTokenNum());
		assertEquals(serviceReqDescriptionStr, followUpWorkPage.serviceReqDescription());
		assertEquals(relatedRecord, followUpWorkPage.getRecordRelatedOnServiceRequest());
		Thread.sleep(1000);
		followUpWorkPage.backButton();
		woDetails.clickBackChevron();
		assignedWorkPage.clickClearButton();
	}

	@Test(groups = {
			"mobile" }, description = "Verify user can edit the details page of created follow up WO with valid/invalid values", timeOut = 600000)
	public void verifyValidInvalidValues() throws Exception {
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage woDetailsPage = new WorkOrderDetailsPage(af);
		FollowUpWorkPage followUpWorkPage = new FollowUpWorkPage(af);
		EditWorkOrderPage editWODetailsPage = new EditWorkOrderPage(af);

		assignedWorkPage.search(woNum2);
		assignedWorkPage.openWorkOrderDetails();

		// Click on Follow Up Work Button on Work Order Details Page
		woDetailsPage.clickFollowUpWorkButton();
		String followUpTitle = followUpWorkPage.getTitle(followUpWorkPage.followUpPageTitle);
		logger.info("Current Page : " + followUpTitle);
		followUpWorkPage.clickPlusButton();

		// Enter follow details
		editWODetailsPage.enterDescription(descriptionStr);
		// Verify that the asset record is found and selected
		assertTrue(followUpWorkPage.searchForAssets(assetNum));
		// Verify that correct location record is found
		assertTrue(followUpWorkPage.searchForLocation(locationNum));

		followUpWorkPage.clickCreateFollowUp();

		Thread.sleep(4000);
		assertEquals("Follow-up work created", followUpWorkPage.toastDisplayed());

		followUpWorkPage.backButton();
		woDetailsPage.acceptWorkorderonDetailsPage();
		// click on edit present on wo details page
		woDetailsPage.editWorkOrderDetails();

		// enter invalid priority
		editWODetailsPage.enterPriority("9999");

		// verify the error message
		assertEquals("Enter a valid wopriority.", editWODetailsPage.getErrorMsgForWrongPriority());

		// Verify Save button is disabled when entered invalid priority
		assertEquals(false, editWODetailsPage.verifySaveButton());

		// Go back to Work order details page
		editWODetailsPage.goBackToWODetailsPage();

		// click on discard button
		editWODetailsPage.discardButtonClick();

		// click on the edit button again
		woDetailsPage.editWorkOrderDetails();

		// Enter Invalid Asset
		editWODetailsPage.assetNumber("ABCD");

		// Click on the save button
		editWODetailsPage.saveEditedWODetails();

		// Move to mobile
		MobileAutomationFramework maf = (MobileAutomationFramework) af;
		maf.switchToParentFrame();
		ErrorPage errorpage = new ErrorPage(maf);

		// Tap on Error page
		errorpage.clickErrorBadge();

		// click open the transaction to verify the error message
		errorpage.clickTransactionChevron(true);

		// verify pop up displayed for invalid error message
		assertEquals(invalidStatus, errorpage.getErrorMessageTextForInvalidAsset());
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

		// Create Second Work Order
		logger.info("Creating second work order");
		String workOrderResult1 = maximoApi.retrieve(new WorkOrder(),
				"addid=1&internalvalues=1&action=system:new&addschema=1");
		newWorkOrder1 = new Gson().fromJson(workOrderResult1, WorkOrder.class);
		newWorkOrder1.setDescription(WORKORDER_DESCRIPTION);
		newWorkOrder1.setSiteId(SetupData.SITEID);
		newWorkOrder1.setWorkType(WorkType.PM.toString());
		maximoApi.create(newWorkOrder1);
		woNum1 = newWorkOrder1.getWoNum();
		logger.info("Work Order 2: {}" + woNum1);

		// Change WO status to Approved
		logger.info("Changing work order status to APPR");
		maximoApi.changeStatus(newWorkOrder1, WoStatus.APPR.toString());

		// Assign the labor
		maximoApi.addAssignmentLabor(newWorkOrder1, labor);
		logger.info("Assignment added");

		// Create Third Work Order
		logger.info("Creating second work order");
		String workOrderResult2 = maximoApi.retrieve(new WorkOrder(),
				"addid=1&internalvalues=1&action=system:new&addschema=1");
		newWorkOrder2 = new Gson().fromJson(workOrderResult2, WorkOrder.class);
		newWorkOrder2.setDescription(WORKORDER_DESCRIPTION);
		newWorkOrder2.setSiteId(SetupData.SITEID);
		newWorkOrder2.setWorkType(WorkType.PM.toString());
		maximoApi.create(newWorkOrder2);
		woNum2 = newWorkOrder2.getWoNum();
		logger.info("Work Order 3: {}" + woNum2);

		// Change WO status to Approved
		logger.info("Changing work order status to APPR");
		maximoApi.changeStatus(newWorkOrder2, WoStatus.APPR.toString());

		// Assign the labor
		maximoApi.addAssignmentLabor(newWorkOrder2, labor);
		logger.info("Assignment added");

		// Create service request
		srNum = AbstractAutomationFramework.randomString(5).toUpperCase();
		ServiceRequest sr = ServiceRequest.fakeSr(srNum, null);
		sr.setSrClass(SetupData.SRCLASS);
		sr.setStatus(SetupData.SRSTATUS);
		sr.setDescription(serviceReqDescriptionStr);
		maximoApi.create(sr);
		logger.info("service request number: {}" + srNum);

		// Create related record token
		List<RelatedRecord> arr = new ArrayList<RelatedRecord>();
		RelatedRecord rc = new RelatedRecord();
		rc.setRelatedRecKey(srNum);
		arr.add(rc);
		newWorkOrder.setRelatedRecord(arr);
		newWorkOrder1.setRelatedRecord(arr);
		maximoApi.update(newWorkOrder);
		maximoApi.update(newWorkOrder1);
		logger.info("Related record token  created");
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
}
