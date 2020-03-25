package com.chungyu.miniapp.userCenter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @Author Vincentxin
 * @Date 2020/3/25
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserInfo extends Model<UserInfo> {

    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 微信名称
     */
    private String nicName;
    /**
     * 性别 0-未知；1-男性；2-女性
     */
    private String gender;
    /**
     * 城市
     */
    private String city;
    /**
     * 省份
     */
    private String province;
    /**
     * 国家
     */
    private String country;
    /**
     * 头像url
     */
    private String avatarUrl;
    /**
     * 微信小程序openid
     */
    private String wxOpenid;
    /**
     * 微信公众平台统一标示
     */
    private String wxUnionid;
    /**
     * 用户电话号
     */
    private String phone;
    /**
     * 创建用户时间
     */
    private Date createTime;
    /**
     * 删除状态(0-未删除；1-删除)
     */
    @TableLogic
    private String isDelete;
    /**
     * 答题状态(0-未答题；1-已答题)
     */
    private int isAnswer;


}
