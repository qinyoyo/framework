package top.iotequ.framework.bean;

import ch.qos.logback.classic.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import top.iotequ.framework.dao.DataDictDao;
import top.iotequ.framework.pojo.DataDict;
import top.iotequ.framework.util.IotequVersionInfo;
import top.iotequ.framework.util.MachineInfo;
import top.iotequ.framework.util.OrgUtil;
import top.iotequ.framework.util.Util;

import java.util.*;

import static top.iotequ.framework.util.StringUtil.uuid;

@Component
public class SpringContext implements ApplicationContextAware {
    private static final Logger log = LoggerFactory.getLogger(SpringContext.class);
    public static Map<String, List<Map<String, Object>>> systemDataDict = null;
    private static ApplicationContext applicationContext = null;
    private static DataDictDao dictDaoInstance;
    private static String projectHomeDirection = null;
    public  static final String nodeId = uuid();
    @Autowired
    private DataDictDao dictDao;

    public static void setProjectHomeDirection(String path) {
        projectHomeDirection = path;
    }
    public static String getProjectHomeDirection() {
        return projectHomeDirection;
    }
    public static void getDictData() {
        systemDataDict = new HashMap<String, List<Map<String, Object>>>();
        List<DataDict> list = dictDaoInstance.listBy("dict!='controller_description'", "dict,order_num");
        String dict = null;
        List<Map<String, Object>> dictList = null;
        if (list != null && list.size() > 0) {
            for (DataDict d : list) { // 支持逗号列表的快速定义方式
                String [] vv = d.getCode().split(",");
                String [] tt = d.getText().split(",");
                int order = d.getOrderNum()==null ? 0 : d.getOrderNum();
                for (int i=0;i<vv.length && i<tt.length;i++) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("value", vv[i].trim());
                    map.put("text", tt[i].trim());
                    map.put("order_num", order+i );
                    if (dict == null) {
                        dictList = new ArrayList<Map<String, Object>>();
                        dict = d.getDict();
                    }
                    if (dict.equals(d.getDict())) dictList.add(map);
                    else {
                        systemDataDict.put(dict, dictList);
                        dict = d.getDict();
                        dictList = new ArrayList<Map<String, Object>>();
                        dictList.add(map);
                    }
                }
            }
            if (dict != null && dictList != null) systemDataDict.put(dict, dictList);
        }
    }

    //获取applicationContext
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        dictDaoInstance = dictDao;
        if (log instanceof ch.qos.logback.classic.Logger) {
           ((ch.qos.logback.classic.Logger)log).setLevel(Level.ALL);
        }
        if (applicationContext == null) {
            List<IotequVersionInfo> versions = IotequVersionInfo.getAllVersions();
            if (!Util.isEmpty(versions)) {
                log.info("-----------------------------------------Version infomation-----------------------------------");
                for (IotequVersionInfo v : versions) {
                    log.info(v.toString());
                }
            }
            applicationContext = context;
            getDictData();
            OrgUtil.getSystemOrgData();
            try {
                String sc = MachineInfo.getSetupCode();
                log.info("Setup code = " + sc);
            } catch (Exception e) {
            }
            log.info("Home direction = " + projectHomeDirection);
            log.info("Node id = " + nodeId);
            log.info("----------------------------------------------------------------------------------------------");
        }
    }

    //通过name获取 Bean.
    public static Object getBean(String name) {
        try {
            return getApplicationContext().getBean(name);
        } catch (Exception e) {
            return null;
        }
    }

    //通过class获取Bean.
    public static <T> T getBean(Class<T> clazz) {
        try {
            return getApplicationContext().getBean(clazz);
        } catch (Exception e) {
            return null;
        }
    }

    //通过name,以及Clazz返回指定的Bean
    public static <T> T getBean(String name, Class<T> clazz) {
        return getApplicationContext().getBean(name, clazz);
    }
}
