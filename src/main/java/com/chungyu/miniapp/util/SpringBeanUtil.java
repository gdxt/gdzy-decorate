package com.chungyu.miniapp.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

/**
 * @Author Vincentxin
 * @Date 2019-05-21
 *
 */
@Configuration
public  class SpringBeanUtil implements ApplicationContextAware {

    private static ApplicationContext ctx;

    public static Object getBean(String id) {
        if (ctx == null) {
            throw new NullPointerException("ApplicationContext is null");
        }
        return ctx.getBean(id);
    }
    public static Object getBean(Class c) {
        if (ctx == null) {
            throw new NullPointerException("ApplicationContext is null");
        }
        return ctx.getBean(c);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ctx = applicationContext;
    }

    }
