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

import {log} from '@maximo/maximo-js-api';
import CommonUtil from "./Technician/utils/CommonUtil";
const TAG = 'RejectController';

class RejectController {
  dialogInitialized(page) {
    this.page = page;
    this.app = this.page.parent.state.appVar;
    delete this.page.parent.state.appVar;

    log.t(TAG, 'dialogInitialized : disableDoneButton --> ' + this.page.parent.state.disableDoneButton);
  }

  dialogOpened() {
    this.page.parent.state.disableDoneButton = true;
    this.page.state.disableDoneButton = true;
    this.page.state.selectedStatus = "";
    delete this.page.parent.state.appVar;

    log.t(TAG, 'dialogOpened : disableDoneButton --> ' + this.page.parent.state.disableDoneButton);

    let poDetailResourceDS = this.app.findDatasource('poDetailResource');
    if (poDetailResourceDS) {
      poDetailResourceDS.forceReload();
    }
  }

  async rejectStatus() {
    let parentPage = this.page.parent;
    this.page.parent.state.loadingstatus = true;
    await parentPage.callController('saveWorkLog', {
      longDescription: "",
      summary: ` ${this.page.state.selectedStatus.value} - ${this.page.state.selectedStatus.description}`,
      visibility: true
    }, true);
    const poDetailDs = await this.app.findDatasource("podetails");
    await CommonUtil.removeAssigned(this.app, parentPage, poDetailDs);
    this.app.state.isRejected = true;
    const polistds = this.page.parent.datasources[this.page.parent.state.selectedDS];
    
    await poDetailDs?.forceReload();
    await polistds?.forceReload();
    
    this.page.parent.state.loadingstatus = false;
    parentPage.findDialog(parentPage.state.statusDialog).closeDialog();

    if (!['schedule', 'approvals'].includes(this.app.currentPage.name)) {
      const schPage = (this.app.findPage("schedule")) ? 'schedule' : 'approvals';
      this.app.setCurrentPage({ name: schPage, resetScroll: true });
    }
  }

  selectStatus(item) {

    this.page.state.disableDoneButton = false;
    this.page.parent.state.disableDoneButton = false;
    this.page.state.selectedStatus = item;
    if (this.page.parent.datasources.rejectList && this.page.parent.datasources.rejectList.state.selection.count > 0) {
      this.page.parent.state.disableDoneButton = false;
    } else {
      this.page.parent.state.disableDoneButton = true;
    }
  }

}

export default RejectController;