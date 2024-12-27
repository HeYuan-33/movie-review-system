package com.example.demo.repository;

import com.example.demo.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// MovieRepository 继承 JpaRepository，提供常用的 CRUD 操作
@Repository
public interface MovieRepository extends JpaRepository<Movie, Integer> {

    // 通过标题模糊查询电影
    List<Movie> findByTitleContaining(String title);

    // 通过类型查询电影
    List<Movie> findByGenre(String genre);
}
