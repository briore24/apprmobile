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

import 'regenerator-runtime/runtime';
import AppController from './AppController';
import WorkOrderDetailsController from './WorkOrderDetailsController';
import { Application, Datasource, JSONDataAdapter, Page, Device, AppSwitcher, MaximoAppSwitcher, DisconnectedSchemaFactory } from '@maximo/maximo-js-api';
import statusitem from './test/statuses-json-data.js';
import tasklist from './test/task-list-json-data.js';
import worktype from "./test/worktype-json-data";
import workorderitem from './test/wo-failure-report-json-data';
import SynonymUtil from "./utils/SynonymUtil";
import WOTimerUtil from "./utils/WOTimerUtil";
import StorageManager from "@maximo/map-component/build/ejs/framework/storage/StorageManager";
import sinon from 'sinon';
import { expect } from '@storybook/test';
import WOUtil from "./utils/WOUtil.js";

function newDatasource(data = workorderitem, name = 'wolistds') {
    const da = new JSONDataAdapter({
        src: data,
        items: 'member'
    });
    const ds = new Datasource(da, {
        idAttribute: 'wonum',
        name: name
    });
    return ds;
}
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
function newLocDS(data = workorderitem, name = 'woLocationds') {
	const da = new JSONDataAdapter({
		src: data,
		items: 'member',
		schema: 'responseInfo.schema',
	});
	
	const ds = new Datasource(da, {
		idAttribute: 'wonum',
		name: name,
	});
	
	return ds;
}
function newAssetLocationDatasource(data = workorderitem, name = 'woAssetLocationds') {
	const da = new JSONDataAdapter({
		src: data,
		items: 'member',
		schema: 'responseInfo.schema',
	});

	const ds = new Datasource(da, {
		idAttribute: 'wonum',
		name: name,
	});

	return ds;
}

describe('AppController', () => {
  let mockedFn;
  let sandbox = null;
	afterEach(function () {
		sandbox.restore();
	}); 
  beforeEach(() => {
    mockedFn = jest.fn();
    sandbox = sinon.createSandbox();
    sandbox.stub(DisconnectedSchemaFactory.get(), 'createIndex').resolves();
  });

  it('set another page', async () => {
    const controller = new AppController();
    const app = new Application();
    app.appSwitcher.currentApp = app;
    const page = new Page({name: 'materials'});
    app.registerPage(page);
    app.registerController(controller);
    await app.initialize();

    app.setCurrentPage = mockedFn;
    controller.navigationItemClicked();

    expect(mockedFn.mock.calls.length).toBe(1);
  });

  it('set materials page', async () => {
    const controller = new AppController();
    const app = new Application();
    const page = new Page({name: 'schedule'});
    app.registerPage(page);
    app.registerController(controller);
    await app.initialize();

    app.setCurrentPage = mockedFn;
    controller.openMaterialsPage();

    expect(mockedFn.mock.calls.length).toBe(1);
  });

  // Assisted by WCA@IBM
  // Latest GenAI contribution: ibm/granite-20b-code-instruct-v2
  it('test the page-changed event is fired on application initialization', async () => {
    const app = new Application();
    const controller = new AppController();
    let nextPage = new Page({
      name: 'schedule',
    });
    let prevPpage = new Page({ name: 'woedit' });
    app.registerPage(prevPpage);
    app.registerPage(nextPage);
    app.registerController(controller);
    await app.initialize();
    app.setCurrentPage = mockedFn;
    await app.emit('page-changed', nextPage, prevPpage);
    nextPage = new Page({
      name: 'schedule',
      state: {
        previousPage: 'schedulecardlist',
        mapOriginPage: 'wodetail',
      }
    });
    prevPpage = new Page({ name: 'workOrderDetails' });
    nextPage.callController = mockedFn;
    nextPage.openPrevPage = mockedFn;
    nextPage.callController('openPrevPage');
    await app.emit('page-changed', nextPage, prevPpage);
    expect(nextPage.openPrevPage.mock.calls.length).toBe(1);
  });

  it('test sould _buildWoStatusSet()', async () => {
    const controller = new AppController();
    const app = new Application();
    app.registerController(controller);
    await app.initialize();
    let item = {
        allowedstates: {
            COMP: [{ description: 'Completed', value: 'COMP', maxvalue: 'COMP' }]
        },
    };
    let data = controller._buildWoStatusSet(item.allowedstates);
    expect(data[0].value).toBe('COMP');
  });

  it('test should _getStatusExternalValue()', async () => {
    const controller = new AppController();
    const app = new Application();
    app.registerController(controller);
    await app.initialize();
    let item = [{ description: 'Completed', defaults: true, value: 'COMP', maxvalue: 'COMP' }];
    let data = controller._getStatusExternalValue(item, 'COMP');
    expect(data).toBe('COMP');
  });

  it('initialized with valid incoming context', async () => {
    const controller = new AppController();
    const app = new Application();
    const page = new Page({name: 'workOrderDetails'});
    app.registerPage(page);
    // mock the setCurrentPage
    const mockSetPage = jest.fn();
    const origSetPage = app.setCurrentPage;
    app.setCurrentPage = mockSetPage;
    app.state.currentPageName = 'workOrderDetails';

    app.registerController(controller);
    app.state.incomingContext = {
      page: 'workOrderDetails',
      wonum: 1022,
      siteid: 'BEDFORD',
      href: 'oslc/os/mxapiwodetail/_QkVERk9SRC8xMjAx',
      breadcrumb: {
        returnName: 'Returning to inspection',
        enableReturnBreadcrumb: true
      }
    };
    await app.initialize();
    expect(mockSetPage.mock.calls[0][0].name).toBe('schedule');
    expect(mockSetPage.mock.calls[1][0].name).toBe('workOrderDetails');
    expect(mockSetPage.mock.calls[1][0].params.wonum).toEqual(1022);
    expect(mockSetPage.mock.calls[1][0].params.siteid).toEqual('BEDFORD');
    expect(mockSetPage.mock.calls[1][0].params.href).toEqual('oslc/os/mxapiwodetail/_QkVERk9SRC8xMjAx');
    app.setCurrentPage = origSetPage;
  });

  it('initialized incoming context of itemId', async () => {
    const controller = new AppController();
    const app = new Application();
    const page = new Page({name: 'materialRequest'});
    app.registerPage(page);
    // mock the setCurrentPage
    const mockSetPage = jest.fn();
    const origSetPage = app.setCurrentPage;
    app.setCurrentPage = mockSetPage;
    app.state.currentPageName = 'materialRequest';

    app.registerController(controller);
    app.state.incomingContext = {
      page: 'materialRequest',
      href: 'oslc/os/mxapiwodetail/_QkVERk9SRC8xMjIx',
      itemId: '2066',
    };
    await app.initialize();
    expect(mockSetPage.mock.calls[0][0].name).toBe('materialRequest');
    expect(mockSetPage.mock.calls[0][0].params.href).toEqual('oslc/os/mxapiwodetail/_QkVERk9SRC8xMjIx');
    app.setCurrentPage = origSetPage;
  });

  it('should open Navigation map for iOS', async () => {
    let mockSetPage = jest.fn();
    global.open = jest.fn();
  
    const controller = new AppController();
    const app = new Application();
  
    app.registerController(controller);
    await app.initialize();
  
    app.setCurrentPage = mockSetPage;

    let option = {
      'geolocationlong':28.12,
      'geolocationlat':78.00,
      'serlong':28.16,
      'serlat':77.12
    }
    app.state = {
      systemProp: {
        'mxe.mobile.navigation.ios': 'AppleMaps',
        'mxe.mobile.navigation.windows': 'GoogleMaps',
        'mxe.mobile.navigation.android' : 'Waze'
      }      
    };

    let device = Device.get();
    device.os = {
      name : 'Symbian'
    };

    await controller.openNavigation(option);
    expect(global.open.mock.calls.length).toEqual(0);

    device.os = {
      name : 'iOS'
    };

    await controller.openNavigation(option);
    expect(global.open.mock.calls.length).toEqual(1);
  });

  it('should open Navigation map for Android', async () => {
    let mockSetPage = jest.fn();
    global.open = jest.fn();
  
    const controller = new AppController();
    const app = new Application();
  
    app.registerController(controller);
    await app.initialize();
  
    app.setCurrentPage = mockSetPage;

    let option = {
      'geolocationlong':28.12,
      'geolocationlat':78.00,
      'serlong':28.16,
      'serlat':77.12
    }
    app.state = {
      systemProp: {
        'mxe.mobile.navigation.ios': 'AppleMaps',
        'mxe.mobile.navigation.windows': 'GoogleMaps',
        'mxe.mobile.navigation.android' : 'Waze'
      }      
    };

    let device = Device.get();
    device.os = {
      name : 'Symbian'
    };

    await controller.openNavigation(option);
    expect(global.open.mock.calls.length).toEqual(0);

    device.os = {
      name : 'Android'
    };

    await controller.openNavigation(option);
    expect(global.open.mock.calls.length).toEqual(1);

    device.os = {
      name : 'Windows'
    };

    await controller.openNavigation(option);
    expect(global.open.mock.calls.length).toEqual(2);
  });

  it('test the openPrevPage function has been called', async () => {
    let app = new Application();
    let controller = new AppController();
    
    let page = new Page({name: 'schedule'});
    let detailPage = new Page({name: 'workOrderDetails'});
    app.registerPage(page);
    app.registerPage(detailPage);
    app.registerController(controller);
    
    await app.initialize();
    await app.emit('page-changed', page, detailPage);
    page.callController = mockedFn;
    page.openPrevPage = mockedFn;
    page.callController('openPrevPage');
    expect(page.openPrevPage.mock.calls.length).toBe(1);
  });

  it('test the follow Up WO has to be called', async () => {
    let app = new Application();
    let controller = new AppController();
    let page = new Page({name: 'schedule'});
    let relatedWorkOrderPage = new Page({name : 'relatedWorkOrder'});
    app.registerPage(page);
    app.registerPage(relatedWorkOrderPage);
    app.registerController(controller);
    await app.initialize();
    await app.emit('page-changed', page,relatedWorkOrderPage);
    page.callController = mockedFn;
  });

  it('test the page-changing event is fired on application initialization', async () => {
    let app = new Application();
    let controller = new AppController();
    
    let nextPage = new Page({name: 'schedule'});
    let prevPpage = new Page({name: 'schedule'});
    app.registerPage(prevPpage);
    app.registerPage(nextPage);
    app.registerController(controller);
    
    await app.initialize();
    await app.emit('page-changing', nextPage, prevPpage);
    nextPage.callController = mockedFn;
    nextPage.setDefaults = mockedFn;
    nextPage.callController('setDefaults');
    expect(nextPage.setDefaults.mock.calls.length).toBe(1);
  });

  it('test the on-app-serialize event is fired on application initialization', async () => {
    let app = new Application();
    let controller = new AppController();
    
    let currentPage = new Page({name: 'tasks'});
    
    currentPage.state.workorder = {
      name: "sample"
    };
    app.registerPage(currentPage);
    let schedulePage = new Page({name: 'schedule'});
    app.registerPage(schedulePage);
    app.registerController(controller);
    await app.initialize();
    await app.emit('on-app-serialize', {app});
    
    expect(app.currentPage.state.workorder).toBe(undefined);

  });

  it('checks if implementation setting for LocalStorageManager in MaximoMobile is successful', async () => {
    StorageManager.setImplementation = jest.fn();
    const controller = new AppController ();
    const app = new Application ();
    Device.get ().isMaximoMobile = true;
    app.registerController (controller);
    await app.initialize ();
    expect(StorageManager.setImplementation.mock.calls.length).not.toBe(0);
    Device.get ().isMaximoMobile = false;
  });

  it('test should call _getStatusDescription()', async () => {
    const controller = new AppController();
    const app = new Application();
    app.registerController(controller);
    await app.initialize();
    let item = [{ description: 'Completed', defaults: true, value: 'COMP', maxvalue: 'COMP' }];
    let data = controller._getStatusDescription(item, 'COMP');
    expect(data).toBe('Completed');
  });

  it('test should call checkAssistPermission()', async () => {
    const controller = new AppController();
    const app = new Application();
    app.registerController(controller);
    await app.initialize();
    localStorage.setItem('masApiUrl', 'https://localhost:3001');
    await controller.checkAssistPermission();
    expect(app.state.isAssistAccessible).toBe(false);
  });

  // Assisted by WCA@IBM
  // Latest GenAI contribution: ibm/granite-20b-code-instruct-v2
  it('test should call registerAssistUrl()', async () => {
    const controller = new AppController();
    const app = new Application();
    app.registerController(controller);
    await app.initialize();
    let assistUrl = 'https://localhost:3001/';
    controller.registerAssistUrl(assistUrl);
    assistUrl = 'https://localhost:3001';
    controller.registerAssistUrl(assistUrl);
  });
  
  // Assisted by WCA@IBM
  // Latest GenAI contribution: ibm/granite-20b-code-instruct-v2
  it('test should call checkWorkspacesForAssitst', async () => {
    const controller = new AppController();
    const app = new Application();
    app.registerController(controller);
    await app.initialize();
    let workspaces = {
      workspaceId: {
        applications: {
          assist: {
            href: 'https://localhost:3001',
            role: 'ACCESS',
          }
        }
      },
    }
    controller.checkWorkspacesForAssist(workspaces);
    controller.checkWorkspacesForAssist({});
  });


  it('complete the task', async () => {
    
    const controller = new AppController();
    const app = new Application();
    const page = new Page({name: 'tasks'});
    let item = [{ taskid: '10',  status: 'INPRG', maxvalue: 'INPRG'}];
    app.registerPage(page);
    app.registerController(controller);
    await app.initialize();

    page.callController = mockedFn;
    page.completeTheTask = mockedFn;
    controller.completeTheTask(item);
    expect(page.completeTheTask.mock.calls.length).toBe(1);
  });

  it('goto assist app', async () => {
    const controller = new AppController();
    const app = new Application();
    const page = new Page({name: 'tasks'});
    const wolistds = newDatasource(workorderitem, 'wolistds', 'member');
    app.registerDatasource(wolistds);
    app.registerPage(page);
    app.registerController(controller);
    await app.initialize();
    const event = {
      item: wolistds.item,
    };
    controller.loadApp = mockedFn;
    app['state'] = {
      appnames : {
        assist : 'assist',
      }
    }
    controller.gotoAssistAppFromTask(event);
    expect(controller.loadApp.mock.calls.length).toBe(1);
  });

  it('open task long desc', async () => {
    const controller = new AppController();
    const app = new Application();
    const page = new Page({name: 'tasks'});
    let item = [{ taskid: '10',  status: 'INPRG', maxvalue: 'INPRG'}];
    app.registerPage(page);
    app.registerController(controller);
    await app.initialize();

    page.callController = mockedFn;
    page.openTaskLongDesc = mockedFn;
    controller.openTaskLongDesc(item);
    expect(page.openTaskLongDesc.mock.calls.length).toBe(1);
  });

  it('should open the followUp Drawer', async () => {
    const controller = new AppController();
    const page = new Page({name: 'woedit'});
    const workOrderDetailsPage = new Page({name: 'workOrderDetails'});

    let app = new Application({      
      state: {
        isRejected: false
      },
      setCurrentPage: jest.fn(),
      toast: jest.fn(),
      findPage : jest.fn(() => ({
        findDatasource: jest.fn(() => ({
          forceReload: jest.fn(() => workorderitem),
          getChildDatasource: jest.fn(() => ({
            load: jest.fn(() => workorderitem),
            save: jest.fn(),
          })),
          item: workorderitem.member[0],
        })),
      })),
      currentPage : {callController: ()=>{'loadRecord'}}
    });
    const woDetailResourceds = newWoDatasource(workorderitem, 'woDetailds');
    workOrderDetailsPage.registerDatasource(woDetailResourceds);
    app.registerPage(workOrderDetailsPage);

    app.registerController(controller);
    app.registerPage(page);
    await app.initialize();
 
    const invokeLoadRecord = sinon.stub(app.currentPage, 'callController');
  
    let evt = {
      item: workorderitem.member[0],
    };
  
    await controller.openMultiAssetFollowupPage(evt);
    expect(app.currentPage.callController).toBeDefined();
    expect(invokeLoadRecord.called).toBe(false);

  })

  it('should checkDownPrompt', async () => {    
    const controller = new AppController();
    const app = new Application();
    const page = new Page({name: 'schedule'});
    app.registerPage(page);
    app.registerController(controller);
    await app.initialize();

    let evt ={
      workorder: {
        downprompt: '1',
      },
      page: page
    }
    controller.checkDownPrompt(evt);
    expect(controller.checkDownPrompt(evt)).toBe(true);

    evt.workorder.downprompt = '0';
    expect(controller.checkDownPrompt(evt)).toBe(false);
  });

  it('test if assetToOpen is setting to blank if navigating from workorderdetails page', async () => {
    let app = new Application();
    let controller = new AppController();
    
    let nextPage = new Page({name: 'schedule'});
    let prevPpage = new Page({name: 'workOrderDetails'});
    app.registerPage(prevPpage);
    app.registerPage(nextPage);
    app.registerController(controller);
    
    await app.initialize();
    await app.emit('page-changing', nextPage, prevPpage);
    nextPage.callController = mockedFn;
    nextPage.setDefaults = mockedFn;
    nextPage.callController('setDefaults');
    expect(nextPage.setDefaults.mock.calls.length).toBe(1);
    expect(prevPpage.state.assetToOpen).toEqual('');
  });

  it('loadApp should invoke appswitcher method', async () => {
    const controller = new AppController();
    const app = new Application();
    app.registerController(controller);
    await app.initialize();
    AppSwitcher.setImplementation(MaximoAppSwitcher, {app: app});
    let switcher = AppSwitcher.get();
    const gotoApplication = sinon.spy(switcher, 'gotoApplication');

    // calling with argument data
    controller.loadApp({appName: 'inspection', options:{}, context: {}});
    sinon.assert.callCount(gotoApplication, 1);

    // calling without appName
    controller.loadApp();
    sinon.assert.callCount(gotoApplication, 1);

    // calling with appName but without options and context data
    controller.loadApp({appName: 'inspection'});
    sinon.assert.callCount(gotoApplication, 2);
  });

  it('should call gotoAssistAppFromTask', async () => {
    let mockSetPage = jest.fn();
    global.open = jest.fn();

    const controller = new AppController();
    const woController = new WorkOrderDetailsController();
    const woDetailsPage = new Page({name: 'workOrderDetails'});
    const page = new Page({name: 'report_work'});
    const gotoAssistApp = sinon.spy(controller, 'gotoAssistAppFromTask');
    const loadAppStub = sinon.spy(controller, 'loadApp');

    const woDetailResourceds = newWoDatasource(workorderitem, 'woDetailResource');
    woDetailsPage.registerDatasource(woDetailResourceds);
    await woDetailResourceds.load();
  
    const woAssetLocationds = newAssetLocationDatasource(workorderitem, 'woAssetLocationds');
    woDetailsPage.registerDatasource(woAssetLocationds);
    await woAssetLocationds.load();
  
    const locDs = newLocDS(workorderitem, 'woLocationds');
    woDetailsPage.registerDatasource(locDs);
    await woAssetLocationds.load();

    const app = new Application();
    const taskDS = newTaskDatasource(tasklist, 'woPlanTaskDetailds');
    const woDetailDS = newWoDatasource(workorderitem, 'woDetailds');    
    app.registerController(controller);
    app.registerController(woController);
    app.registerPage(woDetailsPage);
    app.registerPage(page);
    page.state.fieldChangedManually = false;

    await app.initialize();
    AppSwitcher.setImplementation(MaximoAppSwitcher, {app: app});
    let switcher = AppSwitcher.get();
    const gotoApplication = sinon.spy(switcher, 'gotoApplication');

    app.setCurrentPage = mockSetPage;
    app.registerDatasource(taskDS);
    app.registerDatasource(woDetailDS);

    app.setCurrentPage = mockSetPage;
    app['state'] = {
      appnames : {
        assist : 'assist',
      }
    }

    controller.gotoAssistAppFromTask({ item: {
      '_rowstamp': '1391348',
      'inspectionresult': [
        { 'inspresult': '1005' }
      ]
    }});
    sinon.assert.callCount(gotoAssistApp, 1);
    sinon.assert.callCount(loadAppStub, 1);
    sinon.assert.callCount(gotoApplication, 1);

    let tasks = await taskDS.load();
    expect(tasks.length).toBe(9);
    controller.gotoAssistAppFromTask({});
    sinon.assert.callCount(loadAppStub, 2);
    sinon.assert.callCount(gotoAssistApp, 2);
    sinon.assert.callCount(gotoApplication, 2);

    await woDetailDS.load();
	  woDetailDS.item['_rowstamp'] = '738138';
    controller.gotoAssistAppFromTask({ item: taskDS.items[0] });
    sinon.assert.callCount(loadAppStub, 3);
    sinon.assert.callCount(gotoAssistApp, 3);
    sinon.assert.callCount(gotoApplication, 3);

    woDetailsPage.controllers.push(woController);
    controller.gotoAssistAppFromTask({ item: taskDS.items[0] });
    sinon.assert.callCount(loadAppStub, 4);
    sinon.assert.callCount(gotoAssistApp, 4);
    sinon.assert.callCount(gotoApplication, 4);

  });

  it('should checkDownPrompt', async () => {    
    const controller = new AppController();
    const app = new Application();
    app.registerController(controller);
    await app.initialize();
    const setupIncomingContext = sinon.spy(controller, 'setupIncomingContext');
    controller.onContextReceived();
    sinon.assert.callCount(setupIncomingContext, 1);
  });

  it('Should call MAS apps permissions on MAS environment', async () => {
    const controller = new AppController();
    let checkAssistPermissionSpy = sinon.stub(controller, 'checkAssistPermission');
    let app = null;

    //EAM
    sessionStorage.setItem('isEamApp', true);
    app = new Application();

    app.registerController(controller);
    await app.initialize();

    sinon.assert.notCalled(checkAssistPermissionSpy);

    //MAS
    sessionStorage.setItem('isEamApp', false);
    app = new Application();

    app.registerController(controller);
    await app.initialize();

    sinon.assert.calledOnce(checkAssistPermissionSpy);

    sessionStorage.removeItem('isEamApp');
  });

  it("should validate getWoActivity", async () => {
    const controller = new AppController();
    const app = new Application();
    const page = new Page({ name: "schedule" });
    Device.get ().isMaximoMobile = false;
    const dsworktype = newStatusDatasource(worktype, "dsworktype");
    const synonymdomainData = newStatusDatasource(
      statusitem,
      "synonymdomainData"
    );

    app.registerDatasource(synonymdomainData);
    app.registerDatasource(dsworktype);
    sinon
      .stub(SynonymUtil, "getSynonymDomain")
      .returns({ value: "APPR", maxvalue: "APPR", description: "APPR" });
      sinon.stub(SynonymUtil, 'getDefaultExternalSynonymValue').returns('CAN');
    app.registerPage(page);
    app.registerController(controller);
    await dsworktype.load();
    await app.initialize();
    page.state.selectedStatus = "APPR";
    page.state.selectedStatusMaxValue = "APPR";
    page.state.selectedStatusDescription = "APPR";
    let wods = {
      status_maxvalue: "APPR",
      status_description: "Approved",
      taskid: 10,
      status: "APPR",
      flowcontrolled: true,
      predessorwos: true,
      woactivity: [
        {
          status_maxvalue: "APPR",
          status_description: "Approved",
          taskid: 10,
          predessorwos: false,
          status: "APPR",
        },
      ],
      worktype: 'CM'
    };
    let data = await controller.getWoActivity(page, app, wods);
    expect(data.length).not.toBeNull();
  });

  it("should validate validatePredessor", async () => {
    const controller = new AppController();
    const app = new Application();
    const page = new Page({ name: "tasks" });
    app.registerPage(page);
    app.registerController(controller);
    await app.initialize();
    let taskList = [{
      status_maxvalue: "COMP",
      status_description: "Completed",
      description: "Check tires, lights, horn, mirrors.",
      taskid: 10,
      status: "COMP"
    },
    {
      status_maxvalue: "APPR",
      status_description: "Completed",
      description: "Check windshield wipers and steering.",
      taskid: 20,
      status: "APPR",
      predessorwos:'10,30'
    },
    {
      status_maxvalue: "APPR",
      status_description: "Completed",
      description: "Check windshield wipers and steering.",
      taskid: 30,
      status: "APPR",
      predessorwos:'(10,20)'
    }];
    let data = controller.validatePredessor(taskList, taskList[0]);
    expect(data).toBe(false);

    data = controller.validatePredessor(taskList, taskList[1]);
    expect(data).toBe(false);


    data = controller.validatePredessor(taskList, taskList[2]);
    expect(data).toBe(false);
  });


  it('should return just title', async () => { 
    let mockSetPage = jest.fn();
	  global.open = jest.fn();
    const controller = new AppController();
    const app = new Application();
    const page = new Page({
        name: 'Tasks'
    });

    app.setCurrentPage = mockSetPage;
    app.registerPage(page);

    app.registerController(controller);
    await app.initialize();


    const title = controller.updatePageTitle(
      {page: page, label: 'tasks_title', labelValue: 'Tasks'}
    );
    expect(title.trim()).toBe('Tasks');

  });

  it('should return title with work order number', async () => { 
    let mockSetPage = jest.fn();
	  global.open = jest.fn();
    const controller = new AppController();
    const app = new Application();
    const page = new Page({
        name: 'Tasks'
    });
    page.params = {
      wonum: '001',
    }

    app.setCurrentPage = mockSetPage;
    app.registerPage(page);

    app.registerController(controller);
    await app.initialize();

    const title = controller.updatePageTitle(
      {page: page, label: 'tasks_title', labelValue: 'Tasks'}
    );
    expect(title.trim()).toBe('001 Tasks');

  });

  it('should return title with wonum ', async () => { 
    let mockSetPage = jest.fn();
	  global.open = jest.fn();
    const controller = new AppController();
    const app = new Application();
    const page = new Page({name: 'report_work'});
    page.params = {
      wonum: '001',
    }

    app.setCurrentPage = mockSetPage;
    app.registerPage(page);

    app.registerController(controller);
    await app.initialize();

    const title = controller.updatePageTitle(
      {page: page, label: 'report_work_title', labelValue: 'Report work'}
    );
    expect(title.trim()).toBe('001 Report work');

  });

  it('should return title with parentid-taskid ', async () => { 
    let mockSetPage = jest.fn();
	  global.open = jest.fn();
    const controller = new AppController();
    const app = new Application();
    const page = new Page({name: 'report_work'});
    page.params = {
      wonum: '001',
      istask: true,
      wogroup: '0002',
      taskid: '002'
    }

    app.setCurrentPage = mockSetPage;
    app.registerPage(page);

    app.registerController(controller);
    await app.initialize();

    const title = controller.updatePageTitle(
      {page: page, label: 'report_work_title', labelValue: 'Report work'}
    );
    expect(title.trim()).toBe('0002-002 Report work');

  });

  it('should return default title ', async () => { 
    let mockSetPage = jest.fn();
	  global.open = jest.fn();
    const controller = new AppController();
    const app = new Application();
    const page = new Page({name: 'report_work'});
   
    app.setCurrentPage = mockSetPage;
    app.registerPage(page);

    app.registerController(controller);
    await app.initialize();

    const title = controller.updatePageTitle(
      {page: page, label: 'report_work_title', labelValue: 'Report work', wogroup: '100', taskid: '12', istask: false}
    );
    expect(title.trim()).toBe("Report work");

  }); 

  it("Should Call scanActions", async() => {
    const controller = new AppController();
    const app = new Application();
    const page = new Page({ name: "schedule" });
    app.registerController(controller);
    app.registerPage(page);
    await app.initialize();
    app.state.scanParameter = { page: page, method: 'startStopTimer', scanResParam: { value: '123456', scanResCode: 'nomatch'}}
    page.findDialog = jest.fn().mockReturnValue({
      callController: jest.fn(),
    });
    page.callController = jest.fn().mockImplementation(() => page);

    jest.spyOn(WOTimerUtil, 'startStopTimer').mockImplementation(() => app);
    await controller.scanActions();
    expect(app.findDialog('appAssetMisMatchDialog')).toBeNull();

    app.state.scanParameter = { page: page, method: 'changeStatus', scanResParam: { value: '123456', scanResCode: 'nomatch'}}
    await controller.scanActions();
    expect(app.findDialog('appAssetMisMatchDialog')).toBeNull();

    app.state.scanParameter = { page: page, method: 'completeWorkorder', scanResParam: { value: '123456', scanResCode: 'nomatch'}}
    await controller.scanActions();
    expect(app.findDialog('appAssetMisMatchDialog')).toBeNull();

    app.state.scanParameter = { page: page, method: 'completeWoTask', scanResParam: { value: '123456', scanResCode: 'nomatch'}}
    await controller.scanActions();
    expect(app.findDialog('appAssetMisMatchDialog')).toBeNull();
    
  });

  it("Should call onAssetScan", async() => {
    const controller = new AppController();
    const app = new Application();
    const page = new Page({ name: "schedule" });
    app.registerController(controller);
    app.registerPage(page);
    app.findDialog = jest.fn().mockReturnValue({
      closeDialog: jest.fn()
    });
    app.showDialog = jest.fn();
    await app.initialize();
    app.state.scanParameter = { page: page, method: 'startStopTimer', scanResParam: { scanValue: null, assetnum: "123", locationnum: "123", status: 'INPRG' }}
    await controller.onAssetScan({ value: "123456"});
    expect(app.state.scanParameter.scanResParam.scanValue).toEqual({ value: "123456"});

    await controller.onAssetScan({ value: "123"});
    expect(app.state.skipScan).toBeFalsy();

    controller.closeMismatchAssetScan();
    expect(app.findDialog('appAssetMisMatchDialog')).toBeDefined();

    controller.skipAssetScan();
    expect(app.state.skipScan).toBeTruthy();
  });

  it("Should call navigateToAcm", async() => {
    const controller = new AppController();
    const app = new Application();
    app.registerController(controller);
    await app.initialize();
    AppSwitcher.setImplementation(MaximoAppSwitcher, {app: app});
    let switcher = AppSwitcher.get();
    app['state'] = {
      appnames : {
        assetswitch : 'plusassetswitch',
      }
    }
    const gotoApplication = sinon.spy(switcher, 'gotoApplication');
    controller.navigateToAcm({item: {assetnum: "123", href: "abc"}});
    sinon.assert.callCount(gotoApplication, 1);
  });


  it("Should call redirectToReport", async() => {
    const controller = new AppController();
    const app = new Application();
    const page = new Page({name: 'workOrderDetails'});
    app.registerPage(page);
    app.state = {
      calibParameter: {
        workorder: {
          href: 'abc',
          wonum: '1111',
          siteid: 'BEDFORD'
        }
      }
    }
    app.registerController(controller);
    await app.initialize();
    app.setCurrentPage = mockedFn;
    await controller.redirectToReport();
    expect(mockedFn.mock.calls.length).toBe(1);
  });

  it("Should call redirectCalibration", async() => {
    const controller = new AppController();
    const app = new Application();
    const page = new Page({name: 'workOrderDetails'});
    app.registerPage(page);
    app.state = {
      calibParameter: {
        workorder: {
          href: 'abc',
          wonum: '1111',
          siteid: 'BEDFORD'
        }
      }
    }
    app.registerController(controller);
    await app.initialize();
    app.setCurrentPage = mockedFn;
    await controller.redirectCalibration();
    expect(mockedFn.mock.calls.length).toBe(1);
  });
  
});


it("Should Call saveWorkOrderStatus", async() => {
  const controller = new AppController();
  const app = new Application();
  const page = new Page({ name: "schedule" });
  const reportPage = new Page({ name: "report_page" });
  app.registerController(controller);
  app.registerPage(page);
  await app.initialize();
  app.state.calibParameter = { page: reportPage, app: app, pageName: 'report_work', method: "completeWorkorder", params: {}};
  page.findDialog = jest.fn().mockReturnValue({
    callController: jest.fn(),
  });
  page.callController = jest.fn().mockImplementation(() => page);
  await controller.saveWorkOrderStatus();
  expect(app.findDialog('toolsWarnings')).toBeNull();

  app.state.calibParameter = { page: page, app: app, pageName: 'changeStatus', method: "changeStatus", params: {}};
  await controller.saveWorkOrderStatus();
  expect(app.findDialog('toolsWarnings')).toBeNull();
  
});


it("Should Call resetSkipState", async() => {
  const controller = new AppController();
  const app = new Application();
  const page = new Page({ name: "schedule" });
  app.registerController(controller);
  app.registerPage(page);
  await app.initialize();
  controller.resetSkipState();
  expect(app.state.skipSignature).toBe(false);
  expect(app.state.skipToolWarning).toBe(false);
  expect(app.state.disableScan).toBe(false);
  expect(app.state.skipScan).toBe(false);
});

// Assisted by watsonx Code Assistant 
describe('confirmDialogCloseClick', () => {
  it('should call onCloseClick on confirmDialog', () => {
    const controller = new AppController();
    const app = new Application();
    const page = new Page({ name: "schedule" });
    app.state = {
      confirmDialog: {
        onCloseClick: jest.fn()
      }
    }
    app.registerController(controller);
    app.registerPage(page);
    controller.confirmDialogCloseClick(app);
    expect(app.state.confirmDialog.onCloseClick).toHaveBeenCalled();
  });
});

// Assisted by watsonx Code Assistant 
describe('openMeterReadingDrawer', () => {
  it('should call openMeterReadingDrawer', async () => {
    const controller = new AppController();
    const app = new Application(
      {
        state : {
          meterReading : {}
        }
      }
    );
    const page = new Page({ name: "schedule" });
    app.state = {
      confirmDialog: {
        onCloseClick: jest.fn()
      }
    }

    const event = {
      item : {
        assetnum : "11430",
        locationnum : 'BR430',
        locationmetercount : "2",
        assetmetercount : '1',
        status_maxvalue : 'APPR'
      }
    }
    const meterReadingDrawerSpyOn = jest.spyOn(WOUtil, 'openMeterReadingDrawer');
    app.registerController(controller);
    app.registerPage(page);
    await app.initialize();
    controller.openMeterReadingDrawer(event);
    expect(meterReadingDrawerSpyOn).toHaveBeenCalled();
  });
});
