package com.ibm.maximo.technician.testcases;

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
import com.ibm.maximo.automation.mobile.api.MaximoApi;
import com.ibm.maximo.automation.mobile.api.json.Asset;
import com.ibm.maximo.automation.mobile.api.json.AssetMeter;
import com.ibm.maximo.automation.mobile.api.json.Location;
import com.ibm.maximo.automation.mobile.api.json.LocationMeter;
import com.ibm.maximo.automation.mobile.api.json.Meter;
import com.ibm.maximo.automation.mobile.api.json.WorkOrder;
import com.ibm.maximo.technician.framework.TechnicianTest;
import com.ibm.maximo.technician.page.MetersPage;
import com.ibm.maximo.technician.page.MySchedulePage;
import com.ibm.maximo.technician.page.WorkOrderDetailsPage;
import com.ibm.maximo.technician.setupdata.SetupData;
import com.ibm.maximo.technician.setupdata.SetupData.*;

/*
 * GRAPHITE-47693:[MobileAutomation] Eli should be able to enter meter readings
 * GRAPHITE-64789: [TECHMOBILE] Meters P2 :38M,5TA,5A
 * GRAPHITE-68944: [TECHMOBILE] Meters P2 :38M,1TA,1A
 * GRAPHITE-72367: [TECHMOBILE] Meters P3 :38M,1TA,0A
 * */
public class MetersTest extends TechnicianTest {
	private final Logger logger = LoggerFactory.getLogger(MetersTest.class);
	private AbstractAutomationFramework af;
	private TestSuite testSuite;
	private MaximoApi maximoApi;
	private WorkOrder newWorkOrder, newWorkOrder1;
	private String woNum, woNum1, labor, location, timezone;
	private String enterMeterValue = "123";
	private int numOfMeterWithWO=3;

	@BeforeClass(alwaysRun = true)
	@Parameters({ "configPath" })
	public void setup(String configPath) throws Exception {
		logger.info("********************MetersTest*********************************");
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
			// timezone is set in default information of user
			defaultInformationOfUserTimeZone(timezone, labor);
		} catch (Exception e) {
			e.printStackTrace();
		}
		login(af);
	}

	@AfterClass(alwaysRun = true)
	public void teardown() throws Exception {
		// timezone is blank in default information of user
		defaultInformationOfUserTimeZone("",labor);
		logOut(af);
		// Change WO status to Completed
		logger.info("Changing work order status to COMP");
		maximoApi.changeStatus(newWorkOrder, WoStatus.COMP.toString());
		if (testSuite != null) {
			testSuite.teardown();
		}
	}

	@Test(groups = {
			"mobile" }, description = "Verify save/checkmark button is enabled when technician enters a valid meter reading in any of the associated meter", timeOut = 900000)
	public void aSearchForWorkOrderAndEnterMeterReadings() throws Exception {

		// Open work order details
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		MetersPage metersPage = new MetersPage(af);

		// Search the WO
		assertTrue(assignedWorkPage.search(woNum));
		// click on Meters Icon
		assignedWorkPage.clickMetersButton();
		assertFalse(metersPage.checkMeterValueBeforeReading());
		assertTrue(metersPage.addMeterReadings(enterMeterValue));
		assertTrue(metersPage.checkMeterValueAfterReading(enterMeterValue));
		metersPage.cancel();
	}

	@Test(groups = {
			"priority2" }, description = "Verify that the meters touch-point is displayed in 'Assets and locations' section on WO details page when meter is associated with WO asset and/or location", timeOut = 900000)
	public void aWoDetailMeterTouchPoints() throws Exception {
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		MetersPage metersPage = new MetersPage(af);

		// Search the WO
		assertTrue(assignedWorkPage.search(woNum));
		// Navigate to work order details page
		assignedWorkPage.openCardDetails();
		// Meters Icon
		assertTrue(metersPage.verifyMeterIconOnWODetailPage());
		// Back to WO list view
		WorkOrderDetailsPage.clickBackWOList();
		Thread.sleep(2000);
		// Clear Search Result
		assignedWorkPage.clickClearButton();
	}

	@Test(groups = {
			"priority2" }, description = "Verify that exact date and time when meter reading is entered in the field is captured when technician clicks on 'Save' button", timeOut = 900000)
	public void checkDateAndTimeAfterEnteringMeterReading() throws Exception {
		String enterMeterValue = "124";
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		MetersPage metersPage = new MetersPage(af);
		// Search the WO
		assertTrue(assignedWorkPage.search(woNum));
		// Navigate to work order details page
		assignedWorkPage.openCardDetails();
		// Meters Icon
		assertTrue(metersPage.verifyMeterIconOnWODetailPage());
		// click on Meters Icon
		metersPage.clickMetersButtonOnWoDetail();
		assertTrue(metersPage.addMeterReadingsFromWoDetail(enterMeterValue));
		assertTrue(metersPage.checkMeterValueAfterReadingDateAndTime(enterMeterValue, timezone));
		metersPage.cancelMetersDrawerFromWoDetail();

		// Back to WO list view
		WorkOrderDetailsPage.clickBackWOList();
		Thread.sleep(2000);
		// Clear Search Result
		assignedWorkPage.clickClearButton();
	}

	@Test(groups = {
			"priority2" }, description = "Verify that different date and time values are saved for the meter readings for different meters depending on the entered reading date and time in the fields", timeOut = 900000)
	public void enterDifferentMeterReadingAndVerifyDateAndTime() throws Exception {
		// Verify that new meter reading date and time is saved in
		// ASSETMETER/LOCATIONMETER.NEWREADINGDATE and is current system date and time

		String enterMeterValue = "125";
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		MetersPage metersPage = new MetersPage(af);
		// Search the WO
		assertTrue(assignedWorkPage.search(woNum));
		// Navigate to work order details page
		assignedWorkPage.openCardDetails();
		// Meters Icon
		assertTrue(metersPage.verifyMeterIconOnWODetailPage());
		// click on Meters Icon
		metersPage.clickMetersButtonOnWoDetail();
		assertTrue(metersPage.addMeterReadingsFromWoDetail(enterMeterValue));
		assertTrue(metersPage.checkMeterValueAfterReadingDateAndTime(enterMeterValue, timezone));
		metersPage.cancelMetersDrawerFromWoDetail();

		// Back to WO list view
		WorkOrderDetailsPage.clickBackWOList();
		Thread.sleep(2000);
		// Clear Search Result
		assignedWorkPage.clickClearButton();
	}

	@Test(groups = {
			"priority2" }, description = " Verify that technician can't save the meter readings when entered reading is less than last meter reading for continuous meter without rollover value set", timeOut = 900000)
	public void systemErrorContinuousMeterReading() throws Exception {
		String enterMeterValue = "126", LessMeterReadingThenLastReading = "122";

		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		MetersPage metersPage = new MetersPage(af);
		// Search the WO
		assertTrue(assignedWorkPage.search(woNum));
		// Navigate to work order details page
		assignedWorkPage.openCardDetails();
		// Meters Icon
		assertTrue(metersPage.verifyMeterIconOnWODetailPage());
		// click on Meters Icon
		metersPage.clickMetersButtonOnWoDetail();
		assertTrue(metersPage.addMeterReadingsFromWoDetail(enterMeterValue));
		assertTrue(metersPage.addContinuousMeterReadingsFromWoDetailForAsset(LessMeterReadingThenLastReading,
				numOfMeterWithWO));
		// Error message look : The new reading (122) entered on 10/19/23 11:54:42 am
		// should be greater than the previous reading (123) entered on (10/19/23
		// 11:54:00 am).
		assertTrue(metersPage.showErrorPageAndClose("should be greater than the previous reading"));
		assertTrue(metersPage.cancelUpdateMeterReadingScreen());
		assertTrue(metersPage.disCardUpdateMeterReading());

		assertTrue(metersPage.addContinuousMeterReadingsFromWoDetailForLocation(LessMeterReadingThenLastReading,
				numOfMeterWithWO));
		assertTrue(metersPage.showErrorPageAndClose("should be greater than the previous reading"));
		assertTrue(metersPage.cancelUpdateMeterReadingScreen());
		assertTrue(metersPage.disCardUpdateMeterReading());
		metersPage.cancelMetersDrawerFromWoDetail();

		// Back to WO list view
		WorkOrderDetailsPage.clickBackWOList();
		Thread.sleep(2000);
		// Clear Search Result
		assignedWorkPage.clickClearButton();
	}

	@Test(groups = {
			"priority3" }, description = "Verify that new meter readings are saved in ASSETMETER/LOCATIONMETER.NEWREADING field", timeOut = 900000)
	public void verifyNewMeterReadings() throws Exception {
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		MetersPage metersPage = new MetersPage(af);
		WorkOrderDetailsPage woDetailsPage= new WorkOrderDetailsPage(af);

		// Search the WO
		assertTrue(assignedWorkPage.search(woNum1));
		
		//Open WO details Page
		assignedWorkPage.openCardDetails();

		// Click on meter button on wo details Page
		woDetailsPage.clickMetersButton();

		// Add meter reading from wo details page
		assertTrue(metersPage.addMeterReadingsFromWoDetail(enterMeterValue));

		// Check meter value
		assertTrue(metersPage.VerifyMeterValuesFromWODetailsPage(enterMeterValue));
		metersPage.cancelMetersDrawerFromWoDetail();
		
		// Back to WO list view
		WorkOrderDetailsPage.clickBackWOList();
		// Clear Search Result
	    assignedWorkPage.clickClearButton();
	}

	protected void createDefaultObjects() throws Exception {
		logger.info("Creating default objects");

		String result = maximoApi.retrieve(new Meter(), "addid=1&internalvalues=1&action=system:new&addschema=1");
		Meter newMeterCharacteristic = new Gson().fromJson(result, Meter.class);

		newMeterCharacteristic.setMetername("MC" + newMeterCharacteristic.getMeterid());
		newMeterCharacteristic.setMetertype(MeterType.CHARACTERISTIC.toString());
		newMeterCharacteristic.setDescription("my meter " + newMeterCharacteristic.getMeterid());
		newMeterCharacteristic.setDomainid(MaxDomain.MAXTYPE.toString());

		// Creates Meter CHARACTERISTIC
		maximoApi.create(newMeterCharacteristic);
		logger.info("CHARACTERISTIC Meter: {}" + newMeterCharacteristic.getMeterid());

		String resultGauge = maximoApi.retrieve(new Meter(), "addid=1&internalvalues=1&action=system:new&addschema=1");
		Meter newMeterGauge = new Gson().fromJson(resultGauge, Meter.class);

		newMeterGauge.setMetername("MG" + newMeterGauge.getMeterid());
		newMeterGauge.setMetertype(MeterType.GAUGE.toString());
		newMeterGauge.setDescription("my meter " + newMeterGauge.getMeterid());

		// Creates Meter Gauge
		maximoApi.create(newMeterGauge);
		logger.info("GAUGE Meter: {}" + newMeterGauge.getMeterid());

		String resultContinuous = maximoApi.retrieve(new Meter(),
				"addid=1&internalvalues=1&action=system:new&addschema=1");
		Meter newMeterContinuous = new Gson().fromJson(resultContinuous, Meter.class);

		newMeterContinuous.setMetername("MCO" + newMeterContinuous.getMeterid());
		newMeterContinuous.setMetertype(MeterType.CONTINUOUS.toString());
		newMeterContinuous.setDescription("my meter " + newMeterContinuous.getMeterid());
		newMeterContinuous.setReadingtype(ReadingType.DELTA.toString());

		// Creates Meter CONTINUOUS
		maximoApi.create(newMeterContinuous);
		logger.info("CONTINUOUS Meter: {}" + newMeterContinuous.getMeterid());

		// Create an asset
		logger.info("Creating an asset");
		assetNum = AbstractAutomationFramework.randomString(5).toUpperCase();
		String assetResult = maximoApi.retrieve(new Asset(), "addid=1&internalvalues=1&action=system:new&addschema=1");

		Asset newAsset = new Gson().fromJson(assetResult, Asset.class);

		newAsset.setAssetNum(assetNum);
		newAsset.setDescription("Asset for mobile automation test");
		newAsset.setSiteId(SetupData.SITEID);

		List<AssetMeter> meterList = new ArrayList<AssetMeter>();

		AssetMeter assetMeterCharacteristic = new AssetMeter();
		assetMeterCharacteristic.setMetername(newMeterCharacteristic.getMetername());

		AssetMeter assetMeterGauge = new AssetMeter();
		assetMeterGauge.setMetername(newMeterGauge.getMetername());

		AssetMeter assetMeterContinuous = new AssetMeter();
		assetMeterContinuous.setAvgcalcmethod(AverageMethod.ALL.toString());
		assetMeterContinuous.setMetername(newMeterContinuous.getMetername());

		meterList.add(assetMeterCharacteristic);
		meterList.add(assetMeterGauge);
		meterList.add(assetMeterContinuous);
		newAsset.setAssetMeter(meterList);

		maximoApi.create(newAsset);
		logger.info("Asset: {}" + assetNum);

		// Create a location
		logger.info("Creating a location");
		location = AbstractAutomationFramework.randomString(5).toUpperCase();
		Location newlocation = new Location();

		newlocation.setLocationId(location);
		newlocation.setDescription("location for mobile automation test");
		newlocation.setSiteId(SetupData.SITEID);

		List<LocationMeter> locationMeterList = new ArrayList<LocationMeter>();

		LocationMeter locationMeterCharacteristic = new LocationMeter();
		locationMeterCharacteristic.setMetername(newMeterCharacteristic.getMetername());

		LocationMeter locationMeterGauge = new LocationMeter();
		locationMeterGauge.setMetername(newMeterGauge.getMetername());

		LocationMeter locationMeterContinuous = new LocationMeter();
		locationMeterContinuous.setAvgcalcmethod(AverageMethod.ALL.toString());
		locationMeterContinuous.setMetername(newMeterContinuous.getMetername());

		locationMeterList.add(locationMeterCharacteristic);
		locationMeterList.add(locationMeterGauge);
		locationMeterList.add(locationMeterContinuous);
		newlocation.setLocationmeter(locationMeterList);

		maximoApi.create(newlocation);
		logger.info("location: {}" + location);

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
		maximoApi.create(newWorkOrder);
		logger.info("Work Order: {}" + woNum);

		// Change WO status to Approved
		logger.info("Changing work order status to INPRG");
		maximoApi.changeStatus(newWorkOrder, WoStatus.INPRG.toString());
		// Assignment with labor maxadmin
		maximoApi.addAssignmentLabor(newWorkOrder, labor);
		logger.info("Assignment added");

		// Create 2nd work order
		logger.info("Creating a work order");
		String workOrderResult1 = maximoApi.retrieve(new WorkOrder(),
				"addid=1&internalvalues=1&action=system:new&addschema=1");
		newWorkOrder1 = new Gson().fromJson(workOrderResult1, WorkOrder.class);
		newWorkOrder1.setDescription("WorkOrder for mobile automation");
		newWorkOrder1.setAssetNum(assetNum);
		newWorkOrder1.setGLAccount(SetupData.GLDEBITACCT);
		newWorkOrder1.setLocation(location);

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
