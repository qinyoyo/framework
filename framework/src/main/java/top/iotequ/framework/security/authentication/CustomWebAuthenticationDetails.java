package top.iotequ.framework.security.authentication;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import top.iotequ.framework.util.Util;
import top.iotequ.framework.controller.WeChat;


public class CustomWebAuthenticationDetails extends WebAuthenticationDetails {
	private static final Logger log = LoggerFactory.getLogger(CustomWebAuthenticationDetails.class);
	public static final int login_by_name = 0;
	public static final int login_by_mobile = 1;
	public static final int login_by_wechat = 2;
	public static final int login_by_oauth_password = 3;
	private static final long serialVersionUID = 0L;
	private final String userName;
	private final String password;
	private final String randCode;
	private final String openId;
	private final String rememberme;
	private final int  loginType;
	private final String lang;

	public Map<String,Object> getOpenIdInfo() {
		//https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s
		return null;
	}

	public CustomWebAuthenticationDetails(HttpServletRequest request) {
        super(request);
		lang = request.getParameter("locale");
		Util.setLanguage(request.getParameter("locale"));
        String state=request.getParameter("state");
        String mpOpenid=request.getParameter("mpOpenId");
        boolean isSmsRandCode = Util.boolValue(request.getParameter("isSmsRandCode"));
        if (!Util.isEmpty(mpOpenid)) {
        	loginType=login_by_wechat;
	        userName=mpOpenid;
	        password = "";
	        randCode = null;
	        rememberme = null;
	        openId=null;
		}
		else if (isSmsRandCode) {
			loginType=login_by_mobile;;
			userName = request.getParameter("userName");
			password = "";
			randCode = request.getParameter("randCode");
			rememberme = request.getParameter("remember-me");
			openId=null;
		}
        else if (Util.isEmpty(state)) {
        	loginType=login_by_name;
	        userName = request.getParameter("userName");
	        password = request.getParameter("password");
	        randCode = request.getParameter("randCode");
	        rememberme = request.getParameter("remember-me");
	        openId=request.getParameter("openId");   //  绑定微信用户时上传openid
        } else if (WeChat.isOk() && state.equals(Util.getSession().getId())){
            String code=request.getParameter("code");
            if (!Util.isEmpty(code)) {
	        	loginType=login_by_wechat;
		        userName=Util.getBean(WeChat.class).getOpenId(code);
		        password = "";
		        randCode = null;
		        rememberme = null;
		        openId=null;
            }
            else {
	        	loginType=login_by_wechat;
		        userName=null;
		        password = "";
		        randCode = null;
		        rememberme = null;
		        openId=null;
            }
        }
        else {
        	loginType=login_by_name;
	        userName=null;
	        password = "";
	        randCode = null;
	        rememberme = null;
	        openId=null;          	
        }
    }

	public String getUserName() {
		return userName;
	}

	public String getPassword() {
		return password;
	}
	public int getLoginType() {
		return loginType;
	}
	public String getRandCode() {
		return randCode;
	}

	public String getRememberme() {
		return rememberme;
	}

	public String getLang() {
		return lang;
	}

	public String getOpenId() {
		return openId;
	}
}