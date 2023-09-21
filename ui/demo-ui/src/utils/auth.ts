import { useCache } from '@/hooks/web/useCache'
import { TokenType } from '@/api/login/types'
import { useCookies } from '@vueuse/integrations/useCookies'

const { wsCache } = useCache()
const AccessTokenKey = 'ACCESS_TOKEN'
const RefreshTokenKey = 'REFRESH_TOKEN'
const cookies = useCookies([AccessTokenKey])
const grafanaKey = 'grafana_session'

// 获取token
export const getAccessToken = () => {
  return wsCache.get(AccessTokenKey)
}

// 设置token
export const setToken = (token: TokenType) => {
  wsCache.set(RefreshTokenKey, token.refreshToken, { exp: token.expiresTime })
  wsCache.set(AccessTokenKey, token.accessToken)
  cookies.set(AccessTokenKey, token.accessToken)
  cookies.set(AccessTokenKey, token.accessToken, {domain: "rick.com"})
}

// 删除token
export const removeToken = () => {
  wsCache.delete(RefreshTokenKey)
  wsCache.delete(AccessTokenKey)
  cookies.remove(AccessTokenKey)
  cookies.remove(AccessTokenKey, {domain: "rick.com"})
  cookies.remove(grafanaKey)
}

// 刷新token
export const getRefreshToken = () => {
  return wsCache.get(RefreshTokenKey)
}

const UsernameKey = 'USERNAME'
const PasswordKey = 'PASSWORD'
const RememberMeKey = 'REMEMBER_ME'

export const getUsername = () => {
  return wsCache.get(UsernameKey)
}

export const setUsername = (username: string) => {
  wsCache.set(UsernameKey, username)
}

export const removeUsername = () => {
  wsCache.delete(UsernameKey)
}

export const getPassword = () => {
  return wsCache.get(PasswordKey)
}

export const setPassword = (password: string) => {
  wsCache.set(PasswordKey, password)
}

export const removePassword = () => {
  wsCache.delete(PasswordKey)
}

export const getRememberMe = () => {
  return wsCache.get(RememberMeKey) === 'true'
}

export const setRememberMe = (rememberMe: string) => {
  wsCache.set(RememberMeKey, rememberMe)
}

export const removeRememberMe = () => {
  wsCache.delete(RememberMeKey)
}