package top.iotequ.framework.service.impl;
import top.iotequ.framework.pojo.Message;
import top.iotequ.framework.dao.MessageDao;
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
@ConditionalOnMissingClass({"top.iotequ.framework.service.impl.SysMessageService"})
@Service(value="sysMessageService")
public class CgSysMessageService extends CgService<Message>  {
private static final Logger log = LoggerFactory.getLogger(CgSysMessageService.class);
    @Autowired
    private MessageDao messageDao;
    @Override
    public Class<Message> getEntityClass() {
        return Message.class;
    }
    @Override
    public IDaoService<Message> getDaoService() {
        return messageDao ;
    }
    @Override
    public IFlowService<Message> getFlowService() {
        return null;
    }
    @Override
    public Map<String,Object> getDictionary(Message obj, Boolean useTree, String dynaFields) {
        Map<String,Object> map=new HashMap<>();
        return map;
    }
    @Override
    public Message getDefaultObject(Message message) throws IotequException {
        return message;
    }
    @Override
    public void changeEmpty2Null(Message message) {
        if (Objects.nonNull(message)) {
        }
    }
}
