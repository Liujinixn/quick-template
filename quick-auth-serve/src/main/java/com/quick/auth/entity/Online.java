package com.quick.auth.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 在线情况
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Online extends PublicTime {

    /**
     * 在线状态
     */
    @ApiModelProperty(value = "在线状态[0不在线、1在线]")
    private int onlineStatus;

    /**
     * 在线人数
     */
    @ApiModelProperty(value = "在线数量")
    private int onlineQuantity;

    /**
     * 有参方法 在线数量默认为0
     *
     * @param onlineStatus 在线状态
     */
    public Online(int onlineStatus) {
        this.onlineStatus = onlineStatus;
        this.onlineQuantity = 0;
    }

    /**
     * 设置在线状态和在线人数（在线人数默认为0，即不在线）
     *
     * @param onlineStatus 在线状态
     */
    public void setOnlineStatusAndOnlineQuantity(int onlineStatus) {
        this.onlineStatus = onlineStatus;
        this.onlineQuantity = 0;
    }

    /**
     * 设置在线状态和在线人数
     *
     * @param onlineStatus   在线状态
     * @param onlineQuantity 在线数量
     */
    public void setOnlineStatusAndOnlineQuantity(int onlineStatus, int onlineQuantity) {
        this.onlineStatus = onlineStatus;
        this.onlineQuantity = onlineQuantity;
    }
}
