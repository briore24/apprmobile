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

import com.google.gson.Gson;
import com.ibm.maximo.automation.mobile.AbstractAutomationFramework;
import com.ibm.maximo.automation.mobile.FrameworkFactory;
import com.ibm.maximo.automation.mobile.MobileAutomationFramework;
import com.ibm.maximo.automation.mobile.api.MaximoApi;
import com.ibm.maximo.automation.mobile.api.json.Asset;
import com.ibm.maximo.automation.mobile.api.json.Location;
import com.ibm.maximo.technician.framework.TechnicianTest;
import com.ibm.maximo.technician.page.CreateWorkOrderPage;
import com.ibm.maximo.technician.page.EditWorkOrderPage;
import com.ibm.maximo.technician.page.ErrorPage;
import com.ibm.maximo.technician.page.ReportWorkPage;
import com.ibm.maximo.technician.page.WorkOrderDetailsPage;
import com.ibm.maximo.technician.setupdata.SetupData;
import com.ibm.maximo.technician.setupdata.SetupData.WorkType;

//GRAPHITE-68895: [TECHMOBILE] Create Work Order(Part Three) Scenario-4 :41M,1TA,0A
//GRAPHITE-69751: [TECHMOBILE] Create Work Order(Part Two):41M,3TA,2A

public class CreateWorkOrderTestCaseTwo extends TechnicianTest {

	private final Logger logger = LoggerFactory.getLogger(CreateWorkOrderTestCaseTwo.class);
	public AbstractAutomationFramework af;
	private TestSuite testSuite;
	private MaximoApi maximoApi;
	private String assetNum, locationNum, assetNum1, labor, locationNum1, assetNum2, assetNum3, locationNum2,
			locationNum3, assetNum4, locationNum4, assetNum5;
	private int startYear = 2022, finishYear = 2030, startMonth = 8, startDate = 10, hour = 10, minute = 10;
	private String descriptionStr = "Description of WO";
	private String longDescriptionStr = "Long Description Added";
	private String assetdescription = "ASSET_1";
	private String LOCATION_DESCRIPTION = "INVALID_LOCATION_1";
	private String priority = "1";
	private String hrs = "20", min = "20";

	@BeforeClass(alwaysRun = true)
	@Parameters({ "configPath" })
	public void setup(String configPath) throws Exception {
		logger.info("********************CreateWorkOrderTestCaseTwo*********************************");
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
			createDefaultObjects2();
			createDefaultObjects3();
		} catch (Exception e) {
			e.printStackTrace();
		}
		login(af);
		MobileAutomationFramework maf = (MobileAutomationFramework) this.af;
	}
	
	@AfterClass(alwaysRun = true)
	public void teardown() throws Exception {
		logOut(af);
		logger.info("Delete FAILURECODE via DB query>>"+"DELETE FROM MAXIMO.FAILURELIST WHERE FAILURECODE='"+failureCodeClass+"' OR FAILURECODE='"+failureCodeProblem+"' OR FAILURECODE='"+failureCodeCause+"' OR FAILURECODE='"+failureCodeRemedy+"'");
		// Delete FAILURECODE via DB query
		jdbcConnection.executeUpdateSQL("DELETE FROM MAXIMO.FAILURELIST WHERE FAILURECODE='"+failureCodeClass+"' OR FAILURECODE='"+failureCodeProblem+"' OR FAILURECODE='"+failureCodeCause+"' OR FAILURECODE='"+failureCodeRemedy+"'");
		if (testSuite != null) {
			testSuite.teardown();
		}
	}

	@Test(groups = { "mobile" }, description = "Verify save and discard prompt occur.", timeOut = 1500000)
	public void createWorkOrder() throws Exception {
		MobileAutomationFramework maf = (MobileAutomationFramework) this.af;
		CreateWorkOrderPage createWO = new CreateWorkOrderPage(maf);
		ErrorPage errorpage = new ErrorPage(maf);

		// Click on 9 dots(Navigator menu)
		errorpage.clickNavigatorMenu();

		// Click on plus icon on Navigator page
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

		// Select Schedule start date and time
		createWO.scheduledStartDateAndTime(startYear, startMonth, startDate, hour, minute);

		// Select Schedule finish date and time
		createWO.scheduledFinishDateAndTime(finishYear, startMonth, startDate, hour, minute);

		// Enter Estimated Time
		createWO.estDurHrsLocator(hrs);
		createWO.estDurMinsLocator(min);

		// Click chevron to select work type
		createWO.changeWorkType(WorkType.CM);

		// Verify that the asset record is found and selected
		assertTrue(createWO.searchForAssets(assetNum));

		// Verify that correct location record is found
		assertTrue(createWO.searchForLocation(locationNum));

		createWO.clickBackChevronfromDetails();
		logger.info("Clicking Back Chevron from list page");

		createWO.saveDiscradPop();
		logger.info("Save and Discard pop-up appears");

		createWO.clickDiscardButton();
		logger.info("Discard button clicked");

		createWO.checkSearchButtonAvailbility();
		logger.info("Navigate to list page");

		// Click on 9 dots(Navigator menu)
		errorpage.clickNavigatorMenu();

		// Click on plus icon on Navigator page
		createWO.getplusiconClick();

		// Click on Create Work Order
		createWO.selectCreateWO();

		// Enter Description in create WO
		createWO.insertDescriptionOfWorkOrder(descriptionStr);

		// Enter Long description
		createWO.enterLongDescription(longDescriptionStr);

		// Enter priority
		createWO.priorityEnter(priority);

		createWO.clickBackChevronfromDetails();
		logger.info("Clicking Back Chevron from list page");

		createWO.saveDiscradPop();
		logger.info("Save and Discard pop-up appears");

		createWO.clickSaveButton();
		logger.info("Save button clicked");

		createWO.checkSearchButtonAvailbility();
		logger.info("Navigate to list page");

	}

	@Test(groups = {
			"priority2" }, enabled = true, description = "Verify Location is not automatically populating while one location mapped with multiple asset.", timeOut = 1500000)
	public void createWorkOrderWithOneLocationMultipleAsset() throws Exception {
		MobileAutomationFramework maf = (MobileAutomationFramework) this.af;
		CreateWorkOrderPage createWO = new CreateWorkOrderPage(maf);
		ErrorPage errorpage = new ErrorPage(maf);

		// Click on 9 dots(Navigator menu)
		errorpage.clickNavigatorMenu();

		// Click on plus icon on Navigator page
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

		// Select Schedule start date and time
		createWO.scheduledStartDateAndTime(startYear, startMonth, startDate, hour, minute);

		// Select Schedule finish date and time
		createWO.scheduledFinishDateAndTime(finishYear, startMonth, startDate, hour, minute);

		// Enter Estimated Time
		createWO.estDurHrsLocator(hrs);
		createWO.estDurMinsLocator(min);
		createWO.changeWorkType(WorkType.CM);
		// createWO.assetEnter(assetNum1);
		createWO.enterLocation(locationNum4);
		

		// Verify no asset is populating
		assertEquals(createWO.getAssetTextWODetailsPage(), "");
		logger.info("Verify no asset is populating");

		// Clicking Back Chevron from list page
		createWO.clickBackChevronfromDetails();
		logger.info("Clicking Back Chevron from list page");

		// Save and Discard pop-up appears
		createWO.saveDiscradPop();
		logger.info("Save and Discard pop-up appears");

		// Discard button clicked
		createWO.clickDiscardButton();
		logger.info("Discard button clicked");
		// Bug https://jsw.ibm.com/browse/GRAPHITE-72728
		// Navigate to list page
		createWO.checkSearchButtonAvailbility();
		logger.info("Navigate to list page");

	}

	@Test(groups = {
			"priority2" }, enabled = true, description = "Verify location is pupulating automatically with failure class in failure report ", timeOut = 2500000)
	public void createWorkOrderWithOneLocationOneAsset() throws Exception {
		MobileAutomationFramework maf = (MobileAutomationFramework) this.af;
		CreateWorkOrderPage createWO = new CreateWorkOrderPage(maf);
		ErrorPage errorpage = new ErrorPage(maf);
		WorkOrderDetailsPage workOrderDetailsPage = new WorkOrderDetailsPage(af);
		ReportWorkPage reportWorkPage = new ReportWorkPage(af);

		// Click on 9 dots(Navigator menu)
		errorpage.clickNavigatorMenu();
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

		// Enter priority
		createWO.priorityEnter(priority);
		logger.info("Enter priority");
		maf.scrollPage(1, 2);

		// Select Schedule start date and time
		createWO.scheduledStartDateAndTime(startYear, startMonth, startDate, hour, minute);
		logger.info("Select Schedule start date and time");

		// Select Schedule finish date and time
		createWO.scheduledFinishDateAndTime(finishYear, startMonth, startDate, hour, minute);
		logger.info("Select Schedule finish date and time");

		// Enter Estimated Time
		createWO.estDurHrsLocator(hrs);
		createWO.estDurMinsLocator(min);
		logger.info("Enter Estimated Time");
		
		// Click chevron to select work type
		logger.info("Click chevron to select work type");
		createWO.changeWorkType(WorkType.CM);
		
		// Enter Asset
		createWO.searchForAssets(assetNum3);
		logger.info("Enter Asset");

		// Verify location is populating automatically
		assertEquals(createWO.getLocationTextWODetailsPage(), locationNum3);
		logger.info("Verify location is populating automatically");

		// Click on Create workorder button
		createWO.clickWorkOrderCreate();
		logger.info("Enter Estimated Time");

		maf.scrollPage(1, 2);

		// verify asset number
		assertEquals(assetNum3, workOrderDetailsPage.getTextWOAssetNum());
		logger.info("verify asset number");

		// verify failure class contains value
		workOrderDetailsPage.clickReportWorkButton();
		assertTrue(reportWorkPage.getFailureClass().contains(failureCodeClass));
		logger.info("verify failure class contains value");

		reportWorkPage.clickBackChevron();
		logger.info("Click on failure class back button");
		maf.scrollPage(1, 2);

		assertEquals(workOrderDetailsPage.getAssetID(), assetNum3);
		logger.info("Verify asset number on workorder details page");

		createWO.clickBackButton(maf);
		logger.info("Navigate to list page");

		createWO.checkSearchButtonAvailbility();
		logger.info("Verify search button is available");

	}
	
	@Test(groups = {
			"priority2" }, enabled = true, description = "When updating a location with multiple assets, warning message should be displayed and asset text field should be blank", timeOut = 2500000)
	public void createWorkOrderMultipleAssetWithLocation() throws Exception {
		MobileAutomationFramework maf = (MobileAutomationFramework) this.af;
		CreateWorkOrderPage createWO = new CreateWorkOrderPage(maf);
		ErrorPage errorpage = new ErrorPage(maf);
		WorkOrderDetailsPage workOrderDetailsPage = new WorkOrderDetailsPage(af);
		ReportWorkPage reportWorkPage = new ReportWorkPage(af);
		EditWorkOrderPage editWODetailsPage = new EditWorkOrderPage(af);

		// Click on 9 dots(Navigator menu)
		errorpage.clickNavigatorMenu();
		logger.info("Clicking Back Chevron from list page");

		// Click on plus icon on Navigator page
		createWO.getplusiconClick();
		logger.info("Clicking Back Chevron from list page");

		// Click on Create Work Order
		createWO.selectCreateWO();

		// Enter Description in create WO
		createWO.insertDescriptionOfWorkOrder(descriptionStr);
		logger.info("Clicking Back Chevron from list page");

		// Enter Asset
		createWO.searchForAssets(assetNum3);
		logger.info("Enter Asset");

		// Verify location is populating automatically
		assertEquals(createWO.getLocationTextWODetailsPage(), locationNum3);
		logger.info("Verify location is populating automatically");

		createWO.searchForLocation(locationNum1);
		logger.info("Added location which has two locations assosiated");

		// Verify the error message 
		assertEquals(
				"The location is not associated with the selected asset. If you keep the location, the asset will be cleared.",
				editWODetailsPage.systemMessage());

		// Close system message
		editWODetailsPage.systemMessageYes();

		//Uncomment after this issue is fixed -https://jsw.ibm.com/browse/MAXMOA-5535
		// verify asset number
		//assertEquals("", workOrderDetailsPage.getTextWorkOrderAsset());
		//createWO.searchAsset();

	}

	protected void createDefaultObjects() throws Exception {
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

		maximoApi.create(newAsset);
		logger.info("Asset: {}" + assetNum);

		// Create a location
		logger.info("Creating a location");
		locationNum = AbstractAutomationFramework.randomString(5).toUpperCase();
		Location newlocation = new Location();

		newlocation.setLocationId(locationNum);
		newlocation.setDescription(LOCATION_DESCRIPTION);
		newlocation.setSiteId(SetupData.SITEID);

		maximoApi.create(newlocation);
		logger.info("location: {}" + locationNum);

	}

//Generated by WCA for GP
	/**
	 * Create default objects for testing
	 * 
	 * @throws Exception
	 */
	protected void createDefaultObjects2() throws Exception {

		logger.info("Creating default objects 2");
		// Create a location
		logger.info("Creating a location");
		locationNum3 = AbstractAutomationFramework.randomString(5).toUpperCase();
		Location newlocation = new Location();

		newlocation.setLocationId(locationNum3);
		newlocation.setDescription(LOCATION_DESCRIPTION);
		newlocation.setSiteId(SetupData.SITEID);
		newlocation.setGlaccount(SetupData.GLDEBITACCT);
		newlocation.setFailurecode(failureCodeClass);
		newlocation.setType(SetupData.LocType.OPERATING.toString());
		newlocation.setLocpriority("1");

		maximoApi.create(newlocation);
		logger.info("location3: {}" + locationNum3);

		// Create an asset
		logger.info("Creating an asset");
		assetNum3 = AbstractAutomationFramework.randomString(5).toUpperCase();
		String assetResult = maximoApi.retrieve(new Asset(), "addid=1&internalvalues=1&action=system:new&addschema=1");

		Asset newAsset = new Gson().fromJson(assetResult, Asset.class);

		newAsset.setAssetNum(assetNum3);
		newAsset.setDescription("asset description");
		newAsset.setSiteId(SetupData.SITEID);
		newAsset.setLocation(locationNum3);
		newAsset.setStatus(SetupData.LocAssetStatus.ACTIVE.toString());

		maximoApi.create(newAsset);
		logger.info("Asset3: {}" + assetNum3);
	}

//Generated by WCA for GP
	/**
	 * Create default objects for testing
	 * 
	 * @throws Exception
	 */
	protected void createDefaultObjects3() throws Exception {

		logger.info("Creating default objects 3");
		// Create a location
		logger.info("Creating a location");
		locationNum4 = AbstractAutomationFramework.randomString(5).toUpperCase();
		Location newlocation = new Location();

		newlocation.setLocationId(locationNum4);
		newlocation.setDescription("Loc Desc");
		newlocation.setSiteId(SetupData.SITEID);
		newlocation.setGlaccount(SetupData.GLDEBITACCT);
		newlocation.setFailurecode(failureCodeClass);
		newlocation.setType(SetupData.LocType.OPERATING.toString());
		newlocation.setLocpriority("1");

		maximoApi.create(newlocation);
		logger.info("location4: {}" + locationNum4);

		// Create an asset
		logger.info("Creating an asset");
		assetNum4 = AbstractAutomationFramework.randomString(5).toUpperCase();
		String assetResult = maximoApi.retrieve(new Asset(), "addid=1&internalvalues=1&action=system:new&addschema=1");

		Asset newAsset = new Gson().fromJson(assetResult, Asset.class);

		newAsset.setAssetNum(assetNum4);
		newAsset.setDescription("asset description");
		newAsset.setSiteId(SetupData.SITEID);
		newAsset.setLocation(locationNum4);
		newAsset.setStatus(SetupData.LocAssetStatus.ACTIVE.toString());

		maximoApi.create(newAsset);
		logger.info("Asset4: {}" + assetNum4);

		// Create an asset
		logger.info("Creating an asset");
		assetNum5 = AbstractAutomationFramework.randomString(5).toUpperCase();
		String assetResult1 = maximoApi.retrieve(new Asset(), "addid=1&internalvalues=1&action=system:new&addschema=1");

		Asset newAsset1 = new Gson().fromJson(assetResult1, Asset.class);

		newAsset1.setAssetNum(assetNum5);
		newAsset1.setDescription("asset description");
		newAsset1.setSiteId(SetupData.SITEID);
		newAsset1.setLocation(locationNum4);
		newAsset1.setStatus(SetupData.LocAssetStatus.ACTIVE.toString());

		maximoApi.create(newAsset1);
		logger.info("Asset5: {}" + assetNum5);
		
		// Create a location and link with two assets
		logger.info("Creating a location by S");
		locationNum1 = AbstractAutomationFramework.randomString(5).toUpperCase();
		Location newlocation1 = new Location();

		newlocation1.setLocationId(locationNum1);
		newlocation1.setDescription("Loc Desc");
		newlocation1.setSiteId(SetupData.SITEID);
		newlocation1.setGlaccount(SetupData.GLDEBITACCT);
		newlocation1.setFailurecode(failureCodeClass);
		newlocation1.setType(SetupData.LocType.OPERATING.toString());
		newlocation1.setLocpriority("1");

		maximoApi.create(newlocation1);
		logger.info("location1: {}" + locationNum1);
		
		// Create an asset
		logger.info("Creating an asset");
		assetNum1 = AbstractAutomationFramework.randomString(5).toUpperCase();
		String assetResult2 = maximoApi.retrieve(new Asset(), "addid=1&internalvalues=1&action=system:new&addschema=1");

		Asset newAsset2 = new Gson().fromJson(assetResult2, Asset.class);

		newAsset2.setAssetNum(assetNum1);
		newAsset2.setDescription("asset description");
		newAsset2.setSiteId(SetupData.SITEID);
		newAsset2.setLocation(locationNum1);
		newAsset2.setStatus(SetupData.LocAssetStatus.ACTIVE.toString());

		maximoApi.create(newAsset2);
		logger.info("Asset1: {}" + assetNum1);
	
		
		// Create an asset
		logger.info("Creating an asset");
		assetNum2 = AbstractAutomationFramework.randomString(5).toUpperCase();
		String assetResult3 = maximoApi.retrieve(new Asset(), "addid=1&internalvalues=1&action=system:new&addschema=1");

		Asset newAsset3 = new Gson().fromJson(assetResult3, Asset.class);

		newAsset3.setAssetNum(assetNum2);
		newAsset3.setDescription("asset description");
		newAsset3.setSiteId(SetupData.SITEID);
		newAsset3.setLocation(locationNum1);
		newAsset3.setStatus(SetupData.LocAssetStatus.ACTIVE.toString());

		maximoApi.create(newAsset3);
		logger.info("Asset2: {}" + assetNum2);

	}

}
