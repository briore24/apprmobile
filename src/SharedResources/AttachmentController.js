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

class AttachmentsController {
    pageInitialized(page, app) {
        this.app = app;
        this.page = page;
      }
    async pageResumed(page) {
        const purchaseOrderAttachmentResource = page.datasources['purchaseOrderAttachmentResource'];
        const attachmentListDS = page.datasources['attachmentListDS'];
        await purchaseOrderAttachmentResource.load({noCache:true, itemUrl: page.params.itemhref});
        this.page.state.attachmentPonum = purchaseOrderAttachmentResource.item.ponum;
        attachmentListDS?.clearState();
        attachmentListDS?.forceReload();

    }

    pagePaused(page) {
        const attachmentListDS = page.datasources['attachmentListDS'];
        attachmentListDS.clearState();
        attachmentListDS.resetState();
    }
    
	onAfterLoadData(dataSource, items) {
		let self = this;
        //istanbul ignore else
		if (dataSource.name === 'attachmentListDS' && items) {
			self.app.state.doclinksCountData[this.page.state.attachmentPonum] = items.length;
		}
	}

    forceSyncNeeded(itemUrl) {
        const purchaseOrderUID = itemUrl?.split("/").pop();
        let poList = sessionStorage?.getItem('updated_purchaseorder_attachments');
        //istanbul ignore else
        if (!poList) {
            poList = "[]";
          }
        const poListJson = JSON.parse(poList);
        const index = poListJson.indexOf(purchaseOrderUID);
        if (index > -1){
            //remove item from sessionstorage
            poListJson.splice(index, 1);
            sessionStorage?.setItem('updated_purchaseorder_attachments', JSON.stringify(poListJson));
            return true;
        } else return false;
    }
}
export default AttachmentsController;
