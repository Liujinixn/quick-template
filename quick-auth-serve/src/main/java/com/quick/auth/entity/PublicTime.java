package com.quick.auth.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublicTime {

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

    public void setCreateTimeAndUpdateTime(Date createTime, Date updateTime){
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

}
