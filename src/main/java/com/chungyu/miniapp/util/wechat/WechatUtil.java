package com.chungyu.miniapp.util.wechat;

import cn.hutool.core.util.StrUtil;
import com.chungyu.miniapp.util.OkHttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ValueOperations;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.AlgorithmParameters;
import java.security.Security;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author Vincentxin
 * @Date 2020/3/25
 */
public class WechatUtil {
    private Logger logger = LoggerFactory.getLogger(WechatServiceImpl.class);
    @Autowired
    private ValueOperations redisUtils;

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


        //根据前端传递的唯一标识和类型获取配置文件数据
//        SysconfigPlatformInfoDO sysconfigPlatformInfoDO = ConfigContextHandler.getSysconfigInfo();
        //TODO 组装小程序参数信息
        try {
            String appid = sysconfigPlatformInfoDO.getAppId();
            String secret = sysconfigPlatformInfoDO.getAppSecret();
            Map<String, String> jsonParams = new HashMap<>();
            jsonParams.put("appid", appid);
            jsonParams.put("secret", secret);
            jsonParams.put("js_code", code);
            jsonParams.put("grant_type", "authorization_code");
            String resParams = OkHttpUtils.get(WechatUrlConstants.WECHAT_MINI_OPENID_SESSIONID_URL, jsonParams);
            Map<String, String> reqMap = JSON.parseObject(resParams, Map.class);
            logger.info("微信通过code 获取用户openId和SessionKey  reqMap=====" + reqMap);
            if (reqMap == null || StringUtils.isNotEmpty(reqMap.get("errcode"))) {
                logger.error("微信通过code 获取用户openId和SessionKey失败");
                return null;
            } else {
                logger.info("微信通过code 获取用户小程序的openId和SessionKey结果", reqMap);
                return reqMap;
            }
        } catch (Exception e) {
            logger.info("微信通过code 获取用户openId和SessionKey失败，失败原因:{}", e.getMessage());
            return null;
        }
    }


    /**
     * accessToken小程序
     *
     * @param
     * @return
     */
    public String getProgramAccessToken() {
        String smallroutineaccessToken = RedisConstants.WX_SMALLROUTINE_ACCESSTOKEN;
        Object accessTokenObj = redisUtils.get(smallroutineaccessToken);
        String accessToken = accessTokenObj != null ? accessTokenObj.toString() : null;
        if (StringUtils.isNotEmpty(accessToken)) {
//            logger.info("小程序accessToken为空");
            return accessToken;
        }
        logger.info("=====START===微信小程序获取accessToken=====");
        SysconfigPlatformInfoDO sysconfigPlatformInfoDO = ConfigContextHandler.getSysconfigInfo();
        String appid = sysconfigPlatformInfoDO.getAppId();
        String secret = sysconfigPlatformInfoDO.getAppSecret();
        Map<String, String> jsonParams = new HashMap<>();
        jsonParams.put("grant_type", "client_credential");
        jsonParams.put("appid", appid);
        jsonParams.put("secret", secret);
        try {
            String resParams = OkHttpUtils.get(WechatUrlConstants.WECHAT_ACCESSTOKEN_URL, jsonParams);
            Map<String, String> reqMap = JSON.parseObject(resParams, Map.class);
            if (reqMap != null && StringUtils.isNotEmpty(reqMap.get("access_token"))) {
                accessToken = reqMap.get("access_token");
//                String expiresIn = reqMap.get("expires_in");
                //放入redis
                redisUtils.setKey(RedisConstants.WX_SMALLROUTINE_ACCESSTOKEN, accessToken, 7100L);
            } else {
                logger.error("微信小程序获取accessToken失败，原因:{}", reqMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return accessToken;
    }

    public Map<String,String> decryptData(String encryptedData, String iv, String code) {
        if (StringUtils.isEmpty(code)) {
            logger.error("参数:code, 不能为空");
            return null;
        }
        logger.info("前端传递的参数:code:{}", code);
        Map<String, String> reqMap = wechatService.getOpenidAndSessionKeyByCode(code);
        if (reqMap == null) {
            logger.error("参数:reqMap, 不能为空");
            return null;
        }
        String sessionKey = reqMap.get("session_key");
        logger.info("解密信息：{},偏移量：{},sessionKey:{}", encryptedData, iv, sessionKey);
        // 对称解密秘钥 aeskey = Base64_Decode(session_key), aeskey 是16字节。
        byte[] aesKey = Base64.decodeBase64(sessionKey);
//        logger.info("对称解密之后的sessionKey:{}", aesKey);
        // 对称解密算法初始向量 为Base64_Decode(iv)，其中iv由数据接口返回。
        byte[] aesIV = Base64.decodeBase64(iv);
//        logger.info("对称解密算法初始化向量：{}", iv);
        byte[] aesCipher = Base64.decodeBase64(encryptedData);
//        logger.info("对称解密的目标密文：{}", aesCipher);
        Map<String,String> resultMap = new HashMap<>();
        try {
            String  result = AESDecrypted(aesCipher, aesIV, aesKey);
            logger.info("解密之后的信息为：{}", result);
            resultMap = JSON.parseObject(result, Map.class);
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


    public String AESDecrypted(byte[] encryptedDataBytes, byte[] ivBytes, byte[] sessionKeyBytes) throws Exception {
        //对SessionKey进行补位
        sessionKeyBytes = fullByte(sessionKeyBytes);

//        logger.info("对称解密之后的sessionKey进行补位（16位）:{}", sessionKeyBytes);
        Security.addProvider(new BouncyCastleProvider());
        //密码实例对象构建
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
        //解密key对象创建实例化
        SecretKeySpec spec = new SecretKeySpec(sessionKeyBytes, "AES");

        //运算规则实例对象构建
        AlgorithmParameters parameters = AlgorithmParameters.getInstance("AES");
        //运算规则解密初始向量初始化
        parameters.init(new IvParameterSpec(ivBytes));

        //初始化密码对象
        cipher.init(Cipher.DECRYPT_MODE, spec, parameters);
        byte[] resultByte = cipher.doFinal(encryptedDataBytes);
        if (null != resultByte && resultByte.length > 0) {
            return new String(resultByte, "UTF-8");
        }
        return null;
    }

    public static byte[] fullByte(byte[] byteArr) {
        int base = 16;
        if (byteArr.length % base != 0) {
            int groups = byteArr.length / base + (byteArr.length % base != 0 ? 1 : 0);
            byte[] temp = new byte[groups * base];
            Arrays.fill(temp, (byte) 0);
            System.arraycopy(byteArr, 0, temp, 0, byteArr.length);
            byteArr = temp;
        }
        return byteArr;
    }

    /**
     * RedisAccessToken失效（过期是超出了有效期的时间，失效是没有超出有效期的）
     */
    public String redisAccessTokenRedisExpired() {
        logger.info("=====START===微信公众号获取accessToken=====");
        Map<String, String> reqMap = null;
        String accessToken = null;
        try {
            SysconfigPlatformInfoDO sysconfigPlatformInfoDO = ConfigContextHandler.getSysconfigInfo();
            String appid = sysconfigPlatformInfoDO.getAppId();
            String secret = sysconfigPlatformInfoDO.getAppSecret();
            Map<String, String> jsonParams = new HashMap<>();
            jsonParams.put("grant_type", "client_credential");
            jsonParams.put("appid", appid);
            jsonParams.put("secret", secret);
            String resParams = OkHttpUtils.get(WechatUrlConstants.WECHAT_ACCESSTOKEN_URL, jsonParams);
            reqMap = JSON.parseObject(resParams, Map.class);
            if (reqMap != null && StringUtils.isNotEmpty(reqMap.get("access_token"))) {
                accessToken = reqMap.get("access_token");
            } else {
                logger.error("微信公众号获取accessToken失败，原因:{}", reqMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        redisUtils.delKeyCache(RedisConstants.WX_PUBLICACCOUNT_ACCESSTOKEN);
        redisUtils.setKey(RedisConstants.WX_PUBLICACCOUNT_ACCESSTOKEN, accessToken, 7100L);
        return accessToken;
    }
}
