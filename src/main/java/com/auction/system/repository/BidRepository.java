package com.auction.system.repository;

import com.auction.system.entity.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {
    List<Bid> findByAuctionIdOrderByBidPriceDescCreatedTimeAsc(Long auctionId);
    List<Bid> findByUserIdOrderByCreatedTimeDesc(Long userId);
    List<Bid> findByAuctionIdAndBidStatusOrderByBidPriceDescCreatedTimeAsc(Long auctionId, Integer bidStatus);
}