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
@Table(name = "asset")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Asset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(length = 500)
    private String description;
    
    @Column(name = "asset_no", length = 50)
    private String assetNo;
    
    @Column(name = "category_id")
    private Long categoryId;
    
    @Column(name = "starting_price", precision = 10, scale = 2)
    private BigDecimal startingPrice;
    
    @Column(name = "reserve_price", precision = 10, scale = 2)
    private BigDecimal reservePrice;
    
    @Column(name = "current_price", precision = 10, scale = 2)
    private BigDecimal currentPrice;
    
    @Column(name = "owner_id")
    private Long ownerId;
    
    @Column(name = "status")
    private Integer status; // 1-待审核, 2-审核通过, 3-审核拒绝, 4-已上架, 5-已下架, 6-已售出
    
    @Column(name = "auction_status")
    private Integer auctionStatus; // 1-未开始, 2-进行中, 3-已结束, 4-流拍
    
    @Column(name = "auction_type")
    private Integer auctionType; // 1-公开实时竞价 2-暗拍 3-无底价 4-定向 5-降价
    
    @Column(name = "properties", columnDefinition = "TEXT")
    private String properties;
    
    @CreationTimestamp
    @Column(name = "created_time")
    private LocalDateTime createdTime;
    
    @UpdateTimestamp
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;
    
    @Column(name = "published_time")
    private LocalDateTime publishedTime;
    
    @Column(name = "auction_start_time")
    private LocalDateTime auctionStartTime;
    
    @Column(name = "auction_end_time")
    private LocalDateTime auctionEndTime;
    
    // 拍卖类型常量
    public static final int TYPE_OPEN_BIDDING = 1;    // 公开实时竞价
    public static final int TYPE_SEALED_BID = 2;      // 暗拍
    public static final int TYPE_NO_RESERVE = 3;      // 无底价
    public static final int TYPE_DIRECTED = 4;        // 定向
    public static final int TYPE_DESCENDING = 5;      // 降价
}