package org.simpleframework.ioc;

import org.simpleframework.aop.AspectTarget;
import org.simpleframework.core.annotation.Component;
import org.simpleframework.inject.annotation.Autowired;

/**
 * TestIOC
 *
 * @author chenz
 * @version 1.0
 * @date 2021/9/21
 */
@Component
@SuppressWarnings("UnusedDeclaration")
public class TestIOC {

    @Autowired
    private AspectTarget aspectTarget;

    public void test() {
        Integer result = aspectTarget.testRight();
        System.out.println("结果为" + result);
        aspectTarget.testThrowing();
    }
}
