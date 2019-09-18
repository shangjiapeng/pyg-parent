package com.pyg.seckill.service.impl;

import java.util.Date;
import java.util.List;

import com.pyg.entity.PageResult;
import com.pyg.mapper.TbSeckillGoodsMapper;
import com.pyg.mapper.TbSeckillOrderMapper;
import com.pyg.pojo.TbSeckillGoods;
import com.pyg.util.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pyg.pojo.TbSeckillOrder;
import com.pyg.pojo.TbSeckillOrderExample;
import com.pyg.pojo.TbSeckillOrderExample.Criteria;
import com.pyg.seckill.service.SeckillOrderService;
import org.springframework.data.redis.core.RedisTemplate;


/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {

    @Autowired
    private TbSeckillOrderMapper seckillOrderMapper;

    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IdWorker idWorker;

    /**
     * 查询全部
     */
    @Override
    public List<TbSeckillOrder> findAll() {
        return seckillOrderMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbSeckillOrder> page = (Page<TbSeckillOrder>) seckillOrderMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(TbSeckillOrder seckillOrder) {
        seckillOrderMapper.insert(seckillOrder);
    }


    /**
     * 修改
     */
    @Override
    public void update(TbSeckillOrder seckillOrder) {
        seckillOrderMapper.updateByPrimaryKey(seckillOrder);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TbSeckillOrder findOne(Long id) {
        return seckillOrderMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            seckillOrderMapper.deleteByPrimaryKey(id);
        }
    }


    @Override
    public PageResult findPage(TbSeckillOrder seckillOrder, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbSeckillOrderExample example = new TbSeckillOrderExample();
        Criteria criteria = example.createCriteria();

        if (seckillOrder != null) {
            if (seckillOrder.getUserId() != null && seckillOrder.getUserId().length() > 0) {
                criteria.andUserIdLike("%" + seckillOrder.getUserId() + "%");
            }
            if (seckillOrder.getSellerId() != null && seckillOrder.getSellerId().length() > 0) {
                criteria.andSellerIdLike("%" + seckillOrder.getSellerId() + "%");
            }
            if (seckillOrder.getStatus() != null && seckillOrder.getStatus().length() > 0) {
                criteria.andStatusLike("%" + seckillOrder.getStatus() + "%");
            }
            if (seckillOrder.getReceiverAddress() != null && seckillOrder.getReceiverAddress().length() > 0) {
                criteria.andReceiverAddressLike("%" + seckillOrder.getReceiverAddress() + "%");
            }
            if (seckillOrder.getReceiverMobile() != null && seckillOrder.getReceiverMobile().length() > 0) {
                criteria.andReceiverMobileLike("%" + seckillOrder.getReceiverMobile() + "%");
            }
            if (seckillOrder.getReceiver() != null && seckillOrder.getReceiver().length() > 0) {
                criteria.andReceiverLike("%" + seckillOrder.getReceiver() + "%");
            }
            if (seckillOrder.getTransactionId() != null && seckillOrder.getTransactionId().length() > 0) {
                criteria.andTransactionIdLike("%" + seckillOrder.getTransactionId() + "%");
            }

        }

        Page<TbSeckillOrder> page = (Page<TbSeckillOrder>) seckillOrderMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 秒杀下单
     *
     * @param seckillId
     * @param userId
     */
    @Override
    public void submitOrder(Long seckillId, String userId) {
        //从缓存redis中读数据
        TbSeckillGoods seckillGoods=null;
        try {
            seckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(seckillId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (seckillGoods==null){
            throw new RuntimeException("商品不存在");
        }

        if (seckillGoods.getStockCount()<=0){
            throw new RuntimeException("商品已经被抢完了");
        }
        //如果还有至少一个库存
        //扣减redis库存
        seckillGoods.setStockCount(seckillGoods.getStockCount()-1);
        redisTemplate.boundHashOps("seckillGoods").put(seckillId,seckillGoods);//更新缓存
        if (seckillGoods.getStockCount()==0){//最后一个也卖完了
            seckillGoodsMapper.updateByPrimaryKey(seckillGoods);//同步数据到数据库
            try {
                redisTemplate.boundHashOps("seckillGoods").delete(seckillId);
                System.out.println("商品信息同步到数据库...");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //保存(redis)订单
        long orderId= idWorker.nextId();
        TbSeckillOrder seckillOrder = new TbSeckillOrder();
        seckillOrder.setId(orderId);//秒杀订单号
        seckillOrder.setCreateTime(new Date());
        seckillOrder.setMoney(seckillGoods.getCostPrice());//秒杀价格
        seckillOrder.setSeckillId(seckillId);//秒杀商品id
        seckillOrder.setSellerId(seckillGoods.getSellerId());//商家id
        seckillOrder.setUserId(userId);//用户id
        seckillOrder.setStatus("0");//秒杀订单状态
        try {
            redisTemplate.boundHashOps("seckillOrder").put(userId,seckillOrder);
            System.out.println("保存订单成功(redis)...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据用户名查询秒杀订单(redis)
     *
     * @param userId
     * @return
     */
    @Override
    public TbSeckillOrder searchOrderFromRedisByUserId(String userId) {
        return (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);
    }


    /**
     * 支付成功保存订单
     *
     * @param userId
     * @param orderId
     * @param transactionId
     */
    @Override
    public void saveOrderFromRedisToDb(String userId, Long orderId, String transactionId) {
        System.out.println("saveOrderFromRedisToDb:"+userId);
        //从缓存中提取订单数据
        TbSeckillOrder seckillOrder= searchOrderFromRedisByUserId(userId);
        if (seckillOrder==null){
            throw new RuntimeException("订单不存在");
        }
        //如果传递过来的订单号不符
        if (seckillOrder.getId().longValue()!=orderId.longValue()){
            throw new RuntimeException("订单不相符");
        }
        //修改订单状态
        seckillOrder.setTransactionId(transactionId);//交易流水号
        seckillOrder.setPayTime(new Date());//支付时间
        seckillOrder.setStatus("1");//状态
        //将订单存入数据库
        seckillOrderMapper.insert(seckillOrder);//保存订单到数据库

        //清楚缓存中订单
        redisTemplate.boundHashOps("seckillOrder").delete(userId);//从redis中清除
    }


    /**
     * 从缓存中删除订单
     *(超时未支付时,调用)
     * @param userId
     * @param orderId
     */
    @Override
    public void deleteOrderFromRedis(String userId, Long orderId) {
        //从缓存中提取订单数据
        TbSeckillOrder seckillOrder= searchOrderFromRedisByUserId(userId);
        if (seckillOrder!=null&&seckillOrder.getId().longValue()==orderId.longValue()){
            redisTemplate.boundHashOps("seckillOrder").delete(userId);//删除缓存中的订单
            //恢复库存
            //从缓存中提取秒杀商品
            TbSeckillGoods seckillGoods= (TbSeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(seckillOrder.getSeckillId());
            if (seckillGoods!=null){//如果不为空
                seckillGoods.setStockCount(seckillGoods.getStockCount()+1);
                redisTemplate.boundHashOps("seckillGoods").put(seckillOrder.getSeckillId(),seckillGoods);//更新缓存
            }else {//如果为空,有可能商品,已经被秒完了
                seckillGoods = new TbSeckillGoods();
                //重新设置秒杀商品的属性....
                seckillGoods.setId(seckillOrder.getSeckillId());
                seckillGoods.setStockCount(1);
                redisTemplate.boundHashOps("seckillGoods").put(seckillOrder.getSeckillId(),seckillGoods);//更新缓存
            }
            System.out.println("订单取消"+orderId);
        }
    }

}
