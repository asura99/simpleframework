package org.simpleframework.mvc.processor.impl;

import lombok.extern.slf4j.Slf4j;
import org.simpleframework.core.BeanContainer;
import org.simpleframework.mvc.RequestProcessorChain;
import org.simpleframework.mvc.annotation.RequestMapping;
import org.simpleframework.mvc.annotation.RequestParam;
import org.simpleframework.mvc.annotation.ResponseBody;
import org.simpleframework.mvc.processor.RequestProcessor;
import org.simpleframework.mvc.render.ResultRender;
import org.simpleframework.mvc.render.impl.JsonResultRender;
import org.simpleframework.mvc.render.impl.ResourceNotFoundResultRender;
import org.simpleframework.mvc.render.impl.ViewResultRender;
import org.simpleframework.mvc.type.ControllerMethod;
import org.simpleframework.mvc.type.RequestPathInfo;
import org.simpleframework.util.ConverterUtil;
import org.simpleframework.util.ValidationUtil;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ControllerRequestProcessor
 * <br>
 * Controller请求处理器
 *
 * @author chenz
 * @version 1.0
 * @date 2021/9/21
 */
@SuppressWarnings("RedundantThrows")
@Slf4j
public class ControllerRequestProcessor implements RequestProcessor {

    // IOC 容器
    private final BeanContainer beanContainer;
    // 请求和 controller 映射的集合
    private final Map<RequestPathInfo, ControllerMethod> pathControllerMethodMap = new ConcurrentHashMap<>();

    public ControllerRequestProcessor() {
        this.beanContainer = BeanContainer.getInstance();
        Set<Class<?>> requestMappingSet = this.beanContainer.getClassesByAnnotation(RequestMapping.class);
        initPathControllerMethodMap(requestMappingSet);
    }

    /**
     * 初始化路径控制器方法映射
     *
     * @param requestMappingSet 请求映射集
     * @author chenz
     * @date 2021/09/22
     */
    @SuppressWarnings("squid:S112")
    private void initPathControllerMethodMap(Set<Class<?>> requestMappingSet) {
        if (ValidationUtil.isEmpty(requestMappingSet)) {
            return;
        }
        // 遍历获取 RequestMapping 的值
        for (Class<?> requestMappingClass : requestMappingSet) {
            // 获取注解
            RequestMapping requestMapping = requestMappingClass.getAnnotation(RequestMapping.class);
            // 一级路径
            String basePath = requestMapping.value();

            if (!basePath.startsWith("/")) {
                basePath = "/" + basePath;
            }

            // 遍历所有被 @RequestMapping 标记的方法，获取方法上注解的值作为二级路径
            Method[] methods = requestMappingClass.getDeclaredMethods();
            if (ValidationUtil.isEmpty(methods)) {
                continue;
            }

            initControllerMethodMap(requestMappingClass, basePath, methods);
        }
    }

    /**
     * 将 url, controller method 初始化成 map
     *
     * @param requestMappingClass 请求映射类
     * @param basePath            基本路径
     * @param methods             方法
     * @author chenz
     * @date 2021/10/16
     */
    @SuppressWarnings("squid:S112")
    private void initControllerMethodMap(Class<?> requestMappingClass, String basePath, Method[] methods) {
        for (Method method : methods) {
            // 判断 method 的注解是否是 RequestMapping
            RequestMapping methodRequest = method.getDeclaredAnnotation(RequestMapping.class);
            // 获取 RequestMapping 的值作为二级路径
            String methodPath = methodRequest.value();
            // 检查并在路径的开头补充 "/"
            if (!methodPath.startsWith("/")) {
                methodPath = "/" + methodPath;
            }
            // 拼接一级和二级路径成完整的路径
            String url = basePath + methodPath;

            // 获取被 @RequestParam 标记的参数
            // 获取被标记的参数的数据类型，建立参数名和参数类型的映射
            Map<String, Class<?>> methodParams = new LinkedHashMap<>();
            Parameter[] parameters = method.getParameters();

            if (!ValidationUtil.isEmpty(parameters)) {
                for (Parameter parameter : parameters) {
                    RequestParam anno = parameter.getAnnotation(RequestParam.class);
                    // (暂定) 参数上必须添加 @RequestParam 注解
                    if (null == anno) {
                        throw new RuntimeException("The parameter must have @RequestParam");
                    }
                    // 将注解的值(参数名称):参数类型放入map
                    methodParams.put(anno.value(), parameter.getType());
                }
            }

            // 将获取到的信息封装成 RequestPathInfo 实例和 ControllerMethod 实例，放到映射表中
            String httpMethod = String.valueOf(methodRequest.method());
            RequestPathInfo requestPathInfo = new RequestPathInfo(httpMethod, url);

            // 判断该 requestPathInfo 是否在 pathControllerMethodMap 中，如果在就给警告
            if (this.pathControllerMethodMap.containsKey(requestPathInfo)) {
                log.warn("Duplicate url:{} registration，current class {} method {} will override the former one",
                        requestPathInfo.getHttpPath(), requestMappingClass.getName(), method.getName());
            }

            ControllerMethod controllerMethod = new ControllerMethod(requestMappingClass, method, methodParams);

            // 将 controllerMethod 放进 controller map 里
            log.info("可访问路径: {}", requestPathInfo.getHttpPath());
            this.pathControllerMethodMap.put(requestPathInfo, controllerMethod);
        }
    }

    @Override
    public boolean process(RequestProcessorChain requestProcessorChain) throws Exception {
        // 1. 解析请求方法，路径，controllerMethod
        String method = requestProcessorChain.getRequestMethod();
        String path = requestProcessorChain.getRequestPath();

        ControllerMethod controllerMethod = this.pathControllerMethodMap.get(new RequestPathInfo(method, path));

        if (null == controllerMethod) {
            requestProcessorChain.setResultRender(new ResourceNotFoundResultRender(method, path));
            return false;
        }

        // 2. 解析请求参数，传给方法执行
        Object result = this.invokeControllerMethod(controllerMethod, requestProcessorChain.getRequest());

        // 3. 根据处理的结果，选择对应的 render 进行渲染
        this.setResultRender(result, controllerMethod, requestProcessorChain);

        return true;
    }

    /**
     * 设置渲染器
     *
     * @param result                结果
     * @param controllerMethod      控制器方法
     * @param requestProcessorChain 请求处理器链
     * @author chenz
     * @date 2021/10/14
     */
    private void setResultRender(Object result, ControllerMethod controllerMethod,
            RequestProcessorChain requestProcessorChain) {
        if (null == result) {
            return;
        }

        ResultRender resultRender;

        // 检查是否有 @ResponseBody
        boolean isJSON = controllerMethod.getInvokeMethod().isAnnotationPresent(ResponseBody.class);

        if (isJSON) {
            resultRender = new JsonResultRender(result);
        } else {
            resultRender = new ViewResultRender(result);
        }

        requestProcessorChain.setResultRender(resultRender);
    }

    /**
     * 执行 controller 中的方法
     *
     * @param controllerMethod 控制器方法
     * @param request          请求
     * @return {@link Object }
     * @author chenz
     * @date 2021/10/14
     */
    @SuppressWarnings({"squid:S112", "squid:S3011"})
    private Object invokeControllerMethod(ControllerMethod controllerMethod, HttpServletRequest request) {
        // 1. 从 request 中获取参数
        HashMap<String, String> requestParamMap = new HashMap<>();
        Map<String, String[]> parameterMap = request.getParameterMap();

        for (Map.Entry<String, String[]> parameter : parameterMap.entrySet()) {
            if (!ValidationUtil.isEmpty(parameter.getValue())) {
                // 只支持一个参数一个值
                requestParamMap.put(parameter.getKey(), parameter.getValue()[0]);
            }
        }

        // 2. 根据获取到的请求参数名及对应的值，以及 controllerMethod 里面的参数和类型的映射关系，去实例化方法对应的参数
        List<Object> methodParams = new ArrayList<>();
        Map<String, Class<?>> methodParamMap = controllerMethod.getMethodParameters();

        for (Map.Entry<String, Class<?>> entry : methodParamMap.entrySet()) {
            String paramName = entry.getKey();
            Class<?> type = entry.getValue();
            String requestValue = requestParamMap.get(paramName);
            Object value;
            if (null == requestValue) {
                value = ConverterUtil.primitiveNull(type);
            } else {
                value = ConverterUtil.convert(type, requestValue);
            }
            methodParams.add(value);
        }

        // 3. 执行 controller 中的方法并返回结果
        Object controller = beanContainer.getBean(controllerMethod.getControllerClass());
        Method method = controllerMethod.getInvokeMethod();

        method.setAccessible(true);

        Object result;
        try {
            if (methodParams.isEmpty()) {
                result = method.invoke(controller);
            } else {
                result = method.invoke(controller, methodParams.toArray());
            }
        } catch (InvocationTargetException e) {
            //如果是调用异常的话，需要通过e.getTargetException()
            // 去获取执行方法抛出的异常
            throw new RuntimeException(e.getTargetException());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}
