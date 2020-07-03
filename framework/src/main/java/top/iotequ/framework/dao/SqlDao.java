package top.iotequ.framework.dao;

import java.util.Map;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
//通用DAO方法，没有mapper文件，字段名采用camel进行映射mybatis.map-underscore-to-camel-case: true或采用数据字段名映射
public interface SqlDao {
	@Select("${sqlString}")
	List<Map<String, Object>> select(@Param("sqlString") String sqlString);
	@Update("${sqlString}")
	int execute(@Param("sqlString") String sqlString);
}