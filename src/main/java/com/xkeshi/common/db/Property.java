package com.xkeshi.common.db;

import org.apache.commons.lang3.StringUtils;

/**
 * <br>Author: David <br>
 * 14-5-5.
 */
public class Property {


    private String name;
    private String value;
    private String operator;

    public Property(String name, String value) {
        this.name = name;
        this.value = value;
        this.operator = "and";
    }

    public Property(String name, String value, String operator) {
        this.name = name;
        this.value = value;
        this.operator = StringUtils.equals(operator, "and") ? "and" : "or";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
}
