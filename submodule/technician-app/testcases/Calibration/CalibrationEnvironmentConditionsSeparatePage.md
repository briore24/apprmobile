## Verify Environment Conditions page to be listed for each DS on mobile "My Schedule" application
This test case will verify if Environment Conditions page is listed for each Data Sheet listed for a work order. 
>
### Environment Conditions should be separated by Datasheet 
>
> #### Salesforce case number TS016955108

### Scenario 1 - Verify if each Data sheet listed for a WO has listed a Environment Conditions page 

**Pre-condition:**
NOTE: have a technician user created and added to Technician security group. 

1. Login to maximo classic/Manage as administrator.
2. Go to Data Sheet Template and create a DS with some Asset Functions 
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
5. Click on Calibration icon.
6. Navigate to Data Sheet Page.
7. The two DS are listed for user.
8. Open first DS 
> ***Observed***: Data sheet  one has listed the  Environment Conditions page.
9. Go back the DS list and open second DS 
10. Open second DS
> ***Observed***: Data sheet  two has listed the  Environment Conditions page.
> ***Expected***: Each DS in this work order to have an Environment Conditions page.
>
### Scenario 2 - Verify if each Data sheet listed for a WO has listed a Environment Conditions page and values can be saved individual 

**Pre-condition:**
Have Scenario 1 setup.
**Go to mobile app**
>
**Steps**

1. Login to MAS Mobile app with technician credentials.
2. Click on "My Schedule" tile.
3. Click on "Work created by me" query in mobile.
4. Open the Work Order listed.
5. Click on Calibration icon.
6. Navigate to Data Sheet Page.
7. Open first DS 
8. Open Environment Conditions page.
9. Fill in all values for fileds
10. Click save
> ***Observed***: Environment Conditions page saved successfully.
11. Go back to the DS list and select second Environment Conditions page
12. Fill in all values for fileds
13. Click save
> ***Observed***: Environment Conditions page saved successfully.
14. Go to first DS and opened Environment Conditions page 
15. Verify is first DS Environment Conditions page is not displaying second DS values
> ***Expected***: Environment Conditions page for each DS are saved successfully. 

