package com.ibm.maximo.technician.page;

import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.maximo.automation.mobile.AbstractAutomationFramework;
import com.ibm.maximo.components.ButtonComponent;
import com.ibm.maximo.components.LabelComponent;
import com.ibm.maximo.components.NumberInputComponent;
import com.ibm.maximo.components.TextInputComponent;

public class AddMaterialPage {

	private AbstractAutomationFramework af;
	private String openAddMaterialLookup = "dmn9g";
	private String searchMaterial = "materialLookup_lookup_datalist_search_searchInput";
	private String searchButton = "materialLookup_lookup_datalist_search_searchButton";
	private String rightChevronforSearchMaterial = "dmn9g";
	private String searchResultList = "materialLookup_lookup_datalist__scroller";
	private String storeroomPageHeaderTitle = "storeRoomLookup_sliding_drawer_headerLabel";
	private String rotatingAssetPageHeaderTitle = "rotatingAssetLookup_sliding_drawer_headerLabel";
	private String saveMaterial = "g8ga8_abake";
	private String materialQuantity = "mz8_2";
	private String materialQuanText="//div[@id='mz8_2wrapper']/div/div/div/div/div/button[1]";
	private String taskChevron = "z4x3r";
	private String selectTypeChevron = "dwpwj";
	private String selectTypeReturnChevron = "transactionTypeLookup_lookup_datalist_ISSUETYP|RETURN_selectionCheckBoxIcon_touch";
	private String assetNumber = "zmvp5_fieldValue0";
	private String materialDrawerCloseBtn = "materialsDrawer_button";
	private String saveChangesBtn = "//div[@class='mx--button-container']//button[@id='graphite_unsaved_changes_button_group_graphite_unsaved_changes_primary_button']";
	private String discardChangesBtn = "//div[@class='mx--button-container']//button[@id='graphite_unsaved_changes_button_group_graphite_unsaved_changes_secondary_button']";
	private String discardBtnLocator = "//div[@class='mx--button-container']//button[@id='saveDiscardLaborsDialog_button_group_saveDiscardLaborsDialog_secondary_button']";
    private String firstElementInListLocator = "(//div[@class='mx--tile mx--tile--not-selected'])[1]";
    private String rotatingAssetNumField = "(//div[@class='fieldContainer']//p[contains(text(),'TI4CX')]";
    private String storeRoomLabel = "storeRoomListLookup_sliding_drawer_headerLabel";
    private String selectFirstStoreRoom = "//*[@id='storeRoomListLookup_sliding_drawer_body']//div//div//button[@tabindex='0']";
	private final static Logger logger = LoggerFactory.getLogger(AddMaterialPage.class);
    private String discardChangeClick ="saveDiscardLaborsDialog_button_group_saveDiscardLaborsDialog_secondary_button";
    private String actualTask= "//Button[starts-with(@id,'toolTaskLookup_lookup_datalist')]";
	public AddMaterialPage(AbstractAutomationFramework af) {
		this.af = af;
	}

	/**
	 * Click to Add material Item
	 *
	 * @throws Exception
	 */
	public void rightChevronforSearchMaterial() throws Exception {
		af.waitForElementToBePresent(By.id(rightChevronforSearchMaterial), af.DEFAULT_TIMEOUT_MS * 2);
		af.instantiateComponent(ButtonComponent.class, rightChevronforSearchMaterial).click();
	}


	/**
	 * Click to Add Storeroom
	 *
	 * @throws Exception
	 */
	public void clickToSelectFirstElementInList() throws Exception {
		af.waitForElementToBePresent(By.xpath(firstElementInListLocator), af.DEFAULT_TIMEOUT_MS * 5);
		af.waitForElementToBePresent(By.xpath(firstElementInListLocator),1000).click();
	}
	/**
	 * Select a given material
	 * 
	 * @param query
	 * @return
	 * @throws Exception
	 */
	public boolean search(String query) throws Exception {
		af.waitForElementToBePresent(By.id(searchMaterial));
		ButtonComponent searchconButton = af.instantiateComponent(ButtonComponent.class, searchMaterial);
		searchconButton.click();
		logger.info("Search bar opened");

		TextInputComponent tic = af.instantiateComponent(TextInputComponent.class, searchMaterial);
		tic.setText(query);
		logger.info("Search value inputed");
		af.waitForElementToBePresent(By.id(searchMaterial));
		ButtonComponent searchButton2 = af.instantiateComponent(ButtonComponent.class, searchButton);
		searchButton2.click();
		logger.info("Search button clicked");

		af.waitForElementToNotBePresent(By.cssSelector("#loading0_LongPress"), af.DEFAULT_TIMEOUT_MS * 5);

		return af.isElementExists(By.id(searchResultList));
	}

	/**
	 * Click Select Material
	 *
	 * @throws Exception
	 */
	public boolean clickIdentifiedMaterialFromList(String itemId) throws Exception {
		try {
			String locatorId = "materialLookup_lookup_datalist_" + itemId + "_selectionCheckBoxIcon_touch";
			logger.info("locator >" + locatorId);
			af.waitForElementToBePresent(By.id(locatorId)).click();
			logger.info("Material selected:" + itemId);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * Method to check Rotating Asset Num
	 * 
	 * @param  filterType
	 * @throws Exception
	 */
	public boolean getRotatingAssetNum(String rotAssetNum) throws Exception {
		rotatingAssetNumField = (("//div[@class='fieldValues']//p[contains(text()," + "'").replaceAll("\\s", "") +rotAssetNum+("'"+ ")]").replaceAll("\\s", ""));
		af.waitForElementToBePresent(By.xpath(rotatingAssetNumField),3000);
		return af.isElementExists(By.xpath(rotatingAssetNumField));
	}

	/**
	 * Click Select Material
	 *
	 * @throws Exception
	 */
	public boolean selectTask(String taskId) throws Exception {
		try {
			// click select task chevron
			af.waitForElementToBePresent(By.id(taskChevron)).click();
			af.waitForElementToBePresent(By.xpath(actualTask)).click();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Click save Material
	 *
	 * @throws Exception
	 */
	public void clickSaveButton() throws Exception {
		af.waitForElementToBePresent(By.id(saveMaterial), af.DEFAULT_TIMEOUT_MS * 5);
		af.instantiateComponent(ButtonComponent.class, saveMaterial).click();
		logger.info("Save button clicked");
	}

	/**
	 * Click Add Material Lookup
	 *
	 * @throws Exception
	 */
	public void openAddMterialLookup() throws Exception {
		af.waitForElementToNotBePresent(By.id(openAddMaterialLookup), 5000);
		af.instantiateComponent(ButtonComponent.class, openAddMaterialLookup).click();
	}
	
	/**
	 * Click Close Material Drawer
	 *
	 * @throws Exception
	 */
	public void closeMaterialDrawer() throws Exception {
		af.waitForElementToBePresent(By.id(materialDrawerCloseBtn),1000).click();
	}
	
	/**
	 * Click Discard Changes
	 *
	 * @throws Exception
	 */
	public void clickDiscardChanges() throws Exception {
		af.waitForElementToBePresent(By.xpath(discardChangesBtn),1000).click();
	}
	
	
	/**
	 * Click Save Changes
	 *
	 * @throws Exception
	 */
	public void clickSaveChanges() throws Exception {
		af.waitForElementToBePresent(By.xpath(saveChangesBtn),1000).click();
	}
	
	/**
	 * Click Discard button
	 *
	 * @throws Exception
	 */
	public void clickDiscardButton() throws Exception {
		af.waitForElementToBePresent(By.xpath(discardBtnLocator),1000).click();
	}
	
	/**
	 * Input quantity for the material
	 * 
	 * @param quantity
	 * @throws Exception
	 */
	public void setQuantity(String quantity) throws Exception {
		TextInputComponent textComponent_quantityValue = af.instantiateComponent(TextInputComponent.class,
				materialQuantity);
		textComponent_quantityValue.clearField();
		textComponent_quantityValue.setText(quantity);
	}

	/**
	 * Select RETURN type for the material
	 * 
	 * @throws Exception
	 */
	public void selectTypeAsReturn() throws Exception {
		af.instantiateComponent(ButtonComponent.class, selectTypeChevron).click();
		af.waitForElementToBePresent(By.id(selectTypeReturnChevron), af.DEFAULT_TIMEOUT_MS * 5);
		af.instantiateComponent(ButtonComponent.class, selectTypeReturnChevron).click();
	}

	/**
	 * Method to get populated Asset Number
	 * 
	 * @return {String} asset number
	 */
	public String getAssetNumber() throws Exception {
		af.waitForElementToBePresent(By.id(assetNumber), af.DEFAULT_TIMEOUT_MS * 5);
		return af.instantiateComponent(LabelComponent.class, assetNumber).getLabel();
	}
	
	/**
	 * Method to get Storeroom header title
	 * 
	 * @return {String} asset number
	 */
	public String getStoreroomPageTitle() throws Exception {
		af.waitForElementToBePresent(By.id(storeroomPageHeaderTitle), af.DEFAULT_TIMEOUT_MS * 5);
		return af.instantiateComponent(LabelComponent.class, storeroomPageHeaderTitle).getLabel();
	}
	/**
	 * Method to get Rotating Asset header title
	 * 
	 * @return {String} asset number
	 */
	public String getRotatingAssetPageTitle() throws Exception {
		af.waitForElementToBePresent(By.id(rotatingAssetPageHeaderTitle), af.DEFAULT_TIMEOUT_MS * 5);
		return af.instantiateComponent(LabelComponent.class, rotatingAssetPageHeaderTitle).getLabel();
	}
	
	/**
	 * Method to get Storeroom header title
	 * 
	 * @return {String} asset number
	 */
	public String verifyItemValue() throws Exception {
		af.waitForElementToBePresent(By.id("rg2mg_value"), af.DEFAULT_TIMEOUT_MS * 5);
		return af.instantiateComponent(LabelComponent.class, "rg2mg_value").getLabel();
	}
	
	/**
	 * Method to get Storeroom header title
	 * 
	 * @return {String} asset number
	 */
	public String verifyStoreroomPageTitle() throws Exception {
		af.waitForElementToBePresent(By.id(storeRoomLabel), af.DEFAULT_TIMEOUT_MS * 5);
		return af.instantiateComponent(LabelComponent.class, storeRoomLabel).getLabel();
	}
	
	/**
	 * Method to get Storeroom value
	 * 
	 * @return {String} asset number
	 */
	public String getStoreRoomValueAfterSelection() throws Exception {
		af.waitForElementToBePresent(By.id("v_a8x_fieldValue0"), af.DEFAULT_TIMEOUT_MS * 5);
		return af.instantiateComponent(LabelComponent.class, "v_a8x_fieldValue0").getLabel();
	}
	
	/**
	 * Method to get Storeroom value
	 * 
	 * @return {String} asset number
	 */
	public String getStoreRoomPlaceValue() throws Exception {
		af.waitForElementToBePresent(By.id("v_a8x_fieldValue1"), af.DEFAULT_TIMEOUT_MS * 5);
		return af.instantiateComponent(LabelComponent.class, "v_a8x_fieldValue1").getLabel();
	}
	
	/**
	 * Method to get Quantity value
	 * 
	 * @return {String} Quantity Label
	 */
	public String getQuantityLabel() throws Exception {
		af.waitForElementToBePresent(By.id("g_ywm_field_help_fieldHelpLabel"), af.DEFAULT_TIMEOUT_MS * 5);
		return af.instantiateComponent(LabelComponent.class, "g_ywm_field_help_fieldHelpLabel").getLabel();
	}
	
	/**
	 * Click to Add Storeroom
	 *
	 * @throws Exception
	 */
	public void clickToSelectFirstElementInStoreRoom() throws Exception {
		af.waitForElementToBePresent(By.xpath(selectFirstStoreRoom), af.DEFAULT_TIMEOUT_MS * 3);
		af.waitForElementToBePresent(By.xpath(selectFirstStoreRoom),1000).click();
	}
	
	/**
	 * To get Quantity Value
	 * @return 
	 *
	 * @throws Exception
	 */
	public String getQuantityValue() throws Exception {
		af.waitForElementToBePresent(By.id("g_ywm"), af.DEFAULT_TIMEOUT_MS * 3);
		return af.instantiateComponent(NumberInputComponent.class, "g_ywm").getValue();
	}

	/**
	 * Click to Back from materials page
	 *
	 * @throws Exception
	 */
	public void backFromMaterials() throws Exception {
		af.waitForElementToBePresent(By.id("np3ya-pageheader_breadcrumb_icon"), af.DEFAULT_TIMEOUT_MS * 5);
		af.waitForElementToBePresent(By.id("np3ya-pageheader_breadcrumb_icon"),1000).click();
	}
	
	// Generated by WCA for GP
	/**
	 * Method to wait the MR Lines to be display
	 *
	 * @throws Exception
	 */
	public void waitDataRepresnt() throws Exception {
		af.waitForElementToBePresent(By.id("yxbyd_items_datalistWrapper"), af.DEFAULT_TIMEOUT_MS * 6);
	}
	
	// Generated by WCA for GP
	/**
	 * Method to wait the sliding drawers re-appear
	 *
	 * @throws Exception
	 */
	public void waitSlidingDrawaerReapper() throws Exception {
		af.waitForElementToBePresent(By.id("slidingwodetailsmaterials"), af.DEFAULT_TIMEOUT_MS * 6);
	}
	
	
	// Generated by WCA for GP
	/**
	 * Method to get Order unit Label
	 * 
	 * @return {String} Order unit Label
	 */
	public String getOrderUnitLabel() throws Exception {
		af.waitForElementToBePresent(By.id("e3xjy_label"), af.DEFAULT_TIMEOUT_MS * 3);
		return af.instantiateComponent(LabelComponent.class, "e3xjy_label").getLabel();
	}
	
	// Generated by WCA for GP
	/**
	 * Method to get Quantity value
	 * 
	 * @return {String} Order unit value
	 */
	public String getOrderUnitValue() throws Exception {
		af.waitForElementToBePresent(By.id("e3xjy_value"), af.DEFAULT_TIMEOUT_MS * 3);
		return af.instantiateComponent(LabelComponent.class, "e3xjy_value").getLabel();
	}
	// Generated by WCA for GP
	/**
	 * Method Click On Discard Button
	 *
	 * @throws Exception
	 */

	public void ClicOnToolDiscrad() throws Exception {
		af.waitForElementToBePresent(By.xpath(discardChangesBtn),1000).click();
		af.waitForElementToBePresent(By.id(discardChangeClick), af.DEFAULT_TIMEOUT_MS *10);
		af.instantiateComponent(ButtonComponent.class, discardChangeClick).click();
	}


	/**
	 * clear quantity field for the material
	 *
	 * @param quantity
	 * @throws Exception
	 */
	public void clearQuantity() throws Exception {
		af.waitForElementToBePresent(By.xpath(materialQuanText), af.DEFAULT_TIMEOUT_MS * 3);
		af.instantiateComponent(ButtonComponent.class, materialQuanText).click();
		logger.info("Quantity field cleared");
	}
}