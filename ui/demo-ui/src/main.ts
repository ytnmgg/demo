import { createApp } from 'vue'
import App from '@/App.vue'

// 引入全局样式
import '@/styles/index.less'

// 引入 windi css
import '@/plugins/windi.css'

// 引入状态管理
import { setupStore } from '@/store'

// 引入路由
import { setupRouter } from '@/router'

// 引入组件库 element-plus
import { setupElementPlus } from '@/plugins/elementPlus'

const start = async () => {
  const app = createApp(App)

  setupStore(app)
  
  setupRouter(app)

  setupElementPlus(app)

  app.mount('#app')
}

start()

