package com.ibm.maximo.technician.testcases.calibration.support.objects.maximo;

import com.google.gson.annotations.SerializedName;
import com.ibm.maximo.technician.testcases.calibration.support.utils.sql.SqlQueryBuilder;

import java.math.BigInteger;
import java.util.List;
import java.util.Objects;

public class PluscDsInstr extends CalibrationMaximoObject implements CalibrationMaximoObjectInterface {

    private static final String RESOURCE = "pluscdsinstr";

    private static final String TABLE_NAME = "PLUSCDSINSTR";

    @SerializedName("description")
    private String description;

    @SerializedName("orgid")
    private String orgid;

    @SerializedName("siteid")
    private String siteid;

    @SerializedName("plantype")
    private String plantype;

    @SerializedName("instrcalrangefrom")
    private String instrcalrangefrom;

    @SerializedName("instrcalrangeto")
    private String instrcalrangeto;

    @SerializedName("instrcalrangeeu")
    private String instrcalrangeeu;

    @SerializedName("instroutrangefrom")
    private String instroutrangefrom;

    @SerializedName("instroutrangeto")
    private String instroutrangeto;

    @SerializedName("instroutrangeeu")
    private String instroutrangeeu;

    @SerializedName("inputprecision")
    private Integer inputprecision;

    @SerializedName("outputprecision")
    private Integer outputprecision;

    @SerializedName("processeu")
    private String processeu;

    @SerializedName("processeufactor")
    private String processeufactor;

    @SerializedName("tol1type")
    private String tol1type;

    @SerializedName("tol1lowervalue")
    private String tol1lowervalue;

    @SerializedName("tol1uppervalue")
    private String tol1uppervalue;

    @SerializedName("tol2type")
    private String tol2type;

    @SerializedName("tol2lowervalue")
    private String tol2lowervalue;

    @SerializedName("tol2uppervalue")
    private String tol2uppervalue;

    @SerializedName("tol3type")
    private String tol3type;

    @SerializedName("tol3lowervalue")
    private String tol3lowervalue;

    @SerializedName("tol3uppervalue")
    private String tol3uppervalue;

    @SerializedName("tol4type")
    private String tol4type;

    @SerializedName("tol4lowervalue")
    private String tol4lowervalue;

    @SerializedName("tol4uppervalue")
    private String tol4uppervalue;

    @SerializedName("ron1type")
    private String ron1type;

    @SerializedName("ron1lowervalue")
    private String ron1lowervalue;

    @SerializedName("ron1uppervalue")
    private String ron1uppervalue;

    @SerializedName("tol1status")
    private String tol1status;

    @SerializedName("tol2status")
    private String tol2status;

    @SerializedName("tol3status")
    private String tol3status;

    @SerializedName("tol4status")
    private String tol4status;

    @SerializedName("noadjmadechoice")
    private Integer noadjmadechoice;

    @SerializedName("allowpointinserts")
    private Integer allowpointinserts;

    @SerializedName("instrseq")
    private Integer instrseq;

    @SerializedName("squareroot")
    private Integer squareroot;

    @SerializedName("cliplimits")
    private Integer cliplimits;

    @SerializedName("tol1sumeu")
    private String tol1sumeu;

    @SerializedName("tol2sumeu")
    private String tol2sumeu;

    @SerializedName("tol3sumeu")
    private String tol3sumeu;

    @SerializedName("tol4sumeu")
    private String tol4sumeu;

    @SerializedName("tol1sumspan")
    private String tol1sumspan;

    @SerializedName("tol2sumspan")
    private String tol2sumspan;

    @SerializedName("tol3sumspan")
    private String tol3sumspan;

    @SerializedName("tol4sumspan")
    private String tol4sumspan;

    @SerializedName("tol1sumurv")
    private String tol1sumurv;

    @SerializedName("tol2sumurv")
    private String tol2sumurv;

    @SerializedName("tol3sumurv")
    private String tol3sumurv;

    @SerializedName("tol4sumurv")
    private String tol4sumurv;

    @SerializedName("tol1sumread")
    private String tol1sumread;

    @SerializedName("tol2sumread")
    private String tol2sumread;

    @SerializedName("tol3sumread")
    private String tol3sumread;

    @SerializedName("tol4sumread")
    private String tol4sumread;

    @SerializedName("tol1sumdirection")
    private String tol1sumdirection;

    @SerializedName("tol2sumdirection")
    private String tol2sumdirection;

    @SerializedName("tol3sumdirection")
    private String tol3sumdirection;

    @SerializedName("tol4sumdirection")
    private String tol4sumdirection;

    @SerializedName("tol1noadjlimit")
    private Integer tol1noadjlimit;

    @SerializedName("tol2noadjlimit")
    private Integer tol2noadjlimit;

    @SerializedName("tol3noadjlimit")
    private Integer tol3noadjlimit;

    @SerializedName("tol4noadjlimit")
    private Integer tol4noadjlimit;

    @SerializedName("assetfunction")
    private Integer assetfunction;

    @SerializedName("tol1description")
    private String tol1description;

    @SerializedName("tol2description")
    private String tol2description;

    @SerializedName("tol3description")
    private String tol3description;

    @SerializedName("tol4description")
    private String tol4description;

    @SerializedName("inputrange")
    private Integer inputrange;

    @SerializedName("outputrange")
    private Integer outputrange;

    @SerializedName("noadjmadechoice1")
    private Integer noadjmadechoice1;

    @SerializedName("noadjmadechoice2")
    private Integer noadjmadechoice2;

    @SerializedName("noadjmadechoice3")
    private Integer noadjmadechoice3;

    @SerializedName("noadjmadechoice4")
    private Integer noadjmadechoice4;

    @SerializedName("revisionnum")
    private Integer revisionnum;

    @SerializedName("calpoint")
    private Integer calpoint;

    @SerializedName("calfunction")
    private Integer calfunction;

    @SerializedName("caldynamic")
    private Integer caldynamic;

    @SerializedName("manual")
    private Integer manual;

    @SerializedName("nonlinear")
    private Integer nonlinear;

    @SerializedName("repeatable")
    private Integer repeatable;

    @SerializedName("squared")
    private Integer squared;

    @SerializedName("cliplimitsin")
    private Integer cliplimitsin;

    @SerializedName("pluscdsinstrid")
    private BigInteger pluscdsinstrid;

    @SerializedName("hasld")
    private Integer hasld;

    @SerializedName("langcode")
    private String langcode;

    @SerializedName("confidlevel")
    private String confidlevel;

    @SerializedName("gbfrom1")
    private String gbfrom1;

    @SerializedName("gbfrom2")
    private String gbfrom2;

    @SerializedName("gbfrom3")
    private String gbfrom3;

    @SerializedName("gbfrom4")
    private String gbfrom4;

    @SerializedName("gbmethod")
    private String gbmethod;

    @SerializedName("gbsumdirection1")
    private String gbsumdirection1;

    @SerializedName("gbsumdirection2")
    private String gbsumdirection2;

    @SerializedName("gbsumdirection3")
    private String gbsumdirection3;

    @SerializedName("gbsumdirection4")
    private String gbsumdirection4;

    @SerializedName("gbto1")
    private String gbto1;

    @SerializedName("gbto2")
    private String gbto2;

    @SerializedName("gbto3")
    private String gbto3;

    @SerializedName("gbto4")
    private String gbto4;

    @SerializedName("guardband")
    private String guardband;
    
    @SerializedName("isstandarddeviation")
    private Boolean isstandarddeviation = false;

    @SerializedName("kfactor")
    private String kfactor;

    @SerializedName("uncertfreq")
    private Integer uncertfreq;

    @SerializedName("uncertunits")
    private String uncertunits;

    @SerializedName("pluscdspoint")
    private List<PluscDsPoint> pluscdspoint;
    
    @SerializedName("standarddeviationinputlimit")
    private String standarddeviationinputlimit;
    
    @SerializedName("standarddeviationoutputlimit")
    private String standarddeviationoutputlimit;

    @Override
    public int setup() {
        SqlQueryBuilder builder = new SqlQueryBuilder();

        String statement = builder
            .setTableName(PluscDsInstr.TABLE_NAME)
            .putStrAttr("dsplannum", this.dsplannum)
            .putStrAttr("description", this.description)
            .putStrAttr("orgid", this.orgid)
            .putStrAttr("siteid", this.siteid)
            .putStrAttr("plantype", this.plantype)
            .putStrAttr("instrcalrangefrom", this.instrcalrangefrom)
            .putStrAttr("instrcalrangeto", this.instrcalrangeto)
            .putStrAttr("instrcalrangeeu", this.instrcalrangeeu)
            .putStrAttr("instroutrangefrom", this.instroutrangefrom)
            .putStrAttr("instroutrangeto", this.instroutrangeto)
            .putStrAttr("instroutrangeeu", this.instroutrangeeu)
            .putIntAttr("inputprecision", this.inputprecision)
            .putIntAttr("outputprecision", this.outputprecision)
            .putStrAttr("processeu", this.processeu)
            .putStrAttr("processeufactor", this.processeufactor)
            .putStrAttr("tol1type", this.tol1type)
            .putStrAttr("tol1lowervalue", this.tol1lowervalue)
            .putStrAttr("tol1uppervalue", this.tol1uppervalue)
            .putStrAttr("tol2type", this.tol2type)
            .putStrAttr("tol2lowervalue", this.tol2lowervalue)
            .putStrAttr("tol2uppervalue", this.tol2uppervalue)
            .putStrAttr("tol3type", this.tol3type)
            .putStrAttr("tol3lowervalue", this.tol3lowervalue)
            .putStrAttr("tol3uppervalue", this.tol3uppervalue)
            .putStrAttr("tol4type", this.tol4type)
            .putStrAttr("tol4lowervalue", this.tol4lowervalue)
            .putStrAttr("tol4uppervalue", this.tol4uppervalue)
            .putStrAttr("ron1type", this.ron1type)
            .putStrAttr("ron1lowervalue", this.ron1lowervalue)
            .putStrAttr("ron1uppervalue", this.ron1uppervalue)
            .putStrAttr("tol1status", this.tol1status)
            .putStrAttr("tol2status", this.tol2status)
            .putStrAttr("tol3status", this.tol3status)
            .putStrAttr("tol4status", this.tol4status)
            .putIntAttr("noadjmadechoice", this.noadjmadechoice)
            .putIntAttr("allowpointinserts", this.allowpointinserts)
            .putIntAttr("instrseq", this.instrseq)
            .putIntAttr("squareroot", this.squareroot)
            .putIntAttr("cliplimits", this.cliplimits)
            .putStrAttr("tol1sumeu", this.tol1sumeu)
            .putStrAttr("tol2sumeu", this.tol2sumeu)
            .putStrAttr("tol3sumeu", this.tol3sumeu)
            .putStrAttr("tol4sumeu", this.tol4sumeu)
            .putStrAttr("tol1sumspan", this.tol1sumspan)
            .putStrAttr("tol2sumspan", this.tol2sumspan)
            .putStrAttr("tol3sumspan", this.tol3sumspan)
            .putStrAttr("tol4sumspan", this.tol4sumspan)
            .putStrAttr("tol1sumurv", this.tol1sumurv)
            .putStrAttr("tol2sumurv", this.tol2sumurv)
            .putStrAttr("tol3sumurv", this.tol3sumurv)
            .putStrAttr("tol4sumurv", this.tol4sumurv)
            .putStrAttr("tol1sumread", this.tol1sumread)
            .putStrAttr("tol2sumread", this.tol2sumread)
            .putStrAttr("tol3sumread", this.tol3sumread)
            .putStrAttr("tol4sumread", this.tol4sumread)
            .putStrAttr("tol1sumdirection", this.tol1sumdirection)
            .putStrAttr("tol2sumdirection", this.tol2sumdirection)
            .putStrAttr("tol3sumdirection", this.tol3sumdirection)
            .putStrAttr("tol4sumdirection", this.tol4sumdirection)
            .putIntAttr("tol1noadjlimit", this.tol1noadjlimit)
            .putIntAttr("tol2noadjlimit", this.tol2noadjlimit)
            .putIntAttr("tol3noadjlimit", this.tol3noadjlimit)
            .putIntAttr("tol4noadjlimit", this.tol4noadjlimit)
            .putIntAttr("assetfunction", this.assetfunction)
            .putStrAttr("tol1description", this.tol1description)
            .putStrAttr("tol2description", this.tol2description)
            .putStrAttr("tol3description", this.tol3description)
            .putStrAttr("tol4description", this.tol4description)
            .putIntAttr("inputrange", this.inputrange)
            .putIntAttr("outputrange", this.outputrange)
            .putIntAttr("noadjmadechoice1", this.noadjmadechoice1)
            .putIntAttr("noadjmadechoice2", this.noadjmadechoice2)
            .putIntAttr("noadjmadechoice3", this.noadjmadechoice3)
            .putIntAttr("noadjmadechoice4", this.noadjmadechoice4)
            .putIntAttr("revisionnum", this.revisionnum)
            .putIntAttr("calpoint", this.calpoint)
            .putIntAttr("calfunction", this.calfunction)
            .putIntAttr("caldynamic", this.caldynamic)
            .putIntAttr("manual", this.manual)
            .putIntAttr("nonlinear", this.nonlinear)
            .putIntAttr("repeatable", this.repeatable)
            .putIntAttr("squared", this.squared)
            .putIntAttr("cliplimitsin", this.cliplimitsin)
            .putIntAttr("hasld", this.hasld)
            .putStrAttr("langcode", this.langcode)
            .putStrAttr("confidlevel", this.confidlevel)
            .putStrAttr("gbfrom1", this.gbfrom1)
            .putStrAttr("gbfrom2", this.gbfrom2)
            .putStrAttr("gbfrom3", this.gbfrom3)
            .putStrAttr("gbfrom4", this.gbfrom4)
            .putStrAttr("gbmethod", this.gbmethod)
            .putStrAttr("gbsumdirection1", this.gbsumdirection1)
            .putStrAttr("gbsumdirection2", this.gbsumdirection2)
            .putStrAttr("gbsumdirection3", this.gbsumdirection3)
            .putStrAttr("gbsumdirection4", this.gbsumdirection4)
            .putStrAttr("gbto1", this.gbto1)
            .putStrAttr("gbto2", this.gbto2)
            .putStrAttr("gbto3", this.gbto3)
            .putStrAttr("gbto4", this.gbto4)
            .putStrAttr("guardband", this.guardband)
            .putStrAttr("kfactor", this.kfactor)
            .putIntAttr("uncertfreq", this.uncertfreq)
            .putStrAttr("uncertunits", this.uncertunits)
            .putBoolAttr("isstandarddeviation", this.isstandarddeviation)
            .putDecimalAttr("standarddeviationinputlimit", this.standarddeviationinputlimit)
            .putDecimalAttr("standarddeviationoutputlimit", this.standarddeviationoutputlimit)
            .createInsertStatement();

        	int result = this.executeUpdateSQL(statement);

        	// Setting up child calibration points
        	for (PluscDsPoint pluscdspoint : this.pluscdspoint) {
        		pluscdspoint.setJdbcConnection(this.getJdbcConnection());
        		pluscdspoint.setDsplannum(this.dsplannum);
        		pluscdspoint.setInstrseq(this.instrseq);
        		
        		logger.info("Tearing Down " + pluscdspoint.getClass().getSimpleName());
        		pluscdspoint.teardown();
        		logger.info("Setting up " + pluscdspoint.getClass().getSimpleName());
        		pluscdspoint.setup();
        	}
            
            return result;
    }

    @Override
    public int teardown() {
        SqlQueryBuilder builder = new SqlQueryBuilder();

        String statement = builder
            .setTableName(PluscDsInstr.TABLE_NAME)
            .setWhereClause(String.format("dsplannum = '%s' and assetfunction = '%s'", this.dsplannum, this.assetfunction.toString()))
            .createDeleteStatement();

        return this.executeUpdateSQL(statement);
    }

    public Integer getInstrseq() {
        return instrseq;
    }

    public void setInstrseq(Integer instrseq) {
        this.instrseq = instrseq;
    }
    
    public Integer getAssetfunction() {
    	return this.assetfunction;
    }
    
    public void setAssetfunction(Integer assetfunction) {
    	this.assetfunction = assetfunction;
    }

	public static String getTableName() {
		return TABLE_NAME;
	}

	public String getDescription() {
		return description;
	}

	public String getStandarddeviationinputlimit() {
		return standarddeviationinputlimit;
	}

	public String getStandarddeviationoutputlimit() {
		return standarddeviationoutputlimit;
	}

	public Boolean getIsstandarddeviation() {
		return isstandarddeviation;
	}
    
}
