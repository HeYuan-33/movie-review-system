package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 添加img目录的静态资源路径
        registry.addResourceHandler("/img/**")
                .addResourceLocations("classpath:/static/img/", "file:src/main/resources/static/img/")
                .setCachePeriod(3600)
                .resourceChain(true);
                
        // 添加默认的静态资源路径
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(3600)
                .resourceChain(true);
    }
} 