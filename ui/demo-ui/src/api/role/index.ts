import { useAxios } from '@/hooks/web/useAxios'
import type { Role } from './types';

const request = useAxios()

// 新增
export const create = (data: Role) => {
    return request.post({ url: '/auth/role/create.json', data })
}

// 查询列表
export const pageList = (params: PageRequest) => {
    return request.get({ url: '/auth/role/list.json', params })
}

// 删除
export const deleteById = (data: string) => {
    return request.post({ url: '/auth/role/deleteById.json', data })
}

// 修改角色权限
export const updateRolePermissions = (data: object) => {
    return request.post({ url: '/auth/role/updateRolePermissions.json', data })
}
