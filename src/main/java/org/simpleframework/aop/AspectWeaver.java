package org.simpleframework.aop;

import org.simpleframework.aop.annotation.Aspect;
import org.simpleframework.aop.annotation.Order;
import org.simpleframework.aop.aspect.AspectInfo;
import org.simpleframework.aop.aspect.DefaultAspect;
import org.simpleframework.core.BeanContainer;
import org.simpleframework.util.ValidationUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * AspectWeaver
 * <br>
 * 从IOC容器中取出我们想要实现增强逻辑的类，然后对其进行增强操作之后，再将其放回到IOC容器中
 *
 * @author chenz
 * @version 1.0
 * @date 2021/9/20
 */
@SuppressWarnings({"squid:S112", "unused"})
public class AspectWeaver {

    private final BeanContainer beanContainer;

    public AspectWeaver() {
        this.beanContainer = BeanContainer.getInstance();
    }

    public void doAOP() {
        // 获取所有的切面类
        Set<Class<?>> aspectSet = beanContainer.getClassesByAnnotation(Aspect.class);
        if (ValidationUtil.isEmpty(aspectSet)) {
            return;
        }
        // 拼接 AspectInfoList
        List<AspectInfo> aspectInfoList = packAspectInfoList(aspectSet);
        // 遍历容器中的类
        for (Class<?> targetClass : beanContainer.getClasses()) {
            // 排除自身
            if (targetClass.isAnnotationPresent(Aspect.class)) {
                continue;
            }
            // 粗筛符合条件的 Aspect
            List<AspectInfo> roughMatchedAspectList
                    = collectRoughMatchedAspectListForSpecificClass(aspectInfoList, targetClass);
            // 尝试织入
            wrapIfNecessary(roughMatchedAspectList, targetClass);
        }


    }

    /**
     * 包装切面信息列表
     *
     * @param aspectSet 切面Set
     * @return {@link List<AspectInfo> }
     * @author chenz
     * @date 2021/09/21
     */
    private List<AspectInfo> packAspectInfoList(Set<Class<?>> aspectSet) {
        List<AspectInfo> aspectInfoList = new ArrayList<>();
        for (Class<?> aspectClass : aspectSet) {
            // 检查是否遵规范
            if (!verifyAspect(aspectClass)) {
                throw new RuntimeException("@Aspect and @Order must be added to the Aspect class, and Aspect class " +
                        "must " + "extend from DefaultAspect");
            }
            Aspect aspectTag = aspectClass.getAnnotation(Aspect.class);
            Order orderTag = aspectClass.getAnnotation(Order.class);
            DefaultAspect defaultAspect = (DefaultAspect) beanContainer.getBean(aspectClass);
            // 初始化表达式定位器
            PointcutLocator pointcutLocator = new PointcutLocator(aspectTag.pointcut());
            AspectInfo aspectInfo = new AspectInfo(orderTag.value(), defaultAspect, pointcutLocator);
            aspectInfoList.add(aspectInfo);
        }
        return aspectInfoList;
    }

    /**
     * 通过具体的类粗筛切面列表
     *
     * @param aspectInfoList 切面信息列表
     * @param targetClass    目标类
     * @return {@link List<AspectInfo> }
     * @author chenz
     * @date 2021/09/21
     */
    private List<AspectInfo> collectRoughMatchedAspectListForSpecificClass(List<AspectInfo> aspectInfoList,
            Class<?> targetClass) {
        List<AspectInfo> roughMatchedAspectList = new ArrayList<>();
        for (AspectInfo aspectInfo : aspectInfoList) {
            // 粗筛
            if (aspectInfo.getPointcutLocator().roughMatches(targetClass)) {
                roughMatchedAspectList.add(aspectInfo);
            }
        }
        return roughMatchedAspectList;
    }

    /**
     * 织入切面
     *
     * @param roughMatchedAspectList 粗筛切面列表
     * @param targetClass            目标类
     * @author chenz
     * @date 2021/09/21
     */
    private void wrapIfNecessary(List<AspectInfo> roughMatchedAspectList, Class<?> targetClass) {
        if (ValidationUtil.isEmpty(roughMatchedAspectList)) {
            return;
        }
        AspectListExecutor aspectListExecutor = new AspectListExecutor(targetClass, roughMatchedAspectList);
        Object proxy = ProxyCreator.createProxy(targetClass, aspectListExecutor);
        beanContainer.addBean(targetClass, proxy);
    }

    /**
     * 验证切面
     *
     * @param aspectClass 切面类
     * @return boolean
     * @author chenz
     * @date 2021/09/21
     */
    private boolean verifyAspect(Class<?> aspectClass) {
        return aspectClass.isAnnotationPresent(Aspect.class)
                && aspectClass.isAnnotationPresent(Order.class)
                && DefaultAspect.class.isAssignableFrom(aspectClass);
    }
}
