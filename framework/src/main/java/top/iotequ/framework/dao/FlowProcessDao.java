/**************************************************
    Create by iotequ generator 3.0.0
    Author : Qinyoyo
**************************************************/

package top.iotequ.framework.dao;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import top.iotequ.framework.service.IDaoService;
import top.iotequ.framework.pojo.FlowProcess;
import java.util.*;

//  Mapper dao : FlowProcessDao (流程处理数据访问接口)
public interface FlowProcessDao  extends IDaoService<FlowProcess> {
    int insert(FlowProcess record);
    int insertBatchWithoutId(List<FlowProcess> list);
    int insertBatchWithId(List<FlowProcess> list);
    FlowProcess select(String id);
    int delete(String id);
    int deleteBatch(String ids);
    int deleteList(List<FlowProcess> list);
    int update(FlowProcess record);
    int updateSelective(FlowProcess record);
    int updateBy(@Param("record")FlowProcess record,@Param("id")String id);
    //条件为所有非空字段 and，String采用like的查询模式，其他为=
    List<FlowProcess> list(FlowProcess record);
    //!!!  如 whereString="db_table_name.user_name like '王%'" ,orderString="db_table_name.born_date desc,db_table_name.employee_no asc"
    List<FlowProcess> listBy(@Param("whereString")String whereString,@Param("orderString")String orderString);
    List<FlowProcess> query(@Param("sqlString")String sqlString);
    @Update("${sqlString}")
    int execute(@Param("sqlString") String sqlString);
}
