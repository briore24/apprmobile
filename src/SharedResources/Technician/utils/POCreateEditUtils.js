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

/**
 * clear the ds search;
 * @param {ds} is database name
 */
const clearSearch = async (ds) => {
  /* istanbul ignore else */
  if (ds && ds.lastQuery.qbe && JSON.stringify(ds.lastQuery.qbe) !== "{}") {
    ds.clearQBE();
    await ds.searchQBE(undefined, true);
  }
}

 /**
   * Open Log Type Lookup from WorkLog
   */
const openWorkLogTypeLookup = async (page,ds,lookup) =>{
  ds.clearState();
  await ds.initializeQbe();
  ds.setQBE('domainid', '=', 'LOGTYPE');
  await ds.searchQBE();

  let selectedItem;
  let defaultType = page.state.initialDefaultLogType;
  /* istanbul ignore else */
  if (defaultType) {
    selectedItem = ds.items.filter((item => defaultType.replace(/!/g, "") === item.value));
  }

  /* istanbul ignore else  */
  if (selectedItem && selectedItem[0]) {
    ds.setSelectedItem(selectedItem[0], true);
  }
  page.showDialog(lookup);
}

const resetDataSource = async (app, ds) => {
  let datasource = app?.findDatasource(ds);
  await datasource?.reset(datasource.baseQuery);
};


   // Assisted by WCA@IBM
   // Latest GenAI contribution: ibm/granite-20b-code-instruct-v2
  //function is used to determine whether or not the Save button should be disabled based on the current state of the page. 
const saveDisable = (page) => {
  if (page.state.readOnlyState || page.state.saveDisable) {
    page.state.disableButton = true;
  } else {
    page.state.disableButton = false;
  }
}


// Assisted by WCA@IBM
// Latest GenAI contribution: ibm/granite-20b-code-instruct-v2
/**
* @function selectWorkType
* @description This function is used to set the selected work type in the datasource.
* @param {Object} page - The current page object.
* @param {String} datasourceName - The name of the datasource to update.
* @param {String} workType - The selected work type.
*/
const selectWorkType = (page, datasourceName, workType) => {
  page.datasources[datasourceName].item["worktype"] = workType;
  page.state.worktype = workType;
}

// Assisted by WCA@IBM
// Latest GenAI contribution: ibm/granite-20b-code-instruct-v2
/**
* @function uiRequired
* @description This function is used to check whether the given attribute is required or not.
* @param {Object} page - The current page object.
* @param {String} attributeName - The name of the attribute to check.
* @param {String} attributeValue - The value of the attribute to check.
* @returns {Boolean} - A boolean value indicating whether the attribute is required or not.
*/
const uiRequired = (page, dsName, attributeName, attributeValue) => {
  /* istanbul ignore else */
  if (attributeName === null){
    return false;
  }
  const fieldDontHasValue = attributeValue === undefined || attributeValue === "" ? true : false;
  let returnValue = false;
  const purchaseorderDt = page.datasources[dsName].uiRequired;
  const purchaseorderArray = Object.values(purchaseorderDt)[0] !== undefined ? Object.values(purchaseorderDt)[0] : undefined;
  if (purchaseorderArray !== undefined) {
    returnValue = purchaseorderArray.find(data => data === attributeName) && fieldDontHasValue ? true : false;
  }
  return returnValue;
}

// Assisted by WCA@IBM
// Latest GenAI contribution: ibm/granite-20b-code-instruct-v2
/**
* @function showPOWarnings
* @description This function is used to display warnings on the Work Order form.
* @param {Object} page - The current page object.
* @param {String} dsName - The name of the datasource to update.
* @param {String} field - The field to display the warning for.
* @param {String} message - The warning message to display.
*/
const showPOWarnings = (page, dsName, field, message) => {
  const datasource = page.datasources[dsName];
  datasource?.setWarning(datasource.item, field, message);
}

// Assisted by WCA@IBM
// Latest GenAI contribution: ibm/granite-20b-code-instruct-v2
/**
* @function clearWarnings
* @description This function is used to clear warnings from the Work Order form.
* @param {Object} page - The current page object.
* @param {String} dsName - The name of the datasource to update.
* @param {String} field - The field to clear the warning for.
*/
const clearWarnings = (page, dsName, field) => {
  const datasource = page.datasources[dsName];
  datasource?.clearWarnings(datasource.item, field);
}

const hasPoObject = (item) => {
  return item?.classusewith?.some((cuw) => {
    return cuw.objectname === "PO";
  });
}

const verifyOrgId = (app, orgid) => {
  return !(orgid && orgid !== app.client?.userInfo?.insertOrg)
}

const verifySiteId = (app, siteid) => {
  return !(siteid && siteid !== app.client.userInfo.defaultSite);
}

const functions = {
  openWorkLogTypeLookup,
  clearSearch,
  resetDataSource,
  selectWorkType,
  uiRequired,
  showPOWarnings,
  clearWarnings,
  saveDisable,
  verifyOrgId,
  verifySiteId,
  hasPoObject
};

export default functions;
