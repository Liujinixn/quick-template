package com.quick.base.controller;

import com.quick.common.utils.file.WordPdfUtil;
import com.quick.common.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ResponseHeader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@Api(tags = "测试模块")
public class TestController {

    @GetMapping("/test")
    @ApiOperation(value = "测试接口")
    public Result testPage() {
        log.info("访问测试接口");
        return Result.ok(111);
    }

    @RequestMapping(value = "/fileExp",method = RequestMethod.GET)
    @ApiOperation(value = "wenjian")
    public void fileExp(HttpServletResponse response) throws Exception {
        WordPdfUtil.setResponseInfo(response,"打印.pdf");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", "司马徽");
        params.put("age", "30");
        params.put("sex", "男");
        WordPdfUtil.wordTemplateGeneratePdf(response.getOutputStream(), params, "test.docx");
    }

}
