import request from '@/utils/request'

export const dictionaryApi = {
  // 获取字典列表（分页）
  getList(params) {
    return request({
      url: '/dictionaries',
      method: 'get',
      params
    })
  },

  // 根据类型获取字典列表
  getByType(dictType) {
    return request({
      url: `/dictionaries/type/${dictType}`,
      method: 'get'
    })
  },

  // 获取字典详情
  getById(id) {
    return request({
      url: `/dictionaries/${id}`,
      method: 'get'
    })
  },

  // 创建字典
  create(data) {
    return request({
      url: '/dictionaries',
      method: 'post',
      data
    })
  },

  // 更新字典
  update(id, data) {
    return request({
      url: `/dictionaries/${id}`,
      method: 'put',
      data
    })
  },

  // 删除字典
  delete(id) {
    return request({
      url: `/dictionaries/${id}`,
      method: 'delete'
    })
  },

  // 批量删除字典
  batchDelete(ids) {
    return request({
      url: '/dictionaries/batch',
      method: 'delete',
      data: ids
    })
  },

  // 获取所有字典
  getAll() {
    return request({
      url: '/dictionaries/all',
      method: 'get'
    })
  },

  // 根据类型统计数量
  countByType(dictType) {
    return request({
      url: `/dictionaries/count/${dictType}`,
      method: 'get'
    })
  }
} 