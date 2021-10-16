package org.simpleframework.mvc.render.impl;

import org.simpleframework.mvc.RequestProcessorChain;
import org.simpleframework.mvc.render.ResultRender;

import javax.servlet.http.HttpServletResponse;

/**
 * InternalErrorResultRender
 *
 * @author chenz
 * @version 1.0
 * @date 2021/9/21
 */
public class InternalErrorResultRender implements ResultRender {

    private final String errorMessage;

    public InternalErrorResultRender(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public void render(RequestProcessorChain requestProcessorChain) throws Exception {
        requestProcessorChain.getResponse().sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMessage);
    }
}
