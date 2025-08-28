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
   * Return boolean value to show or hide border
   * @param {item} task item
   */
  computedBorderDisplay(item){
    //istanbul ignore else
   return  !((!item.description_longdescription) || (item.description_longdescription) || (!item.description_longdescription));
  }
}

export default PurchaseOrderDataController;
