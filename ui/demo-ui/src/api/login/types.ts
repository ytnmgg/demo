export type TokenType = {
  id?: number // 编号
  accessToken: string // 访问令牌
  userId?: number // 用户编号
  expiresTime?: number //过期时间
}

export type UserLoginVO = {
  username: string
  password: string
}