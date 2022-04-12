package com.quick.log.mapper;

import com.quick.log.entity.OperateLog;

public interface OperateLogMapper {

    /**
     * 插入日志信息
     *
     * @param operateLog 日志信息
     * @return 影响行数
     */
    int insertOperateLog(OperateLog operateLog);
}
