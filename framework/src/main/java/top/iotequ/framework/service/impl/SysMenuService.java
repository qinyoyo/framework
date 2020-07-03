package top.iotequ.framework.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.iotequ.framework.dao.MenuDao;
import top.iotequ.framework.exception.IotequException;
import top.iotequ.framework.pojo.Menu;
import top.iotequ.framework.util.SqlUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class SysMenuService extends CgSysMenuService {
	@Autowired
	private MenuDao menuDao;
	/*
	@Override
	public boolean dragSort(Class<?> clazz, String sortedIds, String sort, String order, HttpServletRequest request) {
		String [] ids=sortedIds.split(",");
		String o = "asc";
		if (sort!=null) {
			String [] nn=sort.split(",");
			for (int i=0;i<nn.length;i++) {
				if (nn[i].equals("sortNum") && order!=null) {
					String [] oo = order.split(",");
					if (i<oo.length) {
						o=oo[i].toLowerCase().trim();
					}
					break;	
				}
			}
		}
		try {
			int orderNum=10;
			if (o.equals("desc")) {
				for (int i =  ids.length - 1  ;  i>=0 ; i-- ) {
					SqlUtil.sqlExecute("update sys_menu set sort_num=? where id=?", orderNum,ids[i]);
					orderNum += 10;
				}			
			} else {
				for (int i = 0 ; i<ids.length ; i ++ ) {
					SqlUtil.sqlExecute("update sys_menu set sort_num=? where id=?", orderNum,ids[i]);
					orderNum += 10;
				}			
			}
		} catch (Exception e) { return false;}
		return true;
	}

	 */
	private void changeId(List<Menu> list , int id0, int id1, int maxId) {
		if (id0==id1 || list==null || list.isEmpty()) return;
		for (Menu menu : list) {
			if (menu.getId() == id1)  menu.setId(maxId);
			if (menu.getParent()!=null && menu.getParent() == id1) menu.setParent(maxId);
		}
		for (Menu menu : list) {
			if (menu.getId() == id0)  menu.setId(id1);
			if (menu.getParent()!=null && menu.getParent() == id0) menu.setParent(id1);
		}
	}
	/*
	@Override
	public void beforeImport(List<Menu> list,HttpServletRequest request) throws IotequException {
		Integer maxIdObject = SqlUtil.sqlQueryInteger(false,"select max(id) from sys_menu");
		int maxId =  (maxIdObject==null ? 1 : maxIdObject + 1);
		for (Menu menu : list) {
			if (maxId <= menu.getId()) maxId=menu.getId()+1;
		}
		for (Menu menu : list) {
			Menu m = menuDao.selectByNameParentAction(menu.getName(), menu.getParent(),menu.getAction());
			if (m!=null) {
				changeId(list,menu.getId(),m.getId(),maxId);
				maxId++;
			} else {
				changeId(list,menu.getId(),maxId,maxId+1);
				maxId++;
			}
		}
	}
	*/
}
