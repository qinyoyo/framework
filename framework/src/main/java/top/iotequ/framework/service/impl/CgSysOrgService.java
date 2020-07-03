package top.iotequ.framework.service.impl;
import top.iotequ.framework.pojo.Org;
import top.iotequ.framework.dao.OrgDao;
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
@ConditionalOnMissingClass({"top.iotequ.framework.service.impl.SysOrgService"})
@Service(value="sysOrgService")
public class CgSysOrgService extends CgService<Org>  {
private static final Logger log = LoggerFactory.getLogger(CgSysOrgService.class);
    @Autowired
    private OrgDao orgDao;
    @Override
    public Class<Org> getEntityClass() {
        return Org.class;
    }
    @Override
    public IDaoService<Org> getDaoService() {
        return orgDao ;
    }
    @Override
    public IFlowService<Org> getFlowService() {
        return null;
    }
    @Override
    public Map<String,Object> getDictionary(Org obj, Boolean useTree, String dynaFields) {
        Map<String,Object> map=new HashMap<>();
        if (Objects.isNull(dynaFields) || StringUtil.containsItem(dynaFields,"parent")) {
            if (useTree) map.put("dictParent", DictionaryUtil.getTreeViewData(obj,"sys_org","org_code","name","parent","org_code",null,null,null));
            else map.put("dictParent", DictionaryUtil.getDictListFromDatabase(obj,"sys_org","org_code","name",null,false,null));
        }
        if (Objects.isNull(dynaFields) || StringUtil.containsItem(dynaFields,"roleList")) map.put("dictRoleList", DictionaryUtil.getDictListFromDatabase(obj,"sys_role","id","name",null,false,null));
        return map;
    }
    @Override
    public Org getDefaultObject(Org org) throws IotequException {
        checkAvailable();
        if (org==null)  org=new Org();
        else org.setOrgCode(null);
        return org;
    }
    @Override
    public void changeEmpty2Null(Org org) {
        if (Objects.nonNull(org)) {
        }
    }
}
