package com.ibm.maximo.technician.testcases;

import static org.testng.Assert.assertEquals;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.ibm.maximo.automation.mobile.AbstractAutomationFramework;
import com.ibm.maximo.automation.mobile.FrameworkFactory;
import com.ibm.maximo.automation.mobile.api.MaximoApi;
import com.ibm.maximo.automation.mobile.api.json.Asset;
import com.ibm.maximo.automation.mobile.api.json.WorkOrder;
import com.ibm.maximo.automation.mobile.page.login.LoginPage;
import com.ibm.maximo.technician.framework.TechnicianTest;
import com.ibm.maximo.technician.page.MySchedulePage;
import com.ibm.maximo.technician.page.WorkOrderDetailsPage;
import com.ibm.maximo.automation.mobile.api.json.ServiceAddress;
import com.ibm.maximo.automation.mobile.api.json.WoServiceAddress;
import com.google.gson.Gson;
import com.ibm.maximo.technician.setupdata.SetupData;
import com.ibm.maximo.technician.setupdata.SetupData.*;

/*
 * MASR-496: System Property Travel Radius (mxe.mobile.travel.radius) should support a decimal setting
 */

public class StartTravelTest extends TechnicianTest {
	private final Logger logger = LoggerFactory.getLogger(StartTravelTest.class);
	private AbstractAutomationFramework af;
	private TestSuite testSuite;
	private MaximoApi maximoApi;
	public static LoginPage lp;
	private WorkOrder newWorkOrder;
	private String woNum, labor;

	@BeforeClass(alwaysRun = true)
	@Parameters({ "configPath" })
	public void setup(String configPath) throws Exception {
		logger.info("********************StartTravelTest*********************************");
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

	@Test(groups = { "mobile", "desktop" } , description = "Verify user is able to Start Travel", timeOut = 500000)
	public void startTravel() throws Exception {

		// Open work order list page
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage woDetailsPage = new WorkOrderDetailsPage(af);

		assertEquals(true, assignedWorkPage.search(woNum));
		// Click Start Travel
		assignedWorkPage.clickStartTravelButton();
		// Check if Redirected to Work ORder Details page
		assertEquals("Work order", woDetailsPage.getInfo());
		// Check for the Stop Travel button on WO Details Page
		assertEquals("carbon:stop", woDetailsPage.getStopTravelButtonText());
		// Click Stop Travel
		woDetailsPage.clickStopTravelButton();
		// Click Send Button
		woDetailsPage.clickSendButton();
		// Check if Redirected to Work ORder Details page
		assertEquals("Work order", woDetailsPage.getInfo());
		// Click Back Chevron
		woDetailsPage.clickBackChevron();
		assignedWorkPage.clickClearButton();
	}

	protected void createDefaultObjects() throws Exception {
		logger.info("Creating default objects");
		// Update System Properties
		maximoApi.setProperty("mxe.mobile.travel.radius", "COMMON", null, "0.2"); // if technician and WO location is far from 0.2 miles then start travel button will be displayed.
		maximoApi.setProperty("mxe.mobile.travel.prompt", "COMMON", null, "1");
		logger.info("System Properties Set Successfully");

		// Create a Service Address
		logger.info("Creating a Service Address");
		String serAddResult = maximoApi.retrieve(new ServiceAddress(),
				"addid=1&internalvalues=1&action=system:new&addschema=1");
		ServiceAddress newSerAdd = new Gson().fromJson(serAddResult, ServiceAddress.class);
		// Eiffel Tower latitude and longitude coordinates
		newSerAdd.setLatitudeY("48.8584");
		newSerAdd.setLongitudeX("2.2945");
		newSerAdd.setDescription(SetupData.ADDRESSTITLE);
		maximoApi.create(newSerAdd);
		logger.info("Service Address Created :" + newSerAdd.getAddressCode());

		// Link Service Address
		logger.info("Link Service Address with Work Order");
		List<WoServiceAddress> serviceAddressList = new ArrayList<WoServiceAddress>();
		WoServiceAddress newWoServiceAddress = new WoServiceAddress();
		newWoServiceAddress.setsAddressCode(newSerAdd.getAddressCode());
		serviceAddressList.add(newWoServiceAddress);
		logger.info("Service Address Linked to Work Order");

		// Create a workorder
		logger.info("Creating a work order");
		String workOrderResult = maximoApi.retrieve(new WorkOrder(),
				"addid=1&internalvalues=1&action=system:new&addschema=1");
		newWorkOrder = new Gson().fromJson(workOrderResult, WorkOrder.class);
		newWorkOrder.setDescription("WorkeOrder for mobile automation test");
		newWorkOrder.setWorkType(WorkType.PM.toString());
		newWorkOrder.setGLAccount(SetupData.GLDEBITACCT);
		newWorkOrder.setWoServiceAddress(serviceAddressList);
		maximoApi.create(newWorkOrder);
		woNum = newWorkOrder.getWoNum();
		logger.info("Work Order: {}" + woNum);

		// Change WO status to Approved
		logger.info("Changing work order status to APPR");
		maximoApi.changeStatus(newWorkOrder, WoStatus.APPR.toString());

		// Assignment with labor 
		maximoApi.addAssignmentLabor(newWorkOrder, labor);
		logger.info("Assignment added");
	}

}
