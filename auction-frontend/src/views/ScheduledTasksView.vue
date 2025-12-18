<template>
  <div class="scheduled-tasks-view">
    <el-card>
      <div slot="header" class="card-header">
        <span>定时任务管理</span>
        <div>
          <el-button type="primary" @click="showCreateDialog">添加任务</el-button>
        </div>
      </div>
      
      <el-table :data="tasks" stripe style="width: 100%">
        <el-table-column prop="taskName" label="任务名称" min-width="150"></el-table-column>
        <el-table-column prop="taskDescription" label="任务描述" min-width="200"></el-table-column>
        <el-table-column prop="cronExpression" label="执行频率(Cron)" min-width="150"></el-table-column>
        <el-table-column label="执行状态" min-width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.taskStatus === 1 ? 'success' : 'info'">
              {{ scope.row.taskStatus === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="最后执行时间" min-width="150">
          <template slot-scope="scope">
            {{ scope.row.lastExecutionTime ? formatDate(scope.row.lastExecutionTime) : '从未执行' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="200">
          <template slot-scope="scope">
            <el-button 
              size="small" 
              type="primary" 
              @click="editTask(scope.row)">
              编辑
            </el-button>
            <el-button 
              size="small" 
              type="warning" 
              @click="executeTask(scope.row)">
              立即执行
            </el-button>
            <el-button 
              size="small" 
              type="danger" 
              @click="deleteTask(scope.row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <!-- 创建/编辑定时任务对话框 -->
    <el-dialog :visible.sync="dialogVisible" :title="editingTask ? '编辑定时任务' : '创建定时任务'" width="600px">
      <el-form :model="taskForm" label-width="100px" ref="taskForm">
        <el-form-item label="任务名称" prop="taskName">
          <el-input v-model="taskForm.taskName" />
        </el-form-item>
        <el-form-item label="任务描述" prop="taskDescription">
          <el-input v-model="taskForm.taskDescription" type="textarea" />
        </el-form-item>
        <el-form-item label="Cron表达式" prop="cronExpression">
          <el-input v-model="taskForm.cronExpression" />
        </el-form-item>
        <el-form-item label="任务状态" prop="taskStatus">
          <el-switch
            v-model="taskForm.taskStatus"
            :active-value="1"
            :inactive-value="0"
            active-text="启用"
            inactive-text="禁用">
          </el-switch>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveTask">保存</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { scheduledTaskApi } from '../api/scheduledTaskApi'

export default {
  name: 'ScheduledTasksView',
  data() {
    return {
      tasks: [],
      dialogVisible: false,
      editingTask: null,
      taskForm: {
        taskName: '',
        taskDescription: '',
        cronExpression: '',
        taskStatus: 1
      }
    }
  },
  methods: {
    async loadTasks() {
      try {
        const response = await scheduledTaskApi.getScheduledTasks()
        if (response && response.code === 200) {
          this.tasks = response.data || []
        } else {
          this.$message.error((response && response.message) || '加载定时任务列表失败')
          this.tasks = []
        }
      } catch (error) {
        console.error('加载定时任务列表失败:', error)
        this.$message.error('加载定时任务列表失败: ' + (error.message || '未知错误'))
        this.tasks = []
      }
    },
    
    showCreateDialog() {
      this.editingTask = null
      this.taskForm = {
        taskName: '',
        taskDescription: '',
        cronExpression: '',
        taskStatus: 1
      }
      this.dialogVisible = true
    },
    
    editTask(task) {
      this.editingTask = task
      this.taskForm = {
        taskName: task.taskName,
        taskDescription: task.taskDescription,
        cronExpression: task.cronExpression,
        taskStatus: task.taskStatus
      }
      this.dialogVisible = true
    },
    
    async saveTask() {
      try {
        let response
        if (this.editingTask) {
          // 更新任务
          response = await scheduledTaskApi.updateScheduledTask(this.editingTask.id, this.taskForm)
        } else {
          // 创建任务
          response = await scheduledTaskApi.createScheduledTask(this.taskForm)
        }
        
        if (response.code === 200) {
          this.$message.success(this.editingTask ? '更新成功' : '创建成功')
          this.dialogVisible = false
          this.loadTasks()
        } else {
          this.$message.error(response.message || (this.editingTask ? '更新失败' : '创建失败'))
        }
      } catch (error) {
        this.$message.error(this.editingTask ? '更新失败: ' + (error.message || '未知错误') : '创建失败: ' + (error.message || '未知错误'))
      }
    },
    
    async deleteTask(task) {
      try {
        await this.$confirm('确认删除该定时任务吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        const response = await scheduledTaskApi.deleteScheduledTask(task.id)
        if (response.code === 200) {
          this.$message.success('删除成功')
          this.loadTasks()
        } else {
          this.$message.error(response.message || '删除失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error('删除失败: ' + (error.message || '未知错误'))
        }
      }
    },
    
    async executeTask(task) {
      try {
        const response = await scheduledTaskApi.executeScheduledTask(task.id)
        if (response.code === 200) {
          this.$message.success('任务已触发执行')
          this.loadTasks() // 重新加载以更新最后执行时间
        } else {
          this.$message.error(response.message || '执行失败')
        }
      } catch (error) {
        this.$message.error('执行失败: ' + (error.message || '未知错误'))
      }
    },
    
    formatDate(dateString) {
      if (!dateString) return ''
      return new Date(dateString).toLocaleString('zh-CN')
    }
  },
  
  mounted() {
    this.loadTasks()
  }
}
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.dialog-footer {
  text-align: right;
}
</style>