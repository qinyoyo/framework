package top.iotequ.framework.util;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.apache.tomcat.util.http.fileupload.ParameterParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.env.PropertiesPropertySourceLoader;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.boot.web.server.MimeMappings;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import top.iotequ.framework.config.RedisMessageReceiver;
import top.iotequ.framework.dao.DataDictDao;
import top.iotequ.framework.dao.MessageDao;
import top.iotequ.framework.dao.SysLogDao;
import top.iotequ.framework.exception.IotequException;
import top.iotequ.framework.exception.IotequIOException;
import top.iotequ.framework.exception.IotequThrowable;
import top.iotequ.framework.pojo.*;
import top.iotequ.framework.service.ISmsService;
import top.iotequ.framework.bean.SpringContext;
import top.iotequ.framework.util.gson.*;

import javax.servlet.http.*;
import javax.validation.constraints.NotNull;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.URLEncoder;
import java.sql.Time;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 通用工具类，提供一些常用的静态函数方法
 */

public class Util {
    private static final Logger logger = LoggerFactory.getLogger(Util.class);
    public static final String _addtional_condition = "_addtional_condition";
    public static final String _condition_filter_org_ = "_condition_filter_org_";
    private static final String SEND_VERIFY_CODE_TIME = "SVCT_";
    private static final String SENT_VERIFY_CODE = "VC_";
    public static boolean runInIdeMode = false;
    static public Long toLong(Object o) {
        if (o == null)
            return null;
        else {
            try {
                Double d = Double.parseDouble(o.toString());
                return d.longValue();
            } catch (Exception e) {
                return null;
            }
        }
    }

    static public Integer toInt(Object o) {
        if (o == null)
            return null;
        else {
            try {
                Double d = Double.parseDouble(o.toString());
                return (int) d.longValue();
            } catch (Exception e) {
                return null;
            }
        }
    }

    static public <T> T null2Default(T obj, T def) {
        if (obj == null) return def;
        else return obj;
    }

    static public String getTextFromDict(List<Map<String, Object>> dict, Object value) {
        if (dict == null || dict.isEmpty() || value == null) return null;
        for (Map<String, Object> map : dict) {
            if (EntityUtil.entityEquals(value, map.get("value"))) return StringUtil.toString(map.get("text"));
        }
        return null;
    }
    static public String getTextFromDict(Object value,Object [] valueList,String [] textList) {
        if (value == null || isEmpty(valueList) || isEmpty(textList)) return null;
        for (int i=0;i<valueList.length;i++) {
            if (EntityUtil.entityEquals(value, valueList[i])) return i<textList.length ? textList[i] : StringUtil.toString(value);
        }
        return null;
    }
    static public Object getValueFromDict(List<Map<String, Object>> dict, String text) {
        if (dict == null || dict.isEmpty() || text == null) return null;
        for (Map<String, Object> map : dict) {
            if (EntityUtil.entityEquals(text, map.get("text"))) return map.get("value");
        }
        return null;
    }

    /**
     * @param o : 对象
     * @return ：判断对象是否为null或空串，包括全部为空白字符的串
     */
    static public boolean isEmpty(Object o) {
        if (o == null) return true;
        else if (o.getClass().isArray()) {
           return ((Object[])o).length <= 0;
        } else if (o instanceof Collection) {
            return ((Collection)o).size() <= 0;
        } else return StringUtil.toString(o).trim().isEmpty();
    }

    static public String getPath(Class<?> clazz) {
        ApplicationHome home = new ApplicationHome(clazz == null ? File.class : clazz);
        File file = home.getDir();
        if (file != null) return file.getPath();
        else return null;
    }

    static public String getSource(Class<?> clazz) {
        ApplicationHome home = new ApplicationHome(clazz == null ? File.class : clazz);
        File file = home.getSource();
        if (file != null) return file.getPath();
        else return null;
    }

    /**
     * 判断串是否相等
     *
     * @param s1 第一个串
     * @param s2 第二个串
     * @return 两个串相等或均为空时返回真，大小写敏感
     */
    static public boolean equals(String s1, String s2) {
        if (s1 != null && s2 != null)
            return s1.trim().equals(s2.trim());
        else if (s1 == null)
            return isEmpty(s2);
        else
            return isEmpty(s1);
    }

    /**
     * 为空时返回缺省值，否则返回对象的toString
     *
     * @param o   对象
     * @param def 缺省值
     * @return o为空时返回缺省值，否则返回对象的toString
     */
    static public String isEmpty(Object o, String def) {
        if (o == null)
            return def;
        else
            return (o.toString().isEmpty() ? def : o.toString());
    }

    /**
     * 根据字符串解析一个boolean值
     *
     * @param obj 对象
     * @return 解析一个boolean值
     */
    static public boolean boolValue(Object obj) {
        if (obj == null) return false;
        else if (obj instanceof Boolean) return (Boolean) obj;
        else if (obj instanceof Integer) return (Integer) obj != 0;
        else if (obj instanceof Short) return (Short) obj != 0;
        else if (obj instanceof Byte) return (Byte) obj != 0;
        else if (obj instanceof Long) return (Long) obj != 0;
        else {
            String s = obj.toString().trim().toLowerCase();
            if (s.equals("1") || s.equals("true") || s.equals("yes") || s.equals("t") || s.equals("y") || s.equals(".t."))
                return true;
            else
                return false;
        }
    }

    /**
     * 获得contextPath
     *
     * @return contextPath
     */
    static public String getContextPath() {
        HttpServletRequest request = Util.getRequest();
        return request.getContextPath();
    }

    /**
     * 获得完整url地址
     *
     * @param url url
     * @return 完整地址
     */
    static public String realUrl(String url) {
        if (url == null) return null;
        if (url.toLowerCase().startsWith("http")) return url;
        String contextPath = getContextPath();

        if (isEmpty(contextPath) || contextPath.equals("/"))
            return url;
        if (contextPath.endsWith("/") && url.startsWith("/"))
            return contextPath + url.substring(1);
        else
            return contextPath + url;
    }

    /**
     * 判断资源文件是否存在
     *
     * @param resourceFile 资源文件名，全名，包括static,template等目录前缀
     * @return 是否存在
     */
    static public boolean exists(String resourceFile) {
        Resource resource = new ClassPathResource(resourceFile);
        try {
            InputStream is = resource.getInputStream();
            is.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 获得当前对话的 HttpServletRequest
     *
     * @return 获得当前对话的 HttpServletRequest
     */
    public static HttpServletRequest getRequest() {
        if (RequestContextHolder.getRequestAttributes() != null) {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                    .getRequest();
            return request;
        } else return null;
    }

    public static HttpServletResponse getResponse() {
        if (RequestContextHolder.getRequestAttributes() != null) {
            HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                    .getResponse();
            return response;
        } else return null;
    }
    /**
     * 获取当前对话
     *
     * @return 获取当前对话
     */
    public static HttpSession getSession() {
        HttpSession session = getRequest().getSession();
        return session;
    }

    /**
     * 返回当前对话对应键值的属性值
     *
     * @param key 键值
     * @return 返回当前对话对应键值的属性值
     */
    public static Object getSessionAttribute(String key) {
        HttpSession session = getSession();
        if (session != null) return session.getAttribute(key);
        else return null;
    }

    /**
     * 设置当前对话的键值属性
     *
     * @param key  键值
     * @param attr 设置当前对话的键值属性
     */
    public static void setSessionAttribute(String key, Object attr) {
        HttpSession session = getSession();
        if (session != null) session.setAttribute(key, attr);
    }

    /**
     * 删除当前对话的键值属性
     *
     * @param key 键值
     */
    public static void removeSessionAttribute(String key) {
        HttpSession session = getSession();
        if (session != null) session.removeAttribute(key);
    }

    /**
     * 获得会话处理进度
     *
     * @return 当前进度, 0-100
     */
    public static Integer getProgress() {
        Object o = getSessionAttribute("progress");
        if (o != null) {
            try {
                int i = Integer.parseInt(o.toString());
                if (i >= 0 && i <= 100) return i;
            } catch (Exception e) {
                return 100;
            }
        }
        return 100;
    }

    /**
     * 设置会话的处理进度
     *
     * @param p 进度，0-100
     */
    public static void setProgress(Integer p) {
        if (p != null && p >= 0 && p <= 100) setSessionAttribute("progress", p);
        else setSessionAttribute("progress", null);
    }

    /**
     * 当前用户
     *
     * @return 返回当前对话的用户
     */
    public static User getUser() {
        SecurityContext sc = SecurityContextHolder.getContext();
        if (sc != null) {
            Authentication au = sc.getAuthentication();
            if (au != null) {
                Object obj = au.getPrincipal();
                if (obj != null && obj instanceof User) {
                    return (User) obj;
                }
            }
        }
        return null;
    }
    public static String getLanguage() {
        return StringUtil.toJsonString(getSessionAttribute("iotequ_language"));
    }
    public static void setLanguage(String locale) {
        setSessionAttribute("iotequ_language",locale);
    }
    public static boolean hasRoleAdmin() {
        User user = getUser();
        if (user==null || "guest".equals(user.getName())) return false;
        else if ("admin".equals(user.getName())) return true;
        else {
            String roles = user.getRoleList();
            if (isEmpty(roles)) return false;
            return SqlUtil.sqlExist(false,"select * from sys_role where code='admin' and id in ("+roles+")");
        }
    }
    /**
     * 认证状态
     *
     * @return 当前对话是否已经认证
     */
    public static boolean isAuthenticated() {
        SecurityContext sc = SecurityContextHolder.getContext();
        if (sc != null) {
            Authentication au = sc.getAuthentication();
            if (au != null) {
                return au.isAuthenticated() && au.getPrincipal() instanceof User;
            }
        }
        return false;
    }

    /**
     * 获取ip地址
     *
     * @param request request
     * @return 从 request获得客户ip地址
     */
    public static String getIpAddr(HttpServletRequest request) {
        if (request == null)
            request = getRequest();
        if (request == null)
            return null;
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip.equals("0:0:0:0:0:0:0:1")) {
            ip = "localhost";
        }
        return ip;
    }

    public static Object valueOf(Object obj, Class<?> clazz) {
        if (obj == null || clazz == null) return null;
        String type = clazz.getName();
        String s = obj.toString();
        try {
            if (obj.getClass().equals(clazz)) return obj;
            if (type.endsWith("Boolean")) return Util.boolValue(s);
            else if (type.endsWith("String")) return s;
            else if (type.endsWith("Integer")) return Integer.valueOf(s);
            else if (type.endsWith("Long")) return Long.valueOf(s);
            else if (type.endsWith("Short")) return Short.valueOf(s);
            else if (type.endsWith("Byte")) return Byte.valueOf(s);
            else if (type.endsWith("Double")) return Double.valueOf(s);
            else if (type.endsWith("Float")) return Float.valueOf(s);
            else if (type.endsWith("Date")) return DateUtil.string2Date(s);
            else if (type.endsWith("byte[]")) return s.getBytes();
        } catch (Exception e) {
        }
        if (type.equals("boolean")) return Util.boolValue(s);
        else if (type.equals("char")) {
            if (s.isEmpty() || s == null) return (char) 0;
            else return s.charAt(0);
        } else if (type.equals("int")) {
            try {
                return Integer.parseInt(s);
            } catch (Exception e) {
                return (int) 0;
            }
        } else if (type.equals("long")) {
            try {
                return Long.parseLong(s);
            } catch (Exception e) {
                return (long) 0;
            }
        } else if (type.equals("short")) {
            try {
                return Short.parseShort(s);
            } catch (Exception e) {
                return (short) 0;
            }
        } else if (type.equals("byte")) {
            try {
                return Byte.parseByte(s);
            } catch (Exception e) {
                return (byte) 0;
            }
        } else if (type.equals("double")) {
            try {
                return Double.parseDouble(s);
            } catch (Exception e) {
                return (double) 0;
            }
        } else if (type.equals("float")) {
            try {
                return Float.parseFloat(s);
            } catch (Exception e) {
                return (float) 0;
            }
        }
        return null;
    }

    public static Gson getGson() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .registerTypeAdapter(Boolean.class, new GsonBooleanTypeAdapter())
                .registerTypeAdapter(Date.class, new GsonDateTypeAdapter())
                .registerTypeAdapter(Time.class, new GsonTimeTypeAdapter())
                .registerTypeAdapter(java.sql.Date.class,new GsonSqlDateTypeAdapter())
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .registerTypeAdapter(new TypeToken<Map<String, Object>>() {}.getType(), new MapTypeAdapter())
                .create();
        return gson;
    }

    static public Map<String,Object> mapFromJson(String json) {
        return getGson().fromJson(json,new TypeToken<Map<String, Object>>() {}.getType());
    }
    /**
     * 获得cookie值
     *
     * @param request request
     * @param name    名称
     * @return cookie值
     */
    static public String getCookie(HttpServletRequest request, String name) {
        if (request == null || name == null)
            return null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (EntityUtil.entityEquals(c.getName(), name))
                    return c.getValue();
            }
        }
        return null;
    }

    /**
     * 设置cookie值
     *
     * @param request  request
     * @param response response
     * @param name     名称
     * @param value    值
     * @param age      时效
     */
    static public void setCookie(HttpServletRequest request, HttpServletResponse response, String name, String value, int age) {
        if (response == null || name == null)
            return;
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(age);
        String contextPath = request.getContextPath();
        cookie.setPath(contextPath.length() > 0 ? contextPath : "/");
        cookie.setSecure(request.isSecure());
        response.addCookie(cookie);
    }

    /**
     * 获得指定类bean的对象
     *
     * @param clazz 类
     * @param <T>   泛型类型
     * @return bean对象
     */
    public static <T> T getBean(Class<T> clazz) {
        return SpringContext.getBean(clazz);
    }

    public static <T> List<T> getImplementedBean(Class<T> clazz) {
        List<T> list = new ArrayList<T>();
        ApplicationContext context = Util.getApplicationContext();
        Map<String, T> result = context.getBeansOfType(clazz);
        int p = clazz.getName().lastIndexOf(".");
        String myName = StringUtil.firstLetterLower(p >= 0 ? clazz.getName().substring(p + 1) : clazz.getName());
        for (String k : result.keySet()) {
            if (k.equals(myName)) continue;
            T t = result.get(k);
            list.add(t);
        }
        return list;
    }

    /**
     * 通过bean名称获得bean
     *
     * @param beanName bean名
     * @return bean对象
     */
    public static Object getBean(String beanName) {
        return SpringContext.getBean(beanName);
    }

    public static ApplicationContext getApplicationContext() {
        return SpringContext.getApplicationContext();
    }

    /**
     * 当前用户发消息
     *
     * @param receiver 接受者
     * @param title    标题
     * @param content  内容
     * @param url      参考url地址
     * @param eventId  关联对象id
     * @return 消息ID编号，null表示未成功
     */
    public static Integer sendMessage(String receiver, String title, String content, String url, String eventId) {
        return sendMessage(receiver, getUser(), title, content, url, eventId);
    }

    /**
     * 某用户发消息
     *
     * @param receiver 接受者
     * @param sender   发送者，为空世表示系统消息
     * @param title    标题
     * @param content  内容
     * @param url      参考url地址
     * @param eventId  关联对象id
     * @return 消息ID编号，null 表示未成功
     */
    public static Integer sendMessage(String receiver, User sender, String title, String content, String url, String eventId) {
        if (isEmpty(title) || isEmpty(content))
            return 0;
        Message msg = new Message();
        if (sender != null) {
            msg.setSenderName(sender.getRealName());
            if (sender.getName().equals("guest") && Objects.nonNull(sender.getWechatOpenid()))
                msg.setSender(sender.getWechatOpenid());
            else
                msg.setSender(sender.getId());
            /*
            if (!isEmpty(eventId) && !isEmpty(receiver)) { // 删除未读的重复信息
                try {
                    SqlUtil.sqlExecute("delete FROM sys_message where read_time is null and receiver=? and event_id=? and sender=?",
                            receiver, eventId, sender.getId());
                } catch (Exception e) {
                }
            }
            */
        } else {
            msg.setSenderName("system");
            /*
            if (!isEmpty(eventId) && !isEmpty(receiver)) { // 删除未读的重复信息
                try {
                    SqlUtil.sqlExecute(
                            "delete FROM sys_message where read_time is null and receiver=? and event_id=? and sender is null",
                            receiver, eventId);
                } catch (Exception e) {
                }
            }
             */
        }
        msg.setCreateTime(new Date());
        msg.setTitle(title);
        msg.setContent(content);
        msg.setEventId(eventId);
        if (!isEmpty(url) && !url.trim().equals("/"))
            msg.setUrl(url.trim());
        if (!isEmpty(receiver)) {
            msg.setReceiver(receiver);
            Object nm = null;
            try {
                nm = SqlUtil.sqlQueryField("select real_name from sys_user where id=?", receiver);
            } catch (Exception e) {
            }
            if (nm != null)
                msg.setReceiverName(nm.toString());
        }
        MessageDao messageDao = SpringContext.getBean(MessageDao.class);
        messageDao.insert(msg);
        return msg.getId();
    }



    /**
     * 获得系统数据字典
     *
     * @param dict 字典名
     * @return 一个Map，键值为 value，text
     */
    public static List<Map<String, Object>> getDataDict(String dict) {
        return SpringContext.systemDataDict.get(dict);
    }

    /**
     * 设置系统数据字典
     *
     * @param dict   字典名
     * @param values 值序列
     * @param names  显示名称序列
     */
    public static void setDataDict(String dict, Object[] values, String[] names) {
        List<Map<String, Object>> old = getDataDict(dict);
        if (old == null) old = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < values.length; i++) {
            Map<String, Object> map = null;
            for (Map<String, Object> m : old) {
                if (EntityUtil.entityEquals(m.get("value"), values[i])) {
                    map = m;
                    break;
                }
            }
            if (map == null) {
                map = new HashMap<String, Object>();
                map.put("value", values[i].toString());
                map.put("text", i < names.length ? names[i].toString() : "???");
                old.add(map);
            } else {
                map.put("text", i < names.length ? names[i].toString() : map.get("text"));
            }
        }
        DataDictDao dictDao = getBean(DataDictDao.class);
        List<DataDict> list = dictDao.listBy("dict='" + dict + "'", "order_num");
        for (int i = 0; i < old.size(); i++) {
            Map<String, Object> m = old.get(i);
            int mode = 1; // 0: skip, 1: insert 2: update
            if (list != null) {
                for (DataDict d : list) {
                    if (m.get("value").toString().equals(d.getCode())) {
                        if (m.get("text").toString().equals(d.getText()))
                            mode = 0;
                        else {
                            d.setText(m.get("text").toString());
                            dictDao.update(d);
                        }
                        ;
                        break;
                    }
                }
            }
            if (mode == 1) {
                DataDict d = new DataDict();
                d.setCode(m.get("value").toString());
                d.setDict(dict);
                d.setOrderNum(i + 1);
                d.setText(m.get("text").toString());
                dictDao.insert(d);
            }
        }
    }

    /**
     * 获得数据字典显示值
     *
     * @param dict  字典
     * @param value 值
     * @return 显示值
     */

    public static String getDataDictText(String dict, Object value) {
        List<Map<String, Object>> mm = getDataDict(dict);
        if (mm != null && mm.size() > 0) {
            for (Map<String, Object> m : mm) {
                if (EntityUtil.entityEquals(m.get("value"), value)) return StringUtil.toString(m.get("text"));
            }
        }
        return null;
    }

    /**
     * 写日志
     *
     * @param logger  logger
     * @param keyword 关键字
     * @param user    产生日志的用户
     * @param format  格式
     * @param args    参数
     */
    public static void writeLog(Logger logger, String keyword, User user, String format, Object... args) {
        String note = String.format(format, args);
        if (note.length() > 400) note = note.substring(0, 400) + "...";
        if (logger != null) logger.debug(note);
        SysLog log = new SysLog();
        log.setKeyword(keyword);
        log.setNote(note);
        log.setTime(new Date());
        if (user == null) user = getUser();
        if (user != null) {
            log.setUserInfo(user.getRealName());
            log.setUserType(user.getName());
        }
        getBean(SysLogDao.class).insert(log);
    }

    /**
     * 获得试用期剩余时间
     *
     * @param trialDays 试用天数
     * @return 剩余天数
     */
    static public int getTrialDaysLeft(@NotNull String module, int trialDays) {
        Date dt = Util.getVersionBuildTime(module);
        int ds = (int) ((new Date().getTime() - dt.getTime()) / 1000 / 3600 / 24);
        if (ds >= trialDays) return 0;
        else return trialDays - ds;
    }

    /**
     * 获得特定序列号的licence
     *
     * @param sn 序列号
     * @return licence
     */
    static public int getLicence(String sn) {
        try {
            return MachineInfo.getLicence(sn);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 获得特定模块序列号的licence
     *
     * @param sn     序列号
     * @param module 模块名称
     * @return licence
     */
    static public int getLicence(String sn, String module) {
        try {
            return MachineInfo.getLicence(sn, module);
        } catch (Exception e) {
            return 0;
        }
    }

    static public File uploadFileDir(String generatorName) {
        String uploadPath = getBean(Environment.class).getProperty("spring.upload-path");
        return new File(Util.isEmpty(uploadPath, ""), "/" + (isEmpty(generatorName) ? "" : generatorName + "/")).getAbsoluteFile();
    }

    static public String uploadFilename(String entityName, @NotNull String pkId, String name) {
        String nm = Util.isEmpty(entityName, "");
        if (!Util.isEmpty(pkId)) {
            if (nm.isEmpty()) nm = pkId;
            else nm += ("_" + pkId);
        }
        if (!Util.isEmpty(name)) {
            if (nm.isEmpty()) nm = name;
            else nm += ("_" + name);
        }
        return nm;
    }

    /**
     * 将上传的part写入文件
     *
     * @param request  request
     * @param partName request记录part的属性名，多个文件依次为 partName，partName1，partName2...
     * @param dir      保存文件的目录
     * @param prefix   文件名加上这个前缀为新的文件名，为空忽略
     * @param newName  采用新的文件名，为空忽略
     * @param multiple 是否允许上传多个文件
     * @return 写入的文件列表, 支持多个文件上传
     * @throws IotequException 异常
     */
    static public List<File> uploadFile(HttpServletRequest request, String partName, File dir, String prefix, String newName, boolean multiple) throws IotequException {
        try {
            List<File> list = new ArrayList<File>();
            int index = 0;
            while (true) {
                Part part = request.getPart(partName + (index == 0 ? "" : "_"+String.valueOf(index)));
                if (part == null) return list;
                index++;
                String name = "";//part.getSubmittedFileName() 这个函数ie下有问题
                String cd = part.getHeader("Content-Disposition");
                if (cd != null) {
                    String cdl = cd.toLowerCase();
                    if (cdl.startsWith("form-data") || cdl.startsWith("attachment")) {
                        ParameterParser paramParser = new ParameterParser();
                        paramParser.setLowerCaseNames(true);
                        Map<String, String> params = paramParser.parse(cd, ';');
                        if (params.containsKey("filename")) {
                            name = params.get("filename");
                            if (name != null) {
                                name = new File(name.trim()).getName();
                            } else {
                                name = "";
                            }
                        }
                    }
                }
                if (prefix != null) name = prefix + name;
                else if (newName != null) {
                    name = newName + (name.lastIndexOf(".") > 0 ? name.substring(name.lastIndexOf(".")) : "");
                }
                File file = new File(dir, name);
                if (!dir.exists()) dir.mkdirs();
                else if (!multiple) {
                    File[] files = dir.listFiles(new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String name) {
                            return name.startsWith(prefix);
                        }
                    });
                    if (files!=null && files.length>0) {
                        for (int i=0;i<files.length;i++) {
                            files[i].delete();
                        }
                    }
                }
                part.write(file.getAbsolutePath());
                list.add(file);
                if (!multiple) return list;
            }
        } catch (Exception e) {
            throw IotequException.newInstance(e);
        }
    }

    /**
     * 下载指定的文件
     *
     * @param file        需要下载的文件
     * @param response    response
     * @param browserOpen true: 支持浏览器打开
     * @throws IotequException 异常
     */
    static public void downloadFile(File file, HttpServletResponse response, boolean browserOpen) throws IotequException {
        try {
            if (file == null || !file.exists())
                throw new IotequIOException(IotequThrowable.IO_OBJECT_NOT_EXIST, "File not exists");
            String fileName = file.getName();
            if (browserOpen) {
                int p = fileName.lastIndexOf(".");
                String ct = null;
                if (p >= 0) {
                    ct = MimeMappings.DEFAULT.get(fileName.substring(p + 1).toLowerCase());
                }
                response.setHeader("content-type", ct == null ? "application/octet-stream" : ct);
            } else response.setHeader("content-type", "application/octet-stream");
            // 下载文件能正常显示中文
            response.setHeader("Content-Disposition", (browserOpen ? "inline" : "attachment") + ";filename=" + URLEncoder.encode(fileName, "UTF-8"));
            response.setHeader("Content-Length", String.valueOf(file.length()));

            byte[] buffer = new byte[1024];
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            try {
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                OutputStream os = response.getOutputStream();
                int i = bis.read(buffer);
                while (i != -1) {
                    os.write(buffer, 0, i);
                    i = bis.read(buffer);
                }
            } catch (Exception e) {
                throw new IotequIOException(IotequThrowable.IO_EXCEPTION, e.getMessage());
            } finally {
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }catch (Exception e) {
            throw IotequException.newInstance(e);
        }
    }

    public static String get2DCode(Object content, int width, int height, String logoFile) {
        if (content == null) return null;
        File logoPic = isEmpty(logoFile) ? null : new File(logoFile);
        if (logoPic != null && !logoPic.exists()) {
            Resource resource = new ClassPathResource(logoFile);
            if (resource != null) {
                try {
                    logoPic = resource.getFile();
                } catch (IOException e) {
                    logoPic = null;
                }
            } else logoPic = null;
            if (!logoPic.exists()) logoPic = null;
        }
        try {
            return ZxingCode.getQRCode(content.toString(), width, height, logoPic);
        } catch (Exception e) {
            return null;
        }
    }

    private static String additionalPropertyFile(@NotNull String path, @NotNull String fileName) {
        PropertiesPropertySourceLoader propLoader = new PropertiesPropertySourceLoader();
        YamlPropertySourceLoader yamlLoader = new YamlPropertySourceLoader();
        String ext = null;
        File file = null;
        int dp = fileName.lastIndexOf(".");
        if (dp > 0) {
            ext = fileName.substring(dp + 1).toLowerCase();
            file = new File(path, fileName);
            if (file != null && file.exists()) {
                if (Arrays.asList(yamlLoader.getFileExtensions()).contains(ext)) {
                    return file.getAbsolutePath();
                } else {
                    if (Arrays.asList(propLoader.getFileExtensions()).contains(ext)) {
                        return file.getAbsolutePath();
                    }
                }
            }
        } else {
            for (String e : propLoader.getFileExtensions()) {
                file = new File(path, fileName + "." + e);
                if (file.exists()) {
                    return file.getAbsolutePath();
                }
            }
            if (file == null || !file.exists()) {
                for (String e : yamlLoader.getFileExtensions()) {
                    file = new File(path, fileName + "." + e);
                    if (file.exists()) {
                        return file.getAbsolutePath();
                    }
                }
            }
        }
        return null;
    }

    private static String additionalPropertyFile(@NotNull String fileName) {
        String path = SpringContext.getProjectHomeDirection();
        if (isEmpty(path)) return null;
        String fullName = additionalPropertyFile(path, fileName);
        return fullName;
    }

    public static void sendVerifyCodeToMobile(@NotNull String mobilePhone) throws IotequException {
        // 调用验证码发送外部接口
        Object o = Util.getSessionAttribute(SEND_VERIFY_CODE_TIME + mobilePhone);
        if (o != null) {
            Long dt = (Long) o;
            long now = new Date().getTime() / 1000;
            if (now - dt < 120) {   // 1分钟之内不重发
                throw new IotequException(IotequThrowable.SMS_TOO_FREQUENTLY, String.valueOf(now - dt));
            }
        }
        //生成随机验证码
        final StringBuffer sb = new StringBuffer();
        final Random random = new Random();
        final String sourseStr = "0123456789";
        for (int i = 0; i < 6; i++) {
            sb.append(sourseStr.charAt(random.nextInt(sourseStr.length())));
        }
        String vc = sb.toString();
        //调用短信发送接口发送短信
        List<ISmsService> smsServiceList = Util.getImplementedBean(ISmsService.class);
        if (smsServiceList != null) {
            for (ISmsService sms : smsServiceList) {
                if (sms.enabled()) {  // 打开短信登录
                    sms.sendVerifyCodeSms(mobilePhone, vc);
                    break;
                }
            }
        } else {
            throw new IotequException(IotequThrowable.SMS_SERVICE_MISS, "没有短信服务");
        }
        //记录验证码和发送时间，手机号码
        Util.setSessionAttribute(SENT_VERIFY_CODE + mobilePhone, vc);
        Long now = new Date().getTime() / 1000;
        Util.setSessionAttribute(SEND_VERIFY_CODE_TIME + mobilePhone, now);
    }

    public static String getSmsRandCode(String mobilePhone) {
        return StringUtil.toString(getSessionAttribute(SENT_VERIFY_CODE + mobilePhone));
    }
    public static void sendTemplateSmsToMobile(String mobilePhone, String templateName, Map<String, Object> map) throws IotequException {
        //调用短信发送接口发送短信
        List<ISmsService> smsServiceList = Util.getImplementedBean(ISmsService.class);
        if (smsServiceList != null) {
            String pattern = "^1([358][0-9]|4[579]|66|7[0135678]|9[89])[0-9]{8}$";
            if (mobilePhone == null || !Pattern.matches(pattern, mobilePhone))
                throw new IotequException(IotequThrowable.IO_FORMATTER_ERROR, "错误的手机号码");
            Object prevSendTime = Util.getSessionAttribute(isEmpty(templateName, "DEFAULT_SMS_TEMPLATE") + "_" + mobilePhone);
            if (prevSendTime != null) {
                long timepassed = new Date().getTime() - (Long) prevSendTime;
                if (timepassed < 60000) throw new IotequException(IotequThrowable.SMS_TOO_FREQUENTLY, "1分钟内禁止频繁发送");
            }
            for (ISmsService sms : smsServiceList) {
                if (sms.enabled()) {  // 打开短信登录
                    sms.sendTemplateSms(mobilePhone, templateName, map);
                    Util.setSessionAttribute(isEmpty(templateName, "DEFAULT_SMS_TEMPLATE") + "_" + mobilePhone, new Date().getTime());
                    break;
                }
            }
        } else {
            throw new IotequException(IotequThrowable.SMS_SERVICE_MISS, "没有短信服务");
        }
    }

    public static void mobileVerifyCodeCheck(@NotNull String mobilePhone, @NotNull String vc) throws IotequException {
        Object o = Util.getSessionAttribute(SEND_VERIFY_CODE_TIME + mobilePhone);
        if (o != null) {
            Object v = Util.getSessionAttribute(SENT_VERIFY_CODE + mobilePhone);
            if (v == null) throw new IotequException(IotequException.INVALID_VERIFICATION_CODE,"null");
            Long dt = (Long) o;
            long now = new Date().getTime() / 1000;
            if (now - dt > 300) {   // 5分钟之后已经失效
                throw new IotequException(IotequException.INVALID_VERIFICATION_CODE,"invalid");
            }
            if (Util.equals(vc, v.toString())) return;
            else throw new IotequException(IotequException.INVALID_VERIFICATION_CODE,"incorrect");
        } else {
            throw new IotequException(IotequException.INVALID_VERIFICATION_CODE,mobilePhone);
        }
    }

    private static void getProjectHomeDiretion(@NotNull Class<?> clazz) {
        if ("file".equals(clazz.getResource("").getProtocol())) {  // ide模式，使用上級目錄
            runInIdeMode = true;
            String path = getPath(clazz);
            if (path.endsWith("\\target\\classes") || path.endsWith("/target/classes")) {
                File hd = new File(path).getParentFile().getParentFile().getParentFile();
                SpringContext.setProjectHomeDirection(hd.getAbsolutePath());
            } else {
                SpringContext.setProjectHomeDirection(getPath(null));
            }
        } else {
            SpringContext.setProjectHomeDirection(getPath(null));
        }
    }
    /**
     * 获得版本信息
     *
     * @return 版本信息
     */

    static public String getVersion(@NotNull String module) {
        IotequVersionInfo ver = IotequVersionInfo.getVersion(module);
        if (ver != null) {
            return ver.getVersion();
        }
        return null;
    }
    /**
     * 获得版本时间
     *
     * @return 版本build时间
     */
    static public Date getVersionBuildTime(@NotNull String module) {
        IotequVersionInfo ver = IotequVersionInfo.getVersion(module);
        if (ver != null) {
            Date dt = ver.getBuildTime();
            if (Objects.nonNull(dt)) return dt;
        }
        return new Date();
    }
    public static File getWebappFile(String path) throws IotequException {
        File webapp = new File(SpringContext.getProjectHomeDirection(),"/webapp");
        if (isEmpty(path)) return webapp;
        else {
            File file=new File(webapp, path);
            if (file.exists()) return file;
            else throw new IotequException(IotequThrowable.IO_OBJECT_NOT_EXIST, file.getAbsolutePath());
        }
    }

    /************************
     * 将字符串写入文件
     * @param s : 字符串
     * @param file : 文件
     * @throws IotequException 出错抛出异常
     */
    static public void writeToFile(String s, File file) throws IotequException {
        try {
            if (file.exists()) {
                file.delete();
            } else {
                File dir = file.getParentFile();
                if (!dir.exists()) dir.mkdirs();
            }
            PrintWriter fw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8")));
            fw.write(s);
            fw.close();
        } catch (Exception e) {
            throw IotequException.newInstance(e);
        }
    }

    /************************
     * 将字符串添加到文件
     * @param s ： 字符
     * @param file : 文件
     * @throws IotequException 出错抛出异常
     */
    static public void appendToFile(String s, File file) throws IotequException {
        try {
            if (!file.exists()) {
                File dir = file.getParentFile();
                if (!dir.exists()) dir.mkdirs();
            }
            PrintWriter fw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "utf-8")));
            fw.write(s);
            fw.close();
        } catch (Exception e) {
            throw IotequException.newInstance(e);
        }
    }


    public static int getProcessID() {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        System.out.println(runtimeMXBean.getName());
        return Integer.valueOf(runtimeMXBean.getName().split("@")[0])
                .intValue();
    }

    public static void commonApplicationRun(@NotNull Class<?> clazz, String applicationProperties, String customerProperties, String[] args) {
        getProjectHomeDiretion(clazz);
        int pid = getProcessID();
        try {
            writeToFile(String.valueOf(pid), new File(SpringContext.getProjectHomeDirection(),"pid.log"));
        } catch (Exception e) {}
        IotequVersionInfo.readVersionInfo(clazz);
        SpringApplicationBuilder appBuilder = new SpringApplicationBuilder(clazz);
        appBuilder.properties("file.encoding=UTF-8");
        String myPropertyFile = additionalPropertyFile(customerProperties == null ? "iotequ" : customerProperties);
        if (myPropertyFile != null) System.out.println("Use additional user property file : " + myPropertyFile);
        if (applicationProperties != null) {
            String location = "spring.config.location=classpath:/" + applicationProperties +
                    (myPropertyFile == null ? "" : "," + myPropertyFile);
            appBuilder.properties(location);
        } else {
            if (myPropertyFile != null) {
                appBuilder.properties("spring.config.additional-location=" + myPropertyFile);
            }
        }
        appBuilder.run(args);
    }
    public static void sendRedisMessage(String channel,Object message) {
        StringRedisTemplate template = getBean(StringRedisTemplate.class);
        if (template!=null) {
            String strMsg = getGson().toJson(message);
            logger.debug("Send redis {} : {}", channel, message);
            template.convertAndSend(channel,strMsg);
        }
    }
}
