/**************************************************
    Create by iotequ generator 3.0.0
    Author : Qinyoyo
**************************************************/

package top.iotequ.framework.dao;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import top.iotequ.framework.service.IDaoService;
import top.iotequ.framework.pojo.DataDict;
import java.util.*;

//  Mapper dao : DataDictDao (数据字典数据访问接口)
public interface DataDictDao  extends IDaoService<DataDict> {
    int insert(DataDict record);
    int insertBatchWithoutId(List<DataDict> list);
    int insertBatchWithId(List<DataDict> list);
    DataDict select(Integer id);
    int delete(Integer id);
    int deleteBatch(String ids);
    DataDict selectByDictCode(@Param("dict")String dict , @Param("code")String code);
    int deleteByDictCode(@Param("dict")String dict , @Param("code")String code);
    int deleteList(List<DataDict> list);
    int update(DataDict record);
    int updateSelective(DataDict record);
    int updateBy(@Param("record")DataDict record,@Param("id")Integer id);
    //条件为所有非空字段 and，String采用like的查询模式，其他为=
    List<DataDict> list(DataDict record);
    //!!!  如 whereString="user_name like '王%'" ,orderString="born_date desc,employee_no asc"
    List<DataDict> listBy(@Param("whereString")String whereString,@Param("orderString")String orderString);
    List<DataDict> query(@Param("sqlString")String sqlString);
    @Update("${sqlString}")
    int execute(@Param("sqlString") String sqlString);
}
