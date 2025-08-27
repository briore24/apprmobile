## Verify History on an Calibration Asset on mobile "My Schedule" application
This test case will verify if an Asset that is Calibration can display the History Loopkup. 
>
### Scenario 1 - Verify Calibration Asset History if asset has calibration flag on. 
>
**Pre-condition:**
NOTE: have a technician user created and added to Technician security group. 
Excel data sheet for Data Sheet: https://ibm.ent.box.com/file/1631643976004

1. Login to maximo classic/Manage as administrator.
2. Go to Data Sheet Template and create a DS with multiple Asset Functions ( at least 3 asset functions). 
3. Mark as Approved the DS. 
4. Go To Assets application and create an asset.
5. Click Calibration flag for asset and save the asset.
6. Go to "Work Order Tracking" application and create a WO.
7. Add a calibration asset from step 4 to this WO and add the DS created in Step 2.
8. Add Technician user to the work order. 
9. Approved the work order.

**Go to mobile app**
>
**Steps**

1. Login to MAS Mobile app with technician credentials.
2. Click on "My Schedule" tile.
3. Click on "Work created by me" query in mobile.
4. Open the Work Order listed.
5. Open Asset and location chevron
6. Verify if Asset details are listed.
7. Verify if Asset history with Calibration history are listed 
8. Click on Calibration history chevron
9. Verify the calibration history panel will list all the WO records with WO IS's and dates, sorted in ascending older.
10. Select a record from Calibration history list
11. Verify if user can see the Calibration details page. A Technician can see all the essential calibration details but it can't access the Calibration DS from here. 
12. Go back to Navigator list 
13. Click on Assets chevron to see the entire assets list 
14. Switch query to be My Assets 
15. Search for your Asset that is a calibration asset 
16. CLick on the chevron that is next to the Status 
17. Repeat to verify the flow for all the steps from 5 to 11. 
>
**Expected**: At the end of this scenario a Technician will be able to see the calibration details in an Asset from Asset application or from a WO if the asset listed is a calibrated asset. A Technician can see all the essential calibration details but it cannot view or access  calibration data sheet. 
>
### Scenario 2 - Verify Calibration Asset History is not displayed if asset is a normal asset of a linear asset. 
>
**Pre-condition:**
NOTE: have a technician user created and added to Technician security group. 

1. Login to maximo classic/Manage as administrator.
2. Go to Assets application and create an asset without activating the Calibration flag.
3. Go to Work Order Tracking and create a new WO and add the asset from step 2
4. Approve the WO.

**Go to mobile app**
>
**Steps**

1. Login to MAS Mobile app with technician credentials.
2. Click on "My Schedule" tile.
3. Click on "Work created by me" query in mobile.
4. Open the Work Order created in pre-condition.
5. Open Asset and location chevron.
6. Verify if Asset details are listed.
7. Verify if Asset history with Calibration history are NOT available. 
8. From steps 7 Verify if user can't open the cheveron and see the asset calibration details.
9. Go back to Navigator list 
10. Click on Assets chevron to see the entire assets list 
11. Switch query to be My Assets 
12. Search for your Asset created in pre-condition
13. CLick on the chevron that is next to the Status 
17. Repeat to verify the flow for all the steps from 5 to 8. 
>
**Expected**: At the end of this scenario a Technician will not be able to see the Calibration details in an Asset from Asset application or from a WO if the asset listed if the Calibration flag wasn't turned on when asset was created in Manage.