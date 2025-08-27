# View and Create Emergency Workorder from Create Quick Report

These test cases will verify the emergency workorders functionality on the navigator for Technician webapp and mobile containers in online and offline mode.

These test cases will cover functionalities of following user stories:

- GRAPHITE-70621: Emergency Workorder - Quick Reporting for Mobile 

## Scenario 1 - Verify that as a technician, the user should be able to create new emergency workorder for critical situations

**Pre-condition:**
1. Login with admin credentials in Maximo classic/Manage.
2. Enable 'Allow Quick reporting' in the object structures.
3. Set maximo.mobile.usetimer to '1'.

**Steps:**

1. Login to Maximo mobile app with the credentials of technician assigned to the work order.
2. Click on "+" button on navigator and click on Create quick report.
3. Verify that technician can click on Create quick report and navigate to Create quick report page.
4. Add description, asset, location or other details in the quick report.
5. Click on save button and loader should be displayed.

**Result:**

- Create quick report option should be displayed on the navigator and should navigate the technician to Create quick report page.
- Work type should be automatically set as EM.
- The priority of the workorder should be automatically set as ‘1’.
- Quick report should be saved without any errors.
- App loader should be displayed when user saves the work order.
- When user Creating work order by quick reporting Accept & Reject Button Should not be visible though assignment flow is enabled
- Work order should be unassigned and Status should be "In progress" if it is created by quick reporting
- When User is creating work order using Create quick report user is unable to see work order in assigned list page until user not refreshing page  
## Scenario 2 - Verify the status of emergency workorder is 'In Progress' and owner is set to the same user, creating the workorder

**Pre-condition:**

1. Login with admin credentials in Maximo classic/Manage.
2. Enable 'Allow Quick reporting' in the object structures.
3. Enable electronic signature(e-sig).
4. Set maximo.mobile.usetimer to '1'.

**Steps:**

1. Login to Maximo mobile app with the credentials of technician assigned to the work order.
2. Click on "+" button on navigator and click on Create quick report.
3. Verify that technician can click on Create quick report and navigate to Create quick report page.
4. Add description, asset, location or other details in the quick report.
5. Edit the existing priority and work type values. 
6. Click on save button.

**Result:**

- Workorder should be saved without any errors and verify the status of workorder is in 'In Progress' status.
- If physicalsignature property is enabled, after saving the quick report, physical signature popup should be displayed immediately and click save. Then, e-sig prompt should be displayed only for mobile(if enabled) and user enters e-sig key and click save. Workorder should then change the status to 'In Progress'.
- If, physicalsignature is set and user clicks cancel button, then there should be no changes to workorder status. It will dispaly WAPPR status.
- If maximo.mobile.usetimer is 0 it will not start work order.
- The owner should be set to the one who makes the workorder.
- If user clicks and save physical signature, attachment count should display as '1' in WO Details page.
- Technician should be immediately able to book hours and materials against that workorder, record labor hours and complete the workorder.

**Note:**
- Electronic Signature(e-sig) is only applicable for mobile.
- E-sig or physical signature both will retain it's previous state if either of them is cancelled. 


## Scenario 3 - Verify UI of the cards/pages/views for fonts, font sizes, color, text completeness, alignment, positioning etc. is correct and as per design on mobile and other small & large screen devices for all supported form factors

**Pre-condition:**

Pre-conditions as specified for above mentioned test scenarios.

**Steps:**

1. Login to Maximo mobile app with the credentials of technician assigned to work order.
2. Click on "My Schedule" tile to open the list of assigned work orders.
3. Navigate to screens/pages/cards of above mentioned scenarios.
4. Verify UI of the cards/pages for fonts, font sizes, font color, text completeness, alignment, positioning etc. is correct and as per design.
5. Navigate and perform above mentioned test scenarios on mobile and other small screen devices for all supported form factors.

**Result:**

- UI of the cards/pages for fonts, font sizes, color, text completeness, alignment, positioning etc. should be correct and as per design.
- There should be no UI related issues.
- Smart Input version of the components should be used.
- The application should behave as per expectations for all above mentioned test scenarios on mobile and other small screen devices for all supported form factors.
- The UI should be as per design for respective form factor.

## Scenario 4 - Verify all the above test scenarios on mobile devices in online and offline mode

**Pre-condition:**

Pre-conditions as specified for above mentioned test scenarios.

**Steps:**

Perform steps as specified for above mentioned test scenarios.

**Results:**

The application should behave as per expectations for all above mentioned test scenarios on mobile devices in online and offline mode.
