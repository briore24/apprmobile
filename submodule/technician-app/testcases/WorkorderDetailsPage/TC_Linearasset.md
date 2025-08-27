# Eli can work linear assets on his work orders/inspections on Workorder edit page and quick report page

These test cases will verify  Eli can work linear assets on his work orders/inspections on Workorder edit page and quick report page on Technician web app, online and offline mode on mobile containers and the below mentioned test cases will cover the functionalities of following stories.

- GRAPHITE-68326: Eli can work linear assets on his work orders/inspections


**Design URL:**

- <https://www.figma.com/file/RAPUxZUIzaPZonCgW1udeP/Technician-%26-Assets?type=design&node-id=10238-99352&mode=design&t=RMbj7RLFrkhGZD6f-0>

**Note:**

- In case of web app, all transactions are saved in database and refreshed on the UI in real time.
- In case of online mode on mobile containers, transactions are sent to the database for saving instantly but UI is not refreshed from database until 'Check for Updates' button is clicked.
- In case of offline mode on mobile containers, transactions are queued for syncing to the database and synced to database once device is online. UI is not refreshed from database until 'Check for Updates' button is clicked.
- All server side error messages will be displayed in error page on devices post syncing with server and tapping on 'Check for Updates' button in both online and offline modes.

## Scenario 1 - Verify when user selected linear asset while creating work order “linear segment details” section should be visible.

**Pre-condition:**
1. Login to maximo/manage application as Admin.
2. Create a work order.
3. Add Description, Long description, Priority, scheduled start and scheduled finish, Work type, Estimated duration,Linear Asset and Location to the work order.
4. Add a planned Labor to the work order.
5. Save and approve the work order.

**Steps:**
1. Login to Maximo mobile app with Technician assigned to work order.
2. Click on "My Schedule" tile.
3. Search for the newly created Work order in WO list and click on it to open WO details.
4. Click on edit icon in work order details page open a new page.

**Result:**

- User can see the blank linear segment details

## Scenario 2 - Verify when user selecting  linear asset while editing work order on work order details page “linear segment details” section should be visible.

**Pre-condition:**
1. Login to maximo/manage application as Admin.
2. Create a work order.
3. Add Description, Long description, Priority, scheduled start and scheduled finish, Work type, Estimated duration,Non linear Asset and Location to the work order.
4. Add a planned Labor to the work order.
5. Save and approve the work order.

**Steps:**
1. Login to Maximo mobile app with Technician assigned to work order.
2. Click on "My Schedule" tile.
3. Search for the newly created Work order in WO list and click on it to open WO details.
4. Click on edit icon in work order details page open a new page.
5. Click on Asset lookup and select linear asset.

**Result:**
- User can see the blank “linear segment details” page


## Scenario 3 - Verify that as a technician, the user should be able to create new emergency workorder for critical situationsn with linear asset and linear asset segment form
**Pre-condition:**
1. Login with admin credentials in Maximo classic/Manage.
2. Enable 'Allow Quick reporting' in the object structures.
3. Set maximo.mobile.usetimer to '1'.

**Steps:**
1. Login to Maximo mobile app with the credentials of technician assigned to the work order.
2. Click on "+" button on navigator and click on Create quick report.
3. Verify that technician can click on Create quick report and navigate to Create quick report page.
4. Add description, linear asset, location or other details in the quick report.
5. Click on save button.

**Result:**
-	Create quick report option should be displayed on the navigator and should navigate the technician to Create quick report page.
-	Work type should be automatically set as EM.
-	The priority of the workorder should be automatically set as ‘1’.
-	Quick report should be saved without any errors.


## Scenario 4 - Verify when user selecting  linear asset while editing work order on work order details page “linear segment details” section should be visible.

**Pre-condition:**
1. Login to maximo/manage application as Admin.
2. Create a work order.
3. Add Description, Long description, Priority, scheduled start and scheduled finish, Work type, Estimated duration, linear Asset and Location to the work order.
4. Add a planned Labor to the work order.
5. Save and approve the work order.

**Steps:**
1. Login to Maximo mobile app with Technician assigned to work order.
2. Click on "My Schedule" tile.
3. Search for the newly created Work order in WO list and click on it to open WO details.
4. Click on edit icon in work order details page open a new page.
5. Click on Asset lookup and select linear asset.

**Result:**
- User can see the blank “linear segment details” page



## Scenario 5 - Verify when user selecting  linear asset while editing work order on work order details page to non linear asset then “linear segment details” section should not be visible.

**Pre-condition:**
1. Login to maximo/manage application as Admin.
2. Create a work order.
3. Add Description, Long description, Priority, scheduled start and scheduled finish, Work type, Estimated duration, linear Asset and Location to the work order.
4. Add a planned Labor to the work order.
5. Save and approve the work order.

**Steps:**
1. Login to Maximo mobile app with Technician assigned to work order.
2. Click on "My Schedule" tile.
3. Search for the newly created Work order in WO list and click on it to open WO details.
4. Click on edit icon in work order details page open a new page.
5. Click on Asset lookup and select non linear asset.
 
**Result:**
- User can not see the “linear segment details” page

## Scenario 6 - Verify when user can submit blank “linear segment details” form

**Pre-condition:**
1. Login to maximo/manage application as Admin.
2. Create a work order.
3. Add Description, Long description, Priority, scheduled start and scheduled finish, Work type, Estimated duration,Non linear Asset and Location to the work order.
4. Add a planned Labor to the work order.
5. Save and approve the work order.

**Steps:**
1. Login to Maximo mobile app with Technician assigned to work order.
2. Click on "My Schedule" tile.
3. Search for the newly created Work order in WO list and click on it to open WO details.
4. Click on edit icon in work order details page open a new page.
5. Click om Asset lookup and select linear asset.
6. User submitting “linear segment details” form with all blank value

**Result:**
- User cansubmit the blank “linear segment details” page
- blank data  should be visible on work order details page
- “Linear” tag should be present on  work order details page under the Asset and location section


## Scenario 7 - Verify when user can submit “linear segment details” form with all fields value

**Pre-condition:**
1. Login to maximo/manage application as Admin.
2. Create a work order.
3. Add Description, Long description, Priority, scheduled start and scheduled finish, Work type, Estimated duration,Non linear Asset and Location to the work order.
4. Add a planned Labor to the work order.
5. Save and approve the work order.

**Steps:**
1. Login to Maximo mobile app with Technician assigned to work order.
2. Click on "My Schedule" tile.
3. Search for the newly created Work order in WO list and click on it to open WO details.
4. Click on edit icon in work order details page open a new page.
5. Click on Asset lookup and select linear asset.
6. User submitting “linear segment details” form with all fields  value

**Result:**
- User can submit the “linear segment details” form
- All field data  should be visible on work order details page


## Scenario 8 - Verify when user can submit “linear segment details” form with only Start and End section “Refrence point” fields value

**Pre-condition:**
1. Login to maximo/manage application as Admin.
2. Create a work order.
3. Add Description, Long description, Priority, scheduled start and scheduled finish, Work type, Estimated duration,Non linear Asset and Location to the work order.
4. Add a planned Labor to the work order.
5. Save and approve the work order.

**Steps:**
1. Login to Maximo mobile app with Technician assigned to work order.
2. Click on "My Schedule" tile.
3. Search for the newly created Work order in WO list and click on it to open WO details.
4. Click on edit icon in work order details page open a new page.
5. Click on Asset lookup and select linear asset.
6. User submitting “linear segment details” form with only Start and End section “Refrence point” fields value

**Result:**
- User can submit the “linear segment details” form
- only Start and End section “Refrence point” fields value should be visible on work order details page

## Scenario 9 - Verify user can submit “linear segment details” form with negative value in all “Offset filed value”

**Pre-condition:**
1. Login to maximo/manage application as Admin.
2. Create a work order.
3. Add Description, Long description, Priority, scheduled start and scheduled finish, Work type, Estimated duration,Non linear Asset and Location to the work order.
4. Add a planned Labor to the work order.
5. Save and approve the work order.

**Steps:**
1. Login to Maximo mobile app with Technician assigned to work order.
2. Click on "My Schedule" tile.
3. Search for the newly created Work order in WO list and click on it to open WO details.
4. Click on edit icon in work order details page open a new page.
5. Click on Asset lookup and select linear asset.
6. User submitting “linear segment details” form with negative value in all “Offset filed value”.
   Eg. Y-Offset:’-35’,Z-offset:’-55’,Refrence-Offset

**Result:**
- User can submit the “linear segment details” form with all negative value  
- All negative value should be visible when user again editing the same work order
- All negative fields value should be visible on  work order details page


## Scenario 10 - Verify UI of the cards/pages/views for fonts, font sizes, color, text completeness, alignment, positioning etc. is correct and as per design

**Steps:**

1. Login to Maximo mobile app with the Technician.
2. Click on "Technician" tile.
3. Click on nine dot navigation menu.
4. Click on the 'Create work button' on navigator page.
5. Navigate to newly created Work order page.
6. Verify UI of the new page for fonts, font sizes, font color, text completeness, alignment, positioning etc. is correct and as per design.

**Result:**

- UI of the cards/pages for fonts, font sizes, color, text completeness, alignment, positioning etc. should be correct and as per design.
- There should be no UI related issues.
- Smart Input version of the components should be used.

## Scenario 11 - Verify all the above test cases in online and offline/disconnected mode

**Pre-condition:**

Pre-conditions as specified for above mentioned test scenarios.

**Steps:**

Perform steps as specified for above mentioned test scenarios.

**Result:**

The application should behave as per expectations for all above mentioned scenarios in online and offline/disconnected mode on mobiles/tablets and other devices.
