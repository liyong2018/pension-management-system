import request from '@/utils/request'

export const serviceRecordApi = {
  // 获取服务记录列表
  getList(params) {
    return request({
      url: '/service-records',
      method: 'get',
      params
    })
  },

  // 获取服务记录详情
  getById(id) {
    return request({
      url: `/service-records/${id}`,
      method: 'get'
    })
  },

  // 创建服务记录
  create(data) {
    return request({
      url: '/service-records',
      method: 'post',
      data
    })
  },

  // 更新服务记录
  update(id, data) {
    return request({
      url: `/service-records/${id}`,
      method: 'put',
      data
    })
  },

  // 删除服务记录
  delete(id) {
    return request({
      url: `/service-records/${id}`,
      method: 'delete'
    })
  },

  // 批量删除服务记录
  batchDelete(ids) {
    return request({
      url: '/service-records/batch',
      method: 'delete',
      data: ids
    })
  },

  // 根据老人ID获取服务记录
  getByElderlyId(elderlyId) {
    return request({
      url: `/service-records/elderly/${elderlyId}`,
      method: 'get'
    })
  },

  // 获取状态统计
  getStatusStatistics() {
    return request({
      url: '/service-records/status-statistics',
      method: 'get'
    })
  },

  // 获取服务提供者类型统计
  getProviderTypeStatistics() {
    return request({
      url: '/service-records/provider-type-statistics',
      method: 'get'
    })
  },

  // 获取待评价的服务记录
  getRecordsForEvaluation() {
    return request({
      url: '/service-records/for-evaluation',
      method: 'get'
    })
  },

  // 评价服务记录
  evaluate(id, evaluationData) {
    return request({
      url: `/service-records/${id}/evaluate`,
      method: 'put',
      data: evaluationData
    })
  }
} 