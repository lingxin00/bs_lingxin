package org.example.bs_lingxin.common;

import lombok.Data;

/**
 * 全局统一返回结果封装
 */
@Data
public class Result<T> {
    private Integer code;
    private String msg;
    private T data;

    public static <T> Result<T> success(){
        Result<T> r = new Result<>();
        r.setCode(200);
        r.setMsg("操作成功");
        return r;
    }

    public static <T> Result<T> success(T data){
        Result<T> r = new Result<>();
        r.setCode(200);
        r.setMsg("操作成功");
        r.setData(data);
        return r;
    }

    public static <T> Result<T> fail(){
        Result<T> r = new Result<>();
        r.setCode(500);
        r.setMsg("操作失败");
        return r;
    }

    public static <T> Result<T> fail(Integer code,String msg){
        Result<T> r = new Result<>();
        r.setCode(code);
        r.setMsg(msg);
        return r;
    }
}
