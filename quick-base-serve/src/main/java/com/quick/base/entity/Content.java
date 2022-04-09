package com.quick.base.entity;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

@Data
public class Content {

    @NotEmpty(message = "cId 不能为空")
    private String cId;
}
