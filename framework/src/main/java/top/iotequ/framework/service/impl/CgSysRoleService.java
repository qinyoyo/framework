package top.iotequ.framework.service.impl;
import top.iotequ.framework.pojo.Role;
import top.iotequ.framework.dao.RoleDao;
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
@ConditionalOnMissingClass({"top.iotequ.framework.service.impl.SysRoleService"})
@Service(value="sysRoleService")
public class CgSysRoleService extends CgService<Role>  {
private static final Logger log = LoggerFactory.getLogger(CgSysRoleService.class);
    @Autowired
    private RoleDao roleDao;
    @Override
    public Class<Role> getEntityClass() {
        return Role.class;
    }
    @Override
    public IDaoService<Role> getDaoService() {
        return roleDao ;
    }
    @Override
    public IFlowService<Role> getFlowService() {
        return null;
    }
    @Override
    public Map<String,Object> getDictionary(Role obj, Boolean useTree, String dynaFields) {
        Map<String,Object> map=new HashMap<>();
        return map;
    }
    @Override
    public Role getDefaultObject(Role role) throws IotequException {
        checkAvailable();
        if (role==null)  role=new Role();
        else role.setId(null);
        return role;
    }
    @Override
    public void changeEmpty2Null(Role role) {
        if (Objects.nonNull(role)) {
        }
    }
}
