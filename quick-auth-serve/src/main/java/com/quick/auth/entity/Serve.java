package com.quick.auth.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Serve extends PublicTime implements Serializable {

    private static final long serialVersionUID = 8981844145479424700L;

    /**
     * 服务ID
     */
    @ApiModelProperty(value = "服务ID")
    private String serveId;

    /**
     * 服务名称
     */
    @ApiModelProperty(value = "服务名称")
    private String serveName;

    /**
     * 服务密钥
     */
    @ApiModelProperty(value = "服务密钥")
    private String accessKey;

}
