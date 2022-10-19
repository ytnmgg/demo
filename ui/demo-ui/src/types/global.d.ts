declare type AxiosHeaders =
  | 'application/json'
  | 'application/x-www-form-urlencoded'
  | 'multipart/form-data'

declare type AxiosMethod = 'GET' | 'POST' | 'DELETE' | 'PUT'

declare type AxiosResponseType = 'arraybuffer' | 'blob' | 'document' | 'json' | 'text' | 'stream'

declare interface AxiosConfig {
  params?: any
  data?: any
  url?: string
  method?: AxiosMethod
  headersType?: string
  responseType?: AxiosResponseType
}

declare type ElRef<T extends HTMLElement = HTMLDivElement> = Nullable<T>

declare interface IconTypes {
  size?: number
  color?: string
  icon: string
}

declare type Recordable<T = any, K = string> = Record<K extends null | undefined ? string : K, T>
