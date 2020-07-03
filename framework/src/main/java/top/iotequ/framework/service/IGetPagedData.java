package top.iotequ.framework.service;

import com.github.pagehelper.PageInfo;

public interface IGetPagedData<T> {
    PageInfo<T> getPage(int pageNum, int pageSize);
}
