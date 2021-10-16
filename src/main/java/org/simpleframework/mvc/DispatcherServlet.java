package org.simpleframework.mvc;

import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.simpleframework.aop.AspectWeaver;
import org.simpleframework.core.BeanContainer;
import org.simpleframework.inject.DependencyInjector;
import org.simpleframework.mvc.processor.RequestProcessor;
import org.simpleframework.mvc.processor.impl.ControllerRequestProcessor;
import org.simpleframework.mvc.processor.impl.JspRequestProcessor;
import org.simpleframework.mvc.processor.impl.PreRequestProcessor;
import org.simpleframework.mvc.processor.impl.StaticRequestProcessor;
import org.yaml.snakeyaml.Yaml;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * DispatcherServlet
 * 
 * @author chenz
 * @version 1.0
 * @date 2021/9/20
 */
@Slf4j
@WebServlet("/*")
@SuppressWarnings({"MismatchedQueryAndUpdateOfCollection", "unused", "RedundantThrows"})
public class DispatcherServlet extends HttpServlet {

    /**
     * simpleframework 配置文件
     */
    private static final String SIMPLEFRAMEWORK_CONFIG_FILE_YAML = "simpleframework.yml";
    private static final String SIMPLEFRAMEWORK_CONFIG_FILE_PROPERTIES = "application.properties";

    /**
     * 存放请求所需要经过的处理器
     */
    private static final List<RequestProcessor> PROCESSOR = new ArrayList<>();

    @Override
    public void init() throws ServletException {
        BeanContainer beanContainer = BeanContainer.getInstance();
        beanContainer.loadBeans(getSimpleframeworkScanPackagesProperties());

        new AspectWeaver().doAOP();
        new DependencyInjector().doIOC();

        // 初始化请求处理器责任链
        PROCESSOR.add(new PreRequestProcessor());
        PROCESSOR.add(new StaticRequestProcessor(getServletContext()));
        PROCESSOR.add(new JspRequestProcessor(getServletContext()));
        PROCESSOR.add(new ControllerRequestProcessor());
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 创建责任链对象实例
        RequestProcessorChain processorChain = new RequestProcessorChain(PROCESSOR.iterator(), req, resp);
        // 通过责任链模式依次调用请求处理器对请求进行处理
        processorChain.doRequestProcessorChain();
        // 对结果进行渲染
        processorChain.doRender();
    }

    /**
     * 得到 simpleframework 扫描包配置
     *
     * @return {@link String }
     * @author chenz
     * @date 2021/09/21
     */
    private String getSimpleframeworkScanPackagesYAML() {
        Yaml yaml = new Yaml();
        InputStream resourceStream =
                Thread.currentThread().getContextClassLoader().getResourceAsStream(SIMPLEFRAMEWORK_CONFIG_FILE_YAML);
        if (null == resourceStream) {
            log.warn("The {} can not load", SIMPLEFRAMEWORK_CONFIG_FILE_YAML);
        }
        Map<String, String> config = yaml.loadAs(resourceStream, new TypeToken<Map<String, String>>(){}.getRawType().asSubclass(String.class));
        String scanPackages = config.get("simpleframework.scan.packages");
        log.info("The scan packages path is {}", scanPackages);
        return scanPackages;
    }

    private String getSimpleframeworkScanPackagesProperties() {
        Properties properties = new Properties();
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(SIMPLEFRAMEWORK_CONFIG_FILE_PROPERTIES);
        try {
            properties.load(in);
        } catch (IOException e) {
            log.warn("The application.properties can not load");
            e.printStackTrace();
        }

        String scanPackages = properties.getProperty("simpleframework.scan.packages");
        log.info("this is scanPackages: {}", scanPackages);
        return scanPackages;
    }
}
