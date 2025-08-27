package com.ibm.maximo.technician.testcases;

import static org.testng.Assert.assertEquals;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import com.ibm.maximo.automation.mobile.api.json.Location;
import com.ibm.maximo.automation.mobile.api.json.ServiceAddress;
import com.ibm.maximo.automation.mobile.api.json.WoServiceAddress;
import com.ibm.maximo.automation.mobile.api.json.WorkOrder;
import com.ibm.maximo.technician.framework.TechnicianTest;
import com.ibm.maximo.technician.page.CreateWorkOrderPage;
import com.ibm.maximo.technician.page.EditWorkOrderPage;
import com.ibm.maximo.technician.page.MySchedulePage;
import com.ibm.maximo.technician.page.WorkOrderDetailsPage;
import com.ibm.maximo.technician.setupdata.SetupData;
import com.ibm.maximo.technician.setupdata.SetupData.*;

/*
 * MAXMOA-4233: Technician app - The Service Address should have the option of either showing the 
 * 				Formatted Address or the Service Address Description
 * 
 */

public class ServiceAddressFormattedaddress extends TechnicianTest {
	private static final String LOCATION_DESCRIPTION = "Location for mobile automation test";
	private static final String ASSET_DESCRIPTION = "Asset for mobile automation test";
	private final Logger logger = LoggerFactory.getLogger(ServiceAddressFormattedaddress.class);
	private AbstractAutomationFramework af;
	private TestSuite testSuite;
	private MaximoApi maximoApi;
	private WorkOrder newWorkOrder, newWorkOrder1,newWorkOrder2,newWorkOrder3;
	private String assetNum,woNum2,woNum3, labor, location,woNum1, location1,assetNum1,assetNum2,assetNum3;
	private boolean apiCodeSuccess = false;
	
	@BeforeClass(alwaysRun = true)
	@Parameters({ "configPath" })
	public void setup(String configPath) throws Exception {
		logger.info("********************EditWorkOrderDetailsTestCaseTwo*********************************");
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
		maximoApi.changeStatus(newWorkOrder2, WoStatus.COMP.toString());
		maximoApi.changeStatus(newWorkOrder3, WoStatus.COMP.toString());
		if (testSuite != null) {
			testSuite.teardown();
		}
	}


	
	@Test(groups = {
			"mobile" }, description = "The Service Address should have the option of either showing the Formatted Address or the Service Address Description", timeOut = 10000000)
	public void verifyServiceAddressdescription() throws Exception {

		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage workOrderDetailsPage = new WorkOrderDetailsPage(af);
		EditWorkOrderPage editWODetailsPage = new EditWorkOrderPage(af);

		// Search the WO
		assignedWorkPage.search(woNum);
		//Verify Service address on WOlistPage
		assertEquals("Autoamtion Service address", workOrderDetailsPage.getServiceAddressOnWOListPage());
		// Navigate to work order details page
		assignedWorkPage.openCardDetails();
		// Verify Service address on WODetailPge
		assertEquals("Autoamtion Service address", workOrderDetailsPage.getServiceAddress());
		workOrderDetailsPage.clickBackChevron();
		assignedWorkPage.clickClearButton();
		
		// Search the WO
		assignedWorkPage.search(woNum3);
		//Verify Service address on WOlistPage
		assertEquals("Autoamtion Service address", workOrderDetailsPage.getServiceAddressOnWOListPage());
		// Navigate to Work order details page
		assignedWorkPage.openCardDetails();
		// Verify Service address on WODetailPge
		assertEquals("Autoamtion Service address", workOrderDetailsPage.getServiceAddress());
		// Search the WO
		workOrderDetailsPage.clickBackChevron();
		assignedWorkPage.clickClearButton();
	
		// Search the WO
		assignedWorkPage.search(woNum2);
//		assertEquals(SetupData.ADDRESSTITLE, workOrderDetailsPage.getServiceAddressOnWOListPage()); MAXMOA-4442 Bug raised 
		// Navigate to Work order details page
		assignedWorkPage.openCardDetails();
		// Edit button should be visible
//		assertEquals(SetupData.ADDRESSTITLE, workOrderDetailsPage.getServiceAddressr()); MAXMOA-4442 Bug raised 

	}
	
	
	protected void createDefaultObjects() throws Exception {
		logger.info("Creating default objects");
		// Update System Properties

		// Create a Service Address 1
		logger.info("Creating a Service Address");
		String serAddResult = maximoApi.retrieve(new ServiceAddress(),
				"addid=1&internalvalues=1&action=system:new&addschema=1");
		ServiceAddress newSerAdd = new Gson().fromJson(serAddResult, ServiceAddress.class);
		newSerAdd.setLatitudeY(SetupData.LATITUDEY);
		newSerAdd.setLongitudeX(SetupData.LONGITUDEX);
		newSerAdd.setDescription(SetupData.ADDRESSTITLE);
		newSerAdd.setFormattedAddress("Autoamtion Service address");
		maximoApi.create(newSerAdd);
		logger.info("Service Address Created :" + newSerAdd.getAddressCode());

		// Link Service Address
		logger.info("Link Service Address with Work Order");
		List<WoServiceAddress> serviceAddressList = new ArrayList<WoServiceAddress>();
		WoServiceAddress newWoServiceAddress = new WoServiceAddress();
		newWoServiceAddress.setsAddressCode(newSerAdd.getAddressCode());
		serviceAddressList.add(newWoServiceAddress);
		logger.info("Service Address Linked to Work Order");

		// Create an asset
		logger.info("Creating an asset");
		assetNum = AbstractAutomationFramework.randomString(5).toUpperCase();
		String assetResult = maximoApi.retrieve(new Asset(), "addid=1&internalvalues=1&action=system:new&addschema=1");
		Asset newAsset = new Gson().fromJson(assetResult, Asset.class);
		newAsset.setAssetNum(assetNum);
		newAsset.setDescription(ASSET_DESCRIPTION);
		maximoApi.create(newAsset);
		logger.info("Asset: {}" + assetNum);

		// Create a workorder
		logger.info("Creating a work order");
		String workOrderResult = maximoApi.retrieve(new WorkOrder(),
				"addid=1&internalvalues=1&action=system:new&addschema=1");
		newWorkOrder = new Gson().fromJson(workOrderResult, WorkOrder.class);
		newWorkOrder.setDescription("WorkeOrder for mobile automation test");
		newWorkOrder.setAssetNum(assetNum);
		newWorkOrder.setWorkType(WorkType.PM.toString());
		newWorkOrder.setGLAccount(SetupData.GLDEBITACCT);
		newWorkOrder.setWoServiceAddress(serviceAddressList);
		maximoApi.create(newWorkOrder);
		woNum = newWorkOrder.getWoNum();
		logger.info("Work Order: {}" + woNum);

		// Change WO status to Approved
		logger.info("Changing work order status to APPR");
		maximoApi.changeStatus(newWorkOrder, WoStatus.APPR.toString());

		// Assignment with labor maxadmin
		maximoApi.addAssignmentLabor(newWorkOrder, labor);
		logger.info("Assignment added");

		// Create a Service Address 2
		logger.info("Creating a Service Address");
		String serAddResult2 = maximoApi.retrieve(new ServiceAddress(),
				"addid=1&internalvalues=1&action=system:new&addschema=1");
		ServiceAddress newSerAdd2 = new Gson().fromJson(serAddResult2, ServiceAddress.class);
		newSerAdd2.setLatitudeY(SetupData.LATITUDEY);
		newSerAdd2.setLongitudeX(SetupData.LONGITUDEX);
		newSerAdd2.setDescription(SetupData.ADDRESSTITLE);
		maximoApi.create(newSerAdd2);
		logger.info("Service Address Created :" + newSerAdd2.getAddressCode());

		// Link Service Address
		logger.info("Link Service Address with Work Order");
		List<WoServiceAddress> serviceAddressList2 = new ArrayList<WoServiceAddress>();
		WoServiceAddress newWoServiceAddress2 = new WoServiceAddress();
		newWoServiceAddress2.setsAddressCode(newSerAdd2.getAddressCode());
		serviceAddressList2.add(newWoServiceAddress2);
		logger.info("Service Address Linked to Work Order");

		// Create an asset
		logger.info("Creating an asset");
		assetNum2 = AbstractAutomationFramework.randomString(5).toUpperCase();
		String assetResult2 = maximoApi.retrieve(new Asset(), "addid=1&internalvalues=1&action=system:new&addschema=1");
		Asset newAsset2 = new Gson().fromJson(assetResult2, Asset.class);
		newAsset2.setAssetNum(assetNum2);
		newAsset2.setDescription(ASSET_DESCRIPTION);
		maximoApi.create(newAsset2);
		logger.info("Asset: {}" + assetNum2);

		// Create a workorder
		logger.info("Creating a work order");
		String workOrderResult2 = maximoApi.retrieve(new WorkOrder(),
				"addid=1&internalvalues=1&action=system:new&addschema=1");
		newWorkOrder2 = new Gson().fromJson(workOrderResult2, WorkOrder.class);
		newWorkOrder2.setDescription("WorkeOrder for mobile automation test");
		newWorkOrder2.setAssetNum(assetNum2);
		newWorkOrder2.setWorkType(WorkType.PM.toString());
		newWorkOrder2.setGLAccount(SetupData.GLDEBITACCT);
		newWorkOrder2.setWoServiceAddress(serviceAddressList2);
		maximoApi.create(newWorkOrder2);
		woNum2 = newWorkOrder2.getWoNum();
		logger.info("Work Order: {}" + woNum2);

		// Change WO status to Approved
		logger.info("Changing work order status to APPR");
		maximoApi.changeStatus(newWorkOrder2, WoStatus.APPR.toString());

		// Assignment with labor maxadmin
		maximoApi.addAssignmentLabor(newWorkOrder2, labor);
		logger.info("Assignment added");

		// Create a Service Address 3
		logger.info("Creating a Service Address");
		String serAddResult3 = maximoApi.retrieve(new ServiceAddress(),
				"addid=1&internalvalues=1&action=system:new&addschema=1");
		ServiceAddress newSerAdd3 = new Gson().fromJson(serAddResult3, ServiceAddress.class);
		newSerAdd3.setLatitudeY(SetupData.LATITUDEY);
		newSerAdd3.setLongitudeX(SetupData.LONGITUDEX);
		newSerAdd3.setFormattedAddress("Autoamtion Service address");
		maximoApi.create(newSerAdd3);
		logger.info("Service Address Created :" + newSerAdd3.getAddressCode());

		// Link Service Address
		logger.info("Link Service Address with Work Order");
		List<WoServiceAddress> serviceAddressList3 = new ArrayList<WoServiceAddress>();
		WoServiceAddress newWoServiceAddress3 = new WoServiceAddress();
		newWoServiceAddress3.setsAddressCode(newSerAdd3.getAddressCode());
		serviceAddressList3.add(newWoServiceAddress3);
		logger.info("Service Address 3 Linked to Work Order 3");

		// Create an asset
		logger.info("Creating an asset");
		assetNum3 = AbstractAutomationFramework.randomString(5).toUpperCase();
		String assetResult3 = maximoApi.retrieve(new Asset(), "addid=1&internalvalues=1&action=system:new&addschema=1");
		Asset newAsset3 = new Gson().fromJson(assetResult3, Asset.class);
		newAsset3.setAssetNum(assetNum3);
		newAsset3.setDescription(ASSET_DESCRIPTION);
		maximoApi.create(newAsset3);
		logger.info("Asset: {}" + assetNum3);

		// Create a workorder
		logger.info("Creating a work order");
		String workOrderResult3 = maximoApi.retrieve(new WorkOrder(),
				"addid=1&internalvalues=1&action=system:new&addschema=1");
		newWorkOrder3 = new Gson().fromJson(workOrderResult3, WorkOrder.class);
		newWorkOrder3.setDescription("WorkeOrder for mobile automation test");
		newWorkOrder3.setAssetNum(assetNum3);
		newWorkOrder3.setWorkType(WorkType.PM.toString());
		newWorkOrder3.setGLAccount(SetupData.GLDEBITACCT);
		newWorkOrder3.setWoServiceAddress(serviceAddressList3);
		maximoApi.create(newWorkOrder3);
		woNum3 = newWorkOrder3.getWoNum();
		logger.info("Work Order: {}" + woNum3);

		// Change WO status to Approved
		logger.info("Changing work order status to APPR");
		maximoApi.changeStatus(newWorkOrder3, WoStatus.APPR.toString());

		// Assignment with labor maxadmin
		maximoApi.addAssignmentLabor(newWorkOrder3, labor);
		logger.info("Assignment added ");


	}
}