package com.ctdw.project;


import java.util.List;

/**
 * <p>分页对象</p>
 *
 * @author : yzh
 * @date : 2021-11-05 17:14
 **/
public class Page<T> {
    private Integer totalCount;
    private Integer totalPage;
    private List<T> result;

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public List<T> getResult() {
        return result;
    }

    public void setResult(List<T> result) {
        this.result = result;
    }
}
