package com.ibm.maximo.technician.page;


import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.maximo.automation.mobile.AbstractAutomationFramework;
import com.ibm.maximo.automation.mobile.MobileAutomationFramework;
import com.ibm.maximo.components.ButtonComponent;
import com.ibm.maximo.components.DropDownComponent;
import com.ibm.maximo.components.FieldComponent;
import com.ibm.maximo.components.NumberInputComponent;
import com.ibm.maximo.components.RichTextEditorComponent;
import com.ibm.maximo.components.SearchComponent;
import com.ibm.maximo.components.TextAreaComponent;
import com.ibm.maximo.components.TextInputComponent;
import com.ibm.maximo.technician.setupdata.SetupData.*;
import com.ibm.maximo.technician.testcases.CreateWorkOrderTest;

import io.appium.java_client.MobileBy;

public class CreateWorkOrderPage {

	private final static Logger logger = LoggerFactory.getLogger(CreateWorkOrderTest.class);
	private String navigatorClickButton = "NavigatorMenuButton";
	private String addRouteLocator = "navigator_addRoute";
	private String createWOLocator = "[title='Create work order']";
	private String addDescriptionLocator = "j3d2k";
	private String enterLongDescription = "a_x43";
	private String addLongDescriptionLocator = "ggvjj";
	private String saveDiscardDialogSaveButton = "saveDiscardDialogCreatePage_button_group_saveDiscardDialogCreatePage_primary_button";
	private String longDescriptionCloseButton = "longdsEditDialog_close_button";
	private String priorityLocator = "j8265";
	private String workOrderCreationButtonClick = "kngx7-pageheader_buttongroup_mjbgg";
	private String scheduledDateStartLocator = "va287_date";
	private String scheduleStartTimeLocator = "va287_time";
	private String scheduleDateFinishLocator = "j288g_date";
	private String scheduleTimeFinishLocator = "j288g_time";
	private String estDurHrsLocator = "vp7qm_hour";
	private String estDurMinsLocator = "vp7qm_minute";
	private String workType = "k62p3";
	private String threeDotClickForAsset = "dxkr5_actionbuttongroup_overflow";
	private String searchButtonLocator = "maxlib_asset_lookup_filter_lookup_datalist_search_searchButton";
	private String searchButtonLoctaionLocator = "maxlib_location_lookup_filter_lookup_datalist_search_searchButton";
	
	private String threeDotClickForLocation = "awegq_actionbuttongroup_overflow";
	private String searchIconClickAsset = "dxkr5_actionbuttongroup_overflowMenu_dxkr5_actionbuttongroup_a9367_icon";
	private String searchIconClickLocation = "awegq_actionbuttongroup_overflowMenu_awegq_actionbuttongroup_wxry5_icon";
	private String sendTextinSearchForAsset = "maxlib_asset_lookup_filter_lookup_datalist_search_searchInput";
	private String sendTextinSearchForLocation = "maxlib_location_lookup_filter_lookup_datalist_search_searchInput";
	private String assetOfFirstElementInList = "maxlib_k3gdg"; 
	private String assetOfFirstElementInListSelection = "//button[contains(@id,'_selectionCheckBoxIcon_touch')]";
	private String locationOfFirstElementInList = "//div[@class='mx--tile mx--tile--not-selected']";
	private String locationOfFirstElementInListSelection = "//button[contains(@id,'_selectionCheckBoxIcon_touch')]";
	private String selectTickLocator = "kngx7-pageheader_buttongroup_mjbgg_icon";
	private String assetTextWOPage = "yzqaa";
	private String locationDescWOPage = "agajd";
	private String locationTextWOPage = "yzqaa_value";
	private String dropdownSelect = "rzvz4";
	private String modalHeaderLocator = "MXlookup_modal_Page_Header_icon";
	private String assetSearchFilter = "maxlib_asset_lookup_filter_lookup_datalist_actionbuttongroup_undefined";
	private String locationOfFirstElementInListInQuickReporting = "//button[contains(@id, 'selectionCheckBoxIcon_touch')]";
	private String filterTypeSelect = "//div[@class='mx--filter-filters-view-wrapper']//p[contains(text(),'Type')]";
	private String filterOptions = "//div[@class='mx--filter-options-wrapper']//p[contains(text(),'IT Assets')]";
	private String saveFilterDone = "pageHeaderTemplate_buttongroup_MXFilter_modal_done_button";
	private String filterBadgeCount = "//div[@class='mx--datalist-title']//span[contains(text(),'1')]";
	private String resetFilter = "reset_filter";
	private String assetInputLocator = "dxkr5_lookup_buttongroup";
	private String clickBackChevron = "kngx7-pageheader_breadcrumb_icon";
    private String saveDiscradPrompt = "graphite_unsaved_changes";
    private String discardButton = "graphite_unsaved_changes_button_group_graphite_unsaved_changes_secondary_button";
    private String saveButton = "graphite_unsaved_changes_button_group_graphite_unsaved_changes_primary_button";
    public String searchIconButtonLocator = "jgyq6_cards_searchbuttongroup_searchIconButton";
    public String locationInputLocator="awegq_lookup_buttongroup";
    private static String backButtonLogDataPage = "bx--breadcrumb-item-back";
    private String createQuickReport = "[title='New work order (via Quick Reporting)']"; 
    private String assetId = "dxkr5_lookup_buttongroup";

	private String targetDateStartLocator = "wb6nm_date";
	private String targetStartTimeLocator = "wb6nm_time";
	private String targetDateFinishLocator = "bdrap_date";
	private String targetTimeFinishLocator = "bdrap_time";
	private String locationId ="awegq_lookup_buttongroup";

	private AbstractAutomationFramework af;

	public CreateWorkOrderPage(AbstractAutomationFramework af) {
		this.af = af;
	}

	/**
	 * Click on Navigator
	 * 
	 * @throws Exception
	 */
	public void navigatorClick() throws Exception {
		af.instantiateComponent(ButtonComponent.class, navigatorClickButton).clickJs();
		af.waitForElementToNotBePresent(By.id(navigatorClickButton), 3000);
	}

	/**
	 * Click on Back Chevron on Asset Search Page
	 * 
	 * @throws Exception
	 */
	public void clickBackChevron() throws Exception {
		af.waitForElementToBePresent(By.id(modalHeaderLocator)).click();
	}
	
	/**
	 * Method for click on plus icon on Navigator
	 * 
	 * @throws Exception
	 */
	public void getplusiconClick() throws Exception {
		af.instantiateComponent(ButtonComponent.class, addRouteLocator).click();
	}
	
	/**
	 * Check for Filter Icon
	 * @return
	 * @throws Exception
	 */
	public boolean checkFilterIcon() throws Exception {
		return af.isElementExists(By.id(assetSearchFilter));
	}
	
	/**
	 * Click Filter Icon
	 * 
	 * @throws Exception
	 */
	public void clickFilterIcon() throws Exception {
		af.instantiateComponent(ButtonComponent.class, assetSearchFilter).click();
	}
	

	/**
	 * Select Create WorkOrder
	 * 
	 * @throws Exception
	 */
	public void selectCreateWO() throws Exception {
		af.waitForElementToBePresent(By.cssSelector(createWOLocator), af.DEFAULT_TIMEOUT_MS * 3).click();
	}

	/**
	 * Enter description of work order
	 * 
	 * @param descriptionStr
	 * @throws Exception
	 */
	public void insertDescriptionOfWorkOrder(String descriptionStr) throws Exception {
		af.waitForElementToBePresent(By.id(addDescriptionLocator), af.DEFAULT_TIMEOUT_MS * 3);
		TextInputComponent textArea = af.instantiateComponent(TextInputComponent.class, addDescriptionLocator);
		textArea.setText(descriptionStr);
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
	 * Enter Asset field
	 * 
	 * @param Asset
	 * @throws Exception
	 */
	public void assetEnter(String asset) throws Exception {
		af.waitForElementToBePresent(By.id(assetInputLocator), af.DEFAULT_TIMEOUT_MS * 3);
		TextInputComponent textArea = af.instantiateComponent(TextInputComponent.class, assetInputLocator);
		textArea.setText(asset);
		af.waitForElementToBePresent(By.id(threeDotClickForAsset), af.DEFAULT_TIMEOUT_MS * 5);
		af.instantiateComponent(ButtonComponent.class, threeDotClickForAsset).click();
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
	public void scheduledStartDateAndTime(int year, int month, int date,int hrs, int min) throws Exception {
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
	public void targetFinishDateAndTime(int year, int month, int date, int hrs, int min) throws Exception {
		af.waitForElementToBePresent(By.id(targetDateFinishLocator), 2);
		((MobileAutomationFramework) af).setDate(By.id(targetDateFinishLocator), year, month, date);
		af.waitForElementToBePresent(By.id(targetTimeFinishLocator), 2);
		((MobileAutomationFramework) af).setTime(By.id(targetTimeFinishLocator), hrs, min, true);
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
	public void targetStartDateAndTime(int year, int month, int date,int hrs, int min) throws Exception {
		af.waitForElementToBePresent(By.id(targetDateStartLocator), 2);
		((MobileAutomationFramework) af).setDate(By.id(targetDateStartLocator), year, month, date);
		af.waitForElementToBePresent(By.id(targetStartTimeLocator), 2);
		((MobileAutomationFramework) af).setTime(By.id(targetStartTimeLocator), hrs, min, true);
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
	 * Enter Estimated duration hrs
	 * @param hrs 
     * @throws Exception
     */
	public void estDurHrsLocator(String hrs) throws Exception {
		logger.info("enter estimated hours");
		af.waitForElementToBePresent(By.id(estDurHrsLocator), 2);
		NumberInputComponent durationInputBox = af.instantiateComponent(NumberInputComponent.class, estDurHrsLocator);
		durationInputBox.typeAndSendDurationHourKey(hrs, Keys.TAB);
	}

	/**
	 * Enter Estimated duration mins
	 * @param min 
     * @throws Exception
     */
	public void estDurMinsLocator(String min) throws Exception {
		logger.info("enter estimated minutes");
		af.waitForElementToBePresent(By.id(estDurMinsLocator), 2);
		NumberInputComponent durationInputBox = af.instantiateComponent(NumberInputComponent.class, estDurMinsLocator);
		durationInputBox.typeAndSendDurationMinuteKey(min, Keys.ENTER);
	}

	/**
	 * Select work type
	 * 
	 * @param query
	 * @throws Exception
	 */
	public void changeWorkType(WorkType query) throws Exception {
		af.waitForElementToBePresent(By.id(workType), 3).click();
		String locator = "workTyLookup_lookup_datalist_" + query + "_selectionCheckBoxIcon_touch";
		af.waitForElementToBePresent(By.id(locator), 3).click();
		}

	/**
	 * Method to click Work Type filter 
	 * 
	 * @param  filterType
	 * @throws Exception
	 */
	public void selectFilter(String filterType) throws Exception {
		filterTypeSelect = (("//div[@class='mx--filter-filters-view-wrapper']//p[contains(text()," + "'").replaceAll("\\s", "") +filterType+("'"+ ")]").replaceAll("\\s", ""));
		af.waitForElementToBePresent(By.xpath(filterTypeSelect), af.DEFAULT_TIMEOUT_MS * 2);
		af.waitForElementToBePresent(By.xpath(filterTypeSelect),1000).click();
	}
	
	/**
	 * Method to click IT Assets Option filter 
	 * 
	 * @param  option
	 * @throws Exception
	 */
	public void selectFilterOption(String option) throws Exception {
		filterOptions = (("//div[@class='mx--filter-options-wrapper']//p[contains(text()," + "'").replaceAll("\\s", "") +option+("'"+ ")]").replaceAll("\\s", ""));
		af.waitForElementToBePresent(By.xpath(filterOptions), af.DEFAULT_TIMEOUT_MS * 2);
		af.waitForElementToBePresent(By.xpath(filterOptions),1000).click();
	}
	
	/**
	 * Method to check if filter with count 1 exists
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean checkBadgeCount() throws Exception {
		af.waitForElementToBePresent(By.xpath(filterBadgeCount), af.DEFAULT_TIMEOUT_MS * 2);
		return af.isElementExists(By.xpath(filterBadgeCount));
	}
	
	/**
	 * Click on Reset Filter
	 * 
	 * @throws Exception
	 */
	public void resetFilter() throws Exception {
		af.instantiateComponent(ButtonComponent.class, resetFilter).click();
	}
	
	/**
	 * Click on Save Work order
	 * 
	 * @throws Exception
	 */
	public void saveWO() throws Exception {
		logger.info("click started");
		af.instantiateComponent(ButtonComponent.class, selectTickLocator).click();
	}

	
	/**
	 * Click on Save Filter
	 * 
	 * @throws Exception
	 */
	public void saveFilter() throws Exception {
		af.instantiateComponent(ButtonComponent.class, saveFilterDone).click();
	}
	
	/**
	 * Click on Search Asset
	 * 
	 * @throws Exception
	 */
	public void searchAsset() throws Exception {
		af.waitForElementToBePresent(By.id(threeDotClickForAsset), af.DEFAULT_TIMEOUT_MS * 5);
		af.instantiateComponent(ButtonComponent.class, threeDotClickForAsset).click();
		af.instantiateComponent(ButtonComponent.class, searchIconClickAsset).click();
	}
	
	/**
	 * Click on Search Location
	 * 
	 * @throws Exception
	 */
	public void searchLocation() throws Exception {
		af.waitForElementToBePresent(By.id(threeDotClickForLocation), af.DEFAULT_TIMEOUT_MS * 5);
		af.instantiateComponent(ButtonComponent.class, threeDotClickForLocation).click();
		af.instantiateComponent(ButtonComponent.class, searchIconClickLocation).click();
	}
	
	/**
	 * Search for asset field on page
	 * @throws Exception
	 */
	public void searchForAssetsField() throws Exception {
		af.waitForElementToBePresent(By.id(assetInputLocator), af.DEFAULT_TIMEOUT_MS * 3);
		TextInputComponent textArea = af.instantiateComponent(TextInputComponent.class, assetInputLocator);
		textArea.setText("");
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
					TextInputComponent tic = af.instantiateComponent(TextInputComponent.class, sendTextinSearchForAsset);
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
				af.waitForElementToBePresent(By.xpath(assetOfFirstElementInListSelection), 1000).click();
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
			// Set text in the input field
			logger.info("Set text in the input field");
			af.waitForElementToBePresent(By.id(sendTextinSearchForLocation));
			TextInputComponent tic = af.instantiateComponent(TextInputComponent.class, sendTextinSearchForLocation);
			tic.setText(query);

			// Click on the search button
			logger.info("Click on the search button");
			af.waitForElementToBePresent(By.id(searchButtonLoctaionLocator));
			ButtonComponent searchActionButton = af.instantiateComponent(ButtonComponent.class,
					searchButtonLoctaionLocator);
			searchActionButton.click();
			af.waitForElementToBePresent(By.xpath(locationOfFirstElementInList), af.DEFAULT_TIMEOUT_MS * 5);
			af.waitForElementToBePresent(By.xpath(locationOfFirstElementInListSelection),1000).click();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
				
	/**
	 * Save Work Order
	 * 
	 * @throws Exception
	 */
	public void clickWorkOrderCreate() throws Exception {
		af.waitForElementToBePresent(By.id(workOrderCreationButtonClick), 5000);
		af.instantiateComponent(ButtonComponent.class, workOrderCreationButtonClick).click();
	}

	/**
	 * Method to get text on WO page for asset
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getAssetTextWOPage() throws Exception {
		af.waitForElementToBePresent(By.id(assetTextWOPage), 5000);
		return af.instantiateComponent(FieldComponent.class, assetTextWOPage).getValue();
	}
	

	/**
	 * Method to get text on WO page for location
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getLocationDescWOPage() throws Exception {
		return af.instantiateComponent(FieldComponent.class, locationDescWOPage).getValue();
	}
	
	/**
	 * Method to get text on WO page for location
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getLocationTextWOPage() throws Exception {
		return af.instantiateComponent(FieldComponent.class, locationTextWOPage).getValue();
	}

	/**
	 * Click on Assigned work native dropdown
	 * 
	 * @param index
	 * @throws Exception
	 */
	public void openNativeDropdown(int index) throws Exception {
		af.waitForElementToBePresent(By.id(dropdownSelect), 2000);
		DropDownComponent dropDownComponent = af.instantiateComponent(DropDownComponent.class, dropdownSelect);
		dropDownComponent.selectItemByIndex(index);
	}

	/**
	 * Click on Assigned work native dropdown
	 *
	 * @param text
	 * @throws Exception
	 */
	public void openNativeDropdown(String text) throws Exception {
		af.waitForElementToBePresent(By.id(dropdownSelect), 2000);
		DropDownComponent dropDownComponent = af.instantiateComponent(DropDownComponent.class, dropdownSelect);
		dropDownComponent.selectItemByDescription(text);
	}

	/**
	 * Method to click back chevron to navigate to WO details page
	 * 
	 * @throws Exception
	 */
	public void clickBackChevronfromDetails() throws Exception {
		af.waitForElementToBePresent(By.id(clickBackChevron), af.DEFAULT_TIMEOUT_MS * 5);
		af.instantiateComponent(ButtonComponent.class, clickBackChevron).click();
	}

	/**
	 * Method to check save discard pop-up
	 * 
	 * @throws Exception
	 */
	public void saveDiscradPop() throws Exception {
		af.waitForElementToBePresent(By.id(saveDiscradPrompt), af.DEFAULT_TIMEOUT_MS * 5);
	}
	
	/**
	 * Method to check search button availibility
	 * @return 
	 * 
	 * @throws Exception
	 */
	public boolean checkSearchButtonAvailbility() throws Exception {
		try {
			af.waitForElementToBePresent(By.id(searchIconButtonLocator),af.DEFAULT_TIMEOUT_MS * 10);
			af.isElementExists(By.id(searchIconButtonLocator)); 
			return true;
			
		} catch (Exception e) {
			  return false;
			}
		}
	
	/**
	 * Method to click discard button
	 * 
	 * @throws Exception
	 */
	public void clickDiscardButton() throws Exception {
		af.waitForElementToBePresent(By.id(discardButton), af.DEFAULT_TIMEOUT_MS * 5);
		af.instantiateComponent(ButtonComponent.class, discardButton).click();
	}

	/**
	 * Method to click save button
	 * 
	 * @throws Exception
	 */
	public void clickSaveButton() throws Exception {
		af.waitForElementToBePresent(By.id(saveButton), af.DEFAULT_TIMEOUT_MS * 5);
		af.instantiateComponent(ButtonComponent.class, saveButton).click();
	}
	
	// Generated by WCA for GP
	/**
		 * Enter Location
		 * 
		 * @param location
		 * @throws Exception
		 */
	public void enterLocation(String location) throws Exception {
		af.waitForElementToBePresent(By.id(locationInputLocator), af.DEFAULT_TIMEOUT_MS * 3);
		TextInputComponent textArea = af.instantiateComponent(TextInputComponent.class, locationInputLocator);
		textArea.setText(location);
		
	}

	// Generated by WCA for GP
	/**
	 * Get the value of asset text box in work order details page
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getAssetTextWODetailsPage() throws Exception {
		
		return af.instantiateComponent(NumberInputComponent.class, assetInputLocator).getValue();
	}
	
	// Generated by WCA for GP
	/**
	 * Get the value of location text box in work order details page
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getLocationTextWODetailsPage() throws Exception {
		return af.instantiateComponent(NumberInputComponent.class, locationInputLocator).getValue();
	}
	
	// Generated by WCA for GP
	/**
	 * Click Back Button
	 * 
	 * @throws Exception
	 */
	  public static void clickBackButton (MobileAutomationFramework maf) throws Exception {
	       
	        logger.info("Clicking Back Button");
	        if (maf.isIOS()) {
	            maf.clickNativeElement(MobileBy.xpath("//XCUIElementTypeButton[@name='Go to parent page']"));
	        } else {
	        	
	        	maf.waitForElementToBePresent(By.xpath("(//div[@class='" + backButtonLogDataPage + "'])[1]"), maf.DEFAULT_TIMEOUT_MS * 3);
	            WebElement webElement = maf.driver.findElement(By.xpath("(//div[@class='" + backButtonLogDataPage + "'])[1]"));
	            webElement.click();
	        }
	    }
	// Generated by WCA for GP
		/**
		 * Select Create WorkOrder is disabled
		 * 
		 * @throws Exception
		 */
		public boolean isCreateWODisabled() throws Exception {
			System.out.println(af.instantiateComponent(ButtonComponent.class, createWOLocator).isDisabled());
			return af.instantiateComponent(ButtonComponent.class, createWOLocator).isDisabled();
		}
		
	
		
		// Generated by WCA for GP
		/**
			 * Set Scheduled Start Date Without Time
			 * 
			 * @param year
			 * @param month
			 * @param date
			 * @param hrs
			 * @param min
			 * @throws Exception
			 */
			public void scheduledStartDateWithoutTime(int year, int month, int date) throws Exception {
					af.waitForElementToBePresent(By.id(scheduledDateStartLocator), 2);
					((MobileAutomationFramework) af).setDate(By.id(scheduledDateStartLocator), year, month, date);
				}

				// Generated by WCA for GP
				/**
				 * Set Scheduled Finish Date Without Time
				 * 
				 * @param year
				 * @param month
				 * @param date
				 * @param hrs
				 * @param min
				 * @throws Exception
				 */
				public void scheduledFinishDatewithoutTime(int year, int month, int date) throws Exception {
					af.waitForElementToBePresent(By.id(scheduleDateFinishLocator), 2);
					((MobileAutomationFramework) af).setDate(By.id(scheduleDateFinishLocator), year, month, date);
				}

				// Generated by WCA for GP
				/**
				 * Set Scheduled Start Date
				 * 
				 * @param year
				 * @param month
				 * @param date
				 * @throws Exception
				 */
				public void scheduledStartDateGetTime(int year, int month, int date) throws Exception {
					af.waitForElementToBePresent(By.id(scheduledDateStartLocator), 2);
					((MobileAutomationFramework) af).setDate(By.id(scheduledDateStartLocator), year, month, date);
				}

				// Generated by WCA for GP
				/**
				 * Get Scheduled Start Date Time
				 * 
				 * @return
				 * @throws Exception
				 */
				public String scheduledStartDateGetTime() throws Exception {
					return af.instantiateComponent(NumberInputComponent.class, scheduleStartTimeLocator).getValue();
				}

				// Generated by WCA for GP
				/**
				 * Get Scheduled Finish Date Time
				 * 
				 * @return
				 * @throws Exception
				 */
				public String scheduledFinishDateGetTime() throws Exception {

					return af.instantiateComponent(NumberInputComponent.class, scheduleTimeFinishLocator).getValue();
				}

				// Generated by WCA for GP
				/**
				 * Select create quick report
				 * 
				 * @throws Exception
				 */
				public void createQuickReport() throws Exception {
					af.waitForElementToBePresent(By.cssSelector(createQuickReport), af.DEFAULT_TIMEOUT_MS * 3).click();
				}
		
				// Generated by WCA for GP
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
				
				// Generated by WCA for GP
				/**
				 * Enter Location
				 * 
				 * @param location
				 * @throws Exception
				 */
				public void locationNumber(String locationNum) throws Exception {
					TextInputComponent textInput = af.instantiateComponent(TextInputComponent.class, locationId);
					textInput.clearField();
					textInput.typeText(locationNum);
				}
				
				/**
				 * Method to check if filter count exists
				 * 
				 * @return
				 * @throws Exception
				 */
				public boolean checkBadgeCountNotExists() throws Exception {
					return af.isElementExists(By.xpath(filterBadgeCount));
				}
				
				/**
				 * Method to serach location in quick reporting
				 * 
				 * @param query
				 * @return
				 * @throws Exception
				 */
				public boolean searchForLocationInQuickReporting(String query) throws Exception {
					af.waitForElementToBePresent(By.id(threeDotClickForLocation), af.DEFAULT_TIMEOUT_MS * 5);
					af.instantiateComponent(ButtonComponent.class, threeDotClickForLocation).click();
					af.instantiateComponent(ButtonComponent.class, searchIconClickLocation).click();
					try {
						// Set text in the input field
						logger.info("Set text in the input field");
						af.waitForElementToBePresent(By.id(sendTextinSearchForLocation));
						TextInputComponent tic = af.instantiateComponent(TextInputComponent.class,
								sendTextinSearchForLocation);
						tic.setText(query);

						// Click on the search button
						logger.info("Click on the search button");
						af.waitForElementToBePresent(By.id(searchButtonLoctaionLocator));
						ButtonComponent searchActionButton = af.instantiateComponent(ButtonComponent.class,
								searchButtonLoctaionLocator);
						searchActionButton.click();
						af.waitForElementToBePresent(By.xpath(locationOfFirstElementInListInQuickReporting),
								af.DEFAULT_TIMEOUT_MS * 50);
						af.waitForElementToBePresent(By.xpath(locationOfFirstElementInListInQuickReporting), 1000)
								.click();
						return true;
					} catch (Exception e) {
						return false;
					}
				}			
}
