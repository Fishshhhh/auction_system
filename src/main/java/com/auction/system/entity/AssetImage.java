package com.auction.system.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.persistence.*;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "asset_image")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssetImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "asset_id")
    private Long assetId;
    
    @Column(name = "image_url", length = 500)
    private String imageUrl;
    
    @Column(name = "sort_order")
    private Integer sortOrder;
    
    @Column(name = "is_cover")
    private Integer isCover; // 1-是封面, 0-不是封面
    
    @CreationTimestamp
    @Column(name = "created_time")
    private LocalDateTime createdTime;
}