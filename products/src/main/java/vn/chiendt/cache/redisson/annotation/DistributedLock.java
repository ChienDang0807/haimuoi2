package vn.chiendt.cache.redisson.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * Annotation for distributed locking using Redisson
 * 
 * This annotation can be used on methods to automatically acquire and release
 * distributed locks during method execution.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DistributedLock {
    /**
     * The lock key. Can use SpEL expressions to reference method parameters.
     * For example: "#productId" or "#request.id"
     * 
     * @return the lock key
     */
    String key();
    
    /**
     * The lock key prefix. Will be prepended to the key.
     * 
     * @return the lock key prefix
     */
    String prefix() default "lock:";
    
    /**
     * Maximum time to wait for the lock acquisition.
     * 
     * @return wait time in milliseconds
     */
    long waitTime() default 3000;
    
    /**
     * Time to hold the lock after acquisition.
     * 
     * @return lease time in milliseconds
     */
    long leaseTime() default 10000;
    
    /**
     * Time unit for waitTime and leaseTime.
     * 
     * @return the time unit
     */
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;
    
    /**
     * Whether to throw an exception if lock acquisition fails.
     * If false, the method will return null or default value.
     * 
     * @return true if exception should be thrown on lock failure
     */
    boolean throwOnFailure() default true;
    
    /**
     * Custom error message when lock acquisition fails.
     * 
     * @return the error message
     */
    String errorMessage() default "Failed to acquire distributed lock";
}
