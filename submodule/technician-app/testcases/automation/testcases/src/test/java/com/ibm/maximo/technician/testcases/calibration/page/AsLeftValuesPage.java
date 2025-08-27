package com.ibm.maximo.technician.testcases.calibration.page;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.maximo.automation.mobile.AbstractAutomationFramework;
import com.ibm.maximo.automation.mobile.MobileAutomationFramework;

public class AsLeftValuesPage extends CalibrationBaseTest  {

	private AbstractAutomationFramework af;
	JavascriptExecutor jsExec;
	private final static Logger logger = LoggerFactory.getLogger(AsLeftValuesPage.class);
	public static String asLeftValuePageTitleName = "As left values";
	private String backArrowBtn = "xq88g-pageheader_icon";
	private String pageTitle = "xq88g-pageheader_title";
	
	private String asLeftSaveBtn = "xq88g-pageheader_buttongroup_wq_wk";

	// Repeatable page locator
	private String repeatablePageBackArrowBtn = "blcqh-pageheader_icon";
	private String repeatablePageTitle = "blcqh-pageheader_title";
	private String repeatableAssetFunctionStatus = ".//span[@id='ewd56_0']/span";
	private String repeatablePageCalPointSectionLabel = ".//div[@id='yye87_items_%r_LongPress']//p[@id='or3sdcyx']";
	private String repeatablePageCalPointSectionBtn = "(.//div[contains(@id,'childCaretContainer_touch')]/*[contains(@id,'childCaret_touch')])[%s]";
	private String repeatablePageCalPointAsLeftSaveBtn = "blcqh-pageheader_buttongroup_bj44";
	
	private String repeatableSetPointInput = "(.//div[@id='yye87_items_%r_DetailsLongPress']//input[contains(@id,'g6hgprhu')])[%s]";

	public AsLeftValuesPage(AbstractAutomationFramework af) {
        super(af); // Calls the constructor of the base class
        this.af = af;
        this.jsExec = (JavascriptExecutor) af.getDriver();
    }

	public void clickRepeatablePageBackArrowBtn() throws Exception {
		af.waitForElementToBePresent(By.id(repeatablePageBackArrowBtn)).click();
	}

	public String getPageTitle() throws Exception {
		return af.waitForElementToBePresent(By.id(pageTitle)).getText().trim();
	}

	public String getRepeatablePageTitle() throws Exception {
		return af.waitForElementToBePresent(By.id(repeatablePageTitle)).getText().trim();
	}

	public String getRepeatableAssetFunctionStatus() throws Exception {
		return af.waitForElementToBePresent(By.xpath(repeatableAssetFunctionStatus)).getText().trim();
	}

	public String getRepeatableCalPointSectionLabel(int sectionIndex) throws Exception {
		return af
				.waitForElementToBePresent(
						By.xpath(repeatablePageCalPointSectionLabel.replace("%r", String.valueOf(sectionIndex))))
				.getText().trim();
	}

	public void collapseRepeatableCalPointSection(int sectionIndex) throws Exception {
		logger.info("Collapse Calibration Point Accordion");
		if (af.waitForElementToBePresent(
				By.xpath(repeatablePageCalPointSectionBtn.replace("%s", String.valueOf(sectionIndex))))
				.getAttribute("description").contains("up")) {
			af.waitForElementToBePresent(
					By.xpath(repeatablePageCalPointSectionBtn.replace("%s", String.valueOf(sectionIndex)))).click();
		}
	}

	public void enterRepeatableSetPointInput(int sectionIndex, int repeatableIndex, String inputValue)
			throws Exception {
		String xpathLocator = repeatableSetPointInput.replace("%r", String.valueOf(sectionIndex));
		xpathLocator = xpathLocator.replace("%s", String.valueOf(repeatableIndex));
		WebElement repeatableSetPointInputEle = af.waitForElementToBePresent(By.xpath(xpathLocator));

		repeatableSetPointInputEle.clear();
		repeatableSetPointInputEle.sendKeys(inputValue);
	}

	public void saveAsLeftValues() throws Exception {
		af.waitForElementToBePresent(By.id(asLeftSaveBtn)).click();
	}

	public void saveRepeatableAsLeftValues() throws Exception {
		af.waitForElementToBePresent(By.id(repeatablePageCalPointAsLeftSaveBtn)).click();
	}
	
	public boolean saveAndReturnStatus(boolean repeatable, int timeOut) throws Exception {
		if(repeatable)
			saveRepeatableAsLeftValues();
		else
			saveAsLeftValues();
		
		try {
			((MobileAutomationFramework) af).waitForElementToBeDisabled(repeatable ? By.id(repeatablePageCalPointAsLeftSaveBtn) : By.id(asLeftSaveBtn), timeOut);
		} catch (TimeoutException e) {
			return false;
		}
		
		return true;
	}

	// Enter Calibration Point values for Repeatable DS
	public void addRepeatableCalPointValues(String path, String testDataType,
			String repeatablePointValue)
			throws Exception {
		enterRepeatableCalibrationPointValue(path, testDataType ,repeatablePointValue );
	}

	public void validateTolerances(String path, String testDataType, String type) throws Exception {
		verifyTolerances(path, testDataType, type);
	}
}