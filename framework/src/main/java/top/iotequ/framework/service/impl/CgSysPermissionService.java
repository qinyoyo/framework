package top.iotequ.framework.service.impl;
import top.iotequ.framework.pojo.Permission;
import top.iotequ.framework.dao.PermissionDao;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import top.iotequ.framework.exception.IotequException;
import top.iotequ.framework.flow.IFlowService;
import top.iotequ.framework.service.impl.CgService;
import top.iotequ.framework.service.IDaoService;
import org.springframework.stereotype.Service;
import top.iotequ.framework.service.utils.DictionaryUtil;
import top.iotequ.framework.service.utils.UploadDownUtil;
import top.iotequ.framework.service.utils.QueryUtil;
import top.iotequ.framework.util.*;
import java.util.*;

/**************************************************
Create by iotequ codegenerator 3.0.0
Author : Qinyoyo
"**************************************************/
@ConditionalOnMissingClass({"top.iotequ.framework.service.impl.SysPermissionService"})
@Service(value="sysPermissionService")
public class CgSysPermissionService extends CgService<Permission>  {
private static final Logger log = LoggerFactory.getLogger(CgSysPermissionService.class);
    @Autowired
    private PermissionDao permissionDao;
    @Override
    public Class<Permission> getEntityClass() {
        return Permission.class;
    }
    @Override
    public IDaoService<Permission> getDaoService() {
        return permissionDao ;
    }
    @Override
    public IFlowService<Permission> getFlowService() {
        return null;
    }
    @Override
    public Map<String,Object> getDictionary(Permission obj, Boolean useTree, String dynaFields) {
        Map<String,Object> map=new HashMap<>();
        if (Objects.isNull(dynaFields) || StringUtil.containsItem(dynaFields,"role")) map.put("dictRole", DictionaryUtil.getDictListFromDatabase(obj,"sys_role","id","concat(concat(code,'/'),name)",null,false,null));
        if (Objects.isNull(dynaFields) || StringUtil.containsItem(dynaFields,"action")) {
            if (useTree) map.put("dictAction", DictionaryUtil.getTreeViewData(obj,"null","id","value","p","id",null,null,null));
            else map.put("dictAction", DictionaryUtil.getDictListFromDatabase(obj,"null","id","value",null,false,null));
        }
        return map;
    }
    @Override
    public Permission getDefaultObject(Permission permission) throws IotequException {
        return permission;
    }
    @Override
    public void changeEmpty2Null(Permission permission) {
        if (Objects.nonNull(permission)) {
        }
    }
}
