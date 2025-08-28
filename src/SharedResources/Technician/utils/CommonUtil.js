import _ from 'lodash';
import { log, Device } from '@maximo/maximo-js-api';
import MapPreLoadAPI from '@maximo/map-component/build/ejs/framework/loaders/MapPreLoadAPI';
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

/**
 * Marks the status of a work order as "Accepted".
 * @param {Object} app The GlideApplication object
 * @param {Object} page The current page object
 * @param {Object} ds The datasource object
 */
const markStatusAssigned = async (app,page,ds, listDS) => {
  const assignmentDS = ds.getChildDatasource('assignment',ds.item);
  const acceptLabel = await app.getLocalizedLabel('accepted', 'Accepted').toUpperCase();
  const records = await assignmentDS.load();

  const assignmentLength = records?.length;
  const index = (assignmentLength) ? records?.findIndex(assignment => assignment?.laborcode === app.client?.userInfo?.labor?.laborcode && assignment?.assignmentid) : 0;
  const tempRecord = records[index];
  // istanbul ignore else
  if (records.length > 0 && tempRecord) {
    tempRecord.status=acceptLabel;
    tempRecord.status_maxvalue=acceptLabel;
    tempRecord.status_description=acceptLabel;
  }
  try {
    await assignmentDS.save();
    const message = `Assignment ${ds.item.wonum} was assigned to you.`;
    app.toast(
      app.getLocalizedLabel(
        'accepted_wo',
        message,
        [ds.item.wonum]
      ),
    'success');
  } catch (error) {
    log.t("Assigned", "Failed assignment rejection : work order --> " + ds?.item?.wonum + "--> " + error);
    const message = `Assignment ${ds?.item?.wonum} could not be assigned to you.`;
    app.toast(
      app.getLocalizedLabel(
        'accepted_wo_failure',
        message,
        [ds.item?.wonum]
      ),
    'error');
  }
  await ds.forceReload();
  await listDS.forceReload();
}

/**
 * Marks the status of a work order as "Accepted".
 * @param {Object} app The GlideApplication object
 * @param {Object} page The current page object
 * @param {Object} ds The datasource object
 */
const removeAssigned = async (app, page, ds, showToast = true) => {
  const rejectLabel = await app.getLocalizedLabel('rejected', 'Rejected');

  const woDetailDs = app.findDatasource("podetails");
  if (!Device.get().isMaximoMobile || !woDetailDs.item) {
    await woDetailDs.forceReload();
  }
  const records = woDetailDs.item?.assignment ?? [];
  const assignmentLength = records?.length;
  const index = (assignmentLength) ? records?.findIndex(assignment => assignment?.laborcode === app.client?.userInfo?.labor?.laborcode) : 0;

  let tempRecord = woDetailDs.item.assignment[index];
  if (records.length > 0 && tempRecord) {
    tempRecord.status = rejectLabel;
    tempRecord.status_maxvalue = 'WAITASGN';
    tempRecord.status_description = rejectLabel;
    tempRecord.laborcode = null;
    tempRecord.splanreviewdate = null;
  }

  const localPayload = {
    hidewo: 'HIDE',
    href: woDetailDs.item.href,
    assignment: woDetailDs.item.assignment
  };
  const option = {
    responseProperties: "assignment,href",
    localPayload: localPayload,
    // have to add manual action event as in quick work order it's creating new instead 
    // later on more time can be spend to find solution for this but most possibly issue lies at graphite or platform end
    _action: "change"
  };
  const dataToUpdate = {
    assignment: woDetailDs.item.assignment,
    href: woDetailDs.item.href,
    hidewo: 'HIDE',
    // below properties should not be part of original request but found limitation in graphite for bulk put case
    // where it's spreading of payload of child with change and ignoring options
    responseProperties: "assignment,href",
    localPayload: localPayload,
    _action: "change"
  };
  try {
    await woDetailDs.save(dataToUpdate, option);
    /* istanbul ignore next */
    if (showToast) {
      const message = `Assignment ${woDetailDs.item.wonum} was ${(sharedData.clickedUnassignment) ? 'returned' : 'rejected'} and returned to the dispatcher.`;
      app.toast(
        app.getLocalizedLabel(
          (sharedData.clickedUnassignment) ? 'unassigned_wo' : 'rejected_wo',
          message,
          [woDetailDs.item.wonum]
        ),
        'success');
    }
  } catch (error) {
    log.t("Reject", "Failed assignment rejection : work order --> " + woDetailDs.item?.wonum + "--> " + error);
    //istanbul ignore if
    if (showToast) {
      const message = `Assignment ${woDetailDs.item?.wonum} could not be ${(sharedData.clickedUnassignment) ? 'returned' : 'rejected'}. Resync data and try again.`;
      app.toast(
        app.getLocalizedLabel(
          (sharedData.clickedUnassignment) ? 'unassigned_wo_failure' : 'rejected_wo_failure',
          message,
          [woDetailDs.item?.wonum]
        ),
        'error');
    }
  }
}

// Assisted by watsonx Code Assistant 
/**
 * Complete assigned work order.
 * @param {object} app - The application object.
 * @param {object} woDetailDs - The work order detail dataset.
 * @param {object} tempRecord - The temporary record object.
 * @param {object} assignmentDS - The assignment dataset.
 * @returns {Promise<void>} A promise that resolves when the work order is completed.
 */
const completeAssigned = async (app, woDetailDs, tempRecord, assignmentDS) => {
  const compValue = assignmentDS?.items?.find(assignment => assignment.maxvalue === 'COMPLETE' && assignment.defaults);

  tempRecord.status = compValue?.value;
  tempRecord.status_maxvalue = compValue?.maxvalue;
  tempRecord.status_description = compValue?.description;
  tempRecord.finishdate = app.dataFormatter.convertDatetoISO(new Date());//app.dataFormatter.dateWithoutTimeZone(app.dataFormatter.convertDatetoISO(new Date()));

  const localPayload = {
    hidewo: 'HIDE',
    href: woDetailDs.item.href,
    assignment: woDetailDs.item.assignment
  };
  const option = {
    responseProperties: "assignment,href",
    localPayload: localPayload,
    // have to add manual action event as in quick work order it's creating new instead 
    // later on more time can be spend to find solution for this but most possibly issue lies at graphite or platform end
    _action: "change"
  };
  const dataToUpdate = {
    assignment: woDetailDs.item.assignment,
    href: woDetailDs.item.href,
    hidewo: 'HIDE',
    // below properties should not be part of original request but found limitation in graphite for bulk put case
    // where it's spreading of payload of child with change and ignoring options
    responseProperties: "assignment,href",
    localPayload: localPayload,
    _action: "change"
  };
  try {
    await woDetailDs.save(dataToUpdate, option);
    return true;
  } catch (error) {
    log.t("Reassigned", "Failed reassignement : work order --> " + woDetailDs.item?.wonum + "f--> " + error);
    return false;
  }
}

 // Generated by WCA for GP
/**
 * Reset state of Datasource
 */
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
 * Common Function to open a sliding-drawer dialog to show Work Log for the Work Order at Schedule & WorkOrderDetails Page
 * @param {Application} app The application
 * @param {page} page - page name
 * @param {event} event - event containing information about current item
 * @param {workLogDS} is workLogDS - woDetailsWorklogDs from WorkOrderDetails Page & woWorklogDs from Schedule Page
 * @param {drawerName} is drawer names for WorkOrderDetails & Schedule Page
 */
const openWorkLogDrawer = async (app, page, event, workLogDS, drawerName) => {
  // Initialized Loader on Button when Work Log Drawer Icon Clicked
  page.state.chatLogLoading = true;
  const orgId = app.client?.userInfo?.insertOrg;
  const siteId = app.client?.userInfo?.insertSite;
  //istanbul ignore next
  if (page.name === "schedule") {
    page.state.currentItem = event.item.wonum;
    const podetails = page.datasources["podetails"];
    await podetails.load({ noCache: true });
  }
  workLogDS.clearState();
  workLogDS.resetState();
  await workLogDS.load().then((response) => {
    page.state.chatLogGroupData = response;
  });

  // istanbul ignore if
  if (Device.get().isMaximoMobile && workLogDS.options.query.relationship) {
    //Get schema of childdatasource.
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
  const synonymDs = app.datasources["synonymdomainData"];
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

  // istanbul ignore else
  if (schemaDescription) {
    page.state.chatLogDescLength = schemaDescription.maxLength;
  }

  // Stop Loading of Chat Log Icon touchpoint once all data loaded
  page.state.chatLogLoading = false;

  // Open Work Log Drawer once all Data Loaded
  page.showDialog(drawerName);
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
    markStatusAssigned,
    removeAssigned,
    _resetDataSource,
    filterMobileMaxvars,
    openWorkLogDrawer,
    getConfirmDialogLabel,
    clearSharedData,
    sharedData,
};
  
export default functions;
  