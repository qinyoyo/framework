/**************************************************
    Create by iotequ generator 3.0.0
    Author : Qinyoyo
**************************************************/

package top.iotequ.framework.dao;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import top.iotequ.framework.service.IDaoService;
import top.iotequ.framework.pojo.SysLog;
import java.util.*;

//  Mapper dao : SysLogDao (系统日志数据访问接口)
public interface SysLogDao  extends IDaoService<SysLog> {
    int insert(SysLog record);
    int insertBatchWithoutId(List<SysLog> list);
    int insertBatchWithId(List<SysLog> list);
    SysLog select(String id);
    int delete(String id);
    int deleteBatch(String ids);
    int deleteList(List<SysLog> list);
    int update(SysLog record);
    int updateSelective(SysLog record);
    int updateBy(@Param("record")SysLog record,@Param("id")String id);
    //条件为所有非空字段 and，String采用like的查询模式，其他为=
    List<SysLog> list(SysLog record);
    //!!!  如 whereString="user_name like '王%'" ,orderString="born_date desc,employee_no asc"
    List<SysLog> listBy(@Param("whereString")String whereString,@Param("orderString")String orderString);
    List<SysLog> query(@Param("sqlString")String sqlString);
    @Update("${sqlString}")
    int execute(@Param("sqlString") String sqlString);
}
