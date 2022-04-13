package com.quick.log.service;

import com.github.pagehelper.PageInfo;
import com.quick.log.dto.OperateLogDto;
import com.quick.log.entity.OperateLog;

public interface OperateLogService {

    /**
     * 查询操作日志列表
     *
     * @param operateLogDto 查询参数
     * @return PageInfo 数据列表含分页信息
     */
    PageInfo<OperateLog> findList(OperateLogDto operateLogDto);

    /**
     * 插入操作日志信息
     *
     * @param operateLog 日志信息
     * @return 影响行数
     */
    int insertOperateLog(OperateLog operateLog);
}
