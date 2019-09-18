package com.shang.test;

import com.shang.pojo.TbItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext-solr.xml")
public class TestTemplate {

    @Autowired
    private SolrTemplate solrTemplate;

    //添加
    @Test
    public void testAdd() {
        TbItem item = new TbItem();
        item.setId(1L);
        item.setBrand("华为");
        item.setCategory("手机");
        item.setGoodsId(10L);
        item.setSeller("华为京东自营专卖店");
        item.setTitle("华为mate20");
        item.setPrice(new BigDecimal(2000.01));
        solrTemplate.saveBean(item);
        solrTemplate.commit();
    }

    //查询
    @Test
    public void findById() {
        TbItem item = solrTemplate.getById(1L, TbItem.class);
        System.out.println(item.getTitle());
    }

    //删除
    @Test
    public void deleteById() {
        solrTemplate.deleteById("1");
        solrTemplate.commit();
    }

    //添加集合
    @Test
    public void addList() {
        List list = new ArrayList();
        for (int i = 0; i < 100; i++) {
            TbItem item = new TbItem();
            item.setId(i + 1L);
            item.setBrand("华为" + i);
            item.setCategory("手机");
            item.setGoodsId(10L);
            item.setSeller("华为京东自营专卖店");
            item.setTitle("华为mate" + i);
            item.setPrice(new BigDecimal(2000.01 + i));
            list.add(item);
        }
        solrTemplate.saveBeans(list);
        solrTemplate.commit();
    }

    //分页查询
    @Test
    public void queryPage() {
        Query query = new SimpleQuery("*:*");
        query.setOffset(3);//开始的索引
        query.setRows(20);//每页的记录数
        ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);
        for (TbItem item : page.getContent()) {
            System.out.println(item.getTitle() + " " + item.getPrice() + " " + item.getBrand());
        }
        System.out.println("总记录数" + page.getTotalElements());
        System.out.println("总页数" + page.getTotalPages());
    }

    //条件查询
    @Test
    public void queryPageMutil() {
        Query query = new SimpleQuery("*:*");
        Criteria criteria = new Criteria("item_category").contains("手机");
        criteria=criteria.and("item_brand").contains("2");
        query.addCriteria(criteria);
        query.setOffset(0);//开始的索引
        query.setRows(20);//每页的记录数
        ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);
        for (TbItem item : page.getContent()) {
            System.out.println(item.getTitle() + " " + item.getPrice() + " " + item.getBrand());
        }
        System.out.println("总记录数" + page.getTotalElements());
        System.out.println("总页数" + page.getTotalPages());
    }

    //全部删除
    @Test
    public void deleteAll(){
        Query query = new SimpleQuery("*:*");
        solrTemplate.delete(query);
        solrTemplate.commit();
    }

}
