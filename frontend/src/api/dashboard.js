/**
 * 首页数据API服务
 */
import request from '@/utils/request';

/**
 * 获取首页统计数据
 */
export const getDashboardStats = async () => {
  try {
    return await request.get('/dashboard/stats');
  } catch (error) {
    console.error('获取首页统计数据失败:', error);
    throw error;
  }
};

/**
 * 获取最近告警数据
 */
export const getRecentAlarms = async () => {
  try {
    return await request.get('/dashboard/recent-alarms');
  } catch (error) {
    console.error('获取最近告警数据失败:', error);
    throw error;
  }
};

/**
 * 获取地图数据
 */
export const getMapData = async () => {
  try {
    return await request.get('/dashboard/map-data');
  } catch (error) {
    console.error('获取地图数据失败:', error);
    throw error;
  }
};

export default {
  getDashboardStats,
  getRecentAlarms,
  getMapData
}; 