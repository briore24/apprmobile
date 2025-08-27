package com.ibm.maximo.technician.testcases.calibration.page;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Reporter;

import com.ibm.maximo.automation.mobile.AbstractAutomationFramework;
import com.ibm.maximo.automation.mobile.MobileAutomationFramework;
import com.ibm.maximo.components.ToastComponent;
import com.ibm.maximo.technician.testcases.calibration.support.constants.DatasheetConstants;
import com.ibm.maximo.technician.testcases.calibration.support.constants.OutputConstants;

public class CalibrationBaseTest {

	private static AbstractAutomationFramework af;
	private static MobileAutomationFramework maf;
	JavascriptExecutor jsExec;
	private final static Logger logger = LoggerFactory.getLogger(CalibrationBaseTest.class);

	// Constructor to initialize AbstractAutomationFramework
	public CalibrationBaseTest(AbstractAutomationFramework af) {
		this.setAf(af);
		this.jsExec = (JavascriptExecutor) af.getDriver();
		maf = (MobileAutomationFramework) af;
	}

	private String repeatablePageCalPointNominalInput = "(.//div[@id='yye87_items_%r_DetailsLongPress']//input[contains(@id,'iutxoxui')])[%s]";
	private String repeatablePageCalPointDesiredOutput = "(.//div[@id='yye87_items_%r_DetailsLongPress']//input[contains(@id,'qy5jmviy')])[%s]";

	private String repeatableAnalogCalPointSectionLabel = ".//div[@id='yye87_items_%r_LongPress']//p[@id='or3sdcyx']";
	private String repeatableAnalogCalPointSectionBtn = "(.//div[contains(@id,'childCaretContainer_touch')]/*[contains(@id,'childCaret_touch')])[%s]";

	private String repeatableAnalogCalPointNominalInput = "(.//div[@id='yye87_items_%r_DetailsLongPress']//input[contains(@id,'blx1wbzg')])[%s]";
	private String repeatableAnalogCalPointDesiredOutput = "(.//div[@id='yye87_items_%r_DetailsLongPress']//input[contains(@id,'qzywpw8b')])[%s]";
	
	private String repeatableCalPointNonEmpty = ".//div[contains(@id, 'yye87_items')]//input[@value != '']";
	
	protected String inputOutOfRangeMessage = "//div[contains(@id, 'error-msg')]";
	
	private String saveBtnXPath = ".//div[contains(@id, 'pageheader_innerBorderLayout_slot')]//button[.//*[@aria-label='carbon:save']]";
	private String backBtnXPath = ".//div[contains(@id, 'pageheader_innerBorderLayout_slot')]//button[.//*[@aria-label='carbon:chevron--left']]";
	
	protected String readOnlyInlineNotification = ".//div[contains(@id, '%s')]//div[contains(@class, 'cds--inline-notification__title')]";
	private String repeatableNoAdjInlineNotification = "w6q76";
	
	public static String readOnlyNotificationMsg = "The As Found values are copied to As Left, and all fields are read-only.";
	
	private boolean isDiscreteType = false;

	private WebElement getOrWaitForElement(By by) throws Exception {
		try {
			return af.getWebDriver().findElement(by);
		} catch (Exception e) {
			return af.waitForElementToBePresent(by);
		}
	}
	
	public JSONArray readData(String path, String testDataType) throws Exception {
		String content = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
		JSONObject jsonObj = new JSONObject(content);
		JSONArray jsonTestData = null;
		jsonTestData = jsonObj.getJSONArray(testDataType);
		return jsonTestData;
	}

	public String getCalibrationPointSectionLabel(int sectionIndex, String type) throws Exception {
		return getOrWaitForElement(
				By.xpath(OutputConstants.getXPath(type, "SectionLabel", sectionIndex, sectionIndex).replace("%s",
						String.valueOf(sectionIndex))))
				.getText().trim();

	}

	public void expandCalibrationPointSection(int sectionIndex, String type) throws Exception {
		logger.info("Expand Calibration Point Accordion");
		if (getOrWaitForElement(By.xpath(OutputConstants
				.getXPath(type, "SectionBtn", sectionIndex, sectionIndex).replace("%s", String.valueOf(sectionIndex))))
				.getAttribute("description").contains("down")) {
			getOrWaitForElement(
					By.xpath(OutputConstants.getXPath(type, "SectionBtn", sectionIndex, sectionIndex).replace("%s",
							String.valueOf(sectionIndex))))
					.click();
		}
	}

	public void enterNominalInput(int sectionIndex, String inputValue, String type) throws Exception {
		WebElement nominalInputEle = getOrWaitForElement(
				By.xpath(OutputConstants.getXPath(type, "NominalInput", sectionIndex, sectionIndex).replace("%s",
						String.valueOf(sectionIndex))));
		nominalInputEle.clear();
		nominalInputEle.sendKeys(inputValue);
		//nominalInputEle.sendKeys(Keys.TAB);
		maf.hideKeyboard();
	}
	
	public void clearNominalInput(int sectionIndex, String type) throws Exception {
		WebElement nominalInputEle = getOrWaitForElement(
				By.xpath(OutputConstants.getXPath(type, "NominalInput", sectionIndex, sectionIndex).replace("%s",
						String.valueOf(sectionIndex))));
		nominalInputEle.sendKeys(Keys.BACK_SPACE);
		nominalInputEle.clear();
	}
	
	public String getNominalInput(int sectionIndex, String type) throws Exception {
		WebElement nominalInputEle = getOrWaitForElement(
				By.xpath(OutputConstants.getXPath(type, "NominalInput", sectionIndex, sectionIndex).replace("%s",
						String.valueOf(sectionIndex))));
		
		return nominalInputEle.getAttribute("value").toString().trim();
	}
	
	public boolean getNominalInputReadOnlyState(int sectionIndex, String type) throws Exception {
		WebElement nominalInputEle = getOrWaitForElement(
				By.xpath(OutputConstants.getXPath(type, "NominalInput", sectionIndex, sectionIndex).replace("%s",
						String.valueOf(sectionIndex))));
		
		return nominalInputEle.getAttribute("readonly") != null;
	}
	
	public String getDesiredOutput(int sectionIndex, String type) throws Exception {
		WebElement desiredOutputEle = getOrWaitForElement(
				By.xpath(OutputConstants.getXPath(type, "DesiredOutput", sectionIndex, sectionIndex).replace("%s",
						String.valueOf(sectionIndex))));
		
		return desiredOutputEle.getAttribute("value").toString().trim();
	}
	
	public void clearDesiredOutput(int sectionIndex, String type) throws Exception {
		WebElement desiredOutputEle = getOrWaitForElement(
				By.xpath(OutputConstants.getXPath(type, "DesiredOutput", sectionIndex, sectionIndex).replace("%s",
						String.valueOf(sectionIndex))));
		
		desiredOutputEle.sendKeys(Keys.BACK_SPACE);
		desiredOutputEle.clear();
	}
	
	public boolean getDesiredOutputReadOnlyState(int sectionIndex, String type) throws Exception {
		WebElement desiredOutputEle = getOrWaitForElement(
				By.xpath(OutputConstants.getXPath(type, "DesiredOutput", sectionIndex, sectionIndex).replace("%s",
						String.valueOf(sectionIndex))));
		
		return desiredOutputEle.getAttribute("readonly") != null;
	}

	public void enterDesiredOutput(int sectionIndex, String outputValue, String type) throws Exception {
		if(this.isDiscreteType)
			return;
		
		WebElement desiredOutputEle = getOrWaitForElement(
				By.xpath(OutputConstants.getXPath(type, "DesiredOutput", sectionIndex, sectionIndex).replace("%s",
						String.valueOf(sectionIndex))));
		desiredOutputEle.clear();
		desiredOutputEle.sendKeys(outputValue);
		//desiredOutputEle.sendKeys(Keys.TAB);
		maf.hideKeyboard();
	}

	public void enterRepeatableNominalInput(int sectionIndex, int repeatableIndex, String inputValue) throws Exception {
		String xpathLocator = repeatableAnalogCalPointNominalInput.replace("%r", String.valueOf(sectionIndex));
		xpathLocator = xpathLocator.replace("%s", String.valueOf(repeatableIndex));
		logger.info("Nominal Input  [" + sectionIndex + "][" + repeatableIndex + "] = " + inputValue);
		WebElement repeatableNominalInputEle = getOrWaitForElement(By.xpath(xpathLocator));
		repeatableNominalInputEle.clear();
		repeatableNominalInputEle.sendKeys(inputValue);
		//repeatableNominalInputEle.sendKeys(Keys.TAB);
		maf.hideKeyboard();
	}

	public void enterRepeatableDesiredOutput(int sectionIndex, int repeatableIndex, String outputValue)
			throws Exception {
		String xpathLocator = repeatableAnalogCalPointDesiredOutput.replace("%r", String.valueOf(sectionIndex));
		xpathLocator = xpathLocator.replace("%s", String.valueOf(repeatableIndex));
		logger.info("Desired Output [" + sectionIndex + "][" + repeatableIndex + "] = " + outputValue);
		WebElement repeatableDesiredOutputEle = getOrWaitForElement(By.xpath(xpathLocator));
		repeatableDesiredOutputEle.clear();
		repeatableDesiredOutputEle.sendKeys(outputValue);
		//repeatableDesiredOutputEle.sendKeys(Keys.TAB);
		maf.hideKeyboard();
	}

	public String getRepeatableCalPointNominalInput(int sectionIndex, int repeatableIndex) throws Exception {
		String xpathLocator = repeatablePageCalPointNominalInput.replace("%r", String.valueOf(sectionIndex));
		xpathLocator = xpathLocator.replace("%s", String.valueOf(repeatableIndex));
		logger.info("Nominal Input [" + sectionIndex + "][" + repeatableIndex + "] : " + xpathLocator);
		WebElement repeatableNominalInputEle = getOrWaitForElement(By.xpath(xpathLocator));

		return repeatableNominalInputEle.getAttribute("value").toString();
	}

	public String getRepeatableCalPointDesiredOutput(int sectionIndex, int repeatableIndex) throws Exception {
		String xpathLocator = repeatablePageCalPointDesiredOutput.replace("%r", String.valueOf(sectionIndex));
		xpathLocator = xpathLocator.replace("%s", String.valueOf(repeatableIndex));
		logger.info("Desired Output [" + sectionIndex + "][" + repeatableIndex + "] : " + xpathLocator);
		WebElement repeatableDesiredOutputEle = getOrWaitForElement(By.xpath(xpathLocator));

		return repeatableDesiredOutputEle.getAttribute("value").toString();
	}

	public void expandToleranceSection(int sectionIndex, String type) throws Exception {
		logger.info("Expand Tolerance Accordion");
		if (getOrWaitForElement(
				By.xpath(OutputConstants.getXPath(type, "ToleranceSectionIcon", sectionIndex, sectionIndex)
						.replace("%s", String.valueOf(sectionIndex))))
				.getAttribute("description").contains("down")) {
			af.waitForElementToBePresent(
					By.xpath(OutputConstants.getXPath(type, "ToleranceSectionBtn", sectionIndex, sectionIndex)
							.replace("%s", String.valueOf(sectionIndex))))
					.click();
		}
	}

	public void expandRepeatableCalibrationPointSection(int sectionIndex) throws Exception {
		logger.info("Expand Calibration Point Accordion");
		if (getOrWaitForElement(
				By.xpath(repeatableAnalogCalPointSectionBtn.replace("%s", String.valueOf(sectionIndex))))
				.getAttribute("description").contains("down")) {
			getOrWaitForElement(
					By.xpath(repeatableAnalogCalPointSectionBtn.replace("%s", String.valueOf(sectionIndex)))).click();
		}
	}

	public String getTolFromValue(String tolIDFromLocator, int sectionIndex) throws Exception {
		return getOrWaitForElement(By.xpath(tolIDFromLocator.replace("%s", String.valueOf(sectionIndex))))
				.getText().trim();
	}

	public String getTolToValue(String tolIDToLocator, int sectionIndex) throws Exception {
		return getOrWaitForElement(By.xpath(tolIDToLocator.replace("%s", String.valueOf(sectionIndex))))
				.getText().trim();
	}

	public String getTolErrorValue(String tolIDErrorLocator, int sectionIndex) throws Exception {
		return getOrWaitForElement(By.xpath(tolIDErrorLocator.replace("%s", String.valueOf(sectionIndex))))
				.getText().trim();
	}

	public String getAssetErrorValue(String assetErrorLocator, int sectionIndex) throws Exception {
		return getOrWaitForElement(By.xpath(assetErrorLocator.replace("%s", String.valueOf(sectionIndex))))
				.getText().trim();
	}

	public String getProcessErrorValue(String processErrorLocator, int sectionIndex) throws Exception {
		return getOrWaitForElement(By.xpath(processErrorLocator.replace("%s", String.valueOf(sectionIndex))))
				.getText().trim();
	}

	public String getRepeatableAnalogCalPointSectionLabel(int sectionIdex) throws Exception {
		return getOrWaitForElement(
						By.xpath(repeatableAnalogCalPointSectionLabel.replace("%r", String.valueOf(sectionIdex))))
				.getText().trim();
	}

	// Calibration Repeatable point section
	public void enterRepeatableCalibrationPointValue(String path, String testDataType, String repeatablePointValue)
			throws Exception {

		logger.info("Enter repeatable calibration point values");

		// Read validation set collection in "{model}-validation.json" and choose a test
		// case
		String content = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
		JSONObject jsonObject = new JSONObject(content);
		JSONArray jsonTestData = jsonObject.getJSONArray(testDataType);
		int repeat = Integer.parseInt(repeatablePointValue);

		// Apply validation set test case
		for (int i = 1; i <= jsonTestData.length(); i++) {

			JSONObject entry = jsonTestData.getJSONObject(i - 1);
			String label = entry.getString("Input Label");
			
			// Find section label
			String sectionLabel = getRepeatableAnalogCalPointSectionLabel(i);
			logger.info("Opening section with description '" + sectionLabel + "'");
			assertTrue(sectionLabel.contains(label),
					String.format("Should contain label '%s' in section description '%s'", label, sectionLabel));

			// Open section
			expandRepeatableCalibrationPointSection(i);
			
			if(entry.has(DatasheetConstants.REPEATABLE_VALUES)) {
				JSONArray repeatableValuesList = entry.getJSONArray(DatasheetConstants.REPEATABLE_VALUES);
				
				for (int j = 1; j <= repeatableValuesList.length(); j++) {
					JSONObject values = repeatableValuesList.getJSONObject(j-1);

					String input = values.getString(DatasheetConstants.INPUT_VALUE);
					String output = values.getString(DatasheetConstants.OUTPUT_VALUE);
					enterRepeatableNominalInput(i, j, input);
					enterRepeatableDesiredOutput(i, j, output);
				}
				
			} else {
				
				String input = entry.getString(DatasheetConstants.INPUT_VALUE);
				String output = entry.getString(DatasheetConstants.OUTPUT_VALUE);

				// Enter nominal input and desired output for each row in section
				for (int j = 1; j <= repeat; j++) {
					enterRepeatableNominalInput(i, j, input);
					enterRepeatableDesiredOutput(i, j, output);
				}
			}
			
		}
	}
	
	/**
	 * Scroll to calibration point section in order to render the list details.
	 * 
	 * @param index - index of the list item to scroll to
	 * @throws Exception
	 */
	public void scrollToCalPointSection(int index) throws Exception {
		String calPointSectionLocator = OutputConstants.getXPath(this.getType(), "CalPointList").replace("%s", String.valueOf(index));
		String unRenderedListItemLocator = calPointSectionLocator + "//div[contains(@class,'infinite-card-loading')]";
		
		WebElement calPointSectionEle = getOrWaitForElement(By.xpath(calPointSectionLocator));
		jsExec.executeScript("arguments[0].scrollIntoView({block:'center', behavior:'smooth'})", calPointSectionEle);
		af.waitForElementToNotBePresent(By.xpath(unRenderedListItemLocator), af.DEFAULT_TIMEOUT_MS);
	}
	
	//add calibration point availablity
	public void checkAddCalpointAvailable() throws Exception {
			assertEquals(
				af.waitForElementToBePresent(By.id(this.getAddCalpointId())).getText(), "Add calibration point"
			);
			clickOnAddCalPoint();
	}
		
	private String getAddCalpointId() {
		return OutputConstants.getXPath(this.getType(), "AddCalibrationPointTile");
	}
	
	public void clickOnAddCalPoint() throws Exception {
		logger.info("Add calibration point");
		WebElement ele = af.waitForElementToBePresent(By.id(OutputConstants.getXPath(this.getType(), "addCalPointBtnIdLocator")));
		ele.click();
		validateToastOnAddCalPoint();
	}
	
	private void validateToastOnAddCalPoint() {
		String toastText;
		
		ToastComponent toast = new ToastComponent(af.getDriver(), false); // waits for toast element
		toastText = toast.getContentText().trim();
		toast.waitToastClose();
		logger.info("Validated presence of toast.");
		
		assertEquals(toastText, "BMXAR0004E - The Asset Function does not support adding calibration points.");
	}
	

	// Calibration Point section
	public void enterCalibrationPointValues(String path, String testDataType) throws Exception {
		enterCalibrationPointValuesUptoLength(path, testDataType, -1);
	}
	
	// Enter calibration point values upto specified number of points,
	// or enter all points if specified negative value or a number higher than available data length
	public void enterCalibrationPointValuesUptoLength(String path, String testDataType, int numPoints) 
			throws Exception {
		// TODO: make path read into utility function
		JSONArray jsonTestData = readData(path, testDataType);
		int dataLength = jsonTestData.length();
		int stopIndex = Math.min(dataLength, numPoints < 0 ? dataLength : numPoints);
		
		for (int i = 0; i < stopIndex; i++) {
			scrollToCalPointSection(i + 1);
			enterCalibrationPoint(path, i, this.getType(), jsonTestData);
		}
	}
	
	public void clearCalibrationPointValuesUptoLength(String path, String testDataType, int numPoints) 
			throws Exception {
		JSONArray jsonTestData = readData(path, testDataType);
		int dataLength = jsonTestData.length();
		int stopIndex = Math.min(dataLength, numPoints < 0 ? dataLength : numPoints);
		
		for (int i = 0; i < stopIndex; i++) {
			scrollToCalPointSection(i + 1);
			clearCalibrationPoint(i, this.getType());
		}
	}

	public void enterDiscreteCalibrationValues(String path, String testDataType, String condition) throws Exception {

		JSONArray jsonTestData = readData(path, testDataType);

		for (int i = 0; i < jsonTestData.length(); i++) {
			scrollToCalPointSection(i + 1);

			String actualLabel = getCalibrationPointSectionLabel(i, condition);
			String expectedLabel = jsonTestData.getJSONObject(i).get("Input Label").toString();

			assertTrue(actualLabel.contains(expectedLabel));

			expandCalibrationPointSection(i, condition);

			enterNominalInput(i + 1, jsonTestData.getJSONObject(i).get("Input Value").toString(),
					DatasheetConstants.DISCRETE_PREFIX + condition);
		}
	}

	private void enterCalibrationPoint(String path, int i, String type, JSONArray jsonTestData) throws Exception {
		// TODO Auto-generated method stub
		assertTrue(getCalibrationPointSectionLabel(i, type)
				.contains(jsonTestData.getJSONObject(i).get("Input Label").toString()));
		expandCalibrationPointSection(i, type);
		
		enterNominalInput(i + 1, jsonTestData.getJSONObject(i).get("Input Value").toString(), type);
		if (!this.isDiscreteType)
			enterDesiredOutput(i + 1, jsonTestData.getJSONObject(i).get("Output Value").toString(), type);
	}
	
	private void verifyCalibrationPoint(String path, int i, String type, JSONArray jsonTestData) throws Exception {
		expandCalibrationPointSection(i, type);
		
		assertEquals(getNominalInput(i + 1, type), jsonTestData.getJSONObject(i).get("Input Value").toString(), "Verified nominal input for point " + (i+1));
		if (!this.isDiscreteType)
			assertEquals(getDesiredOutput(i + 1, type), jsonTestData.getJSONObject(i).get("Output Value").toString(), "Verified desired output for point " + (i+1));
	}
	
	private void assertCalibrationPointEmpty(int i, String type) throws Exception {
		expandCalibrationPointSection(i, type);
		
		assertTrue(getNominalInput(i + 1, type).isBlank(), "Verified nominal input empty for point " + (i+1));
		if (!this.isDiscreteType)
			assertTrue(getDesiredOutput(i + 1, type).isBlank(), "Verified desired output empty for point " + (i+1));
	}
	
	private void clearCalibrationPoint(int i, String type) throws Exception {
		expandCalibrationPointSection(i, type);
		
		clearNominalInput(i + 1, type);
		if(!this.isDiscreteType)
			clearDesiredOutput(i + 1, type);
	}
	
	public void assertCalibrationPointReadOnlyState(String path, int i, String type, JSONArray jsonTestData, boolean readOnly) throws Exception {
		expandCalibrationPointSection(i, type);
		
		assertEquals(getNominalInputReadOnlyState(i + 1, type), readOnly);
		if (!this.isDiscreteType)
			assertEquals(getDesiredOutputReadOnlyState(i + 1, type), readOnly);
	}
	
	// Verify calibration point values
	public void verifyCalibrationPointValues(String path, String testDataType, boolean readOnlyState) 
			throws Exception {
		JSONArray jsonTestData = readData(path, testDataType);
		
		for (int i = 0; i < jsonTestData.length(); i++) {
			scrollToCalPointSection(i + 1);
			verifyCalibrationPoint(path, i, this.getType(), jsonTestData);
			assertCalibrationPointReadOnlyState(path, i, this.getType(), jsonTestData, readOnlyState);
		}
	}
	
	public void assertCalibrationPointsEmpty(String path, String testDataType) 
			throws Exception {
		JSONArray jsonTestData = readData(path, testDataType);
		
		for (int i = 0; i < jsonTestData.length(); i++) {
			scrollToCalPointSection(i + 1);
			assertCalibrationPointEmpty(i, this.getType());
		}
	}
	
	public void assertRepeatableCalPointsEmpty() 
			throws Exception {
		logger.info("Verify calibration points empty");
		assertFalse(af.isElementExists(By.xpath(repeatableCalPointNonEmpty)));
	}

	public void verifyTolerances(String path, String testDataType, String type) throws Exception {

		JSONArray jsonTestData = readData(path, testDataType);
		// TODO: move looping into assertTolerances method
		for (int i = 0; i < jsonTestData.length(); i++) {
			scrollToCalPointSection(i + 1);
			assertTolerance(i, type, jsonTestData);
		}
	}

	public void assertTolerance(int i, String type, JSONArray jsonTestData) throws Exception {
		logger.info("Verify As Found Tolerances for Datasheet["
				+ jsonTestData.getJSONObject(i).get("Input Label").toString() + "] calibration point.");
		expandCalibrationPointSection(i, type);
		expandToleranceSection(i, type);

		assertEquals(getTolFromValue(OutputConstants.getXPath(type, "Tol1FromValue"), i),
				jsonTestData.getJSONObject(i).get("T1 From").toString());
		assertEquals(getTolToValue(OutputConstants.getXPath(type, "Tol1ToValue"), i),
				jsonTestData.getJSONObject(i).get("T1 To").toString());
		assertEquals(getTolErrorValue(OutputConstants.getXPath(type, "Tol1ErrorValue"), i),
				jsonTestData.getJSONObject(i).get("T1 Error").toString());
		assertEquals(getTolFromValue(OutputConstants.getXPath(type, "Tol2FromValue"), i),
				jsonTestData.getJSONObject(i).get("T2 From").toString());
		assertEquals(getTolToValue(OutputConstants.getXPath(type, "Tol2ToValue"), i),
				jsonTestData.getJSONObject(i).get("T2 To").toString());
		assertEquals(getTolErrorValue(OutputConstants.getXPath(type, "Tol2ErrorValue"), i),
				jsonTestData.getJSONObject(i).get("T2 Error").toString());
		assertEquals(getTolFromValue(OutputConstants.getXPath(type, "Tol3FromValue"), i),
				jsonTestData.getJSONObject(i).get("T3 From").toString());
		assertEquals(getTolToValue(OutputConstants.getXPath(type, "Tol3ToValue"), i),
				jsonTestData.getJSONObject(i).get("T3 To").toString());
		assertEquals(getTolErrorValue(OutputConstants.getXPath(type, "Tol3ErrorValue"), i),
				jsonTestData.getJSONObject(i).get("T3 Error").toString());
		logger.info(type, jsonTestData.getJSONObject(i).has("T4 From"));
		if (jsonTestData.getJSONObject(i).has("T4 From")) {
			assertEquals(getTolFromValue(OutputConstants.getXPath(type, "Tol4FromValue"), i),
					jsonTestData.getJSONObject(i).get("T4 From").toString());
			logger.info(type, jsonTestData.getJSONObject(i).get("T4 To").toString().contains("T4 To"));
			assertEquals(getTolToValue(OutputConstants.getXPath(type, "Tol4ToValue"), i),
					jsonTestData.getJSONObject(i).get("T4 To").toString());
			assertEquals(getTolErrorValue(OutputConstants.getXPath(type, "Tol4ErrorValue"), i),
					jsonTestData.getJSONObject(i).get("T4 Error").toString());
		}

		assertEquals(getAssetErrorValue(OutputConstants.getXPath(type, "AssetErrorValue"), i),
				jsonTestData.getJSONObject(i).get("Asset Error").toString());
		assertEquals(getProcessErrorValue(OutputConstants.getXPath(type, "ProcessErrorValue"), i),
				jsonTestData.getJSONObject(i).get("Process Error").toString());
		Reporter.log("As Found Tolerances verified for datasheet["
				+ jsonTestData.getJSONObject(i).get("Input Label").toString() + "] calibration point.");
	}
	
	public boolean verifyInputOutOfRangeMessage() throws Exception {
		logger.info("Verify Input Out of Range Message");
		WebElement inputOutOfRangeMessageEle = af.waitForElementToBePresent(By.xpath(inputOutOfRangeMessage));
		logger.info("Input out of range message: {}", inputOutOfRangeMessageEle.getText().trim());
		Reporter.log("Input out of range message: " + inputOutOfRangeMessageEle.getText().trim());
		return inputOutOfRangeMessageEle != null;
	}
	
	public boolean verifyToleranceExceedMessage(String type) throws Exception {
		logger.info("Verify Tolerance Exceeded Message");
		WebElement toleranceExceededMsgEle = af.waitForElementToBePresent(By.xpath(OutputConstants.getXPath(type, "ToleranceExceedMessage")));
		logger.info("Tolerance exceeded message: {}", toleranceExceededMsgEle.getText().trim());
		Reporter.log("Tolerance exceeded message: " + toleranceExceededMsgEle.getText().trim());
		return toleranceExceededMsgEle != null;
	}
	
	public boolean verifyToleranceExceedIcon(String type) throws Exception {
		logger.info("Verify Tolerance Exceeded Icon");
		try {
			af.waitForElementToBePresent(By.xpath(OutputConstants.getXPath(type, "ToleranceIcon")));
			af.waitForElementToBePresent(By.xpath(OutputConstants.getXPath(type, "ToleranceIconBottom")));
		} catch (NoSuchElementException e) {
			return false;
		}
		
		return true;
	}
	
	public void clickDiscardSaveBtn(String type) throws Exception {
		af.waitForElementToBePresent(By.xpath(OutputConstants.getXPath(type, "DiscardSaveButton"))).click();
	}
	
	public void verifyNoAdjInlineNotification() throws Exception {
		assertEquals(
			af.waitForElementToBePresent(By.xpath(readOnlyInlineNotification.replace("%s", this.getNoAdjInlineNotificationId()))).getText(),
			readOnlyNotificationMsg
		);
	}
	
	private String getNoAdjInlineNotificationId() {
		if(this.getTypeIsRepeatable())
			return this.repeatableNoAdjInlineNotification;
		return OutputConstants.getXPath(this.getType(), "NoAdjInlineNotification");
	}

	
	// TODO: Use this method and remove the parameter 'type' from the methods wherever applicable
	// That way, no need to pass type externally
	/**
	 * @return The type of page - can be used as an input for the 'type' parameter in
	 * {@link OutputConstants#getXPath} except for Repeatable case. This method
	 * should be called from a subclass instance.
	 */
	public final String getType() {
		String conditionClassStr = this.getClass().getSimpleName();
		
		String type = this.isDiscreteType ? DatasheetConstants.DISCRETE_PREFIX : "";
		
		if(conditionClassStr.startsWith(DatasheetConstants.CONDITION_ASFOUND))
			type += DatasheetConstants.CONDITION_ASFOUND;
		else if(conditionClassStr.startsWith(DatasheetConstants.CONDITION_ASLEFT))
			type += DatasheetConstants.CONDITION_ASLEFT;
		else
			type = DatasheetConstants.CONDITION_REPEATABLE;
		
		return type;
	}
	
	/**
	 * 
	 * @return Whether this page type is repeatable or not. This method
	 * should be called from a subclass instance.
	 * 
	 * @see {@link #getType()}
	 */
	public final boolean getTypeIsRepeatable() {
		return getType() == DatasheetConstants.CONDITION_REPEATABLE;
	}
	
	protected AbstractAutomationFramework getAf() {
		return af;
	}

	protected void setAf(AbstractAutomationFramework af) {
		CalibrationBaseTest.af = af;
	}

	public boolean isDiscreteType() {
		return isDiscreteType;
	}

	public void setDiscreteType(boolean isDiscreteType) {
		this.isDiscreteType = isDiscreteType;
	}
	
	public void saveValues() throws Exception {
		WebElement saveBtnEle = af.waitForElementToBePresent(By.xpath(saveBtnXPath));
		String saveBtnIdLocator = saveBtnEle.getAttribute("id");
		
		saveBtnEle.click();
		af.waitForElementToNotBePresent(By.id(saveBtnIdLocator + "_loading"), af.DEFAULT_TIMEOUT_MS);
	}
	
	public void clickBackArrowBtn() throws Exception {
		af.waitForElementToBePresent(By.xpath(backBtnXPath)).click();
	}

	public static CalibrationBaseTest getPageObj(AbstractAutomationFramework af, String condition) {
		if(condition == DatasheetConstants.CONDITION_ASFOUND)
			return new AsFoundValuesPage(af);
		else
			return new AsLeftValuesPage(af);
	}
}
