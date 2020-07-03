package top.iotequ.framework.service;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import top.iotequ.framework.pojo.CgEntity;
import java.util.List;

public interface IDaoService<T extends CgEntity> {
    int insert(T record);
    int insertBatchWithoutId(List<T> list);
    int insertBatchWithId(List<T> list);
    T select(Integer id);
    T select(String id);
    int delete(Integer id);
    int delete(String id);
    int deleteBatch(String ids);
    int deleteList(List<T> list);
    int update(T record);
    int updateSelective(T record);
    int updateBy(@Param("record")T record, @Param("id")Integer id);
    int updateBy(@Param("record")T record, @Param("id")String id);
    List<T> list(T record);
    List<T> listBy(@Param("whereString")String whereString,@Param("orderString")String orderString);
    List<T> query(@Param("sqlString")String sqlString);
    @Update("${sqlString}")
    int execute(@Param("sqlString") String sqlString);
}
