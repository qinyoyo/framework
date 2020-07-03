/**************************************************
    Create by iotequ generator 3.0.0
    Author : Qinyoyo
**************************************************/

package top.iotequ.framework.dao;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import top.iotequ.framework.service.IDaoService;
import top.iotequ.framework.pojo.Task;
import java.util.*;

//  Mapper dao : TaskDao (调度任务数据访问接口)
public interface TaskDao  extends IDaoService<Task> {
    int insert(Task record);
    int insertBatchWithoutId(List<Task> list);
    int insertBatchWithId(List<Task> list);
    Task select(Integer id);
    int delete(Integer id);
    int deleteBatch(String ids);
    int deleteList(List<Task> list);
    int update(Task record);
    int updateSelective(Task record);
    int updateBy(@Param("record")Task record,@Param("id")Integer id);
    //条件为所有非空字段 and，String采用like的查询模式，其他为=
    List<Task> list(Task record);
    //!!!  如 whereString="user_name like '王%'" ,orderString="born_date desc,employee_no asc"
    List<Task> listBy(@Param("whereString")String whereString,@Param("orderString")String orderString);
    List<Task> query(@Param("sqlString")String sqlString);
    @Update("${sqlString}")
    int execute(@Param("sqlString") String sqlString);
}
