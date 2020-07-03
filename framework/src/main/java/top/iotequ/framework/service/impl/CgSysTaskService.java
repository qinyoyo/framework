package top.iotequ.framework.service.impl;
import top.iotequ.framework.pojo.Task;
import top.iotequ.framework.dao.TaskDao;
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
@ConditionalOnMissingClass({"top.iotequ.framework.service.impl.SysTaskService"})
@Service(value="sysTaskService")
public class CgSysTaskService extends CgService<Task>  {
private static final Logger log = LoggerFactory.getLogger(CgSysTaskService.class);
    @Autowired
    private TaskDao taskDao;
    @Override
    public Class<Task> getEntityClass() {
        return Task.class;
    }
    @Override
    public IDaoService<Task> getDaoService() {
        return taskDao ;
    }
    @Override
    public IFlowService<Task> getFlowService() {
        return null;
    }
    @Override
    public Map<String,Object> getDictionary(Task obj, Boolean useTree, String dynaFields) {
        Map<String,Object> map=new HashMap<>();
        return map;
    }
    @Override
    public Task getDefaultObject(Task task) throws IotequException {
        checkAvailable();
        if (task==null)  task=new Task();
        else task.setId(null);
        return task;
    }
    @Override
    public void changeEmpty2Null(Task task) {
        if (Objects.nonNull(task)) {
        }
    }
    @Override
    public void changeNull2Default(Task task) {
        if (task.getId()==null) {
            task.setId(0);
        }
        if (task.getName()==null) {
            task.setName("");
        }
        if (task.getSceduleYears()==null) {
            task.setSceduleYears("*");
        }
        if (task.getScheduleMonths()==null) {
            task.setScheduleMonths("*");
        }
        if (task.getScheduleDays()==null) {
            task.setScheduleDays("*");
        }
        if (task.getScheduleWeeks()==null) {
            task.setScheduleWeeks("*");
        }
        if (task.getScheduleHours()==null) {
            task.setScheduleHours("*");
        }
        if (task.getScheduleMinutes()==null) {
            task.setScheduleMinutes("*");
        }
        if (task.getClassName()==null) {
            task.setClassName("");
        }
        if (task.getMothodName()==null) {
            task.setMothodName("");
        }
        if (task.getIsStatic()==null) {
            task.setIsStatic(Util.boolValue("1"));
        }
        if (task.getIsRunning()==null) {
            task.setIsRunning(Util.boolValue("1"));
        }
    }
}
