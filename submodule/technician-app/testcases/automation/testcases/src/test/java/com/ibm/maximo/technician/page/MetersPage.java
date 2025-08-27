package com.ibm.maximo.technician.page;

import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.Date;
import java.util.TimeZone;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.maximo.automation.mobile.AbstractAutomationFramework;
import com.ibm.maximo.components.ButtonComponent;
import com.ibm.maximo.components.LabelComponent;
import com.ibm.maximo.components.NumberInputComponent;
import com.ibm.maximo.components.TextInputComponent;

public class MetersPage {
	private final Logger logger = LoggerFactory.getLogger(MetersPage.class);
	private AbstractAutomationFramework af;
	private String chevronButtonOnAssetMeter = "neywn[0]";
	private String enterMeterReadingInFirstAssetMeter = "bjrye[1]";
	private String enterMeterReadingInSecondAssetMeter = "bjrye[2]";
	private String saveButton = "qm2zd_a94nq";
	private String cancelButton = "meterReadingDrawer_button";
	private String xButton = "multiMeterReadingDrawer_button";
	private String addMeterReadingsButton = "kz95b_zkzw9";
	private String checkFirstAssetMeterValueSet = "qg7np[0]_textoverflow_value0";
	private String checkSecondAssetMeterValueSet = "qg7np[1]_textoverflow_value0";
	private String checkThirdAssetMeterValueSet = "qg7np[2]_textoverflow_value0";
	private String checkFirstLocationMeterValueSet = "e7_w7[0]_fieldValue0";
	private String checkSecondLocationMeterValueSet = "e7_w7[1]_fieldValue0";
	private String checkThirdLocationMeterValueSet = "e7_w7[2]_fieldValue0";
	private String chevronButtonOnLocationMeter = "qbn66[0]";
	private String enterMeterReadingInFirstLocationMeter = "r96zn[1]";
	private String enterMeterReadingInSecondLocationMeter = "r96zn[2]";
	private String meterHeaderLabel = "multiMeterReadingDrawer_headerLabel";
	private String getMeterValue = "zkxrj[0]_fieldValue0";
	private String meterIconOnWODetail = "byzkj_xpvzw";
	private String cancelMetersDrawerOnWoDetail = "maxlib_meterReadingDrawer_button";
	private String closeSystemMsg = "graphite_dialog_error_close_button";
	private String errorMessageLocator = "//*[@class='mx--dialog-content-body']";
	private String cancelUpdateMeterReadingViewLocator = "update_meterReading_drawer_detail_button";
	private String disCardUpdateMeterReadingLocator = "graphite_unsaved_changes_button_group_graphite_unsaved_changes_secondary_button";
	private String threeDotsMeters = "maxlib_dkg2g";
	private String previousReadings = "maxlib_dkg2g_menu_menu_maxlib_m3n38";
	
	private WebElement element;
	WebDriver driver;

	public MetersPage(AbstractAutomationFramework af) {
		this.af = af;
	}

	// Method for clicking on chevron Asset Meter button
	public void chevronAssetMeter() throws Exception {
		checkPresentAndClick(chevronButtonOnAssetMeter);
	}

	// Method for clicking on chevron location meter button
	public void chevronLocationMeter() throws Exception {
		checkPresentAndClick(chevronButtonOnLocationMeter);
	}

	// Method for clicking on selectCharacteristicReadingValues button
	public void selectCharacteristicReadingValues() throws Exception {
		String selectReadingValue = "meterReadingLookup_lookup_datalist_ALN_selectionCheckBoxIcon_touch";
		checkPresentAndClick(selectReadingValue);
	}

	// Method for clicking on save button
	public void save() throws Exception {
		checkPresentAndClick(saveButton);
	}

	// Method for clicking on cancel button
	public void cancel() throws Exception {
		checkPresentAndClick(cancelButton);
	}

	// Method for clicking on x button
	public void XClick() throws Exception {
		checkPresentAndClick(xButton);
	}

	// Method for clicking on add meter readings button
	public boolean addMeterReadings(String value) throws Exception {
		try {
			checkPresentAndClick(addMeterReadingsButton);
			chevronAssetMeter();
			selectCharacteristicReadingValues();
			enterAssetMeterReadingValues(value);
			chevronLocationMeter();
			selectCharacteristicReadingValues();
			enterLocationMeterReadingValues(value);
			save();
			af.waitForElementToNotBePresent(By.id(saveButton), 1000);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Add meter reading from WO Details
	 * 
	 * @param value meter reading value
	 * @return boolean base on value enter
	 * @throws Exception
	 */
	public boolean addMeterReadingsFromWoDetail(String value) throws Exception {
		String addMeterReadingsButton = "wk7dq_j79ab", chevronButtonOnAssetMeter = "v7323[0]",
				selectReadingValue = "meterReadingLookupDetail_lookup_datalist_ALN_selectionCheckBoxIcon_touch",
				enterMeterReadingInFirstAssetMeter = "kz_2d[1]", enterMeterReadingInSecondAssetMeter = "kz_2d[2]",
				chevronButtonOnLocationMeter = "wzngx[0]", enterMeterReadingInFirstLocationMeter = "a8pb5[1]",
				enterMeterReadingInSecondLocationMeter = "a8pb5[2]", saveButton = "v69a__ere89";

		try {
			checkPresentAndClick(addMeterReadingsButton);
			logger.info("click on addMeterReadingsButton");
			checkPresentAndClick(chevronButtonOnAssetMeter);
			logger.info("click on chevronButtonOnAssetMeter");
			checkPresentAndClick(selectReadingValue);
			logger.info("click on selectReadingValue");
			af.waitForElementToBePresent(By.id(enterMeterReadingInFirstAssetMeter), 2);
			af.instantiateComponent(ButtonComponent.class, enterMeterReadingInFirstAssetMeter).type(value);
			logger.info("click on Enter First Asset Meter Value");
			af.waitForElementToBePresent(By.id(enterMeterReadingInSecondAssetMeter), 2);
			af.instantiateComponent(ButtonComponent.class, enterMeterReadingInSecondAssetMeter).type(value);
			logger.info("click on Enter Second Asset Meter Value");
			checkPresentAndClick(chevronButtonOnLocationMeter);
			logger.info("click on chevronButtonOnLocationMeter");
			checkPresentAndClick(selectReadingValue);
			logger.info("click on selectReadingValue");
			af.waitForElementToBePresent(By.id(enterMeterReadingInFirstLocationMeter), 2);
			af.instantiateComponent(ButtonComponent.class, enterMeterReadingInFirstLocationMeter).type(value);
			logger.info("click on Enter First Location Meter Value");
			af.waitForElementToBePresent(By.id(enterMeterReadingInSecondLocationMeter), 2);
			af.instantiateComponent(ButtonComponent.class, enterMeterReadingInSecondLocationMeter).type(value);
			logger.info("click on Enter Second Location Meter Value");
			checkPresentAndClick(saveButton);
			af.waitForElementToNotBePresent(By.id(saveButton), 1000);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Checking Meter value before enter meter reading
	 * 
	 * @return boolean value if meter has value before enter meter reading
	 * @throws Exception
	 */
	public boolean checkMeterValueBeforeReading() throws Exception {
		try {
			af.waitForElementToBePresent(By.id(checkFirstAssetMeterValueSet), 2);
			af.waitForElementToBePresent(By.id(checkSecondAssetMeterValueSet), 2);
			af.waitForElementToBePresent(By.id(checkThirdAssetMeterValueSet), 2);
			af.waitForElementToBePresent(By.id(checkFirstLocationMeterValueSet), 2);
			af.waitForElementToBePresent(By.id(checkSecondLocationMeterValueSet), 2);
			af.waitForElementToBePresent(By.id(checkThirdLocationMeterValueSet), 2);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Checking Meter value after enter meter reading
	 * 
	 * @return boolean value if meter has value after enter meter reading
	 * @throws Exception
	 */
	public boolean checkMeterValueAfterReading(String value) throws Exception {
		try {
			boolean a1, a2, a3, l1, l2, l3;
			String sa2, sa3, sl2, sl3;

			af.waitForElementToBePresent(By.id(checkFirstAssetMeterValueSet), 1000);
			a1 = af.isElementExists(By.id(checkFirstAssetMeterValueSet));

			af.waitForElementToBePresent(By.id(checkSecondAssetMeterValueSet), 2);
			sa2 = af.instantiateComponent(ButtonComponent.class, checkSecondAssetMeterValueSet).getText();
			a2 = sa2.equalsIgnoreCase(value);

			af.waitForElementToBePresent(By.id(checkThirdAssetMeterValueSet), 2);
			sa3 = af.instantiateComponent(ButtonComponent.class, checkThirdAssetMeterValueSet).getText();
			a3 = sa3.equalsIgnoreCase(value);

			af.waitForElementToBePresent(By.id(checkFirstLocationMeterValueSet), 2);
			l1 = af.isElementExists(By.id(checkFirstLocationMeterValueSet));

			af.waitForElementToBePresent(By.id(checkSecondLocationMeterValueSet), 2);
			sl2 = af.instantiateComponent(ButtonComponent.class, checkSecondLocationMeterValueSet).getText();
			l2 = sl2.equalsIgnoreCase(value);

			af.waitForElementToBePresent(By.id(checkThirdLocationMeterValueSet), 2);
			sl3 = af.instantiateComponent(ButtonComponent.class, checkThirdLocationMeterValueSet).getText();
			l3 = sl3.equalsIgnoreCase(value);

			return a1 && a2 && a3 && l1 && l2 && l3;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * @param value of meter reading which had been saved in meter
	 * @return check value of meter reading , date , time as boolean
	 * @throws Exception
	 */
	public boolean checkMeterValueAfterReadingDateAndTime(String value,String timezone) throws Exception {
		String checkFirstAssetMeterValueSet = "rkpqj[0]_textoverflow_value0",
				checkSecondAssetMeterValueSet = "rkpqj[1]_textoverflow_value0",
				checkThirdAssetMeterValueSet = "rkpqj[2]_textoverflow_value0",
				checkFirstLocationMeterValueSet = "z3ygy[0]_fieldValue0",
				checkSecondLocationMeterValueSet = "z3ygy[1]_fieldValue0",
				checkThirdLocationMeterValueSet = "z3ygy[2]_fieldValue0";

		boolean a1, a2, a3, l1, l2, l3;
		String sa2, sa3, sl2, sl3;

		af.waitForElementToBePresent(By.id(checkFirstAssetMeterValueSet), 1000);
		a1 = af.isElementExists(By.id(checkFirstAssetMeterValueSet));

		af.waitForElementToBePresent(By.id(checkSecondAssetMeterValueSet), 2);
		sa2 = af.instantiateComponent(ButtonComponent.class, checkSecondAssetMeterValueSet).getText();
		a2 = sa2.equalsIgnoreCase(value);

		af.waitForElementToBePresent(By.id(checkThirdAssetMeterValueSet), 2);
		sa3 = af.instantiateComponent(ButtonComponent.class, checkThirdAssetMeterValueSet).getText();
		a3 = sa3.equalsIgnoreCase(value);

		af.waitForElementToBePresent(By.id(checkFirstLocationMeterValueSet), 2);
		l1 = af.isElementExists(By.id(checkFirstLocationMeterValueSet));

		af.waitForElementToBePresent(By.id(checkSecondLocationMeterValueSet), 2);
		sl2 = af.instantiateComponent(ButtonComponent.class, checkSecondLocationMeterValueSet).getText();
		l2 = sl2.equalsIgnoreCase(value);

		af.waitForElementToBePresent(By.id(checkThirdLocationMeterValueSet), 2);
		sl3 = af.instantiateComponent(ButtonComponent.class, checkThirdLocationMeterValueSet).getText();
		l3 = sl3.equalsIgnoreCase(value);

		boolean valueSaved = a1 && a2 && a3 && l1 && l2 && l3;
		logger.info("valueSaved : " + valueSaved);

		SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy HH:mm z");
		// "SimpleDateFormat" class initialize with object
		// "formatDate" this class acceptes the format of
		// date and time as ""dd/MM/yyyy" and "HH:mm:ss z""
		// "z" use for print the time zone
		Date date = new Date();
		// initialize "Date" class
		formatDate.setTimeZone(TimeZone.getTimeZone(timezone));
		// converting to IST or format the Date as IST
		String myDate = formatDate.format(date);
		// print formatted date and time
		// Output : 20/11/2020 12:42 IST
		logger.info("myDate : " + myDate);

		// getting the current year
		String[] currentDate = myDate.split("/");
		String[] StrYear = currentDate[2].split(" ");
		String currentYear = StrYear[0];
		logger.info("currentYear : " + currentYear);

		// Getting the current day
		String currentDay = currentDate[0];
		logger.info("currentDay : " + currentDay);
		// Getting the current month
		String month = currentDate[1];
		// Create a month instance
		Month currentMonth = Month.of(Integer.parseInt(month));
		logger.info("currentMonth : " + currentMonth);

		String _24HourTime = StrYear[1];
		logger.info("_24HourTime : " + _24HourTime);
		SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
		SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
		Date _24HourDt = _24HourSDF.parse(_24HourTime);

		String time = _12HourSDF.format(_24HourDt); // 10:15 PM
		logger.info("time : " + time);
		String[] currentTimeSplit = time.split(":");
		String currentHr = ((currentTimeSplit[0]).charAt(0) == '0')
				? (currentTimeSplit[0]).substring(1, (currentTimeSplit[0]).length())
				: currentTimeSplit[0];
		logger.info("currentHr : " + currentHr);
		String currentAMOrPM = (currentTimeSplit[1]).substring((currentTimeSplit[1]).length() - 2);
		logger.info("currentAMOrPM : " + currentAMOrPM.toLowerCase());

		String checkFirstAssetMeterDate = "g2p3q[0]_fieldValue0";
		String checkSecondAssetMeterDate = "g2p3q[1]_fieldValue0";
		String checkThirdAssetMeterDate = "g2p3q[2]_fieldValue0";

		String checkFirstLocationMeterDate = "gx22_[0]_fieldValue0";
		String checkSecondLocationMeterDate = "gx22_[1]_fieldValue0";
		String checkThirdLocationMeterDate = "gx22_[2]_fieldValue0";

		String checkFirstAssetMeterTime = "ra7pd[0]_fieldValue0";
		String checkSecondAssetMeterTime = "ra7pd[1]_fieldValue0";
		String checkThirdAssetMeterTime = "ra7pd[2]_fieldValue0";

		String checkFirstLocationMeterTime = "p8bpk[0]_fieldValue0";
		String checkSecondLocationMeterTime = "p8bpk[1]_fieldValue0";
		String checkThirdLocationMeterTime = "p8bpk[2]_fieldValue0";

		boolean ad1, ad2, ad3, ld1, ld2, ld3, at1, at2, at3, lt1, lt2, lt3;
		String sad1, sad2, sad3, sld1, sld2, sld3, sat1, sat2, sat3, slt1, slt2, slt3;

		af.waitForElementToBePresent(By.id(checkFirstAssetMeterDate), 2);
		sad1 = af.instantiateComponent(ButtonComponent.class, checkFirstAssetMeterDate).getText().toLowerCase();
		ad1 = sad1.contains(currentMonth.toString().toLowerCase()) && sad1.contains(currentDay)
				&& sad1.contains(currentYear.toLowerCase());
		logger.info("ad1 : " + ad1);
		af.waitForElementToBePresent(By.id(checkSecondAssetMeterDate), 2);
		sad2 = af.instantiateComponent(ButtonComponent.class, checkSecondAssetMeterDate).getText().toLowerCase();
		ad2 = sad2.contains(currentMonth.toString().toLowerCase()) && sad2.contains(currentDay)
				&& sad2.contains(currentYear);
		logger.info("ad2 : " + ad2);
		af.waitForElementToBePresent(By.id(checkThirdAssetMeterDate), 2);
		sad3 = af.instantiateComponent(ButtonComponent.class, checkThirdAssetMeterDate).getText().toLowerCase();
		ad3 = sad3.contains(currentMonth.toString().toLowerCase()) && sad3.contains(currentDay)
				&& sad3.contains(currentYear);
		logger.info("ad3 : " + ad3);
		af.waitForElementToBePresent(By.id(checkFirstLocationMeterDate), 2);
		sld1 = af.instantiateComponent(ButtonComponent.class, checkFirstLocationMeterDate).getText().toLowerCase();
		ld1 = sld1.contains(currentMonth.toString().toLowerCase()) && sld1.contains(currentDay)
				&& sld1.contains(currentYear);
		logger.info("ld1 : " + ld1);
		af.waitForElementToBePresent(By.id(checkSecondLocationMeterDate), 2);
		sld2 = af.instantiateComponent(ButtonComponent.class, checkSecondLocationMeterDate).getText().toLowerCase();
		ld2 = sld2.contains(currentMonth.toString().toLowerCase()) && sld2.contains(currentDay)
				&& sld2.contains(currentYear);
		logger.info("ld2 : " + ld2);
		af.waitForElementToBePresent(By.id(checkThirdLocationMeterDate), 2);
		sld3 = af.instantiateComponent(ButtonComponent.class, checkThirdLocationMeterDate).getText().toLowerCase();
		ld3 = sld3.contains(currentMonth.toString().toLowerCase()) && sld3.contains(currentDay)
				&& sld3.contains(currentYear);
		logger.info("ld3 : " + ld3);
		af.waitForElementToBePresent(By.id(checkFirstAssetMeterTime), 2);
		sat1 = af.instantiateComponent(ButtonComponent.class, checkFirstAssetMeterTime).getText().toLowerCase();
		logger.info("sat1 : " + sat1);
		String[] sat1split = sat1.split(":", 2);
		String sat1Hr = sat1split[0];
		String sat1AMOrPM = (sat1split[1]).substring((sat1split[1]).length() - 2);
		at1 = sat1AMOrPM.equalsIgnoreCase(currentAMOrPM.toLowerCase()) && sat1Hr.equalsIgnoreCase(currentHr);
		logger.info("at1 : " + at1);

		af.waitForElementToBePresent(By.id(checkSecondAssetMeterTime), 2);
		sat2 = af.instantiateComponent(ButtonComponent.class, checkSecondAssetMeterTime).getText().toLowerCase();
		String[] sat2split = sat2.split(":", 2);
		String sat2Hr = sat2split[0];
		String sat2AMOrPM = (sat2split[1]).substring((sat2split[1]).length() - 2);
		at2 = sat2AMOrPM.equalsIgnoreCase(currentAMOrPM.toLowerCase()) && sat2Hr.equalsIgnoreCase(currentHr);
		logger.info("at2 : " + at2);
		af.waitForElementToBePresent(By.id(checkThirdAssetMeterTime), 2);
		sat3 = af.instantiateComponent(ButtonComponent.class, checkThirdAssetMeterTime).getText().toLowerCase();
		String[] sat3split = sat3.split(":", 2);
		String sat3Hr = sat3split[0];
		String sat3AMOrPM = (sat3split[1]).substring((sat3split[1]).length() - 2);
		at3 = sat3AMOrPM.equalsIgnoreCase(currentAMOrPM.toLowerCase()) && sat3Hr.equalsIgnoreCase(currentHr);
		logger.info("at3 : " + at3);
		af.waitForElementToBePresent(By.id(checkFirstLocationMeterTime), 2);
		slt1 = af.instantiateComponent(ButtonComponent.class, checkFirstLocationMeterTime).getText().toLowerCase();
		String[] slt1split = slt1.split(":", 2);
		String slt1Hr = slt1split[0];
		String slt1AMOrPM = (slt1split[1]).substring((slt1split[1]).length() - 2);
		lt1 = slt1AMOrPM.equalsIgnoreCase(currentAMOrPM.toLowerCase()) && slt1Hr.equalsIgnoreCase(currentHr);
		logger.info("lt1 : " + lt1);

		af.waitForElementToBePresent(By.id(checkSecondLocationMeterTime), 2);
		slt2 = af.instantiateComponent(ButtonComponent.class, checkSecondLocationMeterTime).getText().toLowerCase();
		String[] slt2split = slt2.split(":", 2);
		String slt2Hr = slt2split[0];
		String slt2AMOrPM = (slt2split[1]).substring((slt2split[1]).length() - 2);
		lt2 = slt2AMOrPM.equalsIgnoreCase(currentAMOrPM.toLowerCase()) && slt2Hr.equalsIgnoreCase(currentHr);
		logger.info("lt2 : " + lt2);
		af.waitForElementToBePresent(By.id(checkThirdLocationMeterTime), 2);
		slt3 = af.instantiateComponent(ButtonComponent.class, checkThirdLocationMeterTime).getText().toLowerCase();
		String[] slt3split = slt3.split(":", 2);
		String slt3Hr = slt3split[0];
		String slt3AMOrPM = (slt3split[1]).substring((slt3split[1]).length() - 2);
		lt3 = slt3AMOrPM.equalsIgnoreCase(currentAMOrPM.toLowerCase()) && slt3Hr.equalsIgnoreCase(currentHr);
		logger.info("lt2 : " + lt2);

		boolean valueStatusDateTime = ad1 && ad2 && ad3 && ld1 && ld2 && ld3 && at1 && at2 && at3 && lt1 && lt2 && lt3;
		logger.info("valueStatusDateTime : " + valueStatusDateTime);
		return valueSaved && valueStatusDateTime;
	}

	// Method for clicking on enterAssetMeterReadingValues button
	public boolean enterAssetMeterReadingValues(String value) throws Exception {
		try {
			af.waitForElementToBePresent(By.id(enterMeterReadingInFirstAssetMeter), 2);
			af.instantiateComponent(ButtonComponent.class, enterMeterReadingInFirstAssetMeter).type(value);
			af.waitForElementToBePresent(By.id(enterMeterReadingInSecondAssetMeter), 2);
			af.instantiateComponent(ButtonComponent.class, enterMeterReadingInSecondAssetMeter).type(value);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	// Method for clicking on enterAssetMeterReadingValues button
	public boolean enterLocationMeterReadingValues(String value) throws Exception {
		try {
			af.waitForElementToBePresent(By.id(enterMeterReadingInFirstLocationMeter), 2);
			af.instantiateComponent(ButtonComponent.class, enterMeterReadingInFirstLocationMeter).type(value);
			af.waitForElementToBePresent(By.id(enterMeterReadingInSecondLocationMeter), 2);
			af.instantiateComponent(ButtonComponent.class, enterMeterReadingInSecondLocationMeter).type(value);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getMeterHeader() throws Exception {
		return af.instantiateComponent(LabelComponent.class, meterHeaderLabel).getValue();

	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public String VerifyMeterValue() throws Exception {
		af.waitForElementToBePresent(By.id(getMeterValue), af.DEFAULT_TIMEOUT_MS * 3);
		return af.instantiateComponent(LabelComponent.class, getMeterValue).getValue().trim();
	}

	// click on Meter Button from WO detail page
	public void clickMetersButtonOnWoDetail() throws Exception {
		checkPresentAndClick(meterIconOnWODetail);
	}

	// verify that Meter Icon is present on WO detail page
	public boolean verifyMeterIconOnWODetailPage() throws Exception {
		af.waitForElementToBePresent(By.id(meterIconOnWODetail), af.DEFAULT_TIMEOUT_MS * 10);
		return af.isElementExists(By.id(meterIconOnWODetail));
	}

	// Method for clicking on cancel button from WO detail of Meters page drawer
	public void cancelMetersDrawerFromWoDetail() throws Exception {
		checkPresentAndClick(cancelMetersDrawerOnWoDetail);
	}

	/**
	 * Add ContinuousMeter reading from WO Details for meter attached to Asset
	 * 
	 * @param value meter reading value
	 * @return boolean base on value enter
	 * @throws Exception
	 */
	public boolean addContinuousMeterReadingsFromWoDetailForAsset(String value, int numAssetMeter) throws Exception {
		String addMeterReadingsButton = "wk7dq_j79ab", enterMeterReadingInFirstAssetMeter = "kz_2d[0]",
				enterMeterReadingInSecondAssetMeter = "kz_2d[1]", saveButton = "v69a__ere89";
		String locatorAssetMeter = (numAssetMeter == 1) ? enterMeterReadingInFirstAssetMeter
				: enterMeterReadingInSecondAssetMeter;
		try {
			checkPresentAndClick(addMeterReadingsButton);
			af.waitForElementToBePresent(By.id(locatorAssetMeter), 2);
			af.instantiateComponent(ButtonComponent.class, locatorAssetMeter).type(value);
			logger.info("click on Enter Asset Meter Value");
			checkPresentAndClick(saveButton);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Add ContinuousMeter reading from WO Details for meter attached to location
	 * 
	 * @param value meter reading value
	 * @return boolean base on value enter
	 * @throws Exception
	 */
	public boolean addContinuousMeterReadingsFromWoDetailForLocation(String value, int numLocationMeter)
			throws Exception {
		String addMeterReadingsButton = "wk7dq_j79ab", enterMeterReadingInFirstLocationMeter = "a8pb5[0]",
				enterMeterReadingInSecondLocationMeter = "a8pb5[1]", saveButton = "v69a__ere89";
		String locatorLocationMeter = (numLocationMeter == 1) ? enterMeterReadingInFirstLocationMeter
				: enterMeterReadingInSecondLocationMeter;
		try {
			checkPresentAndClick(addMeterReadingsButton);
			af.waitForElementToBePresent(By.id(locatorLocationMeter), 2);
			af.instantiateComponent(ButtonComponent.class, locatorLocationMeter).type(value);
			logger.info("click on Enter Location Meter Value");
			checkPresentAndClick(saveButton);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Display error message and close
	 * 
	 * @return boolean whether successful
	 * @throws Exception
	 */
	public boolean showErrorPageAndClose(String message) throws Exception {
		try {
			String errorMessage = af.waitForElementToBePresent(By.xpath(errorMessageLocator), af.DEFAULT_TIMEOUT_MS * 10).getText().toLowerCase();
			logger.info("Actual errorMessage:" + errorMessage + " Expected:" + message);
			boolean status = errorMessage.contains(message.toLowerCase());
			logger.info("status :" + status);
			boolean close = checkPresentAndClick(closeSystemMsg);
			logger.info(" close:" + close);
			return status && close;
		} catch (Exception e) {
			logger.info(" We are in catch loop");
			return false;
		}
	}

	/**
	 * Cancel Update Meter Reading screen or page
	 * 
	 * @return boolean whether successful
	 * @throws Exception
	 */
	public boolean cancelUpdateMeterReadingScreen() throws Exception {
		return checkPresentAndClick(cancelUpdateMeterReadingViewLocator);
	}

	/**
	 * Discard Update Meter Reading page
	 * 
	 * @return boolean whether successful
	 * @throws Exception
	 */
	public boolean disCardUpdateMeterReading() throws Exception {
		return checkPresentAndClick(disCardUpdateMeterReadingLocator);
	}

	/**
	 * Check Present locator and click on same locator
	 * 
	 * @return boolean whether successful
	 * @throws Exception
	 */
	public boolean checkPresentAndClick(String locator) throws Exception {
		try {
			af.waitForElementToBePresent(By.id(locator));
			af.instantiateComponent(ButtonComponent.class, locator).click();
			return true;
		} catch (Exception e) {
			logger.info(locator + " > Locator is not present and click failed");
			return false;
		}
	}
	
	// Generated by WCA for GP
	/**
	 * @param value of meter reading which had been saved in meter from wo details page
	 * @return check value of meter reading as boolean
	 * @throws Exception
	 * 
	 */
	public boolean VerifyMeterValuesFromWODetailsPage(String value) throws Exception {
		String  checkSecondAssetMeterValueSet = "rkpqj[1]_textoverflow_value0",
				checkThirdAssetMeterValueSet = "rkpqj[2]_textoverflow_value0",
				checkSecondLocationMeterValueSet = "z3ygy[1]_fieldValue0",
				checkThirdLocationMeterValueSet = "z3ygy[2]_fieldValue0";
		try {
			boolean a1, a2, a3, l1;
			String sa2, sa3;

			af.waitForElementToBePresent(By.id(checkSecondAssetMeterValueSet), 1000);
			a1 = af.isElementExists(By.id(checkSecondAssetMeterValueSet));

			af.waitForElementToBePresent(By.id(checkThirdAssetMeterValueSet), 2);
			sa2 = af.instantiateComponent(ButtonComponent.class, checkThirdAssetMeterValueSet).getText();
			a2 = sa2.equalsIgnoreCase(value);

			af.waitForElementToBePresent(By.id(checkSecondLocationMeterValueSet), 2);
			sa3 = af.instantiateComponent(ButtonComponent.class, checkSecondLocationMeterValueSet).getText();
			a3 = sa3.equalsIgnoreCase(value);

			af.waitForElementToBePresent(By.id(checkThirdLocationMeterValueSet), 2);
			l1 = af.isElementExists(By.id(checkThirdLocationMeterValueSet));

			return a1 && a2 && a3 && l1;
		} catch (Exception e) {
			return false;
		}
	  }
	
          // Generated by WCA for GP

	      // Method for clicking on 3 dots present on slider 
		  public void clickThreeDotsOnMeterSlider() throws Exception {
			checkPresentAndClick(threeDotsMeters);
		    }
	
	       // Generated by WCA for GP
			/**
			 *
			 * @return
			 * @throws Exception
			 */
			public String verifyPreviousReadings() throws Exception {
				af.waitForElementToBePresent(By.id(previousReadings), af.DEFAULT_TIMEOUT_MS * 3);
				return af.instantiateComponent(LabelComponent.class, previousReadings).getValue();
			}
			
			
		    // Generated by WCA for GP

			/**
			 * Add ContinuousMeter reading from WO Details for meter attached to Asset
			 *
			 * @param value meter reading value
			 * @return boolean base on value enter
			 * @throws Exception
			 */
			public boolean addContinuousMeterReadingsFromWoDetailForAssets(String value) throws Exception {
				    String addMeterReadingsButton = "mw_y2_maxlib_pbd42" , enterMeterReadingAssetMeterInFirst= "//input[contains(@id,'maxlib_vye9_')]",
	                saveButton = "maxlib_byx5e_maxlib_yrgxe";
					try{	
						checkPresentAndClick(addMeterReadingsButton);
						af.waitForElementToBePresent(By.xpath(enterMeterReadingAssetMeterInFirst),5);
						//Need to use smart input which currently is not present . Will work on this in the next sprint along with fixing meter scripts
						af.waitForElementToBePresent(By.xpath(enterMeterReadingAssetMeterInFirst), af.DEFAULT_TIMEOUT_MS * 5).sendKeys(value);
						checkPresentAndClick(saveButton);
						return true;
			     } catch (Exception e) {
				return false;
			    }		
	         }
			
		       // Generated by WCA for GP
			
			/**
			 * Checking Meter value after enter meter reading
			 * 
			 * @return boolean value if meter has value after enter meter reading
			 * @throws Exception
			 */
			public boolean checkMeterValue(String value) throws Exception {
				
				   String checkFirstAssetMeterValueSet="maxlib_p72jx[0]_textoverflow_value0";
				try {
					boolean a1;

					af.waitForElementToBePresent(By.id(checkFirstAssetMeterValueSet), 1000);
					a1 = af.isElementExists(By.id(checkFirstAssetMeterValueSet));

					return a1;
				} catch (Exception e) {
					return false;
				}
			}

}
