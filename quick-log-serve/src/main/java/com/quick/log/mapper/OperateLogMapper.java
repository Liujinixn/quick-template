package com.quick.log.mapper;

import com.quick.log.dto.OperateLogDto;
import com.quick.log.entity.OperateLog;

import java.util.List;

public interface OperateLogMapper {

    /**
     * 查询操作日志列表
     *
     * @param operateLogDto 查询参数
     * @return 日志数据列表
     */
    List<OperateLog> findList(OperateLogDto operateLogDto);

    /**
     * 插入日志信息
     *
     * @param operateLog 日志信息
     * @return 影响行数
     */
    int insertOperateLog(OperateLog operateLog);
}
