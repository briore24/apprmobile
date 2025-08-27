package com.ibm.maximo.technician.testcases.calibration.support.objects.maximo;

import com.google.gson.annotations.SerializedName;
import com.ibm.maximo.technician.testcases.calibration.support.utils.sql.SqlQueryBuilder;

import java.math.BigInteger;

public class PlusDsPlan extends CalibrationMaximoObject implements CalibrationMaximoObjectInterface {

    private static final String RESOURCE = "plusdsplan";

    private static final String TABLE_NAME = "PLUSDSPLAN";

    private transient String locationUrl;

    private String href;

    @SerializedName("plusdsplanid")
    private BigInteger plusdsplanid;

    @SerializedName("changedate")
    private String changedate;

    @SerializedName("hasld")
    private Integer hasld;

    @SerializedName("historyflag")
    private Integer historyflag;

    @SerializedName("langcode")
    private String langcode;

    @SerializedName("viewasloop")
    private Integer viewasloop;

    @SerializedName("changeby")
    private String changeby;

    @SerializedName("confidlevel")
    private String confidlevel;

    @SerializedName("description")
    private String description;

    @SerializedName("ds1")
    private String ds1;

    @SerializedName("ds10")
    private String ds10;

    @SerializedName("ds2")
    private String ds2;

    @SerializedName("ds3")
    private String ds3;

    @SerializedName("ds4")
    private String ds4;

    @SerializedName("ds5")
    private String ds5;

    @SerializedName("ds6")
    private String ds6;

    @SerializedName("ds7")
    private String ds7;

    @SerializedName("ds8")
    private String ds8;

    @SerializedName("ds9")
    private String ds9;

    @SerializedName("gbmethod")
    private String gbmethod;

    @SerializedName("guardband")
    private String guardband;

    @SerializedName("kfactor")
    private String kfactor;

    @SerializedName("orgid")
    private String orgid;

    @SerializedName("revcomments")
    private String revcomments;

    @SerializedName("revisionnum")
    private Integer revisionnum;

    @SerializedName("siteid")
    private String siteid;

    @SerializedName("status")
    private String status;

    @SerializedName("systemid")
    private String systemid;

    @SerializedName("systemiddesc")
    private String systemiddesc;

    @SerializedName("uncertfreq")
    private Integer uncertfreq;

    @SerializedName("uncertunits")
    private String uncertunits;

    @Override
    public int setup() {
        SqlQueryBuilder builder = new SqlQueryBuilder();

        String statement = builder
            .setTableName(PlusDsPlan.TABLE_NAME)
            .putSysAttr("changedate", "sysdate")
            .putIntAttr("hasld", this.hasld)
            .putIntAttr("historyflag", this.historyflag)
            .putStrAttr("langcode", this.langcode)
            .putIntAttr("viewasloop", this.viewasloop)
            .putStrAttr("dsplannum", this.dsplannum)
            .putIntAttr("revisionnum", this.revisionnum)
            .putIntAttr("uncertfreq", this.uncertfreq)
            .putStrAttr("changeby", this.changeby)
            .putStrAttr("description", this.description)
            .putStrAttr("description", this.description)
            .putStrAttr("ds1", this.ds1)
            .putStrAttr("ds10", this.ds10)
            .putStrAttr("ds2", this.ds2)
            .putStrAttr("ds3", this.ds3)
            .putStrAttr("ds4", this.ds4)
            .putStrAttr("ds5", this.ds5)
            .putStrAttr("ds6", this.ds6)
            .putStrAttr("ds7", this.ds7)
            .putStrAttr("ds8", this.ds8)
            .putStrAttr("ds9", this.ds9)
            .putStrAttr("gbmethod", this.gbmethod)
            .putStrAttr("guardband", this.guardband)
            .putDecimalAttr("kfactor", this.kfactor)
            .putStrAttr("orgid", this.orgid)
            .putStrAttr("revcomments", this.revcomments)
            .putStrAttr("siteid", this.siteid)
            .putStrAttr("status", this.status)
            .putStrAttr("systemid", this.systemid)
            .putStrAttr("systemiddesc", this.systemiddesc)
            .putStrAttr("uncertunits", this.uncertunits)
            .createInsertStatement();

        return this.executeUpdateSQL(statement);
    }

    @Override
    public int teardown() {
        SqlQueryBuilder builder = new SqlQueryBuilder();

        String statement = builder
            .setTableName(PlusDsPlan.TABLE_NAME)
            .setWhereClause(String.format("dsplannum = '%s'", this.dsplannum))
            .createDeleteStatement();

        return this.executeUpdateSQL(statement);
    }

    public void setChangeby(String changeby) {
        this.changeby = changeby;
    }
}
