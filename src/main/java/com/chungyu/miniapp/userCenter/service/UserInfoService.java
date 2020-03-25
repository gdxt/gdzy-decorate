package com.chungyu.miniapp.userCenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chungyu.miniapp.userCenter.entity.UserInfo;
import com.chungyu.miniapp.util.ApiResult;

import java.util.Map;

/**
 * @Author Vincentxin
 * @Date 2020/3/25
 */
public interface UserInfoService extends IService<UserInfo> {

    /**
     *  注册用户信息
     * @Author Vincentxin
     * @Date 2020/3/25 16:46
     * @param secret
     * @return com.chungyu.miniapp.util.ApiResult
     **/
    ApiResult saveUserInfo(Map<String,String> secret);




}
