<template>
  <div class="organization-index">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>机构列表</span>
          <el-button type="primary" @click="handleCreate">新增机构</el-button>
        </div>
      </template>

      <!-- 搜索区域 -->
      <el-form :inline="true" :model="searchParams" @submit.prevent="fetchOrganizations">
        <el-form-item label="机构名称">
          <el-input v-model="searchParams.name" placeholder="请输入机构名称" clearable></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchOrganizations">查询</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格区域 -->
      <el-table :data="organizations" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="name" label="机构名称" sortable></el-table-column>
        <el-table-column prop="type" label="机构类型"></el-table-column>
        <el-table-column prop="address" label="地址"></el-table-column>
        <el-table-column prop="contactPersonName" label="联系人"></el-table-column>
        <el-table-column prop="contactPersonPhone" label="联系电话"></el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件 -->
      <el-pagination
        style="margin-top: 20px;"
        background
        layout="total, sizes, prev, pager, next, jumper"
        :total="totalElements"
        :page-sizes="[10, 20, 50, 100]"
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      ></el-pagination>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <OrganizationForm
      v-if="dialogVisible"
      :visible="dialogVisible"
      :organization-id="selectedOrganizationId"
      @close="handleCloseDialog"
      @success="handleFormSuccess"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import organizationService from '@/services/organizationService';
import OrganizationForm from '@/components/organization/OrganizationForm.vue'; // 稍后创建

const organizations = ref([]);
const loading = ref(false);
const totalElements = ref(0);
const currentPage = ref(1); // Element Plus 分页从 1 开始
const pageSize = ref(10);
const searchParams = reactive({
  name: ''
});

const dialogVisible = ref(false);
const selectedOrganizationId = ref(null);

const fetchOrganizations = async () => {
  loading.value = true;
  try {
    // 后端分页是从0开始，前端Element Plus分页是从1开始，需要转换
    const params = {
      page: currentPage.value - 1,
      size: pageSize.value,
      sort: 'id,asc' // 默认排序
    };
    // 如果有搜索条件，则添加到参数中
    // 注意：当前后端 organizationService.getOrganizations 不直接支持按名称模糊搜索，
    // 如果需要此功能，应调用 findOrganizationsByName (如果后端已实现对应接口)
    // 或者在前端进行过滤 (数据量大时不推荐)。
    // 此处暂时只按ID排序获取所有，搜索功能依赖后端支持。
    // 如果 searchParams.name 有值，可以考虑调用一个专门的搜索接口，或者前端过滤
    // 这里我们假设后端 getOrganizations 能够接受 name 作为查询参数 (虽然当前 OrganizationController 未明确支持)
    // 或者，我们可以先获取所有数据，然后在前端进行筛选。
    // 为简化，暂时不直接在 getOrganizations 中传递 name，搜索按钮会触发 fetchOrganizations，
    // 但实际的名称过滤逻辑需要后端接口支持或前端实现。
    // 一个更完善的做法是后端提供 /api/organizations?nameContains=xxx 这样的接口
    if (searchParams.name) {
      // 实际应用中，这里可能需要调用不同的API或传递不同的参数
      // params.name = searchParams.name; // 假设API支持name参数过滤
    }

    const response = await organizationService.getOrganizations(params.page, params.size, params.sort);
    organizations.value = response.data.content;
    totalElements.value = response.data.totalElements;
  } catch (error) {
    console.error("Failed to fetch organizations:", error);
    ElMessage.error('获取机构列表失败');
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
  fetchOrganizations();
};

const handleCurrentChange = (val) => {
  currentPage.value = val;
  fetchOrganizations();
};

// 搜索功能触发
// const onSearch = () => {
//   currentPage.value = 1; // 搜索时回到第一页
//   fetchOrganizations();
// };
// 在 el-form 上使用了 @submit.prevent="fetchOrganizations" 和查询按钮的 @click="fetchOrganizations"
// 意味着点击查询按钮或在输入框按回车都会触发 fetchOrganizations
// searchParams.name 的值会在 fetchOrganizations 内部被处理（或需要后端支持）

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
</style> 