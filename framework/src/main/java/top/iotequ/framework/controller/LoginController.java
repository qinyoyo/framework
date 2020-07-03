package top.iotequ.framework.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import top.iotequ.framework.dao.RoleDao;
import top.iotequ.framework.dao.UserDao;
import top.iotequ.framework.exception.IotequAuthenticationException;
import top.iotequ.framework.exception.IotequException;
import top.iotequ.framework.exception.IotequThrowable;
import top.iotequ.framework.pojo.User;
import top.iotequ.framework.security.SpringSecurityConfig;
import top.iotequ.framework.security.authority.UrlAccessDecisionManager;
import top.iotequ.framework.service.ISmsService;
import top.iotequ.framework.service.impl.SysUserService;
import top.iotequ.framework.service.ICgService;
import top.iotequ.framework.service.utils.DictionaryUtil;
import top.iotequ.framework.service.utils.UploadDownUtil;
import top.iotequ.framework.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class LoginController {
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    @Autowired
    RoleDao roleDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private Environment env;
    @Autowired
    private SysUserService userService;

    @Autowired
    ICgService<User> service;
    @Autowired
    private SessionRegistry sessionRegistry;

    public void logoutSession() {
        List<Object> users = sessionRegistry.getAllPrincipals(); // 获取session中所有的用户信息
        for (Object principal : users) {
            List<SessionInformation> sessionsInfo = sessionRegistry.getAllSessions(principal, false); // false代表不包含过期session
            if (null != sessionsInfo && sessionsInfo.size() > 0) {
                for (SessionInformation sessionInformation : sessionsInfo) {
                    sessionInformation.expireNow();
                }
            }
        }
    }

    @ResponseBody
    @RequestMapping(value = SpringSecurityConfig.loginPage+"/invalidSession")
    public ResponseEntity<Map<String, Object>> invalidSession(HttpServletRequest req, HttpServletResponse response) {
        return new RestJson().setErrorCode(IotequThrowable.SESSION_TIMEOUT,null).toResponse();
    }

    @ResponseBody
    @RequestMapping(value = SpringSecurityConfig.loginPage)
    public ResponseEntity<Map<String, Object>> loginJson(HttpServletRequest req, HttpServletResponse response) {
        Map<String, Object> bodyj = new HashMap<>();
        bodyj.put("success", false);
        bodyj.put("status", 403);
        bodyj.put("error", IotequThrowable.ACCESS_DENIED);
        String url = StringUtil.toString(Util.getSessionAttribute(UrlAccessDecisionManager.FORBIDDEN_URL));
        if (Util.isEmpty(url)) url = "/login";
        else Util.removeSessionAttribute(UrlAccessDecisionManager.FORBIDDEN_URL);
        bodyj.put("url", Util.realUrl(url));
        response.setStatus(403);
        HttpStatus status = HttpStatus.valueOf(403);
        return new ResponseEntity<>(bodyj, status);
    }

    @ResponseBody
    @RequestMapping(value = SpringSecurityConfig.loginPage + "/sendMobileCode")
    public String sendMobileCode(String mobilePhone, String randCode, Boolean isRegister, HttpServletRequest req) {
        RestJson j = new RestJson();
        if (Util.isEmpty(mobilePhone) || Util.isEmpty(randCode)) {
            j.setErrorCode(IotequThrowable.NULL_OBJECT, "mobilePhone or randCode is null");
        } else if (!Util.equals(randCode, RandCodeImageService.getRandCode())) {
            j.setMessage(new IotequAuthenticationException(IotequThrowable.INVALID_VERIFICATION_CODE, ""));
        } else {
            try {
                String sql = "SELECT id FROM sys_user where mobile_phone=?";
                if (isRegister != null && isRegister) {
                    if (SqlUtil.sqlExist(false, sql, mobilePhone)) throw new IotequException(IotequThrowable.MOBILE_REGISTERED,mobilePhone);
                    String smsProperty = env.getProperty("sms.modules");
                    if (smsProperty==null || !StringUtil.containsItem(smsProperty,"register")) throw new IotequException(IotequThrowable.CONFIG_ERROR,"sms.modules");
                    smsProperty = env.getProperty("sms.register.blackList");
                    if (existTest(smsProperty,mobilePhone)) throw new IotequException(IotequThrowable.MOBILE_REFUSED,"sms.register.blackList:"+mobilePhone);
                    smsProperty = env.getProperty("sms.register.whilteList");
                    if (!Util.isEmpty(smsProperty) && !existTest(smsProperty,mobilePhone) ) throw new IotequException(IotequThrowable.MOBILE_REFUSED,"sms.register.whilteList:"+mobilePhone);
                } else {
                    if (!SqlUtil.sqlExist(false, sql, mobilePhone)) throw new IotequException(IotequThrowable.USER_NOT_EXIST,mobilePhone);
                }
                Util.sendVerifyCodeToMobile(mobilePhone);
            } catch (Exception e) {
                j.setMessage(e);
            }
        }
        return j.toString();
    }

    private User checkAuth() throws IotequException {
        User user=Util.getUser();
        if (user == null || "guest".equals(user.getName())) {
            throw new IotequException(IotequThrowable.NOT_LOGIN,null);
        }
        return user;
    }

    @RequestMapping(value = SpringSecurityConfig.loginPage + "/profile/record", method = {RequestMethod.GET})
    public ResponseEntity<Map<String, Object>> profile(HttpServletRequest request, HttpServletResponse response) throws Exception {
        RestJson j = new RestJson();
        try {
            User user = checkAuth();
            user = userDao.select(user.getId());
            j.dictionary(service.getDictionary(user,true,null));
            j.data(user);
            return j.toResponse();
        } catch (IotequException e) {
            j.setError(e);
            return j.toResponse();
        }
    }

    @RequestMapping(value = SpringSecurityConfig.loginPage + "/profile/save",method = {RequestMethod.POST})
    public ResponseEntity<Map<String, Object>> profileSave(Integer total_filepart, User user,String idSaved, HttpServletRequest request, HttpServletResponse response) {
        RestJson j = new RestJson();
        try {
            checkAuth();
            return service.doSave(false, null,total_filepart, user, idSaved, request).toResponse();
        } catch (Exception e) {
            j.setError(e);
            return j.toResponse();
        }
    }
    @RequestMapping(value = SpringSecurityConfig.loginPage + "/profile/default",method = {RequestMethod.GET})
    public ResponseEntity<Map<String, Object>> profileDefault(HttpServletRequest request, HttpServletResponse response) {
        return new RestJson().toResponse();
    }

    // 忘记密码用户通过手机登录，密码输入手机验证码
    @RequestMapping(value = SpringSecurityConfig.loginPage + "/profile/password",method = {RequestMethod.POST})
    public ResponseEntity<Map<String, Object>> profilePassword(@RequestParam(required=false) String password, @RequestParam(required=false) String newPassword, @RequestParam(required=false) String id, HttpServletRequest request, HttpServletResponse response) {
        RestJson j = new RestJson();
        try {
            User user = checkAuth();
            if (Util.isEmpty(password) || Util.isEmpty(newPassword) || Util.isEmpty(id))
                throw new IotequException(IotequThrowable.PARAMETER_ERROR,null);
            if (password.equals(newPassword)) throw new IotequException(IotequThrowable.PARAMETER_ERROR,"no change");
            if (!id.equals(user.getId())) throw new IotequException(IotequThrowable.PARAMETER_ERROR,"id");
            boolean randCode = false;
            if (!Util.isEmpty(user.getMobilePhone()) ) {
                try {
                   Util.mobileVerifyCodeCheck(user.getMobilePhone(), password);
                   randCode = true;
                } catch (IotequException ie) {
                }
            }
            if (!randCode) {
                String ep = SqlUtil.sqlQueryString(false,"select password from sys_user where id=?",id);
                if (!StringUtil.encodePassword(password).equals(ep)) {
                    int pet = SqlUtil.sqlQueryInteger(false,"select password_error_times from sys_user where id=?",id);
                    if (pet>=3) {
                        SqlUtil.sqlExecute("update sys_user set locked = 1, password_error_times = password_error_times + 1 where id =?",id);
                        logoutSession();
                        throw new IotequAuthenticationException(IotequThrowable.USER_LOCKED, null);
                    }  else  SqlUtil.sqlExecute("update sys_user set password_error_times = password_error_times + 1 where id =?",id);
                    throw new IotequAuthenticationException(IotequThrowable.NO_AUTHORITY, null);
                }
            }
            String ep=StringUtil.encodePassword(newPassword);
            int rows = SqlUtil.sqlExecute("update sys_user set password=? where id=?",ep,id);
            if (rows != 1) throw new IotequException(IotequThrowable.FAILURE,"rows="+rows);
            return j.toResponse();
        } catch (IotequException e) {
            j.setError(e);
            return j.toResponse();
        }
    }

    // 網絡註冊
    @RequestMapping(value = SpringSecurityConfig.loginPage + "/register/default", method = {RequestMethod.GET})
    public ResponseEntity<Map<String, Object>> defaultValue(Boolean needLoadDictionary, String mobilePhone, String requestDynaFields, User user, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String smsProperty = env.getProperty("sms.register.defaultSql");
        RestJson j = new RestJson();
        if (!Util.isEmpty(smsProperty)) {
            User user1 = SqlUtil.sqlQueryFirst(User.class,false,smsProperty,user.getMobilePhone());
            if (user1!=null) j.data(user1);
        }
        return j.dictionary(userService.getDictionary(user,true, requestDynaFields))
                .toResponse();
    }
    @RequestMapping(value = SpringSecurityConfig.loginPage + "/register/record", method = {RequestMethod.GET})
    public ResponseEntity<Map<String, Object>> registerRrcord(Boolean needLoadDictionary, String mobilePhone, String requestDynaFields, User user, HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            return new RestJson().dictionary(userService.getDictionary(user,true, requestDynaFields)).toResponse();
        } catch (Exception e) {
            return new RestJson().setError(e).toResponse();
        }
    }
    @RequestMapping(value = SpringSecurityConfig.loginPage + "/register/save", method = {RequestMethod.PUT, RequestMethod.POST})
    public ResponseEntity<Map<String, Object>> save(Integer total_filepart, String randCode, User user,HttpServletRequest request, HttpServletResponse response) {
        try {
            return doSave(total_filepart, user,randCode,request, response);
        } catch (Exception e) {
            return new RestJson().setMessage(e).toResponse();
        }
    }

    @Transactional
    public ResponseEntity<Map<String, Object>> doSave(Integer total_filepart, User user,String randCode, HttpServletRequest request, HttpServletResponse response) throws Exception {
        RestJson j = new RestJson();
        if (user == null) {
            throw new IotequException(IotequThrowable.NULL_OBJECT, "user");
        }
        Util.mobileVerifyCodeCheck(user.getMobilePhone(), randCode);
        if (Util.isEmpty(user.getMobilePhone())) user.setMobilePhone(null);
        if (Util.isEmpty(user.getEmail())) user.setEmail(null);
        if (Util.isEmpty(user.getWechatOpenid())) user.setWechatOpenid(null);
        if (Util.isEmpty(user.getEmployeeNo())) user.setEmployeeNo(null);

        if ("".equals(user.getMobilePhone())) user.setMobilePhone(null);
        if ("".equals(user.getEmail())) user.setEmail(null);
        if ("".equals(user.getWechatOpenid())) user.setWechatOpenid(null);
        if ("".equals(user.getEmployeeNo())) user.setEmployeeNo(null);

        user.setOrgPrivilege(null);
        user.setLocked(false);
        user.setState(true);
        user.setIcon(null);

        if (user.getOrgCode() == null) user.setOrgCode(0);
        Integer role = SqlUtil.sqlQueryInteger(false, "select id from sys_role where code=?", "register");
        user.setRoleList(role == null ? null : role.toString());
        userService.setPrimaryKey(user);
        user.setPassword(StringUtil.encodePassword(user.getPassword()));
        user.setRegTime(new Date());
        int rows = userDao.insert(user);
        if (rows <= 0) throw new IotequException(IotequThrowable.FAILURE, "rows <= 0");
        if (total_filepart != null && total_filepart > 0) {
            String fileName = UploadDownUtil.uploadFile("sysUser", "icon", user.getId().toString(), false, true, request);
            user.setIcon(fileName);
            userDao.update(user);
        }
        return j.toResponse();
    }


    @RequestMapping(value = SpringSecurityConfig.loginPage+"/wechat", produces = MediaType.TEXT_HTML_VALUE)
    public String login(HttpServletRequest req, HttpServletResponse response) {
        Object openId = req.getAttribute("openId");  // 微信绑定用户
        if (Util.isEmpty(openId)) {
            String title = env.getProperty("framework.login_title");
            if (Util.isEmpty(title)) title = "loginTitle";
            req.setAttribute("loginTitle", title);
            WeChat weChat = Util.getBean(WeChat.class);
            String wxurl = (WeChat.isOk() && weChat != null ? weChat.getAuthorizeUrl(req) : null);
            if (wxurl != null) req.setAttribute("wxurl", wxurl);
            else if (WeChat.isOk() && weChat != null) {
                try {
                    wxurl = weChat.getQRCodeUrl();
                    if (wxurl != null) {
                        req.setAttribute("qrurl", wxurl);
                        req.setAttribute("checkUrl", Util.realUrl(SpringSecurityConfig.resourcePage + "/checkopenid"));
                    }
                } catch (Exception e1) {
                    //e1.printStackTrace();
                }
            }
            String smsModules = env.getProperty("sms.modules");
            if (smsModules != null && smsModules.toLowerCase().contains("login")) {
                List<ISmsService> smsServiceList = Util.getImplementedBean(ISmsService.class);
                if (smsServiceList != null) {
                    for (ISmsService sms : smsServiceList) {
                        if (sms.enabled()) {  // 打开短信登录
                            req.setAttribute("useMobile", true);
                            break;
                        }
                    }
                }
            }
        } else {
            req.setAttribute("loginTitle", "wechat");
        }
        String pageTitle = env.getProperty("framework.title");
        if (Util.isEmpty(pageTitle)) pageTitle = "iotequ framework";
        req.setAttribute("pageTitle", pageTitle);
        return "/common/login";
    }
    @ResponseBody
    @RequestMapping(value = SpringSecurityConfig.loginPage + "/wechatQrurl")
    public ResponseEntity<Map<String, Object>> qrurl(HttpServletRequest req, HttpServletResponse response) {
        RestJson j = new RestJson();
        WeChat weChat = Util.getBean(WeChat.class);
        String wxurl = (WeChat.isOk() && weChat != null ? weChat.getAuthorizeUrl(req) : null);
        String checkUrl = null;
        if (wxurl != null) j.put("qrurl", wxurl);
        else if (WeChat.isOk() && weChat != null) {
            try {
                wxurl = weChat.getQRCodeUrl();
                if (wxurl != null) {
                    j.put("qrurl", wxurl);
                    checkUrl = Util.realUrl(SpringSecurityConfig.resourcePage + "/checkopenid");
                    j.put("checkUrl", checkUrl);
                }
            } catch (Exception e1) {
                j.setMessage(e1);
            }
        }
        return j.toResponse();
    }

    @ResponseBody
    @RequestMapping(value = SpringSecurityConfig.loginPage, method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> loginPost(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> bodyj = new HashMap<>();
        bodyj.put("success", false);
        bodyj.put("status", 403);
        bodyj.put("error", IotequThrowable.ACCESS_DENIED);
        String url = StringUtil.toString(Util.getSessionAttribute(UrlAccessDecisionManager.FORBIDDEN_URL));
        if (Util.isEmpty(url)) url = "/login";
        else Util.removeSessionAttribute(UrlAccessDecisionManager.FORBIDDEN_URL);
        bodyj.put("url", Util.realUrl(url));
        response.setStatus(403);
        HttpStatus status = HttpStatus.valueOf(403);
        return new ResponseEntity<>(bodyj, status);
    }

    @ResponseBody
    @RequestMapping(value = SpringSecurityConfig.loginPage + "/cancelWeChat")
    public String cancelWeChat(String id, HttpServletRequest req) {
        RestJson j = new RestJson();
        try {
            SqlUtil.sqlExecute("update sys_user set wechat_openid=null where id=?", id);
        } catch (Exception e) {
            j.setMessage(e.getMessage());
        }
        return j.toString();
    }

    @RequestMapping(value = SpringSecurityConfig.loginPage + "/wechat")
    public String loginForWechat(String code, String state, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("code", code);
        request.setAttribute("state", state);
        return "/common/wechat";
    }

    private static boolean existTest(String property, String mobile) throws Exception {
        if (Util.isEmpty(property) || Util.isEmpty(mobile)) return false;
        Pattern pattern = Pattern.compile("^\\s*sql\\s*=\\s*");
        Matcher matcher = pattern.matcher(property);
        if (matcher.find()) {
            String sql = property.substring(matcher.end());
            return SqlUtil.sqlExist(false,sql,mobile);
        } else {
            pattern = Pattern.compile(property);
            matcher = pattern.matcher(mobile);
            return matcher.find();
        }
    }

    // 进度条获取
    @ResponseBody
    @RequestMapping(value = "/res/progress")
    public String getProgress(Boolean reset, HttpServletRequest req) {
        RestJson j = new RestJson();
        Integer p = 0;
        if (Objects.nonNull(reset) && reset) {
            Util.setProgress(0);
            p = 0;
        } else p = Util.getProgress();
        j.put("progress", p);
        return j.toString();
    }

    public Class getEntityClass() {
        return User.class;
    }
}
