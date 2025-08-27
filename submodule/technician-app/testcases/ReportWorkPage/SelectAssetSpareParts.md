# Select asset spare parts from Material used on Report work page

These test cases will cover functionalities of following user stories:

- MAXMOA-7527- Allow Filtering for Spare Parts for the WO Asset

**Design URL:**

- <https://www.figma.com/design/RAPUxZUIzaPZonCgW1udeP/Technician-%26-Assets?node-id=3142-201907&node-type=section&t=Dwve2kq4uuZZFwcS-11>


**Note:**

- In case of web app, all transactions are saved in database and refreshed on the UI in real time.
- In case of online mode on mobile containers, transactions are sent to the database for saving instantly but UI is not refreshed from database until 'Check for Updates' button is clicked.
- In case of offline mode on mobile containers, transactions are queued for syncing to the database and synced to database once device is online. UI is not refreshed from database until 'Check for Updates' button is clicked.
- All server side error messages will be displayed in error page on devices post syncing with server and tapping on 'Check for Updates' button in both online and offline modes.


## Scenario 1 - Verify contents of Select asset spare parts on report work page without spare part associated and not associated.


**Pre-condition:**

1. Login to Maximo/Manage application as admin.
2. Go to item master.
3. Add item , desc , Add items to storeroom 
4. Change status to Active and click on the checkbox of "Roll new status to Organizations & Inventory."
5. Go to Asset, enter name and desc.
6. Go to spare parts tab.
7. Click on the + icon to select the item added on step 3 and 4.
8. Change status to Active.
9. Go to Work order tracking.
10. Create new one with following details :
    a. Add desc.
    b. Add Asset created on step 5,6,7
    c. Go to plan tab.
    d. Click on materials 
    e. Click on the + icon to add item with storeroom and change the quantity to ex . 10.00.
    f. Add the assignment and approve the WO.

**Steps:**

1. Login to technician app with the technician assigned to work order.
2. Click on "My Schedule" tile.
3. Find the work order created in pre-condition steps in "Assigned Work" filter.
4. Click the chevron on the work order card to open WO details page.
5. Click on report work page icon.
6. Go to Materials used and click on the 3 dots .
7. Click on "Select asset spare parts".
8. Click on the chevron besides it .
9. Click on "Select asset" by clicking on the chevron.
10. From lookup we can select the asset created with spare part associated with it from pre-requisite.

**Result:**

The contents to be verified till step 8 for Select asset spare parts not associated.

- The "Select asset spare parts" should be visible when clicked onn 3 dots of material used section.
- There should be header displayed as "Select spare parts".
- Asset name and Asset description should be displayed on the very left side of the page below header .
- In the right corner in the same line of Asset name and Asset description , "Select Asset" will be displayed along with chevron .
- When clicked on Select asset chevron , lookup should open with all item entries to be selected or can be typed to search .

The contents to be verified from step 9 for Select asset spare parts associated.

- The item name with storeroom should be present, "-" and "+" button should be present with quantity field along with checkbox besides it.
- Multiple checkbox can be selected.
- Confirm selection button should be disabled until any selection is made.
- Confirm selection button should be in the blue color.

## Scenario 2 - Verify when no spare part is associated with asset from manage on select spare part page through report work page 

**Pre-condition:**

1. Login to Maximo/Manage application as admin.
2. Go to WO tracking.
3. Approve WO and add assignment.

**Steps:**

1. Login to technician app with the technician assigned to work order.
2. Click on "My Schedule" tile.
3. Find the work order created in pre-condition steps in "Assigned Work" filter.
4. Click the chevron on the work order card to open WO details page.
5. Click on report work page icon.
6. Go to Materials used and click on the 3 dots .
7. Verify "Select spare parts" header.
8. Click on "Select asset spare parts".
9. Click on the chevron besides it .
10. Verify that "No Spare part found" on the page when no spare part is selected with it.

**Result:**

- There should be "No Spare part found" message displayed when no spare asset part is associated with WO .

## Scenario 3 - Verify spare asset parts can be added from report work page 

**Pre-condition:**

1. Login to Maximo/Manage application as admin.
2. Go to item master.
3. Add item , desc , Add items to storeroom
4. Change status to Active and click on the checkbox of "Roll new status to Organizations & Inventory."
5. Go to Asset, enter name and desc.
6. Go to spare parts tab.
7. Click on the + icon to select the item added on step 3 and 4.
8. Change status to Active.
9. Go to Work order tracking.
10. Create new one with following details :
    a. Add desc.
    b. Add Asset created on step 5,6,7
    c. Go to plan tab.
    d. Click on materials
    e. Click on the + icon to add item with storeroom and change the quantity to ex . 10.00.
    f. Add the assignment and approve the WO.

**Steps:**

1. Login to technician app with the technician assigned to work order.
2. Click on "My Schedule" tile.
3. Find the work order created in pre-condition steps in "Assigned Work" filter.
4. Click the chevron on the work order card to open WO details page.
5. Click on report work page icon.
6. Go to Materials used and click on the 3 dots .
7. Click on "Select asset spare parts".
8. Click on the chevron besides it.
9. Click on the checkbox next to the item . 
10. Click on "Confirm Selection".
11. Verify that spare part is added under material used section with quantity, item name and storeroom name.

**Result:**

- The spare part should be added under material used section with quantity, item name and storeroom name in the report work page.

## Scenario 4 - Verify when we update new spare part from select asset lookup then the existing asset with spare part should be removed

**Pre-condition:**

1. Login to Maximo/Manage application as admin.
2. Go to item master.
3. Add item , desc , Add items to storeroom
4. Change status to Active and click on the checkbox of "Roll new status to Organizations & Inventory."
5. Go to Asset, enter name and desc.
6. Go to spare parts tab.
7. Click on the + icon to select the item added on step 3 and 4.
8. Change status to Active.
9. Go to Work order tracking.
10. Create new one with following details :
    a. Add desc.
    b. Add Asset created on step 5,6,7
    c. Go to plan tab.
    d. Click on materials
    e. Click on the + icon to add item with storeroom and change the quantity to ex . 10.00.
    f. Add the assignment and approve the WO.

**Steps:**

1. Login to technician app with the technician assigned to work order.
2. Click on "My Schedule" tile.
3. Find the work order created in pre-condition steps in "Assigned Work" filter.
4. Click the chevron on the work order card to open WO details page.
5. Click on report work page icon.
6. Go to Materials used and click on the 3 dots .
7. Verify "Select spare parts" header.
8. Click on "Select asset spare parts".
9. Click on the chevron besides it .
10. Verify that the asset exists on the page made in the pre-requisite.
11. Click on "Select asset".
12. Verify that select asset lookup open.
13. Select new asset with spare part associated with it first.
14. Click to confirm .
15. Verify that new asset is added on the page and the existing is removed.
16. Click on the checkbox.
17. Click on Confirm selection button.
18. Verify that asset is added on the report work page .
19. Now again go to "Select asset spare parts" page.
20. Click on "Select Asset".
21. Select value from lookup which has no spare part associated with it.
22. Verify that the page should display "No spare part found ".

**Result:**

- The existing spare part should be replaced when added new one from "Select asset" page through report work page.
- When selecting spare part with no value associated, it should display "No spare part found".

## Scenario 5 - Verify that the value of spare part can be added/updated from create work page.

**Pre-condition:**

1. Login to Maximo/Manage application as admin.
2. Go to item master.
3. Add item , desc , Add items to storeroom
4. Change status to Active and click on the checkbox of "Roll new status to Organizations & Inventory."
5. Go to Asset, enter name and desc.
6. Go to spare parts tab.
7. Click on the + icon to select the item added on step 3 and 4.
8. Change status to Active.
9. Go to Work order tracking.
10. Create new one with following details :
    a. Add desc.
    b. Add Asset created on step 5,6,7
    c. Go to plan tab.
    d. Click on materials
    e. Click on the + icon to add item with storeroom and change the quantity to ex . 10.00.
    f. Add the assignment and approve the WO.

**Steps:**

1. Login to technician app with the technician assigned to work order.
2. Click on "My Schedule" tile.
3. Click on + icon and click on "Create work order"
4. Enter Mandatory details and scroll down.
5. In Asset add the new spare part created from lookup or remove the existing one to add.
6. Click on Save button.
7. Go to report work page . 
8. Go to Materials used and click on the 3 dots.
9. Select spare asset parts.
10. Verify that spare part added should be displayed on the select spare part page.

**Result:**

-The spare part can be added/edited from create wo page through asset field.

## Scenario 6 - Verify that when added asset with spare parts into the follow up work order, it should display assets on the child follow up wo in report work page 

**Pre-condition:**

1. Login to Maximo/Manage application as admin.
2. Go to item master.
3. Add item , desc , Add items to storeroom
4. Change status to Active and click on the checkbox of "Roll new status to Organizations & Inventory."
5. Go to Asset, enter name and desc.
6. Go to spare parts tab.
7. Click on the + icon to select the item added on step 3 and 4.
8. Change status to Active.

**Steps:**

1. Login to technician app with the technician assigned to work order.
2. Click on "My Schedule" tile.
3. Find the work order created in pre-condition steps in "Assigned Work" filter.
4. Click the chevron on the work order card to open WO details page.
5. Click on the follow up.
6. Click on "Create follow up work".
7. Scroll down and add asset with spare parts from pre-requisite.
8. Click on Save .
9. Verify that after save it is moved to the create followup work page.
10. Click on the chevron.
11. Click on the report work page. 
12. Go to Materials used and click on the 3 dots. 
13. Select spare asset parts.
14. Verify that added asset should be present on the select spare part page of child follow up wo.

**Result:**

- The child follow up wo should contain the asset with spare parts added from create follow up wo of parent wo .

## Scenario 7 - Verify that asset spare parts quantity can be increased/decreased

**Pre-condition:**

1. Login to Maximo/Manage application as admin.
2. Go to item master.
3. Add item , desc , Add items to storeroom
4. Change status to Active and click on the checkbox of "Roll new status to Organizations & Inventory."
5. Go to Asset, enter name and desc.
6. Go to spare parts tab.
7. Click on the + icon to select the item added on step 3 and 4.
8. Change status to Active.
9. Go to Work order tracking.
10. Create new one with following details :
    a. Add desc.
    b. Add Asset created on step 5,6,7
    c. Go to plan tab.
    d. Click on materials
    e. Click on the + icon to add item with storeroom and change the quantity to ex . 10.00.
    f. Add the assignment and approve the WO.

**Steps:**

1. Login to technician app with the technician assigned to work order.
2. Click on "My Schedule" tile.
3. Find the work order created in pre-condition steps in "Assigned Work" filter.
4. Click the chevron on the work order card to open WO details page.
5. Click on report work page icon.
6. Go to Materials used and click on the 3 dots .
7. Click on "Select asset spare parts".
8. Click on the chevron besides it.
9. Click on the '-' button to reduce the quantity from the quantity mentioned till 0 and then click on Confirm Selection button.
10. Verify that error message is displayed "Please specify a quantity for a material issue/return" when the quantity mentioned is 0.
11. Now increase the quantity from '+' button which is more than the mentioned one in the pre-requisite.
12. Verify that the '+' button will not be allowing to increase it after wards the mentioned quantity.

**Result:** 

- The - button should allow to decrease the quantity and for 0 quantity, it should show error message "Please specify a quantity for a material issue/return".
- The + icon will not be allowing above the quantity mentioned in the pre-requisite.


## Scenario 8 - Verify all the above test scenarios on mobile devices in online and offline mode

**Pre-condition:**

Pre-conditions as specified for above mentioned test scenarios.

**Steps:**

Perform steps as specified for above mentioned test scenarios.

**Results:**

The application should behave as per expectations for all above mentioned test scenarios on mobile devices in online and offline mode.