package top.iotequ.framework.service.impl;

import org.springframework.stereotype.Service;
import top.iotequ.framework.exception.IotequException;
import top.iotequ.framework.exception.IotequThrowable;
import top.iotequ.framework.pojo.Task;
import top.iotequ.framework.config.ScheduleTask;
import top.iotequ.framework.util.RestJson;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class SysTaskService extends CgSysTaskService {
	@Override
	public  void afterSave(Task o0, Task o, HttpServletRequest request, RestJson j) {
		ScheduleTask.readTaskList();
	}

	@Override
	public RestJson doAction(String action, String id, HttpServletRequest request) throws IotequException {
		RestJson j=new RestJson();
		if ("run".equals(action)) {
			int iid=Integer.parseInt(id);
			if (!ScheduleTask.runTaskWithId(iid)) {
				j.setSuccess(false);
				j.setErrorCode(IotequThrowable.FAILURE,"cannot run");
			}
		}
		return j;
	}
}
