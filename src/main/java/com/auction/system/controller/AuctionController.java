package com.auction.system.controller;

import com.auction.system.common.ResponseResult;
import com.auction.system.entity.Auction;
import com.auction.system.entity.AuctionAsset;
import com.auction.system.entity.AuctionWithAsset;
import com.auction.system.entity.Bid;
import com.auction.system.service.AuctionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/auctions")
@CrossOrigin
@Tag(name = "拍卖管理")
public class AuctionController {
    
    @Autowired
    private AuctionService auctionService;
    
    @GetMapping
    @Operation(summary = "获取所有拍卖")
    public ResponseResult<List<Auction>> getAllAuctions() {
        List<Auction> auctions = auctionService.getAllAuctions();
        return ResponseResult.success(auctions);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取拍卖详情")
    public ResponseResult<Auction> getAuctionById(
            @Parameter(description = "拍卖ID") @PathVariable Long id) {
        Optional<Auction> auctionOpt = auctionService.getAuctionById(id);
        return auctionOpt.map(auction -> ResponseResult.success(auction))
                .orElse(ResponseResult.error("拍卖不存在"));
    }
    
    @PostMapping
    @Operation(summary = "创建拍卖")
    public ResponseResult<Auction> createAuction(
            @Parameter(description = "拍卖信息") @RequestBody AuctionWithAsset auctionWithAsset) {
        try {
            // 先保存拍卖基本信息
            Auction auction = new Auction();
            // 复制基本属性
            auction.setTemplateId(auctionWithAsset.getTemplateId());
            auction.setStartPrice(auctionWithAsset.getStartPrice());
            auction.setCurrentPrice(auctionWithAsset.getCurrentPrice());
            auction.setReservePrice(auctionWithAsset.getReservePrice());
            auction.setBuyItNowPrice(auctionWithAsset.getBuyItNowPrice());
            auction.setStartTime(auctionWithAsset.getStartTime());
            auction.setEndTime(auctionWithAsset.getEndTime());
            auction.setAuctionStatus(auctionWithAsset.getAuctionStatus());
            auction.setAuctionType(auctionWithAsset.getAuctionType());
            auction.setBidIncrement(auctionWithAsset.getBidIncrement());
            auction.setExtendTime(auctionWithAsset.getExtendTime());
            auction.setExtendThreshold(auctionWithAsset.getExtendThreshold());
            auction.setInitialPrice(auctionWithAsset.getInitialPrice());
            auction.setPriceStep(auctionWithAsset.getPriceStep());
            auction.setPriceDropInterval(auctionWithAsset.getPriceDropInterval());
            auction.setMinPrice(auctionWithAsset.getMinPrice());
            auction.setNextPriceDropTime(auctionWithAsset.getNextPriceDropTime());
            auction.setQualificationRequired(auctionWithAsset.getQualificationRequired());
            auction.setDepositRate(auctionWithAsset.getDepositRate());
            auction.setIsPackageAuction(auctionWithAsset.getIsPackageAuction());
            
            Auction savedAuction = auctionService.saveAuction(auction);
            
            // 如果包含资产信息，则同时在auction_asset表中创建关联记录
            if (auctionWithAsset.getAssetId() != null) {
                AuctionAsset auctionAsset = new AuctionAsset();
                auctionAsset.setAuctionId(savedAuction.getId());
                auctionAsset.setAssetId(auctionWithAsset.getAssetId());
                auctionAsset.setQuantity(auctionWithAsset.getQuantity() != null ? auctionWithAsset.getQuantity() : 1);
                auctionAsset.setStartPrice(auctionWithAsset.getStartPrice());
                auctionAsset.setCurrentPrice(auctionWithAsset.getCurrentPrice());
                auctionAsset.setReservePrice(auctionWithAsset.getReservePrice());
                auctionService.saveAuctionAsset(auctionAsset);
                
                // 更新拍卖的总价信息
                auctionService.updateAuctionPriceInfo(savedAuction.getId());
            }
            
            return ResponseResult.success("创建成功", savedAuction);
        } catch (Exception e) {
            return ResponseResult.error("创建失败: " + e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "更新拍卖")
    public ResponseResult<Auction> updateAuction(
            @Parameter(description = "拍卖ID") @PathVariable Long id,
            @Parameter(description = "拍卖信息") @RequestBody Auction auction) {
        try {
            auction.setId(id);
            Auction updatedAuction = auctionService.saveAuction(auction);
            return ResponseResult.success("更新成功", updatedAuction);
        } catch (Exception e) {
            return ResponseResult.error("更新失败: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "删除拍卖")
    public ResponseResult<Void> deleteAuction(
            @Parameter(description = "拍卖ID") @PathVariable Long id) {
        try {
            auctionService.deleteAuction(id);
            return ResponseResult.success("删除成功", null);
        } catch (Exception e) {
            return ResponseResult.error("删除失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/{id}/assets")
    @Operation(summary = "获取拍卖关联的资产")
    public ResponseResult<List<AuctionAsset>> getAuctionAssets(
            @Parameter(description = "拍卖ID") @PathVariable Long id) {
        List<AuctionAsset> auctionAssets = auctionService.getAuctionAssets(id);
        return ResponseResult.success(auctionAssets);
    }
    
    @PostMapping("/{id}/assets")
    @Operation(summary = "为拍卖添加资产")
    public ResponseResult<AuctionAsset> addAssetToAuction(
            @Parameter(description = "拍卖ID") @PathVariable Long id,
            @Parameter(description = "拍卖资产信息") @RequestBody AuctionAsset auctionAsset) {
        try {
            auctionAsset.setAuctionId(id);
            AuctionAsset savedAuctionAsset = auctionService.saveAuctionAsset(auctionAsset);
            // 更新拍卖的总价等信息
            auctionService.updateAuctionPriceInfo(id);
            return ResponseResult.success("添加成功", savedAuctionAsset);
        } catch (Exception e) {
            return ResponseResult.error("添加失败: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}/assets/{assetId}")
    @Operation(summary = "从拍卖中移除资产")
    public ResponseResult<Void> removeAssetFromAuction(
            @Parameter(description = "拍卖ID") @PathVariable Long id,
            @Parameter(description = "资产ID") @PathVariable Long assetId) {
        try {
            auctionService.removeAssetFromAuction(id, assetId);
            // 更新拍卖的总价等信息
            auctionService.updateAuctionPriceInfo(id);
            return ResponseResult.success("移除成功", null);
        } catch (Exception e) {
            return ResponseResult.error("移除失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/assets/{assetId}")
    @Operation(summary = "根据资产ID获取相关拍卖")
    public ResponseResult<List<Auction>> getAuctionsByAssetId(
            @Parameter(description = "资产ID") @PathVariable Long assetId) {
        List<Auction> auctions = auctionService.getAuctionsByAssetId(assetId);
        return ResponseResult.success(auctions);
    }
    
    @GetMapping("/status/{status}")
    @Operation(summary = "根据状态获取拍卖")
    public ResponseResult<List<Auction>> getAuctionsByStatus(
            @Parameter(description = "拍卖状态") @PathVariable Integer status) {
        List<Auction> auctions = auctionService.getAuctionsByStatus(status);
        return ResponseResult.success(auctions);
    }
    
    @GetMapping("/{id}/bids")
    @Operation(summary = "获取拍卖的所有出价")
    public ResponseResult<List<Bid>> getAuctionBids(
            @Parameter(description = "拍卖ID") @PathVariable Long id) {
        List<Bid> bids = auctionService.getBidsByAuctionId(id);
        return ResponseResult.success(bids);
    }
    
    @PostMapping("/{id}/bids")
    @Operation(summary = "为拍卖提交出价")
    public ResponseResult<Bid> submitBid(
            @Parameter(description = "拍卖ID") @PathVariable Long id,
            @Parameter(description = "出价信息") @RequestBody Bid bid) {
        try {
            bid.setAuctionId(id);
            Bid processedBid = auctionService.processBid(id, bid);
            return ResponseResult.success("出价成功", processedBid);
        } catch (Exception e) {
            return ResponseResult.error("出价失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/{id}/start")
    @Operation(summary = "手动开始拍卖")
    public ResponseResult<Auction> startAuction(
            @Parameter(description = "拍卖ID") @PathVariable Long id) {
        try {
            Auction startedAuction = auctionService.startAuction(id);
            return ResponseResult.success("拍卖已开始", startedAuction);
        } catch (Exception e) {
            return ResponseResult.error("启动失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/{id}/end")
    @Operation(summary = "手动结束拍卖")
    public ResponseResult<Auction> endAuction(
            @Parameter(description = "拍卖ID") @PathVariable Long id) {
        try {
            Auction endedAuction = auctionService.endAuction(id);
            return ResponseResult.success("拍卖已结束", endedAuction);
        } catch (Exception e) {
            e.printStackTrace();
            // 即使发生异常，也要返回一个友好的错误消息
            return ResponseResult.error("结束失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/user/{userId}/bids")
    @Operation(summary = "获取用户的出价记录")
    public ResponseResult<List<Bid>> getUserBids(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        List<Bid> bids = auctionService.getBidsByUserId(userId);
        return ResponseResult.success(bids);
    }
    
    @GetMapping("/bids")
    @Operation(summary = "获取所有出价记录")
    public ResponseResult<List<Bid>> getAllBids() {
        List<Bid> bids = auctionService.getAllBids();
        return ResponseResult.success(bids);
    }
}