package com.example.demo.repository;

import com.example.demo.model.AccessLog;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AccessLogRepository extends JpaRepository<AccessLog, Integer> {
    List<AccessLog> findTop10ByOrderByTimestampDesc();
    List<AccessLog> findAllByOrderByTimestampDesc();
    List<AccessLog> findByUserUsername(String username);
    List<AccessLog> findByUserUsernameContainingOrActionContainingOrIpAddressContaining(
        String username, String action, String ipAddress, Sort sort);
    long countByTimestampGreaterThanEqual(LocalDateTime timestamp);
    @Query("SELECT COUNT(DISTINCT l.user) FROM AccessLog l WHERE l.timestamp >= :timestamp")
    long countDistinctUserByTimestampGreaterThanEqual(@Param("timestamp") LocalDateTime timestamp);
} 
