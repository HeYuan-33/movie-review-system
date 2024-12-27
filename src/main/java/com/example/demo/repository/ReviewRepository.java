package com.example.demo.repository;

import com.example.demo.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// ReviewRepository 继承 JpaRepository，提供常用的 CRUD 操作
@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

    // 通过电影ID查找所有影评
    List<Review> findByMovieId(Integer movieId);

    // 通过用户ID查找所有影评
    List<Review> findByUserId(Integer userId);

    // 查找电影的影评及评分
    List<Review> findByMovieIdAndUserId(Integer movieId, Integer userId);
}
