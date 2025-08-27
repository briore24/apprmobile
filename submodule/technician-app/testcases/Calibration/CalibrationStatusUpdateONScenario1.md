## Verify Status on Asset Function and Data Sheet on mobile "My Schedule" application
This test case will verify if the Status on the Data Sheet reflects the worst status when multiple Asset Functions are listed. The Status should automatically display the worst condition among them.

In this scenario, the user does not manually set the Status as "Broken" or "Missing" for any Data Sheet or Asset Function. Instead, the user manually enters values for "As Found" and "As Left" for each Asset Function. The Status is then auto-calculated and rolled up to the Data Sheet level. Please see examples bellow.

### In Case of Automatic Status Update: ON 
### As Found/As Left Values ON: Editable when Asset Function Status Calculated
>> 1. Data Sheet Status updated automatically based on Asset function Statuses, Pass/ Fail/ Action/ Adjtoimp/ Adjreqd/ Inspect/ Limiteduse/ OLIM/ Warning, if all the statuses of Asset Function is Calculated.
>> 2. Asset Function Status will display the roll up Status based on hierarchy. 
#### Example when a DS has 3 AF listed: 
> - if first AF has **As Found: ACTION As Left: ACTION**
> - if second AF has **As Found: FAIL As Left: ADJREQD**
> - if third AF has **As Found: FAIL As Left: FAIL**
> - then **Data Sheet Status** will be **FAIL** for both.
> 
#### Another Example when a DS has 3 AF listed: 
> - if first AF has **As Found: ACTION As Left: ACTION**
> - if second AF has **As Found: FAIL As Left: ADJREQD**
> - if third AF has **As Found: PASS As Left: PASS**
> - then **Data Sheet Status** will be **As Found: FAIL As Left: ACTION**
>
> #### Salesforce case number TS016936140
>
### Scenario 1 - In Case of Automatic Status Update: ON 
#### Verify if Data Sheet Status updated automatically based on Asset function Statuses, Pass/ Fail/ Action/ Adjtoimp/ Adjreqd/ Inspect/ Limiteduse/ OLIM/ Warning, if all the statuses of Asset Function is Calculated.

**Pre-condition:**
NOTE: have a technician user created and added to Technician security group. 
Excel data sheet for Data Sheet: https://ibm.ent.box.com/file/1631643976004

1. Login to maximo classic/Manage as administrator.
2. Go to Organizations application and select one org from list.
3. From More Action open Calibration Options.
4. Click 'Work Order - Other Organisation Settings'.
5. Scroll down to 'Automatically set the As Found and As Left Status on WO?' and make sure toggle is ON.
6. Click OK. 
7. Go to Data Sheet Template and create a DS with multiple Asset Functions ( at least 3 asset functions). 
8. Mark as Approved the DS. 
9. Go to "Work Order Tracking" app. and create a WO
10. Add a calibration asset to this WO and add the DS created in Step 7.
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
> ***Observed***: Data sheet Status in empty and can be set by the user. Status available to user is Broken or Missing.
7. Leave Status of Data Sheet empty.
8. Navigate to Asset Function page.
> ***Observed***: Asset Function Statuses are empty for all AF listed in the DS. User can manually set Status. Status available to user is Broken or Missing.
9. Navigate to first AF and click on **As Found** chevron. Fill in all the values and click Save. 
> ***Observed***: Asset Function Status updates automaticaly based on the values inserted by user.
10. Go back to Asset Function page. 
11. Click on Status assigned to the first AF to verify is user can't change it manually. Status is readonly now.
12. Navigate to first AF and click on **As Left** chevron. Fill in all the values and click Save. 
> ***Observed***: Asset Function Status for As Left updates automaticaly based on the values inserted by user. 
13. Go back to Asset Function page. 
14. Click on Status assigned to the first AF to verify is user can't change it manually. Status is readonly now.
10. Go back to Data Sheet page and verify if Status has not been updatedas all AF listed need to have a status, either auto-calculated or Missing/Broken. 
11. Repeat steps 8 to 14 for all Asset Function listed. 
12. Go to Data sheet page and verify Status is now populated. 

**Expected**: In this test case, the Status for the Data Sheet should not be manually selected initially. The possible statuses are "Broken" or "Missing." The user does not choose a status but instead manually enters values for "As Found" and "As Left". Then, the Status is automatically displayed for the user at both the Asset Function and Data Sheet levels. Once the Status is auto-calculated, it becomes read-only and cannot be changed at either level. Status does roll up to Data sheet level only when all Asset Functions are completed. 
