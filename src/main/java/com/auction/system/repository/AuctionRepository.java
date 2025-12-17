package com.auction.system.repository;

import com.auction.system.entity.Auction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AuctionRepository extends JpaRepository<Auction, Long> {
    // 删除原来的findByAssetId方法，因为现在一个资产可能属于多个拍卖
    // List<Auction> findByAssetId(Long assetId);
    List<Auction> findByAuctionStatusOrderByStartTimeAsc(Integer auctionStatus);
    List<Auction> findByAuctionStatusOrderByEndTimeDesc(Integer auctionStatus);
    List<Auction> findByWinnerUserIdOrderByCreatedTimeDesc(Long winnerUserId);
}