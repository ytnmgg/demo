package com.hy.project.demo.web.controller.monitor;

import com.hy.project.demo.common.service.redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author rick.wl
 * @date 2021/11/30
 */
@RestController
@RequestMapping("/redis")
public class RedisController {
    private final static Logger LOGGER = LoggerFactory.getLogger(RedisController.class);

    @Autowired
    RedisService redisService;

    //@GetMapping("/list.json")
    //public @ResponseBody
    //DemoResult<Map<String, Object>> list(String prefix) {
    //    return redisService.list(prefix);
    //}
    //
    //@PostMapping("/remove.json")
    //public @ResponseBody
    //DemoResult<Void> remove(@RequestBody RedisDeleteRequest request) {
    //    redisService.remove(request.getKey());
    //    return DemoResult.buildSuccessResult(null);
    //}

}
