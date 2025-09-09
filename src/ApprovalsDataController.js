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
	if (!item.computedTotalCost) {
		item.computedTotalCost = 0;
	}
	let lines = this.datasource.callController("getLines", item);
	lines.then((result) => {
		if(result.length > 0) {
			result.forEach((line) => {
				item.computedTotalCost += line.loadedcost;
			})
		}
	})

	
	return item.computedTotalCost;
  }
  
  async getLines(item) {
	  let lines = [];
	  let lineDS = this.page.findDatasource('assignedpoLineDS');
	  
	  try {
		await lineDS?.load({ noCache: true, itemUrl: this.page.params.href });
	  
		lineDS.forEach((line) => {
		if (line.ponum === item.ponum) {
			lines.push(line);
		}});
	  } catch (err) {
		  log.i(item, err);
	  } finally {
		  return lines;
	  }
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
	  
	  items.forEach((item) => {
		  if (!item.computedTotalCost) {
			  item.computedTotalCost = 0;
		  }
		  dataSource.callController("computedTotalCost", item);
	  });
    }
  }
}

export default ApprovalsDataController;