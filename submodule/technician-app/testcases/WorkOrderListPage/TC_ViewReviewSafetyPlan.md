# View and review safety plan information on WO list and WO details

These test cases will verify UI, contents and review safety plan functionality for WO list and WO details pages on Technician web app, online and offline mode on mobile containers.

These test cases will cover functionalities of following user stories:

- GRAPHITE-31895: Eli can view safety information on his work orders (drawer content)
- GRAPHITE-47643: Eli want to access Safety plans from work details page
- GRAPHITE-47667: Eli must review safety information before starting his work
- GRAPHITE-47619: Eli must inform he reviewed the Safety Plan info
- GRAPHITE-75990:Add sigoption to make FSM flow optional
- MAXMOA-4034:  Technician (RBA) - Record the person who reviewed the Safety Plan

Design URL:

- <https://ibm.invisionapp.com/share/M3O1XR272PV#/screens>

**Note:**

- In case of web app, all transactions are saved in database and refreshed on the UI in real time.
- In case of online mode on mobile containers, transactions are sent to the database for saving instantly but UI is not refreshed from database until 'Check for Updates' button is clicked.
- In case of offline mode on mobile containers, transactions are queued for syncing to the database and synced to database once device is online. UI is not refreshed from database until 'Check for Updates' button is clicked.
- All server side error messages will be displayed in error page on devices post syncing with server and tapping on 'Check for Updates' button in both online and offline modes.

## Scenario 1 - Verify unavailability of safety plan touch-point on WO list page when safety plan or hazards/hazardous materials are not added to work order

**Pre-condition:**

1. Login to maximo/manage application as admin.
2. Create a work order.
3. Do not associate a safety plan or add hazards/hazardous materials directly in "Safety Plan" tab of the work order.
4. Assign labor/technician to work order in Assignments tab.
5. Approve the work order.

**Steps:**

1. Login to technician app with the technician assigned to work order.
2. Click on "My Schedule" tile.
3. Find the work order created in pre-condition steps in "Assigned Work" filter.
4. Verify that safety plan touch-point is unavailable on work order card in WO list page.

**Result:**

Safety plan touch-point should be unavailable on work order card in WO list page.

## Scenario 2 - Verify availability of safety plan touch-point along with badge count on WO list page when safety plan or hazards/hazardous materials are added to work order

**Pre-condition:**

1. Login to maximo/manage application as admin.
2. Create a work order.
3. Associate a safety plan or add hazards/hazardous materials directly in "Safety Plan" tab of the work order.
4. Assign labor/technician to work order in Assignments tab.
5. Approve the work order.

**Steps:**

1. Login to technician app with the technician assigned to work order.
2. Click on "My Schedule" tile.
3. Find the work order created in pre-condition steps in "Assigned Work" filter.
4. Verify that safety plan touch-point along with badge count is available on work order card in WO list page.

**Result:**

- Safety plan touch-point should be available on work order card in WO list page. It should display on the left by default.
- The badge count indicating the number of hazards and hazardous materials in the safety plan associated with the work order should be displayed.
- The badge count value on safety plan touch-point should match the number of hazards and hazardous materials associated with the work order.

## Scenario 3 - Verify that tapping on safety plan touch-point on WO list page opens safety plan sliding drawer

**Pre-condition:**

1. Login to maximo/manage application as admin.
2. Create a work order.
3. Associate a safety plan or add hazards/hazardous materials directly in "Safety Plan" tab of the work order.
4. Assign labor/technician to work order in Assignments tab.
5. Approve the work order.

**Steps:**

1. Login to technician app with the technician assigned to work order.
2. Click on "My Schedule" tile.
3. Find the work order created in pre-condition steps in "Assigned Work" filter.
4. The safety plan touch-point along with badge count is available on work order card in WO list page.
5. Tap on the safety plan touch-point.
6. Verify that safety plan sliding drawer opens.

**Result:**

Safety plan sliding drawer should open when technician taps on the safety plan touch-point on WO list page.

## Scenario 4 - Verify availability of safety plan row on top of the WO details page when safety plan or hazards/hazardous materials are added to work order and safety plan is not reviewed

**Pre-condition:**

1. Login to maximo/manage application as admin.
2. Create a work order.
3. Associate a safety plan or add hazards/hazardous materials directly in "Safety Plan" tab of the work order.
4. Assign labor/technician to work order in Assignments tab.
5. Approve the work order.

**Steps:**

1. Login to technician app with the technician assigned to work order.
2. Click on "My Schedule" tile.
3. Find the work order created in pre-condition steps in "Assigned Work" filter.
4. Click on chevron on the work order card to open WO details page.
5. Verify availability of safety plan row on top of the WO details page when safety plan or hazards/hazardous materials are added to work order and safety plan is not reviewed.

**Result:**

Safety plan row should be available on top of the WO details page when safety plan or hazards/hazardous materials are added to work order and safety plan is not reviewed.

## Scenario 5 - Verify tapping on safety plan row/tile on the WO details page opens safety plan sliding drawer

**Pre-condition:**

1. Login to maximo/manage application as admin.
2. Create a work order.
3. Associate a safety plan or add hazards/hazardous materials directly in "Safety Plan" tab of the work order.
4. Assign labor/technician to work order in Assignments tab.
5. Approve the work order.

**Steps:**

1. Login to technician app with the technician assigned to work order.
2. Click on "My Schedule" tile.
3. Find the work order created in pre-condition steps in "Assigned Work" filter.
4. Click on chevron on the work order card to open WO details page.
5. The safety plan row on top of the WO details page is displayed.
6. Tap on the safety plan row on top of the WO details page.
7. Verify that safety plan sliding drawer opens.

**Result:**

Safety plan sliding drawer should open when technician taps on the safety plan row/tile on the WO details page.

**Note:**

Perform this scenario when safety plan is reviewed and 'Safety plan' tile/row is displayed on the bottom of WO details page.

## Scenario 6 - Verify contents of safety plan sliding drawer on WO list and WO details pages when only hazards are added to work order and safety plan is not reviewed

**Pre-condition:**

1. Login to maximo/manage application as admin.
2. Create a work order.
3. Associate a safety plan with only hazards or add hazards directly in "Safety Plan" tab of the work order. Make sure there is no hazardous material added to work order.
4. Assign labor/technician to work order in Assignments tab.
5. Approve the work order.

**Steps:**

1. Login to technician app with the technician assigned to work order.
2. Click on "My Schedule" tile.
3. Find the work order created in pre-condition steps in "Assigned Work" filter.
4. The safety plan touch-point along with badge count is available on work order card in WO list page.
5. Tap on the safety plan touch-point to open safety plan sliding drawer.
6. Verify the contents of the safety plan sliding drawer.

**Result:**

The contents of the safety plan sliding drawer should be:

- Header title should be "Safety plan" with "X" button.
- "Review safety plan" section with value as "Not reviewed" along with primary checkmark button.
- Only Hazards section and no Hazardous materials section should be displayed.
- Hazards section should display all hazards associated with the work order. For each hazard, hazard description along with associated precaution(s) description should be displayed.

**Note:**

Perform this scenario when technician taps on safety plan row/tile on the WO details page.

## Scenario 7 - Verify contents of safety plan sliding drawer on WO list and WO details pages when only hazardous materials are added to work order and safety plan is not reviewed

**Pre-condition:**

1. Login to maximo/manage application as admin.
2. Create a work order.
3. Associate a safety plan with only hazardous materials or add only hazards with "Hazardous Material Enabled?" checkbox as checked directly in "Safety Plan" tab of the work order. Make sure there is no hazard with "Hazardous Material Enabled?" checkbox as un-checked added to work order.
4. Assign labor/technician to work order in Assignments tab.
5. Approve the work order.

**Steps:**

1. Login to technician app with the technician assigned to work order.
2. Click on "My Schedule" tile.
3. Find the work order created in pre-condition steps in "Assigned Work" filter.
4. The safety plan touch-point along with badge count is available on work order card in WO list page.
5. Tap on the safety plan touch-point to open safety plan sliding drawer.
6. Verify the contents of the safety plan sliding drawer.

**Result:**

The contents of the safety plan sliding drawer should be:

- Header title should be "Safety plan" with "X" button.
- "Review safety plan" section with value as "Not reviewed" along with primary checkmark button.
- Only "Hazardous materials" section and no Hazards section should be displayed.
- "Hazardous materials" section should display all hazardous materials associated with the work order. For each "Hazardous materials", hazardous material description along with associated precaution(s) description should be displayed.
- Hazards with "Hazardous Material Enabled?" checkbox as checked should be displayed only in "Hazardous materials" section and not in Hazards section.

**Note:**

Perform this scenario when technician taps on safety plan row/tile on the WO details page.

## Scenario 8 - Verify contents of safety plan sliding drawer on WO list and WO details pages when hazards and hazardous materials are added to work order and safety plan is not reviewed

**Pre-condition:**

1. Login to maximo/manage application as admin.
2. Create a work order.
3. Associate a safety plan with both hazards and hazardous materials or add hazards with and without "Hazardous Material Enabled?" checkbox as checked directly in "Safety Plan" tab of the work order.
4. Assign labor/technician to work order in Assignments tab.
5. Approve the work order.

**Steps:**

1. Login to technician app with the technician assigned to work order.
2. Click on "My Schedule" tile.
3. Find the work order created in pre-condition steps in "Assigned Work" filter.
4. The safety plan touch-point along with badge count is available on work order card in WO list page.
5. Tap on the safety plan touch-point to open safety plan sliding drawer.
6. Verify the contents of the safety plan sliding drawer.

**Result:**

The contents of the safety plan sliding drawer should be:

- Header title should be "Safety plan" with "X" button.
- "Review safety plan" section with value as "Not reviewed" along with primary checkmark button.
- Both Hazards and "Hazardous materials" sections should be displayed.
- Hazards section should display all hazards associated with the work order. For each hazard, hazard description along with associated precaution(s) description should be displayed.
- "Hazardous materials" section should display all hazardous materials associated with the work order. For each "Hazardous materials", hazardous material description along with associated precaution(s) description should be displayed.
- Hazards with "Hazardous Material Enabled?" checkbox as checked should be displayed only in "Hazardous materials" section and not in Hazards section.

**Note:**

Perform this scenario when technician taps on safety plan row/tile on the WO details page.

## Scenario 9 - Verify that tapping on blue checkmark button on safety plan sliding drawer on WO list and WO details pages marks the safety plan as reviewed and displays reviewed date and time along with green checkmark

**Pre-condition:**

1. Login to maximo/manage application as admin.
2. Create a work order.
3. Associate a safety plan with hazards/hazardous materials or add hazards/hazardous materials directly in "Safety Plan" tab of the work order.
4. Assign labor/technician to work order in Assignments tab.
5. Approve the work order.

**Steps:**

1. Login to technician app with the technician assigned to work order.
2. Click on "My Schedule" tile.
3. Find the work order created in pre-condition steps in "Assigned Work" filter.
4. The safety plan touch-point along with badge count is available on work order card in WO list page.
5. Tap on the safety plan touch-point to open safety plan sliding drawer.
6. Tap on primary checkmark button in "Review safety plan" section.
7. Verify that tapping on blue checkmark button marks the safety plan as reviewed and displays reviewed date and time along with green checkmark.

**Result:**

- Tapping on blue checkmark button should mark the safety plan as Reviewed and a green checkmark should be displayed.
- The current date and time should be displayed as reviewed date and time.

**Note:**

Perform this scenario on the WO details page.

## Scenario 10 - Verify that clicking the "X" button on safety plan sliding drawer closes the drawer and WO list or WO details page is displayed depending on the page from which drawer was opened

**Pre-condition:**

1. Login to maximo/manage application as admin.
2. Create a work order.
3. Associate a safety plan with hazards/hazardous materials or add hazards/hazardous materials directly in "Safety Plan" tab of the work order.
4. Assign labor/technician to work order in Assignments tab.
5. Approve the work order.

**Steps:**

1. Login to technician app with the technician assigned to work order.
2. Click on "My Schedule" tile.
3. Find the work order created in pre-condition steps in "Assigned Work" filter.
4. The safety plan touch-point along with badge count is available on work order card in WO list page.
5. Tap on the safety plan touch-point to open safety plan sliding drawer.
6. Click "X" button on top left of the drawer.
7. Verify safety plan sliding drawer closes and technician is brought back to WO list page.

**Result:**

Safety plan sliding drawer should close and technician should be brought back to WO list page.

**Note:**

Perform this scenario on the WO details page where technician should be brought back to WO details page when safety plan sliding drawer is closed.

## Scenario 11 - Verify availability of safety plan tile/row on the bottom of WO details page when safety plan is reviewed

**Pre-condition:**

1. Login to maximo/manage application as admin.
2. Create a work order.
3. Associate a safety plan with hazards/hazardous materials or add hazards/hazardous materials directly in "Safety Plan" tab of the work order.
4. Assign labor/technician to work order in Assignments tab.
5. Approve the work order.

**Steps:**

1. Login to technician app with the technician assigned to work order.
2. Click on "My Schedule" tile.
3. Find the work order created in pre-condition steps in "Assigned Work" filter.
4. Click on chevron on the work order card to open WO details page.
5. Safety plan row on top of the WO details page is displayed.
6. Tap on the safety plan row on top of the WO details page to open safety plan sliding drawer.
7. Tap on primary checkmark button in "Review safety plan" section to mark the safety plan as reviewed.
8. Tap on close 'X' button on the sliding drawer.
9. Technician is brought back to WO details page.
10. Verify availability of safety plan tile/row on the bottom of WO details page.

**Result:**

Safety plan tile/row should now removed from top and available on the bottom of WO details page.

## Scenario 12 - Verify the property description and default value for 'maximo.mobile.safetyplan.review' system property

**Steps:**

1. Login to Maximo classic application as admin.
2. Go to system properties.
3. Search 'maximo.mobile.safetyplan.review' system property.
4. Verify the property description and default value for 'maximo.mobile.safetyplan.review' system property.

**Result:**

- The property description for 'maximo.mobile.safetyplan.review' system property should be "You must review the safety plan before you change the status to In progress.".
- The default value for 'maximo.mobile.safetyplan.review' system property should be 0.

## Scenario 13 - Verify that safety plan sliding drawer doesn't open and work order status is changed to INPRG or it's synonym when maximo.mobile.safetyplan.review=0, safety plan is not reviewed and technician tries to start work or change WO status to INPRG or it's synonym on WO list and WO details pages

**Pre-condition:**

1. Login to maximo/manage application as admin.
2. Create a work order.
3. Associate a safety plan with hazards/hazardous materials or add hazards/hazardous materials directly in "Safety Plan" tab of the work order.
4. Assign labor/technician to work order in Assignments tab.
5. Approve the work order.
6. Make sure that maximo.mobile.safetyplan.review system property is set to 0. 
7. Accept & Reject Button visible when "Enable Assignment flow" property will be enabled. 
8. if "Enable Assignment flow" property will be Disabled user can directly See "Start Work" Button.
**Steps:**

1. Login to technician app with the technician assigned to work order.
2. Click on "My Schedule" tile.
3. Find the created work order in pre-condition steps in WO list and click on 'Accept' button.
4. The safety plan touch-point along with badge count is available on work order card in WO list page.
5. Tap on "Start work" button or try to change work order status to INPRG or it's synonym.
6. Verify that safety plan sliding drawer doesn't open and work order status is changed to INPRG or it's synonym.

**Result:**

The safety plan sliding drawer shouldn't open and work order status is changed to INPRG or it's synonym when maximo.mobile.safetyplan.review=0 and safety plan is not reviewed.

**Note:**

Perform this scenario when technician taps on "Start work" button or try to change work order status to INPRG or it's synonym on the WO details page.

## Scenario 14 - Verify that safety plan sliding drawer opens with toast message asking to review the safety plan and WO status is not changed when maximo.mobile.safetyplan.review=1, safety plan is not reviewed and technician tries to start work on WO list and WO details pages

**Pre-condition:**

1. Login to maximo/manage application as admin.
2. Create a work order.
3. Associate a safety plan with hazards/hazardous materials or add hazards/hazardous materials directly in "Safety Plan" tab of the work order.
4. Assign labor/technician to work order in Assignments tab.
5. Approve the work order.
6. Make sure that maximo.mobile.safetyplan.review system property is set to 1. 
7. Accept & Reject Button visible when "Enable Assignment flow" property will be enabled. 
8. if "Enable Assignment flow" property will be Disabled user can directly See "Start Work" Button.
9. Follow Same steps expected while creating WO , do not add GL account.

**Steps:**

1. Login to technician app with the technician assigned to work order.
2. Click on "My Schedule" tile.
3. Find the created work order in pre-condition steps in WO list and click on 'Accept' button.
4. The safety plan touch-point along with badge count is available on work order card in WO list page.
5. Tap on "Start work" button on work order card.
6. Verify that safety plan sliding drawer opens with toast message asking to review the safety plan and work order status is not changed to "In Progress".

**Result:**

- The safety plan sliding drawer should open with toast message asking to review the safety plan and work order status should not change to "In Progress".
- The toast message should be: "You must review the safety plan before you start work."
- When clicked on start work , toast message should be displayed "You must review the safety plan before you start work." Once reviewed when you again click to start WO, GL account message should be thrown and the start work should not be start.

**Note:**

Perform this scenario when technician taps on "Start work" button on the WO details page.

## Scenario 15 - Verify that safety plan sliding drawer opens with toast message asking to review the safety plan and WO status is not changed when maximo.mobile.safetyplan.review=1, safety plan is not reviewed and technician tries to change WO status to INPRG or it's synonym on WO list and WO details pages

**Pre-condition:**

1. Login to maximo/manage application as admin.
2. Create a work order.
3. Associate a safety plan with hazards/hazardous materials or add hazards/hazardous materials directly in "Safety Plan" tab of the work order.
4. Assign labor/technician to work order in Assignments tab.
5. Approve the work order.
6. Make sure that maximo.mobile.safetyplan.review system property is set to 1. 
7. Accept & Reject Button visible when "Enable Assignment flow" property will be enabled. 
8. if "Enable Assignment flow" property will be Disabled user can directly See "Start Work" Button.
**Steps:**

1. Login to technician app with the technician assigned to work order.
2. Click on "My Schedule" tile.
3. Find the created work order in pre-condition steps in WO list and click on 'Accept' button.
4. The safety plan touch-point along with badge count is available on work order card in WO list page.
5. Tap on status action tag on WO card to open "Change status" sliding drawer and try to change WO status to INPRG or it's synonym's.
6. Verify that safety plan sliding drawer opens with toast message asking to review the safety plan and work order status is not changed to INPRG or it's synonym.

**Result:**

- The safety plan sliding drawer should open with toast message asking to review the safety plan and work order status shouldn't change to INPRG or it's synonym.
- The toast message should be: "You must review the safety plan before you start work."

**Note:**

Perform this scenario when technician tries to change WO status to INPRG or it's synonym on the WO details page. In this case, The toast message should be: "You must review the safety plan before you change the status."

## Scenario 16 - Verify that safety plan sliding drawer doesn't open and work order status is changed to INPRG or it's synonym when maximo.mobile.safetyplan.review=1, safety plan is already reviewed and technician tries to start work or change WO status to INPRG or it's synonym on WO list and WO details pages

**Pre-condition:**

1. Login to maximo/manage application as admin.
2. Create a work order.
3. Associate a safety plan with hazards/hazardous materials or add hazards/hazardous materials directly in "Safety Plan" tab of the work order.
4. Assign labor/technician to work order in Assignments tab.
5. Approve the work order. 
6. Accept & Reject Button visible when "Enable Assignment flow" property will be enabled. 
7. Make sure that maximo.mobile.safetyplan.review system property is set to 1.
8. if "Enable Assignment flow" property will be Disabled user can directly See "Start Work" Button.

**Steps:**

1. Login to technician app with the technician assigned to work order.
2. Click on "My Schedule" tile.
3. Find the created work order in pre-condition steps in WO list and click on 'Accept' button.
4. The safety plan touch-point along with badge count is available on work order card in WO list page.
5. Tap on the safety plan touch-point on work order card to open safety plan sliding drawer.
6. Tap on primary checkmark button in "Review safety plan" section to mark the safety plan as reviewed.
7. Tap on close 'X' button on the sliding drawer and technician is brought back to WO details page.
8. Tap on "Start work" button or try to change work order status to INPRG or it's synonym.
9. Verify that safety plan sliding drawer doesn't open and work order status is changed to INPRG or it's synonym.

**Result:**

Safety plan sliding drawer shouldn't open and work order status should change to INPRG or it's synonym when maximo.mobile.safetyplan.review=1, safety plan is already reviewed and technician tries to start work or change WO status to INPRG or it's synonym.

**Note:**

Perform this scenario when technician tries to start work or change WO status to INPRG or it's synonym on the WO details page.

## Scenario 17 - Verify that safety plan sliding drawer doesn't open and work order status is changed as per selected option when maximo.mobile.safetyplan.review=1, safety plan is not reviewed and technician tries to change WO status to any status other than INPRG or it's synonym on WO list and WO details pages

**Pre-condition:**

1. Login to maximo/manage application as admin.
2. Create a work order.
3. Associate a safety plan with hazards/hazardous materials or add hazards/hazardous materials directly in "Safety Plan" tab of the work order.
4. Assign labor/technician to work order in Assignments tab.
5. Approve the work order.
6. Make sure that maximo.mobile.safetyplan.review system property is set to 1. 
7. Accept & Reject Button visible when "Enable Assignment flow" property will be enabled. 
8. if "Enable Assignment flow" property will be Disabled user can directly See "Start Work" Button.
**Steps:**

1. Login to technician app with the technician assigned to work order.
2. Click on "My Schedule" tile.
3. Find the created work order in pre-condition steps in WO list and click on 'Accept' button.
4. The safety plan touch-point along with badge count is available on work order card in WO list page.
5. Tap on status action tag on WO card to open "Change status" sliding drawer and try to change WO status to status other than INPRG or it's synonym.
6. Verify that safety plan sliding drawer doesn't open and work order status is changed to the selected status option in "Change status" drawer.

**Result:**

Safety plan sliding drawer shouldn't open and work order status should change to the selected status option in "Change status" drawer when maximo.mobile.safetyplan.review=1, safety plan is not reviewed and technician tries to change WO status to status other than INPRG or it's synonym.

**Note:**

Perform this scenario when technician tries to change WO status to status other than INPRG or it's synonym on the WO details page.

## Scenario 18 - Verify that safety plan sliding drawer doesn't open and work order status is changed to INPRG or it's synonym when maximo.mobile.safetyplan.review=1, safety plan or Hazards/Hazardous materials are not associated with work order and technician tries to start work or change WO status to INPRG or it's synonym on WO list and WO details pages

**Pre-condition:**

1. Login to maximo/manage application as admin.
2. Create a work order.
3. Don't associate a safety plan with hazards and/or hazardous materials or add hazards directly in "Safety Plan" tab of the work order.
4. Assign labor/technician to work order in Assignments tab.
5. Approve the work order.
6. Make sure that maximo.mobile.safetyplan.review system property is set to 1. 
7. Accept & Reject Button visible when "Enable Assignment flow" property will be enabled. 
8. if "Enable Assignment flow" property will be Disabled user can directly See "Start Work" Button.
**Steps:**

1. Login to technician app with the technician assigned to work order.
2. Click on "My Schedule" tile.
3. Find the created work order in pre-condition steps in WO list and click on 'Accept' button.
4. The safety plan touch-point is unavailable on work order card in WO list page.
5. Tap on "Start work" button or try to change work order status to INPRG or it's synonym.
6. Verify that safety plan sliding drawer doesn't open and work order status is changed to INPRG or it's synonym.

**Result:**

Safety plan sliding drawer shouldn't open and work order status should change to INPRG or it's synonym when maximo.mobile.safetyplan.review=1, safety plan or Hazards/Hazardous materials are not associated with work order and technician tries to start work or change WO status to INPRG or it's synonym.

**Note:**

Perform this scenario when technician tries to start work or change WO status to INPRG or it's synonym on the WO details page.

## Scenario 19 - Verify that safety plan sliding drawer doesn't open when maximo.mobile.safetyplan.review=1, safety plan is not reviewed and technician tries to start travel on WO list and WO details pages

**Pre-condition:**

1. Login to maximo/manage application as admin.
2. Go to Organizations app.
3. Search and select the organization.
4. In Service address options, choose "Use an external or GIS address system" in Address master and "Latitude and longitude" in coordinates then click OK button.
5. Go to System properties.
6. Change the system property "mxe.mobile.travel.navigation" value to 1.
7. Change the system property "mxe.mobile.travel.prompt" value to 1.
8. Change the system property "mxe.mobile.travel.radius" value to 1.
9. Perform 'Live refresh'.
10. Go to service address app and create a new service address by adding the longitude and latitude.
11. Go to work order tracking, create a work order and associate the service address to it.
12. Associate a safety plan with hazards/hazardous materials or add hazards directly in "Safety Plan" tab of the work order.
13. Add assignments for technician and approve the work order.
14. Make sure that maximo.mobile.safetyplan.review system property is set to 1. 
15. Accept & Reject Button visible when "Enable Assignment flow" property will be enabled. 
16. if "Enable Assignment flow" property will be Disabled user can directly See "Start Work" Button.
**Steps:**

1. Login to technician app with the technician assigned to work order.
2. Click on "My Schedule" tile.
3. Find the created work order in pre-condition steps in WO list and click on 'Accept' button.
4. The safety plan touch-point along with badge count is available on work order card in WO list page.
5. Tap on "Start travel" button.
6. Verify that safety plan sliding drawer doesn't open.

**Result:**

Safety plan sliding drawer shouldn't open and stop travel button is displayed on work order card.

**Note:**

Perform this scenario when technician tries to start travel on the WO details page.

## Scenario 20 - Verify that safety plan sliding drawer doesn't open and task or work order status changes to completed when maximo.mobile.safetyplan.review=1, safety plan is not reviewed and technician tries to complete task on task list page or work order using "Complete work" button on Report work page

**Pre-condition:**

1. Login to maximo/manage application as admin.
2. Create a work order and add tasks to work order.
3. Associate a safety plan with hazards/hazardous materials or add hazards/hazardous materials directly in "Safety Plan" tab of the work order.
4. Assign labor/technician to work order in Assignments tab.
5. Approve the work order.
6. Make sure that maximo.mobile.safetyplan.review system property is set to 1. 
7. Accept & Reject Button visible when "Enable Assignment flow" property will be enabled. 
8. if "Enable Assignment flow" property will be Disabled user can directly See "Start Work" Button.
**Steps:**

1. Login to technician app with the technician assigned to work order.
2. Click on "My Schedule" tile.
3. Find the created work order in pre-condition steps in WO list and click on 'Accept' button.
4. Click on chevron on the work order card to open WO details page.
5. Click on task touch-point to open tasks list.
6. Try to mark a task as completed.
7. Go to Report work page and try to click the "Complete work" button.
8. Verify that safety plan sliding drawer doesn't open and task or work order status changes to completed.

**Result:**

Safety plan sliding drawer shouldn't open and task or work order status should change to completed.

## Scenario 21 - Verify the functionality when Safety plan drawer is opened and navigator is clicked and 'Material & Tools' or 'Maps' are selected.

**Pre-condition:**

1. Login to maximo/manage application as admin.
2. Create a work order.
3. Associate a safety plan or add hazards/hazardous materials directly in "Safety Plan" tab of the work order.
4. Assign labor/technician to work order in Assignments tab.
5. Approve the work order.

**Steps:**

1. Login to technician app with the technician assigned to work order.
2. Click on "My Schedule" tile.
3. Find the work order created in pre-condition steps in "Assigned Work" filter.
4. Click on chevron on the work order card to open WO details page.
5. The safety plan row on top of the WO details page is displayed.
6. Tap on the safety plan row on top of the WO details page.
7. Verify that safety plan sliding drawer opens.
8. Click on navigator.
9. Click on 'Material & Tools' or 'Maps' touchpoint.
10. Verify new selected state is opened which is selected by the user and not Safety plan drawer.

**Result:**

New selected state should be opened which is selected by the user and not Safety plan drawer.

## Scenario 22 - Verify review Safety plan for multiple labors/users.

**Pre-condition:**

1. Login to maximo/manage application as admin.
2. Create a work order.
3. Associate a safety plan or add hazards/hazardous materials directly in "Safety Plan" tab of the work order.
4. Assign 2 labors in Assignment tab.
5. Approve the work order.

**Steps:**

1. Login to technician app with the technician assigned to work order.
2. Click on "My Schedule" tile.
3. Find the work order created in pre-condition steps in "Assigned Work" filter.
4. Click on chevron on the work order card to open WO details page.
5. The safety plan row on top of the WO details page is displayed.
6. Tap on the safety plan row on top of the WO details page.
7. Verify slider opens with safety review details.
8. Checkmark the review plan. 
9. Verify current date/time is displayed. 
10. Logout and login with another labor mentioned in the pre-requisite - 4.
11. Follow steps from 1 to 7.
12. Verify that another labor should be able to review the safety plan when it's completed for the 1st labor.

**Result:**

- When safety plan is reviewed in the first labor , it should be pending to be reviewed when logged in with the second labor.
- Safety plan should be added in the WO Details page with current date/time when clicked on the chevron to open review safety plan .

## Scenario 23 - Verify by starting work order for multiple labors/users, when property maximo.mobile.safetyplan.review=1 

**Pre-condition:**

1. Login to maximo/manage application as admin.
2. Go to system properties.
3. Filter the property and make it to 1 if 0 for maximo.mobile.safetyplan.review.
4. Save it and live refresh.
5. Create a work order.
6. Associate a safety plan or add hazards/hazardous materials directly in "Safety Plan" tab of the work order.
7. Assign 2 labors in Assignment tab.
8. Approve the work order.

**Steps:**

1. Login to technician app with the technician assigned to work order.
2. Click on "My Schedule" tile.
3. Find the work order created in pre-condition steps in "Assigned Work" filter.
4. Click on chevron on the work order card to open WO details page.
5. Click to start work order.
6. Verify error message should be thrown "You must review the safety plan before you start work".
7. Logout and login with another labor mentioned in the pre-requisite - 4.
8. Follow steps 1 to 5 
9. Verify error message should be thrown "You must review the safety plan before you start work".

**Result:**

- Error message should be diplayed for both the users when the property maximo.mobile.safetyplan.review= 1

## Scenario 24 - Verify Accept flow for review safety plan with multiple labors/users.

**Pre-condition:**

1. Login to maximo/manage application as admin.
2. Go to security groups.
3. Search for the labors you want assignment for (eg.. maxadmin,wilson)
4. Filter and search mxapiwodetail. 
5. Search for "Enable assignment flow" and enable it 
6. Save it.
7. Create a work order.
8. Associate a safety plan or add hazards/hazardous materials directly in "Safety Plan" tab of the work order.
9. Assign 2 labors in Assignment tab.
10. Approve the work order.

**Steps:**

1. Login to technician app with the technician assigned to work order.
2. Click on "My Schedule" tile.
3. Find the work order created in pre-condition steps in "Assigned Work" filter.
4. Click on chevron on the work order card to open WO details page.
5. Verify you can review safety plan without Accept.
6. Logout and login with new labor follow all steps.
7. Verify you can review safety plan without Accept.
8. Follow same steps for multiple labors and verify that review safety plan can also be reviewed after clicking on Accept button.

**Result:**

- Multiple labors should be independent of each other for Accept button.
- The safety plan can be reviewed with accept and without accepting button.

## Scenario 25 - Verify reject flow for review safety plan with multiple labors/users.

**Pre-condition:**

1. Login to maximo/manage application as admin.
2. Go to security groups.
3. Search for the labors you want assignment for (eg.. maxadmin,wilson)
4. Filter and search mxapiwodetail.
5. Search for "Enable assignment flow" and enable it
6. Save it.
7. Create a work order.
8. Associate a safety plan or add hazards/hazardous materials directly in "Safety Plan" tab of the work order.
9. Assign 2 labors in Assignment tab.
10. Approve the work order.

**Steps:**

1. Login to technician app with the technician assigned to work order.
2. Click on "My Schedule" tile.
3. Find the work order created in pre-condition steps in "Assigned Work" filter.
4. Click on chevron on the work order card to open WO details page.
5. Click on the Reject button, select reason and click on ok.
6. Verify that you should not be able to review the safety plan.
7. Login and Login with the 2nd labor 
8. Follow steps from 1 to 4 .
9. Verify that another labor can Accept/Reject the WO independent of the 1st labor action performed.(Rejected WO).

**Result:**

- 2nd labor should be independent of the Rejected WO and can approve/reject the WO.

## Scenario 26 - Approvals - Verify safety plan review for approvals for "Work orders to review" and "Completed Work orders"

**Pre-condition:**

1. Login to maximo/manage application as admin.
2. Create 2 work orders.
3. Associate a safety plan or add hazards/hazardous materials directly in "Safety Plan" tab of the work order.
4. Assign supervisor.
5. Approve the work order.

**Steps:**

1. Open supmobile app from classic. 
2. You will be by default present on the "Work orders to review".
3. Go to the wo details page.
4. Verify that Review safety plan tile is present 
5. Click on it and the slider will open 
6. Click on the checkmark to review the Safety plan
7. Verify that safety plan is reviewed with date and time 
8. Follow steps 1 and 2.
9. Open 2nd WO and complete it by changing status 
10. Choose "Complete work orders".
11. Open the WO and move to WO details page.
12. Verify that the Safety plan tile is present and can reviewed by following same steps from 5 to 7.

**Result:**

- Safety plan can be reviewed from both "Work orders created" and from "Completed work orders".
- Once reviewed from "Work orders created" and then completed, it cannot be reviewed again from "Completed work orders".
- The multiple labor safety plan is not applicable to Approvals .

## Scenario 27 - Verify the UI of the cards/pages/views for fonts, font sizes, font color, text completeness, alignment, positioning etc. is correct and as per design on mobile and other small screen devices for all supported form factors

**Pre-condition:**


Pre-conditions as specified for above mentioned test scenarios.

**Steps:**

Perform steps as specified for above mentioned test scenarios.

**Result:**

- The cards/pages for fonts, font sizes, font color, text completeness, alignment, positioning etc. are correct and as per design.
- The application should behave as per expectations for all above mentioned scenarios on mobile and other small screen devices for all supported form factors.
- The UI should be as per design for respective form factor.

## Scenario 28 - Verify all the above test cases in offline or disconnected mode

**Pre-condition:**

Pre-conditions as specified for above mentioned test scenarios.

**Steps:**

Perform steps as specified for above mentioned test scenarios.

**Result:**

The application should behave as per expectations for all above mentioned scenarios in offline or disconnected mode on mobiles/tablets/desktops.