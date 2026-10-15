package com.huazie.fleamgmt.springmvc.common;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * <p> Web 请求工具类 </p>
 *
 * @author huazie
 * @version 1.0.0
 * @since 1.0.0
 */
public final class WebUtils {

    /**
     * <p> 私有构造方法，工具类不需要实例化 </p>
     *
     * @since 1.0.0
     */
    private WebUtils() {
    }

    /**
     * <p> 获取当前请求线程绑定的 HttpServletRequest </p>
     *
     * <p> 非请求线程（如异步任务）或非 Servlet 请求上下文时返回 null。 </p>
     *
     * @return 当前请求对象，无请求上下文时为 null
     * @since 1.0.0
     */
    public static HttpServletRequest getRequest() {
        try {
            if (RequestContextHolder.currentRequestAttributes() instanceof ServletRequestAttributes) {
                return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            }
        } catch (IllegalStateException e) {
            // 当前线程不存在请求上下文
        }
        return null;
    }
}
