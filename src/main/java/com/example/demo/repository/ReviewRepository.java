package com.example.demo.repository;

import com.example.demo.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// ReviewRepository 继承 JpaRepository，提供常用的 CRUD 操作
@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
    boolean existsByUserIdAndMovieId(Integer userId, Integer movieId);
    List<Review> findByMovieIdOrderByTimestampDesc(Integer movieId);
    Double findAverageRatingByMovieId(Integer movieId);
    List<Review> findByMovieId(Integer movieId);
    List<Review> findByUserId(Integer userId);
    List<Review> findByMovieTitleContainingOrUserUsernameContaining(String movieTitle, String username);
}