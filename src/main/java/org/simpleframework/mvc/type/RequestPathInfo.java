package org.simpleframework.mvc.type;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * RequestPathInfo
 * <br>
 * 存储http请求的路径和请求的方法
 *
 * @author chenz
 * @version 1.0
 * @date 2021/9/21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestPathInfo {

    /**
     * http 请求方法
     */
    private String httpMethod;

    /**
     * http 请求路径
     */
    private String httpPath;
}
