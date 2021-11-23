package com.ctdw.project;

import java.util.List;

/**
 * <p>分页控制器</p>
 *
 * @author : yzh
 * @date : 2021-11-10 10:26
 **/
public class PageHandle {
    public static ThreadLocal<Integer> totalCount = new ThreadLocal<>();
    public static ThreadLocal<Integer> totalPage = new ThreadLocal<>();

    public static <T> Page<T> startPage(List<T> list) {
        Page<T> page = new Page<>();
        try {
            page.setTotalPage(totalPage.get());
            page.setTotalCount(totalCount.get());
            page.setResult(list);
        } finally {
            totalCount.remove();
            totalPage.remove();
        }
        return page;
    }
}
