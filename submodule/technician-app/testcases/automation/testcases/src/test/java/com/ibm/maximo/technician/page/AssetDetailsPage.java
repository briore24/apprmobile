package com.ibm.maximo.technician.page;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ibm.maximo.automation.mobile.AbstractAutomationFramework;
import com.ibm.maximo.components.ButtonComponent;
import com.ibm.maximo.components.FieldComponent;
import com.ibm.maximo.components.TagComponent;
import com.ibm.maximo.components.WrappedTextComponent;

public class AssetDetailsPage {

	private final static Logger logger = LoggerFactory.getLogger(AssetDetailsPage.class);
	private String description = "gpv3_";
	public String pageTitle = "assetDetails_2_field";
	public final  String assetDetailBreadcrumb = "pjw4x-pageheader_breadcrumb_icon";
	private String assetStatus = "z5qrw_0";
	private String calibrationHistoryTile = "w2d26";
	private String AssetStatusHistoryTile = "y74aw";
	private String calibrationHistoryWoListLabel = "calibrationHistoryWoListDrawer_headerLabel";
	private String assetStatusHistoryWoListLabel = "assetStatusHistoryListDrawer_headerLabel";
	private String WoListItem ="gk8ja[0]";
	private String assetStatusItem = "pbz3b[0]";
	private String calibrationDetailsSectionLabel = "calibrationHistoryDetailsDrawer_headerLabel";
	private String assetStatusDetailsSectionLabel = "assetStatusDetailsDrawer_headerLabel";
	public final  String calibrationDetailBreadcrumb = "calibrationHistoryDetailsDrawer_button";
	public final  String calibrationHistoryCloseButton = "calibrationHistoryWoListDrawer_button";
	public final  String assetStatusDetailBreadcrumb = "assetStatusDetailsDrawer_button";
	public final  String assetStatusHistoryCloseButton = "assetStatusHistoryListDrawer_button";
	

	private AbstractAutomationFramework af;
	
	public AssetDetailsPage(AbstractAutomationFramework af) {
		this.af = af;
	}

	/**
	 * This is generic getTitle method to get string value of element
	 *
	 * @param elementID {string} value of id to get text value of it
	 * @throws Exception
	 */
	public String getTitle(String elementID) throws Exception {
		af.waitForElementToBePresent(By.id(elementID), af.DEFAULT_TIMEOUT_MS * 3);
		String pageTitle = af.instantiateComponent(FieldComponent.class, elementID).getValue();
		logger.info("pageTitle:" + pageTitle);
		return pageTitle;
	}

	/**
	 * asset detail page
	 * 
	 * @return boolean
	 * @throws Exception
	 */
	public boolean checkAssetDetailPage() throws Exception {
		af.waitForElementToBePresent(By.id(description), 2000);
		return af.isElementExists(By.id(description));
	}
	
	/**
	 * Get asset description
	 * 
	 * @return String
	 * @throws Exception
	 */
	public String getTextAssetDescription() throws Exception {
		af.waitForElementToBePresent(By.id(description), 2000);
		String assetDescription = af.instantiateComponent(WrappedTextComponent.class, description).getParentLabel();
		return assetDescription;
	}

	/**
	 * This is generic reusable method to click button
	 *
	 * @param buttonId {string} value of id where click element
	 * @throws Exception
	 */
	public void clickButton(String buttonId) throws Exception {
		af.waitForElementToBePresent(By.id(buttonId));
		af.instantiateComponent(ButtonComponent.class, buttonId).click();
	}
	
	/**
	 * Get asset status
	 * 
	 * @return String
	 * @throws Exception
	 */
	public String getTextAssetStatus() throws Exception {
		af.waitForElementToBePresent(By.id(assetStatus), 2000);
		return af.instantiateComponent(TagComponent.class, assetStatus).getLabel();
	}
	
	/**
	 * Verify Calibration History tile is present
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean isCalibrationHistoryPresent() throws Exception {
		try {
			af.waitForElementToBePresent(By.id(calibrationHistoryTile));
		} catch (NoSuchElementException e) {
			return false;
		}
		return true;
	}
	
	/**
	 * Click on Calibration History tile
	 * 
	 * @throws Exception
	 */
	public void clickCalibrationHistoryTile() throws Exception {
		af.waitForElementToBePresent(By.id(calibrationHistoryTile)).click();
	}
	
	/**
	 * Get calibration History label
	 * 
	 * @return String
	 * @throws Exception
	 */
	public String getCalibrationHisWoListSectionLabel() throws Exception {
		return af.waitForElementToBePresent(By.id(calibrationHistoryWoListLabel)).getText();
	}
	
	/**
	 * Get workorder list
	 * 
	 * @return String
	 * @throws Exception
	 */
	public String getWorkorderList() throws Exception {
		af.waitForElementToBePresent(By.id("ev654[0]_fieldValue0"));
		String workOrderList = af.waitForElementToBePresent(By.id("ev654[0]_fieldValue0")).getText();
		return workOrderList;
	}
	
	/**
	 * Click on Calibration Work order number
	 * 
	 * @throws Exception
	 */
	public void clickWoListItem() throws Exception {
		af.waitForElementToBePresent(By.id(WoListItem)).click();
	}
	
	/**
	 * Get calibration details label
	 * 
	 * @return String
	 * @throws Exception
	 */
	public String getCalibrationDetailsSectionLabel() throws Exception {
		return af.waitForElementToBePresent(By.id(calibrationDetailsSectionLabel)).getText();
	}
	
	/**
	 * Verify Asset status History tile is present
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean isAssetStatusHistoryPresent() throws Exception {
		try {
			af.waitForElementToBePresent(By.id(AssetStatusHistoryTile));
		} catch (NoSuchElementException e) {
			return false;
		}
		return true;
	}
	
	/**
	 * Click on Asset status History tile
	 * 
	 * @throws Exception
	 */
	public void clickAssetStatusHistoryTile() throws Exception {
		af.waitForElementToBePresent(By.id(AssetStatusHistoryTile)).click();
	}
	
	/**
	 * Get Asset status History label
	 * 
	 * @return String
	 * @throws Exception
	 */
	public String getAssetStatusHisWoListSectionLabel() throws Exception {
		return af.waitForElementToBePresent(By.id(assetStatusHistoryWoListLabel)).getText();
	}
	
	/**
	 * Click on Asset status History status
	 * 
	 * @throws Exception
	 */
	public void clickAssetStatusListItem() throws Exception {
		af.waitForElementToBePresent(By.id(assetStatusItem)).click();
	}
	
	/**
	 * Get Asset Status details label
	 * 
	 * @return String
	 * @throws Exception
	 */
	public String getAssetStatusDetailsSectionLabel() throws Exception {
		return af.waitForElementToBePresent(By.id(assetStatusDetailsSectionLabel)).getText();
	}	
}
