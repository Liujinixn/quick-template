package com.quick.log.controller;

import com.quick.common.utils.ip.IpUtil;
import com.quick.common.vo.Result;
import com.quick.log.config.params.LogBackCoreParameters;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.nio.charset.StandardCharsets;

@Slf4j
@RestController
@Api(tags = "Logback API")
@RequestMapping("${request.prefix.log_server}/logback")
public class LogbackController {

    private final int CURRENT_LIMIT_TIME = 10000;

    @Autowired
    LogBackCoreParameters logBackCoreParameters;

    /**
     * 查询本地日志信息
     *
     * @param type     日志类型：log、html
     * @param dateTime 查询的日期 格式：yyyy-MM-dd
     * @param level    日志级别：error、warn、info、debug
     */
    @GetMapping("/{type}/{dateTime}")
    @ApiOperation(value = "查询日志信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "日志类型【txt、html】", dataType = "String", example = "html", paramType = "path"),
            @ApiImplicitParam(name = "dateTime", value = "查询指定日期的日志信息（格式：yyyy-MM-dd）", dataType = "String", example = "2022-03-05", paramType = "path"),
            @ApiImplicitParam(name = "level", value = "txt类型时选择查询的日志级别【error、warn、info、debug】", dataType = "String", example = "info", paramType = "query"),
            @ApiImplicitParam(name = "accessKey", value = "密钥", dataType = "String", example = "info", paramType = "query")
    })
    public Object logList(@PathVariable("type") String type,
                          @PathVariable("dateTime") String dateTime,
                          String level,
                          String accessKey,
                          HttpServletRequest request) {
        // 检查该接口是否禁用
        if (!logBackCoreParameters.isEnable()) {
            return "interface is disabled.";
        }

        String fileName = "html".equalsIgnoreCase(type) ?
                ("logback-" + dateTime + "." + type) : ("logback-" + level + "-" + dateTime + "." + ("txt".equalsIgnoreCase(type) ? "log" : type));

        // API安全控制
        String ipAddress = IpUtil.getIpAddr(request);
        log.info("客户端 {} 访问服务日志 file:{}", ipAddress, fileName);
        // 简易限流
        HttpSession session = request.getSession();
        Long time = (Long) session.getAttribute(ipAddress);
        if (null != time) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - time < CURRENT_LIMIT_TIME) {
                // 10秒内 不能重复 访问， IO流操作资源消耗较大
                log.warn("{}秒内不能重复查询日志", CURRENT_LIMIT_TIME / 1000);
                return "operation interval is too fast.";
            }
        }
        session.setAttribute(ipAddress, System.currentTimeMillis());
        session.setMaxInactiveInterval(2000);
        // 简易验证密钥
        if (!logBackCoreParameters.getLogAccessKeyList().contains(accessKey)) {
            log.info("错误的密钥，access_key:{}", accessKey);
            return "access_key not exists.";
        }

        File file = new File(logBackCoreParameters.getPath() + "/" + fileName);
        if (!file.exists()) {
            // 查询的日志文件不存在
            return Result.build("服务日志文件不存在: fileName=" + file.getName());
        }
        StringBuilder result = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
            String rowContent = null;
            while ((rowContent = br.readLine()) != null) {
                result.append(System.lineSeparator() + rowContent);
            }
            br.close();
        } catch (IOException e) {
            log.error("查询服务日志文件失败", e);
            return Result.build("查询服务日志文件失败: " + e);
        }
        return result.toString();
    }

}
