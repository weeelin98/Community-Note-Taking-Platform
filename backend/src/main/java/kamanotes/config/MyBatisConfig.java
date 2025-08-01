package com.kama.notes.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @ClassName MyBatisConfig
 * @Description MyBatis 
 * @Version v1.0
 */
@Configuration
@MapperScan("com.kama.notes.mapper")
@EnableTransactionManagement
public class MyBatisConfig {
}
