# Asset Swap Functionality

These test cases will  have asset swap function available on mobile like what we can do on desktop site. Asset swap function should be available for different scenarios such with linking with a work order or not, swap between functional locations (eg on train) and workshops, or between two functional locations (eg swapping wheel set on different trains)

These test cases will cover functionalities of following user stories:

- GRAPHITE-71508: Adding asset swap function with work order on mobile app



**Design URL:**

- <https://ibm.invisionapp.com/share/4BO08BSPJ7A#/screens/319617290_Attachments>

**Note:**

- In case of web app, all transactions are saved in database and refreshed on the UI in real time.
- In case of online mode on mobile containers, transactions are sent to the database for saving instantly but UI is not refreshed from database until 'Check for Updates' button is clicked.
- In case of offline mode on mobile containers, transactions are queued for syncing to the database and synced to database once device is online. UI is not refreshed from database until 'Check for Updates' button is clicked.
- All server side error messages will be displayed in error page on devices post syncing with server and tapping on 'Check for Updates' button in both online and offline modes.


## Scenario 1: “Asset Configuration manager Icon” should be Visible on work order Detail page if ACM is installed in Environment.

**Pre-condition:**

1.ACM should install in technician Application.  
2.Open the classic Maximo application and create new work order with ACM configured asset and assign the labour then approve Work order.

**Steps:**
1.	Login to Maximo mobile app with the credentials of technician assigned to the work order.
2.	Click on “9 Dot” menu and Navigate to My schedule
3.	Open Created work order In Maximo Classic Application
4.	Go to Workorder Detail Page.
5.	“Asset Configuration manager Icon” Should be visible.
6.	Click on “Asset Configuration manager Icon” User Should Redirect on Asset Switch page.

**Results:**
- Same Asset should be visible on asset switch page.


## Scenario 2:User should be able to redirect on Asset Switch page from work order detail page .

**Pre-condition:**

1.ACM should install in Application.    
2.Open the maximo classic application and create new work order with ACM configured asset and assign the labour then approve Work order.

Steps:
1.	Login to Maximo mobile app with the credentials of technician assigned to the work order.
2.	Click on “9 Dot” menu and Navigate to My schedule
3.	Open Created work order In Maximo Classic Application
4.	Go to Workorder Detail Page.
5.	“Asset Configuration manager Icon” Should be visible.
6.	Click on "Asset Configuration manager Icon"

**Results:**
1.	User Should Redirect on Asset Switch page and able to see the same asset.

## Scenario 3: When User is Clicked on Back Arrow on Asset Switch page user should be Redirect on Work order details page.

**Pre-condition:**

1.ACM should install in Application.    
2.Open the maximo classic application and create new work order with ACM configured asset and assign the labour then approve Work order.

**Steps:**
1.	Login to Maximo mobile app with the credentials of technician assigned to the work order.
2.	Click on “9 Dot” menu and Navigate to My schedule
3.	Open Created work order In Maximo Classic Application
4.	Go to Workorder Detail Page.
5.	“Asset Configuration manager Icon” Should be visible.
6.	Click on "Asset Configuration manager Icon" it Should Redirect on Asset Switch page.
7.	Click On Back Arrow icon.

**Results:**
- User Should be able to redirect on same work order details page.

## Scenario 4: If ACM is not installed in technician application then   “Asset Configuration manager”  Icon should not be visible on work order detail page

**Pre-condition:**

1.ACM should not be installed in technician application.   
2.Open the maximo classic Application and create new work order with  not ACM configured asset and assign the labour then then approve Work order.

**Steps:**
1.	Login to Maximo mobile app with the credentials of technician assigned to the work order.
2.	Click on “9 Dot” menu and Navigate to My schedule
3.	Open Created work order In Maximo Classic Application
4.	Go to Workorder Detail Page.
5.	Asset Setting Icon Should not be visible
      
**Results:**
- Asset Setting Icon Should not be visible on Workorder Detail Page.


## Scenario 5: Asset Setting Icon should not be visible on Workorder Detail Page.if Added asset is not ACM type


**Pre-condition:**

1.ACM should install in Application.   
2.Open the maximo classic aplication and create new work order and add non ACM asset in work order and assign the labour then approve Work order.

**Steps:**

1.Login to Maximo mobile app with the credentials of technician assigned to the work order.
2.Click on “9 Dot” menu and Navigate to My schedule   
3.Open Created work order In technician Application   
4.Go to Workorder Detail Page.
5.” Asset Configuration manager Icon” Should not be visible.

**Results:**

- ”Asset Configuration manager Icon” should not  be visible on Workorder Detail Page.


## Scenario 6: “Asset Configuration manager Icon” should be Visible on task page if added asset in task is not same as workorder Asset.

**Pre-condition:**

1.ACM should install in Application.  
2.Open the classic Maximo Application  and create new work order with ACM configured asset  and also Create task with Different ACM asset from work order ACM asset and assign the labour then approve Work order.

**Steps:**
1.	Login to Maximo mobile app with the credentials of technician assigned to the work order.
2.	Click on “9 Dot” menu and Navigate to My schedule
3.	Open Created work order In Maximo Classic Application
4.	Go to Workorder Detail Page.
5.	Click on task icon.
6.	Expand Added task
      
**Results:**

- User should be able to see "Asset Configuration Manager Icon” under the task

## Scenario 7: User Should be able to redirect on Asset switch page after clicking on “Asset Configuration manager Icon” under added task.

Pre-condition:

1.ACM should install in Application.  
2.Open the classic Maximo Application  and create new work order with ACM configured asset  and also Create task with Different ACM asset from work order ACM asset and assign the labour then approve Work order.

Steps:
1.	Login to Maximo mobile app with the credentials of technician assigned to the work order.
2.	Click on “9 Dot” menu and Navigate to My schedule
3.	Open Created work order In Maximo Classic Application
4.	Go to Workorder Detail Page.
5.	Click on task icon.
6.	Expand Added task
7.	Click on “Asset Configuration manager icon”
      Results:
2.	user Should Redirect on Asset Switch page and able to see the same asset

## Scenario 8: User Should be able to redirect on Task page after Clicking back button on Asset Switch page.

**Pre-condition:**
1.ACM should install in Application.
2.Open the classic Maximo Application  and create new work order with ACM configured asset  and also Create task with Different ACM asset from work order ACM asset and assign the labour then approve Work order.

**Steps:**
1.	Login to Maximo mobile app with the credentials of technician assigned to the work order.
2.	Click on “9 Dot” menu and Navigate to My schedule
3.	Open Created work order In Maximo Classic Application
4.	Go to Workorder Detail Page.
5.	Click on task icon.
6.	Expand Added task
7.	Click on “Asset Configuration manager icon”
8.	User Should be able to Redirect on Asset Switch page
9.	Click back button on Asset Switch page.
      
**Results:**
    
- User should be able redirect on Task page.

**Note:**

- Please make sure to revert the permission to allowed once testing is completed.

## Scenario 9 - Verify Attachments should not be displayed from the previous work order

**Pre-condition:**

1. Login with Admin credentials in Maximo classic/Manage.
2. Go to work order tracking application.
3. Create a new work order.
4. Add assignments for labor and approve the WO.

**Steps:**

1. Login to maximo mobile EAM app.
2. Click a work order with about 8 photos attached.
3. Go to attachments and click on + button.
4. Add another attachment by clicking "photo or video" option and take a photo
5. Enter a file name
6. Go to the work order list view
7. Press the Home button on the device to put it to sleep
8. Wait for about 1.5 hour and open the app
9. Open the Mobile EAM app. 
10. Click a different work order

**Results:**

- Work order created on step 10th should NOT display attachments from first(previously) created WO.

## Scenario 10 - Verify all the above test cases in offline or disconnected mode

**Pre-condition:**

Pre-conditions as specified for above mentioned test scenarios.

**Steps:**

Perform steps as specified for above mentioned test scenarios.

**Result:**

The application should behave as per expectations for all above mentioned scenarios in offline or disconnected mode on mobiles/tablets/desktops.
