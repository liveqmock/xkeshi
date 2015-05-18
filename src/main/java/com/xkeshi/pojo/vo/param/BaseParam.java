package com.xkeshi.pojo.vo.param;

/**
 * 公共分页参数
 * <p/>
 * <br>
 * User: David <br>
 * Date: 13-8-16 <br>
 */
public abstract class BaseParam {

    String param;
    int pageTo = 1;
    String orderBy ;
    int orderType = 0;
    int isPageRequest = 0;

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public int getPageTo() {
        return pageTo;
    }

    public void setPageTo(int pageTo) {
        this.pageTo = pageTo;
    }

    public abstract String getOrderBy();

    public void setOrderBy(String orderBy){
        this.orderBy = orderBy;
    }


    public abstract int getOrderType();

    public void setOrderType(int orderType){
        this.orderType = orderType;
    }

    public int getPageRequest() {
        //若不是通过上下页点击
        if (isPageRequest == 0){
            pageTo = 1;
        }
        return isPageRequest;
    }

    public void setPageRequest(int pageRequest) {
        isPageRequest = pageRequest;
    }
}
