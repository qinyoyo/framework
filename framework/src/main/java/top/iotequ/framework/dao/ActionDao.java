/**************************************************
    Create by iotequ generator 3.0.0
    Author : Qinyoyo
**************************************************/

package top.iotequ.framework.dao;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import top.iotequ.framework.service.IDaoService;
import top.iotequ.framework.pojo.Action;
import java.util.*;

//  Mapper dao : ActionDao (功能列表数据访问接口)
public interface ActionDao  extends IDaoService<Action> {
    int insert(Action record);
    int insertBatchWithoutId(List<Action> list);
    int insertBatchWithId(List<Action> list);
    Action select(Integer id);
    int delete(Integer id);
    int deleteBatch(String ids);
    List<Action> selectByValue(String value);
    int deleteByValue(String value);
    int deleteBatchByValue(String values);
    int deleteList(List<Action> list);
    int update(Action record);
    int updateSelective(Action record);
    int updateBy(@Param("record")Action record,@Param("id")Integer id);
    //条件为所有非空字段 and，String采用like的查询模式，其他为=
    List<Action> list(Action record);
    //!!!  如 whereString="user_name like '王%'" ,orderString="born_date desc,employee_no asc"
    List<Action> listBy(@Param("whereString")String whereString,@Param("orderString")String orderString);
    List<Action> query(@Param("sqlString")String sqlString);
    @Update("${sqlString}")
    int execute(@Param("sqlString") String sqlString);
}
