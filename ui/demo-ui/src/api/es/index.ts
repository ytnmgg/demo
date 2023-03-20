import { useAxios } from '@/hooks/web/useAxios'
import type { SearchRequest } from './types';

const request = useAxios()

export const search = (params: SearchRequest) => {
    return request.get({ url: '/es/search.json', params })
}