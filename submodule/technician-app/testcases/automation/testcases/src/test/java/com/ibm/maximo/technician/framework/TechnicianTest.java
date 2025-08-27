package com.ibm.maximo.technician.framework;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;

import com.google.gson.Gson;
import com.ibm.maximo.automation.mobile.AbstractAutomationFramework;
import com.ibm.maximo.automation.mobile.AppTest;
import com.ibm.maximo.automation.mobile.FrameworkFactory;
import com.ibm.maximo.automation.mobile.MobileAutomationFramework;
import com.ibm.maximo.automation.mobile.api.JdbcConnection;
import com.ibm.maximo.automation.mobile.api.MaximoApi;
import com.ibm.maximo.automation.mobile.api.json.Asset;
import com.ibm.maximo.automation.mobile.api.json.FailureCode;
import com.ibm.maximo.automation.mobile.api.json.FailureList;
import com.ibm.maximo.automation.mobile.api.json.WorkOrder;
import com.ibm.maximo.automation.mobile.common.AppSwitcher;
import com.ibm.maximo.automation.mobile.common.LoginFlow;
import com.ibm.maximo.automation.mobile.common.LogoutFlow;
import com.ibm.maximo.automation.mobile.common.AppSwitcher.App;
import com.ibm.maximo.automation.mobile.fvt.MobileSetupData;
import com.ibm.maximo.automation.mobile.page.MaximoUrlPage;
import com.ibm.maximo.automation.mobile.page.NavigatorPage;

import com.ibm.maximo.automation.mobile.page.ProfilePage;
import com.ibm.maximo.automation.mobile.page.WelcomePage;
import com.ibm.maximo.automation.mobile.page.login.LoginPage;
import com.ibm.maximo.components.ButtonComponent;
import com.ibm.maximo.technician.ConfigProperties;
import com.ibm.maximo.technician.setupdata.SetupData;

import io.appium.java_client.AppiumDriver;

public abstract class TechnicianTest extends AppTest {

	protected AppiumDriver<?> driver;
	private ConfigProperties config;
	protected TechnicianFramework tf;

	protected String assetNum;
	protected String woNum;
	private MaximoApi maximoApi;
	private final static Logger logger = LoggerFactory.getLogger(TechnicianTest.class);
	public AbstractAutomationFramework af;
	public static LoginPage lp;
	public static String masServer,testType;
	public static MobileSetupData mobileSetupData;
	private static String continueButtonLocator = "landingScreen_ContinueButton";

	public String failureCodeClass = AbstractAutomationFramework.randomString(5).toUpperCase(),
			failureCodeProblem = AbstractAutomationFramework.randomString(5).toUpperCase(),
			failureCodeCause = AbstractAutomationFramework.randomString(5).toUpperCase(),
			failureCodeRemedy = AbstractAutomationFramework.randomString(5).toUpperCase();
	public String descriptionFailureCodeClass = "Failure Code Class " + failureCodeClass,
			descriptionProblem = "problem " + failureCodeProblem, descriptionCause = "cause " + failureCodeCause,
			descriptionRemedy = "remedy " + failureCodeRemedy;
	public String failureCodeClass1 = AbstractAutomationFramework.randomString(5).toUpperCase(),
			failureCodeProblem1 = AbstractAutomationFramework.randomString(5).toUpperCase(),
			failureCodeCause1 = AbstractAutomationFramework.randomString(5).toUpperCase(),
			failureCodeRemedy1 = AbstractAutomationFramework.randomString(5).toUpperCase();
	public String descriptionFailureCodeClass1 = "Failure Code Class " + failureCodeClass1,
			descriptionProblem1 = "problem " + failureCodeProblem1, descriptionCause1 = "cause " + failureCodeCause1,
			descriptionRemedy1 = "remedy " + failureCodeRemedy1;

	public static JdbcConnection jdbcConnection;

	@BeforeSuite(alwaysRun = true)
	@Parameters({ "configPath" })
	public void setupForSuite(String configPath) throws Exception {
		Properties properties = new Properties();
		InputStream in = new BufferedInputStream(new FileInputStream(configPath));
		properties.load(in);
		masServer = properties.getProperty("system.masServer");
		testType = properties.getProperty("system.testType");

		String path1 = null;
		if (properties.containsKey("system.overrideSetupDataPath")) {
			String path = new File(properties.getProperty("system.overrideSetupDataPath")).getAbsolutePath();
			path1 = path.replace("\\", "\\\\");
			logger.info("path>>>>>>>>>>>>" + path1);
		}
		
		mobileSetupData = new MobileSetupData(path1);

		if (mobileSetupData.has("defaultURL") && mobileSetupData.has("defaultServerURL")
				&& mobileSetupData.has("username9") && mobileSetupData.has("password")
				&& mobileSetupData.has("user9ApiKey") && mobileSetupData.has("dbURL")
				&& mobileSetupData.has("dbDriver") && mobileSetupData.has("dbUser")
				&& mobileSetupData.has("dbPassword")) {
			OutputStream OutputStream = new BufferedOutputStream(new FileOutputStream(configPath));
			properties.setProperty("system.maximoServerUrl", mobileSetupData.data.get("defaultURL"));
			logger.info("system.maximoServerUrl>>>>>>>>>>>>" + properties.getProperty("system.maximoServerUrl"));
			properties.setProperty("system.serverUrl", mobileSetupData.data.get("defaultServerURL"));
			logger.info("system.serverUrl>>>>>>>>>>>>" + properties.getProperty("system.serverUrl"));
			properties.setProperty("system.username", mobileSetupData.data.get("username9"));
			logger.info("system.username>>>>>>>>>>>>" + properties.getProperty("system.username"));
			properties.setProperty("system.password", mobileSetupData.data.get("password"));
			logger.info("system.password>>>>>>>>>>>>" + properties.getProperty("system.password"));
			properties.setProperty("system.maximoAPIKey", mobileSetupData.data.get("user9ApiKey"));
			logger.info("system.maximoAPIKey>>>>>>>>>>>>" + properties.getProperty("system.maximoAPIKey"));
			// For FVT pipeline path1 is null where as if config file has system.overrideSetupDataPath then path1 is not null
			if (path1 != null && properties.containsKey("system.overrideSetupDataPath")) {
				properties.setProperty("system.maximoDBURL", mobileSetupData.data.get("dbURL"));
			}
			logger.info("system.maximoDBURL>>>>>>>>>>>>" + properties.getProperty("system.maximoDBURL"));
			properties.setProperty("system.maximoDBDriver", mobileSetupData.data.get("dbDriver"));
			logger.info("system.maximoDBDriver>>>>>>>>>>>>" + properties.getProperty("system.maximoDBDriver"));
			properties.setProperty("system.maximoDBUsername", mobileSetupData.data.get("dbUser"));
			logger.info("system.maximoDBUsername>>>>>>>>>>>>" + properties.getProperty("system.maximoDBUsername"));
			properties.setProperty("system.maximoDBPassword", mobileSetupData.data.get("dbPassword"));
			logger.info("system.maximoDBPassword>>>>>>>>>>>>" + properties.getProperty("system.maximoDBPassword"));
			properties.setProperty("app.username1", mobileSetupData.data.get("username11"));
			logger.info("app.username1>>>>>>>>>>>>" + properties.getProperty("app.username1"));
			properties.setProperty("app.password1", mobileSetupData.data.get("password"));
			logger.info("app.password1>>>>>>>>>>>>" + properties.getProperty("app.password1"));			
			properties.setProperty("app.username2", mobileSetupData.data.get("adminUser"));
			logger.info("app.username2>>>>>>>>>>>>" + properties.getProperty("app.username2"));
			properties.setProperty("app.password2", mobileSetupData.data.get("adminPassword"));
			logger.info("app.password2>>>>>>>>>>>>" + properties.getProperty("app.password2"));
			properties.store(OutputStream, "updated properties");
			OutputStream.close();
		}		
		
		// EAM env
		if(masServer.equals("false")) {
			maximoApi = new MaximoApi();
			logger.info("********************maximoAPIKey*********************************");
			maximoApi.setMaximoServer(properties.getProperty("system.maximoServerUrl"),
					"");
			String expirationStr = "{\"expiration\":-1}";	
			String maximoAPIKey = maximoApi.generateAPIKEY(expirationStr,properties.getProperty("system.username"),
					properties.getProperty("system.password"));
			logger.info("maximoAPIKey:" + maximoAPIKey);
			
			OutputStream OutputStream = new BufferedOutputStream(new FileOutputStream(configPath));		
			properties.setProperty("system.maximoAPIKey", maximoAPIKey);
			properties.store(OutputStream, "updated maximoAPIKey");
			OutputStream.close();
			maximoApi.setMaximoServer(properties.getProperty("system.maximoServerUrl"),properties.getProperty("system.maximoAPIKey"));
		}
		
		jdbcConnection = new JdbcConnection(properties.getProperty("system.maximoDBDriver"),
				properties.getProperty("system.maximoDBURL"), properties.getProperty("system.maximoDBUsername"),
				properties.getProperty("system.maximoDBPassword"));
		jdbcConnection.executeUpdateSQL("UPDATE MAXIMO.MAPMANAGER SET CARBONMAP='1' WHERE MAXIMO.MAPMANAGER.MAPNAME = '"+SetupData.MAPNAME+"'");

		// If Object Structure Mandate Asset Scan grant access is removed then skip asset scan button will be displayed in app
		ObjectStructureMandateAssetScan("Delete");
		// If Object Structure Mandate Asset Scan grant access is enabled then skip asset scan button will not be displayed in app
		//ObjectStructureMandateAssetScan("Insert");
		
		// IF Enable Assignment Flow has not granted access then Accept Reject for WO called FSM is not in operation. Which Needs Delete Query.
		ObjectStructureEnableAssignmentFlow("Delete");
	}
	
	@BeforeClass(alwaysRun = true)
	@Parameters({ "configPath" })
	public void baseSetup(String configPath) throws Exception {
		FrameworkFactory.get();
		Properties properties = new Properties();
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(configPath));
			properties.load(in);

			maximoApi = new MaximoApi();
			maximoApi.setMaximoServer(properties.getProperty("system.maximoServerUrl"),properties.getProperty("system.maximoAPIKey"));

			jdbcConnection = new JdbcConnection(properties.getProperty("system.maximoDBDriver"),
					properties.getProperty("system.maximoDBURL"), properties.getProperty("system.maximoDBUsername"),
					properties.getProperty("system.maximoDBPassword"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@AfterClass(alwaysRun = true)
	public void teardown() throws Exception {
		//jdbcConnection.executeUpdateSQL("UPDATE MAXIMO.WORKORDER SET STATUS = 'COMP' WHERE STATUS= 'APPR' OR STATUS= 'WAPPR' OR STATUS= 'INPRG' OR STATUS= 'CUSTOMCOMP'");
		jdbcConnection.teardown();
	}
	
	public static void woStatusToCompleted(){
		//jdbcConnection.executeUpdateSQL("UPDATE MAXIMO.WORKORDER SET STATUS = 'COMP' WHERE STATUS= 'APPR' OR STATUS= 'WAPPR' OR STATUS= 'INPRG' OR STATUS= 'CUSTOMCOMP'");
	}

	public WelcomePage loginIntoTechnician(String username, String password) throws Exception {
		LoginPage lp = LoginPage.getLoginPage(tf);
		lp.setUsername(username);
		lp.setPassword(password);
		return lp.login();
	}

	public LoginPage configureMaximoUrl() throws Exception {
		MaximoUrlPage mup = new MaximoUrlPage(tf);
		// Set the Maximo stub address
		mup.setMaximoUrl(config.getMaximoUrl());
		return mup.clickNextButton();
	}

	public void permission(AbstractAutomationFramework af) throws Exception {
		af.waitForElementToNotBePresent(By.cssSelector("#loading0_LongPress"), af.DEFAULT_TIMEOUT_MS * 5);
		if (af instanceof MobileAutomationFramework) {
			logger.info("is Mobile");
			try {
				((MobileAutomationFramework) af).allowAllPermission();
			} catch (Exception e) {
				// TODO: handle exception
				logger.info("Do not have permission check");
			}
		}
	}

	protected void createDefaultObjects() throws Exception {
		logger.info("Creating default objects");

		// Create an asset
		logger.info("Creating an asset");
		assetNum = AbstractAutomationFramework.randomString(5).toUpperCase();
		tf.maximoApi.create(Asset.fakeAsset(assetNum));
		logger.info("Asset: {}", assetNum);

		// Create a workorder
		logger.info("Creating a work order");
		woNum = AbstractAutomationFramework.randomString(5).toUpperCase();
		WorkOrder workOrder = WorkOrder.fakeWorkOrder(woNum, assetNum);
		workOrder.setWorkType("PM");
		tf.maximoApi.create(workOrder);
		logger.info("Work Order: {}", woNum);

		// Change WO status to Approved
		logger.info("Changing work order status to APPR");
		tf.maximoApi.changeStatus(workOrder, "APPR");

		List<Map<String, String>> assignment = new ArrayList<>();
		Map<String, String> assignmentItem = new Hashtable<>();

		// Assignment with labor SAM
		logger.info("Creating the assignment for the work order {}", woNum);
		assignmentItem.put("craft", "ELECT");
		assignmentItem.put("laborcode", "SAM");

		assignment.add(assignmentItem);

		Map<String, Object> a = new Hashtable<>();
		a.put("assignment", assignment);

		tf.maximoApi.addAssignment(workOrder, a);
		logger.info("Assignment added");
	}

	public void logOut(AbstractAutomationFramework framework) throws Exception {
		LogoutFlow logoutFlow = new LogoutFlow(framework);
		logoutFlow.logout();
	}
	
	public static boolean isLoaded(AbstractAutomationFramework af) {
		try {
			af.waitForElementToBePresent(By.id("p_mgw_linkContainer"),af.DEFAULT_TIMEOUT_MS);
			af.waitForElementToBePresent(By.cssSelector("#" + continueButtonLocator),af.DEFAULT_TIMEOUT_MS);
			return (af.isElementExists(By.cssSelector("#" + continueButtonLocator))&&
					af.isElementExists(By.id("p_mgw_linkContainer")));
		} catch (Exception e) {
			return false;
		}
	}	
	
	public void login(AbstractAutomationFramework framework) throws Exception {
		this.af = framework;
		if(isLoaded(af) || testType.equalsIgnoreCase("desktop")) {
			LoginFlow loginFlow = new LoginFlow(af);
			loginFlow.login();	
			goToMySchedule(af);
			permission(af);
		}
		else {	
			reLogin();
		}
	}
	
	// Relogin to enter same credentials
	protected void reLogin() throws Exception {
		logger.info("Login again");
		MobileAutomationFramework maf = (MobileAutomationFramework) this.af;
		lp = LoginPage.getLoginPage(maf);
		Properties props = this.af.getProperties();
		logger.info("Username: " + props.getProperty("system.username"));
		logger.info("Password: " + props.getProperty("system.password"));
		lp.loginDirectly(props.getProperty("system.username"), props.getProperty("system.password"));
		goToMySchedule(af);
	}
	
	// Relogin to enter different credentials
	public void reLoginWithDifferentCredentials() throws Exception {
		logger.info("ReLogin with different user");
		MobileAutomationFramework maf = (MobileAutomationFramework) this.af;
		lp = LoginPage.getLoginPage(maf);
		Properties props = this.af.getProperties();
		logger.info("Username " + props.getProperty("app.username1"));
		logger.info("Password " + props.getProperty("app.password1"));
		lp.loginDirectly(props.getProperty("app.username1"), props.getProperty("app.password1"));
		Thread.sleep(15000);
		goToMySchedule(af);
	}
	
	public void goToMySchedule(AbstractAutomationFramework af) throws Exception {
		AppSwitcher appSwitcher = new AppSwitcher(af);
		appSwitcher.switchApp(App.MySchedule);
	}

	/**
	 * Method to launch assets application
	 *
	 * @param af
	 * @throws Exception
	 */
	public void goToAssets(AbstractAutomationFramework af) throws Exception {
		AppSwitcher appSwitcher = new AppSwitcher(af);
		appSwitcher.switchApp(App.Assets);
		}

	/**
	 * Method to launch Inspections application
	 *
	 * @param af
	 * @throws Exception
	 */
	public void goToInspections(AbstractAutomationFramework af) throws Exception {
		AppSwitcher appSwitcher = new AppSwitcher(af);
		appSwitcher.switchApp(App.Inspections);
	}

	//To Update Full Data after checking navigator menu tabs are loaded
	public void updateData() throws Exception {
		String navigatorClick = "NavigatorMenuButton";
		String assetLink = "Assets";
		String scheduleLink = "My Schedule";
		String matandtools = "Materials & Tools";
		String maps = "Map";
		String approvals = "Approvals";
		String invCounting = "Inventory Counting";
		String invReceiving = "Inventory Receiving";
		String inspections = "Inspections";
		String issuesandTransfers = "Issues and Transfers";

		MobileAutomationFramework maf = (MobileAutomationFramework) this.af;
		maf.switchToParentFrame();

		logger.info("Go back to Navigator");
		af.instantiateComponent(ButtonComponent.class, navigatorClick).click();
		af.waitForElementToBePresent(By.xpath("//p[text()='" + scheduleLink + "']/../../../../div[@id='undefined_bl_slot_end']//div[@id='undefined_endBox2']//div"), af.DEFAULT_TIMEOUT_MS * 50);
		logger.info("My Schedule Page Loaded");
		af.waitForElementToBePresent(By.xpath("//p[text()='" + matandtools + "']/../../../../div[@id='undefined_bl_slot_end']//div[@id='undefined_endBox2']//div"), af.DEFAULT_TIMEOUT_MS * 50);
		logger.info("Mat and Tools Page Loaded");
		af.waitForElementToBePresent(By.xpath("//p[text()='" + maps + "']/../../../../div[@id='undefined_bl_slot_end']//div[@id='undefined_endBox2']//div"), af.DEFAULT_TIMEOUT_MS * 50);
		logger.info("Maps Page Loaded");
		af.waitForElementToBePresent(By.xpath("//p[text()='" + approvals + "']/../../../../div[@id='undefined_bl_slot_end']//div[@id='undefined_endBox2']//div"), af.DEFAULT_TIMEOUT_MS * 50);
		logger.info("Approvals Page Loaded");
		af.waitForElementToBePresent(By.xpath("//p[text()='" + invCounting + "']/../../../../div[@id='undefined_bl_slot_end']//div[@id='undefined_endBox2']//div"), af.DEFAULT_TIMEOUT_MS * 50);
		logger.info("Inv Counting Page Loaded");
		af.waitForElementToBePresent(By.xpath("//p[text()='" + invReceiving + "']/../../../../div[@id='undefined_bl_slot_end']//div[@id='undefined_endBox2']//div"), af.DEFAULT_TIMEOUT_MS * 50);
		logger.info("Inv Receiving Page Loaded");
		af.waitForElementToBePresent(By.xpath("//p[text()='" + inspections + "']/../../../../div[@id='undefined_bl_slot_end']//div[@id='undefined_endBox2']//div"), af.DEFAULT_TIMEOUT_MS * 50);
		logger.info("Inspections Page Loaded");
		af.waitForElementToBePresent(By.xpath("//p[text()='" + issuesandTransfers + "']/../../../../div[@id='undefined_bl_slot_end']//div[@id='undefined_endBox2']//div"), af.DEFAULT_TIMEOUT_MS * 50);
		logger.info("Issues and Transfers Page Loaded");
		logger.info("Waiting for asset to be loaded");
		af.waitForElementToBePresent(By.xpath("//p[text()='" + assetLink + "']/../../../../div[@id='undefined_bl_slot_end']//div[@id='undefined_endBox2']//div"), af.DEFAULT_TIMEOUT_MS * 50);
		logger.info("Asset Loaded");

		maf.updateAllData();
		maf.switchToParentFrame();
		maf.changeToDefaultContext();
		goToMySchedule(af);
	}
	
	// Generated by WCA for GP
	/**
	 * Sets the system properties of the authentication.
	 *
	 * @param value the value to set
	 */
	public void systemPropertiesOfAuth(String value) {
		jdbcConnection.executeUpdateSQL("UPDATE MAXIMO.MAXPROP SET MAXIMO.MAXPROP.LIVEREFRESH = '"+value+"' WHERE MAXIMO.MAXPROP.PROPNAME='mxe.int.enableosauth'");
		jdbcConnection.executeUpdateSQL("UPDATE MAXIMO.MAXPROP SET MAXIMO.MAXPROP.ONLINECHANGES = '"+value+"' WHERE MAXIMO.MAXPROP.PROPNAME='mxe.int.enableosauth'");
	}
	
	public void systemPropertiesOfStatusforphysicalsignature(String value) {
		jdbcConnection.executeUpdateSQL("UPDATE MAXIMO.MAXPROP SET MAXIMO.MAXPROP.LIVEREFRESH = '"+value+"' WHERE MAXIMO.MAXPROP.PROPNAME='maximo.mobile.statusforphysicalsignature'");
		jdbcConnection.executeUpdateSQL("UPDATE MAXIMO.MAXPROP SET MAXIMO.MAXPROP.ONLINECHANGES = '"+value+"' WHERE MAXIMO.MAXPROP.PROPNAME='maximo.mobile.statusforphysicalsignature'");
	}
	
	public void ObjectStructureMandateAssetScan(String value) { 
		String query = "SELECT COUNT(*) FROM MAXIMO.APPLICATIONAUTH WHERE GROUPNAME='"+ SetupData.SECURITYGROUP +"' AND APP ='MXAPIWODETAIL' AND OPTIONNAME = 'MANDATEASSETSCAN'";
		Object[] resultsObjects = jdbcConnection.executeSQL(query);
		Object[] resultArray1 = (Object[]) resultsObjects[0];
		boolean isRowExist = resultArray1[0].toString().equals("1");
		logger.info("ObjectStructureMandateAssetScan is Exist >"+isRowExist);
		if (isRowExist && value.equalsIgnoreCase("Delete")) {
			String deleteQuery = "DELETE  FROM MAXIMO.APPLICATIONAUTH WHERE GROUPNAME='"+ SetupData.SECURITYGROUP +"' AND APP ='MXAPIWODETAIL' AND OPTIONNAME = 'MANDATEASSETSCAN'";
			jdbcConnection.executeUpdateSQL(deleteQuery);
		}
		else if (value.equalsIgnoreCase("Insert")) {
			String insertQuery = "INSERT INTO MAXIMO.APPLICATIONAUTH (GROUPNAME, APP, OPTIONNAME, APPLICATIONAUTHID, CONDITIONNUM, ROWSTAMP) VALUES('"+ SetupData.SECURITYGROUP +"', 'MXAPIWODETAIL', 'MANDATEASSETSCAN', 0, '', 0)";
			jdbcConnection.executeUpdateSQL(insertQuery);
		}
	}
	
	// If Enable Assignment Flow has granted access then Accept Reject for WO called FSM is in opration. Which Needs Insert Query.
	// IF Enable Assignment Flow has not granted access then Accept Reject for WO called FSM is not in opration. Which Needs Delete Query.
	public void ObjectStructureEnableAssignmentFlow(String value) { 
		String query = "SELECT COUNT(*) FROM MAXIMO.APPLICATIONAUTH WHERE GROUPNAME='"+ SetupData.SECURITYGROUP +"' AND APP ='MXAPIWODETAIL' AND OPTIONNAME = 'MANAGEASSIGNMENTSTATUS'";
		Object[] resultsObjects = jdbcConnection.executeSQL(query);
		Object[] resultArray1 = (Object[]) resultsObjects[0];
		boolean isRowExist = resultArray1[0].toString().equals("1");
		logger.info("ObjectStructureEnableAssignmentFlow is Exist >"+isRowExist);
		if (isRowExist && value.equalsIgnoreCase("Delete")) {
			String deleteQuery = "DELETE  FROM MAXIMO.APPLICATIONAUTH WHERE GROUPNAME='"+ SetupData.SECURITYGROUP +"' AND APP ='MXAPIWODETAIL' AND OPTIONNAME = 'MANAGEASSIGNMENTSTATUS'";
			jdbcConnection.executeUpdateSQL(deleteQuery);
		}
		else if (value.equalsIgnoreCase("Insert")) {
			String insertQuery = "INSERT INTO MAXIMO.APPLICATIONAUTH (GROUPNAME, APP, OPTIONNAME, APPLICATIONAUTHID, CONDITIONNUM, ROWSTAMP) VALUES('"+ SetupData.SECURITYGROUP +"', 'MXAPIWODETAIL', 'MANAGEASSIGNMENTSTATUS', 0, '', 0)";
			jdbcConnection.executeUpdateSQL(insertQuery);
		}
	}
	
	// Generated by WCA for GP
		/**
		 * Checks if the map name exists in the Maximo Map Manager.
		 *
		 * @param mapName the map name to check
		 * @return true if the map name exists, false otherwise
		 * @throws Exception if an error occurs
		 */
		public static boolean mapNameFromMapManager(String mapName) {
			String query = "SELECT MAPNAME FROM MAXIMO.MAPMANAGER WHERE MAXIMO.MAPMANAGER.MAPNAME = '" + mapName + "'";
			logger.info(query);
			Object[] resultsObjects = jdbcConnection.executeSQL(query);
			Object[] resultArray1 = (Object[]) resultsObjects[0];
			String queryMapName = resultArray1[0].toString();
			logger.info("queryMapName from db>" + queryMapName);
			return queryMapName.equals(mapName);
		}	
	
	// Generated by WCA for GP
		/**
		 * This method creates a failure code and failure list for the specified failure
		 * code, description, parent, and type.
		 *
		 * @param failureCode The failure code.
		 * @param description The description of the failure code.
		 * @param parent      The parent of the failure list.
		 * @param type        The type of the failure list.
		 * @throws Exception If an error occurs.
		 */
		protected void createFailureCodeReturnFailureList(String failureCode, String description, String parent,
				String type) throws Exception {

			// Create a Failure Code
			logger.info("Creating a Failure Code");
			String failureCodeResult = maximoApi.retrieve(new FailureCode(),
					"addid=1&internalvalues=1&action=system:new&addschema=1");
			FailureCode newFailureCode = new Gson().fromJson(failureCodeResult, FailureCode.class);
			newFailureCode.setDescription(description);
			newFailureCode.setOrgId(SetupData.ORGID);
			newFailureCode.setFailureCode(failureCode);
			maximoApi.create(newFailureCode);
			logger.info("Failure Code Created: " + newFailureCode.getFailureCode());

			// Create a Failure list API
			logger.info("Creating a Failure List");
			String failureListResult = maximoApi.retrieve(new FailureList(),
					"addid=1&internalvalues=1&action=system:new&addschema=1");
			FailureList newFailureList = new Gson().fromJson(failureListResult, FailureList.class);
			newFailureList.setOrgId(SetupData.ORGID);
			newFailureList.setFailurecode(failureCode);
			if (parent != null) {
				newFailureList.setParent(parent);
			}
			if (type != null) {
				newFailureList.setType(type);
			}
			maximoApi.create(newFailureList);
			String failurelist = newFailureList.getFailurelist();
			logger.info("Failure list Created: " + failurelist);
		}

		// Generated by WCA for GP
			/**
			 * This method returns the failure list value for the specified failure code.
			 *
			 * @param failurecode The failure code.
			 * @return The failure list value for the specified failure code.
			 */	
		protected String failurelistQueryFromDB(String failurecode) {
			String query = "select f.FAILURELIST  from MAXIMO.FAILURELIST f WHERE FAILURECODE = '" + failurecode + "'";
			logger.info(query);
			Object[] resultsObjects = jdbcConnection.executeSQL(query);
			Object[] resultArray1 = (Object[]) resultsObjects[0];
			String failurelistValue = resultArray1[0].toString();
			logger.info("failurelistValue>" + failurelistValue);
			return failurelistValue;
		}

		// Generated by WCA for GP
		/**
		 * This method creates the failure code hierarchy.
		 *
		 * @throws Exception If there is a problem creating the failure code hierarchy.
		 */
		protected void failurecodeHierarchy() throws Exception {
			createFailureCodeReturnFailureList(failureCodeClass, descriptionFailureCodeClass, "", "");
			createFailureCodeReturnFailureList(failureCodeProblem, descriptionProblem,
					failurelistQueryFromDB(failureCodeClass), "PROBLEM");
			createFailureCodeReturnFailureList(failureCodeCause, descriptionCause,
					failurelistQueryFromDB(failureCodeProblem), "CAUSE");
			createFailureCodeReturnFailureList(failureCodeRemedy, descriptionRemedy,
					failurelistQueryFromDB(failureCodeCause), "REMEDY");
		}

		// Generated by WCA for GP
		/**
		 * This method creates the failure code hierarchy1.
		 *
		 * @throws Exception If there is a problem creating the failure code hierarchy1.
		 */
		protected void failurecodeHierarchy1() throws Exception {
			createFailureCodeReturnFailureList(failureCodeClass1, descriptionFailureCodeClass1, "", "");
			createFailureCodeReturnFailureList(failureCodeProblem1, descriptionProblem1,
					failurelistQueryFromDB(failureCodeClass1), "PROBLEM");
			createFailureCodeReturnFailureList(failureCodeCause1, descriptionCause1,
					failurelistQueryFromDB(failureCodeProblem1), "CAUSE");
			createFailureCodeReturnFailureList(failureCodeRemedy1, descriptionRemedy1,
					failurelistQueryFromDB(failureCodeCause1), "REMEDY");
		}
		
		public void takeScreenshot(String filename, AbstractAutomationFramework aff) throws IOException {
			TakesScreenshot screenshot = (TakesScreenshot) aff.getDriver();
			File source = screenshot.getScreenshotAs(OutputType.FILE);
			File file = new File("./Screenshots/" + filename + ".png");
			FileUtils.copyFile(source, file);
			System.setProperty(filename, file.getAbsolutePath());
			Reporter.log(filename);
			Reporter.log("<img src='" + file.getAbsoluteFile() + "' width='150' height='225' />");
		}

		/**
		 * Method to click checkbox of "Automatically change work order status to INPRG
		 * when a user starts a labor timer"
		 *
		 * @throws Exception
		 */
		public void checkStartTimerInprg(String value) {
			logger.info("Automatically change work order status to INPRG when a user starts a labor timer");
			jdbcConnection.executeUpdateSQL("UPDATE MAXIMO.MAXVARS SET MAXIMO.MAXVARS.VARVALUE = '" + value
					+ "' WHERE MAXIMO.MAXVARS.VARNAME = 'STARTTIMERINPRG'");
		}
	}
