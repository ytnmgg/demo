import type { App } from 'vue'
import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { getAccessToken } from '@/utils/auth'
import { Names } from './router-names';

export const routes: Array<RouteRecordRaw> = [
  {
    path: '/login',
    component: () => import('@/views/Login/Login.vue'),
    name: Names.LOGIN,
    meta: {
      hidden: true,
      title: "登录",
      noTagsView: true
    }
  },
  {
    path: '/',
    component: () => import('@/layout/Layout.vue'),
    redirect: '/index',
    name: Names.HOME,
    meta: {},
    children: [
      {
        path: 'index',
        component: () => import('@/views/Home/Index.vue'),
        name: Names.INDEX,
        meta: {
          title: "首页",
          icon: 'ep:home-filled',
          noCache: true,
          affix: true,
          activeMenu: "index",
        }
      },
      {
        path: 'user',
        component: () => import('@/views/User/User.vue'),
        name: Names.USER,
        meta: {
          title: "用户管理",
          icon: 'ep:home-filled',
          noCache: true,
          affix: true,
          activeMenu: "user",
        }
      },
      {
        path: 'log-login',
        component: () => import('@/views/log/LOGIN/LOGIN-LOG.vue'),
        name: Names.LOGIN_LOG,
        meta: {
          title: "登录日志管理",
          icon: 'ep:home-filled',
          noCache: true,
          affix: true,
          activeMenu: "log-login",
        }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(), // createWebHashHistory URL带#，createWebHistory URL不带#
  strict: true,
  routes, // `routes: routes` 的缩写
  scrollBehavior: () => ({ left: 0, top: 0 })
})

// 路由不重定向白名单
const whiteList = [
  '/login',
  '/register'
]

// 路由加载前
router.beforeEach(async (to, from) => {
  if (!getAccessToken()) {
    if (whiteList.indexOf(to.path) !== -1) {
      return true;
    } else {
      //全部重定向到登录页
      return '/login?redirect=' + to.fullPath;
    }
  }
})


export const setupRouter = (app: App<Element>) => {
  app.use(router)
}

const resetWhiteNameList = [Names.HOME, Names.LOGIN, Names.INDEX]

export const resetRouter = (): void => {
  router.getRoutes().forEach((route) => {
    const { name } = route
    if (name && !resetWhiteNameList.includes(name as Names)) {
      router.hasRoute(name) && router.removeRoute(name)
    }
  })
}

export default router