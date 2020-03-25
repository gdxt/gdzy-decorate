package com.chungyu.miniapp.intercepter;


import cn.hutool.core.util.StrUtil;
import com.chungyu.miniapp.constants.CommonConstants;
import com.chungyu.miniapp.constants.ConfigContextHandler;
import com.chungyu.miniapp.constants.JwtUtils;
import com.chungyu.miniapp.constants.UserToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 功能开启关闭限制
 * @Author Vincentxin
 * @Date 2019-06-24
 */
@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {
    private Logger logger = LoggerFactory.getLogger(AuthInterceptor.class);
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader(CommonConstants.CONTEXT_TOKEN);
        if(StrUtil.isEmpty(token)){
            return true;
        }
        UserToken userToken = JwtUtils.getInfoFromToken(token);
        ConfigContextHandler.setToken(token);
        //logger.info("------设置token"+Thread.currentThread().getId());
        ConfigContextHandler.setUsername(userToken.getUsername());
        ConfigContextHandler.setName(userToken.getName());
        ConfigContextHandler.setUserID(userToken.getUserId());
        return super.preHandle(request, response, handler);
    }
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 清空缓存
        ConfigContextHandler.remove();
        super.afterCompletion(request, response, handler, ex);
    }

}
