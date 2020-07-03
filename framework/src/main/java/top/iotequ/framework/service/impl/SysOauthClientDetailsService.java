package top.iotequ.framework.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import top.iotequ.framework.exception.IotequException;
import top.iotequ.framework.pojo.OauthClientDetails;
import top.iotequ.framework.util.RestJson;
import top.iotequ.framework.util.StringUtil;
import top.iotequ.framework.util.Util;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SysOauthClientDetailsService extends CgSysOauthClientDetailsService {
	@Autowired
	private Environment env;
	public static final String HTTP_HEADER_KEY = "Authorization";
	public static String getPasswordTokenUrl( String host,  String clientId,  String clientSecret,  String scope) {
		if (Util.isEmpty(clientId)) return "ERROR: clientId must get a value";
		if (Util.isEmpty(clientSecret)) return "ERROR: clientSecret must get a value";
		if (Util.isEmpty(scope)) return "ERROR: scope must get a value";
		if (!host.endsWith("/")) host=host+"/";
		return host+String.format("oauth/token?username=%s&password=%s&grant_type=password&client_id=%s&client_secret=%s&scope=%s",
				"USER_NAME","PASSWORD",clientId,clientSecret,scope);
	}

	public static String getImplicitTokenUrl( String host, String clientId, String clientSecret, String scope, String redirectUrl) {
		if (Util.isEmpty(clientId)) return "ERROR: clientId must get a value";
		if (Util.isEmpty(clientSecret)) return "ERROR: clientSecret must get a value";
		if (Util.isEmpty(scope)) return "ERROR: scope must get a value";
		if (Util.isEmpty(redirectUrl)) return "ERROR: redirectUrl must get a value";
		if (!host.endsWith("/")) host=host+"/";
		return host+String.format("oauth/token?grant_type=implicit&client_id=%s&client_secret=%s&scope=%s&redirect_uri=%s",
				clientId,clientSecret,scope,redirectUrl);
	}

	public static String getClientCredentialsTokenUrl( String host, String clientId, String clientSecret, String scope) {
		if (Util.isEmpty(clientId)) return "ERROR: clientId must get a value";
		if (Util.isEmpty(clientSecret)) return "ERROR: clientSecret must get a value";
		if (Util.isEmpty(scope)) return "ERROR: scope must get a value";
		if (!host.endsWith("/")) host=host+"/";
		return host+String.format("oauth/token?grant_type=client_credentials&client_id=%s&client_secret=%s&scope=%s", clientId,clientSecret,scope);
	}

	public static String getAuthorizeCodeUrl( String host, String clientId, String redirectUrl, String scope,String additionalParameters) {
		if (Util.isEmpty(clientId)) return "ERROR: clientId must get a value";
		if (Util.isEmpty(redirectUrl)) return "ERROR: redirectUrl must get a value";
		if (Util.isEmpty(scope)) return "ERROR: scope must get a value";
		if (!host.endsWith("/")) host=host+"/";

		String url = host+String.format("oauth/authorize?client_id=%s&response_type=code&scope=%s&redirect_uri=%s",clientId,scope,redirectUrl);
		if (!Util.isEmpty(additionalParameters)) {
			try {
				Map<String, Object> m = Util.mapFromJson(additionalParameters);
				if (m != null) {
					for (String s : m.keySet()) url = url + "&" + URLEncoder.encode(s,"utf-8") + "=" + URLEncoder.encode(StringUtil.toString(m.get(s)),"utf-8");
				}
			} catch (Exception e){}
		}
		return url;
	}

	public static String getTokenByCodeUrl( String host, String clientId, String clientSecret, String code, String redirectUrl) {
		if (Util.isEmpty(clientId)) return "ERROR: clientId must get a value";
		if (Util.isEmpty(clientSecret)) return "ERROR: clientSecret must get a value";
		if (Util.isEmpty(redirectUrl)) return "ERROR: redirectUrl must get a value";
		if (!host.endsWith("/")) host=host+"/";
		return host+String.format("oauth/token?grant_type=authorization_code&code=CODE&client_id=%s&client_secret=%s&redirect_uri=%s",
				code,clientId,clientSecret,redirectUrl);
	}

	String getAuthUrl(OauthClientDetails obj) {
		boolean http = Util.isEmpty(env.getProperty("server.ssl.key-store"));
		String HOST = http ? "http://localhost":"https://localhost";
		String port = env.getProperty("server.port");
		if (!Util.isEmpty(port)) {
			if (http && !"80".equals(port)) HOST += (":"+port);
			else if (!http && !"443".equals(port)) HOST += (":"+port);
		}
		if (obj==null) return null;
		else {
			String secret = StringUtil.encodePassword(obj.getClientSecret());
			switch (obj.getAuthorizedGrantTypes()) {
				case "password": return getPasswordTokenUrl(HOST,obj.getClientId(),secret,obj.getScope());
				case "authorization_code": return getAuthorizeCodeUrl(HOST,obj.getClientId(),obj.getWebServerRedirectUri(),obj.getScope(),obj.getAdditionalInformation());
				case "implicit": return getImplicitTokenUrl(HOST,obj.getClientId(),secret,obj.getScope(),obj.getWebServerRedirectUri());
				case "client_credentials":return getClientCredentialsTokenUrl(HOST,obj.getClientId(),secret,obj.getScope());
			}
		}
		return null;
	}
	@Override
	public void beforeList(List<OauthClientDetails> list, HttpServletRequest request) {
		for(OauthClientDetails o : list) {
			o.setSecret(StringUtil.encodePassword(o.getClientSecret()));
			o.setAuthUrl(getAuthUrl(o));
		}
	}

	@Override
	public void beforeExport(List<OauthClientDetails> list, HttpServletRequest request) throws IotequException {
		for(OauthClientDetails o : list) 
			o.setClientSecret(StringUtil.encodePassword(o.getClientSecret()));
	}
	@Override
	public  void beforeUpdate(OauthClientDetails obj,HttpServletRequest request) throws IotequException {
		if (obj!=null) {
			obj.setSecret(StringUtil.encodePassword(obj.getClientSecret()));
			obj.setAuthUrl(getAuthUrl(obj));
		}
	}

	@Override
	public void afterSave(OauthClientDetails obj0, OauthClientDetails obj, HttpServletRequest request, RestJson j) throws IotequException {
		if (obj!=null && j!=null) {
			Map<String,Object> map = new HashMap<String,Object>() {{
				put("secret",StringUtil.encodePassword(obj.getClientSecret()));
				put("authUrl",getAuthUrl(obj));
			}};
			j.parameter("record",map);
		}
	}
}
