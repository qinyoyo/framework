package top.iotequ.framework.service.impl;
import top.iotequ.framework.pojo.OauthClientDetails;
import top.iotequ.framework.dao.OauthClientDetailsDao;
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
@ConditionalOnMissingClass({"top.iotequ.framework.service.impl.SysOauthClientDetailsService"})
@Service(value="sysOauthClientDetailsService")
public class CgSysOauthClientDetailsService extends CgService<OauthClientDetails>  {
private static final Logger log = LoggerFactory.getLogger(CgSysOauthClientDetailsService.class);
    @Autowired
    private OauthClientDetailsDao oauthClientDetailsDao;
    @Override
    public Class<OauthClientDetails> getEntityClass() {
        return OauthClientDetails.class;
    }
    @Override
    public IDaoService<OauthClientDetails> getDaoService() {
        return oauthClientDetailsDao ;
    }
    @Override
    public IFlowService<OauthClientDetails> getFlowService() {
        return null;
    }
    @Override
    public Map<String,Object> getDictionary(OauthClientDetails obj, Boolean useTree, String dynaFields) {
        Map<String,Object> map=new HashMap<>();
        if (Objects.isNull(dynaFields) || StringUtil.containsItem(dynaFields,"authorities")) map.put("dictAuthorities", DictionaryUtil.getDictListFromDatabase(obj,"SELECT concat('ROLE_',code) as value,name as text FROM sys_role",null,null,null,false,null));
        return map;
    }
    @Override
    public OauthClientDetails getDefaultObject(OauthClientDetails oauthClientDetails) throws IotequException {
        checkAvailable();
        if (oauthClientDetails==null)  oauthClientDetails=new OauthClientDetails();
        else oauthClientDetails.setClientId(null);
        return oauthClientDetails;
    }
    @Override
    public void changeEmpty2Null(OauthClientDetails oauthClientDetails) {
        if (Objects.nonNull(oauthClientDetails)) {
        }
    }
    @Override
    public void changeNull2Default(OauthClientDetails oauthClientDetails) {
        if (oauthClientDetails.getClientId()==null) {
            oauthClientDetails.setClientId("");
        }
        if (oauthClientDetails.getClientSecret()==null) {
            oauthClientDetails.setClientSecret("");
        }
        if (oauthClientDetails.getScope()==null) {
            oauthClientDetails.setScope("");
        }
        if (oauthClientDetails.getAuthorizedGrantTypes()==null) {
            oauthClientDetails.setAuthorizedGrantTypes("");
        }
        if (oauthClientDetails.getAuthorities()==null) {
            oauthClientDetails.setAuthorities("");
        }
        if (oauthClientDetails.getAccessTokenValidity()==null) {
            oauthClientDetails.setAccessTokenValidity(86400);
        }
        if (oauthClientDetails.getRefreshTokenValidity()==null) {
            oauthClientDetails.setRefreshTokenValidity(0);
        }
        if (oauthClientDetails.getAutoapprove()==null) {
            oauthClientDetails.setAutoapprove("1");
        }
        if (oauthClientDetails.getLocked()==null) {
            oauthClientDetails.setLocked(Util.boolValue("0"));
        }
        if (oauthClientDetails.getEnabled()==null) {
            oauthClientDetails.setEnabled(Util.boolValue("1"));
        }
    }
}
