# Calibration functionality

These test cases will verify calibration functionality on Technician web app, online and offline mode on mobile containers.

These test cases will cover functionalities of following user stories:

- GRAPHITE-59349: The Technician completes the Calibration work order.  Maximo will make all Calibration Checks before allowing Completion

**Design URL:**

- <https://www.figma.com/file/RAPUxZUIzaPZonCgW1udeP/Technician-%26-Assets?type=design&node-id=13537-276997&mode=design>

**Note:**

- In case of web app, all transactions are saved in database and refreshed on the UI in real time.
- In case of online mode on mobile containers, transactions are sent to the database for saving instantly but UI is not refreshed from database until 'Check for Updates' button is clicked.
- In case of offline mode on mobile containers, transactions are queued for syncing to the database and synced to database once device is online. UI is not refreshed from database until 'Check for Updates' button is clicked.
- All server side error messages will be displayed in error page on devices post syncing with server and tapping on 'Check for Updates' button in both online and offline modes.

## Scenario 1 - Verify availability of calibration icon on work order details page and and verify availibility/unavailibility of badge count. Work order should be marked completed after following datasheet and tools mandatory checks.

**Pre-condition:**

1. Login to maximo/manage application as admin.
2. Create a new work order.
3. Assign datasheets to the work order which is in work order tracking application.
4. Add actual tools to the work which should reflect in report work page.
5. In organizations, search EAGLENA and select calibration options and select work order-other organizational settings. 
6. Under Validation of Tools on Work Orders, select between 0, 1 and 2 and perform checks one by one for all three. 
7. Assign labor/technician to work order in Assignments tab and approve the work order.
8. Enable e-signature.

**Steps:**

1. Login to technician app with the technician assigned to work order.
2. Click on "My Schedule" tile.
3. Find the work order created in pre-condition steps in "Assigned Work" filter.
4. Click on the work order card to open wo details page.
5. Verify availability of calibration touchpoint under Asset and Location section.
6. Verify availability of badge count on calibration icon as datasheets are added to it.
7. Click on the calibration touchpoint and navigate to calibration page where technician can change status and add As left and As found status.
8. All datasheets marked as Required needs to be filled before marking work order complete.
9. If any required datasheet is not complete, error popup displays with Incomplete data sheets.
10. If actual tools are not added to actual tools and if actual tool maxvar is 0 (No validation Between Tools and Status of CLOSED or COMP), then no error will be displayed and work order will move to next page for completion.
11. If actual tools are not added to actual tools and if actual tool maxvar is 1(Validate but with warning message) , then warning will be displayed with continue button and technician can move forward.
12. If actual tools are not added to actual tools and if actual tool maxvar is 2(Enforce validation between Tools and Status of CLOSED or COMP), then error will be displayed with and technciian cannot complete work order without adding tools.
13. Electronic signature prompts while completing work order.

**Result:**

- "Calibration" icon should be available on work order details page under Assets and Location section.
- Badge count should be available on "Calibration" icon as datasheets are associated with the work order.
- Technician should be able to change status and add As left and As found status.
- On completing work order, required datasheets should be complete.
- Error popup should display if datasheets are not complete.
- If maxvar set is 0, no erorr or warning message should display.
- If maxvar set is 1, warning message should prompt with a continue button.
- If maxvar set is 2, then error message should prompt and not allow technician to complete work order without adding tools.
- After electronic signature prompt, work order should be marked completed and move out of the WO Assigned List page.

## Scenario 2 - Verify checks for 'Qualified Technician' with labor assigned to the work order and relation between technician and tool field.

**Pre-condition:**

1. Login to maximo/manage application as admin.
2. Add qualifictaion to the labor.
3. Create a tool and assign required qualification to it.
4. Create a calibration new work order.
5. In organizations, search EAGLENA and select calibration options and select work order-other organizational settings. 
6. Under Qualified Technician, select between 0, 1 and 2 and perform checks one by one for all three. 
7. Assign labor/technician to work order in Assignments tab and approve the work order.
8. Enable e-signature.

**Steps:**

1. Login to technician app with the technician assigned to work order.
2. Click on "My Schedule" tile.
3. Find the work order created in pre-condition steps in "Assigned Work" filter.
4. Click on the work order card to open wo details page.
5. Click on the report work page.
6. Click on + icon to add labor or by clicking start WO and start travel options in the work order.
7. If there is No validation between Tool and Technician, then no error will be displayed and labor will be added.
8. If value selected is Validate but with warning message, then only warning message will be prompted with a continue button and when technician clicks on continue buuton labor will be added.
9. If there is Enforce validation between Tool and Technician, then error will be displayed and labor will not be added.

**Result:**

- Technician should be able to navigate to work order details page and open report work page.
- If maxvar set is 0, no erorr or warning message should display and should be able to add labor.
- If maxvar set is 1, warning message should prompt with a continue button and should be able to add labor.
- If maxvar set is 2, then error message should prompt and not allow technician to add labor.

## Scenario 3 - Verify checks for due date in actual tools in accordance with the current date. 

**Pre-condition:**

1. Login to maximo/manage application as admin.
2. Create a tool and enable calibration toggle buttons in the tools page (Internal Calibration? and Is M&TE?)
3. Add tools to storeroom and change its status to Active.
4. Create a new asset and enable calibration toggle button.
5. Create a job plan and set it to active.
6. Add asset and datasheet to job plan.
7. Create PM and generate Work Order.
8. In review dates, add a due date. 

**Steps:**

1. Login to technician app with the technician assigned to work order.
2. Click on "My Schedule" tile.
3. Find the work order created in pre-condition steps in "Assigned Work" filter.
4. Click on the work order card to open wo details page.
5. Click on the report work page.
6. Click on + icon to add tools and select to add rotating tools which was created in the pre-condition steps.
7. Validation check is performed if tool PM due date is greater than the current date or not.

**Result:**

- If tool PM due date is greater than the current date there should be no error or validation message prompted.
- If tool PM due date is in past than the current date then it will throw a validation error message.

## Scenario 4 - Verify Calibration Work Order, Labor and Tools Qualification Check when rotating tools get added and technician is authorized to use the tool

**Pre-condition:**

1. Login to maximo/manage application as admin.
2. Create a tool and enable calibration toggle buttons in the tools page (Internal Calibration? and Is M&TE?)
3. Add tools to storeroom and change its status to Active.
4. Create a new asset and enable calibration toggle button.
5. Create a job plan and set it to active.
6. Add asset and datasheet to job plan.
7. Create PM and generate Work Order.
8. Add required qualifications to the workorder.

**Steps:**

1. Login to technician app with the technician assigned to work order.
2. Click on "My Schedule" tile.
3. Find the work order created in pre-condition steps in "Assigned Work" filter.
4. Click on the work order card to open wo details page.
5. Click on the report work page.
6. Click on + icon to add tools and select to add rotating tools which was created in the pre-condition steps.
7. Select tool and rotating asset for that. 
8. Technician field is displayed. Click on the chevron next to it and select qualification. 

**Result:**

- If technician has the required qualifications then it will be added. 
- If technician doesn't have the matching qualifications then it will throw an error popup. 