package com.ibm.maximo.technician.testcases;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.google.gson.Gson;
import com.ibm.maximo.automation.mobile.AbstractAutomationFramework;
import com.ibm.maximo.automation.mobile.FrameworkFactory;
import com.ibm.maximo.automation.mobile.api.MaximoApi;
import com.ibm.maximo.automation.mobile.api.json.WorkOrder;
import com.ibm.maximo.technician.framework.TechnicianTest;
import com.ibm.maximo.technician.setupdata.SetupData;
import com.ibm.maximo.technician.setupdata.SetupData.WoStatus;
import com.ibm.maximo.technician.setupdata.SetupData.WorkType;
import com.ibm.maximo.technician.page.MySchedulePage;
import com.ibm.maximo.technician.page.ReportWorkPage;
import com.ibm.maximo.technician.page.WorkOrderDetailsPage;
import com.ibm.maximo.components.LabelComponent;

/*
 * MAXMOA-5173:[MobileAutomation] Technician (RBA) - Edit Labor Hours if not approved
 * Scenario 35 : testcases/WorkorderDetailsPage/TC_WODetailsAssetLocationMeters.md
 * */

public class EditLaborOptionsOutsideSettingsUncheckedFromOrganization extends TechnicianTest {

	private final Logger logger = LoggerFactory
			.getLogger(EditLaborOptionsOutsideSettingsUncheckedFromOrganization.class);
	private AbstractAutomationFramework af;
	private TestSuite testSuite;
	private MaximoApi maximoApi;
	private String woNum, timezone, labor, contractId="4999", vendor, masServer;
	private WorkOrder newWorkOrder;
	private String laborHeaderText = "Labor";
	private String laborName, craft, craftDescription, skill;
	private String chevronId="bam2r_items_datalistWrapper";
	private boolean apiCodeSuccess = false;
	private static final String WO_DESCRIPTION = "WO Desc for automation testing";


	@BeforeClass(alwaysRun = true)
	@Parameters({ "configPath" })
	public void setup(String configPath) throws Exception {
		logger.info("********************EditLaborOptionsOutsideSettingsUncheckedFromOrganization*********************************");
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
			skill = SetupData.SKILL;
			vendor = SetupData.VENDOR;
			craft = SetupData.CRAFT;
			laborName = SetupData.LABORNAME;
			laborContractVendorRelationShips();
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

	@Test(groups = { "desktop",
			"mobile" }, description = "Verify when timer is off, labor is internal labor (vendor and contract is not null) then organization settings (Labor options) = Automatically Approve Outside Labor Transactions is unchecked, then edit icon should be present", timeOut = 900000)
	public void verifyOutsideLaborUncheckedFromOrgSettings() throws Exception {

		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage woDetailsPage = new WorkOrderDetailsPage(af);
		ReportWorkPage reportWork = new ReportWorkPage(af);

        // Search the WO
		assertTrue(assignedWorkPage.search(woNum));
        // Click on selected workorder to open Work Order Details Page
		assignedWorkPage.openWorkOrderDetails();

        // Start the work order
		woDetailsPage.clickStartWorkTimerButtonIcon();
        // Stop the timer
		woDetailsPage.clickStopWorkTimerButtonIcon();
        // click on send labor button
		woDetailsPage.clickSendLaborButton();
        // verify the edit icon on the labor is present
		assertTrue(reportWork.isEditLaborButtonPresent(), "Labor edit icon should display");
        // Click chevron to expand labor record
		reportWork.laborChevron(chevronId);
        // verify vendor and craft
		assertEquals(contractId, af.instantiateComponent(LabelComponent.class, "bd_jx[0]_fieldValue0").getValue());
		assertEquals(vendor, af.instantiateComponent(LabelComponent.class, "e3gmj[0]_fieldValue0").getValue());
		assertEquals(skill, af.instantiateComponent(LabelComponent.class, "qez74[0]_label").getValue());

        //go to wo details page
		reportWork.clickBackChevron();
        //go to the list page
		woDetailsPage.clickBackChevron();

	}
	
	// Generated by WCA for GP
	/**
	 * Create default objects
	 * 
	 * @throws Exception
	 */
	protected void createDefaultObjects() throws Exception {
		try {
			logger.info("Creating default objects");

			// Create a workorder
			logger.info("Creating a work order");
			String workOrderResult = maximoApi.retrieve(new WorkOrder(),
					"addid=1&internalvalues=1&action=system:new&addschema=1");
			newWorkOrder = new Gson().fromJson(workOrderResult, WorkOrder.class);
			newWorkOrder.setDescription(WO_DESCRIPTION);
			newWorkOrder.setWorkType(WorkType.PM.toString());
			newWorkOrder.setGLAccount(SetupData.GLDEBITACCT);
			maximoApi.create(newWorkOrder);
			woNum = newWorkOrder.getWoNum();
			logger.info("Work Order: {}" + woNum);

			// Change WO status to Approved
			logger.info("Changing work order status to APPR");
			maximoApi.changeStatus(newWorkOrder, WoStatus.APPR.toString());

			// Assignment with labor wilson
			maximoApi.addAssignmentLabor(newWorkOrder, labor);
			logger.info("Assignment added");

			apiCodeSuccess = true;
		} catch (AssertionError e) {
			logger.info(" SkipException apiCodeSuccess = " + apiCodeSuccess);
			apiCodeSuccess = false;
			throw new Exception("Test Setup API Failed,Stopping execution.");
		}
	}
	
	public void laborContractVendorRelationShips() {
		logger.info("creating craft and vendor");
		String insertQuery = "INSERT INTO MAXIMO.CONTRACT (CONTRACTNUM, DESCRIPTION, MASTERNUM, VENDORREFNUM, CONTRACTTYPE, REVISIONNUM, PURCHASEAGENT, STATUS, STATUSDATE, STARTDATE, ENDDATE, RENEWALDATE, EXTENDABLE, AUTOEXTENDPERIOD, CONDFOREXT, CUSTTERMALLOWED, CUSTNOTIFYPERIOD, VENDTERMALLOWED, VENDNOTIFYPERIOD, VENDOR, CONTACT, FREIGHTTERMS, PAYMENTTERMS, SHIPVIA, CUSTOMERNUM, FOB, TOTALCOST, CHANGEBY, CHANGEDATE, HISTORYFLAG, CURRENCYCODE, EXCHANGERATE, EXCHANGERATE2, EXCHANGEDATE, BUYAHEAD, INCLUSIVE1, INCLUSIVE2, INCLUSIVE3, INCLUSIVE4, INCLUSIVE5, EXTERNALREFID, OWNERSYSID, SENDERSYSID, ORGID, TOTALBASECOST, POREQUIRED, PAYMENTSCHED, HASINSURANCE, INSURANCEEXPDATE, CONTRACTID, REVCOMMENTS, LANGCODE, MASTERREVNUM, PROCESSCLAIM, INSPECTIONREQUIRED, HASLD, ROWSTAMP)"
				+ " VALUES('"+contractId+"', 'TEST', NULL, NULL, 'LABOR', 0, NULL, 'APPR', TIMESTAMP '2025-02-01 04:27:55.000000', TIMESTAMP '2025-02-01 00:00:00.000000', NULL, NULL, 0, NULL, NULL, 1, NULL, 1, NULL, '"+vendor+"', 'GERRY DENNING', NULL, '2/10 NET 30', 'UPS-GR', '6461066', 'MORLEY, CO', 0, '"+ labor +"', TIMESTAMP '2025-02-01 04:28:43.000000', 0, '"+ SetupData.CURRENCYCODE +"', NULL, NULL, NULL, 0, 1, 0, 0, 0, 0, NULL, NULL, NULL, '"+ SetupData.ORGID +"', 0, 0, 0, 0, NULL, 248, NULL, 'EN', NULL, 0, 0, 0, '1552175')";
		jdbcConnection.executeUpdateSQL(insertQuery);

		String insertQuery1 = "INSERT INTO MAXIMO.CRAFTRATE (CRAFT, SKILLLEVEL, ORGID, CONTRACTNUM, STANDARDRATE, CRAFTRATEID, REVISIONNUM, VENDOR, ROWSTAMP) VALUES('"+craft+"', '"+skill+"', '"+ SetupData.ORGID +"', '"+contractId+"', NULL, 306, 0, '"+vendor+"', '1552218')";
		jdbcConnection.executeUpdateSQL(insertQuery1);

		String insertQuery2 = "INSERT INTO MAXIMO.CONTRACTSTATUS (CHANGEBY, CHANGEDATE, MEMO, ORGID, STATUS, CONTRACTNUM, CONTRACTSTATUSID, REVISIONNUM, ROWSTAMP) VALUES('"+labor+"', TIMESTAMP '2025-01-11 10:30:40.000000', NULL, '"+SetupData.ORGID+"', 'APPR', '"+contractId+"', 181, 0, '1666974')";
		jdbcConnection.executeUpdateSQL(insertQuery2);

		String insertQuery3 = "INSERT INTO MAXIMO.LABORCRAFTRATE (CRAFT, SKILLLEVEL, LABORCODE, ORGID, CONTRACTNUM, INHERIT, DEFAULTCRAFT, RATE, GLACCOUNT, CONTROLACCOUNT, LABORCRAFTRATEID, DEFAULTTICKETGLACC, VENDOR, ROWSTAMP) VALUES('"+craft+"', '"+skill+"', '"+labor+"', '"+SetupData.ORGID+"', '"+contractId+"', 0, 1, 10, '"+SetupData.GLDEBITACCT+"', NULL, 1488, '6820-400-000', '"+vendor+"', '1697096')";
		jdbcConnection.executeUpdateSQL(insertQuery3);
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
