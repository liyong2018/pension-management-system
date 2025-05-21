<template>
  <div class="organization-management-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>机构管理</span>
          <el-button type="primary" @click="handleAddNewOrganization">新增机构</el-button>
        </div>
      </template>

      <!-- TODO: 添加搜索和筛选区域 -->
      
      <div class="search-filter-area" style="margin-bottom: 20px;">
        <el-input 
          v-model="searchQuery" 
          placeholder="按机构名称搜索..." 
          style="width: 300px; margin-right: 10px;" 
          clearable 
          @clear="handleSearchClear"
          @keyup.enter="handleSearch"
        />
        <el-button type="primary" @click="handleSearch">搜索</el-button>
      </div>
      

      <OrganizationTable 
        :organizations="organizations"
        :loading="loading"
        :pagination="pagination"
        @edit="handleEditOrganization"
        @delete="handleDeleteOrganization"
        @page-change="handlePageChange"
        @sort-change="handleSortChange"
      />
    </el-card>

    <OrganizationForm
      ref="organizationFormRef"
      :visible="formVisible"
      :organization-data="currentOrganization"
      @close="formVisible = false"
      @submit="handleSubmitForm"
    />

  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useOrganizationStore } from '@/store/organizationStore';
import OrganizationTable from '@/components/organization/OrganizationTable.vue';
import OrganizationForm from '@/components/organization/OrganizationForm.vue';
import { ElMessage, ElMessageBox } from 'element-plus';

const organizationStore = useOrganizationStore();

const organizations = computed(() => organizationStore.getOrganizationsList);
const loading = computed(() => organizationStore.isLoading);
const pagination = computed(() => organizationStore.getPagination);

const formVisible = ref(false);
const organizationFormRef = ref(null); // Ref for the form component
const currentOrganization = ref(null); // For editing
const isEditing = ref(false);
const formLoading = ref(false); // Specific loading state for the form submission

const searchQuery = ref(''); 
// let currentSort = { prop: 'name', order: 'ascending' }; // For sorting
let currentPage = 1;
let currentPageSize = 10;
let currentSortField = 'name';
let currentSortOrder = 'asc';

const loadOrganizations = async () => {
  await organizationStore.fetchOrganizations({
    page: currentPage,
    size: currentPageSize,
    sort: `${currentSortField},${currentSortOrder}`,
    name: searchQuery.value // Pass search query to store
  });
  if (organizationStore.hasError) {
    ElMessage.error(organizationStore.error || '加载机构数据失败');
  }
};

onMounted(() => {
  loadOrganizations();
});

const handleAddNewOrganization = () => {
  isEditing.value = false;
  currentOrganization.value = null; // Or an empty object with default structure
  formVisible.value = true;
  formLoading.value = false; // Reset form loading when opening
  // organizationFormRef.value?.resetForm(); // Reset form if needed when opening
};

const handleEditOrganization = (organization) => {
  isEditing.value = true;
  currentOrganization.value = { ...organization }; // Pass a copy to avoid direct mutation
  formVisible.value = true;
  formLoading.value = false; // Reset form loading when opening
};

const handleDeleteOrganization = async (organizationId) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除该机构吗？此操作不可撤销。',
      '警告',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning',
      }
    );
    await organizationStore.deleteOrganization(organizationId);
    ElMessage.success('机构删除成功');
    // loadOrganizations(); // Store action already re-fetches
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(organizationStore.error || '机构删除失败');
    }
  }
};

const handleSubmitForm = async (formData) => {
  formLoading.value = true; // Start form loading
  try {
    if (isEditing.value && currentOrganization.value && currentOrganization.value.id) {
      await organizationStore.updateOrganization(currentOrganization.value.id, formData);
      ElMessage.success('机构更新成功');
    } else {
      await organizationStore.createOrganization(formData);
      ElMessage.success('机构新增成功');
    }
    formVisible.value = false;
    // loadOrganizations(); // Store action already re-fetches, no need to call here
  } catch (errorData) { // errorData might be the validation errors object from the store
    if (typeof errorData === 'string') {
       ElMessage.error(errorData || (isEditing.value ? '机构更新失败' : '机构新增失败'));
    } else if (typeof errorData === 'object' && errorData !== null) {
      // Handle specific field errors if backend returns them in a structured way
      // For now, just show a generic message or the first error
      const firstError = Object.values(errorData)[0];
      ElMessage.error(firstError || (isEditing.value ? '机构更新失败，请检查表单' : '机构新增失败，请检查表单'));
    } else {
      ElMessage.error(isEditing.value ? '机构更新失败，请稍后重试' : '机构新增失败，请稍后重试');
    }
  } finally {
    formLoading.value = false; // Stop form loading
  }
};

const handlePageChange = (newPage) => {
  currentPage = newPage;
  loadOrganizations();
};

// Example: { column, prop, order }
const handleSortChange = ({ prop, order }) => {
  currentSortField = prop;
  currentSortOrder = order === 'ascending' ? 'asc' : 'desc';
  currentPage = 1; // Reset to first page on sort change
  loadOrganizations();
};

const handleSearch = () => {
  currentPage = 1; // Reset to first page on new search
  loadOrganizations();
};

const handleSearchClear = () => {
  // searchQuery is already cleared by `clearable`
  currentPage = 1;
  loadOrganizations();
};

</script>

<style scoped>
.organization-management-container {
  padding: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
/* Add more styles as needed */
</style> 