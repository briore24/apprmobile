package com.ibm.maximo.technician.page;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.maximo.automation.mobile.AbstractAutomationFramework;
import com.ibm.maximo.automation.mobile.MobileAutomationFramework;
import com.ibm.maximo.components.ButtonComponent;
import com.ibm.maximo.components.DialogComponent;
import com.ibm.maximo.components.FieldComponent;
import com.ibm.maximo.components.LabelComponent;
import com.ibm.maximo.components.NumberInputComponent;
import com.ibm.maximo.components.RichTextEditorComponent;
import com.ibm.maximo.components.TextAreaComponent;
import com.ibm.maximo.components.TextInputComponent;
import com.ibm.maximo.technician.setupdata.SetupData.*;

public class EditWorkOrderPage {

	private final static Logger logger = LoggerFactory.getLogger(EditWorkOrderPage.class);
	public final String editWorkOrderdetailsPageTitle = "bzk2e-pageheader_title";
	public final String workTypeHeaderPageTitle = "workTypeLookup_sliding_drawer_headerLabel";
	private String priority = "yqkzy";
	private String description = "k2_mb";
	private String longDescription = "mandz";
	private String enterLLongDescription = "wg72g";
	private String workType = "edp_e";
	private String startDate = "kga47_date";
	private String startTime = "kga47_time";
	private String finishDate = "y8pqz_date";
	private String finishTime = "y8pqz_time";
	private String woDetaislEditClosebutton = "woDetailsEditDialog_close_button";
	private String saveDiscardDialogSaveButton = "saveDiscardDialog_button_group_saveDiscardDialog_primary_button";
	private String saveEditWO = "bzk2e-pageheader_buttongroup_e297p";
	private String estimatedHrs = "yqpzj_hour";
	private String estimatedMinutes = "yqpzj_minute";
	private String assetId = "va5we_lookup_buttongroup";
	private String locationId = "av_qz_lookup_buttongroup";
	private String workTypeLookupSearchInput = "workTypeLookup_lookup_datalist_search_searchInput";
	private String workTypeLookupSearch = "workTypeLookup_lookup_datalist_search";
	private String threeDotClickForAsset = "va5we_actionbuttongroup_overflow";
	private String searchIconClickAsset = "va5we_actionbuttongroup_overflowMenu_va5we_actionbuttongroup_a72w_";
	private String failureLabel="failurecode_46_label";
	private String locationLabel = "//p[contains(@id,'location_')]";
	private String rotatingItemLabel = "//*[text()='Rotating Item']";
	private String serialLabel = "//*[text()='Serial #']";
	private String typeLabel = "//*[text()='Type']";
	private String vendorLabel = "//*[text()='Vendor']";
	private String selectAllRecords = "filter_datalist_TouchSelectAll_filter_datalist_button";
	private String backIcon = "pageHeaderTemplate_icon";
	private String resetFilterIcon = "reset_filter";
	private String filterIcon = "maxlib_asset_lookup_filter_lookup_datalist_actionbuttongroup_undefined";
	private String txtErrorMsgPriority="yqkzy-error-msg";
	private String editWOBackButton="bzk2e-pageheader_breadcrumb_icon";
	private String discardButton="graphite_unsaved_changes_button_group_graphite_unsaved_changes_secondary_button";
	private String typeNumberBadge = "//span[contains(@id,'_badge')]";
	private String typeChevron="[id^='filter_border_assettype_'][id$='_slot_end']";
	private String workTypeBackButton="workTypeLookup_sliding_drawer_button";
	private String workTypeValue="wgj4_";
	private String systemMessageLocator="e2d7y";
	private String systemMessageCloseLocator="sysMsgDialog_woedit_close_button";
	private String systemMessageYesLocator="sysMsgDialog_woedit_button_group_sysMsgDialog_woedit_primary_button";
	private String targetStartDate = "azvg3_date";
	private String targetStartTime = "azvg3_time";
	private String targetFinishDate = "k_qxj_date";
	private String targetFinishTime = "k_qxj_time";
	private String actualDate="//p[text()='Actual start']";
	
	private String reassignmentDetail ="yyb2k_reassignment_detail";
	private String reassignButton ="confirmDialog_button_group_confirmDialog_primary_button";
	private String laborAssignmentSearch ="laborAssignmentLookup_lookup_datalist_search_searchInput";
	private String laborAssignmentTick ="laborAssignmentLookup_lookup_datalist_search_searchButton";
	private String laborSelectionIcon  ="//button[contains(@id,'selectionCheckBoxIcon')]";
	private String reassignmentDetailList ="bxek9[0]_reassignment_list[0]";
	private String unassignButton="confirmDialog_button_group_confirmDialog_secondary_button";
	private String unassignmentCode="jjxb__items_Asset_selectionCheckBoxIcon_touch";
	private String selectUnassgnmentCode="d9k9w_k3_xr";
	
	private AbstractAutomationFramework af;

	public EditWorkOrderPage(AbstractAutomationFramework af) {
		this.af = af;
	}

	/**
	 * Save Edited WO Details
	 *
	 * @throws Exception
	 */
	public void saveEditedWODetails() throws Exception {
		af.waitForElementToBePresent(By.id(saveEditWO), af.DEFAULT_TIMEOUT_MS * 3).click();
	}

	/**
	 * Enter Description
	 * 
	 * @param value
	 * @throws Exception
	 */
	public void enterDescription(String value) throws Exception {
		af.waitForElementToBePresent(By.id(description), af.DEFAULT_TIMEOUT_MS * 5).click();
		af.waitForElementToBePresent(By.id(description), af.DEFAULT_TIMEOUT_MS * 5).clear();
		af.waitForElementToBePresent(By.id(description), af.DEFAULT_TIMEOUT_MS * 5).sendKeys(value);
		
	}

	/**
	 * Enter Priority
	 * 
	 * @param value
	 * @throws Exception
	 */
	public void enterPriority(String value) throws Exception {
		logger.info("enter priority");
		NumberInputComponent numberInputComponent = af.instantiateComponent(NumberInputComponent.class, priority);
		numberInputComponent.typeInNumberInput(value);
	}

	/**
	 * Enter Long Description
	 * 
	 * @param value
	 * @throws Exception
	 */
	public void enterLongDescription(String value) throws Exception {
		logger.info("enter long description");
		af.instantiateComponent(ButtonComponent.class, longDescription).click();
		RichTextEditorComponent richTextEditor = af.instantiateComponent(RichTextEditorComponent.class,
				enterLLongDescription);
		richTextEditor.type(value);
		af.instantiateComponent(ButtonComponent.class, woDetaislEditClosebutton).click();
		af.instantiateComponent(ButtonComponent.class, saveDiscardDialogSaveButton).click();
	}

	/**
	 * Enter scheduled start date and time
	 * 
	 * @param year
	 * @param month
	 * @param date
	 * @param hrs
	 * @param min
	 * @throws Exception
	 */
	public void scheduledStartDateAndTime(int year, int month, int date, int hrs, int min) throws Exception {
		((MobileAutomationFramework) af).setDate(By.id(startDate), year, month, date);
		((MobileAutomationFramework) af).setTime(By.id(startTime), hrs, min, true);// true means AM
	}

	/**
	 * Enter Finish Date and Time
	 * 
	 * @param year
	 * @param month
	 * @param date
	 * @param hrs
	 * @param min
	 * @throws Exception
	 */
	public void scheduledFinishDateAndTime(int year, int month, int date, int hrs, int min) throws Exception {
		((MobileAutomationFramework) af).setDate(By.id(finishDate), year, month, date);
		((MobileAutomationFramework) af).setTime(By.id(finishTime), hrs, min, true);// true means AM
	}

	/**
	 * Enter scheduled start date and time
	 * 
	 * @param year
	 * @param month
	 * @param date
	 * @param hrs
	 * @param min
	 * @throws Exception
	 */
	public void targetStartDateAndTime(int year, int month, int date, int hrs, int min) throws Exception {
		((MobileAutomationFramework) af).setDate(By.id(targetStartDate), year, month, date);
		((MobileAutomationFramework) af).setTime(By.id(targetStartTime), hrs, min, true);// true means AM
	}

	/**
	 * Enter Finish Date and Time
	 * 
	 * @param year
	 * @param month
	 * @param date
	 * @param hrs
	 * @param min
	 * @throws Exception
	 */
	public void targetFinishDateAndTime(int year, int month, int date, int hrs, int min) throws Exception {
		((MobileAutomationFramework) af).setDate(By.id(targetFinishDate), year, month, date);
		((MobileAutomationFramework) af).setTime(By.id(targetFinishTime), hrs, min, true);// true means AM
	}
	/**
	 * Enter Estimated Duration
	 * 
	 * @param hrs
	 * @param min
	 * @throws Exception
	 */
	public void estimatedDuration(String hrs, String min) throws Exception {
		af.waitForElementToBePresent(By.id(estimatedHrs), 3000);
		af.waitForElementToBePresent(By.id(estimatedMinutes), 3000);
		if (af.isElementExists(By.id(estimatedHrs)) && af.isElementExists(By.id(estimatedMinutes))) {
			af.instantiateComponent(NumberInputComponent.class, estimatedHrs).typeAndSendDurationHourKey(hrs, Keys.TAB);
			af.instantiateComponent(NumberInputComponent.class, estimatedMinutes).typeAndSendDurationMinuteKey(min,Keys.TAB);
		} else {

			logger.info("estimated Hrs or estimated Minutes not found,Possibly Locators changed");
			throw new Exception("estimated Hrs or estimated Minutes not found, Possibly Locators changed");
		}
	}

	/**
	 * Enter Asset
	 * 
	 * @param assetNum
	 * @throws Exception
	 */
	public void assetNumber(String assetNum) throws Exception {
		TextInputComponent textInput = af.instantiateComponent(TextInputComponent.class, assetId);
		textInput.clearField();
		textInput.typeText(assetNum);
	}

	/**
	 * Enter Location
	 * 
	 * @param location
	 * @throws Exception
	 */
	public void locationNumber(String location) throws Exception {
		TextInputComponent textInput = af.instantiateComponent(TextInputComponent.class, locationId);
		textInput.clearField();
		textInput.typeText(location);
	}

	public void changeWorkType(WorkType worktype) {
		af.instantiateComponent(ButtonComponent.class, workType).click();
		String locator = "workTypeLookup_lookup_datalist_" + worktype + "_selectionCheckBoxIcon_touch";
		af.instantiateComponent(ButtonComponent.class, locator).click();
	}

	/**
	 * Click on assets three dots and click filter icon
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean searchForAssets() throws Exception {
		af.waitForElementToBePresent(By.id(threeDotClickForAsset), af.DEFAULT_TIMEOUT_MS * 5);
		af.instantiateComponent(ButtonComponent.class, threeDotClickForAsset).click();
		af.instantiateComponent(ButtonComponent.class, searchIconClickAsset).click();
		try {
			af.waitForElementToBePresent(By.id(filterIcon), 2000);
			af.instantiateComponent(ButtonComponent.class, filterIcon).click();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * Method to get failure class text
	 * 
	 * @return
	 * @throws Exception
	 */
	public String failureClassText() throws Exception {
		LabelComponent labelComponent = af.instantiateComponent(LabelComponent.class, failureLabel);
		return labelComponent.getLabel();
	}

	/**
	 * Method to get location text
	 * 
	 * @return
	 * @throws Exception
	 */
	
	public String locationLabelText() throws Exception {
		LabelComponent labelComponent= new LabelComponent(af.getWebDriver(), By.xpath(locationLabel));
		return labelComponent.getLabel();
	}

	/**
	 * Method to get rotating item text
	 * 
	 * @return
	 * @throws Exception
	 */
	public String rotatingItemText() throws Exception {
		LabelComponent labelComponent= new LabelComponent(af.getWebDriver(), By.xpath(rotatingItemLabel));
		return labelComponent.getLabel();
	}

	/**
	 * Method to get vendor text
	 * 
	 * @return
	 * @throws Exception
	 */
	public String vendorTextMethod() throws Exception {
		LabelComponent labelComponent= new LabelComponent(af.getWebDriver(), By.xpath(vendorLabel));
		return labelComponent.getLabel();
	}

	/**
	 * Method to get serial text
	 * 
	 * @return
	 * @throws Exception
	 */
	public String serialLabelText() throws Exception {
		LabelComponent labelComponent= new LabelComponent(af.getWebDriver(), By.xpath(serialLabel));
		return labelComponent.getLabel();
	}

	/**
	 * Method to get Type text
	 * 
	 * @return
	 * @throws Exception
	 */
	public String typeLabelText() throws Exception {
		LabelComponent labelComponent= new LabelComponent(af.getWebDriver(), By.xpath(typeLabel));
		return labelComponent.getLabel();
	}

	/**
	 * Method to select all serial number records
	 * 
	 * @throws Exception
	 */
	public void serialNumberSelectRecords() throws Exception {
		af.waitForElementToBePresent(By.id(serialLabel), 2000);
		af.instantiateComponent(ButtonComponent.class, serialLabel).click();
		af.instantiateComponent(ButtonComponent.class, selectAllRecords).click();
	}

	/**
	 * Method to click back button on filter page
	 * 
	 * @throws Exception
	 */
	public void filterPagebackButton() throws Exception {
		af.waitForElementToBePresent(By.id(backIcon),5000);
		af.instantiateComponent(ButtonComponent.class, backIcon).click();
	}

	/**
	 * Method to select all vendor records
	 * 
	 * @throws Exception
	 */
	public void VendorSelectRecords() throws Exception {
		af.waitForElementToBePresent(By.id(vendorLabel), 2000);
		af.instantiateComponent(ButtonComponent.class, vendorLabel).click();
	}

	/**
	 * Method to reset filter button
	 * 
	 * @throws Exception
	 */
	public void resetFilterButtonClick() throws Exception {
		af.waitForElementToBePresent(By.id(resetFilterIcon), 2000);
		af.instantiateComponent(ButtonComponent.class, resetFilterIcon).click();
	}
	
	/**
	 * Method to get error message for wrong priority 
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getErrorMsgForWrongPriority() throws Exception {
		LabelComponent labelComponent = af.instantiateComponent(LabelComponent.class, txtErrorMsgPriority);
		return labelComponent.getLabel();
	}
	
	
	/**
	 * Method to save button is enabled or disabled when entered wrong values 
	 * 
	 * @return boolean
	 * @throws Exception
	 */
	public boolean verifySaveButton() throws Exception {
		try {
			af.waitForElementToBeEnabled(By.id(saveEditWO));
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	
	/**
	 * Method to go back to the WO details page
	 * 
	 * @return boolean
	 * @throws Exception
	 */
	public void goBackToWODetailsPage() throws Exception {
		af.waitForElementToBePresent(By.id(editWOBackButton), 2000);
		af.instantiateComponent(ButtonComponent.class, editWOBackButton).click();
	}
	
	/**
	 * Method to click on discard button 
	 * 
	 * @throws Exception
	 */
	public void discardButtonClick() throws Exception {
		af.instantiateComponent(ButtonComponent.class, discardButton ).click();
	}
	
	/**
	 * Method to select all type records
	 * 
	 * @throws Exception
	 */
	public boolean typeRecordSelectMethod() throws Exception {
		boolean typeChevronValue = af.isElementExists(By.cssSelector(typeChevron));
		af.waitForElementToBePresent(By.cssSelector(typeChevron), 1000).click();
		af.waitForElementToBePresent(By.id(selectAllRecords), 1000);
		boolean selectAllRecordsValue = af.isElementExists(By.id(selectAllRecords));
		af.instantiateComponent(ButtonComponent.class, selectAllRecords).click();
		return typeChevronValue && selectAllRecordsValue;
	}
	
	/**
	 * Method to verify badge displayed for Type Number
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean isBadgeDisplayedforTypeNumber() throws Exception {
			return af.isElementExists(By.xpath(typeNumberBadge));
		}
	
	// Generated by WCA for GP
	/**
	 * Method to open work type
	 * 
	 */
	public void OpenWorkType() {
		af.instantiateComponent(ButtonComponent.class, workType).click();
		
	}
	
	// Generated by WCA for GP
	/**
	 * Method to create follow-up work order
	 * 
	 * @throws Exception 
	 */
	public void saveWO() throws Exception {
		af.instantiateComponent(ButtonComponent.class, "bzk2e-pageheader_buttongroup_e297p").click();
	}

	// Generated by WCA for GP
	/**
	 * Method to verify value is selected
	 * 
	 * @param worktype
	 */
	public void workTypeIsSelected(WorkType worktype) {
		String locator = worktype + "_rowContentWrapper";
		af.instantiateComponent(ButtonComponent.class, locator).isSelected();
	}

	/**
	 * Method to select back button
	 * 
	 * @throws Exception
	 */
	public void backButton() throws Exception {
		af.instantiateComponent(ButtonComponent.class, workTypeBackButton).click();
	}
	
	/**
	 * Method to select work type value
	 * 
	 * @return
	 * @throws Exception
	 */
	public String workTypeValue() throws Exception {
		return af.instantiateComponent(FieldComponent.class, workTypeValue).getValue();
	}
	
	// Generated by WCA for GP
	/**
	 * Method to get system message
	 * 
	 * @return
	 * @throws Exception
	 */
	public String systemMessage() throws Exception {
		return af.instantiateComponent(ButtonComponent.class, systemMessageLocator).getText();
	}
	
	// Generated by WCA for GP
	/**
	 * Method to close system message prompt
	 * 
	 * @throws Exception
	 */
	public void systemMessageClose() throws Exception {
		logger.info("started close method");
		af.instantiateComponent(ButtonComponent.class, systemMessageCloseLocator).click();
	}

/**
 * verify actual date
 * 
 * @return boolean
 * @throws Exception
 */
public boolean verifyActualDateInQuickWO() throws Exception {
	return af.isElementExists(By.xpath(actualDate));

}

public void systemMessageYes() throws Exception {
	logger.info("started yes method");
	af.instantiateComponent(ButtonComponent.class, systemMessageYesLocator).click();
}


public void clickReassignmentDetail() throws Exception {
	af.waitForElementToBePresent(By.id(reassignmentDetail)).click();
}
public void clickreassignButton() throws Exception {
	af.waitForElementToBePresent(By.id(reassignButton)).click();
}

public void clickLaborAssignmentSearch(String laborName) throws Exception {
	TextInputComponent textInput = af.instantiateComponent(TextInputComponent.class, laborAssignmentSearch);
	textInput.clearField();
	textInput.typeText(laborName);
}
//public void clickLaborAssignmentSearch() throws Exception {
//	af.waitForElementToBePresent(By.id(laborAssignmentSearch)).click();
//}
public void clickLaborAssignmentTick() throws Exception {
	af.waitForElementToBePresent(By.id(laborAssignmentTick),1000).click();
}
public void clickReassignmentWoListPage() throws Exception {
	af.waitForElementToBePresent(By.id(reassignmentDetailList),1000).click();
}
public void clickLaborUnassignButton() throws Exception {
	af.waitForElementToBePresent(By.id(unassignButton),5000).click();
}
public void clickUnassignmentCode() throws Exception {
	af.waitForElementToBePresent(By.id(unassignmentCode),1000).click();
}
public void clickBlueTickUnassignment() throws Exception {
	af.waitForElementToBePresent(By.id(selectUnassgnmentCode),1000).click();
}
public void clickLaborSelectionIcon() throws Exception {
	af.waitForElementToBePresent(By.xpath(laborSelectionIcon),1000).click();
}


}

