package com.example.emoswx.exception;

import lombok.Data;

/**
 * @author ZZy
 * @date 2022/1/7 17:10
 * @description
 */

@Data
public class EmosException extends RuntimeException{
    private int code = 200;
    private String msg;

    public EmosException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public EmosException(String msg, Throwable e) {
        super(msg, e);
        this.msg = msg;
    }

    public EmosException(String msg, int code) {
        super(msg);
        this.msg = msg;
        this.code = code;
    }

    public EmosException( int code, String msg,Throwable e) {
        super(msg,e);
        this.code = code;
        this.msg = msg;
    }
}
