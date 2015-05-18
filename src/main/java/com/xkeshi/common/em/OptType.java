package com.xkeshi.common.em;

/**
 * 操作类型(包含增删改查)
 * <br>
 * User: David <br>
 * Date: 13-11-26 <br>
 */
public enum OptType {
    CREATE("C"),
    READ("R"),
    UPDATE("U"),
    DELETE("D");

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    private String value;

    private OptType(String value) {
        this.value = value;
    }

    @Override
    public String toString(){
        return value;
    }
}
