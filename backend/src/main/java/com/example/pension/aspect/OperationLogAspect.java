package com.example.pension.aspect;

import com.example.pension.annotation.Loggable;
import com.example.pension.model.User; // 假设 User 是UserDetails的一种实现或可以从中获取相关信息
import com.example.pension.service.OperationLogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class OperationLogAspect {

    private final OperationLogService operationLogService;
    private final ObjectMapper objectMapper; // 用于序列化参数和结果

    @Pointcut("@annotation(com.example.pension.annotation.Loggable)")
    public void loggableMethods() {
    }

    @Around("loggableMethods()")
    public Object logOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = null;
        Throwable throwable = null;
        String errorMessage = null;
        String errorStack = null;

        // 获取 Loggable 注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Loggable loggableAnnotation = method.getAnnotation(Loggable.class);

        String operationType = loggableAnnotation.operationType();
        String module = loggableAnnotation.module();
        String description = loggableAnnotation.description();

        if (description == null || description.isEmpty()) {
            description = signature.getName(); // 默认使用方法名作为描述
        }

        try {
            result = joinPoint.proceed(); // 执行目标方法
            return result;
        } catch (Throwable t) {
            throwable = t;
            errorMessage = t.getMessage();
            // errorStack = Arrays.toString(t.getStackTrace()); // 完整堆栈可能太长
            log.error("Exception in logged method {}: {}", signature.getName(), t.getMessage());
            throw t; // 必须重新抛出异常，否则业务流程会认为异常被处理了
        } finally {
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;

            // 获取当前认证用户信息
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = "anonymousUser";
            Long userId = null;

            if (authentication != null && authentication.isAuthenticated() && !(authentication.getPrincipal() instanceof String)) {
                Object principal = authentication.getPrincipal();
                if (principal instanceof UserDetails) {
                    username = ((UserDetails) principal).getUsername();
                    // 如果 UserDetails 实现类有 getId() 方法，可以尝试获取
                    // 例如，如果 principal 是自定义的 User 对象
                    if (principal instanceof com.example.pension.model.User) { // 假设 User model 有getId
                         userId = ((com.example.pension.model.User)principal).getId();
                    } else if (principal instanceof com.example.pension.model.SystemUser) { // 或者 SystemUser
                         userId = ((com.example.pension.model.SystemUser)principal).getId();
                    }
                } else {
                     username = principal.toString();
                }
            }
            
            // 获取 HTTP 请求信息
            HttpServletRequest request = null;
            String ipAddress = "unknown";
            String userAgent = "unknown";
            String requestUrl = "";
            String requestMethod = "";

            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                request = attributes.getRequest();
                ipAddress = getClientIp(request);
                userAgent = request.getHeader("User-Agent");
                requestUrl = request.getRequestURI();
                requestMethod = request.getMethod();
            }

            // 获取请求参数
            String requestParamsString = "{\"error\":\"Failed to serialize request parameters\"}";
            try {
                Object[] args = joinPoint.getArgs();
                if (args != null && args.length > 0) {
                    // 过滤掉 HttpServletRequest, HttpServletResponse 等不可序列化的参数
                    Object[] serializableArgs = Arrays.stream(args)
                        .filter(arg -> !(arg instanceof HttpServletRequest) && !(arg instanceof HttpServletResponse) && !(arg instanceof Authentication))
                        .toArray();
                    if (serializableArgs.length > 0) {
                         // 对于GET请求，参数可能已在URL中，对于POST/PUT，参数在请求体中
                         // joinPoint.getArgs() 获取的是方法参数的实际值
                        Map<String, Object> paramsMap = new HashMap<>();
                        String[] paramNames = signature.getParameterNames();
                        for (int i = 0; i < serializableArgs.length; i++) {
                            // 确保参数名数组和可序列化参数数组长度一致或做相应调整
                            // 这是一个简化的处理，实际中可能需要更复杂的参数名匹配
                            if (i < paramNames.length) {
                                paramsMap.put(paramNames[i], serializableArgs[i]);
                            } else {
                                paramsMap.put("arg" + i, serializableArgs[i]);
                            }
                        }
                        requestParamsString = objectMapper.writeValueAsString(paramsMap);
                    }
                }
            } catch (Exception e) {
                log.warn("Failed to serialize request parameters for logging: {}", e.getMessage());
                requestParamsString = "{\"error\":\"Failed to serialize request parameters\"}";
            }

            // 获取响应数据
            String responseDataString = "{\"error\":\"Failed to serialize response data\"}";
            if (throwable == null && result != null) {
                try {
                    // 避免序列化 ResponseEntity 本身，而是其 body
                    // Object responseToLog = (result instanceof org.springframework.http.ResponseEntity) ? ((org.springframework.http.ResponseEntity<?>) result).getBody() : result;
                    // responseDataString = objectMapper.writeValueAsString(responseToLog);
                    // 限制响应数据长度
                     String tempResponseData = objectMapper.writeValueAsString(result);
                     responseDataString = tempResponseData.length() > 1000 ? tempResponseData.substring(0, 1000) + "..." : tempResponseData;
                } catch (Exception e) {
                    log.warn("Failed to serialize response data for logging: {}", e.getMessage());
                    responseDataString = "{\"error\":\"Failed to serialize response data\"}";
                }
            }

            String logLevel = (throwable == null) ? "INFO" : "ERROR";

            try {
                 operationLogService.createLog(
                    username,
                    userId,
                    operationType,
                    description, // 使用注解中或方法名生成的描述
                    module,
                    logLevel,
                    ipAddress,
                    userAgent,
                    requestUrl,
                    requestMethod,
                    requestParamsString, // 请求参数的JSON字符串
                    responseDataString,  // 响应数据的JSON字符串
                    (int) executionTime, // 执行时间 (ms)
                    errorMessage,        // 错误消息
                    errorStack           // 错误堆栈 (简化或移除)
                );
            } catch (Exception e) {
                log.error("Failed to save operation log: {}", e.getMessage(), e);
                // AOP中的日志记录失败不应影响主业务
            }
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String remoteAddr = "";
        if (request != null) {
            remoteAddr = request.getHeader("X-Forwarded-For");
            if (remoteAddr == null || remoteAddr.isEmpty() || "unknown".equalsIgnoreCase(remoteAddr)) {
                remoteAddr = request.getHeader("Proxy-Client-IP");
            }
            if (remoteAddr == null || remoteAddr.isEmpty() || "unknown".equalsIgnoreCase(remoteAddr)) {
                remoteAddr = request.getHeader("WL-Proxy-Client-IP");
            }
            if (remoteAddr == null || remoteAddr.isEmpty() || "unknown".equalsIgnoreCase(remoteAddr)) {
                remoteAddr = request.getHeader("HTTP_CLIENT_IP");
            }
            if (remoteAddr == null || remoteAddr.isEmpty() || "unknown".equalsIgnoreCase(remoteAddr)) {
                remoteAddr = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            if (remoteAddr == null || remoteAddr.isEmpty() || "unknown".equalsIgnoreCase(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
            }
            // 对于通过多个代理的情况，X-Forwarded-For 可能包含多个IP，取第一个
            if (remoteAddr != null && remoteAddr.contains(",")) {
                remoteAddr = remoteAddr.split(",")[0];
            }
        }
        return remoteAddr;
    }
} 