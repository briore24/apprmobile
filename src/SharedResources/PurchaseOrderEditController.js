/*
 * Licensed Materials - Property of IBM
 *
 * 5724-U18, 5737-M66
 *
 * (C) Copyright IBM Corp. 2022,2023 All Rights Reserved
 *
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with
 * IBM Corp.
 */

import {log, Device} from "@maximo/maximo-js-api";
import SynonymUtil from './Technician/utils/SynonymUtil';
import WOCreateEditUtils from './Technician/utils/WOCreateEditUtils';
import CommonUtil from './Technician/utils/CommonUtil';
const TAG = "PurchaseOrderEditController";

class PurchaseOrderEditController {

  /**
   * defines fields to implement onCustomSaveTransition() 
   * which is to avoid defaultSaveTransition defined at framework level
   * and uses save method implemented in controller itself.
   */
  constructor() {
    this.onUpdateDataFailed = this.onUpdateDataFailed.bind(this);
    this.saveDataSuccessful = true;
    this.callDefaultSave = true;
    this.validLocationValue = null;
    this.validAssetValue = null;
  }

  /**
  * Updates the selected items array based on the provided action.
  * Adds the item if `add` is true, otherwise removes it.
  *
  * @param {Object} evt - The event object containing item details.
  * @param {boolean} add - Whether to add (`true`) or remove (`false`) the item.
  */
  updateSelection(evt, shouldAdd) {
    const multiid = evt?.item?.multiid || evt?.item?.anywhererefid;
    if (!multiid) return;

    const selectedArray = CommonUtil.sharedData.selectedMultiAssets || [];
    const itemIndex = selectedArray.indexOf(multiid);

    if (shouldAdd && itemIndex === -1) {
      selectedArray.push(multiid);
    } else if (!shouldAdd && itemIndex !== -1) {
      selectedArray.splice(itemIndex, 1);
    }

    CommonUtil.sharedData.selectedMultiAssets = selectedArray;
    evt.item.hideDeleteIcon = shouldAdd;
  }

  /**
   * Marks an item as selected (for deletion).
   * 
   * @param {Object} evt - The event object containing item details.
   */
  deleteMultiLocCi(evt) {
    this.updateSelection(evt, true);
  }

  /**
   * Unselects an item (undo deletion).
   * 
   * @param {Object} evt - The event object containing item details.
   */
  undoMultiLocCi(evt) {
    this.updateSelection(evt, false);
  }

  pageInitialized(page, app) {
    log.t(TAG, "Work order edit Page Initialized");
    this.app = app;
    this.page = page;
    this.page.state.isLocationAssetFocus = false;
  }

  // Assisted by WCA@IBM
  // Latest GenAI contribution: ibm/granite-20b-code-instruct-v2
  /**
   * This function is called when the user clicks the "Close" button on the confirmation dialog.
   * It sets the asset number and location values in the dsCreateWo object based on the selected options.
   */
  onUserConfirmationClose() {
    const dsWoedit = this.app.findDatasource("dsWoedit");
    WOCreateEditUtils.onUserConfirmationClose(this, dsWoedit);
  }
  
  /**
   * If we get the data from the session then will fetch the data from the session and will call the loadRecord
   * If will get workorder data from the page params then will fetch the data from page params and will call the loadRecord
   */
  async pageResumed(){
    this.app.state.pageLoading = true;
    this.page.state.useConfirmDialog = true;
    this.page.state.saveDisable  = false;
    this.page.state.disableButton = false;
    this.page.state.zRefDisable = false;
    this.classificationChanged = false;
    this.page.state.isMobile = Device.get().isMaximoMobile;
    const savedWorkorder=sessionStorage.getItem('woedit_workorder');
    WOCreateEditUtils.loadYRefData(this.app,this.page);
    /* istanbul ignore if  */
    if(this.page?.params?.wo){
      await this.loadRecord(this.page?.params?.wo);
    }else if(savedWorkorder){
      await this.loadRecord(JSON.parse(savedWorkorder));
    }
    this.app.state.pageLoading = false;
  }

  inComingContext(){
    return this.app.state.incomingContext?.page === "workOrderDetails" && this.app.state.incomingContext?.assetnum;
  }

  /**
   * Function to load edit WorkOrder datasource..
   * @param {*} workorder
   */
  async loadRecord(workorder, currentAsset) {
    sessionStorage.removeItem("woedit_workorder");
    // Reset Error State
    this.page.state.readOnlyState = false;
    this.page.state.errorMessage = "";
    this.page.state.linearAssetAvailable = false;
    this.page.state.hasMultiAssetLocCI = CommonUtil.sharedData.isFollowupWO = !!this.page.params.followup;

    this.page.state.pageTitle = this.app.getLocalizedLabel(
      "edit_work_order_label",
      "Edit work order"
    );
    this.page.state.followupWOCreated = false;
    if (this.page.params.followup) {
      this.page.state.pageTitle = this.app.getLocalizedLabel(
        "create_followup_label",
        "Create follow-up WO"
      );
      let newWods = this.page.datasources["newWorkOrderds"];
      newWods.clearState();
      newWods.resetState();
      await newWods.addNew();
    }
    this.page.state.title = this.page.state.pageTitle;
    let dsWoedit = this.page.datasources["dsWoedit"];
    let dsWoEditSetting = this.app.findDatasource("wpEditSettingDS");
    let woEditResource = this.app.findDatasource("woDetailResource");
    if (Device.get().isMaximoMobile) {
      await woEditResource.forceReload();
    }
    if (CommonUtil.sharedData.isFollowupWO) {
      const WOMultiAssetLocCIItems = await this.app.findPage("workOrderDetails")?.findDatasource("woMultiAssetLocationds").forceReload();
      this.page.state.hasMultiAssetLocCI = !!WOMultiAssetLocCIItems?.length;
      await this.loadMultiAssetLocCiData(WOMultiAssetLocCIItems);
    }
    let schema = woEditResource.getSchema();
    if ( this.app.state.incomingContext?.page === "workOrderDetails" && this.app.state.incomingContext?.assetnum) {
      schema=JSON.parse(sessionStorage.getItem("woDetailResource_schema"));
      sessionStorage.removeItem('woDetailResource_schema');
      
      dsWoEditSetting.setSchema(JSON.parse(sessionStorage.getItem("dsWoEditSetting_schema")));
      sessionStorage.removeItem('dsWoEditSetting_schema');
    }
    if (this.page.params.followup) {
      this.page.state.useConfirmDialog = true;
    }
    dsWoedit.setSchema(schema);
    await dsWoEditSetting.initializeQbe();
    // in case workorder edit fails it doesn't finds orgid in that case have added fallback case
    const orgId = workorder.orgid || this.app.client.userInfo.insertOrg;
    dsWoEditSetting.setQBE('orgid', '=', orgId);
    dsWoEditSetting.setQBE('status', '=', workorder.status_maxvalue);
    await dsWoEditSetting.searchQBE();

    let workorderData = [];
    /* istanbul ignore if  */
    if (workorder) {
      if (!this.page.params.followup) {
        this.page.state.pageTitle = this.app.callController('updatePageTitle', {page: this.page, label: 'edit_work_order', labelValue: 'Edit work order'});
      }
      let attributes= dsWoedit.baseQuery.select.split(',');
      let obj={};
      attributes.forEach((element) => {
        if (element === 'worktype') { 
          workorder[element] = workorder[element] || '';
          this.page.state.worktype = workorder[element];
        }
        
        /* istanbul ignore if  */
        if (this.page.params.followup && !(this.app.state.incomingContext && this.app.state.incomingContext.page === "workOrderDetails" && this.app.state.incomingContext.assetnum)) {
          if (['targstartdate', 'targcompdate', 'schedstart', 'schedfinish', 'actstart'].includes(element)) {
            workorder[element] = '';
          }

          if (element === 'estdur') {
            workorder[element] = 0;
          }
        }

        obj[element] = workorder[element];

        // Set Value to HierarchyPath Field
        /* istanbul ignore else */
        if (element === 'hierarchypath') {
          const hierarchypath = workorder.classstructure?.hierarchypath || '';
          obj.hierarchypath = hierarchypath;
          this.page.state.hierarchypath = hierarchypath;
          const specDatas = this.app.findDatasource('woDetailResource').item.workorderspec;
          WOCreateEditUtils.loadSpecifications(this.app, specDatas);
        }

        /* istanbul ignore else */
        if (element === 'classstructureid') {
          const classstructureid = workorder.classstructure?.classstructureid || '';
          obj.classstructureid = classstructureid;
          this.page.state.classstructureid = classstructureid;
        }
         });

      workorderData.push(obj);
      }
    this.page.state.workorderData = workorderData;
    dsWoedit.setSchema(this.page.params.woSchema);
    await dsWoedit.load({ src: workorderData, noCache: true });
    this.readOnlyFields(dsWoedit);
          /* istanbul ignore else */  

    if (this.inComingContext()) {
      this.setIncomingContext();
    }
    this.app.state.pageLoading = false;
  }

    /**
   * Function to open WorkType lookup with preselected item
   */
    async openWorkTypeLookup(evt) {
      let defOrg = evt.item.orgid || this.app.client.userInfo.insertOrg;
      let typeDs = this.app.findDatasource("dsworktype");
      // istanbul ignore next
      if (typeDs && defOrg) {
        typeDs.clearState();
        // istanbul ignore next
        if (!this.page.state.worktype) {
          typeDs.clearSelections();
          const selectedItem = typeDs.items.find(item => item.worktype === this.page.state.worktype);
          if (selectedItem) {
            typeDs.setSelectedItem(selectedItem, true);
          }
        }  
        this.page.showDialog("workTypeLookup");
        this.page.state.dialogOpened = true;
      }
    }

  /**
   * This function called when click on any item in worktype lookup
   * @param {*} event 
   */
  async selectWorkType(event){
    WOCreateEditUtils.selectWorkType(this.page, 'dsWoedit', event.worktype);
  }

  /**
   * Function to update work order
   * @param {event} event 
   */
  async updateAndSaveWorkOrder(event) {
    // Workaround to validate fields before saving
    // Graphite's duration fields is not triggering onValueChanged when field made blank which is not validating value
    this.page.state.errorMessage = this.validateFields();
    if(this.page.state.errorMessage || this.page.state.readOnlyState) {
      let toastTitle = this.app
          .getLocalizedLabel(
            'messages_issue_on_save_body',
            'Fix the indicated errors. Then, save your changes.'
          );
        let toastBody = this.app
          .getLocalizedLabel(
            'messages_issue_on_save_title',
            'Unable to save due to errors on the page'
          );
        this.app.toast(toastBody, 'error', toastTitle);
      return
    }
    this.page.state.saveInProgress = true;
    if(this.page.state.editAssetsLocation) {
      this.page.state.updateWorkorderItem = event;
      this.page.state.saveInProgress=true;
      if (this.page.state.isLocationAssetFocus) {
        return;
      }
    }
    
    this.page.state.isLocationAssetFocus = false;
    
    let dsWoedit = event.page.datasources["dsWoedit"];
    /* istanbul ignore else */
      await WOCreateEditUtils.setPriorityFailureCode(this.app, dsWoedit);
  
    if (event.page.params.followup) {
      await this.createFollowupWo(event);
    } else {
      await this.preparePayload(dsWoedit);
    }

    this.page.state.saveInProgress = false;
  }

  /**
   * Prepare the payload
   *  Reflect.deleteProperty:-  It deletes a property from an object. It same as delete operator
   * */
     async preparePayload(dsWoedit){
      let workorder = dsWoedit.item ? { ...dsWoedit.item } : {};
      let dataToUpdate = { ...workorder };
      let linearAsset = this.app?.findDatasource("linearAsset");

    
      Object.keys(dataToUpdate).forEach(function (key) {
        if (
          dsWoedit?.schema?.required?.includes(key) &&
          dataToUpdate[key] === undefined
        ) {
          delete dataToUpdate[key];
        }
      });

      delete dataToUpdate.locationnum; 
      delete dataToUpdate.workorderid;
      delete dataToUpdate.wonum;

      let woEditResource = this.app.findDatasource("woDetailResource");
      let parentWo = woEditResource.item;
      //Setting the interactive to false on the basis isMobile
      woEditResource.dataAdapter.options.query.interactive = this.page.state.isMobile ? 0 : 1;
      // istanbul ignore next
      if (dataToUpdate) {
        let workAsset =
          woEditResource?.item?.assetnum
            ? woEditResource.item.assetnum
            : undefined;
        let workLocation =
          woEditResource?.item?.locationnum
            ? woEditResource.item.locationnum
            : undefined;

        let attributes = [];
        let responseProps = "";
        if (dsWoedit.baseQuery) {
          attributes = dsWoedit.baseQuery.select.split(",");
          responseProps = dsWoedit.baseQuery.select;
        }
        let localPayload = {};
        attributes.forEach((element) => {
          //DT208126 Maximo Mobile-Error is retained even after the record is corrected
          if (workorder[element] === null || workorder[element] === '' && !["classstructureid", "hierarchypath"].includes(element)) {
            workorder[element] = undefined;
          }
          //END OF DT
          localPayload[element] = workorder[element];
        });

        // istanbul ignore else
        if (this.classificationChanged) {
          if (Device.get().isMaximoMobile) { 
            await WOCreateEditUtils.deleteSpecification(this.app);
          }
          const specDatas = await WOCreateEditUtils.updateSpecificationAttributes(this.app);
          localPayload.workorderspec = specDatas;
          localPayload.classstructure = {
            hierarchypath: workorder.hierarchypath,
            classstructureid: workorder.classstructureid
          }
          dataToUpdate.workorderspec = specDatas;
          dataToUpdate.classstructure = {
            hierarchypath: workorder.hierarchypath,
            classstructureid: workorder.classstructureid
          }
        }

        let option = {
          responseProperties: responseProps
        };
        if(dataToUpdate.failuredescription) {
          delete dataToUpdate.failuredescription
        }
        if(dataToUpdate.failurelistid) {
          delete dataToUpdate.failurelistid
        }
        
       
        option.localPayload = localPayload;
        if( this.callDefaultSave ) {
          this.page.state.useConfirmDialog = false;
        }
        this.saveDataSuccessful = true;
        woEditResource.on('update-data-failed', this.onUpdateDataFailed);

        //Do not go for update if there is any client side error
        if (!this.page.state.errorMessage) {
         await woEditResource.update(dataToUpdate, option);
         /* istanbul ignore next */
          window.setTimeout(() => {
          this.page.state.useConfirmDialog = false;
          this.navigateDtlsPage(parentWo);
            /* istanbul ignore else*/
            let detailPage = this.app.findPage("workOrderDetails");
            if(detailPage){
              detailPage.state.isLinear = this.page.state.assetLinear;
            }
          //DT189589 Incorrect Save Message when trying to edit Follow Up Work
        }, 50);
        } else {
          this.saveDataSuccessful = false;
        }
        
        woEditResource.off('update-data-failed', this.onUpdateDataFailed);
      }
    }

  /**
  * Navigate to details page
  * */
  async navigateDtlsPage(parentWo) {
    /* istanbul ignore if */
    if (this.page.state.assetLinear) {
      let linearAssetDs = this.app.findDatasource("linearAsset");
      await linearAssetDs?.load({ src: [parentWo?.multiassetlocci], noCache: true });
    }
    this.app.setCurrentPage({
      name: "workOrderDetails",
      resetScroll: true,
      params: {
        wonum: parentWo.wonum,
        href: parentWo.href,
        siteid: parentWo.siteid,
        prevPage: "editwo"
      }
    });
  }
  
  /**
   * Update work order on user confirmation dialog save
   * */
  async onCustomSaveTransition() {
    this.callDefaultSave = false;
    await this.updateAndSaveWorkOrder({page: this.page});    
    return {saveDataSuccessful: this.saveDataSuccessful, callDefaultSave: this.callDefaultSave};
  } 

  /**
   * function called On calender datetime value changed
   */
  // istanbul ignore next
  onValueChanged(changeObj) {
    this.page.state.errorMessage = this.validateFields();
    if(changeObj.field === 'endmeasure'){
      if((changeObj.oldValue === changeObj.newValue?.toString() || changeObj.item.endmeasure === changeObj.oldValue)){
        this.page.state.endMeasureUpdate = true;
      }else{
        this.page.state.endMeasureUpdate = false;
      }
    }
   if(changeObj.field === 'startmeasure'){
        if(changeObj.oldValue === changeObj.newValue?.toString() || changeObj.item.startmeasure === changeObj.oldValue){
      this.page.state.measureUpdate = true;
    }else{
      this.page.state.measureUpdate = false;
    }
   }

    //Used to update the value if we remove startfeaturelabel and endfeaturelabel   
    if (changeObj.field === 'startfeaturelabel' && !changeObj.newValue) {
      this.page.state.startOffsetReadOnly = true;
      changeObj.datasource.item.startfeaturelabel = "";
        changeObj.datasource.item.startassetfeatureid = null;
        changeObj.datasource.item.startmeasure = null;
        changeObj.datasource.item.startoffset =null;
    }

    if (changeObj.field === 'endfeaturelabel' && !changeObj.newValue) {
      this.page.state.endOffsetReadOnly = true;
      changeObj.datasource.item.endoffset =null;
        changeObj.datasource.item.endfeaturelabel = "";
        changeObj.datasource.item.endassetfeatureid = null;
      changeObj.datasource.item.endmeasure = null;
    }
  }

  /**
  * Function to validate if field is ui required and not has value on attribute
  * @param attributeName attribute name
  * @param attributeValue attribute value
  * @returns  true or false
  */
 uiRequired(attributeName, attributeValue){
  return WOCreateEditUtils.uiRequired(this.page, 'dsWoedit', attributeName, attributeValue);
 }

validateAndHandleWarnings = (fieldName, fieldValue, arrayListFieldsWithError) => {
  if (this.uiRequired(fieldName, fieldValue)) {
      arrayListFieldsWithError.push({ "attributename": fieldName, "error": true });
      if (fieldName !== 'worktype') {
        this.showWOWarnings(fieldName, "");
        }
    } else {
        arrayListFieldsWithError.push({ "attributename": fieldName, "error": false });
        this.clearWarnings(fieldName);
      }
  };

  updateFieldError = (fieldName, hasError,arrayListFieldsWithError) => {
    arrayListFieldsWithError.push({ "attributename": fieldName, "error": hasError });
  }

  /**
   * Function to validate workorder fields
   */
  validateFields() {
    if (this.page.datasources && this.page.datasources?.dsWoedit?.items[0]) {
      let workorder = this.page.datasources.dsWoedit.items[0];
      let arrayListFieldsWithError = [];
      let errorMessage = "";
      let errorField = "";
      let description = this.getTrimmedDescription(workorder);
      const WOSchedulingDates = CommonUtil.getSystemProp(this.app, 'maximo.mobile.WOSchedulingDates');

      /* DT221877 - MASISMIG-42208 */
      this.validateAndHandleWarnings(
        "description",
        description,
        arrayListFieldsWithError
      );
      this.validateAndHandleWarnings(
        "worktype",
        workorder.worktype,
        arrayListFieldsWithError
      );

      /* DT221877 - MASISMIG-42208 */
      //DT179992: Mobile, required fields not working
      let minEstimatedDuration = this.page.state.minEstimatedDuration ?? 0;
      const isPriorityOutOfRange =
        workorder.wopriority < this.page.state.minPriority ||
        workorder.wopriority > this.page.state.maxPriority;
      const isPriorityInvalid =
        workorder.wopriority &&
        (isPriorityOutOfRange ||
          this.uiRequired("wopriority", workorder.wopriority));

      if (isPriorityInvalid) {
        if (isPriorityOutOfRange) {
          errorMessage = this.app.getLocalizedLabel(
            "priority_error_msg",
            `Priority ${workorder.wopriority} is not a valid priority value between ${this.page.state.minPriority} and ${this.page.state.maxPriority}`,
            [
              workorder.wopriority,
              this.page.state.minPriority,
              this.page.state.maxPriority,
            ]
          );
          errorField = "wopriority";
          this.showWOWarnings(errorField, errorMessage);
          this.page.state.errorMessage = errorMessage;
        }
        this.updateFieldError("wopriority", true, arrayListFieldsWithError);
      } else {
        this.clearWarnings("wopriority");
        this.updateFieldError("wopriority", false, arrayListFieldsWithError);
      }
      // Assisted by WCA@IBM
      // Latest GenAI contribution: ibm/granite-8b-code-instruct
      /**
        * Validate two dates to ensure they are valid and within the correct range.
        * @param {string} startField - The id of the start date field.
        * @param {string} endField - The id of the end date field.
        * @param {string} errorFieldLabel - The label for the error message field.
      */
      const validateDates = (startField, endField, errorFieldLabel) => {
        let checkDatesValidation;
        if (this.page.params.fromQuickReport) {
          checkDatesValidation = false;
        } else {
          checkDatesValidation = (workorder[startField] && workorder[endField] && workorder[startField] > workorder[endField])
            || this.uiRequired(startField, workorder[startField])
            || this.uiRequired(endField, workorder[endField]);
        }
        if (checkDatesValidation) {
          const errorMessage = this.app.getLocalizedLabel(
            `startdate_enddate_compare_msg`,
            `The ${errorFieldLabel} start date must be before the finish date`
          );
          this.showWOWarnings(startField, errorMessage);
          this.page.state.errorMessage = errorMessage;
          this.updateFieldError(startField, true, arrayListFieldsWithError);
          this.updateFieldError(endField, true, arrayListFieldsWithError);
        } else {
          this.clearWarnings(startField);
          this.updateFieldError(startField, false, arrayListFieldsWithError);
          this.updateFieldError(endField, false, arrayListFieldsWithError);
        }
      }
      if (WOSchedulingDates?.includes('SCHEDULE') && !this.page.params.fromQuickReport) {
        validateDates("schedstart", "schedfinish", "scheduled");
      }

      if (WOSchedulingDates?.includes('TARGET') && !this.page.params.fromQuickReport) {
        validateDates("targstartdate", "targcompdate", "target");
      }

      if (this.page.params.fromQuickReport) {
        validateDates("actstart", "actfinish", "actual");
      }
      if (
        workorder.estdur < minEstimatedDuration ||
        this.uiRequired("estdur", workorder.estdur)
      ) {
        errorMessage =
          minEstimatedDuration > 0
            ? this.app.getLocalizedLabel(
                `est_dur_msg`,
                `The duration should be greater then ${minEstimatedDuration}`,
                [minEstimatedDuration]
              )
            : this.app.getLocalizedLabel(
                `est_dur_msg`,
                `The duration should be positive value`
              );
        errorField = "estdur";
        this.showWOWarnings(errorField, errorMessage);
        this.page.state.errorMessage = errorMessage;
        this.updateFieldError("estdur", true, arrayListFieldsWithError);
      } else {
        this.clearWarnings("estdur");
        this.updateFieldError("estdur", false, arrayListFieldsWithError);
      }
      if((!workorder.assetnum && this.uiRequired("assetnum", workorder.assetnum))) {
        errorMessage = this.app.getLocalizedLabel(
            `numberinput_fieldRequiredText`,
            `Fill the required fields`
          );
        errorField = "assetnum";
        this.showWOWarnings(errorField, errorMessage);
        this.page.state.errorMessage = errorMessage;
        this.updateFieldError("assetnum", true, arrayListFieldsWithError);
      } else {
        this.clearWarnings("assetnum");
        this.updateFieldError("assetnum", false, arrayListFieldsWithError);
      }
      arrayListFieldsWithError = this.validateLinearForm(
        workorder,
        arrayListFieldsWithError
      );
      this.page.state.readOnlyState = !!arrayListFieldsWithError.find(
        (data) => data.error === true
      );
      WOCreateEditUtils.saveDisable(this.page);
      return errorMessage;
    }
  }

  getTrimmedDescription(workorder){
   return workorder?.description !== undefined ? workorder?.description.trim() : workorder?.description;
  }
  
   // Assisted by WCA@IBM
  // Latest GenAI contribution: ibm/granite-20b-code-instruct-v2
  //This function validates the start and end feature label field in the layout editor. If the value entered is not a valid number or percentage, an error message is displayed and the user is prompted to enter a valid value.
  validateStartEndFeatureLabel(hasError, errorField, arrayListFieldsWithError) {
    if (hasError === -1) {
      const errorMessage = this.app.getLocalizedLabel(
        `valid_featurelabel`,
        `Enter a valid reference feature on this asset`
      );
      this.showWOWarnings(errorField, errorMessage);
      this.page.state.errorMessage = errorMessage;
      arrayListFieldsWithError.push({ "attributename": errorField, "error": true });
    } else {
      this.clearWarnings(errorField);
      arrayListFieldsWithError.push({ "attributename": errorField, "error": false });
    }
    return arrayListFieldsWithError;
  }

  /**
   * Function to set field warnings
   */
  showWOWarnings(field, message) {
    WOCreateEditUtils.showWOWarnings(this.page, "dsWoedit", field, message);
  }

  /**
   * Function to clear field warnings
   */
  clearWarnings(field) {
    WOCreateEditUtils.clearWarnings(this.page, "dsWoedit", field);
  }

  // istanbul ignore next
  async UpdateTheParentWO() {
    const wodetailsDs = this.app.findDatasource("woDetailResource");
    let option = {
      responseProperties: wodetailsDs.baseQuery.select,
    };
    let dataToUpdate = { hasfollowupwork: true, href: wodetailsDs.item.href };
    wodetailsDs.on("update-data-failed", this.onUpdateDataFailed);
    await wodetailsDs.update(dataToUpdate, option);

    await wodetailsDs.load({ noCache: true }, true);
  }

    // Assisted by WCA@IBM
  // Latest GenAI contribution: ibm/granite-20b-code-instruct-v2
  /**
   * @description - This function will check if all the required fields are filled before saving the data
   * @param {object} dsWoedit - Data set work on edit
   */
  readOnlyFields(dsWoedit) {
    // istanbul ignore else
    if (dsWoedit?.item) {
      const items = dsWoedit?.item ? Object.keys(dsWoedit.item) : '';
      const requireValues =  dsWoedit?.uiRequired?.undefined ?? '';
      //istanbul ignore next
      this.page.state.readOnlyState =
        requireValues &&
        !requireValues.every((r) =>
          items?.includes(r) && dsWoedit.item[r] !== ""
        );
      dsWoedit.state.canSave = false;
    }
  }
  /**
   * Creates a new follow-up work order.
   * @param {*} event 
   */
  async createFollowupWo(event) {
    this.page.state.saveInProgress= true;
    let woDetailRelatedWorkOrder;
    const newWods = this.app.findDatasource("newWorkOrderds");
    let relatedPage = this.app.findPage('relatedWorkOrder');
    let relatedWOds = relatedPage.datasources["woDetailRelatedWorkOrder"];
    let relatedListDS = relatedPage.datasources["relatedrecwo"];
    const dsWoedit = this.app.findDatasource("dsWoedit");
    const woDetailResource = this.app.findDatasource("woDetailResource");
    let parentWo = relatedWOds?.item ?? woDetailResource.item;
    const synonymdomainDS = this.app.datasources['synonymdomainData'];
    const statusData = await SynonymUtil.getDefExtSynonymValueIdWithOrgSite(synonymdomainDS, 'WOSTATUS', 'WAPPR', parentWo?.orgid, parentWo?.siteid);
    let response;
    let relateTypeFUPData = await SynonymUtil.getSynonym(synonymdomainDS, 'RELATETYPE', 'RELATETYPE|FOLLOWUP');
    let relateTypeOrgData = await SynonymUtil.getSynonym(synonymdomainDS, 'RELATETYPE', 'RELATETYPE|ORIGINATOR');

    let attributes = [];
    /* istanbul ignore else */
    if (newWods?.baseQuery) {
      attributes = newWods.baseQuery.select.split(","); 
    }

    //Remove workorderid and wonum before creating followup from parent WO ds.
    delete dsWoedit.item.workorderid;
    delete dsWoedit.item.wonum;

    attributes.forEach((element) => {
      newWods.item[element] = dsWoedit.item[element];
    });

    newWods.item.worktype = this.page.state.worktype;
    newWods.item.hierarchypath = this.page.state.hierarchypath;
    newWods.item.classstructureid = this.page.state.classstructureid;
    newWods.item.siteid = parentWo.siteid;
    newWods.item.orgid = parentWo.orgid;
    newWods.item.woclass = parentWo.woclass;
    newWods.item.origrecordid = parentWo.wonum;
    newWods.item.origrecordclass = parentWo.woclass;
    newWods.item.origwoid = parentWo.workorderid;
    newWods.item.assetnum = parentWo.assetnum;
    newWods.item.failurecode = parentWo.failurecode;
    newWods.item.owner = parentWo.owner;
    newWods.item.onbehalfof = parentWo.onbehalfof;
    newWods.item.location = parentWo.locationnum;
    newWods.item.locationnum = parentWo.locationnum;
    newWods.item.assetdesc = parentWo.assetdesc;
    newWods.item.assetnumber = parentWo.assetnumber;
    newWods.item.failure = parentWo.failure;
    newWods.item.isquickreported = parentWo.isquickreported;
    if (Device.get().isMaximoMobile) { 
      await WOCreateEditUtils.deleteSpecification(this.app);
    }
    newWods.item.workorderspec = await WOCreateEditUtils.updateSpecificationAttributes(this.app);
    newWods.item.classstructure = {
      hierarchypath: this.page.state.hierarchypath,
      classstructureid: this.page.state.classstructureid,
    };

    newWods.item.phone = this.app.client.userInfo.primaryphone;
    newWods.item.changeby = this.app.client.userInfo.personid;
    newWods.item.reportedby = this.app.client.userInfo.personid;
    newWods.item.changedate = new Date();
    newWods.item.reportdate = new Date();
    newWods.item.status = statusData?.value;
    newWods.item.status_maxvalue = statusData?.maxvalue;
    newWods.item.status_description = statusData?.description;
    newWods.item.statusdate = new Date();
    newWods.item.anywhererefid = new Date().getTime();
    
    /* istanbul ignore else */
    if(this.page.state.isMobile) {
      /* istanbul ignore next */
      if(dsWoedit.item.failurecode) {
        newWods.item.failurecode = dsWoedit.item.failurecode;
      }
      /* istanbul ignore next */
      if(dsWoedit.item.assetlocpriority) {
        newWods.item.assetlocpriority = dsWoedit.item.assetlocpriority;
      }
    }

    let workAsset = parentWo?.assetnum;
    let workLocation = parentWo?.locationnum;

    // istanbul ignore else
    if (!dsWoedit.item.assetnum && !dsWoedit.item.locationnum) {
      newWods.item.assetnum = '';
      newWods.item["location"] = '';
    } else if (dsWoedit.item.assetnum !== workAsset || dsWoedit.item.locationnum !== workLocation) {
      newWods.item.assetnumber = dsWoedit.item.assetnum;
      newWods.item.assetnum = dsWoedit.item.assetnum;
      newWods.item["location"] = dsWoedit.item.locationnum;
      newWods.item.locationnum = dsWoedit.item.locationnum;
      newWods.item["locationdesc"] = dsWoedit.item.locationdesc;
      newWods.item.assetdesc = dsWoedit.item.assetdesc;
    } else {
      newWods.item.assetnum = parentWo.assetnum;
      newWods.item.location = parentWo.locationnum;
      newWods.item.assetdesc = parentWo.assetdesc;
      newWods.item.assetnumber = parentWo.assetnumber;
    }
    
    if (this.page.state.assetLinear) {
      let multiassetlocci = {
        startyoffsetref: dsWoedit.item.startyoffsetref,
        assetnum: parentWo.assetnum,
        endyoffsetref: dsWoedit.item.endyoffsetref ,
        startzoffsetref: dsWoedit.item.startzoffsetref ,
        endzoffsetref: dsWoedit.item.endzoffsetref,
        startoffset: dsWoedit.item.startoffset ,
        startzoffset: dsWoedit.item.startzoffset,
        endzoffset: dsWoedit.item.endzoffset ,
        startyoffset: dsWoedit.item.startyoffset,
        endyoffset: dsWoedit.item.endyoffset,
        endoffset: dsWoedit.item.endoffset,
        startmeasure: dsWoedit.item.startmeasure,
        endmeasure: dsWoedit.item.endmeasure,
        startassetfeatureid: dsWoedit.item.startassetfeatureid,
        endassetfeatureid: dsWoedit.item.endassetfeatureid ,
        startfeaturelabel: dsWoedit.item.startfeaturelabel ,
        endfeaturelabel: dsWoedit.item.endfeaturelabel,
        isprimary: 1,
      };
      /* istanbul ignore next */
      newWods.item['multiassetlocci'] = multiassetlocci;
      if (!Device.get().isMaximoMobile) {
        delete newWods.item['woserviceaddress'];
      }
    } 
    /**
     * Filters out selected items and removes `multiid, href, localref` from each object to make multiassetlocci payload
     */
    else if (this.page.state.hasMultiAssetLocCI) {
      const woMultiAssetLocCIdata = await this.app.findPage("workOrderDetails")?.findDatasource("woMultiAssetLocationds").forceReload();
      const filteredPayload = woMultiAssetLocCIdata
        .filter(({ multiid, anywhererefid }) => !CommonUtil.sharedData.selectedMultiAssets?.includes(multiid || anywhererefid))
        .map(({ anywhererefid, multiid, href, localref, ...rest }) => {
          const updatedPayload = { ...rest };

          return updatedPayload;
        });

      filteredPayload.forEach((item, index) => {
        item.progress = false;
        item.anywhererefid = new Date().getTime() + index;
      })

      CommonUtil.sharedData.selectedMultiAssets = [];
      newWods.item.multiassetlocci = filteredPayload;
    }
    //Setting the interactive to false
    newWods.dataAdapter.options.query.interactive = this.page.state.isMobile ? 0 : 1;

    // istanbul ignore next
    try {
      if ( this.callDefaultSave ) {
        this.page.state.useConfirmDialog = false;
      }
      this.saveDataSuccessful = true;
      newWods.on('save-data-failed', this.onUpdateDataFailed);

      //Do not go for save if there is any client side error
      if (!this.page.state.errorMessage) {      
        let localPayloadfup = {...newWods.item};        
        localPayloadfup["relatedwo"] = [{
          relatedwodesc: parentWo.description,
          relatetypedesc: relateTypeOrgData.description,
          relatedreckey: parentWo.wonum,
          href:parentWo.href
        }];
        
        let optionfup = {
          responseProperties: newWods.baseQuery.select,
          localPayload : localPayloadfup
        };
        
        newWods.__itemChanges = {};
        response = await newWods.save(optionfup);
      } else {
        this.saveDataSuccessful = false;
      }

      if ((response && this.saveDataSuccessful)  || (response.hasNewItems && response.items.length > 0)) {
        this.page.state.useConfirmDialog = false;
        if (relatedListDS?.items?.length === 0) {
          await this.UpdateTheParentWO();
        }
        //adding this condition to disable the chevron button of follow up work order in devices if workorder created in devices
        if (Device.get().isMaximoMobile) { this.page.stateChevronDisable = true;}
        this.app.toast(
          this.app.getLocalizedLabel(
            'relatedwo_created_msg',
            `Follow-up work created`
          ),
          'success'
        );
        
        //Update wo to add fup record
        if (Device.get().isMaximoMobile) { 
          if (!relatedPage.initialized || relatedPage?.findDatasource("woDetailRelatedWorkOrder")?.item
                ?.wonum !== woDetailResource?.item?.wonum) {
            relatedPage.initialize();
            }

          woDetailRelatedWorkOrder = relatedPage.findDatasource("woDetailRelatedWorkOrder");
          await woDetailRelatedWorkOrder.load({noCache: true, itemUrl: this.page.params.href });

          let localPayload = {
            href:parentWo.href,
            relatedwo: [
              {
                relatedwodesc: response.items[0].description,
                relatetypedesc: relateTypeFUPData.description,
                origrecordid: response.items[0].origrecordid,
                anywhererefid: response.items[0].anywhererefid,
              }],
            };
            
            let option = {
              responseProperties: "description,anywhererefid",
              localPayload : localPayload
            };
              
            const dataToUpdate = {description: parentWo.description, href:parentWo.href};		
            const relatedDs = relatedWOds ?? woDetailRelatedWorkOrder ;
            await relatedDs?.update(dataToUpdate, option);

            CommonUtil._resetDataSource(relatedPage.findDatasource("relatedrecwo"));
            await relatedPage.findDatasource("relatedrecwo")?.load();

        }
        this.page.state.saveInProgress = false;  
        if (this.page.params.type !== "multiAssetFollowup") {
          this.app.setCurrentPage({
            name: "relatedWorkOrder",
            resetScroll: true,
            params: {
              itemhref: parentWo.href,
              chevronDisable: this.page.stateChevronDisable,
            },
          });
        } else {
          this.app.setCurrentPage({
            name: "multiAssetLocCi",
            resetScroll: true,
            params: {
              itemhref: parentWo.href,
            },
            pushStack: false,
          });
        } 
      }
      newWods.off('save-data-failed', this.onUpdateDataFailed);
    }
    catch (error) /* istanbul ignore next */ {
      log.t(TAG, error);
      this.app.setCurrentPage({name: 'relatedWorkOrder', resetScroll: true, params: {itemhref: parentWo.href}});
      this.page.state.useConfirmDialog = true;
      this.page.state.saveInProgress = false; 
    } finally {
      this.page.state.worktype = '';
      this.page.state.hierarchypath = '';
      this.page.state.classstructureid = '';
    }
	}

  async setIncomingContext () {
    const context = this.app.state.incomingContext;

    this.page.datasources.dsWoedit.item["assetnum"] = context.assetnum;
    this.app.state.incomingContext = null;

    this.onChangeAsset({
      item: this.page.datasources.dsWoedit.item,
      app: this.app,
    });
  }

  /**
   * Function to set flag for 'save-data-failed' event
   */
   onUpdateDataFailed() {
    this.saveDataSuccessful = false;
    this.page.state.saveInProgress = false; 
  }

  /**
   * Set editorValue on the basis of content
   * @param {*} rich text editor on change event 
   */
  onEditorChange(evt) {
    this.page.state.editorValue = evt.target.content;
  }

  /**
   * Reset editorValue on save perform from rich text editor save button
   */
  onEditorSave() {
    this.page.state.editorValue = null;
  }

  /**
   * Callback from dialog back button
   */
  onCloseRichTextDialog() {
    /* istanbul ignore else  */
    if (
      !!this.page.state?.editorValue?.trim() && 
      this.page.datasources?.dsWoedit?.item?.["description_longdescription"] !== this.page.state.editorValue
    ) {
      this.page.showDialog('saveDiscardDialog');
    }
  }

  /**
   * Set long description value.
   * @param {*} workorder item and datasource as event
   */
  setRichTextValue(evt) {
    evt.datasource.item.description_longdescription = this.page.state.editorValue;
    this.onEditorSave();
  }

  /**
   * Reset editorValue on click discard button
   */
  closeSaveDiscardDialog() {
    this.onEditorSave();
  }
  

  /**
   * set Asset/location value when user confirm Yes
   */
  onUserConfirmationYes() {
    let dsWoedit = this.app.findDatasource("dsWoedit");
    let selectedItem = this.page.state.selectedItem;
    /* istanbul ignore else  */
    if (selectedItem.action === "SETASSET") {
      const isValidGL = WOCreateEditUtils.validateGlAccount(this.app, this.page, dsWoedit, selectedItem.item, "SETASSETGL", true);
      WOCreateEditUtils.setAsset(this.app, this.page, dsWoedit, selectedItem.item, this);
      /* istanbul ignore else  */
      if(isValidGL) {
        this.validAssetValue = selectedItem.item.assetnum;
      }
    } else if (selectedItem.action === "SETLOCATION") {
      const isValidGL = WOCreateEditUtils.validateGlAccount(this.app, this.page, dsWoedit, selectedItem.item, "SETLOCGL", true);
      if (selectedItem.item.asset.length > 1) {
        dsWoedit.item.assetnum = "";
        dsWoedit.item.assetdesc = "";
        dsWoedit.item.asset = [];
      }
      WOCreateEditUtils.setLocation(this.app, this.page, dsWoedit, selectedItem.item, this);
      if(isValidGL) {
        this.validLocationValue = selectedItem.item.location || selectedItem.item.locationnum;
      }
    } else if (
      selectedItem.action === "SETLOCGL" ||
      selectedItem.action === "SETASSETGL"
    ) {
      WOCreateEditUtils.setGLAccount(this.app, this.page, dsWoedit, selectedItem.item, selectedItem.action, this);
      (selectedItem.action === "SETLOCGL") ? this.validLocationValue = selectedItem.item.location || selectedItem.item.locationnum : this.validAssetValue = selectedItem.item.assetnum;
    }
  }

  /**
   * set Asset/location value when user confirm NO
   */
  onUserConfirmationNo() {
    WOCreateEditUtils.onUserConfirmationNo(this.app, this.page, 'dsWoedit');
  }

  /**
   * If callUpdateWorkOrder method was called before and we were waiting for assets / location data
   * then after fetch of location / assets proceed with callUpdateWorkOrder and also
   * set editAssetsLocation and saveInProgress to false
   */
   callUpdateWorkOrder() {
    if(this.page.state.saveInProgress) {
      this.page.state.editAssetsLocation = false;
      this.page.saveInProgress = false;
      this.updateAndSaveWorkOrder(this.page.state.updateWorkorderItem);
    }
  }
  // Assisted by WCA@IBM
  // Latest GenAI contribution: ibm/granite-20b-code-instruct-v2
  /**
   * Opens the classification lookup dialog.
   */

  async openClassification() {
    const woClassDataDS = await this.app.findDatasource('woClassDataDS')
    woClassDataDS.clearState();
    await woClassDataDS?.load({ noCache: true, src: CommonUtil.sharedData.treeData });
    this.app.showDialog("classificationLookup");
  }

  // Assisted by WCA@IBM
  // Latest GenAI contribution: ibm/granite-20b-code-instruct-v2
  /**
   * Choose a classification for the work item being edited.
   * @param {Object} itemSelected - The selected classification item.
   */
  async chooseClassification(itemSelected) {
    this.classificationChanged = true;
    await WOCreateEditUtils.chooseClassification(this.app, this.page, "dsWoedit", itemSelected);
  }

  // Assisted by WCA@IBM
  // Latest GenAI contribution: ibm/granite-20b-code-instruct-v2
  /**
   * Clears the classification information from the work order edit datasource.
   * 
   * @returns {Promise<void>} A promise that resolves when the classification information has been cleared.
   */
  async clearClassification() {
    this.classificationChanged = true;
    await WOCreateEditUtils.clearClassification(this.app, this.page, "dsWoedit");
  }
}

export default PurchaseOrderEditController;
