package top.iotequ.framework.event;

import lombok.Getter;
import lombok.Setter;
import top.iotequ.framework.bean.SpringContext;
import top.iotequ.framework.util.Util;

@Setter
@Getter
public class SystemParameterChangedEvent {
    public static final String SP_PERMISSION_INITIAL = "SP_PERMISSION_INITIAL";
    public static final String SP_DATA_DICT = "SP_DATA_DICT";
    public static final String SP_ORG_RELOAD = "SP_ORG_RELOAD";
    public static void sendPeemissionInitialEvent(Object value) {
        SystemParameterChangedEvent event=new SystemParameterChangedEvent(value);
        Util.sendRedisMessage(SP_PERMISSION_INITIAL,event);
    }
    public static void sendDataDictEvent(Object value) {
        SystemParameterChangedEvent event=new SystemParameterChangedEvent(value);
        Util.sendRedisMessage(SP_DATA_DICT,event);
    }
    public static void sendReloadOrgEvent(Object value) {
        SystemParameterChangedEvent event=new SystemParameterChangedEvent(value);
        Util.sendRedisMessage(SP_ORG_RELOAD,event);
    }
    public SystemParameterChangedEvent(Object val) {
        sender = SpringContext.nodeId;
        value = val;
    }
    String sender;
    Object  value;
}
