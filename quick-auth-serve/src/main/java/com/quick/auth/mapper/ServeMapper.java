package com.quick.auth.mapper;

import com.quick.auth.entity.Serve;

public interface ServeMapper {

    /**
     * 根据服务名称 查询服务信息
     *
     * @param serveName 服务名称
     * @return 服务信息
     */
    Serve findServeInfoByServeName(String serveName);
}
