/**************************************************
    Create by iotequ generator 3.0.0
    Author : Qinyoyo
**************************************************/

package top.iotequ.framework.dao;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import top.iotequ.framework.service.IDaoService;
import top.iotequ.framework.pojo.Permission;
import java.util.*;

//  Mapper dao : PermissionDao (功能分配表数据访问接口)
public interface PermissionDao  extends IDaoService<Permission> {
    int insert(Permission record);
    int insertBatchWithoutId(List<Permission> list);
    int insertBatchWithId(List<Permission> list);
    Permission select(Integer id);
    int delete(Integer id);
    int deleteBatch(String ids);
    List<Permission> selectByRole(Integer role);
    int deleteByRole(Integer role);
    int deleteBatchByRole(String roles);
    List<Permission> selectByAction(Integer action);
    int deleteByAction(Integer action);
    int deleteBatchByAction(String actions);
    int deleteList(List<Permission> list);
    int update(Permission record);
    int updateSelective(Permission record);
    int updateBy(@Param("record")Permission record,@Param("id")Integer id);
    //条件为所有非空字段 and，String采用like的查询模式，其他为=
    List<Permission> list(Permission record);
    //!!!  如 whereString="user_name like '王%'" ,orderString="born_date desc,employee_no asc"
    List<Permission> listBy(@Param("whereString")String whereString,@Param("orderString")String orderString);
    List<Permission> query(@Param("sqlString")String sqlString);
    @Update("${sqlString}")
    int execute(@Param("sqlString") String sqlString);
}
