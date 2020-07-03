package top.iotequ.framework.event;

import java.util.Map;

import org.springframework.context.ApplicationEvent;

public class DeviceEvent extends ApplicationEvent  {
	private static final long serialVersionUID = 1L;
	Map<String,Object> data;
	String deviceType;
	public DeviceEvent(Object source,String deviceType,Map<String,Object> map) {
		super(source);
		setDeviceType(deviceType);
		setData(map);
	}
	public Map<String, Object> getData() {
		return data;
	}
	public void setData(Map<String, Object> data) {
		this.data = data;
	}

	public String getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}
}
