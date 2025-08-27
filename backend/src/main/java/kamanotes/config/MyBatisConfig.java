package kamanotes.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @ClassName MyBatisConfig
 * @Description MyBatis 
 * @Version v1.0
 */
@Configuration
@MapperScan("kamanotes.mapper")
@EnableTransactionManagement
public class MyBatisConfig {
}
