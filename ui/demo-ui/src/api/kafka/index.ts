import { useAxios } from '@/hooks/web/useAxios'
import {KafkaGTopicGetRequest, KafkaTopicCreateRequest} from "@/api/kafka/types";

const request = useAxios()

export const getCluster = (params:void) => {
    return request.get({ url: '/kafka/get_cluster.json', params })
}

export const listTopic = (params:PageRequest) => {
    return request.get({ url: '/kafka/list_topic.json', params })
}

export const createTopic = (data:KafkaTopicCreateRequest) => {
    return request.post({ url: '/kafka/create_topic.json', data })
}

export const getTopic = (params:KafkaGTopicGetRequest) => {
    return request.get({ url: '/kafka/get_topic.json', params })
}

export const deleteTopic = (data:KafkaGTopicGetRequest) => {
    return request.post({ url: '/kafka/delete_topic.json', data })
}
