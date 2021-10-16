package org.simpleframework.aop;

import lombok.Getter;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.simpleframework.aop.aspect.AspectInfo;
import org.simpleframework.util.ValidationUtil;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.List;

/**
 * AspectListExecutor
 * <br>
 * 该类是真正利用我们实现好的切面类实现增强逻辑的类，<br>
 * 这个类的增强功能的实现利用的是cglib
 *
 * @author chenz
 * @version 1.0
 * @date 2021/9/20
 */
@SuppressWarnings("squid:S112")
public class AspectListExecutor implements MethodInterceptor {
    /**
     * 被代理的类
     * <br>
     * 存储被增强类所对应的对象，作用是传递给我们自定义的切面类中的方法，<br>
     * 交由自定义的方法去操作
     */
    private final Class<?> targetClass;

    /**
     * 切面类列表
     * <br>
     * 存储对该 targetClass 类进行增强的切面类的列表 <br>
     * 该列表内的数据应按 order 从小到大排序
     */
    @Getter
    private final List<AspectInfo> sortedAspectInfoList;

    public AspectListExecutor(Class<?> targetClass, List<AspectInfo> aspectInfoList) {
        this.targetClass = targetClass;
        // 排序 aspectInfoList 并储存
        this.sortedAspectInfoList = sortedAspectInfoList(aspectInfoList);
    }

    /**
     * 按照 order 的值进行升序排序，确保 order 值小的 aspect 先织入
     *
     * @param aspectInfoList 切面信息列表
     * @return {@link List<AspectInfo> }
     * @author chenz
     * @date 2021/09/21
     */
    private List<AspectInfo> sortedAspectInfoList(List<AspectInfo> aspectInfoList) {
        // 使用方法引用的方式进行排序，效果与使用compare及lambda表达式一致
        aspectInfoList.sort(Comparator.comparingInt(AspectInfo::getOrderIndex));
        return aspectInfoList;
    }

    /**
     * 拦截
     *
     * @param proxy       代理
     * @param method      方法
     * @param args        args
     * @param methodProxy 方法的代理
     * @return {@link Object }
     * @throws Throwable throwable
     * @author chenz
     * @date 2021/10/12
     */
    @Override
    public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        // 存储被代理的方法的返回值
        Object returnValue = null;
        // 粗筛该方法的增强切面
        collectAccurateMatchedAspectList(method);
        // 没有增强切面就直接执行方法并返回结果
        if (ValidationUtil.isEmpty(sortedAspectInfoList)) {
            return methodProxy.invokeSuper(proxy, args);
        }
        // 按照 order 的顺序升序执行完所有的 aspect 的 before 方法
        invokeBeforeAdvices(method, args);
        try {
            // 执行被代理类的方法(用代理对象和被代理方法参数)
            returnValue = methodProxy.invokeSuper(proxy, args);
            // 如果被代理方法正常返回，则按照order的降序执行完所有 aspect 中的 afterReturn 方法
            invokeAfterReturningAdvices(method, args, returnValue);
        } catch (Exception e) {
            // 如果被代理方法抛出异常，则按照 order 的顺序降序执行完所有 Aspect 的 afterThrow 方法
            invokeAfterThrowingAdvices(method, args, e);
        }

        return returnValue;
    }

    /**
     * 精筛切面列表
     *
     * @param method 方法
     * @author chenz
     * @date 2021/09/21
     */
    private void collectAccurateMatchedAspectList(Method method) {
        if (ValidationUtil.isEmpty(sortedAspectInfoList)) {
            return;
        }
        /* 如果该切面类不能增强该方法，就移除
         * 不能使用 sortedAspectInfoList 的 remove，会有并发异常，
         * 需要使用 iterator 的remove方法,
         * removeIf() 是 iterator.remove() 写法的封装
         */
        sortedAspectInfoList.removeIf(aspectInfo -> !aspectInfo.getPointcutLocator().accurateMatches(method));
    }

    /**
     * 按照 order 的顺序 <strong>升序</strong> <br>
     * 执行完所有 aspect 的 <strong>before</strong> 方法
     *
     * @param method 方法
     * @param args   args
     * @author chenz
     * @date 2021/09/21
     */
    private void invokeBeforeAdvices(Method method, Object[] args) throws Throwable {
        for (AspectInfo aspectInfo : sortedAspectInfoList) {
            aspectInfo.getAspectObject().before(targetClass, method, args);
        }
    }

    /**
     * 按照 order 的顺序 <strong>降序</strong> <br>
     * 执行完所有 aspect 中的 <strong>afterReturning</strong> 方法
     *
     * @param method      方法
     * @param args        args
     * @param returnValue 返回值
     * @author chenz
     * @date 2021/09/21
     */
    private void invokeAfterReturningAdvices(Method method, Object[] args, Object returnValue) throws Throwable {
        for (int i = sortedAspectInfoList.size() - 1; i >= 0; i--) {
            sortedAspectInfoList.get(i).getAspectObject().afterReturning(targetClass, method, args, returnValue);
        }
    }

    /**
     * 按照 order 的顺序 <strong>降序</strong> <br>
     * 执行完所有 Aspect 的 <strong>afterThrowing</strong> 方法
     *
     * @param method 方法
     * @param args   args
     * @param e      e
     * @throws Throwable throwable
     * @author chenz
     * @date 2021/09/21
     */
    private void invokeAfterThrowingAdvices(Method method, Object[] args, Exception e) throws Throwable {
        for (int i = sortedAspectInfoList.size() - 1; i >= 0; i--) {
            sortedAspectInfoList.get(i).getAspectObject().afterThrow(targetClass, method, args, e);
        }
    }
}
