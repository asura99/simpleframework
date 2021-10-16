package org.simpleframework.mvc.type;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * ModelAndView
 * <br>
 * 存储处理完后的结果数据，以及显示该数据的视图路径
 *
 * @author chenz
 * @version 1.0
 * @date 2021/9/21
 */
@SuppressWarnings("unused")
public class ModelAndView {

    /**
     * 页面所在的路径
     */
    @Getter
    private String view;

    /**
     * 页面的数据
     */
    @Getter
    private Map<String, Object> model = new HashMap<>();

    /**
     * 设置页面路径
     *
     * @param view 页面路径
     * @return {@link ModelAndView }
     * @author chenz
     * @date 2021/09/21
     */
    public ModelAndView setView(String view) {
        this.view = view;
        return this;
    }

    /**
     * 添加数据
     *
     * @param key   键
     * @param value 值
     * @return {@link ModelAndView }
     * @author chenz
     * @date 2021/09/21
     */
    public ModelAndView addViewData(String key, Object value) {
        this.model.put(key, value);
        return this;
    }
}
