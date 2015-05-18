package com.xkeshi.common.db;

/**
 * <p>
 * 查询参数.
 * </p>
 *
 */
public class Query implements Page,Sort {
    /**
     * 序列化ID
     */
    private static final long serialVersionUID = 8919076199499894558L;
    /**
     * 每页默认100条数据
     */
    protected int pageSize = 100;
    /**
     * 当前页
     */
    protected int currentPage = 1;
    /**
     * 总页数
     */
    protected int totalPages = 0;
    /**
     * 总数据数 默认10000
     */
    protected int totalRows = 10000;
    /**
     * 每页的起始行数
     */
    protected int pageStartRow = 0;
    /**
     * 每页显示数据的终止行数
     */
    protected int pageEndRow = 0;
    /**
     * 是否有下一页
     */
    boolean next = false;
    /**
     * 是否有前一页
     */
    boolean previous = false;
    
    
    /**
     * 当前页起始行序号（序号从1开始计数）
     */
    protected int currentRowStart;

    
    /**
     * 当前页结束行序号（序号从1开始计数）
     */
    protected int currentRowEnd;
    
    
    
    //---------------排序----------------------------
    protected String orderColumns;//用于排序的列明，多个用逗号连接
    
	protected OrderType orderType = OrderType.ASC; //排序方式，默认为asc

    /**
     * 无总计数时使用
     * @param pageSize
     */
    public Query(int pageSize) {
        this.init(pageSize);
    }

    public Query() {
    }
    
    /**
     * 
     * 无总计数时使用
     * @param pageSize
     * @param currentPage
     */
    public Query(int pageSize, int currentPage){
    	this.init(pageSize, currentPage);
    }
    
 
    /**
     * 初始化查询（推荐使用）
     * 
     * @param totalRows
     * @param pageSize
     * @param currentPage
     */
    public Query(int totalRows,int pageSize, int currentPage){
    	this.initPagination(totalRows, pageSize, currentPage);
    }
    

    /**
     * 初始化分页参数
     */
    public void init(int pageSize) {
    	this.init(pageSize,1);
    }
    
    
    /**
     * 初始化分页参数
     */
    public void init(int pageSize,int currentPage) {

        this.pageSize = pageSize;
        
        this.currentPage = currentPage;

        if ((totalRows % pageSize) == 0) {
            totalPages = totalRows / pageSize;
        } else {
            totalPages = totalRows / pageSize + 1;
        }

    }

    @Override
    public void initPagination(int rows, int pageSize, int currentPage) {

        this.pageSize = pageSize;

        this.totalRows = rows;

        if ((totalRows % pageSize) == 0) {
            totalPages = totalRows / pageSize;
        } else {
            totalPages = totalRows / pageSize + 1;
        }
        if (currentPage != 0)
            gotoPage(currentPage);
    }
    
    /**
     * 计算当前页的取值范围：pageStartRow和pageEndRow
     */
    private void calculatePage() {
    	 if ((totalRows % pageSize) == 0) {
             totalPages = totalRows / pageSize;
         } else {
             totalPages = totalRows / pageSize + 1;
         }
    	
    	previous = (currentPage - 1) > 0;
    	
    	next = currentPage < totalPages;
    	
    	if (currentPage * pageSize < totalRows) { // 判断是否为最后一页
    		pageEndRow = currentPage * pageSize;
    		pageStartRow = pageEndRow - pageSize;
    	} else {
    		pageEndRow = totalRows;
    		pageStartRow = pageSize * (totalPages - 1);
    	}
    	
    }

    /**
     * 计算当前页的行取值范围
     * 
     * @param currentPageRowNum
     */
    public void calculatePage(int currentPageRowNum) {
    	this.calculatePage();
    	currentRowStart = pageStartRow + 1;
    	currentRowEnd = pageStartRow + currentPageRowNum;
    }

    /**
     * 直接跳转到指定页数的页面
     *
     * @param page
     */
    public void gotoPage(int page) {
    	
    	currentPage = page;
    	
    	calculatePage();
    	
    }
    
    /**
     * 跳转到上一页
     *
     */
    public void gotoPreviousPage() {
    	gotoPage(currentPage-1);
    }
    
    /**
     * 跳转到下一页
     *
     */
    public void gotoNextPage() {
    	gotoPage(currentPage+1);
    }

    public String debugString() {

        return "共有数据数:" + totalRows + "共有页数:" + totalPages + "当前页数为:"
                + currentPage + "是否有前一页:" + previous + "是否有下一页:"
                + next + "开始行数:" + pageStartRow + "终止行数:" + pageEndRow;

    }


    @Override
    public int getCurrentPage() {
        return currentPage;
    }


    @Override
    public boolean isNext() {
        return next;
    }


    @Override
    public boolean isPrevious() {
        return previous;
    }


    @Override
    public int getPageEndRow() {
        return pageEndRow;
    }


    @Override
    public int getPageSize() {
        return pageSize;
    }


    @Override
    public int getPageStartRow() {
        return pageStartRow;
    }


    @Override
    public int getTotalPages() {
        return totalPages;
    }


    @Override
    public int getTotalRows() {
        return totalRows;
    }


    @Override
    public void setTotalPages(int i) {
        totalPages = i;
    }


    @Override
    public void setCurrentPage(int i) {
        currentPage = i;
    }


    @Override
    public void setNext(boolean b) {
        next = b;
    }


    @Override
    public void setPrevious(boolean b) {
        previous = b;
    }


    @Override
    public void setPageEndRow(int i) {
        pageEndRow = i;
    }


    @Override
    public void setPageSize(int i) {
        pageSize = i;
    }


    @Override
    public void setPageStartRow(int i) {
        pageStartRow = i;
    }


    @Override
    public void setTotalRows(int i) {
        totalRows = i;
    }
    
    @Override
	public String getOrderColumns() {
		return orderColumns;
	}

	@Override
	public void setOrderColumns(String orderColumns) {
		this.orderColumns = orderColumns;
	}

	@Override
	public OrderType getOrderType() {
		return orderType;
	}

	@Override
	public void setOrderType(OrderType orderType) {
		this.orderType = orderType;
	}

	public int getCurrentRowStart() {
		return currentRowStart < 0 ? 0 : currentRowStart; 
	}

	public void setCurrentRowStart(int currentRowStart) {
		this.currentRowStart = currentRowStart;
	}

	public int getCurrentRowEnd() {
		return currentRowEnd < 0 ? 0 : currentRowEnd; 
	}

	public void setCurrentRowEnd(int currentRowEnd) {
		this.currentRowEnd = currentRowEnd;
	}
	
	
	
	
}
