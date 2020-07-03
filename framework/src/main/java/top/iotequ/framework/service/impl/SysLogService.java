package top.iotequ.framework.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import top.iotequ.framework.exception.IotequException;
import top.iotequ.framework.exception.IotequThrowable;
import top.iotequ.framework.pojo.SysLog;
import top.iotequ.framework.service.utils.PageUtil;
import top.iotequ.framework.util.RestJson;
import top.iotequ.framework.util.SqlUtil;
import top.iotequ.framework.util.StringUtil;
import top.iotequ.framework.util.Util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Service
public class SysLogService extends CgSysLogService {
	private static final Logger log = LoggerFactory.getLogger(SysLogService.class);
	@Override
	public RestJson doAction(String action, String id, HttpServletRequest request) throws IotequException {
		if ("clear".equals(action)) {
			SqlUtil.sqlExecute("truncate table sys_log");
			Util.writeLog(log, "clear", null, "清除所有日志");
		}
		else throw new IotequException(IotequThrowable.PARAMETER_ERROR,"Error action code");
		return new RestJson();
	}

	@Override
	public RestJson sqlQuery(Map<String, Object> params) throws Exception {
		String action = StringUtil.toString(params.get("action"));
		RestJson j=new RestJson();
		if ("loginTop10".equals(action)) {
			String sql="SELECT count(*) as times,user_info as userInfo,user_type as userType FROM sys_log where user_info is not null group by user_info,user_type order by times desc";
			PageUtil.setStartPageNumber(10,1);
			List<Map<String,Object>> list = SqlUtil.sqlQuery(false,sql);
			j.data(list);
		}
		return j;
	}
}
