package top.iotequ.framework.security.authority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Service;

import top.iotequ.framework.exception.IotequAccessDeniedException;
import top.iotequ.framework.exception.IotequThrowable;
import top.iotequ.framework.security.SpringSecurityConfig;
import top.iotequ.framework.security.oauth2.Oauth2Filter;
import top.iotequ.framework.security.service.SecurityService;
import top.iotequ.framework.util.Util;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

@Service
public class UrlAccessDecisionManager implements AccessDecisionManager {
	private static final Logger log = LoggerFactory.getLogger(UrlAccessDecisionManager.class);
	public static final String FORBIDDEN_URL = "FORBIDDEN_URL";
	@Override
	public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes)
	            throws AccessDeniedException, InsufficientAuthenticationException {
		HttpServletRequest request = ((FilterInvocation) object).getHttpRequest();
		String url = request.getServletPath();
		log.debug("url={} , roles={}",url,configAttributes);
		if (url.toLowerCase().startsWith("/oauth/")) return;
		if (this.matchers(request, SpringSecurityConfig.loginList))   return ;    //  loginList不需要任何权限
		//if (!authentication.isAuthenticated())   throw new AccessDeniedException("login first");  开放guest权限，不判断
		if (SecurityService.hasGrantedAttribute(request,configAttributes)) return;
		Util.setSessionAttribute(FORBIDDEN_URL, url);
		throw new IotequAccessDeniedException(IotequThrowable.ACCESS_DENIED,url);
	}

	@Override
	public boolean supports(ConfigAttribute attribute) {
		return true;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return  clazz.equals(FilterInvocation.class);
	}
	private boolean matchers(HttpServletRequest request, String... urls) {
		for (String url : urls) {
			AntPathRequestMatcher matcher = new AntPathRequestMatcher(url);
			if (matcher.matches(request)) {
				return true;
			}
		}
		return false;
	}
}
