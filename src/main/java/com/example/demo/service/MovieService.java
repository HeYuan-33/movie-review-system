package com.example.demo.service;

import com.example.demo.model.Movie;
import com.example.demo.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class MovieService {

    @Autowired
    private MovieRepository movieRepository;

    // 获取所有电影
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    // 根据标题和导演进行模糊搜索
    public List<Movie> searchMovies(String keyword) {
        return movieRepository.findByTitleContainingOrDirectorContaining(keyword, keyword);
    }

    // 获取评分最高的前10部电影
    public List<Movie> getTopRatedMovies() {
        List<Movie> allMovies = movieRepository.findAll();
        // 按评分降序排序，如果评分为null则排在最后
        allMovies.sort((m1, m2) -> {
            if (m1.getRating() == null && m2.getRating() == null) {
                return 0;
            }
            if (m1.getRating() == null) {
                return 1;
            }
            if (m2.getRating() == null) {
                return -1;
            }
            return Double.compare(m2.getRating(), m1.getRating());
        });
        // 返回前10部电影，如果不足10部则返回全部
        return allMovies.size() > 10 ? allMovies.subList(0, 10) : allMovies;
    }

    // 根据电影标题和导演部分匹配进行搜索
    public List<Movie> searchMoviesByTitleOrDirector(String title, String director) {
        return movieRepository.findByTitleContainingOrDirectorContaining(title, director);
    }

    // 根据电影ID获取电影详情
    public Movie getMovieById(Integer id) {
        return movieRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("电影不存在"));
    }

    public void updateMovieRating(Integer movieId) {
        Movie movie = movieRepository.findById(movieId)
            .orElseThrow(() -> new RuntimeException("电影不存在"));
            
        // 计算平均评分并保留一位小数
        double averageRating = movie.getReviews().stream()
            .mapToInt(review -> review.getRating())
            .average()
            .orElse(0.0);
            
        // 四舍五入到一位小数
        averageRating = Math.round(averageRating * 10.0) / 10.0;
            
        movie.setRating(averageRating);
        movieRepository.save(movie);
    }

    @Transactional
    public String uploadPoster(MultipartFile poster) throws IOException {
        try {
            // 生成文件名
            String originalFilename = poster.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String filename = UUID.randomUUID().toString() + extension;
            
            // 设置上传目录为static/img目录
            String uploadDir = "src/main/resources/static/img/";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            Path filePath = uploadPath.resolve(filename);
            Files.copy(poster.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            // 返回文件URL（相对路径）
            return "/img/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("上传海报失败：" + e.getMessage(), e);
        }
    }

    @Transactional
    public void saveMovie(Movie movie) {
        try {
            // 如果没有设置图片，使用默认图片
            if (movie.getImageUrl() == null || movie.getImageUrl().isEmpty()) {
                movie.setImageUrl("/img/default-movie.jpg");
            }
            
            // 如果是新电影，设置初始评分为0
            if (movie.getId() == null) {
                movie.setRating(0.0);
            }
            
            movieRepository.save(movie);
        } catch (Exception e) {
            throw new RuntimeException("保存电影失败：" + e.getMessage(), e);
        }
    }

    @Transactional
    public void deleteMovie(Integer id) {
        try {
            Movie movie = getMovieById(id);
            // 删除关联的评论
            movie.getReviews().clear();
            // 删除电影
            movieRepository.delete(movie);
        } catch (Exception e) {
            throw new RuntimeException("删除电影失败：" + e.getMessage(), e);
        }
    }
}
