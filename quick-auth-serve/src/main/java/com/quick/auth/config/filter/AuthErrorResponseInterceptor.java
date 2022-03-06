package com.quick.auth.config.filter;

import com.alibaba.fastjson.JSONObject;
import com.quick.common.vo.Result;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * 该拦截器的作用是为了处理 未登录、权限不足、踢出登录的响应信息 进行返回响应结果
 * 对如下接口进行拦截：
 * /tourist/noLogin  （未登录/凭证失效）
 * /tourist/noAuth   （权限不足）
 * /tourist/kickout  （踢出登录）
 */
public class AuthErrorResponseInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String url = request.getRequestURI();
        url = url.split(";")[0];
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");

        Result result = null;
        switch (url) {
            case "/tourist/noLogin":
                result = Result.build("登录凭证失效");
                break;
            case "/tourist/noAuth":
                result = Result.build("账户权限不足");
                break;
            case "/tourist/kickout":
                result = Result.build("您已被踢出");
                break;
        }
        JSONObject jsonObject = (JSONObject) JSONObject.toJSON(result);
        PrintWriter out = response.getWriter();
        out.append(jsonObject.toJSONString());
        return false;
    }
}
