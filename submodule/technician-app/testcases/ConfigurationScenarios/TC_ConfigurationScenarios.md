# All configuration related scenarios (app.xml/developer machine)

These test cases will verify the easy configuration of tables, Complete work button status on Report work page using app.xml for Technician web app, online and offline mode on mobile containers. These test cases will be tested on developer machine only and will not be deployed to QA/Production servers.

These test cases will cover functionalities of following user stories:
- GRAPHITE-32745: Moe can configure Report work page
- GRAPHITE-35180: [Split] Moe can configure Report work page

## Scenario 1 - Verify that 'Save' button is enabled only when all the required fields contain data (developer machine only)

**Note:**

- There are no required fields in create work order page, So the 'Save' will be enabled by default, However, if a client specified any field as required then that should be configured in the app.xml, and if that field doesn't have data, then the Save button should be disabled.
- To verify the above scenario define any attribute as required from the database or via the app.xml in developer machine and verify whether the save is only enabled when the required field is filled/entered.
- In case of smart input component, required would be manageable from database level only so here we can't make fields required from the app.xml.

**Steps:**

1. Login to Maximo mobile app with the Technician.
2. Click on "Technician" tile.
3. Click on nine dot navigation menu.
4. Click on the 'Create work button' on navigator page.
5. Navigate to newly created Work order page.
6. Verify the new WO attributes defined as required field in app.xml are displayed in create work order page with a blue dot for required field.
7. Enter any value in the new required fields.
8. Verify the 'Save' button is enabled only when the required field is filled.

**Result:**

'Save' button should be only enabled when all the required fields in the create work order contain data.

## Scenario 2 - Verify all the OOTB work order attributes that are defined in the app.xml for the details page are displayed in editable mode

**Note:**

Description, Long description, Priority, Scheduled start and Scheduled finish, Work type, Estimated duration, Asset and Location are the Work order OOTB attributes defined in the app.xml and are displayed in editable format in UI

**Steps:**

1. Login to Maximo mobile app with the Technician.
2. Click on "Technician" tile.
3. Click on nine dot navigation menu.
4. Click on the 'Create work button' on navigator page.
5. Navigate to newly created Work order page.
6. Verify all the work order attributes that are defined in the app.xml for the create work order page are displayed in editable mode.

**Result:**

The UI of the new page should show Description, Long description, Priority, Scheduled start and Scheduled finish, Work type, Estimated duration, Asset and Location attributes in editable mode.

## Scenario 3 - In developer local machine verify non OOTB WO attributes can easily be added to app.xml and verify if those attributes are displayed in editable mode in create work page

**Note:**

- Ask developer to add one or two new work order attributes in app.xml in developer machine
- Not applicable for QA server

**Steps:**

1. Login to Maximo mobile app with the Technician.
2. Click on "Technician" tile.
3. Click on nine dot navigation menu.
4. Click on the 'Create work button' on navigator page.
5. Navigate to newly created Work order page.
6. Verify all the newly added work order attributes in the app.xml are displayed in editable mode in create work order page.

**Result:**

The UI of the new page should display newly added attributes in editable mode.

## Scenario 4 - Verify each attributes defined in app.xml has a unique way to enter/change/edit the respective values

**Steps:**

1. Login to Maximo mobile app with the Technician.
2. Click on "Technician" tile.
3. Click on nine dot navigation menu.
4. Click on the 'Create work button' on navigator page.
5. Navigate to newly created Work order page.
6. The each attributes defined in the app.xml should be displayed in editable mode with unique way of editing the field in the new page.

**Result:**

The below mentioned attributes can be added with following ways:

- For adding WORKORDER.DESCRIPTION, User can enter a work order description should only be allowed to enter 100 characters.
- For adding WORKORDER.LONGDESCRIPTION, User can open the long description Rich text component and enter the long description and save the value and same value will reflect in the Long description field when clicked on back arrow.
- Text content entered in different styles in long description Rich text component should also be reflected in same style when viewed in long description field.
- For adding WORKORDER.WORKTYPE, User can select a work type from the work type lookup.
- For adding WORKORDER.WOPRIORITY, User can enter/specify a work order priority between 0 and 999 or leave the field blank.
- For adding WORKORDER.SCHEDSTART, User can specify the date/time with a date picker input field, It cannot be after the Scheduled finish.
- For adding WORKORDER.SCHEDFINISH, User can specify the date/time with a date picker input field, It cannot be before the Scheduled start.
- For adding WORKORDER.ESTDUR. User can enter the estimated duration.
- For adding WORKORDER.ASSETNUM. User can enter the asset number as well as use the asset search, parts identifier and bar/QR code scanning.
- For adding WORKORDER.LOCATION. User can enter the location number as well as use the location search and bar/QR code scanning.

## Scenario 5 - Verify that technician/client can change the attachments upload location (Developer machine only)

**Pre-condition:**

1. Open the file app.xml and change the attachments folder location from "Attachments" to the new location folder and save it. Deploy the application.
2. Login with Admin credentials in Maximo classic/Manage.
3. Go to work order tracking application.
4. Create a new work order.
5. Add assignments for labor and approve the WO.

**Steps:**

1. Launch the Maximo Mobile with work technician.
2. Click on the "My Schedule" to go to WO list page.
3. Search the work order and click chevron to open WO details page.
4. Click on "Attachments" tile/list item to go to "Attachments" page.
5. Click on the '+' icon present on top right.
6. Browse and select image/photo/video/document to attach.
7. Check the new added attachment in the Attachments page.

**Results:**

New attached file shouldn't be shown in "Attachments" folder but should be shown in the updated attachments upload location.

## Scenario 6 - In developer local machine verify non default OOTB behavior with maximum limit set to 10 or more in the application side to display those many work order records in "Asset and location history" page (dev machine only)

**Note:**

- Ask developer to set maximum limit as 10 in the application side to display those many work order records.

**Pre-condition:**

1. Create an asset and location in maximo classic.
2. Create a work order in maximo classic.
3. Associate the created asset and location with the work order.
4. Add planned labor or add assignments for labor in work order.
5. Approve the work order.
6. Create ten other work orders and associate the above created asset and location to those work orders.Any one  or more work order/s should be closed or completed.

**Steps:**

1. Login to Maximo mobile app with the technician.
2. Click on "My Schedule" tile.
3. Find the work order created in Pre-requisite step 2 in WO list and click it to open WO details.
4. Go to the Asset and location widget in Work order details page.
5. Click on "Asset and location history" touchpoint in Asset and location widget to launch "Asset and location history" page.
6. Verify that all the ten work orders (excluding the current one) are displayed in asset/location section.

**Result:**

All ten work orders (excluding the current one) should be displayed in asset sections.

## Scenario 7 - Verify all the OOTB work order attributes that are defined in the app.xml for the details page are displayed in editable mode

**Note:**

-Description, Long description, Priority, scheduled start and scheduled finish, Work type, Estimated duration, Asset and Location are the Work order OOTB attributes defined in the app.xml and are displayed in editable format in UI

**Pre-condition:**

1. Login to maximo/manage application as Admin.
2. Create a work order.
3. Add description, Long description, Priority, scheduled start and scheduled finish, Work type, Estimated duration to the work order.
4. Add a planned Labor (Eli) to the work order.
5. Save the work order.
6. Approve the work order.

**Steps:**

1. Login to Maximo mobile app with Technician assigned to work order.
2. Click on "My Schedule" tile.
3. Search for the newly created Work order in WO list and click on it to open WO details.
4. Click on edit icon in work order details page open a new page.
5. Verify all the work order attributes that are defined in the app.xml for the details page are displayed in editable mode.

## Scenario 8 - In developer local machine verify non OOTB WO attributes can easily be added to app.xml and verify if those attributes are displayed in editable mode in work order details page

**Note:**

- Ask developer to add one or two new work order attributes in app.xml in developer machine
- Not applicable for QA server

**Pre-condition:**

1. Login to maximo/manage application as Admin.
2. Create a work order.
3. Add Description, Long description, Priority, scheduled start and scheduled finish, Work type, Estimated duration, Asset and Location to the work order.
4. Add a planned Labor (Eli) to the work order.
5. Save the work order.
6. Approve the work order.

**Steps:**

1. Login to Maximo mobile app with Technician assigned to work order.
2. Click on "My Schedule" tile.
3. Search for the newly created Work order in WO list and click on it to open WO details.
4. Click on edit icon in work order details page open a new page.
5. Verify all the newly added work order attributes in the app.xml are displayed in editable mode in details page.

**Result:**

The UI of the new page should display newly added attributes in editable mode in the Overview widget.

## Scenario 9 - Verify all the attributes defined in app.xml are displayed with there predefined values i.e. values defined during the work order creation

**Pre-condition:**

1. Login to maximo/manage application as Admin.
2. Create a work order.
3. Add Description, Long description, Priority, scheduled start and scheduled finish, Work type, Estimated duration, Asset and Location to the work order.
4. Add a planned Labor (Eli) to the work order.
5. Save the work order.
6. Approve the work order.

**Steps:**

1. Login to Maximo mobile app with Technician assigned to work order.
2. Click on "My Schedule" tile.
3. Search for the newly created Work order in WO list and click on it to open WO details.
4. Click on edit icon in work order details page open a new page.
5. The work orders attributes defined in the app.xml should be displayed in editable mode in the new page.
6. Verify the attributes default values are displayed in the attribute fields i.e. values defined during the work order creation.

**Result:**

The UI of the new page should show Description, Long description, Priority, scheduled start and scheduled finish, Work type, Estimated duration, Asset and Location attributes displayed with the pre-populated values defined during the work order creation.

## Scenario 10 - Verify the work order attributes displayed with no predefined values are shown with blank spaces in the new page, Verify technician can enter/edit the attributes fields with no predefined values

**Pre-condition:**

1. Login to maximo/manage application as Admin.
2. Create a work order.
3. Add Description, Long description, Priority, scheduled start and scheduled finish, Work type, Estimated duration, Asset and Location to the work order.
4. Add a planned Labor (Eli) to the work order.
5. Save the work order.
6. Approve the work order.

**Steps:**

1. Login to Maximo mobile app with Technician assigned to work order.
2. Click on "My Schedule" tile.
3. Search for the newly created Work order in WO list and click on it to open WO details.
4. Click on edit icon in work order details page open a new page.
5. The work orders attributes defined in the app.xml should be displayed in editable mode in the new page.
6. Verify the attributes with no predefined values are displayed as empty/blank in the attribute fields.
7. Verify Technician can enter/edit the attributes fields with no predefined values.

**Result:**

- The UI of the new page should show Description, Long description, Priority, scheduled start and scheduled finish, Work type, Estimated duration, Asset and Location attributes.
- The attribute fields show as empty/blank values if the values for those attributes are not predefined.
- Technician should be able to enter/edit the attributes fields with no predefined values.

## Scenario 11 - Verify each attributes defined in app.xml has a unique way to enter/change/edit the respective values

**Pre-condition:**

1. Login to maximo/manage application as Admin.
2. Create a work order.
3. Add Description, Long description, Priority, scheduled start and scheduled finish, Work type, Estimated duration, Asset and Location to the work order.
4. Add a planned Labor (Eli) to the work order.
5. Save the work order.
6. Approve the work order.

**Steps:**

1. Login to Maximo mobile app with Technician assigned to work order.
2. Click on "My Schedule" tile.
3. Search for the newly created Work order in WO list and click on it to open WO details.
4. Click on edit icon in work order details page open a new page.
5. The each attributes defined in the app.xml should be displayed in editable mode with unique way of editing the field in the new page.

**Result:**

Verify the below attributes can be edited with following ways.

- For editing WORKORDER.DESCRIPTION, User can enter a work order description should only be allowed to enter 100 characters.
- For editing WORKORDER.LONGDESCRIPTION, User can open the long description Rich text component and enter the long description and save the value and same value will reflect in the Long description field when clicked on back arrow.
- Verify text content entered in different styles in long description Rich text component should also be reflected in same style when viewed in long description field.
- For editing WORKORDER.WORKTYPE, User can select a work type from the work type lookup.
- For editing WORKORDER.WOPRIORITY, User can enter/specify a work order priority between 0 and 999 or leave the field blank.
- For editing WORKORDER.SCHEDSTART, User can specify the date/time with a date picker input field, It cannot be after the scheduled finish.
- For editing WORKORDER.SCHEDFINISH, User can specify the date/time with a date picker input field, It cannot be before the scheduled start.
- For editing WORKORDER.ESTDUR. User can enter the estimated duration.
- For editing WORKORDER.ASSETNUM. User can enter the asset number as well as use the asset search, parts identifier and bar/QR code scanning.
- For editing WORKORDER.LOCATION. User can enter the location number as well as use the location search and bar/QR code scanning.

## Scenario 12 - Verify that 'Save' button is enabled only when all the required fields contain data (developer machine only)

**Note:**

- There are no required fields in WO edit page, So the 'Save' will be enabled by default, However, if a client specified any field as required then that should be configured in the app.xml, and if that field doesn't have data, then the Save button should be disabled.
- To verify the above scenario define any attribute as required from the database or via the app.xml in developer machine and verify whether the save is only enabled when the required field is filled/entered.
- In case of smart input component, required would be manageable from database level only so here we can't make fields required from the app.xml.

**Pre-condition:**

1. Login to maximo/manage application as Admin.
2. Create a work order.
3. Add a planned Labor (Eli) to the work order.
4. Save the work order.
5. Approve the work order.

**Steps:**

1. Login to Maximo mobile app with Technician assigned to work order.
2. Click on "My Schedule" tile.
3. Search for the newly created Work order in WO list and click on it to open WO details.
4. Click on edit icon in work order details page to open a new page where user can edit the WO attributes.
5. Verify the new WO attributes defined as required field in app.xml are displayed in WO edit page with a blue dot for required field.
6. Enter any value in the new required fields.
7. Verify the 'Save' button is enabled only when the required field is filled.

**Result:**

'Save' button should be only enabled when all the required fields in the WO edit page contain data.

## Scenario 13 - Verify "Get reserved items" button is not available on Planned materials and tools drawer if newly created work order is not synced with Maximo server (Applicable for mobile devices only in offline mode)

**Steps:**

1. Login to technician app with technician credentials on device.
2. Click on "My Schedule" tile.
3. Make device offline by enabling airplane mode on device.
4. Click on "+" button on navigator to open create work order page.
5. Provide description, priority and click save to create a new work order.
6. Open the newly created work order from "Work created locally/on device" view.
7. Click on planned materials and tools touch-point to open "Materials and tools" sliding drawer.
8. Verify that "Get reserved items" button is not displayed.

**Result:**

"Get reserved items" button should not be displayed on planned materials and tools drawer as newly created work order is not synced with Maximo server yet.

## Scenario 14 - Verify shopping cart button is not available on Planned materials and tools drawer if newly created work order is not synced with Maximo server (Applicable for mobile devices only in offline mode)

**Pre-condition:**

1. Login to maximo/manage application as admin.
2. Create a work order.
3. Add planned items/materials and/or tools to work order.
4. Assign labor/technician to work order in Assignments tab.
5. Approve the work order.
6. Create an item in item master, activate and add to a storeroom.

**Steps:**

1. Login to technician app with technician credentials on device.
2. Click on "My Schedule" tile.
3. Make device offline by enabling airplane mode on device.
4. Click on "+" button on navigator to open create work order page.
5. Provide description, priority and click save to create a new work order.
6. Open the newly created work order from "Work created locally" view.
7. Click on planned materials and tools touch-point to open "Materials and tools" sliding drawer.
8. Verify that shopping cart button is not displayed.

**Result:**

Shopping cart button should not be displayed on Planned materials and tools drawer as newly created work order is not synced with Maximo server yet.

## Scenario 15 - Verify that WOCHANGESTATUS.ASOFDATE is set to the current date/time when technician changes the WO status

**Pre-condition:**

1. Login with Admin credentials in Maximo classic/Manage.
2. Go to work order tracking application.
3. Create a new work order.
4. Add assignments for labor and approve the WO.

**Steps:**

1. Launch the Maximo Mobile with work technician.
2. Click on "My Schedule" tile and open the list of work orders.
3. Search the work order and click to open WO details page.
4. Click the gray action tag besides the priority action tag to open the WO status drawer.
5. Select any new status option.
6. Enter comment in "Add a comment" field and click Done button.
7. Check the column "WOCHANGESTATUS.ASOFDATE" in DB.

**Results:**

WOCHANGESTATUS.ASOFDATE should be set to the current date/time when technician changes the WO status.

## Scenario 16 - Verify that app.xml can be configured so that labor transactions for only logged in labor/technician are displayed(Applicable for developer machine only)

**Pre-condition:**

1. Configure app.xml or set flag so that so that labor transactions for only logged in labor/technician are displayed, build and deploy the local development server.
2. Login with Admin credentials in Maximo classic/Manage of local server.
3. Go to work order tracking application.
4. Create a new work order.
5. Add assignments for multiple labors and approve the WO.
6. Add actual labor time records for multiple labors.

**Steps:**

1. Launch the Maximo Mobile Technician app and login with assigned Technician/Labor.
2. Click on "My schedule" tile and open the list of work orders.
3. Search the work order and click to open wo details page.
4. Click on Report work touch-point to open Report work page.
5. Verify that labor transactions for only logged in labor/technician are displayed in Labor section.

**Results:**

- All reported labor transactions for only logged in labor/technician are displayed in Labor section.
- There shouldn't be Labor/Person display name header.

## Scenario 17 - Verify technician can select the task from the lookup, if displayed (When app.xml is configured to display tasks)

**Pre-condition:**

1. Login to maximo/manage application as admin.
2. Create a work order and add planned tasks.
3. Add tool to storeroom with different bins.
4. Add assignments for labor.
5. Approve the work order.

**Steps:**

1. Open the technician-app and login as Eli.
2. Click on the "My Schedule" to open WO list page.
3. Click on any work order to open the work order details page.
4. Click on report work touch point to open report work page.
5. Click on '+' icon to open "Add tool" sliding drawer.
6. Select the tool.
7. Select the storeroom and select bin.
8. Enter the value of Quantity.
9. Enter the value of Hours.

**Result:**

Verify after technician enters the Quantity and Hours, technician should be able to select the task from the lookup, displaying task number and their description.

## Scenario 18 - Verify technician can make asset and location fields mandatory from app.xml

**Pre-condition:**

1. Login to maximo/manage application as admin.
2. Create asset and location.
3. Add assignments for labor.
4. Approve the work order.
5. Make ASSET input field or LOCATION input field mandatory by adding  in app.xml in technician app in MAF and publish.

**Steps:**

1. Open the technician-app.
2. Click on the + icon to create a new work order.
3. Try to save without entering any details in asset and location.
4. Validate the error message.
5. Add asset and location in the fields and click on save.
6. Repeat same stpes on edit work order as well. 

**Result:**

- There should be an error message displayed saying that the required fields needs to be filled and work order should not be filled without mandatory fields.
- There should be no error message displayed if asset and location are added and work order should be saved.

## Scenario 19 - Verify that developer can easily add fields to any of the tables listed in the Report work view

**Pre-condition:**

1. Developer add fields to any of the tables for Failure Report, Labor, Materials and or Tool section in Report work page in app.xml file.
2. Technician application is deployed on developer local server and server is started.
3. Login with Admin credentials in Maximo classic/Manage of developer local server.
4. Go to work order tracking application.
5. Create a new work order.
6. Add assignments for labor and approve the WO.

**Steps:**

1. Launch the Maximo Mobile Technician app and login with assigned Technician/Labor.
2. Click on "My schedule" tile and open the list of work orders.
3. Search the work order and click to open wo details page.
4. Click on Report work touch-point to open Report work view.
5. Verify that the newly added fields in tables are displayed and there is no rendering/UI issues.

**Results:**

The newly added fields in tables for Failure Report, Labor, Materials and or Tool section should be displayed and there are no rendering/UI issues.

## Scenario 20 - Verify that developer can easily modify the Complete work button in the Report work view to change work order status to another status or synonym

**Pre-condition:**

1. Developer changes the complete work button in Report work page to change work order status to another status or synonym in app.xml file.
2. Technician application is deployed on developer local server and server is started.
3. Login with Admin credentials in Maximo classic/Manage of developer local server.
4. Go to work order tracking application.
5. Create a new work order.
6. Add assignments for labor and approve the WO.

**Steps:**

1. Launch the Maximo Mobile Technician app and login with assigned Technician/Labor.
2. Click on "My schedule" tile and open the list of work orders.
3. Search the work order and click to open wo details page.
4. Click on Report work touch-point to open Report work view.
5. Click the Complete Work button (developer can change label of this button too) on Report work page.
6. Verify that work order status is changed to another status or synonym as set by developer in app.xml file.

**Results:**

Work order status should be changed to another status or synonym as set by developer in app.xml file.
