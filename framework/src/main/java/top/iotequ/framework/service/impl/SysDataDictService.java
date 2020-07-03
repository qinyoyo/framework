package top.iotequ.framework.service.impl;

import org.springframework.stereotype.Service;
import top.iotequ.framework.event.SystemParameterChangedEvent;
import top.iotequ.framework.exception.IotequException;
import top.iotequ.framework.pojo.DataDict;
import top.iotequ.framework.bean.SpringContext;
import top.iotequ.framework.util.RestJson;
import top.iotequ.framework.util.SqlUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class SysDataDictService extends CgSysDataDictService {
	@Override
	public String listFilter(String path) {
		return "dict != 'controller_description'";
	};
	void loadDict() {
		SpringContext.getDictData();
		SystemParameterChangedEvent.sendDataDictEvent(null);
	}
	@Override
	public  void beforeSave(DataDict obj0, DataDict obj, boolean updateSelective, HttpServletRequest request) throws IotequException {
		if (obj!=null) {
			if ( 0==obj.getOrderNum()) {
				Integer max= SqlUtil.sqlQueryInteger(false, "select max(order_num) from sys_data_dict where dict=?",obj.getDict());
				obj.setOrderNum(max==null?1:max+1);
			}
			if (obj.getText()==null) obj.setText(obj.getCode());
			else if (obj.getCode()==null) obj.setCode(obj.getText());
		}
	}
	@Override
	public  void afterSave(DataDict obj0, DataDict obj, HttpServletRequest request, RestJson j)  throws IotequException{
		loadDict();
	}
	public  void afterDelete(Object id,HttpServletRequest request,RestJson j)  throws IotequException{
		loadDict();
	}
	@Override
	public void afterImport(List<DataDict> l, String pk, HttpServletRequest request, RestJson j)  throws IotequException{
		if(l!=null) {
			loadDict();
	    }
	}}
