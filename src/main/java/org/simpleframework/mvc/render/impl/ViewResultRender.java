package org.simpleframework.mvc.render.impl;

import org.simpleframework.mvc.RequestProcessorChain;
import org.simpleframework.mvc.render.ResultRender;
import org.simpleframework.mvc.type.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * ViewResultRender
 *
 * @author chenz
 * @version 1.0
 * @date 2021/9/21
 */
@SuppressWarnings("squid:S112")
public class ViewResultRender implements ResultRender {

    public static final String VIEW_PATH = "/templates";
    private ModelAndView modelAndView;

    public ViewResultRender(Object modelAndView) {
        if (modelAndView instanceof ModelAndView) {
            // 如果是 ModelAndView 类型的，直接赋值
            this.modelAndView = (ModelAndView) modelAndView;
        } else if (modelAndView instanceof String) {
            // 传入的是 String，则为视图，需要包装后赋值
            this.modelAndView = new ModelAndView().setView((String) modelAndView);
        } else {
            // 其他情况，抛出异常
            throw new RuntimeException("illegal request result type");
        }
    }

    @Override
    public void render(RequestProcessorChain requestProcessorChain) throws Exception {
        // 获取 request 和 response
        HttpServletRequest request = requestProcessorChain.getRequest();
        HttpServletResponse response = requestProcessorChain.getResponse();

        // 获取视图的路径
        String path = modelAndView.getView();

        // 获取数据
        Map<String, Object> model = modelAndView.getModel();

        // 将数据放进 request 域中
        for (Map.Entry<String, Object> entry : model.entrySet()) {
            request.setAttribute(entry.getKey(), entry.getValue());
        }

        // 请求转发
        request.getRequestDispatcher(VIEW_PATH + path).forward(request, response);
    }
}
