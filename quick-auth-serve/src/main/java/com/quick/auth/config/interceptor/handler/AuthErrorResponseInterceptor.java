package com.quick.auth.config.interceptor.handler;

import com.alibaba.fastjson.JSON;
import com.quick.auth.config.params.RequestPrefixAuthParams;
import com.quick.auth.config.params.ShiroCoreParameters;
import com.quick.common.vo.Result;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 该拦截器的作用是为了处理 未登录、权限不足、踢出登录的响应信息 进行返回响应结果
 * 对如下接口进行拦截：
 * /tourist/noLogin  （未登录/凭证失效）
 * /tourist/noAuth   （权限不足）
 * /tourist/kickout  （踢出登录）
 *
 * @author Liujinxin
 */
public class AuthErrorResponseInterceptor implements HandlerInterceptor {

    @Autowired
    RequestPrefixAuthParams requestPrefixAuthParams;

    @Autowired
    ShiroCoreParameters shiroCoreParameters;

    Map<String, String> pathAndDescMap = new HashMap<>(8);

    @PostConstruct
    public void init() {
        pathAndDescMap.put(shiroCoreParameters.getNoLogin(), "登录凭证失效");
        pathAndDescMap.put(shiroCoreParameters.getNoAuth(), "账户权限不足");
        pathAndDescMap.put(shiroCoreParameters.getKickOut(), "您已被踢出");
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getServletPath();
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        response.getWriter().append(JSON.toJSONString(Result.build(MapUtils.getString(pathAndDescMap, path))));
        return false;
    }
}
