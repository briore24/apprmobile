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

import CommonUtil from './Technician/utils/CommonUtil';

class POScheduleDataController {

  onDatasourceInitialized(ds, owner, app) {
    this.datasource = ds;
    this.owner = owner;
    this.app = app;
  }

  computedPOTitle(item) {
    let computedPOTitle = item.ponum || '';
      if (item.description) {
        computedPOTitle = computedPOTitle + (computedPOTitle ? " " : "") + item.description;
    }
    return computedPOTitle;
  }

  async onAfterLoadData(dataSource, items) {
    let page = this.app.findPage('schedule') || this.app.findPage("approvals");

    if (dataSource.name === page.state.selectedDS && items.length > 0) {
      this.app.state.firstPO = items[0].ponum;
    }
    if(dataSource.name === page.state.selectedDS){ 
      page.state.dataSourceIntializationCount += 1;
      if(page.state.dataSourceIntializationCount > 1){
        page.state.checkForUpdateButton = true;                      
      }
    }
  }
  
  /* Return workroder status and priority on workorder list page.
   * @param {item} item
   * @return {status_description} string value
   * @return {priority} number value 
   */
  computedWOStatusPriority(item) {
    let schedulePage;
    if (this.app?.pages) {
      const schPage = (this.app.findPage("schedule")) ? 'schedule' : 'approvals';
      schedulePage = this.app.pages.find((element) => {
        return (element.name === schPage) ? element : '';
      });
    }
    let valueDisable = this.app.checkSigOption(`${this.app.state.poOSName}.STATUS`) ? false :true ;
    let self = this;
    let poStatus = {
      label: item.status_description || item.status,
      type: 'cool-gray',
      action: true,
      disabled: valueDisable,
      onClick: () => {
        const schPage = (this.app.findPage("schedule")) ? 'schedule' : 'approvals';
        if (schedulePage && schedulePage !== '') {
          schedulePage.callController('openChangeStatusDialog', {
            item: item,
            datasource: schedulePage.state.selectedDS,
            referencePage: schPage,
            selectedDatasource: self.datasource
          });
        }
      }
    };

    if (item.priority !== null && item.priority !== "" && item.priority >=0) {
        return [{
          label: this.app.getLocalizedLabel('priority_label', `Priority ${item.priority}`, [item.priority]),
          type: 'dark-gray',
          disabled: valueDisable,
        }];
    }
  }
}

export default POScheduleDataController;