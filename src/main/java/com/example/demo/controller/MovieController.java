package com.example.demo.controller;

import com.example.demo.model.Movie;
import com.example.demo.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    @Autowired
    private MovieService movieService;

    // 获取所有电影
    @GetMapping
    public List<Movie> getAllMovies() {
        return movieService.getAllMovies();
    }

    // 根据标题或导演模糊搜索电影
    @GetMapping("/search")
    public List<Movie> searchMovies(@RequestParam String keyword) {
        return movieService.searchMovies(keyword);
    }

    // 获取评分最高的前10部电影
    @GetMapping("/top-rated")
    public List<Movie> getTopRatedMovies() {
        return movieService.getTopRatedMovies();
    }

    // 根据ID获取电影详情
    @GetMapping("/{id}")
    public Movie getMovieById(@PathVariable Integer id) {
        return movieService.getMovieById(id);
    }
}
