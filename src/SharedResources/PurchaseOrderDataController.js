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
import CommonUtil from './Technician/utils/CommonUtil'
class PurchaseOrderDataController {
  onDatasourceInitialized(ds, owner, app) {
    this.datasource = ds;
    this.owner = owner;
    this.app = app;
  }
  

  computedItemNum(item) {
    let computedItemNum = null;
    if (item) {
      if (item.itemnum && item.description) {
        computedItemNum = item.itemnum + ' ' + item.description;
      } else {
        computedItemNum = item.itemnum ? item.itemnum : item.description;
      }
    }
    return computedItemNum;
  }
  
  computedPOType(item) {
    let computedPOType = null;

    //istanbul ignore if
    if (item.potype) {
      computedPOType = item.potype + ' ' + (item?.ponum || '');
    } else {
      computedPOType = item?.ponum || '';
    }
    return computedPOType;
  }
  /**
   * Function to display WO status and priority on work order details page.
   */
   computedPODtlStatusPriority(item) {
    let schedulePage;
    let poDtlPage;
    //istanbul ignore next

    // DT214200 Addign the initialze to Maximo Mobile to resolve :: Unable to change status for work orders created without ever visiting the work order list.

    if(this?.app?.pages) {
      const schPage = (this.app.findPage("schedule")) ? 'schedule' : 'approvals';
      schedulePage = this.app.pages.find((element) => {
        return (element.name === schPage) ? element : '';
      });
      if( !schedulePage.initialized){
        schedulePage.initialize();
      }
      poDtlPage = this.app.findPage('purchaseOrderDetails');
    }

    // DT214200 close.
    let valueDisable = this.app.checkSigOption(`${this.app.state.poOSName}.STATUS`) ? false :true ;
    //istanbul ignore next
    let poStatus = {
      label: item.status_description || item.status, 
      type: 'white', 
      action: true,
      disabled: valueDisable, 
      onClick: ()=>{
        if(schedulePage && poDtlPage && poDtlPage?.datasources['poDetailResource']) {
          schedulePage.callController('openChangeStatusDialog', {
            item: item,
            datasource: poDtlPage.datasources['poDetailResource'].name,
            referencePage: 'purchaseOrderDetails',
            selectedDatasource: poDtlPage.datasources['poDetailResource']
          });
        }
      }
    };
    const poDetailDs = this.app.findDatasource("poDetailds");
    const currentItem = poDetailDs.item
    const canInteract = CommonUtil.canInteractWorkOrder(currentItem, this.app);

    if(item.priority !== null && item.priority !== "" && item.priority >=0){
      if(canInteract) {
        return [poStatus,
        {
          label: this.app.getLocalizedLabel('priority_label', `Priority ${item.priority}`, [item.priority]),
          type: 'dark-gray',
          disabled: valueDisable,
        }
      ];
      } else return [
        {
          label: this.app.getLocalizedLabel('priority_label', `Priority ${item.priority}`, [item.priority]),
          type: 'dark-gray',
          disabled: valueDisable,
        }
      ]
    } else {
      return (canInteract) ? [poStatus] : [];
    }
  }

  /**
   * Function called after loading the data
   * @param {dataSource} dataSource 
   * @param {items} items 
   */
  //istanbul ignore next
  async onAfterLoadData(dataSource, items) {
    let incompleteItems = [];
    let page = this.app.findPage("lines");
    page.state.itemToOpen = page.state.itemToOpen ? page.state.itemToOpen : '';
    if (dataSource.name === 'woLineDetailds' && items.length) {
      items.forEach((item) => {
        let status = item.status_maxvalue;
        if (item.taskid && status !== 'COMP' && status !== 'CLOSE' && status !== 'CAN') {
          incompleteItems.push(item);
        }
        if (this.app.currentPage.name === 'lines' && page.state.itemToOpen === '' && status !== 'CLOSE' && status !== 'CAN' && status !== 'COMP') {
          page.state.itemToOpen = item.poid;
        }
      });  
      this.app.state.taskCount = incompleteItems.length;
    }
  }

  /* Return workroder status and priority on workorder list page.
   * @param {item} item
   * @return {status_description} string value
   * @return {priority} number value 
   */
  computedLineStatus(item) {
    let linesPage;
    let lineDS;
    if(this.app) {
      linesPage = this.app.findPage('lines');
      lineDS = this.app.datasources['woLineDetailds'];
    }
    let lineStatus = {
      label: item.status_description || item.status, 
      type: 'warm-gray', 
      action: true, 
      onClick: ()=>{
        if(linesPage && lineDS) {
          linesPage.callController('openChangeStatusDialog', {
            item: item,
            datasource: lineDS.name,
            referencePage: 'lines',
            selectedDatasource: lineDS
          });
        }
      }
    };
    return lineStatus;
  }

  /**
   * Return whether to show lock or complete button
   * @param {item} task item
   */
  hideLockIcon(item) {
    let workTypeDs = this.app.findDatasource("dspotype");
    let poDetailds = this.app.findDatasource("poDetailResource");
    let lineDS = this.app.datasources['woLineDetailds'];
    let woWorkType = poDetailds.item.potype;
    let workType = [];
    const isFlowControlled = this.app.findDatasource('poDetailds')?.item?.flowcontrolled;
    //istanbul ignore else
    if(woWorkType) {
      workType = workTypeDs.items.filter(
        (item) => item.potype === woWorkType
      );
    }
    //istanbul ignore else
    if(isFlowControlled) { 
      let isCompletedPredecessor = this.app.callController('validatePredessor', lineDS.items, item);
      //istanbul ignore if
      if(!isCompletedPredecessor && item.predessorpos && item.status_maxvalue !== 'COMP') {
        return false;
      }
      //istanbul ignore else
      if(workType && workType?.length) {
        //istanbul ignore if
        if(workType[0].startstatus && workType[0].startstatus_maxvalue === 'COMP') {
          return false
        }
        if(workType[0].startstatus && workType[0].startstatus_maxvalue !== 'INPRG') {
          return true;
        }
      }
      //istanbul ignore else
      return poDetailds.item.status_maxvalue === 'INPRG';
    } else {
      return true
    }
  }

  /**
   * Return predessorpos as a string
   * @param {item} task item
   */
  computedPredecessorString(item) {
    let str = '';
    if(item.status_maxvalue !== 'COMP' && item.predessorpos) { 
      let lineids;
      //istanbul ignore if
      if(item.predessorpos.includes('(')) {
        lineids = this.app.callController('getPredssorPoTask',item);
      } else {
        lineids = item.predessorpos.split(',');
      }
      //istanbul ignore else
      if(lineids && lineids.length) {
        return lineids.toString();
      }
    } else {
      return str;
    }
  }

   /**
   * Return boolean value to show or hide border
   * @param {item} task item
   */
  computedBorderDisplay(item){
    //istanbul ignore else
   return  !((!item.description_longdescription) || (item.description_longdescription) || (!item.description_longdescription));
  }
}

export default PurchaseOrderDataController;
