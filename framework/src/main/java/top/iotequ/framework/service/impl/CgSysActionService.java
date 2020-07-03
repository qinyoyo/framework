package top.iotequ.framework.service.impl;
import top.iotequ.framework.pojo.Action;
import top.iotequ.framework.dao.ActionDao;
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
@ConditionalOnMissingClass({"top.iotequ.framework.service.impl.SysActionService"})
@Service(value="sysActionService")
public class CgSysActionService extends CgService<Action>  {
private static final Logger log = LoggerFactory.getLogger(CgSysActionService.class);
    @Autowired
    private ActionDao actionDao;
    @Override
    public Class<Action> getEntityClass() {
        return Action.class;
    }
    @Override
    public IDaoService<Action> getDaoService() {
        return actionDao ;
    }
    @Override
    public IFlowService<Action> getFlowService() {
        return null;
    }
    @Override
    public Map<String,Object> getDictionary(Action obj, Boolean useTree, String dynaFields) {
        Map<String,Object> map=new HashMap<>();
        return map;
    }
    @Override
    public Action getDefaultObject(Action action) throws IotequException {
        return action;
    }
    @Override
    public void changeEmpty2Null(Action action) {
        if (Objects.nonNull(action)) {
        }
    }
}
