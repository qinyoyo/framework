package top.iotequ.framework.security.oauth2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;
import top.iotequ.framework.security.authentication.AuthenticationToken;

@WebFilter(urlPatterns = {"/oauth/authorize"})
public class Oauth2Filter extends GenericFilterBean {
	private static final Logger log = LoggerFactory.getLogger(Oauth2Filter.class);
    @Autowired
    private ClientDetailsService iotequClientDetailsService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String url = ((HttpServletRequest) request).getServletPath();
        log.debug("Oauth2Filter : {}",url);
        if (url.startsWith("/oauth/authorize")) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Authentication newAuth = createAuthentication(request);
            if (newAuth != null) {
                SecurityContextHolder.getContext().setAuthentication(newAuth);
            } else if (auth != null && !(auth instanceof AnonymousAuthenticationToken)) {
                SecurityContextHolder.getContext().setAuthentication(null);
            }
        }
        chain.doFilter(request, response);
    }

    public Authentication createAuthentication(ServletRequest request) {
        String clientId = request.getParameter("client_id");
        if (clientId == null || clientId.isEmpty()) {
            return null;
        }
        ClientDetails client = iotequClientDetailsService.loadClientByClientId(clientId);
        Collection<? extends GrantedAuthority> authorities = new ArrayList<>();
        AuthenticationToken auth = new AuthenticationToken(clientId,
                null,
                client.getAuthorities());
        return auth;
    }
}
