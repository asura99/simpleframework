package org.simpleframework.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 类相关通用方法通过类加载器获取资源信息
 *
 * @author chenz
 * @version 1.0
 * @date 2021/9/20
 */
@Slf4j
@SuppressWarnings({"unused", "squid:S112", "squid:S3011"})
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClassUtil {

    public static final String FILE_PROTOCOL = "file";

    /**
     * 设置属性
     *
     * @param field  属性
     * @param target 目标
     * @param value  值
     * @author chenz
     * @date 2021/09/20
     */
    public static void setField(Field field, Object target, Object value) {
        try {
            field.setAccessible(true);
            field.set(target, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据传入的包名，获取该包以及其子包下所有的类
     *
     * @param packageName 包名
     * @return {@link Set<Class> }
     * @author chenz
     * @date 2021/09/21
     */
    @SneakyThrows
    public static Set<Class<?>> extractPackageClass(String packageName) {
        // 获取类加载器
        ClassLoader classLoader = getClassLoader();
        // 通过类加载器获取加载的资源
        URL url = classLoader.getResource(packageName.replace(".", File.separator));
        if (null == url) {
            log.warn("unable to retrieve anything from package: {}", packageName);
            return Collections.emptySet();
        }
        // 依据不同的资源类型那个，采用不同的方式获取资源的集合
        Set<Class<?>> classSet = null;
        // 文件类型的资源
        if (url.getProtocol().equalsIgnoreCase(FILE_PROTOCOL)) {
            classSet = new HashSet<>();
            File packageDirectory = new File(url.getPath());
            extractClassFile(classSet, packageDirectory, packageName);
        }
        return classSet;
    }


    /**
     * 实例化class
     *
     * @param clazz clazz
     * @return {@link T }
     * @author chenz
     * @date 2021/09/21
     */
    public static <T> T newInstance(Class<T> clazz) {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (InvocationTargetException|NoSuchMethodException|InstantiationException|IllegalAccessException e) {
            log.error("newInstance error", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 递归获取目标package里面的所有class文件（包括子package里的class文件）
     *
     * @param emptyClassSet 空类集
     * @param fileSource    源文件
     * @param packageName   包名
     * @author chenz
     * @date 2021/09/21
     */
    public static void extractClassFile(Set<Class<?>> emptyClassSet, File fileSource, String packageName) {
        if (!fileSource.isDirectory()) {
            return;
        }
        // 是文件夹，调用listFiles方法获取文件夹下的文件或文件夹
        File[] files = fileSource.listFiles(new FileFilter() {

            @Override
            public boolean accept(File file) {
                if (file.isDirectory()) {
                    return true;
                } else {
                    //获取文件的绝对值路径
                    String absoluteFilePath = file.getAbsolutePath();
                    if (absoluteFilePath.endsWith(".class")) {
                        //若是class文件则直接加载
                        addToClassSet(absoluteFilePath);
                    }
                }
                return false;
            }

            /**
             * 根据class文件的绝对路径，获取并生成class对象，放入classSet中
             *
             * @param absoluteFilePath 绝对的文件路径
             * @author chenz
             * @date 2021/09/21
             */
            private void addToClassSet(String absoluteFilePath) {
                //1.从class文件的绝对值路径里面提取出包含了package的类名
                absoluteFilePath = absoluteFilePath.replace(File.separator, ".");
                String className = absoluteFilePath.substring(absoluteFilePath.indexOf(packageName));
                className = className.substring(0, className.lastIndexOf("."));
                //2.通过反射机制获取对应的class对象并加入到classSet里
                Class<?> targetClass = loadClass(className);
                emptyClassSet.add(targetClass);
            }
        });
        if (null != files) {
            for (File file : files) {
                //递归调用
                extractClassFile(emptyClassSet, file, packageName);
            }
        }
    }

    /**
     * 加载类
     *
     * @param className 类名
     * @return {@link Class }
     * @author chenz
     * @date 2021/09/21
     */
    private static Class<?> loadClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            log.error("load class error ", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取当前线程的上下文类加载器
     *
     * @return {@link ClassLoader }
     * @author chenz
     * @date 2021/09/20
     */
    public static ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }
}
