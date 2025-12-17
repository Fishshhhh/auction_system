package com.auction.system.repository;

import com.auction.system.entity.AssetCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AssetCategoryRepository extends JpaRepository<AssetCategory, Long> {
    List<AssetCategory> findByStatusOrderByCreatedTimeDesc(Integer status);
    Optional<AssetCategory> findByCode(String code);
    List<AssetCategory> findByNameContainingIgnoreCase(String name);
}