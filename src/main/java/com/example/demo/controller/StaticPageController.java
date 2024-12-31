package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.demo.service.MovieService;
import java.util.List;
import com.example.demo.model.Movie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class StaticPageController {
    private static final Logger logger = LoggerFactory.getLogger(StaticPageController.class);

    @Autowired
    private MovieService movieService;

    @GetMapping("/about")
    public String showAboutPage() {
        return "about";
    }



    @GetMapping("/movie_rankings")
    public String showMovieRankings(Model model) {
        try {
            logger.info("开始获取电影排行榜数据");
            
            // 先获取所有电影
            List<Movie> allMovies = movieService.getAllMovies();
            logger.info("获取到所有电影数量: {}", allMovies.size());
            
            if (allMovies.isEmpty()) {
                logger.warn("数据库中没有电影数据");
                model.addAttribute("movies", allMovies);
                return "movie_rankings";
            }
            
            // 获取排行榜电影
            List<Movie> topMovies = movieService.getTopRatedMovies();
            logger.info("获取到排行榜电影数量: {}", topMovies.size());
            
            // 打印每部电影的详细信息
            for (Movie movie : topMovies) {
                logger.info("电影详情 - ID: {}, 标题: {}, 导演: {}, 评分: {}, 上映日期: {}, 图片URL: {}", 
                    movie.getId(), movie.getTitle(), movie.getDirector(), 
                    movie.getRating(), movie.getRelease_date(), movie.getImageUrl());
            }
            
            model.addAttribute("movies", topMovies);
            logger.info("成功添加电影数据到模型");
            return "movie_rankings";
        } catch (Exception e) {
            logger.error("获取排行榜数据时发生错误: ", e);
            model.addAttribute("errorMessage", e.getMessage());
            return "error";
        }
    }
} 