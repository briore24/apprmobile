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
import POCreateEditUtils from './Technician/utils/POCreateEditUtils';
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
  }

  pageInitialized(page, app) {
    log.t(TAG, "Purchase order edit Page Initialized");
    this.app = app;
    this.page = page;
    this.page.state.isLocationAssetFocus = false;
  }

  async pageResumed(){
    this.app.state.pageLoading = true;
    this.page.state.useConfirmDialog = true;
    this.page.state.saveDisable  = false;
    this.page.state.isMobile = Device.get().isMaximoMobile;
    const savedPO=sessionStorage.getItem('poedit_po');
    /* istanbul ignore if  */
    if(this.page?.params?.po){
      await this.loadRecord(this.page?.params?.po);
    }else if(savedPO){
      await this.loadRecord(JSON.parse(savedPO));
    }
    this.app.state.pageLoading = false;
  }

  inComingContext(){
    return this.app.state.incomingContext?.page === "poDetails";
  }

  async loadRecord(po) {
    sessionStorage.removeItem("poedit_po");
    // Reset Error State
    this.page.state.readOnlyState = false;
    this.page.state.errorMessage = "";

    this.page.state.pageTitle = this.app.getLocalizedLabel(
      "edit_purchase_order_label",
      "Edit purchase order"
    );
    this.page.state.title = this.page.state.pageTitle;
    let dsPoedit = this.page.datasources["dsPoedit"];
    let poEditResource = this.app.findDatasource("poDetailResource");
    if (Device.get().isMaximoMobile) {
      await poEditResource.forceReload();
    }
    let schema = poEditResource.getSchema();
    if ( this.app.state.incomingContext?.page === "poDetails") {
      schema=JSON.parse(sessionStorage.getItem("poDetailResource_schema"));
      sessionStorage.removeItem('poDetailResource_schema');
      
      dsPoEditSetting.setSchema(JSON.parse(sessionStorage.getItem("dsPoEditSetting_schema")));
      sessionStorage.removeItem('dsPoEditSetting_schema');
    }
    if (this.page.params.followup) {
      this.page.state.useConfirmDialog = true;
    }
    dsPoedit.setSchema(schema);

    let poData = [];
    /* istanbul ignore if  */
    if (po) {
      if (!this.page.params.followup) {
        this.page.state.pageTitle = this.app.callController('updatePageTitle', {page: this.page, label: 'edit_purchase_order', labelValue: 'Edit purchase order'});
      }
      let attributes= dsPoedit.baseQuery.select.split(',');
      let obj={};
      attributes.forEach((element) => {
        if (element === 'potype') { 
          po[element] = po[element] || '';
          this.page.state.potype = po[element];
        }

        obj[element] = po[element];

         });

      poData.push(obj);
      }
    this.page.state.poData = poData;
    dsPoedit.setSchema(this.page.params.poSchema);
    await dsPoedit.load({ src: poData, noCache: true });
    this.readOnlyFields(dsPoedit);

    if (this.inComingContext()) {
      this.setIncomingContext();
    }
    this.app.state.pageLoading = false;
  }

  /**
   * Function to update work order
   * @param {event} event 
   */
  async updateAndSavePurchaseOrder(event) {
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

    let dsPoedit = event.page.datasources["dsPoedit"];
    if (event.page.params.followup) {
      await this.createFollowupWo(event);
    } else {
      await this.preparePayload(dsPoedit);
    }
    this.page.state.saveInProgress = false;
  }

  /**
   * Prepare the payload
   *  Reflect.deleteProperty:-  It deletes a property from an object. It same as delete operator
   * */
     async preparePayload(dsPoedit){
      let po = dsPoedit.item ? { ...dsPoedit.item } : {};
      let dataToUpdate = { ...po };
    
      Object.keys(dataToUpdate).forEach(function (key) {
        if (
          dsPoedit?.schema?.required?.includes(key) &&
          dataToUpdate[key] === undefined
        ) {
          delete dataToUpdate[key];
        }
      });
      delete dataToUpdate.poid;
      delete dataToUpdate.ponum;

      let poEditResource = this.app.findDatasource("poDetailResource");
      let parentPo = poEditResource.item;
      //Setting the interactive to false on the basis isMobile
      poEditResource.dataAdapter.options.query.interactive = this.page.state.isMobile ? 0 : 1;
      // istanbul ignore next
      if (dataToUpdate) {
        let responseProps = "";
        if (dsPoedit.baseQuery) {
          attributes = dsPoedit.baseQuery.select.split(",");
          responseProps = dsPoedit.baseQuery.select;
        }
        let localPayload = {};
        let option = {
          responseProperties: responseProps
        };

        option.localPayload = localPayload;
        if( this.callDefaultSave ) {
          this.page.state.useConfirmDialog = false;
        }
        this.saveDataSuccessful = true;
        poEditResource.on('update-data-failed', this.onUpdateDataFailed);

        //Do not go for update if there is any client side error
        if (!this.page.state.errorMessage) {
         await poEditResource.update(dataToUpdate, option);
         /* istanbul ignore next */
          window.setTimeout(() => {
          this.page.state.useConfirmDialog = false;
          this.navigateDtlsPage(parentPo);
        }, 50);
        } else {
          this.saveDataSuccessful = false;
        }
        
        poEditResource.off('update-data-failed', this.onUpdateDataFailed);
      }
    }

  async navigateDtlsPage(parentPo) {
    this.app.setCurrentPage({
      name: "poDetails",
      resetScroll: true,
      params: {
        ponum: parentPo.ponum,
        siteid: parentPo.siteid,
        prevPage: "editpo"
      }
    });
  }
  
  async onCustomSaveTransition() {
    this.callDefaultSave = false;
    await this.updateAndSavePurchaseOrder({page: this.page});    
    return {saveDataSuccessful: this.saveDataSuccessful, callDefaultSave: this.callDefaultSave};
  } 

  /**
  * Function to validate if field is ui required and not has value on attribute
  * @param attributeName attribute name
  * @param attributeValue attribute value
  * @returns  true or false
  */
 uiRequired(attributeName, attributeValue){
  return POCreateEditUtils.uiRequired(this.page, 'dsPoedit', attributeName, attributeValue);
 }

validateAndHandleWarnings = (fieldName, fieldValue, arrayListFieldsWithError) => {
  if (this.uiRequired(fieldName, fieldValue)) {
      arrayListFieldsWithError.push({ "attributename": fieldName, "error": true });
      if (fieldName !== 'potype') {
        this.showPOWarnings(fieldName, "");
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
   * Function to validate po fields
   */
  validateFields() {
    if (this.page.datasources && this.page.datasources?.dsPoedit?.items[0]) {
      let po = this.page.datasources.dsPoedit.items[0];
      let arrayListFieldsWithError = [];
      let errorMessage = "";
      let errorField = "";
      let description = this.getTrimmedDescription(po);

      /* DT221877 - MASISMIG-42208 */
      this.validateAndHandleWarnings(
        "description",
        description,
        arrayListFieldsWithError
      );
      this.validateAndHandleWarnings(
        "potype",
        po.potype,
        arrayListFieldsWithError
      );
      const isPriorityOutOfRange =
        po.priority < this.page.state.minPriority ||
        po.priority > this.page.state.maxPriority;
      const isPriorityInvalid =
        po.priority &&
        (isPriorityOutOfRange ||
          this.uiRequired("priority", po.priority));

      if (isPriorityInvalid) {
        if (isPriorityOutOfRange) {
          errorMessage = this.app.getLocalizedLabel(
            "priority_error_msg",
            `Priority ${po.popriority} is not a valid priority value between ${this.page.state.minPriority} and ${this.page.state.maxPriority}`,
            [
              po.popriority,
              this.page.state.minPriority,
              this.page.state.maxPriority,
            ]
          );
          errorField = "popriority";
          this.showPOWarnings(errorField, errorMessage);
          this.page.state.errorMessage = errorMessage;
        }
        this.updateFieldError("popriority", true, arrayListFieldsWithError);
      } else {
        this.clearWarnings("popriority");
        this.updateFieldError("popriority", false, arrayListFieldsWithError);
      }

      this.page.state.readOnlyState = !!arrayListFieldsWithError.find(
        (data) => data.error === true
      );
      POCreateEditUtils.saveDisable(this.page);
      return errorMessage;
    }
  }

  getTrimmedDescription(po){
   return po?.description !== undefined ? po?.description.trim() : po?.description;
  }

  /**
   * Function to set field warnings
   */
  showPOWarnings(field, message) {
    POCreateEditUtils.showPOWarnings(this.page, "dsPoedit", field, message);
  }

  /**
   * Function to clear field warnings
   */
  clearWarnings(field) {
    POCreateEditUtils.clearWarnings(this.page, "dsPoedit", field);
  }

  // istanbul ignore next
  async UpdateTheParentWO() {
    const podetailDs = this.app.findDatasource("poDetailResource");
    let option = {
      responseProperties: podetailDs.baseQuery.select,
    };
    let dataToUpdate = { hasfollowupwork: true };
    podetailDs.on("update-data-failed", this.onUpdateDataFailed);
    await podetailDs.update(dataToUpdate, option);

    await podetailDs.load({ noCache: true }, true);
  }

  /**
   * @description - This function will check if all the required fields are filled before saving the data
   * @param {object} dsPoedit - Data set work on edit
   */
  readOnlyFields(dsPoedit) {
    // istanbul ignore else
    if (dsPoedit?.item) {
      const items = dsPoedit?.item ? Object.keys(dsPoedit.item) : '';
      const requireValues =  dsPoedit?.uiRequired?.undefined ?? '';
      //istanbul ignore next
      this.page.state.readOnlyState =
        requireValues &&
        !requireValues.every((r) =>
          items?.includes(r) && dsPoedit.item[r] !== ""
        );
      dsPoedit.state.canSave = false;
    }
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
      this.page.datasources?.dsPoedit?.item?.["description_longdescription"] !== this.page.state.editorValue
    ) {
      this.page.showDialog('saveDiscardDialog');
    }
  }

  /**
   * Set long description value.
   * @param {*} po item and datasource as event
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
   * If callUpdatePurchaseOrder method was called before and we were waiting for assets / location data
   * then after fetch of location / assets proceed with callUpdatePurchaseOrder and also
   * set editAssetsLocation and saveInProgress to false
   */
   callUpdatePurchaseOrder() {
    if(this.page.state.saveInProgress) {
      this.page.saveInProgress = false;
      this.updateAndSavePurchaseOrder(this.page.state.updatePurchaseorderItem);
    }
  }
}

export default PurchaseOrderEditController;
