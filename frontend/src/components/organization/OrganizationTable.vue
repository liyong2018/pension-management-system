<template>
  <div>
    <el-table
      :data="organizations"
      v-loading="loading"
      style="width: 100%"
      border
      stripe
      @sort-change="handleSortChange"
    >
      <el-table-column prop="id" label="ID" width="80" sortable="custom"></el-table-column>
      <el-table-column prop="name" label="机构名称" min-width="180" sortable="custom" show-overflow-tooltip></el-table-column>
      <el-table-column prop="type" label="机构类型" width="150" sortable="custom" show-overflow-tooltip></el-table-column>
      <el-table-column prop="address" label="地址" min-width="200" show-overflow-tooltip></el-table-column>
      <el-table-column prop="contactPersonName" label="联系人" width="120" show-overflow-tooltip></el-table-column>
      <el-table-column prop="contactPersonPhone" label="联系电话" width="150" show-overflow-tooltip></el-table-column>
      <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip></el-table-column>
      <el-table-column prop="createdAt" label="创建时间" width="180" sortable="custom">
        <template #default="scope">
          {{ formatDateTime(scope.row.createdAt) }}
        </template>
      </el-table-column>
      <el-table-column prop="updatedAt" label="更新时间" width="180" sortable="custom">
        <template #default="scope">
          {{ formatDateTime(scope.row.updatedAt) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="scope">
          <el-button size="small" type="primary" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-if="pagination.totalElements > 0"
      background
      layout="total, sizes, prev, pager, next, jumper"
      :total="pagination.totalElements"
      :current-page="pagination.currentPage"
      :page-sizes="[10, 20, 50, 100]"
      :page-size="pagination.pageSize"
      @current-change="handlePageChange"
      @size-change="handleSizeChange"
      style="margin-top: 20px; text-align: right;"
    >
    </el-pagination>
  </div>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue';
import { ElMessage } from 'element-plus'; // Potentially for local messages if needed

const props = defineProps({
  organizations: {
    type: Array,
    required: true,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
  },
  pagination: {
    type: Object,
    required: true,
    default: () => ({
      currentPage: 1,
      totalPages: 1,
      totalElements: 0,
      pageSize: 10
    })
  }
});

const emit = defineEmits(['edit', 'delete', 'page-change', 'size-change', 'sort-change']);

const handleEdit = (organization) => {
  emit('edit', organization);
};

const handleDelete = (id) => {
  emit('delete', id);
};

const handlePageChange = (newPage) => {
  emit('page-change', newPage);
};

const handleSizeChange = (newSize) => {
  // When size changes, typically we want to go back to page 1
  emit('size-change', newSize);
  // The parent component (OrganizationManagement) should handle fetching data with new size and page 1.
  // However, since the store handles fetch and manages current page, we might directly emit page-change with 1.
  // For simplicity, let parent handle it by just emitting new size.
  // emit('page-change', 1); 
};

const handleSortChange = (sortInfo) => {
  // { column, prop, order (ascending/descending) }
  emit('sort-change', sortInfo);
};

// Utility to format date-time, can be moved to a utils file
const formatDateTime = (dateTimeString) => {
  if (!dateTimeString) return '';
  try {
    const options = {
      year: 'numeric', month: '2-digit', day: '2-digit',
      hour: '2-digit', minute: '2-digit', second: '2-digit',
      hour12: false
    };
    return new Intl.DateTimeFormat('zh-CN', options).format(new Date(dateTimeString));
  } catch (e) {
    console.error("Error formatting date: ", e);
    return dateTimeString; // fallback to original string
  }
};

</script>

<style scoped>
/* Add any specific styles for the table if needed */
</style> 