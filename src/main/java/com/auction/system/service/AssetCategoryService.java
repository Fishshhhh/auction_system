package com.auction.system.service;

import com.auction.system.entity.AssetCategory;
import com.auction.system.repository.AssetCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AssetCategoryService {
    
    @Autowired
    private AssetCategoryRepository assetCategoryRepository;
    
    public List<AssetCategory> getAllCategories() {
        return assetCategoryRepository.findByStatusOrderByCreatedTimeDesc(1);
    }
    
    public Page<AssetCategory> getCategoriesWithPagination(Pageable pageable) {
        return assetCategoryRepository.findAll(pageable);
    }
    
    public AssetCategory saveCategory(AssetCategory category) {
        // 检查编码是否已存在
        if (category.getId() == null && assetCategoryRepository.findByCode(category.getCode()).isPresent()) {
            throw new RuntimeException("分类编码已存在");
        }
        return assetCategoryRepository.save(category);
    }
    
    public Optional<AssetCategory> getCategoryById(Long id) {
        return assetCategoryRepository.findById(id);
    }
    
    public void deleteCategory(Long id) {
        assetCategoryRepository.deleteById(id);
    }
    
    public List<AssetCategory> searchCategories(String keyword) {
        return assetCategoryRepository.findByNameContainingIgnoreCase(keyword);
    }
}