## Verify Status on Asset Function and Data Sheet on mobile "My Schedule" application
This test case will verify if Status on Asset Function can be changed manually by user even if it was autocalculated. Also will verify if the entire list of statuses is available on mobile. 
Data Sheet Status will not update automatically based on Asset Function Status, this can be set only manually. 

### In Case of Automatic Status Update: OFF 
### As Found/As Left Values ON: Editable when Asset Function Status Calculated
>> 1. Data sheet status not updated Automatically based on Asset Function statuses.
>> 2. Asset Function Status not updated Automatically, however we are still able to change status manually.
>
> #### Salesforce case number TS016936140

### Scenario 1 - Verify Status can be set manually by user when option 'Automatically set the As Found and As Left Status on WO?' in Organization is set to OFF

**Pre-condition:**
NOTE: have a technician user created and added to Technician security group. 

1. Login to maximo classic/Manage as administrator.
2. Go to Organizations application and select one org from list
3. From More Action open Calibration Options
4. Click 'Work Order - Other Organisation Settings'
5. Scroll down to 'Automatically set the As Found and As Left Status on WO?' and toggle OFF this option
6. Click OK 
7. Go to Data Sheet Template and create a DS with multiple Asset Functions 
8. Mark as Approved the DS 
9. Go to "work order tracking" app. and create a WO
10. Add a calibration asset to this WO and add the DS created in Step 7
11. Add Technician user to the work order. 
12. Approved the work order.


**Go to mobile app**
>
**Steps**

1. Login to MAS Mobile app with technician credentials.
2. Click on "My Schedule" tile.
3. Click on "Work created by me" query in mobile.
4. Open the Work Order listed.
5. Click on Calibration icon.
6. Navigate to Data Sheet Page.
> ***Observed***: Data sheet Status in empty and can be set by the user. 
7. Leave Status of Data Sheet empty.
8. Navigate to Asset Function page.
> ***Observed***: Asset Function Status in empty and can be set by the user. 
9. Navigate to **As Found page** and fill in all the values -> click Save. 
> ***Observed***: Asset Function Status updates automaticaly based on the values inserted by user. 
10. Go back to Asset Function page. 
11. Click on Status and verify is user can change the Status manually. 
> ***Observed***: User can change the Status even if it was previewsly autocalculated.
12. Navigate to **As Left page** and fill in all the values -> click Save.
> ***Observed***: Asset Function Status for As Left updates automaticaly based on the values inserted by user. 
13. Go back to Asset Function page. 
14. Click on Status and Verify:
> 14.1  if user can see the entire list of Statuses.
>
> 14.2  can change the Status manually by selection a Status from list. 
10. Go back to Data Sheet page and verify if Status is still empty but user can set it manually.

**Expected**: Status for Data Sheet to not be automatically as per Asset Function Status. 
          Asset Function to automatically displayed for user when values are manually set in for both As Found and As Left. Status should be changed manually by the user even if it was automatically calculated.  