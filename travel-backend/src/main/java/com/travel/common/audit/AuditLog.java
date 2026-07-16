package com.travel.common.audit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 操作审计注解，打在 Service 方法上自动记录操作日志。
 * resourceId / resourceName / detail 支持 SpEL 表达式。
 * <pre>
 *   @AuditLog(module = "DESTINATION", action = "CREATE",
 *             resourceId = "#result.id", resourceName = "#request.name")
 * </pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuditLog {

    /** 模块：AUTH / DESTINATION / FILE */
    String module();

    /** 操作：LOGIN / CREATE / UPDATE / DELETE / UPLOAD / DELETE_FILE */
    String action();

    /** 资源 ID，SpEL 表达式，如 "#id"、"#result.id" */
    String resourceId() default "";

    /** 资源名称，SpEL 表达式，如 "#request.name" */
    String resourceName() default "";

    /** 操作详情，SpEL 表达式，如 "#result" */
    String detail() default "";

    /** 操作人，SpEL 表达式（仅 LOGIN 需要指定，其他情况自动从 SecurityContext 获取） */
    String username() default "";
}
