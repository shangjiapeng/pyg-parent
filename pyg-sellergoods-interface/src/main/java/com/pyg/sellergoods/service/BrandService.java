package com.pyg.sellergoods.service;

import com.pyg.entity.PageResult;
import com.pyg.pojo.TbBrand;

import java.util.List;
import java.util.Map;

/**
 * Created by crowndint on 2019/1/20.
 */
public interface BrandService {
    /**
     * 查询所有的品牌
     * @return
     */
    public List<TbBrand> findAll();

    /**
     * 返回分页列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    public PageResult findPage(int pageNum, int pageSize);

    /**
     *添加品牌
     * @param brand
     */
    public void add(TbBrand brand);

    /**
     * 修改
     * @param brand
     */
    public void update(TbBrand brand);

    /**
     * 根据id获取实体数据
     * @param id
     * @return
     */
    public TbBrand findOne(Long id);

    /**
     * 批量删除
     * @param ids
     */
    public void delete(Long[] ids);

    /**
     * 品牌条件分页查询
     * @param brand 品牌
     * @param pageNum 当前页码
     * @param pageSize 每页显示的条数
     * @return
     */
    public PageResult findPage(TbBrand brand,int pageNum, int pageSize);

    /**
     * 返回下拉列表数据
     * @return
     */
    public List<Map> selectOptionList();

}
