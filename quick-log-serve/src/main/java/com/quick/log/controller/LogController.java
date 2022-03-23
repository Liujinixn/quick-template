package com.quick.log.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 日志管理
 */
@Slf4j
@RestController
@Api(tags = "日志管理")
@RequestMapping("${request.prefix.log_server}/log")
public class LogController {
}
