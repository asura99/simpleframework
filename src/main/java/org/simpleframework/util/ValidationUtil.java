package org.simpleframework.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Map;

/**
 * ValidationUtil
 * <br>
 * 校验工具类
 *
 * @author chenz
 * @version 1.0
 * @date 2021/9/20
 */
@SuppressWarnings("unused")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ValidationUtil {

    /**
     * 判断字符串是否为空
     *
     * @param obj obj
     * @return boolean
     * @author chenz
     * @date 2021/09/20
     */
    public static boolean isEmpty(String obj) {
        return obj == null || "".equals(obj);
    }

    /**
     * 判断数组是否为空
     *
     * @param obj obj
     * @return boolean
     * @author chenz
     * @date 2021/09/20
     */
    public static boolean isEmpty(Object[] obj) {
        return obj == null || obj.length == 0;
    }

    /**
     * 判断集合是否为空
     *
     * @param obj obj
     * @return boolean
     * @author chenz
     * @date 2021/09/20
     */
    public static boolean isEmpty(Collection<?> obj) {
        return obj == null || obj.isEmpty();
    }

    /**
     * 判断Map是否为空
     *
     * @param obj obj
     * @return boolean
     * @author chenz
     * @date 2021/09/20
     */
    public static boolean isEmpty(Map<?, ?> obj) {
        return obj == null || obj.isEmpty();
    }
}
