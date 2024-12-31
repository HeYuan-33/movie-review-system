package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.model.Role;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // 获取待审核用户列表
    public List<User> getPendingUsers() {
        return userRepository.findByStatus(User.Status.PENDING);
    }

    // 获取所有用户
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // 更新用户状态
    @Transactional
    public void updateUserStatus(Integer userId, User.Status status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        user.setStatus(status);
        userRepository.save(user);
    }

    // 删除用户
    @Transactional
    public void deleteUser(Integer userId) {
        userRepository.deleteById(userId);
    }

    // 注册新用户
    @Transactional
    public User registerUser(User user) {
        // 设置初始状态为待审核
        user.setStatus(User.Status.PENDING);
        return userRepository.save(user);
    }

    // 根据用户名查找用户
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElse(null);
    }

    // 检查用户名是否已存在
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    // 验证用户并返回用户对象
    public User validateAndGetUser(String username, String password) {
        User user = findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            // 检查用户状态
            if (!User.Status.APPROVED.equals(user.getStatus())) {
                throw new RuntimeException("用户尚未通过审核或已被拒绝");
            }
            return user;
        }
        throw new RuntimeException("用户名或密码错误");
    }

    // 验证管理员并返回用户对象
    public User validateAndGetAdmin(String username, String password) {
        User user = validateAndGetUser(username, password);
        if (user != null && !Role.ADMIN.equals(user.getRole())) {
            throw new RuntimeException("该用户不是管理员");
        }
        return user;
    }

    @Transactional
    public User registerUser(String username, String password, String email) {
        if (existsByUsername(username)) {
            throw new RuntimeException("用户名已存在");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setRole(Role.USER);
        user.setStatus(User.Status.PENDING);
        return userRepository.save(user);
    }

    // 根据ID查找用户
    public User findById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }
}