import { useAxios } from '@/hooks/web/useAxios'
import type { JobInfo } from './types';

const request = useAxios()

// 查询任务列表
export const listTask = () => {
    return request.get({ url: '/task/list.json' })
}
