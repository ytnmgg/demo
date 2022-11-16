import { useAxios } from '@/hooks/web/useAxios'
import type { UserRegisterVO, SysUser } from './types';

const request = useAxios()

// 查询当前登录用户（ME）
export const getMe = () => {
    return request.get({ url: '/user/me.json' })
}

// 注册新用户
export const register = (data: UserRegisterVO) => {
    return request.post({ url: '/user/register.json', data })
}

// 查询用户管理列表
export const getUserPageApi = (params: PageRequest) => {
    return request.get({ url: '/user/list.json', params })
}

// 查询用户详情
export const getById = (userId: string) => {
    return request.get({ url: '/user/getById.json?userId=' + userId })
}

// 修改用户
export const updateUser = (data: SysUser) => {
    return request.post({ url: '/user/update.json', data })
}

// 删除用户
export const deleteUser = (data: string) => {
  return request.post({ url: '/user/deleteById.json', data })
}

// 用户密码重置
export const resetUserPwd = (data: SysUser) => {
  return request.post({ url: '/user/updatePwd.json', data })
}

// 修改用户角色
export const updateUserRoles = (data: object) => {
    return request.post({ url: '/user/updateUserRoles.json', data })
}

// // 新增用户
// export const createUserApi = (data: UserVO) => {
//   return request.post({ url: '/system/user/create', data })
// }





// // 导出用户
// export const exportUserApi = (params) => {
//   return request.download({ url: '/system/user/export', params })
// }

// // 下载用户导入模板
// export const importUserTemplateApi = () => {
//   return request.download({ url: '/system/user/get-import-template' })
// }

// // 用户密码重置
// export const resetUserPwdApi = (id: number, password: string) => {
//   const data = {
//     id,
//     password
//   }
//   return request.put({ url: '/system/user/update-password', data: data })
// }

// // 用户状态修改
// export const updateUserStatusApi = (id: number, status: number) => {
//   const data = {
//     id,
//     status
//   }
//   return request.put({ url: '/system/user/update-status', data: data })
// }

// // 获取用户精简信息列表
// export const getListSimpleUsersApi = () => {
//   return request.get({ url: '/system/user/list-all-simple' })
// }
