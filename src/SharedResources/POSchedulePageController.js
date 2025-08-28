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
import SynonymUtil from "./Technician/utils/SynonymUtil";
import CommonUtil from "./Technician/utils/CommonUtil";
const TAG = "POSchedulePageController";


class POSchedulePageController {
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
    ShellCommunicator.get().on("TRANSACTION_UNDONE", this.handleDeleteTransaction.bind(this));
  }

  /**
   * Redirects to details page
   * @param {Object} listItem - clicked item from list
   */
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

  /**
   * Function to open a sliding-drawer dialog to show Work Log for the Work Order with Long Description in Expanded View
   * @param event - should contain
   * event - event containing information about current item.
   * datasource - The Synonymdata Datasource to filter logType
   * item - The Work ORder Selected
   */
  async openWorkLogDrawer(event) {
    this.page.state.editPo = !['CAN'].includes(event?.item?.status_maxvalue);
    await CommonUtil.openWorkLogDrawer(this.app, this.page, event, this.page.datasources["poWorklogDs"], "workLogDrawer");
  }

  /**
  * Validate before closing sliding drawer.
  * @param {validateEvent} validateEvent
  */
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
    //istanbul ignore else
    if (!this.page.state.workLogData?.sendDisable) {
      this.saveWorkLog(this.page.state.workLogData);
    }
  }
// called on discard
  closeWorkLogSaveDiscard() {
    // Close Work Log Drawer
    this.page.findDialog('workLogDrawer')?.closeDialog();
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
    let longDescription = value.longDescription;
    let summary = value.summary;
    let poWorklogDs = this.page.datasources["poWorklogDs"];
    await poWorklogDs.load();
    let longType = value.logType?.value || this.page.state.defaultLogType || poWorklogDs.getSchemaInfo("logtype")?.default;
    // istanbul ignore else
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
        "anywhererefid,createdate,description,description_longdescription,person.displayname--displayname,createby--personid,logtype",
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
      }
    };
    let response;
    // Direct Save Flag used to save work log without using work log UX
    if (directSave) {
      poWorklogDs.on("update-data-failed", this.onUpdateDataFailed);
      response = await poWorklogDs.update(workLog, option);

      // istanbul ignore if
      if (response) {
        poWorklogDs.off("update-data-failed", this.onUpdateDataFailed);
      }

      return;
    }
    try {
      this.app.userInteractionManager.drawerBusy(true);
      this.page.state.chatLogLoading = true;
      this.saveDataSuccessful = true;

      poWorklogDs.on("update-data-failed", this.onUpdateDataFailed);
      response = await poWorklogDs.update(workLog, option);

      // istanbul ignore if
      if (response) {
        poWorklogDs.off("update-data-failed", this.onUpdateDataFailed);
      }

      this.page.state.chatLogGroupData = await this.page.datasources[
        "poWorklogDs"
      ].forceReload();
    } catch {
    } finally {
      this.app.userInteractionManager.drawerBusy(false);
      this.page.state.chatLogLoading = false;
      //Reset default Logtype
      let schemaLogType = this.page.datasources["poWorklogDs"].getSchemaInfo(
        "logtype"
      );
      // istanbul ignore else
      if (schemaLogType) {
        this.page.state.defaultLogType = schemaLogType.default;
      }
    }
    //If no error happen then re-open the drawer
    // istanbul ignore else
    if (this.saveDataSuccessful) {
      this.page.showDialog("workLogDrawer");
    }
  }

  /*
   * Method to open the Change Status slider-drawer. This is called from
   * multiple pages.
   *
   * @param event should contain
   * item - The Work Order selected.
   * datasource - The Datasource for synonymdomain.
   * referencePage - The Page which calls this controller.
   *
   */
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
              const pods = this.app.findDatasource("poDetailds");
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

  /*
   * validate purchaseorder status with respect to sigoption.
   */
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
 
  // Assisted by watsonx Code Assistant 
  /**
   * Close the dialogs for rejecting an assignment and looking up labor assignments.
   * @param {object} app - The application object.
   */
  dialogClosed() {
    this.app?.findPage("schedule")?.findDialog('rejectAssignment')?.closeDialog();
    // this.app?.findPage("schedule")?.findDialog('laborAssignmentLookup')?.closeDialog();
    // CommonUtil.clearSharedData(CommonUtil.sharedData?.allowReassignmentPage);
    CommonUtil.clearSharedData(CommonUtil.sharedData?.event);
  }

  /*
   * Load list data on the basis of selection from dropdown.
   */
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

  /**
   * Approves a purchase order
   * @param {Event} event - The event that triggered the action
   */
  async approvePO(event) {
    this.page.state.loading = true;
    const polistds = this.page.datasources[this.page.state.selectedDS];

    this.page.state.currentItem = event.item.ponum;
    let podetails = this.app.findDatasource("podetails");
    
    await podetails.load({
      noCache: true,
    });
    this.page.state.canloadpodetails = true;
    this.page.state.currentItem = event.item.ponum;
    
    await CommonUtil.markStatusAssigned(this.app, this.page, podetails, polistds);
    this.app.state.showLoaderOnAllPO = this.page.state.loading = false;
  }

  /**
   * @function rejectPO
   * @param {Object} event An object containing the following properties:
   * @param {Object} event.item The purchase order item that was selected.
   * @param {string} event.datasource The name of the datasource that contains the purchase order item.
   * @param {string} event.referencePage The name of the page that triggered the action.
   * @returns {Promise<void>} A promise that resolves when the purchase order details datasource has been loaded and the reject dialog has been displayed.
   */
  async rejectPO(event) {
    this.page.state.poItem = event.item;
    this.page.state.loading = true;
    this.page.state.disableDoneButton = false;
    this.page.state.appVar = this.app;
    this.page.state.referenceDS = event.datasource;
    this.page.state.referencePage = event.referencePage;
    this.page.state.currentItem = event.item.ponum;
    this.page.state.statusDialog = "rejectPO";

    let podetails = this.app.findDatasource("podetails");
    //istanbul ignore else
    if (!podetails.items?.length || (podetails?.items?.[0]?.ponum !== event.item.ponum)) {
      await podetails?.load({ noCache: true });
    }
    this.page.state.canloadpodetails = true;
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
    this.page.state.assignmentHeader = this.app.getLocalizedLabel(
      "reject_header",
      "Reject Purchase Order"
    );
    this.page.state.assignmentSubHeader = this.app.getLocalizedLabel(
      "reject_subheader",
      "Select the rejection code"
    );

    await this.app.showDialog("rejectPO");
    this.app.state.showLoaderOnAllPO = this.page.state.loading = false;
  }


  /**
   * @function updateSignaturePrompt
   * @description This function updates the signature prompt based on the system property.
   */
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

  /**
  * This method is called by clicking on start work or stop work button on work order detail page
  * and start/stop timer for specific work order accordingly.
  * @param {event} event
  */
  //istanbul ignore next
  async openSignatureDialog(event) {
    await this.updateSignaturePrompt(event.item.status);
    this.page.state.sigUploaded = false;
    const podetails = this.app.findDatasource("podetails");
    await podetails.load({ noCache: true });
    this.page.state.canloadpodetails = true;
    this.page.state.compDomainStatus = event.item.status + new Date().getTime();
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

  async onUpload() {
    this.page.state.sigUploaded = true;
  }

  async pageResumed(page) {
    this.trackUserLogin(page, this?.app?.client?.userInfo?.loginID);
    if (this.app.currentPage?.name === 'schedule' && this.app.lastPage?.name === 'purchaseOrderDetails') {
      CommonUtil.sharedData.navigatedFromPOPage = true;
    }
    //On firstLogin the wolist should get synced with server
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

  // Assisted by WCA@IBM
  // Latest GenAI contribution: ibm/granite-8b-code-instruct
  /**
   * Filter the list of items using the page parameters.
   * @param {object} item - The item to be filtered.
   * @returns {Promise<void>} A promise that resolves when the filtering is complete.
   */
  async filterListUsingPageParams(item) {
    const datasource = this.page.findDatasource(this.page.state.selectedDS);
    // istanbul ignore else
    if (!datasource || this.page.state.selectedSwitch === 0) {
        return;
    }
    await datasource.initializeQbe();
    datasource.setQBE("ponum", "=", item);
    const filteredCompClose = await datasource.searchQBE(undefined, true);
    this.openWOCard(filteredCompClose[0]);
  }

  async pagePaused() {
    this.page.findDialog('workLogDrawer')?.closeDialog();
    this.page.findDialog('poStatusChangeDialog')?.closeDialog(); 
    this.app?.findPage("schedule")?.findDialog('poStatusChangeDialog')?.closeDialog();
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
  
  async handleDeleteTransaction(event) { 
    if (event && event.app === this.app.name &&
      (this.app.currentPage.name === this.page.name || this.app.lastPage.name === this.page.name)
    ) {
      await this.app.findDatasource(this.page.state.selectedDS).forceReload();
    }
  }

  /**
   * Changes status of selected work order. This method is being used in approvals app
   * @param {Object} item 
   */
   async changePOStatus(inputData) {

    this.page.state.loading = true;
    this.page.state.currentItem = inputData.item.ponum;

    let dataFormatter = this.app.dataFormatter;
    let currDate = dataFormatter.convertDatetoISO(new Date());
    let approvalPOListDS = this.page.datasources['assignedpoDS'];
    // is this valid?
    let approvedStatus = await SynonymUtil.getSynonym(this.app.findDatasource('synonymdomainData'), 'WOSTATUS', inputData.status);
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
        //href: item.href,
        status: approvedStatus.value,
        status_maxvalue: approvedStatus.maxvalue,
        status_description: approvedStatus.description,
        poid: item.poid
      },
      query: {interactive: false},
      esigCheck: 0
    };
    
    if (this.checkEsigRequired(approvedStatus.value)) {
      option.esigCheck = 1;
    }
    await approvalPOListDS.invokeAction(action, option);
    await approvalPOListDS.forceReload();
    this.page.state.loading = false;
  }

  /**
   * In system properties we would get list of flag on which we have to ask for eSigCheck
   * if current status matches in list we would pass esigCheck 1 and on based of it graphite component
   * will handle to show prompt of esig
   * @returns 1 or 0 (boolean numeric value)
   */
  checkEsigRequired(status) {
    const esigCheck = this.app.state?.systemProp?.["maximo.mobile.postatusforesig"];
    const allowedSignature = esigCheck
      .split(',')
      .map((status) => status.trim());
      const addEsig = allowedSignature.length > 0 &&
      allowedSignature.indexOf(status) > -1;
    return (addEsig) ? 1 : 0;
  }
}

export default POSchedulePageController;