import { log, Device } from '@maximo/maximo-js-api';
import SynonymUtil from './SharedResources/utils/SynonymUtil';
import CommonUtil from './SharedResources/utils/CommonUtil';

class PageController {
	pageInitialized(page, app) {
		log.t(page, "initialized!");
		this.app = app;
		this.page = page;
		let device = Device.get();
	}
	
	constructor() {
		this.onUpdateDataFailed = this.onUpdateDataFailed.bind(this);
		this.saveDataSuccessful = true;
		CommonUtil.sharedData.newPageVisit = true;
	}
	
	// called from approvals card template
	openCard(item) {
		if (item?.ponum) {
			this.app.setCurrentPage({
				name: 'poDetails',
				resetScroll: true,
				params: {
					ponum: item.ponum,
					siteid: item.siteid,
					revisionnum: item.revisionnum,
					firstLogin: this.page.state.firstLogin,
					href: this.page.params.href
				}
			});
		}
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
	
	async loadListData(evt) {
		let ds = this.page.findDatasource(evt.selectedItem.id);
		if (evt.selectedItem.id !== "Unspecified" && evt.selectedItem.id !== "serverSearch") {
			if (ds && !ds.state.loading) {
				ds.clearState();
				ds.resetState();
				await ds.load({ noCache: true, itemUrl: "" });
			}
		}
		else if (evt.selectedItem.id === "serverSearch") {
			ds.clearState();
			ds.resetState();
		}
	}
	
	async openRejectDialog(item) {
		this.app.showDialog('rejectDialog');
		this.app.state.item = item;
	}
	
	async rejectPO(event) {
		// check for input 
		
		// error on no input
		
		// change status & save input on success
	}
	
	async cancelRejectPO() {
		this.page.findDialog('rejectDialog').closeDialog();
	}
	async openLogDrawer(event) {
		this.app.state.chatLogLoading = true;
		this.page.state.item = event.item;
		let drawer = event.drawer;
		
		let groupData;
		
		let logDS = event.datasource;
		
		logDS.clearState();
		logDS.resetState();
		
		const synonymDS = this.app.findDatasource("synonymdomainData");
		await logDS.load().then((response) => { groupData = response; });
		
		if (this.device.isMaximoMobile && logDS.options.query.relationship) {
			logDS.schema = logDS.dependsOn.schema.properties[
				Object.entries(logDS.dependsOn.schema.properties).filter(
				(item) =>
					item[1].relation && item[1].relation.toUpperCase() === logDS.options.query.relationship.toUpperCase()
				).map((obj) => obj[0])[0]].items;
			]
		}
		switch (drawer) {
			case 'commLogDrawer':
				this.page.state.commLogGroupData = groupData;
				let subj = logDS.getSchemaInfo("subject");
				if (subj) {
				  this.page.state.commLogDescLength = subj.maxLength;
				}
				break;
			case 'workLogDrawer':
				this.page.state.workLogGroupData = groupData;
				let schemaLogType = logDS.getSchemaInfo("logtype");
				let schemaDesc = logDS.getSchemaInfo("description");        
				let orgID = this.app.client?.userInfo?.insertOrg;
				let siteID = this.app.client?.userInfo?.insertSite;
				let logType;
				if (schemaLogType) {
				  logType = schemaLogType.default?.replace(/!/g, "");
				}
				let filteredLogTypeList;
				synonymDs.setQBE("domainid", "=", "LOGTYPE");
				synonymDs.setQBE("orgid", orgID);
				synonymDs.setQBE("siteid", siteID);
				filteredLogTypeList = await synonymDs.searchQBE();
				if (filteredLogTypeList.length < 1) {
				  synonymDs.setQBE("siteid", "=", "null");
				  filteredLogTypeList = await synonymDs.searchQBE();
				}
				if (filteredLogTypeList.length < 1) {
				  synonymDs.setQBE("orgid", "=", "null");
				  filteredLogTypeList = await synonymDs.searchQBE();
				}
				this.page.state.defaultLogType = "!CLIENTNOTE!";

				const logItem = synonymDs.items.find((item) => {
				  return item.maxvalue === logType && item.defaults;
				})

				const logValue = logItem ? `!${logItem.value}!` : schemaLogType.default;
				this.page.state.defaultLogType = this.page.state.initialDefaultLogType = logValue;

				if (schemaDesc) {
				  this.page.state.workLogDescLength = schemaDesc.maxLength;
				}
				break;
		}
		
		this.page.state.chatLogLoading = false;
		this.page.showDialog(drawer);
	}
	
	async approvePO(event) {
		this.page.state.loading = true;
		let limits = await this.app.callController("getUserLimits");
		
		await POUtil.approvePO(this.app, this.page, limits, this.page.findDatasource(this.page.state.selectedDS), event);
	}
	
	async pageResumed(page, app) {
		this.trackUserLogin(page, this?.app?.client?.userInfo?.loginID);
		CommonUtil.sharedData.newPageVisit = true;
		page.state.loading = true;
		page.state.isMobile = this.device.isMaximoMobile;
		page.params.href = page.params.href || page.params.itemhref;
		
		
		if (this.page.state.disConnected && this.app.state.networkConnected && this.app.state.refreshOnSubsequentLogin !== false)
		let ds = page.findDatasource(page.state.selectedDS);
		if (this.app.currentPage?.name === 'approvals' && this.app.lastPage?.name === 'poDetails') {
			CommonUtil.sharedData.navigatedFromPOPage = true;
		}
		if (page.state.firstLogin && this.app.state.networkConnected && this.app.state.refreshOnSubsequentLogin !== false) {
			await ds?.forceSync();
		}
		else if (CommonUtil.sharedData.searchedText) {
			ds.baseQuery.searchText = CommonUtil.sharedData.searchedText;
			await ds.load({ ...ds.baseQuery, noCache: true });
			
			CommonUtil.sharedData.searchedText = "";
		} else {
			await ds?.forceReload();
		}
		// attachments count
		if (!app.state.doclinksCountData) {
			app.state.doclinksCountData = {};
		}
		if (!app.state.doclinksCountData[ponum]) {
			app.state.doclinksCountData[ponum] = device.isMaximoMobile ? poDS.item?.doclinks?.member?.length : poDS?.item.doclinkscount;
		}
	}
	
	async resetDatasource() {
		let ds = this.page.findDatasource(this.page.state.selectedDS);
		await ds.reset(ds.baseQuery);
	}
	
	async changeStatus(inputData) {
		this.page.state.loading = true;
		this.page.state.currentItem = inputData.item.ponum;
		
		let dataFormatter = this.app.dataFormatter;
		let currDate = dataFormatter.convertDatetoISO(new Date());
		let ds = this.page.findDatasource(this.page.state.selectedDS);
		let synonymStatus = await SynonymUtil.getSynonym(this.app.findDatasource('synonymdomainData'), 'POSTATUS', inputData.status);
		
		let action = 'changeStatus';
		let item = inputData.item;
		let option = {
			record: item,
			parameters: {
				status: synonymStatus.value,
				date: currDate
			},
			headers: {
				'x-method-override': 'PATCH'
			},
			responseProperties: 'status',
			localPayload: {
				status: synonymStatus.value,
				status_maxvalue: synonymStatus.maxvalue,
				status_description: synonymStatus.description,
				href: item.href
			},
			query: {interactive: false}
		};
		
		await ds.invokeAction(action, option);
		await ds.forceReload();
		this.page.state.loading = false;
	}
}

export default PageController;