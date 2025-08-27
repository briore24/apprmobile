package com.ibm.maximo.technician.page;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.maximo.automation.mobile.AbstractAutomationFramework;
import com.ibm.maximo.components.ButtonComponent;
import com.ibm.maximo.components.FieldComponent;
import com.ibm.maximo.components.LabelComponent;
import com.ibm.maximo.components.NumberInputComponent;
import com.ibm.maximo.components.TextInputComponent;

public class AddToolPage {

	private AbstractAutomationFramework af;
	private final static Logger logger = LoggerFactory.getLogger(AddToolPage.class);
	private String saveButton = "g3kgd_g752v";
	private String toolSelectionButton = "jxr6v";

	private String toolLookup_searchTextInput = "toolLookup_lookup_datalist_search_searchInput";
	private String toolLookup_startSearch = "toolLookup_lookup_datalist_search_searchButton";
	private String quantity_count = "b2w_5";

	private String hours_up = "zn9jz_up";
	private String hours_down = "zn9jz_down";
	private String hours_count_hour = "zn9jz_hour";
	private String hours_count_minute = "zn9jz_minute";
	private String storeroomValue = "yerbp";
	private String binValue = "yp26b";
	private String toolsDrawerButton="toolsDrawer_button";
	private String CalSaveButton = "e_2rw_j8evd";
	private String CalToolSelectionButton = "nawq9";

	public AddToolPage(AbstractAutomationFramework af) {
		this.af = af;
	}

	/**
	 * Method to get Quantity Number
	 * 
	 * @return {String} quantity
	 */
	public String getQuantity() {
		LabelComponent labelComponent = af.instantiateComponent(LabelComponent.class, quantity_count);
		return labelComponent.getLabel();
	}

	/**
	 * Click increase hour button
	 * 
	 * @throws Exception
	 */
	public void increaseHours() throws Exception {
		af.instantiateComponent(ButtonComponent.class, hours_up).click();
	}

	/**
	 * Click decrease hour button
	 * 
	 * @throws Exception
	 */
	public void decreaseHours() throws Exception {
		af.instantiateComponent(ButtonComponent.class, hours_down).click();
	}

	/**
	 * Method to get Hours
	 * 
	 * @return {String} Hours
	 */
	public String getHours() {
		LabelComponent labelComponent = af.instantiateComponent(LabelComponent.class, hours_count_hour);
		LabelComponent labelComponent_minute = af.instantiateComponent(LabelComponent.class, hours_count_minute);
		return labelComponent.getLabel() + ":" + labelComponent_minute.getLabel();
	}

	/**
	 * Method to get populated Asset Number
	 * 
	 * @return {String} asset number
	 */
	public String getAssetNumber() {
		return af.instantiateComponent(LabelComponent.class, "nv_zd_fieldValue0").getLabel();
	}

	/**
	 * Enter regular hours
	 * 
	 * @param hrs
	 * @param min
	 * @throws Exception
	 */
	public void regularHours(String hrs, String min) throws Exception {
		af.instantiateComponent(NumberInputComponent.class, hours_count_hour).typeAndSendDurationHourKey(hrs, Keys.TAB);
		af.instantiateComponent(NumberInputComponent.class, hours_count_minute).typeAndSendDurationMinuteKey(min,
				Keys.TAB);
	}

	public boolean isSaveButtonPresent() throws Exception {
		af.waitForElementToBePresent(By.id(saveButton), af.DEFAULT_TIMEOUT_MS * 20);
		return af.isElementExists(By.id(saveButton));
	}

	/**
	 * Check save button is enabled or disabled
	 * 
	 * @return {boolean} button enabled
	 * @throws Exception
	 */
	public boolean isSaveButtonEnabled() throws Exception {
		if (isSaveButtonPresent()) {
			LabelComponent saveButtonComponent = af.instantiateComponent(LabelComponent.class, saveButton);
			return saveButtonComponent.isEnabled();
		} else {
			logger.info("Save button not found, error here");
			throw new Exception("Save button not found, error here");
		}
	}

	public void clickSaveButton() throws Exception {
		af.instantiateComponent(LabelComponent.class, saveButton).click();
	}

	/**
	 * Select tool to add into report
	 * 
	 * @param toolNum
	 * @param toolId
	 * @throws Exception
	 */
	public void selectTool(String toolNum, String toolId) throws Exception {
		logger.info("start tool selection tool number:" + toolNum + " tool id:" + toolId);
		af.instantiateComponent(ButtonComponent.class, toolSelectionButton).click();

		af.waitForElementToBePresent(By.id(toolLookup_searchTextInput));
		TextInputComponent tic = af.instantiateComponent(TextInputComponent.class, toolLookup_searchTextInput);
		tic.setText(toolNum);
		ButtonComponent searchButton = af.instantiateComponent(ButtonComponent.class, toolLookup_startSearch);
		searchButton.click();
		// sample: toolLookup_lookup_datalist_644_selectionCheckBoxIcon_touch
		String selectedItemId = "toolLookup_lookup_datalist_" + toolId + "_selectionCheckBoxIcon_touch";
		logger.info("tool selected locator >" + selectedItemId);
		af.waitForElementToBePresent(By.id(selectedItemId));
		af.instantiateComponent(ButtonComponent.class, selectedItemId).click();
		logger.info("Tool selected");
	}

	public void setQuantity(String quantity) throws Exception {
		TextInputComponent textComponent_quantityValue = af.instantiateComponent(TextInputComponent.class,
				quantity_count);
		textComponent_quantityValue.clearField();
		Thread.sleep(1000);
		textComponent_quantityValue.setText(quantity);
		af.waitForElementToBePresent(By.id(quantity_count)).sendKeys(Keys.TAB);
	}

	public void setHours(String hours) throws Exception {
		af.instantiateComponent(NumberInputComponent.class, hours_count_hour).typeInNumberInputandSendKey(Keys.BACK_SPACE);
		Thread.sleep(1000);
		af.instantiateComponent(NumberInputComponent.class, hours_count_hour).typeInNumberInputandSendKey(Keys.BACK_SPACE);
		Thread.sleep(1000);
		af.instantiateComponent(NumberInputComponent.class, hours_count_hour).typeAndSendDurationHourKey(hours, Keys.TAB);
	}

	/**
	 * Wait till page is ready
	 * 
	 * @throws Exception
	 */
	public void tillPageLoaded() throws Exception {
		af.waitForElementToNotBePresent(By.id(saveButton), 2000);
	}

	// Generated by WCA for GP
	/**
	 * Method for get storeroom value
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getStoreroom() throws Exception {
		return af.instantiateComponent(FieldComponent.class, storeroomValue).getValue();
	}

	// Generated by WCA for GP
	/**
	 * Method for get bin value
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getBinValue() throws Exception {
		return af.instantiateComponent(FieldComponent.class, binValue).getValue();
	}
	
	// Generated by WCA for GP
		/**
		 * Method for Multiple Storeroom
		 * 
		 * @return
		 * @throws Exception
		 */
		public boolean isMultipleStoreRoomPresent(String location,String location1) throws Exception {
			try {
			String firstStoreRoom ="toolStoreRoomLookup_lookup_datalist_"+location+"_selectionCheckBoxIcon_touch";
			System.out.println(firstStoreRoom);
			String secondStoreRoom ="toolStoreRoomLookup_lookup_datalist_"+location1+"_selectionCheckBoxIcon_touch";
			System.out.println(secondStoreRoom);
			af.waitForElementToBePresent(By.id(firstStoreRoom), af.DEFAULT_TIMEOUT_MS * 5);
			af.waitForElementToBePresent(By.id(secondStoreRoom), af.DEFAULT_TIMEOUT_MS * 5);
			af.instantiateComponent(ButtonComponent.class, firstStoreRoom).click();
			logger.info("Selecting  storeroom");
			return true;
			}catch (Exception e) {
				e.printStackTrace();
				return false;
			} 
		}
		
		/**
		 * Select tool to add into report
		 * 
		 * @param toolNum
		 * @param toolId
		 * @throws Exception
		 */
		public void toolsDrawerClose() throws Exception {
//			logger.info("start tool selection tool number:" + toolNum + " tool id:" + toolId);
			af.instantiateComponent(ButtonComponent.class, toolsDrawerButton).click();

		
		}
		
		public void clickCalSaveButton() throws Exception {
			af.instantiateComponent(LabelComponent.class, CalSaveButton).click();
		}

	/**
	 * Method to get populated Asset Number
	 *
	 * @return {String} asset number
	 */
	public String getCalAssetNumber() {
		return af.instantiateComponent(LabelComponent.class, "wvgwn_fieldValue0").getLabel();
	}
	/**
	 * Select tool to add into report
	 * 
	 * @param toolNum
	 * @param toolId
	 * @throws Exception
	 */
	public void selectCalTool(String toolNum, String toolId) throws Exception {
		logger.info("start tool selection tool number:" + toolNum + " tool id:" + toolId);
		af.instantiateComponent(ButtonComponent.class, CalToolSelectionButton).click();

		af.waitForElementToBePresent(By.id(toolLookup_searchTextInput));
		TextInputComponent tic = af.instantiateComponent(TextInputComponent.class, toolLookup_searchTextInput);
		tic.setText(toolNum);
		ButtonComponent searchButton = af.instantiateComponent(ButtonComponent.class, toolLookup_startSearch);
		searchButton.click();
		// sample: toolLookup_lookup_datalist_644_selectionCheckBoxIcon_touch
		String selectedItemId = "toolLookup_lookup_datalist_" + toolId + "_selectionCheckBoxIcon_touch";
		logger.info("tool selected locator >" + selectedItemId);
		af.waitForElementToBePresent(By.id(selectedItemId));
		af.instantiateComponent(ButtonComponent.class, selectedItemId).click();
		logger.info("Tool selected");
	}

}