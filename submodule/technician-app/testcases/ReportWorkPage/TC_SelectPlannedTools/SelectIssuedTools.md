# Select Planned tools/Select Issued tools from report work page

These test cases will cover functionalities of following user stories:

- MAXMOA-8095- Get Planned or Issued Tools

**Design URL:**

- <https://www.figma.com/design/RAPUxZUIzaPZonCgW1udeP/Technician---Assets?node-id=20180-16914&p=f&t=jjyqqxoD7XimCa4l-0>


**Note:**

- In case of web app, all transactions are saved in database and refreshed on the UI in real time.
- In case of online mode on mobile containers, transactions are sent to the database for saving instantly but UI is not refreshed from database until 'Check for Updates' button is clicked.
- In case of offline mode on mobile containers, transactions are queued for syncing to the database and synced to database once device is online. UI is not refreshed from database until 'Check for Updates' button is clicked.
- All server side error messages will be displayed in error page on devices post syncing with server and tapping on 'Check for Updates' button in both online and offline modes.

## Scenario 1 - Verify UI contents of the select planned tools and Select issued tools page for rotating and non-rotating tools

*Pre-condition:**

1. Login to Maximo/Manage application as admin.
2. Go to Tools.
3. Click on + button to create a new Tool .
4. NOTE: We can create 2 types of tool
   a. Rotating
   b. Non - Rotating Tools .
5. To create a Rotating Tool , click on the checkbox "Rotating ?".
6. Add tools to the storeroom ,add mandatory fields as in bin no etc.
7. Save tool and change the status to Active and also clicking on the checkbox of "Roll new status to organizations and inventory?".
8. For creating a non-rotating tool, just don't check the "Rotating ?" checkbox .
9. Go to Assets.
10. Click on + to create a new asset .
11. Select the rotating item created above .
12. Select location.
13. Change the status to Active.
14. Go to Work order tracking.
15. Create new one with following details :
    a. Add desc.
    b. Add Asset created on step 9,10.
    c. Go to plans.
    d. Click on Tools.
    e. Click on the + icon to add tools ,quantity and hours.
    f. Add the assignment and approve the WO.
16. Go to Purchase orders now .
17. Click on the + button to create a new purchase order.
18. Scroll down and select the company from vendor row .
19. Go to PO lines tab
20. Click on + button to create new PO lines .
21. Select Tool from Line type .
22. Select item ,Conversion factor,storeroom ,quantity and unit cost .
23. Save Purchase order and Approve purchase order.
24. Go to receiving.
25. Search the PO.
26. Click on the + icon to search the material receipts.
27. Search the PO line no.
28. Search the item , add mandatory fields if required .
29. Click on receive rotating items.
30. Click on the autonumber to generate random rotating assets no .
31. For Select issued tools - Go to Stocked tools .
32. Click on Issue Current Tool.
33. Add WO created, rotating asset if the asset has rotating tool ,location, issue To.
34. Change status if required and click on ok button .

**Steps:**

1. Login to technician app with the technician assigned to work order.
2. Click on "My Schedule" tile.
3. Find the work order created in pre-condition steps in "Assigned Work" filter.
4. Click the chevron on the work order card to open WO details page.
5. Click on report work page icon.
6. Go to Tools used.
7. Click on "Select Planned tools" and verify UI contents.
8. Click on "Select Issued tools" and verify UI contents.

**Result:**

Verify UI contents as follows.

- Verify select planned tools/Select issued tools and rotating tools are present as header when page opened .
- When no planned tools associated , the page should display "No non-rotating tools found" and "No rotating tools" found".
- Reset selection , Confirm selection , X button and tool desc should be present beneath the Select planned tools/Select issued tools .
- Verify if no tool desc is added , there should be nothing mentioned on the page.
- In the non-rotating tools section:
  a. TOOL_DESC with Non-rotating tools.
  b. Contains quantity and hour with non-editable pencil icon , checkbox in the right corner .
  c. should be able to click on the checkbox ,the quantity and hours field should become editable.
- In the rotating section:
  a. Tool name and dec should be present with chevron alon
  b. When chevron opened , "select rotating tool" header should be present and beneath it Select a rotating asset for TOOL_DESC should be displayed.
  c. Reset selection , Confirm selection should present with checkbox .
  d. Once again selecting the rotating tool , it will show the could as "COUNT tools selected , COUNT tools planned".
- While editing rotating and non-rotating tools and clicked on X . Save / discard pop up should be present .
- The quantity and hours will not be editable for rotating tools , although the hours field will be editable when clicked on checkbox.

## Scenario 2 - Non-rotating - Verify that tools can be added on the report work page

*Pre-condition:**

1. Please follow pre-requisite of scenario 1.

**Steps:**

1. Login to technician app with the technician assigned to work order.
2. Click on "My Schedule" tile.
3. Find the work order created in pre-condition steps in "Assigned Work" filter.
4. Click the chevron on the work order card to open WO details page.
5. Click on report work page icon.
6. Go to Tools used.
7. Click on "Select Planned tools".
8. Verify header "Select planned tools" and "Non-rotating tools".
9. Verify quantity and hours are non-editable fields with pencil icon.
10. Verify Confirm selection button is disabled.
11. Click on any of the non-rotating tools checkboxes.
12. Verify that quantity and hours field are editable now.
13. Edit the fields .
14. Click on the Confirm selection button.
15. Verify that non-rotating tools are added with TOOL_NAME , hours and quantity in the right corner.

**Result:**

- Verify that non-rotating tools are added with TOOL_NAME , hours and quantity in the right corner.

NOTE: Same to be performed for Select issued tools.

## Scenario 3 - Non-rotating - Verify that save/discard pop up should appear while the edited records are cancelled

*Pre-condition:**

1. Please follow pre-requisite of scenario 1.

**Steps:**

1. Login to technician app with the technician assigned to work order.
2. Click on "My Schedule" tile.
3. Find the work order created in pre-condition steps in "Assigned Work" filter.
4. Click the chevron on the work order card to open WO details page.
5. Click on report work page icon.
6. Go to Tools used.
7. Click on "Select Planned tools".
8. Click any of the non-rotating tools checkbox .
9. Click on the X icon .
10. Verify that Save/Discard pop up comes up .

**Result:**

- The Save/discard pop up should appear when we try to close the page once edited .
- Once clicked on the save button , the record should be saved.
- Once clicked on the discard button the record should be discarded and the user will be redirected to the report work page.

NOTE: Same to be performed for Select issued tools.

## Scenario 4 - Rotating - Verify that rotating tools can be added on the report work page

1. Please follow pre-requisite of scenario 1.

**Steps:**

1. Login to technician app with the technician assigned to work order.
2. Click on "My Schedule" tile.
3. Find the work order created in pre-condition steps in "Assigned Work" filter.
4. Click the chevron on the work order card to open WO details page.
5. Click on report work page icon.
6. Go to Tools used.
7. Click on "Select Planned tools".
8. Click on the chevron of the rotating tools.
9. Verify header as "Select rotating asset".
10. Select checkboxes.
11. Click on the Confirm selection.
12. Verify that rotating tool should be added on the report work page along with desc ,quantity and hours .

**Result:**

- The rotating tools should be added with TOOL_NAME , hours and quantity in the right corner.
- The quantity and hours fields should not be edited.
- When clicked on checkbox.

NOTE: Same to be performed for Select issued tools.

## Scenario 5 - Rotating - Verify that save/discard pop up should appear while the edited records are cancelled

*Pre-condition:**

1. Please follow pre-requisite of scenario 1.

**Steps:**

1. Login to technician app with the technician assigned to work order.
2. Click on "My Schedule" tile.
3. Find the work order created in pre-condition steps in "Assigned Work" filter.
4. Click the chevron on the work order card to open WO details page.
5. Click on report work page icon.
6. Go to Tools used.
7. Click on "Select Planned tools".
8. Click on the chevron of the rotating tools.
9. Select checkboxes.
10. Click on the X button.
11. Verify that Save/Discard pop up comes up .

**Result:**

- The Save/discard pop up should appear when we try to close the page once edited .
- Once clicked on the save button , the record should be saved.
- Once clicked on the discard button the record should be discarded and the user will be redirected to the report work page.

NOTE: Same to be performed for Select issued tools.

## Scenario 6 - Rotating - Verify the Rotating tools count should be updated in front of the chevron

1. Please follow pre-requisite of scenario 1.

**Steps:**

1. Login to technician app with the technician assigned to work order.
2. Click on "My Schedule" tile.
3. Find the work order created in pre-condition steps in "Assigned Work" filter.
4. Click the chevron on the work order card to open WO details page.
5. Click on report work page icon.
6. Go to Tools used.
7. Click on "Select Planned tools".
8. Click on the chevron of the rotating tools.
9. Verify header as "Select rotating asset".
10. Select checkboxes.
11. Click on the Confirm selection.
12. Click on "Select Planned tools" again.
13. Verify that "COUNT tools selected , COUNT tools planned" should be displayed.

**Result:**

- The "COUNT tools selected , COUNT tools planned" should be displayed.

NOTE: Same to be performed for Select issued tools.

## Scenario 7 - Verify all the above test scenarios on mobile devices in online and offline mode

**Pre-condition:**

Pre-conditions as specified for above mentioned test scenarios.

**Steps:**

Perform steps as specified for above mentioned test scenarios.

**Results:**

The application should behave as per expectations for all above mentioned test scenarios on mobile devices in online and offline mode.

NOTE: Same to be performed for Select issued tools.