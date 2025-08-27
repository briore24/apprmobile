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
import CommonUtil from './Technician/utils/CommonUtil';
const TAG = 'PurchaseOrderDetailsController';

class PurchaseOrderDetailsController {
  pageInitialized(page, app) {
    this.app = app;
    this.page = page;
    ShellCommunicator.get().on(
      'TRANSACTION_UNDONE',
      this.handleDeleteTransaction.bind(this)
    );
  }

  /**
   * Method implemented in controller itself.
   */
  constructor() {
    this.onUpdateDataFailed = this.onUpdateDataFailed.bind(this);
    this.saveDataSuccessful = true;
    CommonUtil.sharedData.newPageVisit = true;
  }


  /**
   * Function to open a sliding-drawer dialog to show Purchase Log for the Work Order with Long Description in Expanded View
   * @param event - should contain
   * event - event containing information about current item.
   * datasource - The Synonymdata Datasource to filter logType
   * item - The Work ORder Selected
   */
  async openWorkLogDrawer(event) {
    this.page.state.editPo = !['CAN'].includes(event?.item?.status_maxvalue);
    await CommonUtil.openWorkLogDrawer(this.app, this.page, event, this.page.datasources["poDetailsWorklogDs"], "poWorkLogDrawer");
  }

  navigateToLine(item) {
    if (item?.ponum) {
      this.app.setCurrentPage({
        name: 'lines', 
        params: { ponum: item.ponum, href: item.href },
      });
      this.page.state.navigateToLinePage = true;
    }
  }
  async pageResumed(page, app) {
    CommonUtil.sharedData.newPageVisit = true;
    page.state.editPo = page.state.editDetails = false;
    page.state.loading = true;
    page.state.followupCount = '';
    page.state.historyDisable = false;
    page.state.isMobile = Device.get().isMaximoMobile;
    const poDetailResource = page.datasources['poDetailResource'];
    await poDetailResource?.load({ noCache: true, itemUrl: page.params.href });
    this.page.state.canloadpoDetailResource = true;

    const device = Device.get();
    if (page.params.firstLogin) app.state.highlightStop = false;

    page.state.loadedLog = true;
    page.state.lineLoading = true;
    page.params.href = page.params.href || page.params.itemhref;
    app.datasources['poLineDetailds']?.resetState();

    // offline mode sync
    if (this.page.state.disConnected && this.app.state.networkConnected && this.app.state.refreshOnSubsequentLogin !== false) {
      await poDetailResource?.load({
        noCache: true,
        forceSync: true,
        itemUrl: page.params.href,
      });
      this.page.state.disConnected = false;
    } 

    let poDetailds = app.datasources['poDetailds'];
    await poDetailds?.load({ noCache: true, itemUrl: page.params.href });
    this.app.state.canLoadPoDetailDS = true;

    const poLineDetailds = app.datasources["poLineDetailds"];

    if (poLineDetailds) {
      CommonUtil._resetDataSource(poLineDetailds);
      await poLineDetailds?.load({ noCache: true });
    }
    page.state.loading = false;
    const rejectLabel = app.getLocalizedLabel('rejected', 'Rejected').toUpperCase();
    const index = (poDetailResource?.item?.assignment?.length > 0) ? poDetailResource?.item?.assignment?.findIndex(assignment => assignment?.laborcode === this.app.client?.userInfo?.labor?.laborcode) : 0;
    const tempRecord = poDetailResource?.item?.assignment?.[index]?.status?.toUpperCase() === rejectLabel;
    app.state.isRejected = tempRecord?.[index]?.status?.toUpperCase() === rejectLabel;

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
    this.app.state.lineCountData = this.app.state.lineCountData
      ? this.app.state.lineCountData
      : {};
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

      poDetailResource.item.relatedrecordcount =
        poDetailResource.item.relatedwo?.length || poDetailResource.item.relatedrecordcount;

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
    page.state.lineLoading = false;
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
    this.page.state.isSafetyPlanReviewed = false;
    this.page.findDialog('poWorkLogDrawer')?.closeDialog();
    this.page.findDialog('openChangeStatusDialog')?.closeDialog();
    this.app?.findPage("schedule")?.findDialog('poStatusChangeDialog')?.closeDialog();
    this.app?.findPage("schedule")?.findDialog('rejectPO')?.closeDialog();
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

  /**
  * This method calls when click save button on save discard prompt.
  */
  saveWorkLogSaveDiscard() {
    // Save Entered Data to chat Log
    if (!this.page.state.workLogData?.sendDisable) {
      this.saveWorkLog(this.page.state.workLogData);
    }
  }

  /**
  * This method calls when click discard button on save discard prompt.
  */
  closeWorkLogSaveDiscard() {
    // Close Work Log Drawer
    this.page.findDialog('poWorkLogDrawer')?.closeDialog();
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

  /**
   * Computes the user name based on the provided item.
   * @param {Object} item The item object containing displayname or personid.
   * @returns {string} The computed user name.
   */
  computedUserName(item) {
    return item?.displayname || item?.personid
  }

  /*
   * Method to add new work log
   */
  async saveWorkLog(value, directSave = false) {
    let longDescription = value.longDescription;
    let summary = value.summary;
    let longType = value.logType?.value ? value.logType.value : this.page.state.defaultLogType;
    let poDetailsWorkLogDs = this.page.datasources['poDetailsWorkLogDs'];

    let workLog = {
      createby: this.app.client.userInfo.personid,
      createdate: new Date(),
      logtype: longType,
      description: summary,
      anywhererefid: new Date().getTime(),
      description_longdescription: longDescription,
      clientviewable: value.visibility
    };

    let option = {
      responseProperties:
        'anywhererefid,createdate,description,description_longdescription,person.displayname--displayname,createby--personid,logtype',
      localPayload: {
        createby:
          this.app.client.userInfo.displayName ||
          this.app.client.userInfo.personid,
        personid:
          this.app.client.userInfo.displayName ||
          this.app.client.userInfo.personid,
        createdate: new Date(),
        description: summary,
        logtype: longType,
        anywhererefid: workLog.anywhererefid,
        description_longdescription: longDescription,
      },
    };
    let response;
    // istanbul ignore if
    if (directSave) {
      poDetailsWorkLogDs.on('update-data-failed', this.onUpdateDataFailed);
      response = await poDetailsWorkLogDs.update(workLog, option);

      // istanbul ignore if
      if (response) {
        poDetailsWorkLogDs.off('update-data-failed', this.onUpdateDataFailed);
      }

      return;
    }
    try {
      this.app.userInteractionManager.drawerBusy(true);
      this.page.state.chatLogLoading = true;
      this.saveDataSuccessful = true;

      poDetailsWorkLogDs.on('update-data-failed', this.onUpdateDataFailed);
      response = await poDetailsWorkLogDs.update(workLog, option);
      // istanbul ignore if
      if (response) {
        poDetailsWorkLogDs.off('update-data-failed', this.onUpdateDataFailed);
      }

      this.page.state.chatLogGroupData = await this.page.datasources[
        'poDetailsWorkLogDs'
      ].forceReload();
    } catch {
    } finally {
      this.app.userInteractionManager.drawerBusy(false);
      this.page.state.chatLogLoading = false;
      //Reset default Logtype
      let schemaLogType = this.page.datasources[
        'poDetailsWorkLogDs'
      ].getSchemaInfo('logtype');
      // istanbul ignore else
      if (schemaLogType) {
        this.page.state.defaultLogType = schemaLogType.default;
      }
    }
    //If no error happen then re-open the drawer
    // istanbul ignore else
    if (this.saveDataSuccessful) {
      this.page.showDialog('poWorkLogDrawer');
    }

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
      if (element.name === 'schedule' || element.name === 'approval') {
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
      if (element.name === 'schedule' || element.name === 'approval') {
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

    this.page.state.poloading = true;
    const poDetailDs = await this.app.findDatasource("poDetailds");

    //istanbul ignore if
    if (!this.page?.params?.href) {
      this.app.state.canLoadPoDetailDS = false;
    }
    await poDetailDs?.load({ noCache: true, itemUrl: this.page.params.href });
    this.app.state.canLoadPoDetailDS = true;

    const schedPage = this.app.findPage('schedule') || this.app.findPage("approvals");
    const polistds = this.app.findDatasource(schedPage.state.selectedDS);
    await CommonUtil.markStatusAssigned(this.app, this.page, poDetailDs, polistds);
    this.app.state.showLoaderOnAllPO = this.page.state.poloading = false;
    // TODO: this.app.state.showAssignment = CommonUtil.canInteractWorkOrder(poDetailDs.item, this.app);
  }

  /**
* This method is called by clicking on start work or stop work button on work order detail page
* and start/stop timer for specific work order accordingly.
* @param {event} event
*/
  //istanbul ignore next
  async openSignatureDialog(event) {
    let po = event.item;

    let poDetailds = this.app.findDatasource("podetails");
    //istanbul ignore else
    if (!po?.href) {
      this.page.state.canloadpodetails = false;
    }
    await poDetailds.load({ noCache: true, itemUrl: po.href });
    this.page.state.canloadpodetails = true;
    await this.app.userInteractionManager.openSignature(
      async imageData => {
        log.t(TAG, "base64 image" + imageData);

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
        filename: this.page.state.compDomainStatus,
        datasource: this.app.findDatasource("signatureAttachment"),
        onUpload: this.onUpload.bind(this),
      })
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

  /**
   * Redirects to attachments page.
   */
  showAttachmentPage(event) {
    this.app.state.poStatus = event.item.status_maxvalue;
    this.app.setCurrentPage({
      name: 'attachments',
      params: { itemhref: event.item.href },
    });
  }

  /**
   * Function to open edit work order page when click on edit icon
   * Passing current po details in page params to get the current work order details on edit page
   */
  purchaseOrderEdit(event) {
    let po = event.item;
    let poSchema = this.app.findDatasource(event.datasource).getSchema();
    // istanbul ignore next
    if (po && (po.ponum || po.href)) {
      this.app.state.poDetail = {
        page: 'purchaseOrderDetails',
        ponum: this.page.params.ponum,
        siteid: this.page.params.siteid,
        href: this.page.params.href,
      };
      this.app.setCurrentPage({
        name: 'poedit',
        resetScroll: true,
        params: { // TODO: params
          href: po.href,
          po, poSchema,
          ponum: po.ponum,
          //istask: po.istask,
          //wogroup: po.wogroup,
          //taskid: po.taskid,
          po: event.item
          // fromQuickReport: po.isquickreported
        },
      });
    }
  }

  /*
   * Set current Date/Time
   */
  async setCurrentDateTime() {
    let downTimeReportAsset = this.page.datasources['downTimeReportAsset'];
    let downTimeData = [
      {
        statuschangedate: this.app.dataFormatter.convertDatetoISO(new Date()),
      },
    ];
    await downTimeReportAsset.load({ src: downTimeData, noCache: true });
  }

  /*
   * Functions that support to display the local timezone
   */
  // istanbul ignore next 
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

  /**
   * Set the Log Type from the Lookup
   */
  async setPODetailsLogType(event) {
    this.page.state.defaultLogType = event.value;
  }

  /**
   * Function to set flag for 'save-data-failed' event
   */
  onUpdateDataFailed() {
    this.saveDataSuccessful = false;
  }

  /**
   * closing all dialogs of po detail page
   */
  _closeAllDialogs(page) {
    /* istanbul ignore else */
    if (page?.dialogs?.length) {
      page.dialogs.map((dialog) => page.findDialog(dialog.name).closeDialog());
    }
  }

  /**
   * Handle Delete transaction
   */
  async handleDeleteTransaction(event) {
    //istanbul ignore else
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
          const schPage = (this.app.findPage("schedule")) ? 'schedule' : 'approvals';
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

      let schedPage = this.app.findPage('schedule') || this.app.findPage("approvals");
      // istanbul ignore if
      if (schedPage) {
        await this.app.findDatasource(schedPage.state.selectedDS).forceReload();
      }
    }
  }

}

export default PurchaseOrderDetailsController;
