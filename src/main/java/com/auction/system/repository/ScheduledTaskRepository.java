package com.auction.system.repository;

import com.auction.system.entity.ScheduledTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ScheduledTaskRepository extends JpaRepository<ScheduledTask, Long> {
    Optional<ScheduledTask> findByTaskName(String taskName);
    
    Iterable<ScheduledTask> findByTaskStatus(Integer taskStatus);
}