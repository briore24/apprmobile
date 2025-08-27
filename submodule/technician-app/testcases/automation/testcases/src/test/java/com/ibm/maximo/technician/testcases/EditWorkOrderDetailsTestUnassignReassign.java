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
import com.ibm.maximo.automation.mobile.MobileAutomationFramework;
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
 * MAXMOA-6559: [TECHMOBILE] (FSM) Allow technicians to assign/reassign work :71M,4A
 * 
 */

public class EditWorkOrderDetailsTestUnassignReassign extends TechnicianTest {
	private static final String LOCATION_DESCRIPTION = "Location for mobile automation test";
	private static final String ASSET_DESCRIPTION = "Asset for mobile automation test";
	private static final String CHANGE_ASSET_DESCRIPTION = "Change Asset for mobile automation test";
	private static final String CHANGE_LOCATION_DESCRIPTION = "Change location for mobile automation test";
	private static final String WO_LONG_DESCRIPTION = "Long Description for WorkOrder Details Page - Mobile automation test";
	private final Logger logger = LoggerFactory.getLogger(EditWorkOrderDetailsTestUnassignReassign.class);
	private AbstractAutomationFramework af;
	private TestSuite testSuite;
	private MaximoApi maximoApi;
	private WorkOrder newWorkOrder, newWorkOrder1, newWorkOrder2,newWorkOrder3;
	private String assetNum, woNum, woNum1, woNum2,woNum3,labor,labor2, location, assetNumChange, changeLocation,dateType;
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
			labor2 = properties.getProperty("app.username1");
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
		logger.info("Changing work order status to COMP");
		maximoApi.changeStatus(newWorkOrder1, WoStatus.COMP.toString());
		logger.info("Changing work order status to COMP");
		maximoApi.changeStatus(newWorkOrder2, WoStatus.COMP.toString());
		logger.info("Changing work order status to COMP");
		maximoApi.changeStatus(newWorkOrder3, WoStatus.COMP.toString());
		if (testSuite != null) {
			testSuite.teardown();
		}
	}

	@Test(groups = {
			"mobile1" }, description = "Reassign work order from WO list", timeOut = 900000)
	public void reassignWoFromWoList() throws Exception {

		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage workOrderDetailsPage = new WorkOrderDetailsPage(af);
		EditWorkOrderPage editWODetailsPage = new EditWorkOrderPage(af);

		// Search the WO
		assertEquals(true, assignedWorkPage.search(woNum));
		// Navigate to work order details page
		
		editWODetailsPage.clickReassignmentWoListPage();
		editWODetailsPage.clickreassignButton();
		editWODetailsPage.clickLaborAssignmentSearch(labor2);
		editWODetailsPage.clickLaborSelectionIcon();
		assertEquals(false, assignedWorkPage.search(woNum));
		
//		logOut(af);
//		// Relogin to enter different credentials
//		reLoginWithDifferentCredentials();
//		assertEquals(false, assignedWorkPage.search(woNum1));
//		assignedWorkPage.clickClearButton();


	}

	@Test(groups = {
			"mobile" }, description = "Unassign work order from WOList", timeOut = 9500000)
	public void unassignWoFromWoList() throws Exception {
		MySchedulePage assignedWorkPage = new MySchedulePage(af);

		WorkOrderDetailsPage workOrderDetailsPage = new WorkOrderDetailsPage(af);
		EditWorkOrderPage editWODetailsPage = new EditWorkOrderPage(af);

		// Search the WO
		assertEquals(true, assignedWorkPage.search(woNum3));
		MobileAutomationFramework maf = (MobileAutomationFramework) this.af;
		editWODetailsPage.clickReassignmentWoListPage();
		maf.scrollPage(1, 2);
		//Bug Created MAXMOA-7381
		editWODetailsPage.clickLaborUnassignButton();
		editWODetailsPage.clickUnassignmentCode();
		editWODetailsPage.clickBlueTickUnassignment();
		assertEquals(false, assignedWorkPage.search(woNum3));
	}

	

	@Test(groups = {
			"mobile1" }, description = "Reassign work order from WO Details", timeOut = 900000)
	public void reassignWoFromWoDetails() throws Exception {

		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage workOrderDetailsPage = new WorkOrderDetailsPage(af);
		EditWorkOrderPage editWODetailsPage = new EditWorkOrderPage(af);

		// Search the WO
		assertEquals(true, assignedWorkPage.search(woNum1));
		// Navigate to work order details page
		assignedWorkPage.openCardDetails();
		
		editWODetailsPage.clickReassignmentDetail();
		editWODetailsPage.clickreassignButton();
		editWODetailsPage.clickLaborAssignmentSearch(labor2);
		editWODetailsPage.clickLaborSelectionIcon();
		WorkOrderDetailsPage.clickBackWOList();
		// Clear Search Result

		// Clear Search Result
		assignedWorkPage.clickClearButton();
		assertEquals(false, assignedWorkPage.search(woNum1));
//	logOut(af);
//	// Relogin to enter different credentials
//	reLoginWithDifferentCredentials();
//	assertEquals(false, assignedWorkPage.search(woNum1));
//	assignedWorkPage.clickClearButton();



	}

	@Test(groups = {
			"mobile" }, description = "Unassign work order from WO Details", timeOut = 9500000)
	public void unassignWoFromWoDetails() throws Exception {
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		MobileAutomationFramework maf = (MobileAutomationFramework) this.af;
		WorkOrderDetailsPage workOrderDetailsPage = new WorkOrderDetailsPage(af);
		EditWorkOrderPage editWODetailsPage = new EditWorkOrderPage(af);
		assertEquals(true, assignedWorkPage.search(woNum2));
		// Search the WO
		// Navigate to work order details page
		assignedWorkPage.openCardDetails();
		
		editWODetailsPage.clickReassignmentDetail();
		maf.scrollPage(1, 2);
		//Bug Created MAXMOA-7381
		editWODetailsPage.clickLaborUnassignButton();
		editWODetailsPage.clickUnassignmentCode();
		editWODetailsPage.clickBlueTickUnassignment();
		assertEquals(false, assignedWorkPage.search(woNum2));
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
		
		// Create a third workorder
		logger.info("Creating a forth work order");
		woNum3 = AbstractAutomationFramework.randomString(5).toUpperCase();
		String workOrderResult3 = maximoApi.retrieve(new WorkOrder(),
				"addid=1&internalvalues=1&action=system:new&addschema=1");
		newWorkOrder3 = new Gson().fromJson(workOrderResult3, WorkOrder.class);
		newWorkOrder3.setWoNum(woNum3);
		newWorkOrder3.setDescription("WorkeOrder for mobile automation test");
		newWorkOrder3.setLongDescription(WO_LONG_DESCRIPTION);
		newWorkOrder3.setSiteId(SetupData.SITEID);
		newWorkOrder3.setWorkType(WorkType.PM.toString());
		maximoApi.create(newWorkOrder3);
		logger.info("Work Order: {}" + woNum3);

		// Change WO status to Approved
		logger.info("Changing work order status to APPR");
		maximoApi.changeStatus(newWorkOrder3, WoStatus.APPR.toString());
		// Assignment with labor
		maximoApi.addAssignmentLabor(newWorkOrder3, labor);
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
	
	



}

