import { defineStore } from 'pinia'
import { store } from '../index'
import { cloneDeep } from 'lodash-es'
import type { RouteRecordRaw } from 'vue-router'
import { routes } from '@/router'
// import { generateRoute, flatMultiLevelRoutes } from '@/utils/routerHelper'
import { getAsyncRoutesApi } from '@/api/login'
import { useCache } from '@/hooks/web/useCache'

const { wsCache } = useCache()

export interface PermissionState {
  routers: RouteRecordRaw[]
  addRouters: RouteRecordRaw[]
  menuTabRouters: RouteRecordRaw[]
}

export const usePermissionStore = defineStore({
  id: 'permission',
  state: (): PermissionState => ({
    routers: [],
    addRouters: [],
    menuTabRouters: []
  }),
  persist: {
    enabled: true
  },
  getters: {
    getRouters(): RouteRecordRaw[] {
      return this.routers
    },
    getAddRouters(): RouteRecordRaw[] {
      // return flatMultiLevelRoutes(cloneDeep(this.addRouters))
      return this.routers
    },
    getMenuTabRouters(): RouteRecordRaw[] {
      return this.menuTabRouters
    }
  },
  actions: {
    async generateRoutes(): Promise<unknown> {
      return new Promise<void>(async (resolve) => {
        let res: RouteRecordRaw[]
        if (wsCache.get('roleRouters')) {
          res = wsCache.get('roleRouters') as RouteRecordRaw[]
        } else {
          res = await getAsyncRoutesApi()
          wsCache.set('roleRouters', res)
        }
        // const routerMap: RouteRecordRaw[] = generateRoute(res as RouteRecordRaw[])
        const routerMap: RouteRecordRaw[] = routes
        // 动态路由，404一定要放到最后面
        this.addRouters = routerMap.concat([
          {
            path: '/:path(.*)*',
            redirect: '/404',
            name: '404Page',
            meta: {
              hidden: true,
              breadcrumb: false
            }
          }
        ])
        // 渲染菜单的所有路由
        this.routers = cloneDeep(routes).concat(routerMap)
        resolve()
      })
    },
    setMenuTabRouters(routers: RouteRecordRaw[]): void {
      this.menuTabRouters = routers
    }
  }
})

export const usePermissionStoreWithOut = () => {
  return usePermissionStore(store)
}
