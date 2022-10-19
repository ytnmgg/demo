import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse, AxiosError } from 'axios'
import { ElMessage, ElMessageBox, ElNotification } from 'element-plus'
import qs from 'qs'
import { config, errorCode } from '@/config/axios/config'
import { getAccessToken, removeToken, setToken, getRefreshToken } from '@/utils/auth'
import { resetRouter } from '@/router'
import { useCache } from '@/hooks/web/useCache'

// const tenantEnable = import.meta.env.VITE_APP_TENANT_ENABLE
const { result_code, base_url, request_timeout } = config

// 需要忽略的提示。忽略后，自动 Promise.reject('error')
const ignoreMsgs = [
  '无效的刷新令牌', // 刷新令牌被删除时，不用提示
  '刷新令牌已过期' // 使用刷新令牌，刷新获取新的访问令牌时，结果因为过期失败，此时需要忽略。否则，会导致继续 401，无法跳转到登出界面
]
// 是否显示重新登录
export const isRelogin = { show: false }
// Axios 无感知刷新令牌，参考 https://www.dashingdog.cn/article/11 与 https://segmentfault.com/a/1190000020210980 实现
// 请求队列
let requestList: any[] = []
// 是否正在刷新中
let isRefreshToken = false

// 创建axios实例
const service: AxiosInstance = axios.create({
  baseURL: base_url, // api 的 base_url
  timeout: request_timeout, // 请求超时时间
  withCredentials: false // 禁用 Cookie 等信息
})

// 不设置token白名单
const whiteList = [
  '/get_encrypt_key.json',
  '/login.json',
  '/register.json'
]

// request拦截器
service.interceptors.request.use(
  (config: AxiosRequestConfig) => {

    // 是否需要设置 token
    const needToken = whiteList.indexOf(config.url!) == -1 ? true : false;

    // const isToken = (config!.headers || {}).isToken === false    
    if (getAccessToken() && needToken) {
      (config as Recordable).headers.Authorization = 'Bearer ' + getAccessToken() // 让每个请求携带自定义token
    }

    // 设置租户
    // if (tenantEnable) {
    //   const tenantId = getTenantId()
    //   if (tenantId) (config as Recordable).headers.common['tenant-id'] = tenantId
    // }

    const params = config.params || {}
    const data = config.data || false
    if (
      config.method?.toUpperCase() === 'POST' &&
      config!.headers!['Content-Type'] === 'application/x-www-form-urlencoded'
    ) {
      config.data = qs.stringify(data)
    }

    // get参数编码
    if (config.method?.toUpperCase() === 'GET' && params) {
      let url = config.url + '?'
      for (const propName of Object.keys(params)) {
        const value = params[propName]
        if (value !== void 0 && value !== null && typeof value !== 'undefined') {
          if (typeof value === 'object') {
            for (const val of Object.keys(value)) {
              const params = propName + '[' + val + ']'
              const subPart = encodeURIComponent(params) + '='
              url += subPart + encodeURIComponent(value[val]) + '&'
            }
          } else {
            url += `${propName}=${encodeURIComponent(value)}&`
          }
        }
      }

      // 给 get 请求加上时间戳参数，避免从缓存中拿数据
      // const now = new Date().getTime()
      // params = params.substring(0, url.length - 1) + `?_t=${now}`
      url = url.slice(0, -1)
      config.params = {}
      config.url = url
    }

    return config
  },
  (error: AxiosError) => {
    // Do something with request error
    console.log(error) // for debug
    Promise.reject(error)
  }
)

// response 拦截器
service.interceptors.response.use(
  async (response: AxiosResponse<Recordable>) => {
    const { data } = response
    if (!data) {
      // 返回“[HTTP]请求没有返回值”;
      throw new Error()
    }

    // 未设置状态码则默认成功状态
    const code = parseInt(data.code) || result_code
    // 二进制数据则直接返回
    if (
      response.request.responseType === 'blob' ||
      response.request.responseType === 'arraybuffer'
    ) {
      return response.data
    }
    // 获取错误信息
    const msg = data.message || errorCode[code] || errorCode['default']
    if (ignoreMsgs.indexOf(msg) !== -1) {
      // 如果是忽略的错误码，直接返回 msg 异常
      return Promise.reject(msg)
    } else if (code === 401) {
      // 如果未认证，并且未进行刷新令牌，说明可能是访问令牌过期了
      if (!isRefreshToken) {
        isRefreshToken = true
        // 1. 如果获取不到刷新令牌，则只能执行登出操作
        if (!getRefreshToken()) {
          return handleAuthorized()
        }
        // 2. 进行刷新访问令牌
        try {
          const refreshTokenRes = await refreshToken()
          // 2.1 刷新成功，则回放队列的请求 + 当前请求
          setToken(refreshTokenRes.data)
          requestList.forEach((cb: any) => cb())
          return service(response.config)
        } catch (e) {
          // 为什么需要 catch 异常呢？刷新失败时，请求因为 Promise.reject 触发异常。
          // 2.2 刷新失败，只回放队列的请求
          requestList.forEach((cb: any) => cb())
          // 提示是否要登出。即不回放当前请求！不然会形成递归
          return handleAuthorized()
        } finally {
          requestList = []
          isRefreshToken = false
        }
      } else {
        // 添加到队列，等待刷新获取到新的令牌
        return new Promise((resolve) => {
          requestList.push(() => {
            ; (config as Recordable).headers.Authorization = 'Bearer ' + getAccessToken() // 让每个请求携带自定义token 请根据实际情况自行修改
            resolve(service(response.config))
          })
        })
      }
    } else if (code === 500) {
      ElMessage.error(t('sys.api.errMsg500'))
      return Promise.reject(new Error(msg))
    } else if (code === 901) {
      ElMessage.error(
        '<div>' +
        t('sys.api.errMsg901') +
        '</div>' +
        '<div> &nbsp; </div>' +
        '<div>参考 https://doc.iocoder.cn/ 教程</div>' +
        '<div> &nbsp; </div>' +
        '<div>5 分钟搭建本地环境</div>'
      )
      return Promise.reject(new Error(msg))
    } else if (code !== 200) {
      if (msg === '无效的刷新令牌') {
        // hard coding：忽略这个提示，直接登出
        console.log(msg)
      } else {
        ElNotification.error({
          title: msg
        })
      }
      return Promise.reject('error')
    } else {
      return data
    }
  },
  (error: AxiosError) => {
    console.log('[err]' + error) // for debug
    let { message } = error

    if (message === 'Network Error') {
      message = "网络异常，请稍后重试"
    } else if (message.includes('timeout')) {
      message = "服务繁忙，请稍后重试"
    } else if (message.includes('Request failed with status code')) {
      message = "请求失败：" + message.substr(message.length - 3)
    }
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

const refreshToken = async () => {
  return await service({
    url: '/system/auth/refresh-token?refreshToken=' + getRefreshToken(),
    method: 'post'
  })
}
const handleAuthorized = () => {
  if (!isRelogin.show) {
    isRelogin.show = true
    ElMessageBox.confirm("登录超时,请重新登录!", "系统提示", {
      confirmButtonText: "重新登录",
      cancelButtonText: "取消",
      type: 'warning'
    })
      .then(() => {
        const { wsCache } = useCache()
        resetRouter() // 重置静态路由表
        // wsCache.clear()
        removeToken()
        isRelogin.show = false
        window.location.href = '/'
      })
      .catch(() => {
        isRelogin.show = false
      })
  }
  return Promise.reject("登录超时,请重新登录!")
}
export { service }
