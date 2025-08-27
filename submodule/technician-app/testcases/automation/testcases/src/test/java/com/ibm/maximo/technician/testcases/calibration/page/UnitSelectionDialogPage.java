package com.ibm.maximo.technician.testcases.calibration.page;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Random;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.maximo.automation.mobile.AbstractAutomationFramework;
import com.ibm.maximo.automation.mobile.MobileAutomationFramework;

public class UnitSelectionDialogPage {

	private AbstractAutomationFramework af;
	private final static Logger logger = LoggerFactory.getLogger(UnitSelectionDialogPage.class);

	public enum Units {
		Percent, IVP, DegC, DegF, GMP, Inches, Kg, RPM, Volts, btu, lbs, mA, mm, psi;

		public static Units getRandom() {
			return values()[(int) (Math.random() * values().length)];
		}
		
		public static Units getRandomTempUnit() {
			return new Random().nextInt(2) == 1 ? DegC : DegF;
		}
	}

	private String pageTitle = "unitsLookup_sliding_drawer_headerLabel";
	private String percentLabel1 = "%_attr0_row_fieldValue0";
	private String percentLabel2 = "%_attr1_row_fieldValue0";
	private String percentbtn = ".//button[contains(@id,'%_selectionCheckBoxIcon_touch')]";
	private String ivpPercentLabel1 = "% IVP_attr0_row_fieldValue0";
	private String ivpPercentLabel2 = "% IVP_attr1_row_fieldValue0";
	private String ivpPercentBtn = ".//button[contains(@id,'% IVP_selectionCheckBoxIcon_touch')]";
	private String degCLabel1 = "Deg C_attr0_row_fieldValue0";
	private String degCLabel2 = "Deg C_attr1_row_fieldValue0";
	private String degCBtn = ".//button[contains(@id,'Deg C_selectionCheckBoxIcon_touch')]";
	private String degFLabel1 = "Deg F_attr0_row_fieldValue0";
	private String degFLabel2 = "Deg F_attr1_row_fieldValue0";
	private String degFBtn = ".//button[contains(@id,'Deg F_selectionCheckBoxIcon_touch')]";
	private String gmpLabel1 = "GPM_attr0_row_fieldValue0";
	private String gmpLabel2 = "GPM_attr1_row_fieldValue0";
	private String gmpBtn = ".//button[contains(@id,'GPM_selectionCheckBoxIcon_touch')]";
	private String inchesLabel1 = "Inches_attr0_row_fieldValue0";
	private String inchesLabel2 = "Inches_attr1_row_fieldValue0";
	private String inchesBtn = ".//button[contains(@id,'Inches_selectionCheckBoxIcon_touch')]";
	private String kgLabel1 = "Kg_attr0_row_fieldValue0";
	private String kgLabel2 = "Kg_attr1_row_fieldValue0";
	private String kgBtn = ".//button[contains(@id,'Kg_selectionCheckBoxIcon_touch')]";
	private String rpmLabel1 = "RPM_attr0_row_fieldValue0";
	private String rpmLabel2 = "RPM_attr1_row_fieldValue0";
	private String rpmBtn = ".//button[contains(@id,'RPM_selectionCheckBoxIcon_touch')]";
	private String voltsLabel1 = "Volts_attr0_row_fieldValue0";
	private String voltsLabel2 = "Volts_attr1_row_fieldValue0";
	private String voltsBtn = ".//button[contains(@id,'Volts_selectionCheckBoxIcon_touch')]";
	private String btuLabel1 = "btu_attr0_row_fieldValue0";
	private String btuLabel2 = "btu_attr1_row_fieldValue0";
	private String btuBtn = ".//button[contains(@id,'btu_selectionCheckBoxIcon_touch')]";
	private String lbsLabel1 = "lbs_attr0_row_fieldValue0";
	private String lbsLabel2 = "lbs_attr1_row_fieldValue0";
	private String lbsBtn = ".//button[contains(@id,'lbs_selectionCheckBoxIcon_touch')]";
	private String maLabel1 = "mA_attr0_row_fieldValue0";
	private String maLabel2 = "mA_attr1_row_fieldValue0";
	private String maBtn = ".//button[contains(@id,'mA_selectionCheckBoxIcon_touch')]";
	private String mmLabel1 = "mm_attr0_row_fieldValue0";
	private String mmLabel2 = "mm_attr1_row_fieldValue0";
	private String mmBtn = ".//button[contains(@id,'mm_selectionCheckBoxIcon_touch')]";
	private String psiLabel1 = "psi_attr0_row_fieldValue0";
	private String psiLabel2 = "psi_attr1_row_fieldValue0";
	private String psiBtn = ".//button[contains(@id,'psi_selectionCheckBoxIcon_touch')]";

	public UnitSelectionDialogPage(AbstractAutomationFramework af) {
		this.af = af;
	}

	public String getPageTitle() throws Exception {
		return af.waitForElementToBePresent(By.id(pageTitle)).getText();
	}

	public void verifyAndSelectUnitBtn(String label1Locator, String label1CompareValue, String label2Locator,
			String lable2CompareValue, String unitBtnLocator) throws Exception {
		((MobileAutomationFramework) af).scrollDownUtilElementIsVisible(By.xpath(unitBtnLocator), 10L);
		
		assertTrue((af.waitForElementToBePresent(By.id(label1Locator)).getText().equals(label1CompareValue)
				&& af.waitForElementToBePresent(By.id(label2Locator)).getText().equals(lable2CompareValue)
				&& af.isElementExists(By.xpath(unitBtnLocator))));
		
		WebElement unitButtonElement = af.getWebDriver().findElement(By.xpath(unitBtnLocator));
		
		try {
			af.getDriver().executeScript("arguments[0].scrollIntoView({ block: 'center' })", unitButtonElement);
			unitButtonElement.click();
		} catch (Exception e) {
			// execute Javascript click
			af.getDriver().executeScript("arguments[0].click()", unitButtonElement);
		}
	}

	/**
	 * @param unitsValue
	 * @return
	 * @throws Exception
	 */
	public String selectUnitField(String unitsValue) throws Exception {
		String selectedValue = null;
		switch (unitsValue) {
		case "Percent":
			verifyAndSelectUnitBtn(percentLabel1, "%", percentLabel2, "Percent", percentbtn);
			selectedValue = "%";
			break;
		case "IVP":
			verifyAndSelectUnitBtn(ivpPercentLabel1, "% IVP", ivpPercentLabel2, "% IVP", ivpPercentBtn);
			selectedValue = "% IVP";
			break;
		case "DegC":
			verifyAndSelectUnitBtn(degCLabel1, "Deg C", degCLabel2, "Degrees C", degCBtn);
			selectedValue = "Deg C";
			break;
		case "DegF":
			verifyAndSelectUnitBtn(degFLabel1, "Deg F", degFLabel2, "Degrees F", degFBtn);
			selectedValue = "Deg F";
			break;
		case "GMP":
			verifyAndSelectUnitBtn(gmpLabel1, "GPM", gmpLabel2, "Gallons Per Minute", gmpBtn);
			selectedValue = "GPM";
			break;
		case "Inches":
			verifyAndSelectUnitBtn(inchesLabel1, "Inches", inchesLabel2, "Inches", inchesBtn);
			selectedValue = "Inches";
			break;
		case "Kg":
			verifyAndSelectUnitBtn(kgLabel1, "Kg", kgLabel2, "Kilogram", kgBtn);
			selectedValue = "Kg";
			break;
		case "RPM":
			verifyAndSelectUnitBtn(rpmLabel1, "RPM", rpmLabel2, "Rotations Per Minute", rpmBtn);
			selectedValue = "RPM";
			break;
		case "Volts":
			verifyAndSelectUnitBtn(voltsLabel1, "Volts", voltsLabel2, "Volts", voltsBtn);
			selectedValue = "Volts";
			break;
		case "btu":
			verifyAndSelectUnitBtn(btuLabel1, "btu", btuLabel2, "British Thermal Units", btuBtn);
			selectedValue = "btu";
			break;
		case "lbs":
			verifyAndSelectUnitBtn(lbsLabel1, "lbs", lbsLabel2, "Pounds", lbsBtn);
			selectedValue = "lbs";
			break;
		case "mA":
			verifyAndSelectUnitBtn(maLabel1, "mA", maLabel2, "Milli Amps", maBtn);
			selectedValue = "mA";
			break;
		case "mm":
			verifyAndSelectUnitBtn(mmLabel1, "mm", mmLabel2, "Millimiter", mmBtn);
			selectedValue = "mm";
			break;
		case "psi":
			verifyAndSelectUnitBtn(psiLabel1, "psi", psiLabel2, "Pounds per Square Inch", psiBtn);
			selectedValue = "psi";
			break;
		}

		return selectedValue;
	}
}