package com.ibm.maximo.technician.testcases;

import static org.testng.Assert.assertEquals;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import com.ibm.maximo.automation.mobile.api.json.Location;
import com.ibm.maximo.automation.mobile.api.json.WorkOrder;
import com.ibm.maximo.technician.framework.TechnicianTest;
import com.ibm.maximo.technician.page.EditWorkOrderPage;
import com.ibm.maximo.technician.page.MySchedulePage;
import com.ibm.maximo.technician.page.WorkOrderDetailsPage;
import com.ibm.maximo.technician.setupdata.SetupData;
import com.ibm.maximo.technician.setupdata.SetupData.*;

/*
 * GRAPHITE-51069: [TECHMOBILE] Edit work order page :71M,1TA,1A
 * 
 */

public class EditWorkOrderDetailsTest extends TechnicianTest {
	private static final String LOCATION_DESCRIPTION = "Location for mobile automation test";
	private static final String ASSET_DESCRIPTION = "Asset for mobile automation test";
	private static final String CHANGE_ASSET_DESCRIPTION = "Change Asset for mobile automation test";
	private static final String CHANGE_LOCATION_DESCRIPTION = "Change location for mobile automation test";
	private static final String WO_LONG_DESCRIPTION = "Long Description for WorkOrder Details Page - Mobile automation test";
	private final Logger logger = LoggerFactory.getLogger(EditWorkOrderDetailsTest.class);
	private AbstractAutomationFramework af;
	private TestSuite testSuite;
	private MaximoApi maximoApi;
	private WorkOrder newWorkOrder, newWorkOrder1, newWorkOrder2;
	private String assetNum, woNum, woNum1, woNum2,labor, location, assetNumChange, changeLocation,dateType;
	private String descriptionStr = "Change Description of WO", longDesText = "enter edit long description",
			Number = "1", invalidPriorityNumber = "1001";
	private int startYear = 2022, finishYear = 2030, startMonth = 8, startDate = 10, hour = 10, minute = 10;
	private String priorityErrorMessageBefore="Enter a valid wopriority.";
	private String priorityErrorMessageAfter="Priority 1001 is not a valid priority value between 0 and 999";

	@BeforeClass(alwaysRun = true)
	@Parameters({ "configPath" })
	public void setup(String configPath) throws Exception {
		logger.info("********************EditWorkOrderDetailsTest*********************************");
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
		logger.info("Changing work order status to COMP");
		maximoApi.changeStatus(newWorkOrder, WoStatus.COMP.toString());
		if (testSuite != null) {
			testSuite.teardown();
		}
	}

	@Test(groups = {
			"mobile" }, description = "Verify all the edits/changes made on the WO edit page are reflected on the work order details page", timeOut = 900000)
	public void editWOAndVerifyChangesOnWODetails() throws Exception {

		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage workOrderDetailsPage = new WorkOrderDetailsPage(af);
		EditWorkOrderPage editWODetailsPage = new EditWorkOrderPage(af);

		// Search the WO
		assertEquals(true, assignedWorkPage.search(woNum));
		// Navigate to work order details page
		assignedWorkPage.openCardDetails();

		workOrderDetailsPage.editWorkOrderDetails();

		editWODetailsPage.enterDescription(descriptionStr + woNum);
		editWODetailsPage.enterLongDescription(longDesText);
		editWODetailsPage.enterPriority(Number);
		 dateType=dateConfiguration(); 
		if(dateType.contains("SCHEDULE") && dateType.contains("TARGET") ) {
			editWODetailsPage.scheduledStartDateAndTime(startYear, startMonth, startDate, hour, minute);
			editWODetailsPage.scheduledFinishDateAndTime(finishYear, startMonth, startDate, hour, minute);
			
			editWODetailsPage.targetStartDateAndTime(startYear, startMonth, startDate, hour, minute);
			editWODetailsPage.targetFinishDateAndTime(finishYear, startMonth, startDate, hour, minute);
			}	else if(dateType.contains("TARGET") ) {
			
				editWODetailsPage.targetStartDateAndTime(startYear, startMonth, startDate, hour, minute);
				editWODetailsPage.targetFinishDateAndTime(finishYear, startMonth, startDate, hour, minute);
				} else {
					editWODetailsPage.scheduledStartDateAndTime(startYear, startMonth, startDate, hour, minute);
					editWODetailsPage.scheduledFinishDateAndTime(finishYear, startMonth, startDate, hour, minute);
				}
		editWODetailsPage.estimatedDuration(Number, Number);
		editWODetailsPage.changeWorkType(WorkType.CM);
		editWODetailsPage.assetNumber(assetNumChange);
		editWODetailsPage.locationNumber(changeLocation);
		editWODetailsPage.saveEditedWODetails();

//		verify changes in WO details

		String actualString = WorkOrderDetailsPage.getTextWODescription();
		logger.info("WO Description >" + actualString);
		assertEquals(true, actualString.contains(descriptionStr.concat(woNum.trim())));

		String longDes = workOrderDetailsPage.getTextWOLongDescription();
		logger.info("WO Long Description >" + longDes);
		assertEquals(longDes, longDesText);

		String actualStr = workOrderDetailsPage.getTextWOPriority();
		logger.info("WO Priority >" + actualStr);
		assertEquals(actualStr, "Priority " + Number);
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



		String actualEstimated = workOrderDetailsPage.getTextWOEstimated();
		logger.info("WO Estimated >" + actualEstimated);
		assertEquals(actualEstimated, Number + " hour " + Number + " minute");

		String asset = workOrderDetailsPage.getTextWOAssetNum();
		logger.info("WO Asset Number >" + asset);
		assertEquals(asset, assetNumChange);

		// Bug Created GRAPHITE-67731
//	  String assetChange = workOrderDetailsPage.getTextWOAssetName();
//	  logger.info("WO Asset Name >" + assetChange); // assetNameChange
//	  assertEquals(assetChange, CHANGE_ASSET_DESCRIPTION);
//	  
//	  String loc = workOrderDetailsPage.getTextWOLocationName();
//	  logger.info("WO Location name >" + loc); 
//	  assertEquals(loc,CHANGE_LOCATION_DESCRIPTION);
	  
	  String actualType = workOrderDetailsPage.getTextWOType();
	  logger.info("WO Type >" + actualType.toString().trim());
	  assertEquals(true, actualString.contains(WorkType.CM.toString()+" "+woNum));

	}

	@Test(groups = {
			"priority2" }, description = "Verify that 'Long description' of work order is displayed in Details section on WO details page", timeOut = 9500000)
	public void verifyWOLongDescription() throws Exception {
		MySchedulePage assignedWorkPage = new MySchedulePage(af);

		logger.info(
				"Scenario 21 - Verify that 'Long description' of work order is displayed in Details section on WO details page");
		// Search the work order
		assignedWorkPage.search(woNum1);
		assignedWorkPage.openWorkOrderDetails();
		WorkOrderDetailsPage woDetailPage = new WorkOrderDetailsPage(af);

		// Verify Long description present
		assertEquals(true,
				woDetailPage.getTextWOLongDescription() != null || woDetailPage.getTextWOLongDescription() != " ");
		logger.info("Long Description are present");

		woDetailPage.clickBackChevron();
		logger.info("Back to Schedule list page");
	}

	@Test(groups = { "priority2" }, description = "Verify Invalid priority error message", timeOut = 900000)
	public void verifyInvalidPriority() throws Exception {

		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage workOrderDetailsPage = new WorkOrderDetailsPage(af);
		EditWorkOrderPage editWODetailsPage = new EditWorkOrderPage(af);

		// Search the WO
		assertEquals(true, assignedWorkPage.search(woNum2));
		// Navigate to work order details page
		assignedWorkPage.openCardDetails();

		workOrderDetailsPage.editWorkOrderDetails();

		editWODetailsPage.enterDescription(descriptionStr + woNum2);
		editWODetailsPage.enterLongDescription(longDesText);
		editWODetailsPage.enterPriority(invalidPriorityNumber);

		// verify priority error message on graphite level
		assertEquals(priorityErrorMessageBefore, editWODetailsPage.getErrorMsgForWrongPriority());

		editWODetailsPage.estimatedDuration(Number, Number);

		// verify priority error message on component level
		assertEquals(priorityErrorMessageAfter, editWODetailsPage.getErrorMsgForWrongPriority());

		// verify save button should be disable
		assertEquals(false, editWODetailsPage.verifySaveButton());

		// Navigate back to WO details page
		editWODetailsPage.goBackToWODetailsPage();

		// Click on discard button
		editWODetailsPage.discardButtonClick();
		workOrderDetailsPage.clickBackChevronToListPage();
		assignedWorkPage.clickClearButton();

	}

	protected void createDefaultObjects() throws Exception {
			logger.info("Creating default objects");

			// Create an asset
			logger.info("Creating an asset");
			assetNum = AbstractAutomationFramework.randomString(5).toUpperCase();
			String assetResult = maximoApi.retrieve(new Asset(), "addid=1&internalvalues=1&action=system:new&addschema=1");

			Asset newAsset = new Gson().fromJson(assetResult, Asset.class);

			newAsset.setAssetNum(assetNum);
			newAsset.setDescription(ASSET_DESCRIPTION);
			newAsset.setSiteId(SetupData.SITEID);

			maximoApi.create(newAsset);
			logger.info("Asset: {}" + assetNum);

			// Create a location
			logger.info("Creating a location");
			location = AbstractAutomationFramework.randomString(5).toUpperCase();
			Location newlocation = new Location();

			newlocation.setLocationId(location);
			newlocation.setDescription(LOCATION_DESCRIPTION);
			newlocation.setSiteId(SetupData.SITEID);

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
			logger.info("Changing work order status to APPR");
			maximoApi.changeStatus(newWorkOrder, WoStatus.APPR.toString());
			// Assignment with labor maxadmin
			maximoApi.addAssignmentLabor(newWorkOrder, labor);
			logger.info("Assignment added");

			// Create an asset
			logger.info("Creating an asset");
			assetNumChange = AbstractAutomationFramework.randomString(5).toUpperCase();
			String assetResultChange = maximoApi.retrieve(new Asset(),
					"addid=1&internalvalues=1&action=system:new&addschema=1");

			Asset changeAsset = new Gson().fromJson(assetResultChange, Asset.class);

			changeAsset.setAssetNum(assetNumChange);
			changeAsset.setDescription(CHANGE_ASSET_DESCRIPTION);
			changeAsset.setSiteId(SetupData.SITEID);

			maximoApi.create(changeAsset);
			logger.info("changeAsset: {}" + assetNumChange);

			// Create a location
			logger.info("Creating a location");
			changeLocation = AbstractAutomationFramework.randomString(5).toUpperCase();
			Location changelocation = new Location();

			changelocation.setLocationId(changeLocation);
			changelocation.setDescription(CHANGE_LOCATION_DESCRIPTION);
			changelocation.setSiteId(SetupData.SITEID);

			maximoApi.create(changelocation);
			logger.info("changeLocation: {}" + changeLocation);

		// Create a Second workorder
		logger.info("Creating a second work order");
		woNum1 = AbstractAutomationFramework.randomString(5).toUpperCase();
		String workOrderResult1 = maximoApi.retrieve(new WorkOrder(),
				"addid=1&internalvalues=1&action=system:new&addschema=1");
		newWorkOrder1 = new Gson().fromJson(workOrderResult1, WorkOrder.class);
		newWorkOrder1.setWoNum(woNum1);
		newWorkOrder1.setDescription("WorkeOrder for mobile automation test");
		newWorkOrder1.setLongDescription(WO_LONG_DESCRIPTION);
		newWorkOrder1.setSiteId(SetupData.SITEID);
		newWorkOrder1.setWorkType(WorkType.PM.toString());
		maximoApi.create(newWorkOrder1);
		logger.info("Work Order: {}" + woNum1);

		// Change WO status to Approved
		logger.info("Changing work order status to APPR");
		maximoApi.changeStatus(newWorkOrder1, WoStatus.APPR.toString());
		// Assignment with labor
		maximoApi.addAssignmentLabor(newWorkOrder1, labor);
		logger.info("Assignment added");
		
		// Create a third workorder
		logger.info("Creating a third work order");
		woNum2 = AbstractAutomationFramework.randomString(5).toUpperCase();
		String workOrderResult2 = maximoApi.retrieve(new WorkOrder(),
				"addid=1&internalvalues=1&action=system:new&addschema=1");
		newWorkOrder2 = new Gson().fromJson(workOrderResult2, WorkOrder.class);
		newWorkOrder2.setWoNum(woNum2);
		newWorkOrder2.setDescription("WorkeOrder for mobile automation test");
		newWorkOrder2.setLongDescription(WO_LONG_DESCRIPTION);
		newWorkOrder2.setSiteId(SetupData.SITEID);
		newWorkOrder2.setWorkType(WorkType.PM.toString());
		maximoApi.create(newWorkOrder2);
		logger.info("Work Order: {}" + woNum2);

		// Change WO status to Approved
		logger.info("Changing work order status to APPR");
		maximoApi.changeStatus(newWorkOrder2, WoStatus.APPR.toString());
		// Assignment with labor
		maximoApi.addAssignmentLabor(newWorkOrder2, labor);
		logger.info("Assignment added");
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

