package com.auction.system.controller;

import com.auction.system.common.ResponseResult;
import com.auction.system.entity.Order;
import com.auction.system.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin
@Tag(name = "订单管理")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    @GetMapping
    @Operation(summary = "获取所有订单")
    public ResponseResult<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return ResponseResult.success(orders);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取订单详情")
    public ResponseResult<Order> getOrderById(
            @Parameter(description = "订单ID") @PathVariable Long id) {
        return orderService.getOrderById(id)
                .map(order -> ResponseResult.success(order))
                .orElse(ResponseResult.error("订单不存在"));
    }
    
    @GetMapping("/order-no/{orderNo}")
    @Operation(summary = "根据订单编号获取订单详情")
    public ResponseResult<Order> getOrderByOrderNo(
            @Parameter(description = "订单编号") @PathVariable String orderNo) {
        return orderService.getOrderByOrderNo(orderNo)
                .map(order -> ResponseResult.success(order))
                .orElse(ResponseResult.error("订单不存在"));
    }
    
    @PostMapping
    @Operation(summary = "创建订单")
    public ResponseResult<Order> createOrder(
            @Parameter(description = "订单信息") @RequestBody Order order) {
        try {
            Order savedOrder = orderService.saveOrder(order);
            return ResponseResult.success("创建成功", savedOrder);
        } catch (Exception e) {
            return ResponseResult.error("创建失败: " + e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "更新订单")
    public ResponseResult<Order> updateOrder(
            @Parameter(description = "订单ID") @PathVariable Long id,
            @Parameter(description = "订单信息") @RequestBody Order order) {
        try {
            order.setId(id);
            Order updatedOrder = orderService.saveOrder(order);
            return ResponseResult.success("更新成功", updatedOrder);
        } catch (Exception e) {
            return ResponseResult.error("更新失败: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "删除订单")
    public ResponseResult<Void> deleteOrder(
            @Parameter(description = "订单ID") @PathVariable Long id) {
        try {
            orderService.deleteOrder(id);
            return ResponseResult.success("删除成功", null);
        } catch (Exception e) {
            return ResponseResult.error("删除失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/buyer/{buyerId}")
    @Operation(summary = "根据买家ID获取订单")
    public ResponseResult<List<Order>> getOrdersByBuyer(
            @Parameter(description = "买家ID") @PathVariable Long buyerId) {
        List<Order> orders = orderService.getOrdersByBuyer(buyerId);
        return ResponseResult.success(orders);
    }
    
    @GetMapping("/seller/{sellerId}")
    @Operation(summary = "根据卖家ID获取订单")
    public ResponseResult<List<Order>> getOrdersBySeller(
            @Parameter(description = "卖家ID") @PathVariable Long sellerId) {
        List<Order> orders = orderService.getOrdersBySeller(sellerId);
        return ResponseResult.success(orders);
    }
    
    @GetMapping("/status/{status}")
    @Operation(summary = "根据状态获取订单")
    public ResponseResult<List<Order>> getOrdersByStatus(
            @Parameter(description = "订单状态") @PathVariable Integer status) {
        List<Order> orders = orderService.getOrdersByStatus(status);
        return ResponseResult.success(orders);
    }
    
    @PutMapping("/{id}/pay")
    @Operation(summary = "模拟订单支付")
    public ResponseResult<Order> payOrder(
            @Parameter(description = "订单ID") @PathVariable Long id) {
        try {
            // 模拟支付过程
            Order order = orderService.payOrder(id);
            return ResponseResult.success("支付成功", order);
        } catch (Exception e) {
            return ResponseResult.error("支付失败: " + e.getMessage());
        }
    }
    
    @PutMapping("/{id}/complete")
    @Operation(summary = "完成订单")
    public ResponseResult<Order> completeOrder(
            @Parameter(description = "订单ID") @PathVariable Long id) {
        try {
            Order order = orderService.completeOrder(id);
            return ResponseResult.success("订单已完成", order);
        } catch (Exception e) {
            return ResponseResult.error("操作失败: " + e.getMessage());
        }
    }
}