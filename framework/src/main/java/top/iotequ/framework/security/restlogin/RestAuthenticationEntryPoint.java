package top.iotequ.framework.security.restlogin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import top.iotequ.framework.exception.IotequThrowable;
import top.iotequ.framework.security.authentication.CustomAuthenticationProvider;
import top.iotequ.framework.util.RestJson;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        CustomAuthenticationProvider.sendLoginException(e,httpServletResponse);
    }
}
