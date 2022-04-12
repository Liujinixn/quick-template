package com.quick.log.service;

import com.quick.common.utils.uuid.UUIDUtil;
import com.quick.log.entity.OperateLog;
import com.quick.log.mapper.OperateLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OperateLogServiceImpl implements OperateLogService {

    @Autowired
    OperateLogMapper operateLogMapper;

    @Override
    public int insertOperateLog(OperateLog operateLog) {
        operateLog.setOperateLogId(UUIDUtil.getUniqueIdByUUId());
        return operateLogMapper.insertOperateLog(operateLog);
    }
}
