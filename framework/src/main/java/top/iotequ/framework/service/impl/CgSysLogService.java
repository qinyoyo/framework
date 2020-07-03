package top.iotequ.framework.service.impl;
import top.iotequ.framework.pojo.SysLog;
import top.iotequ.framework.dao.SysLogDao;
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
@ConditionalOnMissingClass({"top.iotequ.framework.service.impl.SysLogService"})
@Service(value="sysLogService")
public class CgSysLogService extends CgService<SysLog>  {
private static final Logger log = LoggerFactory.getLogger(CgSysLogService.class);
    @Autowired
    private SysLogDao sysLogDao;
    @Override
    public Class<SysLog> getEntityClass() {
        return SysLog.class;
    }
    @Override
    public IDaoService<SysLog> getDaoService() {
        return sysLogDao ;
    }
    @Override
    public IFlowService<SysLog> getFlowService() {
        return null;
    }
    @Override
    public Map<String,Object> getDictionary(SysLog obj, Boolean useTree, String dynaFields) {
        Map<String,Object> map=new HashMap<>();
        return map;
    }
    @Override
    public SysLog getDefaultObject(SysLog sysLog) throws IotequException {
        return sysLog;
    }
    @Override
    public void changeEmpty2Null(SysLog sysLog) {
        if (Objects.nonNull(sysLog)) {
        }
    }
}
