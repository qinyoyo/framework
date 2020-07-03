package top.iotequ.framework.util;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.Gson;

import top.iotequ.framework.exception.IotequException;
import top.iotequ.framework.exception.IotequThrowable;

public class EntityUtil {
	
	/**
	 * 获得对象属性
	 * @param obj   对象
	 * @param fieldName  属性名
	 * @return 返回对象的私有属性值
	 * @throws IotequException 异常
	 */
	static public Object getPrivateField(Object obj, String fieldName) throws IotequException {
		try {
			Class<?> clazz = obj.getClass();
			Field field = clazz.getDeclaredField(fieldName);
			field.setAccessible(true);
			return field.get(obj);
		} catch (Exception e) {
			throw IotequException.newInstance(e);
		}
	}
	/**
	 * 设置对象属性
	 * @param obj  对象
	 * @param fieldName 属性名
	 * @param value  属性值
	 * @throws IotequException 异常
	 */
	static public void setPrivateField(Object obj, String fieldName,Object value) throws IotequException {
		try {
			Class<?> clazz = obj.getClass();
			Field field = clazz.getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(obj, value);
		} catch (Exception e) {
			throw IotequException.newInstance(e);
		}
	}
	/**
	 * 设置对象字段属性,仅支持 cg java 类型
	 * @param obj  对象
	 * @param field 属性名
	 * @param value  属性值
	 * @throws IotequException 异常
	 */
	static public void setPrivateField(Object obj, Field field,String value) throws IotequException {
		try {
			field.setAccessible(true);
			Class<?> clazz = field.getType();
			if (clazz.equals(String.class)) field.set(obj, value);
			else if (clazz.equals(Integer.class)) field.set(obj, Integer.valueOf(value));
			else if (clazz.equals(Boolean.class)) field.set(obj, Util.boolValue(value));
			else if (clazz.equals(Short.class)) field.set(obj, Short.valueOf(value));
			else if (clazz.equals(Long.class)) field.set(obj, Long.valueOf(value));
			else if (clazz.equals(Date.class)) field.set(obj, DateUtil.string2Date(value));
			else if (clazz.equals(Double.class)) field.set(obj, Double.valueOf(value));
			else if (clazz.equals(Byte.class)) field.set(obj, Byte.valueOf(value));
			else if (clazz.equals(Float.class)) field.set(obj, Float.valueOf(value));
			else if (clazz.equals(byte[].class)) field.set(obj, value.getBytes());
		} catch (Exception e) {
			throw IotequException.newInstance(e);
		}
	}
	/**
	 * 获得属性的类型
	 * @param clazz 类
	 * @param fieldName 属性名
	 * @return 属性类型
	 * @throws IotequException 异常
	 */
	static public Class<?> getFieldTypeName(Class<?> clazz, String fieldName) throws IotequException {
		try {
			Field field = clazz.getDeclaredField(fieldName);
			return field.getType();
		} catch (Exception e) {
			throw IotequException.newInstance(e);
		}
	}
	/**
	 * 获得方法
	 * @param clazz 类
	 * @param methodName  方法名
	 * @param params  参数表
	 * @return 匹配名称和参数类型的类方法
	 * @throws IotequException 异常
	 */
	public static Method getMethod(Class<?> clazz, String methodName, Object... params) throws IotequException {
		Method[] methods = clazz.getMethods();
		for (Method m : methods) {
			if (methodName.equals(m.getName())) {
				Class<?>[] pm = m.getParameterTypes();
				if (pm.length == params.length) {
					boolean allMatch = true;
					for (int i = 0; i < params.length; i++) {
						if (params[i]!=null && !pm[i].isInstance(params[i])) {
							allMatch = false;
							break;
						}
					}
					if (allMatch)
						return m;
				}
			}
		}
		throw new IotequException(IotequThrowable.REFLECTION_ERROR,"没有这个方法");
	}

	/**
	 * 执行对象方法
	 * @param obj   对象，静态方法传入类对象clazz
	 * @param methodName  方法名
	 * @param params  参数表
	 * @return 执行对象的指定方法
	 * @throws IotequException 异常
	 */
	static public Object runMethod(Object obj, String methodName, Object... params) throws IotequException {
		if (obj == null)
			return null;
		try {
			Class<?> clazz = (obj instanceof Class<?> ? ( Class<?>)obj : obj.getClass());
			Method method = getMethod(clazz, methodName, params);
			method.setAccessible(true);
			return method.invoke(obj instanceof Class<?> ? null : obj, params);
		} catch (Exception e) {
			throw IotequException.newInstance(e);
		}
	}

	public static  Object clone(Object obj) {
		return obj;
	}

	public static Map<String,Object> mapCopy(Map<String,Object> paramsMap) {
		if (paramsMap == null) return null;
		Map<String,Object> resultMap = new HashMap();
		for (String key : paramsMap.keySet()) {
			Object obj = paramsMap.get(key);
			resultMap.put(key,  obj!= null ? clone(obj) : null);
		}
		return resultMap;
	}
	public static Map<String,Object> mapFromEntity(Object object) {
		if (object == null)	return null;
		Map<String, Object> map = new HashMap<>();
		try {			
			Field fields[] = object.getClass().getDeclaredFields();
			for (Field field : fields) {
				field.setAccessible(true);
				Object v=field.get(object);
				if (v!=null) {
					map.put(field.getName(), v);
				}
			}
		} catch (Exception e) { return null; }
		return map;
	}
	
	/**
	 * 从Map构造类对象
	 * @param clazz   pojo类名,如果是派生类，基类的字段将被忽略
	 * @param map   map对象
	 * @param <T> 泛型类型
	 * @return  从一个对象生成一个指定的pojo对象，具有名称相同的值拷贝，否则为空
	 */
	public static <T> T entityFromMap(Map<String,Object>map,Class<T> clazz) {
		if (map == null || clazz==null)	return null;
		try {
			Map<String, Object> newMap = new HashMap<>();
			Field fields[] = clazz.getDeclaredFields();
			for (String p : map.keySet()) {
				Object v=map.get(p);
				if (p!=null) {
					for (Field f : fields) {
						if (Util.isEmpty(v) && f.getType() != String.class ) continue;
						String name=f.getName();
						if (p.equals(name) || StringUtil.camelString(p).equals(name)) {
							if (f.getType().equals(Boolean.class))  {
								newMap.put(name, Util.boolValue(v));
							} else newMap.put(name, v);
							break;
						}
					}
				}
			}
			Gson gson=Util.getGson();
			return gson.fromJson(gson.toJson(newMap), clazz);
		} catch (Exception e) { return null; }
	}

	/**
	 * 从json串获得一个pojo对象
	 * @param json json串
	 * @param clazz 类
	 * @param <T> 泛型类型
	 * @return 类对象，可能为null
	 */
	public static <T> T entityFromJson(String json,Class<T> clazz) {
		if (json == null)
			return null;
		try {
			Gson gson = Util.getGson(); 			
			T obj = gson.fromJson(json, clazz);
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 从request获得一个pojo对象
	 * @param clazz 对象类
	 * @param request request
	 * @param <T> 泛型类型
	 * @return 对象，或null
	 */
	static public <T> T createEntity(Class<T> clazz, HttpServletRequest request) throws IotequException{
		if (request == null || clazz==null)	return null;
		try {
			String contentType = request.getHeader("content-type");
			String method = request.getMethod();
			if (!Util.isEmpty(contentType) && !Util.isEmpty(method)
					&& method.toLowerCase().equals("post") && contentType.toLowerCase().contains("json")) {  // json post 模式
				return HttpUtils.getRequestBody(clazz, request);
			}
			// FormData 或 get 模式
			T obj = clazz.newInstance();
			;
			Field fields[] = clazz.getDeclaredFields();
			for (Field f : fields) {
				String name = f.getName();
				String v = request.getParameter(name);
				if (!Util.isEmpty(v) && !v.equals("NaN")) {
					setPrivateField(obj, f, v);
				}
			}
			return obj;
		} catch (Exception e) {
			throw IotequException.newInstance(e);
		}
	}

	static public Map<String,Object> mapFromBody(HttpServletRequest request) throws Exception{
		if (request == null)	return null;
		String contentType = request.getHeader("content-type");
		String method = request.getMethod();
		if (!Util.isEmpty(contentType) && !Util.isEmpty(method)
				&& method.toLowerCase().equals("post") 	&& contentType.toLowerCase().contains("json")) {  // json post 模式
			return HttpUtils.getRequestBody(request);
		} else {
			Map<String,String[]> mm = request.getParameterMap();
			if (mm!=null) {
				Map<String,Object> map=new HashMap<>();
				for (String key: mm.keySet()) {
					map.put(key,mm.get(key)[0]);
				}
				return map;
			} else return null;
		}
		// FormData 或 get 模式
	}



	/**
	 * 从对象构造类对象
	 * @param to   被修改的对象
	 * @param from   源对象
	 */
	public static void entityCopy(Object to, Object from) {
		if (to == null || from == null)
			return ;
		try {
			Field fields[] = to.getClass().getDeclaredFields();
			for (Field f : fields) {
				try {
					Object v = getPrivateField(from, f.getName());
					f.setAccessible(true);
					f.set(to, v);
				} catch (Exception e) {
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	/**
	 * 从对象构造类对象
	 * @param clazz   pojo类名,如果是派生类，基类的字段将被忽略
	 * @param s   对象
	 * @param <T> 泛型类型
	 * @return  从一个对象生成一个指定的pojo对象，具有名称相同的值拷贝，否则为空
	 */
	public static <T> T entityCopyFrom(Class<T> clazz, Object s) {
		if (s == null)
			return null;
		try {
			T obj = clazz.newInstance();
			Field fields[] = clazz.getDeclaredFields();
			for (Field f : fields) {
				try {
					Object v = getPrivateField(s, f.getName());
					f.setAccessible(true);
					f.set(obj, v);
				} catch (Exception e) {
				}
			}
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public static void copyNonNullField(Object copyTo,Object copyFrom) {
		try {
			Class<?> clazzCopyTo = copyTo.getClass();
			Class<?> clazzCopyFrom = copyFrom.getClass();
			Field[] copyToFields = clazzCopyTo.getDeclaredFields();
			Field[] copyFromFields = clazzCopyFrom.getDeclaredFields();
			for (Field  f : copyFromFields) {
				f.setAccessible(true);
				Object v=f.get(copyFrom);
				if (v!=null) {
					for (Field f1 : copyToFields) {
						if (f1.getName().equals(f.getName())) {
							f1.setAccessible(true);
							f1.set(copyTo, v);
							break;
						}
					}
				}
			}
		} catch (Exception e) {
		}
	}
	/**
	 * 判断两个entity对象是否相等，均为空认为相等
	 * @param o1  pojo1
	 * @param o2  pojo2
	 * @return 是否相等
	 */
	static public boolean entityEquals(Object o1, Object o2) {
		if (Util.isEmpty(o1) && Util.isEmpty(o2))
			return true;
		else if (Util.isEmpty(o1) || Util.isEmpty(o2))
			return false;
		return o1.toString().equals(o2.toString());
	}
	static public boolean isEntityEmpty(Object o) {
		if (o==null) 	return true;
		else {
			try {
				   Class<?> clz = o.getClass();
				   Field[] fields = clz.getDeclaredFields();
				   for (Field f : fields) {			   
						PropertyDescriptor pd = new PropertyDescriptor(f.getName(), clz);
						Method rM = pd.getReadMethod();
						rM.setAccessible(true);
						Object v = rM.invoke(o);
						if (!Util.isEmpty(v)) return false;
				   }
			} catch (Exception e) {
				return Util.isEmpty(o);
			}
		}
		return true;
	}
	/**
	 * 获得数据库表名
	 * @param clazz ： 实体类
	 * @return 数据库表名
	 */
	static public String getDBTableNameFrom(Class<?> clazz) {
		try {
			if (clazz.isAnnotationPresent(CgTableAnnotation.class)) {
				CgTableAnnotation an = clazz.getAnnotation(CgTableAnnotation.class);
				if (an != null) {
					return an.name();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 获得数据库join表名及条件
	 * @param clazz ： 实体类
	 * @return join表
	 */
	static public String getDBJoinConditionFrom(Class<?> clazz) {
		try {
			if (clazz.isAnnotationPresent(CgTableAnnotation.class)) {
				CgTableAnnotation an = clazz.getAnnotation(CgTableAnnotation.class);
				if (an != null) {
					return an.join();
				}
			}
		} catch (Exception e) {
		}
		return null;
	}
	/**
	 * 根据entity名获得数据库字段名 
	 * @param clazz    cg生成的entity类
	 * @param entityName   属性名
	 * @return 数据库字段名
	 */
	static public String getDBFieldNameFrom(Class<?> clazz, String entityName) {
		try {
			Field field = clazz.getDeclaredField(entityName);
			if (field.isAnnotationPresent(CgFieldAnnotation.class)) {
				CgFieldAnnotation an = field.getAnnotation(CgFieldAnnotation.class);
				if (an != null) {
					return an.name();
				}
			}
		} catch (Exception e) {
		}
		return null;
	}
	static public boolean getNullableFrom(Class<?> clazz, String entityName) {
		try {
			Field field = clazz.getDeclaredField(entityName);
			if (field.isAnnotationPresent(CgFieldAnnotation.class)) {
				CgFieldAnnotation an = field.getAnnotation(CgFieldAnnotation.class);
				if (an != null) {
					return an.nullable();
				}
			}
		} catch (Exception e) {
		}
		return false;
	}
	static public boolean getMultipleFrom(Class<?> clazz, String entityName) {
		try {
			Field field = clazz.getDeclaredField(entityName);
			if (field.isAnnotationPresent(CgFieldAnnotation.class)) {
				CgFieldAnnotation an = field.getAnnotation(CgFieldAnnotation.class);
				if (an != null) {
					return an.multiple();
				}
			}
		} catch (Exception e) {
		}
		return false;
	}
	/**
	 * 根据entity名获得数据库字段表达式
	 * @param clazz    cg生成的entity类
	 * @param entityName   属性名
	 * @return 数据库计算字段表达式
	 */
	static public String getDBFieldExpressionFrom(Class<?> clazz, String entityName) {
		try {
			Field field = clazz.getDeclaredField(entityName);
			if (field.isAnnotationPresent(CgFieldAnnotation.class)) {
				CgFieldAnnotation an = field.getAnnotation(CgFieldAnnotation.class);
				if (an != null) {
					return an.expression();
				}
			}
		} catch (Exception e) {
		}
		return null;
	}
	/**
	 * 根据entity名获得数据库字段jdbc类型
	 * @param clazz    cg生成的entity类
	 * @param entityName   属性名
	 * @return 数据库字段jdbc类型
	 */
	static public String getJdbcTypeFrom(Class<?> clazz, String entityName) {
		try {
			Field field = clazz.getDeclaredField(entityName);
			if (field.isAnnotationPresent(CgFieldAnnotation.class)) {
				CgFieldAnnotation an = field.getAnnotation(CgFieldAnnotation.class);
				if (an != null) {
					return an.jdbcType();
				}
			}
		} catch (Exception e) {
		}
		return null;
	}
	/**
	 * 获得数据库主键字段
	 * @param clazz   实体类
	 * @return 数据库主键
	 */
	static public String getDBTablePkFieldFrom(Class<?> clazz) {
		try {
			if (clazz.isAnnotationPresent(CgTableAnnotation.class)) {
				CgTableAnnotation an = clazz.getAnnotation(CgTableAnnotation.class);
				if (an != null) {
					return an.pk();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 获得数据库主键字段
	 * @param clazz   实体类
	 * @return 模块名
	 */
	static public String getModuleFrom(Class<?> clazz) {
		try {
			if (clazz.isAnnotationPresent(CgTableAnnotation.class)) {
				CgTableAnnotation an = clazz.getAnnotation(CgTableAnnotation.class);
				if (an != null) {
					return an.module();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	static public String getParentEntityFieldFrom(Class<?> clazz) {
		try {
			if (clazz.isAnnotationPresent(CgTableAnnotation.class)) {
				CgTableAnnotation an = clazz.getAnnotation(CgTableAnnotation.class);
				if (an != null) {
					return an.parentEntityField();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	static public String getPkTypeFrom(Class<?> clazz) {
		try {
			if (clazz.isAnnotationPresent(CgTableAnnotation.class)) {
				CgTableAnnotation an = clazz.getAnnotation(CgTableAnnotation.class);
				if (an != null) {
					return an.pkType();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 获得数据库主键字段
	 * @param clazz   实体类
	 * @return 是否有licence控制
	 */
	static public boolean hasLicenceFor(Class<?> clazz) {
		try {
			if (clazz.isAnnotationPresent(CgTableAnnotation.class)) {
				CgTableAnnotation an = clazz.getAnnotation(CgTableAnnotation.class);
				if (an != null) {
					return an.hasLicence();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	/**
	 * 获得数据库主键字段
	 * @param clazz   实体类
	 * @return 模块名
	 */
	static public String getBaseUrlFrom(Class<?> clazz) {
		try {
			if (clazz.isAnnotationPresent(CgTableAnnotation.class)) {
				CgTableAnnotation an = clazz.getAnnotation(CgTableAnnotation.class);
				if (an != null) {
					return an.baseUrl();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 获得实体类主键字段
	 * @param clazz   实体类
	 * @return 实体类主键
	 */
	static public String getEntityPkFieldFrom(Class<?> clazz) {
		try {
			if (clazz.isAnnotationPresent(CgTableAnnotation.class)) {
				CgTableAnnotation an = clazz.getAnnotation(CgTableAnnotation.class);
				if (an != null) {
					return an.entityPk();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 获得实体类主键字段
	 * @param obj   对象
	 * @return 实体类主键值
	 */
	static public Object getPkValueFrom(Object obj) {
		String pk=getEntityPkFieldFrom(obj.getClass());
		if (pk!=null) {
			try {
				return getPrivateField(obj,pk);
			} catch (IotequException e) {
				return null;
			}
		} else return null;
	}
	/**
	 * 获得实体类字符串主键字段
	 * @param obj   对象
	 * @return 实体类主键值
	 */
	static public String getStringPkValueFrom(Object obj) {
		Object v =  getPkValueFrom(obj);
		if (v!=null) return v.toString();
		else return null;
	}
	/**
	 * 获得实体类整数主键字段
	 * @param obj   对象
	 * @return 实体类主键值
	 */
	static public Integer getIntPkValueFrom(Object obj) {
		Object v =  getPkValueFrom(obj);
		if (v!=null) return (Integer)v;
		else return null;
	}
}
