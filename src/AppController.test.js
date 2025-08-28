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

import 'regenerator-runtime/runtime';
import AppController from './AppController';
import { Application, Datasource, JSONDataAdapter, Page, Device, AppSwitcher, MaximoAppSwitcher, DisconnectedSchemaFactory } from '@maximo/maximo-js-api';
import statusitem from './SharedResources/Technician/statuses-json-data.js';
import domainitem from './SharedResources/Technician/domain-json-data.js';
import worktype from "./SharedResources/Technician/worktype-json-data";
import workorderitem from './SharedResources/Technician/wo-failure-report-json-data';
import attachmentlistitem from "./SharedResources/Technician/test-attachment-data.js";
import SynonymUtil from "./SharedResources/Technician/utils/SynonymUtil";
import StorageManager from "@maximo/map-component/build/ejs/framework/storage/StorageManager";

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

  it('should test _buildPoStatusSet()', async () => {
    const controller = new AppController();
    const app = new Application();
    app.registerController(controller);
    await app.initialize();
    let item = {
        allowedstates: {
            COMP: [{ description: 'Completed', value: 'COMP', maxvalue: 'COMP' }]
        },
    };
    let data = controller._buildPoStatusSet(item.allowedstates);
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

  it ('it should test _isValidTransitionMaxVal()', async () => {
    const controller = new AppController ();
    const app = new Application ();
    let orginalDevice = Device.get ().isAnywhere;
    Device.get ().isAnywhere = true;
    app.registerController (controller);
    await app.initialize ();

    let isValidTransition = controller._isValidTransitionMaxVal ('APPR', 'CAN');
    expect (isValidTransition).toBe (true);
    
    isValidTransition = controller._isValidTransitionMaxVal ('APPR', 'WAPPR');
    expect (isValidTransition).toBe (true);

    isValidTransition = controller._isValidTransitionMaxVal ('INPRG', 'CAN');
    expect (isValidTransition).toBe (false);

    Device.get ().isAnywhere = orginalDevice;
  });

  it('it should get the external value of the synonymdomain of WO status', async () => {
    const controller = new AppController();
    const app = new Application();
    app.registerController(controller);
    const polistds = newDatasource(workorderitem, 'polistds', 'member');
    app.registerDatasource(polistds);

    const dsDomain = newStatusDatasource(domainitem, 'dsdomains');
    const ds = newStatusDatasource(statusitem, 'synonymdomainData');

    app.registerDatasource(ds);
    app.registerDatasource(dsDomain);

    await polistds.load();
    await app.initialize();
    const event = {
      item: polistds.item,
    };
    let option = {
      datasource: polistds,
      event: event
    };

    let statusList = await controller.getOfflineWoStatusList(option.event);
    expect(controller._getStatusExternalValue(statusList, 'INPRG')).toEqual('INPRG');
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
      ponum: 1022,
      siteid: 'BEDFORD',
      href: 'oslc/os/mxapiwodetail/_QkVERk9SRC8xMjAx',
      breadcrumb: {
        returnName: 'Returning to inspection',
        enableReturnBreadcrumb: true
      }
    };
    await app.initialize();
    expect(mockSetPage.mock.calls[0][0].name).toBe('approvals');
    expect(mockSetPage.mock.calls[1][0].name).toBe('workOrderDetails');
    expect(mockSetPage.mock.calls[1][0].params.ponum).toEqual(1022);
    expect(mockSetPage.mock.calls[1][0].params.siteid).toEqual('BEDFORD');
    expect(mockSetPage.mock.calls[1][0].params.href).toEqual('oslc/os/mxapiwodetail/_QkVERk9SRC8xMjAx');
    app.setCurrentPage = origSetPage;
  });

  it('test the openPrevPage function has been called', async () => {
    let app = new Application();
    let controller = new AppController();
    
    let page = new Page({name: 'approvals'});
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

  it('test the page-changing event is fired on application initialization', async () => {
    let app = new Application();
    let controller = new AppController();
    
    let nextPage = new Page({name: 'approvals'});
    let prevPpage = new Page({name: 'approvals'});
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

  it('checks if implementation setting for LocalStorageManager in MaximoMobile is successful', async () => {
    StorageManager.setImplementation = jest.fn();
    const controller = new AppController ();
    const app = new Application ();
    Device.get ().isMaximoMobile = true;
    app.registerController (controller);
    await app.initialize ();
    expect(StorageManager.setImplementation.mock.calls.length).toEqual(2);
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

  it("Should Call savePurchaseOrderStatus", async() => {
    const controller = new AppController();
    const app = new Application();
    const page = new Page({ name: "schedule" });
    app.registerController(controller);
    app.registerPage(page);
    await app.initialize();
    page.findDialog = jest.fn().mockReturnValue({
      callController: jest.fn(),
    });
    page.callController = jest.fn().mockImplementation(() => page);
    await controller.savePurchaseOrderStatus();
  
    app.state.calibParameter = { page: page, app: app, pageName: 'changeStatus', method: "changeStatus", params: {}};
    await controller.savePurchaseOrderStatus();
    
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

});

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

describe('confirmDialogPrimaryClick', () => {
  it('should call onPrimaryClick on confirmDialog', () => {
    const controller = new AppController();
    const app = new Application();
    const page = new Page({ name: "schedule" });
    app.state = {
      confirmDialog: {
        onPrimaryClick: jest.fn()
      }
    };

    app.registerController(controller);
    app.registerPage(page);

    controller.confirmDialogPrimaryClick.call({ app });

    expect(app.state.confirmDialog.onPrimaryClick).toHaveBeenCalledWith(app);
  });
});

describe('confirmDialogSecondaryClick', () => {
  it('should call onSecondaryClick on confirmDialog', () => {
    const controller = new AppController();
    const app = new Application();
    const page = new Page({ name: "schedule" });
    app.state = {
      confirmDialog: {
        onSecondaryClick: jest.fn()
      }
    };

    app.registerController(controller);
    app.registerPage(page);

    controller.confirmDialogSecondaryClick.call({ app });

    expect(app.state.confirmDialog.onSecondaryClick).toHaveBeenCalledWith(app);
  });
});
