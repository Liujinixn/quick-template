package com.quick.common.vo;

import com.quick.common.utils.constant.CoreConst;
import io.swagger.annotations.ApiModelProperty;

/**
 * 自定义响应结果集
 *
 * @param <T> data数据类型
 * @author Liujinxin
 */
public class Result<T> {

    @ApiModelProperty(value = "状态码")
    private int status;

    @ApiModelProperty(value = "错误信息")
    private String msg;

    @ApiModelProperty(value = "数据内容")
    private T data;

    public Result() {
    }

    public Result(int status, String msg, T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public static <T> Result<T> ok(T data) {
        return new Result<T>(CoreConst.SUCCESS_CODE, "ok", data);
    }

    public static <T> Result<T> build(String msg) {
        return new Result<T>(CoreConst.FAIL_CODE, msg, null);
    }

    public static <T> Result<T> build(int status, String msg) {
        return new Result<T>(status, msg, null);
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
