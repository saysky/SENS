package com.liuyanzhao.sens;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * <pre>
 *     SENS run!
 * </pre>
 *
 * @author : saysky
 * @date : 2017/11/14
 */
@Slf4j
@SpringBootApplication
@EnableCaching
@EnableScheduling
@MapperScan("com.liuyanzhao.sens.mapper*")
@ComponentScan(basePackages = {"com.liuyanzhao.sens"})
public class Application {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(Application.class, args);
        String serverPort = context.getEnvironment().getProperty("server.port");
        log.info("SENS started at http://localhost:" + serverPort);
    }
}
