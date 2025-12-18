package com.auction.system.controller;

import com.auction.system.common.ResponseResult;
import com.auction.system.entity.ScheduledTask;
import com.auction.system.service.ScheduledTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/scheduled-tasks")
@CrossOrigin
@Tag(name = "定时任务管理")
public class ScheduledTaskController {
    
    @Autowired
    private ScheduledTaskService scheduledTaskService;
    
    @GetMapping
    @Operation(summary = "获取所有定时任务")
    public ResponseResult<List<ScheduledTask>> getAllTasks() {
        List<ScheduledTask> tasks = scheduledTaskService.getAllTasks();
        return ResponseResult.success(tasks);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取定时任务详情")
    public ResponseResult<ScheduledTask> getTaskById(
            @Parameter(description = "任务ID") @PathVariable Long id) {
        return scheduledTaskService.getTaskById(id)
                .map(task -> ResponseResult.success(task))
                .orElse(ResponseResult.error("任务不存在"));
    }
    
    @PostMapping
    @Operation(summary = "创建定时任务")
    public ResponseResult<ScheduledTask> createTask(@RequestBody ScheduledTask task) {
        // 设置创建时间
        task.setId(null);
        ScheduledTask savedTask = scheduledTaskService.saveTask(task);
        return ResponseResult.success(savedTask);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "更新定时任务")
    public ResponseResult<ScheduledTask> updateTask(
            @Parameter(description = "任务ID") @PathVariable Long id,
            @RequestBody ScheduledTask task) {
        Optional<ScheduledTask> existingTaskOpt = scheduledTaskService.getTaskById(id);
        if (!existingTaskOpt.isPresent()) {
            return ResponseResult.error("任务不存在");
        }
        
        ScheduledTask existingTask = existingTaskOpt.get();
        // 更新任务信息
        existingTask.setTaskName(task.getTaskName());
        existingTask.setTaskDescription(task.getTaskDescription());
        existingTask.setCronExpression(task.getCronExpression());
        existingTask.setTaskStatus(task.getTaskStatus());
        
        ScheduledTask updatedTask = scheduledTaskService.saveTask(existingTask);
        return ResponseResult.success(updatedTask);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "删除定时任务")
    public ResponseResult<Void> deleteTask(@Parameter(description = "任务ID") @PathVariable Long id) {
        Optional<ScheduledTask> taskOpt = scheduledTaskService.getTaskById(id);
        if (!taskOpt.isPresent()) {
            return ResponseResult.error("任务不存在");
        }
        
        scheduledTaskService.deleteTask(id);
        return ResponseResult.success(null);
    }
    
    @PostMapping("/{id}/execute")
    @Operation(summary = "立即执行定时任务")
    public ResponseResult<Void> executeTask(@Parameter(description = "任务ID") @PathVariable Long id) {
        Optional<ScheduledTask> taskOpt = scheduledTaskService.getTaskById(id);
        if (!taskOpt.isPresent()) {
            return ResponseResult.error("任务不存在");
        }
        
        // 这里应该是触发任务执行的逻辑
        // 在实际应用中，我们会通过某种机制触发任务执行
        // 目前我们只是更新最后执行时间来模拟
        scheduledTaskService.updateLastExecutionTime(id);
        
        return ResponseResult.success(null);
    }
}