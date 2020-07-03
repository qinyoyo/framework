package top.iotequ.framework.service.impl;

import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.iotequ.framework.dao.SysRouteDao;
import top.iotequ.framework.exception.IotequException;
import top.iotequ.framework.pojo.SysRoute;
import top.iotequ.framework.util.HttpUtils;
import top.iotequ.framework.util.RestJson;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Objects;

@Service
public class SysRouteService extends CgSysRouteService {
	@Autowired
	private SysRouteDao sysRouteDao;
	@Override
	public RestJson doAction(String action, String id, HttpServletRequest request) throws IotequException {
		List<SysRoute> routes = HttpUtils.getRequestBody(new TypeToken<List<SysRoute>>() {}.getType(), request);
		if ("route".equals(action)) {
			if (Objects.nonNull(routes) && !routes.isEmpty()) {
				for (SysRoute r : routes) {
					SysRoute route = sysRouteDao.selectByName(r.getName());
					if (Objects.isNull(route)) {
						r.setId(null);
						sysRouteDao.insert(r);
					}
				}
			}
		}
		return new RestJson();
	}
}
