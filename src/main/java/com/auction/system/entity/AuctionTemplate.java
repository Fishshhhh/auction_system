package com.auction.system.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "auction_template")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuctionTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(name = "auction_type", nullable = false)
    private Integer auctionType;  // 1-公开实时竞价 2-暗拍 3-无底价 4-定向 5-降价
    
    @Column(name = "config_params", columnDefinition = "json")
    private String configParams;
    
    @Column(length = 500)
    private String description;
    
    @Column(name = "is_default")
    private Integer isDefault = 0;
    
    @Column(name = "status")
    private Integer status = 1;
    
    // 公开竞价和无底价拍卖通用参数
    @Column(name = "bid_increment", precision = 10, scale = 2)
    private BigDecimal bidIncrement; // 加价幅度
    
    @Column(name = "extend_time")
    private Integer extendTime; // 延时时间(分钟)
    
    @Column(name = "extend_threshold")
    private Integer extendThreshold; // 触发延时的阈值(秒)
    
    // 降价拍卖参数
    @Column(name = "initial_price", precision = 10, scale = 2)
    private BigDecimal initialPrice; // 初始价
    
    @Column(name = "price_step", precision = 10, scale = 2)
    private BigDecimal priceStep; // 降价阶梯
    
    @Column(name = "price_drop_interval")
    private Integer priceDropInterval; // 降价周期(分钟)
    
    @Column(name = "min_price", precision = 10, scale = 2)
    private BigDecimal minPrice; // 最低成交价
    
    // 定向拍卖参数
    @Column(name = "qualification_required")
    private Boolean qualificationRequired = false; // 是否需要资格认证
    
    // 保证金比例(0-30%)
    @Column(name = "deposit_rate", precision = 5, scale = 2)
    private BigDecimal depositRate;
    
    @CreationTimestamp
    @Column(name = "created_time")
    private LocalDateTime createdTime;
    
    @UpdateTimestamp
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;
    
    // 拍卖类型常量
    public static final int TYPE_OPEN_BIDDING = 1;    // 公开实时竞价
    public static final int TYPE_SEALED_BID = 2;      // 暗拍
    public static final int TYPE_NO_RESERVE = 3;      // 无底价
    public static final int TYPE_DIRECTED = 4;        // 定向
    public static final int TYPE_DESCENDING = 5;      // 降价
}