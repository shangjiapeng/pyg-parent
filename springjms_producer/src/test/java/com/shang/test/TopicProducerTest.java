package com.shang.test;
import com.shang.demo.TopicProducer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext-jms-producer.xml")
public class TopicProducerTest {

    @Autowired
    private TopicProducer topicProducer;
    @Test
    public void sendTextMessage() {
        topicProducer.sendTextMessage("springJMS-发布订阅");
    }
}