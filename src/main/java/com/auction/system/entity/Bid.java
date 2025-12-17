package com.auction.system.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "bid")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bid {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "auction_id", nullable = false)
    private Long auctionId;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "bid_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal bidPrice;
    
    // 添加购买数量字段
    @Column(name = "quantity", nullable = false)
    private Integer quantity = 1;
    
    @Column(name = "bid_status", nullable = false)
    private Integer bidStatus = 1; // 1-有效 2-无效 3- winning 4- losing
    
    @CreationTimestamp
    @Column(name = "created_time")
    private LocalDateTime createdTime;
}