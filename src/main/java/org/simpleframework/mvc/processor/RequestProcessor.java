package org.simpleframework.mvc.processor;

import org.simpleframework.mvc.RequestProcessorChain;

/**
 * RequestProcessor
 *
 * @author chenz
 * @version 1.0
 * @date 2021/9/21
 */
@SuppressWarnings("squid:S112")
public interface RequestProcessor {
    /**
     * 处理器
     *
     * @param requestProcessorChain 请求处理器链
     * @return boolean
     * @throws Exception 异常
     * @author chenz
     * @date 2021/09/22
     */
    boolean process(RequestProcessorChain requestProcessorChain) throws Exception;
}
