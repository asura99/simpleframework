package org.simpleframework.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * ConverterUtil
 *
 * @author chenz
 * @version 1.0
 * @date 2021/9/20
 */
@SuppressWarnings({"unused", "squid:S112", "squid:S3776"})
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConverterUtil {
    /**
     * 返回基础类型的空值
     *
     * @param type 类型
     * @return {@link Object }
     * @author chenz
     * @date 2021/10/14
     */
    public static Object primitiveNull(Class<?> type) {
        if (type == int.class || type == double.class || type == short.class || type == long.class || type == byte.class || type == float.class) {
            return 0;
        } else if (type == boolean.class) {
            return false;
        }
        return null;
    }

    /**
     * String 类型转换成对应的参数类型
     *
     * @param type         类型
     * @param requestValue 请求值
     * @return {@link Object }
     * @author chenz
     * @date 2021/10/14
     */
    public static Object convert(Class<?> type, String requestValue) {
        if (isPrimitive(type)) {
            if (ValidationUtil.isEmpty(requestValue)) {
                return primitiveNull(type);
            }
            if (type.equals(int.class) || type.equals(Integer.class)) {
                return Integer.parseInt(requestValue);
            } else if (type.equals(String.class)) {
                return requestValue;
            } else if (type.equals(Double.class) || type.equals(double.class)) {
                return Double.parseDouble(requestValue);
            } else if (type.equals(Float.class) || type.equals(float.class)) {
                return Float.parseFloat(requestValue);
            } else if (type.equals(Long.class) || type.equals(long.class)) {
                return Long.parseLong(requestValue);
            } else if (type.equals(Boolean.class) || type.equals(boolean.class)) {
                return Boolean.parseBoolean(requestValue);
            } else if (type.equals(Short.class) || type.equals(short.class)) {
                return Short.parseShort(requestValue);
            } else if (type.equals(Byte.class) || type.equals(byte.class)) {
                return Byte.parseByte(requestValue);
            }
            return requestValue;
        } else {
            throw new RuntimeException("count not support non primitive type conversion yet");
        }
    }

    /**
     * 判定是否基本数据类型(包括包装类以及String)
     *
     * @param type 类型
     * @return boolean
     * @author chenz
     * @date 2021/10/14
     */
    private static boolean isPrimitive(Class<?> type) {
        return type == boolean.class
                || type == Boolean.class
                || type == double.class
                || type == Double.class
                || type == float.class
                || type == Float.class
                || type == short.class
                || type == Short.class
                || type == int.class
                || type == Integer.class
                || type == long.class
                || type == Long.class
                || type == String.class
                || type == byte.class
                || type == Byte.class
                || type == char.class
                || type == Character.class;
    }
}
