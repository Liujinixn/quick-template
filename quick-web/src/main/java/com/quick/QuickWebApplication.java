package com.quick;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@ServletComponentScan
@SpringBootApplication
public class QuickWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuickWebApplication.class, args);
        log.info("项目启动成功");
    }

}
