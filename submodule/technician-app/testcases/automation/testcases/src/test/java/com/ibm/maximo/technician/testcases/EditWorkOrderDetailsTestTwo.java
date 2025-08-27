package com.ibm.maximo.technician.testcases;

import static org.testng.Assert.assertEquals;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import com.ibm.maximo.automation.mobile.api.MaximoApi;
import com.ibm.maximo.automation.mobile.api.json.Asset;
import com.ibm.maximo.automation.mobile.api.json.Location;
import com.ibm.maximo.automation.mobile.api.json.WorkOrder;
import com.ibm.maximo.technician.framework.TechnicianTest;
import com.ibm.maximo.technician.page.CreateWorkOrderPage;
import com.ibm.maximo.technician.page.EditWorkOrderPage;
import com.ibm.maximo.technician.page.MySchedulePage;
import com.ibm.maximo.technician.page.WorkOrderDetailsPage;
import com.ibm.maximo.technician.setupdata.SetupData;
import com.ibm.maximo.technician.setupdata.SetupData.*;

/*
 * GRAPHITE-71547: [TECHMOBILE] Edit work order page
 * /GRAPHITE-70283: [TECHMOBILE] Edit work order page
 * 
 */

public class EditWorkOrderDetailsTestTwo extends TechnicianTest {
	private static final String LOCATION_DESCRIPTION = "Location for mobile automation test";
	private static final String ASSET_DESCRIPTION = "Asset for mobile automation test";
	private final Logger logger = LoggerFactory.getLogger(EditWorkOrderDetailsTestTwo.class);
	private AbstractAutomationFramework af;
	private TestSuite testSuite;
	private MaximoApi maximoApi;
	private WorkOrder newWorkOrder, newWorkOrder1;
	private String assetNum, woNum, labor, location,woNum1,assetNumChange, location1,assetNum1, header, woDetailsPageHeader;
	private boolean apiCodeSuccess = false;
	
	@BeforeClass(alwaysRun = true)
	@Parameters({ "configPath" })
	public void setup(String configPath) throws Exception {
		logger.info("********************EditWorkOrderDetailsTestCaseTwo*********************************");
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
		if (!apiCodeSuccess) {
			logger.info("stopped framework and quit as API failed = "+apiCodeSuccess);
			FrameworkFactory.stopAll();
		} else {
			login(af);
		}
	}

	@AfterClass(alwaysRun = true)
	public void teardown() throws Exception {
		logOut(af);
		logger.info("Changing work order status to COMP");
		maximoApi.changeStatus(newWorkOrder, WoStatus.COMP.toString());
		if (testSuite != null) {
			testSuite.teardown();
		}
	}

	@Test(groups = {
			"priority3" }, description = "Verify availability of edit icon button on work order details page and verify that clicking on the edit icon on work order details page new page is opened so that user can edit work order details", timeOut = 900000)
	public void editWOAndVerifyChangesOnWODetails() throws Exception {

		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage workOrderDetailsPage = new WorkOrderDetailsPage(af);
		EditWorkOrderPage editWODetailsPage = new EditWorkOrderPage(af);

		// Search the WO
		assertEquals(true, assignedWorkPage.search(woNum));
		// Navigate to work order details page
		assignedWorkPage.openCardDetails();
		workOrderDetailsPage.editButtonExists();
		workOrderDetailsPage.editWorkOrderDetails();

		logger.info("Edit work order page is opened");
		String createEditWorkOrderTitle = workOrderDetailsPage
				.getTitle(editWODetailsPage.editWorkOrderdetailsPageTitle);
		assertEquals(woNum + " " + "Edit work order", createEditWorkOrderTitle);
		editWODetailsPage.saveWO();
	}

	@Test(groups = {
			"priority3" }, description = "Verify the selected work type value is highlighted in work type lookup when the lookup is opened while editing the work type field and also after save operation is performed indicating that it is current value", timeOut = 900000)
	public void editWOAndVerifyChangesOnWODetails2() throws Exception {

		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage workOrderDetailsPage = new WorkOrderDetailsPage(af);
		EditWorkOrderPage editWODetailsPage = new EditWorkOrderPage(af);
		CreateWorkOrderPage createWorkOrder= new CreateWorkOrderPage(af);

		workOrderDetailsPage.editWorkOrderDetails();
		
		editWODetailsPage.changeWorkType(WorkType.EM);
		
		// Open work type page
		editWODetailsPage.OpenWorkType();
		
		// Verify header title of worktype
		logger.info("Work type page is opened");
		String workTypeHeaderTitle = workOrderDetailsPage.getTitle(editWODetailsPage.workTypeHeaderPageTitle);
		assertEquals("Worktype", workTypeHeaderTitle);
		
		// verify work type is selected 
		editWODetailsPage.workTypeIsSelected(WorkType.EM);
		
		// Click back button from work type
		editWODetailsPage.backButton();
		
		// Save work order 
		editWODetailsPage.saveWO();
		
		// Edit the work order 
		workOrderDetailsPage.editWorkOrderDetails();
		
		// Verify selected value is correct
		assertEquals("EM", editWODetailsPage.workTypeValue());
		
		// Change work type 
		editWODetailsPage.changeWorkType(WorkType.CM);
		
		// Save the work order
		editWODetailsPage.saveWO();
		
		// Edit the work order 
		workOrderDetailsPage.editWorkOrderDetails();
		
		// Verify selected value is correct
		assertEquals("CM", editWODetailsPage.workTypeValue());
		
		// Save the work order 
		editWODetailsPage.saveWO();
		
		// Click back chevron to WO list page
		workOrderDetailsPage.clickBackChevron();
		
		// Clear the work order
		assignedWorkPage.clickClearButton();
		
		// Sync the records and click check for update button
		assignedWorkPage.checkForUpdateButton();
		
	}

	@Test(groups = {
			"priority3" }, description = "Verify system message while updating work order with a location which has associated asset different from the selected asset selected location contains multiple assets)", timeOut = 10000000)
	public void editWOAndVerifyChangesOnWODetails3() throws Exception {

		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage workOrderDetailsPage = new WorkOrderDetailsPage(af);
		EditWorkOrderPage editWODetailsPage = new EditWorkOrderPage(af);
		WorkOrderDetailsPage assignedWorkOrderDetailsPage = new WorkOrderDetailsPage(af);

		// Search the WO
		assertEquals(true, assignedWorkPage.search(woNum));
		// Navigate to work order details page
		assignedWorkPage.openCardDetails();
		
		// Edit button should be visible
		workOrderDetailsPage.editButtonExists();
		
		// Navigate to Work order details page
		workOrderDetailsPage.editWorkOrderDetails();
		
		// Verify correct page is opened
		logger.info("Edit work order page is opened");
		String createEditWorkOrderTitle = workOrderDetailsPage
				.getTitle(editWODetailsPage.editWorkOrderdetailsPageTitle);
		
		assertEquals(woNum + " " + "Edit work order", createEditWorkOrderTitle);
		
		// Change asset number 
		editWODetailsPage.assetNumber(assetNum1);
		
		// Save work order
		editWODetailsPage.saveWO();
		
		// Verify the error message 
		assertEquals("The specified asset is not in the current location. Do you want to update the location with this asset's location - " +location1+ "?",editWODetailsPage.systemMessage());
		
		// Close system message
		editWODetailsPage.systemMessageClose();
		
		/*LOGGED BUG - GRAPHITE-71792- Needs to be automated for Yes/No and discard once bug is fixed
		 * // On closing system message, technician should remain at same page - Edit WO
		 * page logger.info("Edit work order page is opened"); String
		 * createEditWorkOrderTitle1 = workOrderDetailsPage
		 * .getTitle(editWODetailsPage.editWorkOrderdetailsPageTitle);
		 * assertEquals(woNum + " " + "Edit work order", createEditWorkOrderTitle1);
		 * editWODetailsPage.saveWO();
		 */	
		
		// Click back chevron for Work order list page
		assignedWorkOrderDetailsPage.clickBackChevron();
		
		// Click on clear work order number 
		assignedWorkPage.clickClearButton();
		
		// Click on check for updates button
		assignedWorkPage.checkForUpdateButton();
		
	}
	
	@Test(groups = {
			"priority3" }, description = " Verify that user can add a asset and location to work order from edit work order page if there is no location added in it", timeOut = 10000000)
	public void editWOAndVerifyChangesOnWODetails4() throws Exception {

		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage workOrderDetailsPage = new WorkOrderDetailsPage(af);
		EditWorkOrderPage editWODetailsPage = new EditWorkOrderPage(af);

		// Search the WO
		assignedWorkPage.search(woNum1);
		
		// Navigate to work order details page
		assignedWorkPage.openCardDetails();
		
		// Edit button should be visible
		workOrderDetailsPage.editButtonExists();
		
		// Navigate to Work order details page
		workOrderDetailsPage.editWorkOrderDetails();
		
		// Verify correct page is opened
		logger.info("Edit work order page is opened");
		String createEditWorkOrderTitle = workOrderDetailsPage
				.getTitle(editWODetailsPage.editWorkOrderdetailsPageTitle);
		assertEquals(woNum1 + " " + "Edit work order", createEditWorkOrderTitle);
		logger.info("Technician is able to view the asset/location text box in editable mode and can be edited/updated");
		
		// Change asset number 
		editWODetailsPage.assetNumber(assetNum1);
		
		// Change location number 
		editWODetailsPage.locationNumber(location1);
		
		// Save the work order 
		editWODetailsPage.saveWO();
	}
	
	protected void createDefaultObjects() throws Exception {
		try {
			logger.info("Creating default objects");
			// Create a location
			logger.info("Creating a location");
			location = AbstractAutomationFramework.randomString(5).toUpperCase();
			Location newlocation = new Location();

			newlocation.setLocationId(location);
			newlocation.setDescription(LOCATION_DESCRIPTION);
			newlocation.setSiteId(SetupData.SITEID);

			assertEquals(maximoApi.create(newlocation), SetupData.CreatedSuccess);
			logger.info("location: {}" + location);
			
			// Create a second location
			logger.info("Creating a second location");
			location1 = AbstractAutomationFramework.randomString(5).toUpperCase();
			Location newlocation1 = new Location();

			newlocation1.setLocationId(location1);
			newlocation1.setDescription(LOCATION_DESCRIPTION);
			newlocation1.setSiteId(SetupData.SITEID);

			assertEquals(maximoApi.create(newlocation1), SetupData.CreatedSuccess);
			logger.info("location: {}" + location1);
			
		// Create an asset
		logger.info("Creating an asset");
		assetNum = AbstractAutomationFramework.randomString(5).toUpperCase();
		String assetResult = maximoApi.retrieve(new Asset(), "addid=1&internalvalues=1&action=system:new&addschema=1");

		Asset newAsset = new Gson().fromJson(assetResult, Asset.class);

		newAsset.setAssetNum(assetNum);
		newAsset.setDescription(ASSET_DESCRIPTION);
		newAsset.setSiteId(SetupData.SITEID);
		newAsset.setLocation(location);

		assertEquals(maximoApi.create(newAsset), SetupData.CreatedSuccess);
		logger.info("Asset: {}" + assetNum);
		
		// Create another asset
		logger.info("Creating another asset");
		assetNum1 = AbstractAutomationFramework.randomString(5).toUpperCase();
		String assetResult1 = maximoApi.retrieve(new Asset(), "addid=1&internalvalues=1&action=system:new&addschema=1");

		Asset newAsset1 = new Gson().fromJson(assetResult1, Asset.class);

		newAsset1.setAssetNum(assetNum1);
		newAsset1.setDescription(ASSET_DESCRIPTION);
		newAsset1.setSiteId(SetupData.SITEID);
		newAsset1.setLocation(location1);

		assertEquals(maximoApi.create(newAsset1), SetupData.CreatedSuccess);
		logger.info("Asset: {}" + assetNum1);

		// Create a workorder
		logger.info("Creating a work order");
		woNum = AbstractAutomationFramework.randomString(5).toUpperCase();
		String workOrderResult = maximoApi.retrieve(new WorkOrder(),
				"addid=1&internalvalues=1&action=system:new&addschema=1");
		newWorkOrder = new Gson().fromJson(workOrderResult, WorkOrder.class);
		newWorkOrder.setWoNum(woNum);
		newWorkOrder.setDescription("WorkeOrder for mobile automation test");

		newWorkOrder.setSiteId(SetupData.SITEID);
		newWorkOrder.setAssetNum(assetNum);
		newWorkOrder.setWorkType(WorkType.PM.toString());
		newWorkOrder.setLocation(location);
		assertEquals(maximoApi.create(newWorkOrder), SetupData.CreatedSuccess);
		logger.info("Work Order: {}" + woNum);

		// Change WO status to Approved
		logger.info("Changing work order status to APPR");
		assertEquals(maximoApi.changeStatus(newWorkOrder, WoStatus.APPR.toString()), SetupData.NoContentSuccess);
		// Assignment with labor 
		assertEquals(maximoApi.addAssignmentLabor(newWorkOrder, labor), SetupData.NoContentSuccess);
		logger.info("Assignment added");
		
		// Create another work order 
		logger.info("Creating a work order");
		woNum1 = AbstractAutomationFramework.randomString(5).toUpperCase();
		String workOrderResult1 = maximoApi.retrieve(new WorkOrder(),
				"addid=1&internalvalues=1&action=system:new&addschema=1");
		newWorkOrder1 = new Gson().fromJson(workOrderResult1, WorkOrder.class);
		newWorkOrder1.setWoNum(woNum1);
		newWorkOrder1.setDescription("WorkeOrder for mobile automation test");

		newWorkOrder1.setSiteId(SetupData.SITEID);
		newWorkOrder.setWorkType(WorkType.PM.toString());
		assertEquals(maximoApi.create(newWorkOrder1), SetupData.CreatedSuccess);
		logger.info("Second Work Order: {}" + woNum1);

		// Change WO status to Approved
		logger.info("Changing work order status to APPR");
		assertEquals(maximoApi.changeStatus(newWorkOrder1, WoStatus.APPR.toString()), SetupData.NoContentSuccess);
		// Assignment with labor 
		assertEquals(maximoApi.addAssignmentLabor(newWorkOrder1, labor), SetupData.NoContentSuccess);
		logger.info("Assignment added");
		
		apiCodeSuccess = true;
		
	} catch (AssertionError e) {
		
		logger.info(" SkipException apiCodeSuccess = " + apiCodeSuccess);
		apiCodeSuccess = false;
		throw new Exception("Test Setup API Failed,Stopping execution.");
	}
	}
}