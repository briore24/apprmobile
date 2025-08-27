## Verify Status on Asset Function and Data Sheet on mobile "My Schedule" application
This test case will verify if Status on Asset Function is rolling up to DS based on some rules.

### In Case of Automatic Status Update: ON 
### As Found/As Left Values ON: Editable when Asset Function Status Calculated
>> In Case of Asset Function Status is set as Missing/ Broken, Data Sheet Status updated automatically taking these two worst case (Missing/ Broken) as top priority. 
>> - a. If Only __one__ Asset Function Status Reported as Missing/ Broken: Data Sheet Status updated based on that Asset Function Status.
>> - b. If __Multiple__ Asset Function Status Reported as Missing/ Broken: Data Sheet Status updated based on last Asset Function Status changed.

#### Example for __a__ when a DS has 3 AF listed: 
>> - if first AF has **As Found: MISSING As Left: BROKEN**
>> - if second AF has **As Found: empty As Left: empty**
>> - if third AF has **As Found: empty As Left: empty**
>
***Observed***: then IMMEDIATELY **Data Sheet Status** will be **As Found: MISSING As Left: BROKEN**.
>
> - But if user fill in first 2 AF with other Status than Missing or Broken -> Data sheet will not display a Status until last AF is also completed. 
>> - if first AF has **As Found: PASS As Left: PASS**
>> - if second AF has **As Found: FAIL As Left: ADJREQD**
>
***Observed***: at this point **Data Sheet Status** will will be empty not yet calculated. 
>> - if third AF has **As Found: FAIL As Left: FAIL**
>
***Observed***: then **Data Sheet Status** will be **FAIL** for both.
> 
#### Example for __b__ when a DS has 5 AF listed: 
>> - if first AF has **As Found: ACTION As Left: Missing**
>> - if second AF has **As Found: BROKEN As Left: ADJREQD**
>> - if third AF has **As Found: PASS As Left: Broken**
>> - if fourth AF has **As Found: PASS As Left: PASS**
>> - if fifth AF has **As Found: Limiteduse As Left: Warning**
>
***Observed***: then **Data Sheet Status** will be as per last AF Status **As Found: Limiteduse As Left: Warning**
>
***Observed***: also in Case of Asset Function Status is Missing/ Broken, we are able to change Status on Data sheet manually for non Calculated Statuses (Missing/ Broken).
>
> #### Salesforce case number TS016936140
>
### Scenario 2 - In Case of Automatic Status Update: ON 
### In Case of Asset Function Status is Missing/ Broken, Data Sheet Status updated automatically taking these two worst case (Missing/ Broken) as top priority. Again two situation rises.
>
>> #### a. If Only ***one*** Asset Function Status Reported as ***Missing/Broken***: Data Sheet Status updated based on that Asset Function Status.

**Pre-condition:**
NOTE: have a technician user created and added to Technician security group. 
Excel data sheet for Data Sheet: https://ibm.ent.box.com/file/1631643976004

1. Login to maximo classic/Manage as administrator.
2. Go to Organizations application and select one org from list.
3. From More Action open Calibration Options.
4. Click 'Work Order - Other Organisation Settings'.
5. Scroll down to 'Automatically set the As Found and As Left Status on WO?' and make sure toggle is ON.
6. Click OK. 
7. Go to Data Sheet Template and create a DS with multiple Asset Functions. 
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
10. Go back to Data Sheet page and verify if Status has not been updated.
11. Go back to Asset Function page.
12. For second AF set both As Found and As Left Status to be Broken/Missing 
13. Go back to Data Sheet page and verify if Status has now changed. 
> ***Observed***: even if there are 3 AF listed (as per example a) and last AF has no status added, Data Sheet now display Status Broken/Missing.
14. If there is another example with 3 AF available go and set first AF as Broken/Missing 
15.  For second AF set both As Found and As Left Status to be Broken/Missing 
> ***Observed***: even if there are 3 AF listed (as per steps 14,15) and only first AF has Status added, Data Sheet now display Status Broken/Missing.
 

**Expected**: In this test case, the Status for the Data Sheet should not be manually selected initially. The possible statuses are "Broken" or "Missing." The user does not choose a status but instead manually enters Status Broken/Missing for "As Found" and "As Left" for one of the AF listed. Then, the Status is automatically displayed for the user at Data Sheet level as Broken/Missing. 
>
>
### Scenario 2 - In Case of Automatic Status Update: ON 
### In Case of Asset Function Status is Missing/ Broken, Data Sheet Status updated automatically taking these two worst case (Missing/ Broken) as top priority. Again two situation rises.
>
>> #### b. If __Multiple__ Asset Function Status Reported as Missing/ Broken: Data Sheet Status updated based on last Asset Function Status changed. In Case of Asset Function Status is Missing/ Broken, we are able to change Status on Data sheet manually for non Calculated Statuses (Missing/ Broken).

**Pre-condition:**
NOTE: have a technician user created and added to Technician security group. 
Excel data sheet for Data Sheet: https://ibm.ent.box.com/file/1631643976004

1. Login to maximo classic/Manage as administrator.
2. Go to Organizations application and select one org from list.
3. From More Action open Calibration Options.
4. Click 'Work Order - Other Organisation Settings'.
5. Scroll down to 'Automatically set the As Found and As Left Status on WO?' and make sure toggle is ON.
6. Click OK. 
7. Go to Data Sheet Template and create a DS with multiple Asset Functions. 
8. Mark as Approved the DS. 
9. Go to "Work Order Tracking" app. and create a WO
10. Add a calibration asset to this WO and add the DS created in Step 7.
11. Add Technician user to the work order. 
12. Approved the work order.

**Go to mobile app**


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
10. Go back to Data Sheet page and verify if Status has not been updated.
11. Go back to Asset Function page.
12. For second AF set both As Found and As Left Status to be Broken/Missing 
13. Go back to Data Sheet page and verify if Status has now changed. 
> ***Observed***: even if there are 5 AF listed (as per example b) and some AF has no status added, Data Sheet now display Status Broken/Missing.
14. Verify if DS Status can be manually changed and only Broken/Missing are available.
15. Go back to AF page and verify if status can be manually changed and only Broken/Missing are available.
16. Go back to AF list and set status for each as follow:
>> - third AF  **As Found: PASS As Left: Adjreqd**
>> - fourth AF **As Found: PASS As Left: BROKEN**
> Check Status of DS after each AF completed at this point.
> ***Expected***: DS Status to be Broken/Missing as it was set for second AF.
>> - fifth AF  **As Found: Limiteduse As Left: Warning**
17. Go back to DS page and verify if Status now is changed as per last update. 
> ***Observed***: then **Data Sheet Status** will be as per last AF Status **As Found: Limiteduse As Left: BROKEN**
16.  Verify if DS Status can't be manually changed. Status is readonly now.