package com.chungyu.miniapp.util.wechat;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.chungyu.miniapp.util.AES;
import com.chungyu.miniapp.util.OkHttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author Vincentxin
 * @Date 2020/3/25
 */
@Component
public class WechatUtil {
    private Logger logger = LoggerFactory.getLogger(WechatUtil.class);
    @Autowired
    private ValueOperations redisUtils;

    @Value("${wechat.appid}")
    private String appid;
    @Value("${wechat.secret}")
    private String secret;
    @Value("${wechat.code2sessionUrl}")
    private String code2sessionUrl;
    /**
     * 通过code获取小程序的openID和sessionKey
     *
     * @param code
     * @return
     * @Author m
     */
    public Map<String, String> getOpenidAndSessionKeyByCode(String code) {
        logger.info("=====START===开始获取小程序的OpenID和SessionKey=====");
        if (StrUtil.isEmpty(code)) {
            logger.error("参数:code, 不能为空");
            return null;
        }
        logger.info("前端传递的参数:code:{}", code);
        try {
            Map<String, String> jsonParams = new HashMap<>();
            jsonParams.put("appid", appid);
            jsonParams.put("secret", secret);
            jsonParams.put("js_code", code);
            jsonParams.put("grant_type", "authorization_code");
            String resParams = OkHttpUtils.get(code2sessionUrl, jsonParams);
            JSONObject reqMap = JSONUtil.parseObj(resParams);
            logger.info("微信通过code 获取用户openId和SessionKey  reqMap=====" + reqMap);
            if (reqMap == null || StrUtil.isNotEmpty((String)reqMap.get("errcode"))) {
                logger.error("微信通过code 获取用户openId和SessionKey失败");
                return null;
            } else {
                logger.info("微信通过code 获取用户小程序的openId和SessionKey结果", reqMap);
                return Convert.toMap(String.class,String.class,reqMap);
            }
        } catch (Exception e) {
            logger.info("微信通过code 获取用户openId和SessionKey失败，失败原因:{}", e.getMessage());
            return null;
        }
    }

    /**
     * 解密手机号
     *
     * @Author Vincentxin
     * @Date 2020/3/25 21:21
     * @param encryptedData
     * @param iv
     * @param code
     * @return java.util.Map<java.lang.String,java.lang.String>
     **/

    public Map<String,String> decryptData(String encryptedData, String iv, String code) {
        if (StrUtil.isEmpty(code)) {
            logger.error("参数:code, 不能为空");
            return null;
        }
        logger.info("前端传递的参数:code:{}", code);
        Map<String, String> reqMap = getOpenidAndSessionKeyByCode(code);
        if (reqMap == null) {
            logger.error("参数:reqMap, 不能为空");
            return null;
        }
        String sessionKey = reqMap.get("session_key");
        logger.info("解密信息：{},偏移量：{},sessionKey:{}", encryptedData, iv, sessionKey);
        Map<String,String> resultMap = new HashMap<>();
        try {
            JSONObject result = AES.wxDecrypt(encryptedData, sessionKey, iv);
            logger.info("解密之后的信息为：{}", result);
            resultMap = Convert.convert(Map.class,result);
            resultMap.remove("watermark");
            resultMap.put("openid",reqMap.get("openid"));
            logger.info("解密信息转换Map后的信息：{}",resultMap);
        } catch (Exception e) {
            logger.error("解密信息异常:{}", e.getMessage());
            e.printStackTrace();
        } finally {
            return resultMap;
        }
    }

    public Map<String,String> decryptData(String encryptedData, String iv, Map<String,String> session) {
        String sessionKey = session.get("session_key");
        Map<String,String> resultMap = new HashMap<>();
        try {
            JSONObject result = AES.wxDecrypt(encryptedData, sessionKey, iv);
            logger.info("解密之后的信息为：{}", result);
            resultMap = Convert.convert(Map.class,result);
            resultMap.remove("watermark");
            resultMap.put("openid",session.get("openid"));
            logger.info("解密信息转换Map后的信息：{}",resultMap);
        } catch (Exception e) {
            logger.error("解密信息异常:{}", e.getMessage());
            e.printStackTrace();
        } finally {
            return resultMap;
        }
    }
}
