package com.auction.system.service;

import com.auction.system.entity.ScheduledTask;
import com.auction.system.repository.ScheduledTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ScheduledTaskService {
    
    @Autowired
    private ScheduledTaskRepository scheduledTaskRepository;
    
    public List<ScheduledTask> getAllTasks() {
        return scheduledTaskRepository.findAll();
    }
    
    public Optional<ScheduledTask> getTaskById(Long id) {
        return scheduledTaskRepository.findById(id);
    }
    
    public Optional<ScheduledTask> getTaskByName(String taskName) {
        return scheduledTaskRepository.findByTaskName(taskName);
    }
    
    public ScheduledTask saveTask(ScheduledTask task) {
        return scheduledTaskRepository.save(task);
    }
    
    public void deleteTask(Long id) {
        scheduledTaskRepository.deleteById(id);
    }
    
    public List<ScheduledTask> getEnabledTasks() {
        return (List<ScheduledTask>) scheduledTaskRepository.findByTaskStatus(ScheduledTask.STATUS_ENABLED);
    }
    
    /**
     * 更新任务的最后执行时间
     * @param taskId 任务ID
     * @return 更新后的任务
     */
    public ScheduledTask updateLastExecutionTime(Long taskId) {
        Optional<ScheduledTask> taskOpt = scheduledTaskRepository.findById(taskId);
        if (taskOpt.isPresent()) {
            ScheduledTask task = taskOpt.get();
            task.setLastExecutionTime(LocalDateTime.now());
            return scheduledTaskRepository.save(task);
        }
        return null;
    }
}