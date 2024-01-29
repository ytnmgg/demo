
export type KafkaTopicCreateRequest = {
    topicName: string
    partitions: number
    replicationFactor: number
}

export type KafkaGTopicGetRequest = {
    topicName: string
}