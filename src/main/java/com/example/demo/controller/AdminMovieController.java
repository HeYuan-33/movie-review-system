package com.example.demo.controller;

import com.example.demo.model.Movie;
import com.example.demo.model.User;
import com.example.demo.model.Role;
import com.example.demo.model.AccessType;
import com.example.demo.service.MovieService;
import com.example.demo.service.AccessLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminMovieController {
    private static final Logger logger = LoggerFactory.getLogger(AdminMovieController.class);

    @Autowired
    private MovieService movieService;

    @Autowired
    private AccessLogService accessLogService;

    @GetMapping("/movies")
    public String showMovies(@RequestParam(required = false) String search,
                           HttpSession session,
                           Model model,
                           HttpServletRequest request) {
        User admin = (User) session.getAttribute("admin");
        if (admin == null || !Role.ADMIN.equals(admin.getRole())) {
            return "redirect:/admin/login";
        }

        try {
            List<Movie> movies;
            if (search != null && !search.trim().isEmpty()) {
                // 搜索电影
                movies = movieService.searchMovies(search.trim());
                model.addAttribute("search", search); // 回显搜索关键词
            } else {
                // 获取所有电影
                movies = movieService.getAllMovies();
            }
            
            model.addAttribute("movies", movies);
            model.addAttribute("admin", admin);

            // 记录访问日志
            accessLogService.logAccess(request, admin, 
                "访问电影管理页面" + (search != null ? " - 搜索: " + search : ""));

            return "admin/movies";
        } catch (Exception e) {
            logger.error("获取电影列表失败: {}", e.getMessage(), e);
            return "redirect:/admin/movies?error=system-error";
        }
    }

    @PostMapping("/movies/save")
    public String saveMovie(@ModelAttribute Movie movie,
                          @RequestParam(required = false) MultipartFile poster,
                          HttpSession session,
                          HttpServletRequest request,
                          Model model) {
        User admin = (User) session.getAttribute("admin");
        if (admin == null || !Role.ADMIN.equals(admin.getRole())) {
            return "redirect:/admin/login";
        }

        try {
            // 处理海报上传
            if (poster != null && !poster.isEmpty()) {
                String posterUrl = movieService.uploadPoster(poster);
                movie.setImageUrl(posterUrl);
            } else if (movie.getId() == null) {
                // 如果是新电影且没有上传海报，使用默认海报
                movie.setImageUrl("/img/default-movie.jpg");
            }

            // 如果是新电影，设置初始评分为0
            if (movie.getId() == null) {
                movie.setRating(0.0);
            }

            movieService.saveMovie(movie);
            
            // 记录操作日志
            String action = movie.getId() == null ? 
                "添加新电影: " + movie.getTitle() : 
                "更新电影: " + movie.getTitle();
            accessLogService.logAccess(request, admin, action);
            
            // 获取所有电影并返回到电影管理页面
            List<Movie> movies = movieService.getAllMovies();
            model.addAttribute("movies", movies);
            model.addAttribute("admin", admin);
            model.addAttribute("success", "save");
            return "admin/movies";
        } catch (Exception e) {
            logger.error("保存电影失败: {}", e.getMessage(), e);
            // 获取所有电影并返回到电影管理页面，同时显示错误信息
            List<Movie> movies = movieService.getAllMovies();
            model.addAttribute("movies", movies);
            model.addAttribute("admin", admin);
            model.addAttribute("error", "save-failed");
            return "admin/movies";
        }
    }

    @PostMapping("/movies/{id}/delete")
    public String deleteMovie(@PathVariable Integer id,
                            HttpSession session,
                            HttpServletRequest request,
                            Model model) {
        User admin = (User) session.getAttribute("admin");
        if (admin == null || !Role.ADMIN.equals(admin.getRole())) {
            return "redirect:/admin/login";
        }

        try {
            Movie movie = movieService.getMovieById(id);
            if (movie != null) {
                // 记录要删除的电影信息
                String movieTitle = movie.getTitle();
                
                // 删除电影
                movieService.deleteMovie(id);
                
                // 记录操作日志
                accessLogService.logAccess(request, admin, "删除电影: " + movieTitle);
                
                // 获取更新后的电影列表并返回到电影管理页面
                List<Movie> movies = movieService.getAllMovies();
                model.addAttribute("movies", movies);
                model.addAttribute("admin", admin);
                model.addAttribute("success", "delete");
                return "admin/movies";
            }
            
            // 如果电影不存在，显示错误信息
            List<Movie> movies = movieService.getAllMovies();
            model.addAttribute("movies", movies);
            model.addAttribute("admin", admin);
            model.addAttribute("error", "delete-failed");
            return "admin/movies";
        } catch (Exception e) {
            logger.error("删除电影失败: {}", e.getMessage(), e);
            // 获取电影列表并返回到电影管理页面，同时显示错误信息
            List<Movie> movies = movieService.getAllMovies();
            model.addAttribute("movies", movies);
            model.addAttribute("admin", admin);
            model.addAttribute("error", "delete-failed");
            return "admin/movies";
        }
    }

    @GetMapping("/movies/{id}")
    @ResponseBody
    public Movie getMovie(@PathVariable Integer id,
                         HttpSession session) {
        User admin = (User) session.getAttribute("admin");
        if (admin == null || !Role.ADMIN.equals(admin.getRole())) {
            throw new RuntimeException("未授权的访问");
        }
        
        try {
            return movieService.getMovieById(id);
        } catch (Exception e) {
            logger.error("获取电影数据失败: {}", e.getMessage(), e);
            throw new RuntimeException("获取电影数据失败");
        }
    }
} 