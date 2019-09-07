package com.mmall.annotation;

import com.mmall.common.ManagerCode;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface ManagerAnnotation {
      ManagerCode value() default ManagerCode.VAlIDATE;
}
