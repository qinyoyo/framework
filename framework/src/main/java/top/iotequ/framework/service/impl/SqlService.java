package top.iotequ.framework.service.impl;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.constraints.NotNull;

import java.util.List;
import java.util.stream.Collectors;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import top.iotequ.framework.dao.SqlDao;
import top.iotequ.framework.exception.IotequDatabaseException;
import top.iotequ.framework.exception.IotequException;
import top.iotequ.framework.util.*;

@Service
public class SqlService {
	@Autowired
	private SqlDao sqlDao;
	@Autowired
	private SqlSessionTemplate sqlSessionTemplate;
	private boolean includeOrgCodeField(String sql) {
		 final String reg = "\\b(?i)select\\b[\\s\\S]*\\b(?i)org_code\\b[\\s\\S]*\\b(?i)from\\b";  
	     Pattern p = Pattern.compile(reg);
	     Matcher m = p.matcher(sql); // 获取 matcher 对象
	     if(m.find()) return true;
	     else return false;
	}
	private void filterOrgCode(List<Map<String, Object>> list) {
		if (list!=null && list.size()>0) {
			int len=list.size();
			Integer[]  orgPrivilegeCode = OrgUtil.getOrgPrivilegeCodeArray();
			if (Util.isEmpty(orgPrivilegeCode)) return;
			String mapKey=null;
			Integer orgCode=null;
			for (int i=0;i<len;i++) {
					Map<String, Object> map = list.get(i);
					if (map==null) continue;
					if (mapKey==null) {
							if (map.containsKey("org_code")) mapKey="org_code";
							else if (map.containsKey("ORG_CODE")) mapKey="ORG_CODE";
							else return;
					}
					try {
						orgCode = (Integer)map.get(mapKey);
						if (orgCode!=null) {
							boolean inSet=false;
							for (int j=0;j<orgPrivilegeCode.length;j++) if (orgPrivilegeCode[j]==orgCode) { inSet=true; break; }
							if (!inSet) {
								list.remove(i);
								len--;
								i--;
							}
						}
					} catch (Exception e) {}
				}
		}
	}

	public <T> List<T> query(Class<T> clazz, boolean filterOrg,String sql,Object ... params) throws IotequDatabaseException {
		String s=SqlUtil.sqlString(sql, params);
		try {
			if (filterOrg) {
				String tableName = EntityUtil.getDBTableNameFrom(clazz);
				String orgPrivilege = OrgUtil.getOrgPrivilege(clazz);
				if (!Util.isEmpty(orgPrivilege)) {    // 判断部门权限
					orgPrivilege = orgPrivilege.trim();
					if (!orgPrivilege.startsWith(tableName + ".")) orgPrivilege = tableName + "." + orgPrivilege;
					s = SqlUtil.sqlAddCondition(s, orgPrivilege);
				}
				filterOrg = false;
			}
			List<Map<String, Object>> list = sqlDao.select(s);
			list = list.stream().filter(item -> Objects.nonNull(item)).collect(Collectors.toList()); // 删除空记录
			if (filterOrg) filterOrgCode(list);
			if (list != null && list.size() > 0) {
				List<T> result = new ArrayList<T>();
				for (Map<String, Object> map : list) {
					T obj = EntityUtil.entityFromMap(map, clazz);
					if (obj != null) result.add(obj);
				}
				return result;
			} else return null;
		} catch (Exception e) {
			throw new IotequDatabaseException(e,s);
		}
	}
	public List<Map<String, Object>> query(boolean filterOrg,String sql,Object ... params) throws IotequDatabaseException {
		String s=SqlUtil.sqlString(sql, params);
		try {
			List<Map<String, Object>> list=sqlDao.select(s);
			list = list.stream().filter(item -> Objects.nonNull(item)).collect(Collectors.toList()); // 删除空记录
			if (filterOrg) filterOrgCode(list);
			return list;
		} catch (Exception e) {
			throw new IotequDatabaseException(e,s);
		}
	}
	
	public Object queryField(boolean filterOrg,String sql,Object ... params) throws IotequDatabaseException {
		String s=SqlUtil.sqlString(sql, params);
		try {
			if (filterOrg && includeOrgCodeField(s)) {
				s = SqlUtil.sqlAddCondition(s, OrgUtil.getOrgPrivilege("org_code"));
				filterOrg = false;
			}
			List<Map<String, Object>> list = sqlDao.select(s);
			if (filterOrg) filterOrgCode(list);
			if (list != null && list.size() > 0) {
				Map<String, Object> map = list.get(0);
				if (map == null) return null;
				for (String p : map.keySet()) {
					return map.get(p);
				}
			}
			return null;
		} catch (Exception e) {
			throw new IotequDatabaseException(e, s);
		}
	}

	public List<Map<String, Object>> select(boolean filterOrg,String sql,Object ... params ) throws IotequDatabaseException {
		String s=SqlUtil.sqlString(sql, params);
		try {
			if (filterOrg && includeOrgCodeField(s)) {
				s = SqlUtil.sqlAddCondition(s, OrgUtil.getOrgPrivilege("org_code"));
				filterOrg = false;
			}
			List<Map<String, Object>> list = sqlDao.select(s);
			if (filterOrg) filterOrgCode(list);
			return list;
		} catch (Exception e) {
			throw new IotequDatabaseException(e, s);
		}
	}
	public int execute(String sql,Object ... params) throws IotequDatabaseException {
		try {
			return sqlDao.execute(SqlUtil.sqlString(sql, params));
		} catch (Exception e) {
			throw new IotequDatabaseException(e, sql);
		}
	}
	public boolean exist(boolean filterOrg,String sql,Object ... params)  {
		String s=SqlUtil.sqlString(sql, params);
		if (filterOrg && includeOrgCodeField(s)) {
			s=SqlUtil.sqlAddCondition(s, OrgUtil.getOrgPrivilege("org_code"));
			filterOrg=false;
		}
		try { 
			List<Map<String, Object>> list=sqlDao.select(s);
			if (filterOrg) filterOrgCode(list);
			return list!=null && list.size()>0;
		} catch (Exception e) {
			return false;
		}
	}
	
	public int getRowCount(@NotNull String table,@NotNull String pkField,Object  id) {	
		String sql=String.format("select count(*) from %s where %s<=?",table,pkField);
		try {
			Object num=queryField( false,sql,id);
			if (num!=null)  {
				return Integer.parseInt(num.toString());
			}
		} catch (Exception e) {}
		return 0;
	}
	public int checkTableLicenceLeft(@NotNull String table,@NotNull String pkField,int licence) {
		String sql = String.format("select count(*) from %s",table);
		try {
			Object o = queryField(false,sql);		
			if (o == null) return licence;
			int rows=Integer.parseInt(o.toString());
			if (rows>licence) {
				/* 删除多余数据，存在数据丢失风险
				String dbId=sqlSessionTemplate.getConfiguration().getDatabaseId();	
				if  ("MySql".equals(dbId)) {
					sql=String.format("delete from %s where %s not in (select %s from (select %s from %s limit %d) t)", table,pkField,pkField,pkField,table,licence);				
				}
				else if ("Oracle".equals(dbId)) {
					sql=String.format("delete from %s where ROWNUM > %d", table,licence);				
				} 
				else if ("SQLServer".equals(dbId)) {
					sql=String.format("delete from %s where %s not in (select TOP %d %s from %s)", table,pkField,licence,pkField,table);							
				} else sql="";
				if (!sql.isEmpty()) sqlDao.execute(sql);
				*/
				return 0;
			} else return licence - rows;
		} catch (Exception e) { return licence;}
	}
	public String licenceCondition(@NotNull String table,@NotNull String pkField,int licence) {
		String sql = String.format("select count(*) from %s",table);
		try {
			Object o = queryField(false,sql);
			if (o == null) return null;
			int rows=Integer.parseInt(o.toString());
			if (rows>licence) {
				String condition=null;
				String dbId=sqlSessionTemplate.getConfiguration().getDatabaseId();	
				if  ("MySql".equals(dbId)) {
					sql=String.format("SELECT %s FROM %s order by %s limit %d", pkField,table,pkField,licence);		
					List<Map<String, Object>> list=sqlDao.select(sql);
					if (list!=null && list.size()>0)  condition = table+"."+pkField + " <= '" + list.get(list.size()-1).get(pkField).toString() + "'";
				}
				else if ("Oracle".equals(dbId)) {
					condition = String.format("ROWNUM <= %d",licence);				
				} 
				else if ("SQLServer".equals(dbId)) {
					sql=String.format("SELECT TOP %d %s FROM %s order by %s", licence,pkField,table,pkField);		
					List<Map<String, Object>> list=sqlDao.select(sql);
					if (list!=null && list.size()>0)  condition = table+"."+pkField + " <= '" + list.get(list.size()-1).get(pkField).toString() + "'";					
				} 
				if (!sql.isEmpty()) sqlDao.execute(sql);
				return condition;
			} else return null;
		} catch (Exception e) { return null;}
	}
	public Object maxIdInLicence(@NotNull String table,@NotNull String pkField,int licence) {
		try {
			String sql=null;
			String dbId=sqlSessionTemplate.getConfiguration().getDatabaseId();	
			if  ("MySql".equals(dbId)) {
				sql=String.format("SELECT %s FROM %s order by %s limit %d", pkField,table,pkField,licence);		
			}
			else if ("Oracle".equals(dbId)) {
				sql=String.format("SELECT %s FROM %s WHERE ROWNUM <= %d order by %s", pkField,table,licence,pkField);			
			} 
			else if ("SQLServer".equals(dbId)) {
				sql=String.format("SELECT TOP %d %s FROM %s order by %s", licence,pkField,table,pkField);		
			} 
			if (sql!=null) {
				List<Map<String, Object>> list=sqlDao.select(sql);
				if (list!=null && list.size()>0)  return list.get(list.size()-1).get(pkField);
			}
			return null;
		} catch (Exception e) { return null;}
	}

	/*获得不同数据库substring函数的实现语句*/
	public String subString(String a,String start,String len) {
		if (a==null || start==null || len==null) return "";
		String dbId=sqlSessionTemplate.getConfiguration().getDatabaseId();	
		if ("Oracle".equals(dbId)) {
			return "substr("+a+","+start+"-1,"+len+")";		
		} else return "substring("+a+","+start+","+len+")";
	}

	/**
	 * 获得不同数据库find_in_set函数的实现语句<br> 
	 * mysql 的 表述类似 find_in_set(a,b)
	 * @param a 被查询的值或字段
	 * @param aIsValue  true:a为值 false:a为字段名
	 * @param aIsStringType a是否字符串类型
	 * @param b 序列
	 * @param bIsValue  true:a为值 false:a为字段名
	 * @return sql语句的find_in_set实现
	 */
	public String findInSet(String a,boolean aIsValue,boolean aIsStringType,String b,boolean bIsValue) {
		if (a==null && b==null) return "1=1";
		else if (a==null || b==null) return "1=2";
		String r;
		if (bIsValue) {
			if (aIsStringType) {
				String [] bb=b.split(",");
				for (int i=0;i<bb.length;i++)  {
					if (i==0) b="'"+bb[i]+"'";
					else b=b+",'"+bb[i]+"'";
				}
				r=(aIsValue? "'"+a+"'" : a)+" in (" + b +")";
			} else {
				r=a+" in (" + b +")";
			}
			return r;
		}
		String dbId=sqlSessionTemplate.getConfiguration().getDatabaseId();
		if ("SQLServer".equals(dbId)) {
				if (aIsValue) {
					r="CHARINDEX('," + a + ",',','+" + b + "+',')>0";
				}
				else if (aIsStringType)
					r="CHARINDEX(','+" + a + "+',',','+" + b + "+',')>0";
				else
					r="CHARINDEX(','+CONVERT(varchar," + a + ")+',',','+" + b + "+',')>0";
		} else	if ("Oracle".equals(dbId)) {
				if (aIsValue) {
					r="INSTR('," + b + ",',','||" + a + "||',')>0";
				}
				else if (aIsStringType)
					r="INSTR(','||" + b + "||',',','||"+a+"||',')>0";
				else
					r="INSTR(','||" + b + "||',',','||to_char("+a+")||',')>0";
		}
		else {  //MySql
			if (aIsValue) a="'"+a+"'";
			if (bIsValue) b="'"+b+"'";
			r="FIND_IN_SET(" + a + "," + b + ")";
		}
		return r;
	}
}
