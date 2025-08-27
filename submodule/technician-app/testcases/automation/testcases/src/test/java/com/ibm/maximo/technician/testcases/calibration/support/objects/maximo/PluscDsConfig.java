package com.ibm.maximo.technician.testcases.calibration.support.objects.maximo;

import com.google.gson.annotations.SerializedName;
import com.ibm.maximo.technician.testcases.calibration.support.utils.sql.SqlQueryBuilder;

import java.math.BigInteger;

public class PluscDsConfig extends CalibrationMaximoObject implements CalibrationMaximoObjectInterface {
	private static final String RESOURCE = "pluscdsconfig";

	private static final String TABLE_NAME = "PLUSCDSCONFIG";
	 
	@SerializedName("pluscdsconfigid")
	private BigInteger pluscdsconfigid;

	@SerializedName("orgid")
	private String orgid;

	@SerializedName("siteid")
	private String siteid;

	@SerializedName("revisionnum")
	private Integer revisionnum;

	@SerializedName("asseterror")
	private Integer asseterror;

	@SerializedName("assetnplaces")
	private Integer assetnplaces;

	@SerializedName("assettruncate")
	private Integer assettruncate;

	@SerializedName("tolerror")
	private Integer tolerror;

	@SerializedName("tolnplaces")
	private Integer tolnplaces;

	@SerializedName("toltruncate")
	private Integer toltruncate;

	@SerializedName("repeatvalue")
	private Integer repeatvalue;

	@SerializedName("stddev")
	private Integer stddev;

	@SerializedName("outputtruncate")
	private Integer outputtruncate;

	@SerializedName("rangetruncate")
	private Integer rangetruncate;
	
	@Override
    public int setup() {
        SqlQueryBuilder builder = new SqlQueryBuilder();

        String statement = builder
            .setTableName(PluscDsConfig.TABLE_NAME)
    		.putStrAttr("dsplannum", this.dsplannum)
    		.putStrAttr("orgid", this.orgid)
    		.putStrAttr("siteid", this.siteid)
    		.putIntAttr("revisionnum", this.revisionnum)
    		.putIntAttr("asseterror", this.asseterror)
    		.putIntAttr("assetnplaces", this.assetnplaces)
    		.putIntAttr("assettruncate", this.assettruncate)
    		.putIntAttr("tolerror", this.tolerror)
    		.putIntAttr("tolnplaces", this.tolnplaces)
    		.putIntAttr("toltruncate", this.toltruncate)
    		.putIntAttr("repeatvalue", this.repeatvalue)
    		.putIntAttr("stddev", this.stddev)
    		.putIntAttr("outputtruncate", this.outputtruncate)
    		.putIntAttr("rangetruncate", this.rangetruncate)
            .createInsertStatement();

        return this.executeUpdateSQL(statement);
    }
	
	 @Override
	    public int teardown() {
	        SqlQueryBuilder builder = new SqlQueryBuilder();

	        String statement = builder
	            .setTableName(PluscDsConfig.TABLE_NAME)
	            .setWhereClause(String.format("dsplannum = '%s'", this.dsplannum))
	            .createDeleteStatement();

	        return this.executeUpdateSQL(statement);
	    }

	public Integer getRepeatvalue() {
		return repeatvalue;
	}
	 
	 
}
