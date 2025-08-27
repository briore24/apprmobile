package com.ibm.maximo.technician.testcases.calibration.page;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.By.ByXPath;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.maximo.automation.mobile.AbstractAutomationFramework;
import com.ibm.maximo.technician.testcases.calibration.support.constants.DatasheetConstants;

public class CalibrationPointsRepeatablePage extends CalibrationBaseTest {

	private final static Logger logger = LoggerFactory.getLogger(CalibrationPointsRepeatablePage.class);

	// Constructor to initialize AbstractAutomationFramework
	public CalibrationPointsRepeatablePage(AbstractAutomationFramework af) {
		super(af);
	}
	
	/* ------------------------------------------------------------------ */
	/*                                                                    */
	/* Locators                                                           */
	/*                                                                    */
	/* A locator is a way to identify elements on a page. It is the       */
	/* argument passed to the Finding element methods.                    */
	/*                                                                    */
	/* ------------------------------------------------------------------ */
	
	private String pageTitle = "blcqh-pageheader_title";
	private String saveBtn = "blcqh-pageheader_buttongroup_bj44";
	private String goBackBtn = "blcqh-pageheader_icon";
	private String toleranceSummaryBtn = "(//*[@datacomponentoriginalid='wwqgk']//button[@data-testid='Button'])[%d]";
	private String tolFrom = "(//*[@id='rdpm5_fieldValue0'])[%d]";
	private String tolTo = "(//*[@id='bm7_e_fieldValue0'])[%d]";
	private String tolError = "(//*[@id='bjj43_fieldValue0'])[%d]";
	private String assetError = "(//*[@id='xz98r_fieldValue0'])[%d]";
	private String processError = "(//*[@id='v3ke8_fieldValue0'])[%d]";

	// Condition As Left
	private String asLeftNominalInput = "(//input[contains(@id,'iutxoxui')])[%d]";
	private String asLeftDesiredOutput = "(//input[contains(@id,'qy5jmviy')])[%d]";
	
	// Standard deviation
	private String standardDeviationLabel = "(//p[@id='dd4px'])";
	private String inputStandardDeviationLimit = "(//p[@id='m8yw5_fieldValue0'])";
	private String inputStandardDeviation = "(//p[@id='gpp94_fieldValue0'])";
	private String inputStandardDeviationError = "(//p[@id='mkjy5_fieldValue0'])";
	private String outputStandardDeviationLimit = "(//p[@id='g37_g_fieldValue0'])";
	private String outputStandardDeviation = "(//p[@id='e7d9q_fieldValue0'])";
	private String outputStandardDeviationError = "(//p[@id='xae7p_fieldValue0'])";
	
	/* ------------------------------------------------------------------ */
	/*                                                                    */
	/* Supporting methods                                                 */
	/*                                                                    */
	/* ------------------------------------------------------------------ */

	private WebElement getOrWaitForElement(By by) throws Exception {
		try {
			return this.getAf().getWebDriver().findElement(by);
		} catch (Exception e) {
			return this.getAf().waitForElementToBePresent(by);
		}
	}
	
	public String getPageTitle() throws Exception {
		return this.getAf().waitForElementToBePresent(By.id(pageTitle)).getText().trim();
	}

	public void enterValuesAsLeft(String path, String testDataType, String repeatablePointValue) throws Exception {

		String content = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
		JSONObject jsonObject = new JSONObject(content);
		JSONArray jsonTestData = jsonObject.getJSONArray(testDataType);
		int repeat = Integer.parseInt(repeatablePointValue);

		// Apply validation set test case
		for (int i = 1; i <= jsonTestData.length(); i++) {

			JSONObject entry = jsonTestData.getJSONObject(i - 1);

			String inputLabel = entry.getString("Input Label");

			// Find section label
			String sectionLabel = this.getRepeatableAnalogCalPointSectionLabel(i);
			logger.info("Opening section with description '" + sectionLabel + "'");
			assertTrue(
				sectionLabel.contains(inputLabel),
				String.format(
					"Should contain label '%s' in section description '%s'",
					inputLabel,
					sectionLabel
				)
			);

			// Open section
			expandRepeatableCalibrationPointSection(i);
			
			if(entry.has(DatasheetConstants.REPEATABLE_VALUES)) {
				JSONArray repeatableValuesList = entry.getJSONArray(DatasheetConstants.REPEATABLE_VALUES);
				
				for (int j = 1; j <= repeatableValuesList.length(); j++) {
					JSONObject values = repeatableValuesList.getJSONObject(j-1);

					String inputValue = values.getString(DatasheetConstants.INPUT_VALUE);
					String outputValue = values.getString(DatasheetConstants.OUTPUT_VALUE);
					int offset = (repeat * (i - 1));

					enterValue(asLeftNominalInput, offset + j, inputValue);
					enterValue(asLeftDesiredOutput, offset + j, outputValue);
					
				}
			} else {
				String inputValue = entry.getString(DatasheetConstants.INPUT_VALUE);
				String outputValue = entry.getString(DatasheetConstants.OUTPUT_VALUE);
				
				// Enter nominal input and desired output for each row in section
				for (int j = 1; j <= repeat; j++) {

					/**
					 * @var {int} offset - Determines offset to access tolerance values.
					 * 
					 * Calibration Repeatable page (./calibration/calibration-points-repeatable.xml) works 
					 * slightly different than their siblings As Left (./calibration/calibrationasleftpoints.xml)
					 * and As Found (./calibration/calibrationpoints.xml) and therefore it renders
					 * the UI elements differently. In this case, due to framework limitations,
					 * the result UI will render all labels with the same id property.
					 * 
					 * For example:
					 * ```html
					 * <section id="section-1">
					 *   <input id="input_rdpm5" value="15.00"/> [1]
					 * 	 <input id="input_rdpm5" value="14.25"/> [2]
					 * 	 <input id="input_rdpm5" value="13.37"/> [3]
					 *   <input id="input_rdpm5" value="12.32"/> [4]
					 * <section>
					 * ...
					 * <section id="section-2">
					 *   <input id="input_rdpm5" value="12.00"/> [5]
					 *   ...
					 * ```
					 * 
					 *   If we want to access a particular tolerance value, i.e "12.00",
					 * we need to take all inputs in the page into account instead
					 * of separating them by sections. Hence, we need to search 
					 * for the position `5` instead of "position 1 of section 2".
					 *   To preserve the function method arguments consistent with
					 * the current design (CalibrationBaseTest.java), we introduced
					 * this `offset` variable that will take the section position `i`
					 * and add 4 spaces, one for each tolerance bound present in the
					 * section.
					 *   Back to the previous example, if I want to access the
					 * tolerance value 1 in section 2, i.e.: "12.00", I need to pass
					 * the section position (1) and an offset will be calculated to
					 * access the correct label.
					 * ```
					 */
					int offset = (repeat * (i - 1));

					enterValue(asLeftNominalInput, offset + j, inputValue);
					enterValue(asLeftDesiredOutput, offset + j, outputValue);
				}
			}

			
		}
	}

	public void save() throws Exception {
		logger.info("Save values");
		this.getAf().waitForElementToBePresent(By.id(saveBtn)).click();
	}

	public void goBack() throws Exception {
		logger.info("Click to go back");
		this.getAf().waitForElementToBePresent(By.id(goBackBtn)).click();
	}
	
	public void validateTolerances(String path, String testDataType) throws Exception {
		logger.info("Validate tolerances");

		this.expandAllToleranceSummaries();
		
		JSONArray jsonTestData = readData(path, testDataType);
		int count = jsonTestData.length();

		for (int i = 1; i <= count; i++) {
			JSONObject entry = jsonTestData.getJSONObject(i - 1);
			this.assertStandardDeviation(i, entry);
			this.assertTolerance(i, entry); // In XPath, index always start at 1
		}
	}
	
	public void assertTolerance(int i, JSONObject entry) throws Exception {
		logger.info(String.format("Assert tolerances for calibration point '%s'", entry.getString("Input Label")));

		/**
		 * @var {int} offset - Determines offset to access tolerance values.
		 * 
		 * Calibration Repeatable page (./calibration/calibration-points-repeatable.xml) works 
		 * slightly different than their siblings As Left (./calibration/calibrationasleftpoints.xml)
		 * and As Found (./calibration/calibrationpoints.xml) and therefore it renders
		 * the UI elements differently. In this case, due to framework limitations,
		 * the result UI will render all labels with the same id property.
		 * 
		 * For example:
		 * ```html
		 * <section id="section-1">
		 *   <p id="rdpm5_fieldValue0">15.00</p> [1]
		 * 	 <p id="rdpm5_fieldValue0">14.25</p> [2]
		 * 	 <p id="rdpm5_fieldValue0">13.37</p> [3]
		 *   <p id="rdpm5_fieldValue0">12.32</p> [4]
		 * <section>
		 * ...
		 * <section id="section-2">
		 *   <p id="rdpm5_fieldValue0">12.00</p> [5]
		 *   ...
		 * ```
		 * 
		 *   If we want to access a particular tolerance value, i.e "12.00",
		 * we need to take all labels in the page into account instead
		 * of separating them by sections. Hence, if we wish to access
		 * the tolerance value "12.00" we need to search for the 
		 * position `5` instead of "position 1 of section 2".
		 *   To preserve the function method arguments consistent with
		 * the current design (CalibrationBaseTest.java), we introduced
		 * this `offset` variable that will take the section position `i`
		 * and add 4 spaces, one for each tolerance bound present in the
		 * section.
		 *   Going back to the previous example, if I want to access the
		 * tolerance value 1 in section 2, i.e.: "12.00", I just need to
		 * pass the section position (1) and an offset will be calculated
		 * to access the correct label.
		 * ```
		 */
		int offset = (4 * (i - 1));

		logger.info("Assert tolerance bounds (From / To / Error)");
		for (int j = 1; j < 5; j++) {

			// Expected tolerance bounds
			String expectedLowerBound = entry.getString(String.format("T%d From", j));
			String expectedUpperBound = entry.getString(String.format("T%d To", j));
			String expectedError = entry.getString(String.format("T%d Error", j));

			// Actual tolerance bounds
			String actualLowerBound = getText(tolFrom, offset + j);
			String actualUpperBound = getText(tolTo, offset + j);
			String actualError = getText(tolError, offset + j);

			// Assert
			assertEquals(actualLowerBound, expectedLowerBound, String.format("Tolerance %d lower bound should be %s", j, expectedLowerBound));
			assertEquals(actualUpperBound, expectedUpperBound, String.format("Tolerance %d upper bound should be %s", j, expectedUpperBound));
			assertEquals(actualError, expectedError, String.format("Tolerance %d error should be %s", j, expectedError));	
		}

		// Expected process and asset errors
		logger.info("Assert asset and process errors");
		String expectedAssetError = entry.getString("Asset Error");
		String expectedProcessError = entry.getString("Process Error");

		// Actual process and asset errors
		String actualAssetError = getText(assetError, i);
		String actualProcessError = getText(processError, i);

		// Assert
		assertEquals(actualAssetError, expectedAssetError, "Asset error should be calculated");
		assertEquals(actualProcessError, expectedProcessError, "Process error should be calculated");
	}
	
	/**
	 * The repeatable calibration page works slightly different than
	 * their siblings when it comes to expanding, collapsing the tolerance
	 * sections. In this case, the page has a single state variable that
	 * controls the visibility of all sections, which means that if I 
	 * click to expand a particular section, all other sections will be
	 * expanded at once.
	 *   So, we use this method to expand all tolerance summary sections
	 * and proceed with asserting each value.
	 * @throws Exception
	 */
	public void expandAllToleranceSummaries() throws Exception {
		logger.info("Expand all tolerance summaries");
		this.toggleToleranceSummary(1);
	}

	public void toggleToleranceSummary(int sectionIndex) throws Exception {

		AbstractAutomationFramework af = this.getAf();
		ByXPath element = (ByXPath) By.xpath(String.format(toleranceSummaryBtn, sectionIndex));

		af.waitForElementToBePresent(element).click();	
	}
	
	private String getText(String locator, int i) throws Exception {
		AbstractAutomationFramework af = this.getAf();
		ByXPath element = (ByXPath) By.xpath(String.format(locator, i)); // In XPath, index always start at 1
		return getOrWaitForElement(element).getText().trim();
	}

	private void enterValue(String locator, int index, String inputValue) throws Exception {
		logger.info("Enter value '" + inputValue + "' for input '" + index + "'");

		AbstractAutomationFramework af = this.getAf();
		ByXPath xpath = (ByXPath) By.xpath(String.format(locator, index)); // In XPath, index always start at 1
		WebElement inputElement = getOrWaitForElement(xpath);

		// Clear the input first if not empty
		inputElement.clear();

		// Entering values
		inputElement.sendKeys(inputValue);
	}
	
	public void assertStandardDeviation(int i, JSONObject entry) throws Exception {
		logger.info(String.format("Assert standard deviation for calibration point '%s'", entry.getString("Input Label")));
		
		if(entry.has("Standard Deviation") && !entry.isNull("Standard Deviation")) {
			String expectedStandardDeviationLabel = entry.getString("Standard Deviation");
			String actualStandardDeviationLabel = getText(standardDeviationLabel);
			
			String expectedInputStandardDeviationLimit = entry.getString("Input Standard Deviation Limit");
			String actualInputStandardDeviationLimit = getText(inputStandardDeviationLimit);
			
			String expectedInputStandardDeviation = entry.getString("Input Standard Deviation");
			String actualInputStandardDeviation = getText(inputStandardDeviation);
			
			String expectedInputStandardDeviationError = entry.getString("Input Standard Deviation Error");
			String actualInputStandardDeviationError = getText(inputStandardDeviationError);
			
			String expectedOutputStandardDeviationLimit = entry.getString("Output Standard Deviation Limit");
			String actualOutputStandardDeviationLimit = getText(outputStandardDeviationLimit);
			
			String expectedOutputStandardDeviation = entry.getString("Output Standard Deviation");
			String actualOutputStandardDeviation = getText(outputStandardDeviation);
			
			String expectedOutputStandardDeviationError = entry.getString("Output Standard Deviation Error");
			String actualOutputStandardDeviationError = getText(outputStandardDeviationError);
			
			// Assert
			assertEquals(actualStandardDeviationLabel, expectedStandardDeviationLabel, "Standard deviation label must match");
			assertEquals(expectedInputStandardDeviationLimit, actualInputStandardDeviationLimit, "Input Standard deviation limit must match");
			assertEquals(expectedInputStandardDeviation, actualInputStandardDeviation, "Input Standard deviation must match");
			assertEquals(expectedInputStandardDeviationError, actualInputStandardDeviationError, "Input Standard deviation error must match");
			assertEquals(expectedOutputStandardDeviationLimit, actualOutputStandardDeviationLimit, "Output Standard deviation limit must match");
			assertEquals(expectedOutputStandardDeviation, actualOutputStandardDeviation, "Output Standard deviation must match");
			assertEquals(expectedOutputStandardDeviationError, actualOutputStandardDeviationError, "Output Standard deviation error must match");
		}
		
	}
	
	public String getText(String locator) throws Exception {
		ByXPath element = (ByXPath) By.xpath(locator); 
		return getOrWaitForElement(element).getText().trim();
	}
}

