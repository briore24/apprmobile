# Edit work order details functionality on 'Work order details' page

These test cases will verify (FSM) Allow technicians to assign/reassign work functionality on 'Work order details' page and work order list page on Technician web app, online and offline mode on mobile containers and the below mentioned test cases will cover the functionalities of following stories.

- MASR-1768: (FSM) Allow technicians to assign/reassign work


**Design URL:**

- <https://www.figma.com/design/RAPUxZUIzaPZonCgW1udeP/Technician-%26-Assets?m=auto&node-id=28124-202519&t=dIrHF7I7gSsMb3Rs-1>


**Note:**

- In case of web app, all transactions are saved in database and refreshed on the UI in real time.
- In case of online mode on mobile containers, transactions are sent to the database for saving instantly but UI is not refreshed from database until 'Check for Updates' button is clicked.
- In case of offline mode on mobile containers, transactions are queued for syncing to the database and synced to database once device is online. UI is not refreshed from database until 'Check for Updates' button is clicked.
- All server side error messages will be displayed in error page on devices post syncing with server and tapping on 'Check for Updates' button in both online and offline modes.

## Scenario 1 - Verify availability of Assign/Reassign icon button on work order details page and verify that clicking on the Assign/Reassign icon on work order details page new PopUp Should open with Unassigned button and Reassign Button

**Pre-condition:**

1. Login to maximo/manage application as Admin and Navigate Security Group>Search technician>Object structure>Mxapiwodetail>Allow work order reassignment Should be enabled.
2. Create a work order.
3. Add description, Long description, Priority, scheduled start and scheduled finish, Work type, Estimated duration,Asset and location to the work order.
4. Add a planned Labor to the work order.
5. Save the work order.
6. Approve the work order.

**Steps:**

1. Login to Maximo mobile app with the technician assigned to work order.
2. Click on "My Schedule" tile.
3.  Search for the newly created Work order in WO list and click on it to open WO details.
4. Verify the availability of Assign/Reassign button on Work order details page.
5. Click on Assign/Reassign Button it should open a popup with two buttons first Unassigned button and second Reassign Button.


**Result:**

- Popup should open with Unassign button and Reassign Button


## Scenario 2 - Verify the User should be able to Unassigned labor by clicking on Unassigned Button On popup

**Pre-condition:**

1. Login to maximo/manage application as Admin and Navigate Security Group>Search technician>Object structure>Mxapiwodetail>Allow work order reassignment Should be enabled.
2. Create a work order.
3. Add description, Long description, Priority, scheduled start and scheduled finish, Work type, Estimated duration,Asset and location to the work order.
4. Add a planned Labor to the work order.
5. Save the work order.
6. Approve the work order.

**Steps:**

1. Login to Maximo mobile app with the technician assigned to work order.
2. Click on "My Schedule" tile.
3.  Search for the newly created Work order in WO list and click on it to open WO details.
4. Verify the availability of Assign/Reassign button on Work order details page.
5. Click on Assign/Reassign Button Assign/Reassign popup should open with Unassigned button and Reassign Button.
6. Click on Unassigned button.
7. Unassign Work(Select the Reason code) side drawer code should be open.
8. Select any reason in radio button and click on blue tick button.
9. User should get toast message "work order 'XXXX' was unassigned and will no longer appear in your work list"


**Result:**
- Work order should be unassigned the labor and user should navigate to work order list page

## Scenario 3 - Verify the User should be able to Reassigned labor by clicking on reassign Button On popup

**Pre-condition:**

1. Login to maximo/manage application as Admin and Navigate Security Group>Search technician>Object structure>Mxapiwodetail>Allow work order reassignment Should be enabled.
2. Create a work order.
3. Add description, Long description, Priority, scheduled start and scheduled finish, Work type, Estimated duration,Asset and location to the work order.
4. Add a planned Labor to the work order.
5. Save the work order.
6. Approve the work order.

**Steps:**

1. Login to Maximo mobile app with the technician assigned to work order.
2. Click on "My Schedule" tile.
3.  Search for the newly created Work order in WO list and click on it to open WO details.
4. Verify the availability of Assign/Reassign button on Work order details page.
5. Click on Assign/Reassign Button Assign/Reassign popup should open with Unassigned button and Reassign Button.
6. Click on Reassigned button.
7. Reassign assignment sliding drawer should be open.
8. User should be able to select new labor and click on blue tick button

**Result:**

- Work order should be Reassigned the labor and user should navigate to work order list page with "Work order to reassigned to Wilson" message

## Scenario 4 - Verify the User should not be able to Unassigned labor by clicking on Unassign Button if work order has been started

***Pre-condition:**

1. Login to maximo/manage application as Admin and Navigate Security Group>Search technician>Object structure>Mxapiwodetail>Allow work order reassignment Should be enabled.
2. Create a work order.
3. Add description, Long description, Priority, scheduled start and scheduled finish, Work type, Estimated duration,Asset and location to the work order.
4. Add a planned Labor to the work order.
5. Save the work order.
6. Approve the work order.

**Steps:**

1. Login to Maximo mobile app with the technician assigned to work order.
2. Click on "My Schedule" tile.
3.  Search for the newly created Work order in WO list and click on it to open WO details.
4. Verify the availability of Assign/Reassign button on Work order details page and click on start work order button.
5. Click on Assign/Reassign Button Assign/Reassign popup should open with Unassigned button and Reassign Button.
6. Click on Unassigned button.

**Result:**
-Work order should not be Unassigned the labor and get the message "Work order cannot be unassigned if its started" 

## Scenario 5 - Verify if user is Rejecting work order and FSM flow and Allow work order reassignment both the Enable User should not get reject assignment popup on screen when user clicking on reject button

**Pre-condition:**

1. Login to maximo/manage application as Admin and Navigate Security Group>Search technician>Object structure>Mxapiwodetail>Allow work order reassignment Should be enabled.
2. Create a work order.
3. Add description, Long description, Priority, scheduled start and scheduled finish, Work type, Estimated duration,Asset and location to the work order.
4. Add a planned Labor to the work order.
5. Save the work order.
6. Approve the work order.

**Steps:**

1. Login to Maximo mobile app with the technician assigned to work order.
2. Click on "My Schedule" tile.
3.  Search for the newly created Work order in WO list and click on it to open WO details.
4. Accept/Reject Button should be visible in Work order list page.
5. User should be able to click on Reject Button.

**Result:**
- User should not get reject assignment popup on screen. 

## Scenario 6 - Verify UI of the cards/pages/views for fonts, font sizes, color, text completeness, alignment, positioning etc. is correct and as per design

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

## Scenario 7 - Verify all the above test cases in online and offline/disconnected mode

**Pre-condition:**

Pre-conditions as specified for above mentioned test scenarios.

**Steps:**

Perform steps as specified for above mentioned test scenarios.

**Result:**

The application should behave as per expectations for all above mentioned scenarios in online and offline/disconnected mode on mobiles/tablets and other devices.



