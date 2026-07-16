package com.travel.common.result;

import lombok.Data;

@Data
public class R<T> {
    private int code;
    private T data;
    private String message;

    private R() {}

    public static <T> R<T> ok(T data) {
        R<T> r = new R<>();
        r.code = ResultCode.SUCCESS.getCode();
        r.message = ResultCode.SUCCESS.getMessage();
        r.data = data;
        return r;
    }

    public static <T> R<T> ok() {
        return ok(null);
    }

    public static <T> R<T> error(int code, String message) {
        R<T> r = new R<>();
        r.code = code;
        r.message = message;
        r.data = null;
        return r;
    }

    public static <T> R<T> error(ResultCode resultCode) {
        return error(resultCode.getCode(), resultCode.getMessage());
    }
}
