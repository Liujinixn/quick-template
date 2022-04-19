package com.quick.config.filter;

import com.quick.config.filter.wrapper.RequestWrapper;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 系统过滤器
 *
 * @author Liujinxin
 */
@Slf4j
@WebFilter(filterName = "ServeFilter", urlPatterns = {"/*"})
public class ServeFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info(">> 过滤器初始化");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        log.info("---------------接口接入---------------");
        log.info(">> 执行过滤操作");
        log.info("Request请求头信息包装（解决拦截器读取流后出现失效）");
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        ServletRequest requestWrapper = new RequestWrapper(httpServletRequest);
        filterChain.doFilter(requestWrapper, servletResponse);
    }

    @Override
    public void destroy() {
        log.info(">> 过滤器销毁");
    }
}

