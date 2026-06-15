package com.easycloud.common;

import lombok.Data;

/**
 * 统一响应结果包装类
 * <p>
 * 所有 API 接口的统一返回格式，包含状态码、消息和数据。
 * 前端/客户端根据 code 判断请求是否成功。
 * <ul>
 *   <li>code=200: 成功</li>
 *   <li>code=400: 客户端错误</li>
 *   <li>code=500: 服务端错误</li>
 * </ul>
 *
 * @param <T> 响应数据类型
 * @author EasyCloud
 * @since 1.0.0
 */
@Data
public class Result<T> {

    /** 响应状态码，200=成功，其他=失败 */
    private int code;

    /** 响应消息/错误描述 */
    private String msg;

    /** 响应数据（泛型，根据接口不同返回不同类型） */
    private T data;

    /**
     * 成功响应（无数据）
     *
     * @param <T> 数据类型
     * @return 成功的 Result 对象
     */
    public static <T> Result<T> ok() {
        return ok(null);
    }

    /**
     * 成功响应（带数据）
     *
     * @param data 响应数据
     * @param <T>  数据类型
     * @return 成功的 Result 对象
     */
    public static <T> Result<T> ok(T data) {
        Result<T> r = new Result<>();
        r.setCode(200);
        r.setMsg("success");
        r.setData(data);
        return r;
    }

    /**
     * 成功响应（自定义消息和数据）
     *
     * @param msg  响应消息
     * @param data 响应数据
     * @param <T>  数据类型
     * @return 成功的 Result 对象
     */
    public static <T> Result<T> ok(String msg, T data) {
        Result<T> r = new Result<>();
        r.setCode(200);
        r.setMsg(msg);
        r.setData(data);
        return r;
    }

    /**
     * 失败响应（默认状态码 400）
     *
     * @param msg 错误消息
     * @param <T> 数据类型
     * @return 失败的 Result 对象
     */
    public static <T> Result<T> fail(String msg) {
        return fail(400, msg);
    }

    /**
     * 失败响应（自定义状态码）
     *
     * @param code 错误状态码
     * @param msg  错误消息
     * @param <T>  数据类型
     * @return 失败的 Result 对象
     */
    public static <T> Result<T> fail(int code, String msg) {
        Result<T> r = new Result<>();
        r.setCode(code);
        r.setMsg(msg);
        return r;
    }
}
