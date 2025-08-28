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
class PurchaseOrderDataController {
  onDatasourceInitialized(ds, owner, app) {
    this.datasource = ds;
    this.owner = owner;
    this.app = app;
  }
  /**
   * Function called after loading the data
   * @param {dataSource} dataSource 
   * @param {items} items 
   */
  //istanbul ignore next
  async onAfterLoadData(dataSource, items) {
    let incompleteItems = [];
    let page = this.app.findPage("lines");
    page.state.itemToOpen = page.state.itemToOpen ? page.state.itemToOpen : '';
    if (dataSource.name === 'woLineDetailds' && items.length) {
      items.forEach((item) => {
        let status = item.status_maxvalue;
        if (item.taskid && status !== 'COMP' && status !== 'CLOSE' && status !== 'CAN') {
          incompleteItems.push(item);
        }
        if (this.app.currentPage.name === 'lines' && page.state.itemToOpen === '' && status !== 'CLOSE' && status !== 'CAN' && status !== 'COMP') {
          page.state.itemToOpen = item.poid;
        }
      });  
      this.app.state.taskCount = incompleteItems.length;
    }
  }

   /**
   * Return boolean value to show or hide border
   * @param {item} task item
   */
  computedBorderDisplay(item){
    //istanbul ignore else
   return  !((!item.description_longdescription) || (item.description_longdescription) || (!item.description_longdescription));
  }
}

export default PurchaseOrderDataController;
