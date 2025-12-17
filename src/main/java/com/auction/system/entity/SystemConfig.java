package com.auction.system.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.persistence.*;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "system_config")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String configKey;
    
    @Column(length = 500)
    private String configValue;
    
    @Column(length = 200)
    private String displayName;
    
    @Column(length = 500)
    private String description;
    
    @Column(name = "config_group", length = 50)
    private String configGroup;
    
    @Column(name = "status")
    private Integer status = 1; // 1-启用, 0-禁用
    
    @CreationTimestamp
    @Column(name = "created_time")
    private LocalDateTime createdTime;
    
    @UpdateTimestamp
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;
}