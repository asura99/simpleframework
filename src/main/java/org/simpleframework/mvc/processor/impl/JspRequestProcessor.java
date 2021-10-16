package org.simpleframework.mvc.processor.impl;

import org.simpleframework.mvc.RequestProcessorChain;
import org.simpleframework.mvc.processor.RequestProcessor;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;

/**
 * JspRequestProcessor
 * <br>
 * jsp资源请求处理
 *
 * @author chenz
 * @version 1.0
 * @date 2021/9/21
 */
@SuppressWarnings("squid:S112")
public class JspRequestProcessor implements RequestProcessor {

    private final RequestDispatcher jspServlet;

    // jsp请求的RequestDispatcher的名称
    private static final String JSP_SERVLET = "jsp";
    // Jsp请求资源路径前缀
    private static final String JSP_RESOURCE_PREFIX = "/templates/";

    public JspRequestProcessor(ServletContext servletContext) {
        this.jspServlet = servletContext.getNamedDispatcher(JSP_SERVLET);
        if (null == jspServlet) {
            throw new RuntimeException("there is no jsp servlet");
        }
    }

    @Override
    public boolean process(RequestProcessorChain requestProcessorChain) throws Exception {
        // 是 jsp 资源就交给 jsp servlet 处理
        if (isJspResource(requestProcessorChain.getRequestPath())) {
            this.jspServlet.forward(requestProcessorChain.getRequest(), requestProcessorChain.getResponse());
            return false;
        }
        return true;
    }

    /**
     * 是否请求的是jsp资源
     *
     * @param url url
     * @return boolean
     * @author chenz
     * @date 2021/09/22
     */
    private boolean isJspResource(String url) {
        return url.startsWith(JSP_RESOURCE_PREFIX);
    }
}
