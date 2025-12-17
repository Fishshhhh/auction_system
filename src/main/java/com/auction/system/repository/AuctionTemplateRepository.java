package com.auction.system.repository;

import com.auction.system.entity.AuctionTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AuctionTemplateRepository extends JpaRepository<AuctionTemplate, Long> {
    List<AuctionTemplate> findByAuctionTypeAndStatusOrderByCreatedTimeDesc(Integer auctionType, Integer status);
    List<AuctionTemplate> findByIsDefaultAndStatusOrderByCreatedTimeDesc(Integer isDefault, Integer status);
    List<AuctionTemplate> findByStatusOrderByCreatedTimeDesc(Integer status);
    List<AuctionTemplate> findByNameContainingIgnoreCase(String name);
}