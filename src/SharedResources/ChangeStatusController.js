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

import {log, Device} from '@maximo/maximo-js-api';
import commonUtil from './Technician/utils/CommonUtil';

const TAG = 'ChangeStatusController';

class ChangeStatusController {

  dialogInitialized(page) {
    this.page = page;
    this.app = this.page.getApplication();
    this.page.state.selectedStatus = "";
    this.page.state.selectedStatusMaxValue = "";
    this.page.state.statusMemo = "";
  }

  dialogOpened() {
    this.app.callController('resetSkipState');
    //this.page.parent.state.disableDoneButton = true;
    this.page.state.selectedStatus = "";
    this.page.state.selectedStatusMaxValue = "";
    this.page.state.statusMemo = "";
  }

  async openSignatureDialog(purchaseorder) {
    const podetails = this.app.findDatasource('podetails');
    await podetails.load({ noCache: true, itemUrl: purchaseorder.href });
    this.page.state.canloadpodetails = true;
    this.app.userInteractionManager.openSignature(
      imageData => {
        log.t(TAG, 'base64 image' + imageData);
      }
      ,
      {
        imageFormat: null,
        primaryIcon: null,
        secondaryIcon: null,
        heading: null,
        primaryButtonSaveText: null,
        secondaryButtonDiscardText: null,
        signatureLabel: null,
        filename: this.page.parent.state.compDomainStatus,
        datasource: this.app.findDatasource('signatureAttachment'),
        onUpload: this.onSignatureUpload.bind(this)
      })
  }

  /*
   * Method to change the status. This is called from from the following pages:
   *    - Schedule Page
   *    - Work Detail Page
   */
  async changeStatus() {
    let parentPage = this.page.parent;
    const poDetails = parentPage.state.poItem;
    if (this.page.state.selectedStatusMaxValue === 'COMP' && this.app.name !== "apprmobile") {
      parentPage.state.loadingstatus = true;
      const datasheetResult = await commonUtil.validateDataSheet(this.app, parentPage, poDetails, calMethodConfig);
      parentPage.state.loadingstatus = false;
      if (!datasheetResult) {
        return;
      }
    }
    let selectedDSName = parentPage.state.referenceDS;
    let purchaseorder = parentPage.state.poItem;
    let selectedStatus = this.page.state.selectedStatus;
    let referencePage;
    let poDetailDs = this.app.findDatasource("poDetailds");
    // Signature Check
    if (parentPage.state.enableSignatureButton && !this.app.state.skipSignature) {
      await this.openSignatureDialog(purchaseorder);
      return;
    }
    this.app.state.skipSignature = false;

    if (parentPage && (parentPage.state.referencePage === 'schedule' || parentPage.state.referencePage === 'approvals')) {
      referencePage = parentPage;
    } else {
      referencePage = this.app.pages.find((element) => {
        // istanbul ignore else
        if (element.name === parentPage.state.referencePage) {
          return element;
        } else {
          return '';
        }
      });
    }
    log.t(TAG, "changeStatus : selectedStatus --> " + selectedStatus);
    let currDate = new Date();
    let dataFormatter = this.app.dataFormatter;
    if (selectedStatus) {
      log.t(
        TAG,
        "changeStatus : Page Item = " +
        parentPage.state.poItem.ponum +
        " selectedDSName --> " +
        selectedDSName +
        " Parent Page poItem --> " +
        parentPage.state.poItem.ponum
      );

      currDate = dataFormatter.convertDatetoISO(currDate);

      log.t(TAG, "changeStatus : currDate Formatted --> " + currDate);

      let currPODatasource = referencePage.datasources[selectedDSName];

      
      let action = 'changeStatus';
      let option = {
        record: purchaseorder,
        parameters: {
          status: this.page.state.selectedStatus,
          date: currDate,
          memo: this.page.state.statusMemo
        },
        headers: {
          'x-method-override': 'PATCH'
        },
        responseProperties: 'status,rel.postatus{href, postatusid, changeby, changedate, status}',
        localPayload: {
          status: this.page.state.selectedStatus,
          status_maxvalue: this.page.state.selectedStatusMaxValue,
          status_description: this.page.state.selectedStatusDescription,
          statusdate: currDate,
        },
        query: {interactive: false, ignorecollectionref:1},
        esigCheck: 0
      };
      if(this.checkEsigRequired()) {
        option.esigCheck = 1;
      }
      // istanbul ignore else
      if (purchaseorder.woactivity && Device.get().isMaximoMobile) {
        let poLines = await this.app.controllers[0].getWoActivity(this.page, this.app, purchaseorder);
        // istanbul ignore else
        if (poLines.length){
          option.localPayload["woactivity"] = poLines;
        }
      }
      try {
        parentPage.state.loadingstatus = true;
        await currPODatasource.invokeAction(action, option);
        referencePage.state.notLoadWoDetailChilds = true;
        await currPODatasource.forceReload();
        referencePage.state.notLoadWoDetailChilds = false;
        parentPage.findDialog(parentPage.state.statusDialog).closeDialog();
        if (this.page.state.selectedStatusMaxValue === 'COMP' || this.page.state.selectedStatusMaxValue === 'CAN' || this.page.state.selectedStatusMaxValue === 'CLOSE') {
          const schPage = (this.app.findPage("schedule")) ? 'schedule' : 'approvals';
          this.app.setCurrentPage({name: schPage});
        }
      } finally {
        parentPage.state.loadingstatus = false;
      }
    }

  }

  /**
   * In system properties we would get list of flag on which we have to ask for eSigCheck
   * if current status matches in list we would pass esigCheck 1 and on based of it graphite component
   * will handle to show prompt of esig
   * @returns 1 or 0 (boolean numeric value)
   */
  checkEsigRequired() {
    const esigCheck = this.app.state.systemProp && this.app.state.systemProp["maximo.mobile.wostatusforesig"];
    const allowedSignature = esigCheck? esigCheck
      .split(',')
      .map((status) => status.trim()): [];
      const addEsig = allowedSignature.length > 0 &&
      allowedSignature.indexOf(this.page.state.selectedStatus) > -1;
    return (addEsig) ? 1 : 0;
  }


  selectStatus(item) {
    log.t(TAG, 'selectStatus : SelectStatus called .. ' + JSON.stringify(item));
    this.page.state.selectedStatus = item.value;
    // this.page.state.disableDoneButton = false;
    // this.page.parent.state.disableDoneButton = false;
    this.page.state.selectedStatusMaxValue = item.maxvalue;
    this.page.state.selectedStatusDescription = item.description;
    this.page.parent.state.compDomainStatus = item.value + new Date().getTime();
    let purchaseorder = this.page.parent.state.poItem;
    let allowedSignatureSystemProp = this.app.state.systemProp && this.app.state.systemProp["maximo.mobile.statusforphysicalsignature"];
   
    // istanbul ignore else
    if (allowedSignatureSystemProp) {
      let allowedSignature = allowedSignatureSystemProp
        .split(',')
        .map((status) => status.trim());

        this.page.parent.state.enableSignatureButton =
        allowedSignature.length > 0 &&
        allowedSignature.indexOf(item.value) > -1;
    }
    // istanbul ignore next
    if((item.maxvalue === 'COMP' || item.maxvalue === 'CLOSE') && purchaseorder.assetisrunning === false && purchaseorder.assetnumber && item._selected) {
      this.app.callController('checkDownPrompt',{purchaseorder:purchaseorder,page: this.page.parent});
    }
    if (this.page.parent.datasources.dsstatusDomainList && this.page.parent.datasources.dsstatusDomainList.state.selection.count > 0) {
      this.page.parent.state.disableDoneButton = false;
    } else {
      this.page.parent.state.disableDoneButton = true;
    }
  }

  setStatusMemo(event) {
    this.page.state.statusMemo = event.currentTarget.value;
    log.t(TAG, 'setStatusMemo : statusMemo --> ' + this.page.state.statusMemo);
  }

  /**
   * This method invokes change status API once signature is uploaded.
   */
  async onSignatureUpload() {
    this.app.state.skipSignature = true;
    await this.changeStatus();
    let poDetailResourceDS = this.app.findDatasource('poDetailResource');
    
    //istanbul ignore else
    if (poDetailResourceDS) {
      await poDetailResourceDS.forceReload();  
      this.app.state.doclinksCountData[poDetailResourceDS.item.ponum] = Device.get().isMaximoMobile ? poDetailResourceDS.item.doclinks.member.length : poDetailResourceDS.item.doclinkscount;
      this.app.state.doclinksCount = this.app.state.doclinksCountData[poDetailResourceDS.item.ponum];
    }
  }

  /**
   * This method invokes on task status selection.
   */
  selectTaskStatus(item) {
    log.t(TAG, 'selectStatus : SelectStatus called .. ' + JSON.stringify(item));
    this.page.parent.state.selectedTaskStatus = item;
    if (this.page.parent.datasources.taskstatusDomainList && this.page.parent.datasources.taskstatusDomainList.state.selection.count > 0) {
      this.page.parent.state.disableDoneButton = false;
    } else {
      this.page.parent.state.disableDoneButton = true;
    }
  }
}

export default ChangeStatusController;
