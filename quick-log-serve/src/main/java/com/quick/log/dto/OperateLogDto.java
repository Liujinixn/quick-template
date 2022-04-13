package com.quick.log.dto;

import com.quick.common.dto.PageDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OperateLogDto extends PageDto {

    @ApiModelProperty(value = "请求路径（模糊）")
    @Length(max = 20, message = "请求路径 最大长度为{max}")
    private String url;

    @ApiModelProperty(value = "请求描述（模糊）")
    @Length(max = 20, message = "请求描述 最大长度为{max}")
    private String description;

    @ApiModelProperty(value = "操作人名称（模糊）")
    @Length(max = 20, message = "操作人名称 最大长度为{max}")
    private String operatingAccount;

    @ApiModelProperty(value = "请求类型")
    @Length(max = 20, message = "请求类型 最大长度为{max}")
    private String requestType;

    @ApiModelProperty(value = "客户端IP（访问IP）")
    @Length(max = 20, message = "客户端IP 最大长度为{max}")
    private String clientIp;

    @ApiModelProperty(value = "操作时间，格式yyyy-MM-dd")
    private String createTime;

}
