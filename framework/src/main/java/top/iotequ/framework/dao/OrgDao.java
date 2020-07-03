/**************************************************
    Create by iotequ generator 3.0.0
    Author : Qinyoyo
**************************************************/

package top.iotequ.framework.dao;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import top.iotequ.framework.service.IDaoService;
import top.iotequ.framework.pojo.Org;
import java.util.*;

//  Mapper dao : OrgDao (组织机构数据访问接口)
public interface OrgDao  extends IDaoService<Org> {
    int insert(Org record);
    int insertBatchWithoutId(List<Org> list);
    int insertBatchWithId(List<Org> list);
    Org select(Integer orgCode);
    int delete(Integer orgCode);
    int deleteBatch(String orgCodes);
    Org selectByNameParent(@Param("name")String name , @Param("parent")Integer parent);
    int deleteByNameParent(@Param("name")String name , @Param("parent")Integer parent);
    int deleteList(List<Org> list);
    List<Org> selectTree(@Param("id")Integer id);  // 选全部参数为null
    int update(Org record);
    int updateSelective(Org record);
    int updateBy(@Param("record")Org record,@Param("orgCode")Integer id);
    //条件为所有非空字段 and，String采用like的查询模式，其他为=
    List<Org> list(Org record);
    //!!!  如 whereString="user_name like '王%'" ,orderString="born_date desc,employee_no asc"
    List<Org> listBy(@Param("whereString")String whereString,@Param("orderString")String orderString);
    List<Org> query(@Param("sqlString")String sqlString);
    @Update("${sqlString}")
    int execute(@Param("sqlString") String sqlString);
}
