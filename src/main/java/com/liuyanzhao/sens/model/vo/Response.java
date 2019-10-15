package com.liuyanzhao.sens.model.vo;


import java.io.Serializable;

/**
 * @author 言曌
 * @date 2018/9/2 下午8:34
 */
public class Response<T> implements Serializable {

    private Integer code = 200;

    private String message;

    private T data;


    public Response() {
    }

    public Response(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> Response<T> yes() {
        return new Response(200, "success", null);
    }

    public static <T> Response<T> yes(T data) {
        return new Response(200, "success", data);
    }

    public static <T> Response<T> yes(String message, T data) {
        return new Response(200, message, data);
    }

    public static <T> Response<T> no() {
        return new Response(500, "error", null);
    }

    public static <T> Response<T> no(String message) {
        return new Response(500, message, null);
    }

    public static <T> Response<T> no(Integer code, String message) {
        return new Response(code, message, null);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Boolean isSuccess() {
        return this.code == 200;
    }

}
