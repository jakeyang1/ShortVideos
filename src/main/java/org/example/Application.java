package org.example;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Hello world!
 *
 */

@EnableAspectJAutoProxy(exposeProxy = true)
@MapperScan("org.example.mapper")
@SpringBootApplication
public class Application {
    public static void main( String[] args )
    {
        SpringApplication.run(Application.class, args);
    }
}
