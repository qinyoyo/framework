package top.iotequ.framework.flow.impl;

import org.springframework.core.env.Environment;
import top.iotequ.framework.dao.FlowProcessDao;
import top.iotequ.framework.exception.IotequException;
import top.iotequ.framework.exception.IotequThrowable;
import top.iotequ.framework.flow.FlowRecord;
import top.iotequ.framework.flow.IFlowService;
import top.iotequ.framework.pojo.CgEntity;
import top.iotequ.framework.pojo.FlowProcess;
import top.iotequ.framework.util.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public abstract class SysFlowProcessService<T extends CgEntity> implements IFlowService<T> {

    @Override
    public List<FlowRecord> getFlowProcess(String flowId) {
        FlowProcessDao dao = Util.getBean(FlowProcessDao.class);
        List<FlowProcess> list = dao.listBy("flow_id='" + flowId + "'", "time desc");
        List<FlowRecord> ret = new ArrayList<>();
        if (!Util.isEmpty(list)) {
            for (FlowProcess fp : list) {
                FlowRecord f = new FlowRecord();
                f.setNextOperator(fp.getNextOperatorSysUserRealName());
                f.setNote(fp.getNote());
                f.setOperation(fp.getOperation());
                f.setOperator(fp.getOperatorSysUserRealName());
                f.setSelection(fp.getSelection());
                f.setState(fp.getStateName1());
                f.setTime(fp.getTime());
                ret.add(f);
            }
        }
        return ret;
    }

    @Override
    public void saveFlowProcess(Object obj) throws IotequException {
        if (Objects.nonNull(obj) && obj instanceof FlowProcess) {
            FlowProcess fp = (FlowProcess) obj;
            FlowProcessDao dao = Util.getBean(FlowProcessDao.class);
            fp.setId(null);
            dao.insert(fp);
        }
    }
    @Override
    public void  deleteFlowProcess(String ids) throws IotequException {
        SqlUtil.sqlExecute("delete from sys_flow_process where flow_id = ?",ids);
    }
    @Override
    public Object generateFlowProcess(HttpServletRequest request, T obj0, T obj1, String operation) throws IotequException {
        if (request == null) throw new IotequException(IotequThrowable.NULL_OBJECT, "flow object");
        if (obj1 == null) throw new IotequException(IotequThrowable.NULL_OBJECT, "entity object");
        String id = StringUtil.toString(obj1.getPkValue());
        if (Util.isEmpty(id)) throw new IotequException(IotequThrowable.NULL_OBJECT, "id");
        FlowProcess fp = new FlowProcess();
        fp.setFlowId(id);
        fp.setTime(new Date());

        Integer oldState = (obj0 == null ? null : getState(obj0));
        Integer state1 = (obj1 == null ? null : getState(obj1));
        fp.setState0(oldState);
        fp.setState1(state1);
        List<Map<String, Object>> sl = getStates();
        fp.setStateName0(Objects.isNull(oldState) ? null : Util.getTextFromDict(sl, oldState));
        fp.setStateName1(Util.getTextFromDict(sl, state1));

        fp.setNextOperator(request.getParameter(flowNextOperator));

        fp.setNote(request.getParameter(flowNote));

        fp.setOperation(Util.isEmpty(operation) ? getName() : getOperationName(operation));

        fp.setSelection(Util.isEmpty(operation) ? "" : request.getParameter(flowSelection));

        fp.setOperator(Util.getUser().getId());

        fp.setNextOperatorSysUserRealName(Util.getUser().getRealName());

        return fp;
    }

    @Override
    public List<Map<String, Object>> getSelections(String operation) {
        return new ArrayList<Map<String, Object>>() {{
            add(new HashMap<String, Object>() {{
                put("value", APPROVE);
                put("text", "system.action.flowApprove");
            }});
            add(new HashMap<String, Object>() {{
                put("value", DENY);
                put("text", "system.action.flowDeny");
            }});
            add(new HashMap<String, Object>() {{
                put("value", REVIEW);
                put("text", "system.action.flowReview");
            }});
        }};
    }

    public String getBaseRoute() { return "/"; }

    @Override
    public String flowRoute(String operation, String flowId) {
        return getBaseRoute() + operation+"?openMode=edit&id="+flowId;
    }
    @Override
    public String recordViewRoute(String flowId) {
        return getBaseRoute() + "record?openMode=view&id="+flowId;
    }

    private void sendSms(String sendTo, String eventName, String eventType, Date eventTime, String operation, String msgid) {
        // "尊敬的 name ,您有关于 eventname 的 eventtype 事件(发生时间eventtime),需要及时operation,请点击http://www.svein.com.cn/msgid进行操作";
        String smsModules = Util.getBean(Environment.class).getProperty("sms.modules");
        if (smsModules != null && smsModules.toLowerCase().contains(this.getModuleName().toLowerCase())) {
            try {
                String mobile = SqlUtil.sqlQueryString(false, "select mobile_phone from sys_user where id=?", sendTo);
                if (!Util.isEmpty(mobile)) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", getUserName(sendTo));
                    map.put("eventname", eventName);
                    map.put("eventtype", eventType);
                    map.put("operation", operation);
                    map.put("eventtime", new Date());
                    if (msgid != null) map.put("messageid", "m/" + msgid);
                    Util.sendTemplateSmsToMobile(mobile, null, map);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    String getUserName(String id) {
        try {
            return SqlUtil.sqlQueryString(false, "select real_name from sys_user where id=?", id);
        } catch (IotequException e) {
            return id;
        }
    }

    void sendMassageNew(HttpServletRequest request, T obj) throws IotequException {
        String flowId = EntityUtil.getStringPkValueFrom(obj);
        String sendTo = this.getNextOperator(obj);
        String nextOperation = this.getNextOperations(obj,sendTo).split(",")[0];
        String flowCopyToList=this.getCopyToList(obj);
        String register = this.getRegisterBy(obj);
        String registerName = getUserName(register);
        String title = "发起流程请求";;
        String content = registerName+"创建"+this.getName()+":"+this.getItemName(obj);;
        String msgid =  null;
        if (!Util.isEmpty(sendTo) && !Util.equals(register,sendTo)) {
            String url = this.flowRoute(nextOperation,flowId);
            msgid = StringUtil.toString(Util.sendMessage(sendTo, title, content, url, flowId));
            if (msgid!=null) sendSms(sendTo, this.getItemName(obj),this.getName() ,new Date(), getOperationName(nextOperation), msgid);
        }
        if (!Util.isEmpty(flowCopyToList)) {
            String[] ss = flowCopyToList.split(",");
            String url = this.recordViewRoute(flowId);
            for (String s : ss) {
                if (!Util.isEmpty(s) && !Util.equals(s, sendTo) && !Util.equals(s, register))
                    Util.sendMessage(s, title, content, url, flowId);
            }
        }
    }
    void sendMassageUpdate(HttpServletRequest request, T obj0, T obj) throws IotequException {
    }

    void sendMassageDelete(HttpServletRequest request, T obj) throws IotequException {
        String flowId = EntityUtil.getStringPkValueFrom(obj);
        String sendTo = this.getNextOperator(obj);
        String flowCopyToList=this.getCopyToList(obj);
        String sender = Util.getUser().getId();
        String senderName = getUserName(sender);
        String register = this.getRegisterBy(obj);
        String registerName = getUserName(register);
        String title = "删除流程";
        String content = senderName+"已删除"+registerName+"发起的"+this.getName();
        if (!Util.equals(sender,sendTo) && !Util.isEmpty(sendTo)) {
            Util.sendMessage(sendTo, title, content, null, flowId);
        }
        if (!Util.equals(register,sender) && !Util.isEmpty(register)) {
            content = "你发起的"+this.getItemName(obj)+",已被"+senderName + "删除";
            Util.sendMessage(register, title, content , null, flowId);
        }
        if (!Util.isEmpty(flowCopyToList)) {
            String[] ss = flowCopyToList.split(",");
            for (String s : ss) {
                if (!Util.isEmpty(s) && !Util.equals(s, sendTo) && !Util.equals(s, register) && !Util.equals(s, sender))
                    Util.sendMessage(s, title, content, null, flowId);
            }
        }
    }

    public void sendMessageFlow(HttpServletRequest request, T obj0, T obj, String operation) throws IotequException {
        String flowId = EntityUtil.getStringPkValueFrom(obj);
        String sendTo = this.getNextOperator(obj);
        String nextOperation = Util.isEmpty(sendTo) ? "" : Util.isEmpty(this.getNextOperations(obj,sendTo),"").split(",")[0];
        String flowCopyToList=this.getCopyToList(obj);
        String sender = Util.getUser().getId();
        String senderName = getUserName(sender);
        String register = this.getRegisterBy(obj);
        String registerName = getUserName(register);
        String title = "流程状态变迁";
        String content = senderName+"已处理"+registerName+"创建"+this.getName()+"";
        String selection = request.getParameter(flowSelection);
        if (APPROVE.equals(selection)) selection = ":通过";
        else if (DENY.equals(selection)) selection = ":否决";
        else if (REVIEW.equals(selection)) selection = ":需要重新评审";
        else selection="";
        if (!Util.isEmpty(sendTo) && !Util.isEmpty(nextOperation) && !Util.equals(sendTo,sender)) {
            String url = this.flowRoute(nextOperation,flowId);
            String msgid =  StringUtil.toString(Util.sendMessage(sendTo, title, content+selection, url, flowId));
            if (msgid!=null) sendSms(sendTo, this.getItemName(obj),this.getName() ,new Date(), getOperationName(nextOperation), msgid);
        }
        if (!Util.equals(register,sender) && !Util.isEmpty(register)) {
            content = "你发起的"+this.getItemName(obj)+",已被"+sender + "处理"+selection;
            String url = this.recordViewRoute(flowId);
            String msgid = StringUtil.toString(Util.sendMessage(register, "流程状态变迁", content , url, flowId));
            if (msgid!=null && Util.isEmpty(sendTo)) sendSms(register, this.getItemName(obj),this.getName() ,new Date(), "查看", msgid);
        }
        if (!Util.isEmpty(flowCopyToList)) {
            String url = this.recordViewRoute(flowId);
            String[] ss = flowCopyToList.split(",");
            for (String s : ss) {
                if (!Util.isEmpty(s) && !Util.equals(s, sendTo) && !Util.equals(s, register) && !Util.equals(s, sender))
                    Util.sendMessage(s, title, content, url, flowId);
            }
        }
    }
    @Override
    public void sendMessage(HttpServletRequest request, T obj0, T obj, String operation) throws IotequException {
        if ("add".equals(operation)) sendMassageNew(request,obj);
        else if ("update".equals(operation)) sendMassageUpdate(request,obj0,obj);
        else if ("delete".equals(operation)) sendMassageDelete(request,obj);
        else if (obj0!=null && obj!=null) sendMessageFlow(request,obj0,obj,operation); 
    }
}
