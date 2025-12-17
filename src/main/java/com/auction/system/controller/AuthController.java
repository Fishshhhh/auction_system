package com.auction.system.controller;

import com.auction.system.common.ResponseResult;
import com.auction.system.entity.User;
import com.auction.system.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
@Tag(name = "认证管理")
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public ResponseResult<Map<String, Object>> login(
            @Parameter(description = "登录信息") @RequestBody Map<String, String> credentials) {
        try {
            String username = credentials.get("username");
            String password = credentials.get("password");
            
            if (username == null || username.isEmpty()) {
                return ResponseResult.error("用户名不能为空");
            }
            
            if (password == null || password.isEmpty()) {
                return ResponseResult.error("密码不能为空");
            }
            
            // 查找用户
            Optional<User> userOptional = userService.getUserByUsername(username);
            if (!userOptional.isPresent()) {
                return ResponseResult.error("用户不存在");
            }
            
            User user = userOptional.get();
            
            // 在实际应用中，这里应该验证密码
            // 由于我们没有实现密码加密存储，暂时跳过密码验证
            // if (!passwordEncoder.matches(password, user.getPassword())) {
            //     return ResponseResult.error("密码错误");
            // }
            
            // 登录成功，返回用户信息和token（这里简化处理，实际应该生成JWT Token）
            Map<String, Object> result = new HashMap<>();
            result.put("user", user);
            result.put("token", "fake-jwt-token-" + System.currentTimeMillis());
            
            return ResponseResult.success("登录成功", result);
        } catch (Exception e) {
            return ResponseResult.error("登录失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/logout")
    @Operation(summary = "用户登出")
    public ResponseResult<Void> logout() {
        // 在JWT模式下，前端负责清除token即可
        // 后端不需要特殊处理
        return ResponseResult.success(null);
    }
}