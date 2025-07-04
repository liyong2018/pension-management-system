import request from '@/utils/request';

const serviceStaffService = {
  // 获取人员列表
  getStaffList(params) {
    return request({
      url: '/service-staff',
      method: 'get',
      params
    });
  },

  // 获取人员统计数据
  getStaffStats() {
    return request({
      url: '/service-staff/stats',
      method: 'get'
    });
  },

  // 获取人员详情
  getStaffDetail(id) {
    return request({
      url: `/service-staff/${id}`,
      method: 'get'
    });
  },

  // 创建人员
  createStaff(data) {
    return request({
      url: '/service-staff',
      method: 'post',
      data
    });
  },

  // 更新人员信息
  updateStaff(id, data) {
    return request({
      url: `/service-staff/${id}`,
      method: 'put',
      data
    });
  },

  // 删除人员
  deleteStaff(id) {
    return request({
      url: `/service-staff/${id}`,
      method: 'delete'
    });
  },

  // 批量删除人员
  batchDeleteStaff(ids) {
    return request({
      url: '/service-staff/batch-delete',
      method: 'post',
      data: { ids }
    });
  },

  // 变更人员状态
  changeStaffStatus(id, status) {
    return request({
      url: `/service-staff/${id}/status`,
      method: 'put',
      data: { status }
    });
  },

  // 获取人员排班信息
  getStaffSchedule(id, params) {
    return request({
      url: `/service-staff/${id}/schedule`,
      method: 'get',
      params
    });
  },

  // 导出人员列表
  exportStaffList(params) {
    return request({
      url: '/service-staff/export',
      method: 'get',
      params,
      responseType: 'blob'
    });
  },

  // 导入人员数据
  importStaffData(file) {
    const formData = new FormData();
    formData.append('file', file);
    return request({
      url: '/service-staff/import',
      method: 'post',
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });
  }
};

export default serviceStaffService;