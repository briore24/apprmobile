package com.ibm.maximo.technician.testcases.calibration.page;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.maximo.automation.mobile.AbstractAutomationFramework;
import com.ibm.maximo.technician.testcases.calibration.page.UnitSelectionDialogPage.Units;

public class EnvironmentalConditionPage {

	private AbstractAutomationFramework af;
	private final static Logger logger = LoggerFactory.getLogger(EnvironmentalConditionPage.class);
	
	private String pageTitle = "EnvironmentalConditionsDialog_headerLabel";
	private String temperatureInput = ".//input[starts-with(@id,'kdqpw')]";
	private String temperatureUnitInput = ".//input[starts-with(@id,'p5qq3')]";
	private String temperatureUnitSearchBtn = ".//button[starts-with(@id,'p5qq3') and contains(@id,'wkz5n')]";
	private String humidityInput = ".//input[starts-with(@id,'y94bp')]";
	private String humidityUnitInput = ".//input[starts-with(@id,'nqk_e')]";
	private String humidityUnitSearchBtn= ".//button[starts-with(@id,'nqk_e') and contains(@id,'pq7dz')]";
	private String berometricPressureInput = ".//input[starts-with(@id,'g955v')]";
	private String berometricPressureUnitInput = ".//input[starts-with(@id,'npxb8')]";
	private String berometricPressureUnitSearchBtn = ".//button[starts-with(@id,'npxb8') and contains(@id,'myj6w')]";
	private String saveBtn = "p9y2q";
	private String closeBtn = "EnvironmentalConditionsDialog_button";
	

	public EnvironmentalConditionPage(AbstractAutomationFramework af) {
		this.af = af;
	}

	public String getPageTitle() throws Exception {
		return af.waitForElementToBePresent(By.id(pageTitle)).getText();
	}
	
	public void inputTemperature(String temperatureValue) throws Exception {
		WebElement temperatureInputEle = af.waitForElementToBePresent(By.xpath(temperatureInput));
		temperatureInputEle.clear();
		temperatureInputEle.sendKeys(temperatureValue);
	}
	
	public String getTemperatureInput() throws Exception {
		return af.waitForElementToBePresent(By.xpath(temperatureInput)).getAttribute("value").toString();
	}
	
	public String getTempUnitInputValue() throws Exception {
		return af.waitForElementToBePresent(By.xpath(temperatureUnitInput)).getAttribute("value").toString();
	}
	
	public void clickTempUnitSearchBtn() throws Exception {
		af.waitForElementToBePresent(By.xpath(temperatureUnitSearchBtn)).click();
	}
	
	public void inputHumidity(String humidityValue) throws Exception {
		WebElement humidityInputEle = af.waitForElementToBePresent(By.xpath(humidityInput));
		humidityInputEle.clear();
		humidityInputEle.sendKeys(humidityValue);
	}
	
	public String getHumidityInput() throws Exception {
		return af.waitForElementToBePresent(By.xpath(humidityInput)).getAttribute("value").toString();
	}
	
	public String getHumidUnitInputValue() throws Exception {
		return af.waitForElementToBePresent(By.xpath(humidityUnitInput)).getAttribute("value").toString();
	}
	
	public void clickHumidUnitSearchBtn() throws Exception {
		af.waitForElementToBePresent(By.xpath(humidityUnitSearchBtn)).click();
	}
	
	public void inputBarometricPressure(String berometricPressureValue) throws Exception {
		WebElement berometricPressureInputEle = af.waitForElementToBePresent(By.xpath(berometricPressureInput));
		berometricPressureInputEle.clear();
		berometricPressureInputEle.sendKeys(berometricPressureValue);
	}
	
	public String getBarometricPressureInput() throws Exception {
		return af.waitForElementToBePresent(By.xpath(berometricPressureInput)).getAttribute("value").toString();
	}
	
	public String getBarometricPressureUnitInputValue() throws Exception {
		return af.waitForElementToBePresent(By.xpath(berometricPressureUnitInput)).getAttribute("value").toString();
	}
	
	public void clickBarometricPressureUnitSearchBtn() throws Exception {
		af.waitForElementToBePresent(By.xpath(berometricPressureUnitSearchBtn)).click();
	}
	
	public void clickSaveBtn() throws Exception {
		af.waitForElementToBePresent(By.id(saveBtn)).click();
	}
	
	public void clickCloseBtn() throws Exception {
		af.waitForElementToBePresent(By.id(closeBtn)).click();
	}
	
	public String enterRandomUnit(boolean validTemperatureUnit) throws Exception {
		UnitSelectionDialogPage unitSelectPageObj = new UnitSelectionDialogPage(af);
		String selectedUnit = validTemperatureUnit ? Units.getRandomTempUnit().toString() : Units.getRandom().toString();
		
		return unitSelectPageObj.selectUnitField(selectedUnit);
	}
	
	public String enterTemperatureWithRandomUnit(String envCondiTempValue) throws Exception {
		logger.info("Enter Temperature.");
		this.inputTemperature(envCondiTempValue);
		logger.info("Select unit for Temperature.");
		this.clickTempUnitSearchBtn();

		String selectedTempUnit = this.enterRandomUnit(true);
		logger.info("Selected Temperature Unit value : " + selectedTempUnit);
		assertEquals(this.getTempUnitInputValue(), selectedTempUnit, "Verify selected temperature unit to be: " + selectedTempUnit);
		
		return selectedTempUnit;
	}
	
	public String enterHumidityWithRandomUnit(String envCondiHumidValue) throws Exception {
		logger.info("Enter Humidity.");
		this.inputHumidity(envCondiHumidValue);
		logger.info("Select unit for Humidity.");
		this.clickHumidUnitSearchBtn();
		
		String selectedHumidUnit = this.enterRandomUnit(false);
		logger.info("Selected Humid Unit value : " + selectedHumidUnit);
		assertEquals(this.getHumidUnitInputValue(), selectedHumidUnit, "Verify selected humidity unit to be: " + selectedHumidUnit);
		
		return selectedHumidUnit;
	}
	
	public String enterBaroPressureWithRandomUnit(String envCondiBaroPressureValue) throws Exception {
		logger.info("Enter Barometric Pressure.");
		this.inputBarometricPressure(envCondiBaroPressureValue);
		logger.info("Select unit for Berometric Pressure.");
		this.clickBarometricPressureUnitSearchBtn();
		
		String selectedBarometricPressureUnit = this.enterRandomUnit(false);
		logger.info("Selected Barometric Pressure Unit value : " + selectedBarometricPressureUnit);
		assertEquals(this.getBarometricPressureUnitInputValue(), selectedBarometricPressureUnit,  "Verify selected barometric pressure unit to be: " + selectedBarometricPressureUnit);
		
		return selectedBarometricPressureUnit;
	}

	public void verifySelectedInputAndUnits(String envCondiTempValue, String selectedTempUnit,
			String envCondiHumidValue, String selectedHumidUnit, String envCondiBaroPressureValue,
			String selectedBarometricPressureUnit) throws Exception {
		logger.info("Verify the data on Environmental Conditions page.");
		assertEquals(this.getTemperatureInput(), envCondiTempValue, "Verify entered Temperature value.");
		assertEquals(this.getTempUnitInputValue(), selectedTempUnit, "Verify Temperature unit selected.");
		assertEquals(this.getHumidityInput(), envCondiHumidValue, "Verify entered Humidity value.");
		assertEquals(this.getHumidUnitInputValue(), selectedHumidUnit, "Verify Humidity unit selected.");
		assertEquals(this.getBarometricPressureInput(), envCondiBaroPressureValue,
				"Verify entered Berometric Pressure value.");
		assertEquals(this.getBarometricPressureUnitInputValue(), selectedBarometricPressureUnit,
				"Verify Berometric Pressure value.");
	}
}