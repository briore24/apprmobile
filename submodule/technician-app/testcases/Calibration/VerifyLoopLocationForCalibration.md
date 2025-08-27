## Verify a Loop Location Flow on Technician Calibration mobile application.
>
A loop calibration is a set of instruments that are grouped and calibrated together. The input and output results of the entire group determines the pass or fail status of the calibration. Calibrating multiple assets with loop location offers several benefits, particularly in industries that rely heavily on precise and coordinated operations like Life Sciences, Nuclear, Manufacturing, Transportations etc. This coordinated approach is essential for maintaining high standards and optimal performance in complex and interconnected industrial systems. 
>
Personas: Lucas – Calibration Technician : This user is responsible for monitoring the infrastructure assets and calibrating them to ensure they are operating within reasonable tolerances.
>
### Scenario 1: User can execute Loop Location based Calibration work orders in a compliant manner
>
As a calibration technician, I want to be able to easily identify which data sheets go with which asset as well as the physical locations of those assets while executing loop location based work order, easily identify which data sheets go with which asset as well as the physical locations of those assets.
>
**Pre-condition:**
>
NOTE: have a technician user created and added to Technician security group. 
Excel data sheet for Data Sheet: https://ibm.ent.box.com/file/1631643976004
>
Technician need to have permission to Data Sheet Templete app, Asset, Work Order Tracking
1. Go to Location 
2. Create a Location and enabled,from Calibration details section, the Loop Calibration? flag. 
3. Save the Location
4. Create a Work Order tracking and add the 'Loop Location created in step 2.
5. Add a Data sheet, or more, the the work order tracking and approve the WO. 
>
**Go to mobile app**
>
**Steps**
>
1. Login to MAS Mobile app with technician credentials.
2. Click on "My Schedule" tile.
3. Click on "Work created by me" query in mobile.
4. Open the Work Order created in Pre-condition.
5. Verify section ' Location details' is present in work order.
6. Confirm that the 'Location Details' section displays the loop location created and linked to the work order in the preconditions.
>
**Expected Results:**
- Lucas successfully logs into the MAS Mobile application using his technician credentials.
- The "My Schedule" tile displays the relevant work orders created by Lucas.
- Upon selecting the work order, the 'Location Details' section is visible and accurately shows the loop location associated with the work order.
>
### Scenario 2: User can see a loop location tag on Calibration Details card
>
**Pre-condition:**
- Scenario one and pre-condition are executed.
>
**Go to mobile app**
>
**Steps**
>
1. Login to MAS Mobile app with technician credentials.
2. Click on "My Schedule" tile.
3. Click on "Work created by me" query in mobile.
4. Open the Work Order created in Pre-condition.
5. Verify if tag 'Loop Location' is present before calibration icon
>
**Expected Results:**
>
- Upon opening the work order, Lucas should see a 'Loop Location' tag displayed before the calibration icon on the calibration details card.
- This tag serves as a visual indicator that the associated asset is part of a loop location, enabling Lucas to quickly identify work orders linked to loop locations.
- The presence of the 'Loop Location' tag enhances the usability of the MAS Mobile application for calibration technicians, allowing them to efficiently manage and prioritize work orders based on loop location associations.
>
### Scenario 3: User can search for asset in asset list page 
>
**Pre-condition:**
- Scenario one and pre-condition are executed.
>
**Go to mobile app**
>
**Steps**
>
1. Login to MAS Mobile app with technician credentials.
2. Click on "My Schedule" tile.
3. Click on "Work created by me" query in mobile.
4. Open the Work Order created in Pre-condition.
5. Click Calibration icon. 
6. Verify if calibration technician user can see listed all the assets assigned to a Loop location. 
7. Verify if calibration technician user can search for an asset in the Asset list.
>
**Expected results:**
- Upon tapping the calibration icon, Lucas is directed to the asset list page, displaying all assets associated with the loop location.
- The asset list page includes a search functionality, enabling Lucas to quickly locate a specific asset by entering relevant search criteria (e.g., asset name, asset ID, or location).
- The search results should accurately reflect the assets matching the search query, allowing Lucas to efficiently identify and manage the assets within the loop location.
- The presence of the search functionality on the asset list page enhances the usability of the MAS Mobile application for calibration technicians, ensuring they can effectively locate and manage assets during loop location-based calibration work orders.
>
### Scenario 4: User can see the completion status of each data sheet within an asset on the asset list page
>
**Pre-condition:**
- Scenario one and pre-condition are executed.
>
**Go to mobile app**
>
**Steps**
>
1. Login to MAS Mobile app with technician credentials.
2. Click on "My Schedule" tile.
3. Click on "Work created by me" query in mobile.
4. Open the Work Order created in Pre-condition.
5. Click Calibration icon. 
6. Fill in As Found and As Left values for all listed AF. 
7. Verify if tag for Asset changed to green if all the AF are completed. 
>
**Expected results:**
- Upon navigating to the asset page, Lucas can view the completion status of each data sheet associated with the asset.
- The asset list page displays a clear and concise overview of the completion status for each data sheet, allowing Lucas to quickly assess the progress of calibration tasks for each asset within the loop location.
- If all data sheets for an asset are completed, the asset tag should change to green, visually indicating that the asset is fully calibrated.
The presence of the completion status for each data sheet on the asset list page enhances the usability of the MAS Mobile application for calibration technicians, ensuring they can efficiently monitor and manage the calibration process for assets within loop locations.