package top.iotequ.framework.security.oauth2;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

//@Order(6)
//@Configuration
//@EnableResourceServer
public class OAuth2ResourceServerConfig implements ResourceServerConfigurer  {
	 @Override
	    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		 	resources
		 		.resourceId("resource_iotequ")
		 	;
	    }

	    @Override
	    public void configure(HttpSecurity http) throws Exception {
	    	http
	            .authorizeRequests().anyRequest().authenticated()
	        ;
	    }

}
