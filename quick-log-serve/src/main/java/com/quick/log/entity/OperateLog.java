package com.quick.log.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OperateLog {

    @ApiModelProperty(value = "id标识")
    private Integer id;

    @ApiModelProperty(value = "操作日志ID")
    private String operateLogId;

    @ApiModelProperty(value = "请求路径")
    private String url;

    @ApiModelProperty(value = "请求描述")
    private String description;

    @ApiModelProperty(value = "请求类型")
    private String requestType;

    @ApiModelProperty(value = "request内容类型")
    private String requestContentType;

    @ApiModelProperty(value = "response内容类型")
    private String responseContentType;

    @ApiModelProperty(value = "客户端IP（访问IP）")
    private String clientIp;

    @ApiModelProperty(value = "用户代理")
    private String userAgent;

    @ApiModelProperty(value = "入参内容")
    private String requestParams;

    @ApiModelProperty(value = "出参内容")
    private String responseParams;

    @ApiModelProperty(value = "耗时ms")
    private Long consumingTime;

    @ApiModelProperty(value = "操作人名称")
    private String operatingAccount;

    @ApiModelProperty(value = "操作时间")
    private Date createTime;

}
