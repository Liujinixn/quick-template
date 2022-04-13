package com.quick.log.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.quick.common.utils.uuid.UUIDUtil;
import com.quick.log.dto.OperateLogDto;
import com.quick.log.entity.OperateLog;
import com.quick.log.mapper.OperateLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OperateLogServiceImpl implements OperateLogService {

    @Autowired
    OperateLogMapper operateLogMapper;

    @Override
    public PageInfo<OperateLog> findList(OperateLogDto operateLogDto) {
        PageHelper.startPage(operateLogDto.getPage(), operateLogDto.getLimit());
        List<OperateLog> dataList = operateLogMapper.findList(operateLogDto);
        return new PageInfo<>(dataList);
    }

    @Override
    public int insertOperateLog(OperateLog operateLog) {
        operateLog.setOperateLogId(UUIDUtil.getUniqueIdByUUId());
        return operateLogMapper.insertOperateLog(operateLog);
    }
}
