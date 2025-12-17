package com.auction.system.controller;

import com.auction.system.common.ResponseResult;
import com.auction.system.entity.AssetCategory;
import com.auction.system.entity.AuctionTemplate;
import com.auction.system.entity.SystemConfig;
import com.auction.system.service.AssetCategoryService;
import com.auction.system.service.AuctionTemplateService;
import com.auction.system.service.SystemConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/config")
@CrossOrigin
@Tag(name = "基础配置模块")
public class ConfigController {
    
    @Autowired
    private AssetCategoryService assetCategoryService;
    
    @Autowired
    private AuctionTemplateService auctionTemplateService;
    
    @Autowired
    private SystemConfigService systemConfigService;
    
    // 资产分类相关接口
    @GetMapping("/categories")
    @Operation(summary = "获取所有资产分类")
    public ResponseResult<List<AssetCategory>> getCategories() {
        List<AssetCategory> categories = assetCategoryService.getAllCategories();
        return ResponseResult.success(categories);
    }

    @PostMapping("/categories")
    @Operation(summary = "创建资产分类")
    public ResponseResult<AssetCategory> createCategory(
            @Parameter(description = "资产分类信息") @RequestBody AssetCategory category) {
        try {
            AssetCategory savedCategory = assetCategoryService.saveCategory(category);
            return ResponseResult.success("创建成功", savedCategory);
        } catch (Exception e) {
            return ResponseResult.error("创建失败: " + e.getMessage());
        }
    }
    
    @PutMapping("/categories/{id}")
    @Operation(summary = "更新资产分类")
    public ResponseResult<AssetCategory> updateCategory(
            @Parameter(description = "分类ID") @PathVariable Long id, 
            @Parameter(description = "资产分类信息") @RequestBody AssetCategory category) {
        try {
            category.setId(id);
            AssetCategory updatedCategory = assetCategoryService.saveCategory(category);
            return ResponseResult.success("更新成功", updatedCategory);
        } catch (Exception e) {
            return ResponseResult.error("更新失败: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/categories/{id}")
    @Operation(summary = "删除资产分类")
    public ResponseResult<Void> deleteCategory(
            @Parameter(description = "分类ID") @PathVariable Long id) {
        try {
            assetCategoryService.deleteCategory(id);
            return ResponseResult.success("删除成功", null);
        } catch (Exception e) {
            return ResponseResult.error("删除失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/categories/search")
    @Operation(summary = "搜索资产分类")
    public ResponseResult<List<AssetCategory>> searchCategories(
            @Parameter(description = "搜索关键词") @RequestParam String keyword) {
        List<AssetCategory> categories = assetCategoryService.searchCategories(keyword);
        return ResponseResult.success(categories);
    }
    
    // 竞拍模板相关接口
    @GetMapping("/templates")
    @Operation(summary = "获取所有竞拍模板")
    public ResponseResult<List<AuctionTemplate>> getTemplates() {
        List<AuctionTemplate> templates = auctionTemplateService.getAllTemplates();
        return ResponseResult.success(templates);
    }
    
    @PostMapping("/templates")
    @Operation(summary = "创建竞拍模板")
    public ResponseResult<AuctionTemplate> createTemplate(
            @Parameter(description = "竞拍模板信息") @RequestBody AuctionTemplate template) {
        try {
            AuctionTemplate savedTemplate = auctionTemplateService.saveTemplate(template);
            return ResponseResult.success("创建成功", savedTemplate);
        } catch (Exception e) {
            return ResponseResult.error("创建失败: " + e.getMessage());
        }
    }
    
    @PutMapping("/templates/{id}")
    @Operation(summary = "更新竞拍模板")
    public ResponseResult<AuctionTemplate> updateTemplate(
            @Parameter(description = "模板ID") @PathVariable Long id, 
            @Parameter(description = "竞拍模板信息") @RequestBody AuctionTemplate template) {
        try {
            template.setId(id);
            AuctionTemplate updatedTemplate = auctionTemplateService.saveTemplate(template);
            return ResponseResult.success("更新成功", updatedTemplate);
        } catch (Exception e) {
            return ResponseResult.error("更新失败: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/templates/{id}")
    @Operation(summary = "删除竞拍模板")
    public ResponseResult<Void> deleteTemplate(
            @Parameter(description = "模板ID") @PathVariable Long id) {
        try {
            auctionTemplateService.deleteTemplate(id);
            return ResponseResult.success("删除成功", null);
        } catch (Exception e) {
            return ResponseResult.error("删除失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/templates/type/{type}")
    @Operation(summary = "根据类型获取竞拍模板")
    public ResponseResult<List<AuctionTemplate>> getTemplatesByType(
            @Parameter(description = "竞拍类型") @PathVariable Integer type) {
        List<AuctionTemplate> templates = auctionTemplateService.getTemplatesByType(type);
        return ResponseResult.success(templates);
    }
    
    // 系统配置相关接口
    @GetMapping("/system")
    @Operation(summary = "获取所有系统配置")
    public ResponseResult<List<SystemConfig>> getSystemConfigs() {
        List<SystemConfig> configs = systemConfigService.getAllConfigs();
        return ResponseResult.success(configs);
    }
    
    @GetMapping("/system/key/{configKey}")
    @Operation(summary = "根据配置键获取系统配置")
    public ResponseResult<SystemConfig> getSystemConfigByKey(
            @Parameter(description = "配置键") @PathVariable String configKey) {
        return systemConfigService.getConfigByKey(configKey)
                .map(config -> ResponseResult.success(config))
                .orElse(ResponseResult.error("配置不存在"));
    }
    
    @GetMapping("/system/group/{configGroup}")
    @Operation(summary = "根据配置组获取系统配置")
    public ResponseResult<List<SystemConfig>> getSystemConfigsByGroup(
            @Parameter(description = "配置组") @PathVariable String configGroup) {
        List<SystemConfig> configs = systemConfigService.getActiveConfigsByGroup(configGroup);
        return ResponseResult.success(configs);
    }
    
    @PostMapping("/system")
    @Operation(summary = "创建系统配置")
    public ResponseResult<SystemConfig> createSystemConfig(
            @Parameter(description = "系统配置信息") @RequestBody SystemConfig config) {
        try {
            SystemConfig savedConfig = systemConfigService.saveConfig(config);
            return ResponseResult.success("创建成功", savedConfig);
        } catch (Exception e) {
            return ResponseResult.error("创建失败: " + e.getMessage());
        }
    }
    
    @PutMapping("/system/{id}")
    @Operation(summary = "更新系统配置")
    public ResponseResult<SystemConfig> updateSystemConfig(
            @Parameter(description = "配置ID") @PathVariable Long id,
            @Parameter(description = "系统配置信息") @RequestBody SystemConfig config) {
        try {
            config.setId(id);
            SystemConfig updatedConfig = systemConfigService.saveConfig(config);
            return ResponseResult.success("更新成功", updatedConfig);
        } catch (Exception e) {
            return ResponseResult.error("更新失败: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/system/{id}")
    @Operation(summary = "删除系统配置")
    public ResponseResult<Void> deleteSystemConfig(
            @Parameter(description = "配置ID") @PathVariable Long id) {
        try {
            systemConfigService.deleteConfig(id);
            return ResponseResult.success("删除成功", null);
        } catch (Exception e) {
            return ResponseResult.error("删除失败: " + e.getMessage());
        }
    }
}