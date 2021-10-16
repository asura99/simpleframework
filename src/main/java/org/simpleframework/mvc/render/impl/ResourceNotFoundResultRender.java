package org.simpleframework.mvc.render.impl;

import org.simpleframework.mvc.RequestProcessorChain;
import org.simpleframework.mvc.render.ResultRender;

import javax.servlet.http.HttpServletResponse;

/**
 * ResourceNotFoundResultRender
 *
 * @author chenz
 * @version 1.0
 * @date 2021/9/21
 */
public class ResourceNotFoundResultRender implements ResultRender {

    private final String httpMethod;
    private final String httpPath;

    public ResourceNotFoundResultRender(String httpMethod, String httpPath) {
        this.httpMethod = httpMethod;
        this.httpPath = httpPath;
    }

    @Override
    public void render(RequestProcessorChain requestProcessorChain) throws Exception {
        requestProcessorChain.getResponse().sendError(HttpServletResponse.SC_NOT_FOUND,
                String.format("获取不到对应的请求资源：请求路径[ %s ]；请求方法[ %s ]", httpPath, httpMethod));
    }
}
