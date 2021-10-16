package org.simpleframework.aop;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.simpleframework.ioc.TestIOC;
import org.simpleframework.core.BeanContainer;
import org.simpleframework.inject.DependencyInjector;

/**
 * TestAOP
 *
 * @author chenz
 * @version 1.0
 * @date 2021/9/21
 */
@Slf4j
class TestAOP {

    private static BeanContainer beanContainer;

    @BeforeEach
    void init() {
        beanContainer = BeanContainer.getInstance();
        beanContainer.loadBeans("org.simpleframework");
        // 进行增强
        new AspectWeaver().doAOP();
        // 依赖注入
        new DependencyInjector().doIOC();
    }


    @DisplayName("测试 AOP & DI")
    @Test
    void test() {
        TestIOC bean = (TestIOC) beanContainer.getBean(TestIOC.class);
        bean.test();
    }
}
