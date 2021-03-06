package com.quick.base.controller;

import com.quick.auth.service.UserService;
import com.quick.base.annotation.AopMethod;
import com.quick.base.entity.FindDto;
import com.quick.common.utils.validator.BeanValidatorUtil;
import com.quick.common.vo.Result;
import com.quick.file.enumerate.FileSuffixTypeEnum;
import com.quick.file.service.FileStoreHandle;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.spec.InvalidParameterSpecException;
import java.util.List;

@Slf4j
@RestController
@Api(tags = "测试模块")
@RequestMapping("${request.prefix.base_server}/test")
public class TestController {

    @Autowired
    UserService userService;

    @Autowired
    FileStoreHandle fileStoreHandle;

    /**
     * 测试 日志记录
     */
    @GetMapping("/test")
    @ApiOperation(value = "测试接口")
    public Result test() {
        log.info("访问测试接口");
        return Result.ok(true);
    }

    /**
     * 测试 AOP切面功能
     */
    @AopMethod(value = "aop测试")
    @GetMapping("/testAop")
    @ApiOperation(value = "测试AOP切面功能接口")
    public Result testAop() {
        log.info("测试AOP切面功能");
        return Result.ok(true);
    }

    /**
     * 测试 BeanValidatorUtil 验证入参工具类
     * 全局配置了 Validator 验证属性不符合规定得异常处理器 com.quick.config.exceptionHandler.ValidationExceptionHandler
     *
     * @param findDto 内部属性对象 直接使用 @Valid 标识
     */
    @PostMapping("/testBeanValidatorUtil")
    @ApiOperation(value = "测试接口-testBeanValidatorUtil验证参数工具")
    public Result testBeanValidatorUtil(@RequestBody FindDto findDto) {
        log.info("测试接口-BeanValidatorUtil验证参数工具");
        try {
            BeanValidatorUtil.check(findDto);
        } catch (InvalidParameterSpecException e) {
            return Result.ok(e.getMessage());
        }
        return Result.ok(findDto);
    }

    /**
     * 测试 Validator 验证入参
     * 全局配置了 Validator 验证属性不符合规定得异常处理器 com.quick.config.exceptionHandler.ValidationExceptionHandler
     *
     * @param findDto 内部属性对象 直接使用 @Valid 标识
     */
    @PostMapping("/testValidatorParamsAttribute")
    @ApiOperation(value = "测试接口-Validator验证参数")
    public Result testValidatorParamsAttribute(@RequestBody @Valid FindDto findDto) {
        log.info("测试接口-Validator验证参数");
        return Result.ok(findDto);
    }

    /**
     * 测试 全局运行时异常处理器
     * 全局配置了 RuntimeException 异常处理器 com.quick.config.exceptionHandler.GlobalExceptionHandler
     *
     * @param findDto 内部属性对象 直接使用 @Valid 标识
     */
    @PostMapping("/testGlobalExceptionHandler")
    @ApiOperation(value = "测试接口-全局异常处理器")
    public Result testGlobalExceptionHandler(@RequestBody @Valid FindDto findDto) {
        log.info("测试接口-全局异常处理器");
        int i = 0 / 0;
        return Result.ok(findDto);
    }

    /**
     * 测试json格式入参-日志记录表
     */
    @PostMapping("/testJsonParams")
    @ApiOperation(value = "测试接口-json格式入参-日志记录表")
    public Result testJsonParams(@RequestBody FindDto findDto) {
        return Result.ok(findDto);
    }

    /**
     * 测试普通格式入参-日志记录表
     */
    @PostMapping("/testParams")
    @ApiOperation(value = "测试接口-普通格式入参-日志记录表")
    public Result testParams(FindDto findDto) {
        return Result.ok(findDto);
    }

    /**
     * 测试post无参数-日志记录表
     */
    @PostMapping("/testNoParams")
    @ApiOperation(value = "测试接口-post无参数-日志记录表")
    public Result testNoParams() {
        return Result.ok(true);
    }

    @PostMapping("/uploadFileLog")
    @ApiOperation(value = "测试接口-文件上传-日志记录表")
    public Result uploadFileLog(MultipartFile file, HttpServletRequest req) {
        return Result.ok(true);
    }

    @PostMapping("/uploadFile")
    @ApiOperation(value = "测试接口-上传文件")
    public String uploadFile(MultipartFile file) throws IOException {
        byte[] bytes = file.getBytes();
        String type = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        FileSuffixTypeEnum typeEnumBySuffix = FileSuffixTypeEnum.getTypeEnumBySuffix(type);
        String fileName = fileStoreHandle.uploadByByte(bytes, typeEnumBySuffix);
        return fileName;
    }

    @PostMapping("/getAccessUrl")
    @ApiOperation(value = "测试接口-临时访问地址")
    public String getAccessUrl(String fileId) throws IOException {
        String accessUrl = fileStoreHandle.getAccessUrl(fileId, 20L);
        return accessUrl;
    }

    @PostMapping("/getAccessUrlPermanent")
    @ApiOperation(value = "测试接口-访问地址")
    public String getAccessUrlPermanent(String fileId) throws IOException {
        String accessUrl = fileStoreHandle.getAccessUrl(fileId);
        return accessUrl;
    }

    @GetMapping("/downloadStream")
    @ApiOperation(value = "测试接口-下载文件", produces = "application/octet-stream")
    public void downloadStream(String fileId, HttpServletResponse response) throws IOException {
        byte[] bytes = null;
        try {
            String[] filePath = fileId.split("/");
            response.setContentType("application/pdf");
            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(filePath[filePath.length - 1], "UTF-8"));

            bytes = fileStoreHandle.downloadStream(fileId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        response.getOutputStream().write(bytes);
    }

    @GetMapping("/deleteFile")
    @ApiOperation(value = "测试接口-删除文件")
    public Object deleteFile(@RequestParam("fileId") List<String> fileIdList) {
        return fileStoreHandle.deleteFile(fileIdList.toArray(new String[]{}));
    }

    @GetMapping("/doesFileExist")
    @ApiOperation(value = "测试接口-检查文件是否存在")
    public Object doesFileExist(String fileId) {
        return fileStoreHandle.doesFileExist(fileId);
    }

}
