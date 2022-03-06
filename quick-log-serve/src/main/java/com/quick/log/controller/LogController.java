package com.quick.log.controller;

import com.quick.common.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.nio.charset.StandardCharsets;

@Slf4j
@RestController
@Api(tags = "日志模块")
@RequestMapping("/logback")
public class LogController {

    @Value("${log.file.path}")
    private String logFilePath;

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
            @ApiImplicitParam(name = "level", value = "txt类型时选择查询的日志级别【error、warn、info、debug】", dataType = "String", example = "info", paramType = "query")
    })
    public Object logList(@PathVariable("type") String type,
                          @PathVariable("dateTime") String dateTime,
                          String level) {
        File file = null;
        if ("html".equalsIgnoreCase(type)) {
            file = new File(logFilePath + "/logback-" + dateTime + "." + type);
        } else {
            file = new File(logFilePath + "/logback-" + level + "-" + dateTime + "." + ("txt".equalsIgnoreCase(type) ? "log" : type));
        }

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
