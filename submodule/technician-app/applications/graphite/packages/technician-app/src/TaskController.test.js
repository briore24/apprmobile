/*
 * Licensed Materials - Property of IBM
 *
 * 5724-U18, 5737-M66
 *
 * (C) Copyright IBM Corp. 2023 All Rights Reserved
 *
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with
 * IBM Corp.
 */

import TaskController from './TaskController';
import { Application, Page, JSONDataAdapter, Datasource, Device } from '@maximo/maximo-js-api';
import tasklist from './test/task-list-json-data.js';
import statusitem from './test/statuses-json-data.js';
import workorderitem from './test/wo-detail-json-data.js';
import SynonymUtil from "./utils/SynonymUtil";
import worktype from "./test/worktype-json-data";

import sinon from 'sinon';

function newStatusDatasource(data = statusitem, name = 'synonymdomainData') {
  const da = new JSONDataAdapter({
      src: data,
      items: 'member',
      schema: 'responseInfo.schema'
  });
  const ds = new Datasource(da, {
      idAttribute: 'value',
      name: name
  });
  return ds;
}

function newTaskDatasource(data = tasklist, name = 'woPlanTaskDetailds') {
  const da = new JSONDataAdapter({
      src: data,
      items: 'member',
      schema: 'responseInfo.schema'
  });
  const ds = new Datasource(da, {
      idAttribute: 'taskid',
      name: name
  });
  return ds;
}

function newWoDatasource(data = workorderitem, name = 'woDetailds') {
	const da = new JSONDataAdapter({
		src: workorderitem,
		items: 'member',
		schema: 'responseInfo.schema',
	});

	const ds = new Datasource(da, {
		idAttribute: 'wonum',
		name: name,
	});

	return ds;
}

it('should open long description dialog', async () => {
	let mockSetPage = jest.fn();
	global.open = jest.fn();

	const controller = new TaskController();
	const app = new Application();
	const page = new Page();

	app.registerController(controller);
	await app.initialize();

	app.setCurrentPage = mockSetPage;
	controller.pageInitialized(page, app);
	await controller.openTaskLongDesc({description_longdescription: "Long description"});
	expect(page.state.dialogOpend).toBeTruthy();	
});

it('should not open long description dialog', async () => {
	let mockSetPage = jest.fn();
	global.open = jest.fn();

	const controller = new TaskController();
	const app = new Application();
	const page = new Page();

	app.registerController(controller);
	await app.initialize();

	app.setCurrentPage = mockSetPage;
	controller.pageInitialized(page, app);
	await controller.openTaskLongDesc();
	expect(page.state.dialogOpend).not.toBeDefined();
});

it('pageResume test', async () => {
	let mockSetPage = jest.fn();
	global.open = jest.fn();

	const controller = new TaskController();
	const app = new Application();
	const page = new Page();
	const woDetailds = newWoDatasource(workorderitem, 'woDetailds');
	const taskDS = newTaskDatasource(tasklist, 'woPlanTaskDetailds');
	taskDS.load();
	const synonymDS = newStatusDatasource(statusitem, 'synonymdomainData');
	app.registerDatasource(synonymDS);
	app.registerController(controller);
	app.registerDatasource(woDetailds);
	app.registerDatasource(taskDS);
	app.client = {
		userInfo: {
			insertOrg: "EAGLENA",
			insertSite: "BEDFORD"
		},
		checkSigOption : (option) => true,
		findSigOption : (appName, sigoption) => true
	};
	let taskSearchStub = sinon.stub(taskDS, 'searchQBE');
	app.state.taskCount= 1;
	await app.initialize();
	app.setCurrentPage = mockSetPage;
	controller.pageInitialized(page, app);
	await controller.pageResumed(page, app);
	expect(taskSearchStub.called).toBe(false);
	Device.get().isMaximoMobile = true;
	app.state.taskCount= 0;
	await controller.pageResumed(page, app);
	expect(taskSearchStub.called).toBe(true);
});

// it('computedMeterCurDate', async () => {
// 	global.open = jest.fn();
// 	const controller = new TaskController();
// 	let app = new Application();

// 	app.dataFormatter.currentUserDateTime = jest.fn().mockReturnValue("2023-01-01T00:00:00Z");
// 	const page = new Page();

// 	await app.initialize();
// 	controller.pageInitialized(page, app);

// 	let result = await controller.computedMeterCurDate();
// 	expect(result).toBe("2023-01-01T00:00:00Z");
// });


it('should validate getWoTask', async () => {
	global.open = jest.fn();
	const controller = new TaskController();
	const app = new Application();
	const page = new Page();
	const woDetailds = newWoDatasource(workorderitem, 'woDetailds');
	const taskDS = newTaskDatasource(tasklist, 'woPlanTaskDetailds' , 'member');
	const synonymDS = newStatusDatasource(statusitem, 'synonymdomainData');
	const dsworktype = newStatusDatasource(worktype, "dsworktype");
	app.registerDatasource(dsworktype);
	app.registerDatasource(synonymDS);
	app.registerController(controller);
	app.registerDatasource(woDetailds);
	app.registerDatasource(taskDS);
	await taskDS.load();
	await dsworktype.load();
	await woDetailds.load();
	await app.initialize();
	controller.pageInitialized(page, app);
	sinon.stub(SynonymUtil, "getSynonym").returns({ value: "COMP", maxvalue: "COMP", description: "COMP" });
	let selectedStatus = { value: "COMP", maxvalue: "COMP", description: "COMP" };
	let selectedItem = {
			status_maxvalue: "APPR",
			status_description: "Approed",
			taskid: 10,
			predessorwos: true,
			woflowcontrolled: true,
			status: "APPR",
		};
	Device.get().isMaximoMobile = true;
	let data = await controller.getWoTask(tasklist.member, selectedItem, selectedStatus);
	expect(data.length).not.toBeNull();
	tasklist.member[0].taskid=null;
	let data1 = await controller.getWoTask(tasklist.member, selectedItem, selectedStatus);
	expect(data1.length).not.toBeNull();
});

it('should open task status dialog', async () => {
	let mockSetPage = jest.fn();
	global.open = jest.fn();

	const controller = new TaskController();
	const app = new Application();
	const page = new Page();
	const parentPage = new Page({name: 'parentPage'});
	page.parent = parentPage ;
	const taskDS = newTaskDatasource(tasklist, 'woPlanTaskDetailds');
	const taskstatusDomainList = newStatusDatasource(statusitem, 'taskstatusDomainList');
	page.registerDatasource(taskstatusDomainList);
	const ds = newStatusDatasource(statusitem, 'synonymdomainData');
	const woData = {...workorderitem};
  	woData.member[0].flowcontrolled = true;
	const woDetailds = newWoDatasource(woData, 'woDetailds');
  	app.registerDatasource(ds);
	app.registerDatasource(woDetailds);
	app.registerDatasource(taskDS);
	await woDetailds.load();
	await ds.load();
	const dsworktype = newWoDatasource(worktype, "dsworktype");
	app.registerDatasource(dsworktype);
	await dsworktype.load();
	app.registerController(controller);
	taskDS.load();
	await app.initialize();

	app.setCurrentPage = mockSetPage;
	jest.spyOn(app, "callController").mockImplementation(() => true);
	controller.pageInitialized(page, app);
	let event = {
		item : {
			status_maxvalue: "INPRG",
			status: "INPRG",
			orgid:'EAGLESA',
			siteid: 'BEDFORD',
			woflowcontrolled: true,
			worktype:'CM',
			predessorwos: '1201(20),1201(10)',
			allowedstates: {
			  COMP: [{ description: "Completed", value: "COMP" }],
			  WAPPR: [{ description: "Waiting on Approval", value: "WAPPR" }],
			},
		  }
	  };
	await controller.openChangeStatusDialog(event);
	expect(page.state.statusList).not.toBeNull();
	expect(page.state.disableDoneButton).toBeTruthy();
	
	delete woDetailds.item['worktype'];
	await controller.openChangeStatusDialog(event);
});

it('should redirect user to report page or workOrderDetails based on systemProp value of maximo.mobile.gotoreportwork', async () => {
	const mockSelectedWO = {
		page: 'workOrderDetails',
		wonum: 1022,
		siteid: 'BEDFORD',
		href: 'oslc/os/mxapiwodetail/_QkVERk9SRC8xMjAx',
	}
	const controller = new TaskController();
	const app = new Application();
	const page = new Page();
	app.registerController(controller);

	await app.initialize();

	const wodetails = newWoDatasource(mockSelectedWO, 'woDetailResource');
	await wodetails.load();
	app.registerDatasource(wodetails);
	
	controller.pageInitialized(page, app);

	await controller.redirectToAssetDetails();
	window.setTimeout(() => {
		expect(app.currentPage.name).toBe("assetDetails");
	}, 1000);

	app.state = {
		systemProp: {
			'maximo.mobile.gotoreportwork': '0'
		},
	};
	
	await controller.redirectToWODetailsOrReport();
	window.setTimeout(() => {
		expect(app.currentPage.name).toBe("report_work");
	}, 1000);

	app.state = {
		systemProp: {
			'maximo.mobile.gotoreportwork': '1'
		},
	};
	
	await controller.redirectToWODetailsOrReport();
	window.setTimeout(() => {
		expect(app.currentPage.name).toBe("workOrderDetails");
	}, 1000);
});

it('should redirect user to report page if all task is either completed/cancelled/closed', () => {
	const mockSelectedWO = {
		page: 'workOrderDetails',
		wonum: 1022,
		siteid: 'BEDFORD',
		href: 'oslc/os/mxapiwodetail/_QkVERk9SRC8xMjAx',
	};
	
	const app = new Application();
	const controller = new TaskController();
	const page = new Page({name: 'page'});
	controller.pageInitialized(page, app);

	const setCurrentPageSpy = sinon.spy(app, "setCurrentPage");
	controller.redirectToReportPage(mockSelectedWO, true);
	sinon.assert.callCount(setCurrentPageSpy, 1);
});

it('should redirect user to report page if all task is either completed/cancelled/closed and navigateToReportWork', async() => {
	const mockSelectedWO = {
		page: 'report_work',
		wonum: 1022,
		siteid: 'BEDFORD',
		href: 'oslc/os/mxapiwodetail/_QkVERk9SRC8xMjAx',
        itemhref: 'oslc/os/mxapiwodetail/_QkVERk9SRC8xMjAx',
        worktype: '', 
        istask: true,
        wogroup: '',
        taskid: ''
	};
	const app = new Application();
	const controller = new TaskController();
	const page = new Page({name: 'report_work', state: {}});
	app.registerPage(page);
	controller.pageInitialized(page, app);
	const setCurrentPageSpy = sinon.spy(app, "setCurrentPage");
	await controller.redirectToReportPage(mockSelectedWO, true);
	sinon.assert.callCount(setCurrentPageSpy, 1);
	expect(page.state.navigateToReportWork).toBeTruthy();
});

// Generated by WCA for GP
it('Should call woCompleteHook', async() => {
	const controller = new TaskController();
	const app = new Application();
	const page = new Page();
	controller.pageInitialized(page, app);

	let sampleTasks = { items: [
		{
			taskid: 1,
			status_maxvalue: 'CLOSE',
			_rowstamp: 123456
		},
		{
			taskid: 2,
			status_maxvalue: 'CAN',
			_rowstamp: 1234567
		},
		{
			taskid: 3,
			status_maxvalue: 'COMP',
			_rowstamp: 1234567
		},
		{
			taskid: 4,
			status_maxvalue: 'INPRG',
			_rowstamp: 1234568
		}
	]};

	const response = await controller.woCompleteHook({taskid: 4}, sampleTasks)
	expect(response).toBe(true);

	const response2 = await controller.woCompleteHook({taskid: 5}, sampleTasks)
	expect(response2).toBe(false);
});

it('should redirect user to report page if all task is either completed/cancelled/closed and navigateToReportWork', async () => {
	let mockSetPage = jest.fn();
	global.open = jest.fn();
	const controller = new TaskController();
	const app = new Application();
	const page = new Page();
	app.registerController(controller);
	await app.initialize();
	app.setCurrentPage = mockSetPage;

	controller.pageInitialized(page, app);

	const dateISO = "2020-12-14T00:00:00.000+05:30";
	const timeISO = "2020-12-14T05:00:00+05:30";

	let date = controller.combineDateTime(dateISO, timeISO, app);
	expect(date).not.toBeNull();
});

// Assisted by watsonx Code Assistant 
it('should call completeWoTask with the correct record and close the dialog', async () => {
	const record = {
		taskItem: 'taskItemValue',
		status: 'WOSTATUS|APPR',
		internalValue: 'APPR',
		value: 'APPR',
		directlyCompleteWoTask: false,
	};


	let mockSetPage = jest.fn();
	global.open = jest.fn();
	const controller = new TaskController();
	const app = new Application();
	const page = new Page();
	app.registerController(controller);
	await app.initialize();
	app.setCurrentPage = mockSetPage;
	controller.pageInitialized(page, app);

	const completeTaskSpy = jest.spyOn(controller, 'completeWoTask');
	controller.changeWoTaskStatus(record);

	expect(completeTaskSpy).toHaveBeenCalled();
});