package com.hy.project.demo.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author rick.wl
 * @date 2022/09/20
 */
public class CommonUtil {
    private final static Logger LOGGER = LoggerFactory.getLogger(CommonUtil.class);

    public static void sleepQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            LOGGER.error("InterruptedException", e);
        }
    }
}
