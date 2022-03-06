package com.quick.base.controller;

import com.quick.common.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Api(tags = "测试模块")
public class TestController {

    @Value("${log.file.path}")
    private String logFilePath;

    @GetMapping("/test")
    @ApiOperation(value = "测试接口")
    public Result testPage() {
        log.info("访问测试接口");
        return Result.ok(111);
    }

}
