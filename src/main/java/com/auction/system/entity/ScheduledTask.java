package com.auction.system.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "scheduled_task")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "task_name", nullable = false, length = 100)
    private String taskName;
    
    @Column(name = "task_description", length = 500)
    private String taskDescription;
    
    @Column(name = "cron_expression", length = 100)
    private String cronExpression;
    
    @Column(name = "task_status")
    private Integer taskStatus = 1; // 1-启用 0-禁用
    
    @Column(name = "last_execution_time")
    private LocalDateTime lastExecutionTime;
    
    @Column(name = "created_time")
    private LocalDateTime createdTime;
    
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;
    
    // 任务状态常量
    public static final int STATUS_ENABLED = 1;   // 启用
    public static final int STATUS_DISABLED = 0;  // 禁用
}