package com.shang.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 消息生产者
 */
@RestController
public class QueueController {

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    @RequestMapping("/send")//getMapping /postMapping 只能接受(get/post)请求
    public void send(String text){
        jmsMessagingTemplate.convertAndSend("shang",text);
    }

    @RequestMapping("/sendMap")
    public void sendMap(){
        Map map = new HashMap<>();
        map.put("mobile","15866886868");
        map.put("name","zhangsan");
        jmsMessagingTemplate.convertAndSend("testMap",map);
    }

    @RequestMapping("/sendsms")
    public void sendSms(){
        Map map=new HashMap<>();
        map.put("mobile", "17683919558");
        map.put("template_code", "SMS_125018654");
        map.put("sign_name", "\u9ED1\u9A6C\u6B66\u6C49\u6821\u533A");
        map.put("param", "{\"number\":\"123456\"}");
        jmsMessagingTemplate.convertAndSend("sms",map);
    }
}
