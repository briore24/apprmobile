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

/* eslint-disable no-console */
import {log, Device, AppSwitcher} from '@maximo/maximo-js-api';
import appResolver from './SharedResources/utils/AppResolver';

const TAG = 'ApprovalsApp';

class AppController {
  applicationInitialized(app) {
    this.app = app;
    // Variable to hide features which are not required for supervisor release.
    this.app.state.hideFeature = true;
    if (Device.get().isMaximoMobile) {
			//Update the Default Storage Manager
			StorageManager.setImplementation(FileSystemStorageManager);
		} else {
			//Update the Default Storage Manager
			StorageManager.setImplementation(LocalStorageManager);
    }
       
    this.setupIncomingContext();    
    // Going back to list page from detail page
    this.app.on('page-changed', (nextPage, prevPage) => {
      if (prevPage?.name === 'poDetails' && nextPage?.name === 'approvals' && nextPage.state?.previousPage === 'schedulecardlist' && nextPage.state.mapOriginPage === 'podetail') {
        nextPage.callController("openPrevPage");
      }
    });
    // Open schedule page from navigator and set default state
    this.app.on('page-changing', (nextPage, prevPage) => {
      if (prevPage?.name === 'approvals' && nextPage?.name === 'approvals') {
        nextPage.callController('setDefaults');
      }
    });
    // Set application reference to be used globally
    appResolver.setApplication(app);
  }

  onContextReceived() {
    this.setupIncomingContext()
  }

  navigationItemClicked(item) {
    log.t(TAG, 'Clicked item', item);
    this.app.setCurrentPage({name: 'approvals'});
  }

  async getOfflinePoStatusList(evt) {
    let statusArr = [];
    let defOrg = evt.item.orgid;
    let defSite = evt.item.siteid;
    let synonymDomainsDS = this.app.findDatasource('synonymdomainData');

    await synonymDomainsDS.initializeQbe();
    synonymDomainsDS.setQBE('domainid', 'POSTATUS')
    synonymDomainsDS.setQBE('orgid', defOrg);
    synonymDomainsDS.setQBE('siteid', defSite);

    let filteredDomainValues = await synonymDomainsDS.searchQBE();

    // istanbul ignore next
    if (filteredDomainValues && filteredDomainValues.length < 1) {
      synonymDomainsDS.setQBE('orgid', '=', defOrg);
      synonymDomainsDS.setQBE('siteid', '=', 'null');
      filteredDomainValues = await synonymDomainsDS.searchQBE();

      // istanbul ignore next
      if (filteredDomainValues && filteredDomainValues.length < 1) {
        synonymDomainsDS.setQBE('orgid', '=', 'null');
        synonymDomainsDS.setQBE('siteid', '=', 'null');
        filteredDomainValues = await synonymDomainsDS.searchQBE();
      }
    }

    filteredDomainValues.forEach((element) => {
      // istanbul ignore next
      if (
        element.value &&
        element.value !== evt.item.status &&
        this._isValidTransitionMaxVal(
          evt.item.status_maxvalue,
          element.maxvalue
        )
      ) {
        statusArr.push({
          id: element.value,
          value: element.value,
          description: element.description,
          defaults: element.defaults,
          maxvalue: element.maxvalue,
          _bulkid: element.value
        });
      }
    });

    return statusArr;
  }

  _getStatusExternalValue(statusArr, internalValue) {
    let externalValue;
    if (statusArr && statusArr.length && internalValue) {
      for (let i = 0; i < statusArr.length; i++) {
        if (statusArr[i].defaults && statusArr[i].maxvalue === internalValue) {
          externalValue = statusArr[i].value;
          break;
        }
      }
    }
    return externalValue;
  }

  _buildPoStatusSet(allowedStates) {
    let statusArr = [];
    if (allowedStates) {
      Object.entries(allowedStates).forEach(([key, value]) => {
        // istanbul ignore else
        if (value) {
          value.forEach((statValue) => {
            statusArr.push({
              id: statValue.value,
              value: statValue.value,
              description: statValue.description,
              defaults: statValue.defaults,
              maxvalue: statValue.maxvalue,
              _bulkid: statValue.value
            });
          });
        }
      });
    }
    return statusArr;
  }

  updatePageTitle (parameters) {
    const ponum = parameters.page.params.ponum || "";
    let pageTitle = this.app.getLocalizedLabel(
        parameters.label,
        `${ponum} ${parameters.labelValue}`,
        [ponum]
      );
    return pageTitle;
  }

  _isValidTransitionMaxVal(from, to) {
    log.t(TAG, 'isValidTransition : from --> ' + from + ' to --> ' + to);
    let transitionMatrix = {
      WAPPR: [
        'WAPPR',
        'INPRG',
        'CAN',
        'WMATL',
        'COMP',
        'APPR',
        'CLOSE',
        'WSCH'
      ],
      WPCOND: ['APPR', 'CAN', 'CLOSE', 'COMP', 'INPRG', 'WAPPR', 'WMATL', 'WSCH'],
      APPR: ['APPR', 'INPRG', 'WMATL', 'COMP', 'WAPPR', 'CLOSE', 'CAN', 'WSCH'],
      WSCH: ['WSCH', 'INPRG', 'WMATL', 'COMP', 'WAPPR', 'CLOSE', 'APPR', 'CAN'],
      WMATL: ['WMATL', 'INPRG', 'COMP', 'WAPPR', 'CLOSE', 'CAN'],
      INPRG: ['INPRG', 'WMATL', 'COMP', 'WAPPR', 'CLOSE'],
      COMP: ['COMP', 'CLOSE'],
      CLOSE: ['CLOSE'],
      CAN: ['CAN']
    };

    if (!transitionMatrix[from] || transitionMatrix[from].indexOf(to) < 0) {
      // Not a valid transition ..
      log.t(TAG, 'isValidTransition : Not a valid transition .. ');
      return false;
    } else {
      // Is a valid transition ..
      log.t(TAG, 'isValidTransition : Is a valid transition .. ');
      return true;
    }
  }

// open poDetails page & set incoming context
  setupIncomingContext() {
    const incomingContext = this.app && this.app.state && this.app.state.incomingContext;

    if (incomingContext && incomingContext.editTrans) {
      incomingContext.page  = 'poDetails';
    }

    if (incomingContext && incomingContext.page && (incomingContext.ponum && incomingContext.siteid)) {
      this.app.setCurrentPage({name:"approvals"});
      this.app.setCurrentPage({
        name: incomingContext.page,
        resetScroll: true,
        params: {ponum: incomingContext.ponum, siteid: incomingContext.siteid, href: incomingContext.href }
      });
    } else if(incomingContext && incomingContext.page) {
      this.app.setCurrentPage({
        name: incomingContext.page,
        resetScroll: true,
		params: { href: incomingContext.href, itemhref: incomingContext.href }
      });
    }
  }

  _getStatusDescription(statusArr, internalValue) {
    let description;
    if (statusArr && statusArr.length && internalValue) {
      for (let i = 0; i < statusArr.length; i++) {
        if (statusArr[i].defaults && statusArr[i].maxvalue === internalValue) {
            description = statusArr[i].description;
          break;
        }
      }
    }
    return description;
  }

  loadApp(args = {}) {
    let appName = args.appName ? args.appName : undefined;
    let breadcrumbData = {returnName: `Returning to ${this.app.name}`, enableReturnBreadcrumb: true};
    if (!appName) {
      log.e(TAG,'loadApp : appName required for navigation.', args);
      return;
    }
    let options = args.options ? args.options : {canReturn: true};
    let context = args.context ? args.context : {};
    let switcher = AppSwitcher.get();
    context.breadcrumb = breadcrumbData;
    switcher.gotoApplication(appName, context, options);
  }

  async scanActions() {
    const params = this.app.state.scanParameter;
    switch(params.method) {
      case "changeStatus":
        params.page.findDialog("poStatusChangeDialog").callController(params.method, {});
        break;
      case "completePurchaseorder":
        params.page.callController(params.method, params.evt);
        break;
      case "completeLineItem":
        params.page.callController(params.method, params.record);
        break;
      default:
        break
    }
    this.app.state.scanParameter = {};
  }



  resetSkipState() {
    this.app.state.skipSignature = false;
  }
  
  confirmDialogPrimaryClick() {
    this.app.state.confirmDialog.onPrimaryClick(this.app);
  }

  confirmDialogSecondaryClick() {
    this.app.state.confirmDialog.onSecondaryClick(this.app);
  }

  confirmDialogCloseClick(app) {
    app.state.confirmDialog.onCloseClick();
  }


}
export default AppController;
