import _ from 'lodash';
import { log, Device } from '@maximo/maximo-js-api';
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


const getSystemProp = (app, systemPropName) => { return app.state?.systemProp?.[systemPropName]; };
const checkSystemProp = async (app, systemPropName, propValue = "1") => { return app.state?.systemProp?.[systemPropName] === propValue; }

const checkSysPropArrExist = async (app, systemPropName, statusComp, returnType = true) => {
    const sigCheck = await getSystemProp(app, systemPropName);
    const allowedSignature = sigCheck? sigCheck.split(',').map((status) => status.trim()): [];
    const addEsig = allowedSignature.length > 0 && allowedSignature.indexOf(statusComp) > -1;
    if (returnType) {
        return !!(addEsig);
    } else {
        return (addEsig) ? 1 : 0;
    }
}

const getOfflineStatusList = async (app, orgId, siteId) => {
    let filteredDomainValues = [];
    const synonymDomainsDS = app.findDatasource('synonymdomainData');
    
    if (!app.state.offlineStatusList?.length) {
      await synonymDomainsDS.initializeQbe();
      synonymDomainsDS.setQBE('domainid', 'POSTATUS')
      synonymDomainsDS.setQBE('orgid', orgId);
      synonymDomainsDS.setQBE('siteid', siteId);

      filteredDomainValues = await synonymDomainsDS.searchQBE();

      // istanbul ignore next
      if (!app.state.offlineStatusList?.length && filteredDomainValues && filteredDomainValues.length < 1) {
        synonymDomainsDS.setQBE('domainid', 'POSTATUS');
        synonymDomainsDS.setQBE('orgid', '=', orgId);
        synonymDomainsDS.setQBE('siteid', '=', 'null');
        filteredDomainValues = await synonymDomainsDS.searchQBE();

        // istanbul ignore next
        if (!app.state.offlineStatusList?.length && filteredDomainValues && filteredDomainValues.length < 1) {
          synonymDomainsDS.setQBE('domainid', 'POSTATUS');
          synonymDomainsDS.setQBE('orgid', '=', 'null');
          synonymDomainsDS.setQBE('siteid', '=', 'null');
          filteredDomainValues = await synonymDomainsDS.searchQBE();
        }
      }
      // istanbul ignore else
      if (!app.state.offlineStatusList?.length) {
        app.state.offlineStatusList = filteredDomainValues;
      }
      synonymDomainsDS.clearQBE();
    }
    return app.state.offlineStatusList;
}

const getOfflineAllowedStatusList = async (app, event) => {
    const statusList = await getOfflineStatusList(app, event.item.orgid, event.item.siteid);
    const statusArr = [];
    statusList.forEach((element) => {
        const isValidTransition = isAllowedStatus(event.item.status_maxvalue, element.maxvalue);
        if (element.value && element.value !== event.item.status && isValidTransition) {
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

/** 
 * This method checks status transition based on internal value.
 * @param { from } from from status
 * @param { to } to to status
*/
const isAllowedStatus = (from, to) => {
    let transitionMatrix = {
      WAPPR: ['WAPPR','CAN','INPRG','CLOSE','APPR','HOLD','PNDREV','REVISE'],
      APPR: ['CAN','INPRG','CLOSE',,'APPR','HOLD','PNDREV','REVISE'],
      INPRG: ['WAPPR','INPRG','CLOSE','HOLD','PNDREV','REVISE'],
      HOLD: ['WAPPR','CAN','INPRG','CLOSE','HOLD','PNDREV','REVISE'],
      PNDREV: ['WAPPR','REVISE','CLOSE','HOLD','CAN','PNDREV'],
      CLOSE: ['CLOSE'],
      CAN: ['CAN']
    };

    if (!transitionMatrix[from] || transitionMatrix[from].indexOf(to) < 0) {
      return false;
    } else {
      return true;
    }
}

const _resetDataSource = (ds) =>{
  ds.clearState();
  ds.resetState();
}

const filterMobileMaxvars = (varname, defDS) => {
  const typeHierarchy = ["SITE", "ORG", "SYSTEM"];
  let MaxVar = [];
  // istanbul ignore next
  if (defDS?.items?.length) {
    MaxVar = defDS.items[0]?.mobilemaxvars
      ?.filter((item) => item.varname === varname && typeHierarchy.includes(item.vartype))
      .sort((a, b) => {
        return typeHierarchy.indexOf(a.vartype) - typeHierarchy.indexOf(b.vartype);
      });
  }
  return MaxVar;
}

const getConfirmDialogLabel = (app, config, dynamicLabel = []) => {
  app.state.confirmDialog = {
    title: config.title ? app.getLocalizedLabel(config.title.label, config.title.value) : '',
    label1: config.confirmDialogLabel1 ? app.getLocalizedLabel(config.confirmDialogLabel1.label, config.confirmDialogLabel1.value) : '',
    label2: config.confirmDialogLabel2 ? app.getLocalizedLabel(config.confirmDialogLabel2.label, config.confirmDialogLabel2.value, dynamicLabel) : '',
    acceptButton: config.confirmDialogAcceptButton ? app.getLocalizedLabel(config.confirmDialogAcceptButton.label, config.confirmDialogAcceptButton.value) : '',
    acceptIcon: config.confirmDialogAcceptIcon || '',
    acceptKind: config.confirmDialogAcceptKind || 'Primary',
    rejectIcon: config.confirmDialogRejectIcon || '',
    rejectButton: config.confirmDialogRejectButton ? app.getLocalizedLabel(config.confirmDialogRejectButton.label, config.confirmDialogRejectButton.value) : '',
    onPrimaryClick: config.onPrimaryClick,
    onSecondaryClick: config.onSecondaryClick,
    onCloseClick: config.onCloseClick
  }
  sharedData.showConfirmDialog = true;
}

const clearSharedData = (propertyName) => {
  if(sharedData[propertyName]) {
    delete sharedData[propertyName];
  }
}

// use below variable to share data app wide, note it's const so you can push key name but don't redeclare it
const sharedData = {
  isCardOpen: false,
  shouldReloadDatasource: false,
  prop1: 'value1',
  clickedPo: false
}

const functions = {
    getSystemProp,
    checkSystemProp,
    checkSysPropArrExist,
    getOfflineStatusList,
    getOfflineAllowedStatusList,
    isAllowedStatus,
    _resetDataSource,
    filterMobileMaxvars,
    getConfirmDialogLabel,
    clearSharedData,
    sharedData
};
  
export default functions;
  