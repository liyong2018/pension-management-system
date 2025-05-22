import request from '@/utils/request'

export const elderlyProfileApi = {
  // 获取人员列表
  getList(params) {
    return request({
      url: '/elderly-profiles',
      method: 'get',
      params
    })
  },

  // 获取人员详情
  getById(id) {
    return request({
      url: `/elderly-profiles/${id}`,
      method: 'get'
    })
  },

  // 创建人员档案
  create(data) {
    return request({
      url: '/elderly-profiles',
      method: 'post',
      data
    })
  },

  // 更新人员档案
  update(id, data) {
    return request({
      url: `/elderly-profiles/${id}`,
      method: 'put',
      data
    })
  },

  // 删除人员档案
  delete(id) {
    return request({
      url: `/elderly-profiles/${id}`,
      method: 'delete'
    })
  },

  // 批量删除人员档案
  batchDelete(ids) {
    return request({
      url: '/elderly-profiles/batch',
      method: 'delete',
      data: ids
    })
  },

  // 获取养老类型统计
  getPensionTypeStatistics() {
    return request({
      url: '/elderly-profiles/pension-type-statistics',
      method: 'get'
    })
  },

  // 获取能力评估统计
  getAbilityAssessmentStatistics() {
    return request({
      url: '/elderly-profiles/ability-assessment-statistics',
      method: 'get'
    })
  },

  // 根据机构ID获取人员列表
  getByOrganizationId(organizationId) {
    return request({
      url: `/elderly-profiles/by-organization/${organizationId}`,
      method: 'get'
    })
  }
} 