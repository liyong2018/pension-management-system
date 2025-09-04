// pages/info-detail/info-detail.js
const api = require('../../utils/api.js');

Page({
  /**
   * 页面的初始数据
   */
  data: {
    loading: true,
    error: '',
    infoId: '',
    category: '',
    infoDetail: {
      id: '',
      title: '',
      content: '',
      summary: '',
      source: '',
      author: '',
      publishTime: '',
      readCount: 0,
      tag: '',
      images: [],
      relatedNews: []
    }
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    console.log('资讯详情页面加载，参数：', options);
    
    if (options.id) {
      this.setData({
        infoId: options.id,
        category: options.category || 'news'
      });
      this.loadInfoDetail(options.id, options.category);
    } else {
      this.setData({
        loading: false,
        error: '缺少资讯ID参数'
      });
    }
  },

  /**
   * 加载资讯详情
   */
  async loadInfoDetail(id, category) {
    try {
      this.setData({ loading: true, error: '' });
      
      // 模拟API调用，实际项目中应该调用真实API
      const mockData = this.getMockData(id, category);
      
      // 模拟网络延迟
      await new Promise(resolve => setTimeout(resolve, 800));
      
      this.setData({
        infoDetail: mockData,
        loading: false
      });
      
      // 设置页面标题
      wx.setNavigationBarTitle({
        title: mockData.title.length > 10 ? mockData.title.substring(0, 10) + '...' : mockData.title
      });
      
      // 增加阅读量
      this.increaseReadCount(id);
      
    } catch (error) {
      console.error('加载资讯详情失败：', error);
      this.setData({
        loading: false,
        error: '加载资讯详情失败，请重试'
      });
    }
  },

  /**
   * 获取模拟数据
   */
  getMockData(id, category) {
    const categoryMap = {
      'news': {
        title: '全国老龄工作会议召开，部署新时代老龄工作重点任务',
        tag: '热点',
        content: `<div class="article-content">
          <p>近日，全国老龄工作会议在北京召开，会议深入学习贯彻习近平总书记关于老龄工作的重要指示精神，总结"十三五"老龄事业发展成就，分析新时代人口老龄化面临的新形势新任务，部署"十四五"老龄工作重点任务。</p>
          
          <h3>会议主要内容</h3>
          <p>会议指出，要坚持以人民为中心的发展思想，实施积极应对人口老龄化国家战略，把积极老龄观、健康老龄化理念融入经济社会发展全过程。</p>
          
          <h3>重点工作部署</h3>
          <ul>
            <li>完善老龄工作体制机制</li>
            <li>加强老年人权益保障</li>
            <li>推进养老服务体系建设</li>
            <li>促进老年人社会参与</li>
            <li>营造敬老爱老社会氛围</li>
          </ul>
          
          <p>会议强调，各地区各部门要高度重视老龄工作，切实把思想和行动统一到党中央决策部署上来，推动老龄事业高质量发展。</p>
        </div>`,
        source: '新华社',
        author: '记者 张明'
      },
      'policy': {
        title: '《关于推进基本养老服务体系建设的意见》正式发布',
        tag: '政策',
        content: `<div class="article-content">
          <p>国务院办公厅近日印发《关于推进基本养老服务体系建设的意见》，明确了基本养老服务的对象、内容和标准，推动建立覆盖全体老年人、权责清晰、保障适度、可持续的基本养老服务体系。</p>
          
          <h3>主要目标</h3>
          <p>到2025年，基本养老服务制度体系基本健全，基本养老服务清单不断完善，服务对象、服务内容、服务标准等清晰明确。</p>
          
          <h3>重点任务</h3>
          <ul>
            <li>建立基本养老服务清单制度</li>
            <li>建立精准服务主动响应机制</li>
            <li>完善基本养老服务保障机制</li>
            <li>提高基本养老服务供给能力</li>
            <li>提升基本养老服务便民水平</li>
          </ul>
        </div>`,
        source: '中国政府网',
        author: '政策解读组'
      },
      'finance': {
        title: '2024年养老金上调3.8%，惠及1.3亿退休人员',
        tag: '财政',
        content: `<div class="article-content">
          <p>人力资源社会保障部、财政部近日发布通知，决定2024年继续统一调整企业和机关事业单位退休人员基本养老金，总体调整水平为2023年退休人员月人均基本养老金的3.8%。</p>
          
          <h3>调整范围</h3>
          <p>2023年12月31日前已按规定办理退休手续并按月领取基本养老金的企业和机关事业单位退休人员。</p>
          
          <h3>调整方式</h3>
          <ul>
            <li>定额调整：体现社会公平</li>
            <li>挂钩调整：体现"多缴多得"、"长缴多得"</li>
            <li>适当倾斜：体现重点关怀</li>
          </ul>
          
          <p>此次调整将惠及1.3亿退休人员，进一步保障退休人员基本生活。</p>
        </div>`,
        source: '人民日报',
        author: '财经记者'
      },
      'military': {
        title: '退役军人事务部：做好退役军人养老服务保障工作',
        tag: '军事',
        content: `<div class="article-content">
          <p>退役军人事务部近日召开专题会议，研究部署退役军人养老服务保障工作，要求各地退役军人事务部门要高度重视，切实做好退役军人养老服务保障。</p>
          
          <h3>工作要求</h3>
          <p>要建立健全退役军人养老服务保障制度，完善服务体系，提升服务质量，确保退役军人老有所养、老有所依。</p>
          
          <h3>重点措施</h3>
          <ul>
            <li>建立退役军人养老服务信息库</li>
            <li>完善优抚对象养老服务政策</li>
            <li>加强光荣院建设管理</li>
            <li>推进医养结合服务</li>
            <li>开展志愿服务活动</li>
          </ul>
        </div>`,
        source: '退役军人事务部',
        author: '部门发言人'
      }
    };

    const baseData = categoryMap[category] || categoryMap['news'];
    
    return {
      id: id,
      title: baseData.title,
      content: baseData.content,
      summary: baseData.title,
      source: baseData.source,
      author: baseData.author,
      publishTime: '2024-01-15 10:30',
      readCount: Math.floor(Math.random() * 1000) + 100,
      tag: baseData.tag,
      images: [],
      relatedNews: [
        {
          id: '2',
          title: '社区养老服务中心建设加快推进',
          publishTime: '2024-01-14'
        },
        {
          id: '3',
          title: '智慧养老平台助力居家养老',
          publishTime: '2024-01-13'
        }
      ]
    };
  },

  /**
   * 增加阅读量
   */
  async increaseReadCount(id) {
    try {
      // 实际项目中应该调用API增加阅读量
      console.log('增加阅读量：', id);
    } catch (error) {
      console.error('增加阅读量失败：', error);
    }
  },

  /**
   * 重试加载
   */
  onRetry() {
    if (this.data.infoId) {
      this.loadInfoDetail(this.data.infoId, this.data.category);
    }
  },

  /**
   * 分享文章
   */
  onShare() {
    const { infoDetail } = this.data;
    wx.showShareMenu({
      withShareTicket: true,
      menus: ['shareAppMessage', 'shareTimeline']
    });
  },

  /**
   * 查看相关新闻
   */
  onViewRelated(e) {
    const id = e.currentTarget.dataset.id;
    wx.redirectTo({
      url: `/pages/info-detail/info-detail?id=${id}&category=${this.data.category}`
    });
  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh() {
    if (this.data.infoId) {
      this.loadInfoDetail(this.data.infoId, this.data.category).finally(() => {
        wx.stopPullDownRefresh();
      });
    } else {
      wx.stopPullDownRefresh();
    }
  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage() {
    const { infoDetail } = this.data;
    return {
      title: infoDetail.title,
      path: `/pages/info-detail/info-detail?id=${infoDetail.id}&category=${this.data.category}`,
      imageUrl: '/images/news-share.png'
    };
  }
});