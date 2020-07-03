package top.iotequ.framework.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import top.iotequ.framework.dao.OrgDao;
import top.iotequ.framework.event.OrgChangedEvent;
import top.iotequ.framework.event.SystemParameterChangedEvent;
import top.iotequ.framework.exception.IotequException;
import top.iotequ.framework.exception.IotequThrowable;
import top.iotequ.framework.pojo.Org;
import top.iotequ.framework.util.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

@Service
public class SysOrgService extends CgSysOrgService {
	@Autowired
	private OrgDao orgDao;
	@Autowired
	ApplicationContext applicationContext;
	@Override
	public  void beforeSave(Org org0, Org org, boolean updateSelective, HttpServletRequest request) throws IotequException {
		super.beforeSave(org0,org,updateSelective, request);
		if (!Util.hasRoleAdmin()) {
			String roles = org.getRoleList();
			IotequException e = new IotequException(IotequThrowable.NO_AUTHORITY,"只有超级管理员才能赋权");
			if (updateSelective) {
				if (!Util.isEmpty(roles)) throw e;
			} else if (Objects.isNull(org0)) {
				if (!Util.isEmpty(roles)) throw e;
			} else {
				if (!EntityUtil.entityEquals(org0.getRoleList(), roles)) throw e;
			}
		}
	}
	private void reloadOrg()  {
		OrgUtil.getSystemOrgData();
		SystemParameterChangedEvent.sendReloadOrgEvent(null);
	}
	@Override
	public  void afterDelete(String ids, HttpServletRequest request, RestJson j) throws IotequException{
		applicationContext.publishEvent(new OrgChangedEvent(this,ids));
		reloadOrg();
	}
	@Override

	public  void afterSave(Org o0,Org o, HttpServletRequest request, RestJson j)  throws IotequException{
		if (o0==null || !EntityUtil.entityEquals((o0).getParent(), (o).getParent())) {
			reloadOrg();
		}
		if (!Util.isEmpty(o)) {
			Org org=(Org)o;
			applicationContext.publishEvent(new OrgChangedEvent(this,org,o0==null));
		}
	}
	@Override

	public  void afterImport(List<Org> list, String pk, HttpServletRequest request, RestJson j)  throws IotequException{
		reloadOrg();
		if (list!=null) {
			for (Org u:list) {
				applicationContext.publishEvent(new OrgChangedEvent(this,u.getOrgCode().toString()));
				applicationContext.publishEvent(new OrgChangedEvent(this,u,true));
			}		
		}
	}
	@Override
	public  void beforeImport(List<Org>list,HttpServletRequest request) throws IotequException {
		for (Org o:list) {
			o.setRoleList(SqlUtil.sqlQueryString(false,"select role_list from sys_org where org_code=?", o.getOrgCode()));    // 禁止导入数据修改权限
		}
	}}
