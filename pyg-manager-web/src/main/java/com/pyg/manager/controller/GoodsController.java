package com.pyg.manager.controller;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.pyg.pojo.TbItem;
import com.pyg.pojogroup.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.pojo.TbGoods;
import com.pyg.sellergoods.service.GoodsService;
import com.pyg.entity.PageResult;
import com.pyg.entity.ResultInfo;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

/**
 * controller
 *
 * @author Administrator
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Reference
    private GoodsService goodsService;

    //@Reference(timeout = 10000)
    //private ItemSearchService itemSearchService;

    //@Reference(timeout = 10000)
    //private ItemPageService itemPageService;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private Destination queueSolrDestination;//用于导入solr索引库的消息目标(点对点)

    @Autowired
    private Destination queueSolrDeleteDestination;//用于删除solr索引库消息的目标(点对点)

    @Autowired
    private Destination topicPageDestination;//用于生成商品详细页的消息目标(发布/订阅)

    @Autowired
    private Destination topicPageDeleteDestination;//用于删除商品详细页的消息目标(发布/订阅)

    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findAll")
    public List<TbGoods> findAll() {
        return goodsService.findAll();
    }


    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findPage")
    public PageResult findPage(int page, int rows) {
        return goodsService.findPage(page, rows);
    }

    //运营商后台不需要商品增加的功能
    /*	*//**
     * 增加
     * @param goods
     * @return
     *//*
	@RequestMapping("/add")
	public ResultInfo add(@RequestBody TbGoods goods){
		try {
			goodsService.add(goods);
			return new ResultInfo(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new ResultInfo(false, "增加失败");
		}
	}
	*/

    /**
     * 修改
     *
     * @param goods
     * @return
     */
    @RequestMapping("/update")
    public ResultInfo update(@RequestBody Goods goods) {
        try {
            goodsService.update(goods);
            return new ResultInfo(true, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultInfo(false, "修改失败");
        }
    }

    /**
     * 获取实体
     *
     * @param id
     * @return
     */
    @RequestMapping("/findOne")
    public Goods findOne(Long id) {
        return goodsService.findOne(id);
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @RequestMapping("/delete")
    public ResultInfo delete(final Long[] ids) {
        try {
            goodsService.delete(ids);
            //从索引库中删除
            //itemSearchService.deleteByGoodsIds(Arrays.asList(ids));

            jmsTemplate.send(queueSolrDeleteDestination, new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    return session.createObjectMessage(ids);
                }
            });

            //批量删除每个服务器上生成的商品的详情页
            jmsTemplate.send(topicPageDeleteDestination, new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    return session.createObjectMessage(ids);
                }
            });

            return new ResultInfo(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultInfo(false, "删除失败");
        }
    }

    /**
     * 查询+分页
     *
     * @param goods
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("/search")
    public PageResult search(@RequestBody TbGoods goods, int page, int rows) {
        return goodsService.findPage(goods, page, rows);
    }

    /**
     * 批量审核商品spu
     *
     * @param ids
     * @param status
     * @return
     */
    @RequestMapping("/updateStatus")
    public ResultInfo updateStatus(final Long[] ids, String status) {
        try {
            goodsService.updateStatus(ids, status);
            //按照spu id查询 sku列表(状态为1)
            if ("1".equals(status)) {//如果是审核通过
                //得到需要导入的sku的列表
                List<TbItem> itemList = goodsService.findItemListByGoodsIdandStatus(ids, status);
                if (itemList.size() > 0) {
                    //导入到solr
                    //itemSearchService.importList(itemList);

                    final String jsonString = JSON.toJSONString(itemList);//转换为json 传输
                    jmsTemplate.send(queueSolrDestination, new MessageCreator() {
                        @Override
                        public Message createMessage(Session session) throws JMSException {
                            return session.createTextMessage(jsonString);
                        }
                    });
                } else {
                    System.out.println("没有sku明细数据");
                }
                //静态页生成
                //for(Long goodsId:ids){
                //itemPageService.genItemHtml(goodsId);
                //}

                //静态网页生成
                jmsTemplate.send(topicPageDestination, new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        return session.createObjectMessage(ids);
                    }
                });

            }
            return new ResultInfo(true, "修改状态成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultInfo(false, "修改状态失败");
        }
    }

    @RequestMapping("/genHtml")
    public void genHtml(Long goodsId) {
        //itemPageService.genItemHtml(goodsId);

    }

}
