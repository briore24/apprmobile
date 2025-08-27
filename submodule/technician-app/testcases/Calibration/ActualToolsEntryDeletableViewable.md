## Verify Environment Conditions page to be listed for each DS on mobile "My Schedule" application
This test case will verify Actual Tools Entry should be deletable, viewable for calibration work order based on MAXVAR
### Environment Conditions should be separated by Datasheet 
>
> #### Salesforce case number TS016955108

### Scenario 1 - Verify when user adding Rotating asset in calibration workorder on report page Rotating asset is mandatory. 
                

**Pre-condition:**
NOTE: have a technician user created and added to Technician security group. 

1. Login to maximo classic/Manage as administrator.
2. Go to Data Sheet Template and create rotating asset and DS with some Asset Functions 
3. Create another Data Sheet Template and add AF to it.
4. Mark as Approved the DS 
9. Go to "work order tracking" app. and create a WO
10. Add a calibration asset to this WO and add the DS created in Steps 2 and 3
11. Add Technician user to the work order. 
12. Approved the work order.


**Go to mobile app**
>
**Steps**

1. Login to MAS Mobile app with technician credentials.
2. Click on "My Schedule" tile.
3. Click on "Work created by me" query in mobile.
4. Open the Work Order listed.
5. Open work order detail page 
6. click on Report page
7. click on add tool
8. Add rotating tool
> ***Expected***: Rotating asset is mandatory field
9. Add Rotating asset.
10. Click on Blue Right tick button

**Results**
Tool should be added and User should be navigate to Report page

### Scenario 2 - Verify when user adding non Rotating asset in calibration workorder on report page Rotating asset is non mandatory.


**Pre-condition:**
NOTE: have a technician user created and added to Technician security group.

1. Login to maximo classic/Manage as administrator.
2. Go to Data Sheet Template and create non rotating asset and DS with some Asset Functions
3. Create another Data Sheet Template and add AF to it.
4. Mark as Approved the DS
9. Go to "work order tracking" app. and create a WO
10. Add a calibration asset to this WO and add the DS created in Steps 2 and 3
11. Add Technician user to the work order.
12. Approved the work order.


**Go to mobile app**
>
**Steps**

1. Login to MAS Mobile app with technician credentials.
2. Click on "My Schedule" tile.
3. Click on "Work created by me" query in mobile.
4. Open the Work Order listed.
5. Open work order detail page
6. click on Report page
7. click on add tool
8. Add rotating tool
> ***Expected***: Rotating asset should not be mandatory field 
9. Click on Blue Right tick button

>**Results**:Tool should be added and User should be navigate to Report page


### Scenario 3 - Verify user should be able to edit and delete the added tool if "Automatically Approve Tool Transactions?" is unchecked
**Pre-condition:**
NOTE: have a technician user created and added to Technician security group.

1. Login to maximo classic/Manage as administrator.
2. Go to Organisation> Search for your organisation >calibration option>Work order other organisation >Automatically Approve Tool Transactions?unchecked
3. Go to Data Sheet Template and create non rotating asset and DS with some Asset Functions
4. Create another Data Sheet Template and add AF to it.
5. Mark as Approved the DS
6. Go to "work order tracking" app. and create a WO 
7. Add a calibration asset to this WO and add the DS created in Steps 2 and 3 
8. Add Technician user to the work order. 
9. Approved the work order.


**Go to mobile app**
>
**Steps**

1. Login to MAS Mobile app with technician credentials.
2. Click on "My Schedule" tile.
3. Click on "Work created by me" query in mobile.
4. Open the Work Order listed.
5. Open work order detail page
6. click on Report page
7. click on add tool
8. Add non rotating tool
> ***Expected***: Rotating asset should not be mandatory field
9. Click on Blue Right tick button
10. Tool should be added and User should be navigate to Report page
11. Chevron should be present on added tool
12. Click on chevron and Tool drawer should be open
13. User can click on delete button

>**Results**:Tool should be deleted and User should be navigate to Report page


### Scenario 4 - Verify user should be unable to edit and delete the added tool if "Automatically Approve Tool Transactions?" is Checked
**Pre-condition:**
NOTE: have a technician user created and added to Technician security group.

1. Login to maximo classic/Manage as administrator.
2. Go to Organisation> Search for your organisation >calibration option>Work order other organisation >Automatically Approve Tool Transactions?checked
3. Go to Data Sheet Template and create non rotating asset and DS with some Asset Functions
4. Create another Data Sheet Template and add AF to it.
5. Mark as Approved the DS
6. Go to "work order tracking" app. and create a WO
7. Add a calibration asset to this WO and add the DS created in Steps 2 and 3
8. Add Technician user to the work order.
9. Approved the work order.


**Go to mobile app**
>
**Steps**

1. Login to MAS Mobile app with technician credentials.
2. Click on "My Schedule" tile.
3. Click on "Work created by me" query in mobile.
4. Open the Work Order listed.
5. Open work order detail page
6. click on Report page
7. click on add tool
8. Add non rotating tool
> ***Expected***: Rotating asset should not be mandatory field
9. Click on Blue Right tick button
10. Tool should be added and User should be navigate to Report page
11. Chevron should not be present on added tool


>**Results**:Tool should be added and User should be navigate to Report page







