package com.quick.base.controller;

import com.quick.auth.service.UserService;
import com.quick.base.entity.FindDto;
import com.quick.common.utils.redis.RedisClient;
import com.quick.common.utils.validator.BeanValidatorUtil;
import com.quick.common.vo.Result;
import com.quick.file.enumerate.FileSuffixTypeEnum;
import com.quick.file.service.FileStoreService;
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
import javax.validation.Valid;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.spec.InvalidParameterSpecException;

@Slf4j
@RestController
@Api(tags = "测试模块")
@RequestMapping("/${request.prefix.base_server}/test")
public class TestController {

    @Autowired
    UserService userService;

    @Autowired
    RedisClient redisClient;

    @Autowired
    FileStoreService ossClientService;

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
    @ApiOperation(value = "测试接口-上传文件到oss")
    public String uploadFile(MultipartFile file) throws IOException {
        byte[] bytes = file.getBytes();
        String type = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        FileSuffixTypeEnum typeEnumBySuffix = FileSuffixTypeEnum.getTypeEnumBySuffix(type);
        String fileName = ossClientService.uploadByByte(bytes, typeEnumBySuffix);
        return fileName;
    }

    @PostMapping("/getAccessUrl")
    @ApiOperation(value = "测试接口-临时访问地址oss")
    public String getAccessUrl(String fileId) throws IOException {
        String accessUrl = ossClientService.getAccessUrl(fileId);
        return accessUrl;
    }

    @PostMapping("/getAccessUrlPermanent")
    @ApiOperation(value = "测试接口-访问地址oss")
    public String getAccessUrlPermanent(String fileId) throws IOException {
        String accessUrl = ossClientService.getAccessUrlPermanent(fileId);
        return accessUrl;
    }

    @GetMapping("/downloadStream")
    @ApiOperation(value = "测试接口-下载文件oss")
    public ResponseEntity<byte[]> downloadStream(String fileId) {
        byte[] bytes = null;
        HttpHeaders headers = new HttpHeaders();
        try {
            headers.setContentDispositionFormData("attachment",
                    new String(fileId.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1));
            headers.add("Access-Control-Expose-Headers", "Content-Disposition");
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            bytes = ossClientService.downloadStream(fileId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

}
