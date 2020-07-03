package top.iotequ.framework.security.authority;

import java.util.ArrayList;
import java.util.Collection;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;

import top.iotequ.framework.exception.IotequException;
import top.iotequ.framework.exception.TokenException;
import top.iotequ.framework.pojo.Role;
import top.iotequ.framework.pojo.User;
import top.iotequ.framework.security.oauth2.OAuth2Util;
import top.iotequ.framework.util.Util;

public class CustomFilterInvocationSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {
	private TokenStore tokenStore=null;
	public CustomFilterInvocationSecurityMetadataSource tokenStore(TokenStore tokenStore) {
		this.tokenStore=tokenStore;
		return this;
	}
	@SuppressWarnings("unchecked")
	@Override
	public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
		@SuppressWarnings("unused")
		FilterInvocation fi = (FilterInvocation) object;
		Collection<ConfigAttribute> collection = OAuth2Util.getAttributes(fi.getHttpRequest(),tokenStore);
		if (collection==null) {
			User user = Util.getUser();
			if (user != null)
				collection = (Collection<ConfigAttribute>) user.getConfigAttribute();
		}		
		if (collection == null || collection.isEmpty()) { // 不能返回空，否则将跳过权限验证。返回guest权限
			collection = new ArrayList<>();
			Role guest=new Role();
			guest.setCode("guest");
			collection.add(guest);
		}
		return collection;
	}


	@Override
	public Collection<ConfigAttribute> getAllConfigAttributes() {
		return null;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return FilterInvocation.class.isAssignableFrom(clazz);
	}
}