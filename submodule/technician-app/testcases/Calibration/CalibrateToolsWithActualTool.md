## Verify MAXVAR Configuration for rotating asset to be integrated based on 3 options available in calibratio options under organization settings (warning, enforce or novalidation)
This test case will verify MAXVAR Configuration for rotating asset to be integrated based on 3 options available in calibratio options under organization settings (warning, enforce or novalidation)
### Environment Conditions should be separated by Datasheet 

### Scenario 1 - Verify No validation message should come if "No validation Between Tools and Status of CLOSED or COMP" configured in maximo classic

**Pre-condition:**
NOTE: have a technician user created and added to Technician security group.

1. Login to maximo classic/Manage as administrator.
2. Go to Organisation> Search for your organisation >calibration option>Work order other organisation >"No validation Between Tools and Status of CLOSED or COMP" >Selected
3. Go to Data Sheet Template and create  2 rotating asset and DS with some Asset Functions
4. Create another Data Sheet Template and add AF to it.
5. Mark as Approved the DS
6. Go to "work order tracking" app. and create a casset1 and casset2
7. Add a casset1 to this WO1 and casset2 to wo2 add the DS created in Steps 2 and 3
8. Add Technician user to the work order.
9. Approved the work order.


**Go to mobile app**
**Steps**

1. Login to Maximo mobile app with the Technician.
2. Click on "Technician" tile.
3. Click on nine dot navigation menu.
4. Open the Work Order listed.
5. Open WO1 order detail page
6. Navigate to Report page and click on add tool
7. Add tool Drawer should open now add casset2 in Rotating asset lookup
8. Save work order and change work order status to Complted or closed
9. search for created wo2 in work order list page
10. Open WO2 order detail page
11. Navigate to Report page and click on add tool
12. Add tool Drawer should open now add casset1 in Rotating asset lookup


**Results**
Due to "No validation Between Tools and Status of CLOSED or COMP" configuration user should be able to save Casset1 Asset in Rotating asset


### Scenario 2 - Verify  validation warning message should come if "Validate but with warning message" configured in maximo classic


**Pre-condition:**
NOTE: have a technician user created and added to Technician security group.

1. Login to maximo classic/Manage as administrator.
2. Go to Organisation> Search for your organisation >calibration option>Work order other organisation >"Validate but with warning message" >Selected
3. Go to Data Sheet Template and create  2 rotating asset and DS with some Asset Functions
4. Create another Data Sheet Template and add AF to it.
5. Mark as Approved the DS
6. Go to "work order tracking" app. and create a casset1 and casset2
7. Add a casset1 to this WO1 and casset2 to wo2 add the DS created in Steps 2 and 3
8. Add Technician user to the work order.
9. Approved the work order.


**Go to mobile app**
>
**Steps**

1. Login to Maximo mobile app with the Technician.
2. Click on "Technician" tile.
3. Click on nine dot navigation menu.
4. Open the Work Order listed.
5. Open WO1 order detail page
6. Navigate to Report page and click on add tool
7. Add tool Drawer should open now add casset2 in Rotating asset lookup
8. Save work order and change work order status to Complted or closed
9. search for created wo2 in work order list page
10. Open WO2 order detail page
11. Navigate to Report page and click on add tool
12. Add tool Drawer should open now add casset1 in Rotating asset lookup


**Results**
Due to "Validate but with warning message" configuration user should be able to save Casset1 Asset in Rotating asset but warning message should pop up on screen 

### Scenario 3 - Verify  User unable to add Rotating asset and error message should come if "Enforce validation between Tools and Status of CLOSED or COMP" configured in maximo classic


**Pre-condition:**
NOTE: have a technician user created and added to Technician security group.

1. Login to maximo classic/Manage as administrator.
2. Go to Organisation> Search for your organisation >calibration option>Work order other organisation >"Enforce validation between Tools and Status of CLOSED or COMP" >Selected
3. Go to Data Sheet Template and create  2 rotating asset and DS with some Asset Functions
4. Create another Data Sheet Template and add AF to it.
5. Mark as Approved the DS
6. Go to "work order tracking" app. and create a casset1 and casset2
7. Add a casset1 to this WO1 and casset2 to wo2 add the DS created in Steps 2 and 3
8. Add Technician user to the work order.
9. Approved the work order.


**Go to mobile app**
**Steps**

1. Login to Maximo mobile app with the Technician.
2. Click on "Technician" tile.
3. Click on nine dot navigation menu.
4. Open the Work Order listed.
5. Open WO1 order detail page
6. Navigate to Report page and click on add tool
7. Add tool Drawer should open now add casset2 in Rotating asset lookup
8. Save work order and change work order status to Complted or closed
9. search for created wo2 in work order list page
10. Open WO2 order detail page
11. Navigate to Report page and click on add tool
12. Add tool Drawer should open now add casset1 in Rotating asset lookup


**Results**
Due to "Enforce validation between Tools and Status of CLOSED or COMP" configuration user should not be able to save Casset1 Asset in Rotating asset and error message should come

### Scenario 4 - Verify  User able to add Rotating asset and error message should come if "Enforce validation between Tools and Status of CLOSED or COMP" configured and work order status is "in progress"


**Pre-condition:**
NOTE: have a technician user created and added to Technician security group.

1. Login to maximo classic/Manage as administrator.
2. Go to Organisation> Search for your organisation >calibration option>Work order other organisation >"Enforce validation between Tools and Status of CLOSED or COMP" >Selected
3. Go to Data Sheet Template and create  2 rotating asset and DS with some Asset Functions
4. Create another Data Sheet Template and add AF to it.
5. Mark as Approved the DS
6. Go to "work order tracking" app. and create a casset1 and casset2
7. Add a casset1 to this WO1 and casset2 to wo2 add the DS created in Steps 2 and 3
8. Add Technician user to the work order.
9. Approved the work order.


**Go to mobile app**
>
**Steps**
1. Login to Maximo mobile app with the Technician.
2. Click on "Technician" tile.
3. Click on nine dot navigation menu.
4. Open the Work Order listed.
5. Open WO1 order detail page
6. Navigate to Report page and click on add tool
7. Add tool Drawer should open now add casset2 in Rotating asset lookup
8. Save work order and change work order status to In progress
9. search for created wo2 in work order list page
10. Open WO2 order detail page
11. Navigate to Report page and click on add tool
12. Add tool Drawer should open now add casset1 in Rotating asset lookup

**Results**
"Enforce validation between Tools and Status of CLOSED or COMP" is configured but Wo1 status is in progress user  be able to save Casset1 Asset in Rotating asset lookup in wo2 work order 






