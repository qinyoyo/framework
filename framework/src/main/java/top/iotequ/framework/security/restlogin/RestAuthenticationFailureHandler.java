package top.iotequ.framework.security.restlogin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import top.iotequ.framework.exception.IotequException;
import top.iotequ.framework.security.authentication.CustomAuthenticationProvider;
import top.iotequ.framework.util.RestJson;
import top.iotequ.framework.util.StringUtil;
import top.iotequ.framework.util.Util;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@Component
public class RestAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        CustomAuthenticationProvider.sendLoginException(e,httpServletResponse);
    }
}
