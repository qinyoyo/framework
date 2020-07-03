package top.iotequ.framework.security.authentication;

import java.util.Locale;

import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import top.iotequ.framework.exception.IotequAuthenticationException;
import top.iotequ.framework.exception.IotequException;
import top.iotequ.framework.exception.IotequThrowable;
import top.iotequ.framework.pojo.User;
import top.iotequ.framework.security.service.SecurityService;
import top.iotequ.framework.controller.RandCodeImageService;
import top.iotequ.framework.util.*;

import javax.servlet.http.HttpServletResponse;

//@Component
public class CustomAuthenticationProvider extends DaoAuthenticationProvider {
	private static String LOGIN_EXCEPTION_MSG="LOGIN_EXCEPTION_MSG";
	private static void setExceptionMessage(String msg) {
		if (Util.isEmpty(msg)) Util.removeSessionAttribute(CustomAuthenticationProvider.LOGIN_EXCEPTION_MSG);
		else {
			Util.setSessionAttribute(LOGIN_EXCEPTION_MSG,msg);
		}
	}
	public static IotequException getLoginException() {
		String msg = StringUtil.toString(Util.getSessionAttribute(CustomAuthenticationProvider.LOGIN_EXCEPTION_MSG));
		if (Objects.nonNull(msg)) {
			setExceptionMessage(null);
			return new IotequException(msg,null);
		} else return null;
	}
	public static void sendLoginException(Exception e, HttpServletResponse httpServletResponse) {
		RestJson j = new RestJson();
		e.printStackTrace();
		Exception ex = CustomAuthenticationProvider.getLoginException();
		if (Objects.nonNull(ex)) j.setMessage(ex);
		else j.setMessage(e);
		j.sendTo(httpServletResponse);
	}
	@Autowired
	SecurityService securityService;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		Object object = authentication.getDetails();
		int loginType = CustomWebAuthenticationDetails.login_by_name;
		UserDetails user=null;
		String msg = null;
		String pass = "";
		String openId = null;
		this.setHideUserNotFoundExceptions(false);
		if (object instanceof CustomWebAuthenticationDetails) {
			CustomWebAuthenticationDetails details = (CustomWebAuthenticationDetails) authentication
					.getDetails();
			pass = details.getPassword();
			openId = details.getOpenId();
			if (!Util.isEmpty(details.getLang())) {
				Util.setSessionAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME,
						new Locale(details.getLang()));   //设置语言
			}
			setExceptionMessage(null);
			loginType = details.getLoginType();
			if (loginType == CustomWebAuthenticationDetails.login_by_name) { // 手机验证码登录或用户名登录需要验证码验证
				if (!Util.equals(details.getRandCode(), RandCodeImageService.getRandCode())) {
					msg = IotequThrowable.INVALID_VERIFICATION_CODE;
					setExceptionMessage(msg);
					throw new IotequAuthenticationException(
							IotequThrowable.INVALID_VERIFICATION_CODE, msg);
				}
			} else if (loginType == CustomWebAuthenticationDetails.login_by_mobile) {
				try {
					Util.mobileVerifyCodeCheck(details.getUserName(), details.getRandCode());
				} catch (IotequException ie) {
					msg = ie.getError();
					setExceptionMessage(msg);
					throw new IotequAuthenticationException(msg, null);
				}
			}
			user = securityService.loadUserByUsername(loginType + ":" + details.getUserName());
			if (user == null || "guest".equals(user.getUsername())) {
				if (user != null && loginType
						== CustomWebAuthenticationDetails.login_by_wechat) {  // 微信登录，没有匹配到用户，需要绑定用户

				} else {
					if (!this.hideUserNotFoundExceptions)
						msg = IotequThrowable.USER_NOT_EXIST;
					else
						msg = IotequThrowable.ERROR;
					setExceptionMessage(msg);
					throw new IotequAuthenticationException(IotequThrowable.USER_NOT_EXIST, msg);
				}
			}
		}  else if (object instanceof Map && "password".equals(StringUtil.toString(((Map)object).get("grant_type")))) {
			loginType = CustomWebAuthenticationDetails.login_by_oauth_password;
			pass = StringUtil.toString(authentication.getCredentials());
			user = securityService.loadUserByUsername(StringUtil.toString(authentication.getPrincipal()));
		}
		if (user==null) throw new IotequAuthenticationException(IotequThrowable.USER_NOT_EXIST,null);
		if (!user.isAccountNonExpired()) {
			msg =IotequThrowable.USER_EXPIRED;
			setExceptionMessage(msg);
			throw new IotequAuthenticationException(IotequThrowable.USER_EXPIRED, msg);
		}
		if (!user.isAccountNonLocked()) {
			msg = IotequThrowable.USER_LOCKED;
			setExceptionMessage(msg);
			throw new IotequAuthenticationException(IotequThrowable.USER_LOCKED, msg);
		}
		if (!user.isEnabled()) {
			msg = IotequThrowable.USER_DISABLED;
			setExceptionMessage(msg);
			throw new IotequAuthenticationException(IotequThrowable.USER_DISABLED, msg);
		}
		if (loginType == CustomWebAuthenticationDetails.login_by_name
				|| loginType==CustomWebAuthenticationDetails.login_by_oauth_password) {  // 用户名登录需要验证密码

			if (Util.isEmpty(pass)) {
				msg =IotequThrowable.PASSWORD_MISS;
				setExceptionMessage(msg);
				throw new IotequAuthenticationException(IotequThrowable.PASSWORD_MISS, msg);
			}
			if (!Util.equals(user.getPassword(), StringUtil.encodePassword(pass))) {
				msg = IotequThrowable.USER_NOT_EXIST;
				setExceptionMessage(msg);
				try {
					int et = SqlUtil.sqlQueryInteger(
							"select password_error_times from sys_user where name=?",
							user.getUsername());
					if (et >= 3) {
						String sql = "update sys_user set password_error_times=password_error_times+1,locked=1 where name=?";
						SqlUtil.sqlExecute(sql, user.getUsername());
					} else {
						String sql = "update sys_user set password_error_times=password_error_times+1 where name=?";
						SqlUtil.sqlExecute(sql, user.getUsername());
					}
				} catch (Exception e) {
				}
				throw new IotequAuthenticationException(IotequThrowable.USER_NOT_EXIST, msg);
			}
			try {
				if (Util.isEmpty(openId)) {
					String sql = "update sys_user set password_error_times=0 where name=?";
					SqlUtil.sqlExecute(sql, user.getUsername());
				} else {
					String sql = "update sys_user set password_error_times=0 ,wechat_openid=? where name=?";
					SqlUtil.sqlExecute(sql, openId, user.getUsername());
				}
			} catch (Exception e) {
			}
		}
		try {
			User who = SqlUtil.sqlQueryFirst(User.class,false,"select * from sys_user where name = ?",user.getUsername());
			Util.writeLog(null, "login", who, "login type = %d ,user name = %s", loginType, user.getUsername());
		} catch (IotequException e) {
		}
		return new AuthenticationToken(user, user.getPassword(), user.getAuthorities());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}
}