<template>
  <div class="log-management">
    <!-- 统计卡片 -->
    <div class="statistics-cards" v-if="stats">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon">
                <el-icon size="24"><Document /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-title">总日志数</div>
                <div class="stat-value total">{{ stats.totalLogs }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon active">
                <el-icon size="24"><CircleCheck /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-title">今日操作</div>
                <div class="stat-value active">{{ stats.todayLogs }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon warning">
                <el-icon size="24"><Warning /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-title">异常操作</div>
                <div class="stat-value warning">{{ stats.errorLogs }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon info">
                <el-icon size="24"><User /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-title">活跃用户</div>
                <div class="stat-value info">{{ stats.activeUsers }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 搜索区域 -->
    <el-card class="search-card">
      <el-form :model="searchForm" label-width="auto" :inline="true">
        <el-form-item label="操作用户">
          <el-input 
            v-model="searchForm.username" 
            placeholder="请输入用户名" 
            clearable
            style="width: 150px"
          />
        </el-form-item>
        <el-form-item label="操作类型">
          <el-select v-model="searchForm.operationType" placeholder="请选择操作类型" clearable style="width: 150px">
            <el-option label="创建" value="CREATE" />
            <el-option label="更新" value="UPDATE" />
            <el-option label="删除" value="DELETE" />
            <el-option label="查询" value="SELECT" />
            <el-option label="登录" value="LOGIN" />
            <el-option label="登出" value="LOGOUT" />
          </el-select>
        </el-form-item>
        <el-form-item label="日志级别">
          <el-select v-model="searchForm.logLevel" placeholder="请选择日志级别" clearable style="width: 120px">
            <el-option label="信息" value="INFO" />
            <el-option label="警告" value="WARN" />
            <el-option label="错误" value="ERROR" />
            <el-option label="调试" value="DEBUG" />
          </el-select>
        </el-form-item>
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="datetimerange"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 360px"
          />
        </el-form-item>
        <el-form-item class="search-buttons-left">
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
        <el-form-item class="table-operations-left">
          <el-button type="warning" @click="handleExport">
            <el-icon><Download /></el-icon>
            导出日志
          </el-button>
          <el-button type="danger" :disabled="!multipleSelection.length" @click="handleBatchDelete">
            批量删除
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card class="table-card">
      <el-table
        :data="logs"
        v-loading="loading"
        @selection-change="handleSelectionChange"
        border
        stripe
        style="width: 100%"
        :header-cell-style="{ backgroundColor: '#f5f7fa', color: '#303133', fontWeight: '600' }"
        :row-style="{ height: '60px' }"
        class="log-table"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="username" label="操作用户" width="120">
          <template #default="scope">
            <div class="user-info">
              <el-avatar :size="32" :src="scope.row.userAvatar">
                <el-icon><User /></el-icon>
              </el-avatar>
              <span class="username">{{ scope.row.username }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="operationType" label="操作类型" width="100">
          <template #default="scope">
            <el-tag :type="getOperationTypeColor(scope.row.operationType)" size="small">
              {{ getOperationTypeText(scope.row.operationType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="operationDesc" label="操作描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="module" label="操作模块" width="120" />
        <el-table-column prop="logLevel" label="日志级别" width="100">
          <template #default="scope">
            <el-tag :type="getLogLevelColor(scope.row.logLevel)" size="small" effect="dark">
              {{ scope.row.logLevel }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="ipAddress" label="IP地址" width="130" />
        <el-table-column prop="requestTime" label="请求时长" width="100" align="center">
          <template #default="scope">
            <span :class="getRequestTimeClass(scope.row.requestTime)">
              {{ scope.row.requestTime }}ms
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="操作时间" width="160" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <div class="action-buttons">
              <el-button type="primary" link size="small" @click="showDetailDialog(scope.row)">
                详情
              </el-button>
              <el-dropdown @command="(command) => handleAction(command, scope.row)" trigger="click">
                <el-button type="primary" link size="small" class="more-btn">
                  更多<el-icon class="el-icon--right"><ArrowDown /></el-icon>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="export">
                      <el-icon><Download /></el-icon>
                      导出详情
                    </el-dropdown-item>
                    <el-dropdown-item command="copy">
                      <el-icon><CopyDocument /></el-icon>
                      复制信息
                    </el-dropdown-item>
                    <el-dropdown-item command="delete" divided class="delete-item">
                      <el-icon><Delete /></el-icon>
                      删除
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 日志详情对话框 -->
    <el-dialog 
      v-model="detailDialogVisible" 
      title="日志详情" 
      width="700px" 
      class="log-dialog"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <div v-if="selectedLog" class="log-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="日志ID">{{ selectedLog.id }}</el-descriptions-item>
          <el-descriptions-item label="操作用户">{{ selectedLog.username }}</el-descriptions-item>
          <el-descriptions-item label="操作类型">
            <el-tag :type="getOperationTypeColor(selectedLog.operationType)">
              {{ getOperationTypeText(selectedLog.operationType) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="日志级别">
            <el-tag :type="getLogLevelColor(selectedLog.logLevel)" effect="dark">
              {{ selectedLog.logLevel }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="操作模块">{{ selectedLog.module }}</el-descriptions-item>
          <el-descriptions-item label="IP地址">{{ selectedLog.ipAddress }}</el-descriptions-item>
          <el-descriptions-item label="用户代理">{{ selectedLog.userAgent || '未知' }}</el-descriptions-item>
          <el-descriptions-item label="请求时长">{{ selectedLog.requestTime }}ms</el-descriptions-item>
          <el-descriptions-item label="操作时间" :span="2">{{ selectedLog.createTime }}</el-descriptions-item>
          <el-descriptions-item label="操作描述" :span="2">{{ selectedLog.operationDesc }}</el-descriptions-item>
        </el-descriptions>

        <div class="request-detail" v-if="selectedLog.requestUrl">
          <h4>请求详情</h4>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="请求URL">{{ selectedLog.requestUrl }}</el-descriptions-item>
            <el-descriptions-item label="请求方法">
              <el-tag size="small">{{ selectedLog.requestMethod }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="请求参数" v-if="selectedLog.requestParams">
              <pre class="json-content">{{ formatJson(selectedLog.requestParams) }}</pre>
            </el-descriptions-item>
            <el-descriptions-item label="响应结果" v-if="selectedLog.responseData">
              <pre class="json-content">{{ formatJson(selectedLog.responseData) }}</pre>
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <div class="error-detail" v-if="selectedLog.errorMessage">
          <h4>错误信息</h4>
          <el-alert 
            :title="selectedLog.errorMessage" 
            type="error" 
            :description="selectedLog.errorStack"
            show-icon
            :closable="false"
          />
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, onMounted, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Document, CircleCheck, Warning, User, Download, View, Delete, ArrowDown, CopyDocument
} from '@element-plus/icons-vue'
import request from '@/utils/request'

export default {
  name: 'LogList',
  components: {
    Document, CircleCheck, Warning, User, Download, View, Delete, ArrowDown, CopyDocument
  },
  setup() {
    // 响应式数据
    const loading = ref(false)
    const logs = ref([])
    const selectedLog = ref(null)
    const multipleSelection = ref([])
    const stats = ref({
      totalLogs: 0,
      todayLogs: 0,
      errorLogs: 0,
      activeUsers: 0
    })

    // 搜索表单
    const searchForm = reactive({
      username: '',
      operationType: '',
      logLevel: '',
      dateRange: null
    })

    // 分页信息
    const pagination = reactive({
      page: 1,
      pageSize: 10,
      total: 0
    })

    // 对话框显示状态
    const detailDialogVisible = ref(false)

    // 加载日志列表
    const loadLogs = async () => {
      loading.value = true
      try {
        const queryParams = {
          page: pagination.page,
          size: pagination.pageSize,
          ...searchForm
        }
        
        // 处理时间范围参数
        if (searchForm.dateRange && searchForm.dateRange.length === 2) {
          queryParams.startTime = searchForm.dateRange[0]
          queryParams.endTime = searchForm.dateRange[1]
        }
        
        // 使用request方法替代fetch
        const data = await request({
          url: 'operation-logs',
          method: 'get',
          params: queryParams
        })
        
        if (data && data.list) {
          logs.value = data.list
          pagination.total = data.total || 0
          
          // 更新统计信息
          const statsData = await request({
            url: 'operation-logs/statistics',
            method: 'get'
          })
          
          if (statsData) {
            stats.value = statsData
          }
        } else {
          console.warn('日志API返回数据格式异常:', data)
          logs.value = []
          pagination.total = 0
        }
      } catch (error) {
        console.error('加载日志列表异常:', error)
        ElMessage.error('加载日志列表失败: ' + error.message)
        logs.value = []
        pagination.total = 0
      } finally {
        loading.value = false
      }
    }

    // 获取操作类型文本
    const getOperationTypeText = (type) => {
      const typeMap = {
        'CREATE': '创建',
        'UPDATE': '更新',
        'DELETE': '删除',
        'SELECT': '查询',
        'LOGIN': '登录',
        'LOGOUT': '登出'
      }
      return typeMap[type] || type
    }

    // 获取操作类型颜色
    const getOperationTypeColor = (type) => {
      const colorMap = {
        'CREATE': 'success',
        'UPDATE': 'warning',
        'DELETE': 'danger',
        'SELECT': 'info',
        'LOGIN': 'primary',
        'LOGOUT': ''
      }
      return colorMap[type] || 'info'
    }

    // 获取日志级别颜色
    const getLogLevelColor = (level) => {
      const colorMap = {
        'INFO': 'info',
        'WARN': 'warning',
        'ERROR': 'danger',
        'DEBUG': 'success'
      }
      return colorMap[level] || 'info'
    }

    // 获取请求时长样式类
    const getRequestTimeClass = (time) => {
      if (time > 1000) return 'request-time-slow'
      if (time > 500) return 'request-time-medium'
      return 'request-time-fast'
    }

    // 格式化JSON
    const formatJson = (jsonStr) => {
      try {
        return JSON.stringify(JSON.parse(jsonStr), null, 2)
      } catch {
        return jsonStr
      }
    }

    // 搜索
    const handleSearch = () => {
      pagination.page = 1
      loadLogs()
    }

    // 重置
    const handleReset = () => {
      Object.keys(searchForm).forEach(key => {
        searchForm[key] = key === 'dateRange' ? null : ''
      })
      pagination.page = 1
      loadLogs()
    }

    // 显示详情对话框
    const showDetailDialog = (log) => {
      selectedLog.value = { ...log }
      detailDialogVisible.value = true
    }

    // 删除日志
    const deleteLog = async (log) => {
      try {
        await ElMessageBox.confirm(
          `确定要删除ID为 ${log.id} 的日志记录吗？此操作不可恢复！`,
          '确认删除',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )
        
        const response = await request({
          url: `operation-logs/${log.id}`,
          method: 'delete'
        })
        
        if (response.ok) {
          ElMessage.success('删除成功')
          loadLogs()
        } else {
          ElMessage.error('删除失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('删除失败')
          console.error('Error deleting log:', error)
        }
      }
    }

    // 分页
    const handleSizeChange = (size) => {
      pagination.pageSize = size
      pagination.page = 1
      loadLogs()
    }

    const handleCurrentChange = (page) => {
      pagination.page = page
      loadLogs()
    }

    // 表格选择变化
    const handleSelectionChange = (selection) => {
      multipleSelection.value = selection
    }

    // 批量删除
    const handleBatchDelete = async () => {
      try {
        await ElMessageBox.confirm(
          `确定要删除选中的 ${multipleSelection.value.length} 条日志记录吗？此操作不可恢复！`,
          '确认批量删除',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )
        
        const ids = multipleSelection.value.map(item => item.id)
        const response = await request({
          url: 'operation-logs/batch',
          method: 'delete',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(ids)
        })
        
        if (response.ok) {
          ElMessage.success('批量删除成功')
          loadLogs()
        } else {
          ElMessage.error('批量删除失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('批量删除失败')
        }
      }
    }

    // 导出日志
    const handleExport = () => {
      ElMessage.info('日志导出功能正在开发中...')
    }

    // 处理操作
    const handleAction = (command, log) => {
      if (command === 'export') {
        // 处理导出详情
        ElMessage.info('导出详情功能正在开发中...')
      } else if (command === 'copy') {
        // 处理复制信息
        ElMessage.info('复制信息功能正在开发中...')
      } else if (command === 'delete') {
        // 处理删除
        deleteLog(log)
      }
    }

    onMounted(() => {
      loadLogs()
    })

    return {
      loading,
      logs,
      selectedLog,
      multipleSelection,
      stats,
      searchForm,
      pagination,
      detailDialogVisible,
      loadLogs,
      getOperationTypeText,
      getOperationTypeColor,
      getLogLevelColor,
      getRequestTimeClass,
      formatJson,
      handleSearch,
      handleReset,
      showDetailDialog,
      deleteLog,
      handleSizeChange,
      handleCurrentChange,
      handleSelectionChange,
      handleBatchDelete,
      handleExport,
      handleAction
    }
  }
}
</script>

<style scoped>
.log-management {
  padding: 20px;
}

.statistics-cards {
  margin-bottom: 20px;
}

.stat-card {
  text-align: center;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.stat-content {
  display: flex;
  align-items: center;
  padding: 20px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  color: white;
}

.stat-icon.active {
  background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
}

.stat-icon.warning {
  background: linear-gradient(135deg, #e6a23c 0%, #ebb563 100%);
}

.stat-icon.info {
  background: linear-gradient(135deg, #f56c6c 0%, #f78989 100%);
}

.stat-info {
  flex: 1;
}

.stat-title {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
  font-weight: 500;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  margin: 0;
}

.stat-value.total {
  color: #409eff;
}

.stat-value.active {
  color: #67c23a;
}

.stat-value.warning {
  color: #e6a23c;
}

.stat-value.info {
  color: #f56c6c;
}

.search-card {
  margin-bottom: 20px;
}

.table-operations-left {
  float: right;
  margin-right: auto;
}

.search-buttons-left {
  margin-left: 20px;
}

.table-card {
  margin-bottom: 20px;
  width: 100%;
}

.table-card :deep(.el-card__body) {
  padding: 0;
}

.log-table {
  width: 100% !important;
  font-size: 14px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.username {
  font-weight: 500;
}

.request-time-fast {
  color: #67c23a;
  font-weight: 500;
}

.request-time-medium {
  color: #e6a23c;
  font-weight: 500;
}

.request-time-slow {
  color: #f56c6c;
  font-weight: 500;
}

.action-buttons {
  display: flex;
  align-items: center;
  gap: 6px;
  flex-wrap: nowrap;
  justify-content: flex-start;
}

.action-buttons .el-button {
  margin: 0;
  padding: 6px 12px;
  min-width: 48px;
  white-space: nowrap;
  font-size: 13px;
  border-radius: 4px;
}

.action-buttons .el-button--primary.is-link {
  color: #409eff;
  background: transparent;
  border: 1px solid transparent;
  transition: all 0.2s ease;
}

.action-buttons .el-button--primary.is-link:hover {
  color: #ffffff;
  background: #409eff;
  border-color: #409eff;
}

.more-btn {
  display: flex;
  align-items: center;
  gap: 4px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

/* 下拉菜单样式优化 */
:deep(.el-dropdown-menu) {
  min-width: 120px;
  padding: 8px 0;
}

:deep(.el-dropdown-menu__item) {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  color: #606266;
  font-size: 14px;
}

:deep(.el-dropdown-menu__item:hover) {
  background-color: #f5f7fa;
  color: #409eff;
}

:deep(.el-dropdown-menu__item.delete-item) {
  color: #f56c6c;
}

:deep(.el-dropdown-menu__item.delete-item:hover) {
  background-color: #fef0f0;
  color: #f56c6c;
}

.log-dialog {
  :deep(.el-dialog) {
    border-radius: 16px;
    overflow: visible;
    box-shadow: 0 25px 50px rgba(0, 0, 0, 0.15);
    border: 1px solid rgba(255, 255, 255, 0.2);
    max-height: 85vh;
    display: flex;
    flex-direction: column;
  }
  
  :deep(.el-dialog__header) {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    padding: 24px 32px;
    margin: 0;
    position: relative;
    overflow: hidden;
  }

  :deep(.el-dialog__header)::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(45deg, rgba(255,255,255,0.1) 0%, transparent 100%);
    pointer-events: none;
  }
  
  :deep(.el-dialog__title) {
    color: white;
    font-size: 20px;
    font-weight: 600;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  }
  
  :deep(.el-dialog__headerbtn) {
    top: 24px;
    right: 32px;
    width: 32px;
    height: 32px;
    background: rgba(255, 255, 255, 0.1);
    border-radius: 50%;
    transition: all 0.3s ease;
  }

  :deep(.el-dialog__headerbtn):hover {
    background: rgba(255, 255, 255, 0.2);
    transform: scale(1.1);
  }
  
  :deep(.el-dialog__headerbtn .el-dialog__close) {
    color: white;
    font-size: 18px;
    font-weight: bold;
  }
  
  :deep(.el-dialog__body) {
    padding: 32px;
    background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
    min-height: 200px;
    flex: 1;
    overflow-y: auto;
  }

  :deep(.el-dialog__footer) {
    background: white;
    padding: 20px 32px;
    border-top: 1px solid #ebeef5;
    flex-shrink: 0;
  }
}

.log-detail {
  background: white;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.request-detail,
.error-detail {
  margin-top: 24px;
}

.request-detail h4,
.error-detail h4 {
  margin: 0 0 16px 0;
  color: #2c3e50;
  font-weight: 600;
  font-size: 16px;
}

.json-content {
  background: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 4px;
  padding: 12px;
  font-family: 'Courier New', monospace;
  font-size: 12px;
  white-space: pre-wrap;
  word-break: break-all;
  max-height: 200px;
  overflow-y: auto;
}

:deep(.el-card) {
  border-radius: 8px;
  border: 1px solid #ebeef5;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.04);
}

:deep(.el-card__header) {
  background-color: #fafafa;
  border-bottom: 1px solid #ebeef5;
  padding: 18px 20px;
}

:deep(.el-button--primary) {
  background-color: #409eff;
  border-color: #409eff;
}

:deep(.el-button--primary:hover) {
  background-color: #66b1ff;
  border-color: #66b1ff;
}

:deep(.el-tag) {
  border-radius: 4px;
  font-weight: 500;
}

:deep(.el-descriptions) {
  :deep(.el-descriptions__header) {
    margin-bottom: 20px;
  }
  
  :deep(.el-descriptions__title) {
    font-size: 16px;
    font-weight: 600;
    color: #2c3e50;
  }
}

:deep(.el-alert) {
  border-radius: 8px;
}
</style> 