package com.example.demo.controller;

import com.example.demo.model.Movie;
import com.example.demo.model.Review;
import com.example.demo.model.User;
import com.example.demo.model.AccessType;
import com.example.demo.service.MovieService;
import com.example.demo.service.ReviewService;
import com.example.demo.service.AccessLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/movie")
public class MovieViewController {

    @Autowired
    private MovieService movieService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private AccessLogService accessLogService;

    // 显示电影列表页面
    @GetMapping("/list")
    public String showMovieList(Model model, HttpSession session, HttpServletRequest request) {
        List<Movie> movies = movieService.getAllMovies();
        model.addAttribute("movies", movies);
        
        // 记录访问日志
        User user = (User) session.getAttribute("user");
        accessLogService.logAccess(request, user, "访问电影列表页面");
        
        return "movie/list";
    }

    // 显示电影详情页面
    @GetMapping("/{id}")
    public String showMovieDetail(@PathVariable Integer id, Model model, 
                                HttpSession session, HttpServletRequest request) {
        Movie movie = movieService.getMovieById(id);
        List<Review> reviews = reviewService.getMovieReviews(id);
        
        model.addAttribute("movie", movie);
        model.addAttribute("reviews", reviews);

        // 记录访问日志
        User user = (User) session.getAttribute("user");
        accessLogService.logAccess(request, user, "查看电影详情：" + movie.getTitle());
        
        return "movie_detail";
    }

    // 处理添加影评
    @PostMapping("/{id}/review")
    public String addReview(@PathVariable Integer id,
                          @RequestParam String content,
                          @RequestParam Integer rating,
                          HttpSession session,
                          HttpServletRequest request) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        Movie movie = movieService.getMovieById(id);
        Review review = new Review();
        review.setMovie(movie);
        review.setUser(user);
        review.setContent(content);
        review.setRating(rating);
        review.setTimestamp(System.currentTimeMillis());

        reviewService.addReview(review);
        
        // 记录评论日志
        accessLogService.logAccess(request, user, 
            "发表影评：" + movie.getTitle() + "，评分：" + rating);
        
        return "redirect:/movie/" + id;
    }
} 