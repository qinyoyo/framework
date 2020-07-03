/**************************************************
    Create by iotequ generator 3.0.0
    Author : Qinyoyo
**************************************************/

package top.iotequ.framework.dao;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import top.iotequ.framework.service.IDaoService;
import top.iotequ.framework.pojo.SysRoute;
import java.util.*;

//  Mapper dao : SysRouteDao (路由表数据访问接口)
public interface SysRouteDao  extends IDaoService<SysRoute> {
    int insert(SysRoute record);
    int insertBatchWithoutId(List<SysRoute> list);
    int insertBatchWithId(List<SysRoute> list);
    SysRoute select(Integer id);
    int delete(Integer id);
    int deleteBatch(String ids);
    SysRoute selectByPath(String path);
    int deleteByPath(String path);
    int deleteBatchByPath(String paths);
    SysRoute selectByName(String name);
    int deleteByName(String name);
    int deleteBatchByName(String names);
    int deleteList(List<SysRoute> list);
    int update(SysRoute record);
    int updateSelective(SysRoute record);
    int updateBy(@Param("record")SysRoute record,@Param("id")Integer id);
    //条件为所有非空字段 and，String采用like的查询模式，其他为=
    List<SysRoute> list(SysRoute record);
    //!!!  如 whereString="user_name like '王%'" ,orderString="born_date desc,employee_no asc"
    List<SysRoute> listBy(@Param("whereString")String whereString,@Param("orderString")String orderString);
    List<SysRoute> query(@Param("sqlString")String sqlString);
    @Update("${sqlString}")
    int execute(@Param("sqlString") String sqlString);
}
