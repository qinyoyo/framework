package top.iotequ.framework.util;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

import org.springframework.core.env.Environment;

import org.springframework.dao.*;
import top.iotequ.framework.exception.IotequException;
import top.iotequ.framework.exception.IotequThrowable;
import top.iotequ.framework.pojo.User;
import top.iotequ.framework.bean.SpringContext;
import top.iotequ.framework.service.impl.SqlService;

/***************          数据库相关操作             ************************/

public class SqlUtil {

	/**
	 * sql写法
	 * @param obj
	 * @return 对象在sql语句里面的写法
	 */
	private static String sqlValue(Object obj) {
		if (obj == null)
			return "null";
		else if (obj instanceof Integer || obj instanceof Long || obj instanceof Short || obj instanceof Double
				|| obj instanceof Float)
			return obj.toString();
		else if (obj instanceof Boolean)
			return (Boolean) obj ? "1" : "0";
		else if (obj instanceof Date)
			return "'" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format((Date) obj) + "'";
		else if (obj.getClass().isArray()) {
			Object[] a = (Object[]) obj;
			String s = "(";
			boolean first = true;
			for (Object o : a) {
				if (first) {
					s += sqlValue(o);
					first = false;
				} else
					s += "," + sqlValue(o);
			}
			s += ")";
			return s;
		} else if (obj instanceof Collection) {
			String s = "(";
			boolean first = true;
			for (Object o : (Collection<?>) obj) {
				if (first) {
					s += sqlValue(o);
					first = false;
				} else
					s += "," + sqlValue(o);
			}
			s += ")";
			return s;
		} else if (obj instanceof String && ((String)obj).split(",").length>1) {
			String[] a = ((String)obj).split(",");
			String s = "(";
			boolean first = true;
			for (String o : a) {
				if (first) {
					s += sqlValue(o);
					first = false;
				} else
					s += "," + sqlValue(o);
			}
			s += ")";
			return s;
		} else {
			String s = obj.toString();
			if ((s.startsWith("'") && s.endsWith("'")) || (s.startsWith("\"") && s.endsWith("\""))) {
				s=s.substring(1, s.length()-2).replace("'", "\\'");
				return s;
			} else {
				s=s.replace("'", "\\'");
			}
			return "'" + s + "'";
		}
	}
	/**
	 * 转换数据库异常报错信息,不再使用，有前端转换
	 * @param e  异常
	 * @return 转换后的异常
	*/
	static public IotequException changeException(DataAccessException e) {
		String cause =  e.getCause() != null ? e.getCause().getMessage() : null;
		if (e instanceof DataIntegrityViolationException) {
			String s=cause.toLowerCase();
			Pattern p = Pattern.compile("duplicate\\s*entry\\s*('[^']*')");
			Matcher m = p.matcher(s);
			if (m.find()) {
				String entry=m.group(1);
				return new IotequException(IotequThrowable.DB_ERROR_DUPLICATE_ENTRY,entry);
			} 
			p = Pattern.compile("column\\s*'([^']*)'\\s*cannot\\s*be\\s*null");
			m = p.matcher(s);
			if (m.find()) {
				return new IotequException(IotequThrowable.DB_ERROR_NULL,m.group(1));
			}
			p = Pattern.compile("too long for column\\s*'([^']*)'");
			m = p.matcher(s);
			if (m.find()) {
				return new IotequException(IotequThrowable.DB_ERROR_TOO_LONG,m.group(1));
			} 
			return new IotequException("DataIntegrityViolationException",e.getMessage());
		} else if (e instanceof TypeMismatchDataAccessException) return new IotequException("TypeMismatchDataAccessException", e.getMessage());
		else if (e instanceof DataAccessResourceFailureException) return new IotequException("DataAccessResourceFailureException", e.getMessage());
		else if (e instanceof DeadlockLoserDataAccessException) return new IotequException("DeadlockLoserDataAccessException", e.getMessage());
		else if (e instanceof OptimisticLockingFailureException) return new IotequException("OptimisticLockingFailureException", e.getMessage());
		else return new IotequException(IotequThrowable.DB_ERROR, e.getClass().getSimpleName()+":"+e.getMessage());
	}

	/**
	 * 将逗号分隔的字符串序列修改为（）集合串
	 * @param set	逗号分隔的序列串或Array或List
	 * @param isStringSet	是否字符序列，字符序列需要加引号
	 * @return	  转换后的()集合字符串
	 */
	public static String sqlSet(Object set,boolean isStringSet) {
		if (Util.isEmpty(set)) return "()";
		StringBuilder sb=new StringBuilder();
		sb.append("(");
		String YH=(isStringSet?"'":"");
		if (set.getClass().isArray() || set instanceof Collection ) {
			Object[] oo=(set.getClass().isArray()? (Object[]) set  : ((Collection<?>) set).toArray());
			for (int i=0;i<oo.length;i++) {
				if (i>0) sb.append(",");
				sb.append(YH+oo[i].toString()+YH);
			}
		} else if (isStringSet) {
				String [] oo=set.toString().split(",");
				for (int i=0;i<oo.length;i++) {
					if (i>0) sb.append(",");
					sb.append(YH+oo[i].toString()+YH);
				}
		} else {
				sb.append(set.toString());
		}
		sb.append(")");
		return sb.toString();
	}
	/**
	 * 将sql语句中的?用参数替换 。
	 * @param sql  sql语句
	 * @param params 参数。
	 * @return 替换后的sql语句，如果参数为逗号分隔的序列，将转换为集合。并且将 =改为 in ,!=改为 not in 。?必须紧跟在=或!=之后
	 */
	static public String sqlString(String sql, Object... params) {
		if (params == null || params.length == 0)
			return sql;
		else if (sql != null) {
			StringBuffer result = new StringBuffer(sql);
			for (Object obj : params) {
				int pos = result.indexOf("?");
				if (pos >= 0) {
					if (obj == null) {
						result.replace(pos, pos + 1, "null"); 
					} else if (obj.getClass().isArray() || obj instanceof Collection || (obj instanceof String && ((String)obj).split(",").length>1) ) { // 序列将=或 != 换成 in 或 not in
						String s = sqlValue(obj);
						result.replace(pos, pos + 1, s);
						String left = result.substring(0, pos);
						String newLeft=replaceEndEqualWith(left,"IN");
						if (!left.equals(newLeft)) {
							result.replace(0, pos, newLeft);
						} 	
					} else {
						String s = sqlValue(obj);
						result.replace(pos, pos + 1, s);
					}
				}
			}
			String r=result.toString();
			return r;
		} else
			return null;
	}
	/**
	 * 用一个实体类对象的值格式化一个sql语句
	 * @param sql  语句,通过 ${entityName} 来引用 pojoObject的属性 entityName。根据类型自动加引号以及进行 in 集合转换
	 * @param pojoObject  实体对象
	 * @return 格式替换后的sql语句。
	 */
	public static String sqlStringUsePojo(String sql, Object pojoObject) {
		if (sql == null)	return sql;
		String regex = "\\$\\{([^{}]+?)\\}";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(sql);
		int lenChanged=0;
		String nsql=sql;
		int len=sql.length();
		while (matcher.find()) {
			int s=lenChanged+matcher.start(),e=lenChanged+matcher.end();
			Object [] pp = getValueAndQuotation(matcher.group(1),pojoObject,null,null);
			Object v=pp[0];
			char quotation=(Boolean)pp[1] ? '\'' : 0;
			nsql=replaceWithValue(nsql, s, e, v,pp.length>2?pp[2]:null,pp.length>3?pp[3]:null,pp.length>4?pp[4]:null, quotation);
			lenChanged = nsql.length() - len;
		}
		return nsql;
	}
	/**
	 * 用一个request对象的值格式化一个sql语句
	 * @param sql  语句,通过 ${entityName} 来引用 request的属性 entityName。根据clazz的同名字段属性自动加引号以及进行 in 集合转换，缺省为字符串类型
	 * @param clazz  类型参考类
	 * @param request HttpServletRequest对象，从http请求传值
	 * @return 格式替换后的sql语句。
	 */
	public static String sqlStringUseRequest(String sql, Class<?> clazz,HttpServletRequest request) {
		if (sql == null || request==null)	return sql;
		String regex = "\\$\\{([^{}]+?)\\}";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(sql);
		int lenChanged=0;
		String nsql=sql;
		int len=sql.length();
		while (matcher.find()) {
			int s=lenChanged+matcher.start(),e=lenChanged+matcher.end();
			Object [] pp = getValueAndQuotation(matcher.group(1),null,clazz,request);
			Object v=pp[0];
			char quotation=(Boolean)pp[1] ? '\'' : 0;
			nsql=replaceWithValue(nsql, s, e, v, pp.length>2?pp[2]:null,pp.length>3?pp[3]:null,pp.length>4?pp[4]:null,quotation);
			lenChanged = nsql.length() - len;
		}
		return nsql;
	}
	static public String sqlReplaceSelectFields(String sql,String newFields ) {
		int pSelect=wordIndexOf(sql,"select",true,true),
		    pFrom=wordIndexOf(sql,"from",true,true);
		if (pSelect >=0 && pFrom > pSelect) {
			return sql.substring(0,pSelect+6) + " " + newFields + " " + sql.substring(pFrom);
		} else return sql;
	}
	static public String sqlAddOrgCondition(String sql) {
		int pSelect=wordIndexOf(sql,"select",true,true),
			pFrom=wordIndexOf(sql,"from",true,true),
			pOrgCode =wordIndexOf(sql,"org_code",true,true);
		if (pSelect >=0 && pFrom > pSelect && pOrgCode > pSelect && pOrgCode < pFrom) {
			return sqlAddCondition(sql,OrgUtil.getOrgPrivilege("org_code"));
		} else return sql;
	}
	static private int sqlConditionStart(String sql,String word,boolean findFirstMatched,boolean ignoreCase) {
		int p=0,len=sql.length();
		while (p<len) {
			String sub = sql.substring(p);
			int pos = wordIndexOf(sub,word,findFirstMatched,ignoreCase);
			if (pos<0) return pos;
			pos += p;
			int kh=0;
			int i=0;
			while (i<pos) {
				char ch = sql.charAt(i);
				if (ch == '(') kh++;
				else if (ch == ')') kh--;
				i++;
			}
			if (kh==0) return pos;
			p=pos + word.length();
		}
		return -1;
	}
	/**
	 * 给sql语句添加一个条件
	 * @param sql  sql语句(不支持where条件含有order having group子句的子查询)
	 * @param condition 需要添加的条件
	 * @return 新的sql语句
	 */
	static public String sqlAddCondition(String sql,String condition) {
		if (Util.isEmpty(condition) || Util.isEmpty(sql)) return sql;
		int pWhere=sqlConditionStart(sql,"where",true,true),
			 pGroup=sqlConditionStart(sql,"group",false,true),
			 pHaving=sqlConditionStart(sql,"having",false,true),
			 pOrder=sqlConditionStart(sql,"order",false,true);
		int pEnd=(pGroup==-1? ( pHaving==-1? pOrder : pHaving ) : pGroup);
		if (pWhere==-1) {
			if (pEnd==-1) return sql + " WHERE " + condition;
			else return sql.substring(0, pEnd) + " WHERE " + condition +" " + sql.substring(pEnd);
		} else {
			String where =  (pEnd==-1? sql.substring(pWhere+5) : sql.substring(pWhere+5,pEnd)) ;
			where = " ("+where.trim()+") AND ("+condition + ") ";
			return sql.substring(0,pWhere+5) +where + (pEnd==-1?"" : sql.substring(pEnd));
		}
	}
	/**
	 * 给sql语句添加一个条件
	 * @param sql  sql语句(不支持where条件含有order子句的子查询)
	 * @param order 需要添加的条件
	 * @return 新的sql语句
	 */
	static public String sqlAddOrderCondition(String sql,String order) {
		if (Util.isEmpty(order) || Util.isEmpty(sql)) return sql;
		int pOrder=wordIndexOf(sql,"order",false,true);
		if (pOrder==-1) {
			return sql + " ORDER BY " + order;
		} else {
			String right=sql.substring(pOrder);
			 String reg="(?!)order\\s*by";
			 Pattern p = Pattern.compile(reg);
		     Matcher m = p.matcher(right);
		     if(m.find()) {
		    	 right=right.substring(m.end());
		      }
			return sql + " ORDER BY " + order + ","+right;
		}
	}
	static public String findInSet(String a,boolean aIsValue,boolean aIsStringType,String b,boolean bIsValue) {
		SqlService sqlService = SpringContext.getBean(SqlService.class);
		return sqlService.findInSet(a,aIsValue,aIsStringType,b,bIsValue);	
	}
	static public String subString(String a,String start,String len) {
		SqlService sqlService = SpringContext.getBean(SqlService.class);
		return sqlService.subString(a,start,len);	
	}
	static private List<Integer> getMatched(String reg,String s) {
	     List<Integer> inStr=new ArrayList<Integer>();
	     Pattern p = Pattern.compile(reg);
	     Matcher m = p.matcher(s); // 获取 matcher 对象
	     while(m.find()) {
	    	 inStr.add(m.start());
	    	 inStr.add(m.end());
	      }
	     return inStr;
	}
	private static String replaceEndEqualWith(String exp,String inOrLike) {
		if (inOrLike==null) inOrLike="IN";
		exp=exp.replaceFirst("\\s*(\\<\\>|\\!=)\\s*$", " NOT "+inOrLike+" ");
		exp=exp.replaceFirst("\\s*=\\s*$", " "+inOrLike+" ");
		return exp;
	}
	/**
	 * 查找第一个匹配的单词，不包括引号内的。用于sql语法分析
	 * @param str   字符串
	 * @param word 需要查找的单词
	 * @findFirstMatched 返回第一个匹配的，否则返回最后一个匹配的
	 * @param ignoreCase 忽略大小写
	 * @return 找到的位置，未找到返回-1
	 */
	static private int wordIndexOf(String str,String word,boolean findFirstMatched,boolean ignoreCase) {
		 final String regSY = "\\\"(\\\\.|[^\\\\\"])*\\\"";  // 匹配双引号内容
		 final String regDY = "\\\'(\\\\.|[^\\\\\"])*\\\'";  //  匹配单引号内容
		 final String reg="\\b"+(ignoreCase?"(?i)":"")+word+"\\b";
	     List<Integer> inStr=getMatched(regSY,str);
	     inStr.addAll(getMatched(regDY,str));
	     List<Integer> matched=getMatched(reg,str);
	     if (matched.size()>=2) {
	    	 int total = matched.size()/2;
	    	 for (int i=(findFirstMatched?0 : total-1) ; (findFirstMatched && i<total) || (!findFirstMatched && i>=0); i += (findFirstMatched?1:-1)) {
	    		 int s=matched.get(2*i),e=matched.get(2*i+1);
	    		 boolean inYH=false;
	    		 for (int j=0;j<inStr.size()/2;j+=2) {
	    			 if ((s>=inStr.get(j) && s<inStr.get(j+1)) || (e>=inStr.get(j) && e<inStr.get(j+1))) {
	    				 inYH=true;
	    				 break;
	    			 }
	    		 }
	    		 if (!inYH) return s;
	    	 }
	     } 
		return-1;
	}


	/**
	 * 执行sql语句
	 * @param sql  sql语句，参数用 ? 代表
	 * @param params  参数
	 * @return 结果为影响的行数
	 * @throws IotequException 异常
	 */
	public static Integer sqlExecute(String sql, Object... params) throws IotequException {
		if (sql == null)
			return null;
		SqlService sqlService = SpringContext.getBean(SqlService.class);
		return sqlService.execute(sql, params);
	}
	/**
	 * 查询sql语句，得到指定类的对象list
	 * @param clazz      对象类
	 * @param filterOrg  是否判断部门权限
	 * @param sql  sql语句，参数用 ? 代表
	 * @param params  参数
	 * @param <T> 泛型类型
	 * @return 对象list
	 * @throws IotequException 异常
	 */
	public static <T> List<T> sqlQuery(Class<T> clazz, boolean filterOrg,String sql, Object... params) throws IotequException {
		if (sql == null)
			return null;
		SqlService sqlService = SpringContext.getBean(SqlService.class);
		return sqlService.query(clazz, filterOrg,sql, params);
	}
	/**
	 * 获得默认的部门权限过滤参数，通过 _condition_filter_org_ 参数控制
	 * @return 是否需要部门权限控制
	 */
	public static boolean getFilterOrg() {
		HttpServletRequest request=Util.getRequest();
		if(request==null) return true;
		boolean filterOrg=true;
		if (request.getAttribute(Util._condition_filter_org_)!=null || request.getParameter(Util._condition_filter_org_)!=null ) {
			if (request.getAttribute(Util._condition_filter_org_)!=null)  filterOrg=(Boolean)request.getAttribute(Util._condition_filter_org_);
			else filterOrg=Util.boolValue(request.getParameter(Util._condition_filter_org_));
		}
		return filterOrg;
	}
	/**
	 * 查询sql语句，得到指定类的对象list。默认判断部门权限
	 * @param clazz      对象类
	 * @param sql  sql语句，参数用 ? 代表
	 * @param params  参数
	 * @param <T> 泛型类型
	 * @return 对象list
	 * @throws IotequException 异常
	 */
	public static <T> List<T> sqlQuery(Class<T> clazz, String sql, Object... params) throws IotequException {
		return sqlQuery(clazz,getFilterOrg(), sql, params);
	}

	/**
	 * 查询sql语句，得到指定类的第一个对象
	 * @param clazz   对象类
	 * @param filterOrg  是否判断部门权限 
	 * @param sql    sql语句，参数用 ? 代表
	 * @param params   参数
	 * @param <T> 泛型类型
	 * @return 第一个对象
	 * @throws IotequException 异常
	 */
	public static <T> T sqlQueryFirst(Class<T> clazz, boolean filterOrg,String sql, Object... params) throws IotequException {
		if (sql == null)
			return null;
		SqlService sqlService = SpringContext.getBean(SqlService.class);
		List<T> list = sqlService.query(clazz, filterOrg,sql, params);
		if (list != null && list.size() > 0)
			return list.get(0);
		else
			return null;
	}
	/**
	 * 查询sql语句，得到指定类的第一个对象。过滤部门权限
	 * @param clazz   对象类
	 * @param sql    sql语句，参数用 ? 代表
	 * @param params   参数
	 * @param <T> 泛型类型
	 * @return 第一个对象
	 * @throws IotequException 异常
	 */
	public static <T> T sqlQueryFirst(Class<T> clazz, String sql, Object... params) throws IotequException {
		return sqlQueryFirst(clazz, getFilterOrg(), sql, params) ;
	}

	/**
	 * 查询sql语句，得到指定类的第一个对象
	 * @param filterOrg  是否判断部门权限 
	 * @param sql    sql语句，参数用 ? 代表
	 * @param params   参数
	 * @return 第一个对象
	 * @throws IotequException 异常
	 */
	public static Map<String,Object> sqlQueryFirst(boolean filterOrg,String sql, Object... params) throws IotequException {
		if (sql == null)
			return null;
		SqlService sqlService = SpringContext.getBean(SqlService.class);
		List<Map<String,Object>> list = sqlService.query(filterOrg,sql, params);
		if (list != null && list.size() > 0)
			return list.get(0);
		else
			return null;
	}
	/**
	 * 查询sql语句，得到指定类的第一个对象。过滤部门权限
	 * @param sql    sql语句，参数用 ? 代表
	 * @param params   参数
	 * @return 第一个对象
	 * @throws IotequException 异常
	 */
	public static Map<String,Object>  sqlQueryFirst(String sql, Object... params) throws IotequException {
		return sqlQueryFirst(getFilterOrg(), sql, params) ;
	}
	
	/**
	 * 查询sql语句，得到一个Map的list
	 * @param filterOrg  是否判断部门权限 
	 * @param sql   sql语句，参数用 ? 代表
	 * @param params   参数
	 * @return Map list
	 * @throws IotequException 异常
	 */
	public static List<Map<String, Object>> sqlQuery(boolean filterOrg,String sql, Object... params) throws IotequException {
		if (sql == null)
			return null;
		SqlService sqlService = SpringContext.getBean(SqlService.class);
		return sqlService.query(filterOrg,sql, params);
	}
	/**
	 * 查询sql语句，得到一个Map的list.判断部门权限 
	 * @param sql   sql语句，参数用 ? 代表
	 * @param params   参数
	 * @return Map list
	 * @throws IotequException 异常
	 */
	public static List<Map<String, Object>> sqlQuery(String sql, Object... params) throws IotequException {
		return sqlQuery(getFilterOrg(),sql,params);
	}

	/**
	 * 使用一个对象传递参数进行查询
	 * @param filterOrg  是否判断部门权限 
	 * @param sql  sql语句，里面包含有 ${字段名}
	 * @param pojoObject 用于替换 ${}的对象
	 * @param params 参数
	 * @return 将sql语句的${}替换为pojoObject对象的属性值，然后进行查询返回查询结果
	 * @throws IotequException 异常
	 */
	public static List<Map<String, Object>> sqlQueryUsePojo(boolean filterOrg,String sql, Object pojoObject,Object... params) throws IotequException {
		if (sql == null || pojoObject==null)	return null;
		sql=sqlStringUsePojo(sql,pojoObject);
		return  sqlQuery(filterOrg, sql,params);
	}

	/**
	 * 查询sql得到一个字段
	 * @param filterOrg  是否判断部门权限 
	 * @param sql sql语句，参数用 ? 代表
	 * @param params  参数
	 * @return 只返回第一行的第一个字段
	 * @throws IotequException 异常
	 */
	public static Object sqlQueryField(boolean filterOrg,String sql, Object... params) throws IotequException {
		if (sql == null)
			return null;
		SqlService sqlService = SpringContext.getBean(SqlService.class);
		return sqlService.queryField(filterOrg,sql, params);
	}
	

	/**
	 * 查询sql得到一个字段.判断部门权限 
	 * @param sql sql语句，参数用 ? 代表
	 * @param params  参数
	 * @return 只返回第一行的第一个字段
	 * @throws IotequException 异常
	 */
	public static Object sqlQueryField(String sql, Object... params) throws IotequException {
		return sqlQueryField(getFilterOrg(),sql, params);
	}
	/**
	 * 查询sql得到一个字符串字段
	 * @param filterOrg  是否判断部门权限 
	 * @param sql sql语句，参数用 ? 代表
	 * @param params    参数
	 * @return 只返回第一行的第一个字段
	 * @throws IotequException 异常
	 */
	public static String sqlQueryString(boolean filterOrg,String sql, Object... params) throws IotequException {
		if (sql == null)
			return null;
		SqlService sqlService = SpringContext.getBean(SqlService.class);
		Object o = sqlService.queryField(filterOrg,sql, params);
		if (o == null)
			return null;
		else
			return o.toString();
	}

	/**
	 * 查询sql得到一个字符串字段.判断部门权限 
	 * @param sql sql语句，参数用 ? 代表
	 * @param params    参数
	 * @return 只返回第一行的第一个字段
	 * @throws IotequException 异常
	 */
	public static String sqlQueryString(String sql, Object... params) throws IotequException {
		return sqlQueryString(getFilterOrg(), sql,params) ;
	}
	
	/**
	 * 查询sql得到一个整数字段
	 * @param filterOrg  是否判断部门权限 
	 * @param sql  sql语句，参数用 ? 代表
	 * @param params   参数
	 * @return 只返回第一行的第一个字段
	 * @throws IotequException 异常
	 */
	public static Integer sqlQueryInteger(boolean filterOrg,String sql, Object... params) throws IotequException {
		if (sql == null)
			return null;
		SqlService sqlService = SpringContext.getBean(SqlService.class);
		Object o = sqlService.queryField(filterOrg,sql, params);
		if (o == null)
			return null;
		else
			return Integer.parseInt(o.toString());
	}
	/**
	 * 获得记录行号
	 * @param table  表
	 * @param pkField 主键
	 * @param id 主键值
	 * @return 行号
	 */
	public static int getRowCount(@NotNull String table,@NotNull String pkField,Object  id) {
		if (id==null) return 0;
		else {
			SqlService sqlService = SpringContext.getBean(SqlService.class);
			return sqlService.getRowCount(table, pkField, id);
		}
	}
	/**
	 * 查询sql得到一个整数字段.判断部门权限 
	 * @param sql  sql语句，参数用 ? 代表
	 * @param params   参数
	 * @return 只返回第一行的第一个字段
	 * @throws IotequException 异常
	 */
	public static Integer sqlQueryInteger(String sql, Object... params) throws IotequException {
		return sqlQueryInteger(getFilterOrg(),sql,params);
	}
	/**
	 * 判断sql是否存在结果集
	 * @param filterOrg  是否判断部门权限 
	 * @param sql  sql语句，参数用 ? 代表
	 * @param params     参数
	 * @return 是否存在结果集
	 */
	public static boolean sqlExist(boolean filterOrg,String sql, Object... params) {
		if (sql == null)
			return false;
		SqlService sqlService = SpringContext.getBean(SqlService.class);
		return sqlService.exist(filterOrg,sql, params);
	}
	/**
	 * 判断sql是否存在结果集.判断部门权限 
	 * @param sql  sql语句，参数用 ? 代表
	 * @param params     参数
	 * @return 是否存在结果集
	 */
	public static boolean sqlExist(String sql, Object... params) {
		return sqlExist(getFilterOrg(),sql, params);
	}	

	/**
	 * 指定位置替换
	 * @param sql SQL语句
	 * @param s 开始位置
	 * @param e 结束为止
	 * @param v 值,=null时判断 v0,v1,v2
	 * @param v0 start值
	 * @param v1 end 值
	 * @param v2 excluse end 值
	 * @param quotation 引号字符，=0时不需要引号
	 * @return 替换后的sql字符串
	 */
	private static String replaceWithValue(String sql,int s,int e,Object v,Object v0,Object v1,Object v2,char quotation) {
		if (v==null) {
			if (v0==null && v1==null && v2==null)
				sql = sql.substring(0, s) + "null" + sql.substring(e);
			else if (v0!=null && v1!=null){
				String left=sql.substring(0, s );
				String newLeft=replaceEndEqualWith(left,"BETWEEN");
				sql = newLeft + (quotation==0?"":String.valueOf(quotation)) + v0.toString() + (quotation==0?"":String.valueOf(quotation))+ " AND "
						+ (quotation==0?"":String.valueOf(quotation)) + v1.toString() + (quotation==0?"":String.valueOf(quotation)) + sql.substring(e );		
			} else {
				String left=sql.substring(0, s );
				Pattern p=Pattern.compile("[\\s\\(\\)]([^\\s\\(\\)]+)\\s*(\\<\\>|\\!=)\\s*$");
				Matcher m=p.matcher(left);
				StringBuilder sb=new StringBuilder();
				if (m.find()) {
					String field = m.group(1);
					if (v0!=null)  sb=sb.append(field).append(" < ").append(quotation==0?"":String.valueOf(quotation)).append(v0.toString()).append(quotation==0?"":String.valueOf(quotation));
					if (v1!=null)  sb=sb.append(sb.length()>0?" OR ":"").append(field).append(" > ").append(quotation==0?"":String.valueOf(quotation)).append(v1.toString()).append(quotation==0?"":String.valueOf(quotation));
					if (v2!=null)  sb=sb.append(sb.length()>0?" OR ":"").append(field).append(" >= ").append(quotation==0?"":String.valueOf(quotation)).append(v2.toString()).append(quotation==0?"":String.valueOf(quotation));
					int sp=m.start();
					if (m.group(0).charAt(0)=='(' || m.group(0).charAt(0)==')' ) sp++;
					left = left.substring(0,sp) + " (" + sb.toString() +") ";
				} else {
					p=Pattern.compile("[\\s\\(\\)]([^\\s\\(\\)]+)\\s*(=)\\s*$");
					m=p.matcher(left);
					if (m.find()) {
						String field = m.group(1);
						if (v0!=null)  sb=sb.append(field).append(" >= ").append(quotation==0?"":String.valueOf(quotation)).append(v0.toString()).append(quotation==0?"":String.valueOf(quotation));
						if (v1!=null)  sb=sb.append(sb.length()>0?" AND ":"").append(field).append(" <= ").append(quotation==0?"":String.valueOf(quotation)).append(v1.toString()).append(quotation==0?"":String.valueOf(quotation));
						if (v2!=null)  sb=sb.append(sb.length()>0?" AND ":"").append(field).append(" < ").append(quotation==0?"":String.valueOf(quotation)).append(v2.toString()).append(quotation==0?"":String.valueOf(quotation));						
						int sp=m.start();
						if (m.group(0).charAt(0)=='(' || m.group(0).charAt(0)==')' ) sp++;
						left = left.substring(0,sp) + " (" + sb.toString() +") ";
					}
				}
				sql = left + sql.substring(e );
			}
		} else if (v.toString().isEmpty()) {
			sql = sql.substring(0, s) +  (quotation==0?"null":String.valueOf(quotation)) +  (quotation==0?"":String.valueOf(quotation)) + sql.substring(e);						
		} else if (v.getClass().isArray() ||  v instanceof List<?> || v.toString().contains(",")) {
			String left=sql.substring(0, s );
			String newLeft=replaceEndEqualWith(left,"IN");
			if (getMatched("\\s+(?i)in\\s*$", newLeft).size()>0) {
				String set=sqlSet(v,quotation!=0);	
				sql=newLeft + set + sql.substring(e);
			} else sql = sql.substring(0, s ) + (quotation==0?"":String.valueOf(quotation)) + v.toString() + (quotation==0?"":String.valueOf(quotation))+ sql.substring(e );			
		} else {
			sql = sql.substring(0, s ) + (quotation==0?"":String.valueOf(quotation)) + v.toString() + (quotation==0?"":String.valueOf(quotation))+ sql.substring(e );
		}
		return sql;
	}
	static private Object[] getValueAndQuotation(String fieldName,Object pojoObject,Class<?> clazz,HttpServletRequest request) {
		Object v=null,v0=null,v1=null,v2=null;
		if (Util.isEmpty(fieldName)) return new Object[] {null,false};
		if (fieldName.toLowerCase().startsWith("system.user.")) return getValueAndQuotation(fieldName.substring(12),Util.getUser(),User.class,null);
		else if (fieldName.equalsIgnoreCase("System.orgListWithSuborg")) return new Object[] { OrgUtil.getOrgChildrenList(Util.getUser()==null?null:Util.getUser().getOrgCode()),false };
		else if (fieldName.equalsIgnoreCase("System.orgPrivilegeList")) return new Object[] { OrgUtil.getOrgChildrenList(null),false }; 
		else if (fieldName.equalsIgnoreCase("System.clientIp")) return new Object[] { Util.getIpAddr(null),true }; 

		try {
			if (pojoObject!=null) {
				v = EntityUtil.getPrivateField(pojoObject,fieldName);
				clazz=pojoObject.getClass();
			}
			else if (request!=null) {
				v=request.getParameter(fieldName);
				if (v==null) {
					v0=request.getParameter(fieldName+"_start");
					v1=request.getParameter(fieldName+"_end");
					v2=request.getParameter(fieldName+"_exclusive_end");
				}
			}
		} catch (Exception e1) {}
		Field field;
		Boolean quotation=true;
		try {
			field = clazz.getDeclaredField(fieldName);
			String type=field.getType().getName();
			if (type.endsWith("Boolean")) { 
				if (v!=null) 	v=((Boolean)v?"1":"0"); 
				quotation=false; 
			}
			else if (type.endsWith("String") || type.endsWith("Date")) {}
			else if (type.endsWith("Integer") || type.endsWith("Short") || type.endsWith("Long") || type.endsWith("Byte") 
					|| type.endsWith("Float") || type.endsWith("Double") || type.endsWith("Decimal")) 
			    quotation=false;
		} catch (Exception excep) {} 		
		return new Object[] {v,quotation,v0,v1,v2};
	}
	
	/**
	 * 检查表的licence
	 * @param table   表名
	 * @param pkField 主键字段
	 * @param licence licence值
	 * @return 剩余licence数 
	 */
	static public int checkTableLicenceLeft(@NotNull String table,@NotNull String pkField,int licence) {
		SqlService sqlService = SpringContext.getBean(SqlService.class);
		return sqlService.checkTableLicenceLeft(table,pkField,licence);
	}
	static public String licenceCondition(@NotNull String table,@NotNull String pkField,int licence) {
		SqlService sqlService = SpringContext.getBean(SqlService.class);
		return sqlService.licenceCondition(table,pkField,licence);
	}
	static public Object maxIdInLicence(@NotNull String table,@NotNull String pkField,int licence) {
		SqlService sqlService = SpringContext.getBean(SqlService.class);
		return sqlService.maxIdInLicence(table,pkField,licence);
	}
	static public Map<String,String> getDatabaseSetting(Environment env) throws IotequException {
		Map<String,String> map=new HashMap<>();
		String datasourceUrl= env.getProperty("spring.datasource.url");  
		if(datasourceUrl==null) throw new IotequException (IotequThrowable.CONFIG_ERROR,"数据库配置有错: spring.datasource.url") {
			private static final long serialVersionUID = 1L;} ;
		String s=env.getProperty("spring.datasource.username");
		if (s==null) throw new IotequException (IotequThrowable.CONFIG_ERROR,"数据库配置有错: spring.datasource.username") {
			private static final long serialVersionUID = 1L;} ;
		map.put("user",s);
		s=env.getProperty("spring.datasource.password");
		if (s==null) map.put("password","");
		else map.put("password",s);
		if (datasourceUrl.toLowerCase().contains("jdbc:mysql")) {
			map.put("databaseId","MySql");
			datasourceUrl=datasourceUrl.split("[?]")[0];
			map.put("database",datasourceUrl.substring(datasourceUrl.lastIndexOf("/")+1));
			String addr=datasourceUrl.substring(datasourceUrl.indexOf("//")+2,datasourceUrl.lastIndexOf("/"));
			String [] ap=addr.split(":");
			map.put("host", ap[0]);
			map.put("port",ap.length==2 ? ap[1] : "3306");
		} else if (datasourceUrl.toLowerCase().contains("jdbc:sqlserver")) {
			map.put("databaseId","SQLServer");
			int pos=datasourceUrl.toLowerCase().indexOf("databasename=");
			int pos1=datasourceUrl.toLowerCase().indexOf(";",pos);
			if (pos1>0) map.put("database",datasourceUrl.substring(pos+13, pos1));
			else map.put("database",datasourceUrl.substring(pos+13));
			String addr=datasourceUrl.substring(datasourceUrl.indexOf("//")+2,datasourceUrl.lastIndexOf(";"));
			String [] ap=addr.split(":");
			map.put("host", ap[0]);
			map.put("port",ap.length==2 ? ap[1] : "1433");		
		} else if (datasourceUrl.toLowerCase().contains("jdbc:oracle")) {
			//jdbc:oracle:thin:@127.0.0.1:1521:iotequ
			map.put("databaseId","Oracle");
			int pos=datasourceUrl.toLowerCase().indexOf("@");
			if (pos<0)  throw new IotequException (IotequThrowable.CONFIG_ERROR,"数据库配置有错: spring.datasource.url") {
				private static final long serialVersionUID = 1L;} ;
			String [] ap=datasourceUrl.substring(pos).split(":");
			map.put("host", ap[0]);
			map.put("port",ap.length>2 ? ap[1] : "1521");	
			map.put("database",ap.length>2?ap[2]:ap[1]);
		}   
		return map;
	}
}
