package com.quick.log.config.interceptor.handle;

import com.alibaba.fastjson.JSON;
import com.quick.common.vo.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 响应拦截处理
 *
 * @author Liujinxin
 */
@ControllerAdvice
public class ResponseIntercept implements ResponseBodyAdvice<Object> {

    private static final Logger log = LoggerFactory.getLogger(ResponseIntercept.class);

    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
        return true;
    }

    @Nullable
    @Override
    public Object beforeBodyWrite(@Nullable Object body,
                                  MethodParameter methodParameter,
                                  MediaType mediaType,
                                  Class<? extends HttpMessageConverter<?>> aClass,
                                  ServerHttpRequest serverHttpRequest,
                                  ServerHttpResponse serverHttpResponse) {
        log.info(">> 执行响应拦截器");
        ServletServerHttpResponse responseTemp = (ServletServerHttpResponse) serverHttpResponse;
        HttpServletResponse resp = responseTemp.getServletResponse();
        ServletServerHttpRequest sshr = (ServletServerHttpRequest) serverHttpRequest;
        HttpServletRequest req = sshr.getServletRequest();
        // 此处的 Result 对象是我自定义的返回值类型,具体根据自己需求修改即可
        if (body instanceof Result) {
            Result result = (Result) body;
            if (result != null) {
                /** 记录日志等操作，日志记录在 com.quick.log.config.interceptor.dealWith.LogInterceptorDealWith 中处理 **/
                log.info("Response响应，Json出参信息写入Attribute[{}]中", ServerLogInterceptor.RESPONSE_RESULT);
                // 将Json出参信息 - 记录到 request attribute属性中传输到com.quick.log.config.interceptor.dealWith.RequestLogInterceptorDealWith.afterCompletion
                req.setAttribute(ServerLogInterceptor.RESPONSE_RESULT, JSON.toJSONString(result));
            }
            /** 这里可以对返回值进行修改二次封装等操作 **/

        }
        return body;
    }
}
