package com.quick.log.config.interceptor.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.quick.auth.entity.Permission;
import com.quick.auth.entity.User;
import com.quick.auth.service.PermissionService;
import com.quick.auth.utils.ShiroUtil;
import com.quick.common.utils.ip.IpUtil;
import com.quick.log.config.params.LogBackCoreParameters;
import com.quick.log.config.params.internal.RecordSpecificPathInfo;
import com.quick.log.entity.OperateLog;
import com.quick.log.service.OperateLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
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
@Slf4j
@Component
public class ServerLogInterceptorHandler implements HandlerInterceptor {

    public static final String REQUEST_PARAMS = "request_params";

    public static final String RESPONSE_RESULT = "response_result";

    private static final String REQUEST_START_TIME = "request_start_time";

    private static final String APPLICATION_JSON = "application/json";

    @Autowired
    PermissionService permissionService;

    @Autowired
    OperateLogService operateLogService;

    @Autowired
    LogBackCoreParameters logBackCoreParameters;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("---------------鉴权接口接入---------------");
        log.info(">> 执行请求前操作");
        log.info("Request请求前，当前系统时间记录Attribute[{}]中", REQUEST_START_TIME);
        request.setAttribute(REQUEST_START_TIME, System.currentTimeMillis());
        if (ServletFileUpload.isMultipartContent(request)) {
            // 上传文件的情况下，无需记录入参到 Attribute 中
            return true;
        }
        log.info("Request请求前，Json入参记录Attribute[{}]中", REQUEST_PARAMS);
        request.setAttribute(REQUEST_PARAMS, parseJsonParams(request));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception exception)
            throws Exception {
        log.info(">> 执行请求完成后操作");
        Permission permissionInfo = getPermission(request);
        User userInfo = ShiroUtil.getLoginUserInfo();
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
        operateLog.setResponseParams(String.valueOf(request.getAttribute(RESPONSE_RESULT)));
        // 计算耗时
        long startTime = (long) request.getAttribute(REQUEST_START_TIME);
        long endTime = System.currentTimeMillis();
        long interval = endTime - startTime;
        operateLog.setConsumingTime(interval);
        operateLogService.insertOperateLog(operateLog);
        log.info("Request请求结束，记录请求和响应信息日志\n" +
                        "接口地址: {}\n请求类型: {}\n接口入参: {}\n" +
                        "接口描述: {}\n客户端IP: {}\n响应内容类型: {}\n" +
                        "响应参数: {}\n耗时：{}ms",
                operateLog.getUrl(), operateLog.getRequestType(), operateLog.getRequestParams(),
                operateLog.getDescription(), operateLog.getClientIp(), operateLog.getResponseContentType(),
                operateLog.getResponseParams(), operateLog.getConsumingTime());
    }

    /**
     * 获取权限信息
     *
     * @param request HttpServlet请求
     * @return 权限信息，如果没有符合的信息，则会返回 new Permission();
     */
    private Permission getPermission(HttpServletRequest request) {
        Permission permissionInfo = null;
        for (RecordSpecificPathInfo pathInfo : logBackCoreParameters.getRecordSpecificPathList()) {
            if (!pathInfo.getPath().equals(request.getRequestURI())) {
                continue;
            }
            permissionInfo = new Permission();
            permissionInfo.setUrl(pathInfo.getPath());
            permissionInfo.setDescription(pathInfo.getDescription());
            return permissionInfo;
        }
        if (null == permissionInfo) {
            permissionInfo = permissionService.findPermissionSimpleInfoByUrl(request.getRequestURI());
        }
        return null == permissionInfo ? new Permission() : permissionInfo;
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
     * Request 包装器对象
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
