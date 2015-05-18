package com.xpos.common.utils;

import java.util.List;

public class Pager<E> {
	public static final Integer MAX_PAGE_SIZE = Integer.MAX_VALUE;// 每页最大记录数限制

	private Integer pageNumber = 1;// 当前页码
	private Integer pageSize = 20;// 每页记录数
	private Integer totalCount = 0;// 总记录数
	private Integer pageCount = 0;// 总页数
	private List<E> list;// 数据List
	private Integer startRow;
	
	public void setStartRow(Integer startRow) {
		this.startRow = startRow;
	}

	/**
	 * 冗余的方法 等待更加好的
	 * */
	public Integer getP() {
		return pageNumber;
	}

	/**
	 * 冗余的方法 等待更加好的
	 * */
	public void setP(Integer pageNumber) {
		this.setPageNumber(pageNumber);
	}

	public Integer getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(Integer pageNumber) {
		if (pageNumber < 1) {
			pageNumber = 1;
		}
		this.pageNumber = pageNumber;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		if (pageSize < 1) {
			pageSize = 1;
		} else if (pageSize > MAX_PAGE_SIZE) {
			pageSize = MAX_PAGE_SIZE;
		}
		this.pageSize = pageSize;
	}

	public Integer getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
		if (pageSize == 0) {
			pageCount = 0;
		} else {
			pageCount = (totalCount + pageSize - 1) / pageSize;
		}
	}

	public Integer getPageCount() {
		return pageCount;
	}

	public void setPageCount(Integer pageCount) {
		this.pageCount = pageCount;
	}

	public Long getStartRecord() {
		return (pageNumber - 1L) * pageSize;
	}

	public List<E> getList() {
		return list;
	}

	public void setList(List<E> list) {
		this.list = list;
	}

	/**
	 * 获取limit 开始的数量（为了程序方便）
	 * */
	public Integer getStartNumber() {
		if (pageNumber < 2) {
			return 0;
		}
		return (this.pageNumber - 1) * this.pageSize;
	}

	/**
	 * 获取limit 结束的数量（为了程序方便）
	 * */
	public Integer getEndNumber() {
		return this.pageSize;
	}

	public boolean isNext() {
		if (this.pageCount > this.pageNumber) {
			return true;
		}
		return false;
	}

	public boolean isForward() {
		if (this.pageNumber <= 1) {
			return false;
		}
		return true;
	}

	public Integer getStartRow() {
		return startRow;
	}
}
