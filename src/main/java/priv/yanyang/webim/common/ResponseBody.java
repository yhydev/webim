/*
package priv.yanyang.webim.common;

public class ResponseBody<T> {

    private String msg;
    private T data;
    private Integer status;


    public static<T> ResponseBody<T> success(T data){
        ResponseBody<T> body = new ResponseBody<T>();
        body.setData(data);
        body.setStatus(1);
        return body;
    }

    public static<T> ResponseBody<T> error(String msg){
        ResponseBody<T> body = new ResponseBody<T>();
        body.setMsg(msg);
        body.setStatus(0);
        return body;
    }

    protected void setMsg(String msg) {
        this.msg = msg;
    }

    protected void setData(T data) {
        this.data = data;
    }

    protected void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

    public Integer getStatus() {
        return status;
    }
}
*/
