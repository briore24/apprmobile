import { Device, log } from '@maximo/maximo-js-api';
import CommonUtil from './SharedResources/utils/CommonUtil';
import SynonymUtil from './SharedResources/utils/SynonymUtil';

class PageController {
	pageInitialized(page, app) {
		this.app = app;
		this.page = page;
		this.device = Device.get();
	}
	
	constructor() {
		this.onUpdateDataFailed = this.onUpdateDataFailed.bind(this);
		this.saveDataSuccessful = true;
	}
	
	trackUserLogin(loginID) {
		const storageKey = 'logindata_' + loginID;
		const firstLoginData = localStorage.getItem(storageKey);
		const newDate = this.app.dataFormatter.convertISOtoDate(new Date());
		if (!firstLoginData || (Math.abs(newDate - this.app.dataFormatter.convertISOtoDate(firstLoginData)) / 3600000) > 24) {
			localStorage.setItem(storageKey, newDate);
			this.app.state.firstLogin = true;
		}
	}
	
	setDefaults() {
		if (this.page === 'approvals') {
			this.page.state.selectedSwitch = 0;
		}
	}
	
	onUpdateDataFailed() { this.saveDataSuccessful = false; }
	// called from approvals page
	openDetailPage(item) {
		if (item?.ponum) {
			this.app.setCurrentPage({
				name: "poDetails",
				resetScroll: true,
				params: {
					ponum: item.ponum,
					siteid: item.siteid,
					firstLogin: this.app.state.firstLogin,
					href: this.page.params.href
				}
			});
			this.app.state.currentItem = item;
		}
	}
	
	async pagePaused() {
		this.page.callController("_closeAllDialogs", this.page);
	}
	
	async pageResumed(page, app) {
		this.trackUserLogin(this?.app?.client?.userInfo?.loginID);
		page.state.loading = true;
		// handle approvals page
		if (page.name === 'approvals') {
			let ds = this.app.findDatasource(this.app.state.selectedDS);
			// reload, search, or sync 
			if (this.app.state.firstLogin && this.app.state.networkConnected && this.app.state.refreshOnSubsequentLogin !== false) {
				await ds.forceSync();
			}
			else if (CommonUtil.sharedData.searchedText) {
				ds.baseQuery.searchText = CommonUtil.sharedData.searchedText;
				await ds.load({ ...ds.baseQuery, noCache: true });
				
				CommonUtil.sharedData.searchedText = "";
			}
			else {
				await ds.forceReload();
			}
			// incoming context
			let incomingContext = this.app.state.incomingContext;
			if (incomingContext?.breadcrumb?.enabledReturnBreadcrumb) {
				this.page.state.breadcrumbWidth = this.app.state.screen.size === "sm" ? 68 : 50;
			}
			if (CommonUtil.SharedData?.clickedPO) {
				this.filterList(CommonUtil.sharedData?.clickedPO);
			}
		}
		// handle PO details page 
		if (page.name === 'poDetails') {
			const device = Device.get();
			CommonUtil.sharedDate.newPageVisit = true;
			page.state.isMobile = device.isMaximoMobile;
			page.state.historyDisable = false;
			
			const poDS = this.app.findDatasource('poDS');
			await poDS.load({ noCache: true, itemUrl: page.params.href });
			
			page.params.href = page.params.href || page.params.itemhref;
			
			// offline mode sync
			if (this.page.state.disConnected && this.app.state.networkConnected && this.app.state.refreshOnSubsequentLogin !== false) {
				await poDS?.load({
					noCache: true,
					forceSync: true,
					itemURL: page.params.href
				});
				this.page.state.disConnected = false;
			}
			
			page.state.loading = false;
			const index = 0;
			
			CommonUtil.sharedData.clickedPO = page.params.ponum;
			if (app.state.incomingContext && poDS.items.length === 0) {
				const loadParams = {
					noCache: true,
					itemUrl: page.params.href
				}
				if (app.state.refreshOnSubsequentLogin !== false) {
					loadParams['forceSync'] = true;
				}
				await poDS.load(loadParams);
				if (poDS.items.length === 0) {
					let errMsg = 'This record is not on your device. Try again or wait until you are online.';
					page.error(this.app.getLocalizedLabel('record_not_on_device', errMsg));
				}
			}
			let ponum = poDS?.item.ponum;
			
			if (!app.state.doclinksCountData) {
				app.state.doclinksCountData = {};
			}
			if (!app.state.doclinksCountData[ponum]) {
				app.state.doclinksCountData[ponum] = device.isMaximoMobile ? poDS.item?.doclinks?.member?.length : poDS?.item.doclinkscount;
			}
			if (device.isMaximoMobile) {
				await poDS.forceReload();
				
				app.statedoclinksCountData[ponum] = poDS.item.doclinks ? poDS.item.doclinks?.member?.length : poDS?.item.doclinkscount;
			}
			app.state.doclinksCount = app.state.doclinksCountData[ponum] ?  app.state.doclinksCountData[ponum] : undefined;
			
			page.state.loadedLog = false;
			let selectedDisplayOption = this.app.client?.getUserProperty('displayOption');
			if (selectedDisplayOption) {
				page.state.rowsSelected = selectedDisplayOption.rowsSelected;
			}
			this.app.state.poStatus = poDS?.item?.status;
		}
		// handle attachments page
	}
	
	async openDialog(event) { this.page.findDialog(event)?.showDialog(); }
	
	async closeDialog(event) { this.page.findDialog(event)?.closeDialog(); }
	
	async openLogDrawer(event) {
		this.app.state.chatLogLoading = true;
		let groupData;
		let logDS = this.app.findDatasource('workLogDS');
		logDS.clearState();
		logDS.resetState();
		
		const synonymDS = this.app.findDatasource('synonymDomainDS');
		await logDS.load().then((response) => { groupData = response; });
		
		if (this.device.isMaximoMobile && logDS.options.query.relationship) {
			logDS.schema = logDS.dependsOn.schema.properties[
				Object.entries(logDS.dependsOn.schema.properties).filter(
				(item) => item[1].relation && item[1].relation.toUpperCase() === logDS.options.query.relationship.toUpperCase()).map((obj) => obj[0])[0]
			].items;
		}
		this.page.state.workLogGroupData = groupData;
		let schemaLogType = logDS.getSchemaInfo('logtype');
		let schemaDesc = logDS.getSchemaInfo('description');
		let orgID = this.app.client?.userInfo?.insertOrg;
		let siteID = this.app.client?.userInfo?.insertSite;
		let logType;
		if (schemaLogType) {
			logType = schemaLogType.default?.replace(/!/g, "");
		}
		let filteredLogTypeList;
		synonymDS.setQBE("domainid", "=", "LOGTYPE");
		synonymDS.setQBE("orgid", "=", orgID);
		synonymDS.setQBE("siteid", "=", siteID);
		filteredLogTypeList = await synonymDS.searchQBE();
		
		if (filteredLogTypeList.length < 1) {
			synonymDS.setQBE("siteid", "=", "null");
			filteredLogTypeList = await synonymDS.searchQBE();
		}
		
		if (filteredLogTypeList.length < 1) {
			synonymDS.setQBE("orgid", "=", "null");
			filteredLogTypeList = await synonymDS.searchQBE();
		}
		
		this.page.state.defaultLogType = "!CLIENTNOTE!";
		
		const logItem = synonymDs.items.find((item) => {
			return item.maxvalue === logType && item.defaults;
		});
		
		const logValue = logItem ? `!${logItem.value}!` : schemaLogType.default;
		this.page.state.defaultLogType = this.page.state.initialDefaultLogType = logValue;
		
		if (schemaDesc) {
			this.page.state.workLogDescLength = schemaDesc.maxLength;
		}
		
		this.page.state.chatLogLoading = false;
		this.page.showDialog('workLogDrawer');
	}
	
	async approvePO(event) {
		this.page.state.loading = true;
		let limits = await this.app.callController("getUserLimits");
		let totPOlim = 0;
		limits.forEach((lim) => {
			totPOlim += lim.polimit;
		});
		
		if (event.computedTotalCost > totPOlim) {
			this.page.state.totalCost = event.computedTotalCost;
			this.page.state.totalPOlimit = totPOlim;
			this.page.showDialog("limitExceededDialog");
		}
		else {
			let status = {
				value: 'APPR',
				maxvalue: 'APPR',
				description: 'Approved'
			};
			
			let memo = "approval";
			await this.page.callController("changeStatus", item, status, memo);
		}
	}
	
	async rejectPO(event) {
		let item = event.item;
		let comment = this.page.state.rejectComment;
		
		if (comment !== "") {
			let status = {
				value: 'HOLD',
				maxvalue: 'HOLD',
				description: 'Hold'
			};
			
			await this.page.callController("changeStatus", item, status, comment);
		}
	}
	
	async changeStatus(item, newStatus, newMemo) {
		this.page.state.loading = true;
		let df = this.app.dataFormatter;
		let action = 'changeStatus';
		let curDate = df.convertDatetoISO(new Date());
		let option = {
			record: item,
			parameters: {
				status: newStatus.value,
				date: curDate,
				memo: newMemo
			},
			headers: {
				'x-method-override': 'PATCH'
			},
			responseProperties: 'status, rel.postatus{postatusid, changeby, changedate, status}',
			localPayload: {
				status: newStatus.value,
				status_maxvalue: newStatus.maxvalue,
				status_description: newStatus.description,
				statusdate: curDate
			},
			query: { interactive: false, ignorecollectionref: 1 },
			esigCheck: 0
		};
		try {
			let ds = this.app.findDatasource('poDS');
			await ds.invokeAction(action, option);
			await ds.forceReload();
		} finally {
			this.page.state.loading = false;
		}
	}
	
	async getLines(item) {
		let ponum = item.ponum;
		let lineDS = this.app.findDatasource('poLineDS');
		await lineDS?.load({ noCache: true, itemUrl: this.page.params.href });
		
		lineDS.clearQBE();
		lineDS.setQBE("ponum", "=", ponum);
		try {
			let result = await lineDS.searchQBE();
			return result;
		} catch(err) {
			alert(err);
		}
	}
	
	async loadListData(event) {
		let ds = this.app.findDatasource(event.selectedItem.id);
		if (event.selectedItem.id !== 'Unspecified' && event.selectedItem.id !== 'serverSearch') {
			if (ds && !ds.state.loading) {
				ds.clearState();
				ds.resetState();
				await ds.load({ noCache: true, itemUrl: "" });
			}
		}
		else if (event.selectedItem.id === 'serverSearch') {
			ds.clearState();
			ds.resetState();
		}
	}
	// called from approvals page
	async filterList(item) {
		const ds = this.page.findDatasource(this.page.state.selectedDS);
		if (!datasource || this.page.state.selectedSwitch === 0) {
			return;
		}
		await ds.initializeQbe();
		ds.setQBE("ponum", "=", item);
		const filteredCompClose = await ds.searchQBE(undefined, true);
		this.openPOCard(filteredCompClose[0]);
	}
	
	async resetDatasource() {
		let ds = this.app.findDatasource(this.app.state.selectedDS);
		await ds.reset(ds.baseQuery);
	}
	
	async setLocaleTime(date_value) {
		const poDS = this.app.findDatasource('poDS');
		const localeString = new Date(`${poDS.item[date_value]}`).toString();
		const newDateValue = this.app.dataFormatter.convertDatetoISO(localeString);
		
		poDS.item[date_value] = newDateValue;
	}
	
	_closeAllDialogs(page) {
		if (page?.dialogs?.length) {
			page.dialogs.map((dialog) => page.findDialog(dialog.name).closeDialog());
		}
	}
}

export default PageController;