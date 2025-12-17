package com.auction.system.service;

import com.auction.system.entity.Auction;
import com.auction.system.entity.Bid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuctionSchedulerService {
    
    @Autowired
    private AuctionService auctionService;
    
    @Autowired
    private OrderService orderService;
    
    /**
     * 每分钟检查一次拍卖状态
     * 更新未开始的拍卖为进行中
     * 更新已结束的拍卖为完成状态
     */
    @Scheduled(fixedRate = 60000) // 每分钟执行一次
    public void updateAuctionStatuses() {
        // 获取所有未开始且到达开始时间的拍卖
        List<Auction> notStartedAuctions = auctionService.getAuctionsByStatus(Auction.STATUS_NOT_STARTED);
        LocalDateTime now = LocalDateTime.now();
        
        // 更新应该开始的拍卖状态
        for (Auction auction : notStartedAuctions) {
            if (auction.getStartTime() != null && !auction.getStartTime().isAfter(now)) {
                auction.setAuctionStatus(Auction.STATUS_IN_PROGRESS);
                auctionService.saveAuction(auction);
            }
        }
        
        // 获取所有进行中且已到达结束时间的拍卖
        List<Auction> inProgressAuctions = auctionService.getAuctionsByStatus(Auction.STATUS_IN_PROGRESS);
        
        // 更新应该结束的拍卖状态
        for (Auction auction : inProgressAuctions) {
            if (auction.getEndTime() != null && !auction.getEndTime().isAfter(now)) {
                finishAuction(auction);
            }
        }
    }
    
    /**
     * 结束拍卖并确定获胜者
     * @param auction 拍卖对象
     */
    private void finishAuction(Auction auction) {
        // 获取有效的出价
        List<Bid> validBids = auctionService.getBidsByAuctionIdAndStatus(auction.getId(), 1);
        
        // 按出价降序、时间升序排序（价格高者优先，同价时时间早者优先）
        List<Bid> sortedBids = validBids.stream()
                .sorted((b1, b2) -> {
                    int priceComparison = b2.getBidPrice().compareTo(b1.getBidPrice());
                    if (priceComparison != 0) {
                        return priceComparison;
                    }
                    return b1.getCreatedTime().compareTo(b2.getCreatedTime());
                })
                .collect(Collectors.toList());
        
        // 检查是否有出价，并且最高出价是否满足保留价
        if (!sortedBids.isEmpty() && 
            (auction.getReservePrice() == null || 
             sortedBids.get(0).getBidPrice().compareTo(auction.getReservePrice()) >= 0)) {
            // 拍卖成功
            Bid winningBid = sortedBids.get(0);
            auction.setAuctionStatus(Auction.STATUS_SOLD);
            auction.setWinnerUserId(winningBid.getUserId());
            auction.setFinalPrice(winningBid.getBidPrice());
            
            // 更新获胜者的出价状态
            winningBid.setBidStatus(3); // 胜出
            auctionService.saveBid(winningBid);
            
            // 更新其他出价状态
            for (int i = 1; i < sortedBids.size(); i++) {
                Bid losingBid = sortedBids.get(i);
                losingBid.setBidStatus(4); // 失败
                auctionService.saveBid(losingBid);
            }
            
            // 创建订单
            try {
                orderService.createOrderFromAuction(auction, auctionService);
            } catch (Exception e) {
                // 记录错误但不影响拍卖状态更新
                e.printStackTrace();
            }
        } else {
            // 流拍
            auction.setAuctionStatus(Auction.STATUS_UNSOLD);
        }
        
        auctionService.saveAuction(auction);
    }
}