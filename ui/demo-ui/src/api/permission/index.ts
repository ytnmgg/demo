import { useAxios } from '@/hooks/web/useAxios'
import type { Permission } from './types';

const request = useAxios()

// 新增
export const create = (data: Permission) => {
    return request.post({ url: '/auth/permission/create.json', data })
}

// 查询列表
export const pageList = (params: PageRequest) => {
    return request.get({ url: '/auth/permission/list.json', params })
}

// 删除
export const deleteById = (data: string) => {
    return request.post({ url: '/auth/permission/deleteById.json', data })
}


