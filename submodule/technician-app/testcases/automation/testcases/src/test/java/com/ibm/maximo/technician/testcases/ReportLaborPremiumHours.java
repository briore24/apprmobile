package com.ibm.maximo.technician.testcases;

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
import com.ibm.maximo.automation.mobile.api.json.WorkOrder;
import com.ibm.maximo.technician.framework.TechnicianTest;
import com.ibm.maximo.technician.page.MySchedulePage;
import com.ibm.maximo.technician.page.ReportWorkPage;
import com.ibm.maximo.technician.page.WorkOrderDetailsPage;
import com.ibm.maximo.technician.setupdata.SetupData;
import com.ibm.maximo.technician.setupdata.SetupData.*;

/*
 * MASR-793: Report Labor Premium Hours
 */
public class ReportLaborPremiumHours extends TechnicianTest {
	private final Logger logger = LoggerFactory.getLogger(ReportLaborPremiumHours.class);
	private AbstractAutomationFramework af;
	private TestSuite testSuite;
	private MaximoApi maximoApi;
	private String woNum, timezone, labor, laborName, masServer;
	private String premiumPayHours = "1", premiumPayMinutes = "00";
	private WorkOrder newWorkOrder;
	private String PremiumPayCode,PremiumPayDescription, PremiumRateType = "MULTIPLIER", PremiumPayRate = "1.5";
	private int PremiumPayId, PpCraftRateId;
	private String craftDesc, skill;
	private boolean apiCodeSuccess = false;

	@BeforeClass(alwaysRun = true)
	@Parameters({ "configPath" })
	public void setup(String configPath) throws Exception {
		logger.info("********************ReportLaborPremiumHours*********************************");
		this.af = FrameworkFactory.get();
		Properties properties = new Properties();
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(configPath));
			properties.load(in);
			timezone = properties.getProperty("capabilities.browserstack.timezone") != null ? properties.getProperty("capabilities.browserstack.timezone") : "UTC";
			labor = properties.getProperty("system.username");
			maximoApi = new MaximoApi();
			maximoApi.setMaximoServer(properties.getProperty("system.maximoServerUrl"),
					properties.getProperty("system.maximoAPIKey"));
			masServer = properties.getProperty("system.masServer");
			// timezone is set in default information of user
			defaultInformationOfUserTimeZone(timezone, labor);
			// EAM for user - wilson , FVT for user - username9 from MobileSetup4MAS.data
			premiumPayAndPPCRAFTRATE();
			
			skill = SetupData.SKILL;
			laborName = SetupData.LABORNAME;
			// EAM env
			if (masServer.equals("false")) {
				// sandbox
				craftDesc = "Electrician";

			} else {
				// FVT SetupData , IVT might be same as sandbox or different
				craftDesc = "Mobile Automation Craft";
			}
			createDefaultObjects();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!apiCodeSuccess) {
			logger.info("stopped framework and quit as API failed = " + apiCodeSuccess);
			FrameworkFactory.stopAll();
		} else {
			login(af);
			if (masServer.equals("true")) {
				// Data will update for IVT
				updateData();
			}
		}
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
			"mobile", "desktop" }, description = "Verify labor Premium pay code functionality on Report work page", timeOut = 800000)
	public void laborPremiumhrs() throws Exception {
		// Search the work order
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		ReportWorkPage reportWork = new ReportWorkPage(af);
		WorkOrderDetailsPage woDetails = new WorkOrderDetailsPage(af);		
		assignedWorkPage.search(woNum);
		// Navigate to WO details page
		assignedWorkPage.openWorkOrderDetails();
		// Navigate to Report work page
		woDetails.clickReportWorkButton();
		// Click on plus icon of labor
		reportWork.clickPlusLaborButton();
		logger.info("Craft on edit labor drawer: " + craftDesc);
		assertEquals(craftDesc, reportWork.getLaborCraftOnDrawer());
		logger.info("Skill level on edit labor drawer: " + skill);
		assertEquals(skill, reportWork.getLaborSkillOnDrawer());
		assertEquals("Premium pay code", reportWork.getPremiumPayCodeLabel());
		reportWork.clickPremiumPayCodeChevron();
		reportWork.searchAndSelectPremiumPayCode(PremiumPayCode);
		assertEquals(PremiumPayRate, reportWork.getPremiumPayRate());
		assertEquals(PremiumRateType, reportWork.getPremiumRateType());
		reportWork.enterPremiumPayHours(premiumPayHours, premiumPayMinutes);		

		// Verify labor record is saved with updated values when technician clicks on
		// "Save" button on labor drawer
		reportWork.saveLabor();

	}


	protected void createDefaultObjects() throws Exception {
		try {
			logger.info("Creating default objects");

			// Create work order
			logger.info("Creating a work order");
			String workOrderResult = maximoApi.retrieve(new WorkOrder(),
					"addid=1&internalvalues=1&action=system:new&addschema=1");
			newWorkOrder = new Gson().fromJson(workOrderResult, WorkOrder.class);
			newWorkOrder.setDescription("WO for mobile automation test");
			newWorkOrder.setSiteId(SetupData.SITEID);
			newWorkOrder.setGLAccount(SetupData.GLDEBITACCT);
			assertEquals(maximoApi.create(newWorkOrder), SetupData.CreatedSuccess);
			woNum = newWorkOrder.getWoNum();
			logger.info("Work Order: " + woNum);

			// Change WO status to Approved
			logger.info("Changing work order status to APPR");
			assertEquals(maximoApi.changeStatus(newWorkOrder, WoStatus.APPR.toString()), SetupData.NoContentSuccess);

			// Assign the labor
			assertEquals(maximoApi.addAssignmentLabor(newWorkOrder, labor), SetupData.NoContentSuccess);
			logger.info("Assignment added");

			apiCodeSuccess = true;
		} catch (AssertionError e) {
			logger.info(" SkipException apiCodeSuccess = " + apiCodeSuccess);
			apiCodeSuccess = false;
			throw new Exception("Test Setup API Failed,Stopping execution.");
		}
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
	/**
	 * Get labor Id by labor Code via database
	 * 
	 * @param laborCode
	 * @return
	 */
	protected void premiumPayAndPPCRAFTRATE() {
		int num = AbstractAutomationFramework.randomInt();
		PremiumPayCode= "OT"+ num;
		PremiumPayId= AbstractAutomationFramework.randomIntRange(999, 9999);
		PpCraftRateId= AbstractAutomationFramework.randomIntRange(999, 9999);
		PremiumPayDescription = "Test premium pay hrs "+ num;
		
		String sqlString = "INSERT INTO MAXIMO.PREMIUMPAY(ORGID, DESCRIPTION, DEFAULTRATETYPE, DEFAULTRATE, GLOBALPPCODE, PREMIUMPAYID, PREMIUMPAYCODE, LANGCODE, HASLD, ROWSTAMP)	VALUES('"+ SetupData.ORGID +"', '"+PremiumPayDescription+"', '"+PremiumRateType+"', '"+PremiumPayRate+"', 1, '"+PremiumPayId+"', '"+PremiumPayCode+"', 'EN', 0, '')";
		logger.info(sqlString);
		jdbcConnection.executeUpdateSQL(sqlString);
		String sqlString2 = "INSERT INTO MAXIMO.PPCRAFTRATE(CRAFT, ORGID, RATETYPE, RATE, INHERIT, PPCRAFTRATEID, PREMIUMPAYCODE, ROWSTAMP) VALUES('"+SetupData.CRAFT+"', '"+ SetupData.ORGID +"', '"+PremiumRateType+"', '"+PremiumPayRate+"', 1, '"+PpCraftRateId+"', '"+PremiumPayCode+"', '')";
		logger.info(sqlString2);
		jdbcConnection.executeUpdateSQL(sqlString2);
		
	};
}
