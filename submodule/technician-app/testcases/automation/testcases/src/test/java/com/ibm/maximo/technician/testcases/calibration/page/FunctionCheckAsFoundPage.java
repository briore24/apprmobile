package com.ibm.maximo.technician.testcases.calibration.page;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Reporter;

import com.ibm.maximo.automation.mobile.AbstractAutomationFramework;
import com.ibm.maximo.automation.mobile.MobileAutomationFramework;
import com.ibm.maximo.components.ButtonComponent;
import com.ibm.maximo.components.TextInputComponent;
import com.ibm.maximo.technician.framework.TechnicianTest;

public class FunctionCheckAsFoundPage {

	private AbstractAutomationFramework af;
	JavascriptExecutor jsExec;
	private final static Logger logger = LoggerFactory.getLogger(FunctionCheckAsFoundPage.class);
	public static String functionCheckAsFoundPageTitle = "As found values";

	private String backArrowBtn = "qx36d-pageheader_icon";
	private String pageTitle = "qx36d-pageheader_title";
	private String assetFunctionName = "ed7dj";
	private String assetFunctionStatus = ".//span[@id='nkxd2_0']//span";
	private String functionCheckName = ".//div[@id='ad8nj[%s]']//p";
	private String allFunctionChecksAsFoundPassBtn = ".//button[@id='asfoundpass']";
	private String functionCheckAsFoundPassBtn = ".//div[@id='ad8nj[%s]']//button[@id='asfoundpass']";
	private String functionCheckAsFoundFailBtn = ".//div[@id='ad8nj[%s]']//button[@id='asfoundfail']";
	private String functionCheckSaveAsFoundBtn = "qx36d-pageheader_buttongroup_vkyad";

	public FunctionCheckAsFoundPage(AbstractAutomationFramework af) {
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
		return af.waitForElementToBePresent(By.xpath(functionCheckName.replace("%s", String.valueOf(functionCheckIndex)))).getText().trim();
	}
	
	public void selectFunctionCheckAsFoundPassBtn(int functionCheckIndex) throws Exception {
		af.waitForElementToBePresent(By.xpath(functionCheckAsFoundPassBtn.replace("%s", String.valueOf(functionCheckIndex)))).click();
	}
	
	public void selectFunctionCheckAsFoundFailBtn(int functionCheckIndex) throws Exception {
		af.waitForElementToBePresent(By.xpath(functionCheckAsFoundFailBtn.replace("%s", String.valueOf(functionCheckIndex)))).click();
	}

	public void saveAsFoundValues() throws Exception {
		af.waitForElementToBePresent(By.id(functionCheckSaveAsFoundBtn)).click();
	}
	
	public boolean isSaveAsFoundValuesBtnDisabled() throws Exception {
		if(af.waitForElementToBePresent(By.id(functionCheckSaveAsFoundBtn)).getAttribute("class").contains("disabled")) {
			return true;
		}else {
			return false;
		}
	}
	
	// Select Function check pass
	public void selectAllAsFoundPass() throws Exception {
		List<WebElement> asFoundPassEle = (List<WebElement>) af.getDriver().findElements(By.xpath(allFunctionChecksAsFoundPassBtn));
		for(int i=0; i<asFoundPassEle.size(); i++) {
			selectFunctionCheckAsFoundPassBtn(i);
		}
	}
	
	public void selectRandomAsFoundFailBtn() throws Exception {
		int random = (int)(Math.random() * 3) + 1;
		selectFunctionCheckAsFoundFailBtn(random);	
	}
	
}