package org.simpleframework.inject;

import lombok.extern.slf4j.Slf4j;
import org.simpleframework.core.BeanContainer;
import org.simpleframework.inject.annotation.Autowired;
import org.simpleframework.util.ClassUtil;
import org.simpleframework.util.ValidationUtil;

import java.lang.reflect.Field;
import java.util.Set;

/**
 * DependencyInjector
 * <br>
 * 依赖注入
 *
 * @author chenz
 * @version 1.0
 * @date 2021/9/20
 */
@Slf4j
@SuppressWarnings("unused")
public class DependencyInjector {

    private final BeanContainer beanContainer;

    public DependencyInjector() {
        beanContainer = BeanContainer.getInstance();
    }

    @SuppressWarnings("all")
    public void doIOC() {
        // 遍历 bean 容器中所有的 class 对象
        Set<Class<?>> classSet = beanContainer.getClasses();
        if (ValidationUtil.isEmpty(classSet)) {
            log.warn("empty classSet in BeanContainer");
            return;
        }
        for (Class<?> clazz : classSet) {
            // 遍历所有成员变量
            Field[] fields = clazz.getDeclaredFields();
            if (ValidationUtil.isEmpty(fields)) {
                continue;
            }
            for (Field field : fields) {
                // 找出 Autowired 标记的变量
                if (field.isAnnotationPresent(Autowired.class)) {
                    Autowired autowired = field.getAnnotation(Autowired.class);
                    String autowiredValue = autowired.value();
                    // 获取成员变量的类型
                    Class<?> fieldClass = field.getType();
                    // 获取成员实例
                    Object fieldInstance = getFieldInstance(fieldClass, autowiredValue);
                    if (null == fieldInstance) {
                        throw new RuntimeException("unable to inject relevant type, target fieldClass is:"
                                + fieldClass.getName() + "autowiredValue:" + autowiredValue);
                    }
                    Object bean = beanContainer.getBean(clazz);
                    ClassUtil.setField(field, bean, fieldInstance);
                }
            }
        }
    }

    /**
     * 获取属性实例
     *
     * @param fieldClass     属性类
     * @param autowiredValue autowired的值
     * @return {@link Object }
     * @author chenz
     * @date 2021/09/21
     */
    private Object getFieldInstance(Class<?> fieldClass, String autowiredValue) {
        Object fieldInstance = beanContainer.getBean(fieldClass);
        if (null != fieldInstance) {
            return fieldInstance;
        }
        Class<?> implementClass = getImplementClass(fieldClass, autowiredValue);
        if (null == implementClass) {
            return null;
        }
        return beanContainer.getBean(implementClass);
    }

    /**
     * 获取实现类
     *
     * @param fieldClass     属性类
     * @param autowiredValue autowired的值
     * @return {@link Class }
     * @author chenz
     * @date 2021/09/21
     */
    @SuppressWarnings("all")
    private Class<?> getImplementClass(Class<?> fieldClass, String autowiredValue) {
        Set<Class<?>> classSet = beanContainer.getClassesBySuper(fieldClass);
        if (ValidationUtil.isEmpty(classSet)) {
            return null;
        }
        if (ValidationUtil.isEmpty(autowiredValue)) {
            if (classSet.size() == 1) {
                return classSet.iterator().next();
            }
            // 多个实现类且未指定就抛出异常
            throw new RuntimeException("multiple implemented classes for " + fieldClass.getName()
                    + ", please set @Autowired's value to pick one !");
        }
        // 寻找匹配的实现类并注入
        for (Class<?> clazz : classSet) {
            if (autowiredValue.equals(clazz.getSimpleName())) {
                return clazz;
            }
        }
        // 没找到匹配的实现类
        return null;
    }
}
