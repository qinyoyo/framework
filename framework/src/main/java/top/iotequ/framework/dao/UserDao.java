/**************************************************
    Create by iotequ generator 3.0.0
    Author : Qinyoyo
**************************************************/

package top.iotequ.framework.dao;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import top.iotequ.framework.service.IDaoService;
import top.iotequ.framework.pojo.User;
import java.util.*;

//  Mapper dao : UserDao (用户数据访问接口)
public interface UserDao  extends IDaoService<User> {
    int insert(User record);
    int insertBatchWithId(List<User> list);
    User select(String id);
    int delete(String id);
    int deleteBatch(String ids);
    User selectByName(String name);
    int deleteByName(String name);
    int deleteBatchByName(String names);
    User selectByMobilePhone(String mobilePhone);
    int deleteByMobilePhone(String mobilePhone);
    int deleteBatchByMobilePhone(String mobilePhones);
    User selectByEmail(String email);
    int deleteByEmail(String email);
    int deleteBatchByEmail(String emails);
    User selectByWechatOpenid(String wechatOpenid);
    int deleteByWechatOpenid(String wechatOpenid);
    int deleteBatchByWechatOpenid(String wechatOpenids);
    List<User> selectByOrgCode(Integer orgCode);
    int deleteByOrgCode(Integer orgCode);
    int deleteBatchByOrgCode(String orgCodes);
    User selectByEmployeeNo(String employeeNo);
    int deleteByEmployeeNo(String employeeNo);
    int deleteBatchByEmployeeNo(String employeeNos);
    User selectByIdTypeIdNumber(@Param("idType")Integer idType , @Param("idNumber")String idNumber);
    int deleteByIdTypeIdNumber(@Param("idType")Integer idType , @Param("idNumber")String idNumber);
    int deleteList(List<User> list);
    int update(User record);
    int updateSelective(User record);
    int updateBy(@Param("record")User record,@Param("id")String id);
    //条件为所有非空字段 and，String采用like的查询模式，其他为=
    List<User> list(User record);
    //!!!  如 whereString="user_name like '王%'" ,orderString="born_date desc,employee_no asc"
    List<User> listBy(@Param("whereString")String whereString,@Param("orderString")String orderString);
    List<User> query(@Param("sqlString")String sqlString);
    @Update("${sqlString}")
    int execute(@Param("sqlString") String sqlString);
}
