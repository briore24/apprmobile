package com.ibm.maximo.technician.testcases;

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
 * GRAPHITE-68944: [TECHMOBILE] Meters P2 :38M,1TA,1A
 * */
public class ContinuousMeterRollOverValueTest extends TechnicianTest {
	private final Logger logger = LoggerFactory.getLogger(ContinuousMeterRollOverValueTest.class);
	private AbstractAutomationFramework af;
	private TestSuite testSuite;
	private MaximoApi maximoApi;
	private WorkOrder newWorkOrder;
	private String woNum, labor, location;
	private String enterMeterValue = "123", rollOver = "100";
	private int numOfMeterWithWO = 1;

	@BeforeClass(alwaysRun = true)
	@Parameters({ "configPath" })
	public void setup(String configPath) throws Exception {
		logger.info("********************ContinuousMeterRollOverValueTest*********************************");
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
		login(af);
	}

	@AfterClass(alwaysRun = true)
	public void teardown() throws Exception {
		logOut(af);
		// Change WO status to Completed
		logger.info("Changing work order status to COMP");
		maximoApi.changeStatus(newWorkOrder, WoStatus.COMP.toString());
		if (testSuite != null) {
			testSuite.teardown();
		}
	}

	@Test(groups = {
			"priority2" }, description = "Verify that technician can't save meter reading which is more than rollover value for continuous meter", timeOut = 900000)
	public void enterMeterReadingValueAboveRollOver() throws Exception {
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
		assertTrue(metersPage.addContinuousMeterReadingsFromWoDetailForAsset(enterMeterValue, numOfMeterWithWO));
		assertTrue(metersPage.showErrorPageAndClose("Readings cannot exceed rollover values. The reading "
				+ enterMeterValue + " is greater than the rollover point " + rollOver + "."));
		assertTrue(metersPage.cancelUpdateMeterReadingScreen());
		assertTrue(metersPage.disCardUpdateMeterReading());

		assertTrue(metersPage.addContinuousMeterReadingsFromWoDetailForLocation(enterMeterValue, numOfMeterWithWO));
		assertTrue(metersPage.showErrorPageAndClose("Readings cannot exceed rollover values. The reading "
				+ enterMeterValue + " is greater than the rollover point " + rollOver + "."));
		assertTrue(metersPage.cancelUpdateMeterReadingScreen());
		assertTrue(metersPage.disCardUpdateMeterReading());
		metersPage.cancelMetersDrawerFromWoDetail();

		// Back to WO list view
		WorkOrderDetailsPage.clickBackWOList();
		Thread.sleep(2000);
		// Clear Search Result
		assignedWorkPage.clickClearButton();
	}

	protected void createDefaultObjects() throws Exception {
		logger.info("Creating default objects");

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

		AssetMeter assetMeterContinuous = new AssetMeter();
		assetMeterContinuous.setAvgcalcmethod(AverageMethod.ALL.toString());
		assetMeterContinuous.setMetername(newMeterContinuous.getMetername());
		assetMeterContinuous.setRollover(rollOver);
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

		LocationMeter locationMeterContinuous = new LocationMeter();
		locationMeterContinuous.setAvgcalcmethod(AverageMethod.ALL.toString());
		locationMeterContinuous.setMetername(newMeterContinuous.getMetername());
		locationMeterContinuous.setRollover(rollOver);
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
	}
}