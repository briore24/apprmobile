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

import { log, Device } from '@maximo/maximo-js-api';
import CommonUtil from './SharedResources/utils/CommonUtil';
const TAG = 'PoPageController';

class PoPageController {
  pageInitialized(page, app) {
    this.app = app;
    this.page = page;
  }
  constructor() {
    this.onUpdateDataFailed = this.onUpdateDataFailed.bind(this);
    this.saveDataSuccessful = true;
    CommonUtil.sharedData.newPageVisit = true;
  }
  async openLogDrawer(event) {
    this.app.state.chatLogLoading = true;
    this.page.state.item = event.item;
    let drawer = event.drawer;
    let groupData; 

    let logDS = event.datasource;

    logDS.clearState();
    logDS.resetState();
    const synonymDs = this.app.findDatasource("synonymdomainData");
    await logDS.load().then((response) => {
      groupData = response;
    });

    if (Device.get().isMaximoMobile && logDS.options.query.relationship) {
      logDS.schema = logDS.dependsOn.schema.properties[
        Object.entries(logDS.dependsOn.schema.properties).filter(
          (item) =>
            item[1].relation && item[1].relation.toUpperCase() === logDS.options.query.relationship.toUpperCase()
        ).map((obj) => obj[0])[0]
      ].items;
    }
    switch(drawer) {
      case 'commLogDrawer':
        this.page.state.commLogGroupData = groupData;
        let subj = logDS.getSchemaInfo("subject");
        if (subj) {
          this.page.state.commLogDescLength = subj.maxLength;
        }
        break;
      case 'workLogDrawer':
        this.page.state.workLogGroupData = groupData;
        let schemaLogType = logDS.getSchemaInfo("logtype");
        let schemaDesc = logDS.getSchemaInfo("description");        
        let orgID = this.app.client?.userInfo?.insertOrg;
        let siteID = this.app.client?.userInfo?.insertSite;
        let logType;
        if (schemaLogType) {
          logType = schemaLogType.default?.replace(/!/g, "");
        }
        let filteredLogTypeList;
        synonymDs.setQBE("domainid", "=", "LOGTYPE");
        synonymDs.setQBE("orgid", orgID);
        synonymDs.setQBE("siteid", siteID);
        filteredLogTypeList = await synonymDs.searchQBE();
        if (filteredLogTypeList.length < 1) {
          synonymDs.setQBE("siteid", "=", "null");
          filteredLogTypeList = await synonymDs.searchQBE();
        }
        if (filteredLogTypeList.length < 1) {
          synonymDs.setQBE("orgid", "=", "null");
          filteredLogTypeList = await synonymDs.searchQBE();
        }
        this.page.state.defaultLogType = "!CLIENTNOTE!";

        const logItem = synonymDs.items.find((item) => {
          return item.maxvalue === logType && item.defaults;
        })

        const logValue = logItem ? `!${logItem.value}!` : schemaLogType.default;
        this.page.state.defaultLogType = this.page.state.initialDefaultLogType = logValue;

        if (schemaDesc) {
          this.page.state.workLogDescLength = schemaDesc.maxLength;
        }
        break;
    }
    this.page.state.chatLogLoading = false;
    this.page.showDialog(drawer);
  }

  async pageResumed(page, app) {
    CommonUtil.sharedData.newPageVisit = true;
    page.state.loading = true;
    page.state.historyDisable = false;
    page.state.isMobile = Device.get().isMaximoMobile;
    const poDs = page.datasources['poDs'];
	  await poDs?.load({ noCache: true, itemUrl: page.params.href });
    const device = Device.get();

    page.state.loadedLog = true;
    page.params.href = page.params.href || page.params.itemhref;
    // page.state.lineLoading = true;
    // offline mode sync
    if (this.page.state.disConnected && this.app.state.networkConnected && this.app.state.refreshOnSubsequentLogin !== false) {
      await poDs?.load({
        noCache: true,
        forceSync: true,
        itemURL: page.params.href
      });
      this.page.state.disConnected = false;
    } 

    page.state.loading = false;
    const index = 0;

    CommonUtil.sharedData.clickedPo = page.params.ponum;
    if (app.state.incomingContext && poDs.items.length === 0) {
      const loadParams = {
        noCache: true,
        itemUrl: page.params.href,
      }
      if (this.app.state.refreshOnSubsequentLogin !== false) {
        loadParams['forceSync'] = true;
      }
      await poDs.load(loadParams);
      if (poDs.items.length === 0) {
        let errorMessage =
          'This record is not on your device. Try again or wait until you are online.';
        page.error(
          this.app.getLocalizedLabel('record_not_on_device', errorMessage)
        );
      }
    }

    let ponum = this.page.datasources['poDs']?.item.ponum;
    page.state.editDetails = !['CAN', 'CLOSE'].includes(this.page.datasources['poDs']?.item?.status_maxvalue);

    if (!app.state.doclinksCountData) {
      app.state.doclinksCountData = {};
    }
    if (!app.state.doclinksCountData[ponum]) {
      app.state.doclinksCountData[ponum] = device.isMaximoMobile
        ? poDs.item?.doclinks?.member?.length
        : poDs?.item.doclinkscount;
    }
    //Reload the attachment list
    if (device.isMaximoMobile) {
      let poDs = page.datasources['poDs'];
      await poDs.forceReload();

      app.state.doclinksCountData[ponum] = poDs.item.doclinks ?
        poDs.item.doclinks?.member?.length
        : poDs?.item.doclinkscount;
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
    this.app.state.purchaseOrderStatus = poDs?.item?.status;
  }

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
    this.page.findDialog('workLogDrawer')?.closeDialog();
    this.app?.findPage("approvals")?.findDialog('poStatusChangeDialog')?.closeDialog();
  }

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
	this.page.findDialog('workLogDrawer')?.closeDialog(); 
  }

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
    let summary = value.summary;
    let longDesc = value.longDescription;
    let personID = this.app.client?.userInfo?.personid;
    let workLogDs = this.page.findDatasource("workLogDs");
    let id = value.messages.currentItem.worklogid + 1;
    let logType = value.logType?.value || this.page.state.defaultLogType || workLogDs.getSchemaInfo("logtype")?.default;

    let workLog = {
      description: summary,
      description_longdescription: longDesc,
      createdate: new Date(),
      createby: personID,
      logtype: logType,
      anywherefid: new Date().getTime(),
      clientviewable: value.visibility,
      worklogid: id
    }
    let options = {
      responseProperties: "anywherefid,createdate,description,description_longdescription,createby,logtype,worklogid",
      localPayload: {
        createby: personID,
        createdate: new Date(),
        description: summary,
        description_longdescription: longDesc,
        logtype: logType,
        anywherefid: workLog.anywherefid,
        worklogid: id
      }

    };
    let response;

    if (directSave) {
      workLogDs.on('update-data-failed', this.onUpdateDataFailed);
      response = await workLogDs.update(workLog, options);
      if (response) {
        workLogDs.off('update-data-failed', this.onUpdateDataFailed);
      }
      return;
    }

    this.app.userInteractionManager.drawerBusy(true);
    this.page.state.chatLogLoading = true;
    this.saveDataSuccessful = true;
    workLogDs.on('update-data-failed', this.onUpdateDataFailed);
    response = await workLogDs.update(workLog, options);
    if(response) {
      workLogDs.off('update-data-failed', this.onUpdateDataFailed);
    }
    this.page.state.workLogGroupData = await workLogDs.forceReload();
    this.app.userInteractionManager.drawerBusy(false);
    this.page.state.chatLogLoading = false;

    let schemaLogType = workLogDs.getSchemaInfo('logtype');
    if (schemaLogType) {
      this.page.state.defaultLogType = schemaLogType.default;
    }
    if(this.saveDataSuccessful) {
      this.page.showDialog("workLogDrawer");
    }
  }

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

  async openLineDetails() {}
  async approvePO() {}
  async rejectPO() {}

  async onUpload(manual = true) {
    if (manual && CommonUtil.sharedData.newPageVisit) {
      CommonUtil.sharedData.newPageVisit = false;
      return;
    }
    //During Start work it will not wait for the API response
    let poDetailResourceDS = this.app.findDatasource("poDs");
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
    const poDs = this.page.datasources['poDs'];

    const localeString = new Date(
      `${poDs.item[date_value]}`
    ).toString();

    const new_date_value = this.app.dataFormatter.convertDatetoISO(
      localeString
    );

    poDs.item[date_value] = new_date_value;
  }

  async setPODetailsLogType(event) { this.page.state.defaultLogType = event.value; }

  onUpdateDataFailed() { this.saveDataSuccessful = false; }

  onAfterLoadData() {
    log.i(TAG, "afterLoadData");
  }

  _closeAllDialogs(page) {
    if (page?.dialogs?.length) {
      page.dialogs.map((dialog) => page.findDialog(dialog.name).closeDialog());
    }
  }

}

export default PoPageController;
