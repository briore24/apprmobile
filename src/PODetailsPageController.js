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

import { log, Device, ShellCommunicator } from '@maximo/maximo-js-api';
import CommonUtil from './SharedResources/utils/CommonUtil';
const TAG = 'PODetailsPageController';

class PODetailsPageController {
  pageInitialized(page, app) {
    this.app = app;
    this.page = page;
    ShellCommunicator.get().on(
      'TRANSACTION_UNDONE',
      this.handleDeleteTransaction.bind(this)
    );
  }
  constructor() {
    this.onUpdateDataFailed = this.onUpdateDataFailed.bind(this);
    this.saveDataSuccessful = true;
    CommonUtil.sharedData.newPageVisit = true;
  }

  async openWorkLogDrawer(event) {
    await CommonUtil.openWorkLogDrawer(this.app, this.page, event, this.page.findDatasource("poDetailWorkLogDs"), "poDetailWorkLogDrawer");
  }

  openRevisionHistory(item) {
    console.log('this is still under construction!');
  }

  navigateToLine(item) {
    console.log('this is still under construction!');
    /*
    if (item?.ponum) {
      this.app.setCurrentPage({
        name: 'lines', 
        params: { ponum: item.ponum, href: item.href },
      });
      this.page.state.navigateToLinePage = true;
    }
      */
  }

  async pageResumed(page, app) {
    CommonUtil.sharedData.newPageVisit = true;
    page.state.loading = true;
    page.state.historyDisable = false;
    page.state.isMobile = Device.get().isMaximoMobile;
    const poDetailResource = page.datasources['poDetailResource'];
	
	await poDetailResource?.load({ noCache: true, itemUrl: page.params.href });

    const device = Device.get();

    page.state.loadedLog = true;
    // page.state.lineLoading = true;
    // offline mode sync
    if (this.page.state.disConnected && this.app.state.networkConnected && this.app.state.refreshOnSubsequentLogin !== false) {
      await poDetailResource?.load({
        noCache: true,
        forceSync: true,
      });
      this.page.state.disConnected = false;
    } 

    page.state.loading = false;
    const index = 0;

    CommonUtil.sharedData.clickedPo = page.params.ponum;
    if (app.state.incomingContext && poDetailResource.items.length === 0) {
      const loadParams = {
        noCache: true,
        itemUrl: page.params.href,
      }
      if (this.app.state.refreshOnSubsequentLogin !== false) {
        loadParams['forceSync'] = true;
      }
      await poDetailResource.load(loadParams);
      if (poDetailResource.items.length === 0) {
        let errorMessage =
          'This record is not on your device. Try again or wait until you are online.';
        page.error(
          this.app.getLocalizedLabel('record_not_on_device', errorMessage)
        );
      }
    }

    let ponum = this.page.datasources['poDetailResource']?.item.ponum;
    page.state.editDetails = !['CAN', 'CLOSE'].includes(this.page.datasources['poDetailResource']?.item?.status_maxvalue);
    page.state.editPo = !['CAN'].includes(this.page.datasources['poDetailResource']?.item?.status_maxvalue);

    if (!app.state.doclinksCountData) {
      app.state.doclinksCountData = {};
    }
    if (!app.state.doclinksCountData[ponum]) {
      app.state.doclinksCountData[ponum] = device.isMaximoMobile
        ? poDetailResource.item?.doclinks?.member?.length
        : poDetailResource?.item.doclinkscount;
    }

    //Reload the attachment list
    if (device.isMaximoMobile) {
      let poDetailResource = page.datasources['poDetailResource'];
      await poDetailResource.forceReload();

      poDetailResource.item.relatedrecordcount = poDetailResource.item.relatedrecordcount;

      app.state.doclinksCountData[ponum] = poDetailResource.item.doclinks ?
        poDetailResource.item.doclinks?.member?.length
        : poDetailResource?.item.doclinkscount;
    }
    app.state.doclinksCount = app.state.doclinksCountData[ponum]
      ? app.state.doclinksCountData[ponum]
      : undefined;

    page.state.loadedLog = false;
    // Load rowsSelected prop from sessionStorage
    let selectedDisplayOption = this.app.client?.getUserProperty('displayOption');
    if (selectedDisplayOption) {
      page.state.rowsSelected = selectedDisplayOption.rowsSelected;
    }

    this.updateSignaturePrompt();
    // page.state.lineLoading = false;
    this.app.state.purchaseOrderStatus = poDetailResource?.item?.status;
  }

// update signature prompt based on system property
  updateSignaturePrompt() {
    let allowedSignatureSystemProp = this.app.state?.systemProp?.["maximo.mobile.statusforphysicalsignature"];
    if (allowedSignatureSystemProp) {
      let allowedSignature = allowedSignatureSystemProp
        .split(",")
        .map((status) => status.trim());
      let selected_status_is_inprg = allowedSignature.indexOf("INPRG") > -1 ? "INPRG" : "APPR";
      this.page.state.enableSignatureButton =
        allowedSignature.length > 0 &&
        allowedSignature.indexOf(selected_status_is_inprg) > -1;
      this.page.state.compDomainStatus = selected_status_is_inprg + new Date().getTime();
    }
  }

  async pagePaused() {
	this.page.findDialog('openChangeStatusDialog')?.closeDialog();
    this.page.findDialog('poDetailWorkLogDrawer')?.closeDialog();
	
    this.app?.findPage("approvals")?.findDialog('poStatusChangeDialog')?.closeDialog();
    this.app?.findPage("approvals")?.findDialog('rejectPO')?.closeDialog();
  }
  /**
  * Validate before closing sliding drawer.
  * @param {validateEvent} validateEvent
  */
  workLogValidate(validateEvent) {
    if (this.page.state.isWorkLogEdit) {
      validateEvent.failed = true;
      this.page.showDialog('saveDiscardWorkLogDetail');
    } else {
      validateEvent.failed = false;
    }
  }
  // calls when save button chosen on save discard prompt
  saveWorkLogSaveDiscard() {
    // Save Entered Data to chat Log
    if (!this.page.state.workLogData?.sendDisable) {
      this.saveWorkLog(this.page.state.workLogData);
    }
  }

  // calls when discard button chosen on save discard prompt
  closeWorkLogSaveDiscard() { 
	this.page.findDialog('poDetailWorkLogDrawer')?.closeDialog(); 
  }
  /**
  * This method is called when any changes done on work log screen and return value as Object with all field value.
  * @param {value} value
  */
  watchChatLogChanges(value) {
    // Clear Debounce Timeout
    clearTimeout(this.page.state.workLogChangeTimeout);
    // Set Debounce Timeout
    this.page.state.workLogChangeTimeout = setTimeout(() => {
      if (value?.summary || value?.longDescription || value?.logType?.value !== this.page.state.initialDefaultLogType?.replace(/!/g, "") || value?.visibility) {
        this.page.state.isWorkLogEdit = true;
        this.page.state.workLogData = value;
        // Clear Debounce Timeout
        clearTimeout(this.page.state.workLogChangeTimeout);
      } else {
        this.page.state.isWorkLogEdit = false;
        this.page.state.workLogData = null;
        // Clear Debounce Timeout
        clearTimeout(this.page.state.workLogChangeTimeout);
      }
    }, 500);
  }

  async saveWorkLog(value, directSave = false) {
    await CommonUtil.saveWorkLog(this.app, this.page, this.page.findDatasource('poDetailWorkLogDs'), 'poDetailWorkLogDrawer', value);
  }

  /*
   * Method to open the Change Status slider-drawer.
   * @param event should contain
   * item - The Work Order selected.
   * datasource - The Datasource for synonymdomain.
   */
  async openPoDtlChangeStatusDialog(event) {
    log.t(
      TAG,
      'openChangeStatusDialog : event --> ' +
      event.datasource +
      ' ponum --> ' +
      event.item.ponum
    );

    let schedulePage = this.app.pages.find((element) => {
      // istanbul ignore else
      if (element.name === 'approval') {
        return element;
      } else {
        return '';
      }
    });
    //istanbul ignore else
    if (schedulePage && schedulePage !== '') {
      schedulePage.callController('openChangeStatusDialog', event);
      this.page.state.navigateToSchedulePage = true;
    }
  }

  /**
   * Opens the Reject Work Order dialog
   * @param {Event} event - The event that triggered the action
   */
  openPoDtlRejectPoDialog(event) {
    log.t(
      TAG,
      'openRejectDialog : event --> ' +
      event.datasource +
      ' ponum --> ' +
      event.item.ponum
    );

    let schedulePage = this.app.pages.find((element) => {
      // istanbul ignore else
      if (element.name === 'approval') {
        return element;
      } else {
        return '';
      }
    });
    //istanbul ignore else
    if (schedulePage && schedulePage !== '') {
      schedulePage.callController('rejectPO', event);
      this.page.state.navigateToSchedulePage = true;
    }
  }

  //istanbul ignore next
  async approvePO(event) {
    log.t(TAG,
      'approvePO : event --> ' +
      event.datasource +
      ' ponum --> ' +
      event.item.ponum
    );

    // check user limits
	
	// check contract reference
	
	// set status to APPR & return to approvals list page
  }


async rejectPO(event) {
	log.t(TAG,
	'rejectPO : event --> ' + 
	event.datasource + 
	'ponum --> ' +
	event.item.ponum
	);
	
	// open rejection sliding drawer
	
	// check for comment save
	
	// change status & return to approvals list page
	
	
}


  /**
* This method invokes complete work API once image is uploaded.
*/
  //istanbul ignore next
  async onUpload(manual = true) {
    if (manual && CommonUtil.sharedData.newPageVisit) {
      CommonUtil.sharedData.newPageVisit = false;
      return;
    }
    //During Start work it will not wait for the API response
    let poDetailResourceDS = this.app.findDatasource("poDetailResource");
    //istanbul ignore else
    if (poDetailResourceDS) {
      this.app.state.doclinksCountData[poDetailResourceDS.item.ponum] = Device.get().isMaximoMobile ? poDetailResourceDS.item?.doclinks?.member?.length : poDetailResourceDS.item?.doclinkscount;
      this.app.state.doclinksCount = this.app.state.doclinksCountData[poDetailResourceDS.item.ponum];
    }
    const po = {
      item: poDetailResourceDS.item,
      datasource: poDetailResourceDS,
    }
    this.updateSignaturePrompt();
  }
  showAttachmentPage(event) {
    this.app.state.poStatus = event.item.status_maxvalue;
    this.app.setCurrentPage({
      name: 'attachments',
      params: { itemhref: event.item.href },
    });
  }

  async setLocaleTime(date_value) {
    const poDetailResource = this.page.datasources['poDetailResource'];

    const localeString = new Date(
      `${poDetailResource.item[date_value]}`
    ).toString();

    const new_date_value = this.app.dataFormatter.convertDatetoISO(
      localeString
    );

    poDetailResource.item[date_value] = new_date_value;
  }

  async setPODetailsLogType(event) { this.page.state.defaultLogType = event.value; }

  onUpdateDataFailed() { this.saveDataSuccessful = false; }

  _closeAllDialogs(page) {
    if (page?.dialogs?.length) {
      page.dialogs.map((dialog) => page.findDialog(dialog.name).closeDialog());
    }
  }

  async handleDeleteTransaction(event) {
    if (
      event.app === this.app.name &&
      (this.app.currentPage.name === this.page.name ||
        this.app.lastPage.name === this.page.name)
    ) {
      const poDetailResource = this.page.datasources['poDetailResource'];
      //See of the detail page's record is the same one that had the transaction deleted.
      /* istanbul ignore else */
      if (poDetailResource?.currentItem?.href === event.href) {
        let records = await poDetailResource.load({
          noCache: true,
          itemUrl: poDetailResource.currentItem.href,
        });

        //If no record was returned then the work order was removed so redirect the user to the schedule page.
        /* istanbul ignore else */
        if (!records || records.length === 0) {
          this._closeAllDialogs(this.page);
          const schPage = 'approvals';
          this.app.setCurrentPage({ name: schPage, resetScroll: true });
        }
      } else if (
        this.app.currentPage.name !== this.page.name &&
        this.app.currentPage?.getMainDatasource()?.currentItem?.href === event.href
      ) {
        await this.app.currentPage
          .getMainDatasource()
          .load({ noCache: true, itemUrl: event.href });
      }

      let schedPage = this.app.findPage("approvals");
      // istanbul ignore if
      if (schedPage) {
        await this.app.findDatasource(schedPage.state.selectedDS).forceReload();
      }
    }
  }

}

export default PODetailsPageController;
