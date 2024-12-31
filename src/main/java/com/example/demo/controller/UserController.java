package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.model.Role;
import com.example.demo.model.AccessType;
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

@Controller
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private AccessLogService accessLogService;

    // 显示首页
    @GetMapping("/hh影评网站")
    public String showIndex() {
        return "index";
    }

    // 显示用户主页
    @GetMapping("/home")
    public String showHomePage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        return "home";
    }

    // 显示登录页面
    @GetMapping("/login")
    public String showLoginPage(HttpSession session) {
        // 如果用户已经登录，重定向到相应页面
        User user = (User) session.getAttribute("user");
        if (user != null) {
            if (Role.ADMIN == user.getRole()) {
                return "redirect:/admin/dashboard";
            }
            return "redirect:/home";
        }
        return "login";
    }

    // 处理登录请求
    @PostMapping("/login")
    public String login(@RequestParam String username,
                       @RequestParam String password,
                       HttpSession session,
                       HttpServletRequest request,
                       Model model) {
        try {
            logger.info("尝试用户登录: {}", username);
            User user = userService.validateAndGetUser(username, password);
            if (user != null) {
                session.setAttribute("user", user);
                // 记录登录日志
                accessLogService.logAccess(request, user, "用户登录成功");
                logger.info("用户登录成功: {}", username);
                if (Role.ADMIN == user.getRole()) {
                    session.setAttribute("admin", user);  // 如果是管理员，同时设置admin属性
                    return "redirect:/admin/dashboard";
                }
                return "redirect:/home";  // 普通用户登录成功后重定向到home页面
            }
            logger.warn("用户登录失败: {} - 用户名或密码错误", username);
            model.addAttribute("error", "用户名或密码错误");
            return "login";
        } catch (Exception e) {
            logger.error("用户登录异常: {} - {}", username, e.getMessage(), e);
            model.addAttribute("error", "登录失败：" + e.getMessage());
            return "login";
        }
    }

    // 显示注册页面
    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    // 处理注册请求
    @PostMapping("/register")
    public String register(@RequestParam String username,
                         @RequestParam String password,
                         @RequestParam String email,
                         HttpServletRequest request,
                         Model model) {
        try {
            logger.info("尝试注册新用户: {}", username);
            User user = userService.registerUser(username, password, email);
            // 记录用户注册日志
            accessLogService.logAccess(request, user, "新用户注册");
            logger.info("用户注册成功: {}", username);
            model.addAttribute("message", "注册成功，请登录");
            return "redirect:/login";
        } catch (Exception e) {
            logger.error("用户注册失败: {} - {}", username, e.getMessage(), e);
            model.addAttribute("error", "注册失败：" + e.getMessage());
            return "register";
        }
    }

    // 处理登出请求
    @GetMapping("/logout")
    public String logout(HttpSession session, HttpServletRequest request) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            // 记录登出日志
            accessLogService.logAccess(request, user, "用户登出");
            session.removeAttribute("user");
            session.invalidate();
        }
        return "redirect:/";  // 登出后返回首页
    }
}