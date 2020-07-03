package top.iotequ.framework.controller;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import top.iotequ.framework.exception.IotequException;
import top.iotequ.framework.exception.IotequThrowable;
import top.iotequ.framework.security.SpringSecurityConfig;
import top.iotequ.framework.util.*;

@Controller
@ConditionalOnProperty(value = "wechat.appid", matchIfMissing = false)
public class WeChat implements ApplicationContextAware {
	static String appid=null;
	static String appSecret=null;
	static String openAppid=null;
	static String openAppSecret=null;
	static String loginUrl=null;
	static String qqUrl=null;
	static String qqApiUrl=null;
	static String checkUrl=null;
	String accessToken;
	long getTokenTime;
	long tokenExpireTime;
	String jsApiTicket;
	long ticketExpireTime;
	long getTiketTime;
	Map<String,String> scanLoginSerssions;
	@Autowired
	private Environment env;
	private static final Logger log = LoggerFactory.getLogger(WeChat.class);
	static public boolean isOk() {
		return !Util.isEmpty(appid) && !Util.isEmpty(loginUrl) && !"null".equals(appid);
	};
	@ResponseBody
	@RequestMapping(value = SpringSecurityConfig.resourcePage+"/wechat",method=RequestMethod.GET)
	public String wechatServer(HttpServletRequest request,String signature,String timestamp,String nonce,String echostr) {
        String token = "svein_tech_co_ltd";
        String success="success";
        if (!Util.isEmpty(echostr)) {
        	List<String> list=new ArrayList<>();
        	list.add(token);
        	list.add(timestamp);
        	list.add(nonce);
        	list.sort(new Comparator<String>() {
				@Override
				public int compare(String o1, String o2) {
					if (o1==null) return -1;
					else if (o2==null) return 1;
					return o1.compareTo(o2);
				}      		
        	});
        	int hashCode=list.hashCode();
        	if (String.valueOf(hashCode).equals(signature)) return echostr;
        	else {
        		log.debug("hashcode={},signature={}",hashCode,signature);
        		return echostr;
        	}
        } else {
        	return success;
        }
	}
	@ResponseBody
	@RequestMapping(value = SpringSecurityConfig.resourcePage+"/wechat",method=RequestMethod.POST)
	public String wechatServer(HttpServletRequest request,@RequestBody String webData) {
		log.debug("get post message from wechat");
        String success="success";
        if (Util.isEmpty(webData)) {
        	return success;
        } else {
        	try {
            	webData=URLDecoder.decode(webData,"UTF-8");
            	log.debug(webData);
				JSONObject json = XML.toJSONObject(webData).getJSONObject("xml");
				log.debug(json.toString());
				String msgType=json.getString("MsgType").toLowerCase();  //  event
				if ("event".equals(msgType)) {
					String eventType = json.getString("Event").toLowerCase();    //  subscribe
					if ("subscribe".equals(eventType) || "scan".equals(eventType)) {
						String key=json.getString("EventKey");  
						String openId=json.getString("FromUserName");
			            if(!Util.isEmpty(key) && !Util.isEmpty(openId)){
			             		scanLoginSerssions.put(key, openId);
			            }
					}
				}
	            return success;
			} catch (Exception e) {
				log.debug(e.getMessage());
				return success;
			}
        }
	}
	@SuppressWarnings("serial")
	@ResponseBody
	@RequestMapping(value = SpringSecurityConfig.resourcePage+"/checkopenid" ) 
	public String checkOpenid(HttpServletRequest request,String key) {   
		final String fkey=Util.isEmpty(key)?Util.getSession().getId() : key; 
		RestJson j = new RestJson();
		try {
			if (Util.isEmpty(checkUrl)) {
				String openid=scanLoginSerssions.get(fkey);   // 本地调用 key = null
				if (!Util.isEmpty(openid)) {
					scanLoginSerssions.remove(key);
					j.put("openid", openid);
				} else j.setSuccess(false);
			} else {
				JSONObject result=HttpUtils.doJsonGet(checkUrl, new HashMap<String,Object>(){{put("key",fkey);}});   // 远程调用
				j.setSuccess(result.getBoolean("success"));
				if (j.isSuccess()) j.put("openid", result.getString("openid"));
			}
		} catch (Exception e) {
			log.debug(e.getMessage());
			j.setMessage(e);
		}
		return j.toString();
	}
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		appid = env.getProperty("wechat.appid");
		appSecret = env.getProperty("wechat.appSecret");
		openAppid = env.getProperty("wechat.openAppid");
		openAppSecret = env.getProperty("wechat.openAppSecret");
		loginUrl = env.getProperty("wechat.loginUrl");
		qqUrl = env.getProperty("wechat.qqOpenUrl");
		accessToken = null;
		getTokenTime = 0;
		tokenExpireTime = 0;

		jsApiTicket = null;
		ticketExpireTime = 0;
		getTiketTime = 0;
		
		checkUrl=env.getProperty("wechat.checkUrl");
		
		scanLoginSerssions=new HashMap<>();
		if (Util.isEmpty(qqUrl))
			qqUrl = "https://open.weixin.qq.com";
		qqApiUrl = env.getProperty("wechat.qqApiUrl");
		if (Util.isEmpty(qqApiUrl))
			qqApiUrl = "https://api.weixin.qq.com";
		log.debug("appid={}", appid);
		log.debug("appSecret={}", appSecret);
		log.debug("loginUrl={}", loginUrl);
		log.debug("qqUrl={}", qqUrl);
		log.debug("qqApiUrl={}", qqApiUrl);
		log.debug("checkUrl={}", checkUrl);
	}

	public static boolean isWeChat(HttpServletRequest req) {
		return req.getHeader("USER-AGENT").toLowerCase().indexOf("micromessenger") >= 0;
	}

	/**
	 * 得到微信授权登录的地址
	 * @param req HttpServletRequest
	 * @return url地址，返回空表示无此功能
	 */
	public String getAuthorizeUrl(HttpServletRequest req) {
		if (Util.isEmpty(appid) || Util.isEmpty(loginUrl)) {
			log.debug("appid==null or loginUrl==null");
			return null;
		}
		if (isWeChat(req)) {
			String tempWX = "%s/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_base&state=%s#wechat_redirect";
			try {
				return String.format(tempWX, qqUrl, appid, URLEncoder.encode(loginUrl, "utf-8"),
						Util.getSession().getId());
			} catch (Exception e) {
				log.debug(e.getMessage());
				return null;
			}
		} else if (Util.isEmpty(openAppid)) 
			return null;
		else {
			String temp = "%s/connect/qrconnect?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_login&state=%s#wechat_redirect";
			try {
				return String.format(temp, qqUrl, openAppid, URLEncoder.encode(loginUrl, "utf-8"),
						Util.getSession().getId());
			} catch (Exception e) {
				log.debug(e.getMessage());
				return null;
			}
		}
	}

	/**
	 * 根据code获得openid
	 * @param code 微信回调返回的code
	 * @return openid
	 */
	public String getOpenId(String code) {
		try {
			if (Util.isEmpty(appid) || Util.isEmpty(appSecret)) {
				log.debug("appid 或 appSecret 配置错或未配置");
				throw new IotequException(IotequThrowable.WX_CLIENT_INFO_MISS,"appid 或 appSecret 配置错或未配置");
			}
			String url = String.format(
					"%s/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code", qqApiUrl,
					appid, appSecret, code);
			JSONObject result = HttpUtils.doJsonGet(url, null);
			if (result!=null && result.has("openid")) {
				String openid=result.getString("openid");
				if (result.has("access_token")) accessToken = result.getString("access_token");
				if (result.has("expires_in"))	{
					tokenExpireTime = result.getLong("expires_in"); 
					getTokenTime = System.currentTimeMillis();
				} 
				return openid;
			} else {
				if (result!=null)  {
					log.debug(result.toString());
				}
				return null;
			}

		} catch (Exception e) {
			log.debug(e.getMessage());
			return null;
		}
	}
	/**
	 * 根据openid获得用户信息
	 * @param openid  openid
	 * @return 用户信息
	 * @throws IotequException 异常
	 */
	public JSONObject getUserInfoByOpenid(String openid) throws IotequException {
		String access_tocken = getAccessToken();
		String url = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=" + access_tocken + "&openid=" + openid;
		return HttpUtils.doJsonGet(url, null);
    }

	/**
	 * 获取access_tocken
	 * @return 获得accesstoken
	 * @throws IotequException 异常
	 */
	public String getAccessToken() throws IotequException {
		if (Util.isEmpty(appid) || Util.isEmpty(appSecret)) {
			log.debug("appid 或 appSecret 配置错或未配置");
			throw new IotequException(IotequThrowable.WX_CLIENT_INFO_MISS,"appid 或 appSecret 配置错或未配置");
		}
		String accessTokenUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";	
		long now = System.currentTimeMillis();
		if (Util.isEmpty(accessToken) || (now - getTokenTime > tokenExpireTime * 1000)) {
			accessToken=null;
			String url = accessTokenUrl.replace("APPID", appid).replace("APPSECRET", appSecret);
			JSONObject result =HttpUtils.doJsonGet(url, null);
			if (result != null && result.has("access_token")) {
				accessToken = result.getString("access_token");
				if (Util.isEmpty(accessToken)) 
					throw new IotequException(IotequThrowable.WX_API_ERROR,StringUtil.toString(result.get("errmsg")));
				if (result.has("expires_in")) {
					tokenExpireTime = result.getLong("expires_in");
					getTokenTime = System.currentTimeMillis();
				}
			}
			else {
				if (result!=null) 
					throw new IotequException(IotequThrowable.WX_TOKEN_FAILURE,result.toString());
			}
		}
		return accessToken;
	}

	/**
	 * 判断jsApiTicket是否已经存在或者是否过期
	 * @return  jsApiTicket
	 * @throws IotequException 异常
	 */
	public String getJsapiTicket() throws IotequException {
		String token = getAccessToken();
		if (token == null || token.isEmpty())
			return null;
		long now = System.currentTimeMillis();
		if (Util.isEmpty(jsApiTicket) || (now - getTiketTime > ticketExpireTime * 1000)) {
			jsApiTicket=null;
			String apiTicketUrl = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=ACCESS_TOKEN&type=jsapi";
			String url = apiTicketUrl.replace("ACCESS_TOKEN", token);
			JSONObject ticketInfo = HttpUtils.doJsonGet(url,null);
			if (ticketInfo != null && ticketInfo.has("ticket")) {
				jsApiTicket = ticketInfo.getString("ticket");
				if (ticketInfo.has("expires_in")) {
					ticketExpireTime = ticketInfo.getLong("expires_in");
					getTiketTime = System.currentTimeMillis();
				}
			}
		}
		return jsApiTicket;
	}
    static public class A {
    	public String action_name;
    	public Long expire_seconds;
    	public Map<String,Map<String, String>> action_info;  	
    } ;
    // 获得公众号二维码图片地址
	public String getQRCodeUrl() throws IotequException {
        String access_token = getAccessToken();
        if (Util.isEmpty(access_token)) {
        	log.debug("access_token==null");
        	return null;
        }
        String url = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=" + access_token;
        String body="{\"action_name\":\"ACTION_NAME\",\"expire_seconds\":EXPIRE_SECONDS,\"action_info\":{\"scene\":{\"scene_str\":\"SCENE_STR\"}}}"
        		            .replace("ACTION_NAME", "QR_STR_SCENE")
        		            .replace("EXPIRE_SECONDS","600")
        		            .replaceAll("SCENE_STR", Util.getSession().getId());
        JSONObject resultMap = HttpUtils.doJsonPost(url, null, body);
        if (resultMap != null) {
            log.debug(resultMap.toString());
        	String qrTicket=resultMap.has("ticket")?resultMap.getString("ticket"):null;
        	if (Util.isEmpty(qrTicket)) {
        		int errcode=resultMap.has("errcode")?resultMap.getInt("errcode"):0;
        		if (errcode==40001) {  // 重试一次
        			log.debug("retry ...");
        			accessToken=null;
        			access_token = getAccessToken();
        			url = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=" + access_token;
        			resultMap = HttpUtils.doJsonPost(url, null, body);
        			log.debug(resultMap.toString());
        			qrTicket=resultMap.has("ticket")?resultMap.getString("ticket"):null;
        			if (!Util.isEmpty(qrTicket)) {
        				try {
							return "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=" + URLEncoder.encode(qrTicket, "utf-8");
						}catch (Exception e) {
							throw IotequException.newInstance(e);
						}
        			}
        		}
        		return null;
        	} else {
        		try {
					return "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=" + URLEncoder.encode(qrTicket, "utf-8");
				}catch (Exception e) {
					throw IotequException.newInstance(e);
				}
        	}
        }
        log.debug("resultMap==null");
        return null;
    }
}
