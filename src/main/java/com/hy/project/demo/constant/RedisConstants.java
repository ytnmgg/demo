package com.hy.project.demo.constant;

/**
 * @author rick.wl
 * @date 2022/11/15
 */
public class RedisConstants {

    public final static String KEY_AUTH_CONF = "SYS_AUTH";

    public final static String KEY_KEYS = "SYS_KEYS";

    public final static String KEY_ROLES = "SYS_ROLES";

    public final static String KEY_LOCK = "SYS_LOCK";

    public static final String KEY_USER_INFO_PREFIX = "USER";

    public static final String KEY_TOKEN_PREFIX = "TOKEN";

    public final static String KEY_LOGIN_HASH = "LOGIN_H";

    public final static String KEY_LOGIN_SET = "LOGIN_S";


    /**
     * 分布式锁持续时间
     */
    public final static long LOCK_DURATION_SECONDS = 3;

    /**
     * 获取分布式锁重试时间
     */
    public final static long LOCK_TRY_MILLISECONDS = 3000;

    public final static Long REDIS_SCRIPT_RETURN_SUCCESS = 1L;

}
