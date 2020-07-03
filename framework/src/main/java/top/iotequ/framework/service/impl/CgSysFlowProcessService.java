package top.iotequ.framework.service.impl;
import top.iotequ.framework.pojo.FlowProcess;
import top.iotequ.framework.dao.FlowProcessDao;
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
@ConditionalOnMissingClass({"top.iotequ.framework.service.impl.SysFlowProcessService"})
@Service(value="sysFlowProcessService")
public class CgSysFlowProcessService extends CgService<FlowProcess>  {
private static final Logger log = LoggerFactory.getLogger(CgSysFlowProcessService.class);
    @Autowired
    private FlowProcessDao flowProcessDao;
    @Override
    public Class<FlowProcess> getEntityClass() {
        return FlowProcess.class;
    }
    @Override
    public IDaoService<FlowProcess> getDaoService() {
        return flowProcessDao ;
    }
    @Override
    public IFlowService<FlowProcess> getFlowService() {
        return null;
    }
    @Override
    public Map<String,Object> getDictionary(FlowProcess obj, Boolean useTree, String dynaFields) {
        Map<String,Object> map=new HashMap<>();
        return map;
    }
    @Override
    public FlowProcess getDefaultObject(FlowProcess flowProcess) throws IotequException {
        return flowProcess;
    }
    @Override
    public void changeEmpty2Null(FlowProcess flowProcess) {
        if (Objects.nonNull(flowProcess)) {
        }
    }
}
