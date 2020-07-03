package top.iotequ.framework.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.iotequ.framework.dao.MessageDao;
import top.iotequ.framework.exception.IotequException;
import top.iotequ.framework.pojo.Message;
import top.iotequ.framework.pojo.User;
import top.iotequ.framework.util.RestJson;
import top.iotequ.framework.util.SqlUtil;
import top.iotequ.framework.util.Util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Service
public class SysMessageService extends CgSysMessageService {
	@Autowired
	private MessageDao messageDao;
	@Override
	public String listFilter(String path) {
		String whereString="receiver is null";    // 只能看自己的消息
		User user= Util.getUser();
		if (user!=null && !user.getName().equals("guest")) {
			whereString="(receiver is null or receiver = '"+user.getId()+"')";
		}
		return whereString;
	}

	@Override
	public  void beforeUpdate(Message msg, HttpServletRequest request) throws IotequException {
		if (msg!=null && msg.getReadTime()==null) {
			msg.setReadTime(new Date());
			messageDao.update(msg);
		}
	}
	
	@Override
	public RestJson doAction(String action, String id, HttpServletRequest request)
			throws IotequException {
		RestJson j=new RestJson();
		if ("readAll".equals(action)) {
			User user=Util.getUser();
			if (user!=null && !user.getName().equals("guest")) {
				String sql="update sys_message set read_time=? where receiver=? and read_time is null";
				SqlUtil.sqlExecute(sql, new Date(),user.getId());
			}		
		} else if ("read".equals(action)) {
			if (!Util.isEmpty(id)) {
				Message msg = messageDao.select(Integer.parseInt(id));
				if (msg != null && msg.getReadTime() == null) {
					msg.setReadTime(new Date());
					messageDao.update(msg);
				}
			}
		}
		return j;
	}

}
