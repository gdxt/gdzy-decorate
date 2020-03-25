package com.chungyu.miniapp.util;
import java.util.HashMap;


public class ApiResult extends HashMap<String, Object> {
    private static final long serialVersionUID = 1L;

    /**
     * 编码
     */
    private Integer code;


    /**
     * 消息
     */
    private String msg;


    /**
     * 数据
     */
    private Object result;


    public ApiResult() {

    }


    public ApiResult(Integer code, String msg) {
        this.put("code", code);
        this.put("msg", msg);
    }



    public static ApiResult failure(Integer code, String message) {
        ApiResult result = new ApiResult();
        result.setCode(code);
        result.setMsg(message);
        return result;
    }




    public Integer getCode() {
        return (Integer) this.get("code");
    }


    public void setCode(Integer code) {
        this.put("code", code);
    }


    public String getMsg() {
        return this.get("msg").toString();
    }


    public void setMsg(String msg) {
        this.put("msg", msg);
    }


    public Object getResult() {
        return this.get("result");
    }


    public void setResult(Object result) {
        this.put("result", result);
    }
}
