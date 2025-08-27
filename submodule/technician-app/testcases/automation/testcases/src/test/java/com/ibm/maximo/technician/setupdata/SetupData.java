package com.ibm.maximo.technician.setupdata;

import com.ibm.maximo.technician.framework.TechnicianTest;

public class SetupData extends TechnicianTest {
	public enum WoStatus {
		APPR, WPCOND, CAN, CLOSE, COMP, HISTEDIT, INPRG, WAPPR, WMATL, WSCH
	}

	public enum WorkType {
		CAL, CM, CMCAL, CP, EM, EMCAL, EV, PM, PMCAL, IN
	}

	public enum WorkLogType {
		APPTNOTE, CLIENTNOTE, UPDATE, WORK
	}
	
	public enum LocAssetStatus {
		ACTIVE, BROKEN, DECOMMISSIONED, IMPORTED, INACTIVE, LIMITEDUSE, MISSING, OPERATING, SEALED
	}

	public enum MeterType {
		CONTINUOUS, GAUGE, CHARACTERISTIC
	}

	public enum ReadingType {
		ACTUAL, DELTA, RESET
	}

	public enum AverageMethod {
		ALL, STATIC
	}

	public enum MaxDomain {
		MAXTYPE
	}

	public enum ItemStatus {
		ACTIVE, OBSOLETE, PENDING, PENDOBS, PLANNING
	}

	public enum LocType {
		COURIER, HOLDING, LABOR, OPERATING, REPAIR, SALVAGE, STOREROOM, VENDOR
	}

	public enum ItemType {
		ITEM, STDSERVICE, TOOL
	}

	public enum LineType {
		ITEM, MATERIAL
	}
	
	public enum CalDatasheetStatus{
		ACTION, ADJREQD, ADJTOIMP, BROKEN, FAIL, INSPECT, LIMITEDUSE, MISSING, OLIM, PASS, WARNING
	}

	public static final int CreatedSuccess = 201;
	public static final int OkSuccess = 200;
	public static final int NoContentSuccess= 204;

	public static final String SITEID = mobileSetupData.isPresent ? mobileSetupData.data.get("site1") : "BEDFORD";
	public static final String STOREROOM = mobileSetupData.isPresent ? mobileSetupData.data.get("storeroom") : "CENTRAL";
	public static final String ORGID = mobileSetupData.isPresent ? mobileSetupData.data.get("organization") : "EAGLENA";
	public static final String OWNERGROUP = mobileSetupData.isPresent ? mobileSetupData.data.get("mobpergroup1") : "ENG";
	public static final String CURRENCYCODE = mobileSetupData.isPresent ? mobileSetupData.data.get("currency") : "USD";
	public static final String ISSUEUNIT =mobileSetupData.isPresent ? mobileSetupData.data.get("unitOfMeasure") : "EACH";
	public static final String ITEMSET = mobileSetupData.isPresent ? mobileSetupData.data.get("itemSet") : "SET1";
	public static final String GLDEBITACCT = mobileSetupData.isPresent ? mobileSetupData.data.get("glAccount") : "6000-200-000";
	public static final String SKILL = mobileSetupData.isPresent ? mobileSetupData.data.get("mobskillLevel") : "FIRSTCLASS";
	public static final String VENDOR = mobileSetupData.isPresent ? mobileSetupData.data.get("vendor") : "CMC";
	public static final String CRAFT = mobileSetupData.isPresent ? mobileSetupData.data.get("mobilecraft") : "ELECT";
	public static final String LABORNAME = mobileSetupData.isPresent ? mobileSetupData.data.get("user9_display_name") : "Mike Wilson";
	public static final String LABORNAME1 = mobileSetupData.isPresent ? mobileSetupData.data.get("user11_display_name") : "Sam Murphy";
	public static final String MAPNAME = mobileSetupData.isPresent ? mobileSetupData.data.get("mapname") : "LBS_DEMO";
	public static final String SECURITYGROUP = mobileSetupData.isPresent ? mobileSetupData.data.get("securityGroup") : "TECHNICIAN";
	public static final String ASSET_DESCRIPTION = "Asset Description ";
	public static final String WORKORDER_DESCRIPTION = "Work Order Description ";
	public static final String TOOL_DESCRIPTION = "TOOL ADDED";
	public static final String DFLTBIN = "bin101";
	public static final String LATITUDEY = "32.265942";
	public static final String LONGITUDEX = "75.646873";
	public static final String ADDRESSTITLE = "Automation Test Address";
	public static final String MANUFACTURER = "EMI";
	public static final String MATERIALNAME = "StoreRoom";
	public static final String ACTUALLABORHOUR = "1";
	public static final String ASSETLOCATION = "BR200";
	public static final String SRCLASS = "SR";
	public static final String SRSTATUS = "NEW";
	public static final String RELTYPE = "FS";
	public static final String LANGCODE = "EN";
}
