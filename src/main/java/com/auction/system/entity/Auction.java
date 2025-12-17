package com.auction.system.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "auction")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Auction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // 删除原有的assetId字段，因为现在支持多资产
    // @Column(name = "asset_id", nullable = false)
    // private Long assetId;
    
    @Column(name = "template_id")
    private Long templateId;
    
    // 总起拍价（所有资产的起拍价总和）
    @Column(name = "start_price", precision = 10, scale = 2)
    private BigDecimal startPrice;
    
    // 总当前价（所有资产的当前价总和）
    @Column(name = "current_price", precision = 10, scale = 2)
    private BigDecimal currentPrice;
    
    // 总保留价（所有资产的保留价总和）
    @Column(name = "reserve_price", precision = 10, scale = 2)
    private BigDecimal reservePrice;
    
    @Column(name = "buy_it_now_price", precision = 10, scale = 2)
    private BigDecimal buyItNowPrice;
    
    @Column(name = "start_time")
    private LocalDateTime startTime;
    
    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    @Column(name = "auction_status", nullable = false)
    private Integer auctionStatus = 1; // 1-未开始 2-进行中 3-已结束 4-已成交 5-流拍
    
    @Column(name = "auction_type")
    private Integer auctionType; // 1-公开实时竞价 2-暗拍 3-无底价 4-定向 5-降价
    
    // 公开竞价和无底价拍卖通用参数
    @Column(name = "bid_increment", precision = 10, scale = 2)
    private BigDecimal bidIncrement; // 加价幅度
    
    @Column(name = "extend_time")
    private Integer extendTime; // 延时时间(分钟)
    
    @Column(name = "extend_threshold")
    private Integer extendThreshold; // 触发延时的阈值(秒)
    
    // 降价拍卖特有字段
    @Column(name = "initial_price", precision = 10, scale = 2)
    private BigDecimal initialPrice; // 初始价
    
    @Column(name = "price_step", precision = 10, scale = 2)
    private BigDecimal priceStep; // 降价阶梯
    
    @Column(name = "price_drop_interval")
    private Integer priceDropInterval; // 降价周期(分钟)
    
    @Column(name = "min_price", precision = 10, scale = 2)
    private BigDecimal minPrice; // 最低成交价
    
    @Column(name = "next_price_drop_time")
    private LocalDateTime nextPriceDropTime; // 下次降价时间
    
    // 定向拍卖特有字段
    @Column(name = "qualification_required")
    private Boolean qualificationRequired = false; // 是否需要资格认证
    
    @Column(name = "winner_user_id")
    private Long winnerUserId;
    
    @Column(name = "final_price", precision = 10, scale = 2)
    private BigDecimal finalPrice;
    
    // 保证金相关
    @Column(name = "deposit_required")
    private Boolean depositRequired = false; // 是否需要缴纳保证金
    
    @Column(name = "deposit_amount", precision = 10, scale = 2)
    private BigDecimal depositAmount; // 保证金金额
    
    @CreationTimestamp
    @Column(name = "created_time")
    private LocalDateTime createdTime;
    
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;
    
    // 拍卖类型常量
    public static final int TYPE_OPEN_BIDDING = 1;    // 公开实时竞价
    public static final int TYPE_SEALED_BID = 2;      // 暗拍
    public static final int TYPE_NO_RESERVE = 3;      // 无底价
    public static final int TYPE_DIRECTED = 4;        // 定向
    public static final int TYPE_DESCENDING = 5;      // 降价
    
    // 拍卖状态常量
    public static final int STATUS_NOT_STARTED = 1;  // 未开始
    public static final int STATUS_IN_PROGRESS = 2;  // 进行中
    public static final int STATUS_ENDED = 3;        // 已结束
    public static final int STATUS_SOLD = 4;         // 已成交
    public static final int STATUS_UNSOLD = 5;       // 流拍
}