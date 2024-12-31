package com.example.demo.repository;

import com.example.demo.model.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, Integer> {
    List<Reply> findByReviewIdOrderByTimestampAsc(Integer reviewId);
} 