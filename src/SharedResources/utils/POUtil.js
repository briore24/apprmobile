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
 
const approvePO = (limits, ds, item) => {
	let totalPoLimit = 0;
	limits.forEach((lim) => {
		log.i(lim.groupname, lim.polimit);
		totalPoLimit += lim.polimit;
	})
	let totalCost = item.computedTotalCost;
	if (totalCost > totalPoLimit) {
		console.log("total cost exceeds total PO limit");
		// handle error 
	}
	
	// check receipttolerance, receipttolqty, & receipttolamt 
	
	// maximum allowed qty = ordered qty + received qty 
	
	// warning message if user entry > max qty
	
	// if checks passed, change status to APPR?
}

const rejectPO = (app, page, ds, item) => {
	// open rejection dialog
	
	// check that message was saved 
	
	// reject 
}


const functions = {
	approvePO,
	rejectPO
};


export default functions;
