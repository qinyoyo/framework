/**************************************************
    Create by iotequ generator 3.0.0
    Author : Qinyoyo
**************************************************/

package top.iotequ.framework.dao;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import top.iotequ.framework.service.IDaoService;
import top.iotequ.framework.pojo.Message;
import java.util.*;

//  Mapper dao : MessageDao (消息数据访问接口)
public interface MessageDao  extends IDaoService<Message> {
    int insert(Message record);
    int insertBatchWithoutId(List<Message> list);
    int insertBatchWithId(List<Message> list);
    Message select(Integer id);
    int delete(Integer id);
    int deleteBatch(String ids);
    int deleteList(List<Message> list);
    int update(Message record);
    int updateSelective(Message record);
    int updateBy(@Param("record")Message record,@Param("id")Integer id);
    //条件为所有非空字段 and，String采用like的查询模式，其他为=
    List<Message> list(Message record);
    //!!!  如 whereString="user_name like '王%'" ,orderString="born_date desc,employee_no asc"
    List<Message> listBy(@Param("whereString")String whereString,@Param("orderString")String orderString);
    List<Message> query(@Param("sqlString")String sqlString);
    @Update("${sqlString}")
    int execute(@Param("sqlString") String sqlString);
}
