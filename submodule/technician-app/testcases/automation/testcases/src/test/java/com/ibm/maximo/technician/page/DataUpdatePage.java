package com.ibm.maximo.technician.page;

import java.util.concurrent.TimeoutException;

import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.maximo.automation.mobile.AbstractAutomationFramework;
import com.ibm.maximo.components.ButtonComponent;
import com.ibm.maximo.components.LabelComponent;

public class DataUpdatePage {
	private AbstractAutomationFramework af;
	private final static Logger logger = LoggerFactory.getLogger(ReportWorkPage.class);
	private String syncBadgeLocator = "navigator_syncbadge";
	private String pageHeaderTitleLocator = "m56yx-pageheader_title";

    public DataUpdatePage(AbstractAutomationFramework af) {
    	this.af= af;
    }
   
	public void openDataUpdatePage() throws Exception {
		if (!isDataUpdatePage()) {
			logger.info("open DataUpdatePage");	
			af.waitForElementToBePresent(By.id("ApplicationSyncButton"));
			af.instantiateComponent(ButtonComponent.class, "ApplicationSyncButton").click();
			af.waitForElementToBePresent(By.id(pageHeaderTitleLocator),2000);
		}
	}
	
	public boolean isDataUpdatePage() throws Exception {
		return af.isElementExists(By.id(pageHeaderTitleLocator));
	}

	public boolean noSynchronizationInProgress() throws Exception {
		String elementId = "a8p_a", text;
		
		af.waitForElementToBePresent(By.id(elementId),2000);
		if (af.isElementExists(By.id(pageHeaderTitleLocator))) {
			text =  af.instantiateComponent(LabelComponent.class, elementId).getValue();
			return "No transactions to upload".equals(text);}
		else
			return false;
	}
	
	public void waitSyncBadgeDisappear() throws Exception {
		waitSyncBadgeDisappear(10);
	}
	
	public int waitSyncBadgeDisappear(int timeout) throws Exception {
		logger.info("wait data update sync badge disappear in " + timeout + " seconds");
		try {
			// Using waitForElementToNotBePresent() over waitNotPresent() to catch the exception
			af.waitForElementToNotBePresent(By.id(syncBadgeLocator), timeout);
		}
		// Catching the case where waiting timed out
		catch (TimeoutException e) {
			return 1;
		}
		// Catching all other exceptions
		catch (Exception e) {
			return 2;
		}
		return 0;
	}
	
}