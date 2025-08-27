package com.ibm.maximo.technician.testcases.calibration.support.utils.sql;

import java.math.BigInteger;
import java.util.*;

public class SqlQueryBuilder {

    public static final String STATEMENT_SEPARATOR = "\n";
    
    public static final String ID_SUFFIX = "ID";
    public static final String SEQUENCE_SUFFIX = "SEQ.NEXTVAL";
    
    public static final String DEFAULT_SCHEMA = "MAXIMO";

    HashMap<String, String> attributes = new HashMap<>();

    private String tableName = null;

    private String databaseSchema = null;

    private String[] fields = null;

    private String variableName = null;

    private String variableType = null;

    private String variableValue = null;

    private String whereClause = null;

    public SqlQueryBuilder() {
    	this(DEFAULT_SCHEMA);
    }
    
    public SqlQueryBuilder(String databaseSchema) {
    	this.databaseSchema = databaseSchema;
    }

    public SqlQueryBuilder setWhereClause(String whereClause) {
        this.whereClause = whereClause;
        return this;
    }

    public SqlQueryBuilder putBoolAttr(String name, Boolean value) {
        this.attributes.put(name, value ? "1" : "0");
        return this;
    }

    public SqlQueryBuilder putStrAttr(String name, String value) {
        String valueStr = value == null ? "null" : "'" + value + "'";
        this.attributes.put(name, valueStr);
        return this;
    }

    public SqlQueryBuilder putSysAttr(String name, String value) {
        this.attributes.put(name, value);
        return this;
    }

    public SqlQueryBuilder putBigIntAttr(String name, BigInteger value) {
        this.attributes.put(name, value == null ? "null" : value.toString());
        return this;
    }

    public SqlQueryBuilder putIntAttr(String name, Integer value) {
        this.attributes.put(name, value == null ? "null" : value.toString());
        return this;
    }

    public SqlQueryBuilder putDecimalAttr(String name, String value) {
        this.attributes.put(name, value);
        return this;
    }

    public SqlQueryBuilder putFloatAttr(String name, Float value) {
        this.attributes.put(name, value != null ? Float.toString(value) : "null");
        return this;
    }

    public SqlQueryBuilder setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    };

    public SqlQueryBuilder setFields(String[] fields) {
        this.fields = fields;
        return this;
    }

    public String createVariable() {

        String statement1 = String.format("DROP VARIABLE %s.%s;", this.databaseSchema, this.variableName);
        String statement2 = String.format("CREATE VARIABLE %s.%s %s;", this.databaseSchema, this.variableName, this.variableType);
        String statement3 = this.variableValue != null
                ? String.format("SET %s.%s = (%s);", this.databaseSchema, this.variableName, this.variableValue)
                : "";

        this.clear();

        return statement1 + " " + statement2 + " " + statement3;
    }

    public void clear() {
        this.tableName = null;
        this.fields = null;
        this.attributes = new HashMap<>();
        this.variableName = null;
        this.variableType = null;
        this.variableValue = null;
        this.whereClause = "";
    }

    public String createDeleteStatement() {
        String statement = String.format(
                "DELETE FROM %s WHERE %s",
                this.getTableName(),
                this.whereClause
        );

        this.clear();

        return statement;
    }

    public String createInsertStatement() {
    	/**
    	 * populate Id attribute. 
    	 * e.g. {PLUSDSPLAN}ID : {MAXIMO.PLUSDSPLAN}SEQ.NEXTVAL
    	 */
    	this.attributes.put(this.tableName + ID_SUFFIX, this.getTableName() + SEQUENCE_SUFFIX);
    	
        Set<String> attributesSet = this.attributes.keySet();
        String[] attributeNames = attributesSet.toArray(new String[0]);
        List<String> attributeValues = new ArrayList<>();

        for (String fieldName : attributeNames) {
            String fieldValue = this.attributes.get(fieldName);
            attributeValues.add(fieldValue);
        }

        String query = String.format(
                "INSERT INTO %s (%s) VALUES (%s)",
                this.getTableName(),
                String.join(", ", attributeNames),
                String.join(", ", attributeValues.toArray(new String[0]))
        );

        this.clear();

        return query;
    }

    public String createSelectStatement() {

        String select = String.format(
            "SELECT %s FROM %s",
            String.join(", ", this.fields),
            this.getTableName()
        );

        String whereClause = this.whereClause != null
            ? String.format(" WHERE %s", this.whereClause)
            : "";

        this.clear();

        return select + whereClause;
    }
    
    public String createUpdateStatement(String dsplannum) throws Exception {
    	if(dsplannum == null || dsplannum.isBlank())
    		throw new IllegalArgumentException("Must specify a dsplannum for update statement");
    	
    	String dsplanFilterClause = String.format("dsplannum = '%s'", dsplannum);
    	
    	if(this.whereClause != null && !this.whereClause.isBlank())
    		this.whereClause = dsplanFilterClause + String.format(" and (%s)", this.whereClause);
    	else
    		this.whereClause = dsplanFilterClause;
    	
    	Set<String> attributesSet = this.attributes.keySet();
        String[] attributeNames = attributesSet.toArray(new String[0]);

        List<String> updateAttrs = new ArrayList<>();
        
        for (String fieldName : attributeNames) {
            String fieldValue = this.attributes.get(fieldName);
            updateAttrs.add(String.format("%s = %s", fieldName, fieldValue));
        }
        
        String query = String.format(
                "UPDATE %s SET %s WHERE %s",
                this.getTableName(),
                String.join(", ", updateAttrs),
                this.whereClause
        );

        this.clear();

        return query;
    }

    public String createSetSchemaStatement() {
        String statement = "SET SCHEMA " + this.databaseSchema + ";";
        this.clear();
        return statement;
    }

    public SqlQueryBuilder setDatabaseSchema(String databaseSchema) {
        this.databaseSchema = databaseSchema;
        return this;
    }

    public SqlQueryBuilder setVariable(String schema, String variableName, String variableType, String variableValue) {
        this.databaseSchema = schema;
        this.variableName = variableName;
        this.variableType = variableType;
        this.variableValue = variableValue;
        return this;
    }

    public String getTableName() {
        return this.databaseSchema != null
                ? this.databaseSchema + "." + this.tableName
                : this.tableName;
    }
}
