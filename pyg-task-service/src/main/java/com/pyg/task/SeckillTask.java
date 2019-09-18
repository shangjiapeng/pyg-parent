package com.pyg.task;

import com.pyg.mapper.TbSeckillGoodsMapper;
import com.pyg.pojo.TbSeckillGoods;
import com.pyg.pojo.TbSeckillGoodsExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class SeckillTask {

    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    public RedisTemplate redisTemplate;

    /**
     * 刷新秒杀商品
     */
    @Scheduled(cron = "0 * * * * ?")
    public void refreshSeckillGoods() {
        System.out.println("执行了秒杀商品的增量更新,任务调度" + new Date());
        //查询所有的秒杀商品键集合
        List goodsIdList = new ArrayList(redisTemplate.boundHashOps("seckillGoods").keys());

        //从数据库中查询秒杀商品列表
        TbSeckillGoodsExample example = new TbSeckillGoodsExample();
        TbSeckillGoodsExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1");//审核通过的
        criteria.andStockCountGreaterThan(0);//剩余库存>0
        criteria.andStartTimeLessThanOrEqualTo(new Date());//开始时间小于等于当前时间
        criteria.andEndTimeGreaterThan(new Date());//结束的时间大于当前时间
        if (goodsIdList.size() > 0) {
            criteria.andIdNotIn(goodsIdList);//排除缓存中已经存在的id集合
        }
        List<TbSeckillGoods> seckillGoodsList = seckillGoodsMapper.selectByExample(example);
        //将查询出来的列表装入缓存
        for (TbSeckillGoods seckillGoods : seckillGoodsList) {
            redisTemplate.boundHashOps("seckillGoods").put(seckillGoods.getId(), seckillGoods);
            System.out.println("增量更新秒杀商品id:" + seckillGoods.getId());
        }
    }

    /**
     * 移除秒杀商品
     */
    @Scheduled(cron="* * * * * ?")
    public void removeSeckillGoods(){
        System.out.println("移除秒杀商品任务在执行");
        //扫描缓存中秒杀商品列表，发现过期的移除
        List<TbSeckillGoods> seckillGoodsList = redisTemplate.boundHashOps("seckillGoods").values();
        for( TbSeckillGoods seckillGoods :seckillGoodsList ){
            if(seckillGoods.getEndTime().getTime()<new Date().getTime()  ){//如果结束日期
                seckillGoodsMapper.updateByPrimaryKey(seckillGoods);//向数据库保存记录
                redisTemplate.boundHashOps("seckillGoods").delete(seckillGoods.getId());//移除缓存数
                System.out.println("移除秒杀商品"+seckillGoods.getId());
            }
        }
        System.out.println("移除秒杀商品任务结束");
    }

}

