package org.simpleframework.aop.aspect;

import java.lang.reflect.Method;

/**
 * DefaultAspect
 * <br>
 * 定义框架支持的advice
 *
 * @author chenz
 * @version 1.0
 * @date 2021/9/21
 */
@SuppressWarnings({"unused", "squid:S112", "squid:S1610"})
public abstract class DefaultAspect {
    /**
     * 事前拦截
     *
     * @param targetClass 被代理的目标类
     * @param method      被代理的目标方法
     * @param args        被代理目标方法对应的参数列表
     * @author chenz
     * @date 2021/09/21
     */
    public void before(Class<?> targetClass, Method method, Object[] args) throws Throwable {
    }

    /**
     * 事后拦截
     *
     * @param targetClass 被代理的目标类
     * @param method      被代理的目标方法
     * @param args        被代理的目标方法对应的参数列表
     * @param returnValue 被代理的目标方法执行后返回值
     * @return {@link Object }
     * @throws Throwable throwable
     * @author chenz
     * @date 2021/09/21
     */
    @SuppressWarnings("UnusedReturnValue")
    public Object afterReturning(Class<?> targetClass, Method method, Object[] args, Object returnValue) throws Throwable {
        return returnValue;
    }

    /**
     * 抛出异常后拦截
     *
     * @param targetClass 被代理的目标类
     * @param method      被代理的目标方法
     * @param args        被代理的目标方法对应的参数列表
     * @param throwable   被代理的目标方法抛出的异常
     * @throws Throwable throwable
     * @author chenz
     * @date 2021/09/21
     */
    public void afterThrow(Class<?> targetClass, Method method, Object[] args, Throwable throwable) throws Throwable {
    }
}
