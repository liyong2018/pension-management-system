import request from '@/utils/request'

export const volunteerApi = {
  // 获取志愿者列表
  getVolunteerList(params) {
    return request({
      url: '/volunteers',
      method: 'get',
      params
    })
  },

  // 获取志愿者详情
  getVolunteerById(id) {
    return request({
      url: `/volunteers/${id}`,
      method: 'get'
    })
  },

  // 创建志愿者
  createVolunteer(data) {
    return request({
      url: '/volunteers',
      method: 'post',
      data
    })
  },

  // 更新志愿者
  updateVolunteer(id, data) {
    return request({
      url: `/volunteers/${id}`,
      method: 'put',
      data
    })
  },

  // 删除志愿者
  deleteVolunteer(id) {
    return request({
      url: `/volunteers/${id}`,
      method: 'delete'
    })
  },

  // 更新志愿者状态
  updateVolunteerStatus(id, status) {
    return request({
      url: `/volunteers/${id}/status`,
      method: 'put',
      params: { status }
    })
  },

  // 更新志愿者服务统计
  updateServiceStats(id, hours, points) {
    return request({
      url: `/volunteers/${id}/service-stats`,
      method: 'put',
      params: { hours, points }
    })
  },

  // 获取优秀志愿者排行榜
  getTopVolunteers() {
    return request({
      url: '/volunteers/top',
      method: 'get'
    })
  },

  // 获取志愿者统计信息
  getVolunteerStats() {
    return request({
      url: '/volunteers/stats',
      method: 'get'
    })
  }
} 