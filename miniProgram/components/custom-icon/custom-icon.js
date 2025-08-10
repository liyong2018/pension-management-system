Component({
  properties: {
    name: {
      type: String,
      value: ''
    },
    size: {
      type: String,
      value: '24px'
    },
    color: {
      type: String,
      value: '#000000'
    },
    // 是否强制使用PNG图标
    usePng: {
      type: Boolean,
      value: false
    }
  },

  data: {
    // PNG图标映射表
    pngIconMap: {
      'user': '/images/icons/user.png',
      'home': '/images/icons/home.png',
      'heart': '/images/icons/heart.png',
      'search': '/images/icons/search.png',
      'call': '/images/icons/call.png',
      'calendar': '/images/icons/calendar.png',
      'setting': '/images/icons/setting.png',
      'close': '/images/icons/close.png',
      'time': '/images/icons/time.png',
      'share': '/images/icons/share.png',
      'add': '/images/icons/add.png',
      'usergroup': '/images/icons/usergroup.png',
      'chart-bubble': '/images/icons/chart-bubble.png',
      'chart-line': '/images/icons/chart-line.png',
      'lightbulb': '/images/icons/lightbulb.png',
      'bluetooth': '/images/icons/bluetooth.png'
    },
    showPngIcon: false,
    fontLoaded: false
  },

  lifetimes: {
    attached() {
      this.checkFontLoaded();
    }
  },

  methods: {
    // 检查字体是否加载成功
    checkFontLoaded() {
      const { name, usePng } = this.properties;
      
      // 如果强制使用PNG或者图标名在PNG映射表中，直接使用PNG
      if (usePng || this.data.pngIconMap[name]) {
        this.setData({
          showPngIcon: true
        });
        return;
      }

      // 尝试检测字体是否加载
      setTimeout(() => {
        // 简单的字体检测：如果是常见的TDesign图标名，尝试使用字体图标
        const tdesignIcons = [
          'user', 'home', 'heart', 'search', 'call', 'calendar',
          'setting', 'close', 'time', 'share', 'add', 'usergroup',
          'chart-bubble', 'chart-line', 'lightbulb', 'bluetooth'
        ];
        
        if (tdesignIcons.includes(name)) {
          // 如果有对应的PNG图标，使用PNG作为备选
          if (this.data.pngIconMap[name]) {
            this.setData({
              showPngIcon: true
            });
          } else {
            this.setData({
              fontLoaded: true
            });
          }
        } else {
          // 其他图标尝试使用TDesign字体图标
          this.setData({
            fontLoaded: true
          });
        }
      }, 100);
    },

    // 图标点击事件
    onIconTap() {
      this.triggerEvent('tap', {
        name: this.properties.name
      });
    },

    // PNG图标加载失败时的处理
    onPngError() {
      console.warn(`PNG图标加载失败: ${this.properties.name}`);
      // 回退到字体图标
      this.setData({
        showPngIcon: false,
        fontLoaded: true
      });
    }
  }
});