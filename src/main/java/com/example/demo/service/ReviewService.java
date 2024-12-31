package com.example.demo.service;
import com.example.demo.model.User;
import com.example.demo.model.Movie;
import com.example.demo.repository.MovieRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.MovieService;
import com.example.demo.model.Review;
import com.example.demo.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private MovieRepository movieRepository;
    
    @Autowired
    private MovieService movieService;

    public Review addReview(Review review) {
        review.setTimestamp(System.currentTimeMillis());
        Review savedReview = reviewRepository.save(review);
        // 更新电影评分
        movieService.updateMovieRating(review.getMovie().getId());
        return savedReview;
    }

    public List<Review> getMovieReviews(Integer movieId) {
        return reviewRepository.findByMovieIdOrderByTimestampDesc(movieId);
    }

    public Review getReviewById(Integer reviewId) {
        return reviewRepository.findById(reviewId)
            .orElseThrow(() -> new RuntimeException("评论不存在"));
    }

    public void deleteReview(Integer reviewId, User user) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new RuntimeException("评论不存在"));
            
        // 检查是否是评论的作者
        if (!review.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("您只能删除自己的评论");
        }
        
        // 保存电影ID以便更新评分
        Integer movieId = review.getMovie().getId();
        
        // 删除评论
        reviewRepository.deleteById(reviewId);
        
        // 更新电影评分
        movieService.updateMovieRating(movieId);
    }

    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    public List<Review> searchReviews(String keyword) {
        return reviewRepository.findByMovieTitleContainingOrUserUsernameContaining(keyword, keyword);
    }

    @Transactional
    public void deleteReview(Integer id) {
        reviewRepository.deleteById(id);
    }

    @Transactional
    public void updateMovieRating(Integer movieId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("电影不存在"));
        
        // 计算平均评分并保留一位小数
        double averageRating = movie.getReviews().stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
        
        // 四舍五入到一位小数
        averageRating = Math.round(averageRating * 10.0) / 10.0;
        
        movie.setRating(averageRating);
        movieRepository.save(movie);
    }
}