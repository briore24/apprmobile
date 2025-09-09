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

import { log } from '@maximo/maximo-js-api'
import CommonUtil from './SharedResources/utils/CommonUtil';

class ApprovalsDataController {

  onDatasourceInitialized(ds, owner, app) {
    this.datasource = ds;
    this.owner = owner;
    this.app = app;
	this.page = this.app.findPage('approvals');
  }
  computedPOStatusPriority(item) {
	let valueDisable = this.app.checkSigOption(`${this.app.state.poOSName}.STATUS`) ? false : true ;
	let self = this;
	let poStatus = {
		label: item.status_description || item.status,
		type: 'cool-gray',
		action: true,
		disabled: valueDisable,
		onClick: () => {
			this.app.findPage("approvals").callController('openChangeStatusDialog', {
				item: item,
				datasource: this.app.findPage("approvals").state.selectedDS,
				referencePage: this.app.findPage("approvals"),
				selectedDatasource: this.app.findPage("approvals").state.selectedDS
			});
		}
	};
	
	if (item.priority !== null && item.priority !== "" && item.priority > 0) {
		return [poStatus,
		{
			label: this.app.getLocalizedLabel('priority_label', `Priority ${item.priority}`, [item.priority]),
			type: 'dark-gray',
			disabled: valueDisable,
		}];
	} else {
		return [poStatus];
	}
  }

  computedTotalCost(item) {
	let totalcost = 0;
	let lines = item.poline;
	if (lines !== 'undefined') {
		lines.forEach((line) => {
			totalcost += line.loadedcost;
		});
	}
	return totalcost;
  }
  
  async onAfterLoadData(dataSource, items) {
    let page = this.app.findPage("approvals");
	let costs = {};
    if (dataSource.name === page.state.selectedDS && items.length > 0) {
      this.app.state.firstPO = items[0].ponum;
    }
    if (dataSource.name === page.state.selectedDS){ 
      page.state.dataSourceIntializationCount += 1;
      if(page.state.dataSourceIntializationCount > 1){
        page.state.checkForUpdateButton = true;                      
      }
    }
  }
}

export default ApprovalsDataController;