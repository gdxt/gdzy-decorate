package com.chungyu.miniapp.userCenter.service.iml;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chungyu.miniapp.userCenter.entity.UserInfo;
import com.chungyu.miniapp.userCenter.mapper.UserInfoMapper;
import com.chungyu.miniapp.userCenter.service.UserInfoService;
import com.chungyu.miniapp.util.ApiResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @Author Vincentxin
 * @Date 2020/3/25
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {
    @Autowired
    private UserInfoMapper  userInfoMapper;

    @Override
    public ApiResult saveUserInfo(Map<String, String> secret) {
        //TODO 用户信息
        // 解密手机号
        // 落库用户信息
        // 组建token
        // 返回用户token信息封装
        return null;
    }
}
