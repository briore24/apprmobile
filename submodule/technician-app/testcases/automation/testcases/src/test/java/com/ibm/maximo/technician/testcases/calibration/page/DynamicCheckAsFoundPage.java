package com.ibm.maximo.technician.testcases.calibration.page;

import static org.testng.Assert.assertEquals;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.maximo.automation.mobile.AbstractAutomationFramework;
import com.ibm.maximo.technician.testcases.calibration.page.UnitSelectionDialogPage.Units;

public class DynamicCheckAsFoundPage {

	private AbstractAutomationFramework af;
	// JavascriptExecutor jsExec;
	private final static Logger logger = LoggerFactory.getLogger(DynamicCheckAsFoundPage.class);
	public static String dynamicCheckAsFoundPageTitle = "As found values";

	private String backArrowBtn = "qx36d-pageheader_icon";
	private String pageTitle = "qx36d-pageheader_title";
	private String assetFunctionName = "ed7dj";
	private String assetFunctionStatus = ".//span[@id='nkxd2_0']//span";
	private String dynamicCheckPointkName = ".//p[@id='a2kpx[%s]']";
	private String valueInputFields = ".//input[contains(@id,'zpkag')]";
	private String valueInput = "(.//li)[%s]//input[contains(@id,'zpkag')]";
	private String unitInput = "(.//li)[%s]//input[contains(@id,'q9bxz') and contains(@id, '_lookup_buttongroup')]";
	private String unitSearchBtn = "(.//li)[%s]//button[contains(@id,'q9bxz') and contains(@id, '_actionbuttongroup_g47ye')]";
	private String dynamicCheckSaveBtn = "qx36d-pageheader_buttongroup_vkyad";

	public DynamicCheckAsFoundPage(AbstractAutomationFramework af) {
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

	public String getDynamicCheckPointName(int dynamciCheckPointIndex) throws Exception {
		return af
				.waitForElementToBePresent(
						By.xpath(dynamicCheckPointkName.replace("%s", String.valueOf(dynamciCheckPointIndex))))
				.getText().trim();
	}

	public void enterValueInput(int index, String value) throws Exception {
		af.waitForElementToBePresent(By.xpath(valueInput.replace("%s", String.valueOf(index + 1)))).sendKeys(value);
	}

	public String getValueInput(int index) throws Exception {
		return af.waitForElementToBePresent(By.xpath(valueInput.replace("%s", String.valueOf(index + 1))))
				.getAttribute("value").toString();
	}

	public void clickUnitSearchBtn(int index) throws Exception {
		af.waitForElementToBePresent(By.xpath(unitSearchBtn.replace("%s", String.valueOf(index + 1)))).click();
	}

	public String getUnitInput(int index) throws Exception {
		return af.waitForElementToBePresent(By.xpath(unitInput.replace("%s", String.valueOf(index + 1))))
				.getAttribute("value").toString();
	}

	public void saveAsFoundValues() throws Exception {
		af.waitForElementToBePresent(By.id(dynamicCheckSaveBtn)).click();
	}

	public void enterValueAndSelectUnit() throws Exception {
		UnitSelectionDialogPage unitSelectPageObj = new UnitSelectionDialogPage(af);
		List<WebElement> valueInputEle = (List<WebElement>) af.getDriver().findElements(By.xpath(valueInputFields));
		int randomValue;
		String unitsValue, selectedValues = null;
		for (int i = 0; i < valueInputEle.size(); i++) {
			randomValue = (int) (Math.random() * 100);
			unitsValue = Units.getRandom().toString();
			enterValueInput(i, String.valueOf(randomValue + 1));
			clickUnitSearchBtn(i);
			logger.info("Selected Unit[" + i + "] : " + unitSelectPageObj.selectUnitField(unitsValue));
			selectedValues = selectedValues + (randomValue + 1) + "=" + unitsValue + ",";
		}
		logger.info("Value entered and selected : " + selectedValues.replace("null", ""));
		System.setProperty("AsFoundDynamicCheckSelectedValue", selectedValues.replace("null", ""));
	}

	public String getUnitsValue(String unitsValue) {
		String value = null;
		switch (unitsValue) {
		case "Percent":
			value = "%";
			break;
		case "IVP":
			value = "% IVP";
			break;
		case "DegC":
			value = "Deg C";
			break;
		case "DegF":
			value = "Deg F";
			break;
		case "GMP":
			value = "GPM";
			break;
		case "Inches":
			value = "Inches";
			break;
		case "Kg":
			value = "Kg";
			break;
		case "RPM":
			value = "RPM";
			break;
		case "Volts":
			value = "Volts";
			break;
		case "btu":
			value = "btu";
			break;
		case "lbs":
			value = "lbs";
			break;
		case "mA":
			value = "mA";
			break;
		case "mm":
			value = "mm";
			break;
		case "psi":
			value = "psi";
			break;
		}
		return value;
	}

	public void verifySavedValues() throws Exception {
		List<WebElement> valueInputEle = (List<WebElement>) af.getDriver().findElements(By.xpath(valueInputFields));
		String selectedValues = System.getProperty("AsFoundDynamicCheckSelectedValue");
		for (int i = 0; i < valueInputEle.size(); i++) {
			assertEquals(getValueInput(i), selectedValues.split(",")[i].split("=")[0],
					"Verify entered input values matched");
			assertEquals(getUnitInput(i), getUnitsValue(selectedValues.split(",")[i].split("=")[1]),
					"Verify selected units value matched");
		}
	}

	public boolean isSaveAsFoundValuesBtnDisabled() throws Exception {
		if (af.waitForElementToBePresent(By.id(dynamicCheckSaveBtn)).getAttribute("class").contains("disabled")) {
			return true;
		} else {
			return false;
		}
	}
}