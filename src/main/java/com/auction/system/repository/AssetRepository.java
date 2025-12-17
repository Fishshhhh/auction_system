package com.auction.system.repository;

import com.auction.system.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {
    List<Asset> findByCategoryIdAndStatusOrderByCreatedTimeDesc(Long categoryId, Integer status);
    List<Asset> findByOwnerIdAndStatusOrderByCreatedTimeDesc(Long ownerId, Integer status);
    List<Asset> findByStatusOrderByCreatedTimeDesc(Integer status);
    List<Asset> findByAuctionStatusOrderByAuctionStartTimeAsc(Integer auctionStatus);
    
    // 添加根据所有者ID获取所有资产的方法（不限状态）
    List<Asset> findByOwnerIdOrderByCreatedTimeDesc(Long ownerId);
    
    @Query("SELECT a FROM Asset a WHERE a.name LIKE %:keyword% OR a.description LIKE %:keyword%")
    List<Asset> searchAssets(@Param("keyword") String keyword);
    
    List<Asset> findByCategoryIdAndAuctionStatusOrderByAuctionStartTimeAsc(Long categoryId, Integer auctionStatus);
}