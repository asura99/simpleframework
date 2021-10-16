package org.simpleframework.aop;

import lombok.extern.slf4j.Slf4j;
import org.simpleframework.aop.annotation.Aspect;
import org.simpleframework.aop.annotation.Order;
import org.simpleframework.aop.aspect.DefaultAspect;

import java.lang.reflect.Method;

/**
 * TestAspect1
 *
 * @author chenz
 * @version 1.0
 * @date 2021/9/21
 */
@Slf4j
@Order(1)
@SuppressWarnings({"unused", "RedundantThrows"})
@Aspect(pointcut = "execution(* org.simpleframework.aop.*.*(..))")
public class TestAspect extends DefaultAspect {

    @Override
    public void before(Class<?> targetClass, Method method, Object[] args) throws Throwable {
        log.info("前置通知执行了...");
    }

    @Override
    public Object afterReturning(Class<?> targetClass, Method method, Object[] args, Object returnValue) throws Throwable {
        log.info("后置通知执行了...");
        int result = Integer.parseInt(returnValue.toString());
        // 并不能改变返回值
        return result + 20;
    }

    @Override
    public void afterThrow(Class<?> targetClass, Method method, Object[] args, Throwable throwable) throws Throwable {
        log.info("异常通知执行了...");
    }
}
