## 001 As a Calibration Engineer/ Supervisor, I should have the ability to choose Standard deviation check box while editing/ creating data sheets for repeatability calibration. I should have the ability to define acceptable limit for standard deviation.
>
### Detailed Description
Currently, the calculation for repeatable calibration points is done based on average tolerance, which is not sufficient. In 9.1, we want to use the standard deviation also to get more accurate results. As part of this feature, we need to add a checkbox in Data Sheet Template Application (inside Asset Functions table) to indicate whether to include Standard Deviation or not.
>
### Use Case 
As a Calibration Engineer/ Supervisor, I want to be able to choose Standard deviation checkbox so that I can include standard deviation along with average tolerance for repeatable calibration points calculation.
>
### Scenario 1 - Verify if technician can complete a WO with  a DS that has standard deviation and Is Repeatable ON ( Maximo Classic application).
>
**Pre-condition:**
NOTE: have a technician user created and added to Technician security group. 
Excel data sheet for Data Sheet: https://ibm.ent.box.com/file/1631643976004
>> Technician need to have permission to Data Sheet Templete app, Asset, Work Order Tracking
>
**Steps**

1. Go to Data Sheet Template and create a new one
NOTE: pick an example from list https://ibm.ent.box.com/file/1452170128608 
2.  Give a name 
3.  Click + and add a Asset Functions 
4.  Click Repetable checkbox; Click ON configuration tab and add the correct value in 'Repeat value' filed. For example 2.
5.  Go back to Data Sheet tab
6.  in Asset Function check 'Include Standard Deviation?' checkbox
7.  Add value for 'Standard Deviation Input Limit' example: 1
8.  Add value for 'Standard Deviation Output Limit' example: 1
9.  Fill in as per Box file the rest of the fileds for Asset Function
10. Add calibration points 
11. Save the DS
> Verify if Data Sheet is saved correctly. 
> Verify if 'Standard Deviation Input Limit/ Output Limit' values are listed. 
12. Approve Data sheet 
>
13. Go to Asset and create a calibration asset 
14. Go to Work Order Tracking and create a new WO
15. Add asset created in step 13
16. Click on Data Sheet tab
17. Click + to add a new DS; Click on Data sheet Detail Menu and select Go To Data Sheet Template 
18. From step 17 select DS created in Step one and click Return with value. 
19. Save Work order 
>
20. Expand Asset Function and Verify if 'Standard Deviation Input Limit/ Output Limit' values are listed. 
21. Expand each Calibration point and add values for As Found and As Left
22. Save WO and verify is saving successfully. 
23. Verify Standard deviation is calculated.
>
24. Mark Work Order as Completed
25. Verify if WO can be compleated successfully.
>
### Scenario 2 - Verify if technician can complete a WO with  a DS that has standard deviation and Is Repeatable ON ( Maximo Mobile).
>
**Pre-condition:**
NOTE: have a technician user created and added to Technician security group. 
Excel data sheet for Data Sheet: https://ibm.ent.box.com/file/1631643976004
>> Technician need to have permission to Data Sheet Templete app, Asset, Work Order Tracking
>> Have a DS created with average tolerance for repeatable calibration points created and assigned to 
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
5. Click on Calibration icon.
6. Navigate to Data Sheet Page.
7. Open a Calibration Asset function 
8. Fill in data for As Found and As Left 
9. Verify if AF is saved corectly
10. Verify if WO can be completed.
>
**Expected**: At the end of this scenario a Technician will be able to see to see the standar deviation calculated for As Found and As Left for a repeatable DS. A Technician can close a WO once As Left and As Found are meeting the requirements. 
>