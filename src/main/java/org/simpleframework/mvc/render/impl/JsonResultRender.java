package org.simpleframework.mvc.render.impl;

import com.google.gson.Gson;
import org.simpleframework.mvc.RequestProcessorChain;
import org.simpleframework.mvc.render.ResultRender;

import java.io.PrintWriter;

/**
 * JsonResultRender
 *
 * @author chenz
 * @version 1.0
 * @date 2021/9/21
 */
public class JsonResultRender implements ResultRender {

    private final Object jsonData;

    public JsonResultRender(Object jsonData) {this.jsonData = jsonData;}

    @Override
    public void render(RequestProcessorChain requestProcessorChain) throws Exception {
        // 设置响应头
        requestProcessorChain.getResponse().setContentType("application/json");
        requestProcessorChain.getResponse().setCharacterEncoding("UTF-8");
        // 使用 gson 包装
        try (PrintWriter writer = requestProcessorChain.getResponse().getWriter()) {
            Gson gson = new Gson();
            // 数据转成 json 并写入响应流
            writer.write(gson.toJson(jsonData));
            writer.flush();
        }
    }
}
