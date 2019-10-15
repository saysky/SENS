package com.liuyanzhao.sens.exception;

/**
 * @author 言曌
 * @date 2019-08-09 16:47
 */

public class SensBusinessException extends RuntimeException {

    private Integer code;

    private String message;


    public SensBusinessException() {
        super();
    }

    public SensBusinessException(String message) {
        this.code = 500;
        this.message = message;
    }

    public SensBusinessException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
