package com.shang.demo;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class Consumer {

    @JmsListener(destination="shang")
    public void readMessage(String text){
        System.out.println("接收到消息："+text);
    }
    @JmsListener(destination = "testMap")
    public void readMap(Map map){
        System.out.println(map);
    }
}