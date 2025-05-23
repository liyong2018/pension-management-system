import request from '@/utils/request'

export const smartDeviceApi = {
  // 获取设备列表（分页）
  getList(params) {
    return request({
      url: '/smart-devices',
      method: 'get',
      params
    })
  },

  // 搜索设备
  searchDevices(params) {
    return request({
      url: '/smart-devices/search',
      method: 'get',
      params
    })
  },

  // 获取单个设备详情
  getById(id) {
    return request({
      url: `/smart-devices/${id}`,
      method: 'get'
    })
  },

  // 创建设备
  create(data) {
    return request({
      url: '/smart-devices',
      method: 'post',
      data
    })
  },

  // 更新设备
  update(id, data) {
    return request({
      url: `/smart-devices/${id}`,
      method: 'put',
      data
    })
  },

  // 删除设备
  delete(id) {
    return request({
      url: `/smart-devices/${id}`,
      method: 'delete'
    })
  },

  // 批量删除设备
  batchDelete(ids) {
    return request({
      url: '/smart-devices/batch',
      method: 'delete',
      data: ids
    })
  },

  // 更新设备状态
  updateStatus(id, status) {
    return request({
      url: `/smart-devices/${id}/status`,
      method: 'put',
      params: { status }
    })
  },

  // 更新设备电量
  updateBattery(id, batteryLevel) {
    return request({
      url: `/smart-devices/${id}/battery`,
      method: 'put',
      params: { batteryLevel }
    })
  },

  // 更新设备实时数据
  updateRealtimeData(id, data) {
    return request({
      url: `/smart-devices/${id}/realtime-data`,
      method: 'put',
      data
    })
  },

  // 获取指定老人的设备列表
  getByElderlyId(elderlyId) {
    return request({
      url: `/smart-devices/elderly/${elderlyId}`,
      method: 'get'
    })
  },

  // 获取指定机构的设备列表
  getByOrganizationId(organizationId) {
    return request({
      url: `/smart-devices/organization/${organizationId}`,
      method: 'get'
    })
  },

  // 获取需要维护的设备列表
  getMaintenanceNeeded() {
    return request({
      url: '/smart-devices/maintenance/needed',
      method: 'get'
    })
  },

  // 获取设备类型统计
  getTypeStatistics() {
    return request({
      url: '/smart-devices/statistics/type',
      method: 'get'
    })
  },

  // 获取设备状态统计
  getStatusStatistics() {
    return request({
      url: '/smart-devices/statistics/status',
      method: 'get'
    })
  }
} 