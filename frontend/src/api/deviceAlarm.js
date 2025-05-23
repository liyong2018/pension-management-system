import request from '@/utils/request'

export const deviceAlarmApi = {
  // 获取告警记录列表（分页）
  getList(params) {
    return request({
      url: '/device-alarms',
      method: 'get',
      params
    })
  },

  // 搜索告警记录
  searchAlarms(params) {
    return request({
      url: '/device-alarms/search',
      method: 'get',
      params
    })
  },

  // 获取单个告警记录详情
  getById(id) {
    return request({
      url: `/device-alarms/${id}`,
      method: 'get'
    })
  },

  // 创建告警记录
  create(data) {
    return request({
      url: '/device-alarms',
      method: 'post',
      data
    })
  },

  // 更新告警记录
  update(id, data) {
    return request({
      url: `/device-alarms/${id}`,
      method: 'put',
      data
    })
  },

  // 删除告警记录
  delete(id) {
    return request({
      url: `/device-alarms/${id}`,
      method: 'delete'
    })
  },

  // 批量删除告警记录
  batchDelete(ids) {
    return request({
      url: '/device-alarms/batch',
      method: 'delete',
      data: ids
    })
  },

  // 获取指定设备的告警记录
  getByDeviceId(deviceId) {
    return request({
      url: `/device-alarms/device/${deviceId}`,
      method: 'get'
    })
  },

  // 处理告警
  processAlarm(id, params) {
    return request({
      url: `/device-alarms/${id}/process`,
      method: 'put',
      params
    })
  },

  // 获取告警类型统计
  getTypeStatistics() {
    return request({
      url: '/device-alarms/statistics/type',
      method: 'get'
    })
  },

  // 获取告警级别统计
  getLevelStatistics() {
    return request({
      url: '/device-alarms/statistics/level',
      method: 'get'
    })
  },

  // 获取处理状态统计
  getStatusStatistics() {
    return request({
      url: '/device-alarms/statistics/status',
      method: 'get'
    })
  },

  // 获取未处理告警数量
  getUnprocessedCount() {
    return request({
      url: '/device-alarms/unprocessed/count',
      method: 'get'
    })
  }
} 