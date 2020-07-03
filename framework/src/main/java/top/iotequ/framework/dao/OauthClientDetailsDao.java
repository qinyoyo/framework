/**************************************************
    Create by iotequ generator 3.0.0
    Author : Qinyoyo
**************************************************/

package top.iotequ.framework.dao;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import top.iotequ.framework.service.IDaoService;
import top.iotequ.framework.pojo.OauthClientDetails;
import java.util.*;

//  Mapper dao : OauthClientDetailsDao (OAuth2客户端配置数据访问接口)
public interface OauthClientDetailsDao  extends IDaoService<OauthClientDetails> {
    int insert(OauthClientDetails record);
    int insertBatchWithId(List<OauthClientDetails> list);
    OauthClientDetails select(String clientId);
    int delete(String clientId);
    int deleteBatch(String clientIds);
    int deleteList(List<OauthClientDetails> list);
    int update(OauthClientDetails record);
    int updateSelective(OauthClientDetails record);
    int updateBy(@Param("record")OauthClientDetails record,@Param("clientId")String id);
    //条件为所有非空字段 and，String采用like的查询模式，其他为=
    List<OauthClientDetails> list(OauthClientDetails record);
    //!!!  如 whereString="user_name like '王%'" ,orderString="born_date desc,employee_no asc"
    List<OauthClientDetails> listBy(@Param("whereString")String whereString,@Param("orderString")String orderString);
    List<OauthClientDetails> query(@Param("sqlString")String sqlString);
    @Update("${sqlString}")
    int execute(@Param("sqlString") String sqlString);
}
