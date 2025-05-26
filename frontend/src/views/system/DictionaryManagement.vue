<template>
  <div class="dictionary-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>字典管理</span>
          <div class="header-stats">
            <el-tag type="info" size="small">
              共 {{ totalCount }} 条字典数据
            </el-tag>
          </div>
        </div>
      </template>

      <el-tabs v-model="activeTab" type="border-card" @tab-change="handleTabChange">
        <!-- 所属社区 Tab -->
        <el-tab-pane name="community">
          <template #label>
            <span class="tab-label">
              <el-icon><Location /></el-icon>
              所属社区
              <el-badge :value="communityCount" :hidden="communityCount === 0" />
            </span>
          </template>
          <DictionaryTab 
            dict-type="community"
            dict-type-name="所属社区"
            :key="'community'"
            @data-change="handleDataChange"
          />
        </el-tab-pane>

        <!-- 养老类型 Tab -->
        <el-tab-pane name="pensionType">
          <template #label>
            <span class="tab-label">
              <el-icon><House /></el-icon>
              养老类型
              <el-badge :value="pensionTypeCount" :hidden="pensionTypeCount === 0" />
            </span>
          </template>
          <DictionaryTab 
            dict-type="pensionType"
            dict-type-name="养老类型"
            :key="'pensionType'"
            @data-change="handleDataChange"
          />
        </el-tab-pane>

        <!-- 老人类型 Tab -->
        <el-tab-pane name="elderlyType">
          <template #label>
            <span class="tab-label">
              <el-icon><User /></el-icon>
              老人类型
              <el-badge :value="elderlyTypeCount" :hidden="elderlyTypeCount === 0" />
            </span>
          </template>
          <DictionaryTab 
            dict-type="elderly_type"
            dict-type-name="老人类型"
            :key="'elderly_type'"
            @data-change="handleDataChange"
          />
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 使用说明 -->
    <el-card class="help-card" v-if="showHelp">
      <template #header>
        <div class="help-header">
          <span>使用说明</span>
          <el-button type="text" @click="showHelp = false">
            <el-icon><Close /></el-icon>
          </el-button>
        </div>
      </template>
      <div class="help-content">
        <h4>字典管理功能说明：</h4>
        <ul>
          <li><strong>所属社区：</strong>用于管理老人档案中的社区选项，可以添加、编辑、删除社区信息</li>
          <li><strong>养老类型：</strong>用于管理老人档案中的养老类型选项，如居家养老、机构养老等</li>
          <li><strong>老人类型：</strong>用于管理老人档案中的老人类型选项，如普通老人、空巢老人、独居老人等</li>
          <li><strong>字典编码：</strong>用于程序内部识别，建议使用英文大写字母和下划线，如：COMMUNITY_001</li>
          <li><strong>字典标签：</strong>显示给用户看的名称，如：朝阳公园社区</li>
          <li><strong>字典值：</strong>实际存储的数据值，通常与标签相同</li>
          <li><strong>排序：</strong>控制在下拉框中的显示顺序，数字越小排序越靠前</li>
        </ul>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Location, House, User, Close } from '@element-plus/icons-vue'
import DictionaryTab from './components/DictionaryTab.vue'
import { dictionaryApi } from '@/api/dictionary'
import { DICT_TYPES } from '@/constants/dictionary'

const activeTab = ref('community')
const showHelp = ref(true)
const communityCount = ref(0)
const pensionTypeCount = ref(0)
const elderlyTypeCount = ref(0)
const totalCount = ref(0)

// 获取统计数据
const fetchStatistics = async () => {
  try {
    const [communityResult, pensionTypeResult, elderlyTypeResult] = await Promise.all([
      dictionaryApi.countByType(DICT_TYPES.COMMUNITY),
      dictionaryApi.countByType(DICT_TYPES.PENSION_TYPE),
      dictionaryApi.countByType(DICT_TYPES.ELDERLY_TYPE)
    ])
    
    communityCount.value = communityResult || 0
    pensionTypeCount.value = pensionTypeResult || 0
    elderlyTypeCount.value = elderlyTypeResult || 0
    totalCount.value = communityCount.value + pensionTypeCount.value + elderlyTypeCount.value
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

// 处理Tab切换
const handleTabChange = (tabName) => {
  activeTab.value = tabName
}

// 处理数据变化（子组件触发）
const handleDataChange = () => {
  fetchStatistics()
}

// 组件挂载时获取统计数据
onMounted(() => {
  fetchStatistics()
})
</script>

<style scoped>
.dictionary-management {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-stats {
  display: flex;
  gap: 10px;
  align-items: center;
}

.tab-label {
  display: flex;
  align-items: center;
  gap: 6px;
}

.help-card {
  margin-top: 20px;
}

.help-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.help-content h4 {
  margin: 0 0 12px 0;
  color: #303133;
}

.help-content ul {
  margin: 0;
  padding-left: 20px;
}

.help-content li {
  margin-bottom: 8px;
  line-height: 1.6;
  color: #606266;
}

.help-content strong {
  color: #409eff;
}

:deep(.el-tabs--border-card) {
  box-shadow: none;
  border: 1px solid #dcdfe6;
}

:deep(.el-tabs__content) {
  padding: 20px;
}

:deep(.el-badge) {
  margin-left: 6px;
}

:deep(.el-badge__content) {
  background-color: #409eff;
  border-color: #409eff;
}
</style> 