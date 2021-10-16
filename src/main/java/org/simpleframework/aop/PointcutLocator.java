package org.simpleframework.aop;

import org.aspectj.weaver.tools.PointcutExpression;
import org.aspectj.weaver.tools.PointcutParser;
import org.aspectj.weaver.tools.ShadowMatch;

import java.lang.reflect.Method;

/**
 * PointcutLocator
 * <br>
 * 解析 <code>@Aspect</code> 注解中的切面表达式，并且定位需要被增强的目标
 *
 * @author chenz
 * @version 1.0
 * @date 2021/9/20
 */
@SuppressWarnings("unused")
public class PointcutLocator {

    /**
     * 切入点表达式
     */
    private final PointcutExpression pointcutExpression;

    public PointcutLocator(String expression) {
        /*
         * Pointcut解析器,直接给他赋值上AspectJ的所有表达式,以便支持对众多表达式的解析
         */
        PointcutParser parser =
                PointcutParser.getPointcutParserSupportingSpecifiedPrimitivesAndUsingContextClassloaderForResolution(PointcutParser.getAllSupportedPointcutPrimitives());
        this.pointcutExpression = parser.parsePointcutExpression(expression);
    }

    /**
     * 判断传入的 class 对象是否是 Aspect 的目标代理类 <br>
     * 即匹配 Pointcut 表达式（粗筛）
     *
     * @param targetClass 目标类
     * @return boolean
     * @author chenz
     * @date 2021/09/21
     */
    public boolean roughMatches(Class<?> targetClass) {
        // couldMatchJoinPointsInType 比较坑，只能效验 within
        // 不能效验（execution,call,get,set），面对无法效验的表达式，直接返回true
        return pointcutExpression.couldMatchJoinPointsInType(targetClass);
    }

    /**
     * 判断传入的 Method 对象是否是 Aspect 的目标 dialing 方法 <br>
     * 即匹配 Pointcut 表达式（精筛）
     *
     * @param method 方法
     * @return boolean
     * @author chenz
     * @date 2021/09/21
     */
    public boolean accurateMatches(Method method) {
        ShadowMatch shadowMatch = pointcutExpression.matchesMethodExecution(method);
        return shadowMatch.alwaysMatches();
    }
}
