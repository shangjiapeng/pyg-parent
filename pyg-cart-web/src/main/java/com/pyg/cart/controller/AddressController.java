package com.pyg.cart.controller;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.pojo.TbAddress;
import com.pyg.user.service.AddressService;

import com.pyg.entity.PageResult;
import com.pyg.entity.ResultInfo;
/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/address")
public class AddressController {

	@Reference
	private AddressService addressService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbAddress> findAll(){			
		return addressService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return addressService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param newAddress
	 * @return
	 */
	@RequestMapping("/add")
	public ResultInfo add(@RequestBody TbAddress newAddress){
		String userId = SecurityContextHolder.getContext().getAuthentication().getName();
		newAddress.setUserId(userId);
		try {
			addressService.add(newAddress);
			return new ResultInfo(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new ResultInfo(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param address
	 * @return
	 */
	@RequestMapping("/update")
	public ResultInfo update(@RequestBody TbAddress address){
		try {
			addressService.update(address);
			return new ResultInfo(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new ResultInfo(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public TbAddress findOne(Long id){
		return addressService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public ResultInfo delete(Long [] ids){
		try {
			addressService.delete(ids);
			return new ResultInfo(true, "删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new ResultInfo(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param address
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbAddress address, int page, int rows  ){
		return addressService.findPage(address, page, rows);		
	}

	/**
	 * 使用已经登录的用户的id查询该用户的收货地址
	 * @return addressList
	 */
	@RequestMapping("/findListByLoginUser")
	public List<TbAddress> findListByLoginUser(){
		//获取登陆的用户
		String userId = SecurityContextHolder.getContext().getAuthentication().getName();
		List<TbAddress> addressList = addressService.findAddressListByUserId(userId);
		return  addressList;
	}
	
}
