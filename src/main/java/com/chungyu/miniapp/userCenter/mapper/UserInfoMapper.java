package com.chungyu.miniapp.userCenter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chungyu.miniapp.userCenter.entity.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @Author Vincentxin
 * @Date 2020/3/25
 */
@Mapper
@Repository
public interface UserInfoMapper extends BaseMapper<UserInfo> {

}
