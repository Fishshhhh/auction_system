package com.auction.system.service;

import com.auction.system.entity.Auction;
import com.auction.system.entity.AuctionAsset;
import com.auction.system.entity.Order;
import com.auction.system.entity.OrderItem;
import com.auction.system.repository.OrderRepository;
import com.auction.system.repository.OrderItemRepository;
import com.auction.system.repository.AuctionAssetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderItemRepository orderItemRepository;
    
    @Autowired
    private AuctionAssetRepository auctionAssetRepository;
    
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }
    
    public Optional<Order> getOrderByOrderNo(String orderNo) {
        return orderRepository.findByOrderNo(orderNo);
    }
    
    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }
    
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }
    
    public List<Order> getOrdersByBuyer(Long buyerUserId) {
        return orderRepository.findByBuyerUserIdOrderByCreatedTimeDesc(buyerUserId);
    }
    
    public List<Order> getOrdersBySeller(Long sellerUserId) {
        return orderRepository.findBySellerUserIdOrderByCreatedTimeDesc(sellerUserId);
    }
    
    public List<Order> getOrdersByStatus(Integer orderStatus) {
        return orderRepository.findByOrderStatusOrderByCreatedTimeDesc(orderStatus);
    }
    
    public List<Order> getOrdersByAuctionId(Long auctionId) {
        return orderRepository.findByAuctionId(auctionId);
    }
    
    /**
     * 模拟订单支付
     * @param id 订单ID
     * @return 更新后的订单
     * @throws Exception 支付异常
     */
    public Order payOrder(Long id) throws Exception {
        Optional<Order> orderOpt = orderRepository.findById(id);
        if (!orderOpt.isPresent()) {
            throw new Exception("订单不存在");
        }
        
        Order order = orderOpt.get();
        if (order.getOrderStatus() != 1) { // 不是待付款状态
            throw new Exception("订单状态不正确，无法支付");
        }
        
        order.setOrderStatus(2); // 已付款
        order.setPaymentTime(LocalDateTime.now());
        return orderRepository.save(order);
    }
    
    /**
     * 完成订单
     * @param id 订单ID
     * @return 更新后的订单
     * @throws Exception 完成订单异常
     */
    public Order completeOrder(Long id) throws Exception {
        Optional<Order> orderOpt = orderRepository.findById(id);
        if (!orderOpt.isPresent()) {
            throw new Exception("订单不存在");
        }
        
        Order order = orderOpt.get();
        if (order.getOrderStatus() != 2) { // 不是已付款状态
            throw new Exception("订单状态不正确，无法完成");
        }
        
        order.setOrderStatus(3); // 已完成
        order.setCompletedTime(LocalDateTime.now());
        return orderRepository.save(order);
    }
    
    /**
     * 根据拍卖结果创建订单
     * @param auction 拍卖对象
     * @param auctionService 拍卖服务（通过参数传入，避免循环依赖）
     * @return 创建的订单
     */
    public Order createOrderFromAuction(Auction auction, AuctionService auctionService) throws Exception {
        if (auction.getWinnerUserId() == null) {
            throw new Exception("拍卖没有获胜者，无法创建订单");
        }
        
        // 创建订单
        Order order = new Order();
        order.setOrderNo(generateOrderNo()); // 生成订单号
        order.setAuctionId(auction.getId());
        // assetId不再适用，因为一个拍卖可以包含多个资产
        order.setBuyerUserId(auction.getWinnerUserId());
        // 这里应该从资产信息中获取卖家ID，暂时设为默认值1
        order.setSellerUserId(1L);
        order.setOrderAmount(auction.getFinalPrice());
        order.setActualAmount(auction.getFinalPrice());
        order.setQuantity(1); // 总数量，默认为1
        order.setOrderStatus(1); // 待付款
        order.setCreatedTime(LocalDateTime.now());
        order.setUpdatedTime(LocalDateTime.now());
        
        Order savedOrder = orderRepository.save(order);
        
        // 创建订单项（针对每个拍卖资产）
        List<AuctionAsset> auctionAssets = auctionAssetRepository.findByAuctionId(auction.getId());
        for (AuctionAsset auctionAsset : auctionAssets) {
            OrderItem item = new OrderItem();
            item.setOrderId(savedOrder.getId());
            item.setAssetId(auctionAsset.getAssetId());
            item.setQuantity(auctionAsset.getQuantity());
            item.setUnitPrice(auctionAsset.getCurrentPrice());
            // 计算小计金额 = 单价 * 数量
            BigDecimal subtotal = auctionAsset.getCurrentPrice().multiply(new BigDecimal(auctionAsset.getQuantity()));
            item.setSubtotalAmount(subtotal);
            item.setCreatedTime(LocalDateTime.now());
            item.setUpdatedTime(LocalDateTime.now());
            orderItemRepository.save(item);
        }
        
        return savedOrder;
    }
    
    /**
     * 生成订单号
     * @return 订单号
     */
    private String generateOrderNo() {
        // 生成唯一的订单号，格式：ORDER + 时间戳 + 4位随机数
        return "ORDER" + System.currentTimeMillis() + String.format("%04d", (int)(Math.random() * 10000));
    }
}