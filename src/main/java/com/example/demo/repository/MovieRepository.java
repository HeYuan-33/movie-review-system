package com.example.demo.repository;

import com.example.demo.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// MovieRepository 继承 JpaRepository，提供常用的 CRUD 操作
@Repository
public interface MovieRepository extends JpaRepository<Movie, Integer> {
    List<Movie> findByTitleContainingOrDirectorContaining(String title, String director);
    List<Movie> findTop10ByOrderByRatingDesc();
    List<Movie> findByTitleContainingIgnoreCase(String keyword);
    List<Movie> findAllByOrderByRatingDesc();
}