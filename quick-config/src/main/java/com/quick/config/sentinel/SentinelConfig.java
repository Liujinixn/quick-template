package com.quick.config.sentinel;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * 接口流控配置
 *
 * @author Liujinxin
 */
@Configuration
@ConditionalOnProperty(prefix = "sentinel", name = "enable", havingValue = "true", matchIfMissing = true)
class SentinelConfig {

    @Autowired
    WebApplicationContext context;

    /**
     * 配置sentinel阈值类型QPS的单机阈值（即1秒，用户最多访问次数）
     */
    @Value("${sentinel.flowGrade.qps.count:15}")
    private double sentinelFlowGradeQpsCount;

    /**
     * 将控制规则载入到 Sentinel
     */
    @PostConstruct
    private void initRules() {
        List<FlowRule> flowRuleList = getAllControllerApiAddress();
        FlowRuleManager.loadRules(flowRuleList);
    }

    /**
     * 获取 Sentinel 控制规则列表（根据系统所有的controller接口创建控制规则）
     *
     * 该方法中 apiInfoList 集合，记录系统中 的接口信息（可通过该信息生成接口文档等功能）
     * apiInfoList 格式： [
     *                      {
     *                          "url": "/role/list",
     *                          "name": "role_list",
     *                          "type": "GET",
     *                      },
     *                      {
     *                          "url": "/role/add",
     *                          "name": "role_add",
     *                          "type": "POST",
     *                      }
     *                    ]
     *
     * @return sentinel 控制规则列表
     */
    private List<FlowRule> getAllControllerApiAddress() {
        List<FlowRule> flowRuleList = new ArrayList<>();
        RequestMappingHandlerMapping mapping = context.getBean(RequestMappingHandlerMapping.class);
        Map<RequestMappingInfo, HandlerMethod> map = mapping.getHandlerMethods();
        List<Map<String, String>> apiInfoList = new ArrayList<>();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : map.entrySet()) {
            Map<String, String> initMap = new HashMap<>(3);
            RequestMappingInfo info = entry.getKey();
            // 请求类型
            RequestMethodsRequestCondition methodsCondition = info.getMethodsCondition();
            // 请求url
            PatternsRequestCondition pattern = info.getPatternsCondition();
            // 如果类型不为空则获取
            Set<RequestMethod> methods = methodsCondition.getMethods();
            if (ObjectUtils.isEmpty(pattern) || CollectionUtils.isEmpty(pattern.getPatterns()) || CollectionUtils.isEmpty(methods)) {
                continue;
            }
            Set<String> patterns = pattern.getPatterns();
            // 请求url
            for (String url : patterns) {
                url = urlHandle(url);
                flowRuleList.add(createFlowRuleInfo(url));
                initMap.put("url", url);
                initMap.put("name", url.replaceAll("/", "_").substring(1));
            }
            for (RequestMethod requestMethod : methods) {
                initMap.put("type", requestMethod.toString());
            }
            apiInfoList.add(initMap);
        }
        return flowRuleList;
    }

    /**
     * 创建 sentinel 控制规则对象
     *
     * @param url 资源名称 （对象接口地址）
     * @return FlowRule 控制规则
     */
    private FlowRule createFlowRuleInfo(String url) {
        FlowRule rule = new FlowRule();
        rule.setResource(url);
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        // 每秒调用最大次数
        rule.setCount(sentinelFlowGradeQpsCount);
        return rule;
    }

    /**
     * url路径处理
     *
     * @param url 目标url
     * @return 处理后的url 格式会按照以 / 开始
     */
    private String urlHandle(String url) {
        if (url.contains("//")) {
            // 当前接口出现 “//**//**” 或 “/**//**” 或 “//**/**” 结构
            return url.replace("//", "/");
        }
        if (!url.startsWith("/")) {
            //当前接口出现 "**/**" 结构
            return "/" + url;
        }
        return url;
    }
}
