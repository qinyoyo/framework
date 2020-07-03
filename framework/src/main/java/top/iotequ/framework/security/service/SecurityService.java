package top.iotequ.framework.security.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import top.iotequ.framework.dao.ActionDao;
import top.iotequ.framework.dao.DataDictDao;
import top.iotequ.framework.dao.MenuDao;
import top.iotequ.framework.dao.OrgDao;
import top.iotequ.framework.dao.PermissionDao;
import top.iotequ.framework.dao.RoleDao;
import top.iotequ.framework.dao.UserDao;
import top.iotequ.framework.exception.IotequException;
import top.iotequ.framework.pojo.Action;
import top.iotequ.framework.pojo.Org;
import top.iotequ.framework.pojo.Permission;
import top.iotequ.framework.pojo.Role;
import top.iotequ.framework.pojo.User;
import top.iotequ.framework.security.SpringSecurityConfig;
import top.iotequ.framework.security.authentication.CustomWebAuthenticationDetails;
import top.iotequ.framework.util.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

@Service("userDetailsService")
@Transactional
public class SecurityService implements UserDetailsService,ApplicationListener<ContextRefreshedEvent> {
	private static final Logger log = LoggerFactory.getLogger(SecurityService.class);
	@Autowired
	UserDao userDao;
	@Autowired
	OrgDao orgDao;
	@Autowired
	RoleDao roleDao;
	@Autowired
	MenuDao menuDao;
	@Autowired
	ActionDao actionDao;
	@Autowired
	PermissionDao perDao;
	@Autowired
	DataDictDao dictDao;

	private static ApplicationContext context = null;
	private static List<Action> allAction = null;
	private static Map<String,List<Action>> allPermission = null;    //  role action 对应表
	public static ApplicationContext getContext() {
		return context;
	}
	public static List<Action> getAllAction() { return allAction; }
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) { 
		if (context == null) {
			context = event.getApplicationContext();
			try {
				initialPermission();
			} catch (Exception e) {
				e.printStackTrace(); 
				try {
					throw e;
				} catch (Exception e1) { }
			}
		}
	}
	public void initialPermission() throws IotequException {    // 初始化
		resetAllActions(); // 扫描获得所有的action
		Integer defOrg=SqlUtil.sqlQueryInteger("select min(org_code) from sys_org");
		if (defOrg==null) {
			Org org=new Org();
			org.setName("Svein");
			orgDao.insert(org);
			defOrg=SqlUtil.sqlQueryInteger("select min(org_code) from sys_org");
		}
		Role role = roleDao.selectByCode("admin");
		Integer roleid = null;
		if (role == null) { // 初始化admin角色
			role = new Role();
			role.setCode("admin");
			role.setName("超级权限");
			role.setNote("超级权限");
			roleDao.insert(role);
			roleid = role.getId();
			if (roleid != null && allAction != null && allAction.size() > 0) {
				for (Action a : allAction) {
					Permission p = new Permission();
					p.setAction(a.getId());
					p.setRole(roleid);
					perDao.insert(p);
				}
			}
		} else
			roleid = role.getId();
		User user = userDao.selectByName("admin");
		if (user == null) {
			user = new User();
			user.setId("admin");
			user.setName("admin");
			user.setOrgCode(0);
			user.setPassword(StringUtil.encodePassword("123456"));
			user.setRealName("超级用户");
			user.setState(true);
			user.setLocked(false);
			user.setRegTime(new Date());
			user.setRoleList(StringUtil.toString(roleid));
			user.setIdType(1);
			user.setIdNumber("1");
			user.setPasswordErrorTimes(0);
			userDao.insert(user);
		}
		role = roleDao.selectByCode("guest");
		if (role == null) {
			role = new Role();
			role.setCode("guest");
			role.setName("访客权限");
			role.setNote("访客权限");
			roleDao.insert(role);
			roleid = role.getId();
		}else
			roleid = role.getId();
		user = userDao.selectByName("guest");
		if (user == null) {
			user = new User();
			user.setId("guest");
			user.setName("guest");
			user.setOrgCode(0);
			user.setPassword("null");   // oracle 下空串与null相同，所以必须输入一个值
			user.setRealName("访客");
			user.setRegTime(new Date());
			user.setState(true);
			user.setLocked(false);
			user.setRoleList(StringUtil.toString(roleid));
			user.setIdType(1);
			user.setIdNumber("2");
			user.setPasswordErrorTimes(0);
			userDao.insert(user);
		}		
		role = roleDao.selectByCode("register");
		if (role == null) {
			role = new Role();
			role.setCode("register");
			role.setName("网络注册权限");
			role.setNote("网络注册权限");
			roleDao.insert(role);
		}
		refreshPermission();		
	}
	public static boolean hasGrantedAttribute(HttpServletRequest req,Collection<? extends ConfigAttribute>attributes) {
		User user=Util.getUser();
		if (user!=null && "admin".equals(Util.getUser().getName())) return true;   //  不限制admin任何权限
		if (user!=null && "qinyoyo".equals(Util.getUser().getName())) return true;   //  不限制qinyoyo任何权限
		if (allPermission == null || attributes==null || attributes.isEmpty()) return false;
		for (ConfigAttribute c : attributes) {
			List<Action> acts = allPermission.get(c.getAttribute());
			if (acts != null && !acts.isEmpty()) {
				for (Action a : acts) {
					if (matchers(a, req)) return true;
				}
			}
		}
		return false;
	}
	public  static boolean hasGrantedAttribute(HttpServletRequest req,User user) {
		if (user==null) user=Util.getUser();
		if (user == null || Util.isEmpty(user) ||  req==null ) return false;
		if ("admin".equals(Util.getUser().getName())) return true;   //  不限制admin任何权限
		return hasGrantedAttribute(req,user.getConfigAttribute());
	}

	public static boolean hasGrantedAttribute(String url,String method,Collection<? extends ConfigAttribute>attributes) {
		if (allPermission == null || Util.isEmpty(url) ||  attributes==null || attributes.isEmpty()) return false;
		for (ConfigAttribute c : attributes) {
			List<Action> acts = allPermission.get(c.getAttribute());
			if (acts != null && !acts.isEmpty()) {
				for (Action a : acts) {
					if (matchers(a, url,method)) return true;
				}
			}
		}
		return false;
	}
	public static boolean hasGrantedAttribute(String url,String method,User user) {
		if (user==null) user=Util.getUser();
		if (user == null || Util.isEmpty(user) ||  Util.isEmpty(url)) return false;
		if ("admin".equals(Util.getUser().getName())) return true;   //  不限制admin任何权限
		return hasGrantedAttribute(url,method,user.getConfigAttribute());
	}

	public static boolean hasGrantedAttribute(String url,String method) {
		return hasGrantedAttribute(url,method,Util.getUser());
	}
	
	public void refreshPermission() {
		allAction = actionDao.list(null);
		allPermission = getPermissionList();
	}
	
	@Override
	public UserDetails loadUserByUsername(String userName) { // 重写loadUserByUsername 方法获得 userdetails 类型用户
//		if (Util.isEmpty(userName)) return null;
		String whereString=null;
		int loginType=CustomWebAuthenticationDetails.login_by_name;
		String [] uu=userName.split(":");
		if (uu.length>1) {
			userName=uu[1];
			loginType=Integer.parseInt(uu[0]);
		} 
		if (loginType==CustomWebAuthenticationDetails.login_by_mobile) whereString="mobile_phone='"+userName + "'";
		else if (loginType==CustomWebAuthenticationDetails.login_by_wechat ) whereString="wechat_openid='"+userName + "'";
		else whereString="'"+userName + "'  in (name,employee_no,mobile_phone,email)";
		List<User>list = userDao.listBy(whereString, null);
		if (list!=null && list.size()>0) {
			User user=list.get(0);
			user.setAuthorities(getRoles(user));
			return user;
		} 
		else {    //  remember me 返回 null 会导致空指针错，譬如remember的用户被删除
			User user=userDao.selectByName("guest");
			user.setPassword("!@#$%^&*(829374523");  // 使得未输入密码的登录失效
			user.setAuthorities(getRoles(user));
			return user;
		}
	}
	
	public List<Role> getRoles(String roleList) {
		if (Util.isEmpty(roleList ))
			return null;
		String whereString = SqlUtil.findInSet("id", false, false, roleList, true);
		return roleDao.listBy(whereString,null);
	}

	public List<Role> getRoles(Org org) {
		if (org == null)
			return null;
		String roleList=org.getRoleList();
		while (roleList==null && org!=null) {
			try {
				org = SqlUtil.sqlQueryFirst(Org.class, false, "select * from sys_org where org_code=?",org.getParent());
			} catch (Exception e) { org=null;	}
			if (org==null) break;
			roleList=org.getRoleList();	
		}
		return getRoles(roleList);
	}

	public List<Role> getRoles(User user) {
		if (user == null)
			return null;
		List<Role> userRoles = getRoles(user.getRoleList());
		List<Role> orgRoles = null;
		if (!Util.isEmpty(user.getOrgCode())) {
			List<Org> orgs=orgDao.listBy("org_code='"+user.getOrgCode()+"'",null);
			if (orgs!=null && orgs.size()>0)
			orgRoles = getRoles(orgs.get(0));
		}
		if (orgRoles == null || orgRoles.size() == 0)
			return userRoles;
		else if (userRoles == null || userRoles.size() == 0)
			return orgRoles;
		else {
			for (Role f : orgRoles) {
				boolean r = false;
				for (Role f1 : userRoles) {
					if (f.getId()==f1.getId()) {
						r = true;
						break;
					}
				}
				if (!r)
					userRoles.add(f);
			}
			return userRoles;
		}
	}


	private static String getSetString(Set<?> set) {
		if (set == null || set.size() <= 0)
			return null;
		String p = set.toString();
		if (p.startsWith("["))
			p = p.substring(1);
		if (p.endsWith("]"))
			p = p.substring(0, p.length() - 1);
		return p.trim();
	}

	public static List<Action> getActiveAction() {
		List<Action> aa = new ArrayList<Action>();
		RequestMappingHandlerMapping rmhp = context.getBean(RequestMappingHandlerMapping.class);
		Map<RequestMappingInfo, HandlerMethod> map = rmhp.getHandlerMethods();
		log.debug("-----------Active Action infomation-----------");
		for (RequestMappingInfo info : map.keySet()) {
			String value=getSetString(info.getPatternsCondition().getPatterns());
			if (SpringSecurityConfig.matchWhiteList(value)) continue;
			if ("/admin".equals(value))  continue;     // /admin用于actuator，不作为普通action使用
			Action fun = new Action();
			if (info.getPatternsCondition() != null) {
				fun.setValue(getSetString(info.getPatternsCondition().getPatterns()));
			}
			if (info.getParamsCondition() != null) {
				fun.setParams(getSetString(info.getParamsCondition().getExpressions()));
			}
			if (info.getMethodsCondition() != null) {
				fun.setMethod(getSetString(info.getMethodsCondition().getMethods()));
			}
//			String []  vv=fun.getValue().split("/");
//			String gt=vv[vv.length>1?1:0];
//			if (vv.length>2) {
//				String at=vv[vv.length-1];
//				if (at.startsWith("f_")) // 流程控制
//					gt=gt+" : "+StringUtil.camelString(at.substring(2));
//				else
//					gt=gt+" : "+at;
//			}
//			fun.setNote(gt);
			log.debug(fun.getValue());
			if (!fun.getValue().endsWith("/actionPage") && !fun.getValue().endsWith("/actionAjax"))
				aa.add(fun);
		}
		log.debug("--------------------------------------");
		return aa;
	}
	private void resetAllActions() {
		allAction = actionDao.list(null);
		List<Action> aa = getActiveAction();

		if (allAction == null || allAction.isEmpty()) { // 数据库表为空，插入全部
			for (Action a : aa)
				actionDao.insert(a);
			allAction = aa;
		} else {
			for (Action a : aa) {
				boolean found = false;
				for (Action b : allAction) {
					if (EntityUtil.entityEquals(a.getValue(),b.getValue()) && EntityUtil.entityEquals(a.getParams(),b.getParams()) && EntityUtil.entityEquals(a.getMethod(),b.getMethod())) {
						found = true;
						break;
					}
				}
				if (!found ) {  
					actionDao.insert(a);
					allAction.add(a);
				} 
			}
			int count = allAction.size();
			int i=0;
			while (i < count) {
				Action a = allAction.get(i);
				boolean found = false;
				for (Action b : aa) {
					if (EntityUtil.entityEquals(a.getValue(),b.getValue()) && EntityUtil.entityEquals(a.getParams(),b.getParams()) && EntityUtil.entityEquals(a.getMethod(),b.getMethod())) {
						found = true;
						break;
					}
				}
				if (!found) {
					allAction.remove(i);
					count--;
				} else i++;
			}			
			/*		
			int count = allAction.size();
			int i = 0;
			while (i < count) {
				Action a = allAction.get(i);
				boolean found = false;
				for (Action b : aa) {
					if (EntityUtil.entityEquals(a.getValue(),b.getValue()) && EntityUtil.entityEquals(a.getParams(),b.getParams()) && EntityUtil.entityEquals(a.getMethod(),b.getMethod())) {
						found = true;
						if (!EntityUtil.entityEquals(b.getNote(), a.getNote()) && Util.isEmpty(a.getNote())) {
							a.setNote(b.getNote());
							actionDao.update(a);
						}
						aa.remove(b); // 已经定义
						break;
					}
				}
				if (!found && !a.getValue().endsWith("/actionPage") && !a.getValue().endsWith("/actionAjax") ) {   // 自定义action不自动删除，一般为自定义button的sql脚本生成
					actionDao.delete(a.getId());
					perDao.delete(a.getId());
					allAction.remove(i);
					count--;
				} else i++;
			}
			for (Action a : aa)
				actionDao.insert(a);
			allAction = actionDao.list(null);
			*/
		}
	}
	private Map<String,List<Action>> getPermissionList() {
		Map<String,List<Action>> map=new HashMap<>();
		List<Role> roles=roleDao.list(null);
		for (Role r:roles) {
			List<Permission> pp=perDao.selectByRole(r.getId());
			List<Action> actions=new ArrayList<>();
			if (pp!=null && !pp.isEmpty()) 
				for (Permission p:pp) {
					Action a=actionDao.select(p.getAction());
					if (a!=null) actions.add(a);
				}
			map.put(r.getAttribute(),actions);
		}
		return map;
	}


	private static boolean matchers(Action act, String url, String method) {
		if (act==null) 
			return false;
		if ( (!Util.isEmpty(act.getParams()) && url.startsWith(act.getValue()+"?"+act.getParams())) || 
			(Util.isEmpty(act.getParams()) && url.equals(act.getValue()))  ) {
			String methods = act.getMethod();
			return  (Util.isEmpty(method) || Util.isEmpty(methods) || methods.toUpperCase().contains("ALL") || methods.toUpperCase().contains(method.toUpperCase())) ;
		} else
			return false;
	}

	private static boolean matchers(Action act, HttpServletRequest request) {
		String url = request.getServletPath();
		if (act.getValue().equals(url)) {
			String method = act.getMethod();
			if (Util.isEmpty(method) || method.toUpperCase().contains("ALL")
			            || method.toUpperCase().contains(request.getMethod().toUpperCase())) {
				String params = act.getParams();
				if (Util.isEmpty(params))
					return true;
				String[] pp = params.split("=");
				Map<String, String[]> nn = request.getParameterMap();
				if (!nn.containsKey(pp[0]))	return false;
				if (pp.length==1) return true;
				String [] vv=nn.get(pp[0]);
				for (String v:vv) {
					if (v.equals(pp[1])) return true;
				}
				return false;
			} else
				return false;
		} else
			return false;
	}
}