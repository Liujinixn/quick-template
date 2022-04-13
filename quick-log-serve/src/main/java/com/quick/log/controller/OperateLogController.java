package com.quick.log.controller;

import com.github.pagehelper.PageInfo;
import com.quick.common.vo.Result;
import com.quick.log.dto.OperateLogDto;
import com.quick.log.entity.OperateLog;
import com.quick.log.service.OperateLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@Api(tags = "日志管理")
@RequestMapping("${request.prefix.log_server}/operateLog")
public class OperateLogController {

    @Autowired
    OperateLogService operateLogService;

    /**
     * 查询操作日志列表
     *
     * @param operateLogDto 查询参数
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询操作日志列表")
    public Result<PageInfo<OperateLog>> findList(@RequestBody @Valid OperateLogDto operateLogDto) {
        if (StringUtils.isNotBlank(operateLogDto.getCreateTime())) {
            if (!operateLogDto.getCreateTime().matches("^\\d{4}-\\d{2}-\\d{2}$")) {
                return Result.build("操作时间 格式必须为yyyy-MM-dd");
            }
        }
        return Result.ok(operateLogService.findList(operateLogDto));
    }

}
