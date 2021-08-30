package com.hy.project.demo.exception;

/**
 * @author rick.wl
 * @date 2021/08/28
 */
public class DemoException extends RuntimeException {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 7787538555758350329L;

    /**
     * 错误码
     */
    private DemoExceptionEnum code;

    /**
     * 错误详情
     */
    private Object errorsDetail;

    /**
     * 构造器
     *
     * @param code 错误码
     */
    public DemoException(final DemoExceptionEnum code) {
        super(code.getDescription());
        this.code = code;
    }

    /**
     * 创建一个<code>PcException</code>
     *
     * @param code         错误码
     * @param errorMessage 错误描述
     */
    public DemoException(final DemoExceptionEnum code, final String errorMessage) {
        super(errorMessage);
        this.code = code;
    }

    /**
     * 创建一个<code>PcException</code>
     *
     * @param code         错误码
     * @param errorMessage 错误描述
     * @param cause        异常
     */
    public DemoException(final DemoExceptionEnum code, final String errorMessage, final Throwable cause) {
        super(errorMessage, cause);
        this.code = code;
    }

    /**
     * 创建一个<code>PcException</code>
     *
     * @param code       错误码
     * @param cause      异常
     * @param formatter  错误异常格式化消息
     * @param formatArgs 错误信息
     */
    public DemoException(final DemoExceptionEnum code, final Throwable cause, final String formatter,
        final Object... formatArgs) {
        super(String.format(formatter, formatArgs), cause);
        this.code = code;
    }

    /**
     * 创建一个<code>PcException</code>
     *
     * @param code       错误码
     * @param formatter  错误异常格式化消息
     * @param formatArgs 错误信息
     */
    public DemoException(final DemoExceptionEnum code, final String formatter, final Object... formatArgs) {
        super(String.format(formatter, formatArgs));
        this.code = code;
    }

    public DemoExceptionEnum getCode() {
        return code;
    }

    public void setCode(DemoExceptionEnum code) {
        this.code = code;
    }

    public Object getErrorsDetail() {
        return errorsDetail;
    }

    public void setErrorsDetail(Object errorsDetail) {
        this.errorsDetail = errorsDetail;
    }
}