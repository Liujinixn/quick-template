package com.quick.base.controller;

import com.quick.auth.vo.base.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "测试模块")
public class TestController {

    @GetMapping("/test")
    @ApiOperation(value = "测试接口")
    public Result testPage() {
        return Result.ok(111);
    }

}
