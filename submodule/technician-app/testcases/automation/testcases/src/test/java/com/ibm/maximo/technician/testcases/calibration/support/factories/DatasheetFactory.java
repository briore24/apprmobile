package com.ibm.maximo.technician.testcases.calibration.support.factories;

import com.ibm.maximo.automation.mobile.api.JdbcConnection;
import com.ibm.maximo.automation.mobile.api.json.WorkOrder;
import com.ibm.maximo.technician.testcases.calibration.support.constants.BaseConstants;
import com.ibm.maximo.technician.testcases.calibration.support.objects.maximo.*;
import com.ibm.maximo.technician.testcases.calibration.support.utils.DataFileParser;
import com.ibm.maximo.technician.testcases.calibration.support.utils.sql.SqlQueryBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DatasheetFactory extends BaseFactory {

    private final static Logger logger = LoggerFactory.getLogger(DatasheetFactory.class);

    private String datasheetName;

    private String assetnum;

    private String siteId;

    private String orgId;
    
    private Integer repeatValue;
    
    private boolean enableNoAdjustment = false;
    
    private List<PluscDsInstr> pluscDsInstrList;
    
    public DatasheetFactory() {
    	super();
    }
    
    public DatasheetFactory(String datasheetName) {
    	this.datasheetName = datasheetName;
    }

    public void createByModelName(String modelName) throws Exception {

        String folderPath = String.format(
            "%s/calibration/support/data/datasheets/%s",
            BaseConstants.PROJECT_RELATIVE_PATH,
            modelName
        );

        this.create(modelName, folderPath);
    }

    public void create(String modelName, String folderPath) throws Exception {
    	logger.info(String.format("Creating datasheet model \"%s\"", modelName));

        // Get sequence number
        Integer nextInstrSeqNumber = this.findNextInstrSeqNumber();

        // Create Plan
        this.createPlan(
            Paths.get(folderPath + "/" + modelName + "-datasheet.plan.json"),
            modelName,
            nextInstrSeqNumber
        );
        
        if(this.enableNoAdjustment)
        	this.updateDsForNoAdjustment();
    }
    
    /**
     * Create a datasheet using information from the work order object. Does not add to work order.
     * To add to work order, use setPlusCWODS on work order object and call update (if work order already created) 
     * 
     * @param woObj - WorkOrder object
     * @param labor - assigned labor 
     * @param targetModel - datasheet model
     * @param jdbcConnection - JdbcConnection connection instance
     * 
     * @throws Exception
     */
    public void createDataSheet(WorkOrder woObj, String labor, String targetModel, JdbcConnection jdbcConnection) throws Exception {
    	this.createDataSheet(
    			woObj.getAssetNum(),
    			woObj.getSiteId(), 
    			woObj.getOrgId(), 
    			labor, 
    			targetModel, 
    			jdbcConnection
    		);
    }
    
    /**
     * Create a datasheet given assetnum, siteid and orgid.
     * 
     * @param assetnum
     * @param siteId
     * @param orgId
     * @param labor - assigned labor
     * @param targetModel - datasheet model
     * @param jdbcConnection - JdbcConnection connection instance
     * 
     * @throws Exception
     */
    public void createDataSheet(String assetnum, String siteId, String orgId, String labor, String targetModel, JdbcConnection jdbcConnection) throws Exception {
    	this.setSystemUsername(labor)
    	.setJdbcConnection(jdbcConnection)
    	.setAssetnum(assetnum)
    	.setSiteId(siteId)
    	.setOrgId(orgId)
    	.createByModelName(targetModel);
    }

    public void createPlan(Path filePath, String modelName, Integer nextInstrSeqNumber) throws Exception {

        logger.info("===== Creating datasheet plan [" + filePath.getFileName() + "] =====");

        JdbcConnection jdbcConnection = this.getJdbcConnection();
        String datasheetName = this.getDatasheetName();
        String systemUsername = this.getSystemUsername();
        String orgId = this.getOrgId();
        String siteId = this.getSiteId();
        String assetnum = this.getAssetnum();

        // Parse file and separate into JSON String parts
        logger.info("Reading json file");
        
        String folderPath = String.format(
                "%s/calibration/support/data/datasheets/%s",
                BaseConstants.PROJECT_RELATIVE_PATH,
                modelName
            );
        
        Path dsConfigFilePath = Paths.get(folderPath + "/" + modelName + "-datasheet.config.json");
        
        if(!Files.exists(dsConfigFilePath)) {
        	dsConfigFilePath = Paths.get(String.format(
    	            "%s/calibration/support/data/datasheets/common-datasheet.pluscdsconfig.json",
    	            BaseConstants.PROJECT_RELATIVE_PATH
    	        ));
        	logger.info("PLUSCDSCONFIG data not found, using common-datasheet.pluscdsconfig.json");
        } else {
        	logger.info("Using PLUSCDSCONFIG data found in file: {}", modelName + "-datasheet.pluscdsconfig.json");
        }
        
        JSONObject plan = (JSONObject) DataFileParser.parseJsonFile(filePath.toString());
        JSONObject dsconfig = (JSONObject) DataFileParser.parseJsonFile(dsConfigFilePath.toString());
        
        String plusdsplanStr = (String) plan.get("plusdsplan").toString();
        String pluscdsconfigStr = (String) dsconfig.get("pluscdsconfig").toString();
        String pluscdsstatusStr = (String) plan.get("pluscdsstatus").toString();
        String pluscdsassetlinkStr = (String) plan.get("pluscdsassetlink").toString();
        // Instantiate Objects
        logger.info("Instantiating objects");
        PlusDsPlan plusDsPlan = this.gson.fromJson(plusdsplanStr, PlusDsPlan.class);
        PluscDsConfig pluscDsConfig = this.gson.fromJson(pluscdsconfigStr, PluscDsConfig.class);
        setRepeatValue(pluscDsConfig.getRepeatvalue());
        PluscDsStatus plusDsStatus = this.gson.fromJson(pluscdsstatusStr, PluscDsStatus.class);
        PluscDsAssetLink pluscDsAssetLink = this.gson.fromJson(pluscdsassetlinkStr, PluscDsAssetLink.class);
        
        Object pluscdsinstrStrObj = plan.get("pluscdsinstr");
        boolean multipleAssetFunctions = this.isMultiAsset(pluscdsinstrStrObj.toString());
        
        List<PluscDsInstr> pluscDsInstrList = this.deserializePluscDsInstr(pluscdsinstrStrObj, nextInstrSeqNumber, multipleAssetFunctions);
        setPluscDsInstrList(pluscDsInstrList);
        // Assign nextInstrSeqNumber to Asset Function and ownership to DSPlan and DSStatus
        logger.info("Assigning attribute values to objects");
        plusDsPlan.setChangeby(systemUsername);
        plusDsStatus.setChangeby(systemUsername);
        pluscDsAssetLink.setOrgid(orgId);
        pluscDsAssetLink.setSiteid(siteId);
        pluscDsAssetLink.setAssetnum(assetnum);

        // Create objects
        // *Heads up!* The SQL statements here should be in this exact order:
        // plusDsPlan, plusDsStatus, pluscDsPoints and pluscdsinstr
        logger.info("Creating objects");
        List<CalibrationMaximoObject> objects = new ArrayList<>();
        objects.add(plusDsPlan);
        objects.add(pluscDsConfig);
        objects.add(plusDsStatus);
        objects.addAll(pluscDsInstrList);
        objects.add(pluscDsAssetLink);

        // Assign DB properties and DSPLANNUM to all objects
        for(CalibrationMaximoObject object : objects) {
            object.setJdbcConnection(jdbcConnection);
            object.setDsplannum(datasheetName);
        }

        // Execute SQL statements
        logger.info("Initiating teardown and setup:");
        for (CalibrationMaximoObject object : objects) {
            logger.info("Tearing Down " + object.getClass().getSimpleName());
            object.teardown();

            logger.info("Setting Up " + object.getClass().getSimpleName());
            object.setup();
        }

        logger.info("===== Finished creating datasheet plan. =====");
    }
    
    private List<PluscDsInstr> deserializePluscDsInstr(Object pluscdsinstrStrObj, Integer nextInstrSeqNumber, boolean isMultiAsset) {
    	logger.info("Deserializing pluscdsinstr property");
    	String pluscdsinstrStr = (String) pluscdsinstrStrObj.toString();
        List<PluscDsInstr> objects = new ArrayList<>();

        // Multiple assets were registered
        if (isMultiAsset) {
            JSONArray pluscDsInstrArr = (JSONArray) pluscdsinstrStrObj;
            for(int i = 0; i < pluscDsInstrArr.length(); i++) {
                String entry = (String) pluscDsInstrArr.get(i).toString();
                PluscDsInstr pluscDsInstr = this.gson.fromJson(entry, PluscDsInstr.class);
                objects.add(pluscDsInstr);
            }
        // Single asset to add
        } else {
            PluscDsInstr pluscDsInstr = this.gson.fromJson(pluscdsinstrStr, PluscDsInstr.class);
            objects.add(pluscDsInstr);
        }

        // Assigning a instr sequence number and asset function number and increment as we go
        Integer sequence = nextInstrSeqNumber;
        Integer startAssetfunction = 1;
        for(PluscDsInstr object : objects) {
            object.setInstrseq(sequence);
            object.setAssetfunction(startAssetfunction++);
            sequence++;
        }

        return objects;
    }

    /**
     * Determines whether instance "pluscdsinstr" is array or object
     * @param pluscdsinstrStr
     * @return Boolean
     */
    private Boolean isMultiAsset(String pluscdsinstrStr) {
        Object json = new JSONTokener(pluscdsinstrStr).nextValue();
        return json instanceof JSONArray;
    }

    public Integer findNextInstrSeqNumber() {

        SqlQueryBuilder builder = new SqlQueryBuilder();

        // Should create: "SELECT COALESCE(MAX(INSTRSEQ), 1000) FROM MAXIMO.PLUSCDSPOINT"
        String query = builder
            .setTableName(PluscDsPoint.TABLE_NAME)
            .setFields(new String[]{"COALESCE(MAX(INSTRSEQ), 1000)"})
            .createSelectStatement();

        Object [] resultSet = this.getJdbcConnection().executeSQL(query);
        Object [] resultArray = (Object[]) resultSet[0];

        return Integer.parseInt(resultArray[0].toString()) + 1;
    }
    
    private void updateDsForNoAdjustment() throws Exception {
    	SqlQueryBuilder builder = new SqlQueryBuilder();
    	
    	// dsplan
    	String dsplanQuery = builder.setTableName(PluscDsInstr.getTableName())
    		.putIntAttr("noadjmadechoice1", 1)
    		.setWhereClause("calfunction=0 and caldynamic=0")
    		.createUpdateStatement(this.datasheetName);
    	
    	this.getJdbcConnection().executeUpdateSQL(dsplanQuery);
    }

    public String getDatasheetName() {
        return datasheetName;
    }

    public DatasheetFactory setDatasheetName(String datasheetName) {
        this.datasheetName = datasheetName;
        return this;
    }

    public String getAssetnum() {
        return assetnum;
    }

    public DatasheetFactory setAssetnum(String assetnum) {
        this.assetnum = assetnum;
        return this;
    }

    public String getSiteId() {
        return siteId;
    }

    public DatasheetFactory setSiteId(String siteId) {
        this.siteId = siteId;
        return this;
    }

    public String getOrgId() {
        return orgId;
    }

    public DatasheetFactory setOrgId(String orgId) {
        this.orgId = orgId;
        return this;
    }

    @Override
    public DatasheetFactory setJdbcConnection(JdbcConnection jdbcConnection) {
        super.setJdbcConnection(jdbcConnection);
        return this;
    }
    
    @Override
    public DatasheetFactory setSystemUsername(String systemUsername) {
    	super.setSystemUsername(systemUsername);
    	return this;
    }

	public boolean isEnableNoAdjustment() {
		return enableNoAdjustment;
	}

	public void setEnableNoAdjustment(boolean enableNoAdjustment) {
		this.enableNoAdjustment = enableNoAdjustment;
	}

	public Integer getRepeatValue() {
		return repeatValue;
	}

	public void setRepeatValue(Integer repeatValue) {
		this.repeatValue = repeatValue;
	}

	public List<PluscDsInstr> getPluscDsInstrList() {
		return pluscDsInstrList;
	}

	public void setPluscDsInstrList(List<PluscDsInstr> pluscDsInstrList) {
		this.pluscDsInstrList = pluscDsInstrList;
	}
	
}
