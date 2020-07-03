package top.iotequ.framework.config;

import java.io.File;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import top.iotequ.framework.bean.SpringContext;

@Configuration
public class MyWebAppConfigurer implements WebMvcConfigurer  {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations("classpath:/webapp/");
        String webapp=new File(SpringContext.getProjectHomeDirection(),"webapp").getAbsolutePath();
        if (webapp.indexOf("\\")>=0) webapp+="\\";
        else webapp+="/";
        registry.addResourceHandler("/**").addResourceLocations("file:"+webapp);
    }   
}
