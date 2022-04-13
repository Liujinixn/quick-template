package com.quick.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageDto {

    @ApiModelProperty(value = "页码", example = "1")
    @Range(min = 1, max = 1000000000, message = "页码 可选值【{min}~{max}】")
    private int page = 1;

    @ApiModelProperty(value = "页长", example = "10")
    @Range(min = 1, max = 10000, message = "页长 可选值【{min}~{max}】")
    private int limit = 10;

}
