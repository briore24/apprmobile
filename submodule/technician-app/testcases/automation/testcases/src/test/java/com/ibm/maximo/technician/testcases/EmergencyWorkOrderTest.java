package com.ibm.maximo.technician.testcases;

import static org.junit.Assert.assertFalse;
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

import com.google.gson.Gson;
import com.ibm.maximo.automation.mobile.AbstractAutomationFramework;
import com.ibm.maximo.automation.mobile.FrameworkFactory;
import com.ibm.maximo.automation.mobile.MobileAutomationFramework;
import com.ibm.maximo.automation.mobile.api.MaximoApi;
import com.ibm.maximo.automation.mobile.api.json.Asset;
import com.ibm.maximo.automation.mobile.api.json.Location;
import com.ibm.maximo.automation.mobile.api.json.WorkOrder;
import com.ibm.maximo.technician.framework.TechnicianTest;
import com.ibm.maximo.technician.page.CreateWorkOrderPage;
import com.ibm.maximo.technician.page.EditWorkOrderPage;
import com.ibm.maximo.technician.page.ErrorPage;
import com.ibm.maximo.technician.page.MySchedulePage;
import com.ibm.maximo.technician.page.WorkOrderDetailsPage;
import com.ibm.maximo.technician.setupdata.SetupData;
import com.ibm.maximo.technician.setupdata.SetupData.*;

/*
 * GRAPHITE-71133 - [TECHMOBILE] Emergency Work Order 
 */

public class EmergencyWorkOrderTest extends TechnicianTest {
	private final Logger logger = LoggerFactory.getLogger(EmergencyWorkOrderTest.class);
	public AbstractAutomationFramework af;
	private TestSuite testSuite;
	private MaximoApi maximoApi;
	private String assetNum, assetNum1, locationNum, locationNum1,labor,assetNum2,location,location1;
	private String descriptionStr = "Description of WO";
	private String assetdescription = "ASSET_1";
	private String assetdescription1 = "ASSET FOR ASSET SEARCH AND ADD TEST";
	private String LOCATION_DESCRIPTION = "INVALID_LOCATION_1";
	private String priorityValue = "1";
	private String LOCATION_DESCRIPTION1 = "LOCATOIN FOR LOCATION SEARCH AND ADD TEST";
	private boolean apiCodeSuccess = false;
	private static final String ASSET_DESCRIPTION = "Asset for mobile automation test";
	@BeforeClass(alwaysRun = true)
	@Parameters({ "configPath" })
	public void setup(String configPath) throws Exception {
		logger.info("********************EmergencyWorkOrderTest*********************************");
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
			createDefaultObjects2(); 
			login(af);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!apiCodeSuccess) {
			logger.info("stopped framework and quit as API failed = "+apiCodeSuccess);
			FrameworkFactory.stopAll();
		} else {
			if (masServer.equals("true")) {
				// FVT SetupData , IVT might be same as sandbox or different
				updateData();
			}
		}
	}
	
	@AfterClass(alwaysRun = true)
	public void teardown() throws Exception {
		resetSystemProperty();
		logOut(af);
		if (testSuite != null) {
			testSuite.teardown();
		}
	}

	@Test(groups = { "mobile" }, description = "Verify the user can save the work order.", timeOut = 9000000)
	public void createQuickReportWithSignature() throws Exception {

		MobileAutomationFramework maf = (MobileAutomationFramework) this.af;
		CreateWorkOrderPage createWO = new CreateWorkOrderPage(maf);
		WorkOrderDetailsPage workOrderDetailsPage = new WorkOrderDetailsPage(af);
		ErrorPage errorpage = new ErrorPage(maf);
		
		//Click on 9 dots(Navigator menu)
		errorpage.clickNavigatorMenu();
		//Click on plus icon on Navigator page
		logger.info("clicked + button");
		createWO.getplusiconClick();

		// Click on Create Quick report
		createWO.createQuickReport();

		// Enter Description in create WO
		createWO.insertDescriptionOfWorkOrder(descriptionStr);

		createWO.searchForAssetsField();

		// Verify that the asset record is found and selected
		assertTrue(createWO.searchForAssets(assetNum));

		// Verify that correct location record is found
		assertTrue(createWO.searchForLocationInQuickReporting(locationNum));

		// Tap on Confirm work Order Creation button (check mark at the top of the page)
		createWO.clickWorkOrderCreate();

		logger.info("Verify signature promt displayed");
		// Verify signature header
		assertEquals("Signature required", workOrderDetailsPage.signatureHeaderText());
		
		// Enter signature
		workOrderDetailsPage.enterSignature();
		
		// Save signature 
		workOrderDetailsPage.saveSignature();
		
		// Verify priority is "1"
		String actualStr = workOrderDetailsPage.getWOPriority();
		logger.info("WO Priority >" + actualStr);
		assertEquals(actualStr, "Priority " + priorityValue);

		// verify work order status is changed to In Progress
		assertEquals("In progress", workOrderDetailsPage.verifyWOstatus());
		logger.info("In progreess verified");
	}
	
	@Test(groups = { "mobile" }, description = "Verify the user can save the work order.", timeOut = 9000000)
	public void createQuickReportWithoutSignature() throws Exception {

		MobileAutomationFramework maf = (MobileAutomationFramework) this.af;
		CreateWorkOrderPage createWO = new CreateWorkOrderPage(maf);
		WorkOrderDetailsPage workOrderDetailsPage = new WorkOrderDetailsPage(af);
		ErrorPage errorpage = new ErrorPage(maf);
	
		//Click on 9 dots(Navigator menu)
		errorpage.clickNavigatorMenu();
		//Click on plus icon on Navigator page
		logger.info("clicked + button");
		createWO.getplusiconClick();

		// Click on Create Quick Report
		createWO.createQuickReport();

		// Enter Description in create WO
		createWO.insertDescriptionOfWorkOrder(descriptionStr);

		createWO.searchForAssetsField();

		// Verify that the asset record is found and selected
		assertTrue(createWO.searchForAssets(assetNum));

		// Verify that correct location record is found
		assertTrue(createWO.searchForLocationInQuickReporting(locationNum));

		// Tap on Confirm work Order Creation button (check mark at the top of the page)
		createWO.clickWorkOrderCreate();

		logger.info("Verify signature promt displayed");
		// Verify signature header
		assertEquals("Signature required", workOrderDetailsPage.signatureHeaderText());
		// Cancel signature prompt
		workOrderDetailsPage.signatureCancel();
		
		// Verify priority is "1"
		String actualStr = workOrderDetailsPage.getWOPriority();
		logger.info("WO Priority >" + actualStr);
		assertEquals(actualStr, "Priority " + priorityValue);
		
		// Verify status is "waiting on approval"
		assertEquals("Waiting on approval", workOrderDetailsPage.verifyWOstatus());
		logger.info("Waiting on approval status verified");
	}
	@Test(groups = { "mobile" }, description = "Verify the user can save the work order.", timeOut = 9000000)
	public void createQuickReportVerifyActualStartDate() throws Exception {

		MobileAutomationFramework maf = (MobileAutomationFramework) this.af;
		CreateWorkOrderPage createWO = new CreateWorkOrderPage(maf);
		WorkOrderDetailsPage workOrderDetailsPage = new WorkOrderDetailsPage(af);
		EditWorkOrderPage editWODetailsPage = new EditWorkOrderPage(af);
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		ErrorPage errorpage = new ErrorPage(maf);
		
		//Click on 9 dots(Navigator menu)
		errorpage.clickNavigatorMenu();
		//Click on plus icon on Navigator page
		logger.info("clicked + button");
		createWO.getplusiconClick();
		// Click on Create Quick report
		createWO.createQuickReport();

		// Enter Description in create WO
		createWO.insertDescriptionOfWorkOrder(descriptionStr);
		createWO.assetNumber(assetNum);
		createWO.locationNumber(locationNum);


		// Tap on Confirm work Order Creation button (check mark at the top of the page)
		createWO.clickWorkOrderCreate();
		logger.info("Verify signature promt displayed");
		// Verify signature header
		assertEquals("Signature required", workOrderDetailsPage.signatureHeaderText());
		
		// Enter signature
		workOrderDetailsPage.enterSignature();
		
		// Save signature 
		workOrderDetailsPage.saveSignature();
		workOrderDetailsPage.editWorkOrderDetails();

		assertEquals(true,editWODetailsPage.verifyActualDateInQuickWO());
		
		editWODetailsPage.goBackToWODetailsPage();
		
		//Bug raised MAXMOA-4617
//		workOrderDetailsPage.clickStartWorkTimerButtonIcon();
//
//		workOrderDetailsPage.clickStopWorkTimerButtonIcon();
//		// Click Send Button
//		workOrderDetailsPage.clickSendButton();
//		// Check if Redirected to Work ORder Details page
//		assertEquals("Work order", workOrderDetailsPage.getInfo());
//		workOrderDetailsPage.editWorkOrderDetails();
//
//		assertEquals(true,editWODetailsPage.verifyActualDateInQuickWO());
//		editWODetailsPage.goBackToWODetailsPage();
		workOrderDetailsPage.clickBackChevron();
		
		

	}


	protected void createDefaultObjects() throws Exception {
		try {
			systemPropertiesOfStatusforphysicalsignature("1");
			updateSystemProperties();
			logger.info("Creating default objects");
			// Create an asset
			logger.info("Creating an asset");
			assetNum = AbstractAutomationFramework.randomString(5).toUpperCase();
			String assetResult = maximoApi.retrieve(new Asset(), "addid=1&internalvalues=1&action=system:new&addschema=1");

			Asset newAsset = new Gson().fromJson(assetResult, Asset.class);

			newAsset.setAssetNum(assetNum);
			newAsset.setDescription(assetdescription);
			newAsset.setSiteId(SetupData.SITEID);
			newAsset.setStatus(SetupData.LocAssetStatus.ACTIVE.toString());

			assertEquals(maximoApi.create(newAsset), SetupData.CreatedSuccess);
			logger.info("Asset: {}" + assetNum);

			// Create a location
			logger.info("Creating a location");
			locationNum = AbstractAutomationFramework.randomString(5).toUpperCase();
			Location newlocation = new Location();

			newlocation.setLocationId(locationNum);
			newlocation.setDescription(LOCATION_DESCRIPTION);
			newlocation.setSiteId(SetupData.SITEID);
			newlocation.setGlaccount(SetupData.GLDEBITACCT);
			assertEquals(maximoApi.create(newlocation), SetupData.CreatedSuccess);
			logger.info("location: {}" + locationNum);

		
			apiCodeSuccess = true;
		} catch (AssertionError e) {
			logger.info(" SkipException apiCodeSuccess = " + apiCodeSuccess);
			apiCodeSuccess = false;
			throw new Exception("Test Setup API Failed,Stopping execution.");
		}
	}
	
	protected void createDefaultObjects2() throws Exception {
		try {
			logger.info("Creating default objects");
			// Create a location
		
			
			// Create a second location
			logger.info("Creating a second location");
			location1 = AbstractAutomationFramework.randomString(5).toUpperCase();
			Location newlocation1 = new Location();

			newlocation1.setLocationId(location1);
			newlocation1.setDescription(LOCATION_DESCRIPTION);
			newlocation1.setSiteId(SetupData.SITEID);

			assertEquals(maximoApi.create(newlocation1), SetupData.CreatedSuccess);
			logger.info("location: {}" + location1);
			

		
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

				
		apiCodeSuccess = true;
		
	} catch (AssertionError e) {
		
		logger.info(" SkipException apiCodeSuccess = " + apiCodeSuccess);
		apiCodeSuccess = false;
		throw new Exception("Test Setup API Failed,Stopping execution.");
	}
	}

	// Generated by WCA for GP
	/**
	 * Method to update the maximo.mobile.statusforphysicalsignature property to
	 * INPRG
	 * 
	 * @throws Exception
	 */
	protected void updateSystemProperties() throws Exception {
		logger.info("Set the value of maximo.mobile.statusforphysicalsignature to INPRG");
		maximoApi.setProperty("maximo.mobile.statusforphysicalsignature", "COMMON", null, WoStatus.INPRG.toString());
		logger.info("Properties set successfully");
	}
	
	// Generated by WCA for GP
	/**
	 * Method to reset the maximo.mobile.statusforphysicalsignature property 
	 * 
	 * 
	 * @throws Exception
	 */
	protected void resetSystemProperty() throws Exception {
		logger.info("Set the value of maximo.mobile.statusforphysicalsignature to original value");
		maximoApi.setProperty("maximo.mobile.statusforphysicalsignature", "COMMON", null, "");
		logger.info("Properties set successfully");
	}
	
}