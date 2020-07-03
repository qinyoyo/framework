package top.iotequ.framework.config;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import top.iotequ.framework.pojo.Task;
import top.iotequ.framework.util.*;

@Configuration
@EnableScheduling
public class ScheduleTask {
	private static final Logger log = LoggerFactory.getLogger(ScheduleTask.class);
    static List<Task> scheduleTasks = null;
    static public boolean runTaskWithId(int id) {
    	List<Task> l=null;
		try {
			l = SqlUtil.sqlQuery(Task.class, "select * from sys_task where id=?", id);
		} catch (Exception e) {}
    	if (l!=null && l.size()==1) {
			new TaskThread(l.get(0)).start(); 
			log.info(DateUtil.date2String(new Date(), null)+" run task ["+l.get(0).getName()+"]");
			return true;
    	} else return false;
    }
    static public void readTaskList() {
    	try {
			scheduleTasks = SqlUtil.sqlQuery(Task.class, "select * from sys_task where is_running=1 order by id");
		} catch (Exception e) {
			scheduleTasks=null;
		}
    }
	@Scheduled(cron = "0 0/10 * * * ?") // 每10分钟执行一次
    public void checkTask() {
        if (scheduleTasks==null) readTaskList();
        if (scheduleTasks==null || scheduleTasks.size()==0 ) return; 
		Calendar ca=Calendar.getInstance();
		int y=ca.get(Calendar.YEAR), m=ca.get(Calendar.MONTH),d=ca.get(Calendar.DAY_OF_MONTH),
			 h=ca.get(Calendar.HOUR_OF_DAY),mi=ca.get(Calendar.MINUTE),w=ca.get(Calendar.DAY_OF_WEEK)-1;
		mi = (mi/10) * 10;
		for (Task t:scheduleTasks) {
			if (   (t.getSceduleYears().contains("*") || t.getSceduleYears().contains(String.valueOf(y) ) )  &&
					(t.getScheduleMonths().contains("*") || t.getScheduleMonths().contains(String.valueOf(m) ) )  &&
					(t.getScheduleDays().contains("*") || t.getScheduleDays().contains(String.valueOf(d) ) )  &&
					(t.getScheduleHours().contains("*") || t.getScheduleHours().contains(String.valueOf(h) ) )  &&
					(t.getScheduleMinutes().contains("*") || t.getScheduleMinutes().contains(String.valueOf(mi) ) )  &&
					(t.getScheduleWeeks().contains("*") || t.getScheduleWeeks().contains(String.valueOf(w) ) )  	) {
				new TaskThread(t).start(); 
				log.info(DateUtil.date2String(new Date(), null)+" run task ["+t.getName()+"]");
			}
		}
    }
	public static  class TaskThread extends Thread 
	{ 
		private Task task; 
		public TaskThread(Task task) 
		{ 
			this.task = task; 
		} 
		public void run() 
		{ 
			try {
				if (task!=null) {
					Object [] pp =( Util.isEmpty(task.getParames()) ? null : task.getParames().split(","));
					if (task.getClassName().equalsIgnoreCase("sql")) {
						if (pp==null)
							SqlUtil.sqlExecute(task.getMothodName());
						else
							SqlUtil.sqlExecute(task.getMothodName(),pp);
					} else {
						Object obj = null;
						if (!task.getIsStatic()) {
							obj = Util.getBean(task.getClassName());
							if (obj == null) {
								log.error(DateUtil.date2String(new Date(), null)
										+" task ["+task.getName()+"] exception : bean "+task.getClassName() + " not exist");
								return;
							}
						}
						if (pp==null)
								EntityUtil.runMethod(obj, task.getMothodName());
							else
								EntityUtil.runMethod(obj, task.getMothodName(),pp);
					}
				}
			} catch (Exception e) {
				log.info(DateUtil.date2String(new Date(), null)+" task ["+task.getName()+"] exception :"+e.getMessage());
			}
		} 
	} 
}
