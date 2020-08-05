package com.akxy.autocheck;

import com.akxy.autocheck.autocheck.AutoCheckAspectSupport;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@Import(AutoCheckAspectSupport.class)
public class AutoCheckApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutoCheckApplication.class, args);
    }

}
