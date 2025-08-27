package com.ibm.maximo.technician.page;

import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.maximo.automation.mobile.AbstractAutomationFramework;
import com.ibm.maximo.automation.mobile.MobileAutomationFramework;
import com.ibm.maximo.components.ButtonComponent;
import com.ibm.maximo.components.DropDownComponent;
import com.ibm.maximo.components.LabelComponent;
import com.ibm.maximo.components.TagComponent;
import com.ibm.maximo.components.TextInputComponent;

public class AssetListPage {
	private final static Logger logger = LoggerFactory.getLogger(AssetListPage.class);
	private String infoLocator = "p26j8";
	private String searchIconLocator = "jgyq6_cards_searchbuttongroup_searchIconButton";
	private String searchInputLocator = "jgyq6_cards_search_searchInput";
	private String searchButtonLocator = "jgyq6_cards_search_searchButton"; // or checkmark
	private String clearSearchButtonLocator = "jgyq6_cards_search_clearButton";
	private String resultsLabelLocator = "recordlabel";
	private String AssetChevron = "xx9jk[0]_chevron";
	private String metersButton = "zxejr[0]_bag6e[0]";
	private String crossButton = "assetmeterReadingDrawer_button";
	private String myAssetsFilterOnAssetListPage = "j5web";
	private String refreshButton = "jgyq6_cards_searchbuttongroup_jgyq6_cards_RefreshButon";
	private String myAssetPageTitle = "j5web_option_activeAssetListDS";
	private String recordLabel = "recordlabel";
	private String noAssetFound = "noDataWrapper";
	private String noDataRefreshButton = "jgyq6_cards_RefreshButon";
	private String loadingIcon= "#loading0_LongPress";
	private String assetTitle= "rnbgv[0]";
	private String assetPhoto = "ev27g[0]";
	

	private AbstractAutomationFramework af;

	public AssetListPage(AbstractAutomationFramework af) throws Exception {
		this.af = af;

		af.waitForElementToNotBePresent(By.cssSelector("#loading0_LongPress"), af.DEFAULT_TIMEOUT_MS * 5);
		if (af instanceof MobileAutomationFramework) {
			logger.info("is Mobile");
		}
	}

	public String getInfo() throws Exception {
		logger.info("go to get info method");
		af.waitForElementToBePresent(By.id(infoLocator));
		ButtonComponent buttonComponent = af.instantiateComponent(ButtonComponent.class, infoLocator);
		return buttonComponent.getButtonText();
	}

	public boolean search(String query) throws Exception {
		
		af.waitForElementToNotBePresent(By.cssSelector("#loading0_LongPress"), af.DEFAULT_TIMEOUT_MS * 5);
		
		af.waitForElementToBePresent(By.id(searchIconLocator), af.DEFAULT_TIMEOUT_MS * 5);
		ButtonComponent searchconButton = af.instantiateComponent(ButtonComponent.class, searchIconLocator);
		searchconButton.click();

		af.waitForElementToBePresent(By.id(searchInputLocator), af.DEFAULT_TIMEOUT_MS * 5);
		TextInputComponent tic = af.instantiateComponent(TextInputComponent.class, searchInputLocator);
		tic.setText(query);

		af.waitForElementToBePresent(By.id(searchButtonLocator), af.DEFAULT_TIMEOUT_MS * 5);
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

	// Click on magnify button
	public void clickSearchButton() throws Exception {
		af.waitForElementToBePresent(By.id(searchIconLocator),af.DEFAULT_TIMEOUT_MS * 50);
		af.instantiateComponent(ButtonComponent.class, searchIconLocator).click();
	}

	// Clears the search input text
	public void clickClearButton() throws Exception {
		af.waitForElementToBePresent(By.id(clearSearchButtonLocator));
		af.instantiateComponent(ButtonComponent.class, clearSearchButtonLocator).click();
	}

	// Click on checkmark and search input text
	public boolean clearAndSearch(String query) throws Exception {
		af.waitForElementToBePresent(By.id(clearSearchButtonLocator), af.DEFAULT_TIMEOUT_MS * 50);
		af.instantiateComponent(ButtonComponent.class, clearSearchButtonLocator).click();

		logger.info("cleared >>");
		af.waitForElementToBePresent(By.id(searchInputLocator),af.DEFAULT_TIMEOUT_MS * 50);
		TextInputComponent tic = af.instantiateComponent(TextInputComponent.class, searchInputLocator);
		tic.setText(query);
		
		logger.info("typed");
		af.waitForElementToBePresent(By.id(searchButtonLocator),af.DEFAULT_TIMEOUT_MS * 50);
		ButtonComponent searchButton2 = af.instantiateComponent(ButtonComponent.class, searchButtonLocator);
		searchButton2.click();

		af.waitForElementToNotBePresent(By.cssSelector("#loading0_LongPress"), af.DEFAULT_TIMEOUT_MS * 50);
		try {
			af.waitForElementToBePresent(By.cssSelector("#" + resultsLabelLocator), 1);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public String getSearchResult() {
		LabelComponent labelComponent = af.instantiateComponent(LabelComponent.class, resultsLabelLocator);
		return labelComponent.getLabel();
	}

	// Click on chevron Icon from asset
	public void openAssetDetails() {
		af.instantiateComponent(ButtonComponent.class, AssetChevron).click();
	}

	// Click on 'X' to close the work log drawer
	public void closeDrawer() {
		af.instantiateComponent(ButtonComponent.class, crossButton).click();
	}

	/**
	 * Get 'My Assets' filter on Asset list page
	 * 
	 * @return String
	 * @throws Exception
	 */
	public String getMyAssetsFilterOnAssetListPage() throws Exception {
		DropDownComponent dropDownComponent = af.instantiateComponent(DropDownComponent.class,
				myAssetsFilterOnAssetListPage);
		return dropDownComponent.getSelected();
	}

	// Click on refresh button
	public void clickRefreshButton() throws Exception {
		af.waitForElementToBePresent(By.id(refreshButton), af.DEFAULT_TIMEOUT_MS * 3);
		af.instantiateComponent(ButtonComponent.class, refreshButton).click();
	}

	/**
	 * Open the meters drawer
	 * 
	 * @return MetersPage
	 * @throws Exception
	 */
	public MetersPage clickMetersButton() throws Exception {
		af.instantiateComponent(ButtonComponent.class, metersButton).click();
		return new MetersPage(af);
	}

	/**
	 * This is method to get string value of element
	 *
	 * @param elementID {string} value of id to get text value of it
	 * @throws Exception
	 */
	public String dropDownDefaultValue() throws Exception {
		af.waitForElementToBePresent(By.id(myAssetPageTitle), af.DEFAULT_TIMEOUT_MS * 3);
		LabelComponent defaultValue = af.instantiateComponent(LabelComponent.class, myAssetPageTitle);
		return defaultValue.getValue();
	}

	// Method for validate the Record Label shows on Page
	public boolean recordLabel() throws Exception {
		try {
			af.waitForElementToBePresent(By.id(recordLabel), af.DEFAULT_TIMEOUT_MS * 3);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Method to get text for no Asset records found
	 * 
	 * @throws Exception
	 */
	public String getNoAssetFound() throws Exception {
		af.waitForElementToBePresent(By.id(noAssetFound));
		LabelComponent labelComponent = af.instantiateComponent(LabelComponent.class, noAssetFound);
		return labelComponent.getValue();
	}

	/**
	 * Click on Assets created by me native dropdown
	 * 
	 * @param index
	 * @throws Exception
	 */
	public void openNativeDropdown(int index) throws Exception {
		af.waitForElementToBePresent(By.id(myAssetsFilterOnAssetListPage), 2000);
		DropDownComponent dropDownComponent = af.instantiateComponent(DropDownComponent.class,
				myAssetsFilterOnAssetListPage);
		dropDownComponent.selectItemByIndex(index);
	}

	// Clears the search input text
	public void clickNoDataRefreshButton() throws Exception {
		try {
		af.instantiateComponent(ButtonComponent.class, noDataRefreshButton).click();
		}
		catch (Exception e) {
			// TODO: handle exception
			logger.info("Do not have Refresh button when No records on the page");
		}
		
	}
	
	/**
	 * check meter button visible
	 *
	 * @return Boolean
	 * @throws Exception
	 */
	public boolean isMeterButtonVisible() throws Exception {
		try {
			Thread.sleep(100);
			return af.isElementExists(By.id(metersButton));
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * wait for loading icon invisible
	 *
	 * @throws Exception
	 */
	public void waitForLoadingNotVisible()throws Exception {
		af.waitForElementToNotBePresent(By.cssSelector(loadingIcon), af.DEFAULT_TIMEOUT_MS * 5);
	}
	public void waitForLoadingNotVisible(int timeout)throws Exception {
		af.waitForElementToNotBePresent(By.cssSelector(loadingIcon), timeout * 5);
	}
	
	/**
	 * Get Asset title 
	 * 
	 * @return String
	 * @throws Exception
	 */
	public String getTextAssetTitle() throws Exception {
		af.waitForElementToBePresent(By.id(assetTitle), af.DEFAULT_TIMEOUT_MS * 50);
		String assetTitleString = af.instantiateComponent(TagComponent.class, assetTitle).getLabel();

		logger.info("assetTitle:" + assetTitleString);
		return assetTitleString;
	}
	
	/**
	 * check Asset Photo displayed on list page
	 *
	 * @return Boolean
	 * @throws Exception
	 */
	public boolean isAssetPhotoButtonAvailable(Boolean doWaitForElement) throws Exception {
		try {
			if (doWaitForElement) {
				af.waitForElementToBePresent(By.id(assetPhoto), af.DEFAULT_TIMEOUT_MS * 1);
			}
			return af.isElementExists(By.id(assetPhoto));
		} catch (Exception e) {
			logger.info("Asset Photo is not available");
			return false;
		}
	}

}
