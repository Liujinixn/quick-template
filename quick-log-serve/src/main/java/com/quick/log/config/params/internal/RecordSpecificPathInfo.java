package com.quick.log.config.params.internal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 特定的接口信息
 *
 * @author Liujinxin
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecordSpecificPathInfo {

    /**
     * 路径地址
     */
    private String path;

    /**
     * 路径描述
     */
    private String description;
}

