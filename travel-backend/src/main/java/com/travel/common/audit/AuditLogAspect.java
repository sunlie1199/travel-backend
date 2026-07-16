package com.travel.common.audit;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditLogAspect {

    private final AuditLogService auditLogService;
    private final SpelExpressionParser expressionParser = new SpelExpressionParser();
    private final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    @Around("@annotation(auditLog)")
    public Object around(ProceedingJoinPoint pjp, AuditLog auditLog) throws Throwable {
        EvaluationContext context = buildEvaluationContext(pjp);
        String ip = getClientIp();
        String username = resolveUsername(auditLog, context);

        AuditLogEntry entry = new AuditLogEntry();
        entry.setUsername(username);
        entry.setModule(auditLog.module());
        entry.setAction(auditLog.action());
        entry.setIp(ip);

        try {
            Object result = pjp.proceed();
            context.setVariable("result", result);

            entry.setSuccess(1);
            entry.setResourceId(evaluateString(auditLog.resourceId(), context));
            entry.setResourceName(evaluateString(auditLog.resourceName(), context));
            entry.setDetail(evaluateObject(auditLog.detail(), context));
            auditLogService.logAsync(entry);

            return result;
        } catch (Throwable t) {
            entry.setSuccess(0);
            entry.setErrorMsg(truncate(t.getMessage(), 500));
            entry.setResourceId(evaluateString(auditLog.resourceId(), context));
            entry.setResourceName(evaluateString(auditLog.resourceName(), context));
            auditLogService.logAsync(entry);

            throw t;
        }
    }

    private EvaluationContext buildEvaluationContext(ProceedingJoinPoint pjp) {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        String[] paramNames = parameterNameDiscoverer.getParameterNames(method);
        Object[] paramValues = pjp.getArgs();

        StandardEvaluationContext context = new StandardEvaluationContext();
        if (paramNames != null) {
            for (int i = 0; i < paramNames.length; i++) {
                context.setVariable(paramNames[i], paramValues[i]);
            }
        }
        return context;
    }

    private String resolveUsername(AuditLog auditLog, EvaluationContext context) {
        if (!auditLog.username().isEmpty()) {
            String username = evaluateString(auditLog.username(), context);
            if (username != null && !username.isEmpty()) {
                return username;
            }
        }
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getName())) {
            return authentication.getName();
        }
        return "unknown";
    }

    private String evaluateString(String expression, EvaluationContext context) {
        if (expression == null || expression.isEmpty()) {
            return null;
        }
        try {
            return expressionParser.parseExpression(expression).getValue(context, String.class);
        } catch (Exception e) {
            return null;
        }
    }

    private Object evaluateObject(String expression, EvaluationContext context) {
        if (expression == null || expression.isEmpty()) {
            return null;
        }
        try {
            return expressionParser.parseExpression(expression).getValue(context, Object.class);
        } catch (Exception e) {
            return null;
        }
    }

    private String getClientIp() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        HttpServletRequest request = attributes.getRequest();
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    private String truncate(String s, int maxLen) {
        if (s == null) {
            return null;
        }
        return s.length() <= maxLen ? s : s.substring(0, maxLen);
    }
}
