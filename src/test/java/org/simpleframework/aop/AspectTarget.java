package org.simpleframework.aop;

import lombok.extern.slf4j.Slf4j;
import org.simpleframework.core.annotation.Component;

/**
 * TestAspect
 *
 * @author chenz
 * @version 1.0
 * @date 2021/9/21
 */
@Slf4j
@Component
public class AspectTarget {

    public Integer testRight() {
        log.info("AspectTarget#Right()方法执行！");
        return 10;
    }

    public void testThrowing() {
        log.info("AspectTarget#testThrowing()方法执行");
        throw new RuntimeException("我是异常");
    }
}
