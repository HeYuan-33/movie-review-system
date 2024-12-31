package com.example.demo.service;

import com.example.demo.model.AccessLog;
import com.example.demo.model.User;
import com.example.demo.repository.AccessLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AccessLogService {

    @Autowired
    private AccessLogRepository accessLogRepository;

    public void logAccess(HttpServletRequest request, User user, String action) {
        AccessLog log = new AccessLog();
        log.setUser(user);
        log.setAction(action);
        log.setIpAddress(request.getRemoteAddr());
        log.setTimestamp(LocalDateTime.now());
        accessLogRepository.save(log);
    }

    public List<AccessLog> getRecentLogs(int limit) {
        return accessLogRepository.findTop10ByOrderByTimestampDesc();
    }

    public List<AccessLog> getAllLogs() {
        return accessLogRepository.findAll(Sort.by(Sort.Direction.DESC, "timestamp"));
    }

    public List<AccessLog> getUserLogs(String username) {
        return accessLogRepository.findByUserUsername(username);
    }

    public List<AccessLog> searchLogs(String keyword) {
        return accessLogRepository.findByUserUsernameContainingOrActionContainingOrIpAddressContaining(
            keyword, keyword, keyword, Sort.by(Sort.Direction.DESC, "timestamp"));
    }

    public long getTodayVisitCount() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        return accessLogRepository.countByTimestampGreaterThanEqual(startOfDay);
    }

    public long getTodayActiveUserCount() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        return accessLogRepository.countDistinctUserByTimestampGreaterThanEqual(startOfDay);
    }
} 