package com.pyg.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.entity.PageResult;
import com.pyg.entity.ResultInfo;
import com.pyg.pojo.TbBrand;
import com.pyg.sellergoods.service.BrandService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("brand")
public class BrandController {
    /*
        通过接口的全类名去远程的服务找对应的接口实现并调用方法
        ，而这里得到的接口实现是一个本地的代理对象，有代理对象的方法去完成远程方法调用（通过Socket）
     */
    @Reference
    private BrandService brandService;

    /**
     * 查询所有
     *
     * @return
     */
    @RequestMapping("findAll")
    public List<TbBrand> findAll() {

        return brandService.findAll();
    }

    /**
     * 分页返回全部的列表
     *
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("/findPage")
    public PageResult findPage(int page, int rows) {
        return brandService.findPage(page, rows);
    }

    /**
     * 添加品牌
     *
     * @param brand
     */
    @RequestMapping("/add")
    public ResultInfo add(@RequestBody TbBrand brand) {
        try {
            brandService.add(brand);
            return new ResultInfo(true, "添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultInfo(false, "添加失败");
        }
    }

    /**
     * 修改品牌
     *
     * @param brand
     * @return
     */
    @RequestMapping("/update")
    public ResultInfo update(@RequestBody TbBrand brand) {
        try {
            brandService.update(brand);
            return new ResultInfo(true, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultInfo(false, "修改失败");
        }
    }

    /**
     * 根据id查单个品牌的信息
     *
     * @param id
     * @return
     */
    @RequestMapping("/findOne")
    public TbBrand findOne(long id) {
        return brandService.findOne(id);
    }

    @RequestMapping("/delete")
    public ResultInfo delete(Long[] ids) {
        try {
            brandService.delete(ids);
            return new ResultInfo(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultInfo(false, "删除失败");
        }
    }

    /**
     * 条件查询+分页
     *
     * @param brand
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("/search")
    public PageResult search(@RequestBody TbBrand brand, int page, int rows) {
        return brandService.findPage(brand, page, rows);
    }

    /**
     * 查询下拉列表数据
     *
     * @return
     */
    @RequestMapping("/selectOptionList")
    public List<Map> selectOptionList() {
        return brandService.selectOptionList();
    }
}
