# Start travel and non-travel labor transactions functionality on WO list and WO details pages

These test cases will verify starting travel and non-travel labor transactions functionality on WO list and WO details pages for Technician web app, online and offline mode on mobile containers.

These test cases will cover functionalities of following user stories:

- Graphite-16784: Eli can start a non-travel work order
- Graphite-9516: Eli can select a work order to start travel
- Graphite-25842: Eli can select a work order to stop travel
- GRAPHITE-47153: Eli should not be able to start multiple timers at the same time
- GRAPHITE-33940: Review Start travel x Start work icons
- GRAPHITE-31463: When Eli taps Stop Travel, he should remain on the same page
- GRAPHITE-30902: Eli should go to the work details page after clicking on Start Work button in the list page
- GRAPHITE-75990:Add sigoption to make FSM flow optional
- MASR-497: Travel button configurable to be invoked on demand irrespective of gps location from store

**Design URL:**

- <https://ibm.invisionapp.com/d/?redirHash=#/console/15360095/319573756/preview?scrollOffset=290>
- <https://ibm.invisionapp.com/share/WCO04JB7AHK#/screens>
- <https://ibm.invisionapp.com/share/8CO22AJFQH6#/screens>

**Note:**

- In case of web app, all transactions are saved in database and refreshed on the UI in real time.
- In case of online mode on mobile containers, transactions are sent to the database for saving instantly but UI is not refreshed from database until 'Check Updates' button is clicked.
- In case of offline mode on mobile containers, transactions are queued for syncing to the database and synced to database once device is online. UI is not refreshed from database until 'Check Updates' button is clicked.
- All server side error messages will be displayed in error page on devices post syncing with server and tapping on 'Check Updates' button in both online and offline modes.

## Scenario 1 - Verify that "Start travel" button is not displayed when the organization does not use X and Y defined in the service address, geo-location tracking is turned off on the browser/device and when technician denies permission to use location services on browser/device

**Pre-condition:**

1. Login with Admin credentials in Maximo classic/Manage.
2. Goto Organization app.
3. Click on the organization. Select the Organization.
4. Click on the service address options.
5. Select the radio button "Use an external or GIS address system".
6. Create a work order and associate the service address to it.
7. Add assignments for labor and approve the work order.

**Steps:**

1. Launch the Maximo Mobile application using technician credentials.
2. Click on settings in the browser/device and turn the geo-location to OFF.
3. Deny permission to use location services on browser/devices.
4. Click on "My Schedule" tile to open the list of assigned work orders.
5. Verify that "Start travel" button is not displayed on work order cards.

**Result:**

The "Start travel" button should not be displayed on work order cards.

## Scenario 2 - Verify that "Start travel" button is displayed when the organization uses X and Y defined in the service address, when the geo-location on the browser/device is turned ON and when technician grants permission to use location services on browser/device

**Pre-condition:**

1. Login with Admin credentials in Maximo classic/Manage.
2. Goto service address and create a new service address by adding the longitude and latitude.
3. Create a work order and associate the service address to it.
4. Add assignments for labor and approve the work order. 
5. Accept & Reject Button visible when "Enable Assignment flow" property will be enabled. 
6. if "Enable Assignment flow" property will be Disabled user can directly See "Start Work" Button.
**Steps:**

1. Click on settings in the browser/device and turn the geo-location to ON.
2. Launch the Maximo Mobile application using technician credentials.
3. Grant permission to use location services on browser/devices.
4. Click on "My Schedule" tile to open the list of assigned work orders and click on 'Accept' button.
5. Verify that "Start travel" button is displayed on work order cards.

**Result:**

- "Start travel" button with "Start travel" label should be displayed on webapp and large screen devices. "Start travel" button without "Start travel" label should be displayed on small/mid screen devices.
- The icon on "Start travel" button should be "carbon:road".

## Scenario 3 - Verify that "Start travel" button is displayed when property mxe.mobile.travel.prompt is set to 1

**Pre-condition:**

1. Login with Admin credentials in Maximo classic/Manage.
2. Goto service address and create a new service address by adding the longitude and latitude.
3. Create a work order and associate the service address to it.
4. Add assignments for labor and approve the work order.
5. Goto Global properties and set the property mxe.mobile.travel.prompt=1. 
6. Accept & Reject Button visible when "Enable Assignment flow" property will be enabled. 
7. if "Enable Assignment flow" property will be Disabled user can directly See "Start Work" Button.
**Steps:**

1. Launch the Maximo Mobile application using technician credentials.
2. Click on "My Schedule" tile to open the list of assigned work orders and click on 'Accept' button.
3. Find the created work order in pre-condition steps in WO list and click on 'Accept' button.
4. Verify that "Start travel" button is displayed.

**Result:**

- "Start travel" button with "Start travel" label should be displayed on webapp and large screen devices. "Start travel" button without "Start travel" label should be displayed on small/mid screen devices.
- The icon on "Start travel" button should be "carbon:road".

## Scenario 4 - Verify that "Start work" button is displayed when property mxe.mobile.travel.prompt is set to 0

**Pre-condition:**

1. Login with Admin credentials in Maximo classic/Manage.
2. Set "maximo.mobile.usetimer" system property value to 1.
3. Goto service address and create a new service address by adding the longitude and latitude.
4. Create a work order and associate the service address to it.
5. Add assignments for labor and approve the work order.
6. Goto Global properties and set the property mxe.mobile.travel.prompt=0. 
7. Accept & Reject Button visible when "Enable Assignment flow" property will be enabled. 
8. if "Enable Assignment flow" property will be Disabled user can directly See "Start Work" Button.
**Steps:**

1. Launch the Maximo Mobile application using technician credentials.
2. Click on "My Schedule" tile to open the list of assigned work orders.
3. Find the created work order in pre-condition steps in WO list and click on 'Accept' button.
4. Verify that "Start work with timer" button is displayed.

**Result:**

- "Start work with timer" button with label "Start work" should be displayed on webapp and large screen devices. "Start work with timer" button without "Start work" label should be displayed on small/mid screen devices.
- The icon on "Start work with timer" button should be "maximo:start-work".

## Scenario 5 - Verify that "Start travel" button is displayed when technician region is set to UK (metric region), properties mxe.mobile.travel.prompt=1 and mxe.mobile.travel.radius=1 and technician is more than 1 km away from work order service address

**Pre-condition:**

1. Login with Admin credentials in Maximo classic/Manage.
2. Goto service address and create a new service address by adding the longitude and latitude.
3. Create a work order and associate the service address to it.
4. Add assignments for labor and approve the work order.
5. Goto Global properties and set the properties mxe.mobile.travel.prompt=1 and mxe.mobile.travel.radius=1.
6. Set technician locale/region to be UK (metric region). 
7. Accept & Reject Button visible when "Enable Assignment flow" property will be enabled. 
8. if "Enable Assignment flow" property will be Disabled user can directly See "Start Work" Button.
**Steps:**

1. Launch the Maximo Mobile application using technician credentials.
2. Click on "My Schedule" tile to open the list of assigned work orders.
3. Find the created work order in pre-condition steps in WO list and click on 'Accept' button.
4. Verify that "Start travel" button is displayed if technician is more than 1 km away from work order service address.

**Result:**

- "Start travel" button should be displayed for all work orders where technician is more than 1 km away from work orders service address.
- "Start travel" button with "Start travel" label should be displayed on webapp and large screen devices. "Start travel" button without "Start travel" label should be displayed on small/mid screen devices.
- The icon on "Start travel" button should be "carbon:road".

## Scenario 6 - Verify that "Start travel" button is displayed when technician region is set to US (imperial region), properties mxe.mobile.travel.prompt=1 and mxe.mobile.travel.radius=1 and technician is more than 1 mile away from work order service address

**Pre-condition:**

1. Login with Admin credentials in Maximo classic/Manage.
2. Goto service address and create a new service address by adding the longitude and latitude.
3. Create a work order and associate the service address to it.
4. Add assignments for labor and approve the work order.
5. Goto Global properties and set the properties mxe.mobile.travel.prompt=1 and mxe.mobile.travel.radius=1.
6. Set technician locale/region to be US (imperial region).
7. Accept & Reject Button visible when "Enable Assignment flow" property will be enabled. 
8. if "Enable Assignment flow" property will be Disabled user can directly See "Start Work" Button.

**Steps:**

1. Launch the Maximo Mobile application using technician credentials.
2. Click on "My Schedule" tile to open the list of assigned work orders.
3. Find the created work order in pre-condition steps in WO list and click on 'Accept' button.
4. Verify that "Start travel" button is displayed if technician is more than 1 mile away from work order service address.

**Result:**

- "Start travel" button should be displayed for all work orders where technician is more than 1 mile away from work orders service address.
- "Start travel" button with "Start travel" label should be displayed on webapp and large screen devices. "Start travel" button without "Start travel" label should be displayed on small/mid screen devices.
- The icon on "Start travel" button should be "carbon:road".

## Scenario 7 - Verify technician can click on start travel button to start labor transaction of work type as "Travel time"

**Pre-condition:**

1. Login with Admin credentials in Maximo classic/Manage.
2. Goto service address and create a new service address by adding the longitude and latitude.
3. Create a work order and associate the service address to it.
4. Add assignments for labor and approve the work order.
5. Goto Global properties and set the property mxe.mobile.travel.prompt=1. 
6. Accept & Reject Button visible when "Enable Assignment flow" property will be enabled. 
7. if "Enable Assignment flow" property will be Disabled user can directly See "Start Work" Button.
**Steps:**

1. Launch the Maximo Mobile application using technician credentials.
2. Click on "My Schedule" tile to open the list of assigned work orders.
3. Find the created work order in pre-condition steps in WO list and click on 'Accept' button.
4. Click on the "Start travel" for the created work order.
5. Verify that work order status is changed to "In progress" and labor transaction is started with work type as "Travel time".

**Result:**

- The user can click on "Start travel" button.
- The labor transaction should start with work type of "Travel time".
- The work order status should be changed to "In progress" when MAXVARINPUT.STARTTIMERINPRG=1.

## Scenario 8 - Verify that "Start travel" button changes to "Stop travel" button when technician taps on "Start travel" button

**Pre-condition:**

1. Login with Admin credentials in Maximo classic/Manage.
2. Goto service address and create a new service address by adding the longitude and latitude.
3. Create a work order and associate the service address to it.
4. Add assignments for labor and approve the work order.
5. Goto Global properties and set the property mxe.mobile.travel.prompt=1. 
6. Accept & Reject Button visible when "Enable Assignment flow" property will be enabled. 
7. if "Enable Assignment flow" property will be Disabled user can directly See "Start Work" Button.
**Steps:**

1. Launch the Maximo Mobile application using technician credentials.
2. Click on "My Schedule" tile to open the list of assigned work orders.
3. Find the created work order in pre-condition steps in WO list and click on 'Accept' button.
4. Click on the "Start travel" for the created work order.
5. Verify that "Start travel" button changes to "Stop travel" button.

**Result:**

"Start travel" button should change to "Stop travel" button.

## Scenario 9 - Verify that "Start travel" or "Start work" button is displayed on work order card depending on the service address location is outside or inside the value set in property "mxe.mobile.travel.radius". Also, verify that "Stop travel" button is displayed when labor transaction is started with work type as "Travel time"

**Pre-condition:**

1. Login with Admin credentials in Maximo classic/Manage.
2. Set "maximo.mobile.usetimer" system property value to 1.
3. Goto service address and create a new service address by adding the longitude and latitude.
4. Create a work order and associate the service address to it.
5. Add assignments for labor and approve the work order.
6. Goto Global properties and set the property mxe.mobile.travel.prompt=1 and mxe.mobile.travel.radius=5. 
7. Accept & Reject Button visible when "Enable Assignment flow" property will be enabled. 
8. if "Enable Assignment flow" property will be Disabled user can directly See "Start Work" Button.
**Steps:**

1. Launch the Maximo Mobile application using technician credentials.
2. Click on "My Schedule" tile to open the list of assigned work orders.
3. Find the created work order in pre-condition steps in WO list and click on 'Accept' button.
4. Click on the "Start travel" button for the created work order so that a new labor transaction is started with work type as "Travel time".
5. Click on "Stop travel" button.
6. Verify that "Start travel" or "Start work with timer" button is displayed on work order card depending on the service address location is outside or inside the value set in property "mxe.mobile.travel.radius".

**Result:**

- "Start travel" button is displayed on work order card if work order service address location is outside the value set in property "mxe.mobile.travel.radius".
- The icon on "Start travel" button should be "carbon:road".
- "Start work with timer" button is displayed on work order card if work order service address location is inside the value set in property "mxe.mobile.travel.radius".
- The icon on "Start work with timer" button should be "maximo:start-work".
- "Stop travel" button should be displayed when labor transaction is started with work type as "Travel time".

## Scenario 10 - Verify that "Pause work" button is not displayed on the work order list and details pages when labor transaction is started with work type as "Travel time"

**Pre-condition:**

1. Login with Admin credentials in Maximo classic/Manage.
2. Goto service address and create a new service address by adding the longitude and latitude.
3. Create a work order and associate the service address to it.
4. Add assignments for labor and approve the work order.
5. Goto Global properties and set the property mxe.mobile.travel.prompt=1. 
6. Accept & Reject Button visible when "Enable Assignment flow" property will be enabled. 
7. if "Enable Assignment flow" property will be Disabled user can directly See "Start Work" Button.
**Steps:**

1. Launch the Maximo Mobile application using technician credentials.
2. Click on "My Schedule" tile to open the list of assigned work orders.
3. Find the created work order in pre-condition steps in WO list and click on 'Accept' button.
4. Click on the "Start travel" button for the created work order so that a new labor transaction is started with work type as "Travel time".
5. Verify that "Stop travel" is primary button (blue color) for the first work order in the WO list and WO details.
6. Verify that technician can tap on "Stop travel" from the WO list and details pages.
7. Verify that "Pause work" button is not displayed on work order card and work order details.

**Result:**

- "Pause work" button should not be displayed on the work order list and details pages when labor transaction is started with work type as "Travel time".
- "Stop travel" should be primary button (blue color) for the first work order in the WO list and WO details.
- Technician should be able to tap on "Stop travel" from the WO list and details pages.

## Scenario 11 - Verify that technician stays on the same page on tapping the "send" button on Labor time confirm dialog (which opens after tapping "Stop travel" button) when "Confirm Time Calculated by Timer?" is checked in org system settings

**Pre-condition:**

1. Login with Admin credentials in Maximo classic/Manage.
2. Goto service address and create a new service address by adding the longitude and latitude.
3. Create a work order and associate the service address to it.
4. Add assignments for labor and approve the work order.
5. Goto Global properties and set the property mxe.mobile.travel.prompt=1.
6. Make sure "Confirm Time Calculated by Timer?" is checked in organization's system settings. 
7. Accept & Reject Button visible when "Enable Assignment flow" property will be enabled. 
8. if "Enable Assignment flow" property will be Disabled user can directly See "Start Work" Button.
**Steps:**

1. Launch the Maximo Mobile application using technician credentials.
2. Click on "My Schedule" tile to open the list of assigned work orders.
3. Find the created work order in pre-condition steps in WO list and click on 'Accept' button.
4. Click on the "Start travel" button for the created work order so that a new labor transaction is started with work type as "Travel time".
5. Tap on "Stop travel" button from either from WO list card or WO details.
6. Labor time confirm dialog opens, which has an option to delete the transaction.
7. If a technician  clicks on delete it is removed from report work page under labor section.
8. For the other case, click on send button.
Verify that technician stays on the same page i.e. WO list or WO details.

**Result:**

- Tap on "Stop travel" button from either from WO list card or WO details.
- Labor time confirm dialog opens, which has an option to delete the transaction.
- If a technician  clicks on delete it is removed from report work page under labor section.
- For the other case, click on send button.
- Verify that technician stays on the same page i.e. WO list or WO details.

## Scenario 12 - Verify that technician stays on the same page on tapping "Stop travel" button when "Confirm Time Calculated by Timer?" is unchecked in organization's system settings

**Pre-condition:**

1. Login with Admin credentials in Maximo classic/Manage.
2. Goto service address and create a new service address by adding the longitude and latitude.
3. Create a work order and associate the service address to it.
4. Add assignments for labor and approve the work order.
5. Goto Global properties and set the property mxe.mobile.travel.prompt=1.
6. Make sure "Confirm Time Calculated by Timer?" is unchecked in organization's system settings. 
7. Accept & Reject Button visible when "Enable Assignment flow" property will be enabled. 
8. if "Enable Assignment flow" property will be Disabled user can directly See "Start Work" Button.
**Steps:**

1. Launch the Maximo Mobile application using technician credentials.
2. Click on "My Schedule" tile to open the list of assigned work orders.
3. Find the created work order in pre-condition steps in WO list and click on 'Accept' button.
4. Click on the "Start travel" button for the created work order so that a new labor transaction is started with work type as "Travel time".
5. Tap on "Stop travel" button from either from WO list card or WO details.
6. Verify that technician stays on the same page i.e. WO list or WO details on tapping the "Stop travel" button.

**Result:**

- Technician should stay on the same page i.e. WO list or WO details on tapping the "Stop travel" button.
- In Report work page, labor transaction record should be updated with the end date and time, duration, regular hours and travel time type information.

## Scenario 19 - Verify "Pause work" and "Stop work" buttons are displayed when technician clicks on the "Start work" button and distance between technician and service address < mxe.mobile.travel.radius

**Pre-condition:**

1. Login with Admin credentials in Maximo classic/Manage.
2. Create a service address for work order with latitude and longitude (which is less than 700 miles from technician location).
3. Create a work order and associate the service address to it.
4. Add assignments for labor and approve the work order.
5. Goto Global properties and set the property mxe.mobile.travel.prompt=1 and mxe.mobile.travel.radius=700. 
6. Accept & Reject Button visible when "Enable Assignment flow" property will be enabled. 
7. if "Enable Assignment flow" property will be Disabled user can directly See "Start Work" Button.
**Steps:**

1. Launch the Maximo Mobile application using technician credentials.
2. Click on "My Schedule" tile to open the list of assigned work orders.
3. Find the created work order in pre-condition steps in WO list and click on 'Accept' button.
4. "Start work" button is displayed for the created work order.
5. Click on "Start work" button from either the WO list or details page so that a new labor transaction is started.
6. Technician is navigated to WO details page when "Start work" button is clicked from WO list.
7. Verify "Pause work" and "Stop work" buttons are displayed on WO list and WO details pages.

**Result:**

"Pause work" and "Stop work" buttons should be displayed when technician clicks on the "Start work" button and distance between technician and service address < mxe.mobile.travel.radius.

## Scenario 13 - Verify "Start travel" button is displayed when technician clicks on the "Stop travel" button and distance between technician and service address > mxe.mobile.travel.radius

**Pre-condition:**

1. Login with Admin credentials in Maximo classic/Manage.
2. Create a service address for work order with latitude and longitude (which is more than 600 miles from technician location).
3. Create a work order and associate the service address to it.
4. Add assignments for labor and approve the work order.
5. Goto Global properties and set the property mxe.mobile.travel.prompt=1 and mxe.mobile.travel.radius=600. 
6. Accept & Reject Button visible when "Enable Assignment flow" property will be enabled. 
7. if "Enable Assignment flow" property will be Disabled user can directly See "Start Work" Button.
**Steps:**

1. Launch the Maximo Mobile application using technician credentials.
2. Click on "My Schedule" tile to open the list of assigned work orders.
3. Find the created work order in pre-condition steps in WO list and click on 'Accept' button.
4. "Start travel" button is displayed for the created work order.
5. Click on "Start travel" button from either the WO list or details page so that a new labor transaction is started.
6. Tap on "Stop travel" from either the WO list or details page.
7. Verify "Start travel" button is displayed.

**Result:**

- "Start travel" button should be displayed when technician clicks on the "Stop travel" button and distance between technician and service address > mxe.mobile.travel.radius.
- "Start travel" button with "Start travel" label should be displayed on webapp and large screen devices. "Start travel" button without "Start travel" label should be displayed on small/mid screen devices.
- The icon on "Start travel" button should be "carbon:road".

## Scenario 14 - Verify that "Start work" button is displayed on the WO card in WO list and details when work order status is either of "Approved", "In progress", "Waiting on material", "Waiting on plant cond" and "Waiting to be scheduled"

**Pre-condition:**

1. Login with Admin credentials in Maximo classic/Manage.
2. Set "maximo.mobile.usetimer" system property value to 1.
3. Create a new work order, add assignments for labor.
4. Change work order status to either of "Approved", "In progress", "Waiting on material", "Waiting on plant cond" and "Waiting to be scheduled". 
5. Accept & Reject Button visible when "Enable Assignment flow" property will be enabled. 
6. if "Enable Assignment flow" property will be Disabled user can directly See "Start Work" Button.
**Steps:**

1. Launch the Maximo Mobile application using technician credentials.
2. Click on "My Schedule" tile to open the list of assigned work orders.
3. Find the created work order in pre-condition steps in WO list and click on 'Accept' button.
4. Verify that "Start work with timer" button is displayed on the WO card in WO list and details pages.
5. Verify that "Start work with timer" button has primary design(blue button) when work order is the first work order in WO list, otherwise it has the regular/secondary design.

**Result:**

- "Start work with timer" button should be displayed on the WO card in WO list and details when work order status is either of "Approved", "In progress", "Waiting on material", "Waiting on plant cond" and "Waiting to be scheduled".
- "Start work with timer" button with label "Start work" should be displayed on webapp and large screen devices. "Start work with timer" button without "Start work" label should be displayed on small/mid screen devices.
- "Start work with timer" button should have primary design(blue button) on the WO card in WO list and details when work order is the first work order in WO list, otherwise it has the regular/secondary design.
- The icon on "Start work with timer" button should be "maximo:start-work".

## Scenario 15 - Verify that "Start work" button is not displayed on WO details when the work order is in either of "Waiting on approval", "Completed", "Closed" or "Canceled" status

**Pre-condition:**

1. Login with Admin credentials in Maximo classic/Manage.
2. Create a new work order, add assignments for labor and approve the work order. 
3. Accept & Reject Button visible when "Enable Assignment flow" property will be enabled. 
4. if "Enable Assignment flow" property will be Disabled user can directly See "Start Work" Button.
**Steps:**

1. Launch the Maximo Mobile application using technician credentials.
2. Click on "My Schedule" tile to open the list of assigned work orders.
3. Find the created work order in pre-condition steps in WO list and click on 'Accept' button..
4. Change the work order status to either of "Waiting on approval", "Completed", "Closed" or "Canceled".
5. Verify that "Start work" button is not displayed on WO details.

**Result:**

"Start work" button should not be displayed on WO details when the work order is in either of "Waiting on approval", "Completed", "Closed" or "Canceled" status.

## Scenario 16 - Verify "Pause work" and "Stop work" buttons are displayed when technician clicks on the "Start work" button on WO list or WO details

**Pre-condition:**

1. Login with Admin credentials in Maximo classic/Manage.
2. Create a new work order, add assignments for labor and approve the work order. 
3. Accept & Reject Button visible when "Enable Assignment flow" property will be enabled. 
4. if "Enable Assignment flow" property will be Disabled user can directly See "Start Work" Button.
**Steps:**

1. Launch the Maximo Mobile application using technician credentials.
2. Click on "My Schedule" tile to open the list of assigned work orders.
3. Find the created work order in pre-condition steps in WO list and click on 'Accept' button.
4. Click on the "Start work" button from work order card on work order list or work order details so that a new labor transaction is started.
5. Verify "Pause work" and "Stop work" buttons are displayed.

**Result:**

"Pause work" and "Stop work" buttons should be displayed when technician clicks on the "Start work" button on WO list or WO details.

## Scenario 17 - Verify work order status is changed to "In progress" when MAXVARINPUT.STARTTIMERINPRG=1 and technician starts a new labor transaction from WO list or WO details

**Pre-condition:**

1. Login with Admin credentials in Maximo classic/Manage.
2. Create a new work order, add assignments for labor and approve the work order.
3. Go to Administration > Organizations. Search and click on organization.
4. Go to System Settings and enable the option "Automatically change the work order status to INPRG when user starts the Labor timer". 
5. Accept & Reject Button visible when "Enable Assignment flow" property will be enabled. 
6. if "Enable Assignment flow" property will be Disabled user can directly See "Start Work" Button.
**Steps:**

1. Launch the Maximo Mobile application using technician credentials.
2. Click on "My Schedule" tile to open the list of assigned work orders.
3. Find the created work order in pre-condition steps in WO list and click on 'Accept' button.
4. Click on the "Start work" button from work order card on work order list or work order details so that a new labor transaction is started.
5. Verify work order status is changed to "In progress".

**Result:**

Work order status should change to "In progress" when MAXVARINPUT.STARTTIMERINPRG=1 and technician starts a new labor transaction from WO list or WO details.

## Scenario 18 - Verify work order status remains unchanged when MAXVARINPUT.STARTTIMERINPRG=0 and technician starts a new labor transaction from WO list or WO details

**Pre-condition:**

1. Login with Admin credentials in Maximo classic/Manage.
2. Create a new work order, add assignments for labor and approve the work order.
3. Go to Administration > Organizations. Search and click on organization.
4. Go to System Settings and uncheck/disable the option "Automatically change the work order status to INPRG when user starts the Labor timer". 
5. Accept & Reject Button visible when "Enable Assignment flow" property will be enabled. 
6. if "Enable Assignment flow" property will be Disabled user can directly See "Start Work" Button.
**Steps:**

1. Launch the Maximo Mobile application using technician credentials.
2. Click on "My Schedule" tile to open the list of assigned work orders.
3. Find the created work order in pre-condition steps in WO list and click on 'Accept' button.
4. Click on the "Start work" button from work order card on work order list or work order details so that a new labor transaction is started.
5. Verify work order status remains unchanged.

**Result:**

Work order status should remain unchanged when MAXVARINPUT.STARTTIMERINPRG=0 and technician starts a new labor transaction from WO list or WO details.

## Scenario 19 - Verify technician can see a new labor transaction record in 'Labor' section on 'Report Work' page when technician starts a new labor transaction from WO list or WO details

**Pre-condition:**

1. Login with Admin credentials in Maximo classic/Manage.
2. Create a new work order, add assignments for labor and approve the work order. 
3. Accept & Reject Button visible when "Enable Assignment flow" property will be enabled. 
4. if "Enable Assignment flow" property will be Disabled user can directly See "Start Work" Button.
**Steps:**

1. Launch the Maximo Mobile application using technician credentials.
2. Click on "My Schedule" tile to open the list of assigned work orders.
3. Find the created work order in pre-condition steps in WO list and click on 'Accept' button.
4. Click on the "Start work" button from work order card on work order list or work order details so that a new labor transaction is started.
5. Go to 'Report work' page.
6. Verify that a new labor transaction record is created in 'Labor' section.

**Result:**

A new labor transaction record should be created in 'Labor' section on the 'Report Work' page when technician starts a new labor transaction from WO list or WO details.

## Scenario 20 - Verify that technician can start only one timer at a time in mobile apps when "maximo.mobile.allowmultipletimers" system property is 0

**Pre-condition:**

1. Login with Admin credentials in Maximo classic/Manage.
2. Go to work order tracking application.
3. Create multiple work orders and associate the service address.
4. Add assignments for labor and approve the work orders.
5. Make sure that none of the work orders assigned to technician/labor has started timer/labor transaction for him/her.
6. Set "maximo.mobile.allowmultipletimers" system property as 0. 
7. Accept & Reject Button visible when "Enable Assignment flow" property will be enabled. 
8. if "Enable Assignment flow" property will be Disabled user can directly See "Start Work" Button.oto Global properties and set the property mxe.mobile.travel.prompt=1 and mxe.mobile.travel.radius=1.

**Steps:**

1. Launch the Maximo Mobile app with assigned technician credentials.
2. Click on "My Schedule" tile to open the list of work orders.
3. Search the work order and Click on Accept button
4. tap on "Start travel" button on the work order card.
5. Timer or labor transaction is started for the technician/labor.
6. Search another work order and tap on "Start travel" button on the work order card.
7. Verify that technician is unable to start second timer or labor transaction.

**Results:**

- Technician should be unable to start second timer or labor transaction when one timer is already started for the same technician.
- A popup message "Timer already started." should be displayed when technician tries to start second timer or labor transaction.

**Note:**

- Perform the above mentioned scenario from WO details page and it should work as expected.
- Perform the above mentioned scenario with combination of clicking "Start travel" and "Start work" buttons on different work orders.

## Scenario 21 - Verify that technician can start multiple timer at a time in mobile apps when "maximo.mobile.allowmultipletimers" system property is 1 and viceversa

**Pre-condition:**

1. Login with Admin credentials in Maximo classic/Manage.
2. Go to work order tracking application.
3. Create multiple work orders and associate the service address.
4. Add assignments for labor and approve the work orders.
5. Make sure that none of the work orders assigned to technician/labor has started timer/labor transaction for him/her.
6. Set "maximo.mobile.allowmultipletimers" system property as 1.
7. Goto Global properties and set the property mxe.mobile.travel.prompt=1 and mxe.mobile.travel.radius=1. 
8. Accept & Reject Button visible when "Enable Assignment flow" property will be enabled. 
9. if "Enable Assignment flow" property will be Disabled user can directly See "Start Work" Button.
**Steps:**

1. Launch the Maximo Mobile app with assigned technician credentials.
2. Click on "My Schedule" tile to open the list of work orders.
3. Search the work order and Click on Accept button 
4. tap on "Start travel" button on the work order card.
5. Timer or labor transaction is started for the technician/labor.
6. Search another work order and tap on "Start travel" button on the work order card.
7. Verify that technician is unable to start second timer or labor transaction.

**Results:**

- Technician should be able to start multiple timer at a time in mobile apps.
- A popup message "Timer already started." should be displayed when technician tries to start second timer or labor transaction.

## Scenario 22 - Verify that Start travel button should be visible when ‘mxe.mobile.travel.radius’ properties is set to 0

**Pre-condition:**

1. Login with Admin credentials in Maximo classic/Manage.
2. Go to work order tracking application.
3. Create work orders and associate the service address with latitude and longitude .
4. Add assignments for labor and approve the work orders.
5. Set "mxe.mobile.travel.radius" system property as 0,Set "maximo.mobile.usetimer" system property value to 1,set the property mxe.mobile.travel.prompt=0.
6. Goto Global properties and set the property mxe.mobile.travel.prompt=1 and mxe.mobile.travel.radius=1.
7. Accept & Reject Button visible when "Enable Assignment flow" property will be enabled.
8. if "Enable Assignment flow" property will be Disabled user can directly See "Start Travel" Button.
   **Steps:**

1. Launch the Maximo Mobile app with assigned technician credentials.
2. Click on "My Schedule" tile to open the list of work orders.
3. Search the work order and Click on Accept button if "Enable Assignment flow" enabled.

**Results:**

- "Start Travel" button should be visible work order details page.


**Note:**

- Perform the above mentioned scenario from WO details page and it should work as expected.
- Perform the above mentioned scenario with combination of clicking "Start travel" and "Start work" buttons on different work orders.

## Scenario 23 - Verify Start travel and start work both button should visible

**Pre-condition:**

1. Login with Admin credentials in Maximo classic/Manage.
2. Go to work order tracking application.
3. Create work orders and associate the service address with latitude and longitude .
4. Add assignments for labor and approve the work orders.
5. Set "mxe.mobile.travel.radius" system property as 0.
6. Goto Global properties and set the property mxe.mobile.travel.prompt=1 and mxe.mobile.travel.radius=1.
7. Accept & Reject Button visible when "Enable Assignment flow" property will be enabled.
8. if "Enable Assignment flow" property will be Disabled user can directly See "Start Travel" Button.
   **Steps:**

1. Launch the Maximo Mobile app with assigned technician credentials.
2. Click on "My Schedule" tile to open the list of work orders.
3. Search the work order and Click on Accept button if "Enable Assignment flow" enabled.

**Results:**

- Start travel and start work both button should visible on work order details page.

## Scenario 24 - Verify that when user clicking on start travel button pause travel and stop travel button should be visible

**Pre-condition:**

1. Login with Admin credentials in Maximo classic/Manage.
2. Go to work order tracking application.
3. Create work orders and associate the service address with latitude and longitude .
4. Add assignments for labor and approve the work orders.
5. Set "mxe.mobile.travel.radius" system property as 0.
6. Goto Global properties and set the property mxe.mobile.travel.prompt=1 and mxe.mobile.travel.radius=1.
7. Accept & Reject Button visible when "Enable Assignment flow" property will be enabled.
8. if "Enable Assignment flow" property will be Disabled user can directly See "Start Travel" Button.
   **Steps:**

1. Launch the Maximo Mobile app with assigned technician credentials.
2. Click on "My Schedule" tile to open the list of work orders.
3. Search the work order and Click on Accept button if "Enable Assignment flow" enabled.
4. Start travel and start work both button should visible on work order details page.
5. User should able to click on Start travel Button

**Results:**
- Pause travel and stop travel button should be visible

## Scenario 25 -Verify When user clicking on pause travel time approval prompt should be open

**Pre-condition:**

1. Login with Admin credentials in Maximo classic/Manage.
2. Go to work order tracking application.
3. Create work orders and associate the service address with latitude and longitude .
4. Add assignments for labor and approve the work orders.
5. Set "mxe.mobile.travel.radius" system property as 0.
6. Goto Global properties and set the property mxe.mobile.travel.prompt=1 and mxe.mobile.travel.radius=1.
7. Accept & Reject Button visible when "Enable Assignment flow" property will be enabled.
8. if "Enable Assignment flow" property will be Disabled user can directly See "Start Travel" Button.
   **Steps:**

1. Launch the Maximo Mobile app with assigned technician credentials.
2. Click on "My Schedule" tile to open the list of work orders.
3. Search the work order and Click on Accept button if "Enable Assignment flow" enabled.
4. Start travel and start work both button should visible on work order details page.
5. User should able to click on Start travel Button
6. Pause travel and stop travel button should be visible
7. User should be able to click on pause travel button

**Results:**
- time approval prompt should be open


## Scenario 26 -Verify that When user click on pause travel and then delete the time entry operation

**Pre-condition:**

1. Login with Admin credentials in Maximo classic/Manage.
2. Go to work order tracking application.
3. Create work orders and associate the service address with latitude and longitude .
4. Add assignments for labor and approve the work orders.
5. Set "mxe.mobile.travel.radius" system property as 0.
6. Goto Global properties and set the property mxe.mobile.travel.prompt=1 and mxe.mobile.travel.radius=1.
7. Accept & Reject Button visible when "Enable Assignment flow" property will be enabled.
8. if "Enable Assignment flow" property will be Disabled user can directly See "Start Travel" Button.
   **Steps:**

1. Launch the Maximo Mobile app with assigned technician credentials.
2. Click on "My Schedule" tile to open the list of work orders.
3. Search the work order and Click on Accept button if "Enable Assignment flow" enabled.
4. Start travel and start work both button should visible on work order details page.
5. User should able to click on Start travel Button
6. Pause travel and stop travel button should be visible
7. User should be able to click on pause travel button
8. time approval prompt should be open
9. User should able to click on delete time entry

**Results:**
- "Travel time was deleted" toast message should come and user should redirect on work order detail page

## Scenario 27 -Verify that when user click on pause travel and then doing edit time operation

**Pre-condition:**

1. Login with Admin credentials in Maximo classic/Manage.
2. Go to work order tracking application.
3. Create work orders and associate the service address with latitude and longitude .
4. Add assignments for labor and approve the work orders.
5. Set "mxe.mobile.travel.radius" system property as 0.
6. Goto Global properties and set the property mxe.mobile.travel.prompt=1 and mxe.mobile.travel.radius=1.
7. Accept & Reject Button visible when "Enable Assignment flow" property will be enabled.
8. if "Enable Assignment flow" property will be Disabled user can directly See "Start Travel" Button.
   **Steps:**

1. Launch the Maximo Mobile app with assigned technician credentials.
2. Click on "My Schedule" tile to open the list of work orders.
3. Search the work order and Click on Accept button if "Enable Assignment flow" enabled.
4. Start travel and start work both button should visible on work order details page.
5. User should able to click on Start travel Button
6. Pause travel and stop travel button should be visible
7. User should be able to click on pause travel button
8. time approval prompt should be open
9. User should able to click on edit time button
10. User should redirect on report page and labor transaction drawer should be open.
11. Verify Labor name as a current username and type as "Travel time" should auto populate.
12. User should able to click on blue tick save button
13. User should redirect on work order detail page 

**Results:**
- "Travel time approved successfully" toast message should come and user should redirect on work order detail page
- In report work page travel time entry should be saved and visible to user

## Scenario 28 -Verify that when user click on pause travel and then Approve travel time operation

**Pre-condition:**

1. Login with Admin credentials in Maximo classic/Manage.
2. Go to work order tracking application.
3. Create work orders and associate the service address with latitude and longitude .
4. Add assignments for labor and approve the work orders.
5. Set "mxe.mobile.travel.radius" system property as 0.
6. Goto Global properties and set the property mxe.mobile.travel.prompt=1 and mxe.mobile.travel.radius=1.
7. Accept & Reject Button visible when "Enable Assignment flow" property will be enabled.
8. if "Enable Assignment flow" property will be Disabled user can directly See "Start Travel" Button.
   **Steps:**

1. Launch the Maximo Mobile app with assigned technician credentials.
2. Click on "My Schedule" tile to open the list of work orders.
3. Search the work order and Click on Accept button if "Enable Assignment flow" enabled.
4. Start travel and start work both button should visible on work order details page.
5. User should able to click on Start travel Button
6. Pause travel and stop travel button should be visible
7. User should be able to click on pause travel button
8. time approval prompt should be open
9. User should able to click on Approve button


**Results:**
- "Travel time approved successfully" toast message should come and user should redirect on work order detail page
- In report work page travel time entry should be saved and visible to user


## Scenario 29 -Verify that When user click on stop travel and then delete the time entry operation

**Pre-condition:**

1. Login with Admin credentials in Maximo classic/Manage.
2. Go to work order tracking application.
3. Create work orders and associate the service address with latitude and longitude .
4. Add assignments for labor and approve the work orders.
5. Set "mxe.mobile.travel.radius" system property as 0.
6. Goto Global properties and set the property mxe.mobile.travel.prompt=1 and mxe.mobile.travel.radius=1.
7. Accept & Reject Button visible when "Enable Assignment flow" property will be enabled.
8. if "Enable Assignment flow" property will be Disabled user can directly See "Start Travel" Button.
   **Steps:**

1. Launch the Maximo Mobile app with assigned technician credentials.
2. Click on "My Schedule" tile to open the list of work orders.
3. Search the work order and Click on Accept button if "Enable Assignment flow" enabled.
4. Start travel and start work both button should visible on work order details page.
5. User should able to click on Start travel Button
6. Pause travel and stop travel button should be visible
7. User should be able to click on stop travel button
8. time approval prompt should be open
9. User should able to click on delete time entry

**Results:**
- "Travel time was deleted" toast message should come and user should redirect on work order detail page

## Scenario 30 -Verify that when user click on stop travel and then doing edit time operation

**Pre-condition:**

1. Login with Admin credentials in Maximo classic/Manage.
2. Go to work order tracking application.
3. Create work orders and associate the service address with latitude and longitude .
4. Add assignments for labor and approve the work orders.
5. Set "mxe.mobile.travel.radius" system property as 0.
6. Goto Global properties and set the property mxe.mobile.travel.prompt=1 and mxe.mobile.travel.radius=1.
7. Accept & Reject Button visible when "Enable Assignment flow" property will be enabled.
8. if "Enable Assignment flow" property will be Disabled user can directly See "Start Travel" Button.
   **Steps:**

1. Launch the Maximo Mobile app with assigned technician credentials.
2. Click on "My Schedule" tile to open the list of work orders.
3. Search the work order and Click on Accept button if "Enable Assignment flow" enabled.
4. Start travel and start work both button should visible on work order details page.
5. User should able to click on Start travel Button
6. Pause travel and stop travel button should be visible
7. User should be able to click on stop travel button
8. time approval prompt should be open
9. User should able to click on edit time button
10. User should redirect on report page and labor transaction drawer should be open.
11. Verify Labor name as a current username and type as "Travel time" should auto populate.
12. User should able to click on blue tick save button
13. User should redirect on work order detail page

**Results:**
- "Travel time approved successfully" toast message should come and user should redirect on work order detail page
- In report work page travel time entry should be saved and visible to user

## Scenario 31 -Verify that when user click on stop travel and then Approve travel time operation

**Pre-condition:**

1. Login with Admin credentials in Maximo classic/Manage.
2. Go to work order tracking application.
3. Create work orders and associate the service address with latitude and longitude .
4. Add assignments for labor and approve the work orders.
5. Set "mxe.mobile.travel.radius" system property as 0.
6. Goto Global properties and set the property mxe.mobile.travel.prompt=1 and mxe.mobile.travel.radius=1.
7. Accept & Reject Button visible when "Enable Assignment flow" property will be enabled.
8. if "Enable Assignment flow" property will be Disabled user can directly See "Start Travel" Button.
   **Steps:**

1. Launch the Maximo Mobile app with assigned technician credentials.
2. Click on "My Schedule" tile to open the list of work orders.
3. Search the work order and Click on Accept button if "Enable Assignment flow" enabled.
4. Start travel and start work both button should visible on work order details page.
5. User should able to click on Start travel Button
6. Pause travel and stop travel button should be visible
7. User should be able to click on stop travel button
8. time approval prompt should be open
9. User should able to click on Approve button


**Results:**
- "Travel time approved successfully" toast message should come and user should redirect on work order detail page
- In report work page travel time entry should be saved and visible to user

**Note:**

- Perform the above mentioned scenario from WO details page and it should work as expected.
- Perform the above mentioned scenario with combination of clicking "Start travel" and "Start work" buttons on different work orders.

## Scenario 32 -Verify that when map is enable start travel functionality should work on map page

**Pre-condition:**

1. Login with Admin credentials in Maximo classic/Manage.
2. Go to work order tracking application.
3. Create work orders and associate the service address with latitude and longitude .
4. Add assignments for labor and approve the work orders.
5. Set "mxe.mobile.travel.radius" system property as 0.
6. Goto Global properties and set the property mxe.mobile.travel.prompt=1 and mxe.mobile.travel.radius=1.
7. Accept & Reject Button visible when "Enable Assignment flow" property will be enabled.
8. if "Enable Assignment flow" property will be Disabled user can directly See "Start Travel" Button.
   **Steps:**

1. Launch the Maximo Mobile app with assigned technician credentials.
2. Click on "My Schedule" tile to open the list of work orders.
3. Search the work order and Click on Accept button if "Enable Assignment flow" enabled.
4. Start travel and start work both button should visible on work order details page.
5. Click on service address
6. user should redirect on map application with current work order


**Results:**
-Verify scenario-22 to Scenario-31 all should work on map application.


**Note:**

- Perform the above mentioned scenario from WO details page and it should work as expected.
- Perform the above mentioned scenario with combination of clicking "Start travel" and "Start work" buttons on different work orders.

## Scenario 33 - Verify the UI of the cards/pages/views for fonts, font sizes, font color, text completeness, alignment, positioning etc. is correct and as per design on mobile and other small screen devices for all supported form factors

**Pre-condition:**

Pre-conditions as specified for above mentioned test scenarios.

**Steps:**

Perform steps as specified for above mentioned test scenarios.

**Result:**

- The cards/pages for fonts, font sizes, font color, text completeness, alignment, positioning etc. are correct and as per design.
- The application should behave as per expectations for all above mentioned scenarios on mobile and other small screen devices for all supported form factors.
- The UI should be as per design for respective form factor.

## Scenario 34 - Verify all the above test cases in online and offline/disconnected mode

**Pre-condition:**

Pre-conditions as specified for above mentioned test scenarios.

**Steps:**

Perform steps as specified for above mentioned test scenarios.

**Result:**

The application should behave as per expectations for all above mentioned scenarios in online and offline/disconnected mode on mobiles/tablets/browsers.
