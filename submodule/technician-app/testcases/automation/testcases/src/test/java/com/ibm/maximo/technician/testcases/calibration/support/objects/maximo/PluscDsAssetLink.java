package com.ibm.maximo.technician.testcases.calibration.support.objects.maximo;

import com.google.gson.annotations.SerializedName;
import com.ibm.maximo.technician.testcases.calibration.support.utils.sql.SqlQueryBuilder;

import java.math.BigInteger;

public class PluscDsAssetLink extends CalibrationMaximoObject implements CalibrationMaximoObjectInterface {

    private static final String RESOURCE = "pluscdsassetlink";

    private static final String TABLE_NAME = "PLUSCDSASSETLINK";

    @SerializedName("dsassetslinkid")
    private BigInteger dsassetslinkid;

    @SerializedName("orgid")
    private String orgid;

    @SerializedName("siteid")
    private String siteid;

    @SerializedName("itemnum")
    private String itemnum;

    @SerializedName("assetnum")
    private String assetnum;

    @SerializedName("location")
    private String location;

    @SerializedName("dsal1")
    private String dsal1;

    @SerializedName("dsal2")
    private String dsal2;

    @SerializedName("dsal3")
    private String dsal3;

    @SerializedName("dsal4")
    private String dsal4;

    @SerializedName("dsal5")
    private String dsal5;

    @SerializedName("dsal6")
    private String dsal6;

    @SerializedName("dsal7")
    private String dsal7;

    @SerializedName("dsal9")
    private String dsal9;

    @SerializedName("dsal10")
    private String dsal10;

    @SerializedName("dsal8")
    private String dsal8;

    @SerializedName("revisionnum")
    private Integer revisionnum;

    @SerializedName("pluscdsassetlinkid")
    private BigInteger pluscdsassetlinkid;

    @SerializedName("hasld")
    private Integer hasld;

    @SerializedName("langcode")
    private String langcode;

    @Override
    public int setup() {
        SqlQueryBuilder builder = new SqlQueryBuilder();

        String statement = builder
            .setTableName(PluscDsAssetLink.TABLE_NAME)
            .putBigIntAttr("dsassetslinkid", this.dsassetslinkid)
            .putStrAttr("dsplannum", this.dsplannum)
            .putStrAttr("orgid", this.orgid)
            .putStrAttr("siteid", this.siteid)
            .putStrAttr("itemnum", this.itemnum)
            .putStrAttr("assetnum", this.assetnum)
            .putStrAttr("location", this.location)
            .putStrAttr("dsal1", this.dsal1)
            .putStrAttr("dsal2", this.dsal2)
            .putStrAttr("dsal3", this.dsal3)
            .putStrAttr("dsal4", this.dsal4)
            .putStrAttr("dsal5", this.dsal5)
            .putStrAttr("dsal6", this.dsal6)
            .putStrAttr("dsal7", this.dsal7)
            .putStrAttr("dsal9", this.dsal9)
            .putStrAttr("dsal10", this.dsal10)
            .putStrAttr("dsal8", this.dsal8)
            .putIntAttr("revisionnum", this.revisionnum)
            .putIntAttr("hasld", this.hasld)
            .putStrAttr("langcode", this.langcode)
            .createInsertStatement();

        return this.executeUpdateSQL(statement);
    }

    @Override
    public int teardown() {
        SqlQueryBuilder builder = new SqlQueryBuilder();

        String statement = builder
            .setTableName(PluscDsAssetLink.TABLE_NAME)
            .setWhereClause(String.format("dsplannum = '%s'", this.dsplannum))
            .createDeleteStatement();

        return this.executeUpdateSQL(statement);
    }

    public String getOrgid() {
        return orgid;
    }

    public void setOrgid(String orgid) {
        this.orgid = orgid;
    }

    public String getSiteid() {
        return siteid;
    }

    public void setSiteid(String siteid) {
        this.siteid = siteid;
    }

    public String getAssetnum() {
        return assetnum;
    }

    public void setAssetnum(String assetnum) {
        this.assetnum = assetnum;
    }
}
