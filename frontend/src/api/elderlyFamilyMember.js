import request from '@/utils/request'

export const elderlyFamilyMemberApi = {
  // 获取老人的所有家属
  getAllByElderlyId(elderlyId) {
    return request({
      url: `/api/elderly-profiles/${elderlyId}/family-members`,
      method: 'get'
    })
  },

  // 获取家属详情
  getById(elderlyId, id) {
    return request({
      url: `/api/elderly-profiles/${elderlyId}/family-members/${id}`,
      method: 'get'
    })
  },

  // 添加家属
  create(elderlyId, data) {
    return request({
      url: `/api/elderly-profiles/${elderlyId}/family-members`,
      method: 'post',
      data
    })
  },

  // 更新家属信息
  update(elderlyId, id, data) {
    return request({
      url: `/api/elderly-profiles/${elderlyId}/family-members/${id}`,
      method: 'put',
      data
    })
  },

  // 删除家属
  delete(elderlyId, id) {
    return request({
      url: `/api/elderly-profiles/${elderlyId}/family-members/${id}`,
      method: 'delete'
    })
  },

  // 删除老人的所有家属
  deleteAllByElderlyId(elderlyId) {
    return request({
      url: `/api/elderly-profiles/${elderlyId}/family-members`,
      method: 'delete'
    })
  }
} 