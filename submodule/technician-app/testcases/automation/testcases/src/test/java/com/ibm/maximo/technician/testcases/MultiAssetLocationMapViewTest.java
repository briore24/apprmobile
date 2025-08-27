package com.ibm.maximo.technician.testcases;

import static org.testng.Assert.*;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.ibm.maximo.automation.mobile.api.json.*;
import com.ibm.maximo.automation.mobile.api.json.Inspection.InspField;
import com.ibm.maximo.automation.mobile.api.json.Inspection.InspFieldOption;
import com.ibm.maximo.automation.mobile.api.json.Inspection.InspForm;
import com.ibm.maximo.automation.mobile.api.json.Inspection.InspQuestion;
import com.ibm.maximo.technician.setupdata.SetupData.WorkType;
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
import com.ibm.maximo.technician.framework.TechnicianTest;
import com.ibm.maximo.technician.page.AssetDetailsPage;
import com.ibm.maximo.technician.page.EditWorkOrderPage;
import com.ibm.maximo.technician.page.FollowUpWorkPage;
import com.ibm.maximo.technician.page.InspectionsPage;
import com.ibm.maximo.technician.page.MetersPage;
import com.ibm.maximo.technician.page.MultipleAssetsAndLocationsPage;
import com.ibm.maximo.technician.page.MySchedulePage;
import com.ibm.maximo.technician.page.WorkOrderDetailsPage;
import com.ibm.maximo.technician.setupdata.SetupData;
import com.ibm.maximo.technician.setupdata.SetupData.*;

/*
 * MASR-495:Add the Map Touchpoint to the Multi Asset Location Section so Technician can Visualize a single/multiple asset or location on the map
 */

public class MultiAssetLocationMapViewTest extends TechnicianTest {
	private final Logger logger = LoggerFactory.getLogger(MultiAssetLocationMapViewTest.class);
	private AbstractAutomationFramework af;
	private TestSuite testSuite;
	private MaximoApi maximoApi;
	private WorkOrder newWorkOrder;
	private static final String LOCATION_DESCRIPTION = "Location for mobile automation test";
	private String assetNum, assetNum2, assetNum3, labor, locationNum, locationNum2, serviceAddress;
	private static final String WORKORDER_DESCRIPTION = "Work Order for Automation Test";
	private String woNum;
	public static String form;
	public static String inspFormNum;

	@BeforeClass(alwaysRun = true)
	@Parameters({ "configPath" })
	public void setup(String configPath) throws Exception {
		logger.info("********************MultiAssetLocationMapViewTest*********************************");
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

	@Test(groups = { "mobile" , "desktop"}, description = "Verify Multiple touch points on Map view of Multiple Assets and locations page", timeOut = 800000)
	public void WorkOrderWithMultiAssetLocation() throws Exception {

		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage woDetailsPage = new WorkOrderDetailsPage(af);
		MultipleAssetsAndLocationsPage multiALpage = new MultipleAssetsAndLocationsPage(af);
		MetersPage meterPage = new MetersPage(af);
		InspectionsPage inspectionsPage = new InspectionsPage(af);
		FollowUpWorkPage followUpWorkPage = new FollowUpWorkPage(af);
		EditWorkOrderPage editWorkOrder = new EditWorkOrderPage(af);
		AssetDetailsPage assetDetailsPage = new AssetDetailsPage(af);

		assignedWorkPage.search(woNum);
		assignedWorkPage.openWorkOrderDetails();		
		Thread.sleep(1000);
		// Test data created with 2 multi asset and location with WO. So, count check is 2
		assertEquals("2", woDetailsPage.multiAssetsAndLocationsCountDisplay("2"));
		woDetailsPage.multiAssetsAndLocationsChevron();
		assertEquals(multiALpage.getTextMultipleAssetsAndLocations(),
				"Multiple assets and locations");
		assertTrue(multiALpage.checkOnMultiAssetLocationIcon()); // Mark completed
		// assetNum2 had Meter
		multiALpage.search(assetNum2);
		assertTrue(multiALpage.verifyServiceAddressIconDisplayed());// List view
		multiALpage.clickOnServiceAddressButton();
		// all asset on page had create follow up icon
		assertTrue(multiALpage.verifyCreateFollowupIconDisplayed());
		multiALpage.clickOnCreateFollowupWOButton();
		String createFollowUpTitle = woDetailsPage.getTitle(followUpWorkPage.CreateFollowUpPageTitle);
		assertEquals("Create follow-up WO", createFollowUpTitle);
		assertTrue(editWorkOrder.verifySaveButton());
		woDetailsPage.clickButton(woDetailsPage.workOrderBreadcrumb); // back to Multiple Assets and locations page
		//multiALpage.clickClearButton();
		assertTrue(multiALpage.unCheckOnMultiAssetLocationIcon()); // Remove Mark completed
		assertTrue(multiALpage.verifyServiceAddressIconDisplayed());// Map view
		assertTrue(multiALpage.verifyMeterIconDisplayed());
		multiALpage.clickMeterButton();
		meterPage.cancelMetersDrawerFromWoDetail();
		multiALpage.openAssetsDetails();
		Thread.sleep(10000);
		// Move to Asset app and check details
		assertEquals("Asset Details", assetDetailsPage.getTitle(assetDetailsPage.pageTitle));
		// Navigate back to tech app of Multiple assets and locations page
		assetDetailsPage.clickButton(assetDetailsPage.assetDetailBreadcrumb);
		Thread.sleep(5000);
		assertTrue(multiALpage.verifySearchOnMapViewDisplayed());
		logger.info("Navigation back to Multiple assets and locations page in Technician app done correctly.");
		
		// assetNum 3 had Inspection form
		multiALpage.searchOnMapView(assetNum3);
		multiALpage.openDetailsOnMapView();
		assertTrue(multiALpage.IsInspectionButtonDisplayed());
		multiALpage.clickInspectionButton();
		Thread.sleep(10000);
		// Verify that user is navigated to Inspections app and inspection form assigned
		// to work order is displayed for conducting inspection.
		assertEquals(inspectionsPage.getInfo(), form);
		logger.info("Inspections Page is correctly displayed.");
		// Click back from inspections page to work order
		inspectionsPage.clickBackFromInspectionToWorkOrderButton();
		Thread.sleep(5000);
		assertTrue(multiALpage.verifySearchOnMapViewDisplayed());
		logger.info("Navigation back to Multiple assets and locations page in Technician app done correctly.");
		
		multiALpage.clickOnListViewButton();
		multiALpage.clickOnMapViewButton();

	}

	protected void createDefaultObjects() throws Exception {
		// create Form and Inspection
		createDefaultForm();
		logger.info("Creating default objects");
		
		// Create a Service Address
		logger.info("Creating a Service Address");
		String serAddResult = maximoApi.retrieve(new ServiceAddress(),
				"addid=1&internalvalues=1&action=system:new&addschema=1");
		ServiceAddress newSerAdd = new Gson().fromJson(serAddResult, ServiceAddress.class);
		newSerAdd.setLatitudeY(SetupData.LATITUDEY);
		newSerAdd.setLongitudeX(SetupData.LONGITUDEX);
		newSerAdd.setDescription(SetupData.ADDRESSTITLE);
		maximoApi.create(newSerAdd);
		serviceAddress = newSerAdd.getAddressCode();
		logger.info("Service Address Created :" + serviceAddress);
		
		// Create a location
		logger.info("Creating a location");
		locationNum = AbstractAutomationFramework.randomString(5).toUpperCase();
		Location newlocation = new Location();
		newlocation.setLocationId(locationNum);
		newlocation.setDescription(LOCATION_DESCRIPTION+locationNum);
		newlocation.setSiteId(SetupData.SITEID);
		newlocation.setType(SetupData.LocType.OPERATING.toString());
		maximoApi.create(newlocation);
		logger.info("location: {}" + locationNum);

		// Create an Asset
		logger.info("Creating an asset");
		assetNum = AbstractAutomationFramework.randomString(5).toUpperCase();
		String assetResult = maximoApi.retrieve(new Asset(), "addid=1&internalvalues=1&action=system:new&addschema=1");
		Asset newAsset = new Gson().fromJson(assetResult, Asset.class);
		newAsset.setAssetNum(assetNum);
		newAsset.setDescription(SetupData.ASSET_DESCRIPTION + assetNum);
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
		newWorkOrder.setWoPriority(1);
		newWorkOrder.setWorkType(WorkType.PM.toString());
		newWorkOrder.setGLAccount(SetupData.GLDEBITACCT);
		newWorkOrder.setAssetNum(assetNum);
		newWorkOrder.setInspFormNum(inspFormNum); // WO had same inspection form
		maximoApi.create(newWorkOrder);
		woNum = newWorkOrder.getWoNum();
		logger.info("Work Order 1: {}" + woNum);
		
		// Create Multiple Assets
		List<MultipleAssetsLocationsCIs> amalc = new ArrayList<MultipleAssetsLocationsCIs>();
		
		// Create a location
		logger.info("Creating a location");
		locationNum2 = AbstractAutomationFramework.randomString(5).toUpperCase();
		Location newlocation2 = new Location();
		newlocation2.setLocationId(locationNum2);
		newlocation2.setDescription(LOCATION_DESCRIPTION+locationNum2);
		newlocation2.setSiteId(SetupData.SITEID);
		newlocation2.setType(SetupData.LocType.OPERATING.toString());
		newlocation2.setSaddresscode(serviceAddress);
		maximoApi.create(newlocation2);
		logger.info("location2: {}" + locationNum2);

		// create Meter
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
		assetNum2 = AbstractAutomationFramework.randomString(5).toUpperCase();
		String assetResult2 = maximoApi.retrieve(new Asset(), "addid=1&internalvalues=1&action=system:new&addschema=1");
		Asset newAsset2 = new Gson().fromJson(assetResult2, Asset.class);

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
		newAsset2.setAssetMeter(meterList);
		newAsset2.setAssetNum(assetNum2);
		newAsset2.setDescription(SetupData.ASSET_DESCRIPTION + assetNum2);
		newAsset2.setLocation(locationNum2);
		newAsset2.setSiteId(SetupData.SITEID);
		newAsset2.setStatus(SetupData.LocAssetStatus.ACTIVE.toString());
		logger.info("Creating an asset ********* 2");
		maximoApi.create(newAsset2);
		logger.info("Asset: {}" + assetNum2);
				
		MultipleAssetsLocationsCIs firstAssetLoc = new MultipleAssetsLocationsCIs();
		firstAssetLoc.setIsprimary(false);
		firstAssetLoc.setAssetNum(assetNum2);
			
		// Create an Asset
		assetNum3 = AbstractAutomationFramework.randomString(5).toUpperCase();
		String assetResult3 = maximoApi.retrieve(new Asset(), "addid=1&internalvalues=1&action=system:new&addschema=1");
		Asset newAsset3 = new Gson().fromJson(assetResult3, Asset.class);
		newAsset3.setAssetNum(assetNum3);
		newAsset3.setDescription(SetupData.ASSET_DESCRIPTION + assetNum3);
		newAsset3.setSiteId(SetupData.SITEID);
		newAsset3.setSaddresscode(serviceAddress);
		newAsset3.setStatus(SetupData.LocAssetStatus.ACTIVE.toString());
		logger.info("Creating an asset ********* 3");
		maximoApi.create(newAsset3);
		logger.info("Asset3: {}" + assetNum3);

		MultipleAssetsLocationsCIs SecAsset = new MultipleAssetsLocationsCIs();
		SecAsset.setIsprimary(false);
		SecAsset.setAssetNum(assetNum3);
		SecAsset.setInspFormNum(inspFormNum);// Inspection form added with assetNum3
				
		amalc.add(firstAssetLoc);
		amalc.add(SecAsset);		
		newWorkOrder.setMultiassetlocci(amalc);
		maximoApi.update(newWorkOrder);
		logger.info("Multiple Assets with Meter and Inspection added");

		// Change WO status to Approved
		logger.info("Changing work order status to APPR");
		maximoApi.changeStatus(newWorkOrder, WoStatus.APPR.toString());

		// Assign the labor
		maximoApi.addAssignmentLabor(newWorkOrder, labor);
		logger.info("Assignment added");
	}
	
	
	/**
	 * Create Form
	 * 
	 * @throws Exception
	 */
	public void createDefaultForm() throws Exception {
		logger.info("Creating default inspection form");
		logger.info("getting the next id available");
		// Get the next id
		String result = maximoApi.retrieve(new InspForm(), "addid=1&internalvalues=1&action=system:new&addschema=1");
		InspForm inspForm = new Gson().fromJson(result, InspForm.class);

		form = AbstractAutomationFramework.randomString(5).toUpperCase();
		inspForm.setName(form);
		inspForm.setDescription("Automated Test Form");

		// Create the draft
		logger.info("creating draft");
		maximoApi.create(inspForm);

		// Get inspFormNum
		inspFormNum = inspForm.getInspFormNum();

		logger.info("adding questions");
		inspForm.setInspQuestion(getDefaultInspQuestions());

		// Add questions
		maximoApi.update(inspForm);

		// Activate
		logger.info("activating form");
		maximoApi.changeStatus(inspForm, ItemStatus.ACTIVE.toString(), "changeFormStatus");
		logger.info("Form: {}" + form);

	}

	/**
	 * Adding questions to the Form
	 * 
	 * @return inspQuestions
	 */
	private List<InspQuestion> getDefaultInspQuestions() {
		List<InspQuestion> inspQuestions = new ArrayList<>();

		// First question
		InspQuestion inspQuestion = new InspQuestion();
		inspQuestion.setDescription("Question 1 - Date and time");
		inspQuestion.setSequence(1);
		inspQuestion.setGroupSeq(1);
		inspQuestion.setAction("Add");

		InspField inspField = new InspField();
		inspField.setFieldtypeMaxvalue("DT");
		inspField.setFieldType("DT");
		inspField.setDescription("Enter date and time");
		inspField.setRequired(true);
		inspField.setSkipOption(false);
		inspField.setVisible(true);
		inspField.setShowDate(false);
		inspField.setShowTime(false);
		inspField.setSequence(1);
		inspField.setAction("Add");

		List<InspField> inspFields = new ArrayList<>();
		inspFields.add(inspField);

		inspQuestion.setInspField(inspFields);

		inspQuestions.add(inspQuestion);

		// Second question
		inspQuestion = new InspQuestion();
		inspQuestion.setDescription("Question 2 - Numeric");
		inspQuestion.setSequence(2);
		inspQuestion.setGroupSeq(2);
		inspQuestion.setAction("Add");

		inspField = new InspField();
		inspField.setFieldtypeMaxvalue("SE");
		inspField.setFieldType("SE");
		inspField.setDescription("Enter a number");
		inspField.setRequired(true);
		inspField.setSkipOption(false);
		inspField.setVisible(true);
		inspField.setShowDate(false);
		inspField.setShowTime(false);
		inspField.setSequence(1);
		inspField.setAction("Add");

		inspFields = new ArrayList<>();
		inspFields.add(inspField);

		inspQuestion.setInspField(inspFields);

		inspQuestions.add(inspQuestion);

		// Third question
		inspQuestion = new InspQuestion();
		inspQuestion.setDescription("Question 3 - Single Choice");
		inspQuestion.setSequence(3);
		inspQuestion.setGroupSeq(3);
		inspQuestion.setAction("Add");

		inspField = new InspField();
		inspField.setFieldtypeMaxvalue("SO");
		inspField.setFieldType("SO");
		inspField.setDescription("Select an option");
		inspField.setRequired(false);
		inspField.setSkipOption(true);
		inspField.setVisible(true);
		inspField.setShowDate(false);
		inspField.setShowTime(false);

		List<InspFieldOption> inspFieldOptions = new ArrayList<>();
		InspFieldOption inspFieldOption = new InspFieldOption();
		inspFieldOption.setDescription("Option 1");
		inspFieldOption.setSequence(1);
		inspFieldOption.setAction("Add");
		inspFieldOptions.add(inspFieldOption);

		inspFieldOption = new InspFieldOption();
		inspFieldOption.setDescription("Option 2");
		inspFieldOption.setSequence(2);
		inspFieldOption.setAction("Add");
		inspFieldOptions.add(inspFieldOption);

		inspField.setInspFieldOption(inspFieldOptions);
		inspField.setSequence(1);
		inspField.setAction("Add");

		inspFields = new ArrayList<>();
		inspFields.add(inspField);

		inspQuestion.setInspField(inspFields);

		inspQuestions.add(inspQuestion);

		// Fourth question
		inspQuestion = new InspQuestion();
		inspQuestion.setDescription("Question 4 - Text response");
		inspQuestion.setSequence(4);
		inspQuestion.setGroupSeq(4);
		inspQuestion.setAction("Add");

		inspField = new InspField();
		inspField.setFieldtypeMaxvalue("TR");
		inspField.setFieldType("TR");
		inspField.setDescription("Type something here");
		inspField.setRequired(false);
		inspField.setSkipOption(true);
		inspField.setVisible(true);
		inspField.setShowDate(false);
		inspField.setShowTime(false);
		inspField.setSequence(1);
		inspField.setAction("Add");

		inspFields = new ArrayList<>();
		inspFields.add(inspField);

		inspQuestion.setInspField(inspFields);

		inspQuestions.add(inspQuestion);

		return inspQuestions;
	}
}
