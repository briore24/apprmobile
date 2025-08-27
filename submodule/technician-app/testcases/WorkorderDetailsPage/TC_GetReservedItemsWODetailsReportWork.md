# Get reserved items and report as actuals functionality from "WO details" and "Report work" pages

These test cases will verify "Get reserved items" and report those items as actuals functionality from "Planned materials and tools" touch-point on "WO details" and "Materials used" section on "Report work" pages using Technician web app, online and offline mode on mobile containers.

These test cases will cover functionalities of following user stories:

- GRAPHITE-43251: Eli should be able to report Actual materials from Reservations
- GRAPHITE-18345: Eli must not be able to access pages and actions where he does not have permission

**Design URL:**

- <https://ibm.invisionapp.com/share/VEO1QAH4D79#/screens>

**Note:**

- In case of web app, all transactions are saved in database and refreshed on the UI in real time.
- In case of online mode on mobile containers, transactions are sent to the database for saving instantly but UI is not refreshed from database until 'Check for Updates' button is clicked.
- In case of offline mode on mobile containers, transactions are queued for syncing to the database and synced to database once device is online. UI is not refreshed from database until 'Check for Updates' button is clicked.
- All server side error messages will be displayed in error page on devices post syncing with server and tapping on 'Check for Updates' button in both online and offline modes.

## Scenario 1 - Verify contents of planned materials and tools sliding drawer on WO details page

**Pre-condition:**

1. Login to Maximo/Manage application as admin.
2. Create a work order.
3. Add planned items.
4. Add planned tools.
5. Assign labor/technician to work order in Assignments tab.
6. Approve the work order.

**Steps:**

1. Login to technician app with the technician assigned to work order.
2. Click on "My Schedule" tile.
3. Find the work order created in pre-condition steps in "Assigned Work" filter.
4. Click the chevron on the work order card to open WO details page.
5. Click on planned materials and tools touch-point to open "Materials and tools" sliding drawer.
6. Verify the contents of the planned materials and tools sliding drawer.

**Result:**

The contents of the planned materials and tools sliding drawer should be:

- Header title should be "Materials and tools".
- "Shopping cart"/"Request materials" and "Get reserved items" button should be displayed on extreme right of header or within a 3 dot menu.
- Both Materials and Tools sections should be displayed.
- Materials section should display all planned items and materials associated with the work order.
- Tools section should display all planned tools associated with the work order.

For each material, following fields should be displayed:

- Item ID: WPMATERIAL.ITEMNUM
- Description: WPMATERIAL.DESCRIPTION
- Quantity: WPMATERIAL.ITEMQTY (font size 20px)
- Storeroom: LOCATIONS.DESCRIPTION

For each tool, following fields should be displayed:

- Tool ID: WPTOOL.ITEMNUM
- Description: WPTOOL.DESCRIPTION
- Quantity: WPTOOL.ITEMQTY (font size 20px)
- Hours: WPTOOL.HOURS
- Storeroom: LOCATIONS.DESCRIPTION

**Note:**

Tool Hours and Storeroom description should be displayed in separate lines for each Tool. Even if, Tool Hours is 0, it should be displayed in Tools section.

Verify this scenario for various combinations of data like having

- Quantity as zero, small integer, decimal, big/huge quantity etc.
- Store, Material and/or Tool description as large text, special characters etc.
- Hours as integer, having minutes data, large/high hours.

## Scenario 2 - Verify unavailability of "Get reserved items" button when work order status is "Waiting for approval"

**Pre-condition:**

1. Login to Maximo/Manage application as admin.
2. Create a work order.
3. Add planned items with/without reservations.
4. Add planned tools with/without reservations.
5. Assign labor/technician to work order in Assignments tab.
6. Approve the work order.

**Steps:**

1. Login to technician app with the technician assigned to work order.
2. Click on "My Schedule" tile.
3. Find the work order created in pre-condition steps in "Assigned Work" filter.
4. Click the chevron on the work order card to open WO details page.
5. Change work order status to "Waiting for approval".
6. Click on planned materials and tools touch-point to open "Materials and tools" sliding drawer.
7. Verify "Get reserved items" button is unavailable on planned materials and tools sliding drawer.

**Result:**

"Get reserved items" button should be unavailable on planned materials and tools sliding drawer.

**Note:**

Verify this scenario for "Get reserved items" button on "Materials used" section on "Report work" page too.

## Scenario 3 - Verify availability of "Get reserved items" button when work order status is "Approved", "In progress" (or any synonyms)

**Pre-condition:**

1. Login to Maximo/Manage application as admin.
2. Create a work order.
3. Add planned items with/without reservations.
4. Add planned tools with/without reservations.
5. Assign labor/technician to work order in Assignments tab.
6. Approve the work order.

**Steps:**

1. Login to technician app with the technician assigned to work order.
2. Click on "My Schedule" tile.
3. Find the work order created in pre-condition steps in "Assigned Work" filter.
4. Click the chevron on the work order card to open WO details page.
5. Click on planned materials and tools touch-point to open "Materials and tools" sliding drawer.
6. Verify "Get reserved items" button is displayed on extreme right of header or within a 3 dot menu on planned materials and tools sliding drawer.
7. Close the planned materials and tools sliding drawer and change work order status to "In progress".
8. Click on planned materials and tools touch-point to open "Materials and tools" sliding drawer.
9. Verify "Get reserved items" button is displayed on extreme right of header or within a 3 dot menu on planned materials and tools sliding drawer.

**Result:**

"Get reserved items" button should be displayed on extreme right of header or within a 3 dot menu on planned materials and tools sliding drawer.

**Note:**

Verify this scenario for "Get reserved items" button on "Materials used" section on "Report work" page too.

## Scenario 4 - Verify contents of "Reserved items" page when technician clicks on "Get reserved items" button and no planned items are added or reserved. Also, verify when planned items are added or reserved

**Pre-condition:**

1. Login to Maximo/Manage application as admin.
2. Create a work order.
3. Add planned items without reservations or do not add any planned item.
4. Add planned tools with/without reservations.
5. Assign labor/technician to work order in Assignments tab.
6. Approve the work order.

**Steps:**

1. Login to technician app with the technician assigned to work order.
2. Click on "My Schedule" tile.
3. Find the work order created in pre-condition steps in "Assigned Work" filter.
4. Click the chevron on the work order card to open WO details page.
5. Click on planned materials and tools touch-point to open "Materials and tools" sliding drawer.
6. Click on "Get reserved items" button to open "Reserved items" page.
7. Verify the contents of the "Reserved items" page.

**Result:**

The contents of the "Reserved items" page should be(when planned items are not added or reserved):

- Header title should be "Reserved items" with back button.
- Work order description is displayed below the header.
- "No reserved items." message.

The contents of the "Reserved items" page should be(when planned items are added or reserved):

- Header title should be "Reserved items" with back button and disabled "Confirm Selection" button.
- Work order description is displayed below the header.
- Record for each reserved planned item with Item id, Item description, Storeroom, reserved quantity information and a checkbox to select the record.
- No reserved tool should be displayed.

**Note:**

Verify this scenario for "Get reserved items" button on "Materials used" section on "Report work" page too.

## Scenario 5 - Verify technician can select multiple rotating assets when clicked on chevron of rotating item

**Pre-condition:**

1. Login to Maximo/Manage application as admin.
2. Go to items and create a new item.
3. Click on the rotating checkbox.
4. Add items to storeroom.
5. Change status and click on the roll down changes .
6. Go to Assets now and click on create new asset.
7. Select the rotating item added from chevron and location will be added automatically.
8. Change status to Active.
9. Go to work order tracking.
10. Add Asset with location.
11. Go to plans and add the rotating item added along with quantity (eg. 5.00) and storeroom.
12. Add the labor from assignment .
13. Approve the WO .

**Steps:**

1. Login to technician app with the technician assigned to work order.
2. Click on "My Schedule" tile.
3. Find the work order created in pre-condition steps in "Assigned Work" filter.
4. Click the chevron on the work order card to open WO details page.
5. Click on planned materials and tools touch-point to open "Materials and tools" sliding drawer.
6. Click on "Get reserved items" button to open "Reserved items" page.
7. Verify that added rotating item should have:
   Regex :."Item name along with storeroom name beneath it with chevron present on the right.
8. Click on the chevron.
9. Verify technician can select one or more checkboxes for reserved item records.
10. Click on the Confirm Selection button .
11. Verify that rotating assets should be added on the report work page with quantity and ISSUE type with toaster message "Rotating items added"
12. Now again click on "Get reserved items".
13. Verify that quantity remaining is mentioned besides chevron (eg .2 out of 5 are selected)

**Result:**

- If there are no rotating items added , the "Get reserved page" should display "No rotating items found".
- Technician should be able to select one or more checkboxes for one or more rotating items on "Reserved items" page.
- It should display toast message "Reserved items added" when clicked on "Confirm selection" button.
- When technician clicks on Confirm Selection icon on items page, the spinner should rotate and immediately navigates to the report work page with quantity and ISSUE type . 
- The remaining quantity should be displayed besides chevron. (eg .2 out of 5 are selected)
- On the "Confirm selection" page , the rotating assets will be only be selected as per the quantity added on step no 11. otherwise toaster message will be displayed "Reported assets must be equal or smaller than reserved quant…"

**Note:**

Verify this scenario for "Get reserved items" button on "Materials used" section on "Report work" page too.

## Scenario 6 - Verify "Confirm Selection" button is enabled when only one or more non-rotating items are selected.

**Pre-condition:**

1. Login to Maximo/Manage application as admin.
2. Create a work order.
3. Add Asset with location.
4. Under plans , Add non- rotating item (eg. 560-00) with storeroom.
5. Add quantity .
6. Assign labor/technician to work order in Assignments tab.
7. Approve the work order.

**Steps:**

1. Login to technician app with the technician assigned to work order.
2. Click on "My Schedule" tile.
3. Find the work order created in pre-condition steps in "Assigned Work" filter.
4. Click the chevron on the work order card to open WO details page.
5. Click on planned materials and tools touch-point to open "Materials and tools" sliding drawer.
6. Click on "Get reserved items" button to open "Reserved items" page.
7. Select one or more non-rotating items.
8. Verify that confirm selection button is enabled.

**Result:*

"Confirm Selection" button should be only enabled when one or more non-rotating items are selected.

**Note:**

Verify this scenario for "Get reserved items" button on "Materials used" section on "Report work" page too.

## Scenario 7 - Verify all selected reserved items are reported as actuals in "Materials used" section on "Report work" page when technician clicks on "Confirm Selection" button

**Pre-condition:**

1. Login to Maximo/Manage application as admin.
2. Create a work order.
3. Add one or more planned items with reservations.
4. Assign labor/technician to work order in Assignments tab.
5. Approve the work order.

**Steps:**

1. Login to technician app with the technician assigned to work order.
2. Click on "My Schedule" tile.
3. Find the work order created in pre-condition steps in "Assigned Work" filter.
4. Click the chevron on the work order card to open WO details page.
5. Click on planned materials and tools touch-point to open "Materials and tools" sliding drawer.
6. Click on "Get reserved items" button to open "Reserved items" page.
7. Select checkbox for one or more reserved item records.
8. Click "Confirm Selectiond" button.
9. Verify all selected reserved items are reported as actuals in "Materials used" section on "Report work" page.

**Result:**

- Technician should be navigated to "Report work" page.
- Each selected reserved planned item record should be reported as actuals in "Materials used" section on "Report work" page.
- The planned reserved items details should match with the details of items reported as actuals in "Materials used" section on "Report work" page.

**Note:**

Verify this scenario for "Get reserved items" button on "Materials used" section on "Report work" page too.

## Scenario 8 - Verify that current balance of the respective items in inventory is reduced by reserved quantity when reserved items are reported as actuals

**Pre-condition:**

1. Login to Maximo/Manage application as admin.
2. Create a work order.
3. Add one or more planned items with reservations.
4. Assign labor/technician to work order in Assignments tab.
5. Approve the work order.

**Steps:**

1. Login to technician app with the technician assigned to work order.
2. Click on "My Schedule" tile.
3. Find the work order created in pre-condition steps in "Assigned Work" filter.
4. Click the chevron on the work order card to open WO details page.
5. Click on planned materials and tools touch-point to open "Materials and tools" sliding drawer.
6. Click on "Get reserved items" button to open "Reserved items" page.
7. Select checkbox for one or more reserved item records.
8. Click "Confirm Selection" button to report selected reserved items as actuals.
9. Verify that current balance of the respective items is reduced by reserved quantity in Maximo/Manage inventory app.

**Result:**

The current balance of the respective reserved planned items in inventory should be reduced by reserved quantity when reserved items are reported as actuals.

**Note:**

Verify this scenario for "Get reserved items" button on "Materials used" section on "Report work" page too.

## Scenario 9 - Verify technician is navigated back to the materials and tools sliding drawer when technician clicks on back chevron on "Reserved items" page

**Pre-condition:**

1. Login to Maximo/Manage application as admin.
2. Create a work order.
3. Add one or more planned items with reservations.
4. Assign labor/technician to work order in Assignments tab.
5. Approve the work order.

**Steps:**

1. Login to technician app with the technician assigned to work order.
2. Click on "My Schedule" tile.
3. Find the work order created in pre-condition steps in "Assigned Work" filter.
4. Click the chevron on the work order card to open WO details page.
5. Click on planned materials and tools touch-point to open "Materials and tools" sliding drawer.
6. Click on "Get reserved items" button to open "Reserved items" page.
7. Click back button on "Reserved items" page.

**Result:**

Technician should be navigated back to the materials and tools sliding drawer when technician clicks on back chevron on "Reserved items" page.

**Note:**

Verify that in this scenario technician should be navigated to "Report work" page when technician clicks back button on "Reserved items" page and "Reserved items" page was opened using "Get reserved items" button on "Report work" page.

## Scenario 10 - Verify that "Confirm Selection >" button under 3 dot menu in Materials used section on Report work page is either disabled or hidden when technician do not have permission for reporting reserved items as actuals

**Pre-condition:**

1. Login to Maximo classic application as admin.
2. Go to Users application and search for technician user.
3. Open technician user and go to Security groups tab.
4. "Actual reserv"(ACTUALRESERV) sig option in MXAPIWODETAIL Object Structure is enabled by default in TECHMOBILE security template. It can be verified using "Technician" tools and tasks in Applications tab for Technician security group.
5. Remove permission for "Actual reserv"(ACTUALRESERV) sig option in MXAPIWODETAIL Object Structure from each of the assigned security group to the technician user.
6. Create a new work order, add assignments for labor/technician and approve the work order.
7. Log off all users assigned to modified security groups or restart the maximo server.

**Steps:**

1. Login to Maximo mobile app with the technician assigned to work order.
2. Click on 'My Schedule' tile.
3. Search the work order and click on chevron on work order card to open the work order details page.
4. Click on report work touch point to open 'Report work' page.
5. Verify that "Confirm Selection" button under 3 dot menu in Materials used section on Report work page is either disabled or hidden.

**Results:**

The "Confirm Selection" button under 3 dot menu in Materials used section on Report work page should be either disabled or hidden when technician do not have permission for reporting reserved items as actuals.

**Note:**

Please make sure to revert the permission to allowed once testing is completed.

## Scenario 11 - Verify that "Confirm Selection >" button under 3 dot menu in planned materials and tools drawer on WO details page is either disabled or hidden when technician do not have permission for reporting reserved items as actuals

**Pre-condition:**

1. Login to Maximo classic application as admin.
2. Go to Users application and search for technician user.
3. Open technician user and go to Security groups tab.
4. "Actual reserv"(ACTUALRESERV) sig option in MXAPIWODETAIL Object Structure is enabled by default in TECHMOBILE security template. It can be verified using "Technician" tools and tasks in Applications tab for Technician security group.
5. Remove permission for "Actual reserv"(ACTUALRESERV) sig option in MXAPIWODETAIL Object Structure from each of the assigned security group to the technician user.
6. Create a new work order, add assignments for labor/technician and approve the work order.
7. Log off all users assigned to modified security groups or restart the maximo server.

**Steps:**

1. Login to Maximo mobile app with the technician assigned to work order.
2. Click on 'My Schedule' tile.
3. Search the work order and click on chevron on work order card to open the work order details page.
4. Click on planned materials and tools touch point to open 'Materials and tools' sliding drawer.
5. Verify that "Confirm Selection >" button under 3 dot menu in planned materials and tools drawer on WO details page is either disabled or hidden.

**Results:**

The "Confirm Selection >" button under 3 dot menu in planned materials and tools drawer on WO details page should be either disabled or hidden when technician do not have permission for reporting reserved items as actuals.

**Note:**

Please make sure to revert the permission to allowed once testing is completed.

## Scenario 12 - Verify contents of Non-Rotating items.

**Pre-condition:**

1. Login to Maximo/Manage application as admin.
2. Create a work order.
3. Add Asset with location.
4. Under plans , Add non- rotating item (eg. 560-00) with storeroom.
5. Add quantity .
6. Assign labor/technician to work order in Assignments tab.
7. Approve the work order.

**Steps:**

1. Login to Maximo mobile app with the technician assigned to work order.
2. Click on 'My Schedule' tile.
3. Search the work order and click on chevron on work order card to open the work order details page.
4. Go to report work page >> materials used section .
5. Click on 3 dots and go to " Get reserved items".
6. Verify UI content .

**Results:**

- The non-rotating row should be displayed first with header "Non-rotating items"
- It should be displayed with non-rotating item value name and beneath it should be storeroom name mentioned.
- The quantity added should be displayed in the right side corner with + and - icons which can be used to increase/decreas the quantity.
- Besides the quantity the checkbox should be present to add the values .
- when no non-rotating item is added , then it should display "No non-rotating items found".
- "Reserved items added" toaster message is displayed when non-rotating item are added.

## Scenario 13 - Verify single/multiple non-Rotating items can be added .

**Pre-condition:**

1. Login to Maximo/Manage application as admin.
2. Create a work order.
3. Add Asset with location.
4. Under plans , Add non- rotating item (eg. 560-00) with storeroom.
5. Add quantity .
6. Assign labor/technician to work order in Assignments tab.
7. Approve the work order.

**Steps:**

1. Login to Maximo mobile app with the technician assigned to work order.
2. Click on 'My Schedule' tile.
3. Search the work order and click on chevron on work order card to open the work order details page.
4. Go to report work page >> materials used section .
5. Click on 3 dots and go to " Get reserved items".
6. Click on the non-rotating items checkbox .
7. Verify the values are added on report work page with quantity mentioned and with ISSUE type mentioned.

**Results:**

- The user can select single or multiple non-rotating values and it should be added with quantity value on the report work page.

## Scenario 14 - Verify increased/decreased quantity of the non-rotating item should be added to the report work page

**Pre-condition:**

1. Login to Maximo/Manage application as admin.
2. Create a work order.
3. Add Asset with location.
4. Under plans , Add non- rotating item (eg. 560-00) with storeroom.
5. Add quantity .
6. Assign labor/technician to work order in Assignments tab.
7. Approve the work order.

**Steps:**

1. Login to Maximo mobile app with the technician assigned to work order.
2. Click on 'My Schedule' tile.
3. Search the work order and click on chevron on work order card to open the work order details page.
4. Go to report work page >> materials used section .
5. Click on 3 dots and go to " Get reserved items".
6. Increase/decrease the quantity.
7. Click on the checkbox.
8. Verify that the non-rotating items should be added when the quantity is increased/decreased.

**Results:**

- The quantity when increased/decreased should be added accordingly on the report work page.
- The decreased quantity should be displayed when the Get reserved page is opened again from the actual quantity.

## Scenario 15 - Verify the UI of the cards/pages/views for fonts, font sizes, font color, text completeness, alignment, positioning etc. is correct and as per design on mobile and other small screen devices for all supported form factors

**Pre-condition:**

Pre-conditions as specified for above mentioned test scenarios.

**Steps:**

1. Perform steps as specified for above mentioned test scenarios.
2. Navigate and perform above mentioned test scenarios on mobile and other small screen devices for all supported form factors.

**Result:**

- The cards/pages for fonts, font sizes, font color, text completeness, alignment, positioning etc. are correct and as per design.
- The application should behave as per expectations for all above mentioned test scenarios on mobile and other small screen devices for all supported form factors.
- The UI should be as per design for respective form factor.

## Scenario 16 - Verify all the above test scenarios on mobile devices in online and offline mode

**Pre-condition:**

Pre-conditions as specified for above mentioned test scenarios.

**Steps:**

Perform steps as specified for above mentioned test scenarios.

**Results:**

The application should behave as per expectations for all above mentioned test scenarios on mobile devices in online and offline mode.
