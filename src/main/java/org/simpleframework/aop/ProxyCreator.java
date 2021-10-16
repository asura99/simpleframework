package org.simpleframework.aop;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

/**
 * ProxyCreator
 * <br>
 * 根据目标对象和我们的增强逻辑，组合出最终的代理类对象
 *
 * @author chenz
 * @version 1.0
 * @date 2021/9/20
 */
@SuppressWarnings("unused")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProxyCreator {

    /**
     * 创建动态代理对象并返回
     *
     * @param targetClass       被代理的class对象
     * @param methodInterceptor 方法拦截器
     * @return {@link Object }
     * @author chenz
     * @date 2021/09/21
     */
    public static Object createProxy(Class<?> targetClass, MethodInterceptor methodInterceptor) {
        return Enhancer.create(targetClass, methodInterceptor);
    }
}
