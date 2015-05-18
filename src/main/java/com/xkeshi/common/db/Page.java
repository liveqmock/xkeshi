package com.xkeshi.common.db;

import java.io.Serializable;

/**
 * <p>
 * 分页接口.
 * </p>
 */
public interface Page extends Serializable {

    int getCurrentPage();

    boolean isNext();

    boolean isPrevious();

    int getPageEndRow();

    int getPageSize();

    int getPageStartRow();

    int getTotalPages();

    int getTotalRows();

    void setTotalPages(int i);

    void setCurrentPage(int i);

    void setNext(boolean b);

    void setPrevious(boolean b);

    void setPageEndRow(int i);

    void setPageSize(int i);

    void setPageStartRow(int i);

    void setTotalRows(int i);

    void initPagination(int rows, int pageSize, int currentPage);
}
