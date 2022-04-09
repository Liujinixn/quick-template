package com.quick.config.exceptionHandler;


import com.quick.common.utils.constant.CoreConst;
import com.quick.common.vo.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 全局 validation入参效验异常
 */
@ControllerAdvice
@ResponseBody
public class ValidationExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ValidationExceptionHandler.class);

    private static final String logExceptionFormat = "Capture Exception By ValidationExceptionHandler: Code: %s Detail: %s";

    /**
     * 数据效验异常 处理器
     */
    @ExceptionHandler(value = {BindException.class, MethodArgumentNotValidException.class})
    public Result validationExceptionHandler(Exception ex) {
        return validationDataResultFormat(CoreConst.FAIL_CODE, ex);
    }

    private <T extends Throwable> Result validationDataResultFormat(Integer status, T exception) {
        log(status, exception);
        BindingResult bindResult = null;
        if (exception instanceof BindException) {
            bindResult = ((BindException) exception).getBindingResult();
        } else if (exception instanceof MethodArgumentNotValidException) {
            bindResult = ((MethodArgumentNotValidException) exception).getBindingResult();
        }
        String msg;
        if (bindResult != null && bindResult.hasErrors()) {
            msg = bindResult.getAllErrors().get(0).getDefaultMessage();
            if (msg.contains("NumberFormatException")) {
                msg = "参数类型错误！";
            }
        } else {
            msg = "系统繁忙，请稍后重试...";
        }
        return Result.build(status, msg);
    }

    /**
     * 记录日志
     */
    private <T extends Throwable> void log(Integer status, T exception) {
        exception.printStackTrace();
        log.error(String.format(logExceptionFormat, status, exception.getMessage()));
    }

}
