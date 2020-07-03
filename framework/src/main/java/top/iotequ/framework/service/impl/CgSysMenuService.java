package top.iotequ.framework.service.impl;
import top.iotequ.framework.pojo.Menu;
import top.iotequ.framework.dao.MenuDao;
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
@ConditionalOnMissingClass({"top.iotequ.framework.service.impl.SysMenuService"})
@Service(value="sysMenuService")
public class CgSysMenuService extends CgService<Menu>  {
private static final Logger log = LoggerFactory.getLogger(CgSysMenuService.class);
    @Autowired
    private MenuDao menuDao;
    @Override
    public Class<Menu> getEntityClass() {
        return Menu.class;
    }
    @Override
    public IDaoService<Menu> getDaoService() {
        return menuDao ;
    }
    @Override
    public IFlowService<Menu> getFlowService() {
        return null;
    }
    @Override
    public Map<String,Object> getDictionary(Menu obj, Boolean useTree, String dynaFields) {
        Map<String,Object> map=new HashMap<>();
        if (Objects.isNull(dynaFields) || StringUtil.containsItem(dynaFields,"parent")) {
            if (useTree) map.put("dictParent", DictionaryUtil.getTreeViewData(obj,"sys_menu","id","name","parent","id",null,null,null));
            else map.put("dictParent", DictionaryUtil.getDictListFromDatabase(obj,"sys_menu","id","name",null,false,null));
        }
        //dictAction: defined in vue file
        return map;
    }
    @Override
    public Menu getDefaultObject(Menu menu) throws IotequException {
        checkAvailable();
        if (menu==null)  menu=new Menu();
        else menu.setId(null);
        return menu;
    }
    @Override
    public void changeEmpty2Null(Menu menu) {
        if (Objects.nonNull(menu)) {
            if ("".equals(menu.getAction())) menu.setAction(null);
        }
    }
    @Override
    public void changeNull2Default(Menu menu) {
        if (menu.getId()==null) {
            menu.setId(0);
        }
        if (menu.getSortNum()==null) {
            menu.setSortNum(10);
        }
        if (menu.getName()==null) {
            menu.setName("");
        }
        if (menu.getIsDivider()==null) {
            menu.setIsDivider(Util.boolValue("0"));
        }
        if (menu.getMobileHidden()==null) {
            menu.setMobileHidden(Util.boolValue("0"));
        }
    }
}
