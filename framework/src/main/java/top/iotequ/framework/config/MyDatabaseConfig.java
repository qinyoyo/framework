package top.iotequ.framework.config;

import java.util.Properties;

import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
public class MyDatabaseConfig {
	
    @Bean
    public DatabaseIdProvider getDatabaseIdProvider() {
        DatabaseIdProvider databaseIdProvider = new VendorDatabaseIdProvider();
        Properties p = new Properties();
        p.setProperty("MySQL", "MySql");   // 第一个不能随意修改，第二个为databaseId，可修改
        p.setProperty("SQL Server", "SQLServer");
        p.setProperty("Oracle", "Oracle");
        databaseIdProvider.setProperties(p);
        return databaseIdProvider;
    }
}