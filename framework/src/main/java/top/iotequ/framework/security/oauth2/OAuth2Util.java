package top.iotequ.framework.security.oauth2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.lang.NonNull;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import top.iotequ.framework.exception.IotequException;
import top.iotequ.framework.exception.TokenException;
import top.iotequ.framework.pojo.Role;
import top.iotequ.framework.util.Util;

public class OAuth2Util {
	public static final String HTTP_HEADER_KEY = "Authorization";

	public static String getPasswordTokenUrl(@NonNull String host,@NonNull String userName,@NonNull String password,
			@NonNull String clientId,@NonNull String clientSecret,@NonNull String scope) {
		if (!host.endsWith("/")) host=host+"/";
		return host+String.format("oauth/token?username=%s&password=%s&grant_type=password&client_id=%s&client_secret=%s&scope=%s", 
				userName,password,clientId,clientSecret,scope);
	}	
	
	public static String getImplicitTokenUrl(@NonNull String host,@NonNull String clientId,@NonNull String clientSecret,@NonNull String scope,@NonNull String redirectUrl) {
		if (!host.endsWith("/")) host=host+"/";
		return host+String.format("oauth/token?grant_type=implicit&client_id=%s&client_secret=%s&scope=%s&redirect_uri=%s", 
				clientId,clientSecret,scope,redirectUrl);
	}	
	
	public static String getClientCredentialsTokenUrl(@NonNull String host,@NonNull String clientId,@NonNull String clientSecret,@NonNull String scope) {
		if (!host.endsWith("/")) host=host+"/";
		return host+String.format("oauth/token?grant_type=client_credentials&client_id=%s&client_secret=%s&scope=%s", clientId,clientSecret,scope);
	}

	public static String getAuthorizeCodeUrl(@NonNull String host,@NonNull String clientId,@NonNull String redirectUrl,@NonNull String scope,String state) {
		if (!host.endsWith("/")) host=host+"/";
		return host+String.format("oauth/authorize?client_id=%s&response_type=code&scope=%s&redirect_uri=%s%s",
				clientId,scope,redirectUrl,(state==null?"":"&state="+state));
	}

	public static String getTokenByCodeUrl(@NonNull String host,@NonNull String clientId,@NonNull String clientSecret,@NonNull String code,@NonNull String redirectUrl) {
		if (!host.endsWith("/")) host=host+"/";
		return host+String.format("oauth/token?grant_type=authorization_code&code=%s&client_id=%s&client_secret=%s&redirect_uri=%s",
				code,clientId,clientSecret,redirectUrl);
	}

	public static Collection<ConfigAttribute> getAttributes(HttpServletRequest request,
			TokenStore tokenStore) throws IllegalArgumentException {
		if (tokenStore==null) throw  new TokenException(IotequException.NULL_OBJECT,"Token store为空，请检查oauth2配置");
		if (request!=null) {
			String tokenHeader=request.getHeader(HTTP_HEADER_KEY);
		    if (tokenHeader==null) tokenHeader=request.getParameter(OAuth2AccessToken.ACCESS_TOKEN);
			if (tokenHeader==null || tokenHeader.isEmpty())  return null;
			else {
				String[] tokenParams = tokenHeader.split(" ");
				String token = tokenParams[tokenParams.length-1];
				OAuth2AccessToken ac=tokenStore.readAccessToken(token);
				if (ac==null) throw new TokenException(IotequException.INVALID_TOKEN,"无效的token");
				if (ac.isExpired()) throw new TokenException(IotequException.TOKEN_EXPIRED,"Token 已过期");
				OAuth2Authentication auth = tokenStore.readAuthentication(token);
				if (auth!=null) {
					Collection<ConfigAttribute> collection = new ArrayList<>();
					for ( GrantedAuthority a : auth.getAuthorities()) {
						Role r=new Role();
						r.setCode(a.getAuthority().substring(5));
						collection.add(r);
					}
					return collection;
				} else {
					throw  new TokenException(IotequException.INVALID_TOKEN,"无效的token");
				}
			}
		} else return null;
	}

	public static OAuth2AccessToken getOAuth2AccessToken(String json) {
		try {
			@SuppressWarnings("unchecked")
			Map<String, String> result = Util.getGson().fromJson(json, Map.class);
			if (result!=null) return DefaultOAuth2AccessToken.valueOf(result);
			else return null;
		} catch (Exception e) {
			return null;
		}
	}

	public static Map<String,Object> getTokenHeader(OAuth2AccessToken token) {
		if (token==null) return null;
		String tokenString = token.getValue();
		String tokenType=token.getTokenType();
		if (tokenType!=null && !tokenType.isEmpty()) tokenString=tokenType+" "+tokenString;
		Map<String,Object> map = new HashMap<>();
		map.put(HTTP_HEADER_KEY,tokenString);
		return map;
	}

}
