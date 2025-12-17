package com.auction.system.service;

import com.auction.system.entity.User;
import com.auction.system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    public List<User> getAllUsers() {
        return userRepository.findByStatusOrderByCreatedTimeDesc(1);
    }
    
    public User saveUser(User user) {
        // 检查用户名是否已存在
        if (user.getId() == null && userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("用户名已存在");
        }
        return userRepository.save(user);
    }
    
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
    public List<User> getUsersByType(Integer userType) {
        return userRepository.findByUserTypeAndStatusOrderByCreatedTimeDesc(userType, 1);
    }
    
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}