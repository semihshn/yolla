package com.yolla.paymentapi.common.lock;

import com.yolla.paymentapi.common.exception.ExceptionType;
import com.yolla.paymentapi.common.exception.PaymentApiBusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class LockRedisAdapter implements LockPort {

    private final StringRedisTemplate redisTemplate;
    private final Duration lockDuration = Duration.ofSeconds(30);

    @Override
    public void lock(String aggregateId) {
        Boolean isLocked;
        try {
            isLocked = redisTemplate.opsForValue().setIfAbsent(aggregateId, Boolean.TRUE.toString(), lockDuration);
        } catch (Exception e) {
            log.info("Could not lock for aggregate id {}", aggregateId);
            return;
        }

        if (Boolean.FALSE.equals(isLocked))
            throw new PaymentApiBusinessException(ExceptionType.REDIS_LOCK_EXCEPTION);

        log.info("Acquired lock for aggregate id {}", aggregateId);
    }

    @Override
    public void unlock(String aggregateId) {
        try {
            redisTemplate.delete(aggregateId);
            log.info("Released lock for aggregate id {}", aggregateId);
        } catch (Exception e) {
            log.info("Could not unlock for aggregate id {}", aggregateId);
        }
    }

}