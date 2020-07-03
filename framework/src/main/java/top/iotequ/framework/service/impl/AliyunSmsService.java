package top.iotequ.framework.service.impl;

import top.iotequ.framework.exception.IotequException;
import top.iotequ.framework.exception.IotequThrowable;
import top.iotequ.framework.service.ISmsService;
import top.iotequ.framework.util.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;

import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.google.gson.Gson;

@Service
@ConditionalOnProperty(value = "aliyunsms.accessKeyId", matchIfMissing = false)
public class AliyunSmsService implements ISmsService,ApplicationContextAware {
	static List<CommonRequest> cachedMessage=new ArrayList<>();
	static final int maxStringLength = 20;
	static final int startSendHour = 9;
	static final int endSendHour = 20;
	static public class ResponseParameters {
		String Recommend;
		String Message;
		String RequestId;
		String HostId;
		String Code;
	}
	String accessKeyId;
	String secret;
	String signName;
	String templateCode;
	String msgTemplateCode;
	@Autowired  
    private Environment env; 
	public void getShortUrl(String url) {
	    String host = "https://short.market.alicloudapi.com/short";
	    String appcode = "你自己的AppCode";
	    Map<String, Object> headers = new HashMap<String, Object>();
	    //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
	    headers.put("Authorization", "APPCODE " + appcode);
	    //根据API的要求，定义相对应的Content-Type
	    headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
	    Map<String, Object> querys = new HashMap<String, Object>();
	    Map<String, Object> bodys = new HashMap<String, Object>();
	    bodys.put("src", "https://www.uulucky.com/");
	    try {
	    	HttpResponse response = HttpUtils.doPost(host, headers, querys, bodys);
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	}
	void setSmsTemplate(Map<String,Object>source,Map<String,Object>smsParams) {
		for (String key : source.keySet()) {
			String s=StringUtil.toString(source.get(key));
			if (s==null)  smsParams.put(key, "");
			else {
				if (s.length()<=maxStringLength) smsParams.put(key, s);
				else smsParams.put(key, s.substring(0,maxStringLength));
			}
		}
	}
	@Override
	public void sendTemplateSms(String mobile,String templateName,Map<String,Object> map) throws IotequException {
		if (!Util.isEmpty(msgTemplateCode)) {
			DefaultProfile profile = DefaultProfile.getProfile("default", accessKeyId,secret);
			IAcsClient client = new DefaultAcsClient(profile);
			CommonRequest request = new CommonRequest();
			request.setMethod(MethodType.POST);
			request.setDomain("dysmsapi.aliyuncs.com");
			request.setVersion("2017-05-25");
			request.setAction("SendSms");
			request.putQueryParameter("PhoneNumbers", mobile);
			request.putQueryParameter("SignName", signName);
			request.putQueryParameter("TemplateCode", Util.isEmpty(templateName,msgTemplateCode));

			Map<String,Object> smsMap=new HashMap<>();
			if (map!=null) setSmsTemplate(map,smsMap);
			String params = Util.getGson().toJson(smsMap);
			request.putQueryParameter("TemplateParam", params);
			if (Util.runInIdeMode) {
				System.out.println("Send message to "+mobile);
				String temp = "尊敬的%s,您有关于%s的%s事件(发生时间%s),需要及时%s,请点击http://www.svein.com.cn/%s进行操作";
				System.out.println(String.format(temp,smsMap.get("name"),smsMap.get("eventname"),smsMap.get("eventtype"),smsMap.get("eventtime"),smsMap.get("operation"),smsMap.get("messageid")));
				return;
			}
			int hour=Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
			if (hour>endSendHour || hour<startSendHour) {
				cachedMessage.add(request);
			} else {
				try {
					CommonResponse response = client.getCommonResponse(request);
					ResponseParameters r = new Gson().fromJson(response.getData(), ResponseParameters.class);
					if (!"ok".equals(r.Code.toLowerCase())) {
						throw new IotequException(IotequThrowable.ALI_SMS_ERROR, r.Message);
					}
				}catch (Exception e) {
					throw IotequException.newInstance(e);
				}
			}
		}
	}
	@Override
	public void sendVerifyCodeSms(String mobile, String vc) throws IotequException {
		DefaultProfile profile = DefaultProfile.getProfile("default", accessKeyId,secret);
		IAcsClient client = new DefaultAcsClient(profile);
		CommonRequest request = new CommonRequest();
		request.setMethod(MethodType.POST);
		request.setDomain("dysmsapi.aliyuncs.com");
		request.setVersion("2017-05-25");
		request.setAction("SendSms");
		request.putQueryParameter("PhoneNumbers", mobile);
		request.putQueryParameter("SignName", signName);
		request.putQueryParameter("TemplateCode", templateCode);
		request.putQueryParameter("TemplateParam", "{ \"code\": \"" + vc + "\"}");
		if (Util.runInIdeMode) {
			System.out.println("Send message to "+mobile);
			System.out.println("     message:  "+ "您正在通过手机登录，验证码" + vc + "，5分钟内有效");
			return;
		}
		try {
			CommonResponse response = client.getCommonResponse(request);
			ResponseParameters r = new Gson().fromJson(response.getData(), ResponseParameters.class);
			if (!"ok".equals(r.Code.toLowerCase())) {
				throw new IotequException(IotequThrowable.ALI_SMS_ERROR, r.Message);
			}
		}catch (Exception e) {
			throw IotequException.newInstance(e);
		}
	}

	@Override
	public boolean enabled() {	
		return  (!Util.isEmpty(accessKeyId) && !Util.isEmpty(secret) && !Util.isEmpty(signName) && !Util.isEmpty(templateCode));
	}
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		accessKeyId=env.getProperty("aliyunsms.accessKeyId");
		secret=env.getProperty("aliyunsms.secret");
		signName=env.getProperty("aliyunsms.signName");
		templateCode=env.getProperty("aliyunsms.templateCode");
		msgTemplateCode=env.getProperty("aliyunsms.msgTemplateCode");	
        Timer t = new Timer();
     // 时间类
        Calendar startDate = Calendar.getInstance();

        //设置开始执行的时间为 某年-某月-某月 00:00:00
        startDate.set(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.get(Calendar.DATE), startSendHour, 30, 0);
        if (startDate.getTime().getTime()<new Date().getTime()) 
        	startDate.setTime(DateUtil.dateAdd(startDate.getTime(),1,DateUtil.DAY));
        // 24小时的毫秒设定
        long timeInterval = 24 * 60 * 60 * 1000;
        t.schedule(new TimerTask() {
            public void run() {
            	int size=cachedMessage.size();
            	while (size>0) {
            		CommonRequest request = cachedMessage.get(0);
            		DefaultProfile profile = DefaultProfile.getProfile("default", accessKeyId,secret);
            		IAcsClient client = new DefaultAcsClient(profile);
            		try {
						CommonResponse response = client.getCommonResponse(request);
					} catch (Exception e) {}
            		cachedMessage.remove(0);
            		size=cachedMessage.size();
            	}
            }
        }, startDate.getTime(), timeInterval ); 
	}
}
