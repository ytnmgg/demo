import type { App } from 'vue'
import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { getAccessToken } from '@/utils/auth'
import { Names } from './router-names';
import NProgress from 'nprogress'
import 'nprogress/nprogress.css' 

export const routes: Array<RouteRecordRaw> = [
  {
    path: '/login',
    component: () => import('@/views/LoginV2/Login.vue'),
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
          noCache: true,
          affix: true,
          activeMenu: "user",
        }
      },
      {
        path: 'role',
        component: () => import('@/views/Role/Role.vue'),
        name: Names.ROLE,
        meta: {
          title: "角色管理",
          noCache: true,
          affix: true,
          activeMenu: "role",
        }
      },
      {
        path: 'permission',
        component: () => import('@/views/Permission/Permission.vue'),
        name: Names.PERMISSION,
        meta: {
          title: "权限管理",
          noCache: true,
          affix: true,
          activeMenu: "permission",
        }
      },
      {
        path: 'logins',
        component: () => import('@/views/Logins/Logins.vue'),
        name: Names.LOGINS,
        meta: {
          title: "在线用户管理",
          noCache: true,
          affix: true,
          activeMenu: "logins",
        }
      },
      {
        path: 'task',
        component: () => import('@/views/Task/Task.vue'),
        name: Names.TASK,
        meta: {
          title: "任务管理",
          noCache: true,
          affix: true,
          activeMenu: "task",
        }
      },
      {
        path: 'log-access',
        component: () => import('@/views/Log/ACCESS/ACCESS-LOG.vue'),
        name: Names.ACCESS_LOG,
        meta: {
          title: "access日志",
          icon: 'ep:home-filled',
          noCache: true,
          affix: true,
          activeMenu: "log-access",
        }
      },
      {
        path: 'log-login',
        component: () => import('@/views/Log/LOGIN/LOGIN-LOG.vue'),
        name: Names.LOGIN_LOG,
        meta: {
          title: "登录日志管理",
          icon: 'ep:home-filled',
          noCache: true,
          affix: true,
          activeMenu: "log-login",
        }
      },
      {
        path: 'log-op',
        component: () => import('@/views/Log/OP/OP-LOG.vue'),
        name: Names.OP_LOG,
        meta: {
          title: "操作日志",
          icon: 'ep:home-filled',
          noCache: true,
          affix: true,
          activeMenu: "log-op",
        }
      },
      {
        path: 'kafka',
        component: () => import('@/views/Kafka/Kafka.vue'),
        name: "kafka",
        meta: {
          activeMenu: "kafka",
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

// 配置进度条
NProgress.configure({
  showSpinner: false
})

// 路由不重定向白名单
const whiteList = [
  '/login',
  '/register'
]

// 路由加载前
router.beforeEach(async (to, from) => {
  // 开启页面进度条
  NProgress.start()

  if (!getAccessToken()) {
    if (whiteList.indexOf(to.path) !== -1) {
      return true;
    } else {
      //全部重定向到登录页
      return '/login?redirect=' + to.fullPath;
    }
  }
})

// 路由加载后
router.afterEach((to) => {
  // 关闭页面进度条
  NProgress.done()
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