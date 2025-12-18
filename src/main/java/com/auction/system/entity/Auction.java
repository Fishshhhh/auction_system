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
@Table(name = "auction")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Auction {
    public static final int STATUS_NOT_STARTED = 1;  // 未开始
    public static final int STATUS_IN_PROGRESS = 2;  // 进行中
    public static final int STATUS_ENDED = 3;        // 已结束
    public static final int STATUS_COMPLETED = 4;    // 已成交
    public static final int STATUS_FAILED = 5;       // 流拍
    public static final int STATUS_SOLD = 4;         // 已成交（同STATUS_COMPLETED）
    public static final int STATUS_UNSOLD = 5;       // 流拍（同STATUS_FAILED）
    
    public static final int TYPE_OPEN_BIDDING = 1;   // 公开实时竞价
    public static final int TYPE_SEALED_BID = 2;     // 暗拍
    public static final int TYPE_NO_RESERVE = 3;     // 无底价
    public static final int TYPE_DIRECTED = 4;       // 定向
    public static final int TYPE_DESCENDING = 5;     // 降价
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "template_id")
    private Long templateId;
    
    @Column(name = "start_price", precision = 10, scale = 2)
    private BigDecimal startPrice;
    
    @Column(name = "current_price", precision = 10, scale = 2)
    private BigDecimal currentPrice;
    
    @Column(name = "reserve_price", precision = 10, scale = 2)
    private BigDecimal reservePrice;
    
    @Column(name = "buy_it_now_price", precision = 10, scale = 2)
    private BigDecimal buyItNowPrice;
    
    @Column(name = "final_price", precision = 10, scale = 2)
    private BigDecimal finalPrice; // 最终成交价格
    
    @Column(name = "start_time")
    private LocalDateTime startTime;
    
    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    @Column(name = "auction_status")
    private Integer auctionStatus = STATUS_NOT_STARTED;
    
    @Column(name = "auction_type")
    private Integer auctionType = TYPE_OPEN_BIDDING;
    
    @Column(name = "bid_increment", precision = 10, scale = 2)
    private BigDecimal bidIncrement;
    
    @Column(name = "extend_time")
    private Integer extendTime; // 延时时间(分钟)
    
    @Column(name = "extend_threshold")
    private Integer extendThreshold; // 延时阈值(秒)
    
    @Column(name = "initial_price", precision = 10, scale = 2)
    private BigDecimal initialPrice; // 降价拍卖初始价格
    
    @Column(name = "price_step", precision = 10, scale = 2)
    private BigDecimal priceStep; // 降价阶梯
    
    @Column(name = "price_drop_interval")
    private Integer priceDropInterval; // 降价周期(分钟)
    
    @Column(name = "min_price", precision = 10, scale = 2)
    private BigDecimal minPrice; // 最低价
    
    @Column(name = "next_price_drop_time")
    private LocalDateTime nextPriceDropTime; // 下次降价时间
    
    @Column(name = "winner_user_id")
    private Long winnerUserId; // 竞拍获胜用户ID
    
    @Column(name = "qualification_required")
    private Boolean qualificationRequired = false; // 是否需要资格认证
    
    @Column(name = "deposit_rate", precision = 5, scale = 2)
    private BigDecimal depositRate; // 保证金比例
    
    @Column(name = "is_package_auction")
    private Boolean isPackageAuction = false; // 是否打包拍卖
    
    @CreationTimestamp
    @Column(name = "created_time")
    private LocalDateTime createdTime;
    
    @UpdateTimestamp
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;
    
    // 实用方法
    public boolean isInProgress() {
        return this.auctionStatus != null && this.auctionStatus == STATUS_IN_PROGRESS;
    }
    
    public boolean isEnded() {
        return this.auctionStatus != null && 
               (this.auctionStatus == STATUS_ENDED || 
                this.auctionStatus == STATUS_COMPLETED || 
                this.auctionStatus == STATUS_FAILED);
    }
    
    public boolean isOpenBidding() {
        return this.auctionType != null && this.auctionType == TYPE_OPEN_BIDDING;
    }
    
    public boolean isSealedBid() {
        return this.auctionType != null && this.auctionType == TYPE_SEALED_BID;
    }
    
    public boolean isNoReserve() {
        return this.auctionType != null && this.auctionType == TYPE_NO_RESERVE;
    }
    
    public boolean isDirected() {
        return this.auctionType != null && this.auctionType == TYPE_DIRECTED;
    }
    
    public boolean isDescending() {
        return this.auctionType != null && this.auctionType == TYPE_DESCENDING;
    }
    
    public boolean isPackageAuction() {
        return this.isPackageAuction != null && this.isPackageAuction;
    }
    
    // Getter and Setter for finalPrice
    public BigDecimal getFinalPrice() {
        return finalPrice;
    }
    
    public void setFinalPrice(BigDecimal finalPrice) {
        this.finalPrice = finalPrice;
    }
}