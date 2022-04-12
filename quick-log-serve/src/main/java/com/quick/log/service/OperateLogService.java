package com.quick.log.service;

import com.quick.log.entity.OperateLog;

public interface OperateLogService {

    /**
     * 插入日志信息
     *
     * @param operateLog 日志信息
     * @return 影响行数
     */
    int insertOperateLog(OperateLog operateLog);

}
