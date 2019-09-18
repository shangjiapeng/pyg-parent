package com.shang.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * springboot的初始化应用
 */

@SpringBootApplication(scanBasePackages = "com.shang")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }
}
