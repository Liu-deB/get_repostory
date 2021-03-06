package com.usian.utils;

import java.io.Serializable;
import java.util.List;

public class PageResult implements Serializable {
    private Integer pageIndex;
    private Long totalPage;
    private List result;

    public PageResult() {
    }

    public Integer getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
    }

    public Long getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Long totalPage) {
        this.totalPage = totalPage;
    }

    public List getResult() {
        return result;
    }

    public void setResult(List result) {
        this.result = result;
    }

    public PageResult(Integer pageIndex, Long totalPage, List result) {
        this.pageIndex = pageIndex;
        this.totalPage = totalPage;
        this.result = result;
    }
}
