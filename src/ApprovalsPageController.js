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

import { log, Device, ShellCommunicator } from "@maximo/maximo-js-api";
import "regenerator-runtime/runtime";
import SynonymUtil from "./SharedResources/utils/SynonymUtil";
import CommonUtil from "./SharedResources/utils/CommonUtil";
const TAG = "ApprovalsPageController";


class ApprovalsPageController {
  pageInitialized(page, app) {
    log.t(TAG, "Page Initialized");
    this.app = app;
    this.page = page;
    this.page.state.editPo = false;
    this.app.state.selectedPoListDs = this.page.state.selectedDS;
    let device = Device.get();

  }
  constructor() {
    this.onUpdateDataFailed = this.onUpdateDataFailed.bind(this);
    this.saveDataSuccessful = true;
  }

  showPODetail(item) {
    // istanbul ignore else
    if (item && item.ponum && !this.page.state.transactionProgress) {
      this.app.setCurrentPage({
        name: "poDetails",
        resetScroll: true,
        params: {
          ponum: item.ponum,
          siteid: item.siteid,
          firstLogin: this.page.state.firstLogin,
        },
      });
    }
  }


  async openWorkLogDrawer(event) {
    this.page.state.editPo = !['CAN'].includes(event?.item?.status_maxvalue);
    await CommonUtil.openWorkLogDrawer(this.app, this.page, event, this.page.findDatasource("poWorkLogDs"), "workLogDrawer");
  }

  workLogValidate(validateEvent) {
    if (this.page.state.isWorkLogEdit) {
      validateEvent.failed = true;
      this.page.showDialog('saveDiscardWorkLog');
    } else {
      validateEvent.failed = false;
    }
  }

// called on save
  saveWorkLogSaveDiscard() {
    // Save Entered Data to chat Log
    if (!this.page.state.workLogData?.sendDisable) {
      this.saveWorkLog(this.page.state.workLogData);
    }
  }
// called on discard
  closeWorkLogSaveDiscard() {
    this.page.findDialog('workLogDrawer')?.closeDialog();
  }

  watchChatLogChanges(value) {
    // Clear Debounce Timeout
    clearTimeout(this.page.state.workLogChangeTimeout);
    // Set Debounce Timeout
    this.page.state.workLogChangeTimeout = setTimeout(() => {
      if (value?.summary || value?.longDescription || (this.page.state.initialDefaultLogType && value?.logType?.value !== this.page.state.initialDefaultLogType?.replace(/!/g, "")) || value?.visibility) {
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
    await CommonUtil.saveWorkLog(this.app, this.page, this.page.findDatasource('poWorkLogDs'), this.page.findDialog('workLogDrawer'), value);
  }

  async openChangeStatusDialog(event) {
    this.page.state.statusMemo = "";
    let statusArr = [];
    let poType = [];
    const maxVal = event.item.status_maxvalue;

    statusArr = await CommonUtil.getOfflineAllowedStatusList(this.app, event, false);
    log.t(
      TAG,
      "openChangeStatusDialog : statusArr --> " + JSON.stringify(statusArr)
    );

    let statusLstDS = this.page.datasources["dsstatusDomainList"];
    statusLstDS.clearSelections();

    // istanbul ignore else
    if(event.item.flowcontrolled) {
      let filterValues= []
      
      let poTypeStartMaxVal = poType?.length && poType[0].startstatus ? poType[0].startstatus_maxvalue : '';
      let poTypeEndMaxVal = poType?.length && poType[0].completestatus ? poType[0].completestatus_maxvalue : '';
      if(!event.item.potype || !poTypeStartMaxVal) {
        // istanbul ignore if
        if(maxVal !== 'COMP') {
          filterValues = ['CLOSE', 'COMP']; 
        }
        
        if(maxVal === 'INPRG') {
          filterValues = ['CLOSE', 'COMP', 'WMATL', 'WAPPR']; 
        }
        
      } else if(event.item.potype && event.item.flowcontrolled && poType?.length) {
        // istanbul ignore else
        if(poTypeEndMaxVal === 'COMP') {
          filterValues = ['CLOSE'];
        } else if(poTypeEndMaxVal === 'CLOSE') {
          filterValues = ['CLOSE'];
        } else if(poTypeEndMaxVal === 'INPRG') {
          filterValues = ['CLOSE','COMP', 'INPRG'];
        }
        // istanbul ignore next
        if(poTypeStartMaxVal) {
          if(poTypeStartMaxVal === 'APPR' || poTypeStartMaxVal === 'WMATL' || poTypeStartMaxVal === 'WSCH') {
            if (maxVal !== 'COMP') {
              filterValues = [...filterValues, 'WAPPR', 'COMP'];
            } else {
              filterValues = ['WAPPR'];
            }
          }
          // istanbul ignore if
          if(poTypeStartMaxVal === 'INPRG' &&  maxVal === 'INPRG') {
            filterValues = [...filterValues, 'WMATL', 'WAPPR', 'COMP'];
            // istanbul ignore next
            if (event?.item?.flowcontrolled) {
              const pods = this.app.findDatasource("podetailDs");
              await pods.load({
                noCache: true,
                itemUrl: event.item.href,
              });
              if (this.app.state.taskCount === 0) {
                filterValues = [...filterValues, 'WMATL', 'WAPPR'];
              }
            }
          }
        }
      }

      // istanbul ignore else
      if(maxVal === 'COMP' && longitudex && latitudey) {
        filterValues = ['WAPPR', 'CLOSE'];
      }

      // istanbul ignore else
      if(filterValues?.length) {
        statusArr = statusArr.filter(item => filterValues.indexOf(item.maxvalue) === -1);
      }
    } // istanbul ignore else
     else if(longitudex && latitudey && lastLabTransData?.timerstatus_maxvalue === 'ACTIVE') {
        statusArr = statusArr.filter(item => ['CLOSE'].indexOf(item.maxvalue) === -1);
    }

    await statusLstDS.load({ src: statusArr, noCache: true });

    // set maximum length of comment text-area in changestatus through checking datasource schema
    const selectedDS = event.selectedDatasource;
    //istanbul ignore else
    if (selectedDS) {
      const commentsMaxLength = event.selectedDatasource.getFieldSize(
        "np_statusmemo"
      );
      //istanbul ignore else
      if (commentsMaxLength !== -1) {
        this.page.state.memoMaxLength = commentsMaxLength;
      }
    }
    let signatureAttachment = this.app.findDatasource("signatureAttachment");
    this.page.state.disableDoneButton = true;
    this.page.state.enableSignatureButton = false;
    this.page.state.poItem = event.item;
    this.page.state.signatureDs = signatureAttachment;
    this.page.state.referenceDS = event.datasource;
    this.page.state.referencePage = event.referencePage;
    this.page.state.statusDialog = "poStatusChangeDialog";

    this.page.showDialog("poStatusChangeDialog");
  }

  validatePoStatus(statusobj){
    let validPoStatus = true;      
    Object.entries(JSON.parse(this.app.state.poStatSigOptions)).forEach(([key, value]) =>{
       //istanbul ignore next
      if(value === statusobj.maxvalue){       
        validPoStatus = this.app.checkSigOption(`${this.app.state.poOSName}.${key}`)? true :false ;        

      }
    });
    return validPoStatus;
  }
 
  async loadPOListData(evt) {
    this.app.state.selectedPoListDs = evt.selectedItem.id;

    let seldatasource = this.page.datasources[evt.selectedItem.id];
    //istanbul ignore else
    if (evt.selectedItem.id !== "Unspecified" && evt.selectedItem.id !== "serverSearch") {
      //istanbul ignore else
      if (seldatasource && !seldatasource.state.loading) {
        seldatasource.clearState();
        seldatasource.resetState();
        await seldatasource.load({ noCache: true, itemUrl: "" });
      }
    } else if (evt.selectedItem.id === "serverSearch") {
      seldatasource.clearState();
      seldatasource.resetState();
    }
  }

onAfterLoadData(){
	log.i(TAG, 'data loaded on approvals page!');
}

  async approvePO(event) {
    this.page.state.loading = true;
	
	await CommonUtil.approvePO()
	
  }

  async rejectPO(event) {
    this.page.state.poItem = event.item;
    this.page.state.loading = true;
    this.page.state.disableDoneButton = false;
    this.page.state.appVar = this.app;
    this.page.state.referenceDS = event.datasource;
    this.page.state.referencePage = event.referencePage;
    this.page.state.currentItem = event.item.ponum;
    this.page.state.statusDialog = "rejectPO";

    let podetails = this.app.findDatasource("poDetailds");
    let statusLstDS = this.page.datasources["rejectList"];
    statusLstDS?.clearSelections();

    const rejectDS = this.page.findDatasource("rejectList");
    //istanbul ignore else
    if(!rejectDS?.items?.length) {
      let dnewreadingDS = this.app.findDatasource("alnDomainDS");
      await dnewreadingDS.initializeQbe();
      dnewreadingDS?.setQBE('domainid', '=', 'POREJECT');
      await dnewreadingDS?.searchQBE();
      
      if(dnewreadingDS.items) {
        await statusLstDS.load({ src: [...dnewreadingDS.items], noCache: true });
      }
    }
    this.page.state.rejectHeader = this.app.getLocalizedLabel(
      "reject_header",
      "Reject Purchase Order"
    );
    this.page.state.rejectSubHeader = this.app.getLocalizedLabel(
      "reject_subheader",
      "Select the rejection code"
    );

    await this.app.showDialog("rejectPO");
    this.app.state.showLoaderOnAllPO = this.page.state.loading = false;
  }

  updateSignaturePrompt(selected_status_is_inprg) {
    let allowedSignatureSystemProp = this.app.state?.systemProp?.["maximo.mobile.statusforphysicalsignature"];
    if (allowedSignatureSystemProp) {
      const allowedSignature = allowedSignatureSystemProp
        .split(",")
        .map((status) => status.trim());
      this.page.state.enableSignatureButton =
        allowedSignature.length > 0 &&
        allowedSignature.indexOf(selected_status_is_inprg) > -1;
    }
  }

  async onUpload() {
    this.page.state.sigUploaded = true;
  }

  async pageResumed(page) {
    this.trackUserLogin(page, this?.app?.client?.userInfo?.loginID);
    if (this.app.currentPage?.name === 'approvals' && this.app.lastPage?.name === 'poDetails') {
      CommonUtil.sharedData.navigatedFromPOPage = true;
    }
    //On firstLogin the list should sync with server
    if (page.state.firstLogin && this.app.state.networkConnected && this.app.state.refreshOnSubsequentLogin !== false) {
      await this.page.datasources[page.state.selectedDS]?.forceSync();
    } else if (CommonUtil.sharedData.searchedText) {
      let datasource = this.page.findDatasource(page.state.selectedDS);

      datasource.baseQuery.searchText = CommonUtil.sharedData.searchedText;
      await datasource.load({ ...datasource.baseQuery, noCache: true });

      CommonUtil.sharedData.searchedText = "";
    } else {
      await this.page.findDatasource(page.state.selectedDS)?.forceReload();
    }

    let incomingContext = this.app.state.incomingContext;
    // istanbul ignore else
    if (incomingContext?.breadcrumb?.enableReturnBreadcrumb) {
      // istanbul ignore next
      this.page.state.breadcrumbWidth =
        this.app.state.screen.size === "sm" ? 68 : 50;
    }
    // istanbul ignore else
    if(CommonUtil.sharedData?.clickedPo) {
      this.filterListUsingPageParams(CommonUtil.sharedData?.clickedPo);
    }
  }

  async filterListUsingPageParams(item) {
    const datasource = this.page.findDatasource(this.page.state.selectedDS);
    // istanbul ignore else
    if (!datasource || this.page.state.selectedSwitch === 0) {
        return;
    }
    await datasource.initializeQbe();
    datasource.setQBE("ponum", "=", item);
    const filteredCompClose = await datasource.searchQBE(undefined, true);
    this.openPOCard(filteredCompClose[0]);
  }

  async pagePaused() {
    this.page.findDialog('workLogDrawer')?.closeDialog();
    this.page.findDialog('poStatusChangeDialog')?.closeDialog(); 
	}

  trackUserLogin(page, loginID) {
    const storageKey = 'logindata_' + loginID;
    const firstLoginData = localStorage.getItem(storageKey);
    const newDate = this.app.dataFormatter.convertISOtoDate(new Date());
    if (!firstLoginData || (Math.abs(newDate - this.app.dataFormatter.convertISOtoDate(firstLoginData)) / 3600000) > 24) {
      localStorage.setItem(storageKey, newDate);
      page.state.firstLogin = true;
    }  
  }

  async resetDatasource() {
    let datasource = this.page.datasources[this.page.state.selectedDS];
    await datasource.reset(datasource.baseQuery);
  }

  setDefaults() {
    this.page.state.selectedSwitch = 0;
  }

  async setLogType(event) {
    this.page.state.defaultLogType = event.value;
  }

  onUpdateDataFailed() {
    this.saveDataSuccessful = false;
  }
  
  async changePOStatus(inputData) {

    this.page.state.loading = true;
    this.page.state.currentItem = inputData.item.ponum;

    let dataFormatter = this.app.dataFormatter;
    let currDate = dataFormatter.convertDatetoISO(new Date());
    let approvalPOListDS = this.page.datasources['assignedpoDS'];
    let approvedStatus = await SynonymUtil.getSynonym(this.app.findDatasource('synonymdomainData'), 'POSTATUS', inputData.status);
    let action = 'changeStatus';
    let item = inputData.item;
    let option = {
      record: item,
      parameters: {
        status: approvedStatus.value,
        date: currDate,
      },
      headers: {
        'x-method-override': 'PATCH'
      },
      responseProperties: 'status',
      localPayload: {
        status: approvedStatus.value,
        status_maxvalue: approvedStatus.maxvalue,
        status_description: approvedStatus.description,
        poid: item.poid
      },
      query: {interactive: false},
    };
    
    await approvalPOListDS.invokeAction(action, option);
    await approvalPOListDS.forceReload();
    this.page.state.loading = false;
  }
}

export default ApprovalsPageController;