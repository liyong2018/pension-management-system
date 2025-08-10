// 服务订单相关API
const request = require('../utils/request')

// 获取服务订单列表
function getServiceRecords(params) {
  return request({
    url: '/api/service-records',
    method: 'GET',
    data: params
  })
}

// 获取服务订单详情
function getServiceRecordDetail(id) {
  return request({
    url: `/api/service-records/${id}`,
    method: 'GET'
  })
}

// 更新服务订单状态
function updateServiceRecordStatus(id, status) {
  return request({
    url: `/api/service-records/${id}/status`,
    method: 'PUT',
    data: { status }
  })
}

module.exports = {
  getServiceRecords,
  getServiceRecordDetail,
  updateServiceRecordStatus
}