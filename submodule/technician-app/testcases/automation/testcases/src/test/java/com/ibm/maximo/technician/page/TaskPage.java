package com.ibm.maximo.technician.page;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.maximo.automation.mobile.AbstractAutomationFramework;
import com.ibm.maximo.components.AssistCardTemplateComponent;
import com.ibm.maximo.components.ButtonComponent;
import com.ibm.maximo.components.DataListComponent;
import com.ibm.maximo.components.DataListItemComponent;
import com.ibm.maximo.components.FilterComponent;
import com.ibm.maximo.components.IconComponent;
import com.ibm.maximo.components.LabelComponent;
import com.ibm.maximo.components.PictogramComponent;
import com.ibm.maximo.components.RichTextEditorComponent;
import com.ibm.maximo.components.RichTextViewerComponent;
import com.ibm.maximo.components.TagComponent;
import com.ibm.maximo.technician.framework.TechnicianTest;

public class TaskPage {
	private final Logger logger = LoggerFactory.getLogger(TaskPage.class);
	private String taskStatusChangeDropdownLocator = "//*[text()='Waiting on approval']";
	private String selectCompleteTaskIcon = "zd92m_items_COMP_selectionCheckBoxIcon_touch";
	private String saveStatusChangeButton = "//*[@id='kamve_k4vqy']";
	private static final String TaskCompleteButton = "pb_mv[";// +indexnumber+"]"
	private static final String greenCheckMarkLocator = "cnjkdsu6[";// +indexnumber+"]";
	private String assetLocator = "jy394";
	private String locationLocator = "ds_u8";
	private String assetIcon = "wrrn7";
	private String locationIcon = "cs779";
	private String taskStatus = "gxm55_0";
	private String taskTitle = "e9rgg[";
	private String taskTitle2 = "]";
	private String clickStatusChange = "gxm55";
	private String taskBackChevron = "nr2dj-pageheader_breadcrumb_icon";
	private String doneButton = "//button[@id='qajz4']";
	private String closeTaskStatus = "taskStatusChangeDialog_button";
	private String taskLongDesc = "q98gxstrippedRichTextViewer_label";
	private String taskText = "jrqqj";
	private static final String taskExpandLocator1 = "q439v_items_";
	private static final String taskExpandLocator2 = "_datalistWrapper";
	private String attachmentId = "rgm_j";
	private static final String taskAssetName = "jy394_textoverflow_value0";
	private static final String taskAssetDescription = "bgpk2";
	private static final String taskLocationDescription = "pds6r_fieldValue0";
	private static final String taskDescription = "q_k3x[";
	private static final String expandIcon1 = "q439v_items_";
	private static final String expandIcon2 = "_childCaret_touch";
	private static final String pageTitle = "nr2dj-pageheader_title";
	private static final String expandIconClick2 = "_childCaretContainer_touch";
	private String doneButtonId = "qajz4";
	public String taskPageHeader = "toolTaskLookup_sliding_drawer_headerLabel";
	private String taskContent = "//*[contains(text(),'Task-$index')]";
	private String taskBackButton = "toolTaskLookup_sliding_drawer_button";
	private String lockICon= "z96yb[1]";
	private String firstTaskExpand ="//div[@tabindex='0']//div[starts-with(@id,'q439v_items_') and contains(@id,'_childCaretContainer_touch')]";
	private String secondTaskExpand ="//div[@tabindex='-1']//div[starts-with(@id,'q439v_items_') and contains(@id,'_childCaretContainer_touch')]";
	private String verifyApprovedTask1 ="//span[@id='gxm55_0']//span[text()='Approved']";
	private String greenTickID ="yn9rr[0]";
	private String backButton ="nr2dj-pageheader_breadcrumb_icon";
	private String taskCount ="yyb2k_qdn4v_badge";
	private String addLongDescriptionLocator ="bknra";
	private String addLongDescriptionTextLocator ="richtextViewer-stripped-container";
	private String longDescriptionBackButton ="planTaskLongDesc_close_button";
	private String crossButton ="reportTimeDrawer_button";
	
	private AbstractAutomationFramework af;

	public TaskPage(AbstractAutomationFramework af) {
		this.af = af;
	}

	// Save Complete status
	public void saveStatusChange() throws Exception {
		af.waitForElementToBePresent(By.xpath(saveStatusChangeButton), af.DEFAULT_TIMEOUT_MS * 2);
		af.waitForElementToBePresent(By.xpath(saveStatusChangeButton), 1).click();
	}

	/*
	 * Method for verifying attachment exist
	 *
	 * @return boolean
	 * 
	 * @throws Exception
	 */
	public boolean isAttachmentExists() throws Exception {
		try {

			return af.isElementExists(By.id(attachmentId));

		} catch (Exception e) {
			return false;
		}
	}

	// Click on Change Status Drawer
	public void taskStatusChange() throws Exception {
		af.waitForElementToBePresent(By.xpath(taskStatusChangeDropdownLocator), af.DEFAULT_TIMEOUT_MS * 5);
		af.waitForElementToBePresent(By.xpath(taskStatusChangeDropdownLocator), 1000).click();
	}

	// Click on Complete status on Change Status Drawer
	public void selectCompleteTaskStatus() throws Exception {
		af.instantiateComponent(ButtonComponent.class, selectCompleteTaskIcon).click();
		af.waitForElementToBePresent(By.id(selectCompleteTaskIcon), 2000);
	}

	// Get blue checkmark on task page
	public String getBlueCheckmarkOnTaskPage(int indexnumber) throws Exception {
		af.waitForElementToBePresent(By.id(pageTitle), 3000);
		return af.instantiateComponent(ButtonComponent.class, TaskCompleteButton + indexnumber + "]")
				.getIconDescription();
	}

	// Get new Status on Task Page
	public String getGreenCheckmarkOnTaskPage(int indexnumber) throws Exception {
		af.waitForElementToBePresent(By.id(pageTitle), 1000);
		return af.instantiateComponent(IconComponent.class, greenCheckMarkLocator + indexnumber + "]").getIconName();
	}

	// Method to get asset Number
	public String getAssetNum() throws Exception {
		af.waitForElementToBePresent(By.id(assetLocator), 2000);
		LabelComponent labelComponent = af.instantiateComponent(LabelComponent.class, assetLocator);
		return labelComponent.getLabel();
	}

	// Method to get Location Number
	public String getLocationNum() throws Exception {
		af.waitForElementToBePresent(By.id(locationLocator), 2000);
		LabelComponent labelComponent = af.instantiateComponent(LabelComponent.class, locationLocator);
		return labelComponent.getLabel();
	}

	// Get asset icon on task page
	public String getAssetIcon() throws Exception {
		af.waitForElementToBePresent(By.id(assetIcon), 2000);
		return af.instantiateComponent(ButtonComponent.class, assetIcon).getIconDescription();
	}

	// Get location icon on task page
	public String getLocationIcon() {
		return af.instantiateComponent(ButtonComponent.class, locationIcon).getIconDescription();
	}

	/**
	 * Method to get status text on Task Page
	 * 
	 * @return String task status
	 * @throws Exception
	 */
	public String verifyTaskStatus() throws Exception {
		af.waitForElementToBePresent(By.id(taskStatus), 5000);
		return af.instantiateComponent(TagComponent.class, taskStatus).getLabel();
	}

	/**
	 * Method to get click status dropdown on Task Page
	 * 
	 * @return void task status
	 * @throws Exception
	 */
	public void clickTaskStatus() throws Exception {
		af.waitForElementToBePresent(By.id(taskStatus), 5000);
		af.instantiateComponent(LabelComponent.class, taskStatus).click();
	}

	/**
	 * Method to open task status drawer
	 * 
	 * @throws Exception
	 */
	public void openTaskStatusDrawer() throws Exception {
		af.instantiateComponent(ButtonComponent.class, clickStatusChange).click();
	}

	/**
	 * Method to click back chevron to navigate to WO details page
	 * 
	 * @throws Exception
	 */
	public void clickBackChevron() throws Exception {
		af.waitForElementToBePresent(By.id(taskBackChevron), af.DEFAULT_TIMEOUT_MS * 5);
		af.instantiateComponent(ButtonComponent.class, taskBackChevron).click();
	}

	/**
	 * Method to complete task
	 * 
	 * @throws Exception
	 */
	public void completeTask(String blueTickId) throws Exception {
		af.waitForElementToBePresent(By.id(blueTickId), af.DEFAULT_TIMEOUT_MS * 5);
		af.instantiateComponent(ButtonComponent.class, blueTickId).click();
	}

	/**
	 * Method to click Done button
	 * 
	 * @throws Exception
	 */
	public void clickDone() throws Exception {
		af.waitForElementToBePresent(By.xpath(doneButton), af.DEFAULT_TIMEOUT_MS * 5);
		af.instantiateComponent(ButtonComponent.class, doneButton).click();
	}

	/**
	 * Method to collapse/expand task
	 * 
	 * @param chevronId, index
	 * @throws Exception
	 */
	public void taskChevron(String chevronId, int index) throws Exception {
		af.waitForElementToBePresent(By.id(chevronId), af.DEFAULT_TIMEOUT_MS * 5);
		DataListComponent dl1 = af.instantiateComponent(DataListComponent.class, chevronId);
		List<DataListItemComponent> list = dl1.getChildrenTouch();
		DataListItemComponent item1 = list.get(index);
		item1.clickCaretTouch();
	}

	/**
	 * Method to close task status change drawer
	 * 
	 * @throws Exception
	 */
	public void closeTaskStatusDrawer() throws Exception {
		af.instantiateComponent(ButtonComponent.class, closeTaskStatus).click();
	}

	/**
	 * Method to get task description
	 * 
	 * @param taskDesc
	 * @return String task description
	 */
	public String getTaskDescription(String taskDesc) {
		LabelComponent labelComponent = af.instantiateComponent(LabelComponent.class, taskDesc);
		return labelComponent.getLabel();
	}

	/**
	 * Method to get task long description
	 * 
	 * @return String task long description
	 */
	public String getTaskLongDescription() {
		LabelComponent labelComponent = af.instantiateComponent(LabelComponent.class, taskLongDesc);
		return labelComponent.getLabel();
	}

	/**
	 * Method to get lock icon on task
	 * 
	 * @param lockIcon
	 * @return String lock icon description
	 * @throws Exception
	 */
	public String getLockIcon(String lockIcon) throws Exception {
		return af.instantiateComponent(ButtonComponent.class, lockIcon).getIconDescription();
	}

	/**
	 * Method to get text of locked task status
	 * 
	 * @return String task text
	 */
	public String getLockedTaskText() {
		LabelComponent labelComponent = af.instantiateComponent(LabelComponent.class, taskText);
		return labelComponent.getLabel();
	}

	/**
	 * Method to get blue checkmark on task
	 * 
	 * @return String blue checkmark icon description
	 * @throws Exception
	 */
	public String getBlueCheckmark(String id) throws Exception {
		af.waitForElementToBePresent(By.id(pageTitle), 12000);
		return af.instantiateComponent(ButtonComponent.class, id).getIconDescription();
	}

	/**
	 * Method to get green checkmark on task
	 * 
	 * @return String green checkmark icon description
	 * @throws Exception
	 */
	public String getGreenCheckmark(String id) throws Exception {
		af.waitForElementToBePresent(By.id(pageTitle), 12000);
		return af.instantiateComponent(ButtonComponent.class, id).getIconDescription();
	}

	/**
	 * Method to verify done is disabled or enabled
	 * 
	 * @return boolean
	 * @throws Exception
	 */
	public boolean verifyDoneButton() throws Exception {
		try {
			af.waitForElementToBeEnabled(By.xpath(doneButton));
			return true;

		} catch (Exception e) {
			logger.info("Done Button not found possible ID changed");
			return false;

		}
	}

	/**
	 * Method to verify if task exists
	 * 
	 * @return boolean is task existent
	 * @throws Exception
	 */
	public boolean isTaskExists(int taskPosition) throws Exception {
		return af.isElementExists(By.id(taskTitle + taskPosition + taskTitle2));

	}

	/**
	 * Method to get task title
	 *
	 * @return String task title
	 */
	public String getTaskTitle(int taskPosition) {
		return af.instantiateComponent(LabelComponent.class, taskTitle + taskPosition + taskTitle2).getValue();

	}

	/**
	 * Method to verify if task is expanded
	 * 
	 * @return boolean is task expanded (checks by existent ul element when non
	 *         expanded)
	 * @throws Exception
	 */
	public boolean isTaskExpanded(String wonum, int taskid) throws Exception {
		Object[] resultsObjects = TechnicianTest.jdbcConnection.executeSQL(
				"select workorderid from MAXIMO.WORKORDER where taskid = '" + taskid + "'" + " and parent = '" + wonum + "'");
		Object[] resultArray1 = (Object[]) resultsObjects[0];
		String value = resultArray1[0] + "";
		af.waitForElementToBePresent(By.id(pageTitle), 12000);
		return !af.isElementExists(By.id(taskExpandLocator1 + value + taskExpandLocator2));
	}

	/**
	 * Method to click Done task button
	 * 
	 * 
	 */

	public void clickDoneTaskButton(int index) throws Exception {
//		af.waitForElementToBePresent(By.id(TaskCompleteButton + index + "]"), 25000);
//		af.instantiateComponent(LabelComponent.class, TaskCompleteButton + index + "]").click();
		try {
			af.waitForElementToBePresent(By.id(TaskCompleteButton + index + "]"), af.DEFAULT_TIMEOUT_MS * 70);
			af.instantiateComponent(LabelComponent.class, TaskCompleteButton + index + "]").click();
		} catch (StaleElementReferenceException se) {
			af.waitForElementToBePresent(By.id(TaskCompleteButton + index + "]"), af.DEFAULT_TIMEOUT_MS * 70);
			af.instantiateComponent(LabelComponent.class, TaskCompleteButton + index + "]").click();
		} catch (Exception e) {
			throw new RuntimeException("enable to handle StaleElementReferenceException ");
		}
	}

	/**
	 * Method to get task asset
	 * 
	 * @return String task asset
	 */
	public String getTaskAssetName() {
		LabelComponent labelComponent = af.instantiateComponent(LabelComponent.class, taskAssetName);
		return labelComponent.getValue();
	}

	/**
	 * Method to get task asset description
	 * 
	 * @return String task asset description
	 */
	public String getTaskAssetDescription() throws Exception {
		af.waitForElementToBePresent(By.id(taskAssetDescription), 2000);
		LabelComponent labelComponent = af.instantiateComponent(LabelComponent.class, taskAssetDescription);
		return labelComponent.getValue();
	}

	/**
	 * Method to get task location description
	 * 
	 * @return String task location description
	 */
	public String getTaskLocationDescription() {
		LabelComponent labelComponent = af.instantiateComponent(LabelComponent.class, taskLocationDescription);
		return labelComponent.getValue();
	}

	/**
	 * Method to get task description
	 * 
	 * @return String task description
	 */
	public String getTaskDescription() {
		LabelComponent labelComponent = af.instantiateComponent(LabelComponent.class, taskDescription);
		return labelComponent.getValue();
	}

	/**
	 * Method to get task description
	 * 
	 * @return String task description
	 */
	public String getTaskPageTitle() {
		LabelComponent labelComponent = af.instantiateComponent(LabelComponent.class, pageTitle);
		return labelComponent.getValue();
	}

	/**
	 * Method to get expand task button description
	 * 
	 * @return String description
	 * 
	 */
	public String taskExpandIconValue(String wonum, int taskid) {
		Object[] resultsObjects = TechnicianTest.jdbcConnection.executeSQL(
				"select workorderid from MAXIMO.WORKORDER where taskid = '" + taskid + "'" + " and parent = '" + wonum + "'");
		Object[] resultArray1 = (Object[]) resultsObjects[0];
		String value = expandIcon1 + resultArray1[0] + expandIcon2;
		PictogramComponent pc = af.instantiateComponent(PictogramComponent.class, value);
		return pc.getAttribute("description");

	}

	/**
	 * Method to get click chevron icon
	 * 
	 * @throws Exception
	 * 
	 */
	public void clickTaskExpandIcon(String wonum, int taskid) throws Exception {
		af.waitForElementToBePresent(By.id(pageTitle), 25000);
		Object[] resultsObjects = TechnicianTest.jdbcConnection.executeSQL(
				"select workorderid from MAXIMO.WORKORDER where taskid = '" + taskid + "'" + " and parent = '" + wonum + "'");
		Object[] resultArray1 = (Object[]) resultsObjects[0];
		String value = expandIcon1 + resultArray1[0] + expandIconClick2;
		af.instantiateComponent(PictogramComponent.class, value).click();
	}

	// Verify if button is secondary
	public boolean verifyCompleteButtonSecondary(int indexnumber) {
		return af.instantiateComponent(AssistCardTemplateComponent.class, TaskCompleteButton + indexnumber + "]")
				.isChevronSecondary();
	}

	// Verify if button is primary
	public boolean verifyCompleteButtonPrimary(int indexnumber) {
		return af.instantiateComponent(AssistCardTemplateComponent.class, TaskCompleteButton + indexnumber + "]")
				.isChevronPrimary();
	}

	/**
	 * Method to click Done button
	 * 
	 * @throws Exception
	 */
	public void clickDoneButton() throws Exception {
		af.waitForElementToBePresent(By.id(doneButtonId), af.DEFAULT_TIMEOUT_MS * 5);
		af.instantiateComponent(ButtonComponent.class, doneButtonId).click();
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
		logger.info("pageTitle:" + pageTitle);
		return pageTitle;
	}

	/**
	 * Method to verify task content
	 * 
	 * @param optionIndex
	 * @return
	 * @throws Exception
	 */
	public String verifyTaskContent(int index) throws Exception {
		LabelComponent labelComponent = new LabelComponent(af.getWebDriver(),By.xpath(taskContent.replace("$index", String.valueOf(index))));
		return labelComponent.getValue();
	}
	
	/**
	 * Method to click back button
	 * 
	 * @throws Exception
	 */
	public void taskPageBackButton() throws Exception {
		af.waitForElementToBePresent(By.id(taskBackButton), 4000);
		af.instantiateComponent(ButtonComponent.class, taskBackButton).click();
	}
	/**
	 * Method to click back button
	 * 
	 * @throws Exception
	 */
	public boolean verifyLockIcon() throws Exception {
		af.waitForElementToBePresent(By.id(lockICon), 2000);
		return af.isElementExists(By.id(lockICon));
		
	}
	
	/**
	 * Method to verify if task is expanded
	 * 
	 * @return boolean is task expanded (checks by existent ul element when non
	 *         expanded)
	 * @throws Exception
	 */
	public boolean expandTask(String wonum, int taskid) throws Exception {
		Object[] resultsObjects = TechnicianTest.jdbcConnection.executeSQL(
				"select workorderid from MAXIMO.WORKORDER where taskid = '" + taskid + "'" + " and parent = '" + wonum + "'");
		Object[] resultArray1 = (Object[]) resultsObjects[0];
		String value = resultArray1[0] + "";
		af.waitForElementToBePresent(By.id(pageTitle), 12000);
		af.waitForElementToBePresent(By.id(taskExpandLocator1 + value + taskExpandLocator2), 12000).click();
		return !af.isElementExists(By.id(taskExpandLocator1 + value + taskExpandLocator2));
	}
	
	
	// Generated by WCA for GP
	/**
		 * Check if Complete Task Present
		 * 
		 * @param blueTickId
		 * @throws Exception
		 */
	public void isCompleteTaskPresent(String blueTickId) throws Exception {
		af.waitForElementToBePresent(By.id(blueTickId), af.DEFAULT_TIMEOUT_MS * 5);
	}
	
	// Generated by WCA for GP
	/**
		 * Click on Second Task
		 * 
		 * @throws Exception
		 */
	public void clickOnSecondTask() throws Exception {
		af.waitForElementToBePresent(By.xpath(secondTaskExpand), 2000).click();;
	}
	
	// Generated by WCA for GP
	/**
		 * Check if Approved Present
		 * 
		 * @return
		 * @throws Exception
		 */
	public boolean isApprovedPresent() throws Exception {
		 af.waitForElementToBePresent(By.xpath(verifyApprovedTask1), 2000);
		return af.isElementExists(By.xpath(verifyApprovedTask1));
	}
	
	// Generated by WCA for GP
	/**
		 * Check if Green Tick Present
		 * 
		 * @return
		 * @throws Exception
		 */
	public boolean isGreenTickPresent() throws Exception {
		 af.waitForElementToBePresent(By.id(greenTickID), 2000);
		return af.isElementExists(By.id(greenTickID));
	}
	
	// Generated by WCA for GP
	/**
	 * Click back button
	 * 
	 * @throws Exception
	 */
	public void backButtonTaskPage() throws Exception {
		af.waitForElementToBePresent(By.id(backButton), 5000).click();
	}
	
	// Generated by WCA for GP
	/**
	 * Verify count of task
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getTaskCountDisplay() throws Exception {
		af.waitForElementToBePresent(By.id(taskCount), af.DEFAULT_TIMEOUT_MS * 2);
		FilterComponent tag = af.instantiateComponent(FilterComponent.class, taskCount);
		return tag.getTagCount();
	}
	
	// Generated by WCA for GP
	/**
	 * Method to verify long text icon exists
	 * 
	 * @throws Exception
	 */
	public void longDecsriptionIconExists() throws Exception {
		af.waitForElementToBePresent(By.id(addLongDescriptionLocator), 2000);
	}
	
	// Generated by WCA for GP
	/**
	 * Method to click long description icon
	 * 
	 * @throws Exception
	 */
	public void clickLongDescriptionIcon() throws Exception {
		af.instantiateComponent(ButtonComponent.class, addLongDescriptionLocator).click();
	}
	
	// Generated by WCA for GP
	/**
	 * Method to click back from long description page
	 * 
	 * @throws Exception
	 */
	public void longDescriptionBackButton() throws Exception {
		af.instantiateComponent(ButtonComponent.class, longDescriptionBackButton).click();
	}
	
	// Generated by WCA for GP
	/**
	 * Method to get long description text
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getTextWOLongDescription() throws Exception {
		RichTextViewerComponent richTextView = af.instantiateComponent(RichTextViewerComponent.class,
				addLongDescriptionTextLocator);
		return richTextView.getRichTextViewerStrippedDescription();
	}
	
	// Generated by WCA for GP
	/**
	 * Click on X button
	 * 
	 * @throws Exception
	 */
	public void crossButtonLaborPage() throws Exception {
		af.instantiateComponent(ButtonComponent.class, crossButton).click();
	}
}
