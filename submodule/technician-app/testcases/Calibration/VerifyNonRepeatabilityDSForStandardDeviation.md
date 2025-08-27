## 001 As a Calibration Engineer/ Supervisor, I should have the ability to keep default value for repeatibility check box as unchecked while editing/ creating data sheets for repeatability calibration. 
>

### Use Case 
As a Calibration Engineer/ Supervisor, I want to choose repeatability checkbox as unchecked and create a data sheet for repeatable calibration.
>
### Scenario 1 - Verify if technician can complete a WO with  a DS that has Is Repeatable OFF and standard deviation fields are readonly. ( Maximo Classic application).
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
4.  Keep Repetable checkbox unchecked [OFF].
5.  Go back to Data Sheet tab
6.  Verify in Asset Function 'Include Standard Deviation?' checkbox, 'Standard Deviation Input Limit' and 'Standard Deviation Output Limit' Fields are disabled.
7.  Fill in as per Box file the rest of the fileds for Asset Function
8.  Add calibration points 
9.  Save the DS
> Verify there's no error or message related to standard deviation. 
> Verify if Data Sheet is saved correctly. 
> Verify if 'Standard Deviation Input Limit/ Output Limit' fields are disabled. 
10. Approve Data sheet 
>
11. Go to Asset and create a calibration asset 
12. Go to Work Order Tracking and create a new WO
13. Add asset created in step 12
14. Click on Data Sheet tab
15. Click + to add a new DS; Click on Data sheet Detail Menu and select Go To Data Sheet Template 
16. Select DS created in Step one and click Return with value. 
>
17. Save WO and verify is saving successfully. 
18. Mark Work Order as Completed
19. Verify if WO can be compleated successfully.
20. Verify there's no error or message related to standard deviation. 

### Scenario 2 - Verify if technician can complete a WO with  a DS that has Is Repeatable OFF and standard deviation fields are readonly. ( Maximo Mobile).
>
**Pre-condition:**
NOTE: have a technician user created and added to Technician security group. 
Excel data sheet for Data Sheet: https://ibm.ent.box.com/file/1631643976004
>> Technician need to have permission to Data Sheet Templete app, Asset, Work Order Tracking
>> Have a DS created with average tolerance for repeatable calibration points created and assigned to 
1. Login to maximo classic/Manage as administrator.
2. Go to Data Sheet Template and create a DS with multiple Asset Functions ( at least 3 asset functions) and keep Repeatability checkbox as unchecked.
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
8. Verify if WO can be completed.
>
**Expected**: At the end of this scenario a Technician will be able to see the wo details.