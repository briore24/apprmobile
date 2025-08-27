package com.ibm.maximo.technician.testcases;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import com.ibm.maximo.automation.mobile.page.login.LoginPage;
import com.ibm.maximo.technician.framework.TechnicianTest;
import com.ibm.maximo.technician.page.MySchedulePage;
import com.ibm.maximo.technician.page.WorkOrderDetailsPage;
import com.ibm.maximo.technician.setupdata.SetupData;
import com.ibm.maximo.technician.setupdata.SetupData.WoStatus;
import com.ibm.maximo.technician.setupdata.SetupData.WorkType;


/*GRAPHITE-55300:[TECHMOBILE] Specification
 *MASR-1035: Technician (RBA) - Specification should be updateable 
 */
public class ViewAndEditSpecificationsTest extends TechnicianTest {
	private final Logger logger = LoggerFactory.getLogger(ViewAndEditSpecificationsTest.class);
	private AbstractAutomationFramework af;
	private TestSuite testSuite;
	private MaximoApi maximoApi;
	private WorkOrder newWorkOrder;
	private String woNum;
	private String labor;
	public static LoginPage lp;
	private String specificationTitle = "Specifications";
	private int classStructureid, classStructureid2, classStructureuid, classStructureuid2, classSpecId, classUseWithId, assetAttributeId;
	private String assetAttrId="SIZET";
	private String workOrderId;
	private String numValue = "";
	private int parent;
	private String var1="PUMPT",var2="CNTRFGLT";
	
	// private static final String ASSET_DESCRIPTION = "Asset for mobile automation test";
	private static final String WO_DESCRIPTION = "WorkeOrder for mobile automation test";

	@BeforeClass(alwaysRun = true)
	@Parameters({ "configPath" })
	public void setup(String configPath) throws Exception {
		logger.info("********************ViewAndEditSpecificationsTest*********************************");
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
		// complete WO after testcase executed
		maximoApi.changeStatus(newWorkOrder, WoStatus.COMP.toString());
		if (testSuite != null) {
			testSuite.teardown();
		}
	}

	@Test(groups = { "mobile", "desktop" }, description = "Verify Specifications drawer open on classfied work order", timeOut = 900000)
	public void ViewAndEditSpecifications() throws Exception {
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage woDetails = new WorkOrderDetailsPage(af);
		assertTrue(assignedWorkPage.search(woNum), "Fail : Task Work Order is not displayed");
		// Navigate to work order details page
		assignedWorkPage.openCardDetails();
		// Wait for start work button enabled
		woDetails.startWorkButtonEnabled();
		Thread.sleep(5000);
		// Click on Specification
		woDetails.clickSpecificationTouchpoint();
		// Verify Specification Title
		assertEquals(specificationTitle, woDetails.specificationTitle());
		// edit specifications
		woDetails.editSpecifications();
		woDetails.enterValueForSpecificationsField("2");
		woDetails.clickOnSaveIconOfEditSpecifications();
		//af.waitForElementToNotBePresent(By.id("cds--loading__stroke"), 5000);
		Thread.sleep(5000);
		assertEquals("2.00", woDetails.getCheckValueForSpecifications());
		// Click on Close Specification Drawer
		woDetails.closeSpecificationTouchpoint();
		// Verify Classification Id Text
		logger.info("var1>>>>" + var1 + "          var2>>>>"+ var2);
		assertTrue( woDetails.classificationIdText().contains(var1));
		assertTrue( woDetails.classificationIdText().contains(var2));
	}
	
	protected void createDefaultObjects() throws Exception {
		logger.info("Creating default objects");
		// Update System Properties
		this.updateSystemProperties();
		
		// Create a workorder
		logger.info("Creating a work order");
		String workOrderResult = maximoApi.retrieve(new WorkOrder(),
				"addid=1&internalvalues=1&action=system:new&addschema=1");
		newWorkOrder = new Gson().fromJson(workOrderResult, WorkOrder.class);
		newWorkOrder.setDescription(WO_DESCRIPTION);
		// newWorkOrder.setAssetNum(assetNum);
		newWorkOrder.setWorkType(WorkType.PM.toString());
		maximoApi.create(newWorkOrder);
		woNum = newWorkOrder.getWoNum();
		logger.info("Work Order: {}" + woNum);
		
		// Set Specification
		this.setSpecification();
		logger.info("Specification added in Work Order");
		
		// Change WO status to Approved
		logger.info("Changing work order status to APPR");
		maximoApi.changeStatus(newWorkOrder, WoStatus.APPR.toString());

		// Assignment with labor wilson
		maximoApi.addAssignmentLabor(newWorkOrder, labor);
		logger.info("Assignment added");

	}
	
	protected void updateSystemProperties() throws Exception {
		// Set System Properties
		maximoApi.setProperty("maximo.mobile.usetimer","COMMON",null,"1");
		logger.info("System Properties Set Successfully");
	}
	
	protected void setSpecification() throws Exception {
		try {	
			Random random = new Random();
			classStructureid = random.nextInt(1000000);classStructureuid = random.nextInt(2000);
			String query1 = "INSERT INTO MAXIMO.CLASSSTRUCTURE (CLASSSTRUCTUREID, DESCRIPTION, GENASSETDESC, ORGID, PARENT, CLASSIFICATIONID, USECLASSINDESC, "+"TYPE"+", SITEID, CLASSSTRUCTUREUID, HASCHILDREN, LANGCODE, HASLD, SHOWINASSETTOPO, SHOW, SORTORDER, ROWSTAMP) VALUES('"+classStructureid+"','PUMP', 0, '"+SetupData.ORGID+"', NULL, '"+var1+"', 1, NULL, '"+SetupData.SITEID +"', '"+classStructureuid+"', 1, 'EN', 0, 1, 1, NULL, '')";
			jdbcConnection.executeUpdateSQL(query1);
			parent = classStructureid;
			classStructureid2 = random.nextInt(100000);classStructureuid2 = random.nextInt(2000);
			String query2 = "INSERT INTO MAXIMO.CLASSSTRUCTURE (CLASSSTRUCTUREID, DESCRIPTION, GENASSETDESC, ORGID, PARENT, CLASSIFICATIONID, USECLASSINDESC, "+"TYPE"+", SITEID, CLASSSTRUCTUREUID, HASCHILDREN, LANGCODE, HASLD, SHOWINASSETTOPO, SHOW, SORTORDER, ROWSTAMP) VALUES('"+classStructureid2+"','Centrifugal Pump', 0, '"+SetupData.ORGID+"', '"+parent+"', '"+var2+"', 1, NULL, '"+SetupData.SITEID +"', '"+classStructureuid2+"', 0, 'EN', 0, 1, 1, NULL, '')";
			jdbcConnection.executeUpdateSQL(query2);
			assetAttributeId = random.nextInt(2000);
			String queryA = "INSERT INTO MAXIMO.ASSETATTRIBUTE (ASSETATTRID, DESCRIPTION, DATATYPE, MEASUREUNITID, DOMAINID, ATTRDESCPREFIX, ORGID, SITEID, ASSETATTRIBUTEID, ROWSTAMP) VALUES('"+assetAttrId+"', 'SizeT', 'NUMERIC', NULL, NULL, NULL, '" + SetupData.ORGID + "', NULL, '"+assetAttributeId+"', '')";
			jdbcConnection.executeUpdateSQL(queryA);
			classSpecId = classStructureuid = random.nextInt(100000);
			String query3 = "INSERT INTO MAXIMO.CLASSSPEC (CLASSSTRUCTUREID, ASSETATTRID, MEASUREUNITID, DOMAINID, ATTRDESCPREFIX, CS01, CS02, CS03, CS04, CS05, ORGID, SECTION, SITEID, CLASSSPECID, APPLYDOWNHIER, INHERITEDFROM, INHERITEDFROMID, TABLEATTRIBUTE, LOOKUPNAME, LINKEDTOATTRIBUTE, LINKEDTOSECTION, ASSETATTRIBUTEID, CONTINUOUS, LINEARTYPE, ROWSTAMP) VALUES('"+classStructureid2+"', '" + assetAttrId + "', 'IN', NULL, NULL, NULL, NULL, NULL, NULL, NULL, '" + SetupData.ORGID + "', NULL, '" + SetupData.SITEID + "', '"+classSpecId+"', 0, NULL, NULL, NULL, NULL, NULL, NULL, 1, 0, NULL, '')";
			jdbcConnection.executeUpdateSQL(query3);
			classUseWithId = random.nextInt(5000);
			String query4 = "INSERT INTO MAXIMO.CLASSUSEWITH (CLASSUSEWITHID, CLASSSTRUCTUREID, OBJECTNAME, DESCRIPTION, OBJECTVALUE, TOPLEVEL, ROWSTAMP) VALUES('"+classUseWithId+"', '"+classStructureid2+"', 'WORKORDER', 'Use with Work Orders', 'WORKORDER', 0, '')";
			jdbcConnection.executeUpdateSQL(query4);		
			// assign above class structure id to created asset
			String updateQuery = "update MAXIMO.WORKORDER M set M.CLASSSTRUCTUREID='" + classStructureid2 + "' where M.WONUM = '" + woNum + "'";
			int updateResult = jdbcConnection.executeUpdateSQL(updateQuery);
			logger.info("=== update work order classstructureid status === ", updateResult);
			int workOrderSpecId = random.nextInt(10000);
			int rowStamp = random.nextInt(100000);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String currentDate = sdf.format(new Date());
			
			// Get Work Order Id
			String workOrderQuery = "SELECT M.WORKORDERID FROM MAXIMO.WORKORDER M WHERE M.WONUM = '" + woNum + "'";
			Object[] woResult = jdbcConnection.executeSQL(workOrderQuery);
			Object[] woResultArray = (Object[]) woResult[0];
			workOrderId = woResultArray[0].toString();

			String insertQuery = "INSERT INTO MAXIMO.WORKORDERSPEC (WONUM,WORKORDERSPECID,ASSETATTRID,CLASSSTRUCTUREID,NUMVALUE,DISPLAYSEQUENCE,CHANGEDATE,CHANGEBY,SITEID,ORGID,ROWSTAMP,REFOBJECTID,REFOBJECTNAME,MANDATORY,CLASSSPECID) VALUES ('"
					+ woNum + "','" + workOrderSpecId + "','" + assetAttrId + "','" + classStructureid2 + "','" + numValue + "','1',TO_DATE('" + currentDate + "','YYYY-MM-DD'),'" + 
					labor.toUpperCase() + "','" + SetupData.SITEID + "','" + SetupData.ORGID + "','" + rowStamp + "','" + workOrderId + "','WORKORDER','0','"+classSpecId+"')";
					
			int insertResult = jdbcConnection.executeUpdateSQL(insertQuery);
			logger.info("=== insert workorderspec status === ", insertResult);
		} catch (Exception e) {
			e.printStackTrace();
		}
	} 
}