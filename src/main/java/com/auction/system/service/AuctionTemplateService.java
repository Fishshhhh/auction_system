package com.auction.system.service;

import com.auction.system.entity.AuctionTemplate;
import com.auction.system.repository.AuctionTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AuctionTemplateService {
    
    @Autowired
    private AuctionTemplateRepository auctionTemplateRepository;
    
    public List<AuctionTemplate> getAllTemplates() {
        return auctionTemplateRepository.findByStatusOrderByCreatedTimeDesc(1);
    }
    
    public AuctionTemplate saveTemplate(AuctionTemplate template) {
        return auctionTemplateRepository.save(template);
    }
    
    public Optional<AuctionTemplate> getTemplateById(Long id) {
        return auctionTemplateRepository.findById(id);
    }
    
    public void deleteTemplate(Long id) {
        auctionTemplateRepository.deleteById(id);
    }
    
    public List<AuctionTemplate> getTemplatesByType(Integer type) {
        return auctionTemplateRepository.findByAuctionTypeAndStatusOrderByCreatedTimeDesc(type, 1);
    }
    
    public List<AuctionTemplate> getDefaultTemplates() {
        return auctionTemplateRepository.findByIsDefaultAndStatusOrderByCreatedTimeDesc(1, 1);
    }
}