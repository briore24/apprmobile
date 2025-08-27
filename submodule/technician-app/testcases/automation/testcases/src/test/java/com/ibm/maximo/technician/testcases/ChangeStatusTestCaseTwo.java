package com.ibm.maximo.technician.testcases;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

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
import com.ibm.maximo.automation.mobile.api.json.WorkOrder;
import com.ibm.maximo.technician.framework.TechnicianTest;

import com.ibm.maximo.technician.page.MySchedulePage;
import com.ibm.maximo.technician.page.ReportWorkPage;
import com.ibm.maximo.technician.page.WorkOrderDetailsPage;
import com.ibm.maximo.technician.setupdata.SetupData;
import com.ibm.maximo.technician.setupdata.SetupData.*;

//GRAPHITE-65541: Change WO Status: 47M,1TA,0A

public class ChangeStatusTestCaseTwo extends TechnicianTest {
	private final Logger logger = LoggerFactory.getLogger(ChangeStatusTestCaseTwo.class);
	private AbstractAutomationFramework af;
	private TestSuite testSuite;
	private MaximoApi maximoApi;
	private String assetNum, woNum, labor;
	private WorkOrder newWorkOrder;

	@BeforeClass(alwaysRun = true)
	@Parameters({ "configPath" })
	public void setup(String configPath) throws Exception {
		logger.info("********************ChangeStatusTestCaseTwo*********************************");
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
		if (testSuite != null) {
			testSuite.teardown();
		}
	}

	@Test(groups = {
			"priority2" }, description = "Verify while completing a WO, a toast/message is not displayed which is for information only, if the asset is Down and WORKTYPE.PROMPTDOWN is unchecked", timeOut = 900000)
	public void downPromptMessage() throws Exception {

		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage assignedWorkOrderDetailsPage = new WorkOrderDetailsPage(af);
		ReportWorkPage assignedReportWorkPage = new ReportWorkPage(af);

		assignedWorkPage.search(woNum);
		assignedWorkPage.openWorkOrderDetails();
		String workOrderHeader = assignedWorkOrderDetailsPage
				.getTitle(assignedWorkOrderDetailsPage.workOrderHeaderText);
		assertEquals("Work order", workOrderHeader);
		logger.info("Change the asset status to Down");
		assignedWorkOrderDetailsPage.openAssetStatusDrawer();
		logger.info("Open asset status drawer");
		assignedWorkOrderDetailsPage.clickAssetDownButton();
		logger.info("Asset down button is clicked");
		assignedWorkOrderDetailsPage.saveAssetStatus();
		logger.info("Save asset status changed");
		assignedWorkOrderDetailsPage.clickReportWorkButton();
		logger.info("Report Work button clicked");
		assignedReportWorkPage.completeWorkOrder();
		logger.info("Work Order Completed");
		assertTrue(assignedReportWorkPage.waitForSystemMsgDialog());
		assertEquals("Work order " + woNum + " completed", assignedReportWorkPage.toastMessageDisplayed());
	}

	/**
	 * Method to create work order and asset
	 * 
	 * @throws Exception
	 */
	protected void createDefaultObjects() throws Exception {

		logger.info("Creating default objects");

		// Create an asset
		logger.info("Creating an asset");
		assetNum = AbstractAutomationFramework.randomString(5).toUpperCase();
		String assetResult = maximoApi.retrieve(new Asset(), "addid=1&internalvalues=1&action=system:new&addschema=1");
		Asset newAsset = new Gson().fromJson(assetResult, Asset.class);
		newAsset.setAssetNum(assetNum);
		newAsset.setSiteId(SetupData.SITEID);
		newAsset.setStatus(SetupData.LocAssetStatus.ACTIVE.toString());
		maximoApi.create(newAsset);
		logger.info("Asset: " + assetNum);

		// Create a work order
		logger.info("Creating a work order");
		woNum = AbstractAutomationFramework.randomString(5).toUpperCase();
		String workOrderResult = maximoApi.retrieve(new WorkOrder(),
				"addid=1&internalvalues=1&action=system:new&addschema=1");
		WorkOrder newWorkOrder = new Gson().fromJson(workOrderResult, WorkOrder.class);
		newWorkOrder.setWoNum(woNum);
		newWorkOrder.setDescription("WorkOrder for mobile automation test");
		newWorkOrder.setSiteId(SetupData.SITEID);
		newWorkOrder.setAssetNum(assetNum);
		newWorkOrder.setWorkType(SetupData.WorkType.PM.toString());
		maximoApi.create(newWorkOrder);
		logger.info("Work Order: " + woNum);

		// Change WO status to Approved
		maximoApi.changeStatus(newWorkOrder, WoStatus.APPR.toString());
		logger.info("Changing work order status to APPR");

		// Assign the labor
		maximoApi.addAssignmentLabor(newWorkOrder, labor);
		logger.info("Assignment added");

		resetSystemProperty();
		checkAssetDownPromptValue();
	}

	public void checkAssetDownPromptValue() {
		logger.info("Running SQL - Setting Organization settings");
		logger.info("Check the WORKTYPE.PROMPTDOWN value");
		jdbcConnection.executeUpdateSQL("update MAXIMO.maxvars M set M.varvalue = '1' where M.varname = 'DOWNPROMPT' and M.orgid = '"+SetupData.ORGID+"'");
	}

	protected void resetSystemProperty() throws Exception {
		logger.info("reset system properties to default value");
		maximoApi.setProperty("maximo.mobile.completestatus", "COMMON", null, WoStatus.COMP.toString());
		maximoApi.setProperty("maximo.mobile.statusforphysicalsignature", "COMMON", null, "");
		logger.info("Reset Default Done maximo.mobile.statusforphysicalsignature "
				+ maximoApi.getSystemProps("maximo.mobile.statusforphysicalsignature"));
		logger.info("Reset Default Done maximo.mobile.completestatus "
				+ maximoApi.getSystemProps("maximo.mobile.completestatus"));
	}
}
