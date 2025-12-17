package com.auction.system.repository;

import com.auction.system.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderNo(String orderNo);
    List<Order> findByBuyerUserIdOrderByCreatedTimeDesc(Long buyerUserId);
    List<Order> findBySellerUserIdOrderByCreatedTimeDesc(Long sellerUserId);
    List<Order> findByOrderStatusOrderByCreatedTimeDesc(Integer orderStatus);
    List<Order> findByAuctionId(Long auctionId);
}