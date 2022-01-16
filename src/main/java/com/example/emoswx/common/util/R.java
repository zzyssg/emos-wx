package com.example.emoswx.common.util;

import org.apache.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ZZy
 * @date 2022/1/8 13:02
 * @description
 */
public class R extends HashMap<String, Object> {
    public R() {
        put("code", HttpStatus.SC_OK);
        put("msg", "success");
    }

    public static R error() {
        return error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "未知错误，请联系管理员!");
    }

    public static R error(String msg) {
        return error(HttpStatus.SC_INTERNAL_SERVER_ERROR, msg);
    }

    public static R error(int code,String msg) {
        R r = new R();
        r.put("code", code);
        r.put("msg", msg);
        return r;
    }

    public static R ok() {
        return new R();
    }

    /*仅更改msg*/
    public static R ok(String msg) {
        R r = new R();
        r.put("msg", msg);
        return r;
    }

    /*将web对象添加到R对象中*/
    public static R ok(Map<String,Object> res) {
        R r = new R();
        r.putAll(res);
        return r;
    }

    //链式添加额外信息
    public R put(String k,Object v) {
        //TODO
        super.put(k, v);
        return this;
    }
}
