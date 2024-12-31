package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.model.AccessType;
import com.example.demo.service.ReviewService;
import com.example.demo.service.AccessLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/review")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private AccessLogService accessLogService;

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable Integer id, 
                                        HttpSession session,
                                        HttpServletRequest request) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(401).body("请先登录");
        }

        try {
            reviewService.deleteReview(id, user);
            
            // 记录删除评论的操作
            accessLogService.logAccess(request, user, 
                "删除评论 ID: " + id);
            
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
} 