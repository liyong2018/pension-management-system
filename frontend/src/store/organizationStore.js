import { defineStore } from 'pinia';
import organizationService from '@/services/organizationService';

export const useOrganizationStore = defineStore('organization', {
  state: () => ({
    organizations: [],
    selectedOrganization: null,
    loading: false,
    error: null,
    pagination: {
      currentPage: 1,
      totalPages: 1,
      totalElements: 0,
      pageSize: 10, // 与后端分页大小一致或可配置
    },
  }),
  actions: {
    async fetchOrganizations(params) {
      this.loading = true;
      this.error = null;
      try {
        const apiParams = {
          pageNum: params.page, // PageHelper使用pageNum而不是page
          pageSize: params.size,
          name: params.name?.trim() || undefined
        };
        
        const response = await organizationService.getOrganizations(apiParams);
        
        // 检查响应数据
        if (response) {
          this.organizations = response.list || []; // PageHelper使用list而不是content
          this.pagination = {
            currentPage: response.pageNum || 1,
            totalPages: response.pages || 1,
            totalElements: response.total || 0,
            pageSize: response.pageSize || params.size
          };
        } else {
          throw new Error('未获取到机构数据');
        }
      } catch (err) {
        console.error('获取机构列表失败:', err);
        this.error = err.message || '获取机构列表失败';
        this.organizations = [];
        this.pagination = {
          currentPage: 1,
          totalPages: 0,
          totalElements: 0,
          pageSize: params.size
        };
      } finally {
        this.loading = false;
      }
    },
    async fetchOrganizationById(id) {
      this.loading = true;
      this.error = null;
      try {
        const response = await organizationService.getOrganization(id);
        this.selectedOrganization = response.data;
        return response.data; // 返回获取到的数据，方便组件使用
      } catch (err) {
        this.error = err.response?.data?.message || err.message || '获取机构详情失败';
        this.selectedOrganization = null;
      } finally {
        this.loading = false;
      }
    },
    async createOrganization(organizationData) {
      this.loading = true;
      this.error = null;
      try {
        const response = await organizationService.createOrganization(organizationData);
        // 创建成功后可以重新加载列表或将新项添加到列表顶部
        await this.fetchOrganizations({ page: this.pagination.currentPage, size: this.pagination.pageSize });
        return response.data; // 返回创建成功的数据
      } catch (err) {
        this.error = err.response?.data?.message || err.message || '创建机构失败';
        // 根据后端返回的校验错误信息进行提示
        if (err.response?.data && typeof err.response.data === 'object') {
          // err.response.data 可能直接是错误map {field: message}
          return Promise.reject(err.response.data); 
        }
        return Promise.reject(this.error);
      } finally {
        this.loading = false;
      }
    },
    async updateOrganization(id, organizationData) {
      this.loading = true;
      this.error = null;
      try {
        const response = await organizationService.updateOrganization(id, organizationData);
        // 更新成功后可以更新列表中的对应项或重新加载列表
        await this.fetchOrganizations({ page: this.pagination.currentPage, size: this.pagination.pageSize });
        this.selectedOrganization = null; // 清除选中的，如果适用
        return response.data; // 返回更新成功的数据
      } catch (err) {
        this.error = err.response?.data?.message || err.message || '更新机构失败';
         if (err.response?.data && typeof err.response.data === 'object') {
          return Promise.reject(err.response.data);
        }
        return Promise.reject(this.error);
      } finally {
        this.loading = false;
      }
    },
    async deleteOrganization(id) {
      this.loading = true;
      this.error = null;
      try {
        await organizationService.deleteOrganization(id);
        // 删除成功后重新加载列表
        // 如果删除的是当前页最后一条，可能需要调整页码
        await this.fetchOrganizations({ page: this.pagination.currentPage, size: this.pagination.pageSize });
      } catch (err) {
        this.error = err.response?.data?.message || err.message || '删除机构失败';
      } finally {
        this.loading = false;
      }
    },
    selectOrganization(organization) {
      this.selectedOrganization = organization;
    },
    clearSelectedOrganization() {
      this.selectedOrganization = null;
    }
  },
  getters: {
    isLoading: (state) => state.loading,
    hasError: (state) => state.error !== null,
    getOrganizationsList: (state) => state.organizations,
    getSelectedOrganization: (state) => state.selectedOrganization,
    getPagination: (state) => state.pagination,
  }
}); 