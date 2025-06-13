<template>
  <div class="organization-index">
    <el-card class="box-card" style="margin-bottom: 20px;">
      <!-- 搜索区域 -->
      <el-form :inline="true" :model="searchParams" @submit.prevent="handleSearch" class="search-bar-flex">
        <el-form-item label="机构名称">
          <el-input v-model="searchParams.name" placeholder="请输入机构名称" clearable></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
        </el-form-item>
        <div style="margin-left:auto; display:flex; gap:12px; align-items:center;">
          <el-button type="primary" @click="handleCreate">新增机构</el-button>
          <el-button type="danger" :disabled="selectedOrganizations.length === 0" @click="handleBatchDelete">
            批量删除
          </el-button>
        </div>
      </el-form>
    </el-card>
    <el-card class="box-card">
      <!-- 表格区域 -->
      <el-table :data="organizations" style="width: 100%" v-loading="loading" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="45"></el-table-column>
        <el-table-column prop="id" label="ID" width="60"></el-table-column>
        <el-table-column prop="name" label="机构名称" min-width="140" show-overflow-tooltip></el-table-column>
        <el-table-column prop="shortName" label="机构简称" width="100" show-overflow-tooltip></el-table-column>
        <el-table-column prop="type" label="机构类型" width="120" show-overflow-tooltip></el-table-column>
        <el-table-column prop="address" label="地址" min-width="200" show-overflow-tooltip></el-table-column>
        <el-table-column prop="directorName" label="负责人" width="90" show-overflow-tooltip></el-table-column>
        <el-table-column prop="directorContact" label="联系电话" width="120" show-overflow-tooltip></el-table-column>
        <el-table-column prop="bedCount" label="床位数" width="80" align="right"></el-table-column>
        <el-table-column prop="actualServiceCount" label="服务人数" width="90" align="right"></el-table-column>
        <el-table-column prop="employeeCount" label="员工数" width="80" align="right"></el-table-column>
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="handleDetail(scope.row)">详情</el-button>
            <el-button size="small" type="primary" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件 -->
      <el-pagination style="margin-top: 20px;" background layout="total, sizes, prev, pager, next, jumper"
        :total="totalElements" :page-sizes="[10, 20, 50, 100]" v-model:current-page="currentPage"
        v-model:page-size="pageSize" @size-change="handleSizeChange"
        @current-change="handleCurrentChange"></el-pagination>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <OrganizationForm v-if="dialogVisible" :visible="dialogVisible" :organization-id="selectedOrganizationId"
      @close="handleCloseDialog" @success="handleFormSuccess" />

    <!-- 详情对话框 -->
    <OrganizationDetail v-if="detailDialogVisible" :visible="detailDialogVisible"
      :organization-id="selectedOrganizationId" @close="handleCloseDetailDialog" />
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import organizationService from '@/services/organizationService';
import OrganizationForm from '@/components/organization/OrganizationForm.vue';
import OrganizationDetail from '@/components/organization/OrganizationDetail.vue';

const organizations = ref([]);
const loading = ref(false);
const totalElements = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);
const searchParams = reactive({
  name: ''
});

const dialogVisible = ref(false);
const selectedOrganizationId = ref(null);
const selectedOrganizations = ref([]); // 存储选中的机构

const detailDialogVisible = ref(false);
const selectedOrganization = ref(null);

const fetchOrganizations = async () => {
  loading.value = true;
  try {
    const params = {
      pageNum: currentPage.value, // 修改: page -> pageNum, 后端PageHelper使用pageNum，通常1-indexed
      pageSize: pageSize.value, // 修改: size -> pageSize
      // sort: 'id,asc', // 暂时移除排序，后端默认可能已有排序或需要特定格式
      name: searchParams.name || undefined // 确保空字符串不作为参数发送
    };

    const response = await organizationService.getOrganizations(params);
    // 修改: 直接使用 response (因为 request.js 返回 response.data)
    if (response && response.list) {
      organizations.value = response.list;
      totalElements.value = response.total;
      currentPage.value = response.pageNum; // 修改: response.data.number -> response.pageNum
      pageSize.value = response.pageSize;   // 修改: response.data.size -> response.pageSize
    } else {
      organizations.value = [];
      totalElements.value = 0;
      // 如果 response 为空或 response.list 为空，也提示
      ElMessage.warning(response?.list ? '机构数据为空' : '未获取到机构数据或数据格式不正确');
    }
  } catch (error) {
    console.error("Failed to fetch organizations:", error);
    ElMessage.error('获取机构列表失败');
    organizations.value = [];
    totalElements.value = 0;
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  fetchOrganizations();
});

const handleCreate = () => {
  selectedOrganizationId.value = null;
  dialogVisible.value = true;
};

const handleEdit = (organization) => {
  selectedOrganizationId.value = organization.id;
  dialogVisible.value = true;
};

// 处理表格多选变化
const handleSelectionChange = (selection) => {
  selectedOrganizations.value = selection;
};

// 批量删除
const handleBatchDelete = () => {
  if (selectedOrganizations.value.length === 0) {
    return;
  }

  ElMessageBox.confirm(
    `确定要删除选中的 ${selectedOrganizations.value.length} 个机构吗？此操作不可恢复！`,
    '警告',
    {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'warning',
    }
  )
    .then(async () => {
      loading.value = true;
      try {
        const ids = selectedOrganizations.value.map(org => org.id);
        await organizationService.batchDeleteOrganizations(ids);
        ElMessage({
          type: 'success',
          message: '批量删除成功',
          duration: 2000
        });
        selectedOrganizations.value = []; // 清空选中项
        await fetchOrganizations(); // 重新加载列表
      } catch (error) {
        console.error("Failed to delete organizations:", error);
        ElMessage({
          type: 'error',
          message: error.message || '批量删除失败，请重试',
          duration: 5000
        });
      } finally {
        loading.value = false;
      }
    })
    .catch(() => {
      // 用户取消删除
      ElMessage({
        type: 'info',
        message: '已取消删除操作',
        duration: 2000
      });
    });
};

const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除该机构吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      try {
        await organizationService.deleteOrganization(id);
        ElMessage.success('删除成功');
        fetchOrganizations(); // 重新加载列表
      } catch (error) {
        console.error("Failed to delete organization:", error);
        ElMessage.error(error.response?.data?.message || '删除失败');
      }
    })
    .catch(() => {
      // 用户取消删除
    });
};

const handleCloseDialog = () => {
  dialogVisible.value = false;
  selectedOrganizationId.value = null;
};

const handleFormSuccess = () => {
  handleCloseDialog();
  fetchOrganizations(); // 刷新列表
};

const handleSizeChange = (val) => {
  pageSize.value = val;
  currentPage.value = 1; // 重置到第一页
  fetchOrganizations();
};

const handleCurrentChange = (val) => {
  currentPage.value = val;
  fetchOrganizations();
};

const handleSearch = () => {
  currentPage.value = 1; // 搜索时重置到第一页
  fetchOrganizations();
};

const handleDetail = (organization) => {
  selectedOrganizationId.value = organization.id;
  detailDialogVisible.value = true;
};

const handleCloseDetailDialog = () => {
  detailDialogVisible.value = false;
  selectedOrganizationId.value = null;
};
</script>

<style scoped>
.organization-index {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.search-bar-flex {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
}
</style>