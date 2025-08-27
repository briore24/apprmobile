package com.ibm.maximo.technician.page;

import org.openqa.selenium.By;

import com.ibm.maximo.automation.mobile.AbstractAutomationFramework;
import com.ibm.maximo.components.ButtonComponent;
import com.ibm.maximo.components.LabelComponent;
import com.ibm.maximo.components.TextAreaComponent;

public class FailureReportingPage {

	private AbstractAutomationFramework af;

	private String failureClassButton = "j5anr[0]";
	private String failureClassValue = "mrm4e[$index]";
	private String problemButton = "j5anr[1]";
	private String problemValue = "ynnqx[$index]";
	public String crossProblemButton = "nweme[1]";	
	public String crossCauseButton = "nweme[2]";	
	public String crossRemedyButton = "nweme[3]";		
	private String causeButton = "j5anr[2]";
	private String causeValue = "ej4y7[$index]";
	private String remedyButton = "j5anr[3]";
	private String remedyValue = "qdpa6[$index]";
	private String summaryFIeld = "failureRemark";
	private String txtAreaDescription="mw4md";
	private String saveButton = "pageHeaderTemplate_buttongroup_m4458";
	private String backButton = "pageHeaderTemplate_iconBox";
	private String closeFailureClass="nweme[0]";
	public String PageHeader = "pageHeaderTemplate_title";
	private String crossValue = "nweme[$index]";
	private String detailsIcon = "j5anr[4]";
	private String failureDateAndTimeChevron = "w4y6w";

	public FailureReportingPage(AbstractAutomationFramework af) {
		this.af = af;
	}
	
	/**
	 * Click failure class elements
	 * 
	 * @param buttonLocator
	 */
	public ReportWorkPage clickElement(String buttonLocator) {
	    af.instantiateComponent(ButtonComponent.class, buttonLocator).click();
	    return new ReportWorkPage(af);
	}

	/**
	 * Open failure class list
	 * 
	 * @throws Exception
	 */
	public ReportWorkPage openFailureClassList() throws Exception {
		return clickElement(failureClassButton);
	}

	/**
	 * Open problem list
	 * 
	 * @throws Exception
	 */
	public ReportWorkPage openProblemList() throws Exception {
		return clickElement(problemButton);
	}

	/**
	 * Open cause list
	 * 
	 * @throws Exception
	 */
	public ReportWorkPage openCauseList() throws Exception {
		return clickElement(causeButton);
	}

	/**
	 * Open remedy list
	 * 
	 * @throws Exception
	 */
	public ReportWorkPage openRemedyList() throws Exception {
		return clickElement(remedyButton);
	}

	/**
	 * Choose failure class
	 * 
	 * @param optionIndex
	 * @throws Exception
	 */
	public ReportWorkPage chooseFailureClassValue(int optionIndex) throws Exception {
		af.instantiateComponent(ButtonComponent.class, failureClassValue.replace("$index", String.valueOf(optionIndex))).click();
		return new ReportWorkPage(af);
	}

	/**
	 * Choose problem
	 * 
	 * @param optionIndex
	 * @throws Exception
	 */
	public ReportWorkPage chooseProblemValue(int optionIndex) throws Exception {
		af.instantiateComponent(ButtonComponent.class, problemValue.replace("$index", String.valueOf(optionIndex))).click();
		return new ReportWorkPage(af);
	}

	/**
	 * Choose cause
	 * 
	 * @param optionIndex
	 * @throws Exception
	 */
	public ReportWorkPage chooseCauseValue(int optionIndex) throws Exception {
		af.instantiateComponent(ButtonComponent.class, causeValue.replace("$index", String.valueOf(optionIndex))).click();
		return new ReportWorkPage(af);
	}

	/**
	 * Choose remedy
	 * 
	 * @param optionIndex
	 * @throws Exception
	 */
	public ReportWorkPage chooseRemedyValue(int optionIndex) throws Exception {
		af.instantiateComponent(ButtonComponent.class, remedyValue.replace("$index", String.valueOf(optionIndex))).click();
		return new ReportWorkPage(af);
	}

	/**
	 * Add remarks
	 * 
	 * @param Summary
	 * @throws Exception
	 */
	public void addSummary(String remarks) throws Exception {
	    new TextAreaComponent(af.getDriver(), summaryFIeld, true).type(remarks);
	}

	/**
	 * Click save button
	 * 
	 * @throws Exception
	 */
	public ReportWorkPage save() throws Exception {
		af.waitForElementToBePresent(By.id(saveButton));
		af.instantiateComponent(ButtonComponent.class, saveButton).click();
		return new ReportWorkPage(af);
	}
	
	/**
	 * Click back chevron
	 * 
	 * @throws Exception
	 */
	public ReportWorkPage back() throws Exception {
		af.instantiateComponent(ButtonComponent.class, backButton).click();
		return new ReportWorkPage(af);
	}
		
	/**
	 * Clear remarks
	 * 
	 * @throws Exception
	 */
	public void clearRemarks() throws Exception {
	    new TextAreaComponent(af.getDriver(), summaryFIeld, true).clearField();
	}

	/**
	 * Click 'X' to clear problem
	 * @throws Exception 
	 */
	public void closeProblemValue() throws Exception {
		af.waitForElementToBePresent(By.id(crossProblemButton));
		af.instantiateComponent(ButtonComponent.class, crossProblemButton).click();
	}
	
	/**
	 * Click 'X' to clear cause
	 * @throws Exception 
	 */
	public void closeCauseValue() throws Exception {
		af.waitForElementToBePresent(By.id(crossCauseButton));
		af.instantiateComponent(ButtonComponent.class, crossCauseButton).click();
	}
	
	/**
	 * Click 'X' to clear remedy
	 * @throws Exception 
	 */
	public void closeRemedyValue() throws Exception {
		af.waitForElementToBePresent(By.id(crossRemedyButton));
		af.instantiateComponent(ButtonComponent.class, crossRemedyButton).click();
	}
	
	/**
	 * Add Description
	 * 
	 * @param desc
	 * @throws Exception
	 */
	public void addDescription(String desc) throws Exception {
	    new TextAreaComponent(af.getDriver(), txtAreaDescription, true).type(desc);
	}
	
	/**
	 * Click 'X' to clear failure Class
	 * @throws Exception 
	 */
	public void closeFailureClass() throws Exception {
		af.waitForElementToBePresent(By.id(closeFailureClass));
		af.instantiateComponent(ButtonComponent.class, closeFailureClass).click();
	}
	

	/**
	 * Method to verify if problem chevron exists
	 * 
	 * @return
	 */
	public boolean isProblemChevronNotExist() {
		return af.isElementExists(By.id(problemButton));
	}
	
	/**
	 * Method to verify if cause chevron exists
	 * 
	 * @return
	 */
	public boolean isCauseChevronNotExist() {
		return af.isElementExists(By.id(causeButton));
	}
	
	/**
	 * Method to verify if remedy chevron exists
	 * 
	 * @return
	 */
	public boolean isRemedyChevronNotExist() {
		return af.isElementExists(By.id(remedyButton));
	}
	
	
	/**
	 * Method to verify if Failure date and time chevron exists
	 * 
	 * @return
	 */
	public boolean isFailureDateAndTimeChevronExist() {
		return af.isElementExists(By.id(failureDateAndTimeChevron));
	}
	
	/**
	 * Method to enter Failure date and time
	 * @throws Exception 
	 */
	public void clickFailureDateAndTime() throws Exception {
		af.waitForElementToBePresent(By.id(failureDateAndTimeChevron));
		af.instantiateComponent(ButtonComponent.class, failureDateAndTimeChevron).click();
	}
	
	/**
	 * Check child fields are cleared out when parent field is removed
	 * 
	 * @param id
	 * @throws Exception
	 */
	public boolean checkChildFieldIsRemoved(String id) {
		return af.isElementExists(By.id(id));

	}
	
	/**
	 * Method to click back from failure class
	 * 
	 * @throws Exception
	 */
	public void backButton() throws Exception {
		af.instantiateComponent(ButtonComponent.class, "pageHeaderTemplate_icon").click();
	}
	
	/**
	 * This is generic getTitle method to get string value of element
	 *
	 * @param elementID {string} value of id to get text value of it
	 * @throws Exception
	 */
	public String getTitle(String elementID) throws Exception {
		af.waitForElementToBePresent(By.id(elementID), af.DEFAULT_TIMEOUT_MS * 10);
		String pageTitle = af.instantiateComponent(LabelComponent.class, elementID).text();
		return pageTitle;
	}
	
	/**
	 * Method to verify X icon is displayed for failure class
	 * 
	 * @param optionIndex
	 * @return
	 * @throws Exception
	 */
	public boolean checkCrossIconDisplayed(int optionIndex) throws Exception {
		return af.isElementExists(By.id(crossValue.replace("$index", String.valueOf(optionIndex))));
	}
	
	/**
	 * Click on details field
	 * 
	 * @return
	 * @throws Exception
	 */
	public void detailsField() throws Exception {
	    	
		//af.waitForElementToBePresent(By.xpath(detailsIcon), af.DEFAULT_TIMEOUT_MS * 2);
		af.waitForElementToBePresent(By.id(detailsIcon),1000).click();
	}
	
	
	}
