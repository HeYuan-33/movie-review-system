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
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {
    private static final Logger logger = LoggerFactory.getLogger(AdminUserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private AccessLogService accessLogService;

    @GetMapping("")
    public String showUsers(@RequestParam(required = false) String search,
                          HttpSession session,
                          Model model,
                          HttpServletRequest request) {
        User admin = (User) session.getAttribute("admin");
        if (admin == null || !Role.ADMIN.equals(admin.getRole())) {
            return "redirect:/admin/login";
        }

        try {
            List<User> users = userService.getAllUsers();
            model.addAttribute("users", users);
            model.addAttribute("admin", admin);

            // 记录访问日志
            accessLogService.logAccess(request, admin, 
                "访问用户管理页面" + (search != null ? " - 搜索: " + search : ""));

            return "admin/users";
        } catch (Exception e) {
            logger.error("获取用户列表失败: {}", e.getMessage(), e);
            return "redirect:/admin/dashboard";
        }
    }

    @PostMapping("/{id}/status")
    public String updateUserStatus(@PathVariable Integer id,
                                 @RequestParam User.Status status,
                                 HttpSession session,
                                 HttpServletRequest request) {
        User admin = (User) session.getAttribute("admin");
        if (admin == null || !Role.ADMIN.equals(admin.getRole())) {
            return "redirect:/admin/login";
        }

        try {
            User user = userService.findById(id);
            if (user != null) {
                userService.updateUserStatus(id, status);
                accessLogService.logAccess(request, admin, 
                    "更新用户状态 - " + user.getUsername() + " -> " + status);
            }
            return "redirect:/admin/users";
        } catch (Exception e) {
            logger.error("更新用户状态失败: {}", e.getMessage(), e);
            return "redirect:/admin/users?error=status-update-failed";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable Integer id,
                           HttpSession session,
                           HttpServletRequest request) {
        User admin = (User) session.getAttribute("admin");
        if (admin == null || !Role.ADMIN.equals(admin.getRole())) {
            return "redirect:/admin/login";
        }

        try {
            User user = userService.findById(id);
            if (user != null) {
                userService.deleteUser(id);
                accessLogService.logAccess(request, admin, 
                    "删除用户 - " + user.getUsername());
            }
            return "redirect:/admin/users";
        } catch (Exception e) {
            logger.error("删除用户失败: {}", e.getMessage(), e);
            return "redirect:/admin/users?error=delete-failed";
        }
    }
} 