package com.ibm.maximo.technician.testcases;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
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
import com.ibm.maximo.automation.mobile.api.json.AssetMeter;
import com.ibm.maximo.automation.mobile.api.json.Meter;
import com.ibm.maximo.automation.mobile.api.json.MultipleAssetsLocationsCIs;
import com.ibm.maximo.automation.mobile.api.json.WorkOrder;
import com.ibm.maximo.automation.mobile.api.json.Inspection.InspField;
import com.ibm.maximo.automation.mobile.api.json.Inspection.InspFieldOption;
import com.ibm.maximo.automation.mobile.api.json.Inspection.InspForm;
import com.ibm.maximo.automation.mobile.api.json.Inspection.InspQuestion;
import com.ibm.maximo.technician.framework.TechnicianTest;
import com.ibm.maximo.technician.page.InspectionsPage;
import com.ibm.maximo.technician.page.MySchedulePage;
import com.ibm.maximo.technician.page.WorkOrderDetailsPage;
import com.ibm.maximo.technician.setupdata.SetupData;
import com.ibm.maximo.technician.setupdata.SetupData.AverageMethod;
import com.ibm.maximo.technician.setupdata.SetupData.MaxDomain;
import com.ibm.maximo.technician.setupdata.SetupData.MeterType;
import com.ibm.maximo.technician.setupdata.SetupData.WoStatus;
import com.ibm.maximo.technician.setupdata.SetupData.WorkType;

//GRAPHITE-68411 - Scenario 7- Verify that all multi asset/multi location records which are added to work order are displayed on wo details page 
//                 Scenario 15-  Verify if there is an inspection form assigned to a multi asset/location record, the inspection touch-point is displayed on that record
//                 Scenario 16-  Verify user is able to navigate to inspection page, when user click on inspection touch-point, if there is an inspection assigned to a multi asset/location record
// 				   Scenario 17 - Verify user is able to navigate back to work order details page when user clicks on back arrow/button on inspection page
public class MultiAssetLocationTestSuiteFour extends TechnicianTest {
	private final Logger logger = LoggerFactory.getLogger(MultiAssetLocationTestSuiteFour.class);
	private AbstractAutomationFramework af;
	private TestSuite testSuite;
	private MaximoApi maximoApi;
	private String assetNum, assetNum1, assetNum2, header, form,form1,inspFormNum1, locationNum, inspFormNum, assetNum3, assetNum4, woNum1, labor,
			locationNum1, locationNum2, woNum, woDetailsPageHeader;
	private WorkOrder workOrder, workOrder1;
	private List<WorkOrder> workOrderList;
	int meterValue;
	private boolean apiCodeSuccess = false;

	private static final String ASSET_DESCRIPTION = "Asset Description ";


	@BeforeClass(alwaysRun = true)
	@Parameters({ "configPath" })
	public void setup(String configPath) throws Exception {
		logger.info("********************MultiAssetLocationTestSuiteFour*********************************");
		this.af = FrameworkFactory.get();
		Properties properties = new Properties();
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(configPath));
			properties.load(in);
			labor = properties.getProperty("system.username");
			maximoApi = new MaximoApi();
			maximoApi.setMaximoServer(properties.getProperty("system.maximoServerUrl"),
					properties.getProperty("system.maximoAPIKey"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		workOrderList = new ArrayList<>();

		// create Form
		createDefaultForm();
		// Get inspFormNum
		inspFormNum1 = inspFormNum;
		form1 = form;

		// Create Multi Assets
		createAssetWithLocation(locationNum);
		assetNum2 = assetNum;
		createAssetWithLocation(locationNum1);
		assetNum3 = assetNum;
		createAssetWithLocation(locationNum2);
		assetNum4 = assetNum;

		// Create Work Order with Multiple Assets
		createWorkOrderWithMultiAsset(assetNum, assetNum1, assetNum2);
		woNum1 = woNum;
		workOrder1 = workOrder;

		login(af);
		goToInspections(af);
		goToMySchedule(af);
	}

	@AfterClass(alwaysRun = true)
	public void teardown() throws Exception {
		logOut(af);
		
		for(WorkOrder workOrder: workOrderList) {
			logger.info("Change Status to complete for Workorder " + workOrder.getWoNum());
			maximoApi.changeStatus(workOrder, WoStatus.COMP.toString());
		}
		
		//TODO : Complete WorkOrder which is created as testdata
		if (testSuite != null) {
			testSuite.teardown();
		}
	}

	@Test(groups = {
			"priority2" }, description = "Verify that all multi asset/multi location records which are added to work order are displayed on wo details page", timeOut = 11000000)

	public void verifyMultiAssetAndLocationOnWODetailsPage() throws Exception {
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage workOrderDetailspage = new WorkOrderDetailsPage(af);

		// Search the WO
		assignedWorkPage.search(woNum);
		// Click on Chevron Icon on Work Order
		assignedWorkPage.openWorkOrderDetails();
		// Verify Asset and location section is displayed
		Thread.sleep(1000);
		assertTrue(af.isElementExists(By.id(workOrderDetailspage.assetAndLocationSection)));
		logger.info("Asset and location section is correctly displayed.");
		// Verify Asset ID is displayed
		assertEquals(workOrderDetailspage.getAssetID(), assetNum);
		// Verify Asset Description is displayed
		assertEquals(workOrderDetailspage.getAssetDescription(), "Asset Description " + assetNum);
		logger.info("Asset description is correctly displayed.");
		// Verify Asset icon
		workOrderDetailspage.verifyAssetIcon();
		workOrderDetailspage.clickBackChevron();
		assignedWorkPage.clickClearButton();
	}

	@Test(groups = {
			"priority2" }, description = "Verify if there is an inspection form assigned to a multi asset/location record, the inspection touch-point is displayed on that record", timeOut = 1100000)

	public void VerifyInspectionTouchpoint() throws Exception {

		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage assignedWorkOrderDetailsPage = new WorkOrderDetailsPage(af);

		// Search the WO
		assignedWorkPage.search(woNum1);
		// Click on Chevron Icon on Work Order
		assignedWorkPage.openWorkOrderDetails();
		// Verify Inspections touch point is present
		assignedWorkOrderDetailsPage.verifyInspectionsTouchPointIsPresent();
		
		assignedWorkOrderDetailsPage.clickBackChevron();
		assignedWorkPage.clickClearButton();
	}
	
	@Test(groups = {
	"priority2" }, description = "Verify user is able to navigate to inspection page, when user click on inspection touch-point, if there is an inspection assigned to a multi asset/location record ",timeOut= 1100000)
	 	 public void VerifyUserNavigatesToInspectionPage() throws Exception {
		
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage assignedWorkOrderDetailsPage = new WorkOrderDetailsPage(af);
		
		// Search the WO
		assignedWorkPage.search(woNum1);
		// Click on Chevron Icon on Work Order
		assignedWorkPage.openWorkOrderDetails();
		
		//click on inspection button.
		assignedWorkOrderDetailsPage.clickInspectionButton();
		
		//Verify Inspections page is displayed correctly .
		assertEquals(InspectionsPage.getInfo(), "Inspections");
		logger.info("Inspections Page is correctly displayed.");
		
		//click on back button to move back to wo details page
		InspectionsPage.clickBackFromInspectionToWorkOrderButton();
		
		assignedWorkOrderDetailsPage.clickBackChevron();
	}
	
	@Test(groups = {
	"priority2" }, description = "Verify that the meter touch-point functionality for each multi asset/location record on wo details page",timeOut= 1100000)
	 public void VerifyMeterIconsOnMultiAssetLocation() throws Exception {
		MobileAutomationFramework maf = (MobileAutomationFramework) this.af;
		MySchedulePage assignedWorkPage = new MySchedulePage(maf);
		WorkOrderDetailsPage assignedWorkOrderDetailsPage = new WorkOrderDetailsPage(maf);
		

		// Search the WO
		assignedWorkPage.search(woNum1);
		// Click on Chevron Icon on Work Order
		assignedWorkPage.openWorkOrderDetails();
		
		//verify first meter icon in asset and location 
		assignedWorkOrderDetailsPage.isMeterExistInLabor();
		
		//verify on second caret to expand
		assignedWorkOrderDetailsPage.openMultiassetForSecondCaretupdownIcon();
		//verify 2nd meter icon.
		assignedWorkOrderDetailsPage.isMeterIconExistsInMultiassetAndlocation();
		//click on third caret to expand
		assignedWorkOrderDetailsPage.openMultiassetForThirdCaretupdownIcon();	
		//verify 3rd meter icon 
		assignedWorkOrderDetailsPage.isMeterIconExistsInMultiassetAndlocation();
		assignedWorkOrderDetailsPage.clickBackChevron();
		assignedWorkPage.clickClearButton();
	}
	
	@Test(groups = {
	"priority3" }, description = "Verify user is able to navigate to inspection page, when user click on inspection touch-point, if there is an inspection assigned to a multi asset/location record ",timeOut= 1100000)
	 public void VerifyUserNavigatesToInspectionPageAndBack() throws Exception {
		
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage assignedWorkOrderDetailsPage = new WorkOrderDetailsPage(af);
		
		// Search the WO
		assignedWorkPage.search(woNum1);
		// Click on Chevron Icon on Work Order
		assignedWorkPage.openWorkOrderDetails();
		
		//click on inspection button.
		assignedWorkOrderDetailsPage.clickInspectionButton();
		
		//Verify Inspections page is displayed correctly .
		assertEquals(InspectionsPage.getInfo(), "Inspections");
		logger.info("Inspections Page is correctly displayed.");
		
		//click on back button to move back to Work order details page
		InspectionsPage.clickBackFromInspectionToWorkOrderButton();
		
		// Verify WO Details work page header value
		header = assignedWorkOrderDetailsPage.getTitle(assignedWorkOrderDetailsPage.infoLocator);
		woDetailsPageHeader = "Work order";
		assertEquals(header, woDetailsPageHeader);
		logger.info("User returned to details page of technician application");
		
		// Click back chevron for Work order list page
		assignedWorkOrderDetailsPage.clickBackChevron();				
	}
	
	/**
	 * Create Work Order with Multiple Assets
	 * 
	 * @throws Exception
	 */
	public void createWorkOrderWithMultiAsset(String multiAssetLocCI1, String multiAssetLocCI2, String multiAssetLocCI3)
			throws Exception {
		try {
		logger.info("Creating a work order");
		woNum = AbstractAutomationFramework.randomString(5).toUpperCase();
		String workOrderResult = maximoApi.retrieve(new WorkOrder(),
				"addid=1&internalvalues=1&action=system:new&addschema=1");
		WorkOrder newWorkOrder = new Gson().fromJson(workOrderResult, WorkOrder.class);
		newWorkOrder.setWoNum(woNum);
		newWorkOrder.setDescription("WorkeOrder for mobile automation test");
		newWorkOrder.setSiteId(SetupData.SITEID);
		newWorkOrder.setAssetNum(assetNum);
		newWorkOrder.setInspFormNum(inspFormNum);
		newWorkOrder.setWorkType(WorkType.PM.toString());
		assertEquals(maximoApi.create(newWorkOrder), SetupData.CreatedSuccess);
		logger.info("Work Order: {}" + woNum);

		// Create Multiple Locations
		List<MultipleAssetsLocationsCIs> amalc = new ArrayList<MultipleAssetsLocationsCIs>();
		amalc.add(new MultipleAssetsLocationsCIs(multiAssetLocCI1, null, null,inspFormNum));
		amalc.add(new MultipleAssetsLocationsCIs(multiAssetLocCI2, null, null,inspFormNum));
		amalc.add(new MultipleAssetsLocationsCIs(multiAssetLocCI3, null, null,inspFormNum));
		newWorkOrder.setMultiassetlocci(amalc);
		maximoApi.update(newWorkOrder);
		logger.info("Multiple Assets added");

		// Assignment with labor 
		assertEquals(maximoApi.addAssignmentLabor(newWorkOrder, labor), SetupData.NoContentSuccess);
		logger.info("Assignment added");

		// Change WO status to Approved
		logger.info("Changing work order status to APPR");
		assertEquals(maximoApi.changeStatus(newWorkOrder, WoStatus.APPR.toString()), SetupData.NoContentSuccess);

		// Add Workorder intoList for complete status
		workOrderList.add(newWorkOrder);
		apiCodeSuccess = true;
	} catch (AssertionError e) {
		logger.info(" SkipException apiCodeSuccess = " + apiCodeSuccess);
		apiCodeSuccess = false;
		throw new Exception("Test Setup API Failed,Stopping execution.");
	}
	}


	/**
	 * Create Asset with Location
	 * 
	 * @throws Exception
	 */
	public void createAssetWithLocation(String locationNum) throws Exception {
		try {
			logger.info("Creating a work order");
		String result = maximoApi.retrieve(new Meter(), "addid=1&internalvalues=1&action=system:new&addschema=1");
		Meter newMeterCharacteristic = new Gson().fromJson(result, Meter.class);
		newMeterCharacteristic.setMetername("MC" + newMeterCharacteristic.getMeterid());
		logger.info("--------------" + newMeterCharacteristic.getMeterid());
		meterValue = newMeterCharacteristic.getMeterid();
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
		
		
		// Create an asset
		logger.info("Creating an asset");
		assetNum = AbstractAutomationFramework.randomString(5).toUpperCase();
		String assetResult = maximoApi.retrieve(new Asset(), "addid=1&internalvalues=1&action=system:new&addschema=1");

		Asset newAsset = new Gson().fromJson(assetResult, Asset.class);

		newAsset.setAssetNum(assetNum);
		newAsset.setDescription(ASSET_DESCRIPTION + assetNum);
		newAsset.setSiteId(SetupData.SITEID);
		newAsset.setLocation(locationNum);
		List<AssetMeter> meterList = new ArrayList<AssetMeter>();
		AssetMeter assetMeterCharacteristic = new AssetMeter();
		assetMeterCharacteristic.setMetername(newMeterCharacteristic.getMetername());
		AssetMeter assetMeterGauge = new AssetMeter();
		assetMeterGauge.setMetername(newMeterGauge.getMetername());
		AssetMeter assetMeterContinuous = new AssetMeter();
		assetMeterContinuous.setAvgcalcmethod(AverageMethod.ALL.toString());
		assetMeterContinuous.setMetername(newMeterCharacteristic.getMetername());
		meterList.add(assetMeterCharacteristic);
		newAsset.setAssetMeter(meterList);
		assertEquals(maximoApi.create(newAsset), SetupData.CreatedSuccess);
		logger.info("Asset: {}" + assetNum);
		apiCodeSuccess = true;
		} catch (AssertionError e) {
			logger.info(" SkipException apiCodeSuccess = " + apiCodeSuccess);
			apiCodeSuccess = false;
			throw new Exception("Test Setup API Failed,Stopping execution.");
		}
		}

	/**
	 * Create Form
	 * 
	 * @throws Exception
	 */
	public void createDefaultForm() throws Exception {
		try {
			logger.info("Creating a work order");logger.info("Creating default inspection form");
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
		maximoApi.changeStatus(inspForm, "ACTIVE", "changeFormStatus");
		logger.info("Form: {}" + form);

		apiCodeSuccess = true;
	} catch (AssertionError e) {
		logger.info(" SkipException apiCodeSuccess = " + apiCodeSuccess);
		apiCodeSuccess = false;
		throw new Exception("Test Setup API Failed,Stopping execution.");
	}
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
