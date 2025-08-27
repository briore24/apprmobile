# Enforce Asset Scan functionality on 'Work order details' page

These test cases will verify Enforce Asset Scan functionality on 'Work order details' page for Technician web app, online and offline mode on mobile containers.

These test cases will cover functionalities of following user stories:

- GRAPHITE-71179: Enforce Scanning before Start Work Order - Part 2



**Design URL:**

-Not applicable

**Note:**

- In case of web app, all transactions are saved in database and refreshed on the UI in real time.
- In case of online mode on mobile containers, transactions are sent to the database for saving instantly but UI is not refreshed from database until 'Check for Updates' button is clicked.
- In case of offline mode on mobile containers, transactions are queued for syncing to the database and synced to database once device is online. UI is not refreshed from database until 'Check for Updates' button is clicked.
- All server side error messages will be displayed in error page on devices post syncing with server and tapping on 'Check for Updates' button in both online and offline modes.

## Scenario 1 - Verify that work order status is changed to "In progress" when STARTTIMERINPRG=1 and technician taps on Start button on work order list and work order details pages -p1

**Pre-condition:**
1. Login with Admin credentials in Maximo classic/Manage.
2. Set "maximo.mobile.usetimer" system property value to 1.
3. Make sure "Automatically change work order status to INPRG when a user starts a labor timer?" in organization's System settings is checked/selected.
4. Go to work order tracking application.
5. Create multiple work orders.
6. Add assignments for labor and approve the work orders.
7. "Enforce Asset Scan" properties should be enabled.

**Steps:**

1. Launch the Maximo Mobile with assigned technician credentials.
2. Click on "My Schedule" tile to open the list of work orders.
3. Search the work order and tap on Start button on the work order card.
4. Verify the work order status once Start button is tapped on work order card.
5. Go to work order list page, search another work order and click chevron on the work order card to open work order details page.
6. Tap on Start button on the work order details page.
7. Verify the work order status once Start button is tapped on work order details page.
8. Work order status should change to "In progress" when “Enforce Asset Scan ” pop up Should be appear and user can scan the asset
**Results:**
- Work order status should change to "In progress" when STARTTIMERINPRG=1 and technician taps on Start button on work order list and work order details pages.


## Scenario 2 - Verify "Complete work" button on Report work page when technician clicks/taps on stop button from work order list and work order details pages -p1
**Pre-condition:**
1. Login with Admin credentials in Maximo classic/Manage.
2. Go to work order tracking application.
3. Create multiple work orders.
4. Add assignments for labor and approve the work orders.
5. Make sure "Automatically change work order status to INPRG when a user starts a labor timer?" in organization's System settings is checked/selected.
6. Make sure "Confirm Time Calculated by Timer?" in organization's System settings is un-checked/de-selected.
7. “Enforce Asset Scan" properties should be enabled

**Steps:**
1. Launch the Maximo Mobile with assigned technician credentials.
2. Click on "My Schedule" tile to open the list of work orders.
3. Search the work order.
4. Click/Tap on Start button on the work order card to start the labor transaction timer.
5. Barcode scanner should be appear and user can scan barcode and the workorder should be change status as “In Progress”
6. Start button changes to Pause and Stop buttons and the technician is navigated to work order details page. Work order status changes to In Progress.
7. Click/tap on Stop button from work order details pages or go back and click Stop button from work order list.
8. Technician is navigated to Report work page.
9. Verify "Complete work" button style on Report work page.
10. Click on “Complete work” button barcode
11. “Enforce Asset Scan ” pop up should be appear and user can scan barcode and the workorder should be change status as “Completed”

**Results:**
- workorder should be change status as “Completed”

## Scenario 3 - Validate available status options in change status drawer when work order status is "In progress" on WO list/details pages -p2
**Pre-condition:**
1. Login with Admin credentials in Maximo classic/Manage.
2. Go to work order tracking application.
3. Create a new work order.
4. Add assignments for labor and change the WO status to In progress.
5. “Enforce Asset Scan" properties should be enabled

**Steps:**
1. Launch the Maximo Mobile with work technician.
2. Click on "My Schedule" tile and open the list of work orders.
3. Search the work order and click to open WO details page.
4. Change Work Order status to in Progress.
5. “Enforce Asset Scan ” pop up should be appear user can scan the barcode and then work order status should be Changed as “In progress”
**Results:**
work order status should be Changed as “In progress”
## Scenario 4 - Verify status of work order and associated tasks when work order is flow controlled and tasks don't have predecessor -p2(WORKTYPE.STARTSTATUS is filled in)
**Pre-condition:**
1. Login with Admin credentials in Maximo classic/Manage.
2. Go to Organizations and open technician organization, click on "Work Order Options" and select "Work Type" option.
3. Filter the records for "Work Order Class" value as "WORKORDER".
4. Expand record with "Work Type" as "CM".
5. In "Process Flow" section, select "Start Status" field value as "APPR".
6. Select "Complete Status" field value as "COMP" and click OK button.
7. Go to work order tracking application.
8. Create a new work order with work type as "CM".
9. Select/Check the checkbox for "Under flow control?".
10. Create two planned tasks in the work order.
11. Add labor from assignments tab and approve the work order.
12. For technician organization in organizations app, make sure that "Automatically change work order status to INPRG when a user starts a labor timer?" is checked/selected in system settings OR maximo.mobile.usetimer is set to 0 in system properties.
13. “Enforce Asset Scan" properties should be enabled

 
**Steps:**
1. Launch the Maximo Mobile app and login with assigned technician credentials.
2. Click on "My Schedule" tile and open the list of work orders.
3. Search the work order created in Pre-condition and click on "Start work" button or change work order status to "In progress".
4. Login with Admin credentials in Maximo classic/Manage.
5. Verify that “Enforce Asset Scan ” pop up is coming and after scaning asset the work order status is updated to "In Progress"

**Results:**
- The parent work order is completed automatically once both tasks/task work orders are marked completed.

## Scenario 5 - Verify that “Enforce Asset Scan ” pop up Should not be visible after cliking on"Start travel" button -p1
**Pre-condition:**
1. Login with Admin credentials in Maximo classic/Manage.
2. Goto service address and create a new service address by adding the longitude and latitude.
3. Create a work order and associate the service address to it.
4. Add assignments for labor and approve the work order.
5. “Enforce Asset Scan" properties should be enabled

**Steps:**
1. Click on settings in the browser/device and turn the geo-location to ON.
2. Launch the Maximo Mobile application using technician credentials.
3. Grant permission to use location services on browser/devices.
4. Click on "My Schedule" tile to open the list of assigned work orders.
5. Verify that "Start travel" button is displayed on work order cards.
6. Click On “Start Travel” Button

**Result:**
- "Start travel" button with "Start travel" label should be displayed on webapp and large screen devices. "Start travel" button without "Start travel" label should be displayed on small/mid screen devices.
- Enforce Asset Scan window should not be Appeared

## Scenario 6 - Verify that When “Mandate Asset Scan” and “Enforce Asset Scan” Both are Selected Skip Button on “Enforce Asset Scan ” pop up should not be visible p2
**Pre-condition:**
1. Login with Admin credentials in Maximo classic/Manage.
2. Go to work order tracking application.
3. Create a new work order.
4. Add assignments for labor and change the WO status to In progress.
5. “Enforce Asset Scan" and “Mandate asset Scan” properties should be enabled

**Steps:**
1. Launch the Maximo Mobile with work technician.
2. Click on "My Schedule" tile and open the list of work orders.
3. Search the work order and click to open WO details page.
4. Change Work Order status to in Progress.
5. “Enforce Asset Scan ” pop up should be appear

**Results:**
- Skip Button Should not be visible
- user can scan the barcode and then work order status should be Changed as “In progress”

## Scenario 7 - Verify that When Enforce Asset Scan” is Selected and user Clicking on Skip button in “Enforce Asset Scan ” pop up, Worklog page should have “Full Date”, “User Name” and “Work order started without Scanning the asset number ” message p2

**Pre-condition:**
1. Login with Admin credentials in Maximo classic/Manage.
2. Go to work order tracking application.
3. Create a new work order.
4. Add assignments for labour and change the WO status to In progress.
5. “Enforce Asset Scan" and “Mandate asset Scan” properties should be enabled

**Steps:**
1. Launch the Maximo Mobile with work technician.
2. Click on "My Schedule" tile and open the list of work orders.
3. Search the work order and click to open WO details page.
4. Change Work Order status to in Progress.
5. “Enforce Asset Scan ” pop up should be appear
6. Click on Skip Button on Window

**Results:**
1. work order status should be Changed as “In progress”
2. Worklog page should have “Full Date”, “User Name” and “Work order started without Scanning the asset number ” message


## Scenario 8 - Verify that asset mismatched dialog should be prompted if scanned barcode number different then asset number  
**Pre-condition:**
1. Login with Admin credentials in Maximo classic/Manage.
2. Go to work order tracking application.
3. Create a new work order.
4. Add assignments for labor and change the WO status to In progress.
5. “Enforce Asset Scan" properties should be enabled

**Steps:**
1. Launch the Maximo Mobile with work technician.
2. Click on "My Schedule" tile and open the list of work orders.
3. Search the work order and click to open WO details page.
4. Change Work Order status to in Progress.
5. “Enforce Asset Scan ” pop up should be appear user can scan the barcode
6. Scanned barcode number which is different from current asset number

**Results:**
- Verify that asset mismatched dialog should be prompted if scanned barcode number different then asset number  and also user can rescan the new barcode number by clicking on scan button and If user click on cancel then previous dialog should be open

## Scenario 8 - Verify that user scanned with correct barcode and also when user try to scan asset again once user already scanned once

**Pre-condition:**

1. Login Classic site.
2. Go to asset and create a new asset.
3. Create a new work order and assign the created asset to work order.
4. Create an barcode for above created asset.

**Steps:**

1. Login the Technician app.
2. Click on created work order on work order list page.
3. Click on Asset scan icon.
4. Scan the barcode.
5. Barcode scanned successfully.
6. Click on scan touchpoint button try to scan again.

**Result:**

- Barcode should be match with asset which assign with work order and a toast message should be displayed "Asset Confirmed".(Result for steps 1-5)
- Barcode should be match with asset which assign with work order when user try to scan the again and a toast message should be displayed "Asset Confirmed".(Result for step 6)

## Scenario 9 - Verify the UI of the cards/pages/views for fonts, font sizes, font color, text completeness, alignment, positioning etc. is correct and as per design on mobile and other small screen devices for all supported form factors

**Pre-condition:**

1. Login Classic site.
2. Go to asset and create a new asset.
3. Create a new work order and assign the created asset to work order.
4. Create the barcode for asset.

**Steps:**

1. Login to techMobile app with the technician assigned to work order.
2. Navigate to screens/pages/cards of above mentioned scenarios.
3. Verify UI of the cards/pages for fonts, font sizes, font color, text completeness, alignment, positioning etc. is correct and as per design.

**Result:**

- The cards/pages for fonts, font sizes, font color, text completeness, alignment, positioning etc. are correct and as per design.
- The application should behave as per expectations for all above mentioned scenarios on mobile and other small screen devices for all supported form factors.
- The UI should be as per design for respective form factor.

## Scenario 10 - Verify the test cases from Serial number 1 to 20 in offline or disconnected mode

**Pre-condition:**

1. Login Classic site.
2. Go to asset and create a new asset.
3. Create a new work order and assign the created asset to work order.
4. Create the barcode for asset.

**Steps:**

1. Login to techMobile app with the technician assigned to work order.
2. Navigate and perform above mentioned scenarios in offline or disconnected mode on mobiles/tablets/desktops.

**Result:**

- The application should behave as per expectations for all above mentioned scenarios in offline or disconnected mode on mobiles/tablets/desktops.
