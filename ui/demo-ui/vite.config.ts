import { resolve } from 'path'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import ElementPlus from 'unplugin-element-plus/vite'
import WindiCSS from 'vite-plugin-windicss'
import { createSvgIconsPlugin } from 'vite-plugin-svg-icons'
import PurgeIcons from 'vite-plugin-purge-icons'

// 当前执行node命令时文件夹的地址（工作目录）
const root = process.cwd()

// 路径查找
function pathResolve(dir: string) {
  return resolve(root, '.', dir)
}

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    // unplugin-element-plus，按需引入element-plus
    // https://element-plus.org/zh-CN/guide/quickstart.html#%E6%89%8B%E5%8A%A8%E5%AF%BC%E5%85%A5
    // https://github.com/element-plus/unplugin-element-plus#readme
    ElementPlus({
      // options
    }),
    WindiCSS(),
    PurgeIcons(),
    createSvgIconsPlugin({
      iconDirs: [pathResolve('src/assets/svgs')],
      symbolId: 'icon-[dir]-[name]',
      svgoOptions: true
    })
  ],
  server: {
    hmr: {
      // 打开浏览器页面错误信息调试浮层
      overlay: true
    }
  },
  resolve: {
    extensions: ['.mjs', '.js', '.ts', '.jsx', '.tsx', '.json', '.less', '.css'],
    alias: [
      {
        find: /\@\//,
        replacement: `${pathResolve('src')}/`
      }
    ]
  },
})
