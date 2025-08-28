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
        responseProperties: 'status,rel.postatus{postatusid, changeby, changedate, status}',
        localPayload: {
          status: this.page.state.selectedStatus,
          status_maxvalue: this.page.state.selectedStatusMaxValue,
          status_description: this.page.state.selectedStatusDescription,
          statusdate: currDate,
        },
        query: {interactive: false, ignorecollectionref:1},
        esigCheck: 0
      };
      try {
        parentPage.state.loadingstatus = true;
        await currPODatasource.invokeAction(action, option);
        await currPODatasource.forceReload();
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

  selectStatus(item) {
    log.t(TAG, 'selectStatus : SelectStatus called .. ' + JSON.stringify(item));
    this.page.state.selectedStatus = item.value;
    this.page.state.selectedStatusMaxValue = item.maxvalue;
    this.page.state.selectedStatusDescription = item.description;
    this.page.parent.state.compDomainStatus = item.value + new Date().getTime();
    let allowedSignatureSystemProp = this.app.state.systemProp && this.app.state.systemProp["maximo.mobile.statusforphysicalsignature"];
   
    if (allowedSignatureSystemProp) {
      let allowedSignature = allowedSignatureSystemProp
        .split(',')
        .map((status) => status.trim());

        this.page.parent.state.enableSignatureButton =
        allowedSignature.length > 0 &&
        allowedSignature.indexOf(item.value) > -1;
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
}

export default ChangeStatusController;
