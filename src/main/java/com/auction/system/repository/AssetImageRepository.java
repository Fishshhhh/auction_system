package com.auction.system.repository;

import com.auction.system.entity.AssetImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AssetImageRepository extends JpaRepository<AssetImage, Long> {
    List<AssetImage> findByAssetIdOrderBySortOrderAsc(Long assetId);
    List<AssetImage> findByAssetIdAndIsCoverOrderByCreatedTimeDesc(Long assetId, Integer isCover);
    
    @Modifying
    @Query("UPDATE AssetImage SET isCover = ?2 WHERE assetId = ?1")
    void updateIsCoverByAssetId(Long assetId, Integer isCover);
}