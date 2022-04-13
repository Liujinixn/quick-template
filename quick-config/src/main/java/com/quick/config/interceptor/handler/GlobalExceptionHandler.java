package com.quick.config.interceptor.handler;

import com.quick.common.utils.constant.CoreConst;
import com.quick.common.vo.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 系统 运行时异常处理器
 *
 * @author Liujinxin
 */
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private static final String LOG_EXCEPTION_FORMAT = "Capture Exception By GlobalExceptionHandler: Code: %s Detail: %s";

    /**
     * 运行时异常 处理器
     */
    @ExceptionHandler(value = RuntimeException.class)
    public Result resolveException(HttpServletRequest request, Exception ex) {
        log(CoreConst.FAIL_CODE, ex);
        return Result.build(CoreConst.FAIL_CODE, ex.getMessage());
    }

    /**
     * 记录日志
     */
    private <T extends Throwable> void log(Integer status, T exception) {
        exception.printStackTrace();
        log.error(String.format(LOG_EXCEPTION_FORMAT, status, exception.getMessage()));
    }
}
