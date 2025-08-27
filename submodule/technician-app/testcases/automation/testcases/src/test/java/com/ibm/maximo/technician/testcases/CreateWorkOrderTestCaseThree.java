package com.ibm.maximo.technician.testcases;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;

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
import com.ibm.maximo.automation.mobile.api.json.WorkOrder;
import com.ibm.maximo.automation.mobile.common.AppSwitcher;
import com.ibm.maximo.automation.mobile.common.AppSwitcher.App;
import com.ibm.maximo.automation.mobile.page.login.LoginPage;
import com.ibm.maximo.technician.framework.TechnicianTest;
import com.ibm.maximo.technician.page.CreateWorkOrderPage;
import com.ibm.maximo.technician.page.EditWorkOrderPage;
import com.ibm.maximo.technician.page.ErrorPage;
import com.ibm.maximo.technician.page.MySchedulePage;
import com.ibm.maximo.technician.page.WorkOrderDetailsPage;
import com.ibm.maximo.technician.setupdata.SetupData;
import com.ibm.maximo.technician.setupdata.SetupData.WoStatus;
import com.ibm.maximo.technician.setupdata.SetupData.WorkType;

//GRAPHITE-68895: [TECHMOBILE] Create Work Order(Part Three) Scenario-4 :41M,1TA,0A

public class CreateWorkOrderTestCaseThree extends TechnicianTest {

	private final Logger logger = LoggerFactory.getLogger(CreateWorkOrderTestCaseThree.class);
	public AbstractAutomationFramework af;
	public MobileAutomationFramework maf;
	private TestSuite testSuite;
	private MaximoApi maximoApi;
	private String assetNum, locationNum, labor, timezone;
	private int startYear = 2022, finishYear = 2030, startMonth = 8, startDate = 10, hour = 10, minute = 10;
	private String descriptionStr = "Description of WO";
	private String changeDescriptionStr = "Change Description of WO";
	private String longDescriptionStr = "Long Description Added";
	private String assetdescription = "ASSET_1";
	private WorkOrder newWorkOrder;
	private String LOCATION_DESCRIPTION = "INVALID_LOCATION_1";
	private String priority = "1";
	private String hrs = "20", min = "20";
	private final String graphiteNavigatorMenuBtnLocator = "NavigatorMenuButton";

	@BeforeClass(alwaysRun = true)
	@Parameters({ "configPath" })
	public void setup(String configPath) throws Exception {
		logger.info("********************CreateWorkOrderTestCaseThree*********************************");
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
			defaultInformationOfUserTimeZone(timezone, labor);
		} catch (Exception e) {
			e.printStackTrace();
		}
		login(af);
	}

	@AfterClass(alwaysRun = true)
	public void teardown() throws Exception {
		// timezone is blank in default information of user
		defaultInformationOfUserTimeZone("", labor);
		maximoApi.changeStatus(newWorkOrder, WoStatus.COMP.toString());
		logOut(af);
		if (testSuite != null) {
			testSuite.teardown();
		}
	}

	@Test(groups = { "priority2" }, description = "Default time", timeOut = 19500000)
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
		createWO.scheduledStartDateWithoutTime(startYear, startMonth, startDate);
		assertTrue((getCurrentUtcHoursStartDate().contains(createWO.scheduledStartDateGetTime())
				|| getCurrentUtcHoursStartDatePlusOneMin().contains(createWO.scheduledStartDateGetTime())));

		// Select Schedule finish date and time
		createWO.scheduledFinishDatewithoutTime(finishYear, startMonth, startDate);

		assertTrue((getCurrentUtcHoursStartDate().contains(createWO.scheduledFinishDateGetTime())
				|| getCurrentUtcHoursStartDatePlusOneMin().contains(createWO.scheduledFinishDateGetTime())));

		// Enter Estimated Time
		createWO.estDurHrsLocator(hrs);
		createWO.estDurMinsLocator(min);

		// Click chevron to select work type
		createWO.changeWorkType(WorkType.CM);

		// Verify that the asset record is found and selected
		assertTrue(createWO.searchForAssets(assetNum));

		// Verify that correct location record is found
		assertTrue(createWO.searchForLocation(locationNum));

		// Tap on Confirm work Order Creation button (check mark at the top of the page)
		createWO.clickWorkOrderCreate();

		// Assert to verify asset number displayed is correct
		createWO.clickBackButton(maf);
		logger.info("Navigate to list page");

		createWO.checkSearchButtonAvailbility();
		logger.info("Verify search button is available");
	}

	@Test(groups = { "priority2" }, description = "Verify save and discard prompt occur.", timeOut = 11500000)
	public void createWorkOrder2() throws Exception {
		MobileAutomationFramework maf = (MobileAutomationFramework) this.af;
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage workOrderDetailsPage = new WorkOrderDetailsPage(af);
		EditWorkOrderPage editWODetailsPage = new EditWorkOrderPage(af);
		ErrorPage errorpage = new ErrorPage(af);
		AppSwitcher appSwitcher = new AppSwitcher(af);
		CreateWorkOrderPage createWO = new CreateWorkOrderPage(af);
		// Search the WO
		assertEquals(true, assignedWorkPage.search(woNum));
		// Navigate to work order details page
		assignedWorkPage.openCardDetails();

		workOrderDetailsPage.editWorkOrderDetails();

		// Edit work order Details
		editWODetailsPage.enterDescription(changeDescriptionStr + woNum);
		editWODetailsPage.enterLongDescription(longDescriptionStr);
		editWODetailsPage.enterPriority(priority);

		// Click on 9 dots(Navigator menu)
		errorpage.clickNavigatorMenu();

		// Click on My Schedule
		appSwitcher.switchApp(App.MySchedule);
		createWO.saveDiscradPop();
		logger.info("Save and Discard pop-up appears");
		// Click on discard button
		createWO.clickDiscardButton();
		logger.info("Discard button clicked");

		createWO.clickBackButton(maf);
		logger.info("Navigate to list page");

		createWO.checkSearchButtonAvailbility();
		logger.info("Verify search button is available");

	}

	// These test case is part of P2 Automation - https://github.ibm.com/maximo-app-framework/technician-app/blob/MAS-8.11/testcases/Navigator/TC_CreateWorkOrder.md#scenario-39---verify-that-create-work-order--menu-button-is-either-disabled-or-hidden-when-technician-taps-on--button-and-technician-do-not-have-permission-for-create-new-work-ordercreatenewwo-sig-option 
	// This test cases can't be automated. It needs removal of permission of MXAPIWODETAIL object structure which is part of security group. Its not depending on users. If we remove permission and then again permission need to provided if these test cases failed then rest all testcases will fail in suite. There is no API at present.
	// app.username2 = abby & app.password2=abby should be present in respective
	// Config file for assetmanager
	@Test(groups = {
			"priority2" }, enabled=false, description = "Verify that 'Create work order +' menu button is either disabled ", timeOut = 11500000)
	public void createWorkOrder3() throws Exception {
		MobileAutomationFramework maf = (MobileAutomationFramework) this.af;

		CreateWorkOrderPage createWO = new CreateWorkOrderPage(af);

		logOut(af);

		// Relogin to enter different credentials
		reLoginWithDifferentCredentials();

		// Click on plus icon on Navigator page
		createWO.getplusiconClick();

		// Create Work Order Should be disabled
		assertEquals(true, createWO.isCreateWODisabled());
	}

	protected void createDefaultObjects() throws Exception {

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

		// Create a workorder
		logger.info("Creating a work order");
		String workOrderResult = maximoApi.retrieve(new WorkOrder(),
				"addid=1&internalvalues=1&action=system:new&addschema=1");
		newWorkOrder = new Gson().fromJson(workOrderResult, WorkOrder.class);
		newWorkOrder.setDescription("WorkeOrder for mobile automation test");
		newWorkOrder.setSiteId(SetupData.SITEID);
		newWorkOrder.setAssetNum(assetNum);
		newWorkOrder.setWorkType(WorkType.PM.toString());
		newWorkOrder.setLocation(locationNum);
		maximoApi.create(newWorkOrder);
		woNum = newWorkOrder.getWoNum();
		logger.info("Work Order: {}" + woNum);

		// Assignment with labor maxadmin
		maximoApi.addAssignmentLabor(newWorkOrder, labor);
		logger.info("Assignment added");
		// Change WO status to Approved
		logger.info("Changing work order status to APPR");
		maximoApi.changeStatus(newWorkOrder, WoStatus.APPR.toString());
	}

	// Relogin to enter different credentials
	public void reLoginWithDifferentCredentials() throws Exception {
		try {
			logger.info("ReLogin with different user");
			MobileAutomationFramework maf = (MobileAutomationFramework) this.af;
			lp = LoginPage.getLoginPage(maf);
			Properties props = this.af.getProperties();
			logger.info("Username " + props.getProperty("app.username2"));
			logger.info("Password " + props.getProperty("app.password2"));
			lp.loginDirectly(props.getProperty("app.username2"), props.getProperty("app.password2"));

			maf.waitForElementToBePresent(By.id(graphiteNavigatorMenuBtnLocator), 5000);
			maf.waitForElementToBePresent(By.id(graphiteNavigatorMenuBtnLocator)).click();
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("user is not present");

		}
	}

	// create getCurrentUtcTime() method to get the current UTC time
	public String getCurrentUtcHoursStartDate() throws Exception { // handling ParseException
		// create an instance of the SimpleDateFormat class
		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
		// set UTC time zone by using SimpleDateFormat class
		sdf.setTimeZone(TimeZone.getTimeZone(timezone));
		// create another instance of the SimpleDateFormat class for local date format
		SimpleDateFormat ldf = new SimpleDateFormat("hh:mm a");
		// declare and initialize a date variable which we return to the main method
		Date d1 = null;
		String time1 = null;
		// use try catch block to parse date in UTC time zone
		try {
			// parsing date using SimpleDateFormat class
			d1 = ldf.parse(sdf.format(new Date()));
			time1 = ldf.format(d1).toString();
			logger.info("Issue with Startdate time1" + time1);
		}
		// catch block for handling ParseException
		catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info("Issue with Startdate");
		}

		return time1;

	}

	// create getCurrentUtcTime() method to get the current UTC time pluse 1 min
	public String getCurrentUtcHoursStartDatePlusOneMin() throws Exception { // handling ParseException
		// create an instance of the SimpleDateFormat class
		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
		// set UTC time zone by using SimpleDateFormat class
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		// create another instance of the SimpleDateFormat class for local date format
		SimpleDateFormat ldf = new SimpleDateFormat("hh:mm a");
		// declare and initialize a date variable which we return to the main method
		Date d1 = null;
		String newTime = null;
		// use try catch block to parse date in UTC time zone
		try {
			// parsing date using SimpleDateFormat class
			d1 = ldf.parse(ldf.format(new Date()));

			Calendar cal = Calendar.getInstance();
			cal.setTime(d1);
			cal.add(Calendar.MINUTE, 1);
			newTime = sdf.format(cal.getTime());

		}
		// catch block for handling ParseException
		catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info("Issue with getCurrentUtcHoursStartDatePlusOneMin()");
		}

		return newTime;

	}

	// Generated by WCA for GP
	/**
	 * Sets the system properties of the Default Information timezone.
	 *
	 * @param timezone the value to set , labor the value to set
	 */
	public void defaultInformationOfUserTimeZone(String timezone, String labor) {
		jdbcConnection.executeUpdateSQL("UPDATE MAXIMO.PERSON SET MAXIMO.PERSON.TIMEZONE = '" + timezone
				+ "' WHERE MAXIMO.PERSON.PERSONID = '" + labor + "'");
	}
}
