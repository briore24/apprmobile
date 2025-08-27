/*
 * Licensed Materials - Property of IBM
 *
 * 5737-M60, 5737-M66
 *
 * (C) Copyright IBM Corp. 2021,2023 All Rights Reserved
 *
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with
 * IBM Corp.
 */
import commonUtil from "./utils/CommonUtil";

import WOCreateEditUtils from "./utils/WOCreateEditUtils";

class ClassificationController {
  // Assisted by WCA@IBM
  // Latest GenAI contribution: ibm/granite-20b-code-instruct-v2
  /**
   * This method is called when the datasource is initialized. It is passed the datasource object, the owning component, and the application instance.
   * @param {Object} ds - The datasource object.
   * @param {Object} owner - The owning component.
   * @param {Object} app - The application instance.
   */
  onDatasourceInitialized(ds, owner, app) {
    this.datasource = ds;
    this.owner = owner;
    this.app = app;
  }

  // Assisted by WCA@IBM
  // Latest GenAI contribution: ibm/granite-20b-code-instruct-v2
  /**
   * @param {DataSource} dataSource - The data source object.
   * @param {Array<any>} items - An array of items loaded from the data source.
   */
  async onAfterLoadData(dataSource, items) {
    const totalCount = dataSource.dataAdapter.totalCount;

    // Initial page size is 1 so we get the totalCount
    // Now we reset and query the full set of classifications and exit
    // This will trigger onAfterLoadData() a second time
    // istanbul ignore else
    if (items.length < totalCount) {
      dataSource.reset({ ...dataSource.options.query, size: totalCount }, true);
      return; // exit after first load
    }
    // Filter Invalid Classification
    const filteredItems = WOCreateEditUtils.filterClassifications(this.app, items);

    // Set Tree Data to woClassDataDs
    //istanbul ignore next
    const treeData = this.listToTree(filteredItems);
    commonUtil.sharedData.treeData = treeData;
    await this.app.datasources['woClassDataDS']?.load({
      src: {
        member: treeData,
      },
    });
  }

  

  // Assisted by WCA@IBM
  // Latest GenAI contribution: ibm/granite-20b-code-instruct-v2
  /**
   * Converts a flat list of class structures into a hierarchical tree structure.
   * @param {Array} list - The flat list of class structures.
   * @returns {Array} The hierarchical tree structure.
   */
  listToTree(list) {
    const mapParent = {};
    const treeList = [];

    // Map Parent Classstucture
    list.forEach((item, index) => {
      mapParent[item.classstructureid] = index;
    });

    // Map Child Classstucture
    list.forEach((item) => {
      // istanbul ignore else
      if (item.parent !== undefined && list[mapParent[item.parent]]) {
        if (list[mapParent[item.parent]].children === undefined || list[mapParent[item.parent]].children.length === 0) {
          list[mapParent[item.parent]].children = [];
        }
        list[mapParent[item.parent]].children.push(item);
      } else {
        treeList.push(item);
      }
    });

    return treeList;
  }

}

export default ClassificationController;
