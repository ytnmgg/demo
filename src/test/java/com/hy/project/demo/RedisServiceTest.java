package com.hy.project.demo;

import java.util.concurrent.TimeUnit;

import com.hy.project.demo.service.common.RedisService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author rick.wl
 * @date 2021/08/30
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DemoApplication.class)
@TestPropertySource("classpath:test.properties")
public class RedisServiceTest {

    @Autowired
    RedisService redisService;

    @Test
    public void redisTest() {

        String key = "test_key";
        System.out.println(redisService.exists(key));

        redisService.set(key, "haha", 20L, TimeUnit.SECONDS);

        System.out.println(redisService.get(key));
        redisService.remove(key);
        System.out.println(redisService.exists(key));


    }
}
