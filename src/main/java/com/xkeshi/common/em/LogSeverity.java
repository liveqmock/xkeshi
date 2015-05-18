package com.xkeshi.common.em;

/**
 * 日志操作严重级别
 * <br>
 * User: David <br>
 * Date: 13-11-26 <br>
 */
public enum LogSeverity {
    INFO(1),
    Warn(2),
    Error(3);

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    private int index;

    private LogSeverity(int index) {
        this.index = index;
    }

    @Override
    public String toString(){
        return this.name().toLowerCase();
    }



}
