package com.chungyu.miniapp.config;




import com.chungyu.miniapp.intercepter.AuthInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AuthConfig implements WebMvcConfigurer {

    //注入功能过滤的拦截器
    @Bean
    public AuthInterceptor configDIAndAuthInterceptor(){
        return  new AuthInterceptor();
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        /**
         * 添加功能拦截器
         */
        //增加功能拦截器
        InterceptorRegistration addInterceptorForFunction = registry.addInterceptor(configDIAndAuthInterceptor());
        // 排除配置
        addInterceptorForFunction.excludePathPatterns("/error");
        addInterceptorForFunction.excludePathPatterns("/register");
        addInterceptorForFunction.excludePathPatterns("/login");
        //拦截所有地址
        addInterceptorForFunction.addPathPatterns("/**");

    }
}
