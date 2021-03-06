package com.quick.auth.shiro.filter;

import com.alibaba.fastjson.JSON;
import com.quick.auth.entity.User;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.DefaultSessionKey;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.Map;

/**
 * Shiro踢出会话控制过滤器
 *
 * @author Liujinxin
 */
public class KickoutSessionControlFilter extends AccessControlFilter {

    /**
     * Shiro 内置session队列 踢出列表key
     */
    private final String SESSION_MANAGER_QUEUE_KICK_OUT_KEY = "kickout";

    /**
     * 在线用户Redis储存前缀
     */
    public String onlineUser = "online_user";

    /**
     * 踢出后到的地址
     */
    private String kickoutUrl;

    /**
     * 踢出之前登录的（false顶掉当前登录的）/之后登录的用户（true顶掉之前的） 默认踢出之前登录的用户
     */
    private boolean kickoutAfter = false;

    /**
     * 同一个帐号最大会话数 默认5
     */
    private int maxSession = 5;

    private SessionManager sessionManager;

    private Cache<String, LinkedList<Serializable>> cache;

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        return false;
    }

    public LinkedList<Serializable> getChcheOnlineUser(String username) {
        LinkedList<Serializable> deque = cache.get(username);
        return deque;
    }

    @Override
    @SuppressWarnings("all")
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        Subject subject = getSubject(request, response);
        if (!subject.isAuthenticated() && !subject.isRemembered()) {
            //  如果没有登录，直接进行之后的流程
            return true;
        }

        Session session = subject.getSession();
        User user = (User) subject.getPrincipal();
        String username = user.getUsername();
        Serializable sessionId = session.getId();

        //  读取缓存   没有就存入
        LinkedList<Serializable> deque = getChcheOnlineUser(username);
        //  如果此用户没有session队列，也就是还没有登录过，缓存中没有
        //  就new一个空队列，不然deque对象为空，会报空指针
        if (deque == null) {
            deque = new LinkedList<>();
        }

        // 如果队列里没有此sessionId，且用户没有被踢出；放入队列
        if (!deque.contains(sessionId) && session.getAttribute(SESSION_MANAGER_QUEUE_KICK_OUT_KEY) == null) {
            // 将sessionId存入队列
            deque.add(sessionId);
            // 将用户的sessionId队列缓存
            cache.put(username, deque);
        }

        // 如果队列里的sessionId数超出最大会话数，开始踢人
        while (deque.size() > maxSession) {
            Serializable kickoutSessionId = null;
            // 如果踢出后者
            if (kickoutAfter) {
                kickoutSessionId = deque.removeFirst();
                // 踢出后再更新下缓存队列
                cache.put(username, deque);
            } else { // 否则踢出前者
                kickoutSessionId = deque.removeLast();
                // 踢出后再更新下缓存队列
                cache.put(username, deque);
            }

            try {
                // 获取被踢出的sessionId的session对象
                Session kickoutSession = sessionManager.getSession(new DefaultSessionKey(kickoutSessionId));
                if (kickoutSession != null) {
                    // 设置会话的kickout属性表示踢出了
                    kickoutSession.setAttribute(SESSION_MANAGER_QUEUE_KICK_OUT_KEY, true);
                }
            } catch (Exception e) {
                // ignore exception
            }
        }

        // 如果被踢出了，直接退出，重定向到踢出后的地址
        if (session.getAttribute(SESSION_MANAGER_QUEUE_KICK_OUT_KEY) != null && (Boolean) session.getAttribute(SESSION_MANAGER_QUEUE_KICK_OUT_KEY) == true) {
            // 会话被踢出了
            try {
                // 退出登录
                subject.logout();
            } catch (Exception e) {
                // ignore
            }
            saveRequest(request);

			/*
			// 判断是不是Ajax请求
            Map<String, String> resultMap = new HashMap<String, String>(2);
			if ("XMLHttpRequest".equalsIgnoreCase(((HttpServletRequest) request).getHeader("X-Requested-With"))) {
				resultMap.put("user_status", "300");
				resultMap.put("message", "您已经在其他地方登录，请重新登录！");
				// 输出json串
				out(response, resultMap);
			}else{
				// 重定向
				WebUtils.issueRedirect(request, response, kickoutUrl);
			}
			*/

            // 转发
            // request.getRequestDispatcher(kickoutUrl).forward(request, response);

            //  重定向
            WebUtils.issueRedirect(request, response, kickoutUrl);
            return false;
        }
        return true;
    }

    private void out(ServletResponse hresponse, Map<String, String> resultMap) {
        try {
            hresponse.setCharacterEncoding("UTF-8");
            PrintWriter out = hresponse.getWriter();
            out.println(JSON.toJSONString(resultMap));
            out.flush();
            out.close();
        } catch (Exception e) {
            System.err.println("KickoutSessionFilter.class 输出JSON异常，可以忽略。");
        }
    }

    public void setKickoutUrl(String kickoutUrl) {
        this.kickoutUrl = kickoutUrl;
    }

    public String getOnlineUser() {
        return onlineUser;
    }

    public void setOnlineUser(String onlineUser) {
        this.onlineUser = onlineUser;
    }

    public void setKickoutAfter(boolean kickoutAfter) {
        this.kickoutAfter = kickoutAfter;
    }

    public void setMaxSession(int maxSession) {
        this.maxSession = maxSession;
    }

    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public void setCacheManager(CacheManager cacheManager) {
        this.cache = cacheManager.getCache(onlineUser);
    }

}
