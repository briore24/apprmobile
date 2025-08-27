package com.ibm.maximo.technician.testcases;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

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
import com.ibm.maximo.automation.mobile.api.json.Asset;
import com.ibm.maximo.automation.mobile.api.json.Location;
import com.ibm.maximo.automation.mobile.common.AppSwitcher;
import com.ibm.maximo.automation.mobile.common.AppSwitcher.App;
import com.ibm.maximo.technician.framework.TechnicianTest;
import com.ibm.maximo.technician.page.CreateWorkOrderPage;
import com.ibm.maximo.technician.page.EditWorkOrderPage;
import com.ibm.maximo.technician.page.ErrorPage;
import com.ibm.maximo.technician.page.MySchedulePage;
import com.ibm.maximo.technician.page.ReportWorkPage;
import com.ibm.maximo.technician.page.WorkOrderDetailsPage;
import com.ibm.maximo.technician.setupdata.SetupData;
import com.ibm.maximo.technician.setupdata.SetupData.*;

/*
 * GRAPHITE-51048 - [TECHMOBILE] Create Work Order 
 * GRAPHITE-70252 - [TECHMOBILE] Create Work Order: P3
 */

public class CreateWorkOrderTest extends TechnicianTest {
	private final Logger logger = LoggerFactory.getLogger(CreateWorkOrderTest.class);
	public AbstractAutomationFramework af;
	private TestSuite testSuite;
	private MaximoApi maximoApi;
	private String assetNum, assetNum1 , locationNum , locationNum1, header, reportHeader,dateType;
	private int startYear = 2022, finishYear = 2030, startMonth = 8, startDate = 10, hour = 10, minute = 10;
	private String descriptionStr = "Description of WO";
	private String longDescriptionStr = "Long Description Added";
	private String assetdescription = "ASSET_1";
	private String assetdescription1 = "ASSET FOR ASSET SEARCH AND ADD TEST";
	private String assetdescriptionInvalid = "ASSET_1";
	private String LOCATION_DESCRIPTION = "INVALID_LOCATION_1";
	private String LOCATION_DESCRIPTION1 = "LOCATOIN FOR LOCATION SEARCH AND ADD TEST";
	private String priority = "1";
	private String hrs = "20", min = "20";
	private boolean apiCodeSuccess = false;

	private String invalidStatus = "BMXAA0090E - Asset "+assetdescriptionInvalid+" is not a valid asset, or its status is not an operating status.";

	@BeforeClass(alwaysRun = true)
	@Parameters({ "configPath" })
	public void setup(String configPath) throws Exception {
		logger.info("********************CreateWorkOrderTest*********************************");
		this.af = FrameworkFactory.get();
		Properties properties = new Properties();
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(configPath));
			properties.load(in);
			maximoApi = new MaximoApi();
			maximoApi.setMaximoServer(properties.getProperty("system.maximoServerUrl"),
					properties.getProperty("system.maximoAPIKey"));
			createDefaultObjects();
		} catch (Exception e) {
			e.printStackTrace();
		}
		login(af);
		if (masServer.equals("true")) {
			// Data will update for IVT
			  updateData(); 
			  }	}

	@AfterClass(alwaysRun = true)
	public void teardown() throws Exception {
		logOut(af);
		if (testSuite != null) {
			testSuite.teardown();
		}
	}

	@Test(groups = { 
		"mobile" }, description = "Verify the user can save the work order.", timeOut = 9500000)
	public void createWorkOrder() throws Exception {

		MobileAutomationFramework maf = (MobileAutomationFramework) this.af;
		CreateWorkOrderPage createWO = new CreateWorkOrderPage(maf);
		ErrorPage errorpage = new ErrorPage(maf);
		WorkOrderDetailsPage workOrderDetailsPage = new WorkOrderDetailsPage(af);
		
//		 Click on 9 dots(Navigator menu)
		errorpage.clickNavigatorMenu();
//		 Click on plus icon on Navigator page
		createWO.getplusiconClick();

		// Click on Create Work Order
		createWO.selectCreateWO();

		// Enter Description in create WO
		createWO.insertDescriptionOfWorkOrder(descriptionStr);

		// Enter Long description
		createWO.enterLongDescription(longDescriptionStr);

		// Enter priority
		createWO.priorityEnter(priority);
		
		maf.scrollPage(1, 2);

		 dateType=dateConfiguration(); 
		if(dateType.contains("SCHEDULE") && dateType.contains("TARGET") ) {
			// Select Schedule start date and time
			createWO.scheduledStartDateAndTime(startYear, startMonth, startDate, hour, minute);

			// Select Schedule finish date and time
			createWO.scheduledFinishDateAndTime(finishYear, startMonth, startDate, hour, minute);

			
			// Select Schedule start date and time
			createWO.targetStartDateAndTime(startYear, startMonth, startDate, hour, minute);

			// Select Schedule finish date and time
			createWO.targetFinishDateAndTime(finishYear, startMonth, startDate, hour, minute);

			}	else if(dateType.contains("TARGET") ) {
				// Select Schedule start date and time
				createWO.targetStartDateAndTime(startYear, startMonth, startDate, hour, minute);

				// Select Schedule finish date and time
				createWO.targetFinishDateAndTime(finishYear, startMonth, startDate, hour, minute);
				} else {
					createWO.scheduledStartDateAndTime(startYear, startMonth, startDate, hour, minute);

					// Select Schedule finish date and time
					createWO.scheduledFinishDateAndTime(finishYear, startMonth, startDate, hour, minute);
				}

		// Enter Estimated Time
		createWO.estDurHrsLocator(hrs);
		createWO.estDurMinsLocator(min);

		// Click chevron to select work type
		createWO.changeWorkType(WorkType.CM);

		// Verify that the asset record is found and selected
		createWO.assetNumber(assetNum);

		// Verify that correct location record is found
		createWO.locationNumber(locationNum1);

		// Tap on Confirm work Order Creation button (check mark at the top of the page)
		createWO.clickWorkOrderCreate();

		// Assert to verify asset number displayed is correct
		assertEquals(assetNum, createWO.getAssetTextWOPage());
		if(dateType.contains("SCHEDULE") && dateType.contains("TARGET") ) {
			String start = workOrderDetailsPage.getTextWOStart();
			String expectedStartDate = dateFormat(startYear, startMonth, startDate);
			logger.info("WO start >" + start);
			assertEquals(start, expectedStartDate);
			String finish = workOrderDetailsPage.getTextWOFinish();
			String expectedFinishtDate = dateFormat(finishYear, startMonth, startDate);
			logger.info("WO finish >" + finish);
			assertEquals(finish, expectedFinishtDate);
			
			String startTarget = workOrderDetailsPage.getTextTargetWOStart();
			String expectedTargetStartDate = dateFormat(startYear, startMonth, startDate);
			logger.info("WO Target start Date>" + startTarget);
			assertEquals(startTarget, expectedTargetStartDate);
			String finishTarget = workOrderDetailsPage.getTextTargetWOFinish();
			String expectedTargetFinishtDate = dateFormat(finishYear, startMonth, startDate);
			logger.info("WO target finish Date>" + finishTarget);
			assertEquals(finishTarget, expectedTargetFinishtDate);
			} else if(dateType.contains("TARGET")) {
				String startTarget = workOrderDetailsPage.getTextTargetWOStart();
				String expectedTargetStartDate = dateFormat(startYear, startMonth, startDate);
				logger.info("WO Target start Date>" + startTarget);
				assertEquals(startTarget, expectedTargetStartDate);
				String finishTarget = workOrderDetailsPage.getTextTargetWOFinish();
				String expectedTargetFinishtDate = dateFormat(finishYear, startMonth, startDate);
				logger.info("WO target finish Date>" + finishTarget);
				assertEquals(finishTarget, expectedTargetFinishtDate);	
			} else {
				String start = workOrderDetailsPage.getTextWOStart();
				String expectedStartDate = dateFormat(startYear, startMonth, startDate);
				logger.info("WO start >" + start);
				assertEquals(start, expectedStartDate);
				String finish = workOrderDetailsPage.getTextWOFinish();
				String expectedFinishtDate = dateFormat(finishYear, startMonth, startDate);
				logger.info("WO finish >" + finish);
				assertEquals(finish, expectedFinishtDate);
			}
	}

	@Test(groups = {
			"mobile" }, description = "Verify when user enter the invalid asset value in asset field then error message is displayed", timeOut = 950000)
	public void invalidAssetError() throws Exception {

		MobileAutomationFramework maf = (MobileAutomationFramework) this.af;
		CreateWorkOrderPage createWO = new CreateWorkOrderPage(maf);
		ErrorPage errorpage = new ErrorPage(maf);

		// Click on 9 dots(Navigator menu)
		errorpage.clickNavigatorMenu();

		// Click on plus icon
		createWO.getplusiconClick();

		// Click on Create Work Order
		createWO.selectCreateWO();

		// Enter Description in create WO
		createWO.insertDescriptionOfWorkOrder(assetdescriptionInvalid);

		// Insert description of asset
		errorpage.addAssetDescription(assetdescriptionInvalid);

		// Tap on Confirm work Order Creation button (check mark at the top of the page)
		createWO.clickWorkOrderCreate();
		
		
		com.ibm.maximo.automation.mobile.page.ErrorPage err = new com.ibm.maximo.automation.mobile.page.ErrorPage(maf);
		maf.switchToParentFrame();

		// Tap on Error page
		err.clickErrorBadge();
		
		//click open the transaction to verify the error message 
		err.clickTransactionChevron(true);
		
		//verify pop up displayed for invalid error message 
		assertEquals(invalidStatus, err.getErrorMessageTextForInvalidAsset());				
		
//		/*
//		 * Need to uncomment this once issue is resolved GRAPHITE-52351
//		 * 
//		 * 
//		 * // Verify that there's the message "Asset invalid_asset_1 is not a valid
//		 * asset // or its status is not an operating status"
//		 * assertEquals(invalidStatus, errorpage.getErrorMessageText());
//		 */
		
		//click open the transaction to verify the error message 
		err.clickTransactionChevron(true);
				
		//verify pop up displayed for invalid error message 
		assertEquals(invalidStatus, err.getErrorMessageTextForInvalidAsset());
		
		maf.changeToDefaultContext();

	}

	@Test(groups = {
			"mobile" }, description = "Verify that all newly created work order with the values in the required field and when save action is performed, User is taken to the WO details view and the page with all the values specified + Verify user can see the asset/location IDs and descriptions", timeOut = 950000)
	public void workOrderCreatedByMe() throws Exception {

		MobileAutomationFramework maf = (MobileAutomationFramework) this.af;
		CreateWorkOrderPage createWO = new CreateWorkOrderPage(maf);
		ErrorPage errorpage = new ErrorPage(maf);
		AppSwitcher appSwitcher = new AppSwitcher(af);
		MySchedulePage assignedWorkPage = new MySchedulePage(af);

		// Called permission in constructor as unwanted permission page is coming.
		MySchedulePage ms = new MySchedulePage(af);

		// Tap on Navigator menu
		errorpage.clickNavigatorMenu();

		// Click on My Schedule
		appSwitcher.switchApp(App.MySchedule);

		// Open Work Orders created by me from dropdown
		createWO.openNativeDropdown(2);

		// Assert work order created is found in "WO created by me"
		assertEquals(true, assignedWorkPage.search(assetNum));

	}

	@Test(groups = {
	      "priority2" }, description = "Verify that technician can add an asset or location to work order from create work order page using search i.e. by searching and filtering with various options displayed such as: Location and Status", timeOut = 9500000)
     public void createWOAssetLocationTest() throws Exception {

		MobileAutomationFramework maf = (MobileAutomationFramework) this.af;
		CreateWorkOrderPage createWO = new CreateWorkOrderPage(maf);
		ErrorPage errorpage = new ErrorPage(maf);
	
       //Click on 9 dots(Navigator menu)
		errorpage.clickNavigatorMenu();
	
        //Click on plus icon on Navigator page
		createWO.getplusiconClick();

		//Click on Create Work Order
		createWO.selectCreateWO();

		//Scenario 7 , 8 , 11 , 12 , 14
		
		//Click on Search Asset
		createWO.searchAsset();

		//Check Filter Icon Is Present
		assertTrue(createWO.checkFilterIcon());
	
		//Click on Filter Icon
		createWO.clickFilterIcon();
	
		//Select Type Filter
		createWO.selectFilter("Type");
		
		//Select only IT asset option
		createWO.selectFilterOption("IT Assets");
	
		//Click on Save Filter
		createWO.saveFilter();
	
		//Check the selected filter count
		assertTrue(createWO.checkBadgeCount());
		logger.info("Filter Count Increased: " +createWO.checkBadgeCount());
	
		//Click Filter Icon Again
		createWO.clickFilterIcon();
	
		//Click on Reset Filter
		createWO.resetFilter();

		//Save Filter Selections
		createWO.saveFilter();

		//Check no filter are selected
		assertFalse(createWO.checkBadgeCountNotExists());
		
		//Go back to Create WO Page
		createWO.clickBackChevron();
		
	    //----Scenario 11:Verify that when user add asset, which has associated location then after clicking the save button, location should also be displayed on wo details page-------------
		
		// Adding asset which has a location associated
		assertTrue(createWO.searchForAssets(assetNum1));
		
		logger.info("Click create work order");
		createWO.clickWorkOrderCreate();
		
		// Assert to verify asset number displayed is correct
		assertEquals(assetNum1, createWO.getAssetTextWOPage());
		
		// Assert to verify asset number displayed is correct
		assertEquals(locationNum1, createWO.getLocationDescWOPage());
		
}
	@Test(groups = {
    "priority3" }, description = "Verify that technician shouldn't be able to insert tasks, planned labor and materials or tools on newly created work order", timeOut = 950000)
public void scenario24() throws Exception {

	MobileAutomationFramework maf = (MobileAutomationFramework) this.af;
	CreateWorkOrderPage createWO = new CreateWorkOrderPage(maf);
	ReportWorkPage reportWorkPage = new ReportWorkPage(af);
	WorkOrderDetailsPage woDetails = new WorkOrderDetailsPage(af);
	ErrorPage errorpage = new ErrorPage(maf);

	errorpage.clickNavigatorMenu();
	// Click on plus icon on Navigator page
	createWO.getplusiconClick();
	
	// Select + icon to create WO 
	createWO.selectCreateWO();

	// Enter Description in create WO
	createWO.insertDescriptionOfWorkOrder(descriptionStr);

	// Enter priority
	createWO.priorityEnter(priority);

	// Tap on Confirm work Order Creation button (check mark at the top of the page)
	createWO.clickWorkOrderCreate();
	
	// Click on Report work button
	woDetails.clickReportWorkButton();
	
	// verify header of the page
	header = woDetails.getTitle(woDetails.reportWorkPageTitle);
	reportHeader ="Report work";
	assertEquals(header, reportHeader);
	logger.info("Report work Page header verified");
	
	// Verify labor + icon is not displayed 
	assertFalse(reportWorkPage.isLaborButtonPresent(),"Labor + icon should not display");
	
	// Verify Material button is not displayed 
	assertFalse(reportWorkPage.isMaterialButtonPresent(),"Material button should not be displayed");
	
	// Verify Tools icon should not be displayed 
	assertFalse(reportWorkPage.isToolsButtonPresent(),"Tools icon should not be displayed");
	
}
	
	@Test(groups = {
    "priority3" }, description = "Verify system message while updating work order with an asset which is not in the selected location(with GL account)", timeOut = 9500000)
public void scenario28() throws Exception {

	MobileAutomationFramework maf = (MobileAutomationFramework) this.af;
	CreateWorkOrderPage createWO = new CreateWorkOrderPage(maf);
	EditWorkOrderPage editWODetailsPage = new EditWorkOrderPage(af);
	WorkOrderDetailsPage woDetails = new WorkOrderDetailsPage(af);
	ErrorPage errorpage = new ErrorPage(maf);
	
	errorpage.clickNavigatorMenu();

    //Click on plus icon on Navigator page
	createWO.getplusiconClick();

	// Click + create work order icon
	createWO.selectCreateWO();
	
	// Enter Description in create WO
	createWO.insertDescriptionOfWorkOrder(descriptionStr);

	// Enter priority
	createWO.priorityEnter(priority);
	assertTrue(createWO.searchForAssets(assetNum));
	
	// Verify location is populating automatically
	assertEquals(createWO.getLocationTextWODetailsPage(), locationNum);
	
	// Change asset number
	createWO.assetNumber(assetNum1);

	// Tap on Confirm work Order Creation button (check mark at the top of the page)
	createWO.clickWorkOrderCreate();
	
	// GRAPHITE-71792- uncomment this once bug is fixed 
	//assertEquals("The specified asset is not in the current location. Do you want to update the location with this asset's location - " +locationNum+ "?",editWODetailsPage.systemMessage());
	// Close system message
	//editWODetailsPage.systemMessageClose();
	
}
	 
	
	protected void createDefaultObjects() throws Exception {
		try {
			// Create a location
			logger.info("Creating a location");
			locationNum = AbstractAutomationFramework.randomString(5).toUpperCase();
			Location newlocation = new Location();

			newlocation.setLocationId(locationNum);
			newlocation.setDescription(LOCATION_DESCRIPTION);
			newlocation.setSiteId(SetupData.SITEID);
			assertEquals(maximoApi.create(newlocation), SetupData.CreatedSuccess);
			logger.info("location: {}" + locationNum);
			
			// Create an asset
			logger.info("Creating an asset");
			assetNum = AbstractAutomationFramework.randomString(5).toUpperCase();
			String assetResult = maximoApi.retrieve(new Asset(),
					"addid=1&internalvalues=1&action=system:new&addschema=1");

			Asset newAsset = new Gson().fromJson(assetResult, Asset.class);

			newAsset.setAssetNum(assetNum);
			newAsset.setDescription(assetdescription);
			newAsset.setSiteId(SetupData.SITEID);
			newAsset.setLocation(locationNum);
			newAsset.setStatus(SetupData.LocAssetStatus.ACTIVE.toString());

			assertEquals(maximoApi.create(newAsset), SetupData.CreatedSuccess);
			logger.info("Asset: {}" + assetNum);
			// Create one more location
			logger.info("Creating other location");
			locationNum1 = AbstractAutomationFramework.randomString(5).toUpperCase();
			Location newlocation1 = new Location();

			newlocation1.setLocationId(locationNum1);
			newlocation1.setDescription(LOCATION_DESCRIPTION1);
			newlocation1.setSiteId(SetupData.SITEID);
			newlocation1.setStatus(SetupData.LocAssetStatus.OPERATING.toString());

			assertEquals(maximoApi.create(newlocation1), SetupData.CreatedSuccess);
			logger.info("location: {}" + locationNum1);
			
			// Create one more asset
			logger.info("Creating other asset");
			assetNum1 = AbstractAutomationFramework.randomString(5).toUpperCase();
			String assetResult1 = maximoApi.retrieve(new Asset(),
					"addid=1&internalvalues=1&action=system:new&addschema=1");

			Asset newAsset1 = new Gson().fromJson(assetResult1, Asset.class);

			newAsset1.setAssetNum(assetNum1);
			newAsset1.setDescription(assetdescription1);
			newAsset1.setLocation(locationNum1);
			newAsset1.setSiteId(SetupData.SITEID);
			newAsset1.setLocation(locationNum1);
			newAsset1.setStatus(SetupData.LocAssetStatus.OPERATING.toString());
			assertEquals(maximoApi.create(newAsset1), SetupData.CreatedSuccess);
			logger.info("Asset: {}" + assetNum1);

			
			apiCodeSuccess = true;
		} catch (AssertionError e) {
			logger.info(" SkipException apiCodeSuccess = " + apiCodeSuccess);
			apiCodeSuccess = false;
			throw new Exception("Test Setup API Failed,Stopping execution.");
		}
}

	// to verify date format
	public String dateFormat(int startYear, int startMonth, int startDate) {

	      String strDate = null;
	      try {
	         SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
	         Date date1 = formatter.parse(startDate+"/"+startMonth+"/"+startYear);

	         formatter = new SimpleDateFormat("MMMM dd, yyyy");
	       strDate = formatter.format(date1);
	      System.out.println("Date Format with dd MMMM yyyy : "+strDate);


	      } catch (Exception e) {
	          e.printStackTrace();}

	      return strDate;
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