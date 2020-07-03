package top.iotequ.framework.service.impl;
import top.iotequ.framework.pojo.SysRoute;
import top.iotequ.framework.dao.SysRouteDao;
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
@ConditionalOnMissingClass({"top.iotequ.framework.service.impl.SysRouteService"})
@Service(value="sysRouteService")
public class CgSysRouteService extends CgService<SysRoute>  {
private static final Logger log = LoggerFactory.getLogger(CgSysRouteService.class);
    @Autowired
    private SysRouteDao sysRouteDao;
    @Override
    public Class<SysRoute> getEntityClass() {
        return SysRoute.class;
    }
    @Override
    public IDaoService<SysRoute> getDaoService() {
        return sysRouteDao ;
    }
    @Override
    public IFlowService<SysRoute> getFlowService() {
        return null;
    }
    @Override
    public Map<String,Object> getDictionary(SysRoute obj, Boolean useTree, String dynaFields) {
        Map<String,Object> map=new HashMap<>();
        return map;
    }
    @Override
    public SysRoute getDefaultObject(SysRoute sysRoute) throws IotequException {
        checkAvailable();
        if (sysRoute==null)  sysRoute=new SysRoute();
        else sysRoute.setId(null);
        return sysRoute;
    }
    @Override
    public void changeEmpty2Null(SysRoute sysRoute) {
        if (Objects.nonNull(sysRoute)) {
        }
    }
    @Override
    public void changeNull2Default(SysRoute sysRoute) {
        if (sysRoute.getId()==null) {
            sysRoute.setId(0);
        }
        if (sysRoute.getPath()==null) {
            sysRoute.setPath("");
        }
        if (sysRoute.getName()==null) {
            sysRoute.setName("");
        }
        if (sysRoute.getComponent()==null) {
            sysRoute.setComponent("Layout");
        }
        if (sysRoute.getTitle()==null) {
            sysRoute.setTitle("");
        }
        if (sysRoute.getBreadcrumbShow()==null) {
            sysRoute.setBreadcrumbShow(Util.boolValue("1"));
        }
        if (sysRoute.getNeedCache()==null) {
            sysRoute.setNeedCache(Util.boolValue("1"));
        }
        if (sysRoute.getTagView()==null) {
            sysRoute.setTagView(Util.boolValue("1"));
        }
    }
}
