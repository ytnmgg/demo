import type { App } from 'vue'

// 需要全局引入一些组件，如ElScrollbar，不然一些下拉项样式有问题
import ElementPlus from 'element-plus'

import 'element-plus/dist/index.css'

// const plugins = [ElLoading]

// const components = [ElScrollbar, ElButton]

export const setupElementPlus = (app: App<Element>) => {
  // plugins.forEach((plugin) => {
  //   app.use(plugin)
  // })

  // components.forEach((component) => {
  //   app.component(component.name, component)
  // })
  app.use(ElementPlus)
}