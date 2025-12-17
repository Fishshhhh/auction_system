package com.auction.system.service;

import com.auction.system.entity.Auction;
import com.auction.system.entity.AuctionAsset;
import com.auction.system.entity.Bid;
import com.auction.system.repository.AuctionRepository;
import com.auction.system.repository.AuctionAssetRepository;
import com.auction.system.repository.BidRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class AuctionService {
    
    @Autowired
    private AuctionRepository auctionRepository;
    
    @Autowired
    private AuctionAssetRepository auctionAssetRepository;
    
    @Autowired
    private BidRepository bidRepository;
    
    @Autowired
    private OrderService orderService;
    
    public List<Auction> getAllAuctions() {
        return auctionRepository.findAll();
    }
    
    public Optional<Auction> getAuctionById(Long id) {
        return auctionRepository.findById(id);
    }
    
    public Auction saveAuction(Auction auction) {
        // 如果拍卖类型未设置，从模板中获取
        if (auction.getAuctionType() == null && auction.getTemplateId() != null) {
            // 这里应该从模板中获取拍卖类型，为了简化直接设置为公开实时竞价
            auction.setAuctionType(Auction.TYPE_OPEN_BIDDING);
        }
        
        return auctionRepository.save(auction);
    }
    
    public AuctionAsset saveAuctionAsset(AuctionAsset auctionAsset) {
        return auctionAssetRepository.save(auctionAsset);
    }
    
    public void removeAssetFromAuction(Long auctionId, Long assetId) {
        // 查找对应的拍卖资产记录
        List<AuctionAsset> auctionAssets = auctionAssetRepository.findByAuctionId(auctionId);
        for (AuctionAsset aa : auctionAssets) {
            if (aa.getAssetId().equals(assetId)) {
                auctionAssetRepository.deleteById(aa.getId());
                break;
            }
        }
    }
    
    /**
     * 更新拍卖的价格信息（起拍价、当前价、保留价）
     * @param auctionId 拍卖ID
     */
    public void updateAuctionPriceInfo(Long auctionId) {
        Optional<Auction> auctionOpt = auctionRepository.findById(auctionId);
        if (!auctionOpt.isPresent()) {
            return;
        }
        
        Auction auction = auctionOpt.get();
        List<AuctionAsset> auctionAssets = auctionAssetRepository.findByAuctionId(auctionId);
        
        // 计算总价
        BigDecimal totalStartPrice = BigDecimal.ZERO;
        BigDecimal totalCurrentPrice = BigDecimal.ZERO;
        BigDecimal totalReservePrice = BigDecimal.ZERO;
        
        for (AuctionAsset asset : auctionAssets) {
            if (asset.getStartPrice() != null) {
                totalStartPrice = totalStartPrice.add(
                    asset.getStartPrice().multiply(new BigDecimal(asset.getQuantity())));
            }
            if (asset.getCurrentPrice() != null) {
                totalCurrentPrice = totalCurrentPrice.add(
                    asset.getCurrentPrice().multiply(new BigDecimal(asset.getQuantity())));
            }
            if (asset.getReservePrice() != null) {
                totalReservePrice = totalReservePrice.add(
                    asset.getReservePrice().multiply(new BigDecimal(asset.getQuantity())));
            }
        }
        
        auction.setStartPrice(totalStartPrice);
        auction.setCurrentPrice(totalCurrentPrice);
        auction.setReservePrice(totalReservePrice);
        
        auctionRepository.save(auction);
    }
    
    public void deleteAuction(Long id) {
        // 删除关联的拍卖资产记录
        auctionAssetRepository.deleteByAuctionId(id);
        auctionRepository.deleteById(id);
    }
    
    public List<Auction> getAuctionsByStatus(Integer status) {
        return auctionRepository.findByAuctionStatusOrderByStartTimeAsc(status);
    }
    
    public List<Auction> getAuctionsByAssetId(Long assetId) {
        List<AuctionAsset> auctionAssets = auctionAssetRepository.findByAssetId(assetId);
        return auctionAssets.stream()
                .map(auctionAsset -> {
                    Optional<Auction> auctionOpt = auctionRepository.findById(auctionAsset.getAuctionId());
                    return auctionOpt.orElse(null);
                })
                .filter(auction -> auction != null)
                .collect(Collectors.toList());
    }
    
    public List<AuctionAsset> getAuctionAssets(Long auctionId) {
        return auctionAssetRepository.findByAuctionId(auctionId);
    }
    
    public List<Bid> getBidsByAuctionId(Long auctionId) {
        return bidRepository.findByAuctionIdOrderByBidPriceDescCreatedTimeAsc(auctionId);
    }
    
    public List<Bid> getBidsByAuctionIdAndStatus(Long auctionId, Integer status) {
        return bidRepository.findByAuctionIdAndBidStatusOrderByBidPriceDescCreatedTimeAsc(auctionId, status);
    }
    
    public Bid saveBid(Bid bid) {
        return bidRepository.save(bid);
    }
    
    public Optional<Bid> getBidById(Long id) {
        return bidRepository.findById(id);
    }
    
    public List<Bid> getBidsByUserId(Long userId) {
        return bidRepository.findByUserIdOrderByCreatedTimeDesc(userId);
    }
    
    /**
     * 处理出价逻辑，根据不同拍卖类型处理
     * @param auctionId 拍卖ID
     * @param bid 出价信息
     * @return 处理后的出价
     */
    public Bid processBid(Long auctionId, Bid bid) throws Exception {
        Optional<Auction> auctionOpt = auctionRepository.findById(auctionId);
        if (!auctionOpt.isPresent()) {
            throw new Exception("拍卖不存在");
        }
        
        Auction auction = auctionOpt.get();
        
        // 检查拍卖状态
        if (!auction.getAuctionStatus().equals(Auction.STATUS_IN_PROGRESS)) {
            throw new Exception("拍卖不在进行中状态");
        }
        
        // 检查拍卖是否已结束
        if (auction.getEndTime() != null && auction.getEndTime().isBefore(java.time.LocalDateTime.now())) {
            throw new Exception("拍卖已结束");
        }
        
        // 根据拍卖类型处理出价
        switch (auction.getAuctionType()) {
            case Auction.TYPE_OPEN_BIDDING: // 公开实时竞价
                return processOpenBidding(auction, bid);
            case Auction.TYPE_SEALED_BID: // 暗拍
                return processSealedBid(auction, bid);
            case Auction.TYPE_NO_RESERVE: // 无底价
                return processNoReserve(auction, bid);
            case Auction.TYPE_DIRECTED: // 定向
                return processDirected(auction, bid);
            case Auction.TYPE_DESCENDING: // 降价
                return processDescending(auction, bid);
            default:
                throw new Exception("不支持的拍卖类型");
        }
    }
    
    /**
     * 处理公开实时竞价模式
     * @param auction 拍卖信息
     * @param bid 出价信息
     * @return 处理后的出价
     */
    private Bid processOpenBidding(Auction auction, Bid bid) throws Exception {
        // 检查出价是否高于当前价格
        if (bid.getBidPrice().compareTo(auction.getCurrentPrice()) <= 0) {
            throw new Exception("出价必须高于当前价格");
        }
        
        // 检查加价幅度
        if (auction.getBidIncrement() != null && auction.getBidIncrement().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal priceDifference = bid.getBidPrice().subtract(auction.getCurrentPrice());
            if (priceDifference.compareTo(auction.getBidIncrement()) < 0) {
                throw new Exception("加价幅度不能小于" + auction.getBidIncrement());
            }
        }
        
        // 保存出价
        bid.setAuctionId(auction.getId());
        bid.setBidStatus(1); // 有效
        Bid savedBid = bidRepository.save(bid);
        
        // 更新拍卖当前价格
        auction.setCurrentPrice(bid.getBidPrice());
        auctionRepository.save(auction);
        
        // 检查是否需要延时
        checkAndExtendAuctionTime(auction, bid.getCreatedTime());
        
        return savedBid;
    }
    
    /**
     * 处理暗拍模式
     * @param auction 拍卖信息
     * @param bid 出价信息
     * @return 处理后的出价
     */
    private Bid processSealedBid(Auction auction, Bid bid) throws Exception {
        // 暗拍模式下，出价只能提交一次
        List<Bid> existingBids = bidRepository.findByAuctionIdAndBidStatusOrderByBidPriceDescCreatedTimeAsc(
            auction.getId(), 1);
        for (Bid existingBid : existingBids) {
            if (existingBid.getUserId().equals(bid.getUserId())) {
                throw new Exception("您已经提交过报价，暗拍模式下只能报价一次");
            }
        }
        
        // 保存出价（但不出现在公开列表中）
        bid.setAuctionId(auction.getId());
        bid.setBidStatus(3); // 密封状态
        return bidRepository.save(bid);
    }
    
    /**
     * 处理无底价拍卖模式
     * @param auction 拍卖信息
     * @param bid 出价信息
     * @return 处理后的出价
     */
    private Bid processNoReserve(Auction auction, Bid bid) throws Exception {
        // 检查出价是否高于或等于当前价格
        if (bid.getBidPrice().compareTo(auction.getCurrentPrice()) < 0) {
            throw new Exception("出价不能低于当前价格");
        }
        
        // 检查加价幅度
        if (auction.getBidIncrement() != null && auction.getBidIncrement().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal priceDifference = bid.getBidPrice().subtract(auction.getCurrentPrice());
            if (priceDifference.compareTo(auction.getBidIncrement()) < 0) {
                throw new Exception("加价幅度不能小于" + auction.getBidIncrement());
            }
        }
        
        // 保存出价
        bid.setAuctionId(auction.getId());
        bid.setBidStatus(1); // 有效
        Bid savedBid = bidRepository.save(bid);
        
        // 更新拍卖当前价格
        auction.setCurrentPrice(bid.getBidPrice());
        auctionRepository.save(auction);
        
        // 检查是否需要延时
        checkAndExtendAuctionTime(auction, bid.getCreatedTime());
        
        return savedBid;
    }
    
    /**
     * 处理定向拍卖模式
     * @param auction 拍卖信息
     * @param bid 出价信息
     * @return 处理后的出价
     */
    private Bid processDirected(Auction auction, Bid bid) throws Exception {
        // 检查用户是否有资格参与定向拍卖
        if (auction.getQualificationRequired() != null && auction.getQualificationRequired()) {
            // 这里应该检查用户的资格，为了简化我们假设所有用户都有资格
            // 实际项目中应该查询用户资格信息
        }
        
        // 检查出价是否高于当前价格
        if (bid.getBidPrice().compareTo(auction.getCurrentPrice()) <= 0) {
            throw new Exception("出价必须高于当前价格");
        }
        
        // 检查加价幅度
        if (auction.getBidIncrement() != null && auction.getBidIncrement().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal priceDifference = bid.getBidPrice().subtract(auction.getCurrentPrice());
            if (priceDifference.compareTo(auction.getBidIncrement()) < 0) {
                throw new Exception("加价幅度不能小于" + auction.getBidIncrement());
            }
        }
        
        // 保存出价
        bid.setAuctionId(auction.getId());
        bid.setBidStatus(1); // 有效
        Bid savedBid = bidRepository.save(bid);
        
        // 更新拍卖当前价格
        auction.setCurrentPrice(bid.getBidPrice());
        auctionRepository.save(auction);
        
        // 检查是否需要延时
        checkAndExtendAuctionTime(auction, bid.getCreatedTime());
        
        return savedBid;
    }
    
    /**
     * 处理降价拍卖模式
     * @param auction 拍卖信息
     * @param bid 出价信息
     * @return 处理后的出价
     */
    private Bid processDescending(Auction auction, Bid bid) throws Exception {
        // 降价拍卖中，用户接受当前价格即成交
        // 检查出价是否等于当前价格
        if (bid.getBidPrice().compareTo(auction.getCurrentPrice()) != 0) {
            throw new Exception("出价必须等于当前价格");
        }
        
        // 保存出价
        bid.setAuctionId(auction.getId());
        bid.setBidStatus(1); // 有效
        Bid savedBid = bidRepository.save(bid);
        
        // 设置拍卖获胜者和最终价格
        auction.setWinnerUserId(bid.getUserId());
        auction.setFinalPrice(bid.getBidPrice());
        auction.setAuctionStatus(Auction.STATUS_SOLD);
        auctionRepository.save(auction);
        
        return savedBid;
    }
    
    /**
     * 检查并延长拍卖时间（用于公开竞价、无底价和定向拍卖）
     * @param auction 拍卖信息
     * @param bidTime 出价时间
     */
    private void checkAndExtendAuctionTime(Auction auction, LocalDateTime bidTime) {
        // 检查是否需要延时
        if (auction.getExtendThreshold() != null && auction.getExtendThreshold() > 0 
                && auction.getEndTime() != null && auction.getExtendTime() != null) {
            // 计算距离结束时间的秒数
            long secondsToEnd = java.time.Duration.between(bidTime, auction.getEndTime()).getSeconds();
            
            // 如果在阈值范围内出价，则延长拍卖时间
            if (secondsToEnd <= auction.getExtendThreshold()) {
                LocalDateTime newEndTime = auction.getEndTime().plusMinutes(auction.getExtendTime());
                auction.setEndTime(newEndTime);
                auctionRepository.save(auction);
            }
        }
    }
    
    /**
     * 手动开始拍卖
     * @param auctionId 拍卖ID
     * @return 更新后的拍卖
     */
    public Auction startAuction(Long auctionId) throws Exception {
        Optional<Auction> auctionOpt = auctionRepository.findById(auctionId);
        if (!auctionOpt.isPresent()) {
            throw new Exception("拍卖不存在");
        }
        
        Auction auction = auctionOpt.get();
        if (auction.getAuctionStatus() != Auction.STATUS_NOT_STARTED) {
            throw new Exception("只有未开始的拍卖才能手动开始");
        }
        
        auction.setAuctionStatus(Auction.STATUS_IN_PROGRESS);
        auction.setStartTime(LocalDateTime.now());
        return auctionRepository.save(auction);
    }
    
    /**
     * 手动结束拍卖
     * @param auctionId 拍卖ID
     * @return 更新后的拍卖
     */
    public Auction endAuction(Long auctionId) throws Exception {
        Optional<Auction> auctionOpt = auctionRepository.findById(auctionId);
        if (!auctionOpt.isPresent()) {
            throw new Exception("拍卖不存在");
        }
        
        Auction auction = auctionOpt.get();
        if (auction.getAuctionStatus() != Auction.STATUS_IN_PROGRESS) {
            throw new Exception("只有进行中的拍卖才能手动结束");
        }
        
        finishAuction(auction);
        return auctionRepository.save(auction);
    }
    
    /**
     * 结束拍卖并确定获胜者
     * @param auction 拍卖对象
     */
    private void finishAuction(Auction auction) {
        // 根据拍卖类型处理结束逻辑
        switch (auction.getAuctionType()) {
            case Auction.TYPE_OPEN_BIDDING: // 公开实时竞价
            case Auction.TYPE_NO_RESERVE: // 无底价
            case Auction.TYPE_DIRECTED: // 定向
                finishStandardAuction(auction);
                break;
            case Auction.TYPE_SEALED_BID: // 暗拍
                finishSealedBidAuction(auction);
                break;
            case Auction.TYPE_DESCENDING: // 降价拍卖
                // 降价拍卖在有人接受价格时就已经结束了
                if (auction.getAuctionStatus() != Auction.STATUS_SOLD) {
                    auction.setAuctionStatus(Auction.STATUS_UNSOLD);
                    auction.setEndTime(LocalDateTime.now());
                }
                break;
            default:
                auction.setAuctionStatus(Auction.STATUS_UNSOLD);
                auction.setEndTime(LocalDateTime.now());
                break;
        }
    }
    
    /**
     * 结束标准拍卖（公开竞价、无底价、定向）
     * @param auction 拍卖对象
     */
    private void finishStandardAuction(Auction auction) {
        // 获取有效的出价
        List<Bid> validBids = getBidsByAuctionIdAndStatus(auction.getId(), 1);
        
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
            auction.setEndTime(LocalDateTime.now());
            
            // 更新获胜者的出价状态
            winningBid.setBidStatus(3); // 胜出
            saveBid(winningBid);
            
            // 更新其他出价状态
            for (int i = 1; i < sortedBids.size(); i++) {
                Bid losingBid = sortedBids.get(i);
                losingBid.setBidStatus(4); // 失败
                saveBid(losingBid);
            }
            
            // 创建订单
            try {
                orderService.createOrderFromAuction(auction, this);
            } catch (Exception e) {
                // 记录错误但不影响拍卖状态更新
                e.printStackTrace();
            }
        } else {
            // 流拍
            auction.setAuctionStatus(Auction.STATUS_UNSOLD);
            auction.setEndTime(LocalDateTime.now());
        }
    }
    
    /**
     * 结束暗拍
     * @param auction 拍卖对象
     */
    private void finishSealedBidAuction(Auction auction) {
        // 获取密封的出价
        List<Bid> sealedBids = getBidsByAuctionIdAndStatus(auction.getId(), 3);
        
        // 解密出价并按出价降序、时间升序排序
        List<Bid> sortedBids = sealedBids.stream()
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
            // 更新出价状态为有效
            winningBid.setBidStatus(3); // 胜出
            saveBid(winningBid);
            
            auction.setAuctionStatus(Auction.STATUS_SOLD);
            auction.setWinnerUserId(winningBid.getUserId());
            auction.setFinalPrice(winningBid.getBidPrice());
            auction.setEndTime(LocalDateTime.now());
            
            // 更新其他出价状态
            for (int i = 1; i < sortedBids.size(); i++) {
                Bid losingBid = sortedBids.get(i);
                losingBid.setBidStatus(4); // 失败
                saveBid(losingBid);
            }
            
            // 创建订单
            try {
                orderService.createOrderFromAuction(auction, this);
            } catch (Exception e) {
                // 记录错误但不影响拍卖状态更新
                e.printStackTrace();
            }
        } else {
            // 流拍
            auction.setAuctionStatus(Auction.STATUS_UNSOLD);
            auction.setEndTime(LocalDateTime.now());
            
            // 更新所有出价状态为失败
            for (Bid bid : sealedBids) {
                bid.setBidStatus(4); // 失败
                saveBid(bid);
            }
        }
    }
    
    /**
     * 降价拍卖定时任务 - 更新价格
     * @param auction 拍卖对象
     */
    public void updateDescendingAuctionPrice(Auction auction) {
        if (auction.getAuctionType() != Auction.TYPE_DESCENDING) {
            return;
        }
        
        // 检查是否到达下次降价时间
        if (auction.getNextPriceDropTime() != null && 
            LocalDateTime.now().isAfter(auction.getNextPriceDropTime())) {
            
            // 计算新价格
            BigDecimal newPrice = auction.getCurrentPrice().subtract(auction.getPriceStep());
            
            // 检查是否达到最低价
            if (newPrice.compareTo(auction.getMinPrice()) >= 0) {
                auction.setCurrentPrice(newPrice);
                
                // 设置下次降价时间
                if (auction.getPriceDropInterval() != null && auction.getPriceDropInterval() > 0) {
                    auction.setNextPriceDropTime(
                        auction.getNextPriceDropTime().plusMinutes(auction.getPriceDropInterval()));
                }
                
                auctionRepository.save(auction);
            } else {
                // 达到最低价仍未成交，流拍
                auction.setAuctionStatus(Auction.STATUS_UNSOLD);
                auction.setEndTime(LocalDateTime.now());
                auctionRepository.save(auction);
            }
        }
    }
}