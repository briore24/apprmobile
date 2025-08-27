package com.ibm.maximo.technician.page;

import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.maximo.automation.mobile.AbstractAutomationFramework;
import com.ibm.maximo.components.ButtonComponent;
import com.ibm.maximo.components.LabelComponent;
import com.ibm.maximo.components.TextInputComponent;
import com.ibm.maximo.components.WrappedTextComponent;

public class MultipleAssetsAndLocationsPage {
	private final static Logger logger = LoggerFactory.getLogger(MultipleAssetsAndLocationsPage.class);
	private String searchIconLocator = "kj5n5_cards_searchbuttongroup_searchIconButton";
	private String searchInputLocator = "kj5n5_cards_search_searchInput"; 
	private String searchButtonLocator = "kj5n5_cards_search_searchButton" , searchButtonMapLocator = "jy36x_items_search_searchButton" ,openDetailsOnMapView = "a59z_[0]";
	private String clearSearchButtonLocator = "kj5n5_cards_search_clearButton";
	private String resultsLabelLocator = "recordlabel";
	private String AssetLocationsChevron = "cardtemplate2[0]_chevron";
	private String backChevron = "mmrdr-pageheader_breadcrumb_icon";
	private String checkedAssetLocationIcon = "kw45k[0]", uncheckedAssetLocationIcon = "e5dxn[0]";
	private String listIconFromMapView = "pageTemplateSwitch_v57r9_0_icon";
	private String sortingIcon="kj5n5_cards_searchbuttongroup_mryw3";
	private String mapViewID="pageTemplateSwitch_p2v_6_1_icon";
	private String inspectionButton = "ngjd3[0]_bpd44[0]" , createFollowupWO = "ngjd3[0]_wmdyj[0]", meterIcon = "ngjd3[0]_wagwp[0]";
	private String serviceAddressIcon ="ngjd3[0]_zp578[0]";
	private String searchOnMappage = "jy36x_items_search_searchInput"; 
	private String description = "multiAssetLocCi_2_field_fieldValue0";

	private AbstractAutomationFramework af;

	public MultipleAssetsAndLocationsPage(AbstractAutomationFramework af) throws Exception {
		this.af = af;
	}
	
	/**
	 * Get work order description
	 * 
	 * @return String
	 * @throws Exception
	 */
	public String getTextMultipleAssetsAndLocations() throws Exception {
		af.waitForElementToBePresent(By.id(description), af.DEFAULT_TIMEOUT_MS * 100);
		return af.instantiateComponent(WrappedTextComponent.class, description).getParentLabel();
	}
	
	// click on Meter Button on multiassetlocation page
	public void clickMeterButton() throws Exception {
		checkPresentAndClick(meterIcon);
	}
	
	/**
	 * Check Present locator and click on same locator
	 * 
	 * @return boolean whether successful
	 * @throws Exception
	 */
	public boolean checkPresentAndClick(String locator) throws Exception {
		try {
			af.waitForElementToBePresent(By.id(locator));
			af.instantiateComponent(ButtonComponent.class, locator).click();
			return true;
		} catch (Exception e) {
			logger.info(locator + " > Locator is not present and click failed");
			return false;
		}
	}

	// verify that Meter Icon is present on multiassetlocation page
	public boolean verifyMeterIconDisplayed() throws Exception {
		af.waitForElementToBePresent(By.id(meterIcon), af.DEFAULT_TIMEOUT_MS * 10);
		return af.isElementExists(By.id(meterIcon));
	}
	
	// verify that create follow up WO is present on multiassetlocation page
	public boolean verifyCreateFollowupIconDisplayed() throws Exception {
		af.waitForElementToBePresent(By.id(createFollowupWO), af.DEFAULT_TIMEOUT_MS * 10);
		return af.isElementExists(By.id(createFollowupWO));
	}
	
	/**
	 * Method for clicking on List view icon
	 *
	 * @return void
	 * @throws Exception
	 */
	public void clickOnListViewButton() throws Exception {
		af.waitForElementToBePresent(By.id(listIconFromMapView));
		clickButton(listIconFromMapView);
	}
	
	/**
	 * Method for clicking on Map View Icon
	 *
	 * @return void
	 * @throws Exception
	 */
	public void clickOnMapViewButton() throws Exception {
		af.waitForElementToBePresent(By.id(mapViewID));
		clickButton(mapViewID);
	}
	
	/**
	 * Method for clicking on create followup WO button
	 *
	 * @return void
	 * @throws Exception
	 */
	public void clickOnCreateFollowupWOButton() throws Exception {
		af.waitForElementToBePresent(By.id(createFollowupWO));
		clickButton(createFollowupWO);
	}
	
	// verify that Service Address Icon is present on multiassetlocation page
	public boolean verifyServiceAddressIconDisplayed() throws Exception {
		af.waitForElementToBePresent(By.id(serviceAddressIcon), af.DEFAULT_TIMEOUT_MS * 10);
		return af.isElementExists(By.id(serviceAddressIcon));
	}
	
	/**
	 * Method for clicking on Service Address button
	 *
	 * @return void
	 * @throws Exception
	 */
	public void clickOnServiceAddressButton() throws Exception {
		af.waitForElementToBePresent(By.id(serviceAddressIcon));
		clickButton(serviceAddressIcon);
	}
	
	// verify that Search on Map view is present on multiassetlocation page
	public boolean verifySearchOnMapViewDisplayed() throws Exception {
		af.waitForElementToBePresent(By.id(searchOnMappage), af.DEFAULT_TIMEOUT_MS * 10);
		return af.isElementExists(By.id(searchOnMappage));
	}
	
	
	/**
	 * Method for clicking on inspection button
	 *
	 * @return void
	 * @throws Exception
	 */
	public void clickInspectionButton() throws Exception {
		af.waitForElementToBePresent(By.id(inspectionButton));
		clickButton(inspectionButton);
	}
	
	/**
	 * Method for clicking on check button of multiassetlocation of asset/location
	 *
	 * @return boolean
	 * @throws Exception
	 */
	public boolean checkOnMultiAssetLocationIcon() throws Exception {
		af.waitForElementToBePresent(By.id(uncheckedAssetLocationIcon));
		clickButton(uncheckedAssetLocationIcon);
		af.waitForElementToBePresent(By.id(checkedAssetLocationIcon));
		return af.isElementExists(By.id(checkedAssetLocationIcon));
	}
	
	/**
	 * Method for clicking on uncheck button of multiassetlocation of asset/location
	 *
	 * @return boolean
	 * @throws Exception
	 */
	public boolean unCheckOnMultiAssetLocationIcon() throws Exception {
		af.waitForElementToBePresent(By.id(checkedAssetLocationIcon));
		clickButton(checkedAssetLocationIcon);
		af.waitForElementToBePresent(By.id(uncheckedAssetLocationIcon));
		return af.isElementExists(By.id(uncheckedAssetLocationIcon));
	}

	/**
	 * Method for checking of inspection button is displayed on multiassetlocation page
	 *
	 * @return boolean
	 * @throws Exception
	 */
	public boolean IsInspectionButtonDisplayed() throws Exception {
		af.waitForElementToBePresent(By.id(inspectionButton));
		return af.isElementExists(By.id(inspectionButton));
	}
	
	public boolean search(String query) throws Exception {	
		if (af.isElementExists(By.id(searchInputLocator))) {

			if (af.isElementExists(By.id(clearSearchButtonLocator))) {
				// Clear the search filter
				logger.info("Clear the search filter");
				clickClearButton();
			}
			// Wait
			af.waitForElementToNotBePresent(By.cssSelector("#loading0_LongPress"), af.DEFAULT_TIMEOUT_MS * 5);
			if (af.isElementExists(By.id(searchInputLocator))) {

				// Set text in the input field
				logger.info("Set text in the input field");
				af.waitForElementToBePresent(By.id(searchInputLocator));
				TextInputComponent tic = af.instantiateComponent(TextInputComponent.class, searchInputLocator);
				tic.setText(query);

				// Click on the search button
				logger.info("Click on the search button");
				af.waitForElementToBePresent(By.id(searchButtonLocator));
				ButtonComponent searchActionButton = af.instantiateComponent(ButtonComponent.class,
						searchButtonLocator);
				searchActionButton.click();

				// Wait
				af.waitForElementToNotBePresent(By.cssSelector("#loading0_LongPress"), af.DEFAULT_TIMEOUT_MS * 5);

				try {
					af.waitForElementToBePresent(By.cssSelector("#" + resultsLabelLocator), 1);
					return true;
				} catch (Exception e) {
					return false;
				}
			}

		} else {

			af.waitForElementToBePresent(By.id(searchIconLocator));
			// Open the search bar
			logger.info("Open the search bar");
			ButtonComponent searchIconButton = af.instantiateComponent(ButtonComponent.class, searchIconLocator);
			searchIconButton.click();

			if (af.isElementExists(By.id(clearSearchButtonLocator))) {
				// Clear the search filter
				logger.info("Clear the search filter");
				clickClearButton();
			}

			// Set text in the input field
			logger.info("Set text in the input field");
			af.waitForElementToBePresent(By.id(searchInputLocator));
			TextInputComponent tic = af.instantiateComponent(TextInputComponent.class, searchInputLocator);
			tic.setText(query);
			// Click on the search button
			logger.info("Click on the search button");
			af.waitForElementToBePresent(By.id(searchButtonLocator));
			ButtonComponent searchActionButton = af.instantiateComponent(ButtonComponent.class, searchButtonLocator);
			searchActionButton.click();
			// Wait
			af.waitForElementToNotBePresent(By.cssSelector("#loading0_LongPress"), af.DEFAULT_TIMEOUT_MS * 5);
			try {
				af.waitForElementToBePresent(By.cssSelector("#" + resultsLabelLocator), 1);
				return true;
			} catch (Exception e) {
				return false;
			}
		}
		return false;
	}

	// search on Map view
	public void searchOnMapView(String value) throws Exception {
		af.waitForElementToBePresent(By.id(searchOnMappage));
		TextInputComponent tic = af.instantiateComponent(TextInputComponent.class, searchOnMappage);
		tic.setText(value);
		ButtonComponent searchActionButton = af.instantiateComponent(ButtonComponent.class,
				searchButtonMapLocator);
		searchActionButton.click();
	}
			
	// click on first entry on Map view
	public void openDetailsOnMapView() throws Exception {
		af.waitForElementToBePresent(By.id(openDetailsOnMapView));
		ButtonComponent searchActionButton = af.instantiateComponent(ButtonComponent.class, openDetailsOnMapView);
		searchActionButton.click();
	}		

	// Clears the search input text
	public void clickClearButton() throws Exception {
		af.waitForElementToBePresent(By.id(clearSearchButtonLocator));
		af.instantiateComponent(ButtonComponent.class, clearSearchButtonLocator).click();
	}

	// Click on checkmark and search input text
	public boolean clearAndSearch(String query) throws Exception {
		af.waitForElementToBePresent(By.id(clearSearchButtonLocator));
		af.instantiateComponent(ButtonComponent.class, clearSearchButtonLocator).click();
		TextInputComponent tic = af.instantiateComponent(TextInputComponent.class, searchInputLocator);
		tic.setText(query);
		ButtonComponent searchButton2 = af.instantiateComponent(ButtonComponent.class, searchButtonLocator);
		searchButton2.click();
		af.waitForElementToNotBePresent(By.cssSelector("#loading0_LongPress"), af.DEFAULT_TIMEOUT_MS * 5);
		try {
			af.waitForElementToBePresent(By.cssSelector("#" + resultsLabelLocator), 1);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	// Click on back chevron
	public void clickBackChevron() {
		af.instantiateComponent(ButtonComponent.class, backChevron).click();
	}

	public String getSearchResult() {
		LabelComponent labelComponent = af.instantiateComponent(LabelComponent.class, resultsLabelLocator);
		return labelComponent.getLabel();
	}

	// Click on chevron Icon on Asset/Locations from Multi Assets and Locations 
	public void openAssetsDetails() throws Exception {
		af.instantiateComponent(ButtonComponent.class, AssetLocationsChevron).click();
		//af.waitForElementToNotBePresent(By.id("em97_-pageheader_buttongroup_e4ppv_loading"), 2000);
	}

	/**
	 * This is generic reusable method to click button
	 *
	 * @param buttonId {string} value of id where click element
	 * @throws Exception
	 */
	public void clickButton(String buttonId) throws Exception {
		af.waitForElementToBePresent(By.id(buttonId), 12000);
		af.instantiateComponent(ButtonComponent.class, buttonId).click();
	}

}
