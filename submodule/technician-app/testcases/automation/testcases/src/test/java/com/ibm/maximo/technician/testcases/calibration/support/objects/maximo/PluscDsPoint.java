package com.ibm.maximo.technician.testcases.calibration.support.objects.maximo;

import com.google.gson.annotations.SerializedName;
import com.ibm.maximo.technician.testcases.calibration.support.utils.sql.SqlQueryBuilder;

import java.math.BigInteger;

public class PluscDsPoint extends CalibrationMaximoObject implements CalibrationMaximoObjectInterface {

    private static final String RESOURCE = "pluscdspoint";

    public static final String TABLE_NAME = "PLUSCDSPOINT";

    @SerializedName("pluscdspointid")
    private BigInteger pluscdspointid;

    @SerializedName("orgid")
    private String orgid;

    @SerializedName("siteid")
    private String siteid;

    @SerializedName("inputpercent")
    private String inputpercent;

    @SerializedName("outputvalue")
    private String outputvalue;

    @SerializedName("inputvalue")
    private String inputvalue;

    @SerializedName("point")
    private Integer point;

    @SerializedName("ron1upper")
    private String ron1upper;

    @SerializedName("ron1lower")
    private String ron1lower;

    @SerializedName("tol1lower")
    private String tol1lower;

    @SerializedName("tol1upper")
    private String tol1upper;

    @SerializedName("tol2lower")
    private String tol2lower;

    @SerializedName("tol2upper")
    private String tol2upper;

    @SerializedName("tol3lower")
    private String tol3lower;

    @SerializedName("tol3upper")
    private String tol3upper;

    @SerializedName("tol4lower")
    private String tol4lower;

    @SerializedName("tol4upper")
    private String tol4upper;

    @SerializedName("setpointaction")
    private String setpointaction;

    @SerializedName("setpointvalue")
    private String setpointvalue;

    @SerializedName("plantype")
    private String plantype;

    @SerializedName("pointdescription")
    private String pointdescription;

    @SerializedName("instrseq")
    private Integer instrseq;

    @SerializedName("direction")
    private String direction;

    @SerializedName("revisionnum")
    private Integer revisionnum;

    @SerializedName("ron1type")
    private String ron1type;

    @SerializedName("toleranceeu")
    private String toleranceeu;

    @SerializedName("setpointadj")
    private Integer setpointadj;

    @SerializedName("calpoint")
    private Integer calpoint;

    @SerializedName("calfunction")
    private Integer calfunction;

    @SerializedName("caldynamic")
    private Integer caldynamic;

    @SerializedName("hasld")
    private Integer hasld;

    @SerializedName("langcode")
    private String langcode;

    @Override
    public int setup() {
        SqlQueryBuilder builder = new SqlQueryBuilder();

        String statement = builder
            .setTableName(PluscDsPoint.TABLE_NAME)
            .putStrAttr("dsplannum", this.dsplannum)
            .putStrAttr("orgid", this.orgid)
            .putStrAttr("siteid", this.siteid)
            .putStrAttr("inputpercent", this.inputpercent)
            .putStrAttr("outputvalue", this.outputvalue)
            .putStrAttr("inputvalue", this.inputvalue)
            .putIntAttr("point", this.point)
            .putStrAttr("ron1upper", this.ron1upper)
            .putStrAttr("ron1lower", this.ron1lower)
            .putStrAttr("tol1lower", this.tol1lower)
            .putStrAttr("tol1upper", this.tol1upper)
            .putStrAttr("tol2lower", this.tol2lower)
            .putStrAttr("tol2upper", this.tol2upper)
            .putStrAttr("tol3lower", this.tol3lower)
            .putStrAttr("tol3upper", this.tol3upper)
            .putStrAttr("tol4lower", this.tol4lower)
            .putStrAttr("tol4upper", this.tol4upper)
            .putStrAttr("setpointaction", this.setpointaction)
            .putStrAttr("setpointvalue", this.setpointvalue)
            .putStrAttr("plantype", this.plantype)
            .putStrAttr("pointdescription", this.pointdescription)
            .putStrAttr("direction", this.direction)
            .putIntAttr("revisionnum", this.revisionnum)
            .putStrAttr("ron1type", this.ron1type)
            .putStrAttr("toleranceeu", this.toleranceeu)
            .putIntAttr("setpointadj", this.setpointadj)
            .putIntAttr("calpoint", this.calpoint)
            .putIntAttr("calfunction", this.calfunction)
            .putIntAttr("caldynamic", this.caldynamic)
            .putIntAttr("hasld", this.hasld)
            .putStrAttr("langcode", this.langcode)
            .putIntAttr("instrseq", this.instrseq)
            .createInsertStatement();

        return this.executeUpdateSQL(statement);
    }

    @Override
    public int teardown() {
        SqlQueryBuilder builder = new SqlQueryBuilder();

        String statement = builder
            .setTableName(PluscDsPoint.TABLE_NAME)
            .setWhereClause(String.format("dsplannum = '%s' and point = %s and instrseq = %s", this.dsplannum, this.point.toString(), this.instrseq.toString()))
            .createDeleteStatement();

        return this.executeUpdateSQL(statement);
    }

    public void setInstrseq(Integer instrseq) {
        this.instrseq = instrseq;
    }
}
