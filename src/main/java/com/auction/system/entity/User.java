package com.auction.system.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.persistence.*;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 50)
    private String username;
    
    @Column(length = 100)
    private String email;
    
    @Column(length = 50)
    private String phone;
    
    @Column(name = "full_name", length = 100)
    private String fullName;
    
    @Column(name = "user_type")
    private Integer userType; // 1-管理员, 2-普通用户, 3-卖家, 4-买家
    
    @Column(name = "status")
    private Integer status = 1; // 1-启用, 0-禁用
    
    @CreationTimestamp
    @Column(name = "created_time")
    private LocalDateTime createdTime;
    
    @UpdateTimestamp
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;
}