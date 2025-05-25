import request from '@/utils/request';

const organizationService = {
  /**
   * 获取机构列表（分页）
   * @param {Object} params - 查询参数
   * @param {number} params.pageNum - 页码 (1-indexed)
   * @param {number} params.pageSize - 每页大小
   * @param {string} params.name - 机构名称关键词
   * @returns {Promise<Object>}
   */
  async getOrganizations(params) {
    return request({
      url: 'organizations',
      method: 'get',
      params
    });
  },

  /**
   * 根据ID获取机构信息
   * @param {number} id - 机构ID
   * @returns {Promise<Object>}
   */
  getOrganizationById(id) {
    return request({
      url: `organizations/${id}`,
      method: 'get'
    });
  },

  /**
   * 创建新机构
   * @param {Object} organizationData - 机构数据
   * @returns {Promise<Object>}
   */
  createOrganization(data) {
    return request({
      url: 'organizations',
      method: 'post',
      data
    });
  },

  /**
   * 更新机构信息
   * @param {number} id - 机构ID
   * @param {Object} organizationData - 需要更新的机构数据
   * @returns {Promise<Object>}
   */
  updateOrganization(id, data) {
    return request({
      url: `organizations/${id}`,
      method: 'put',
      data
    });
  },

  /**
   * 删除机构
   * @param {number} id - 机构ID
   * @returns {Promise<Object>}
   */
  deleteOrganization(id) {
    return request({
      url: `organizations/${id}`,
      method: 'delete'
    });
  },

  /**
   * 批量删除机构
   * @param {number[]} ids - 机构ID数组
   * @returns {Promise<void>}
   */
  async batchDeleteOrganizations(ids) {
    try {
      await Promise.all(ids.map(id => this.deleteOrganization(id)));
    } catch (error) {
      console.error('批量删除失败:', error);
      throw new Error('批量删除失败，请重试');
    }
  },

  /**
   * 获取所有机构负责人列表
   * @returns {Promise<Object>}
   */
  getAllDirectors() {
    return request({
      url: 'system-users/directors',
      method: 'get'
    });
  },

  /**
   * 根据机构ID获取机构负责人列表
   * @param {number} organizationId - 机构ID（可选）
   * @returns {Promise<Object>}
   */
  getDirectorsByOrganization(organizationId) {
    return request({
      url: 'system-users/directors/by-organization',
      method: 'get',
      params: { organizationId }
    });
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

export default organizationService; 