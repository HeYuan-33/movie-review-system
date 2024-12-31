package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.model.Role;
import com.example.demo.model.AccessLog;
import com.example.demo.service.UserService;
import com.example.demo.service.AccessLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private AccessLogService accessLogService;

    @GetMapping("/login")
    public String showLoginPage(HttpSession session) {
        User admin = (User) session.getAttribute("admin");
        if (admin != null && Role.ADMIN.equals(admin.getRole())) {
            return "redirect:/admin/dashboard";
        }
        return "admin/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                       @RequestParam String password,
                       HttpSession session,
                       HttpServletRequest request,
                       Model model) {
        try {
            logger.info("尝试管理员登录: {}", username);
            User user = userService.validateAndGetUser(username, password);
            
            if (user != null && Role.ADMIN.equals(user.getRole())) {
                session.setAttribute("admin", user);
                session.setAttribute("user", user);  // 同时设置user属性
                
                // 记录登录日志
                accessLogService.logAccess(request, user, "管理员登录成功");
                
                logger.info("管理员登录成功: {}", username);
                return "redirect:/admin/dashboard";
            }
            
            logger.warn("管理员登录失败: {} - 用户名或密码错误或不是管理员", username);
            model.addAttribute("error", "用户名或密码错误，或者该用户不是管理员");
            return "admin/login";
        } catch (Exception e) {
            logger.error("管理员登录异常: {} - {}", username, e.getMessage(), e);
            model.addAttribute("error", "登录失败：" + e.getMessage());
            return "admin/login";
        }
    }

    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model, HttpServletRequest request) {
        User admin = (User) session.getAttribute("admin");
        if (admin == null || !Role.ADMIN.equals(admin.getRole())) {
            return "redirect:/admin/login";
        }

        try {
            // 获取最近的访问日志
            List<AccessLog> recentLogs = accessLogService.getRecentLogs(10);
            // 获取今日访问量
            long todayVisits = accessLogService.getTodayVisitCount();
            // 获取今日活跃用户数
            long activeUsers = accessLogService.getTodayActiveUserCount();

            model.addAttribute("recentLogs", recentLogs);
            model.addAttribute("todayVisits", todayVisits);
            model.addAttribute("activeUsers", activeUsers);
            model.addAttribute("admin", admin);

            // 记录访问日志
            accessLogService.logAccess(request, admin, "访问管理员仪表板");

            return "admin/dashboard";
        } catch (Exception e) {
            logger.error("获取仪表板数据失败: {}", e.getMessage(), e);
            return "redirect:/admin/login";
        }
    }

    @GetMapping("/logs")
    public String showLogs(@RequestParam(required = false) String search,
                         HttpSession session,
                         Model model,
                         HttpServletRequest request) {
        User admin = (User) session.getAttribute("admin");
        if (admin == null || !Role.ADMIN.equals(admin.getRole())) {
            return "redirect:/admin/login";
        }

        try {
            List<AccessLog> logs;
            if (search != null && !search.trim().isEmpty()) {
                // 搜索日志
                logs = accessLogService.searchLogs(search.trim());
            } else {
                // 获取所有日志
                logs = accessLogService.getAllLogs();
            }

            model.addAttribute("logs", logs);
            model.addAttribute("search", search); // 回显搜索关键词
            model.addAttribute("admin", admin);

            // 记录访问日志
            accessLogService.logAccess(request, admin, 
                "访问日志页面" + (search != null ? " - 搜索: " + search : ""));

            return "admin/logs";
        } catch (Exception e) {
            logger.error("获取访问日志失败: {}", e.getMessage(), e);
            return "redirect:/admin/dashboard";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, HttpServletRequest request) {
        User admin = (User) session.getAttribute("admin");
        if (admin != null) {
            // 记录登出日志
            accessLogService.logAccess(request, admin, "管理员登出");
            session.removeAttribute("admin");
            session.invalidate();
        }
        return "redirect:/admin/login";
    }
} 