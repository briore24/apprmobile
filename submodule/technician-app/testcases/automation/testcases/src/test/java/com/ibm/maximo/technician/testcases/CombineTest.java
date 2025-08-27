package com.ibm.maximo.technician.testcases;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertEquals;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.SkipException;
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
import com.ibm.maximo.automation.mobile.api.json.Task;
import com.ibm.maximo.automation.mobile.api.json.WorkOrder;
import com.ibm.maximo.automation.mobile.common.AppSwitcher;
import com.ibm.maximo.automation.mobile.common.AppSwitcher.App;
import com.ibm.maximo.components.ChatLogComponent;
import com.ibm.maximo.components.LabelComponent;
import com.ibm.maximo.components.TextAreaComponent;
import com.ibm.maximo.technician.framework.TechnicianTest;
import com.ibm.maximo.technician.page.AssetDetailsPage;
import com.ibm.maximo.technician.page.AssetListPage;
import com.ibm.maximo.technician.page.CreateWorkOrderPage;
import com.ibm.maximo.technician.page.EditWorkOrderPage;
import com.ibm.maximo.technician.page.MySchedulePage;
import com.ibm.maximo.technician.page.WorkLogPage;
import com.ibm.maximo.technician.page.WorkOrderDetailsPage;
import com.ibm.maximo.technician.setupdata.SetupData;
import com.ibm.maximo.technician.setupdata.SetupData.*;

/* SearchTest
 * GRAPHITE-51049: View and Search work lists 3A
 * GRAPHITE-64716: [TECHMOBILE] View and Search work lists :18M,5TA,5A
 */

/* ViewWorkOrderTest
 * GRAPHITE-50896: Keep the WO number visible after pressing the "Edit" button inside a Work Order on the App
 */

/* AddWorkLog
 * GRAPHITE-47694: Eli should be able to start enter work logs to a WO
 * GRAPHITE-31727: Enable Long Description support for the Chat-Log component
 * GRAPHITE-64661: [TECHMOBILE] Worklog :15M,2TA,0A Scenario-2 , Scenario-6
 */

/* NavigatorTest
 * GRAPHITE-51047: Launch from Navigator (My schedule, maps, tools and materials)
 */

/* AssetLookupTest
 * GRAPHITE-44632 vendor and serial number in the asset lookup attribute
 */

/*
 * SwitchToAssetDetailsTest
 * GRAPHITE-69311: [TECHMOBILE] WO Details Asset Location: 47M,3TA,3A
 */

// Note CombineTest TC P1 = 12  , P2 = 11
public class CombineTest extends TechnicianTest {
	private final Logger logger = LoggerFactory.getLogger(CombineTest.class);
	private AbstractAutomationFramework af;
	private TestSuite testSuite;
	private boolean apiCodeSuccess = false;
	private MaximoApi maximoApi;
	private WorkOrder newWorkOrder, newWorkOrder1;
	private String assetNum, woNum, woNum1, woNum2, labor, masServer, deviceType;
	private boolean mapConfiguration;
	private String noteDescription = "Automation WorkLog Text on WO list page";
	private String noteSummaryText = "Summary for WO";
	private String longDescriptionText = "Long decsription for WO added";
	private String chatLogEditHeadertext = "Add note";
	private String noteDescriptionDetailsPage = "Automation WorkLog Text on WO details page";
	private String noteSummaryText100 = "Lorem ipsum dolor sit amet, consectetuer "
			+ "adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. "
			+ "Sum sociis natoque penatibus et magnis dis parturient montes, "
			+ "nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque"
			+ " eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo, "
			+ "fringilla vel, aliquet nec, vulputate eget, arcu. In enim justo, rhoncus ut, "
			+ "imperdiet a, venenatis vitae, justo. Nullam dictum felis eu pede mollis "
			+ "pretium. Integer tincidunt. Cras dapibus. Vivamus elementum semper nisi. "
			+ "Aenean vulputate eleifend tellus. Aenean leo ligula, porttitor eu, "
			+ "consequat vitae, eleifend ac, enim. Aliquam lorem ante, dapibus in, " + "viverra quis, feugiat a,";
	private String noteDescription1 = "Automation WorkLog Text on WO list page 1";
	private String noteDescription2 = "Automation WorkLog Text on WO list page 2";
	private String saveDiscardText = "To leave this page, you must first discard or save your changes.";

	@BeforeClass(alwaysRun = true)
	@Parameters({ "configPath" })
	public void setup(String configPath) throws Exception {
		logger.info("********************CombineTest*********************************");
		this.af = FrameworkFactory.get();
		Properties properties = new Properties();
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(configPath));
			properties.load(in);
			labor = properties.getProperty("system.username");
			deviceType = properties.getProperty("system.deviceType");
			maximoApi = new MaximoApi();
			maximoApi.setMaximoServer(properties.getProperty("system.maximoServerUrl"),
					properties.getProperty("system.maximoAPIKey"));
			createDefaultObjects();
			mapConfiguration = mapNameFromMapManager(SetupData.MAPNAME);
			logger.info("mapConfiguration = " + mapConfiguration);
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
		// complete WO via DB after testcase executed
		jdbcConnection.executeUpdateSQL("UPDATE MAXIMO.WORKORDER SET STATUS = 'COMP' WHERE WONUM = '" + woNum + "'");
		jdbcConnection.executeUpdateSQL("UPDATE MAXIMO.WORKORDER SET STATUS = 'COMP' WHERE WONUM = '" + woNum1 + "'");
		if (testSuite != null) {
			testSuite.teardown();
		}
	}

	@Test(enabled = true, groups = {
			"mobile", "desktop" }, description = "Verify user can view and search in a WO list by WO number", timeOut = 200000)
	public void aSearchForWorkOrderByWONumber() throws Exception {
		MySchedulePage assignedWorkPage = new MySchedulePage(af);

		// Verify user is able to search the work order with WORKORDER.WONUM
		assertTrue(assignedWorkPage.search(woNum));

		// Clear Search Result
		assignedWorkPage.clickClearButton();
	}

	@Test(enabled = true, groups = {
			"mobile", "desktop" }, description = "Search WO by Asset Number Description in a WO list", timeOut = 200000)
	public void aSearchForWorkOrderByAssetNumberDesc() throws Exception {
		// Permission check in MySchedulePage Constructor then WO list page displayed
		MySchedulePage assignedWorkPage = new MySchedulePage(af);

		// Verify user is able to search the work order with
		// WORKORDER.ASSETNUM.DESCRIPTION
		assertTrue(assignedWorkPage.search(SetupData.ASSET_DESCRIPTION + assetNum));

		// Clear Search Result
		assignedWorkPage.clickClearButton();
	}

	@Test(enabled = true, groups = {
			"mobile", "desktop" }, description = "Search WO by description in a WO list", timeOut = 200000)
	public void aSearchForWorkOrderByDescription() throws Exception {
		MySchedulePage assignedWorkPage = new MySchedulePage(af);

		// Verify user is able to search the work order with WORKORDER.DESCRIPTION
		assertTrue(assignedWorkPage.search(SetupData.WORKORDER_DESCRIPTION + assetNum));

		// Clear Search Result
		assignedWorkPage.clickClearButton();
	}

	@Test(enabled = true, groups = {
			"mobile", "desktop" }, description = "Verify user is able to go to detail in Work order page and it has correct title", timeOut = 500000)
	public void viewWorkOrderDetails() throws Exception {

		// Permission check in MySchedulePage Constructor then WO list page displayed
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		// Open work order detail page
		WorkOrderDetailsPage assignedWorkOrderDetailsPage = new WorkOrderDetailsPage(af);
		assertTrue(assignedWorkPage.search(woNum));

		// Click on Chevron Icon on Work Order
		assignedWorkPage.openWorkOrderDetails();

		// click on Edit Work Order
		assignedWorkOrderDetailsPage.editWorkOrderDetails();
		// verify Work Order page title value
		String dynamicTitle = assignedWorkOrderDetailsPage.getTitle(assignedWorkOrderDetailsPage.woEditPageTitle);
		String expectedTitle = woNum + " Edit work order";
		assertEquals(expectedTitle, dynamicTitle);
		assignedWorkOrderDetailsPage.clickButton(assignedWorkOrderDetailsPage.workOrderBreadcrumb);
	}

	@Test(enabled = true, dependsOnMethods = { "viewWorkOrderDetails" }, groups = {
			"mobile", "desktop" }, description = "Verify user is able to go to detail in report page and it has correct title", timeOut = 500000)
	public void viewWorkOrderReport() throws Exception {

		WorkOrderDetailsPage assignedWorkOrderDetailsPage = new WorkOrderDetailsPage(af);

		assignedWorkOrderDetailsPage.clickReportWorkButton();
		// verify Report page title value
		String dynamicTitle = assignedWorkOrderDetailsPage.getTitle(assignedWorkOrderDetailsPage.reportWorkPageTitle);
		String expectedTitle = woNum + " Report work";
		assertEquals(expectedTitle, dynamicTitle);
		assignedWorkOrderDetailsPage.clickButton(assignedWorkOrderDetailsPage.reportWorkBreadcrumb);

	}

	@Test(enabled = true, dependsOnMethods = { "viewWorkOrderReport" }, groups = {
			"mobile", "desktop" }, description = "Verify user is able to go to detail in task page and it has correct title", timeOut = 500000)
	public void viewWorkOrderTask() throws Exception {
		// Open work order list page
		WorkOrderDetailsPage assignedWorkOrderDetailsPage = new WorkOrderDetailsPage(af);

		// click on Task Work
		assignedWorkOrderDetailsPage.clickTaskButton();
		// verify Task page title value
		String dynamicTitle = assignedWorkOrderDetailsPage.getTitle(assignedWorkOrderDetailsPage.taskPageTitle);
		String expectedTitle = woNum + " Tasks";
		assertEquals(expectedTitle, dynamicTitle);
		assignedWorkOrderDetailsPage.clickButton(assignedWorkOrderDetailsPage.taskBreadcrumb);

		// Back to WO list view
		WorkOrderDetailsPage.clickBackWOList();
		Thread.sleep(2000);
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		// Clear Search Result
		assignedWorkPage.clickClearButton();
	}

	// Open bug MAXMOA-3998 due to which disable a test
	@Test(enabled = false, groups = {
			"mobile" }, description = "Verify user is able to add worklog on WO list page and WO details page", timeOut = 500000)
	public void checkAddWorkLog() throws Exception {
		
		if (deviceType.equalsIgnoreCase("ios"))
			throw new SkipException("Skipping this test due to open bug in framework MAXMOA-954 & MAXMOA-957");
		
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkLogPage workLogPage = new WorkLogPage(af);

		// Search the work order
		assignedWorkPage.search(woNum);

		// Verify the availability of "Work log" touch-point on the work order card list page
		assignedWorkPage.clickWorkLogTouchpoint();

		// Assert work log drawer header
		assertEquals("Work log", assignedWorkPage.getWorkLogDrawerHeader());

		// Add work log in the text area
		TextAreaComponent textArea = af.instantiateComponent(TextAreaComponent.class, "g7wpp_ChatLogTextInput");
		textArea.type(noteDescription);

		// Verify that technician can enter work log entry using send key
		ChatLogComponent chatLog = af.instantiateComponent(ChatLogComponent.class, "g7wpp_chatLogSendBtn");
		chatLog.sendText();

		// Assert the date being shown is the current date
		LabelComponent dateLabel = af.instantiateComponent(LabelComponent.class, "chatLogDate_0_date");
		SimpleDateFormat month = new SimpleDateFormat("MMMM");
		SimpleDateFormat day = new SimpleDateFormat("dd");
		SimpleDateFormat year = new SimpleDateFormat("yyyy");
		assertEquals(
				"Today, " + month.format(new Date()) + " " + day.format(new Date()) + ", " + year.format(new Date()),
				dateLabel.text());

		// Assert work log note
		assertEquals(noteDescription, assignedWorkPage.getWorkLogOnWoListPage());

		// Verify "expand" icon/button is displayed on the "Work log" drawer
		workLogPage.clickExpandIcon();

		// verify title of page
		String chatLogEditHeader = workLogPage.getTitle(workLogPage.chatLogTitle);
		assertEquals(chatLogEditHeadertext, chatLogEditHeader);

		// enter summary
		workLogPage.noteSummary(noteSummaryText);

		// enter long description
		workLogPage.enterLongDescription(longDescriptionText);

		assertEquals("Work log type", workLogPage.workLogType());

		// select worklog type
		workLogPage.addWorkLogType(WorkLogType.UPDATE);

		// save worklog details
		workLogPage.saveWorkLog();

		// click on chevron to expand long description
		workLogPage.clickWorkLogChevron();

		assertEquals(longDescriptionText, workLogPage.verifyLongDescription());

		// click on back button to navigate to worklog page
		workLogPage.backButton();

		// click on x button
		workLogPage.closeDrawer();

		// navigate to WO details page
		assignedWorkPage.openWorkOrderDetails();

		// Verify the availability of "Work log" touch-point on the work order details page
		WorkOrderDetailsPage woDetailPage = new WorkOrderDetailsPage(af);
		woDetailPage.clickWorkLogButton();

		// Add work log in the text area
		textArea = af.instantiateComponent(TextAreaComponent.class, "qj2ge_ChatLogTextInput");
		textArea.type(noteDescriptionDetailsPage);

		// Click on send button
		chatLog = af.instantiateComponent(ChatLogComponent.class, "qj2ge_chatLogSendBtn");
		chatLog.sendText();

		// Assert work log note
		assertEquals(true, woDetailPage.getWorkLogOnWoDetailsPage());

		woDetailPage.closeDrawer();
		woDetailPage.clickBackChevron();

		assignedWorkPage.clickClearButton();
	}

	@Test(enabled = true, groups = {
			"mobile" }, description = "Verify that tapping on My Schedule opens the WO List with Assigned work filter", timeOut = 500000)
	public void clickOnMySchedule() throws Exception {
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		AppSwitcher appSwitcher = new AppSwitcher(af);
		appSwitcher.switchApp(App.MySchedule);
// Verify WO List opens with "Assigned work" filter
		logger.info("Verify WO List opens with Assigned work filter");
		assertEquals("Assigned work", assignedWorkPage.getAssignedWorkFilterOnMySchedulePage());
	}

	@Test(enabled = true, groups = {
			"mobile" }, description = "Verify that tapping on Map opens the WO list with Assigned work filter in map view", timeOut = 500000)
	public void clickOnMap() throws Exception {

		if (!mapConfiguration)
			throw new SkipException("Skipping this test as Maximo Spatial Map is not configured");

		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage assignedWorkOrderDetailsPage = new WorkOrderDetailsPage(af);
		AppSwitcher appSwitcher = new AppSwitcher(af);

// Verify "Map" opens with "Assigned work" filter
		logger.info("Verify Map opens with Assigned work filter");
		appSwitcher.switchApp(App.Map);
		assertEquals("Assigned work", assignedWorkPage.getAssignedWorkFilterOnMapPage());
		if(af.isElementExists(By.id("qbm84"))) {
			assignedWorkOrderDetailsPage.clickButton("qbm84");
		}
		assertEquals(true, assignedWorkPage.verifySearchInputOnMap());

//Back to WO List
		assignedWorkPage.clickOnWOListIconFromMapView();
	}

	@Test(enabled = true, groups = {
			"mobile" }, description = "Verify that tapping on Materials & Tools opens the planned materials and tools list with Assigned work filter", timeOut = 500000)
	public void clickOnMaterialsAndTools() throws Exception {
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		AppSwitcher appSwitcher = new AppSwitcher(af);

// Verify "Materials & Tools" opens with "Assigned work" filter
		logger.info("Verify Materials & Tools opens with Assigned work filter");
		appSwitcher.switchApp(App.MaterialAndTools);
		assertEquals("Assigned work", assignedWorkPage.getAssignedWorkFilterOnMaterialsToolsPage());
		appSwitcher.switchApp(App.MySchedule);
	}

	@Test(enabled = true, groups = {
			"mobile" }, description = "Verify user can report an asset is down or up on WO details page", timeOut = 500000)
	public void checkAssetLookup() throws Exception {
// Search the work order
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage workOrderDetailsPage = new WorkOrderDetailsPage(af);
		EditWorkOrderPage editWODetailsPage = new EditWorkOrderPage(af);

		assignedWorkPage.search(woNum);
		assignedWorkPage.openWorkOrderDetails();
		logger.info("WO Details page displayed");
		workOrderDetailsPage.editWorkOrderDetails();
		String dynamicTitle = workOrderDetailsPage.getTitle(workOrderDetailsPage.woEditPageTitle);
		String expectedTitle = woNum + " Edit work order";
		assertEquals(expectedTitle, dynamicTitle);
		logger.info("Title asserted");
		editWODetailsPage.searchForAssets();
		logger.info("search selected");
		editWODetailsPage.resetFilterButtonClick();
		assertEquals("Location", editWODetailsPage.locationLabelText());
		assertEquals("Rotating Item", editWODetailsPage.rotatingItemText());
		assertEquals("Type", editWODetailsPage.typeLabelText());
		assertEquals("Vendor", editWODetailsPage.vendorTextMethod());
		assertTrue(editWODetailsPage.typeRecordSelectMethod());
		editWODetailsPage.filterPagebackButton();
		assertTrue(editWODetailsPage.isBadgeDisplayedforTypeNumber());
		editWODetailsPage.resetFilterButtonClick();
		editWODetailsPage.filterPagebackButton();
		workOrderDetailsPage.clickButton("MXlookup_modal_Page_Header_icon");
		editWODetailsPage.goBackToWODetailsPage();
		workOrderDetailsPage.clickBackChevronToListPage();
		assignedWorkPage.clickClearButton();
	}

	@Test(enabled = false, groups = { "mobile" }, description = "switchToAssetDetailsTest", timeOut = 900000)
	public void switchToAssetDetailsTest() throws Exception {
		// Open work order list page
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage assignedWorkOrderDetailsPage = new WorkOrderDetailsPage(af);
		AssetDetailsPage assetDetailsPage = new AssetDetailsPage(af);
		// check Asset App is loaded
		goToAssets(af);
		AssetListPage assetListPage = new AssetListPage(af);

		// Select Assets created by me option
		logger.info("Select Assets Created by Me option");
		assetListPage.openNativeDropdown(1);

		// reload asset list
		logger.info("reload asset list");
		assetListPage.clickRefreshButton();
		assetListPage.waitForLoadingNotVisible();

		// Search with asset number
		logger.info("Search with asset number : " + assetNum);
		boolean searchOutput = assetListPage.search(assetNum);

		if (!searchOutput)
			throw new SkipException("Skipping this test due to Asset is not found in Asset Application.");

		goToMySchedule(af);
		// Search the workorder
		assertTrue(assignedWorkPage.search(woNum), "Searched workorder was not found");
		// Click on Chevron Icon on Work Order
		assignedWorkPage.openWorkOrderDetails();
		boolean assetChevron = assignedWorkOrderDetailsPage.assetChevronDisplayed();
		try {
			if (assetChevron) {
				// Click on Asset chevron to navigate to asset details page
				assignedWorkOrderDetailsPage.clickAssetChevronButton();
				boolean checkAssetDetailPage = assetDetailsPage.checkAssetDetailPage();

				if (checkAssetDetailPage) {
					String assetDescription = SetupData.ASSET_DESCRIPTION + assetNum + assetNum;
					assertEquals(assetDetailsPage.getTextAssetDescription(), assetDescription);
					assertEquals("Asset Details", assetDetailsPage.getTitle(assetDetailsPage.pageTitle));
					// Navigate back to tech app to workorder details page
					assetDetailsPage.clickButton(assetDetailsPage.assetDetailBreadcrumb);
					String workorderDescription = SetupData.WORKORDER_DESCRIPTION + assetNum + WorkType.PM.toString()+ " " + woNum;
					assertEquals(WorkOrderDetailsPage.getTextWODescription(), workorderDescription);
					logger.info("Navigation back to work order details page in Technician app done correctly.");
					// Back to WO list view
					WorkOrderDetailsPage.clickBackWOList();
				} else {
					// Back to WO list view
					WorkOrderDetailsPage.clickBackWOList();
					assertTrue(assetChevron, "asset detail page is not Displayed");
				}
			} else {
				// Back to WO list view
				WorkOrderDetailsPage.clickBackWOList();
				assertTrue(assetChevron, "asset Chevron is not Displayed");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
// Logged bug- https://jsw.ibm.com/browse/MAXMOA-7448. Can be uncommented once resolved
	/*@Test(enabled = true, groups = {
			"priority2" }, description = "Verify that user can search for the completed Work order in the 'Work order history' filter for mobile", timeOut = 500000)
	public void verifySearchForCompletedWOInWOHistory() throws Exception {
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		MobileAutomationFramework maf = (MobileAutomationFramework) this.af;
		CreateWorkOrderPage createWO = new CreateWorkOrderPage(maf);

		// Open Work Order history from dropdown
		createWO.openNativeDropdown("Work order history");

		// Assert work order completed is found in "WorkOrder History"
		assertTrue(assignedWorkPage.search(woNum2));

		// Open Assigned Work from dropdown
		createWO.openNativeDropdown("Assigned work");
	}*/

	@Test(enabled = true, groups = {
			"priority2" }, description = "Search WO by Asset Number in a WO list", timeOut = 500000)
	public void aSearchForWorkOrderByAssetNumber() throws Exception {
		MySchedulePage assignedWorkPage = new MySchedulePage(af);

		// Verify user is able to search the work order with WORKORDER.ASSETNUM
		assertTrue(assignedWorkPage.search(assetNum));

		// Clear Search Result
		assignedWorkPage.clickClearButton();
	}

	@Test(enabled = true, groups = {
			"priority2" }, description = "Verify user can view and search in a WO list by WO status", timeOut = 500000)
	public void aSearchForWorkOrderByStatus() throws Exception {
		MySchedulePage assignedWorkPage = new MySchedulePage(af);

		// Verify user is able to search the work order with WORKORDER.STATUS
		assertTrue(assignedWorkPage.search(WoStatus.APPR.toString()));

		// Clear Search Result
		assignedWorkPage.clickClearButton();
	}

	@Test(enabled = true, groups = {
			"priority2" }, description = "Verify the work log entries in the \"Work log\" drawer on work order list and details pages & "
			+ "Verify Save/Discard Dialog Popup when close without saving changes", timeOut = 900000)
	public void checkWorkLog() throws Exception {
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkLogPage workLogPage = new WorkLogPage(af);

		//Scenario-2: Verify the work log entries in the \"Work log\" drawer on work order list and details pages
		logger.info("Verify the work log entries in the \\\"Work log\\\" drawer on work order list and details pages");

		// Search the work order
		assignedWorkPage.search(woNum1);

		// Verify the availability of "Work log" touch-point on the work order card list page
		assignedWorkPage.clickWorkLogTouchpoint();

		// Assert work log drawer header
		assertEquals("Work log", assignedWorkPage.getWorkLogDrawerHeader());

		// Add work log in the text area
		TextAreaComponent textArea = af.instantiateComponent(TextAreaComponent.class, "g7wpp_ChatLogTextInput");
		textArea.type(noteDescription1);

		// Verify that technician can enter work log entry using send key
		ChatLogComponent chatLog = af.instantiateComponent(ChatLogComponent.class, "g7wpp_chatLogSendBtn");
		chatLog.sendText();

		workLogPage.closeDrawer();
		assignedWorkPage.checkForUpdateButton();
		assignedWorkPage.clickWarningButton();
		assignedWorkPage.search(woNum1);
		
		assignedWorkPage.clickWorkLogTouchpoint();

		// Add work log in the text area
		TextAreaComponent textArea1 = af.instantiateComponent(TextAreaComponent.class, "g7wpp_ChatLogTextInput");
		textArea1.type(noteDescription2);

		// Verify that technician can enter work log entry using send key
		ChatLogComponent chatLog1 = af.instantiateComponent(ChatLogComponent.class, "g7wpp_chatLogSendBtn");
		chatLog1.sendText();

		workLogPage.closeDrawer();
		assignedWorkPage.checkForUpdateButton();
		assignedWorkPage.clickWarningButton();
		assignedWorkPage.search(woNum1);
		assignedWorkPage.clickWorkLogTouchpoint();

		// Check Top work chat log is old
		assertEquals(noteDescription1, workLogPage.getFirstWorkLogOnWoListPage());
		logger.info("work log label 1:" + workLogPage.getFirstWorkLogOnWoListPage());

		// Check Bottom work chat log is most recent chat log
		assertEquals(noteDescription2, workLogPage.getSecondWorkLogOnWoListPage());
		logger.info("work log label 2:" + workLogPage.getSecondWorkLogOnWoListPage());

		workLogPage.closeDrawer();
		assignedWorkPage.clickClearButton();

		// Scenario-6: : Verify the save/discard popup is displayed when technician clicks on back button without saving the data entered in the fields
		logger.info(
				": Verify the save/discard popup is displayed when technician clicks on back button without saving the data entered in the fields");

		// Search the work order
		assignedWorkPage.search(woNum1);

		// Verify the availability of "Work log" touch-point on the work order card list page
		assignedWorkPage.clickWorkLogTouchpoint();

		// Add work log in the text area
		TextAreaComponent textArea2 = af.instantiateComponent(TextAreaComponent.class, "g7wpp_ChatLogTextInput");
		textArea2.type(noteDescription1);

		// Verify "expand" icon/button is displayed on the "Work log" drawer
		workLogPage.clickExpandIcon();

		// enter summary
		workLogPage.noteSummary(noteSummaryText100);

		// enter long description
		workLogPage.enterLongDescription(longDescriptionText);

		//Click back button
		workLogPage.clickBackChevron();

		//Click on close drawer button
		workLogPage.closeDrawer();

		//Check if Save/Discard dialog popups up
		assertTrue(workLogPage.saveDiscardPopup());

		//Check save/discard popup text
		logger.info("Save/Discard Popup text : " + workLogPage.getSaveDiscardPopupText());
		assertEquals(saveDiscardText, workLogPage.getSaveDiscardPopupText());

		//Click Discard Button on Dialog
		workLogPage.clickDiscardButton();
		assignedWorkPage.clickClearButton();
	}

	// Purpose : switchToAssetDetailScenario25 is not depends on checkWorkLog but mark as depends to run after checkWorkLog
	@Test(enabled = true, dependsOnMethods = { "checkWorkLog" },groups = {
			"priority2" }, description = "Verify that technician navigates to Asset details in Asset manager app to view more information of work order asset when technician taps on right chevron displayed on \"Asset and location\" section on WO details", timeOut = 900000)
	public void switchToAssetDetailScenario25() throws Exception {

// Open work order list page
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		WorkOrderDetailsPage assignedWorkOrderDetailsPage = new WorkOrderDetailsPage(af);

//check Asset App is loaded on listing
		goToAssets(af);
		goToMySchedule(af);

// Search the workorder
		assertTrue(assignedWorkPage.search(woNum), "Work Order found");

// Click on Chevron Icon on Work Order
		assignedWorkPage.openWorkOrderDetails();

// Verify Asset chevron is displayed
		assertTrue(assignedWorkOrderDetailsPage.assetChevronDisplayed());
		logger.info("assetChevronDisplayed");
	}

	@Test(enabled = true, dependsOnMethods = { "switchToAssetDetailScenario25" }, groups = {
			"priority2" }, description = "Verify that right chevron is displayed on \"Asset and location\" section on WO details when asset details of primary asset associated with work order are already downloaded/available in mobile app and technician user has access to Asset manager app", timeOut = 9000000)
	public void switchToAssetDetailScenario26() throws Exception {

		// Open work order list page
		WorkOrderDetailsPage assignedWorkOrderDetailsPage = new WorkOrderDetailsPage(af);
		AssetDetailsPage assetDetailsPage = new AssetDetailsPage(af);

		// Verify Asset chevron is displayed
		assignedWorkOrderDetailsPage.clickAssetChevronButton();

		// Verify Asset description on asset manager application
		String assetDescription = SetupData.ASSET_DESCRIPTION + assetNum + assetNum;
		assertEquals(assetDescription, assetDetailsPage.getTextAssetDescription());
		assertEquals("Asset Details", assetDetailsPage.getTitle(assetDetailsPage.pageTitle));

		// Verify Asset status is Active
		assertEquals(assetDetailsPage.getTextAssetStatus().toLowerCase(),
				SetupData.LocAssetStatus.ACTIVE.toString().toLowerCase());
	}

	@Test(enabled = true, dependsOnMethods = { "switchToAssetDetailScenario26" }, groups = {
			"priority2" }, description = "Verify that technician navigates back to WO details of same work order in technician app when technician taps on back arrow/button on Asset details in Asset manager app", timeOut = 900000)
	public void switchToAssetDetailScenario27() throws Exception {

		// Open work order list page
		WorkOrderDetailsPage assignedWorkOrderDetailsPage = new WorkOrderDetailsPage(af);
		AssetDetailsPage assetDetailsPage = new AssetDetailsPage(af);

		// Click back button from asset manager application to technician
		assetDetailsPage.clickButton(assetDetailsPage.assetDetailBreadcrumb);

		// Verify navigation is successful by verifying header of WO Details page
		assertEquals("Work order", assignedWorkOrderDetailsPage.getWoDtlsWOHeader());

		// Verify user navigated to the correct workOrder
		String workOrderDescription = SetupData.WORKORDER_DESCRIPTION + assetNum + WorkType.PM.toString() + " " + woNum;
		assertEquals(workOrderDescription, assignedWorkOrderDetailsPage.getTextWODescriptionandNumber());
	}
	
	@Test(enabled = true, groups = {
	"mobile" }, description = "Search all records dropdown", timeOut = 500000)
	public void searchAllrecords() throws Exception {

		// Open work order list page
		MySchedulePage assignedWorkPage = new MySchedulePage(af);
		MobileAutomationFramework maf = (MobileAutomationFramework) this.af;
		WorkOrderDetailsPage woDetailsPage = new WorkOrderDetailsPage(af);
		// Search the work order
		CreateWorkOrderPage createWO = new CreateWorkOrderPage(maf);

		// Open Work Order history from dropdown
		createWO.openNativeDropdown("Search all records");
		assertTrue(assignedWorkPage.search(woNum), "Fail : Search Work Order is not displayed");
		// Open Assigned Work from dropdown
		createWO.openNativeDropdown("Assigned work");
		
	}

	protected void createDefaultObjects() throws Exception {
		try {
			logger.info("Creating default objects");

			// Create an asset
			logger.info("Creating an asset");
			assetNum = AbstractAutomationFramework.randomString(5).toUpperCase();
			String assetResult = maximoApi.retrieve(new Asset(),
					"addid=1&internalvalues=1&action=system:new&addschema=1");
			Asset newAsset = new Gson().fromJson(assetResult, Asset.class);
			newAsset.setAssetNum(assetNum);
			newAsset.setDescription(SetupData.ASSET_DESCRIPTION + assetNum);
			newAsset.setSiteId(SetupData.SITEID);
			newAsset.setStatus(SetupData.LocAssetStatus.ACTIVE.toString());
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

			// Add Tasks in WO
			logger.info("Task API Call started ");
			List<Task> arr = new ArrayList<Task>();
			Task task = new Task();
			Random rand = new Random();
			// Random float number
			task.setEstdur(rand.nextFloat() * (1000F - 1F));
			task.setSiteid(SetupData.SITEID);
			task.setDescription("Workorder for mobile automation test");
			task.setOwnergroup(SetupData.OWNERGROUP);
			task.setParentchgsstatus(true);
			// Random number between 0-999
			task.setTaskid(rand.nextInt(1000));
			task.setStatus(WoStatus.APPR.toString());
			arr.add(task);
			newWorkOrder.setWoactivity(arr);
			maximoApi.update(newWorkOrder);
			logger.info("Task added");

			// Assign the labor
			assertEquals(maximoApi.addAssignmentLabor(newWorkOrder, labor), SetupData.NoContentSuccess);
			logger.info("Assignment added");

			// Change WO status to Approved
			logger.info("Changing work order status to APPR");
			assertEquals(maximoApi.changeStatus(newWorkOrder, WoStatus.APPR.toString()), SetupData.NoContentSuccess);

			// Create a 2nd workorder
			logger.info("Creating Second work order");
			String workOrderResult1 = maximoApi.retrieve(new WorkOrder(),
					"addid=1&internalvalues=1&action=system:new&addschema=1");
			newWorkOrder1 = new Gson().fromJson(workOrderResult1, WorkOrder.class);
			newWorkOrder1.setDescription("WorkOrder for mobile automation test");
			maximoApi.create(newWorkOrder1);
			woNum1 = newWorkOrder1.getWoNum();
			logger.info("Work Order: " + newWorkOrder1.getWoNum());

			// Assign the labor
			maximoApi.addAssignmentLabor(newWorkOrder1, labor);
			logger.info("Assignment added");
			// Change WO status to Approved
			logger.info("Changing work order status to APPR");
			maximoApi.changeStatus(newWorkOrder1, WoStatus.APPR.toString());
			logger.info("WO Status Changed");

			// Create a 3rd workorder
			logger.info("Creating a work order 2");
			String workOrderResult2 = maximoApi.retrieve(new WorkOrder(),
					"addid=1&internalvalues=1&action=system:new&addschema=1");
			newWorkOrder1 = new Gson().fromJson(workOrderResult2, WorkOrder.class);
			newWorkOrder1.setDescription("WO Under History" + assetNum);
			newWorkOrder1.setSiteId(SetupData.SITEID);
			newWorkOrder1.setAssetNum(assetNum);
			newWorkOrder1.setWorkType(WorkType.PM.toString());
			assertEquals(maximoApi.create(newWorkOrder1), SetupData.CreatedSuccess);
			woNum2 = newWorkOrder1.getWoNum();
			logger.info("Work Order: {}" + woNum2);

			// Assign the labor
			assertEquals(maximoApi.addAssignmentLabor(newWorkOrder1, labor), SetupData.NoContentSuccess);
			logger.info("Assignment added");

			// Change WO status to Approved
			logger.info("Changing work order status to APPR");
			assertEquals(maximoApi.changeStatus(newWorkOrder1, WoStatus.APPR.toString()), SetupData.NoContentSuccess);

			logger.info("Changing work order status to COMP");
			assertEquals(maximoApi.changeStatus(newWorkOrder1, WoStatus.COMP.toString()), SetupData.NoContentSuccess);

			apiCodeSuccess = true;
		} catch (AssertionError e) {
			logger.info(" SkipException apiCodeSuccess = " + apiCodeSuccess);
			apiCodeSuccess = false;
			throw new Exception("Test Setup API Failed,Stopping execution.");
		}
	}
}