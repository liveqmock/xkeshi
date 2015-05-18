package com.xkeshi.common.em;

/**
 * 操作日志分组类型,用于不同视图中筛选需要显示的日志
 * <br>
 * User: David <br>
 * Date: 13-11-26 <br>
 */
public enum OptLogGroup {

    CLIENT_DETAIL,
    ASSET_DETAIL,
    EMPLOYEE_DETAIL,
    DEAL_ASSET_DETAIL,
    DEAL_ASSET_INTEREST_INVENTORY_DETAIL


    ;


    @Override
    public String toString(){
        return this.name().toLowerCase();
    }
}
