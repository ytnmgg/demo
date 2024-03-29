package com.hy.project.demo.common.exception;

/**
 * @author rick.wl
 * @date 2021/08/28
 */
public enum DemoExceptionEnum {

    //========================================================================//
    //                        系统错误码[000-299]                              //
    //========================================================================//
    /** 未知异常 */
    UNKNOWN_EXCEPTION("001", "服务器异常"),

    /** 数据库异常 */
    DB_EXCEPTION("002", "数据库异常"),

    /** JSON转换异常 */
    JSON_EXCEPTION("003", "JSON转换异常"),

    /** 非预期执行 */
    UNEXPECTED("004", "非预期执行"),

    /** 配置异常 */
    CONFIGURATION_EXCEPTION("005", "配置异常"),

    /** 参数错误异常 */
    INVALID_PARAM_EXCEPTION("006", "参数错误异常"),

    /** 日期数据格式异常 */
    DATE_FORMAT_EXCEPTION("007", "日期数据格式异常"),

    /** 权限异常 */
    PERMISSION_DENIED_EXCEPTION("008", "权限异常"),

    /** 文件读写异常 */
    FILE_EXCEPTION("009", "文件读写异常"),

    /** 幂等异常 */
    IDEMPOTENT_EXCEPTION("010", "幂等异常"),

    /** 数据库重复插入异常 */
    DUPLICATE_KEY_EXCEPTION("011", "数据库重复插入异常"),

    /** 数据库行锁异常 */
    ROW_LOCK_EXCEPTION("012", "数据库行锁异常"),

    /** 远程调用业务异常 */
    REMOTE_BIZ_EXCEPTION("013", "远程调用业务异常"),

    /** 远程调用未知异常 */
    REMOTE_UNKNOWN_EXCEPTION("014", "远程调用未知异常"),

    /** HTTP调用异常 */
    HTTP_EXCEPTION("015", "HTTP调用异常"),

    /** redis调用异常 */
    REDIS_EXCEPTION("016", "redis调用异常"),

    /** redis锁异常 */
    REDIS_LOCK_EXCEPTION("017", "redis锁异常"),

    //========================================================================//
    //                        入参错误码[300-599]                              //
    //========================================================================//
    /** 用户名长度不符合要求 */
    USER_NAME_LENGTH_INVALID("400-301", "用户名长度不符合要求"),

    /** 用户密码长度不符合要求 */
    USER_PASSWORD_LENGTH_INVALID("400-302", "用户密码长度不符合要求"),

    /** 登录失败 */
    LOGIN_FAIL("400-303", "Login failed, check the username or password."),

    //========================================================================//
    //                        权限类错误码[600-799]                              //
    //========================================================================//
    /** 登录状态失效 */
    AUTH_STATUS_INVALID("401-600", "Need login."),

    /** 无权限（通用） */
    ACCESS_DENIED("403-601", "Access denied."),

    ;

    /** 枚举编码 */
    private final String         code;

    /** 描述说明 */
    private final String         description;

    DemoExceptionEnum(final String code, final String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}