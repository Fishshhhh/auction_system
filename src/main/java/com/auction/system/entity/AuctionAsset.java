package com.auction.system.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "auction_asset")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuctionAsset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "auction_id", nullable = false)
    private Long auctionId;
    
    @Column(name = "asset_id", nullable = false)
    private Long assetId;
    
    /**
     * 资产数量
     * 表示该资产在此次拍卖中的数量
     */
    @Column(name = "quantity", nullable = false)
    private Integer quantity = 1;
    
    /**
     * 单个资产起拍价
     */
    @Column(name = "start_price", precision = 10, scale = 2)
    private BigDecimal startPrice;
    
    /**
     * 单个资产当前价
     */
    @Column(name = "current_price", precision = 10, scale = 2)
    private BigDecimal currentPrice;
    
    /**
     * 单个资产保留价
     */
    @Column(name = "reserve_price", precision = 10, scale = 2)
    private BigDecimal reservePrice;
    
    @CreationTimestamp
    @Column(name = "created_time")
    private LocalDateTime createdTime;
    
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;
}