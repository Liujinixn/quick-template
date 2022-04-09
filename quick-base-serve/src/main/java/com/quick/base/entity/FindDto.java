package com.quick.base.entity;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;

@Data
public class FindDto {

    @NotEmpty(message = "id 不能为空")
    private String id;

    @NotEmpty(message = "name 不能为空")
    private String name;

    @Valid
    private Content content;

}
