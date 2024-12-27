package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.model.Movie;
import com.example.demo.repository.MovieRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MovieRepository movieRepository;

    // 保存用户
    public void saveUser(User user) {
        userRepository.save(user);
    }

    // 验证用户登录
    public boolean validateUser(String username, String password) {
        User user = userRepository.findByUsername(username);
        return user != null && user.getPassword().equals(password);
    }

    // 根据关键字查询影片
    public List<Movie> searchMovies(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return movieRepository.findAll();  // 如果没有提供关键词，返回所有影片
        } else {
            return movieRepository.findByTitleContaining(keyword);  // 模糊查询标题
        }
    }
}
