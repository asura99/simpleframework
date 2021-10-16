package org.simpleframework.mvc.type;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * ControllerMethod
 * <br>
 * 存储Controller类和其具有的方法实例，方法参数的映射
 *
 * @author chenz
 * @version 1.0
 * @date 2021/9/21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ControllerMethod {

    /**
     * controller 对应的 class 对象
     */
    private Class<?> controllerClass;

    /**
     * 执行的 controller 方法实例
     */
    private Method invokeMethod;

    /**
     * 方法参数名称及对应的参数类型
     */
    private Map<String, Class<?>> methodParameters;
}
