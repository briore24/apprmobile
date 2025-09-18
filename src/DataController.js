import { Device, log } from '@maximo/maximo-js-api';

class DataController {
	onDatasourceInitialized(ds, owner, app) {
		this.datasource = ds;
		this.owner = owner;
		this.app = app;
	}
	
	computedTotalCost(item) {
		let totalCost = 0;
		let lines = item.poline;
		if (lines !== 'undefined') {
			lines.forEach((line) => {
				totalCost += line.loadedcost;
			});
		}
		return totalCost;
	}
	
	async onAfterLoadData(ds, items) {
		if (ds.name === this.app.state.selectedDS) {
			this.app.state.dsInitCount += 1;
			if (this.app.state.dsInitCount > 1) {
				let page = this.app.findPage("approvals");
				page.state.checkForUpdateButton = true;
			}
			
			if (items.length > 0) {
				this.app.state.firstPO = items[0].ponum;
			}
		}
	}
}

export default DataController;