package com.ibm.maximo.technician.page;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.maximo.automation.mobile.AbstractAutomationFramework;
import com.ibm.maximo.automation.mobile.MobileAutomationFramework;
import com.ibm.maximo.components.ButtonComponent;
import com.ibm.maximo.components.DataListComponent;
import com.ibm.maximo.components.DataListItemComponent;
import com.ibm.maximo.components.DialogComponent;
import com.ibm.maximo.components.FieldComponent;
import com.ibm.maximo.components.LabelComponent;
import com.ibm.maximo.components.NumberInputComponent;
import com.ibm.maximo.components.TextInputComponent;
import com.ibm.maximo.components.ToastComponent;
import com.ibm.maximo.technician.setupdata.SetupData.WoStatus;
import com.ibm.maximo.technician.setupdata.SetupData.WorkType;

public class ReportWorkPage {

	private AbstractAutomationFramework af;
	private final static Logger logger = LoggerFactory.getLogger(ReportWorkPage.class);
	private String editButton = "z7ej9";
	private String completeWorkOrder = "//button[contains(@id, 'gmzj8-pageheader_buttongroup_')]";
	private String reportWorkPage = "report_work_content";
	private String getClassValue = "b33g__textoverflow_value0";
	private String systemMsg = "graphite_dialog_error";
	private String closeSystemMsg = "graphite_dialog_error_close_button";
	private String toastMessage = "UINotificationContainer";
	private String addLabor = "ygndp";
	private String premiumPayCodeChevron = "nzz__";
	private String premiumPayCodeLabel = "bdr7x_label";
	private String premiumRateType = "bpmrd_fieldValue0";
	private String premiumPayRate = "jvwxm_fieldValue0";
	private String searchInputPremiumPayLookup = "premiumPayLookup_lookup_datalist_search_searchInput";
	private String searchButtonPremiumPay = "premiumPayLookup_lookup_datalist_search_searchButton";	
	private String selectPremiumPayCode = "premiumPayLookup_lookup_datalist_0_selectionCheckBoxIcon_touch";
	private String openLaborType = "nj99a";
	private String saveLabor = "zrbke_ngvdg";
	private String closeLaborDrawer = "reportTimeDrawer_button";
	private String editLabor = "wbbj9[0]";
	private String startTimeOfStartedLabor = "pq34x[0]_fieldValue0";
	private String hours = "regularHours_hour";
	private String minutes = "regularHours_minute";
	private String premiumPayHours = "Premiumpayhours_hour";
	private String premiumPayMinutes = "Premiumpayhours_minute";
	private String startTime = "startTime";
	private String laborName = "qez74[0]_fieldValue0";
	private String secondLaborName = "qez74[1]_fieldValue0";
	private String laborNameOnDrawer = "g5_4e_textoverflow_value0";
	private String craft = "b9em8_fieldValue0";
	private String skillLevel = "r83pw_fieldValue0";
	private String vendor = "mw3xr_fieldValue0";
	private String contract = "g246e_fieldValue0";
	private String laborStartDate = "x4qx8[0]";
	private String laborStartTime = "pq34x[1]_fieldValue0";
	private String laborEndDate = "z2k4w[1]";
	private String laborEndTime = "vv6jr[1]_fieldValue0";
	private String laborType = "z_463[1]_fieldValue0";
	private String laborTypeOnDrawer = "wdd_v_fieldValue0";
	private String laborHours = "nv2nk[1]_fieldValue0";
	private String laborTaskChevron = "egq9y";
	private String laborSelectionChevron = "e85dm";
	private String laborCheckmark = "laborLookup_lookup_datalist_search_searchButton";
	private String searchInputLabor = "laborLookup_lookup_datalist_search_searchInput";
	private String endTimeLabor = "endTime";
	private String endDateLabor = "endDate_dpi";

	private String materialsName = "nnqxy[0]_fieldValue0";
	private String toolsName = "nnxr4[0]_fieldValue0";
	private String startDateOnLaborDrawer = "z9er7";
	private String reportPageBackChevron = "gmzj8-pageheader_breadcrumb_icon";
	// Add materials
	private String addMaterialThreeDots = "nd44n";
	private String addItems = "nd44n_menu_menu_qrxpm";
	private String firstMaterial = "d76q3[0]";
	private String graphite_dialog_error_title = "graphite_dialog_error_title";
	private String graphite_dialog_error_close_button = "graphite_dialog_error_close_button";

	// Add tools
	private String actualLaborHour = "nv2nk[0]_fieldValue0";
	private String toolsAddButton = "pbvjm";
	private String toolAddedIndicator = "x8b42[0]";
	private String btn3DotsAtMaterialsUsed = "nd44n";
	private String btnGetSelected = "q7r2e-pageheader_buttongroup_rwgaq";
	private String reportCompleteWorkButtonOne = "gmzj8-pageheader_buttongroup_p6aav";
	private String reportCompleteWorkButtonTwo = "gmzj8-pageheader_buttongroup_kyz55";
	private String materialDescriptionField = "//div[@class='tooltipTarget']//p[contains(text(),'61503 Brake Shoe Kit')]";
	private String taskButton = "egq9y";
	public String laborPageHeader = "reportTimeDrawer_headerLabel";
	public String laborRecord="penzj[0]";
	String chevronId = "bam2r_items_datalistWrapper";
	public String laboorDropDown = "//div[@class='mx--datalist mx--childIndicator_touch'][1]";
	public String startDateTime = "zq76n[0]";
	public String endDateTime = "bak8p[0]";
	public String laborTransactionType = "z_463[0]";
	public String regularHours = "nv2nk[0]";
	public String dateChangeLocator ="startDate";
	private String saveDiscardHeader = "saveDiscardLaborsDialog_title";
	private String saveDiscardText = "v5zyx";
	private String saveDiscardPopupClose = "saveDiscardLaborsDialog_close_button";
	private String laborID ="ygndp";
	private String materialId ="nd44n";
	private String toolsId ="pbvjm";
	private String editlaborIcon ="eegyr[0]";
	private String deleteIcon ="wp_a6";
	private String laborNameXpath ="//p[contains(@id,'attr1_row_fieldValue0')]";
	private String noLabor ="bam2r_items_datalistWrapper";
	public String laborNameValue ;
	private String laborValues ="g5_4e_textoverflow_value0";
	private String laborCheckboxSelection="[id$=\'_selectionCheckBoxIcon_touch\']";
	private String getReservedItems="nd44n_menu_menu_nwgka";
	public String getReservednonRotatingItemsPageHeader= "m98ez";
	public String getNonRotatingItemNo="p5kke[0]";
	public String checkbxNonRotatingGetReservedItems=".mx--datalist-select-btn button";
	private String getNonRotatingItemsAdded = "d76q3[0]_fieldValue0";
	private String firstLaborChevron="//div[@id='mnnvy_slot_center']//div[@class='mx--col-item-wrapper'][1]//div[contains(@id,'childCaretContainer_touch')]";
	private String secondLaborChevron="//div[@id='mnnvy_slot_center']//div[@class='mx--col-item-wrapper'][2]//div[contains(@id,'childCaretContainer_touch')]";
	private String selectSpareAssetPart="nd44n_menu_menu_ywky7";
	public String getSelectAssetSparePartHeader="wgzgb-pageheader_title";
	private String btnConfirmSelectionOnSparePartsPage="wgzgb-pageheader_buttongroup_ky_dk";
	
	public ReportWorkPage(AbstractAutomationFramework af) {
		this.af = af;
	}

	/**
	 * Click failure edit button
	 * 
	 * @throws Exception
	 */
	public FailureReportingPage edit() throws Exception {
		af.instantiateComponent(ButtonComponent.class, editButton).click();
		return new FailureReportingPage(af);
	}

	/**
	 * Get failure class value
	 * 
	 * @throws Exception
	 */
	public String getFailureClass() throws Exception {
		af.waitForElementToBePresent(By.id(getClassValue),5);
		return af.instantiateComponent(LabelComponent.class, getClassValue).getValue();
	}

	/**
	 * Click Complete work button
	 * 
	 * @throws Exception
	 */
	public void completeWorkOrder() throws Exception {
		af.waitForElementToBePresent(By.xpath(completeWorkOrder), af.DEFAULT_TIMEOUT_MS * 5);
		if (af.isElementExists(By.xpath(completeWorkOrder))) {
			af.waitForElementToBePresent(By.xpath(completeWorkOrder), af.DEFAULT_TIMEOUT_MS * 5).click();
		} else {
			logger.info("Complete work order button not found possible ID changed");
			throw new Exception("Complete work order button not found possible ID changed");
		}
	}

	/**
	 * Click Complete work button
	 * 
	 * @throws Exception
	 */
	public void completeWorkOrderButton() throws Exception {
		af.waitForElementToBePresent(By.xpath(completeWorkOrder), 5000);
		af.instantiateComponent(ButtonComponent.class, completeWorkOrder).click();
	}

	/**
	 * Check page is report work page
	 * 
	 * @throws Exception
	 */
	public boolean checkCurrentPage() throws Exception {
		try {
			af.waitForElementToBePresent(By.id(reportWorkPage));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Click Complete work button when WORKTYPE.PROMPTDOWN is checked and physical
	 * signature property is set to COMP
	 * 
	 * @throws Exception
	 */
	public void completeWorkSignature() throws Exception {
		af.instantiateComponent(ButtonComponent.class, "gmzj8-pageheader_buttongroup_kyz55").click();
		af.waitForElementToNotBePresent(By.id("gmzj8-pageheader_buttongroup_kyz55"), 5000);
	}

	/**
	 * Check system message when user clicks on Complete work button and asset
	 * status is Down
	 * 
	 * @return String system message
	 * @throws Exception
	 */
	public String getSystemMsg() {
		DialogComponent dc = af.instantiateComponent(DialogComponent.class, systemMsg);
		return dc.getDialogContentText();
	}

	/**
	 * Click 'X' icon to close system message
	 * 
	 * @throws Exception
	 */
	public void closeSystemMsg() throws Exception {
		af.waitForElementToBePresent(By.id(closeSystemMsg), af.DEFAULT_TIMEOUT_MS * 5);
		af.instantiateComponent(ButtonComponent.class, closeSystemMsg).click();
	}

	/**
	 * Method to verify toast message
	 * 
	 * @return Toast Title
	 * @throws Exception
	 */
	public String toastMessageDisplayed() throws Exception {
		ToastComponent tc = af.instantiateComponent(ToastComponent.class, toastMessage);
		tc.waitToastPresent();
		logger.info("Toast Title:" + tc.getContentText());
		return tc.getContentText();
	}

	/**
	 * Click on '+' button after 'Tools used' to open 'Add tool' page
	 *
	 * @return AddToolPage
	 * @throws Exception
	 */
	public AddToolPage clickAddToolButton() throws Exception {
		af.waitForElementToBePresent(By.id(toolsAddButton));
		af.instantiateComponent(ButtonComponent.class, toolsAddButton).click();
		logger.info("Tools used + clicked");
		return new AddToolPage(af);
	}

	/**
	 * Click on '+' button to open add labor drawer
	 *
	 * @throws Exception
	 */
	public void clickPlusLaborButton() throws Exception {
		af.waitForElementToBePresent(By.id(addLabor), 5000);
		af.instantiateComponent(ButtonComponent.class, addLabor).click();
	}

	/**
	 * Click on PremiumPayCode Chevron button to open look up of premium pay code
	 *
	 * @throws Exception
	 */
	public void clickPremiumPayCodeChevron() throws Exception {
		af.waitForElementToBePresent(By.id(premiumPayCodeChevron), 5000);
		af.instantiateComponent(ButtonComponent.class, premiumPayCodeChevron).click();
	}
	
	/**
	 * Get on PremiumPayCode label
	 *
	 * @throws Exception
	 */
	public String getPremiumPayCodeLabel() throws Exception {
		af.waitForElementToBePresent(By.id(premiumPayCodeLabel), 5000);
		return af.instantiateComponent(LabelComponent.class, premiumPayCodeLabel).text();
	}
	
	/**
	 * Search and select PremiumPayCode from open look up of premium pay code
	 * @param premiumPayCode
	 * @throws Exception
	 */
	public void searchAndSelectPremiumPayCode(String premiumPayCode) throws Exception {

		af.waitForElementToBePresent(By.id(searchInputPremiumPayLookup), 5000);
		TextInputComponent tic = af.instantiateComponent(TextInputComponent.class, searchInputPremiumPayLookup);
		tic.setText(premiumPayCode);
		// click search button
		af.waitForElementToBePresent(By.id(searchButtonPremiumPay), 5000);
		ButtonComponent searchButton2 = af.instantiateComponent(ButtonComponent.class, searchButtonPremiumPay);
		searchButton2.click();
		
		af.waitForElementToBePresent(By.id(selectPremiumPayCode), 5000);
		af.instantiateComponent(ButtonComponent.class, selectPremiumPayCode).click();
	}
	
	/**
	 * Get on PremiumPayRate
	 *
	 * @throws Exception
	 */
	public String getPremiumPayRate() throws Exception {
		af.waitForElementToBePresent(By.id(premiumPayRate), 5000);
		return af.instantiateComponent(LabelComponent.class, premiumPayRate).text();
	}
	
	/**
	 * Get on PremiumPayType
	 *
	 * @throws Exception
	 */
	public String getPremiumRateType() throws Exception {
		af.waitForElementToBePresent(By.id(premiumRateType), 5000);
		return af.instantiateComponent(LabelComponent.class, premiumRateType).text();
	}
	
	/**
	 * enter PremiumPay hours
	 * @param hrs
	 * @param min
	 * @throws Exception
	 */
	public void enterPremiumPayHours(String hrs, String min) throws Exception {
		af.instantiateComponent(NumberInputComponent.class, premiumPayHours).typeAndSendDurationHourKey(hrs, Keys.TAB);
		af.instantiateComponent(NumberInputComponent.class, premiumPayMinutes).typeAndSendDurationMinuteKey(min, Keys.TAB);
	}
	
	/**
	 * Change labor type
	 * 
	 * @throws Exception
	 */
	public void changeLaborType(WoStatus type) throws Exception {
		af.instantiateComponent(ButtonComponent.class, openLaborType).click();
		af.instantiateComponent(ButtonComponent.class,
				"transTypeLookup_lookup_datalist_LTTYPE|" + type + "_selectionCheckBoxIcon_touch").click();
	}

	/**
	 * Click blue checkmark to save labor record
	 *
	 * @throws Exception
	 */
	public void saveLabor() throws Exception {
		af.instantiateComponent(ButtonComponent.class, saveLabor).click();
		af.waitForElementToNotBePresent(By.id(saveLabor), 5000);
	}

	/**
	 * Click on 'X' to close labor drawer
	 *
	 * @throws Exception
	 */
	public void closeLaborDrawer() throws Exception {
		af.waitForElementToBePresent(By.id(closeLaborDrawer), 1000);
		af.instantiateComponent(ButtonComponent.class, closeLaborDrawer).click();
	}

	/**
	 * Click pencil button to edit labor record
	 *
	 * @throws Exception
	 */
	public void editLabor() throws Exception {
		af.instantiateComponent(ButtonComponent.class, editLabor).click();
	}

	/**
	 * Get start time of started labor transaction
	 * 
	 * @return start time of started labor
	 * @throws Exception
	 */
	public String getStartTime() throws Exception {
		return af.instantiateComponent(LabelComponent.class, startTimeOfStartedLabor).getValue();
	}

	/**
	 * Enter regular hours
	 *
	 * @param hrs
	 * @param min
	 * @throws Exception
	 */
	public void regularHours(String hrs, String min) throws Exception {
		af.instantiateComponent(NumberInputComponent.class, hours).typeAndSendDurationHourKey(hrs, Keys.TAB);
		af.instantiateComponent(NumberInputComponent.class, minutes).typeAndSendDurationMinuteKey(min, Keys.TAB);
	}

	/**
	 * Enter start time on labor drawer
	 *
	 * @param hrs
	 * @param min
	 * @throws Exception
	 */
	public void startTime(int hrs, int min, boolean value) throws Exception {
		((MobileAutomationFramework) af).setTime(By.id(startTime), hrs, min, value);
	}

	/**
	 * Get labor name on saved labor record
	 *
	 * @return String labor name
	 * @throws Exception 
	 */
	public String getLaborName() throws Exception {
		af.waitForElementToBePresent(By.id(laborName), af.DEFAULT_TIMEOUT_MS);
		return af.instantiateComponent(LabelComponent.class, laborName).getLabel();
	}

	/**
	 * Get labor name on saved labor record
	 * 
	 * @return String labor name
	 */
	public String getSecondLaborName() {
		return af.instantiateComponent(LabelComponent.class, secondLaborName).getLabel();
	}

	/**
	 * Is 2nd labor name existing
	 *
	 * @return String labor name
	 */
	public boolean is2ndLaborExisting() {
		try {
			af.waitForElementToBePresent(By.id(secondLaborName), af.DEFAULT_TIMEOUT_MS);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Get labor name on labor drawer
	 *
	 * @return String labor name
	 */
	public String getLaborNameOnDrawer() {
		return af.instantiateComponent(LabelComponent.class, laborNameOnDrawer).getLabel();
	}

	/**
	 * Get craft on labor drawer
	 *
	 * @return String craft value
	 */
	public String getLaborCraftOnDrawer() {
		return af.instantiateComponent(LabelComponent.class, craft).getValue();
	}

	/**
	 * Get skill level on labor drawer
	 *
	 * @return String skill level value
	 */
	public String getLaborSkillOnDrawer() {
		return af.instantiateComponent(LabelComponent.class, skillLevel).getValue();
	}
	
	/**
	 * Get Vendor on labor drawer
	 *
	 * @return String Vendor level value
	 */
	public String getLaborVendorOnDrawer() {
		return af.instantiateComponent(LabelComponent.class, vendor).getValue();
	}
	
	/**
	 * Get Contract on labor drawer
	 *
	 * @return String Contract level value
	 */
	public String getLaborContractOnDrawer() {
		return af.instantiateComponent(LabelComponent.class, contract).getValue();
	}
	
	/**
	 * Get labor start date on saved labor record
	 *
	 * @return String labor start date
	 */
	public String getLaborStartDate() {
		return af.instantiateComponent(LabelComponent.class, laborStartDate).getValue();
	}

	/**
	 * Get labor start time on saved labor record
	 *
	 * @return String labor start time
	 */
	public String getLaborStartTime() throws Exception {
		return af.instantiateComponent(LabelComponent.class, laborStartTime).getValue();
	}

	/**
	 * Get labor end date on saved labor record
	 *
	 * @return String labor end date
	 */
	public String getLaborEndDate() {
		return af.instantiateComponent(LabelComponent.class, laborEndDate).getValue();
	}

	/**
	 * Get labor end time on labor record
	 *
	 * @return String labor end time
	 */
	public String getLaborEndTime() {
		return af.instantiateComponent(LabelComponent.class, laborEndTime).getValue();
	}

	/**
	 * Get labor type on saved labor record
	 *
	 * @return String labor type
	 */
	public String getLaborType() {
		return af.instantiateComponent(LabelComponent.class, laborType).getValue();
	}

	/**
	 * Get labor type on labor drawer
	 *
	 * @return String labor type
	 */
	public String getLaborTypeOnDrawer() {
		return af.instantiateComponent(LabelComponent.class, laborTypeOnDrawer).getValue();
	}

	/**
	 * Get labor time duration on saved labor record
	 *
	 * @return String labor time duration
	 */
	public String getLaborHours() {
		return af.instantiateComponent(LabelComponent.class, laborHours).getValue();
	}

	/**
	 * Get labor start date is auto-filled when user opens add labor drawer
	 *
	 * @return String labor start date
	 */
	public String getStartDate() {
		return af.instantiateComponent(FieldComponent.class, startDateOnLaborDrawer).getValue();
	}

	/**
	 * Click labor chevron to expand
	 *
	 * @param chevronId
	 * @throws Exception
	 */
	public void laborChevron(String chevronId) throws Exception {
		af.waitForElementToBePresent(By.id(chevronId), af.DEFAULT_TIMEOUT_MS * 5);
		DataListComponent dl1 = af.instantiateComponent(DataListComponent.class, chevronId);
		List<DataListItemComponent> list = dl1.getChildrenTouch();
		DataListItemComponent item1 = list.get(0);
		item1.clickCaretTouch();
	}

	/**
	 * Click back chevron to navigate to wo details page
	 *
	 * @throws Exception
	 */
	public void clickBackChevron() throws Exception {
		af.instantiateComponent(ButtonComponent.class, reportPageBackChevron).click();
	}

	/**
	 * Get Materials Name on Used Materials record
	 * 
	 * @return String Materials Name
	 * @throws Exception 
	 */
	public String getMaterialName() throws Exception {
		af.scrollPage(1, 2);
		WebElement materialNameEle = af.waitForElementToBePresent(By.id(materialsName));
		af.scrollToElement(materialNameEle);
		return af.instantiateComponent(LabelComponent.class, materialsName).getValue();

	}

	/**
	 * Get Tools Name on Used Tools record
	 * 
	 * @return String Tools Name
	 * @throws Exception 
	 */
	public String getToolsName() throws Exception {
		WebElement toolsNameEle = af.waitForElementToBePresent(By.id(toolsName));
		af.scrollToElement(toolsNameEle);
		return af.instantiateComponent(LabelComponent.class, toolsName).getValue();
	}

	/**
	 * Click on Chevron icon to open First labor reported actual time
	 *
	 * @throws Exception
	 */

	public void firstLaborChevron(String chevronId) throws Exception {
		af.waitForElementToBePresent(By.id(chevronId), af.DEFAULT_TIMEOUT_MS * 5);
		DataListComponent dl1 = af.instantiateComponent(DataListComponent.class, chevronId);
		List<DataListItemComponent> list = dl1.getChildrenTouch();
		DataListItemComponent item1 = list.get(0);
		item1.clickCaretTouch();
	}

	/**
	 * click on Chevron icon to open second labor reported actual time
	 *
	 * @throws Exception
	 */

	public void secondLaborChevron(String chevronId) throws Exception {
		af.waitForElementToBePresent(By.id(chevronId), af.DEFAULT_TIMEOUT_MS * 5);
		DataListComponent dl1 = af.instantiateComponent(DataListComponent.class, chevronId);
		List<DataListItemComponent> list = dl1.getChildrenTouch();
		DataListItemComponent item1 = list.get(1);
		item1.clickCaretTouch();
	}
	/**
	 * Click on Chevron icon to open First labor reported actual time
	 *
	 * @throws Exception
	 */
	public void firstLaborChevronForActualTools() throws Exception {
		af.waitForElementToBePresent(By.xpath(firstLaborChevron), af.DEFAULT_TIMEOUT_MS * 5).click();

	}
	/**
	 * click on Chevron icon to open second labor reported actual time
	 *
	 * @throws Exception
	 */
	public void secondLaborChevronForActualTools() throws Exception {
		af.waitForElementToBePresent(By.xpath(secondLaborChevron), af.DEFAULT_TIMEOUT_MS * 5).click();
	}
	/**
	 * Get First Labor actual reported work hour
	 *
	 * @return
	 * @throws Exception 
	 */
	public String getActualLaborhours() throws Exception {
		WebElement repeatableSetPointInputEle = af.waitForElementToBePresent(By.id(actualLaborHour));
		af.scrollToElement(repeatableSetPointInputEle);
		return af.instantiateComponent(LabelComponent.class, actualLaborHour).getValue();
	}

	/**
	 * Method to verify Complete work button is disabled or enabled
	 * 
	 * @return boolean
	 * @throws Exception
	 */

	public boolean verifyCompleteWork() throws Exception {
		try {
			af.waitForElementToBeEnabled(By.id(completeWorkOrder));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Click Three Dots to add Material
	 *
	 * @throws Exception
	 */
	public void addMaterialThreeDots() throws Exception {
		af.instantiateComponent(ButtonComponent.class, addMaterialThreeDots).click();
	}

	/**
	 * Click Add Items
	 *
	 * @throws Exception
	 */
	public void addItems() throws Exception {
		af.waitForElementToBePresent(By.id(addItems), af.DEFAULT_TIMEOUT_MS * 5);
		af.instantiateComponent(ButtonComponent.class, addItems).click();
	}

	/**
	 * Method to verify any tool is added or not
	 * 
	 * @return boolean
	 * @throws Exception
	 */
	public boolean verifyToolAdded() throws Exception {
		try {
			af.waitForElementToBePresent(By.id(toolAddedIndicator));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Method to verify any material is added or not
	 * 
	 * @return boolean
	 * @throws Exception
	 */
	public boolean verifyMaterialAdded() throws Exception {
		try {
			af.waitForElementToBePresent(By.id(firstMaterial));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Display error message and return
	 * 
	 * @return boolean whether successful
	 * @throws Exception
	 */
	public boolean showErrorPageAndReturn() throws Exception {
		try {
			af.waitForElementToBePresent(By.id(graphite_dialog_error_title));
			af.instantiateComponent(ButtonComponent.class, graphite_dialog_error_close_button).click();
			return true;
		} catch (Exception e) {
			logger.info("Failed: during error page pops up");
			return false;
		}
	}

	/**
	 * Click on 3 dots at materials used
	 *
	 * @throws Exception
	 */
	public void clickOn3Dotsbutton() throws Exception {
		af.instantiateComponent(ButtonComponent.class, btn3DotsAtMaterialsUsed).click();
		af.waitForElementToBePresent(By.id(btn3DotsAtMaterialsUsed), 5000);
	}

	/**
	 * Method to verify Get Reserved items is disabled or enabled
	 *
	 * @return boolean
	 * @throws Exception
	 */
	public boolean verifyGetReservedItemsDisabled() throws Exception {
		try {
			af.waitForElementToBeEnabled(By.id(btnGetSelected));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Check in Labor pop up, whether Task field is available
	 *
	 * @return
	 */
	public boolean isTaskExistInLabor() {
		return af.isElementExists(By.id(laborTaskChevron));
	}

	/**
	 * Check if Created Material is populating in Report Work Page
	 *
	 * @return
	 * @throws Exception
	 */
	public boolean isMaterialExist(String option) throws Exception {
		materialDescriptionField = (("//div[@class='mx--datalist-flex']//p[contains(text()," + "'") + option
				+ ("'" + ")]"));
		af.waitForElementToBePresent(By.xpath(materialDescriptionField), 3000);
		return af.isElementExists(By.xpath(materialDescriptionField));
	}

	/**
	 * In Labor pop up, select a new labor and return
	 *
	 * @param newLabor
	 * @throws Exception
	 */
	public void changeLabor(String newLabor, String newLaborId) throws Exception {
		// click labor search chevron
		af.waitForElementToBePresent(By.id(laborSelectionChevron));
		af.instantiateComponent(ButtonComponent.class, laborSelectionChevron).click();

		// input in text
		af.waitForElementToBePresent(By.id(searchInputLabor));
		TextInputComponent tic = af.instantiateComponent(TextInputComponent.class, searchInputLabor);
		tic.setText(newLabor);
		logger.info("Search value inputed");

		// click search button
		af.waitForElementToBePresent(By.id(laborCheckmark), af.DEFAULT_TIMEOUT_MS * 30);
		ButtonComponent searchButton2 = af.instantiateComponent(ButtonComponent.class, laborCheckmark);
		searchButton2.click();
		String newLaborIdLocation = "laborLookup_lookup_datalist_" + newLaborId + "_selectionCheckBoxIcon_touch";
		af.waitForElementToBePresent(By.id(newLaborIdLocation), 3000);

		// select row to return
		ButtonComponent searchButton3 = af.instantiateComponent(ButtonComponent.class, newLaborIdLocation);
		searchButton3.click();
	}
	
	/**
	 * In Labor pop up, select a new labor and return
	 *
	 * @param newLabor
	 * @throws Exception
	 */
	public String getLaborName(String newLabor, String newLaborId) throws Exception {
		// click labor search chevron
		af.waitForElementToBePresent(By.id(laborSelectionChevron));
		af.instantiateComponent(ButtonComponent.class, laborSelectionChevron).click();

		// input in text
		af.waitForElementToBePresent(By.id(searchInputLabor));
		TextInputComponent tic = af.instantiateComponent(TextInputComponent.class, searchInputLabor);
		tic.setText(newLabor);
		logger.info("Search value inputed");

		// click search button
		af.waitForElementToBePresent(By.id(laborCheckmark), af.DEFAULT_TIMEOUT_MS * 30);
		ButtonComponent searchButton2 = af.instantiateComponent(ButtonComponent.class, laborCheckmark);
		searchButton2.click();
		String newLaborIdLocation = "laborLookup_lookup_datalist_" + newLaborId + "_selectionCheckBoxIcon_touch";
		af.waitForElementToBePresent(By.id(newLaborIdLocation), 3000);
		return laborNameValue =af.waitForElementToBePresent(By.xpath(laborNameXpath), 2000).getText();
		
	}


	/**
	 * Method to save button is enabled or disabled
	 * 
	 * @return boolean
	 * @throws Exception
	 */
	public boolean verifySaveButtonEnabled() throws Exception {
		try {
			af.waitForElementToBePresent(By.id(completeWorkOrder), af.DEFAULT_TIMEOUT_MS * 3);
			af.waitForElementToBeEnabled(By.id(completeWorkOrder));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Method to verify if end date exists
	 * 
	 * @return
	 */
	public boolean isDateExist() {
		return af.isElementExists(By.id(endDateLabor));
	}

	/**
	 * Method to verify if end time exists
	 * 
	 * @return
	 */
	public boolean isTimeExist() {
		return af.isElementExists(By.id(endTimeLabor));
	}

	public void completeReportWorkButton() throws Exception {
		af.waitForElementToBePresent(By.xpath(completeWorkOrder), 3000);
		if (af.isElementExists(By.id(reportCompleteWorkButtonOne))) {
			af.instantiateComponent(ButtonComponent.class, reportCompleteWorkButtonOne).click();
		} else if (af.isElementExists(By.id(reportCompleteWorkButtonTwo))) {
			af.instantiateComponent(ButtonComponent.class, reportCompleteWorkButtonTwo).click();
		} else {
			logger.info("Complete work order button not found possible ID changed");
			throw new Exception("Complete work order button not found possible ID changed");
		}
	}

	/**
	 * Method to verify if system message appears
	 * 
	 * @return
	 */
	public boolean waitForSystemMsgDialog() throws Exception {
		try {
			af.waitForElementToNotBePresent(By.id(systemMsg), 5000);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Method to click on task button
	 * 
	 * @throws Exception
	 */
	public void clickTaskButton() throws Exception {
		af.waitForElementToBePresent(By.id(taskButton), 3000);
		af.instantiateComponent(ButtonComponent.class, taskButton).click();
	}

	/**
	 * This is generic getTitle method to get string value of element
	 *
	 * @param elementID {string} value of id to get text value of it
	 * @throws Exception
	 */
	public String getTitle(String elementID) throws Exception {
		af.waitForElementToBePresent(By.id(elementID), af.DEFAULT_TIMEOUT_MS * 10);
		String pageTitle = af.instantiateComponent(LabelComponent.class, elementID).text();
		logger.info("pageTitle:" + pageTitle);
		return pageTitle;
	}
	
	// Generated by WCA for GP
	/**
	 * Method to verify labor exists
	 * 
	 * @return
	 */
	public boolean isLaborRecordExists() {
		return af.isElementExists(By.id(laborRecord));
	}

	// Generated by WCA for GP
	/**
	 * Method to expand chevron
	 * 
	 * @throws Exception
	 */
	public void laborChevronExpand() throws Exception {
		af.waitForElementToBePresent(By.id(chevronId), af.DEFAULT_TIMEOUT_MS * 5);
		DataListComponent dl1 = af.instantiateComponent(DataListComponent.class, chevronId);
		List<DataListItemComponent> list = dl1.getChildrenTouch();
		DataListItemComponent item1 = list.get(0);
		item1.clickCaretTouch();
	}

	// Generated by WCA for GP
	/**
	 * Method to click Labor Details drop down
	 * 
	 * @throws Exception
	 */
	public void clickLaborDetailsDropDown() throws Exception {
		af.waitForElementToBePresent(By.xpath(laboorDropDown), 5000).click();
	}

    // Generated by WCA for GP
	/**
	 * Method to verify start date and time exist
	 * 
	 * @throws Exception
	 */
	public boolean isStartDateTimeExist() throws Exception {
		af.waitForElementToBePresent(By.id(startDateTime), 5000);
		return af.isElementExists(By.id(startDateTime));
	}
	
	// Generated by WCA for GP
	/**
	 * Method to verify end data and time exist
	 * 
	 * @throws Exception
	 */
	public boolean isEndDateTimeExist() {
		return af.isElementExists(By.id(endDateTime));
	}
	
	// Generated by WCA for GP
	/**
	 * Method to verify Labor transaction type exist
	 * 
	 * @throws Exception
	 */
	public boolean isLaborTransactionTypeExist() {
		return af.isElementExists(By.id(laborTransactionType));
	}
	
	// Generated by WCA for GP
	/**
	 * Method to verify Regular hour exist
	 * 
	 * @throws Exception
	 */
	public boolean isRegularHourExist() {
		return af.isElementExists(By.id(regularHours));
	}
	
	// Generated by WCA for GP
	/**
	 * Method to enter date
	 * 
	 * @param year
	 * @param month
	 * @param date
	 * @throws Exception
	 */
	public void enterDate(int year, int month, int date) throws Exception {
		af.waitForElementToBePresent(By.id(dateChangeLocator), 2);
		((MobileAutomationFramework) af).setDate(By.id(dateChangeLocator), year, month, date);
	}
	
	// Generated by WCA for GP
	/**
	 * Method to get header text
	 * 
	 * @return
	 */
	public String getSaveDiscardHeader() {
		return af.instantiateComponent(ButtonComponent.class, saveDiscardHeader).getText();
	}
	
	// Generated by WCA for GP
	/**
	 * Method to get save/discard text
	 * 
	 * @return
	 */
	public String getSaveDiscardText() {
		return af.instantiateComponent(ButtonComponent.class, saveDiscardText).getText();
	}
	
	// Generated by WCA for GP
	/**
	 * Method to click discard button
	 * 
	 */
	public void discardButtonClick() {
	af.instantiateComponent(ButtonComponent.class, saveDiscardPopupClose).click();
	}
	
	// Generated by WCA for GP
	/**
	 * Verify labor button exists
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean isLaborButtonPresent() throws Exception {
		return af.isElementExists(By.id(laborID));
	}
	
	// Generated by WCA for GP
	/**
	 * Verify Materials button exists
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean isMaterialButtonPresent() throws Exception {
		return af.isElementExists(By.id(materialId));
	}
	
	// Generated by WCA for GP
	/**
	 * Verify Tools button exists
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean isToolsButtonPresent() throws Exception {
		return af.isElementExists(By.id(toolsId));
	}
	
	/**
	 * Method to get Labor text when no labor is added
	 * 
	 * @return
	 */
	public String getNoLaborText() {
		return af.instantiateComponent(ButtonComponent.class, noLabor).getText();
	}
	
	/**
	 * Method to click edit icon in labor section
	 * 
	 * @throws Exception
	 */
	public void clickEditIcon() throws Exception {
		af.instantiateComponent(ButtonComponent.class, editlaborIcon).click();
	}

	/**
	 * Method to click on delete icon
	 * 
	 * @throws Exception
	 */
	public void clickDeleteIcon() throws Exception {
		af.instantiateComponent(ButtonComponent.class, deleteIcon).click();
	}
		
	// Generated by WCA for GP
	/**
	 * Method to verify edit button exists or not exists
	 * 
	 * @return
	 */
	public boolean isEditLaborButtonPresent() {
		try {
			af.waitForElementToBeEnabled(By.id(editlaborIcon), 5);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	// Assisted by watsonx Code Assistant 
	/**
	 * Method to select new labor
	 * 
	 * @throws Exception
	 */
	public void selectNewLabor() throws Exception {
		// click labor search chevron
		af.waitForElementToBePresent(By.id(laborSelectionChevron));
		af.instantiateComponent(ButtonComponent.class, laborSelectionChevron).click();
		af.waitForElementToBePresent(By.cssSelector(laborCheckboxSelection), 2000).click();
	}

	// Assisted by watsonx Code Assistant 
	/**
	 *  Method to verify labor values exists
	 * 
	 * @returns {boolean}
	 */
	public boolean isLaborExist() {
		return af.isElementExists(By.id(laborValues));
	}
	
	// Assisted by watsonx Code Assistant 
	/**
	 * Click blue checkmark to save labor record
	 * 
	 * @throws Exception
	 */
	public void saveLaborSelection() throws Exception {
		af.instantiateComponent(ButtonComponent.class, "laborLookup_done_buttongroup_laborLookup_done_button").click();
		af.waitForElementToNotBePresent(By.id("laborLookup_done_buttongroup_laborLookup_done_button"), 5000);
	}
	
	
	public void changeLaborReportWorkPage(String newLabor) throws Exception {
		// click labor search chevron
		af.waitForElementToBePresent(By.id(laborSelectionChevron));
		af.instantiateComponent(ButtonComponent.class, laborSelectionChevron).click();

		// input in text
		af.waitForElementToBePresent(By.id(searchInputLabor));
		TextInputComponent tic = af.instantiateComponent(TextInputComponent.class, searchInputLabor);
		tic.setText(newLabor);
		logger.info("Search value inputed");

		// click search button
		af.waitForElementToBePresent(By.id(laborCheckmark), af.DEFAULT_TIMEOUT_MS *10);
		ButtonComponent searchButton2 = af.instantiateComponent(ButtonComponent.class, laborCheckmark);
		searchButton2.click();
		af.waitForElementToBePresent(By.cssSelector(laborCheckboxSelection), 5000).click();
		af.waitForElementToBePresent(By.id("laborLookup_lookup_datalist_search_clearButton"), 2000).click();
	}
	
	// Generated by WCA for GP
	/**
	 * Method to click on 3 dots after opening the report work page in the material used section
	 *
	 * @throws Exception
	 */
	public void clickGetReservedItems() throws Exception {
		af.waitForElementToBePresent(By.id(getReservedItems), 5000);
		af.instantiateComponent(ButtonComponent.class, getReservedItems).click();
	}
	
	// Generated by WCA for GP
	/**
	 * Method to click on the checkbox for the non-rotating item and spare part item checkbox
	 * 
	 * @throws Exception
	 */
	public void clickOnNonRotatingItemCheckbox() throws Exception {
		af.waitForElementToBePresent(By.cssSelector(checkbxNonRotatingGetReservedItems), 5000);
		af.getWebDriver().findElement(By.cssSelector(checkbxNonRotatingGetReservedItems)).click();
	}
	
	// Generated by WCA for GP
	/**
	 * Method to click on 3 dots after opening the report work page in the material
	 * used section
	 *
	 * @throws Exception
	 */
	public void clickOnConfirmSelectionBtn() throws Exception {
		af.waitForElementToBePresent(By.id(btnGetSelected), 5000);
		af.instantiateComponent(ButtonComponent.class, btnGetSelected).click();
	}
		
	// Generated by WCA for GP
	/**
	 * Method to verify the items added in Materials / spare parts verification as well on the report work page
	 * 
	 * @throws Exception
	 */
	public String verifyNonRotatingItemsAdded() throws Exception {
		af.waitForElementToBePresent(By.id(getNonRotatingItemsAdded), af.DEFAULT_TIMEOUT_MS * 3);
		logger.info(af.instantiateComponent(LabelComponent.class, getNonRotatingItemsAdded).getValue());
		return af.instantiateComponent(LabelComponent.class, getNonRotatingItemsAdded).getValue().trim();

	}
	
	// Generated by WCA for GP
	/**
	 * Method to click on select asset spare part from 3 dots material 
	 *
	 * @throws Exception
	 */
	public void clickSelectAssetSparePart() throws Exception {
		af.waitForElementToBePresent(By.id(selectSpareAssetPart), 5000);
		af.instantiateComponent(ButtonComponent.class, selectSpareAssetPart).click();
	}
	
	// Generated by WCA for GP
	/**
	 * Method to click on Confirm selection page of select spare part page
	 *
	 * @throws Exception
	 */
	public void clickOnConfirmSelectionBtnOnSelectSparePartPage() throws Exception {
		af.waitForElementToBePresent(By.id(btnConfirmSelectionOnSparePartsPage), 5000);
		af.instantiateComponent(ButtonComponent.class, btnConfirmSelectionOnSparePartsPage).click();
	}
	
}
