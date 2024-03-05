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
    roles?: Array<object>
}


export type UserRegisterVO = {
    username: string
    password: string
    roleIds: Array<string>
}

export type LoginInfo = {
    token?: string
    userId?: string
    userName?: string
    loginIp?: string
    loginTime?: string
    userAgent?: string
}
