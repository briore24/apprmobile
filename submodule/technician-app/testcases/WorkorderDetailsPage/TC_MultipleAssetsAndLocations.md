# Multi Assets and Locations touch points includes create follow up WO, Inspection , Service Address , Meter Readings , Map view with Assets, Navigate to Asset app / Inspection App

These test cases will verify the navigation from Technician app Multi Assets and Locations page to Inspections app/Multi Assets and locations Map view/ Asset App and back to Technician app on work order details in web app, online and offline mode on mobile containers.

These test cases will cover functionalities of following user stories:

- MASR-1317:  Review MultiAsset details and related touchpoints
- MASR-495: Add the Map Touchpoint to the Multi Asset Location Section so Technician can Visualize a single/multiple asset or location on the map

**Design URL:**

- <https://www.figma.com/design/RAPUxZUIzaPZonCgW1udeP/Technician-%26-Assets?m=auto&node-id=27267-139513&t=46M0oxYNb6Er81zf-1>
- <https://www.figma.com/design/RAPUxZUIzaPZonCgW1udeP/Technician---Assets?node-id=27279-218200&t=58VknrtM6E58WKXt-0>

**Note:**

- In case of web app, all transactions are saved in database and refreshed on the UI in real time.
- In case of online mode on mobile containers, transactions are sent to the database for saving instantly but UI is not refreshed from database until 'Check Updates' button is clicked.
- In case of offline mode on mobile containers, transactions are queued for syncing to the database and synced to database once device is online. UI is not refreshed from database until 'Check Updates' button is clicked.
- All server side error messages will be displayed in error page on devices post syncing with server and tapping on 'Check Updates' button in both online and offline modes.

## Scenario 1 - Verify availability of "Multiple assets and locations" list item/tile on work order details page and verify availibility/unavailibility of badge count

**Pre-condition:**

1. Login with Admin credentials in Maximo classic/Manage.
2. Go to work order tracking application.
3. Create a new work order.
4. Add the sequence to the assets under Multiple Assets,Locations and CIs.
5. Add multi asset records under multiassetlocci. 
6. Add multi location records without asset under multiassetlocci.
7. Add assignments for labor and approve the WO.

**Steps:**

1. Login to technician app with the technician assigned to work order.
2. Click on "My Schedule" tile.
3. Find the work order created in pre-condition steps in "Assigned Work" filter.
4. Click on the work order card to open wo details page.
5. Verify availability of "Multiple assets and locations" list item/tile.
6. Verify availability of badge count on "Multiple assets and locations" list item/tile.

**Result:**

- "Multiple assets and locations" list item/tile should be available on work order details page.
- Badge count should be available on "Multiple assets and locations" list item/tile.
- Badge count value should be 2 as per asset and locations is associated with the work order of multiassetlocCi.

## Scenario 2 - Verify "Multiple assets and locations" page of WorkOrder

**Pre-condition:**

1. Login with Admin credentials in Maximo classic/Manage.
2. Go to work order tracking application.
3. Create a new work order.
4. Add the sequence to the assets under Multiple Assets,Locations and CIs.
5. Add multi asset records under multiassetlocci.
6. Add multi location records without asset under multiassetlocci.
7. Add assignments for labor and approve the WO.

**Steps:**

1. Login to technician app with the technician assigned to work order.
2. Click on "My Schedule" tile.
3. Find the work order created in pre-condition steps in "Assigned Work" filter.
4. Click on the work order card to open wo details page.
5. Verify availability of "Multiple assets and locations" list item/tile.
6. Verify availability of badge count on "Multiple assets and locations" list item/tile.
7. Click on "Multiple assets and locations" list item/tile.

**Result:**

- "Multiple assets and locations" page should be displayed with Assets , Locations , No support of CI.
- "Multiple assets and locations" page should displayed record count. In above case count is 2.
- Search Icon, Filter Icon, Sort options should be displayed on "Multiple assets and locations" page.
- Asset should have asset icon with asset details as per figma.
- Location should have location icon with location details as per figma.
- Mark completed icon on assets and locations.
- "Multiple assets and locations" page should not displayed any record related to CI.

## Scenario 3 - Verify "Multiple assets and locations" page had Create Follow up WO ,Inspection, Service Address, Meter touch points

**Pre-condition:**

1. Login with Admin credentials in Maximo classic/Manage.
2. Go to work order tracking application.
3. Create a new work order.
4. Add the sequence to the assets under Multiple Assets,Locations and CIs.
5. Add multi asset (asset which had meter associated with it) records under multiassetlocci and add Inspection form and results.
6. Add multi location (location which had meter associated with it) records without asset under multiassetlocci and add Inspection form and results.
7. Add multi asset/location with service address under multiassetlocci.
8. Add assignments for labor and approve the WO.

**Steps:**

1. Login to technician app with the technician assigned to work order.
2. Click on "My Schedule" tile.
3. Find the work order created in pre-condition steps in "Assigned Work" filter.
4. Click on the work order card to open wo details page.
5. Verify availability of "Multiple assets and locations" list item/tile.
6. Verify availability of badge count on "Multiple assets and locations" list item/tile.
7. Click on "Multiple assets and locations" list item/tile.

**Result:**

- "Multiple assets and locations" page should be displayed with Assets , Locations , No support of CI.
- "Multiple assets and locations" page should displayed record count. In above case count is 3.
- Search Icon, Filter Icon, Sort options should be displayed on "Multiple assets and locations" page.
- Asset should have asset icon with asset details as per figma.
- Location should have location icon with location details as per figma.
- Mark completed icon on assets and locations.
- Asset should have Meter,Inspection,Service Address,Create Follow WO touch points displayed with respective asset and location.Refer figma for UI.

## Scenario 4 - Verify "Multiple assets and locations" page navigate to Inspection app and back to same page.

**Pre-condition:**

1. Login with Admin credentials in Maximo classic/Manage.
2. Go to work order tracking application.
3. Create a new work order.
4. Add the sequence to the assets under Multiple Assets,Locations and CIs.
5. Add multi asset (asset which had meter associated with it) records under multiassetlocci and add Inspection form and results.
6. Add multi location (location which had meter associated with it) records without asset under multiassetlocci and add Inspection form and results.
7. Add multi asset/location with service address under multiassetlocci.
8. Add assignments for labor and approve the WO.

**Steps:**

1. Login to technician app with the technician assigned to work order.
2. Click on "My Schedule" tile.
3. Find the work order created in pre-condition steps in "Assigned Work" filter.
4. Click on the work order card to open wo details page.
5. Verify availability of "Multiple assets and locations" list item/tile.
6. Verify availability of badge count on "Multiple assets and locations" list item/tile.
7. Click on "Multiple assets and locations" list item/tile.
8. Select Asset/Location which as Inspection icon.
9. Click on Inspection icon.

**Result:**

- Redirected to Inspection app and form should be displayed.
- When user complete a form filling then user is back to "Multiple assets and locations" page of technician.
- Without complete a form then click on back in Inspection app then back to  "Multiple assets and locations" page of technician.


## Scenario 5 - Verify "Multiple assets and locations" page navigate to Asset app and back to same page.

**Pre-condition:**

1. Login with Admin credentials in Maximo classic/Manage.
2. Go to work order tracking application.
3. Create a new work order.
4. Add the sequence to the assets under Multiple Assets,Locations and CIs.
5. Add multi asset records under multiassetlocci. 
6. Add assignments for labor and approve the WO.

**Steps:**

1. Login to technician app with the technician assigned to work order.
2. Click on "My Schedule" tile.
3. Find the work order created in pre-condition steps in "Assigned Work" filter.
4. Click on the work order card to open wo details page.
5. Verify availability of "Multiple assets and locations" list item/tile.
6. Verify availability of badge count on "Multiple assets and locations" list item/tile.
7. Click on "Multiple assets and locations" list item/tile.
8. Select Asset and click on chevron Icon of it.

**Result:**

- Redirected to Asset app and asset details should be displayed. If Asset is present in asset app else no record found information.
- When user see asset details and click on back button then "Multiple assets and locations" page of technician should displayed.

## Scenario 6 - Verify "Multiple assets and locations" which only location then chevron icon will not be displayed.

**Pre-condition:**

1. Login with Admin credentials in Maximo classic/Manage.
2. Go to work order tracking application.
3. Create a new work order.
4. Add the sequence to the assets under Multiple Assets,Locations and CIs.
5. Add multi location only without asset records under multiassetlocci.
6. Add assignments for labor and approve the WO.

**Steps:**

1. Login to technician app with the technician assigned to work order.
2. Click on "My Schedule" tile.
3. Find the work order created in pre-condition steps in "Assigned Work" filter.
4. Click on the work order card to open wo details page.
5. Verify availability of "Multiple assets and locations" list item/tile.
6. Verify availability of badge count on "Multiple assets and locations" list item/tile.
7. Click on "Multiple assets and locations" list item/tile.
8. Select location which had no asset with it.

**Result:**

- Only Location should not have a chevron icon on "Multiple assets and locations" page.

## Scenario 7 - Verify "Multiple assets and locations" page asset/location had meters and enter meter reading.

**Pre-condition:**

1. Login with Admin credentials in Maximo classic/Manage.
2. Go to work order tracking application.
3. Create a new work order.
4. Add the sequence to the assets under Multiple Assets,Locations and CIs.
5. Add multi asset (asset which had meter associated with it) records under multiassetlocci.
6. Add multi location (location which had meter associated with it) records without asset under multiassetlocci.
7. Add assignments for labor and approve the WO.

**Steps:**

1. Login to technician app with the technician assigned to work order.
2. Click on "My Schedule" tile.
3. Find the work order created in pre-condition steps in "Assigned Work" filter.
4. Click on the work order card to open wo details page.
5. Verify availability of "Multiple assets and locations" list item/tile.
6. Verify availability of badge count on "Multiple assets and locations" list item/tile.
7. Click on "Multiple assets and locations" list item/tile.
8. Select Asset/Location which as Meter icon.
9. Click on Meter icon.

**Result:**

- Meter drawer associated with asset/location should be displayed.
- User should be able to enter meter reading and able to displayed old meter reading. All meter had shared resources, data should be enter is common for same meter from any place.

## Scenario 8 - Verify "Multiple assets and locations" page asset/location all record had create follow up WO displayed.

**Pre-condition:**

1. Login with Admin credentials in Maximo classic/Manage.
2. Go to work order tracking application.
3. Create a new work order.
4. Add the sequence to the assets under Multiple Assets,Locations and CIs.
5. Add multi asset records under multiassetlocci.
6. Add multi location records without asset under multiassetlocci.
7. Add assignments for labor and approve the WO.

**Steps:**

1. Login to technician app with the technician assigned to work order.
2. Click on "My Schedule" tile.
3. Find the work order created in pre-condition steps in "Assigned Work" filter.
4. Click on the work order card to open wo details page.
5. Verify availability of "Multiple assets and locations" list item/tile.
6. Verify availability of badge count on "Multiple assets and locations" list item/tile.
7. Click on "Multiple assets and locations" list item/tile.
8. Select Asset/Location.
9. Click on create follow up WO.

**Result:**

- Create follow-up WO page in edit.
- WO data will be prefilled with details.
- New section of "Parent assets and locations"
- Asset and/or location details should be displayed of multiassetlocation with Delete icon by default.
- When click on save then follow up WO toast is displayed and user is back to "Multiple assets and locations" page. Again back to WO details, check a new follow up WO under follow up WO.
- When user click on back icon without any changes then follow up WO should be created and user is back to "Multiple assets and locations" page.

## Scenario 9 - Verify that when click on service address icon on "Multiple assets and locations" page then Map view open and displayed assets/locations in UI of Map.

**Pre-condition:**

1. Login with Admin credentials in Maximo classic/Manage.
2. Go to work order tracking application.
3. Create a new work order.
4. Add the sequence to the assets under Multiple Assets,Locations and CIs.
5. Add multi asset (asset which had service address associated with it) records under multiassetlocci.
6. Add multi location (location which had service address associated with it) records without asset under multiassetlocci.
7. Add assignments for labor and approve the WO.

**Steps:**

1. Login to technician app with the technician assigned to work order.
2. Click on "My Schedule" tile.
3. Find the work order created in pre-condition steps in "Assigned Work" filter.
4. Click on the work order card to open wo details page.
5. Verify availability of "Multiple assets and locations" list item/tile.
6. Verify availability of badge count on "Multiple assets and locations" list item/tile.
7. Click on "Multiple assets and locations" list item/tile.
8. Click on service address icon of asset. / Click on service address icon of location.

**Result:**

- "Multiple assets and locations" page should be displayed with Assets , Locations , No support of CI.
- Asset should have Service Address Icon as per figma.Refer figma for UI. / Location should have Service Address Icon as per figma.Refer figma for UI.
- Map view should be open and location/asset Icon in Map area should be displayed.

## Scenario 10 - Verify "Multiple assets and locations" in Map view open and displayed assets/locations in UI.

1. Login with Admin credentials in Maximo classic/Manage.
2. Go to work order tracking application.
3. Create a new work order.
4. Add the sequence to the assets under Multiple Assets,Locations and CIs.
5. Add multi asset (asset which had meter associated with it) records under multiassetlocci and add Inspection form and results.
6. Add multi location (location which had meter associated with it) records without asset under multiassetlocci and add Inspection form and results.
7. Add multi asset/location with service address under multiassetlocci.
8. Add assignments for labor and approve the WO.

**Steps:**

1. Login to technician app with the technician assigned to work order.
2. Click on "My Schedule" tile.
3. Find the work order created in pre-condition steps in "Assigned Work" filter.
4. Click on the work order card to open wo details page.
5. Verify availability of "Multiple assets and locations" list item/tile.
6. Verify availability of badge count on "Multiple assets and locations" list item/tile.
7. Click on "Multiple assets and locations" list item/tile.
8. Click on Map view.
9. Search asset/location which we had used/created in above manage step.

**Result:**

- List with asset,location and Map view should be displayed.
- "Multiple assets and locations" page of Map view should be displayed with Assets , Locations , No support of CI.
- Search Icon, scanner Icon should be displayed on "Multiple assets and locations" page of Map view.
- Asset should have asset icon with asset details as per figma.
- Location should have location icon with location details as per figma.
- Mark completed icon on assets and locations.
- Asset/Location should have Meter,Inspection,Service Address,Create Follow WO touch points displayed with respective asset and location.Refer figma for UI.

Note : Above 1 to 8 Test cases should be executed from Map view of Multiple assets and locations page.

## Scenario 11 - Verify the UI of the cards/pages/views for fonts, font sizes, font color, text completeness, alignment, positioning etc. is correct and as per design on mobile and other small screen devices for all supported form factors

**Pre-condition:**

Pre-conditions as specified for above mentioned test scenarios.

**Steps:**

1. Login to Maximo mobile app with the credentials of user assigned to work order.
2. Navigate to screens/pages/cards of above mentioned scenarios.
3. Verify UI of the cards/pages for fonts, font sizes, font color, text completeness, alignment, positioning etc. is correct and as per design.

**Results:**

- UI of the cards/pages for fonts, font sizes, font color, text completeness, alignment, positioning etc. should be correct and as per design.
- There should be no UI related issues.
- The application should behave as per expectations for all above mentioned scenarios on mobile and other small screen devices for all supported form factors.
- The UI should be as per design for respective form factor.

## Scenario 12 - Verify all the above test scenarios on mobile devices in online and offline mode

**Pre-condition:**

Pre-conditions as specified for above mentioned test scenarios.

**Steps:**

Perform steps as specified for above mentioned test scenarios.

**Results:**

The application should behave as per expectations for all above mentioned test scenarios on mobile devices in online and offline mode.
