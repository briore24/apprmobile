package com.ibm.maximo.technician.testcases.calibration.support.factories;

import com.google.gson.Gson;
import com.ibm.maximo.automation.mobile.api.JdbcConnection;

public class BaseFactory {
    protected String systemUsername;

    protected Gson gson;

    private JdbcConnection jdbcConnection;

    public BaseFactory() {
    	this.gson = new Gson();
    }
    
    public String getSystemUsername() {
        return systemUsername;
    }

    public BaseFactory setSystemUsername(String systemUsername) {
        this.systemUsername = systemUsername;
        return this;
    }

    public JdbcConnection getJdbcConnection() {
        return jdbcConnection;
    }

    public BaseFactory setJdbcConnection(JdbcConnection jdbcConnection) {
        this.jdbcConnection = jdbcConnection;
        return this;
    }
}
