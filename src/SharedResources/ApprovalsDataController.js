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

import CommonUtil from './utils/CommonUtil';

class ApprovalsDataController {

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
    let page = this.app.findPage("approvals");

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
}

export default ApprovalsDataController;