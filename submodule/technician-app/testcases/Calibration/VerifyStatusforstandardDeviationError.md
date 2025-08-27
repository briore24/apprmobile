## Verify Status on Asset Function and Data Sheet.
This test case will verify if the Status on the Data Sheet reflects the worst status when multiple Asset Functions are listed. The Status should automatically display the worst condition among them on the basis of Standard deviation error. 

In this scenario, if Standard Deviation error is less than 0 than it will mark a status as FAIL otherwise it will update the status according to the tolerance error.

>
### Scenario 1 - In Case of Automatic Status Update: ON 
#### Verify if Data sheet and Assest function status is updated as Fail on the basis of Standard Deviation error as less than 0  else on the basis of tolerance of Asset function Statuses, Pass/ Fail/ Action/ Adjtoimp/ Adjreqd/ Inspect/ Limiteduse/ OLIM/ Warning, if all the statuses of Asset Function is Calculated.

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
12. Navigate to Data Sheet tab.
> ***Observed***: Data sheet Status in empty.
13. Leave Status of Data Sheet empty.
14. Expand Asset Function.
> ***Observed***: Asset Function Statuses are empty for all AF listed in the DS.
15. Add input and output limits or set point values out of the limits mention for asset function.
16. Standard deviation error value is less than 0
> ***Observed***: Asset Function Status will update automaticaly as FAIL based on the standard deviation error values calculated.
17. Add input and ouput values or set point vaue within limit
> ***Observed***: Asset Function Status will update automaticaly as PASS or based on the asset error and tolerance values.
16. Repeat step 15 for all Asset Function listed. 
17. Check Data sheet(s) and verify Status is now populated. 

**Expected**: In this test case, the Status for the assest function is set according to Standard deviation error. Where standard deviation is less than 0 than status will update as Fail else it will update the status as per the tolerance calculation. 
