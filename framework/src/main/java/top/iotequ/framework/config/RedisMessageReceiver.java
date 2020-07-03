package top.iotequ.framework.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import top.iotequ.framework.bean.SpringContext;
import top.iotequ.framework.event.SystemParameterChangedEvent;
import top.iotequ.framework.security.service.SecurityService;
import top.iotequ.framework.util.OrgUtil;
import top.iotequ.framework.util.Util;

@Service
@ConditionalOnProperty(value="spring.redis.url")
public class RedisMessageReceiver {
    private static final Logger log = LoggerFactory.getLogger(RedisMessageReceiver.class);
    public void receiveMessage(String message, String channel) {
        log.debug("Redis received {} : {}", channel, message);
        try {
            SystemParameterChangedEvent event = Util.getGson().fromJson(message, SystemParameterChangedEvent.class);
            if (event!=null) {
                if (!SpringContext.nodeId.equals(event.getSender())) {
                    switch (channel) {
                        case SystemParameterChangedEvent.SP_PERMISSION_INITIAL:
                            SecurityService securityService=Util.getBean(SecurityService.class);
                            if (securityService!=null)  securityService.refreshPermission();
                            break;
                        case SystemParameterChangedEvent.SP_DATA_DICT:
                            SpringContext.getDictData();
                            break;
                        case SystemParameterChangedEvent.SP_ORG_RELOAD:
                            OrgUtil.getSystemOrgData();
                            break;
                    }
                }
            }
        }
        catch (Exception e) {
        }
    }
}