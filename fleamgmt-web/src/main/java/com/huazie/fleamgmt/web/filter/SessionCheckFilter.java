package com.huazie.fleamgmt.web.filter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.huazie.frame.common.util.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p> Session信息校验 </p>
 *
 * @author huazie
 * @version 1.0.0
 * @since 1.0.0
 */
public class SessionCheckFilter implements Filter {

    private final static Logger LOGGER = LoggerFactory.getLogger(SessionCheckFilter.class);

    protected FilterConfig filterConfig = null;

    private String redirectURL = null;

    private Set<String> checkUrlSet = new HashSet<String>();

    private Set<String> ingoreUrlSet = new HashSet<String>();

    private Set<String> requestPrefixSet = new HashSet<String>();

    private String sessionKey = null;

    @Override
    public void destroy() {
        this.checkUrlSet.clear();
        this.ingoreUrlSet.clear();
        this.requestPrefixSet.clear();
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpSession session = request.getSession();
        // 如果是忽略的URL
        if (sessionKey == null || isInIngoreUrls(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        // 请求被列在了过滤器中了
        if (isInCheckUrls(request)) {
            response.sendRedirect(request.getContextPath() + redirectURL);
            return;
        }
        // 如果是业务请求并且用户没有登录，重定向到指定页面
        if (isBusiRequest(request) && isNotLogin(session)) {
            response.sendRedirect(request.getContextPath() + redirectURL);
            return;
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    /**
     * <p> 请求的URL是否是需要校验的URL </p>
     *
     * @param request 请求对象
     * @return true：是 , false: 不是
     * @since 1.0.0
     */
    private boolean isInCheckUrls(HttpServletRequest request) {
        String uri = request.getServletPath() + (request.getPathInfo() == null ? "" : request.getPathInfo());
        Iterator<String> it = checkUrlSet.iterator();
        while (it.hasNext()) {
            String prefix = it.next();
            if (uri.startsWith(prefix) || uri.contains(prefix)) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("SessionCheckFilter##isInCheckUrls() The {} is in {}, true", uri, checkUrlSet);
                }
                return true;
            }
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("SessionCheckFilter##isInCheckUrls() The {} is in {}, false", uri, checkUrlSet);
        }

        return false;
    }

    /**
     * <p> 请求的URL是否是需要忽略的URL </p>
     *
     * @param request 请求对象
     * @return true：是 , false: 不是
     * @since 1.0.0
     */
    private boolean isInIngoreUrls(HttpServletRequest request) {
        String uri = request.getServletPath() + (request.getPathInfo() == null ? "" : request.getPathInfo());
        Iterator<String> it = ingoreUrlSet.iterator();
        while (it.hasNext()) {
            String prefix = it.next();
            if (uri.startsWith(prefix) || uri.contains(prefix)) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("SessionCheckFilter##isInIngoreUrls() The {} is in {}, true", uri, ingoreUrlSet);
                }
                return true;
            }
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("SessionCheckFilter##isInIngoreUrls() The {} is in {}, false", uri, ingoreUrlSet);
        }
        return false;
    }

    /**
     * <p> 请求的URL是否是业务请求 </p>
     *
     * @param request 请求对象
     * @return true：是 , false: 不是
     * @since 1.0.0
     */
    private boolean isBusiRequest(HttpServletRequest request) {

        String uri = request.getServletPath() + (request.getPathInfo() == null ? "" : request.getPathInfo());

        Iterator<String> it = requestPrefixSet.iterator();
        while (it.hasNext()) {
            String prefix = it.next();

            if (uri.startsWith(prefix)) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("SessionCheckFilter##isBusiRequest() The {} starts with {}, true", uri, requestPrefixSet);
                }
                return true;
            }
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("SessionCheckFilter##isBusiRequest() The {} starts with {}, false", uri);
        }
        return false;
    }

    /**
     * <p> 根据Session判断用户是否已经登录 </p>
     *
     * @param session HttpSession对象
     * @return true：未登录 , false: 已登录
     * @since 1.0.0
     */
    private boolean isNotLogin(HttpSession session) {
        Object sessionObj = session.getAttribute(sessionKey);
        if (ObjectUtils.isEmpty(sessionObj)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("SessionCheckFilter##isNotLogin() Not Login");
            }
            return true;
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("SessionCheckFilter##isNotLogin() Login");
        }
        return false;
    }

    @Override
    public void init(FilterConfig config) {
        this.filterConfig = config;
        this.redirectURL = this.filterConfig.getInitParameter("RedirectURL");
        this.sessionKey = this.filterConfig.getInitParameter("CheckSessionKey");
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("SessionCheckFilter##init() RedirectURL={}", redirectURL);
            LOGGER.debug("SessionCheckFilter##init() SessionKey={}", sessionKey);
        }
        String checkUrlsStr = this.filterConfig.getInitParameter("CheckUrls");
        String IgnoreUrlsStr = this.filterConfig.getInitParameter("IgnoreUrls");
        String requestPrefixStr = this.filterConfig.getInitParameter("RequestPrefix");
        if (checkUrlsStr != null) {
            String[] params = checkUrlsStr.split(",");
            for (int i = 0; i < params.length; i++) {
                checkUrlSet.add(params[i].trim());
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("SessionCheckFilter##init() CheckUrlSet={}", checkUrlSet);
            }
        }
        if (IgnoreUrlsStr != null) {
            String[] params = IgnoreUrlsStr.split(",");
            for (int i = 0; i < params.length; i++) {
                ingoreUrlSet.add(params[i].trim());
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("SessionCheckFilter##init() IngoreUrlSet={}", ingoreUrlSet);
            }
        }
        if (requestPrefixStr != null) {
            String[] params = requestPrefixStr.split(",");
            for (int i = 0; i < params.length; i++) {
                requestPrefixSet.add(params[i].trim());
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("SessionCheckFilter##init() RequestPrefixSet={}", requestPrefixSet);
            }
        }

    }

}
