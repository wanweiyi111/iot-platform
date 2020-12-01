package com.hzyw.iot.platform.devicemanager.domain.comm;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import lombok.Data;

/**
 * ResultVO
 *
 * @blame IOT Team
 */
@Data
public class ResultVO<T> {
    static final int SUCCESS = 0;
    static final int FAILED = 1;
    private Integer code; //状态码 0:成功, 1:失败
    private String msg; //返回信息
    private T data; //返回数据
    private PageBean page; //分页信息
    private PageInfo pageInfo;
    public ResultVO(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ResultVO(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public ResultVO(Integer code, String msg, PageBean page, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.page = page;
    }

    public ResultVO(Integer code, String msg, PageInfo pageInfo) {
        this.code = code;
        this.msg = msg;
        this.pageInfo = pageInfo;
    }
    /**
     * 构建成功返回
     *
     * @param msg 返回信息
     * @return ResultVO
     */
    public static ResultVO success(String msg) {
        return new ResultVO(0, msg);
    }

    /**
     * 构建成功返回
     *
     * @param data 返回数据
     * @param <T>
     * @return
     */
    public static <T> ResultVO<T> success(T data) {
        return new ResultVO(0, "success", data);
    }

    /**
     * 请求成功
     *
     * @param page 分页信息
     * @param data
     * @param <T>
     * @return
     */
    public static <T> ResultVO<T> pageSuccess(PageBean page, T data) {
        return new ResultVO(0, "success", page, data);
    }

    /**
     * 请求成功
     *
     * @param pageInfo 分页信息
     * @param <T>
     * @return
     */
    public static <T> ResultVO<T> pageSuccess(PageInfo pageInfo) {
        return new ResultVO(0, "success", pageInfo);
    }
    /**
     * 构建失败返回
     *
     * @param msg 返回信息
     * @return ResultVO
     */
    public static ResultVO failed(String msg) {
        return new ResultVO(1, msg);
    }


    /**
     * 构建失败返回
     *
     * @param msg  返回信息
     * @param data 返回数据
     * @param <T>  类型
     * @return ResultVO
     */
    public static <T> ResultVO<T> failed(String msg, T data) {
        return new ResultVO(1, msg, data);
    }
}