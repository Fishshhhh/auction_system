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
import org.springframework.transaction.annotation.Propagation;
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
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public Order createOrderFromAuction(Auction auction, AuctionService auctionService) throws Exception {
        System.out.println("开始创建订单，拍卖ID: " + auction.getId());
        
        // 检查拍卖是否有获胜者
        if (auction.getWinnerUserId() == null) {
            System.out.println("拍卖没有获胜者，无法创建订单");
            return null;
        }
        
        // 检查获胜者ID是否有效
        if (auction.getWinnerUserId() <= 0) {
            System.out.println("拍卖获胜者ID无效: " + auction.getWinnerUserId());
            return null;
        }
        
        System.out.println("获胜者用户ID: " + auction.getWinnerUserId());
        System.out.println("最终价格: " + auction.getFinalPrice());
        
        // 创建订单
        Order order = new Order();
        order.setOrderNo(generateOrderNo()); // 生成订单号
        order.setAuctionId(auction.getId());
        
        // 设置买家ID（获胜者）
        order.setBuyerUserId(auction.getWinnerUserId());
        
        // 设置卖家ID（从资产信息中获取，如果没有则使用默认值1）
        order.setSellerUserId(1L);
        
        // 设置订单金额（基于拍卖的最终价格和资产数量计算总金额）
        BigDecimal totalAmount = calculateTotalAmount(auction);
        order.setOrderAmount(totalAmount);
        order.setActualAmount(totalAmount);
        
        // 设置数量
        order.setQuantity(getAuctionAssetQuantity(auction));
        order.setOrderStatus(1); // 待付款
        
        System.out.println("准备保存订单: " + order);
        
        // 保存订单
        Order savedOrder = orderRepository.save(order);
        System.out.println("订单保存成功，订单ID: " + savedOrder.getId());
        
        // 创建订单项（针对每个拍卖资产）
        List<AuctionAsset> auctionAssets = auctionAssetRepository.findByAuctionId(auction.getId());
        System.out.println("找到 " + auctionAssets.size() + " 个拍卖资产");
        
        if (auctionAssets.isEmpty()) {
            System.out.println("警告：拍卖没有关联的资产");
        }
        
        for (AuctionAsset auctionAsset : auctionAssets) {
            System.out.println("处理资产: " + auctionAsset.getAssetId());
            OrderItem item = new OrderItem();
            item.setOrderId(savedOrder.getId());
            item.setAssetId(auctionAsset.getAssetId());
            item.setQuantity(auctionAsset.getQuantity() != null ? auctionAsset.getQuantity() : 0);
            item.setUnitPrice(auctionAsset.getCurrentPrice() != null ? auctionAsset.getCurrentPrice() : BigDecimal.ZERO);
            
            // 计算小计金额 = 单价 * 数量
            BigDecimal quantity = new BigDecimal(auctionAsset.getQuantity() != null ? auctionAsset.getQuantity() : 0);
            BigDecimal unitPrice = auctionAsset.getCurrentPrice() != null ? auctionAsset.getCurrentPrice() : BigDecimal.ZERO;
            BigDecimal subtotal = unitPrice.multiply(quantity);
            item.setSubtotalAmount(subtotal);
            
            System.out.println("准备保存订单项: " + item);
            orderItemRepository.save(item);
            System.out.println("订单项保存成功");
        }
        
        System.out.println("订单创建完成，订单号: " + savedOrder.getOrderNo());
        return savedOrder;
    }
    
    /**
     * 计算拍卖总金额
     * @param auction 拍卖对象
     * @return 总金额
     */
    private BigDecimal calculateTotalAmount(Auction auction) {
        if (auction.getFinalPrice() == null) {
            return BigDecimal.ZERO;
        }
        
        Integer quantity = getAuctionAssetQuantity(auction);
        if (quantity == null || quantity <= 0) {
            return BigDecimal.ZERO;
        }
        
        return auction.getFinalPrice().multiply(new BigDecimal(quantity));
    }
    
    /**
     * 获取拍卖资产总数量
     * @param auction 拍卖对象
     * @return 资产总数量
     */
    private Integer getAuctionAssetQuantity(Auction auction) {
        List<AuctionAsset> auctionAssets = auctionAssetRepository.findByAuctionId(auction.getId());
        if (auctionAssets.isEmpty()) {
            return 0;
        }
        
        // 通常一个拍卖只有一个资产，直接返回第一个资产的数量
        return auctionAssets.get(0).getQuantity();
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