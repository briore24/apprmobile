package com.ibm.maximo.technician.testcases.calibration.support.objects.maximo;

import com.google.gson.annotations.SerializedName;
import com.ibm.maximo.automation.mobile.api.JdbcConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CalibrationMaximoObject {

    Logger logger = LoggerFactory.getLogger(CalibrationMaximoObject.class);

    @SerializedName("dsplannum")
    protected String dsplannum;

    private JdbcConnection jdbcConnection;
    
    public String getDsplannum() {
        return dsplannum;
    }

    public void setDsplannum(String dsplannum) {
        this.dsplannum = dsplannum;
    }

    public int setup() {
        return 0;
    }

    public int teardown() {
        return 0;
    }

    public int executeUpdateSQL(String query) {
        return jdbcConnection.executeUpdateSQL(query);
    }

    public JdbcConnection getJdbcConnection() {
        return jdbcConnection;
    }

    public void setJdbcConnection(JdbcConnection jdbcConnection) {
        this.jdbcConnection = jdbcConnection;
    }
}
