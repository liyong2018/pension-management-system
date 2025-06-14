<template>
  <el-dialog
    v-model="visible"
    title="选择图标"
    width="800px"
    :before-close="handleClose"
  >
    <div class="icon-selector">
      <!-- 搜索框 -->
      <el-input
        v-model="searchText"
        placeholder="搜索图标名称"
        clearable
        class="search-input"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>

      <!-- 图标网格 -->
      <div class="icon-grid">
        <div
          v-for="(iconName, index) in filteredIcons"
          :key="index"
          class="icon-item"
          :class="{ active: selectedIcon === iconName }"
          @click="selectIcon(iconName)"
        >
          <el-icon :size="24">
            <component :is="getIconComponent(iconName)" />
          </el-icon>
          <span class="icon-name">{{ iconName }}</span>
        </div>
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="handleConfirm" :disabled="!selectedIcon">
          确定
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import {
  Search,
  Menu,
  User,
  Setting,
  Lock,
  Document,
  House,
  Location,
  Avatar,
  Operation,
  Link,
  FolderOpened,
  Folder,
  Files,
  Plus,
  Edit,
  Delete,
  View,
  Switch,
  Download,
  Upload,
  Refresh,
  Close,
  Check,
  Warning,
  Bell,
  Message,
  Phone,
  Calendar,
  Clock,
  Star,
  Flag,
  Share,
  CopyDocument,
  Printer,
  VideoCamera,
  Microphone,
  Headset,
  Monitor,
  Mouse,
  Cpu,
  Connection,
  DataBoard,
  PieChart,
  Histogram,
  TrendCharts,
  Grid,
  List,
  Filter,
  Sort,
  Rank,
  Tools,
  Compass,
  Guide,
  Help,
  QuestionFilled,
  InfoFilled,
  WarningFilled,
  CircleCheck,
  CircleClose,
  SuccessFilled,
  CirclePlus,
  Remove,
  ArrowLeft,
  ArrowRight,
  ArrowUp,
  ArrowDown,
  Back,
  Right,
  Top,
  Bottom,
  Expand,
  Fold,
  FullScreen,
  ScaleToOriginal,
  ZoomIn,
  ZoomOut
} from '@element-plus/icons-vue'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  currentIcon: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['update:modelValue', 'select'])

// 对话框可见性
const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

// 搜索文本
const searchText = ref('')

// 选中的图标
const selectedIcon = ref('')

// 可用图标列表
const availableIcons = [
  'Search', 'Menu', 'User', 'Setting', 'Lock', 'Document', 'House', 'Location',
  'Avatar', 'Operation', 'Link', 'FolderOpened', 'Folder', 'Files', 'Plus', 'Edit',
  'Delete', 'View', 'Switch', 'Download', 'Upload', 'Refresh', 'Close', 'Check',
  'Warning', 'Bell', 'Message', 'Phone', 'Calendar', 'Clock',
  'Star', 'Flag', 'Printer', 'Share', 'CopyDocument',
  'VideoCamera', 'Microphone', 'Headset', 'Monitor',  'Mouse',
  'Cpu', 'Connection', 'DataBoard',
  'PieChart', 'Histogram', 'TrendCharts', 'Grid', 'List', 'Filter',
  'Sort', 'Rank', 'Tools', 'Compass', 'Guide', 'Help', 'QuestionFilled',
  'InfoFilled', 'WarningFilled', 'CircleCheck', 'CircleClose', 'SuccessFilled',
  'CirclePlus', 'Remove', 'ArrowLeft', 'ArrowRight', 'ArrowUp', 'ArrowDown',
  'Back', 'Right', 'Top', 'Bottom', 'Expand', 'Fold', 'FullScreen',
  'ScaleToOriginal', 'ZoomIn', 'ZoomOut'
]

// 图标映射
const iconMap = {
  Search, Menu, User, Setting, Lock, Document, House, Location, Avatar,
  Operation, Link, FolderOpened, Folder, Files, Plus, Edit, Delete,
  View, Switch, Download, Upload, Refresh, Close, Check, Warning,
  Bell, Message, Phone, Calendar, Clock,
  Star,
  Flag, Share, CopyDocument, Printer,
  VideoCamera, Microphone, Headset, Monitor, Mouse,
  Cpu, Connection, DataBoard,
  PieChart, Histogram, TrendCharts, Grid, List, Filter,
  Sort, Rank, Tools, Compass, Guide, Help, QuestionFilled,
  InfoFilled, WarningFilled, CircleCheck, CircleClose, SuccessFilled,
  CirclePlus, Remove, ArrowLeft, ArrowRight, ArrowUp, ArrowDown,
  Back, Right, Top, Bottom, Expand, Fold, FullScreen,
  ScaleToOriginal, ZoomIn, ZoomOut
}

// 获取图标组件
const getIconComponent = (iconName) => {
  return iconMap[iconName] || Menu
}

// 过滤后的图标列表
const filteredIcons = computed(() => {
  if (!searchText.value) {
    return availableIcons
  }
  return availableIcons.filter(icon => 
    icon.toLowerCase().includes(searchText.value.toLowerCase())
  )
})

// 选择图标
const selectIcon = (iconName) => {
  selectedIcon.value = iconName
}

// 确认选择
const handleConfirm = () => {
  if (selectedIcon.value) {
    emit('select', selectedIcon.value)
    handleClose()
  }
}

// 关闭对话框
const handleClose = () => {
  visible.value = false
  selectedIcon.value = ''
  searchText.value = ''
}

// 监听当前图标变化
watch(() => props.currentIcon, (newIcon) => {
  selectedIcon.value = newIcon
}, { immediate: true })

// 监听对话框打开
watch(() => props.modelValue, (newValue) => {
  if (newValue) {
    selectedIcon.value = props.currentIcon
  }
})
</script>

<style scoped>
.icon-selector {
  max-height: 500px;
}

.search-input {
  margin-bottom: 20px;
}

.icon-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: 12px;
  max-height: 400px;
  overflow-y: auto;
  padding: 10px;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
}

.icon-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 12px 8px;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s;
  background-color: #fff;
}

.icon-item:hover {
  border-color: #409eff;
  background-color: #ecf5ff;
}

.icon-item.active {
  border-color: #409eff;
  background-color: #409eff;
  color: #fff;
}

.icon-name {
  margin-top: 8px;
  font-size: 12px;
  text-align: center;
  word-break: break-all;
}

.dialog-footer {
  text-align: right;
}
</style>