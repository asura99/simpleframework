package org.simpleframework.mvc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * RequestParam
 *
 * @author chenz
 * @version 1.0
 * @date 2021/9/21
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestParam {

    /**
     * 参数名称
     */
    String value() default "";

    /**
     * 该参数是否必要
     */
    boolean required() default true;
}
