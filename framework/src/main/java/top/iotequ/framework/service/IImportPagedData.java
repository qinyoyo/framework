package top.iotequ.framework.service;

import java.util.List;

public interface IImportPagedData<T> {
    String saveData(List<T> data,int startProgress,int endProgress) throws Exception;
}
