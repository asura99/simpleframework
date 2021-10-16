package org.simpleframework.mvc.render;

import org.simpleframework.mvc.RequestProcessorChain;

/**
 * ResultRender
 *
 * @author chenz
 * @version 1.0
 * @date 2021/9/21
 */
@SuppressWarnings("squid:S112")
public interface ResultRender {

    /**
     * 渲染器
     *
     * @param requestProcessorChain 请求处理器链
     * @throws Exception 异常
     * @author chenz
     * @date 2021/09/22
     */
    void render(RequestProcessorChain requestProcessorChain) throws Exception;
}
