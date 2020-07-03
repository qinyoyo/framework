package top.iotequ.framework.security.oauth2;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import top.iotequ.framework.controller.HomeController;
import top.iotequ.framework.util.HttpUtils;
import top.iotequ.framework.util.Util;

@RestController
public class Oauth2CodeTestController implements ApplicationRunner {
	@Autowired  
    private Environment env; 
	private String HOST="http://localhost";
    @RequestMapping(value="/res/code")
    String codeTest(HttpServletRequest request,String code,String state) throws Exception {
        if (code!=null) {
        	System.out.printf("code=%s, state=%s\n",code,state);
            String url = OAuth2Util.getTokenByCodeUrl("https://localhost", "pay",
                    "e10adc3949ba59abbe56e057f20f883e",
                    code, "https://localhost/res/code");
            System.out.println(url);
            String json = HttpUtils.getHttpString(HttpUtils.doPost(url));
            System.out.println(json);
            OAuth2AccessToken token = OAuth2Util.getOAuth2AccessToken(json);
            System.out.println(token.toString());
            Map<String,Object> headers = OAuth2Util.getTokenHeader(token);
            System.out.println(headers.toString());
            return HttpUtils.getHttpString(HttpUtils.doPost("https://localhost/sysUser/list",headers,null,""));
        } else{
        	System.out.println("code=null");
            return null;
        }
    }

    @RequestMapping(value="/res/oauth2/url")
    String testClient(HttpServletRequest request,String type,String host) throws Exception {
    	if (Util.isEmpty(host))  host=HOST;
    	if (type==null) type="";
    	type=type.toLowerCase().trim();
    	String url="/res/oauth2/url?type=[client|code|password|implicit]<&host=http://www.svein.com.cn>";
    	if (type.equals("client"))
    		url=OAuth2Util.getClientCredentialsTokenUrl(host, "pay", "e10adc3949ba59abbe56e057f20f883e", "api");
    	else if (type.equals("code"))
    		url=OAuth2Util.getAuthorizeCodeUrl(host, "pay", host+"/res/code", "api", "abcd");
    	else if (type.equals("password"))
    		url=OAuth2Util.getPasswordTokenUrl(host,"admin","123456","pay", "e10adc3949ba59abbe56e057f20f883e", "api");
    	else if (type.equals("implicit"))
    		url=OAuth2Util.getImplicitTokenUrl(host,"pay", "e10adc3949ba59abbe56e057f20f883e", "api",host+"/res/code");
    	return url;
    }

	@Override
	public void run(ApplicationArguments args) throws Exception {
		if (env.getProperty("server.port",Integer.class)==443) HOST="https://localhost";
	}
}
