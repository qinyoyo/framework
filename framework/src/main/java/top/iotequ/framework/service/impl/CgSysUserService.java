package top.iotequ.framework.service.impl;
import top.iotequ.framework.pojo.User;
import top.iotequ.framework.dao.UserDao;
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
@ConditionalOnMissingClass({"top.iotequ.framework.service.impl.SysUserService"})
@Service(value="sysUserService")
public class CgSysUserService extends CgService<User>  {
private static final Logger log = LoggerFactory.getLogger(CgSysUserService.class);
    @Autowired
    private UserDao userDao;
    @Override
    public Class<User> getEntityClass() {
        return User.class;
    }
    @Override
    public IDaoService<User> getDaoService() {
        return userDao ;
    }
    @Override
    public IFlowService<User> getFlowService() {
        return null;
    }
    @Override
    public Map<String,Object> getDictionary(User obj, Boolean useTree, String dynaFields) {
        Map<String,Object> map=new HashMap<>();
        if (Objects.isNull(dynaFields) || StringUtil.containsItem(dynaFields,"sex")) map.put("dictSex", DictionaryUtil.getDictListFromDatabase(obj,"sys_sex",null,null,null,false,null));
        if (Objects.isNull(dynaFields) || StringUtil.containsItem(dynaFields,"orgCode")) {
            if (useTree) map.put("dictOrgCode", DictionaryUtil.getTreeViewData(obj,"select org_code,name from sys_org","org_code","name","parent","org_code",null,null,null));
            else map.put("dictOrgCode", DictionaryUtil.getDictListFromDatabase(obj,"select org_code,name from sys_org","org_code","name",null,false,null));
        }
        if (Objects.isNull(dynaFields) || StringUtil.containsItem(dynaFields,"orgPrivilege")) {
            if (useTree) map.put("dictOrgPrivilege", DictionaryUtil.getTreeViewData(obj,"select org_code,name from sys_org","org_code","name","parent","org_code",null,null,null));
            else map.put("dictOrgPrivilege", DictionaryUtil.getDictListFromDatabase(obj,"select org_code,name from sys_org","org_code","name",null,false,null));
        }
        if (Objects.isNull(dynaFields) || StringUtil.containsItem(dynaFields,"roleList")) map.put("dictRoleList", DictionaryUtil.getDictListFromDatabase(obj,"sys_role","id","name",null,false,null));
        if (Objects.isNull(dynaFields) || StringUtil.containsItem(dynaFields,"idType")) map.put("dictIdType", DictionaryUtil.getDictListFromDatabase(obj,"sys_id_type",null,null,null,false,null));
        return map;
    }
    @Override
    public User getDefaultObject(User user) throws IotequException {
        checkAvailable();
        if (user==null)  user=new User();
        else user.setId(null);
        user.setOrgCode(Util.getUser().getOrgCode());
        return user;
    }
    @Override
    public void changeEmpty2Null(User user) {
        if (Objects.nonNull(user)) {
            if ("".equals(user.getMobilePhone())) user.setMobilePhone(null);
            if ("".equals(user.getEmail())) user.setEmail(null);
            if ("".equals(user.getWechatOpenid())) user.setWechatOpenid(null);
            if ("".equals(user.getEmployeeNo())) user.setEmployeeNo(null);
        }
    }
    @Override
    public void changeNull2Default(User user) {
        if (user.getId()==null) {
            user.setId("");
        }
        if (user.getName()==null) {
            user.setName("");
        }
        if (user.getRealName()==null) {
            user.setRealName("");
        }
        if (user.getOrgCode()==null) {
            user.setOrgCode(Util.getUser().getOrgCode());
        }
        if (user.getLocked()==null) {
            user.setLocked(Util.boolValue("0"));
        }
        if (user.getState()==null) {
            user.setState(Util.boolValue("1"));
        }
        if (user.getIdType()==null) {
            user.setIdType(1);
        }
        if (user.getIdNumber()==null) {
            user.setIdNumber("");
        }
        if (user.getPassword()==null) {
            user.setPassword("123456");
        }
        if (user.getPasswordErrorTimes()==null) {
            user.setPasswordErrorTimes(0);
        }
    }
    @Override
    public void changeFileField( User user) {
        if (user.getId()!=null)
            user.setIcon(UploadDownUtil.removeFilesExclusive("sysUser","icon",user.getId().toString(),user.getIcon(),true));
        else
            user.setIcon(null);
    }
}
