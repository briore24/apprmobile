package com.ibm.maximo.technician.testcases;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertEquals;
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
import com.ibm.maximo.automation.mobile.api.MaximoApi;
import com.ibm.maximo.automation.mobile.api.json.Asset;
import com.ibm.maximo.automation.mobile.api.json.LinearRefConversation;
import com.ibm.maximo.automation.mobile.api.json.LinearRefMethod;
import com.ibm.maximo.automation.mobile.api.json.Location;
import com.ibm.maximo.automation.mobile.api.json.MeasureUnit;
import com.ibm.maximo.automation.mobile.api.json.WorkOrder;
import com.ibm.maximo.technician.framework.TechnicianTest;
import com.ibm.maximo.technician.page.MySchedulePage;
import com.ibm.maximo.technician.setupdata.SetupData;
import com.ibm.maximo.technician.setupdata.SetupData.*;

public class LinearAssetTest extends TechnicianTest {
	private final Logger logger = LoggerFactory.getLogger(LinearAssetTest.class);
	private AbstractAutomationFramework af;
	private TestSuite testSuite;
	private boolean apiCodeSuccess = false;
	private MaximoApi maximoApi;
	private WorkOrder newWorkOrder;
	private int featuresId, featuresId2, assetFeatureId, assetFeatureId2;
	private String assetNum, woNum, labor, location, lrmValue, feature, feature2;
	private static final String LOCATION_DESCRIPTION = "Location for mobile automation test ";

	@BeforeClass(alwaysRun = true)
	@Parameters({ "configPath" })
	public void setup(String configPath) throws Exception {
		logger.info("********************LinearAssetTest*********************************");
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
			logger.info("stopped framework and quit as API failed = " + apiCodeSuccess);
			FrameworkFactory.stopAll();
		} else {
			login(af);
		}
	}

	@AfterClass(alwaysRun = true)
	public void teardown() throws Exception {
		if (testSuite != null) {
			testSuite.teardown();
		}
	}

	@Test(enabled = true, groups = {
			"mobile" }, description = "Verify user can view and search in a WO list by WO number", timeOut = 200000)
	public void aSearchForWorkOrderByWONumber() throws Exception {
		MySchedulePage assignedWorkPage = new MySchedulePage(af);

		// Verify user is able to search the work order with WORKORDER.WONUM
		assertTrue(assignedWorkPage.search(woNum));
	}

	protected void createDefaultObjects() throws Exception {
		try {
			logger.info("Creating default objects");
			createFeatures();
			createNewLinearReferencingMethod();
			
			// Create a location
			logger.info("Creating a location");
			location = AbstractAutomationFramework.randomString(5).toUpperCase();
			Location newlocation = new Location();

			newlocation.setLocationId(location);
			newlocation.setDescription(LOCATION_DESCRIPTION);
			newlocation.setSiteId(SetupData.SITEID);

			maximoApi.create(newlocation);
			logger.info("location: {}" + location);
						
			// Create an asset
			logger.info("Creating an asset");
			assetNum = AbstractAutomationFramework.randomString(5).toUpperCase();
			String assetResult = maximoApi.retrieve(new Asset(),
					"addid=1&internalvalues=1&action=system:new&addschema=1");
			Asset newAsset = new Gson().fromJson(assetResult, Asset.class);
			newAsset.setAssetNum(assetNum);
			newAsset.setDescription("Linear " + assetNum);
			newAsset.setSiteId(SetupData.SITEID);
			newAsset.setStatus(SetupData.LocAssetStatus.ACTIVE.toString());
			newAsset.setIslinear(true);
			newAsset.setLrm(lrmValue);
			newAsset.setStartmeasure("10");
			newAsset.setEndmeasure("100");
			newAsset.setDirection("NORTH");
			assertEquals(maximoApi.create(newAsset), SetupData.CreatedSuccess);
			logger.info("Asset: {}" + assetNum);

			// Create a workorder
			logger.info("Creating a work order");
			String workOrderResult = maximoApi.retrieve(new WorkOrder(),
					"addid=1&internalvalues=1&action=system:new&addschema=1");
			newWorkOrder = new Gson().fromJson(workOrderResult, WorkOrder.class);
			newWorkOrder.setDescription(SetupData.WORKORDER_DESCRIPTION + assetNum);
			newWorkOrder.setSiteId(SetupData.SITEID);
			newWorkOrder.setAssetNum(assetNum);
			newWorkOrder.setWorkType(WorkType.PM.toString());
			assertEquals(maximoApi.create(newWorkOrder), SetupData.CreatedSuccess);
			woNum = newWorkOrder.getWoNum();
			logger.info("Work Order: {}" + woNum);

			// Assign the labor
			assertEquals(maximoApi.addAssignmentLabor(newWorkOrder, labor), SetupData.NoContentSuccess);
			logger.info("Assignment added");

			// Change WO status to Approved
			logger.info("Changing work order status to APPR");
			assertEquals(maximoApi.changeStatus(newWorkOrder, WoStatus.APPR.toString()), SetupData.NoContentSuccess);

			associatedFeaturesWithAsset();
			apiCodeSuccess = true;
		} catch (AssertionError e) {
			logger.info(" SkipException apiCodeSuccess = " + apiCodeSuccess);
			apiCodeSuccess = false;
			throw new Exception("Test Setup API Failed,Stopping execution.");
		}
	}

	public void associatedFeaturesWithAsset() throws Exception {
		assetFeatureId = AbstractAutomationFramework.randomInt();
		String insertQuery = "INSERT INTO MAXIMO.ASSETFEATURE (ASSETFEATUREID, SHARED, CLASSSTRUCTUREID, STARTMEASURE, ENDMEASURE, STARTOFFSET, ENDOFFSET, STARTYOFFSET, ENDYOFFSET, STARTYOFFSETREF, ENDYOFFSETREF, STARTZOFFSET, ENDZOFFSET, STARTZOFFSETREF, ENDZOFFSETREF, SITEID, LABEL, FEATURE, ASSETNUM, STARTFEATURE, ENDFEATURE, ORGID, ASSETUID, FEATURESID, STARTASSETFEATUREID, ENDASSETFEATUREID, ISLINEARREF, ASSETLOCRELATIONID, STARTFEATURELABEL, ENDFEATURELABEL, BASEMEASUREUNITID, ENDBASEMEASURE, ENDMEASUREUNITID, STARTBASEMEASURE, STARTMEASUREUNITID, STARTOFFSETUNITID, ENDOFFSETUNITID, LINEARASSETFEATUREID, ROWSTAMP) VALUES('"+assetFeatureId+"', 0, '132"+featuresId+"', 10.00, 100.00, NULL, NULL, 0.00, 0.00, NULL, NULL, 0.00, 0.00, NULL, NULL, '"+ SetupData.SITEID +"', 'MP"+featuresId+"', '"+feature+"', '"+assetNum+"', NULL, NULL, '"+ SetupData.ORGID +"', NULL, NULL, NULL, NULL, 1, NULL, 'Start"+featuresId+"', 'End"+featuresId+"', 'BQLOX', 100100.00, 'VRGKJ', 10010.00, 'VRGKJ', 'BQLOX', 'BQLOX', 2, 0)";
		logger.info("insertQuery: {}" + insertQuery);
		jdbcConnection.executeUpdateSQL(insertQuery);
		assetFeatureId = AbstractAutomationFramework.randomInt();
		String insertQuery2 = "INSERT INTO MAXIMO.ASSETFEATURE (ASSETFEATUREID, SHARED, CLASSSTRUCTUREID, STARTMEASURE, ENDMEASURE, STARTOFFSET, ENDOFFSET, STARTYOFFSET, ENDYOFFSET, STARTYOFFSETREF, ENDYOFFSETREF, STARTZOFFSET, ENDZOFFSET, STARTZOFFSETREF, ENDZOFFSETREF, SITEID, LABEL, FEATURE, ASSETNUM, STARTFEATURE, ENDFEATURE, ORGID, ASSETUID, FEATURESID, STARTASSETFEATUREID, ENDASSETFEATUREID, ISLINEARREF, ASSETLOCRELATIONID, STARTFEATURELABEL, ENDFEATURELABEL, BASEMEASUREUNITID, ENDBASEMEASURE, ENDMEASUREUNITID, STARTBASEMEASURE, STARTMEASUREUNITID, STARTOFFSETUNITID, ENDOFFSETUNITID, LINEARASSETFEATUREID, ROWSTAMP) VALUES('"+assetFeatureId2+"', 0, '132"+featuresId2+"', 10.00, 100.00, NULL, NULL, 0.00, 0.00, NULL, NULL, 0.00, 0.00, NULL, NULL, '"+ SetupData.SITEID +"', 'MP"+featuresId2+"', '"+feature2+"', '"+assetNum+"', NULL, NULL, '"+ SetupData.ORGID +"', NULL, NULL, NULL, NULL, 1, NULL, 'Start"+featuresId2+"', 'End"+featuresId2+"', 'BQLOX', 100100.00, 'VRGKJ', 10010.00, 'VRGKJ', 'BQLOX', 'BQLOX', 2, 0)";
		logger.info("insertQuery: {}" + insertQuery2);
		jdbcConnection.executeUpdateSQL(insertQuery2);
	}

	public void createFeatures() throws Exception {
		featuresId = AbstractAutomationFramework.randomInt();
		feature = AbstractAutomationFramework.randomString(5).toUpperCase();
		String insertQuery = "INSERT INTO MAXIMO.FEATURES (FEATURESID, FEATURE, FEATURETYPE, CONTINUOUS, DESCRIPTION, SHARED, CLASSSTRUCTUREID, LANGCODE, HASLD, STATUS, ISLINEARREF, RELATIONID, ROWSTAMP) VALUES('"+featuresId+"', '"+feature+"', 'LINEAR', 0, 'Railroad Ties (aka Sleepers)"+featuresId+"', 0, '132"+featuresId+"', 'EN', 1, 'NOT READY', 0, NULL, 0)";
		logger.info("insertQuery: {}" + insertQuery);
		jdbcConnection.executeUpdateSQL(insertQuery);
		featuresId2 = AbstractAutomationFramework.randomInt();
		feature2 = AbstractAutomationFramework.randomString(5).toUpperCase();
		String insertQuery2 = "INSERT INTO MAXIMO.FEATURES (FEATURESID, FEATURE, FEATURETYPE, CONTINUOUS, DESCRIPTION, SHARED, CLASSSTRUCTUREID, LANGCODE, HASLD, STATUS, ISLINEARREF, RELATIONID, ROWSTAMP) VALUES('"+featuresId2+"', '"+feature2+"', 'LINEAR', 0, 'Linear features second "+featuresId2+"', 0, '132"+featuresId2+"', 'EN', 1, 'NOT READY', 0, NULL, 0)";
		logger.info("insertQuery: {}" + insertQuery2);
		jdbcConnection.executeUpdateSQL(insertQuery2);
	}

	public void createNewLinearReferencingMethod() throws Exception {
		// Create a New Linear Referencing Method
		
		logger.info("Creating a first Measure of Unit");
		String unitOfMeasure = AbstractAutomationFramework.randomString(5).toUpperCase();
		String description = "Mobile automation description " + unitOfMeasure;
		MeasureUnit unit = new MeasureUnit();
		unit.setMeasureUnitId(unitOfMeasure);
		unit.setDescription(description);
		maximoApi.create(unit);
		logger.info("Unit Of Measure: " + unitOfMeasure);

		logger.info("Creating a second Measure of Unit");
		String unitOfMeasure1 = AbstractAutomationFramework.randomString(5).toUpperCase();
		String description1 = "Mobile automation description " + unitOfMeasure1;
		MeasureUnit unit1 = new MeasureUnit();
		unit1.setMeasureUnitId(unitOfMeasure1);
		unit1.setDescription(description1);
		maximoApi.create(unit1);
		logger.info("Unit Of Measure: " + unitOfMeasure1);	

		logger.info("Creating a new Conversation");
		LinearRefConversation linearReferencingConversation = new LinearRefConversation();
		linearReferencingConversation.setToMeasureUnit(unitOfMeasure);
		linearReferencingConversation.setFromMeasureUnit(unitOfMeasure1);
		linearReferencingConversation.setConversion(1001);
		linearReferencingConversation.setItemSetId(SetupData.ITEMSET);
		maximoApi.create(linearReferencingConversation);

		logger.info("Creating a new Linear Referencing Method");
		lrmValue = AbstractAutomationFramework.randomString(5).toUpperCase();
		LinearRefMethod newLRM = new LinearRefMethod();
		newLRM.setLrm(lrmValue);
		newLRM.setDescription(lrmValue + " Description absolute measure in miles");
		newLRM.setYOffSetMeasureUnitId(unitOfMeasure);
		newLRM.setZOffSetMeasureUnitId(unitOfMeasure);
		newLRM.setBaseMeasureUnitId(unitOfMeasure);
		newLRM.setLangCode(SetupData.LANGCODE);
		newLRM.setMeasureUnitId(unitOfMeasure1);
		newLRM.setHasld(false);
		newLRM.setOffSetMeasureUnitId(unitOfMeasure);

		maximoApi.create(newLRM);
		logger.info("New Linear Referencing Method Created: " + lrmValue);
	}
}