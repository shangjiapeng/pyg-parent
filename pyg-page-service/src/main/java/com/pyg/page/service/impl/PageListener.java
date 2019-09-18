package com.pyg.page.service.impl;

import com.pyg.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.*;

/**
 * 监听类,用于生成静态的网页
 */
@Component
public class PageListener implements MessageListener {

    @Autowired
    private ItemPageService itemPageService;

    @Override
    public void onMessage(Message message) {
        ObjectMessage objectMessage= (ObjectMessage) message;
        Long[] ids;
        try {
            ids = (Long[]) objectMessage.getObject();
            System.out.println("接收到消息:"+ids);
            for (Long goodsId : ids) {
                itemPageService.genItemHtml(goodsId);
            }
            System.out.println("网页生成完毕");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
