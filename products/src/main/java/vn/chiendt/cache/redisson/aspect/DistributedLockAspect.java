package vn.chiendt.cache.redisson.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import vn.chiendt.cache.redisson.annotation.DistributedLock;
import vn.chiendt.cache.redisson.service.RedisDistributedLocker;
import vn.chiendt.cache.redisson.service.RedisDistributedService;


@Aspect
@Component
@Slf4j
public class DistributedLockAspect {
    
    @Autowired
    private RedisDistributedService redisDistributedService;
    
    private final ExpressionParser parser = new SpelExpressionParser();
    
    @Around("@annotation(distributedLock)")
    public Object around(ProceedingJoinPoint joinPoint, DistributedLock distributedLock) throws Throwable {
        String lockKey = buildLockKey(joinPoint, distributedLock);
        RedisDistributedLocker locker = redisDistributedService.getDistributedLock(lockKey);
        
        boolean lockAcquired = false;
        try {
            // Try to acquire the lock
            lockAcquired = locker.tryLock(
                distributedLock.waitTime(),
                distributedLock.leaseTime(),
                distributedLock.timeUnit()
            );
            
            if (!lockAcquired) {
                log.warn("Failed to acquire distributed lock: {}", lockKey);
                if (distributedLock.throwOnFailure()) {
                    throw new RuntimeException(distributedLock.errorMessage());
                }
                return null;
            }
            
            log.debug("Acquired distributed lock: {}", lockKey);
            
            // Execute the method
            return joinPoint.proceed();
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Interrupted while waiting for distributed lock: {}", lockKey, e);
            if (distributedLock.throwOnFailure()) {
                throw new RuntimeException("Interrupted while waiting for distributed lock", e);
            }
            return null;
        } catch (Exception e) {
            log.error("Error during distributed lock execution: {}", lockKey, e);
            throw e;
        } finally {
            if (lockAcquired) {
                try {
                    locker.unlock();
                    log.debug("Released distributed lock: {}", lockKey);
                } catch (Exception e) {
                    log.error("Error releasing distributed lock: {}", lockKey, e);
                }
            }
        }
    }
    
    private String buildLockKey(ProceedingJoinPoint joinPoint, DistributedLock distributedLock) {
        String keyExpression = distributedLock.key();
        String prefix = distributedLock.prefix();
        
        // If the key doesn't contain SpEL expressions, return as is
        if (!keyExpression.contains("#")) {
            return prefix + keyExpression;
        }
        
        try {
            // Parse SpEL expression
            Expression expression = parser.parseExpression(keyExpression);
            EvaluationContext context = new StandardEvaluationContext();
            
            // Add method parameters to context
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            String[] paramNames = signature.getParameterNames();
            Object[] args = joinPoint.getArgs();
            
            for (int i = 0; i < paramNames.length; i++) {
                context.setVariable(paramNames[i], args[i]);
            }
            
            // Evaluate the expression
            Object result = expression.getValue(context);
            return prefix + (result != null ? result.toString() : keyExpression);
            
        } catch (Exception e) {
            log.warn("Failed to parse SpEL expression: {}, using original key", keyExpression, e);
            return prefix + keyExpression;
        }
    }
}
