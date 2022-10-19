const config: {
    base_url: string
    result_code: number | string
    default_headers: AxiosHeaders
    request_timeout: number
  } = {
    /**
     * api请求基础路径
     */
    base_url: import.meta.env.VITE_BASE_URL + import.meta.env.VITE_API_URL,
    /**
     * 接口成功返回状态码
     */
    result_code: 200,
  
    /**
     * 接口请求超时时间
     */
    request_timeout: 30000,
  
    /**
     * 默认接口请求类型
     * 可选值：application/x-www-form-urlencoded multipart/form-data
     */
    default_headers: 'application/json'
  }

  const errorCode = {
    '401': '认证失败，无法访问系统资源',
    '403': '当前操作没有权限',
    '404': '访问资源不存在',
    default: '系统未知错误，请反馈给管理员'
  }
  
  export { config, errorCode }
  