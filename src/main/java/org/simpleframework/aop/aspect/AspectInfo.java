package org.simpleframework.aop.aspect;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.simpleframework.aop.PointcutLocator;

/**
 * AspectInfo
 *
 * @author chenz
 * @version 1.0
 * @date 2021/9/21
 */
@Getter
@AllArgsConstructor
public class AspectInfo {

    /**
     * 切面类的优先指数
     */
    private int orderIndex;

    /**
     * 真正做增强的切面类对象的引用
     */
    private DefaultAspect aspectObject;

    /**
     * 切点定位器
     */
    private PointcutLocator pointcutLocator;
}
