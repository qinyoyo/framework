package top.iotequ.framework.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import top.iotequ.framework.dao.MenuDao;
import top.iotequ.framework.dao.MessageDao;
import top.iotequ.framework.dao.SysRouteDao;
import top.iotequ.framework.dao.UserDao;
import top.iotequ.framework.exception.IotequThrowable;
import top.iotequ.framework.pojo.*;
import top.iotequ.framework.security.SpringSecurityConfig;
import top.iotequ.framework.security.service.SecurityService;
import top.iotequ.framework.bean.SpringContext;
import top.iotequ.framework.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class HomeController implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(HomeController.class);
    @Autowired
    MessageDao msgDao;
    @Autowired
    SysRouteDao sysRouteDao;
    @Autowired
    MenuDao menuDao;
    @Autowired
    UserDao userDao;
    @Autowired
    SecurityService securityService;
    List<String> activeApis;
    @Autowired
    private Environment env;

    @RequestMapping(value = "/m/{id}")
    public ResponseEntity<Map<String, Object>> m(@PathVariable String id, HttpServletRequest request, HttpServletResponse response) {
        RestJson j = new RestJson();
        try {
            String url = SqlUtil.sqlQueryString(false, "select url from sys_message where id=?", Integer.parseInt(id));
            if (!Util.isEmpty(url)) {
                j.put("url", url);
            } else j.setErrorCode(IotequThrowable.NULL_OBJECT, id);
        } catch (Exception e) {
            j.setError(e);
        }
        return j.toResponse();
    }

    @RequestMapping(value = "/")
    public String testRunning(HttpServletRequest request, HttpServletResponse response) {
        StringBuilder sb = new StringBuilder();
        sb.append("<h1>I am running...</h1>");
        sb.append("<div>" + "Home direction = " + SpringContext.getProjectHomeDirection() + "</div>");
        try {
            String sc = MachineInfo.getSetupCode();
            sb.append("<div>" + "Setup code = " + sc + "</div>");
        } catch (Exception e) {
        }
        List<IotequVersionInfo> versions = IotequVersionInfo.getAllVersions();
        if (!Util.isEmpty(versions)) {
            sb.append("<div>Modules：</div>");
            for (IotequVersionInfo v : versions) {
                sb.append("<div>&nbsp;&nbsp;&nbsp;&nbsp;" + v.toString() + "</div>");
            }
        }
        List<Action> actions = SecurityService.getActiveAction();
        if (!Util.isEmpty(actions)) {
            sb.append("<div>Actions：</div>");
            for (Action a : actions) {
                sb.append("<div>&nbsp;&nbsp;&nbsp;&nbsp;" + a.getValue() + (Util.isEmpty(a.getMethod()) ? "" : "&nbsp;&nbsp;("+a.getMethod()+")") + "</div>");
            }
        }
        return sb.toString();
    }

    private void changeMenuParams(List<Menu> menus) {
        if (menus == null || menus.isEmpty()) return;
        for (int i = 0; i < menus.size(); i++) {
            Menu m = menus.get(i);
            //m.setName(messageService.getLang(m.getName())); 前端渲染
            //m.setMobilename(messageService.getLang(m.getMobilename()));
            String url = m.getAction();
            if (!Util.isEmpty(url) && url.equals("/")) {
                m.setAction(null);
            }
            List<Menu> children = m.getChildren();
            if (children != null && !children.isEmpty()) changeMenuParams(children);
            children = m.getChildren();
            if ((children == null || children.isEmpty()) && Util.isEmpty(m.getAction())) {
                menus.remove(m);
                i--;
            }

        }
    }

    private void menuSort(List<Menu> menus) {
        Collections.sort(menus, new Comparator<Menu>() {
            @Override
            public int compare(Menu o1, Menu o2) {
                return o1.getSortNum() - o2.getSortNum();
            }
        });
        for (Menu m : menus) {
            List<Menu> sons = m.getChildren();
            if (sons != null && sons.size() > 0) menuSort(sons);
        }
    }

    @ResponseBody
    @RequestMapping(value = "/res/language")
    public ResponseEntity<Map<String, Object>> language(HttpServletRequest request, HttpServletResponse response, String locale) {
        if (!Util.isEmpty(locale)) Util.setLanguage(locale);
        return new RestJson().toResponse();
    }

    private Map<String, Object> homeParameters() {
        Map<String, Object> map = new HashMap<>();
        List<IotequVersionInfo> versions = IotequVersionInfo.getAllVersions();
        if (!Util.isEmpty(versions)) {
            List<String> vslist = versions.stream().map((a) -> a.toString()).collect(Collectors.toList());
            map.put("versionInfo", vslist);
        }
        List<Message> msgList = null;
        User user = Util.getUser();
        if (user == null) {
            msgList = msgDao.listBy("read_time is null and receiver is null", "create_time desc");
            user = userDao.selectByName("guest");
        } else {
            user = userDao.select(user.getId());
            msgList = msgDao.listBy("read_time is null and (receiver is null or receiver = '" + user.getId() + "')", "create_time desc");
        }
        if (user != null) {
            List<Menu> menus = menuDao.selectTree(null);
            if ("admin".equals(user.getName()) && menus != null) {
                Menu actuator = new Menu();
                actuator.setAction("/admin");
                actuator.setName("Actuator");
                actuator.setSortNum(10000000);
                if (menus.size() > 0) {
                    Menu fm = menus.get(0);
                    if (fm.getChildren() != null) {
                        actuator.setParent(fm.getId());
                        fm.getChildren().add(actuator);
                    }
                } else menus.add(actuator);
            }
            List<Role> roles = securityService.getRoles(user);
            if ("admin".equals(user.getName())) {
                map.put("apis", activeApis);
                map.put("roles", new String[]{"admin"});
            } else if (roles == null || roles.isEmpty()) {
                menus = null;
            } else {
                List<String> apis = new ArrayList<>();
                String[] rolesArray = new String[roles.size()];
                Integer[] rolesId = new Integer[roles.size()];
                for (int i = 0; i < roles.size(); i++) {
                    rolesArray[i] = roles.get(i).getCode();
                    rolesId[i] = roles.get(i).getId();
                }
                try {
                    List<Map<String, Object>> auths = SqlUtil.sqlQuery("select distinct a.value as url from sys_permission p, sys_action a where p.action = a.id and p.role = ?", rolesId);
                    if (Objects.nonNull(auths) && auths.size() > 0) {
                        for (Map<String, Object> auth : auths) {
                            apis.add(StringUtil.toString(auth.get("url")));
                        }
                    }
                } catch (Exception e) {
                }
                map.put("roles", rolesArray);
                map.put("apis", apis.stream().filter(i -> activeApis.stream().anyMatch(a -> a.equals(i))).collect(Collectors.toList()));
            }

            changeMenuParams(menus);
            if (menus != null && !menus.isEmpty()) {
                menuSort(menus);
                map.put("menus", menus);
            }
            map.put("user", user);
        } else {
            log.error("Guest not found!");
        }
        if (msgList != null && msgList.size() > 0) {
            map.put("messages", msgList.size());
            for (Message m : msgList) {
                String url = m.getUrl();
                if (url != null) {
                    url = url.trim();
                    if (url.equals("/") || url.isEmpty()) url = null;
                    m.setUrl(url);
                }
            }
            map.put("msgList", msgList);
        } else {
            map.put("messages", 0);
        }
        return map;
    }

    @ResponseBody
    @RequestMapping(value = SpringSecurityConfig.loginPage + "/info", method = GET)
    public ResponseEntity<Map<String, Object>> loginInfo(HttpServletRequest request) {
        Util.setLanguage(request.getParameter("locale"));
        RestJson j = new RestJson();
        if (Util.isAuthenticated()) {
            Map<String, Object> map = homeParameters();
            j.data(map);
        } else {
            j.setError(IotequThrowable.NO_AUTHORITY, "", null);
        }
        return j.toResponse();
    }

    @ResponseBody
    @RequestMapping(value = "/sysRoute/routes", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> saveRoutes(@RequestBody List<SysRoute> list) {
        if (!Util.isEmpty(list)) {
            for (SysRoute route : list) {
                sysRouteDao.deleteByPath(route.getPath());
                sysRouteDao.insert(route);
            }
        }
        return new RestJson().toResponse();
    }

    public Class getEntityClass() {
        return Menu.class;
    }

    @Override
    public void run(String... args) throws Exception {
        List<Action> activeActions = SecurityService.getActiveAction();
        activeApis = activeActions.stream().map(i -> i.getValue()).collect(Collectors.toList());
    }
}