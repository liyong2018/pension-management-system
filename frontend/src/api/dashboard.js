/**
 * 首页数据API服务
 */

const API_BASE_URL = '/api/dashboard';

/**
 * 获取首页统计数据
 */
export const getDashboardStats = async () => {
  try {
    const response = await fetch(`${API_BASE_URL}/stats`);
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    return await response.json();
  } catch (error) {
    console.error('获取首页统计数据失败:', error);
    throw error;
  }
};

/**
 * 获取实时告警数据
 */
export const getRecentAlarms = async () => {
  try {
    const response = await fetch(`${API_BASE_URL}/alarms`);
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    return await response.json();
  } catch (error) {
    console.error('获取告警数据失败:', error);
    throw error;
  }
};

/**
 * 获取地图数据
 */
export const getMapData = async () => {
  try {
    const response = await fetch(`${API_BASE_URL}/map`);
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    return await response.json();
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