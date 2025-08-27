package com.ibm.maximo.technician.testcases.calibration.page;

import static org.testng.Assert.assertEquals;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.maximo.automation.mobile.AbstractAutomationFramework;
import com.ibm.maximo.components.ButtonComponent;
import com.ibm.maximo.components.SlidingDrawerComponent;
import com.ibm.maximo.components.TextAreaComponent;
import com.ibm.maximo.components.ToastComponent;

public class RemarksPage {

	private AbstractAutomationFramework af;
	private final static Logger logger = LoggerFactory.getLogger(RemarksPage.class);
	
	private String remarksDrawerIdLocator = "startDrawerContainer_slidingDrawerController";
	private String saveBtnIdLocator = "gm6dj";

	public RemarksPage(AbstractAutomationFramework af) {
		this.af = af;
	}
	
	private SlidingDrawerComponent getRemarksDrawerComponent() {
		return af.instantiateComponent(SlidingDrawerComponent.class, remarksDrawerIdLocator);
	}
	
	public String getHeaderText() {
		return getRemarksDrawerComponent().getHeaderLabel();
	}
	
	public void validateHeader() throws Exception {
		assertEquals(getHeaderText(), "Additional Remarks");
		logger.info("Validated header text.");
	}
	
	public void closeRemarksSection() {
		logger.info("Close remarks section");
		getRemarksDrawerComponent().closeDrawer();
	}
	
	public void saveRemarks() {
		logger.info("Save remarks");
		af.instantiateComponent(ButtonComponent.class, saveBtnIdLocator).click();
		validateToastOnSaveRemark();
	}
	
	public void setRemark(String remark) {
		TextAreaComponent descriptionField = getRemarksDrawerComponent().getAttachmentDescField();
		descriptionField.clearField();
		
		WebElement descriptionFieldEle = descriptionField.find("textarea");
		String prevVal = descriptionFieldEle.getText();
		
		if(!remark.isBlank() && remark != null) {
			logger.info("Enter remark : {}", remark);
			descriptionFieldEle.sendKeys(remark);
		} else {
			logger.info("Clear remark");
			descriptionField.typeAndSendKey(" ", Keys.BACK_SPACE); // only clearField doesn't work for textarea
		}
		
		//register the change, in case of IOS
		if (af.isiOS()) {
			String triggerReactTracker = String.format("if (arguments[0]._valueTracker) { arguments[0]._valueTracker.setValue('%s') };", prevVal);
			String triggerInputEvent = "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));";
			af.getDriver().executeScript(triggerReactTracker + triggerInputEvent, descriptionFieldEle);
		}
	}
	
	public void enterAndValidateRemark(String remark) throws Exception {
		DatasheetListPage datasheetListPage = new DatasheetListPage(af);
		AssetFunctionPage dataSheetPageObj = new AssetFunctionPage(af);
		
		this.setRemark(remark);
		this.saveRemarks();
		
		logger.info("Navigate back and visit remarks section again...");
		dataSheetPageObj.clickBackArrowBtn();
		datasheetListPage.clickNavigationToDatasheet();
		dataSheetPageObj.clickRemarksSection();
		
		this.validateRemark(remark);
		this.closeRemarksSection();
	}
	
	public void clearAndValidateRemarkEmpty() throws Exception {
		enterAndValidateRemark("");
	}
	
	public String getRemarkText() {
		return getRemarksDrawerComponent().getAttachmentDescField().getTextAreaText();
	}
	
	public void validateRemark(String expectedRemark) throws Exception {
		assertEquals(getRemarkText(), expectedRemark);
	}
	
	private void validateToastOnSaveRemark() {
		String toastText;
		
		ToastComponent toast = new ToastComponent(af.getDriver(), false); // waits for toast element
		toastText = toast.getContentText().trim();
		toast.waitToastClose();
		logger.info("Validated presence of toast.");
		
		assertEquals(toastText, "Your remarks were added.");
	}
}