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

import DatasheetConstants from "../rules/constants/DatasheetConstants";
import CommonUtil from "../../utils/CommonUtil";
import getMaxVars from "../../utils/getMaxVars";

class CalibrationHelper {
  constructor(datasheetDS = null, calpointsDS = null, assetfunctionDS = null) {
    //istanbul ignore next
    if (datasheetDS) {
      this.setDatasheetDS(datasheetDS);
    }

    //istanbul ignore next
    if (calpointsDS) {
      this.setCalpointsDS(calpointsDS);
    }
    //istanbul ignore next
    if (assetfunctionDS) {
      this.setAssetFunctionDS(assetfunctionDS);
    }
  }

  /**
   * Setter.
   * @returns undefined
   */
  //istanbul ignore next
  setCalpointsDS(calpointsDS) {
    this.calpointsDS = calpointsDS;
  }

  /**
   * Setter.
   * @returns undefined
   */
  //istanbul ignore next
  setDatasheetDS(datasheetDS) {
    this.datasheetDS = datasheetDS;
  }

  /**
   * Sets asset function (pluscwodsinstr) datasource as parent of
   * calibration points datasource. Calibration points and Asset
   * Function datasources are instatiated with getChildDatasource in
   * AssetFunctionsController and are being detached from its parent.
   * Therefore, we use this method to make sure they have a proper
   * parent that we have control of.
   *
   * @param {Datasource} assetfunctionDS
   * @returns void
   */
  //istanbul ignore next
  setAssetFunctionDS(assetfunctionDS) {
    const calpointsDS = this.getCalpointsDS();
    calpointsDS.parent = assetfunctionDS;
  }

  /**
   * Get reference to asset function datasource.
   * @returns {Datasource}
   */
  //istanbul ignore next
  getAssetFunctionDS() {
    const calpointsDS = this.getCalpointsDS();
    return calpointsDS.parent;
  }

  /**
   * Getter.
   * @returns {Datasource}
   */
  //istanbul ignore next
  getCalpointsDS() {
    return this.calpointsDS;
  }

  /**
   * Getter.
   * @returns {Datasource}
   */
  //istanbul ignore next
  getDatasheetDS() {
    return this.datasheetDS;
  }

  /**
   * Defines if datasheet should be updated manually or automatically
   * after calculations are perfomed in the calibration point page.
   *   If value is "0", then we turn the update off and allow user to
   * change it manually in datasheet page. If value is "1", disable
   * this feature on datasheet page and let it be updated in the
   * CalibrationPointHandler.
   *   If the maxvar "PLUSCAUTOSTATUS" is not set, then use default
   * value "1".
   *
   * @returns {Boolean}
   */
  static getAutoUpdate() {
    let isAutoUpdateStatus = true;
    const [maxvar] =
      CommonUtil.filterMobileMaxvars(DatasheetConstants.PLUSCAUTOSTATUS, {
        items: [
          {
            mobilemaxvars: getMaxVars(),
          },
        ],
      }) || [];

    if (maxvar) {
      isAutoUpdateStatus =
        maxvar.varvalue ===
        DatasheetConstants.UPDATE_DATASHEET_STATUS_AUTOMATICALLY;
    }
    return isAutoUpdateStatus;
  }

  static checkMissingOrBrokenStatus(assetFunctions, condition) {
    let statusList = [];
    let status = null;
    for (let i = 0; i < assetFunctions.length; i++) {
      statusList.push(assetFunctions[i][`${condition}calstatus`]);
    }
    if (statusList.includes(DatasheetConstants.STATUS_MISSING)) {
      status = DatasheetConstants.STATUS_MISSING;
    } else if (statusList.includes(DatasheetConstants.STATUS_BROKEN)) {
      status = DatasheetConstants.STATUS_BROKEN;
    }
    return status;
  }
  
}

export default CalibrationHelper;
