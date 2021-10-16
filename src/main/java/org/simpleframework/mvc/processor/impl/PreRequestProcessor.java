package org.simpleframework.mvc.processor.impl;

import lombok.extern.slf4j.Slf4j;
import org.simpleframework.mvc.RequestProcessorChain;
import org.simpleframework.mvc.processor.RequestProcessor;

/**
 * PreRequestProcessor
 * <br>
 * 请求预处理,包括编码以及路径处理
 *
 * @author chenz
 * @version 1.0
 * @date 2021/9/21
 */
@Slf4j
public class PreRequestProcessor implements RequestProcessor {
    @Override
    public boolean process(RequestProcessorChain requestProcessorChain) throws Exception {
        // 设置编码
        requestProcessorChain.getRequest().setCharacterEncoding("UTF-8");
        // 将请求路径末尾的/剔除，为后续匹配Controller请求路径做准备
        String requestPath = requestProcessorChain.getRequestPath();
        if (requestPath.length() > 1 && requestPath.endsWith("/")) {
            requestProcessorChain.setRequestPath(requestPath.substring(0, requestPath.length() - 1));
        }
        // 开头补充
        if (!requestPath.startsWith("/")) {
            requestProcessorChain.setRequestPath("/" + requestPath);
        }
        log.info("preprocess request {} {}", requestProcessorChain.getRequestMethod(), requestProcessorChain.getRequestPath());
        return true;
    }
}
