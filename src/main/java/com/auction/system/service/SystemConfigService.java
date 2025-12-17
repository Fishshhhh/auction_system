package com.auction.system.service;

import com.auction.system.entity.SystemConfig;
import com.auction.system.repository.SystemConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SystemConfigService {
    
    @Autowired
    private SystemConfigRepository systemConfigRepository;
    
    public List<SystemConfig> getAllConfigs() {
        return systemConfigRepository.findAll();
    }
    
    public Optional<SystemConfig> getConfigByKey(String configKey) {
        return systemConfigRepository.findByConfigKey(configKey);
    }
    
    public List<SystemConfig> getConfigsByGroup(String configGroup) {
        return systemConfigRepository.findByConfigGroup(configGroup);
    }
    
    public List<SystemConfig> getActiveConfigsByGroup(String configGroup) {
        return systemConfigRepository.findByConfigGroupAndStatus(configGroup, 1);
    }
    
    public SystemConfig saveConfig(SystemConfig config) {
        return systemConfigRepository.save(config);
    }
    
    public void deleteConfig(Long id) {
        systemConfigRepository.deleteById(id);
    }
    
    public List<SystemConfig> getConfigsByKeyPrefix(String prefix) {
        return systemConfigRepository.findByConfigKeyStartingWith(prefix);
    }
}