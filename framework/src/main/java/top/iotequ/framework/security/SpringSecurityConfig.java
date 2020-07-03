package top.iotequ.framework.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import top.iotequ.framework.security.authentication.CustomAuthenticationDetailsSource;
import top.iotequ.framework.security.authentication.CustomAuthenticationProvider;
import top.iotequ.framework.security.authority.CustomFilterInvocationSecurityMetadataSource;
import top.iotequ.framework.security.authority.UrlAccessDecisionManager;
import top.iotequ.framework.security.restlogin.*;
import top.iotequ.framework.security.service.SecurityService;
import top.iotequ.framework.util.StringUtil;
import top.iotequ.framework.util.Util;

@Order(2)
@Configuration
@ComponentScan(basePackages = {"top.iotequ.framework.security"})
@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    public static final String loginPage = "/login";
    public static final String resourcePage = "/res";
    public static final String commonPage = "/common";
    public static final String errorPage = "/error";
    public static final String loginProcessingUrl = "/login/login";
    // public static final String loginErrorUrl = "/login/error";
    // 默认值 public static final String logoutUrl = "/logout";
    public static final String[] whiteList = new String[]
            {"/index.html","/static/**",  // vue 目录文件
            "/","/m/**", "/**/*.js", "/**/*.css",
            "/webjars/**", "/favicon.ico", "/MP_*.txt", resourcePage + "/**", commonPage + "/**",
            errorPage + "/**", errorPage + "*"};   //  以上名单不经过security验证
    public static final String[] loginList = new String[]{"/oauth/**", loginPage + "/**", "/log*",
            "/"}; // 登录需要的白名单，不能放到whiteList里，由于绕过了security，认证登录等无法实现
    //	public static final String[] whiteList = new String[] { "/**/*.js", "/**/*.css", "/**/*.png", "/**/*.gif",
//	            "/**/*.jpg", "/**/*.ico", "/**/*.font", "/**/*.ttf", "/randCodeImage**", "/plug-in/**", "/css/**",
//	            "/fonts/**", "/images/**", "/js/**", "/common/**","/error/**" };   //  以上名单不经过security验证
    private static final Logger log = LoggerFactory.getLogger(SpringSecurityConfig.class);

    @Autowired
    private SecurityService userDetailsService;
    @Autowired
    private CustomAuthenticationDetailsSource authenticationDetailsSource;

    @Autowired
    private RestAuthenticationEntryPoint authenticationEntryPoint;  //  未登陆时返回 JSON 格式的数据给前端（否则为 html）
    @Autowired
    private RestAuthenticationSuccessHandler authenticationSuccessHandler;  // 登录成功返回的 JSON 格式数据给前端（否则为 html）
    @Autowired
    private RestAuthenticationFailureHandler authenticationFailureHandler;  //  登录失败返回的 JSON 格式数据给前端（否则为 html）
    @Autowired
    private RestLogoutSuccessHandler logoutSuccessHandler;  // 注销成功返回的 JSON 格式数据给前端（否则为 登录时的 html）
    @Autowired
    private RestAccessDeniedHandler accessDeniedHandler;    // 无权访问返回的 JSON 格式数据给前端（否则为 403 html 页面）
    @Autowired
    private SessionRegistry sessionRegistry;
    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }
    public SpringSecurityConfig() {
        super();
    }

    private static boolean matchWhiteList(String value, String[] wl) {
        for (String s : wl) {
            if (s.endsWith("**")) {
                s = s.replace("**", "");
                if (value.startsWith(s)) {
                    return true;
                }
            } else if (s.endsWith("*")) {
                s = s.replace("*", "");
                if (value.startsWith(s)) {
                    String s1 = value.substring(s.length());
                    if (s1 == null || s1.length() == 0 || s1.indexOf("/") < 0) {
                        return true;
                    }
                }
            } else if (s.equals(value)) {
                return true;
            }
        }
        return false;
    }

    public static boolean matchWhiteList(String value) {
        if (matchWhiteList(value, whiteList)) {
            return true;
        } else {
            return matchWhiteList(value, loginList);
        }
    }

    PasswordEncoder passwordEncoder() {
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                return StringUtil.encodePassword((String) rawPassword);
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return encodedPassword.equals(StringUtil.encodePassword((String) rawPassword));
            }
        };
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        final CustomAuthenticationProvider authProvider = new CustomAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Override
    public void configure(final WebSecurity web) {
        web.ignoring().antMatchers(whiteList);
    }

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth
                .authenticationProvider(authProvider())
                .userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        TokenStore tokenStore= Util.getBean(TokenStore.class);
        http
                .cors()
                .and()

                .csrf().disable()

                .httpBasic().authenticationEntryPoint(authenticationEntryPoint)
                .and()

                .authorizeRequests().antMatchers(loginList).permitAll()
                .and()

                .authorizeRequests().antMatchers("/oauth/**").permitAll()
                .anyRequest().authenticated()

                .withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
                    public <O extends FilterSecurityInterceptor> O postProcess(O fsi) {
                        fsi.setSecurityMetadataSource(new CustomFilterInvocationSecurityMetadataSource().tokenStore(tokenStore));
                        fsi.setAccessDecisionManager(new UrlAccessDecisionManager());
                        return fsi;
                    }
                })
                .accessDecisionManager(new UrlAccessDecisionManager())
                .and()

                .formLogin()
                .loginPage(loginPage)
                .loginProcessingUrl(loginProcessingUrl)
                .successHandler(authenticationSuccessHandler)
                .failureHandler(authenticationFailureHandler)
                .authenticationDetailsSource(authenticationDetailsSource)
                .permitAll()
                .and()

                .logout()
                .logoutSuccessHandler(logoutSuccessHandler)
                .permitAll()
                .and()

                .rememberMe().tokenValiditySeconds(7 * 24 * 3600)
                .and()

                //.anonymous().principal("guest").authorities("ROLE_guest")  // 禁止匿名登录
                //.and()

                .exceptionHandling().accessDeniedHandler(accessDeniedHandler)
                .and()

                .headers().frameOptions().disable()
                .and()
                .sessionManagement()
                .invalidSessionUrl(loginPage+"/invalidSession")  //回报异常
//                .maximumSessions(1)
//                .sessionRegistry(sessionRegistry)
        ;

    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}