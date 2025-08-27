package com.ibm.maximo.technician.testcases.calibration.page;

import java.awt.Button;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.maximo.automation.mobile.AbstractAutomationFramework;
import com.ibm.maximo.automation.mobile.MobileAutomationFramework;
import com.ibm.maximo.components.ButtonComponent;
import com.ibm.maximo.components.DataListComponent;
import com.ibm.maximo.components.DataListItemComponent;
import com.ibm.maximo.components.DialogComponent;
import com.ibm.maximo.components.FieldComponent;
import com.ibm.maximo.components.LabelComponent;
import com.ibm.maximo.components.NumberInputComponent;
import com.ibm.maximo.components.TextInputComponent;
import com.ibm.maximo.components.ToastComponent;
import com.ibm.maximo.technician.setupdata.SetupData.WoStatus;

public class DatasheetListPage {

	private AbstractAutomationFramework af;
	private final static Logger logger = LoggerFactory.getLogger(DatasheetListPage.class);
	public static String datasheetListPageTitleName = "Associated data sheets";
	private String backArrowBtn = "mobcaldslistpagetitle-pageheader_icon";
	private String pageSubTitle = "mobcaldslistpagetitle-pageheader_title";
	private String dataSheetName = ".//*[@id='qz5p5[0]']";
	private String asFoundStatus = ".//*[@id='jv23x_asfound[0]']/span";
	private String asFoundStatusAction = ".//*[@id='jv23x_asfound[0]_action']";
	private String asLeftStatus = ".//*[@id='qwad7[0]']/span";
	private String asLeftStatusAction = ".//*[@id='qwad7[0]_action']";
	private String dataSheetNextArrow = ".//*[@id='gm4av[0]']";
	
	

	public DatasheetListPage(AbstractAutomationFramework af) {
		this.af = af;
	}
	
	public boolean isDataSheetListPage() throws Exception {
		try {
			af.waitForElementToBePresent(By.id(pageSubTitle));
		} catch (NoSuchElementException e) {
			return false;
		}
		return true;
	}
	
	public void clickBackArrowBtn() throws Exception {
		af.waitForElementToBePresent(By.id(backArrowBtn)).click();
	}

	public String getPageSubtitle() throws Exception {
		return af.waitForElementToBePresent(By.id(pageSubTitle)).getText();
	}
	
	public String getDatasheetName() throws Exception {
		return af.waitForElementToBePresent(By.xpath(dataSheetName)).getText();
	}
	
	public String getAsFoundStatus() throws Exception {
		return af.waitForElementToBePresent(By.xpath(asFoundStatus)).getText();
	}
	
	public void clickAsFoundStatusActionBtn() throws Exception {
		af.waitForElementToBePresent(By.xpath(asFoundStatusAction)).click();
	}
	
	public String getAsLeftStatus() throws Exception {
		return af.waitForElementToBePresent(By.xpath(asLeftStatus)).getText();
	}
	
	public void clickAsLeftStatusActionBtn() throws Exception {
		af.waitForElementToBePresent(By.xpath(asLeftStatusAction)).click();
	}
	
	public void clickNavigationToDatasheet() throws Exception {
		af.waitForElementToBePresent(By.xpath(dataSheetNextArrow)).click();
	}
}