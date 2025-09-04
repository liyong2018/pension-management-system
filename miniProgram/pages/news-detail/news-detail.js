// pages/news-detail/news-detail.js
const api = require('../../utils/api.js');

Page({
  /**
   * 页面的初始数据
   */
  data: {
    loading: true,
    error: '',
    newsId: '',
    newsDetail: {
      id: '',
      title: '',
      content: '',
      summary: '',
      image: '',
      author: '',
      publishTime: '',
      readCount: 0,
      likeCount: 0,
      commentCount: 0,
      isLiked: false,
      tags: [],
      relatedNews: []
    }
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    console.log('社区动态详情页面加载，参数：', options);
    
    if (options.id) {
      this.setData({
        newsId: options.id
      });
      this.loadNewsDetail(options.id);
    } else {
      this.setData({
        loading: false,
        error: '缺少新闻ID参数'
      });
    }
  },

  /**
   * 加载新闻详情
   */
  async loadNewsDetail(id) {
    try {
      this.setData({ loading: true, error: '' });
      
      // 模拟API调用，实际项目中应该调用真实API
      const mockData = this.getMockData(id);
      
      // 模拟网络延迟
      await new Promise(resolve => setTimeout(resolve, 800));
      
      this.setData({
        newsDetail: mockData,
        loading: false
      });
      
      // 设置页面标题
      wx.setNavigationBarTitle({
        title: mockData.title.length > 10 ? mockData.title.substring(0, 10) + '...' : mockData.title
      });
      
      // 增加阅读量
      this.increaseReadCount(id);
      
    } catch (error) {
      console.error('加载新闻详情失败：', error);
      this.setData({
        loading: false,
        error: '加载新闻详情失败，请重试'
      });
    }
  },

  /**
   * 获取模拟数据
   */
  getMockData(id) {
    const newsData = {
      '1': {
        title: '社区健康讲座通知',
        content: `<div class="news-content">
          <p>亲爱的社区居民朋友们：</p>
          
          <p>为了提高社区老年人的健康意识，普及健康知识，我们特邀请市人民医院的专家医生为大家举办健康讲座。</p>
          
          <h3>讲座详情</h3>
          <ul>
            <li><strong>时间：</strong>本周六（1月20日）上午9:00-11:00</li>
            <li><strong>地点：</strong>社区活动中心二楼会议室</li>
            <li><strong>主讲人：</strong>李医生（心血管内科主任医师）</li>
            <li><strong>主题：</strong>老年人心血管疾病预防与保健</li>
          </ul>
          
          <h3>讲座内容</h3>
          <p>本次讲座将重点介绍：</p>
          <ul>
            <li>常见心血管疾病的预防措施</li>
            <li>日常生活中的健康注意事项</li>
            <li>合理饮食与运动指导</li>
            <li>药物使用的注意事项</li>
            <li>紧急情况的处理方法</li>
          </ul>
          
          <p>讲座结束后，李医生还将为大家提供免费的健康咨询服务，欢迎大家踊跃参加！</p>
          
          <p><strong>报名方式：</strong>请到社区服务中心前台登记，或拨打电话：010-12345678</p>
          
          <p>期待您的参与！</p>
        </div>`,
        image: '/images/news1.jpg',
        author: '社区服务中心',
        tags: ['健康', '讲座', '社区活动']
      },
      '2': {
        title: '志愿者招募活动',
        content: `<div class="news-content">
          <p>亲爱的社区朋友们：</p>
          
          <p>为了更好地为社区老年人提供贴心服务，我们现面向社会招募爱心志愿者，诚邀您加入我们的志愿者团队！</p>
          
          <h3>招募对象</h3>
          <ul>
            <li>年龄18-65岁，身体健康</li>
            <li>有爱心、耐心，乐于助人</li>
            <li>能够定期参与志愿服务活动</li>
            <li>具备基本的沟通能力</li>
          </ul>
          
          <h3>服务内容</h3>
          <p>志愿者主要参与以下服务：</p>
          <ul>
            <li>陪伴聊天：与老年人交流，缓解孤独感</li>
            <li>生活协助：帮助购物、取药等日常事务</li>
            <li>健康监测：协助测量血压、血糖等</li>
            <li>文娱活动：组织开展各类文体活动</li>
            <li>应急响应：协助处理紧急情况</li>
          </ul>
          
          <h3>志愿者福利</h3>
          <ul>
            <li>提供专业培训和指导</li>
            <li>颁发志愿服务证书</li>
            <li>定期组织志愿者交流活动</li>
            <li>优秀志愿者将获得表彰奖励</li>
          </ul>
          
          <p><strong>报名方式：</strong></p>
          <p>请携带身份证到社区服务中心填写报名表，或关注我们的微信公众号在线报名。</p>
          
          <p>让我们一起用爱心温暖社区，用行动传递正能量！</p>
        </div>`,
        image: '/images/news2.jpg',
        author: '志愿者协会',
        tags: ['志愿服务', '招募', '爱心']
      }
    };

    const defaultData = {
      title: '社区最新动态',
      content: `<div class="news-content">
        <p>这里是社区动态的详细内容...</p>
      </div>`,
      image: '/images/default-news.jpg',
      author: '社区管理员',
      tags: ['社区', '动态']
    };

    const data = newsData[id] || defaultData;
    
    return {
      id: id,
      title: data.title,
      content: data.content,
      summary: data.title,
      image: data.image,
      author: data.author,
      publishTime: '2024-01-15 14:30',
      readCount: Math.floor(Math.random() * 500) + 50,
      likeCount: Math.floor(Math.random() * 100) + 10,
      commentCount: Math.floor(Math.random() * 50) + 5,
      isLiked: false,
      tags: data.tags,
      relatedNews: [
        {
          id: '3',
          title: '社区老年大学春季招生开始',
          publishTime: '2024-01-14',
          image: '/images/news3.jpg'
        },
        {
          id: '4',
          title: '社区医疗服务站升级改造完成',
          publishTime: '2024-01-13',
          image: '/images/news4.jpg'
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
    if (this.data.newsId) {
      this.loadNewsDetail(this.data.newsId);
    }
  },

  /**
   * 点赞/取消点赞
   */
  async onToggleLike() {
    try {
      const { newsDetail } = this.data;
      const newIsLiked = !newsDetail.isLiked;
      const newLikeCount = newIsLiked ? newsDetail.likeCount + 1 : newsDetail.likeCount - 1;
      
      this.setData({
        'newsDetail.isLiked': newIsLiked,
        'newsDetail.likeCount': newLikeCount
      });
      
      // 实际项目中应该调用API更新点赞状态
      console.log('点赞状态更新：', newIsLiked);
      
      wx.showToast({
        title: newIsLiked ? '点赞成功' : '取消点赞',
        icon: 'success',
        duration: 1000
      });
      
    } catch (error) {
      console.error('点赞操作失败：', error);
      wx.showToast({
        title: '操作失败',
        icon: 'none'
      });
    }
  },

  /**
   * 分享新闻
   */
  onShare() {
    const { newsDetail } = this.data;
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
      url: `/pages/news-detail/news-detail?id=${id}`
    });
  },

  /**
   * 预览图片
   */
  onPreviewImage() {
    const { newsDetail } = this.data;
    if (newsDetail.image) {
      wx.previewImage({
        urls: [newsDetail.image],
        current: newsDetail.image
      });
    }
  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh() {
    if (this.data.newsId) {
      this.loadNewsDetail(this.data.newsId).finally(() => {
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
    const { newsDetail } = this.data;
    return {
      title: newsDetail.title,
      path: `/pages/news-detail/news-detail?id=${newsDetail.id}`,
      imageUrl: newsDetail.image || '/images/news-share.png'
    };
  }
});