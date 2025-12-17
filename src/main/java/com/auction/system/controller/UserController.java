package com.auction.system.controller;

import com.auction.system.common.ResponseResult;
import com.auction.system.entity.User;
import com.auction.system.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
@Tag(name = "用户管理")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping
    @Operation(summary = "获取所有用户")
    public ResponseResult<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseResult.success(users);
    }
    
    @PostMapping
    @Operation(summary = "创建用户")
    public ResponseResult<User> createUser(
            @Parameter(description = "用户信息") @RequestBody User user) {
        try {
            User savedUser = userService.saveUser(user);
            return ResponseResult.success("创建成功", savedUser);
        } catch (Exception e) {
            return ResponseResult.error("创建失败: " + e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "更新用户")
    public ResponseResult<User> updateUser(
            @Parameter(description = "用户ID") @PathVariable Long id, 
            @Parameter(description = "用户信息") @RequestBody User user) {
        try {
            user.setId(id);
            User updatedUser = userService.saveUser(user);
            return ResponseResult.success("更新成功", updatedUser);
        } catch (Exception e) {
            return ResponseResult.error("更新失败: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户")
    public ResponseResult<Void> deleteUser(
            @Parameter(description = "用户ID") @PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseResult.success("删除成功", null);
        } catch (Exception e) {
            return ResponseResult.error("删除失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/type/{userType}")
    @Operation(summary = "根据用户类型获取用户")
    public ResponseResult<List<User>> getUsersByType(
            @Parameter(description = "用户类型") @PathVariable Integer userType) {
        List<User> users = userService.getUsersByType(userType);
        return ResponseResult.success(users);
    }
    
    @GetMapping("/username/{username}")
    @Operation(summary = "根据用户名获取用户")
    public ResponseResult<User> getUserByUsername(
            @Parameter(description = "用户名") @PathVariable String username) {
        Optional<User> user = userService.getUserByUsername(username);
        if (user.isPresent()) {
            return ResponseResult.success(user.get());
        } else {
            return ResponseResult.error("用户不存在");
        }
    }
}