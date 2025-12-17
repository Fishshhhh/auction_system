package com.auction.system.repository;

import com.auction.system.entity.AuctionAsset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AuctionAssetRepository extends JpaRepository<AuctionAsset, Long> {
    List<AuctionAsset> findByAuctionId(Long auctionId);
    List<AuctionAsset> findByAssetId(Long assetId);
    
    @Modifying
    @Query("DELETE FROM AuctionAsset aa WHERE aa.auctionId = ?1")
    void deleteByAuctionId(Long auctionId);
}