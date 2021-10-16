package org.simpleframework.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BeanContainerTest
 *
 * @author chenz
 * @version 1.0
 * @date 2021/9/21
 */
class BeanContainerTest {

    private static BeanContainer beanContainer;

    @BeforeEach
    void init() {
        beanContainer = BeanContainer.getInstance();
    }

    @DisplayName("加载目标对象及其实例到BeanContainer：loadBeansTest")
    @Test
    void loadBeansTest() {
        // 没有加载 bean 时查看容器是否被加载
        assertFalse(beanContainer.isLoaded());
        // 加载指定包下的 bean
        beanContainer.loadBeans("org.simpleframework.chen");
        // 查看是否和预想的一样
        Assertions.assertEquals(2, beanContainer.size());
        // 测试当前 bean 容器是否被加载
        assertTrue(beanContainer.isLoaded());
    }
}