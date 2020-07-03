package top.iotequ.framework.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import top.iotequ.framework.pojo.Org;
import top.iotequ.framework.pojo.User;

public class OrgUtil {
    static class OrgWithChildren extends Org {
		String childrenString;
		public OrgWithChildren(Org org) {
			setOrgCode(org.getOrgCode());
			setParent(org.getParent());
			setName(org.getName());
			setRoleList(org.getRoleList());
		}
    	public String getChildrenString() {
			return childrenString;
		}
		public void setChildrenString(String childrenString) {
			this.childrenString = childrenString;
		}
		public String getOrgPrivilegeCondition(String  field) {
			String set = getOrgCode().toString();
			if (!Util.isEmpty(childrenString)) set = set + "," + childrenString;
			return field + " in("+set+")"; 
		}
		public boolean isMeOrChildren(int org) {
	    	if (org==getOrgCode()) return true;
    		String children = getChildrenString();
	    	if (children!=null) {
	    			String [] cc = children.split(",");
	    			for (int i=0;i<cc.length;i++) if (org==Integer.parseInt(cc[i])) return true;
	    	}	
	    	return false;
		}
    }
    private static void setChildren(OrgWithChildren who,List<OrgWithChildren> list) {
    	int id=who.getOrgCode();
    	String children=null;
    	for (OrgWithChildren o : list) {
    		Integer pid= o.getParent();
    		if ((pid==null && id==0) || (pid!=null && pid==id)) {
    			if (children==null) children = o.getOrgCode().toString();
    			else children += "," + o.getOrgCode().toString();
    			setChildren(o,list);
    			String cc = o.getChildrenString();
    			if (cc!=null && !cc.isEmpty()) children += "," + cc;
    		}
    	}
    	who.setChildrenString(children);
    }
    private static List<OrgWithChildren> systemOrgList=new ArrayList<OrgWithChildren>();
    public static void getSystemOrgData() {
    	systemOrgList.clear();
    	List<Org> list=null;
		try {
			list = SqlUtil.sqlQuery(Org.class,false, "select * from sys_org");
		} catch (Exception e) {	e.printStackTrace();	}
    	if (list!=null && list.size()>0) {
    		for (Org o : list) {
    			systemOrgList.add(new OrgWithChildren(o));
    		}
    	}
    	if (systemOrgList.size()>0) {
    		for (OrgWithChildren oc : systemOrgList) {
    			setChildren(oc,systemOrgList);
    		}
    	}
    }
    public static String getOrgName(int orgCode) {
		Optional<OrgWithChildren> org = systemOrgList.stream().filter(o -> o.getOrgCode() != null && o.getOrgCode() == orgCode).findFirst();
		if (org.isPresent()) {
			return org.get().getName();
		} else return null;
	}
	public static String getOrgFullName(int orgCode) {
		Optional<OrgWithChildren> orgOp = systemOrgList.stream().filter(o -> o.getOrgCode() != null && o.getOrgCode() == orgCode).findFirst();
		if (orgOp.isPresent()) {
			OrgWithChildren org = orgOp.get();
			if (org.getParent()!=null && org.getParent()!=0) {
				String pname=getOrgFullName(org.getParent());
				if (Util.isEmpty(pname)) return org.getName();
				else return pname+"-"+org.getName();
			} else return org.getName();
		} else return null;
	}
    /**
     * 判断是否为其子部门
     * @param org  需要判断的部门代码
     * @param orgList 与这个部门或其子部门比较
     * @return org是否为orgList或其子部门
     */
    public static boolean isOrgChildren(int org,int orgList) {
    	if (org==orgList) return true;
    	OrgWithChildren oc = null;
    	for (int i=0;i<systemOrgList.size();i++) if (systemOrgList.get(i).getOrgCode() == orgList) { oc=systemOrgList.get(i);break; }
    	if (oc!=null) {
    		return oc.isMeOrChildren(org);
    	}
    	return false;
    }
    
    /**
     * 获得所有的上级部门
     * @param org 部门编码
     * @return 上级部门编码列表
     */
    public static List<Integer> orgParents(int org) {
    	if (org==0) return null;
    	for (int i=0;i<systemOrgList.size();i++) {
    		if (systemOrgList.get(i).getOrgCode() == org) {
    			Integer p = systemOrgList.get(i).getParent();
    			if (p==null || p==0) return null;
    			else {
    				List<Integer> pp=orgParents(p);
    				if (pp==null) pp=new ArrayList<Integer>();
    				pp.add(p);
    				return pp;
    			}
    		}
    	}
    	return null;
    }
    /**
     * 判断是否为当前权限下的子部门
     * @param org  需要判断的部门代码
     * @return org是否当前权限下的子部门
     */
    public static boolean isOrgChildren(int org) {
    	Integer orgList=getOrgPrivilegeCode();
    	if (orgList!=null) return isOrgChildren(org,orgList);
    	else return true;
    }

	static public Integer getOrgPrivilegeCode() {
		User u=Util.getUser();
		if (u!=null) {
			if (EntityUtil.entityEquals(u.getName(),"admin")) return null;
			Integer orgCode = u.getOrgPrivilege();
			if (Util.isEmpty(orgCode)) return u.getOrgCode();
			else 	return orgCode;
		}
		return null;
	}
	static public Integer[] getOrgPrivilegeCodeArray() {
		Integer orgPrivilegeCode = getOrgPrivilegeCode();
		if (orgPrivilegeCode==null) return null;
		return getOrgPrivilegeCodeArray(orgPrivilegeCode);
	}
	static public Integer[] getOrgPrivilegeCodeArray(int orgCode) {
		String ocl = getOrgChildrenList(orgCode);
		if (ocl==null) return null;
		String[] pp = ocl.split(",");
		Integer[] rr= new Integer[pp.length];
		for (int i=0;i<pp.length;i++)  rr[i]=Integer.parseInt(pp[i]);
		return rr;
	}
	static public String getOrgChildrenList(Integer org) {
		if (org==null)  org=getOrgPrivilegeCode();
		if (org==null) return null;
		for (OrgWithChildren oc : systemOrgList) {
			if (oc.getOrgCode() ==org ) {
				String children=oc.getChildrenString();
				if (children==null) return org.toString();
				else {
					return org+","+children;
				}
			}
		}
		return null;		
	}
	/**
	 * 获得类的部门权限条件
	 * @param clazz  类
	 * @return  null或 org_code in ()
	 */
	static public String getOrgPrivilege(Class<?> clazz) {
		String orgCodeFieldName = EntityUtil.getDBFieldNameFrom(clazz, "orgCode");
		return getOrgPrivilege(orgCodeFieldName);
	}
	/**
	 * 获得类的部门权限条件
	 * @param orgCodeFieldName  数据库部门编码字段名，org_code
	 * @return  null或 org_code in ()
	 */
	static public String getOrgPrivilege(String orgCodeFieldName) {
		if (!Util.isEmpty(orgCodeFieldName))  {    // 判断部门权限
			Integer orgPrivilegeCode = getOrgPrivilegeCode();
			if (!Util.isEmpty(orgPrivilegeCode)) {
				for (OrgWithChildren oc : systemOrgList)
					if (EntityUtil.entityEquals(oc.getOrgCode() , orgPrivilegeCode) )
						return oc.getOrgPrivilegeCondition(orgCodeFieldName);
			}
		}  
		return null;
	}
	/**
	 * 获得可以从部门继承的数据属性
	 * @param table   数据表表
	 * @param orgTable  部门表，树状结构，通过 org_code,parent构成树
	 * @param field   可继承的字段，table与orgTable均有此字段
	 * @param pk     table的主键
	 * @param id      table的id值
	 * @return 返回table的字段值，如果为空，返回部门的该值，如果为空，上溯上级部门定义的该值
	 */
	static public Object getOrgInheritableField(String table,String orgTable,String field,String pk,Object id) {
		Map<String, Object> map=null;
		try {
			map = SqlUtil.sqlQueryFirst(false,String.format("select org_code , %s from %s where %s=?",field,table,pk),id);
		} catch (Exception e) {
			return null;
		}
		if (map==null) return null;
		Object r=map.get(field);
		if (r!=null) return r;
		Object orgCode=map.get("org_code");
		while (orgCode!=null && (Integer)orgCode!=0) {
			String sql=String.format("SELECT %s,org_code,parent FROM %s where org_code=?",field,orgTable);
			Map<String, Object> o=null;
			try {
				o = SqlUtil.sqlQueryFirst(false,sql, orgCode);
			} catch (Exception e) {}
			if (o!=null) {
				r=map.get(field);
				orgCode = o.get("parent");
			} else return null;
		}
		return null;
	}

}
