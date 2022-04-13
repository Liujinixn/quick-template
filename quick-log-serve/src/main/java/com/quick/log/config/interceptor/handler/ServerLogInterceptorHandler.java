package com.quick.log.config.interceptor.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.quick.auth.config.params.ShiroCoreParameters;
import com.quick.auth.entity.Permission;
import com.quick.auth.entity.User;
import com.quick.auth.service.PermissionService;
import com.quick.auth.service.UserService;
import com.quick.common.utils.ip.IpUtil;
import com.quick.common.utils.redis.RedisClient;
import com.quick.log.entity.OperateLog;
import com.quick.log.service.OperateLogService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 拦截器处理-记录操作日志
 *
 * @author Liujinxin
 */
@Component
public class ServerLogInterceptorHandler implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(ServerLogInterceptorHandler.class);

    public static final String REQUEST_PARAMS = "request_params";

    public static final String RESPONSE_RESULT = "response_result";

    private static final String REQUEST_START_TIME = "request_start_time";

    private static final String APPLICATION_JSON = "application/json";

    @Autowired
    PermissionService permissionService;

    @Autowired
    UserService userService;

    @Autowired
    ShiroCoreParameters shiroCoreParameters;

    @Autowired
    RedisClient redisClient;

    @Autowired
    OperateLogService operateLogService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info(">> 执行请求前拦截操作");
        log.info("Request请求前，当前系统时间记录Attribute[{}]中", REQUEST_START_TIME);
        request.setAttribute(REQUEST_START_TIME, System.currentTimeMillis());
        log.info("Request请求前，Json入参记录Attribute[{}]中", REQUEST_PARAMS);
        request.setAttribute(REQUEST_PARAMS, parseJsonParams(request));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception exception)
            throws Exception {
        log.info(">> 执行请求后拦截操作");
        Permission permissionInfo = permissionService.findPermissionSimpleInfoByUrl(request.getRequestURI());
        if (null == permissionInfo) {
            permissionInfo = new Permission();
        }
        User userInfo = userService.getLoginUserAllInfo();
        if (null == userInfo) {
            userInfo = new User();
        }
        // request 入参
        OperateLog operateLog = new OperateLog();
        operateLog.setUrl(request.getServletPath());
        operateLog.setRequestType(request.getMethod());
        operateLog.setRequestParams(parseRequestParams(request));
        String requestContentType = request.getContentType();
        if (StringUtils.isNotBlank(requestContentType)) {
            int end = requestContentType.indexOf(";");
            if (end != -1) {
                requestContentType = request.getContentType().substring(0, end);
            }
        }
        operateLog.setRequestContentType(requestContentType);
        operateLog.setDescription(permissionInfo.getDescription());
        operateLog.setClientIp(IpUtil.getIpAddr(request));
        operateLog.setUserAgent(request.getHeader("User-Agent"));
        operateLog.setOperatingAccount(userInfo.getUsername());
        // response 出参
        operateLog.setResponseContentType(response.getContentType());
        if (APPLICATION_JSON.equals(response.getContentType())) {
            operateLog.setResponseParams((String) request.getAttribute(RESPONSE_RESULT));
        } else {
            operateLog.setResponseParams("body could not be parsed, don't show.");
        }
        // 计算耗时
        long startTime = (long) request.getAttribute(REQUEST_START_TIME);
        long endTime = System.currentTimeMillis();
        long interval = endTime - startTime;
        operateLog.setConsumingTime(interval);
        operateLogService.insertOperateLog(operateLog);
        log.info("Request请求后，此次请求和响应信息记录日志表\n" +
                        "接口地址: {}\n请求类型: {}\n接口入参: {}\n" +
                        "接口描述: {}\n客户端IP: {}\n响应内容类型: {}\n" +
                        "响应参数: {}\n耗时：{}ms",
                operateLog.getUrl(), operateLog.getRequestType(), operateLog.getRequestParams(),
                operateLog.getDescription(), operateLog.getClientIp(), operateLog.getResponseContentType(),
                operateLog.getResponseParams(), operateLog.getConsumingTime());
    }

    /**
     * 解析请求参数（不区分post还是get请求）
     *
     * @param request HttpServletRequest信息
     */
    private String parseRequestParams(HttpServletRequest request) {
        String resultParams;
        String contentType = null == request.getContentType() ? "" : request.getContentType();
        switch (contentType) {
            case APPLICATION_JSON:
                resultParams = null == request.getAttribute(REQUEST_PARAMS) ? "{}" : String.valueOf(request.getAttribute(REQUEST_PARAMS));
                break;
            default:
                Map<String, Object> reqMap = new HashMap<>(6);
                Set<Map.Entry<String, String[]>> entry = request.getParameterMap().entrySet();
                Iterator<Map.Entry<String, String[]>> it = entry.iterator();
                while (it.hasNext()) {
                    Map.Entry<String, String[]> me = it.next();
                    String key = me.getKey();
                    String value = "null".equals(me.getValue()[0]) ? "" : me.getValue()[0];
                    reqMap.put(key, value);
                }
                resultParams = JSON.toJSONString(reqMap);
        }
        return resultParams;
    }

    /**
     * 解析请求头中的 json 入参参数
     *
     * @param request HttpServletRequest 请求头
     * @return
     */
    private String parseJsonParams(HttpServletRequest request) {
        if (StringUtils.isBlank(request.getContentType())) {
            return null;
        }
        if (!APPLICATION_JSON.equals(request.getContentType())) {
            return null;
        }
        JSONObject parameterMap = JSON.parseObject(new RequestWrapper(request).getBody());
        return parameterMap.toString();
    }

    /**
     * request 包装器
     */
    private class RequestWrapper extends HttpServletRequestWrapper {

        private final byte[] body;

        public RequestWrapper(HttpServletRequest request) {
            super(request);
            String sessionStream = getBodyString(request);
            body = sessionStream.getBytes(StandardCharsets.UTF_8);
        }

        //获取请求体
        public String getBody() {
            return new String(body, StandardCharsets.UTF_8);
        }

        /**
         * 获取请求Body
         *
         * @param request
         * @return
         */
        public String getBodyString(final ServletRequest request) {
            StringBuilder sb = new StringBuilder();
            InputStream inputStream = null;
            BufferedReader reader = null;
            try {
                inputStream = cloneInputStream(request.getInputStream());
                reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return sb.toString();
        }

        /**
         * Description: 复制输入流</br>
         *
         * @param inputStream
         * @return</br>
         */
        public InputStream cloneInputStream(ServletInputStream inputStream) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            try {
                while ((len = inputStream.read(buffer)) > -1) {
                    byteArrayOutputStream.write(buffer, 0, len);
                }
                byteArrayOutputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            InputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            return byteArrayInputStream;
        }

        @Override
        public BufferedReader getReader() throws IOException {
            return new BufferedReader(new InputStreamReader(getInputStream()));
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {

            final ByteArrayInputStream bais = new ByteArrayInputStream(body);

            return new ServletInputStream() {

                @Override
                public int read() throws IOException {
                    return bais.read();
                }

                @Override
                public boolean isFinished() {
                    return false;
                }

                @Override
                public boolean isReady() {
                    return false;
                }

                @Override
                public void setReadListener(ReadListener readListener) {
                }
            };
        }
    }


}
