package com.hy.project.demo.common.util;

import java.util.Collection;
import java.util.Objects;

import com.hy.project.demo.common.exception.DemoException;
import com.hy.project.demo.common.exception.DemoExceptionEnum;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author rick.wl
 * @date 2021/08/28
 */
public class AssertUtil {

    public static void notBlank(final String str, final DemoExceptionEnum errEnum,
        final String formatter, final Object... formatArgs) {

        if (StringUtils.isBlank(str)) {
            throw new DemoException(errEnum, formatter, formatArgs);
        }
    }

    public static void notEmpty(final Collection<?> collection, final DemoExceptionEnum errEnum,
        final String formatter, final Object... formatArgs) {

        if (CollectionUtils.isEmpty(collection)) {
            throw new DemoException(errEnum, formatter, formatArgs);
        }
    }

    public static void isEmpty(final Collection<?> collection, final DemoExceptionEnum errEnum,
        final String formatter, final Object... formatArgs) {

        if (CollectionUtils.isNotEmpty(collection)) {
            throw new DemoException(errEnum, formatter, formatArgs);
        }
    }

    public static void notNull(final Object object, final DemoExceptionEnum errEnum,
        final String formatter, final Object... formatArgs) {

        if (null == object) {
            throw new DemoException(errEnum, formatter, formatArgs);
        }
    }

    public static void isNull(final Object object, final DemoExceptionEnum errEnum,
        final String formatter, final Object... formatArgs) {

        if (null != object) {
            throw new DemoException(errEnum, formatter, formatArgs);
        }
    }

    public static void isTrue(final boolean conf, final DemoExceptionEnum errEnum,
        final String formatter, final Object... formatArgs) {
        if (!conf) {
            throw new DemoException(errEnum, formatter, formatArgs);
        }
    }

    public static void isFalse(final boolean conf, final DemoExceptionEnum errEnum,
        final String formatter, final Object... formatArgs) {
        if (conf) {
            throw new DemoException(errEnum, formatter, formatArgs);
        }
    }

    public static void equals(final Integer first, final Integer second, final DemoExceptionEnum errEnum,
        final String formatter, final Object... formatArgs) {

        isTrue(Objects.equals(first, second), errEnum, formatter, formatArgs);
    }
}
