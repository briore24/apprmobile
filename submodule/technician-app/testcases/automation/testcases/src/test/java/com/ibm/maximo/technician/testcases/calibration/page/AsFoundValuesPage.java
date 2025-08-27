package com.ibm.maximo.technician.testcases.calibration.page;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import com.ibm.maximo.automation.mobile.AbstractAutomationFramework;
import com.ibm.maximo.automation.mobile.MobileAutomationFramework;

public class AsFoundValuesPage extends CalibrationBaseTest  {

	private AbstractAutomationFramework af;
	JavascriptExecutor jsExec;
	public static String asFoundValuesPageTitleName = "As found values";

	private String backArrowBtn = "qx36d-pageheader_icon";
	private String pageTitle = "qx36d-pageheader_title";


	private String asFoundSaveBtn = "qx36d-pageheader_buttongroup_vkyad";


	// Repeatable Analog Calibration Point elements
	private String repeatableCallPointBackArrowBtn = "blcqh-pageheader_icon";
	private String repeatableCallPointPageTitle = "blcqh-pageheader_title";
	private String repeatableAssetFunctionStatus = ".//span[@id='ewd56_0']/span";
	
	private String repeatableAsFoundSaveBtn = "blcqh-pageheader_buttongroup_bj44";

	private String repeatableSetPointInput = "(.//div[@id='yye87_items_%r_DetailsLongPress']//input[contains(@id,'bwp5j')])[%s]";

	public AsFoundValuesPage(AbstractAutomationFramework af) {
        super(af);  // Calls the constructor of the base class
        this.af = af;
        this.jsExec = (JavascriptExecutor) af.getDriver();
    }

	public void repeatablePageClickBackArrowBtn() throws Exception {
		af.waitForElementToBePresent(By.id(repeatableCallPointBackArrowBtn)).click();
	}

	public String getPageTitle() throws Exception {
		return af.waitForElementToBePresent(By.id(pageTitle)).getText().trim();
	}

	public String getRepeatablePointPageTitle() throws Exception {
		return af.waitForElementToBePresent(By.id(repeatableCallPointPageTitle)).getText().trim();
	}

	public String getRepeatableAssetFunctionStatus() throws Exception {
		return af.waitForElementToBePresent(By.xpath(repeatableAssetFunctionStatus)).getText().trim();
	}

	public void enterRepeatableSetPointInput(int sectionIndex, int repeatableIndex, String inputValue)
			throws Exception {
		String xpathLocator = repeatableSetPointInput.replace("%r", String.valueOf(sectionIndex));
		xpathLocator = xpathLocator.replace("%s", String.valueOf(repeatableIndex));
		WebElement repeatableSetPointInputEle = af.waitForElementToBePresent(By.xpath(xpathLocator));

		repeatableSetPointInputEle.clear();
		repeatableSetPointInputEle.sendKeys(inputValue);
	}

	public void saveAsFoundValues() throws Exception {
		af.waitForElementToBePresent(By.id(asFoundSaveBtn)).click();
	}
	
	public boolean saveAndReturnStatus(boolean repeatable, int timeOut) throws Exception {
		if(repeatable)
			saveRepeatableAsFoundValues();
		else
			saveAsFoundValues();
		
		try {
			((MobileAutomationFramework) af).waitForElementToBeDisabled(repeatable ? By.id(repeatableAsFoundSaveBtn) : By.id(asFoundSaveBtn), timeOut);
		} catch (TimeoutException e) {
			return false;
		}
		
		return true;
	}

	public void saveRepeatableAsFoundValues() throws Exception {
		af.waitForElementToBePresent(By.id(repeatableAsFoundSaveBtn)).click();
	}

	public void addRepeatableCalibrationPointValue(String path, String testDataType,
		String repeatablePointValue) throws Exception {
		enterRepeatableCalibrationPointValue(path, testDataType, repeatablePointValue);
	}

	public void validateTolerances(String path, String testDataType, String type) throws Exception {
		verifyTolerances(path, testDataType, type);
	}
}