package com.easycloud.config;

import com.easycloud.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * <p>
 * 防止未处理的异常泄露内部堆栈信息、数据库结构等敏感数据。
 * 所有异常统一返回通用错误格式，不暴露内部实现细节。
 *
 * @author EasyCloud
 * @since 1.0.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理运行时异常（业务逻辑异常）
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result<?> handleRuntimeException(RuntimeException e) {
        log.warn("业务异常: {}", e.getMessage());
        return Result.fail(e.getMessage() != null ? e.getMessage() : "操作失败");
    }

    /**
     * 处理类型转换异常（ClassCastException, NumberFormatException）
     */
    @ExceptionHandler({ClassCastException.class, NumberFormatException.class, IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleTypeException(Exception e) {
        log.warn("参数类型错误: {}", e.getMessage());
        return Result.fail("请求参数格式不正确");
    }

    /**
     * 处理空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleNullPointerException(NullPointerException e) {
        log.warn("空指针异常: {}", e.getMessage());
        return Result.fail("请求参数不完整");
    }

    /**
     * 处理所有其他未捕获的异常（兜底）
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<?> handleException(Exception e) {
        log.error("系统异常: {}", e.getMessage(), e);
        return Result.fail("服务器内部错误，请稍后重试");
    }
}
