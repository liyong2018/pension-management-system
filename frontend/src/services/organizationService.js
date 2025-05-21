import axios from 'axios';

const API_URL = '/api/organizations'; // 根据 vite.config.js 中的代理配置

// 创建通用的 axios 实例
const apiClient = axios.create({
  baseURL: '/', // 因为 vite 会处理代理，所以这里可以是根路径
  headers: {
    'Content-Type': 'application/json',
  },
});

export default {
  /**
   * 获取机构列表（分页）
   * @param {number} page - 页码 (0-indexed)
   * @param {number} size - 每页大小
   * @param {string} sort - 排序字段, 例如: name,asc
   * @returns {Promise<Object>}
   */
  getOrganizations(page = 0, size = 10, sort = 'id,asc') {
    return apiClient.get(API_URL, {
      params: { page, size, sort },
    });
  },

  /**
   * 根据ID获取机构信息
   * @param {number} id - 机构ID
   * @returns {Promise<Object>}
   */
  getOrganizationById(id) {
    return apiClient.get(`${API_URL}/${id}`);
  },

  /**
   * 创建新机构
   * @param {Object} organizationData - 机构数据
   * @returns {Promise<Object>}
   */
  createOrganization(organizationData) {
    return apiClient.post(API_URL, organizationData);
  },

  /**
   * 更新机构信息
   * @param {number} id - 机构ID
   * @param {Object} organizationData - 需要更新的机构数据
   * @returns {Promise<Object>}
   */
  updateOrganization(id, organizationData) {
    return apiClient.put(`${API_URL}/${id}`, organizationData);
  },

  /**
   * 删除机构
   * @param {number} id - 机构ID
   * @returns {Promise<Object>}
   */
  deleteOrganization(id) {
    return apiClient.delete(`${API_URL}/${id}`);
  },

  /**
   * 根据名称搜索机构 (假设后端支持此功能, 如果不支持则前端自行过滤或调整)
   * 请注意: 后端 OrganizationService 已包含 getOrganizationByName，但控制器未暴露，
   * 如果需要此功能，需要在 OrganizationController 中添加相应端点。
   * 此处暂时注释掉，如果需要，请先在后端实现。
   */
  // findOrganizationsByName(name) {
  //   return apiClient.get(API_URL, { params: { name } });
  // }
}; 