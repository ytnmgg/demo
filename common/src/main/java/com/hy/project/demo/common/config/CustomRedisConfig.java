package com.hy.project.demo.common.config;

import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author rick.wl
 * @date 2021/11/04
 */
@Configuration
public class CustomRedisConfig {
    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        //设置序列化规则，防止使用默认 JdkSerializationRedisSerializer，会在key和value前面自动添加乱码，不利于在服务器用redis-cli管理
        // key的序列化采用StringRedisSerializer
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        // value值的序列化采用fastJsonRedisSerializer
        template.setValueSerializer(new FastJsonRedisSerializer<>(Object.class));
        template.setHashValueSerializer(new FastJsonRedisSerializer<>(Object.class));

        return template;
    }

}
