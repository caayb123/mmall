package com.mmall.AopHandler;

import com.mmall.annotation.ManagerAnnotation;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.RoleCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;

@Aspect
@Component
public class AuthHandler {
    @Pointcut("@annotation(com.mmall.annotation.ManagerAnnotation)")
    public void start(){}

    @Around("start()")
    public ServerResponse access(ProceedingJoinPoint joinPoint){   //ProceedingJoinPoint获取切入点方法
       User user=getUser();
       MethodSignature  joinPointSignature = (MethodSignature) joinPoint.getSignature();  //获取切入点方法签名
        Method method = joinPointSignature.getMethod();//获取切入点方法
        ManagerAnnotation annotation = method.getAnnotation(ManagerAnnotation.class); //通过反射获取注解信息
        if (annotation!=null){
            switch (annotation.value()){
                case VAlIDATE: //验证登录和权限
                 if (user==null){
                     return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
                 }
                 if (user.getRole()!= RoleCode.ROLE_ADMIN.getCode()){
                        return ServerResponse.createByErrorMessage("无权限操作");
                 }
                 break;
                 case NOAUTHORITY:  //只验证登录
                  if (user==null){
                         return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
                     }
                  break;
                 default:break;
            }

        }
        ServerResponse serverResponse=null;
        try {
            serverResponse=(ServerResponse)joinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return serverResponse;
    }
      /**
           * @Description: 从session中获取当前用户
           * @Author: cyb
           */
      private User getUser(){
          RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();  //从持有上下文的Request容器中获取线程本地变量
          ServletRequestAttributes servletRequestAttributes =(ServletRequestAttributes) requestAttributes; //类型强制转换
          HttpServletRequest request = servletRequestAttributes.getRequest();  //获取request对象
          HttpSession session = request.getSession();
          User user =(User) session.getAttribute(Const.CURRENT_USER);
          return user;
      }

}
