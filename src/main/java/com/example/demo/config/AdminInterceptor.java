package com.example.demo.config;

import com.example.demo.model.User;
import com.example.demo.model.Role;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class AdminInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        User admin = (User) request.getSession().getAttribute("admin");
        if (admin == null || !Role.ADMIN.equals(admin.getRole())) {
            response.sendRedirect("/admin/login");
            return false;
        }
        return true;
    }
} 