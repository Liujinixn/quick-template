package com.quick.log.config.interceptor.handler;

import com.alibaba.fastjson.JSON;
import com.quick.common.vo.Result;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@ControllerAdvice
public class ResponseInterceptHandler implements ResponseBodyAdvice<Object> {

    /**
     * 响应内容，最大限额容量，响应内存超过该限额，则内容不入库
     */
    private final int MAX_BYTE = 1024 * 3;

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
        // log.info(">> 执行响应拦截器");
        ServletServerHttpResponse responseTemp = (ServletServerHttpResponse) serverHttpResponse;
        HttpServletResponse resp = responseTemp.getServletResponse();
        ServletServerHttpRequest sshr = (ServletServerHttpRequest) serverHttpRequest;
        HttpServletRequest req = sshr.getServletRequest();

        // 判定返回为Json数据
        if (body instanceof Result) {
            Result result = (Result) body;
            if (result != null) {
                log.info("Response响应，Json出参信息写入Attribute[{}]中", ServerLogInterceptorHandler.RESPONSE_RESULT);
                String jsonString = JSON.toJSONString(result);
                req.setAttribute(ServerLogInterceptorHandler.RESPONSE_RESULT, jsonString);
            }
            return body;
        }

        String jsonString = JSON.toJSONString(body);
        // 判定响应内容数据量过大
        if (jsonString.length() > MAX_BYTE) {
            req.setAttribute(ServerLogInterceptorHandler.RESPONSE_RESULT, "body could not be parsed, don't show.");
            return body;
        }

        // 判定为普通文本
        if (jsonString.startsWith("\"") && jsonString.endsWith("\"")) {
            int length = jsonString.length();
            req.setAttribute(ServerLogInterceptorHandler.RESPONSE_RESULT, jsonString.substring(1, --length));
        }
        return body;
    }
}
