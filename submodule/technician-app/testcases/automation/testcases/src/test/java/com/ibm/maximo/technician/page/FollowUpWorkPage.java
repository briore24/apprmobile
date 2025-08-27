package com.ibm.maximo.technician.page;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.maximo.automation.mobile.AbstractAutomationFramework;
import com.ibm.maximo.automation.mobile.MobileAutomationFramework;
import com.ibm.maximo.components.ButtonComponent;
import com.ibm.maximo.components.FieldComponent;
import com.ibm.maximo.components.LabelComponent;
import com.ibm.maximo.components.NumberInputComponent;
import com.ibm.maximo.components.RichTextEditorComponent;
import com.ibm.maximo.components.SearchComponent;
import com.ibm.maximo.components.TextAreaComponent;
import com.ibm.maximo.components.TextInputComponent;
import com.ibm.maximo.components.ToastComponent;
import com.ibm.maximo.technician.setupdata.SetupData.WorkType;
import com.ibm.maximo.technician.testcases.CreateWorkOrderTest;

public class FollowUpWorkPage {

	private AbstractAutomationFramework af;
	private final static Logger logger = LoggerFactory.getLogger(CreateWorkOrderTest.class);
	public final String followUpPageTitle = "relatedWorkOrder_2_field";
	public final String CreateFollowUpPageTitle = "bzk2e-pageheader_title";
	private String clickPlusLocator = "k6je4_m3nvj";
	private String completeFollowUpButton = "bzk2e-pageheader_buttongroup_e297p";
	public final String relatedWorkOrders = "a89dm";
	private String backButtonLocator = "zj78m-pageheader_breadcrumb_icon";
	private String toastMessage = "UINotificationContainer";
	private String assetAndLocationText = "vmn3n", parentAssetAndLocationText = "yaa42";
	private String followUpDesc = "gek7p[0]";
	private String followUpWorkChevron = "v8__a[0]";
	private String followUpOriginatingRecord = "q5vn_[0]";
	public String tokenHeader = "xxknj";
	public String noRelatedRecordsFound="noDataWrapper";
	public String btnPlusLocatorIconToCreateFollowUpWO="k6je4_m3nvj_icon";
	public String btnDoneIcon="zj78m-pageheader_buttongroup_ykwv4_icon";
	private String assetId = "va5we_lookup_buttongroup";
	private String locationId = "av_qz_lookup_buttongroup";
	
	private String addDescriptionLocator = "k2_mb";
	private String enterLongDescription = "wg72g";
	private String addLongDescriptionLocator = "mandz";
	private String saveDiscardDialogSaveButton = "saveDiscardDialog_button_group_saveDiscardDialog_primary_button";
	private String longDescriptionCloseButton = "woDetailsEditDialog_close_button";
	private String priorityLocator = "yqkzy";
	private String scheduledDateStartLocator = "kga47_date";
	private String scheduleStartTimeLocator = "kga47_time";
	private String scheduleDateFinishLocator = "y8pqz_date";
	private String scheduleTimeFinishLocator = "y8pqz_time";
	private String estDurLocator = "yqpzj";
	private String estimatedHrs = "yqpzj_hour";
	private String estimatedMinutes = "yqpzj_minute";
	private String workType = "edp_e";
	
	private String assetOfFirstElementInList = "maxlib_nyb3q";
	
	private String threeDotClickForAsset = "va5we_actionbuttongroup_overflow";
	private String threeDotClickForLocation = "av_qz_actionbuttongroup_overflow";
	private String searchIconClickAsset = "va5we_actionbuttongroup_overflowMenu_va5we_actionbuttongroup_a72w_";
	private String searchIconClickLocation = "av_qz_actionbuttongroup_overflowMenu_av_qz_actionbuttongroup_andxk";
	private String sendTextinSearchForAsset = "maxlib_asset_lookup_filter_lookup_datalist_search_searchInput";
	private String sendTextinSearchForLocation = "maxlib_location_lookup_filter_lookup_datalist_search";
	private String searchButtonLocator = "maxlib_asset_lookup_filter_lookup_datalist_search_searchButton";
	private String modalHeaderLocator = "MXlookup_modal_Page_Header_icon";
	private String yesClick ="sysMsgDialog_woedit_button_group_sysMsgDialog_woedit_primary_button";
	private String getNavigatorMenuButtonClick = "NavigatorMenuButton";
	private String userProfileLocator = "navigator_userProfileprofile-image-wrapper";
	private String plusButton = "k6je4_m3nvj"; 
	private String overviewLabel = "a4ma4";
	private String tokenNumber = "dpg5a[0]";
	private String serviceDesc = "ndpna[0]";
	private String recordRelatedLabel = "bx9g_[0]";
	private String targetStartDate = "azvg3_date";
	private String targetStartTime = "azvg3_time";
	private String targetFinishDate = "k_qxj_date";
	private String targetFinishTime = "k_qxj_time";
	
	public FollowUpWorkPage(AbstractAutomationFramework af) {
		this.af = af;
	}

	/**
	 * Click plus button
	 * 
	 * @throws Exception
	 */
	public void clickPlusButton() throws Exception {
		af.waitForElementToBePresent(By.id(clickPlusLocator), af.DEFAULT_TIMEOUT_MS * 3);
		af.instantiateComponent(ButtonComponent.class, clickPlusLocator).click();
	}
	
	/**
	 * Click chevron button
	 * 
	 * @throws Exception
	 */
	public void clickOpenChevron() throws Exception {
		af.waitForElementToBePresent(By.id(followUpWorkChevron), af.DEFAULT_TIMEOUT_MS * 5);
		af.instantiateComponent(ButtonComponent.class, followUpWorkChevron).click();
	}


	/**
	 * Follow up work order displayed
	 * 
	 * @return boolean
	 * @throws Exception
	 */
	public boolean followUpWoDisplayed() throws Exception {
		return af.isElementExists(By.id(clickPlusLocator));
	}

	/**
	 * Method to get Header Title
	 * 
	 * @param elementID
	 * @return
	 * @throws Exception
	 */
	public String getTitle(String elementID) throws Exception {
		af.waitForElementToBePresent(By.id(elementID), af.DEFAULT_TIMEOUT_MS * 3);
		String pageTitle = af.instantiateComponent(FieldComponent.class, elementID).getValue();
		return pageTitle;
	}

	/**
	 * Method to create follow-up work order
	 * 
	 * @throws Exception
	 */
	public void clickCreateFollowUp() throws Exception {
		af.instantiateComponent(ButtonComponent.class, completeFollowUpButton).click();
	}

	

	/**
	 * Method to return to WO Details Page
	 * 
	 * @throws Exception
	 */
	public void backButton() throws Exception {
		af.waitForElementToBePresent(By.id(backButtonLocator), af.DEFAULT_TIMEOUT_MS * 7);
		af.instantiateComponent(ToastComponent.class, backButtonLocator).click();
	}

	/**
	 * Method to display toast message
	 * 
	 * @return toast content
	 * @throws Exception
	 */
	public String toastDisplayed() throws Exception {
		// af.waitForElementToBePresent(By.id(toastMessage),af.DEFAULT_TIMEOUT_MS*3);
		ToastComponent tc = af.instantiateComponent(ToastComponent.class, toastMessage);
		return tc.getContentText();
	}

	/**
	 * Method to display related work orders label
	 * 
	 * @return related work order text
	 * @throws Exception
	 */
	public String relatedWorkOrdersText() throws Exception {
		af.waitForElementToBePresent(By.id(relatedWorkOrders), af.DEFAULT_TIMEOUT_MS * 3);
		LabelComponent labelComponent = af.instantiateComponent(LabelComponent.class, relatedWorkOrders);
		return labelComponent.getLabel();
	}

	/**
	 * Method to display related sr token header label
	 * 
	 * @return related service request token header text
	 * @throws Exception
	 */
	public String relatedTokenHeaderText() throws Exception {
		af.waitForElementToBePresent(By.id(tokenHeader), af.DEFAULT_TIMEOUT_MS * 3);
		LabelComponent labelComponent = af.instantiateComponent(LabelComponent.class, tokenHeader);
		return labelComponent.getLabel();
	}

	/**
	 * 
	 * Follow-up work order description
	 * 
	 * @return follow-up work order description
	 * @throws Exception
	 */
	public String relatedSRTokenNum() throws Exception {
		af.waitForElementToBePresent(By.id(tokenNumber), af.DEFAULT_TIMEOUT_MS * 2);
		LabelComponent labelComponent = af.instantiateComponent(LabelComponent.class, tokenNumber);
		return labelComponent.getLabel();
	}

	/**
	 * 
	 * Work order related record token description
	 * 
	 * @return related record token description
	 * @throws Exception
	 */
	public String followUpDescription() throws Exception {
		af.waitForElementToBePresent(By.id(followUpDesc), af.DEFAULT_TIMEOUT_MS * 2);
		LabelComponent labelComponent = af.instantiateComponent(LabelComponent.class, followUpDesc);
		return labelComponent.getLabel();
	}

		/**
	 * 
	 * Follow Up Originating Record Description
	 * 
	 * @return originating record text
	 * @throws Exception
	 */
	public String originatingRecordText() throws Exception {
		af.waitForElementToBePresent(By.id(followUpOriginatingRecord), af.DEFAULT_TIMEOUT_MS * 2);
		LabelComponent labelComponent = af.instantiateComponent(LabelComponent.class, followUpOriginatingRecord);
		return labelComponent.getLabel();
	}

	/**
	 * Method to display asset/location label
	 * 
	 * @return asset/location label
	 * @throws Exception
	 */
	public String assetLocationText() throws Exception {
		af.waitForElementToBePresent(By.id(assetAndLocationText), af.DEFAULT_TIMEOUT_MS * 2);
		LabelComponent labelComponent = af.instantiateComponent(LabelComponent.class, assetAndLocationText);
		return labelComponent.getLabel();
	}
	
	/**
	 * Method to display parent asset location label
	 * 
	 * @return parent asset location label
	 * @throws Exception
	 */
	public String parentAssetLocationText() throws Exception {
		af.waitForElementToBePresent(By.id(parentAssetAndLocationText), af.DEFAULT_TIMEOUT_MS * 2);
		LabelComponent labelComponent = af.instantiateComponent(LabelComponent.class, parentAssetAndLocationText);
		return labelComponent.getLabel();
	}

	/**
	 *
	 * Service request Related
	 * @throws Exception
	 */
	public String getRecordRelatedOnServiceRequest() throws Exception {
		af.waitForElementToBePresent(By.id(recordRelatedLabel), af.DEFAULT_TIMEOUT_MS * 2);
		LabelComponent labelComponent = af.instantiateComponent(LabelComponent.class, recordRelatedLabel);
		return labelComponent.getLabel();
	}

	/**
	 * Method to get text for no related records found
	 *
	 * @throws Exception
	 */
	public String getDescForNoRelatedRecordsFound() throws Exception{
		af.waitForElementToBePresent(By.id(noRelatedRecordsFound), af.DEFAULT_TIMEOUT_MS * 2);
		LabelComponent labelComponent = af.instantiateComponent(LabelComponent.class, noRelatedRecordsFound);
		return labelComponent.getValue();
	}

	/**
	 *
	 * Service Request description
	 * return service request desc
	 * @throws Exception
	 */
	public String serviceReqDescription() throws Exception {
		af.waitForElementToBePresent(By.id(serviceDesc), af.DEFAULT_TIMEOUT_MS * 2);
		LabelComponent labelComponent = af.instantiateComponent(LabelComponent.class, serviceDesc);
		return labelComponent.getLabel();
	}

	/**
	 * Enter Asset
	 *
	 * @param assetNum
	 * @throws Exception
	 */
	public void assetNumber(String assetNum) throws Exception {
		TextInputComponent textInput = af.instantiateComponent(TextInputComponent.class, assetId);
		textInput.setText(assetNum);
	}

	/**
	 * Enter Location
	 *
	 * @param location
	 * @throws Exception
	 */
	public void locationNumber(String location) throws Exception {
		TextInputComponent textInput = af.instantiateComponent(TextInputComponent.class, locationId);
		textInput.setText(location);
	}
	
	/**
	 * Enter description of work order
	 * 
	 * @param descriptionStr
	 * @throws Exception
	 */
	public void insertDescriptionOfWorkOrder(String descriptionStr) throws Exception {
		af.waitForElementToBePresent(By.id(addDescriptionLocator), af.DEFAULT_TIMEOUT_MS * 5);
		TextAreaComponent textArea = af.instantiateComponent(TextAreaComponent.class, addDescriptionLocator);
		textArea.type(descriptionStr);
	}

	/**
	 * Enter Long Description
	 * 
	 * @param longDescriptionStr
	 * @throws Exception
	 */
	public void enterLongDescription(String longDescriptionStr) throws Exception {
		af.instantiateComponent(ButtonComponent.class, addLongDescriptionLocator).click();
		RichTextEditorComponent richTextEditor = af.instantiateComponent(RichTextEditorComponent.class,
				enterLongDescription);
		richTextEditor.type(longDescriptionStr);
		af.instantiateComponent(ButtonComponent.class, longDescriptionCloseButton).click();
		af.instantiateComponent(ButtonComponent.class, saveDiscardDialogSaveButton).click();
	}

	
	/**
	 * Enter start date and time
	 * 
	 * @param year
	 * @param month
	 * @param date
	 * @param hrs
	 * @param min
	 * @throws Exception
	 */
	public void scheduledStartDateAndTime(int year, int month, int date, int hrs, int min) throws Exception {
		af.waitForElementToBePresent(By.id(scheduledDateStartLocator), 2);
		((MobileAutomationFramework) af).setDate(By.id(scheduledDateStartLocator), year, month, date);
		af.waitForElementToBePresent(By.id(scheduleStartTimeLocator), 2);
		((MobileAutomationFramework) af).setTime(By.id(scheduleStartTimeLocator), hrs, min, true);
	}

	/**
	 * Enter finish date and time
	 * 
	 * @param year
	 * @param month
	 * @param date
	 * @param hrs
	 * @param min
	 * @throws Exception
	 */
	public void scheduledFinishDateAndTime(int year, int month, int date, int hrs, int min) throws Exception {
		af.waitForElementToBePresent(By.id(scheduleDateFinishLocator), 2);
		((MobileAutomationFramework) af).setDate(By.id(scheduleDateFinishLocator), year, month, date);
		af.waitForElementToBePresent(By.id(scheduleTimeFinishLocator), 2);
		((MobileAutomationFramework) af).setTime(By.id(scheduleTimeFinishLocator), hrs, min, true);
	}

	

	/**
	 * Select work type
	 * 
	 * @param string
	 * @throws Exception
	 */
	public void changeWorkType(String string) throws Exception {
		af.instantiateComponent(ButtonComponent.class, workType).click();
		String locator = "workTypeLookup_lookup_datalist_" + string + "_selectionCheckBoxIcon_touch";
		af.instantiateComponent(ButtonComponent.class, locator).click();
	}

	/**
	 * Enter priority field
	 * 
	 * @param priority
	 * @throws Exception
	 */
	public void priorityEnter(String priority) throws Exception {
		logger.info("enter priority");
		NumberInputComponent numberInputComponent = af.instantiateComponent(NumberInputComponent.class,
				priorityLocator);
		numberInputComponent.typeInNumberInput(priority);
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
	 * Search for asset and select the expected one
	 * 
	 * @param query
	 * @return
	 * @throws Exception
	 */
	public boolean searchForAssets(String query) throws Exception {
		af.waitForElementToBePresent(By.id(threeDotClickForAsset), af.DEFAULT_TIMEOUT_MS * 5);
		af.instantiateComponent(ButtonComponent.class, threeDotClickForAsset).click();
		af.instantiateComponent(ButtonComponent.class, searchIconClickAsset).click();
		try {
			if (af.isElementExists(By.id(sendTextinSearchForAsset))) {
				// Wait
				af.waitForElementToNotBePresent(By.cssSelector("#loading0_LongPress"), af.DEFAULT_TIMEOUT_MS * 5);
				if (af.isElementExists(By.id(sendTextinSearchForAsset))) {
					// Set text in the input field
					logger.info("Set text in the input field");
					af.waitForElementToBePresent(By.id(sendTextinSearchForAsset));
					TextInputComponent tic = af.instantiateComponent(TextInputComponent.class,
							sendTextinSearchForAsset);
					tic.setText(query);
					// Click on the search button
					logger.info("Click on the search button");
					af.waitForElementToBePresent(By.id(searchButtonLocator));
					ButtonComponent searchActionButton = af.instantiateComponent(ButtonComponent.class,
							searchButtonLocator);
					searchActionButton.click();
					// Wait
					af.waitForElementToNotBePresent(By.cssSelector("#loading0_LongPress"), af.DEFAULT_TIMEOUT_MS * 5);
				}
			}
			af.waitForElementToBePresent(By.id(assetOfFirstElementInList), 2000);
			boolean assetExistInList = af.isElementExists(By.id(assetOfFirstElementInList));
			if (assetExistInList) {
				af.instantiateComponent(ButtonComponent.class, assetOfFirstElementInList).click();
			}
			return assetExistInList;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Search for Location
	 * 
	 * @param query
	 * @return
	 * @throws Exception
	 */
	public boolean searchForLocation(String query) throws Exception {
		
		
		af.waitForElementToBePresent(By.id(threeDotClickForLocation), af.DEFAULT_TIMEOUT_MS * 5);
		af.instantiateComponent(ButtonComponent.class, threeDotClickForLocation).click();
		af.instantiateComponent(ButtonComponent.class, searchIconClickLocation).click();
		try {
			SearchComponent searchField = af.instantiateComponent(SearchComponent.class, sendTextinSearchForLocation);
			searchField.typeAndSendEnterKey(query);
			af.waitForElementToBePresent(By.id(modalHeaderLocator)).click();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * Enter Location
	 *
	 * @param location
	 * @throws Exception
	 */
	public void locationNumberClick() throws Exception {
		TextInputComponent textInput = af.instantiateComponent(TextInputComponent.class, locationId);
		textInput.click();
	}
	
	/**
	 * Method to click back button on filter page
	 * 
	 * @throws Exception
	 */
	public void yesClickButton() throws Exception {
		af.waitForElementToBePresent(By.id(yesClick),10000);
		af.instantiateComponent(ButtonComponent.class, yesClick).click();
	}
	

	// Generated by WCA for GP
	/**
	 * Get the value of location text box in work order details page
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getLocationTextWODetailsPageOnfollowUpWO() throws Exception {
		return af.instantiateComponent(NumberInputComponent.class, locationId).getValue();
	}
	//Generated by WCA for GP
	/**
	 * Method to check search button availibility
	 * @return 
	 * 
	 * @throws Exception
	 */
	public boolean checkplusButtonAvailbility() throws Exception {
		try {
			af.waitForElementToBePresent(By.id(plusButton),af.DEFAULT_TIMEOUT_MS * 50);
			af.isElementExists(By.id("k6je4_m3nvj")); 
			return true;
			
		} catch (Exception e) {
			  return false;
			}
		}
	
	//Generated by WCA for GP
	/**
	 * Method for clicking on Navigator
	 * 
	 * @throws Exception
	 */
	public void clickNavigatorMenu() throws Exception {
		WebElement profile = af.waitForElementToBePresent(By.id(userProfileLocator), 2);
		if (!profile.isDisplayed()) {
			af.instantiateComponent(ButtonComponent.class, getNavigatorMenuButtonClick).click();
		}
	}
	//Generated by WCA for GP
	/**
	 * Enter Asset
	 *
	 * @param assetNum
	 * @throws Exception
	 */
	public void clearAndAddassetNumber(String assetNum) throws Exception {
		TextInputComponent textInput = af.instantiateComponent(TextInputComponent.class, assetId);
		textInput.clearField();
		textInput.setText(assetNum);
	}
	
	/**
	 * Method to check overview label text
	 * 
	 * @return
	 * @throws Exception
	 */
	public String overviewLabel() throws Exception {
		return af.instantiateComponent(ButtonComponent.class, overviewLabel).getText();		
	}
	
	/**
	 * Method to check if location is displayed
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean locationDisplayed() throws Exception {
		return af.isElementExists(By.id(locationId));
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
}



