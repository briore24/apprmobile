package com.ibm.maximo.technician.testcases.calibration.page;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.maximo.automation.mobile.AbstractAutomationFramework;

public class FunctionCheckAsLeftPage {

	private AbstractAutomationFramework af;
	JavascriptExecutor jsExec;
	private final static Logger logger = LoggerFactory.getLogger(FunctionCheckAsLeftPage.class);
	public static String functionCheckAsLeftPageTitle = "As left values";

	private String backArrowBtn = "xq88g-pageheader_icon";
	private String pageTitle = "xq88g-pageheader_title";
	private String assetFunctionName = "ed7dk";
	private String assetFunctionStatus = ".//span[@id='e2ba9_0']//span";
	private String functionCheckName = ".//div[@id='a3538[%s]']//p";
	private String allFunctionChecksAsLeftPassBtn = ".//button[@id='asleftpass']";
	private String functionCheckAsLeftPassBtn = ".//div[@id='a3538[%s]']//button[@id='asleftpass']";
	private String allFunctionChecksAsLeftFailBtn = ".//button[@id='asleftfail']";
	private String functionCheckAsLeftFailBtn = ".//div[@id='a3538[%s]']//button[@id='asleftfail']";
	private String functionCheckSaveAsLeftBtn = "xq88g-pageheader_buttongroup_wq_wk";

	public FunctionCheckAsLeftPage(AbstractAutomationFramework af) {
		this.af = af;
	}

	public void clickBackArrowBtn() throws Exception {
		af.waitForElementToBePresent(By.id(backArrowBtn)).click();
	}

	public String getPageTitle() throws Exception {
		return af.waitForElementToBePresent(By.id(pageTitle)).getText().trim();
	}

	public String getAssetFunctionName() throws Exception {
		return af.waitForElementToBePresent(By.id(assetFunctionName)).getText().trim();
	}

	public String getAssetFunctionStatus() throws Exception {
		return af.waitForElementToBePresent(By.xpath(assetFunctionStatus)).getText().trim();
	}

	public String getFunctionCheckName(int functionCheckIndex) throws Exception {
		return af.waitForElementToBePresent(
				By.xpath(functionCheckName.replace("%s", String.valueOf(functionCheckIndex)))).getText().trim();
	}

	public void selectFunctionCheckAsLeftPassBtn(int functionCheckIndex) throws Exception {
		af.waitForElementToBePresent(
				By.xpath(functionCheckAsLeftPassBtn.replace("%s", String.valueOf(functionCheckIndex)))).click();
	}

	public void selectFunctionCheckAsLeftFailBtn(int functionCheckIndex) throws Exception {
		af.waitForElementToBePresent(
				By.xpath(functionCheckAsLeftFailBtn.replace("%s", String.valueOf(functionCheckIndex)))).click();
	}

	public void saveAsLeftValues() throws Exception {
		af.waitForElementToBePresent(By.id(functionCheckSaveAsLeftBtn)).click();
	}

	public boolean isSaveAsLeftValuesBtnDisabled() throws Exception {
		if (af.waitForElementToBePresent(By.id(functionCheckSaveAsLeftBtn)).getAttribute("class")
				.contains("disabled")) {
			return true;
		} else {
			return false;
		}
	}

	// Select Function check pass
	public void selectAllAsLeftPass() throws Exception {
		List<WebElement> asFoundPassEle = (List<WebElement>) af.getDriver()
				.findElements(By.xpath(allFunctionChecksAsLeftPassBtn));
		for (int i = 0; i < asFoundPassEle.size(); i++) {
			selectFunctionCheckAsLeftPassBtn(i);
		}
	}

	public void selectRandomAsLeftFailBtn() throws Exception {
		int random = (int) (Math.random() * 3) + 1;
		selectFunctionCheckAsLeftFailBtn(random);
	}

	public static void main(String[] args) {
		System.out.println((int)(Math.random() * 3));
		System.out.println((int)(Math.random() * 3)+1);
	}
}