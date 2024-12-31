package com.example.demo.controller;

import com.example.demo.model.Review;
import com.example.demo.model.User;
import com.example.demo.model.Role;
import com.example.demo.model.AccessType;
import com.example.demo.service.ReviewService;
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
@RequestMapping("/admin/reviews")
public class AdminReviewController {
    private static final Logger logger = LoggerFactory.getLogger(AdminReviewController.class);

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private AccessLogService accessLogService;

    @GetMapping("")
    public String showReviews(@RequestParam(required = false) String search,
                            HttpSession session, 
                            Model model,
                            HttpServletRequest request) {
        User admin = (User) session.getAttribute("admin");
        if (admin == null || !Role.ADMIN.equals(admin.getRole())) {
            return "redirect:/admin/login";
        }

        try {
            List<Review> reviews;
            if (search != null && !search.trim().isEmpty()) {
                reviews = reviewService.searchReviews(search);
            } else {
                reviews = reviewService.getAllReviews();
            }

            model.addAttribute("reviews", reviews);
            model.addAttribute("admin", admin);
            
            // 记录访问日志
            accessLogService.logAccess(request, admin, 
                "访问评论管理页面" + (search != null ? " - 搜索: " + search : ""));
            
            return "admin/reviews";
        } catch (Exception e) {
            logger.error("获取评论列表失败: {}", e.getMessage(), e);
            return "redirect:/admin/dashboard";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteReview(@PathVariable Integer id,
                             HttpSession session,
                             HttpServletRequest request) {
        User admin = (User) session.getAttribute("admin");
        if (admin == null || !Role.ADMIN.equals(admin.getRole())) {
            return "redirect:/admin/login";
        }

        try {
            Review review = reviewService.getReviewById(id);
            if (review != null) {
                reviewService.deleteReview(id);
                accessLogService.logAccess(request, admin, "删除评论: " + review.getContent());
            }
            return "redirect:/admin/reviews";
        } catch (Exception e) {
            logger.error("删除评论失败: {}", e.getMessage(), e);
            return "redirect:/admin/reviews?error=delete-failed";
        }
    }
} 