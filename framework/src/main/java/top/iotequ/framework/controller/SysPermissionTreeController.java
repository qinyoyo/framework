package top.iotequ.framework.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import top.iotequ.framework.dao.ActionDao;
import top.iotequ.framework.dao.PermissionDao;
import top.iotequ.framework.event.SystemParameterChangedEvent;
import top.iotequ.framework.exception.IotequException;
import top.iotequ.framework.exception.IotequThrowable;
import top.iotequ.framework.pojo.Action;
import top.iotequ.framework.pojo.Permission;
import top.iotequ.framework.security.service.SecurityService;
import top.iotequ.framework.util.EntityUtil;
import top.iotequ.framework.util.RestJson;
import top.iotequ.framework.util.Util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**************************************************
    Create by iotequ codegenerator 3.0.0
    Author : Qinyoyo
"**************************************************/
@SuppressWarnings("unused")
@RestController
@RequestMapping("/framework/sysPermissionTree")
public class SysPermissionTreeController {
	private static final Logger log = LoggerFactory.getLogger(SysPermissionTreeController.class);
	@Autowired
	private PermissionDao permissionDao;
    @Autowired
    private ActionDao actionDao;
    @Autowired
    private SecurityService securityService;

    private Map<String, Object> findNodeByUrl(final List<Map<String, Object>> mapList, final String url) {
        for (Map<String, Object> m : mapList) {
            if (EntityUtil.entityEquals(m.get("url"), url)) return m;
            Object o = m.get("nodes");
            if (o != null) {
                @SuppressWarnings("unchecked")
                Map<String, Object> r = findNodeByUrl((List<Map<String, Object>>) o, url);
                if (r != null) return r;
            }
        }
        return null;
    }

    private Map<String, Object> addActionTreeNode(List<Map<String, Object>> mapList, Action a) {
        Map<String, Object> map = new HashMap<>();
        final String url = a.getValue() + (a.getParams() == null ? "" : "?" + a.getParams()) + (a.getMethod() == null ? "" : "[" + a.getMethod() + "]");
        if (Objects.isNull(a.getId()))
            map.put("value", 0);
        else
            map.put("value", a.getId());
        if (url.isEmpty() || url.equals("/"))
            map.put("text", "system.action.all");
        else if (Util.isEmpty(a.getNote())) {
            if (a.getId()>0) {// 实际节点
                String [] paths = a.getValue().split("/");
                if (paths.length >= 3) {
                    String generatorName = paths[paths.length-2];
                    String ac = paths[paths.length-1];
                    if (ac.startsWith("f_")) {
                        map.put("text", generatorName + ".title." + ac.substring(2));
                    } else if (generatorName.equals("action")) {
                        generatorName = paths[paths.length-3];
                        map.put("text", generatorName + ".action." + ac);
                    } else {
                        if (ac.equals("batdel")) ac="batchDelete";
                        map.put("text", "system.action." + ac);
                    }
                }
            } else {
                String [] paths = a.getValue().split("/");
                String generatorName = paths[paths.length-1];
                map.put("text", generatorName+".title.code");
            }
        }
        else
            map.put("text",a.getNote());
        map.put("url", url);
        String path = a.getValue();
        int p = path.lastIndexOf("/");
        if (p >= 0 && p < path.length() - 1) {   //  存在上级目录，递归
            Action parent = new Action();
            String purl = path.substring(0, p);
            parent.setValue(purl);
            parent.setId( a.getId()>0 ? -a.getId() : a.getId());
            Map<String, Object> node = findNodeByUrl(mapList, purl);
            if (node == null) node = addActionTreeNode(mapList, parent);
            Object nodes = node.get("nodes");
            if (nodes != null) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> children = (List<Map<String, Object>>) nodes;
                children.add(map);
            } else {
                List<Map<String, Object>> children = new ArrayList<>();
                children.add(map);
                node.put("nodes", children);
            }
        } else {
            Map<String, Object> node = findNodeByUrl(mapList, url);   // 按名称查找节点，避免重复
            if (node == null) mapList.add(map);
        }
        return map;
    }

    private List<Map<String, Object>> getActionTree() {
        List<Action> list = actionDao.listBy(null, "value");
        List<Map<String, Object>> mapList = new ArrayList<>();
        if (list == null || list.isEmpty()) return mapList;
        for (Action a : list) {
            addActionTreeNode(mapList, a);
        }
        return mapList;
    }

    private List<Integer> getPermissionList(int role) {
        List<Permission> pp = permissionDao.selectByRole(role);
        List<Integer> actions = new ArrayList<>();
        if (pp != null && !pp.isEmpty()) for (Permission p : pp) actions.add(p.getAction());
        return actions;
    }

    public RestJson savePermission(Integer role, List<Integer> actions, HttpServletRequest request, HttpServletResponse response) {
        RestJson j = new RestJson();
        int rows = 0;
        try {
            permissionDao.deleteByRole(role);
            if (!Util.isEmpty(actions)) {
                List<Permission> list = new ArrayList<>();
                for (Integer act : actions) {
                    if (Objects.nonNull(act) && act > 0) {
                        Permission p = new Permission();
                        p.setAction(act);
                        p.setRole(role);
                        list.add(p);
                    }
                }
                if (!list.isEmpty()) rows = permissionDao.insertBatchWithoutId(list);
                log.debug("insert:rows=" + rows);
            } else rows = 1;
            if (rows <= 0) {
                j.setSuccess(false);
                j.setErrorCode(IotequThrowable.FAILURE,"insert rows <= 0");
            } else {
            }
        } catch (Exception e) {
            j.setSuccess(false);
            j.setMessage(e);
        } finally {
            securityService.refreshPermission();
            SystemParameterChangedEvent.sendPeemissionInitialEvent(null);
        }
        return j;
    }

	public Map<String,Object> getDictionary(HttpServletRequest request,Permission permission,Boolean forList,Boolean useTree) {
		Map<String,Object> map=new HashMap<>();
		map.put("actions", getActionTree());
		return map;
	}
	@RequestMapping(value = "/record",method = {RequestMethod.GET})
	public ResponseEntity<Map<String, Object>> record(Boolean needLoadDictionary,String id,HttpServletRequest request, HttpServletResponse response) throws Exception {
		RestJson j = new RestJson();
		if (Objects.nonNull(needLoadDictionary) && needLoadDictionary) j.dictionary(getDictionary(request,null,false,true));
        int role = -1;
        try {
            role = Integer.parseInt(id);
            List<Integer> checked = getPermissionList(role);
            Map<String,Object> map=new HashMap<>();
            map.put("actions",checked);
            map.put("role",role);
            j.data(map);
        } catch (Exception e) {
        }
		return j.toResponse();
	}
	@RequestMapping(value = "/update",method = {RequestMethod.PUT,RequestMethod.POST})
	public ResponseEntity<Map<String, Object>> save(@RequestBody Map<String,Object> map,HttpServletRequest request, HttpServletResponse response) {
		Integer role = (Integer) map.get("role");
		List<Integer> actions = (List<Integer>) map.get("actions");
        RestJson j=savePermission(role,actions,request,response);
		return j.toResponse();
	}

    public Class getEntityClass() {
        return Permission.class;
    }
}