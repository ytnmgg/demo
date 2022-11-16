import type { App } from 'vue'

// 需要全局引入一些组件，如ElScrollbar，不然一些下拉项样式有问题
import ElementPlus from 'element-plus'

import 'element-plus/dist/index.css'

export const setupElementPlus = (app: App<Element>) => {

  app.use(ElementPlus)
}