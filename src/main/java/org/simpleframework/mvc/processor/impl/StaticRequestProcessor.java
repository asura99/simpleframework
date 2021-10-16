package org.simpleframework.mvc.processor.impl;

import lombok.extern.slf4j.Slf4j;
import org.simpleframework.mvc.RequestProcessorChain;
import org.simpleframework.mvc.processor.RequestProcessor;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;

/**
 * StaticRequestProcessor
 * <br>
 * 静态资源请求处理，包括但不限于图片，css，以及js文件等, 转发到 DefaultServlet
 *
 * @author chenz
 * @version 1.0
 * @date 2021/9/21
 */
@Slf4j
@SuppressWarnings("squid:S112")
public class StaticRequestProcessor implements RequestProcessor {

    public static final String DEFAULT_TOMCAT_SERVLET = "default";
    public static final String STATIC_RESOURCE_PREFIX = "/static/";
    // tomcat默认请求派发器RequestDispatcher的名称
    RequestDispatcher defaultDispatcher;

    public StaticRequestProcessor(ServletContext servletContext) {
        // 获取默认的 dispatcher
        this.defaultDispatcher = servletContext.getNamedDispatcher(DEFAULT_TOMCAT_SERVLET);
        if (this.defaultDispatcher == null) {
            throw new RuntimeException("There is no default tomcat servlet");
        }
        log.info("The default servlet for static resource is {}", DEFAULT_TOMCAT_SERVLET);
    }

    @Override
    public boolean process(RequestProcessorChain requestProcessorChain) throws Exception {
        // 1.通过请求路径判断是否是请求的静态资源 webapp/static
        if (isStaticResource(requestProcessorChain.getRequestPath())) {
            // 2.如果是静态资源，则将请求转发给 default servlet处理
            defaultDispatcher.forward(requestProcessorChain.getRequest(), requestProcessorChain.getResponse());
            return false;
        }
        return true;
    }

    /**
     * 通过请求路径前缀（目录）是否为静态资源 /static/
     *
     * @param path 路径
     * @return boolean
     * @author chenz
     * @date 2021/09/22
     */
    private boolean isStaticResource(String path) {
        return path.startsWith(STATIC_RESOURCE_PREFIX);
    }

}
