package com.ibm.maximo.technician.testcases;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;

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
import com.ibm.maximo.automation.mobile.api.json.Item;
import com.ibm.maximo.automation.mobile.api.json.ItemChangeStatus;

import com.ibm.maximo.automation.mobile.api.json.StoreRoom;
import com.ibm.maximo.automation.mobile.api.json.ToolItem;
import com.ibm.maximo.automation.mobile.api.json.WorkOrder;
import com.ibm.maximo.automation.mobile.api.json.WpMaterial;
import com.ibm.maximo.automation.mobile.api.json.WpTool;
import com.ibm.maximo.technician.framework.TechnicianTest;
import com.ibm.maximo.technician.page.MySchedulePage;
import com.ibm.maximo.technician.page.WorkOrderDetailsPage;
import com.ibm.maximo.technician.setupdata.SetupData;
import com.ibm.maximo.technician.setupdata.SetupData.WoStatus;
import com.ibm.maximo.technician.setupdata.SetupData.WorkType;
import com.ibm.maximo.technician.setupdata.SetupData.ItemStatus;
import com.ibm.maximo.components.LabelComponent;

/**
 * Scenario 24 - Verify user can add multiple material request from the material request page by clicking on "+" button in material section
 * https://jsw.ibm.com/browse/GRAPHITE-51071 - [TECHMOBILE] Planned Material and Tools touch point and Material request :44M,6TA,0A
 * 
 * Pre-condition:
 * - Login to maximo/manage application as admin.
 * - Create a work order.
 * - Add planned items/materials and/or tools to work order.
 * - Assign labor/technician to work order in Assignments tab.
 * - Approve the work order.
 * - Create multiple items in item master, activate and add to a storeroom.
 * Steps:
 * 1. Login to technician app with the technician assigned to work order.
 * 2. Click on "My Schedule" tile.
 * 3. Find the work order created in pre-condition steps in "Assigned Work" filter and click on it to open WO details page.
 * 4. Click on planned materials and tools touchpoint to open "Materials and tools" sliding drawer.
 * 5. Click on shopping cart button to open "Material request" page.
 * 6. Click on the "+" button in Materials list section to open "Add item" sliding drawer.
 * 7. Click on '>' chevron in Item field to open item lookup.
 * 8. Search first item which was created in pre-condition steps, in item lookup and select it.
 * 9. Click on save/checkmark button on "Add item" sliding drawer.
 * 10. Click on the "+" button in Materials list section again to select another item and save it.
 * 11. Again click on "+" button in material list section, select another item and save it.
 * 12. Fill all the mandatory details in Request details section and click on send button.
 * 
 * Result:
 * - Three MR requests should be displayed in Material list section.
 * - For each material request in Material list section, Material name and description along with storeroom description is displayed. Also, value of quantity is displayed.
 * - Verify user should be able to successfully save the request i.e. for each MR record an associated MRLINE record is created in database after user has clicked the send button.
 * 
 * Scenario 28 - Verify each "Material request" will be an individual MR with a single MRLINE record and when technician requests a second item, a new MR with associated MRLINE is created
 * 
 * Steps:
 * 10. Provide values for Priority, "Required date" and "Drop to" fields.
 * 11. Click on Send button to save the Material request record.
 * 12. Repeat steps 6 to 11 in order to create new MR request for some other item.
 * 
 * Result:
 * - Each "Material request" should be an individual MR with a single MRLINE record and when technician requests a second item, a new MR with associated MRLINE should be created.
 * - All fields for each MR record and associated MR line record should be saved correctly. Refer previous scenario for fields details.
 * 
 * Scenario 31 - Verify material requests for the work order is displayed in the planned materials and tools sliding drawer
 * with following MR details: MRNUM, STATUS and REQUESTEDDATE, when technician has successfully submitted the MR
 * 
 * Technician is navigated to materials and tools sliding drawer.
 * Verify material request created in previous steps is displayed with following fields: MRNUM, STATUS, REQUESTEDDATE with respective values.
 *
 * Result:
 *
 * After clicking on 'send' button, technician should be navigated to the materials and tools sliding drawer.
 * Material request created should be displayed with following fields: MRNUM, STATUS, REQUESTEDDATE with respective values.
 * MRNUM will be empty until material request is synced with the server.
 * 
 * Scenario 33 - Verify that the item sliding drawer is displayed with MR Line data in read only mode when technician clicks on chevron on each MR Line in material list section
 * 
 * Steps: 
 * 8. Material request created in previous steps is displayed with following fields: MRNUM, STATUS, REQUESTEDDATE with respective values.
 * 9. Click on the chevron next to the MR record on the materials and tools sliding drawer to open MR page.
 * 10. Click on chevron on each MR Line in material list section.
 * 11. Verify item sliding drawer is displayed with MR Line data in read only mode.
 * 
 * Result:
 * - The item sliding drawer should be displayed with MR Line data in read only mode.
 * - Item sliding drawer should have: Type, item number, description, storeroom, quantity, order unit, manufacturer and vendor fields with respective values.
 * 
 * @author evelynmurasaki @gabriel.bonamico
 *
 *  Scenario 16 - Verify storeroom is auto-populated when there is only one storeroom associated with selected item
 * Pre-condition:
 * 1. Login to maximo/manage application as admin.
 * 2. Create a work order.
 * 3. Assign labor/technician to work order in Assignments tab.
 * 4. Approve the work order.
 * 5. Create an item in item master, activate and add it to a storeroom.
 * 
 * Steps:
 * 1. Login to technician app with the technician assigned to work order.
 * 2. Click on "My Schedule" tile.
 * 3. Find the work order created in pre-condition steps in "Assigned Work" filter and click on it to open WO details page.
 * 4. Click on planned materials and tools touch-point to open "Materials and tools" sliding drawer.
 * 5. Click on shopping cart button to open "Material request" page.
 * 6. Click on the "+" button in Materials list section to open "Add item" sliding drawer.
 * 7. Click on '>' chevron in Item field to open item lookup.
 * 8. Search the item which was created in pre-condition steps, in item lookup and select it.
 * 9. Verify the fields displayed in "Add item" sliding drawer.
 * 
 * Result:
 * Storeroom should be auto-populated when there is only one storeroom associated with selected item.
 * "Add item" sliding drawer should have following contents:
 * 
 * 1. "Type" field with value Item.
 * 2. "Item" field with Item Id and Item Description for the selected item and chevron to open Item lookup. It is mandatory field.
 * 3. "Storeroom" field with Storeroom description of the storeroom associated with selected item and available quantity.
 * 4. "Quantity" numeric spinner with default value as "1.00". It is mandatory field.
 * 5. "Order unit" of the selected item.
 * 6. "Manufacturer" of the selected item. If manufacturer is not provided then placeholder is displayed.
 * 7. "Vendor" of the selected item. If vendor is not provided then placeholder is displayed.
 * 
 * Scenario 21 - Verify the contents of "Material request" page after technician adds a new item to Materials List section
 * Pre-condition:
 * 
 * Login to maximo/manage application as admin.
 * Create a work order.
 * Add planned items/materials and/or tools to work order.
 * Assign labor/technician to work order in Assignments tab.
 * Approve the work order.
 * Create multiple items in item master, activate and add to a storeroom.
 * Steps:
 * 
 * Login to technician app with the technician assigned to work order.
 * Click on "My Schedule" tile.
 * Find the work order created in pre-condition steps in "Assigned Work" filter and click on it to open WO details page.
 * Click on planned materials and tools touch-point to open "Materials and tools" sliding drawer.
 * Click on shopping cart button to open "Material request" page.
 * Click on the "+" button in Materials list section to open "Add item" sliding drawer.
 * Click on '>' chevron in Item field to open item lookup.
 * Search first item which was created in pre-condition steps, in item lookup and select it.
 * Click on save/checkmark button on "Add item" sliding drawer.
 * Click on the "+" button in Materials list section again to select another item and save it.
 * Result:
 * 
 * The existing item record in Materials list section should be replaced with the newly selected item record.
 * 
 * The "Material request" page should have following contents:
 * 
 * "Material request" header title with back and disabled Send button.
 * "Materials list" section with '+' button and newly saved/added item with item ID, item description, storeroom description, quantity information.
 * "Required date", Priority and "Drop to" fields.
 * "Required date" and Priority are mandatory fields.
 * 
 * 
 * Scenario 24 - Verify Send button is enabled only when all the mandatory fields are provided on Material Request page
 * Pre-condition:
 * 
 * Login to maximo/manage application as admin.
 * Create a work order.
 * Add planned items/materials and/or tools to work order.
 * Assign labor/technician to work order in Assignments tab.
 * Approve the work order.
 * Create an item in item master, activate and add it to a storeroom.
 * 
 * Steps:
 * Login to technician app with the technician assigned to work order.
 * Click on "My Schedule" tile.
 * Find the work order created in pre-condition steps in "Assigned Work" filter and click on it to open WO details page.
 * Click on planned materials and tools touch-point to open "Materials and tools" sliding drawer.
 * Click on shopping cart button to open "Material request" page.
 * Click on the "+" button in Materials list section to open "Add item" sliding drawer.
 * Click on '>' chevron in Item field to open item lookup.
 * Search the item which was created in pre-condition steps, in item lookup and select it.
 * Click on save/checkmark button on "Add item" sliding drawer to add item record in "Materials list" section.
 * Provide valid values of Priority and "Required date" fields.
 * Result:
 * 
 * Send button should be enabled with blue color when all the mandatory fields are provided on "Material request" page.
 * Clearing out any mandatory field or providing invalid values in Priority and "Required date" fields should disable the send button.
 * 
 * 
 * @author nirmalbhagwani
 */

public class PlannedMaterialsToolsRequestMaterialsforItemTypesTwo extends TechnicianTest {
	private final Logger logger = LoggerFactory.getLogger(PlannedMaterialsToolsRequestMaterialsforItemTypesTwo.class);
	private AbstractAutomationFramework af;
	private TestSuite testSuite;
	private MaximoApi maximoApi;
	private WorkOrder newWorkOrder;
	private String woNum, labor, location, locationDescription,timezone;
	private String itemNum, itemDescription, itemId, tool0, toolDescription0, tool1, toolDescription1, tool2;
	private String itemNum1, itemDescription1, itemNum2, location2, itemNum3, itemDescription3;
	private String materialDescription = AbstractAutomationFramework.randomString(10).toUpperCase();
	private static String storeroomLabel1 = "v_a8x_fieldValue0";
	private static final String ASSET_DESCRIPTION = "Asset Description ";
	private static final String WORKORDER_DESCRIPTION = "Work Order Description ";

	@BeforeClass(alwaysRun = true)
	@Parameters({ "configPath" })
	public void setup(String configPath) throws Exception {
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
			defaultInformationOfUserTimeZone(timezone, labor);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		logger.info(" * 1. Login to technician app with the technician assigned to work order.\n"
				+   " * 2. Click on \"My Schedule\" tile.");
		login(af);
		  if (masServer.equals("true")) {
			  // Data will update for IVT
			  updateData(); 
			  }
	}

	@AfterClass(alwaysRun = true)
	public void teardown() throws Exception {
		logOut(af);
//		 Change WO status to Completed
		logger.info("Changing work order status to COMP");
		maximoApi.changeStatus(newWorkOrder, WoStatus.COMP.toString());
		if (testSuite != null) {
			testSuite.teardown();
		}
	}

	@Test(groups = { "priority2" }, description = "Scenarios 16, 21, 24", timeOut = 1000000)

	public void PlannedMaterialsToolsRequestMaterialsforItemTypesScenario162124() throws Exception {

		// Open work order details
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage workOrderDetailsPage = new WorkOrderDetailsPage(af);
		
		logger.info("3. Find the work order created in pre-condition steps in \"Assigned Work\" filter.");
		assertTrue(assignedWorkPage.search(woNum), "Fail : Search Work Order is not displayed");
		// Navigate to work order details page
		assignedWorkPage.openCardDetails();
		
		logger.info("4. Click on planned materials and tools touchpoint to open \"Materials and tools\" sliding drawer.");
		workOrderDetailsPage.clickMaterialAndToolTouchpoint();
		
		logger.info("5. Click on shopping cart button to open \"Material request\" page.");
		workOrderDetailsPage.clickMaterialAndToolMenu();
		workOrderDetailsPage.clickShoppingCart();

		logger.info("6. Click on the \"+\" button in Materials list section to open \"Add item\" sliding drawer.");
		workOrderDetailsPage.clickAddMaterialIcon();
		
		logger.info("7. Click on '>' chevron in Item field to open item lookup.");
		workOrderDetailsPage.clickItemChevron();

		logger.info("8. Search first item which was created in pre-condition steps, in item lookup and select it.");
		workOrderDetailsPage.searchForItemAndSelectItem(itemNum1, itemIdQueryFromDB(itemNum1));
		
		String selectedStore = af.instantiateComponent(LabelComponent.class, storeroomLabel1).getLabel();
		assertEquals(selectedStore, location);

		logger.info("9. Click on save/checkmark button on \"Add item\" sliding drawer.");
		workOrderDetailsPage.clickSaveAddItem();
		
		workOrderDetailsPage.requiredDateUnderRequiredDetailsOfMaterialRequest(2022, 10, 20);
		String priority1 = String.valueOf(AbstractAutomationFramework.randomInt());
		if (priority1.equals("0")) {
			priority1 = String.valueOf(AbstractAutomationFramework.randomInt());
		}
		workOrderDetailsPage.setPriorityOnMaterialRequest(String.valueOf(priority1));
		String droppoint1 = AbstractAutomationFramework.randomString(12).toUpperCase();
		workOrderDetailsPage.dropToMaterialRequest(droppoint1);
		workOrderDetailsPage.sendMaterialRequest();

		logger.info("Three MR requests should be displayed in Material list section.");
		workOrderDetailsPage.getMaterialsToolsSlidingDrawerHeaderLabel();
		assertTrue(workOrderDetailsPage.verifyFirstRequestedMaterialStatus());
		assertTrue(workOrderDetailsPage.isMaterialExist(woNum, itemNum, itemDescription, locationDescription, "1234567.45"));
	}

	@Test(groups = { "mobile" }, description = "Scenarios 28, 31 and 33", timeOut = 1000000)

	public void PlannedMaterialsToolsRequestMaterialsforItemTypesScenario24() throws Exception {

		// Open work order details
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage workOrderDetailsPage = new WorkOrderDetailsPage(af);

		logger.info("3. Find the work order created in pre-condition steps in \"Assigned Work\" filter.");
		assertTrue(assignedWorkPage.search(woNum), "Fail : Search Work Order is not displayed");
		// Navigate to work order details page
		assignedWorkPage.openCardDetails();
		
		logger.info("4. Click on planned materials and tools touchpoint to open \"Materials and tools\" sliding drawer.");
		workOrderDetailsPage.clickMaterialAndToolTouchpoint();
		
		logger.info("5. Click on shopping cart button to open \"Material request\" page.");
		workOrderDetailsPage.clickMaterialAndToolMenu();
		workOrderDetailsPage.clickShoppingCart();

		logger.info("6. Click on the \"+\" button in Materials list section to open \"Add item\" sliding drawer.");
		workOrderDetailsPage.clickAddMaterialIcon();
		
		logger.info("7. Click on '>' chevron in Item field to open item lookup.");
		workOrderDetailsPage.clickItemChevron();

		logger.info("8. Search first item which was created in pre-condition steps, in item lookup and select it.");
		workOrderDetailsPage.searchForItemAndSelectItem(itemNum1, itemIdQueryFromDB(itemNum1));
		workOrderDetailsPage.typeQuantityMaterialRequestAddItemSlidingDrawer("1.1");
		
		logger.info("9. Click on save/checkmark button on \"Add item\" sliding drawer.");
		workOrderDetailsPage.clickSaveAddItem();
		workOrderDetailsPage.requiredDateUnderRequiredDetailsOfMaterialRequest(2022, 10, 20);
		String priority1 = String.valueOf(AbstractAutomationFramework.randomInt());
		if (priority1.equals("0")) {
			priority1 = String.valueOf(AbstractAutomationFramework.randomInt());
		}
		workOrderDetailsPage.setPriorityOnMaterialRequest(String.valueOf(priority1));
		String droppoint1 = AbstractAutomationFramework.randomString(12).toUpperCase();
		workOrderDetailsPage.dropToMaterialRequest(droppoint1);
		workOrderDetailsPage.sendMaterialRequest();
		
		logger.info("10. Click on the \"+\" button in Materials list section again to select another item and save it.");
		//TODO remove duplicated clickMaterialAndToolMenu when defect is resolved: GRAPHITE-54876 - [Technician] - Mobile - Materials and Tools sliding drawer refreshes twice when you click on something
		Thread.sleep(9000);
		workOrderDetailsPage.clickMaterialAndToolMenu();
		workOrderDetailsPage.clickShoppingCart();
		workOrderDetailsPage.clickAddMaterialIcon();
		workOrderDetailsPage.clickItemChevron();
		//TODO  take a look at this method with defect GRAPHITE-54792 - [Technician] - Select Item Search field from Work Order Page > Materials and Tools > Request Materials > Add Item is pre-populated with previous entry
		workOrderDetailsPage.searchForItemAndSelectItem(itemNum2, itemIdQueryFromDB(itemNum2));
		workOrderDetailsPage.typeQuantityMaterialRequestAddItemSlidingDrawer("2.2");
		workOrderDetailsPage.clickSaveAddItem();
		logger.info("Verify material submit button is disabled");
		assertTrue(workOrderDetailsPage.materialDisabled());
		workOrderDetailsPage.requiredDateUnderRequiredDetailsOfMaterialRequest(2022, 10, 21);
		String priority2 = String.valueOf(AbstractAutomationFramework.randomInt());
		if (priority2.equals("0")) {
			priority2 = String.valueOf(AbstractAutomationFramework.randomInt());
		}
		workOrderDetailsPage.setPriorityOnMaterialRequest(String.valueOf(priority2));
		String droppoint2 = AbstractAutomationFramework.randomString(12).toUpperCase();
		workOrderDetailsPage.dropToMaterialRequest(droppoint2);
		workOrderDetailsPage.sendMaterialRequest();
		
		logger.info("11. Again click on \"+\" button in material list section, select another item and save it.");
		//TODO remove duplicated clickMaterialAndToolMenu when defect is resolved: GRAPHITE-54876 - [Technician] - Mobile - Materials and Tools sliding drawer refreshes twice when you click on something
		Thread.sleep(3000);
		workOrderDetailsPage.clickMaterialAndToolMenu();
		workOrderDetailsPage.clickShoppingCart();
		workOrderDetailsPage.clickAddMaterialIcon();
		workOrderDetailsPage.clickItemChevron();
		//TODO  take a look at this method with defect GRAPHITE-54792 - [Technician] - Select Item Search field from Work Order Page > Materials and Tools > Request Materials > Add Item is pre-populated with previous entry
		workOrderDetailsPage.searchForItemAndSelectItem(itemNum3, itemIdQueryFromDB(itemNum3));
		workOrderDetailsPage.typeQuantityMaterialRequestAddItemSlidingDrawer("3.3");
		workOrderDetailsPage.clickSaveAddItem();
		
		logger.info("12. Fill all the mandatory details in Request details section and click on send button.");
		workOrderDetailsPage.requiredDateUnderRequiredDetailsOfMaterialRequest(2022, 10, 22);
		String priority3 = String.valueOf(AbstractAutomationFramework.randomInt());
		if (priority3.equals("0")) {
			priority3 = String.valueOf(AbstractAutomationFramework.randomInt());
		}
		workOrderDetailsPage.setPriorityOnMaterialRequest(String.valueOf(priority3));
		String droppoint3 = AbstractAutomationFramework.randomString(12).toUpperCase();
		workOrderDetailsPage.dropToMaterialRequest(droppoint3);
		workOrderDetailsPage.sendMaterialRequest();

		/**
		 * Scenario 24:
		 * - Three MR requests should be displayed in Material list section.
		 * - For each material request in Material list section, Material name and description along with storeroom description is displayed. Also, value of quantity is displayed.
		 * - Verify user should be able to successfully save the request i.e. for each MR record an associated MRLINE record is created in database after user has clicked the send button.
		 * 
		 * Scenario 28:
		 * - Each "Material request" should be an individual MR with a single MRLINE record and when technician requests a second item, a new MR with associated MRLINE should be created.
		 * - All fields for each MR record and associated MR line record should be saved correctly. Refer previous scenario for fields details.
		 */
		logger.info("Three MR requests should be displayed in Material list section.");
		Thread.sleep(5000);
		workOrderDetailsPage.getMaterialsToolsSlidingDrawerHeaderLabel();
		Thread.sleep(3000);
		assertTrue(workOrderDetailsPage.verifyFirstRequestedMaterialStatus());
		Thread.sleep(5000);
		String mrnum = workOrderDetailsPage.clickRequestedMaterialListChevron(woNum, itemNum3, location2);
		//TODO to be refactored after defect is resolved - GRAPHITE-54822 - [Technician] - Material Request - Material List section has misleading information
		assertTrue(workOrderDetailsPage.verifyItemOnMaterialList(woNum, mrnum, itemNum3, itemDescription3, location2, null, "3.3"));
		workOrderDetailsPage.clickItemChevronOnMaterialList(woNum, itemNum3, itemDescription3, location2, null, "3.3");
		
		Object[] MRTable = workOrderDetailsPage.getValueFromMRTable(mrnum, SetupData.SITEID);
		Object[] WORKORDERTable = workOrderDetailsPage.getValueFromWORKORDERTable(woNum, SetupData.SITEID);
		Object[] MRLINETable = workOrderDetailsPage.getValueFromMRLINETable(mrnum, itemNum3, SetupData.SITEID);
		String curdate = getUtcDate();
		
		//defect GRAPHITE-54981 - [Technician] - MR.Description is not generated as expected
		//assertEquals(MRTable[0]+"", "Material Request for Work Order "+woNum);
		assertEquals(MRTable[1]+"", labor.toUpperCase());
		assertEquals(MRTable[2]+"", labor.toUpperCase());
		logger.info("MR requireddate: " + MRTable[3]+"");
		String requireddate = MRTable[3]+"";
		requireddate = requireddate.substring(0, 10);
		logger.info("MR requireddate: " + requireddate);
		assertEquals(requireddate, "2022-10-22");
		
		logger.info("MR priority: " + MRTable[4]+"");
		logger.info("MR priority: " + priority3);
		assertEquals(MRTable[4]+"", priority3);
		
		logger.info("MR droppoint: " + MRTable[5]+"");
		logger.info("MR droppoint: " + droppoint3);
		assertEquals(MRTable[5]+"", droppoint3);
		
		logger.info("MR glaccount: " + MRTable[6]+"");
		logger.info("MR glaccount: " + SetupData.GLDEBITACCT);
		logger.info("WO glaccount: " + WORKORDERTable[0]+"");
		assertEquals(MRTable[6]+"", SetupData.GLDEBITACCT);
		assertEquals(WORKORDERTable[0]+"", SetupData.GLDEBITACCT);
		
		logger.info("MR wonum: " + MRTable[7]+"");
		logger.info("MR wonum: " + woNum);
		assertEquals(MRTable[7]+"", woNum);
		
		logger.info("MR location: " + MRTable[8]+"");
		logger.info("MR location: " + WORKORDERTable[1]+"");
		assertEquals(MRTable[8]+"", WORKORDERTable[1]+"");
		
		logger.info("MR assetnum: " + MRTable[9]+"");
		logger.info("MR assetnum: " + WORKORDERTable[2]+"");
		assertEquals(MRTable[9]+"", WORKORDERTable[2]+"");
		
		logger.info("MR siteid: " + MRTable[10]+"");
		logger.info("MR siteid: " + WORKORDERTable[3]+"");
		assertEquals(MRTable[10]+"", WORKORDERTable[3]+"");
		
		logger.info("MR orgid: " + MRTable[11]+"");
		logger.info("MR orgid: " + WORKORDERTable[4]+"");
		assertEquals(MRTable[11]+"", WORKORDERTable[4]+"");
		
		logger.info("MR status: " + MRTable[12]+"");
		assertEquals(MRTable[12]+"", "DRAFT");
		
		String curdatesql = MRTable[13]+"";
		curdatesql = curdatesql.substring(0, 10);
		logger.info("MR current date: " + MRTable[13]+"");
		logger.info("MR current date: " + curdatesql);
		logger.info("MR current date: " + curdate);
		assertEquals(curdatesql,curdate);
		
		logger.info("MRLine linetype: " + MRLINETable[1]+"");
		logger.info("MRLine linetype: " + SetupData.LineType.ITEM);
		assertEquals(MRLINETable[1]+"", SetupData.LineType.ITEM.toString());
		
		logger.info("MRLine itemnum: " + MRLINETable[2]+"");
		logger.info("MRLine itemnum: " + itemNum3);
		assertEquals(MRLINETable[2]+"",itemNum3);
		
		logger.info("MRLine itemdescription: " + MRLINETable[3]+"");
		logger.info("MRLine iteitemdescriptionmnum: " + itemDescription3);
		assertEquals(MRLINETable[3]+"",itemDescription3);
		
		logger.info("MRLine manufacturer: " + MRLINETable[4]+"");
		logger.info("MRLine manufacturer: " + SetupData.MANUFACTURER);
		assertEquals(MRLINETable[4]+"",SetupData.MANUFACTURER);
		
		//question on https://github.ibm.com/maximo-app-framework/technician-app/pull/1874
		//assertEquals(MRLINETable[13]+"",vendor);
		//assertEquals(MRLINETable[5]+"",modelnum)
		//assertEquals(MRLINETable[6]+"",catalogcode);
		
		logger.info("MRLine qty: " + MRLINETable[7] + "");
		// Note : On EAM env - qty : 3.3 , On FVT env - qty : 3.30
		if (("" + MRLINETable[7] + "").trim().toString().equalsIgnoreCase("3.3")) {
			assertEquals(MRLINETable[7] + "", "3.3");
		} else {
			assertEquals(MRLINETable[7] + "", "3.30");
		}
		
		logger.info("MRLine issueunit: " + MRLINETable[8]+"");
		logger.info("MRLine issueunit: " + SetupData.ISSUEUNIT);
		assertEquals(MRLINETable[8]+"",SetupData.ISSUEUNIT);
		
		logger.info("MRLine conversion: " + MRLINETable[9]+"");
		assertEquals(MRLINETable[9]+"".toString(),"null");
		
		logger.info("MRLine unitcost: " + MRLINETable[10]+"");	
		// Note : On EAM env - unitcost : 0 , On FVT env - unitcost : 0.00
		if (("" + MRLINETable[10] + "").trim().toString().equalsIgnoreCase("0")) {
			assertEquals(MRLINETable[10] + "", "0");
		} else {
			assertEquals(MRLINETable[10] + "", "0.00");
		}
		
		logger.info("MRLine linecost: " + MRLINETable[11]+"");
		// Note : On EAM env - linecost : 0 , On FVT env - linecost : 0.00
		if (("" + MRLINETable[11] + "").trim().toString().equalsIgnoreCase("0")) {
			assertEquals(MRLINETable[11] + "", "0");
		} else {
			assertEquals(MRLINETable[11] + "", "0.00");
		}
		
		logger.info("MRLine currencycode: " + MRLINETable[12]+"");
		assertEquals(MRLINETable[12]+"",SetupData.CURRENCYCODE);
		
		logger.info("MRLine vendor: " + MRLINETable[13]+"");
		assertEquals(MRLINETable[13]+"".toString(),"null");
		
		logger.info("MRLine storeloc: " + MRLINETable[14]+"");
		logger.info("MRLine storeloc: " + location2);
		assertEquals(MRLINETable[14]+"",location2);
		
		logger.info("MRLine gldebitaccount: " + MRLINETable[15]+"");
		logger.info("MRLine gldebitaccount: " + SetupData.GLDEBITACCT);
		assertEquals(MRLINETable[15]+"",SetupData.GLDEBITACCT);
		
		logger.info("MRLine inspectionrequired: " + MRLINETable[16]+"");
		assertEquals(MRLINETable[16]+"","0");
		
		String mrlinereqdate = MRLINETable[17]+"";
		mrlinereqdate = mrlinereqdate.substring(0, 10);
		logger.info("MRLine requireddate: " + mrlinereqdate);	
		logger.info("MRLine requireddate: " + requireddate);	
		assertEquals(mrlinereqdate,requireddate);
		
		logger.info("MRLine isdistributed: " + MRLINETable[18]+"");	 
		Thread.sleep(3000);
		assertEquals(MRLINETable[18]+"","0");
		workOrderDetailsPage.clickAddItemOpenDrawerButton();
		workOrderDetailsPage.clickAddItemCloseDrawerButton();
		workOrderDetailsPage.clickBackBreadcrumbIconOnMaterialRequest();

	}

	protected void createDefaultObjects() throws Exception {
		logger.info("Creating default objects");
		
		// Creating storeroom without description
		logger.info("Creating an storeroom");
		location2 = AbstractAutomationFramework.randomString(12).toUpperCase();
		String storeRoomResult2 = maximoApi.retrieve(new StoreRoom(),
				"lean=1&addid=1&action=system:new&ignorecollectionref=1");
		StoreRoom storeRoom2 = new Gson().fromJson(storeRoomResult2, StoreRoom.class);
		storeRoom2.setLocation(location2);

		maximoApi.create(storeRoom2);
		logger.info("storeRoom location: " + location2);
		
		// Creating the item with description and added to storeroom without description
		logger.info("Creating an item");
		itemNum3 = AbstractAutomationFramework.randomString(10).toUpperCase();
		String itemResult3 = maximoApi.retrieve(new Item(),
				"lean=1&addid=1&domainmeta=1&internalvalues=1&action=system:new&oslc.select=itemsetid,lottype,issueunit,orderunit,rotating,itemtype");
		Item item3 = new Gson().fromJson(itemResult3, Item.class);
		item3.setItemNum(itemNum3);
		item3.setRotating(true);
		itemDescription3 = AbstractAutomationFramework.randomString(10).toUpperCase();
		item3.setDescription(itemDescription3);
		maximoApi.create(item3);
		logger.info("ItemNum: " + itemNum3);
		itemIdQueryFromDB(itemNum3);
		
		// item status change and rolldown
		logger.info("Item status change to Active");
		ItemChangeStatus itemChangeStatus5 = new ItemChangeStatus();
		List<ItemChangeStatus> arr = new ArrayList<>();
		itemChangeStatus5.setStatus(ItemStatus.ACTIVE.toString());
		itemChangeStatus5.setRollDown("true");
		arr.add(itemChangeStatus5);
		item3.setItemChangeStatus(arr);
		maximoApi.update(item3);
		logger.info("Item status changed");

		// add Item to StoreRoom
		maximoApi.addItemToStoreRoom(itemNum3, SetupData.ITEMSET, location2, SetupData.SITEID, "false",
				SetupData.ISSUEUNIT, "1234567.45", SetupData.DFLTBIN, "", "", "0.0", "1234567.45", "0.0", "0", "0", "0.0", "0.0", "true");
		logger.info("added Item to StoreRoom successfully");

		// Create an asset
		logger.info("Creating an asset");
		assetNum = AbstractAutomationFramework.randomString(5).toUpperCase();
		String assetResult = maximoApi.retrieve(new Asset(), "addid=1&internalvalues=1&action=system:new&addschema=1");
		Asset newAsset = new Gson().fromJson(assetResult, Asset.class);
		newAsset.setAssetNum(assetNum);
		newAsset.setDescription(ASSET_DESCRIPTION + assetNum);
		newAsset.setManufacturer(SetupData.MANUFACTURER);
		newAsset.setItemNum(itemNum3);
		maximoApi.create(newAsset);
		logger.info("Asset: " + assetNum);

		// Create a work order
		logger.info("Creating a work order");
		String workOrderResult = maximoApi.retrieve(new WorkOrder(),
				"addid=1&internalvalues=1&action=system:new&addschema=1");
		newWorkOrder = new Gson().fromJson(workOrderResult, WorkOrder.class);
		newWorkOrder.setDescription(WORKORDER_DESCRIPTION + assetNum);
		newWorkOrder.setSiteId(SetupData.SITEID);
		newWorkOrder.setAssetNum(assetNum);
		newWorkOrder.setWorkType(WorkType.PM.toString());
		newWorkOrder.setGlaccount(SetupData.GLDEBITACCT);
		maximoApi.create(newWorkOrder);
		woNum = newWorkOrder.getWoNum();
		logger.info("Work Order: " + woNum);

		// Assign the labor
		maximoApi.addAssignmentLabor(newWorkOrder, labor);
		logger.info("Assignment added");

		// Creating storeroom
		logger.info("Creating an storeroom");
		location = AbstractAutomationFramework.randomString(10).toUpperCase();
		String storeRoomResult = maximoApi.retrieve(new StoreRoom(),
				"lean=1&addid=1&action=system:new&ignorecollectionref=1");
		StoreRoom storeRoom = new Gson().fromJson(storeRoomResult, StoreRoom.class);
		locationDescription = AbstractAutomationFramework.randomString(10).toUpperCase();
		storeRoom.setDescription(locationDescription);
		storeRoom.setLocation(location);

		maximoApi.create(storeRoom);
		logger.info("storeRoom location: " + location);
		
		// Creating the item
		logger.info("Creating an item");
		itemNum = AbstractAutomationFramework.randomString(10).toUpperCase();
		String itemResult = maximoApi.retrieve(new Item(),
				"lean=1&addid=1&domainmeta=1&internalvalues=1&action=system:new&oslc.select=itemsetid,lottype,issueunit,orderunit,rotating,itemtype");
		Item item = new Gson().fromJson(itemResult, Item.class);
		item.setItemNum(itemNum);
		itemDescription = AbstractAutomationFramework.randomString(10).toUpperCase();
		item.setDescription(itemDescription);
		maximoApi.create(item);
		logger.info("ItemNum: " + itemNum);
		itemIdQueryFromDB(itemNum);
		
		// item status change and rolldown
		logger.info("Item status change to Active");
		ItemChangeStatus ics = new ItemChangeStatus();
		ics.setStatus(ItemStatus.ACTIVE.toString());
		ics.setRollDown("true");
		arr.add(ics);
		item.setItemChangeStatus(arr);
		maximoApi.update(item);
		logger.info("Item status changed");

		// add Item to StoreRoom
		maximoApi.addItemToStoreRoom(itemNum, SetupData.ITEMSET, location, SetupData.SITEID, "false",
				SetupData.ISSUEUNIT, "1234567.45", SetupData.DFLTBIN, "", "", "0.0", "1234567.45", "0.0", "0", "0", "0.0", "0.0", "true");
		logger.info("added Item to StoreRoom successfully");

		logger.info("Adding item to wpmaterial");
		WpMaterial wpmaterial = new WpMaterial();
		wpmaterial.setLinetype(SetupData.LineType.ITEM.toString());
		wpmaterial.setItemNum(itemNum);
		wpmaterial.setItemQty(1234567.45);
		wpmaterial.setLocation(location);
		List<WpMaterial> wpMaterialArray = new ArrayList<WpMaterial>();
		wpMaterialArray.add(wpmaterial);
		
		logger.info("Adding material to wpmaterial");
		materialDescription = AbstractAutomationFramework.randomString(10).toUpperCase();
		WpMaterial wpmaterial1 = new WpMaterial();
		wpmaterial1.setLinetype(SetupData.LineType.MATERIAL.toString());
		wpmaterial1.setDescription(materialDescription);
		wpmaterial1.setItemQty(0);
		wpMaterialArray.add(wpmaterial1);
		newWorkOrder.setWpMaterial(wpMaterialArray);
				
		// Creating a tool
		logger.info("Creating a tool");
		tool0 = AbstractAutomationFramework.randomString(30).toUpperCase();
		toolDescription0 = AbstractAutomationFramework.randomString(10).toUpperCase();

		String toolItemResult = maximoApi.retrieve(new ToolItem(),
				"addid=1&internalvalues=1&action=system:new&oslc.select=itemnum,siteid,description,itemsetid,rotating,lottype,itemtype,status,itemid&addschema=1");
		ToolItem toolItem = new Gson().fromJson(toolItemResult, ToolItem.class);
		toolItem.setItemNum(tool0);
		toolItem.setDescription(toolDescription0);
		maximoApi.create(toolItem);
		logger.info("ToolItem: " + tool0);

		// item status change and rolldown
		logger.info("Changing Tool Status");
		//List<ItemChangeStatus> arr = new ArrayList<>();
		ItemChangeStatus itemChangeStatus = new ItemChangeStatus();
		itemChangeStatus.setStatus(ItemStatus.ACTIVE.toString());
		itemChangeStatus.setRollDown("true");
		arr.add(itemChangeStatus);
		toolItem.setItemChangeStatus(arr);
		maximoApi.update(toolItem);
		logger.info("Tool API status changed");

		// Add Tool to StoreRoom
		maximoApi.addItemToStoreRoom(tool0, SetupData.ITEMSET, location, SetupData.SITEID, "false",
				SetupData.ISSUEUNIT, "123456.45", SetupData.DFLTBIN, "", "", "0.0", "123456.45", "0.0", "0", "0", "0.0", "0.0",
				"true");
		logger.info("added Tool to StoreRoom successfully");

		// Adding Tool to Workorder
		logger.info("Adding wptool");
		WpTool wptool = new WpTool();
		wptool.setItemNum(toolItem.getItemNum());
		wptool.setItemQty(123456.45);
		wptool.setRate(1);
		wptool.setReservereq(true);
		wptool.setHours("123.5");
		List<WpTool> wpToolArray = new ArrayList<WpTool>();
		wpToolArray.add(wptool);
		//
		// Creating a tool
		logger.info("Creating a tool");
		tool1 = AbstractAutomationFramework.randomString(10).toUpperCase();
		toolDescription1 = AbstractAutomationFramework.randomString(10).toUpperCase();

		String toolItemResult1 = maximoApi.retrieve(new ToolItem(),
				"addid=1&internalvalues=1&action=system:new&oslc.select=itemnum,siteid,description,itemsetid,rotating,lottype,itemtype,status,itemid&addschema=1");
		ToolItem toolItem1 = new Gson().fromJson(toolItemResult1, ToolItem.class);
		toolItem1.setItemNum(tool1);
		toolItem1.setDescription(toolDescription1);
		maximoApi.create(toolItem1);
		logger.info("ToolItem: " + tool1);

		// item status change and rolldown
		logger.info("Changing Tool Status");
		//List<ItemChangeStatus> arr = new ArrayList<>();
		ItemChangeStatus itemChangeStatus1 = new ItemChangeStatus();
		itemChangeStatus1.setStatus(ItemStatus.ACTIVE.toString());
		itemChangeStatus1.setRollDown("true");
		arr.add(itemChangeStatus1);
		toolItem1.setItemChangeStatus(arr);
		maximoApi.update(toolItem1);
		logger.info("Tool API status changed");

		// Add Tool to StoreRoom
		maximoApi.addItemToStoreRoom(tool1, SetupData.ITEMSET, location, SetupData.SITEID, "false",
				SetupData.ISSUEUNIT, "123456.45", SetupData.DFLTBIN, "", "", "0.0", "123456.45", "0.0", "0", "0", "0.0", "0.0",
				"true");
		logger.info("added Tool to StoreRoom successfully");

		// Adding Tool to Workorder
		logger.info("Adding wptool");
		WpTool wptool1 = new WpTool();
		wptool1.setItemNum(toolItem1.getItemNum());
		wptool1.setItemQty(0);
		wptool1.setRate(1);
		wptool1.setReservereq(true);
		wptool1.setHours("0.5");
		wpToolArray.add(wptool1);

		// Creating a tool
		logger.info("Creating a tool");
		tool2 = AbstractAutomationFramework.randomString(10).toUpperCase();
		//creating tool2 without description on purpose to validate different fulfillment 
		//toolDescription2 = AbstractAutomationFramework.randomString(10).toUpperCase();

		String toolItemResult2 = maximoApi.retrieve(new ToolItem(),
				"addid=1&internalvalues=1&action=system:new&oslc.select=itemnum,siteid,description,itemsetid,rotating,lottype,itemtype,status,itemid&addschema=1");
		ToolItem toolItem2 = new Gson().fromJson(toolItemResult2, ToolItem.class);
		toolItem2.setItemNum(tool2);
		//toolItem2.setDescription(toolDescription2);
		maximoApi.create(toolItem2);
		logger.info("ToolItem: " + tool2);

		// item status change and rolldown
		logger.info("Changing Tool Status");
		//List<ItemChangeStatus> arr = new ArrayList<>();
		ItemChangeStatus itemChangeStatus2 = new ItemChangeStatus();
		itemChangeStatus2.setStatus(ItemStatus.ACTIVE.toString());
		itemChangeStatus2.setRollDown("true");
		arr.add(itemChangeStatus2);
		toolItem2.setItemChangeStatus(arr);
		maximoApi.update(toolItem2);
		logger.info("Tool API status changed");

		// Add Tool to StoreRoom
		maximoApi.addItemToStoreRoom(tool2, SetupData.ITEMSET, location, SetupData.SITEID, "false",
				SetupData.ISSUEUNIT, "123456.45", SetupData.DFLTBIN, "", "", "0.0", "123456.45", "0.0", "0", "0", "0.0", "0.0",
				"true");
		logger.info("added Tool to StoreRoom successfully");

		// Adding Tool to Workorder
		logger.info("Adding wptool");
		WpTool wptool2 = new WpTool();
		wptool2.setItemNum(toolItem2.getItemNum());
		wptool2.setItemQty(123456.45);
		wptool2.setRate(1);
		wptool2.setReservereq(true);
		wpToolArray.add(wptool2);
		newWorkOrder.setWpTool(wpToolArray);
		maximoApi.update(newWorkOrder);

		// Change WO status to Approved
		logger.info("Changing work order status to APPR");
		maximoApi.changeStatus(newWorkOrder, WoStatus.APPR.toString());
		logger.info("APPR WO");
		
		//Creating multiple items to be requested later
		
		// Creating the item with description and added to storeroom with description
		logger.info("Creating an item");
		itemNum1 = AbstractAutomationFramework.randomString(10).toUpperCase();
		String itemResult1 = maximoApi.retrieve(new Item(),
				"lean=1&addid=1&domainmeta=1&internalvalues=1&action=system:new&oslc.select=itemsetid,lottype,issueunit,orderunit,rotating,itemtype");
		Item item1 = new Gson().fromJson(itemResult1, Item.class);
		item1.setItemNum(itemNum1);
		itemDescription1 = AbstractAutomationFramework.randomString(10).toUpperCase();
		item1.setDescription(itemDescription1);
		maximoApi.create(item1);
		logger.info("ItemNum1: " + itemNum1);
		itemIdQueryFromDB(itemNum1);
		
		// item status change and rolldown
		logger.info("Item status change to Active");
		ItemChangeStatus itemChangeStatus3 = new ItemChangeStatus();
		itemChangeStatus3.setStatus(ItemStatus.ACTIVE.toString());
		itemChangeStatus3.setRollDown("true");
		arr.add(itemChangeStatus3);
		item1.setItemChangeStatus(arr);
		maximoApi.update(item1);
		logger.info("Item status changed");

		// add Item to StoreRoom
		maximoApi.addItemToStoreRoom(itemNum1, SetupData.ITEMSET, location, SetupData.SITEID, "false",
				SetupData.ISSUEUNIT, "1234567.45", SetupData.DFLTBIN, "", "", "0.0", "1234567.45", "0.0", "0", "0", "0.0", "0.0", "true");
		logger.info("added Item to StoreRoom successfully");
		
		// Creating the item without description and added to storeroom without description
		logger.info("Creating an item");
		itemNum2 = AbstractAutomationFramework.randomString(10).toUpperCase();
		String itemResult2 = maximoApi.retrieve(new Item(),
				"lean=1&addid=1&domainmeta=1&internalvalues=1&action=system:new&oslc.select=itemsetid,lottype,issueunit,orderunit,rotating,itemtype");
		Item item2 = new Gson().fromJson(itemResult2, Item.class);
		item2.setItemNum(itemNum2);
		maximoApi.create(item2);
		logger.info("ItemNum: " + itemNum2);
		itemIdQueryFromDB(itemNum2);
		
		// item status change and rolldown
		logger.info("Item status change to Active");
		ItemChangeStatus itemChangeStatus4 = new ItemChangeStatus();
		itemChangeStatus4.setStatus(ItemStatus.ACTIVE.toString());
		itemChangeStatus4.setRollDown("true");
		arr.add(itemChangeStatus4);
		item2.setItemChangeStatus(arr);
		maximoApi.update(item2);
		logger.info("Item status changed");

		// add Item to StoreRoom
		maximoApi.addItemToStoreRoom(itemNum2, SetupData.ITEMSET, location2, SetupData.SITEID, "false",
				SetupData.ISSUEUNIT, "1234567.45", SetupData.DFLTBIN, "", "", "0.0", "1234567.45", "0.0", "0", "0", "0.0", "0.0", "true");
		logger.info("added Item to StoreRoom successfully");

	}

	protected String itemIdQueryFromDB(String itemnum) {
		String query = "select I.itemid from MAXIMO.item I where I.itemnum = '" + itemnum + "' and I.itemsetid= '" + SetupData.ITEMSET + "'";
		logger.info(query);
		Object[] resultsObjects = jdbcConnection.executeSQL(query);
		Object[] resultArray1 = (Object[]) resultsObjects[0];
		itemId = resultArray1[0].toString();
		logger.info("itemId>" + itemId);
		return itemId;
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
	// create getCurrentUtcTime() method to get the current UTC time
	public String getUtcDate() throws Exception { // handling ParseException
		// create an instance of the SimpleDateFormat class
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// set UTC time zone by using SimpleDateFormat class
		sdf.setTimeZone(TimeZone.getTimeZone(timezone));
		// create another instance of the SimpleDateFormat class for local date format
		SimpleDateFormat ldf = new SimpleDateFormat("yyyy-MM-dd");
		// declare and initialize a date variable which we return to the main method
		Date d1 = null;
		String time1 = null;
		// use try catch block to parse date in UTC time zone
		try {
			// parsing date using SimpleDateFormat class
			d1 = ldf.parse(sdf.format(new Date()));
			time1 = ldf.format(d1).toString();
			logger.info("Issue with Startdate time1" + time1);
		}
		// catch block for handling ParseException
		catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info("Issue with Startdate");
		}

		return time1;

	}

}
