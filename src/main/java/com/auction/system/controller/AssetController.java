package com.auction.system.controller;

import com.auction.system.common.ResponseResult;
import com.auction.system.entity.Asset;
import com.auction.system.entity.AssetImage;
import com.auction.system.entity.Auction;
import com.auction.system.service.AssetService;
import com.auction.system.service.AuctionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/assets")
@CrossOrigin
@Tag(name = "资产管理")
public class AssetController {
    
    @Autowired
    private AssetService assetService;
    
    @Autowired
    private AuctionService auctionService;
    
    @GetMapping
    @Operation(summary = "获取所有资产")
    public ResponseResult<List<Asset>> getAllAssets() {
        List<Asset> assets = assetService.getAllAssets();
        return ResponseResult.success(assets);
    }
    
    @GetMapping("/all")
    @Operation(summary = "获取所有资产（管理员专用）")
    public ResponseResult<List<Asset>> getAllAssetsAdmin() {
        List<Asset> assets = assetService.getAllAssetsAdmin();
        return ResponseResult.success(assets);
    }
    
    @GetMapping("/category/{categoryId}")
    @Operation(summary = "根据分类获取资产")
    public ResponseResult<List<Asset>> getAssetsByCategory(
            @Parameter(description = "分类ID") @PathVariable Long categoryId) {
        List<Asset> assets = assetService.getAssetsByCategory(categoryId);
        return ResponseResult.success(assets);
    }
    
    @GetMapping("/owner/{ownerId}")
    @Operation(summary = "根据所有者获取资产")
    public ResponseResult<List<Asset>> getAssetsByOwner(
            @Parameter(description = "所有者ID") @PathVariable Long ownerId) {
        List<Asset> assets = assetService.getAssetsByOwner(ownerId);
        return ResponseResult.success(assets);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取资产详情")
    public ResponseResult<Asset> getAssetById(
            @Parameter(description = "资产ID") @PathVariable Long id) {
        return assetService.getAssetById(id)
                .map(asset -> ResponseResult.success(asset))
                .orElse(ResponseResult.error("资产不存在"));
    }
    
    @PostMapping
    @Operation(summary = "创建资产")
    public ResponseResult<Asset> createAsset(
            @Parameter(description = "资产信息") @RequestBody Asset asset) {
        try {
            Asset savedAsset = assetService.saveAsset(asset);
            return ResponseResult.success("创建成功", savedAsset);
        } catch (Exception e) {
            return ResponseResult.error("创建失败: " + e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "更新资产")
    public ResponseResult<Asset> updateAsset(
            @Parameter(description = "资产ID") @PathVariable Long id,
            @Parameter(description = "资产信息") @RequestBody Asset asset) {
        try {
            asset.setId(id);
            Asset updatedAsset = assetService.saveAsset(asset);
            return ResponseResult.success("更新成功", updatedAsset);
        } catch (Exception e) {
            return ResponseResult.error("更新失败: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "删除资产")
    public ResponseResult<Void> deleteAsset(
            @Parameter(description = "资产ID") @PathVariable Long id) {
        try {
            assetService.deleteAsset(id);
            return ResponseResult.success("删除成功", null);
        } catch (Exception e) {
            return ResponseResult.error("删除失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/search")
    @Operation(summary = "搜索资产")
    public ResponseResult<List<Asset>> searchAssets(
            @Parameter(description = "搜索关键词") @RequestParam String keyword) {
        List<Asset> assets = assetService.searchAssets(keyword);
        return ResponseResult.success(assets);
    }
    
    @PutMapping("/{id}/publish")
    @Operation(summary = "发布资产")
    public ResponseResult<Asset> publishAsset(
            @Parameter(description = "资产ID") @PathVariable Long id) {
        try {
            Optional<Asset> assetOpt = assetService.getAssetById(id);
            if (assetOpt.isPresent()) {
                Asset asset = assetOpt.get();
                asset.setStatus(4); // 已发布状态
                Asset updatedAsset = assetService.saveAsset(asset);
                return ResponseResult.success("发布成功", updatedAsset);
            } else {
                return ResponseResult.error("资产不存在");
            }
        } catch (Exception e) {
            return ResponseResult.error("发布失败: " + e.getMessage());
        }
    }
    
    @PutMapping("/batch-publish")
    @Operation(summary = "批量发布资产")
    public ResponseResult<List<Asset>> batchPublishAssets(
            @Parameter(description = "资产ID列表") @RequestBody List<Long> assetIds) {
        try {
            List<Asset> updatedAssets = assetService.batchPublishAssets(assetIds);
            return ResponseResult.success("批量发布成功", updatedAssets);
        } catch (Exception e) {
            return ResponseResult.error("批量发布失败: " + e.getMessage());
        }
    }
    
    @PutMapping("/{id}/unpublish")
    @Operation(summary = "取消发布资产")
    public ResponseResult<Asset> unpublishAsset(
            @Parameter(description = "资产ID") @PathVariable Long id) {
        try {
            Optional<Asset> assetOpt = assetService.getAssetById(id);
            if (assetOpt.isPresent()) {
                Asset asset = assetOpt.get();
                asset.setStatus(1); // 初始状态
                Asset updatedAsset = assetService.saveAsset(asset);
                return ResponseResult.success("取消发布成功", updatedAsset);
            } else {
                return ResponseResult.error("资产不存在");
            }
        } catch (Exception e) {
            return ResponseResult.error("取消发布失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/{id}/images")
    @Operation(summary = "获取资产图片")
    public ResponseResult<List<AssetImage>> getAssetImages(
            @Parameter(description = "资产ID") @PathVariable Long id) {
        List<AssetImage> images = assetService.getAssetImages(id);
        return ResponseResult.success(images);
    }
    
    @GetMapping("/{id}/cover-image")
    @Operation(summary = "获取资产封面图片")
    public ResponseResult<AssetImage> getAssetCoverImage(
            @Parameter(description = "资产ID") @PathVariable Long id) {
        Optional<AssetImage> coverImage = assetService.getAssetCoverImage(id);
        return coverImage.map(image -> ResponseResult.success(image))
                .orElse(ResponseResult.error("未找到封面图片"));
    }
    
    @PostMapping("/{id}/images")
    @Operation(summary = "添加资产图片")
    public ResponseResult<AssetImage> addAssetImage(
            @Parameter(description = "资产ID") @PathVariable Long id,
            @Parameter(description = "图片信息") @RequestBody AssetImage assetImage) {
        try {
            assetImage.setAssetId(id);
            AssetImage savedImage = assetService.saveAssetImage(assetImage);
            return ResponseResult.success("添加成功", savedImage);
        } catch (Exception e) {
            return ResponseResult.error("添加失败: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/images/{imageId}")
    @Operation(summary = "删除资产图片")
    public ResponseResult<Void> deleteAssetImage(
            @Parameter(description = "图片ID") @PathVariable Long imageId) {
        try {
            assetService.deleteAssetImage(imageId);
            return ResponseResult.success("删除成功", null);
        } catch (Exception e) {
            return ResponseResult.error("删除失败: " + e.getMessage());
        }
    }
    
    @PutMapping("/{id}/cover-image/{imageId}")
    @Operation(summary = "设置资产封面图片")
    public ResponseResult<AssetImage> setCoverImage(
            @Parameter(description = "资产ID") @PathVariable Long id,
            @Parameter(description = "图片ID") @PathVariable Long imageId) {
        try {
            Optional<AssetImage> updatedImage = assetService.setCoverImage(id, imageId);
            return updatedImage.map(image -> ResponseResult.success("封面设置成功", image))
                    .orElse(ResponseResult.error("设置封面失败"));
        } catch (Exception e) {
            return ResponseResult.error("设置封面失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/{id}/auctions")
    @Operation(summary = "获取资产相关的拍卖")
    public ResponseResult<List<Auction>> getAssetAuctions(
            @Parameter(description = "资产ID") @PathVariable Long id) {
        List<Auction> auctions = auctionService.getAuctionsByAssetId(id);
        return ResponseResult.success(auctions);
    }
}