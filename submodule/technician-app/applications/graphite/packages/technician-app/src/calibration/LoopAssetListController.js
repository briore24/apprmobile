/*
 * Licensed Materials - Property of IBM
 *
 * 5724-U18, 5737-M66
 *
 * (C) Copyright IBM Corp. 2025 All Rights Reserved
 *
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with
 * IBM Corp.
 */


/** Utils */
import CalibrationHelper from "./utils/CalibrationHelper";

/** Log */
import { log } from "@maximo/maximo-js-api";

/** Constants */
import DatasheetConstants from "./rules/constants/DatasheetConstants";

const TAG = "LoopAssetListController";

/**
 * LoopAssetListController
 *
 * @typedef {import('@maximo/maximo-js-api').Page} Page
 * @typedef {import('@maximo/maximo-js-api').Application} Application
 * @typedef {import('@maximo/maximo-js-api').Datasource} Datasource
 */
class LoopAssetListController {
  pageInitialized(page, app) {
    log.t(TAG, "Page Initialized");
    this.app = app;
    this.page = page;
  }

  pageResumed(page, app) {
    // States
    this.app.state.dataSheetTitle = this.app.state.datasheetName;
    this.page.state.autoupdate = CalibrationHelper.getAutoUpdate();

    // Load work order calibration details
    this.loadWoList(app, page);
  }

  /**
   * Load Work Order
   * @param {Application} app - Graphite App reference.
   * @param {Page} page - Graphite Page reference.
   */
  async loadWoList(app, page) {
    // Start loading
    page.state.loading = true;
    page.state.canLoadLoopDS = true;

    const ds = this.app.findDatasource("dsLoop");
    ds.addIgnoreField('completedwodscount');

    ds.clearQBE();
    ds.setQBE("location", page.params.location);
    await ds.searchQBE(undefined, true);


    // Complete loading
    page.state.canLoadLoopDs = false;
    page.state.filteredCount = ds.items.length;
    this.calculateStatus(ds.items);
    page.state.loading = false;
  }

  // Assisted by watsonx Code Assistant 
  /**
    * Go back to the work order details page.
    * @param {string} wonum - The work order number.
    * @param {string} href - The URL of the data sheet.
    * @param {string} siteid - The site ID.
    * @returns {void}
  */
  goBack() {
    this.app.setCurrentPage({
      name: "workOrderDetails",
      resetScroll: true,
      params: {
        wonum: this.app.state.datasheetWonum,
        href: this.app.state.dataSheethref,
        siteid: this.app.state.datasheetSiteid,
      },
    });
  }

  // Assisted by watsonx Code Assistant 
  /**
    * Resets the status of the items.
    * @param {Array} items - The items to reset the status for.
    * @returns {void}
  */
  resetStatus(items) {
    for (let i = 0; i < items.length; i++) {
      const item= items[i];
      item.completedwodscount = 0;
    }
  }

  // Assisted by watsonx Code Assistant 
  /**
    * Calculates the status of items.
    * @param {Array} items - The items to calculate the status for.
    * @returns {void}
  */
  calculateStatus(items) {
    this.resetStatus(items);
    for (let i = 0; i < items.length; i++) {
      const item= items[i];
      for(let j = 0; j < item.pluscwods?.length; j++) {
        if(this.isValidStatus(item.pluscwods[j].asfoundcalstatus) && this.isValidStatus(item.pluscwods[j].asleftcalstatus)) {
          item.completedwodscount += 1;
        }
      }
    }
  }

  /**
   * Checks if the given status is one of the valid statuses for to be counted in loop location.
   * @param {string} status - The status to be checked - asfound and asleft. 
   * @returns {boolean} - True if the status is valid, false otherwise.
   */
  isValidStatus(status) {
    return DatasheetConstants.STATUS_LIST.includes(status);
  }  

  // Assisted by watsonx Code Assistant 
  /**
   * Navigate to calibration from asset list.
   * @param {Object} item - The item to navigate to.
   * @param {string} item.href - The href of the item.
   * @param {string} item.assetnum - The asset number of the item.
   * @param {boolean} item.iscalibration - Whether the item is a calibration.
   * @param {string} [item.assetdesc] - The asset description of the item.
   * @param {string} [item.locationnum] - The location number of the item.
   * @param {string} [item.locationdesc] - The location description of the item.
   * @param {string} [item.wonum] - The WONUM of the item.
   * @param {string} [item.siteid] - The site ID of the item.
   * @returns {void}
  */
  navigateToCalibrationFromAssetList(item) {
    this.app.state.dataSheethref = item.href;
    this.app.state.assetnum = item.assetnum;
    if (item.assetnum && item.iscalibration) {
      let assetDescription = "";
      if (item.assetdesc) {
        assetDescription = item.assetdesc;
      }
      this.app.state.datasheetName = `${item.assetnum} ${assetDescription}`;
    } else if (item.locationnum && item.pluscloop) {
      this.app.state.datasheetName = `${
        item.locationnum} ${item.locationdesc ? item.locationdesc : ""}`;
    } else {
      this.app.state.datasheetName = "";
    }
    this.app.state.datasheetWonum = item.wonum;
    this.app.state.datasheetSiteid = item.siteid;
    if (item?.wonum) {
      this.app.setCurrentPage({
        name: "datasheets",
        params: {
          location: item.locationnum,
          href: item.href,
          wonum: item.wonum,
          isLoop: true,
        },
      });
    }
  }
}

export default LoopAssetListController;
