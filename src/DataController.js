import { log, Device } from '@maximo/maximo-js-api';
import CommonUtil from './SharedResources/utils/CommonUtil';

class DataController {
	onDatasourceInitialized(ds, owner, app) {
		this.datasource = ds;
		this.owner = owner;
		this.app = app;
		this.page = this.app.currentPage;
	}
	
	computedTotalCost(item) {
		let totalcost = 0;
		let lines = item.poline;
		
		if (lines.length > 0) {
			lines.forEach((line) => {
				totalcost += line.loadedcost;
			})
		}
		return totalcost;
	}
	
	async onAfterLoadData(ds, items) {
		if (ds.name === this.page.state.selectedDS) {
			if (items.length > 0) {
				this.app.state.firstPO = items[0].ponum;
			}
			this.page.state.dsInitCount += 1;
			if (this.page.state.dsInitCount > 1) {
				this.page.state.checkForUpdateButton = true;
			}
		}
	}
	
}

export default DataController;