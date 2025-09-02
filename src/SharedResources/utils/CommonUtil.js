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

/**
 * Return System Property Value 
 * @param {Application} app application object
 * @param {String} systemPropName system property Name.
 */
const getSystemProp = (app, systemPropName) => {
    return app.state?.systemProp?.[systemPropName];
};

/**
 * Return true if system property value is same as expected value else return false 
 * @param {Application} app application object
 * @param {String} systemPropName system property name.
 * @param {String} propValue system property expected value. default value is 1
 */
const checkSystemProp = async (app, systemPropName, propValue = "1") => {
    return app.state?.systemProp?.[systemPropName] === propValue;
}

/**
 * Return true of false base on allowed property 
 * @param {Application} app application object
 * @param {String} systemPropName system property name for physical Sig.
 * @param {String} statusComp Complete Status name from maximo properties.
 * @param {Boolean} returnType Type of returning value , returnType True will return true/false, returnType false will return 1 or 0
 */
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

/**
 * Return the list of work order status list which is defined in synonymdomain table. 
 * if data already present in state "offlineStatusList" it will prevent repetative calls of synonymdomain
 * @param {app} app application object
 * @param {orgId} orgId Organization Id
 * @param {siteId} siteId Site Id
 */
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
/**
 * Return List of allowed status based on current status
 * @param {statusList} statusList list of offline status
 * @param {event} event datasource event object
*/
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
      WAPPR: ['COMP','WAPPR','CAN','INPRG','WSCH','CLOSE','WMATL','APPR'],
      WPCOND: ['COMP','WAPPR','CAN','INPRG','WSCH','CLOSE','WMATL','APPR'],
      APPR: ['COMP','WAPPR','CAN','INPRG','WSCH','CLOSE','WMATL','APPR'],
      WSCH: ['COMP','WAPPR','CAN','INPRG','WSCH','CLOSE','WMATL','APPR'],
      WMATL: ['COMP','WAPPR','CAN','INPRG','CLOSE','WMATL'],
      INPRG: ['COMP', 'WAPPR','INPRG','CLOSE','WMATL'],
      COMP: ['COMP', 'CLOSE'],
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

/**
 * To filter mobile maxvar for a given varname in order of SITE, ORG, SYSTEM hierarchy
 * @param {varname} of maxvars for mobile
 * @param {defDS} is defaultSetDs - Datasource
 */
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

/**
 * Common Function to open a sliding-drawer dialog to show Work Log for the purchase Order at Schedule & poDetails Page
 * @param {Application} app The application
 * @param {page} page - page name
 * @param {event} event - event containing information about current item
 * @param {workLogDS} is workLogDS - poWorklogDs from poDetails Page & poWorklogDs from Schedule Page
 * @param {drawerName} is drawer names for poDetails & Schedule Page
 */
const openWorkLogDrawer = async (app, page, event, workLogDS, drawerName) => {
  // Initialized Loader on Button when Work Log Drawer Icon Clicked
  app.state.chatLogLoading = true;
  const orgId = app.client?.userInfo?.insertOrg;
  const siteId = app.client?.userInfo?.insertSite;
  
  workLogDS.clearState();
  workLogDS.resetState();
  
  await workLogDS.load().then((response) => {
    page.state.chatLogGroupData = response;
  });

  // get work log schema 
  if (Device.get().isMaximoMobile && workLogDS.options.query.relationship) {
    workLogDS.schema =
      workLogDS.dependsOn.schema.properties[
        Object.entries(workLogDS.dependsOn.schema.properties)
          .filter(
            (item) =>
              item[1].relation &&
              item[1].relation.toUpperCase() ===
              workLogDS.options.query.relationship.toUpperCase()
          )
          .map((obj) => obj[0])[0]
      ].items;
  }

  let schemaLogType = workLogDS.getSchemaInfo("logtype");
  let schemaDescription = workLogDS.getSchemaInfo("description");
  let logTypeValue;
  //istanbul ignore else
  if (schemaLogType) {
    logTypeValue = schemaLogType.default?.replace(/!/g, "");
  }
  // Filter Logtype data from synonydomain Datasource which passed in Chat-log Component
  const synonymDs = app.findDatasource("synonymdomainData")
  let filteredLogTypeList;
  // Initalized QBE with Org and Site
  synonymDs.clearState();
  await synonymDs.initializeQbe();
  synonymDs.setQBE("domainid", "=", "LOGTYPE");
  synonymDs.setQBE('orgid', orgId);
  synonymDs.setQBE('siteid', siteId);
  filteredLogTypeList = await synonymDs.searchQBE();

  // istanbul ignore if
  if (filteredLogTypeList.length < 1) {
    synonymDs.setQBE('siteid', '=', 'null');
    filteredLogTypeList = await synonymDs.searchQBE(); 
  }

  // istanbul ignore if
  if (filteredLogTypeList.length < 1) {
    synonymDs.setQBE('orgid', '=', 'null');
    filteredLogTypeList = await synonymDs.searchQBE();
  }

  page.state.workLogItem = event.item;
  page.state.defaultLogType = "!CLIENTNOTE!";

  const logItem = synonymDs.items.find((item) => {
    return item.maxvalue === logTypeValue && item.defaults;
  });
  const logValue = logItem ? `!${logItem.value}!` : schemaLogType.default;
  page.state.defaultLogType = page.state.initialDefaultLogType = logValue;

  // set max length from schema info 
  if (schemaDescription) {
    page.state.chatLogDescLength = schemaDescription.maxLength;
  }

  page.state.chatLogLoading = false;
  page.showDialog(drawerName)
}

const saveWorkLog = async (app, page, datasource, drawer, value, directSave) => {
	let summary = value.summary;
	let longDesc = value.longDescription;
	let personId = app.client.userInfo.personid;
	let logType = value.logType?.value || page.state.defaultLogType || datasource.getSchemaInfo("logtype")?.default;
	let saveDate = new Date();
	let refid = new Date().getTime();
	
	let workLogDS = datasource;
	
	let workLog = {
		description: summary,
		description_longdescription: longDesc,
		createdate: saveDate,
		createby: personId,
		logtype: logType,
		anywherefid: refid,
		clientviewable: value.visibility
	};
	
	let options = {
		responseProperties: "anywherefid,createdate,description,description_longdescription,createby,logtype",
		localPayload: {
			createby: personId,
			createdate: saveDate,
			description: summary,
			description_longdescription: longDesc,
			logtype: logType,
			anywherefid: workLog.anywherefid
		}
	};
	let response;

  if (directSave) {
    workLogDS.on('update-data-failed', page.onUpdateDataFailed);
    response = await workLogDS.update(workLog, option);	
    if (response) {
		  datasource.off("update-data-failed", page.onUpdateDataFailed);
		  console.log(response);
	  }
    return;
  }
  try {
    app.userInteractionManager.drawerBusy(true);
    page.state.chatLogLoading = true;
    page.saveDataSuccessful = true;

    workLogDS.on('update-data-failed', page.onUpdateDataFailed);
    response = await workLogDS.update(workLog, option);

    if (response) {
      workLogDS.off('update-data-failed', page.onUpdateDataFailed);
    }
    
    page.state.chatLogGroupData = await workLogDS.forceReload();
  } catch {

  } finally {
    app.userInteractionManager.drawerBusy(false);
    page.state.chatLogLoading = false;

    let schemaLogType = datasource.getSchemaInfo('logtype');
    if (schemaLogType) {
      page.state.defaultLogType = schemaLogType.default;
    }
  }

  if (page.saveDataSuccessful) {
    page.showDialog(drawer);
  }
}

// Assisted by watsonx Code Assistant 
/**
 * Returns the confirm dialog label.
 * @param {object} app - The application object.
 * @param {object} config - The configuration object.
 * @param {array} dynamicLabel - The dynamic label array.
 */
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

const approvePO = async (app, page, item) => {
	// check user security groups
	
	// check po limits
	
	// check lines
	
	// approve record 
	
	// return to approvals page
}

const rejectPO = async (app, page, item) => {
	// open rejection drawer & wait for comment
	
	// confirm comment save & reject record
	
	// return to approvals page 
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
    openWorkLogDrawer,
	saveWorkLog,
    getConfirmDialogLabel,
    clearSharedData,
    sharedData,
};
  
export default functions;
  