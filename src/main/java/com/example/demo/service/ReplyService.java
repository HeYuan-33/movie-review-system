package com.example.demo.service;

import com.example.demo.model.Reply;
import com.example.demo.model.User;
import com.example.demo.repository.ReplyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReplyService {
    @Autowired
    private ReplyRepository replyRepository;

    public Reply addReply(Reply reply) {
        reply.setTimestamp(System.currentTimeMillis());
        return replyRepository.save(reply);
    }

    public List<Reply> getReviewReplies(Integer reviewId) {
        return replyRepository.findByReviewIdOrderByTimestampAsc(reviewId);
    }

    public void deleteReply(Integer replyId, User user) {
        Reply reply = replyRepository.findById(replyId)
            .orElseThrow(() -> new RuntimeException("回复不存在"));
            
        // 检查是否是回复的作者
        if (!reply.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("您只能删除自己的回复");
        }
        
        replyRepository.deleteById(replyId);
    }
} 