package com.auction.system.service;

import com.auction.system.entity.Asset;
import com.auction.system.entity.AssetImage;
import com.auction.system.repository.AssetRepository;
import com.auction.system.repository.AssetImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class AssetService {
    
    @Autowired
    private AssetRepository assetRepository;
    
    @Autowired
    private AssetImageRepository assetImageRepository;
    
    public List<Asset> getAllAssets() {
        return assetRepository.findAll(); // 返回所有资产，而不是只返回已上架的资产
    }
    
    public List<Asset> getAllAssetsAdmin() {
        return assetRepository.findAll(); // 管理员可以查看所有资产
    }
    
    public List<Asset> getAssetsByCategory(Long categoryId) {
        return assetRepository.findByCategoryIdAndStatusOrderByCreatedTimeDesc(categoryId, 4);
    }
    
    public List<Asset> getAssetsByOwner(Long ownerId) {
        // 修改为返回所有状态的资产，而不仅仅是已上架的资产
        return assetRepository.findByOwnerIdOrderByCreatedTimeDesc(ownerId);
    }
    
    public Optional<Asset> getAssetById(Long id) {
        return assetRepository.findById(id);
    }
    
    public Asset saveAsset(Asset asset) {
        // 自动生成资产编码
        if (asset.getAssetNo() == null || asset.getAssetNo().isEmpty()) {
            // 使用时间戳生成唯一资产编码
            String assetNo = "AST" + System.currentTimeMillis();
            asset.setAssetNo(assetNo);
        }
        
        // 如果是已上架状态且发布时间为空，则设置发布时间为当前时间
        if (asset.getStatus() != null && asset.getStatus() == 4 && asset.getPublishedTime() == null) {
            asset.setPublishedTime(LocalDateTime.now());
        }
        
        return assetRepository.save(asset);
    }
    
    public void deleteAsset(Long id) {
        assetRepository.deleteById(id);
    }
    
    public List<Asset> searchAssets(String keyword) {
        return assetRepository.searchAssets(keyword);
    }
    
    public List<Asset> getAssetsByAuctionStatus(Integer auctionStatus) {
        return assetRepository.findByAuctionStatusOrderByAuctionStartTimeAsc(auctionStatus);
    }
    
    public List<AssetImage> getAssetImages(Long assetId) {
        return assetImageRepository.findByAssetIdOrderBySortOrderAsc(assetId);
    }
    
    public Optional<AssetImage> getAssetCoverImage(Long assetId) {
        List<AssetImage> coverImages = assetImageRepository.findByAssetIdAndIsCoverOrderByCreatedTimeDesc(assetId, 1);
        if (!coverImages.isEmpty()) {
            return Optional.of(coverImages.get(0));
        }
        return Optional.empty();
    }
    
    public AssetImage saveAssetImage(AssetImage assetImage) {
        return assetImageRepository.save(assetImage);
    }
    
    public void deleteAssetImage(Long imageId) {
        assetImageRepository.deleteById(imageId);
    }
    
    public Optional<AssetImage> setCoverImage(Long assetId, Long imageId) {
        // 将该资产的所有图片设为非封面
        assetImageRepository.updateIsCoverByAssetId(assetId, 0);
        
        // 将指定图片设为封面
        Optional<AssetImage> imageOpt = assetImageRepository.findById(imageId);
        if (imageOpt.isPresent()) {
            AssetImage image = imageOpt.get();
            image.setIsCover(1);
            return Optional.of(assetImageRepository.save(image));
        }
        return Optional.empty();
    }
    
    /**
     * 批量发布资产
     * @param assetIds 资产ID列表
     * @return 更新后的资产列表
     */
    public List<Asset> batchPublishAssets(List<Long> assetIds) {
        return assetIds.stream()
                .map(assetId -> {
                    Optional<Asset> assetOpt = assetRepository.findById(assetId);
                    if (assetOpt.isPresent()) {
                        Asset asset = assetOpt.get();
                        asset.setStatus(4); // 已发布状态
                        asset.setPublishedTime(LocalDateTime.now()); // 设置发布时间
                        return assetRepository.save(asset);
                    }
                    return null;
                })
                .filter(asset -> asset != null)
                .collect(Collectors.toList());
    }
}