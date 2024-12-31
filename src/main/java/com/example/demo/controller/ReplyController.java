package com.example.demo.controller;

import com.example.demo.model.Reply;
import com.example.demo.model.Review;
import com.example.demo.model.User;
import com.example.demo.model.AccessType;
import com.example.demo.service.ReplyService;
import com.example.demo.service.ReviewService;
import com.example.demo.service.AccessLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/reply")
public class ReplyController {

    @Autowired
    private ReplyService replyService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private AccessLogService accessLogService;

    @PostMapping("/review/{reviewId}")
    public ResponseEntity<?> addReply(@PathVariable Integer reviewId,
                                    @RequestParam String content,
                                    HttpSession session,
                                    HttpServletRequest request) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(401).body("请先登录");
        }

        try {
            Review review = reviewService.getReviewById(reviewId);
            
            Reply reply = new Reply();
            reply.setReview(review);
            reply.setUser(user);
            reply.setContent(content);
            
            Reply savedReply = replyService.addReply(reply);
            
            // 记录添加回复的操作
            accessLogService.logAccess(request, user, 
                "回复评论 ID: " + reviewId);
            
            return ResponseEntity.ok(savedReply);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReply(@PathVariable Integer id,
                                       HttpSession session,
                                       HttpServletRequest request) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(401).body("请先登录");
        }

        try {
            replyService.deleteReply(id, user);
            
            // 记录删除回复的操作
            accessLogService.logAccess(request, user, 
                "删除回复 ID: " + id);
            
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
} 