package com.pyg.solrutil;

import com.alibaba.fastjson.JSON;
import com.pyg.mapper.TbItemMapper;
import com.pyg.pojo.TbItem;
import com.pyg.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SolrUtil {
    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private SolrTemplate solrTemplate;

    public void importItemData(){
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1");//必须是审核通过的才导入
        List<TbItem> itemList = itemMapper.selectByExample(example);
        System.out.println("--商品列表--");
        for (TbItem item : itemList) {
            System.out.println(item.getId()+" "+item.getTitle()+" "+item.getPrice());
            //动态域
            Map specMap = JSON.parseObject(item.getSpec(), Map.class);//从数据库中提取:规格json字符串转换为map
            item.setSpecMap(specMap);
        }
        //数据批量导入solr
        solrTemplate.saveBeans(itemList);
        solrTemplate.commit();
        System.out.println("--结束--");
    }

    public static void main(String[] args){
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
        SolrUtil solrUtil = (SolrUtil) context.getBean("solrUtil");
        solrUtil.importItemData();
    }
}
