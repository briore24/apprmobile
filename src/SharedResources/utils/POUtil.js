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
 
import { log, Device } from '@maximo/maximo-js-api';
 
const approvePO = (app, page, limits, ds, item) => {
	let totalPoLimit = 0;
	limits.forEach((lim) => {
		log.i(lim.groupname, lim.polimit);
		totalPoLimit += lim.polimit;
	})
	let totalCost = item.computedTotalCost;
	if (totalCost > totalPoLimit) {
		console.log("total cost exceeds total PO limit");
		app.showDialog("limitExceededDialog");
	}
	
	// check receipttolerance, receipttolqty, & receipttolamt 
	
	// maximum allowed qty = ordered qty + received qty 
	
	// warning message if user entry > max qty
	
	// if checks passed, change status to APPR?
}

const rejectPO = (app, page, ds) => {
	let item = app.state.rejectItem;
	let comment = app.state.rejectionComment;
	// check that message was saved 
	if (comment !== "") {
		let dataFormatter = this.app.dataFormatter;
		let action = 'changeStatus';
		let curDate = dataFormatter.convertDatetoISO(new Date());
		let option = {
			record: item,
			parameters: {
				status: 'CLOSE',
				date: curDate,
				memo: comment
			},
			headers: {
				'x-method-override': 'PATCH'
			},
			responseProperties: 'status, rel.postatus{postatusid, changeby, changedate, status}',
			localPayload: {
				status: 'CLOSE',
				status_maxvalue: 'CLOSE',
				status_description: 'CLOSE',
				statusdate: curDate
			},
			query: {interactive: false, ignorecollectionref:1},
			esigCheck: 0
		};	
	}
}


const functions = {
	approvePO,
	rejectPO
};


export default functions;
