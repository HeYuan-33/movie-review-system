package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.model.Status;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    // 显示注册页面
    @GetMapping("/user/register")
    public String showRegistrationForm() {
        return "register";
    }

    // 处理用户注册
    @PostMapping("/user/register")
    public String registerUser(@RequestParam String username,
                               @RequestParam String password,
                               @RequestParam String email,
                               Model model) {
        // 创建一个用户对象并保存，默认角色为 "USER" 和状态为 PENDING
        User user = new User(username, password, email, "USER", Status.PENDING);
        userService.saveUser(user); // 保存用户到数据库

        // 提示注册成功
        model.addAttribute("message", "Registration successful. Please wait for approval.");
        return "login"; // 注册成功后跳转到登录页面
    }


    // 显示登录页面
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    // 处理用户登录
    @PostMapping("/login")
    public String loginUser(@RequestParam String username, @RequestParam String password, Model model) {
        // 在这里做用户登录验证
        boolean isValid = userService.validateUser(username, password);
        if (isValid) {
            return "movie_search";  // 登录成功后跳转到影片搜索页面
        } else {
            model.addAttribute("error", "Invalid username or password");
            return "login";  // 登录失败，显示错误消息
        }
    }

    // 影片查询页面
    @GetMapping("/movie/search")
    public String searchMovies(@RequestParam(value = "keyword", required = false) String keyword, Model model) {
        // 从数据库获取电影列表，进行关键字模糊查询
        model.addAttribute("movies", userService.searchMovies(keyword));
        return "movie_search";
    }
}
