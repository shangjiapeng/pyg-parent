package com.pyg.service;


import com.pyg.pojo.TbSeller;
import com.pyg.sellergoods.service.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by crowndint on 2018/10/15.
 */
//@Component("userDetailService")
public class UserDetailServiceImpl implements UserDetailsService {
    //@Reference
    private SellerService sellerService;

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //提交表单进行登录的时候，根据提交的username-parameter指定的参数值,从数据库查询TbSeller对象
        List<GrantedAuthority> authorities = new ArrayList<>();
        //给当前用户ROLE_SELLER权限
        authorities.add(new SimpleGrantedAuthority("ROLE_SELLER"));
        //得到商家对象
        TbSeller seller = sellerService.findOne(username);
        if (username != null) {
            //只有审核通过才去校验密是否正确
            if ("1".equals(seller.getStatus())) {
                //这里返回的User对象的密码会和表单提交的密码进行比对
                return new User(seller.getSellerId(), seller.getPassword(), authorities);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}
