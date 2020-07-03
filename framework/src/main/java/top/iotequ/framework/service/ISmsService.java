package top.iotequ.framework.service;

import java.util.Map;

import top.iotequ.framework.exception.IotequException;
import top.iotequ.framework.pojo.Message;

public interface ISmsService {
	/**
	 * 服务是否可用
	 * @return 服务是否可用
	 */
	public boolean enabled();
	/**
	 * 发送短消息
	 * @param mobile	手机号码
	 * @param templateName 模板名
	 * @param params 参数化的消息内容
	 * @throws IotequException 出错返回异常
	 */
	public void sendTemplateSms(String mobile,String templateName,Map<String,Object> params) throws IotequException;
	/**
	 * 发送验证码
	 * @param mobile 手机号码
	 * @param vc 验证码
	 * @throws IotequException 出错返回异常
	 */
	public void sendVerifyCodeSms(String mobile,String vc) throws IotequException;
}
