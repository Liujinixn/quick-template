package com.quick.auth.shiro;

import com.alibaba.druid.util.StringUtils;
import com.quick.auth.config.params.ShiroCoreParameters;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.Serializable;

/**
 * 自定义session管理器
 *
 * @author Liujinxin
 */
public class CustomSessionManager extends DefaultWebSessionManager {

    @Autowired
    ShiroCoreParameters shiroCoreParameters;

    /**
     * 头信息中具有sessionid
     * 请求头：Authorization： sessionid
     * 指定sessionId的获取方式
     */
    @Override
    protected Serializable getSessionId(ServletRequest request, ServletResponse response) {
        // 获取请求头Authorization中的数据
        String id = WebUtils.toHttp(request).getHeader(shiroCoreParameters.getTokenKey());
        if (StringUtils.isEmpty(id)) {
            // 如果没有携带，生成新的sessionId
            return super.getSessionId(request, response);
        } else {
            // 请求头信息：bearer sessionid
            id = id.replaceAll(shiroCoreParameters.getTokenValuePrefix(), "");
            // 返回sessionId
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_SOURCE, "header");
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID, id);
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID, Boolean.TRUE);
            return id;
        }
    }
}
