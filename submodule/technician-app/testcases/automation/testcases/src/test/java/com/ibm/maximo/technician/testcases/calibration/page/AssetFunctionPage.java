package com.ibm.maximo.technician.testcases.calibration.page;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Reporter;

import com.ibm.maximo.automation.mobile.AbstractAutomationFramework;
import com.ibm.maximo.components.ButtonComponent;
import com.ibm.maximo.components.IconComponent;
import com.ibm.maximo.components.ToggleComponent;
import com.ibm.maximo.technician.testcases.calibration.support.constants.DatasheetConstants;
import com.ibm.maximo.technician.testcases.calibration.support.constants.OutputConstants;
import com.ibm.maximo.technician.testcases.calibration.support.objects.maximo.PluscDsInstr;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class AssetFunctionPage {

	private AbstractAutomationFramework af;
	private final static Logger logger = LoggerFactory.getLogger(AssetFunctionPage.class);
	public static String assetFunctionPageTitleName = "Asset functions";
	public static String assetFunctionPageTypeValue = "ANALOG";
	public static String assetFunctionPageTypeLabel = "Type";

	private String backArrowBtn = "y9yx9-pageheader_icon";
	private String pageTitle = "y9yx9-pageheader_title";
	private String pageSubTitle = "y9yx9-pageheader_subtitle";
	private String noAdjustmentBaseId = "vd3pb";
	private String noAdjustmentBtnXPath = ".//button[contains(@id, '" + noAdjustmentBaseId + "')]";
	private String noAdjustmentLabelXPath = ".//label[contains(@id, '" + noAdjustmentBaseId + "')]";
	private String noAdjustmentWarningHeaderID = "noAdjLimit%s_title";
	private String noAdjustmentWarningConfirmBtnID = "noAdjLimit%s_button_group_noAdjLimit%s_primary_button";
	private String noAdjustmentWarningCancelBtnID = "noAdjLimit%s_button_group_noAdjLimit%s_secondary_button";
	private String noAdjustmentOnWarningLabelID = "eq6bb";
	private String noAdjustmentOffWarningLabelID = "wmq6_";
	private String asFoundNextArrowBtnBaseID = "ryxr8";
	private String asLeftNextArrowBtnBaseID = "phqyy7a2";
	private String asFoundStatus = ".//span[starts-with(@id, 'pqvzm')]/span";
	private String asLeftStatus = ".//span[starts-with(@id, 'xdmkx')]/span";
	private String asFoundNextArrowBtn = ".//*[@id='" + asFoundNextArrowBtnBaseID + "[0]']";
	private String asLeftNextArrowBtn = ".//*[@id='" + asLeftNextArrowBtnBaseID + "[0]']";
	private String environmentalConditionSectionLabel = "wvqrk";
	private String environmentConditionSectionBtn = "b2xw4";
	private String remarksSectionLabel = "zzgek";
	private String remarksSectionBtn = "nb5bq";
	private String dataSheetwarningTitle = "dataSheetWarnings_title";
	private String dataSheetWarningMessage = "xk229";
	private String dataSheetButton = "dataSheetWarnings_button_group_dataSheetWarnings_primary_button";
	private String dataSheetTitle = "mobcaldslistpagetitle-pageheader_title";
	private String assetFunctionValue = ".//p[@id='zb5vg_fieldValue0']";
	private String assetFunctionLabel = ".//p[@id='zb5vg_label']";
	private String assetFunctionHeader = ".//p[@id='bdgqk']";
	
	//Locators for tolerance fields
	private String dataSheetToleranceStatusXPath = "(.//div[@id='aobwp7je_value']/p)[%s]";
	private String dataSheetToleranceSumDirectionXPath = "(.//div[@id='a5n5q_value']/p)[%s]";
	private String dataSheetToleranceSumEUXPath = "(.//div[@id='er6yz_value']/p)[%s]";
	private String dataSheetToleranceSumSpanXPath = "(.//div[@id='my8wy_value']/p)[%s]";
	private String dataSheetToleranceSumURVXPath = "(.//div[@id='m4wdy_value']/p)[%s]";
	private String dataSheetToleranceSumREADXPath = "(.//div[@id='nvjng_value']/p)[%s]";

	//**********Multi Asset Function locators**********
	private String assetFunctionSection = ".//p[contains(text(),'%s')]";
	private String assetFunctionAsFoundArrowBtn = ".//p[contains(text(),'%s')]/ancestor::div[contains(@id,'textBody')]//button[contains(@id,'" + asFoundNextArrowBtnBaseID + "')]";
	private String assetFunctionAsFoundStatus = ".//p[contains(text(),'%s')]/ancestor::div[contains(@id,'textBody')]//div[@datacomponentoriginalid='zeerd']//span/span";
	private String assetFunctionAsLeftArrowBtn = ".//p[contains(text(),'%s')]/ancestor::div[contains(@id,'textBody')]//button[contains(@id,'" + asLeftNextArrowBtnBaseID + "')]";
	private String assetFunctionAsLeftStatus = ".//p[contains(text(),'%s')]/ancestor::div[contains(@id,'textBody')]//div[@datacomponentoriginalid='jj6za']//span/span";
	private String assetFunctionSectionExpandBtn = ".//div[contains(@id, 'childCaretContainer_touch')]";
	private String assetFunctionSectionIcon = ".//*[contains(@id, 'childCaret_touch')]";
	private String assetFunctionAsFoundStatusMenu = ".//p[contains(text(),'%s')]/ancestor::div[contains(@id,'textBody')]//span[starts-with(@id, 'pqvzm')]//div[contains(@id, 'action')]";
	private String assetFunctionAsLeftStatusMenu = ".//p[contains(text(),'%s')]/ancestor::div[contains(@id,'textBody')]//span[starts-with(@id, 'xdmkx')]//div[contains(@id, 'action')]";

	public static final String noAdjOffWarningMsg = "The As Left values will be cleared and all input and output fields will become editable.";
	public static final String noAdjOnWarningMsg = "The As Found values will be copied to the As Left values and all input and output fields will become read only.";
	
	private String changeStatusSlidingDrawerHeader = "assetFunctionStatusDialog_headerLabel";
	public static final String changeStatusHeader = "Change status";
	private String statusBtnId = "s3z4gql3_items_%s_selectionCheckBoxIcon_touch";
	private String resetStatusBtnId = "e96gp";
	
	private String assetFunctionDescriptionValue = "(//p[@id='bzvdb[0]'])";
	private String standardDeviationInputLimitValue = "(//p[@id='y4dkk_textoverflow_value0'])";
	private String standardDeviationOutputLimitValue = "(//p[@id='x3zbg_textoverflow_value0'])";
	
	public AssetFunctionPage(AbstractAutomationFramework af) {
		this.af = af;
	}
	
	public boolean isAssetFunctionPage() throws Exception {
		try {
			af.waitForElementToBePresent(By.id(pageTitle));
		} catch (NoSuchElementException e) {
			return false;
		}
		return true;
	}
	
	public void clickBackArrowBtn() throws Exception {
		af.waitForElementToBePresent(By.id(backArrowBtn)).click();
	}

	public String getPageTitle() throws Exception {
		return af.waitForElementToBePresent(By.id(pageTitle)).getText();
	}
	
	public String getPageSubTitle() throws Exception {
		return af.waitForElementToBePresent(By.id(pageSubTitle)).getText();
	}
	
	public boolean isAssetFunctionPresent(String assetFunctionName) {
		return af.isElementExists(By.xpath(assetFunctionSection.replace("%s", assetFunctionName)));
	}
	
	/**
	 * Assert presence of asset functions given an array of asset function names
	 * 
	 * @param assetFunctionArr - String array of asset function names
	 * 
	 */
	public void assertAssetFunctionsPresent(String[] assetFunctionArr) {
		assertAssetFunctionsPresent("", assetFunctionArr, "");
	}
	
	/**
	 * Assert presence of asset functions given an array of asset function names
	 * 
	 * @param preFix - Common prefix of asset function names (if any)
	 * @param assetFunctionArr - String array of af names / unique part of af names
	 * @param postFix - Common postfix of asset function names (if any)
	 * 
	 */
	public void assertAssetFunctionsPresent(String preFix, String[] assetFunctionArr, String postFix) {
		for (String afName : assetFunctionArr) {
			assertTrue(isAssetFunctionPresent(preFix + afName + postFix), String.format("Asset function: %s should be present", afName));
		}
	}
	
	private ToggleComponent getNoAdjToggleComponent() throws Exception {
		return af.instantiateComponent(ToggleComponent.class, 
				af.waitForElementToBePresent(By.xpath(noAdjustmentLabelXPath)).getAttribute("id"));
	}
	
	public boolean isNoAdjustmentBtnPresent() {
		return af.isElementExists(By.xpath(noAdjustmentBtnXPath));
	}
	
	public boolean isNoAdjustmentBtnSelected() throws Exception {
		return getNoAdjToggleComponent().isToggleOn();
	}
	
	public boolean isNoAdjustmentBtnEnabled() throws Exception {
		return af.waitForElementToBePresent(By.xpath(noAdjustmentBtnXPath)).isEnabled();
	}
	
	public void clickNoAdjustmentBtn() throws Exception {
		assertTrue(isNoAdjustmentBtnEnabled(), "Verify no adjustment button is enabled");
		
		boolean previousSelectionState = isNoAdjustmentBtnSelected();
		
		getNoAdjToggleComponent().clickOnToggleButton();
		handleNoAdjustmentWarningDialog(previousSelectionState);
		waitForNoAdjBtnSelectedState(!previousSelectionState);
		
		validateConditionStatuses(!previousSelectionState);
	}
	
	private void validateConditionStatuses(boolean currentSelectionState) throws Exception {
		AssetFunctionPage assetFunctionPage = new AssetFunctionPage(af);
		
		logger.info("Verifying statuses after no-adjustment clicked");
		assertEquals(assetFunctionPage.getAsFoundStatus(), DatasheetConstants.STATUS_PASS);
		assertEquals(assetFunctionPage.getAsLeftStatus(), 
				currentSelectionState ? DatasheetConstants.STATUS_PASS : DatasheetConstants.STATUS_EMPTY);
	}

	private void waitForNoAdjBtnSelectedState(boolean selectionState) throws Exception {
		ToggleComponent noAdjToggleComponent = getNoAdjToggleComponent();
		new WebDriverWait(af.getDriver(), af.DEFAULT_TIMEOUT_MS)
			.until(f -> {
					return noAdjToggleComponent.isToggleOn() == selectionState;
			});
	}
	
	public void handleNoAdjustmentWarningDialog(boolean previousSelectionState) throws Exception {
		String warningDialogPrimaryBtnLocator = noAdjustmentWarningConfirmBtnID.replace("%s", previousSelectionState ? "Off" : "");
		String warningDialogLabel = af.waitForElementToBePresent(
				By.id(previousSelectionState ? noAdjustmentOffWarningLabelID : noAdjustmentOnWarningLabelID)
			).getText();
		
		assertEquals(warningDialogLabel, previousSelectionState ? noAdjOffWarningMsg : noAdjOnWarningMsg, "Verify warning message label");
		
		af.instantiateComponent(ButtonComponent.class, warningDialogPrimaryBtnLocator).click();
		af.waitForElementToNotBePresent(By.id(warningDialogPrimaryBtnLocator), af.DEFAULT_TIMEOUT_MS);
		
		logger.info("Click confirm on no adjustment warning dialog...");
	}
	
	public String getAsFoundStatus() throws Exception {
		return af.waitForElementToBePresent(By.xpath(asFoundStatus)).getText().trim();
	}
	
	public String getAsLeftStatus() throws Exception {
		return af.waitForElementToBePresent(By.xpath(asLeftStatus)).getText().trim();
	}
	
	public void clickAsFoundNextArrowBtn() throws Exception {
		af.waitForElementToBePresent(By.xpath(asFoundNextArrowBtn)).click();
	}
	
	public void clickNextArrowBtn(String condition) throws Exception {
		af.waitForElementToBePresent(By.xpath(OutputConstants.getXPath(condition, "assetFuncNextArrowBtn"))).click();
	}
	
	public void clickAssetFunctionAsFoundArrowBtn(String assetFunctionName) throws Exception {
		af.waitForElementToBePresent(By.xpath(assetFunctionAsFoundArrowBtn.replace("%s", assetFunctionName))).click();
	}
	
	public void clickAssetFunctionAsFoundStatusMenu(String assetFunctionName) throws Exception {
		af.waitForElementToBePresent(By.xpath(assetFunctionAsFoundStatusMenu.replace("%s", assetFunctionName))).click();
	}
	
	public String getAssetFunctionAsFoundStatus(String assetFunctionName) throws Exception {
		return af.waitForElementToBePresent(By.xpath(assetFunctionAsFoundStatus.replace("%s", assetFunctionName))).getText().trim();
	}
	
	public void clickAsLeftNextArrowBtn() throws Exception {
		af.waitForElementToBePresent(By.xpath(asLeftNextArrowBtn)).click();
	}
	
	public void clickAssetFunctionAsLeftArrowBtn(String assetFunctionName) throws Exception {
		af.waitForElementToBePresent(By.xpath(assetFunctionAsLeftArrowBtn.replace("%s", assetFunctionName))).click();
	}
	
	public void clickAssetFunctionAsLeftStatusMenu(String assetFunctionName) throws Exception {
		af.waitForElementToBePresent(By.xpath(assetFunctionAsLeftStatusMenu.replace("%s", assetFunctionName))).click();
	}
	
	public String getAssetFunctionAsLeftStatus(String assetFunctionName) throws Exception {
		return af.waitForElementToBePresent(By.xpath(assetFunctionAsLeftStatus.replace("%s", assetFunctionName))).getText().trim();
	}
	
	public String getEnvConditionSectionLabel() throws Exception {
		return af.waitForElementToBePresent(By.id(environmentalConditionSectionLabel)).getText();
	}
	
	public String getRemarksSectionLabel() throws Exception {
		return af.waitForElementToBePresent(By.id(remarksSectionLabel)).getText();
	}
	
	public void clickEnvConditionSection() throws Exception {
		af.waitForElementToBePresent(By.id(environmentConditionSectionBtn)).click();
	}
	
	public void clickRemarksSection() throws Exception {
		af.instantiateComponent(ButtonComponent.class, remarksSectionBtn).click();
	}
	
	public String dataSheetwarningHeader() {
		return af.instantiateComponent(ButtonComponent.class, dataSheetwarningTitle).getText();
	}
	
	public String verifyWarningMessage() {
		return af.instantiateComponent(ButtonComponent.class, dataSheetWarningMessage).getText();
	}
	
	public void clickDataSheet() {
		af.instantiateComponent(IconComponent.class, dataSheetButton).click();
	}
	
	public String verifyDataSheetTitle() {
		return af.instantiateComponent(ButtonComponent.class, dataSheetTitle).getText();
	}	
	
	private String getToleranceFieldValue(int tolIndex, String fieldName) throws Exception {
		WebElement dataSheetToleranceFieldEle = null;
		
		switch (fieldName) {
			case "status":
				dataSheetToleranceFieldEle = af.waitForElementToBePresent(By.xpath(dataSheetToleranceStatusXPath.replace("%s", String.valueOf(tolIndex))));
				break;
			case "sumdirection":
				dataSheetToleranceFieldEle = af.waitForElementToBePresent(By.xpath(dataSheetToleranceSumDirectionXPath.replace("%s", String.valueOf(tolIndex))));
				break;
			case "sumeu":
				dataSheetToleranceFieldEle = af.waitForElementToBePresent(By.xpath(dataSheetToleranceSumEUXPath.replace("%s", String.valueOf(tolIndex))));
				break;
			case "sumspan":
				dataSheetToleranceFieldEle = af.waitForElementToBePresent(By.xpath(dataSheetToleranceSumSpanXPath.replace("%s", String.valueOf(tolIndex))));
				break;
			case "sumurv":
				dataSheetToleranceFieldEle = af.waitForElementToBePresent(By.xpath(dataSheetToleranceSumURVXPath.replace("%s", String.valueOf(tolIndex))));
				break;
			case "sumread":
				dataSheetToleranceFieldEle = af.waitForElementToBePresent(By.xpath(dataSheetToleranceSumREADXPath.replace("%s", String.valueOf(tolIndex))));
				break;
		}
		
		try {
			af.getDriver().executeScript("arguments[0].scrollIntoViewIfNeeded(true)", dataSheetToleranceFieldEle);
		} catch (Exception e) {}
		
		return dataSheetToleranceFieldEle.getAttribute("id").contains("unspecified") ? "" : dataSheetToleranceFieldEle.getText().trim();
	}
	
	public void verifyToleranceFields(String dataSheetName) throws Exception {
		String path = "src/test/resources/calibration_testdata/DatasheetInputData.json";
		String content = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
		JSONObject jsonObj = new JSONObject(content);
		JSONArray jsonTolFieldData = jsonObj.getJSONObject(dataSheetName).getJSONArray("TOLERANCE_FIELDS");
		
		//iterate over 4 tolerances
		for (int i = 1; i <= 4; i++) {
			logger.info("Verifying tolerance fields for tolerance: {}", i);
			assertEquals(getToleranceFieldValue(i, "status"), getExpectedToleranceField(i, jsonTolFieldData, "status"), "Verify tolerance status");
			assertEquals(getToleranceFieldValue(i, "sumdirection"), getExpectedToleranceField(i, jsonTolFieldData, "sumdirection"), "Verify tolerance sum direction");
			assertEquals(getToleranceFieldValue(i, "sumeu"), getExpectedToleranceField(i, jsonTolFieldData, "sumeu"), "Verify tolerance sum EU");
			assertEquals(getToleranceFieldValue(i, "sumspan"), getExpectedToleranceField(i ,jsonTolFieldData, "sumspan"), "Verify tolerance sum span");
			assertEquals(getToleranceFieldValue(i, "sumurv"), getExpectedToleranceField(i, jsonTolFieldData, "sumurv"), "Verify tolerance sum URV");
			assertEquals(getToleranceFieldValue(i, "sumread"), getExpectedToleranceField(i, jsonTolFieldData, "sumread"), "Verify tolerance sum READ");
		}
	}

	private String getExpectedToleranceField(int tolIndex, JSONArray jsonTolFieldData, String fieldName) {
		return jsonTolFieldData.getJSONObject(tolIndex - 1).get(fieldName).toString();
	}
	
	public void expandAssetFunctionSection() throws Exception {
		logger.info("Expand Asset Function Accordion");

		WebElement assetFunctionSectionAccordion = af.waitForElementToBePresent(By.xpath(assetFunctionSectionExpandBtn));
		WebElement assetFunctionSectionIconEle = af.waitForElementToBePresent(By.xpath(assetFunctionSectionIcon));
		
		if(assetFunctionSectionIconEle.getAttribute(("description")).contains("chevron--down")) {
			af.getDriver().executeScript("arguments[0].scrollIntoView({ block: 'center' });", assetFunctionSectionAccordion);
			assetFunctionSectionAccordion.click();
		}
	}
	
	public void collapseAssetFunctionSection() throws Exception {
		logger.info("Collapse Asset Function Accordion");

		WebElement assetFunctionSectionAccordion = af.waitForElementToBePresent(By.xpath(assetFunctionSectionExpandBtn));
		WebElement assetFunctionSectionIconEle = af.waitForElementToBePresent(By.xpath(assetFunctionSectionIcon));
		
		if(assetFunctionSectionIconEle.getAttribute(("description")).contains("chevron--up")) {
			af.getDriver().executeScript("arguments[0].scrollIntoView({ block: 'center' });", assetFunctionSectionAccordion);
			assetFunctionSectionAccordion.click();
		}
	}
	
	public String getFunctionLabel() throws Exception {
		af.waitForElementToBePresent(By.xpath(assetFunctionLabel));
		return af.waitForElementToBePresent(By.xpath(assetFunctionLabel)).getText();
	}
	
	public String getFunctionValue() throws Exception {
		af.waitForElementToBePresent(By.xpath(assetFunctionValue));
		return af.waitForElementToBePresent(By.xpath(assetFunctionValue)).getText();
	}
	
	public boolean isAssetFunctionLabelPresent() throws Exception {
		af.waitForElementToBePresent(By.xpath(assetFunctionHeader));
		return af.isElementExists(By.xpath(assetFunctionHeader));
	}
	
	public void verifyChangeStatusHeader() throws Exception {
		assertEquals(af.waitForElementToBePresent(By.id(changeStatusSlidingDrawerHeader)).getText(), changeStatusHeader);
	}
	
	public void clickAssetFunctionStatusMenu(String condition, String assetFunctionName) throws Exception {
		if(condition == DatasheetConstants.CONDITION_ASFOUND)
			clickAssetFunctionAsFoundStatusMenu(assetFunctionName);
		else if (condition == DatasheetConstants.CONDITION_ASLEFT)
			clickAssetFunctionAsLeftStatusMenu(assetFunctionName);
	}
	
	public void selectAssetFunctionStatus(String condition, String assetFunctionName, String status) throws Exception {
		clickAssetFunctionStatusMenu(condition, assetFunctionName);
		verifyChangeStatusHeader();
		af.waitForElementToBePresent(By.id(statusBtnId.replace("%s", status))).click();
	}
	
	public void resetAssetFunctionStatus(String condition, String assetFunctionName) throws Exception {
		clickAssetFunctionStatusMenu(condition, assetFunctionName);
		verifyChangeStatusHeader();
		af.waitForElementToBePresent(By.id(resetStatusBtnId)).click();
	}
	public void clickAssetFunctionNextArrowBtn(String condition, String assetFunctionName) throws Exception {
		if(condition == DatasheetConstants.CONDITION_ASFOUND)
			clickAssetFunctionAsFoundArrowBtn(assetFunctionName);
		else
			clickAssetFunctionAsLeftArrowBtn(assetFunctionName);
	}

	public String getAssetFunctionStatus(String condition, String assetFunctionName) throws Exception {
		if(condition == DatasheetConstants.CONDITION_ASFOUND)
			return getAssetFunctionAsFoundStatus(assetFunctionName);
		else 
			return getAssetFunctionAsLeftStatus(assetFunctionName);
	}
	
	public void assertStandardDeviationLimit(CalibrationPointsRepeatablePage repeatablePage,
			AssetFunctionPage dataSheetPageObj, List<PluscDsInstr> pluscDsInstrList) throws Exception {
		if (!pluscDsInstrList.isEmpty()) {
			PluscDsInstr assetFunction = pluscDsInstrList.get(0);
			if (assetFunction.getIsstandarddeviation()) {
				logger.info("Verify standard deviation limits");
				Reporter.log("Verify standard deviation limits");
				dataSheetPageObj.expandAssetFunctionSection();

				String description = repeatablePage.getText(assetFunctionDescriptionValue);
				assertEquals(description, assetFunction.getDescription(),
						String.format("Asset Function description '%s' should be equal to '%s'.", description,
								assetFunction.getDescription()));

				String standardDeviationInputLimit = repeatablePage.getText(standardDeviationInputLimitValue);

				assertEquals(standardDeviationInputLimit, assetFunction.getStandarddeviationinputlimit(),
						String.format("Asset Function standard deviation input limit '%s' should be equal to '%s'.",
								standardDeviationInputLimit, assetFunction.getStandarddeviationinputlimit()));

				String standardDeviationOutputLimit = repeatablePage.getText(standardDeviationOutputLimitValue);

				assertEquals(standardDeviationOutputLimit, assetFunction.getStandarddeviationoutputlimit(),
						String.format("Asset Function standard deviation output limit '%s' should be equal to '%s'.",
								standardDeviationOutputLimit, assetFunction.getStandarddeviationoutputlimit()));

				dataSheetPageObj.collapseAssetFunctionSection();
			}
		}
	}
}