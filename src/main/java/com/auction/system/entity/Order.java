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
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "order_no", nullable = false, unique = true)
    private String orderNo;
    
    @Column(name = "auction_id", nullable = false)
    private Long auctionId;
    
    // 删除原有的assetId字段，因为一个订单可能包含多个资产
    // @Column(name = "asset_id", nullable = false)
    // private Long assetId;
    
    @Column(name = "buyer_user_id", nullable = false)
    private Long buyerUserId;
    
    @Column(name = "seller_user_id", nullable = false)
    private Long sellerUserId;
    
    @Column(name = "order_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal orderAmount;
    
    @Column(name = "actual_amount", precision = 10, scale = 2)
    private BigDecimal actualAmount;
    
    // 购买数量
    @Column(name = "quantity", nullable = false)
    private Integer quantity = 1;
    
    @Column(name = "order_status", nullable = false)
    private Integer orderStatus = 1; // 1-待付款 2-已付款 3-已完成 4-已取消 5-退款中 6-已退款
    
    @Column(name = "payment_method")
    private Integer paymentMethod; // 1-支付宝 2-微信 3-银行卡
    
    @Column(name = "payment_time")
    private LocalDateTime paymentTime;
    
    @Column(name = "completed_time")
    private LocalDateTime completedTime;
    
    @CreationTimestamp
    @Column(name = "created_time")
    private LocalDateTime createdTime;
    
    @UpdateTimestamp
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;
}