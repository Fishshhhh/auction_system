package com.auction.system.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
public class AuctionWithAsset extends Auction {
    private Long assetId;
    private Integer quantity;
    private BigDecimal startPrice;
    private BigDecimal currentPrice;
    private BigDecimal reservePrice;
}