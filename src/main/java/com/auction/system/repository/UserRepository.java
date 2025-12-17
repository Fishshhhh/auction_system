package com.auction.system.repository;

import com.auction.system.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByUserTypeAndStatusOrderByCreatedTimeDesc(Integer userType, Integer status);
    List<User> findByStatusOrderByCreatedTimeDesc(Integer status);
    Optional<User> findByUsername(String username);
    List<User> findByFullNameContainingIgnoreCase(String fullName);
}