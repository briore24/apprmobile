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

import {Device} from '@maximo/maximo-js-api';
import SynonymUtil from './Technician/utils/SynonymUtil';
import commonUtil from "./Technician/utils/CommonUtil";

class LineController {
  pageInitialized(page, app) {
    this.app = app;
    this.page = page;
  }

  openLineLongDesc(item) {
    if (item) {
      this.page.state.lineLongDesc = item.description_longdescription;
      this.page.showDialog('poLineLongDesc');
      this.page.state.dialogOpend = true;
    }
  }

  /**
   * Function to inject time part into dateISO and return dateTime object.
   * For ex: dt = 2020-12-15T00:00:00.000+05:30, time = 2020-12-14T03:00:00.000+05:30
   * And it will return as 2020-12-15T03:00:00.000+05:30
   */
  combineDateTime(dateISO, timeISO, app) {
    let dataFormatter = (app) ? app.dataFormatter : this.app.dataFormatter;
    let date = dataFormatter.convertISOtoDate(dateISO);
    let time = dataFormatter.convertISOtoDate(timeISO);
    date.setHours(time.getHours());
    date.setMinutes(time.getMinutes());
    return date;
  }

/**
 * Function to check if all lines are complete
 * 
 * @param {object} lineItem - The current line item
 * @param {object} lineds - The line datasource
 * @returns {boolean} - True if all lines are complete and Next status of work order to be COMP, false otherwise
 */
  async poCompleteHook(lineItem, lineds) {
    const linesLeft = [];
    if (lineds?.items) {
      lineds.items.forEach((item) => {
        let status = item.status_maxvalue;
        if (item.lineid && item.lineid !== lineItem.lineid && !['COMP','CLOSE', 'CAN'].includes(status)) {
          linesLeft.push(item._rowstamp);
        }
      });
    }
    // istanbul ignore else
    return !linesLeft.length;
  }

  /*
   * Method to complete the line.
   */
  //istanbul ignore next
  async completePoLine(record) {
    if (!this.page.state.lineDisabled) {
      this.page.state.lineDisabled = true;
    } else {
      return;
    }
    let item = record.lineItem;
    // istanbul ignore else
    if (item) {
      item.disabled = true;
      this.page.state.lineLoadingstatus = true;
      this.page.state.currentLine = item.lineid;
      localStorage.setItem('scrollpos', window.scrollY);

      let statusData;
      if (record.directlyCompletePoLine) {
        statusData = await SynonymUtil.getSynonymDomain(this.app.findDatasource('synonymdomainData'), 'WOSTATUS', record.status);
      } else { //DT254965 : Adding a synonym function to get a synonym for a specific maxvalue and value.
        statusData = await SynonymUtil.getSynonymValue(this.app.findDatasource('synonymdomainData'), 'WOSTATUS', record.internalValue, record.value);
      }
      if (statusData) {
        let lineds = this.app.findDatasource('poLineDetailds');
       
        let line = {
          parameters: {
            status: statusData.value,
            date: this.app.dataFormatter.currentUserDateTime()
          },
          record: {href: item.localref},
          responseProperties: 'status,status_maxvalue,poid',
          localPayload: {
            poactivity: [{
              href: item.href,
              status: statusData.value,
              date: this.app.dataFormatter.currentUserDateTime(),
              status_maxvalue: statusData.maxvalue,
              status_description: statusData.description || statusData.value,
              poid: item.poid
            }],
          },
        };
        let incompLineCount = [];
        let isMobile = Device.get().isMaximoMobile;
        try {
          let response = await lineds.invokeAction('changeStatus', line);
          await lineds.forceReload();

          if (isMobile) {
            //Since mobile's response doesn't come from the server, just use localPayload
            response = line.localPayload.poactivity[0];
          }

          if (response) {
            item.status = isMobile ? statusData.value : response.status;
            item.status_maxvalue = isMobile ? statusData.maxvalue : response.status_maxvalue;
            item.status_description = (isMobile ? statusData.description : response.status_description) || statusData.value;

            if (lineds?.items) {
              lineds.items.forEach((item) => {
                let status = item.status_maxvalue;
                if (item.lineid && status !== 'CLOSE' && status !== 'CAN' && status !== 'COMP') {
                  incompLineCount.push(item._rowstamp);
                }
              });
              this.app.state.lineCount = incompLineCount.length;
            }
            this.page.state.itemToOpens=[];
            this.page.state.itemToOpen='';
            if (incompLineCount.length >= 1) {
              const itemToOpen_poid = lineds.items.find(item => item.poid && item._rowstamp === incompLineCount[0]);
              if (itemToOpen_poid) {
                this.page.state.itemToOpen = itemToOpen_poid.poid;
               } 
            }
            // istanbul ignore else
            if(!incompLineCount.length) {
              this.page.state.itemToOpen = '';
            }
            setTimeout(() => {
              this.page.state.lineDisabled = false;
              item.disabled = false;
            })
            const schPage = this.app.findPage("schedule") || this.app.findPage('approvals');
            let SchedulePageDS = this.app.findDatasource(schPage.state.selectedDS);
            if (SchedulePageDS) {
              await SchedulePageDS.forceReload();
            }

          }
        }catch (err) {
          item.disabled = false;
          this.page.state.lineDisabled = false;
          //handle error
        }
      } else {
        this.app.toast(
          this.app.getLocalizedLabel(
            'fail_get_synonym',
            `Can not get the synonym data for WOSTATUS`,
            ['WOSTATUS']
          ),
          'error'
        );  
      }
      //this.page.state.disableButton = false;
      this.page.state.lineLoadingstatus = false;
      item.disabled = false;
    }
    else if (this.page.state.lineDisabled) {
      this.page.state.lineDisabled = false;
    }
  }

  /**
   * function return lines from workorder.
   * @param {lineLIst} is linelist ds.
   * @param {selectedItem} is selected line object.
   * @param {selectedStatus} is changed item object.
   */
  async getWoLine(lineList, selectedItem, selectedStatus) {
    let workTypeDs = this.app.findDatasource("dsworktype");
    let woDetailds = this.app.findDatasource("woDetailds");
    let woWorkType = woDetailds.item.worktype;
    let workType = [];
    let woLineList = [];
    let initialStatus = 'INPRG';
    /* istanbul ignore else */
    if(woWorkType) {
      workType = workTypeDs.items.filter(
        (item) => item.worktype === woDetailds.item.worktype
      );
    }
    let workTypeStartMaxVal = workType?.length && workType[0].startstatus ? workType[0].startstatus_maxvalue : /* istanbul ignore next */'';
    /* istanbul ignore next */
    if(workTypeStartMaxVal === 'APPR' || workTypeStartMaxVal === 'WSCH' || workTypeStartMaxVal === 'WMATL' || workTypeStartMaxVal === 'INPRG') {
      initialStatus = workType[0].startstatus;
    }

    let INPRGStatus = await SynonymUtil.getSynonym(this.app.findDatasource('synonymdomainData'), 'WOSTATUS', `WOSTATUS|${initialStatus}`);
    /* istanbul ignore else */
    if(lineList.length){
      const tempLineList = lineList.map((item) => {
        /* istanbul ignore else */
        if(item.lineid) {
          /* istanbul ignore else */
          if(item.lineid === selectedItem.lineid) {
            return {
              ...item, 
              status: selectedStatus.value, 
              status_maxvalue : selectedStatus.maxvalue,
              status_description : selectedStatus.description
            };
          } else {
            return {
              ...item, 
              status: item.status, 
              status_maxvalue : item.status_maxvalue,
              status_description : item.status_description
            };
          }          
        }
        return item
      });

      woLineList = tempLineList.map((item) => {
        /* istanbul ignore else */
        if(item.lineid) {
          /* istanbul ignore next */
          if(isFlowControlled && item.predessorwos && item.lineid && item.lineid !== selectedItem.lineid) {
            let isComplitedPredessor =  this.app.callController('validatePredessor',tempLineList,item);
            let setStatus = isComplitedPredessor && (workTypeStartMaxVal !== 'CAN' && workTypeStartMaxVal !== 'WAPPR');
            return {
              ...item, 
              status: setStatus ? INPRGStatus.value : item.status, 
              status_maxvalue : setStatus ? INPRGStatus.maxvalue : item.status_maxvalue,
              status_description : setStatus ? INPRGStatus.description : item.status_description
            };
          } else {
            return {
              ...item, 
              status: item.status, 
              status_maxvalue : item.status_maxvalue,
              status_description : item.status_description
            };
          }        
        }
        return item
      });
    }
    return woLineList;
  }

  /**
   * Method invoked whenever page is visited. Check if maximoMobile will filter datasource with QBE approach.
   * @param {Object} page 
   * @param {Object} app 
   */
  async pageResumed(page, app) {
    page.state.lineDisabled = true;
    page.state.measurementDialog = false;
    let pageTitle = app.callController('updatePageTitle', {page: page, label: 'lines_title', labelValue: 'Lines'});
    //istanbul ignore next
    if (!pageTitle) { // If title return null or empty then retrying to fetch title again because app load takes time in appController
      window.setTimeout(() => {
        pageTitle = app.callController('updatePageTitle', {page: page, label: 'lines_title', labelValue: 'Lines'});
        page.state.pageTitle = pageTitle;
      }, 1);
    } else { // If title return value then set to state
      page.state.pageTitle = pageTitle;
    }
    page.state.inspectionAccess = app.checkSigOption(`${app.state.appnames.inspection}.READ`);
    page.state.enforceAssetScan = app.checkSigOption(`${app.state.woOSName}.ENFORCEASSETSCAN`);
    page.state.assetSwicthAccess = app.checkSigOption(`${app.state.appnames.assetswitch}.READ`) ? true :false ;
    let device = Device.get();
    page.state.itemToOpen = '';
    let woDetailds = app.findDatasource('woDetailds');

    //istanbul ignore next
    if(woDetailds.items.length === 0){
     //istanbul ignore else
     if (!page?.params?.href) {
      this.app.state.canLoadWoDetailDS = false;
    }
      await woDetailds.load({noCache: true, itemUrl: page.params.href});
      this.app.state.canLoadWoDetailDS = true;
    }

    page.state.workorder = woDetailds.item;

    let lineDataSource = app.findDatasource('woPlanLineDetailds'); 

    if (device.isMaximoMobile) {
      let externalStatusList = await SynonymUtil.getExternalStatusList(app, ['INPRG', 'WAPPR', 'WMATL', 'APPR', 'WSCH', 'WPCOND', 'COMP']);
      await lineDataSource.initializeQbe();
      lineDataSource.setQBE('status', 'in', externalStatusList);
      await lineDataSource.searchQBE(undefined, true);
    }
    else{
      await lineDataSource.forceReload();
    }

    //istanbul ignore next
    if(app.state.incomingContext && lineDataSource.items.length === 0) {
      woDetailds = app.findDatasource('woDetailds');
      // istanbul ignore else
      if (this.app.state.refreshOnSubsequentLogin !== false) {
        await woDetailds.forceSync();
      }
      await lineDataSource.forceReload();
      // istanbul ignore else
      if(lineDataSource.items.length === 0) {
        let errorMessage = 'This record is not on your device. Try again or wait until you are online.';
        page.error(
          this.app.getLocalizedLabel("record_not_on_device", errorMessage)
        );
      }
    }  
    
    // istanbul ignore else
    if(this.app.checkSigOption(`${this.app.state.appnames.assetmobile}.READ`) && woDetailds?.item?.assetnum !== lineDataSource?.item?.assetnum){
      await this.getLineAssetsHref(page.state.itemToOpen,lineDataSource,woDetailds);
    }
     this.page.state.lineDisabled = false;
  }

  /*
   * Method to open line status lookup from line page:
   */
  async openChangeStatusDialog(event) {
    let statusArr = [];
    statusArr = await commonUtil.getOfflineAllowedStatusList(this.app, event, false);
    this.page.state.selectedLineItem = event.item;
    let statusLstDS = this.page.datasources["linestatusDomainList"];
    statusLstDS.clearSelections();
    await statusLstDS.load({ src: statusArr, noCache: true });
    this.page.showDialog("lineStatusChangeDialog", { parent: this.page });
  }


  async changePoLineStatus() {
    let record = {
      lineItem: this.page.state.selectedLineItem,
      status: `STATUS|${this.page.state.selectedLineStatus?.value}`,
      date: this.app.dataFormatter.currentUserDateTime(),
      internalValue: this.page.state.selectedLineStatus?.maxvalue,
      value: this.page.state.selectedLineStatus?.value,
      directlyCompletePoLine : false
    }
    try {
      await this.completePoLine(record);
    } catch (error) {
      
    } finally {
      this.page.findDialog("lineStatusChangeDialog")?.closeDialog();
    }
  }
}

export default LineController;
