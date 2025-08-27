package com.ibm.maximo.technician.testcases;
import static org.testng.Assert.assertEquals;
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
import com.ibm.maximo.automation.mobile.MobileAutomationFramework;
import com.ibm.maximo.automation.mobile.api.MaximoApi;
import com.ibm.maximo.automation.mobile.api.json.Asset;
import com.ibm.maximo.automation.mobile.api.json.AssetMeter;
import com.ibm.maximo.automation.mobile.api.json.Meter;
import com.ibm.maximo.automation.mobile.api.json.WorkOrder;
import com.ibm.maximo.technician.framework.TechnicianTest;
import com.ibm.maximo.technician.page.CreateWorkOrderPage;
import com.ibm.maximo.technician.page.EditWorkOrderPage;
import com.ibm.maximo.technician.page.ErrorPage;
import com.ibm.maximo.technician.page.MetersPage;
import com.ibm.maximo.technician.page.MySchedulePage;
import com.ibm.maximo.technician.page.WorkOrderDetailsPage;
import com.ibm.maximo.technician.setupdata.SetupData;
import com.ibm.maximo.technician.setupdata.SetupData.AverageMethod;
import com.ibm.maximo.technician.setupdata.SetupData.MeterType;
import com.ibm.maximo.technician.setupdata.SetupData.ReadingType;
import com.ibm.maximo.technician.setupdata.SetupData.WoStatus;
/*
 * MAXMOA-5173:[MobileAutomation] Make meters available for work orders created while offline in Work Technician
 * Scenario 38 & 40 : testcases/WorkorderDetailsPage/TC_WODetailsAssetLocationMeters.md
 * */
public class MetersCreateWorkOrderTest extends TechnicianTest{
	
	private final Logger logger = LoggerFactory.getLogger(MetersTest.class);
	private AbstractAutomationFramework af;
	private TestSuite testSuite;
	private MaximoApi maximoApi;
	private WorkOrder newWorkOrder ;
	private String timezone, labor ,assetNum1;
	private String descriptionStr = "Description of WO";
	private String priority = "9";	
	
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
		if (testSuite != null) {
			testSuite.teardown();
		}
	}
	
	 @Test(groups = {
	"desktop","mobile" }, description = "Verify that while creating WO from + icon, the added asset with meter associated should display the meter details on the WO details and list page.\n", timeOut = 900000)
     public void verifyMeterDetailsFromCreateWOAsset() throws Exception {
		
		    String enterMeterValue = "100";
		    MobileAutomationFramework maf = (MobileAutomationFramework) this.af;
			CreateWorkOrderPage createWO = new CreateWorkOrderPage(maf);
			ErrorPage errorpage = new ErrorPage(maf);
			MetersPage metersPage = new MetersPage(af);

			//Click on 9 dots(Navigator menu)
			errorpage.clickNavigatorMenu();	
			//Click on plus icon on Navigator page
			logger.info("clicked + button");
			//Click on plus icon on Navigator page
			createWO.getplusiconClick();
			// Click on Create Work Order
			createWO.selectCreateWO();
			// Enter Description in create WO
			createWO.insertDescriptionOfWorkOrder(descriptionStr);
			// Enter priority
			createWO.priorityEnter(priority);
			maf.scrollPage(1, 2);
		 	// Verify that the asset record is found and selected
			createWO.assetNumber(assetNum);
			// Tap on Confirm work Order Creation button (check mark at the top of the page)
			createWO.clickWorkOrderCreate();	
			// Assert to verify asset number displayed is correct
			assertEquals(assetNum, createWO.getAssetTextWOPage());
			// Meters Icon
			assertTrue(metersPage.verifyMeterIconOnWODetailPage());	
			// click on Meters Icon
			metersPage.clickMetersButtonOnWoDetail();			
			// click on 3 dots
			metersPage.clickThreeDotsOnMeterSlider();
			//verify previous readings label
			assertEquals(metersPage.verifyPreviousReadings(), "Previous readings");	
			//click on 3 dots to close it
			metersPage.clickMetersButtonOnWoDetail();
			//add meter values and verify
			assertTrue(metersPage.addContinuousMeterReadingsFromWoDetailForAssets(enterMeterValue));
			assertTrue(metersPage.checkMeterValue(enterMeterValue));
			//click on cross icon to close slider
			metersPage.cancelMetersDrawerFromWoDetail();
		
	}

	@Test(groups = { "desktop",
			"mobile" }, description = "Verify that while editing created WO from + icon, the added asset with meter associated should display the meter details edited on the WO details and list page.\n", timeOut = 900000)
	public void verifyMeterDetailsFromEditWOAsset() throws Exception {

	        String enterMeterValue = "200";
	    	MobileAutomationFramework maf = (MobileAutomationFramework) this.af;
	    	CreateWorkOrderPage createWO = new CreateWorkOrderPage(maf);
		    WorkOrderDetailsPage wodetails = new WorkOrderDetailsPage(af);
		    MetersPage metersPage = new MetersPage(af);
			MySchedulePage assignedWorkPage = new MySchedulePage(af);
			EditWorkOrderPage editWODetailsPage = new EditWorkOrderPage(af);
			

		    // click on the edit icon
		    wodetails.editWorkOrderDetails();
		   // Verify correct page is opened
		 	logger.info("Edit work order page is opened");
		 	wodetails.getTitle(editWODetailsPage.editWorkOrderdetailsPageTitle);
	     	maf.scrollPage(1, 2);
	    	// update asset
			editWODetailsPage.assetNumber(assetNum1);
			editWODetailsPage.saveEditedWODetails();
			// Assert to verify asset number displayed is correct
			String asset = wodetails.getTextWOAssetNum();
			logger.info("WO Asset Number >" + asset);
			assertEquals(asset, assetNum1);
		    // Meters Icon
		    assertTrue(metersPage.verifyMeterIconOnWODetailPage());
		    // click on Meters Icon
		    metersPage.clickMetersButtonOnWoDetail();
		    // click on 3 dots
		    metersPage.clickThreeDotsOnMeterSlider();
		    // verify previous readings label
	     	assertEquals(metersPage.verifyPreviousReadings(), "Previous readings");
		    // click on 3 dots to close it
		    metersPage.clickMetersButtonOnWoDetail();
		    // add meter values and verify
		    assertTrue(metersPage.addContinuousMeterReadingsFromWoDetailForAssets(enterMeterValue));
			assertTrue(metersPage.checkMeterValue(enterMeterValue));
		    // click on cross icon to close slider
		    metersPage.cancelMetersDrawerFromWoDetail();    
		    // Back to WO list view
			WorkOrderDetailsPage.clickBackWOList();
	}
	 
	
	protected void createDefaultObjects() throws Exception {
		logger.info("Creating default objects");
		
		String resultContinuous = maximoApi.retrieve(new Meter(),
				"addid=1&internalvalues=1&action=system:new&addschema=1");
		Meter newMeterContinuous = new Gson().fromJson(resultContinuous, Meter.class);
		
		//meter 
		resultContinuous = maximoApi.retrieve(new Meter(),
				"addid=1&internalvalues=1&action=system:new&addschema=1");
		newMeterContinuous = new Gson().fromJson(resultContinuous, Meter.class);
		newMeterContinuous.setMetername("MCO" + newMeterContinuous.getMeterid());
		newMeterContinuous.setMetertype(MeterType.CONTINUOUS.toString());
		newMeterContinuous.setDescription("my meter " + newMeterContinuous.getMeterid());
		newMeterContinuous.setReadingtype(ReadingType.DELTA.toString());
		// Creates Meter CONTINUOUS
		maximoApi.create(newMeterContinuous);
		logger.info("CONTINUOUS Meter: {}" + newMeterContinuous.getMeterid());
			
		// Create an asset 1
		logger.info("Creating asset 1");
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
		meterList.add(assetMeterContinuous);
		newAsset.setAssetMeter(meterList);
		maximoApi.create(newAsset);
		logger.info("Asset: {}" + assetNum);
		
		// Create an asset 2
		logger.info("Creating asset 2");
		assetNum1 = AbstractAutomationFramework.randomString(5).toUpperCase();
		String assetResult1 = maximoApi.retrieve(new Asset(), "addid=1&internalvalues=1&action=system:new&addschema=1");
		Asset newAsset1 = new Gson().fromJson(assetResult1, Asset.class);
		newAsset1.setAssetNum(assetNum1);
		newAsset1.setDescription("Asset for mobile automation test");
		newAsset1.setSiteId(SetupData.SITEID);
		newAsset1.setAssetMeter(meterList);
		maximoApi.create(newAsset1);
		logger.info("Asset: {}" + assetNum1);
		
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
	