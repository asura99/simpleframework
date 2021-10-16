package org.simpleframework.core;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.simpleframework.aop.annotation.Aspect;
import org.simpleframework.core.annotation.*;
import org.simpleframework.util.ClassUtil;
import org.simpleframework.util.ValidationUtil;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * BeanContainer
 * <br>
 * Bean 容器
 *
 * @author chenz
 * @version 1.0
 * @date 2021/9/20
 */
@Slf4j
@SuppressWarnings("unused")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BeanContainer {

    /**
     * 存放所有被配置标记的目标对象的Map
     */
    private final Map<Class<?>, Object> beanMap = new ConcurrentHashMap<>();

    /**
     * 加载Bean的注解列表
     */
    private static final List<Class<? extends Annotation>> BEAN_ANNOTATIONS
            = Arrays.asList(Component.class, Controller.class, RestController.class, Service.class, Repository.class, Aspect.class);

    /**
     * 容器是否已经被加载
     */
    private boolean loaded = false;

    /**
     * 是否被加载过
     *
     * @return boolean
     * @author chenz
     * @date 2021/09/20
     */
    public boolean isLoaded() {
        return loaded;
    }

    /**
     * 获取bean实例的数量
     *
     * @return int
     * @author chenz
     * @date 2021/09/20
     */
    public int size() {
        return beanMap.size();
    }

    /**
     * 使用内部枚举类实现单例模式创建bean容器对象
     *
     * @author chen
     * @date 2021/09/20
     */
    private enum ContainerHolder {
        HOLDER;
        private final BeanContainer instance;

        ContainerHolder() {
            instance = new BeanContainer();
        }
    }

    /**
     * 获得bean容器实例
     *
     * @return {@link BeanContainer }
     * @author chenz
     * @date 2021/09/20
     */
    public static BeanContainer getInstance() {
        return ContainerHolder.HOLDER.instance;
    }

    /**
     * 扫描并加载所有的bean
     *
     * @param packageName 包名
     * @author chenz
     * @date 2021/09/20
     */
    public synchronized void loadBeans(String packageName) {
        // 检查是否已经加载完成
        if (isLoaded()) {
            log.warn("bean has been loaded!");
            return;
        }
        Set<Class<?>> classSet = ClassUtil.extractPackageClass(packageName);
        // 类是否为空
        if (ValidationUtil.isEmpty(classSet)) {
            log.warn("extract nothing from packageName {}", packageName);
            return;
        }
        for (Class<?> clazz : classSet) {
            for (Class<? extends Annotation> annotation : BEAN_ANNOTATIONS) {
                // 检查是否使用了定义的注解
                if (clazz.isAnnotationPresent(annotation)) {
                    // 将 class 作为 key ，实例作为 value ，放入 beanMap 中
                    beanMap.put(clazz, ClassUtil.newInstance(clazz));
                }
            }
        }
        loaded = true;
    }


    /**
     * 添加 bean 对象
     *
     * @param clazz clazz
     * @param bean  bean 对象
     * @return {@link Object }
     * @author chenz
     * @date 2021/09/21
     */
    @SuppressWarnings("UnusedReturnValue")
    public Object addBean(Class<?> clazz, Object bean) {
        return beanMap.put(clazz, bean);
    }

    /**
     * 删除 bean 对象
     *
     * @param clazz clazz
     * @return {@link Object }
     * @author chenz
     * @date 2021/09/21
     */
    public Object remove(Class<?> clazz) {
        return beanMap.remove(clazz);
    }

    /**
     * 获取bean对象
     *
     * @param clazz clazz
     * @return {@link Object }
     * @author chenz
     * @date 2021/09/21
     */
    public Object getBean(Class<?> clazz) {
        return beanMap.get(clazz);
    }

    /**
     * 获取容器中所有的class对象
     *
     * @return {@link Set<Class> }
     * @author chenz
     * @date 2021/09/21
     */
    public Set<Class<?>> getClasses() {
        return beanMap.keySet();
    }

    /**
     * 获取所有的bean对象
     *
     * @return {@link Set<Object> }
     * @author chenz
     * @date 2021/09/21
     */
    public Set<Object> getBeans() {
        return new HashSet<>(beanMap.values());
    }

    /**
     * 根据注解筛选出bean的class集合
     *
     * @param annotation 注释
     * @return {@link Set<Class> }
     * @author chenz
     * @date 2021/09/21
     */
    public Set<Class<?>> getClassesByAnnotation(Class<? extends Annotation> annotation) {
        // 获取所有class对象
        Set<Class<?>> keySet = getClasses();
        if (ValidationUtil.isEmpty(keySet)) {
            log.warn("nothing in beanMap");
            return Collections.emptySet();
        }
        // 通过注解筛选class对象
        Set<Class<?>> classSet = new HashSet<>();
        for (Class<?> clazz : keySet) {
            if (clazz.isAnnotationPresent(annotation)) {
                classSet.add(clazz);
            }
        }
        return classSet;
    }

    /**
     * 通过接口或者父类获取实现类或者子类的class集合，不包括其本身
     *
     * @param interfaceOrClass 接口或类
     * @return {@link Set<Class> }
     * @author chenz
     * @date 2021/09/21
     */
    public Set<Class<?>> getClassesBySuper(Class<?> interfaceOrClass) {
        // 获取所有class对象
        Set<Class<?>> keySet = getClasses();
        if (ValidationUtil.isEmpty(keySet)) {
            log.warn("nothing in beanMap");
            return Collections.emptySet();
        }
        // 通过注解筛选class对象
        Set<Class<?>> classSet = new HashSet<>();
        for (Class<?> clazz : keySet) {
            if (interfaceOrClass.isAssignableFrom(clazz) && !clazz.equals(interfaceOrClass)) {
                classSet.add(clazz);
            }
        }
        return classSet;
    }

}
