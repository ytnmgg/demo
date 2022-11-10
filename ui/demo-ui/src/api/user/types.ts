export type SysUser = {
    userId: string
    userType?: string
    userName?: string
    nickName?: string
    email?: string
    phonenumber?: string
    sex?: string
    avatar?: string
    status?: string
}


export type UserRegisterVO = {
    username: string
    password: string
  }

export type PageRequest = {
    pageSize: number
    pageIndex: number
}
