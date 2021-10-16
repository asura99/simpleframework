package org.simpleframework.mvc;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.simpleframework.mvc.processor.RequestProcessor;
import org.simpleframework.mvc.render.ResultRender;
import org.simpleframework.mvc.render.impl.DefaultResultRender;
import org.simpleframework.mvc.render.impl.InternalErrorResultRender;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;

/**
 * RequestProcessorChain
 * <br>
 * 以责任链的模式执行注册的请求处理器 <br>
 * 委派给特定的 Render 实例对处理后的结果进行渲染
 *
 * @author chenz
 * @version 1.0
 * @date 2021/9/20
 */
@Data
@Slf4j
@SuppressWarnings("squid:S112")
public class RequestProcessorChain {
    // 请求处理器迭代器
    private Iterator<RequestProcessor> requestProcessorIterator;

    private HttpServletRequest request;

    private HttpServletResponse response;

    // http请求方法
    private String requestMethod;

    // http请求路径
    private String requestPath;

    // http响应状态码
    private int responseCode;

    // 请求结果渲染器
    private ResultRender resultRender;

    public RequestProcessorChain(Iterator<RequestProcessor> requestProcessorIterator, HttpServletRequest request,
            HttpServletResponse response) {
        this.requestProcessorIterator = requestProcessorIterator;
        this.request = request;
        this.response = response;
        this.requestMethod = request.getMethod();
        this.requestPath = request.getPathInfo();
        this.responseCode = HttpServletResponse.SC_OK;
    }

    /**
     * 以责任链模式执行请求链
     *
     * @author chenz
     * @date 2021/09/22
     */
    public void doRequestProcessorChain() {
        // 遍历迭代器注册的请求处理器实现类列表
        try {
            while (requestProcessorIterator.hasNext()) {
                // 直到某个处理器执行后返回 false 为止
                if (!requestProcessorIterator.next().process(this)) {
                    break;
                }
            }
        } catch (Exception e) {
            // 期间出现异常，则交给内部异常渲染处理器
            this.resultRender = new InternalErrorResultRender(e.getMessage());
            log.error("doRequestProcessorChain error: ", e);
        }
    }

    /**
     * 执行渲染
     *
     * @author chenz
     * @date 2021/09/22
     */
    public void doRender() {
        // 如果请求处理器实现类均未选择合适的渲染器，就使用默认渲染器
        if (null == this.resultRender) {
            this.resultRender = new DefaultResultRender();
        }
        // 调用渲染器的 render 方法对结果进行渲染
        try {
            this.resultRender.render(this);
        } catch (Exception e) {
            log.error("doRender error: ", e);
            throw new RuntimeException(e);
        }
    }

}
