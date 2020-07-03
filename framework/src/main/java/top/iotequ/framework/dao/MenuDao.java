/**************************************************
    Create by iotequ generator 3.0.0
    Author : Qinyoyo
**************************************************/

package top.iotequ.framework.dao;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import top.iotequ.framework.service.IDaoService;
import top.iotequ.framework.pojo.Menu;
import java.util.*;

//  Mapper dao : MenuDao (系统菜单数据访问接口)
public interface MenuDao  extends IDaoService<Menu> {
    int insert(Menu record);
    int insertBatchWithoutId(List<Menu> list);
    int insertBatchWithId(List<Menu> list);
    Menu select(Integer id);
    int delete(Integer id);
    int deleteBatch(String ids);
    Menu selectByNameParentAction(@Param("name")String name , @Param("parent")Integer parent , @Param("action")String action);
    int deleteByNameParentAction(@Param("name")String name , @Param("parent")Integer parent , @Param("action")String action);
    int deleteList(List<Menu> list);
    List<Menu> selectTree(@Param("id")Integer id);  // 选全部参数为null
    int update(Menu record);
    int updateSelective(Menu record);
    int updateBy(@Param("record")Menu record,@Param("id")Integer id);
    //条件为所有非空字段 and，String采用like的查询模式，其他为=
    List<Menu> list(Menu record);
    //!!!  如 whereString="user_name like '王%'" ,orderString="born_date desc,employee_no asc"
    List<Menu> listBy(@Param("whereString")String whereString,@Param("orderString")String orderString);
    List<Menu> query(@Param("sqlString")String sqlString);
    @Update("${sqlString}")
    int execute(@Param("sqlString") String sqlString);
}
