package com.mmall.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;



import java.io.Serializable;

/**
      * @Description: 高复用性服务端响应变量，方法扩展
      * @Author: cyb
      * @JsonSerialize(include = @JsonInclude(JsonInclude.Include.NON_NULL)在序列化时为null的对象，key也会消失
      * @JsonIgnore 使其不在json序列化中
      */

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServerResponse<T> implements Serializable {

       private int status;  //状态
       private String msg;  //消息
       private T data;  //可扩展泛型

       private ServerResponse(int status) {
              this.status = status;
       }

       private ServerResponse(int status, T data) {    //如果传的不是String类型才会调用此构造函数
              this.status = status;
              this.data = data;
       }

       private ServerResponse(int status, String msg, T data) {
              this.status = status;
              this.msg = msg;
              this.data = data;
       }

       private ServerResponse(int status, String msg) {
              this.status = status;
              this.msg = msg;
       }
       @JsonIgnore
       public boolean isSuccess(){
               return this.status==ResponseCode.SUCCESS.getCode();
       }

       public int getStatus() {
              return status;
       }

       public String getMsg() {
              return msg;
       }

       public T getData() {
              return data;
       }

       public static <T> ServerResponse<T> createBySuccess(){
              return new ServerResponse<T>(ResponseCode.SUCCESS.getCode());
       }
       public static <T> ServerResponse<T> createBySuccessMessage(String msg){
              return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg);
       }
       public static <T> ServerResponse<T> createBySuccess(T data){
              return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),data);
       }
       public static <T> ServerResponse<T> createBySuccess(String msg,T data){
              return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg,data);
       }
       public static <T> ServerResponse<T> createByEroor(){
              return new ServerResponse<T>(ResponseCode.ERROR.getCode(),ResponseCode.ERROR.getDesc());
       }
       public static <T> ServerResponse<T> createByErrorMessage(String errorMessage){
              return new ServerResponse<T>(ResponseCode.ERROR.getCode(),errorMessage);
       }
       public static <T> ServerResponse<T> createByErrorMessage(int errorCode,String errorMessage) {
              return new ServerResponse<T>(errorCode,errorMessage);
       }
}
