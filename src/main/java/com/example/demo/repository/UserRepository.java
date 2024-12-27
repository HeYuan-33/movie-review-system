package com.example.demo.repository;

import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// UserRepository 继承 JpaRepository，提供常用的 CRUD 操作
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    // 通过用户名查找用户
    User findByUsername(String username);
}
