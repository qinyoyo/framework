package top.iotequ.framework.config;

import java.util.Properties;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.github.pagehelper.PageHelper;

@Configuration
public class PageConfig {

	@Autowired
    SqlSessionFactory sqlSessionFactory;
    @Bean
    public PageHelper pageHelper() throws Exception {
    	PageHelper pageHelper = new PageHelper();; 
        Properties properties = new Properties();
        String dbId=sqlSessionFactory.getConfiguration().getDatabaseId();
        properties.setProperty("helperDialect", dbId==null?"mysql":dbId.toLowerCase());
        properties.setProperty("reasonable", "true");
        properties.setProperty("pageSizeZero", "true");
        properties.setProperty("supportMethodsArguments", "true");
        properties.setProperty("params","count=countSql");
        pageHelper.setProperties(properties);
        return pageHelper;
    }

}
