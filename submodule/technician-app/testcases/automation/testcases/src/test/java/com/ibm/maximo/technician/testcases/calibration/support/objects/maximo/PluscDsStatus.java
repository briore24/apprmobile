package com.ibm.maximo.technician.testcases.calibration.support.objects.maximo;

import com.google.gson.annotations.SerializedName;
import com.ibm.maximo.technician.testcases.calibration.support.utils.sql.SqlQueryBuilder;

import java.math.BigInteger;

public class PluscDsStatus extends CalibrationMaximoObject implements CalibrationMaximoObjectInterface {
    private static final String RESOURCE = "pluscdsstatus";

    private static final String TABLE_NAME = "PLUSCDSSTATUS";

    private transient String locationUrl;

    private String href;

    @SerializedName("pluscdsstatusid")
    private BigInteger pluscdsstatusid;

    @SerializedName("changedate")
    private String changedate;

    @SerializedName("status")
    private String status;

    @SerializedName("changeby")
    private String changeby;

    @SerializedName("memo")
    private String memo;

    @SerializedName("orgid")
    private String orgid;

    @SerializedName("siteid")
    private String siteid;

    @SerializedName("revisionnum")
    private Integer revisionnum;

    public String getStatus() {
        return status;
    }

    public String getChangeby() {
        return changeby;
    }

    public String getMemo() {
        return memo;
    }

    public String getOrgid() {
        return orgid;
    }

    public String getSiteid() {
        return siteid;
    }

    public Integer getRevisionnum() {
        return revisionnum;
    }

    @Override
    public int setup() {
        SqlQueryBuilder builder = new SqlQueryBuilder();

        String statement = builder
            .setTableName(PluscDsStatus.TABLE_NAME)
            .putStrAttr("dsplannum", this.dsplannum)
            .putStrAttr("status", this.status)
            .putStrAttr("changeby", this.changeby)
            .putSysAttr("changedate", "sysdate")
            .putStrAttr("memo", this.memo)
            .putStrAttr("orgid", this.orgid)
            .putStrAttr("siteid", this.siteid)
            .putIntAttr("revisionnum", this.revisionnum)
            .createInsertStatement();

        return this.executeUpdateSQL(statement);
    }

    @Override
    public int teardown() {
        SqlQueryBuilder builder = new SqlQueryBuilder();

        String statement = builder
            .setTableName(PluscDsStatus.TABLE_NAME)
            .setWhereClause("dsplannum = '" + this.dsplannum + "'")
            .createDeleteStatement();

        return this.executeUpdateSQL(statement);
    }

    public void setChangeby(String changeby) {
        this.changeby = changeby;
    }
}
