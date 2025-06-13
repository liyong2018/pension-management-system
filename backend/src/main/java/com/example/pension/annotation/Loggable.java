package com.example.pension.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Loggable {
    String operationType() default ""; // 操作类型，例如：查询、新增、修改、删除、登录、登出等
    String module() default "";        // 操作模块，例如：用户管理、角色管理、登录认证等
    String description() default "";   // 操作描述，可以为空，如果为空，可以尝试从方法签名或参数动态生成
} 