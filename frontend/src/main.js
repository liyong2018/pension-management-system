import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'

// 引入 Element Plus
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
// 如果需要全局引入 Element Plus 图标
// import * as ElementPlusIconsVue from '@element-plus/icons-vue'

// 引入全局自定义样式 (如果需要)
// import './assets/main.css'

const app = createApp(App)

// 全局注册 Element Plus 图标 (如果需要)
// for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
//   app.component(key, component)
// }

app.use(createPinia()) // 使用 Pinia
app.use(router)       // 使用 Router
app.use(ElementPlus)  // 使用 Element Plus

app.mount('#app') 