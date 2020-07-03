/**************************************************
    Create by iotequ generator 3.0.0
    Author : Qinyoyo
**************************************************/

package top.iotequ.framework.dao;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import top.iotequ.framework.service.IDaoService;
import top.iotequ.framework.pojo.Role;
import java.util.*;

//  Mapper dao : RoleDao (角色表数据访问接口)
public interface RoleDao  extends IDaoService<Role> {
    int insert(Role record);
    int insertBatchWithoutId(List<Role> list);
    int insertBatchWithId(List<Role> list);
    Role select(Integer id);
    int delete(Integer id);
    int deleteBatch(String ids);
    Role selectByCode(String code);
    int deleteByCode(String code);
    int deleteBatchByCode(String codes);
    Role selectByName(String name);
    int deleteByName(String name);
    int deleteBatchByName(String names);
    int deleteList(List<Role> list);
    int update(Role record);
    int updateSelective(Role record);
    int updateBy(@Param("record")Role record,@Param("id")Integer id);
    //条件为所有非空字段 and，String采用like的查询模式，其他为=
    List<Role> list(Role record);
    //!!!  如 whereString="user_name like '王%'" ,orderString="born_date desc,employee_no asc"
    List<Role> listBy(@Param("whereString")String whereString,@Param("orderString")String orderString);
    List<Role> query(@Param("sqlString")String sqlString);
    @Update("${sqlString}")
    int execute(@Param("sqlString") String sqlString);
}
