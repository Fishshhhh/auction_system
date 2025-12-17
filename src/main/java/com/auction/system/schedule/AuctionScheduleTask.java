package com.auction.system.schedule;

import com.auction.system.entity.Auction;
import com.auction.system.service.AuctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class AuctionScheduleTask {
    
    @Autowired
    private AuctionService auctionService;
    
    /**
     * 每分钟检查一次降价拍卖的价格更新
     */
    @Scheduled(fixedRate = 60000) // 每分钟执行一次
    public void updateDescendingAuctionPrices() {
        try {
            // 获取所有进行中的降价拍卖
            List<Auction> descendingAuctions = auctionService.getAuctionsByStatus(Auction.STATUS_IN_PROGRESS)
                .stream()
                .filter(auction -> auction.getAuctionType() == Auction.TYPE_DESCENDING)
                .collect(java.util.stream.Collectors.toList());
            
            // 更新每个降价拍卖的价格
            for (Auction auction : descendingAuctions) {
                auctionService.updateDescendingAuctionPrice(auction);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}