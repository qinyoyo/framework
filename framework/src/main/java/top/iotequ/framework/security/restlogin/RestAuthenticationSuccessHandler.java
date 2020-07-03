package top.iotequ.framework.security.restlogin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import top.iotequ.framework.exception.IotequException;
import top.iotequ.framework.exception.IotequThrowable;
import top.iotequ.framework.pojo.User;
import top.iotequ.framework.security.authentication.CustomAuthenticationProvider;
import top.iotequ.framework.security.authentication.CustomWebAuthenticationDetails;
import top.iotequ.framework.util.RestJson;
import top.iotequ.framework.util.Util;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@Component
public class RestAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        RestJson j = new RestJson();
        if (authentication.getPrincipal() instanceof User) {
            User user = (User) authentication.getPrincipal();
            if (Util.isEmpty(user.getName())) {
                IotequException e = CustomAuthenticationProvider.getLoginException();
                if (Objects.nonNull(e)) throw e;
                else throw new IotequException(IotequThrowable.ACCESS_DENIED, "name is empty");
            }
            if ("guest".equals(user.getName())) {
                CustomWebAuthenticationDetails details = (CustomWebAuthenticationDetails) authentication
                        .getDetails();
                if (details.getLoginType()
                        == CustomWebAuthenticationDetails.login_by_wechat) {   // 微信授权登录，没有绑定，需要绑定账户
                    j.put("openId", details.getUserName());
                    j.setMessage("please bind openid to user");
                }
            }
            if (!user.isCredentialsNonExpired()) {  //  密码过期，需要修改密码
                j.setError(IotequThrowable.PASSWORD_EXPIRED, "", null);
            }
        }
        j.sendTo(response);
    }
}

