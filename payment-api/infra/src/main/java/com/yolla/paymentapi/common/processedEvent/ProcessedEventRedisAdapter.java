package com.yolla.paymentapi.common.processedEvent;

import com.yolla.paymentapi.common.exception.ProcessedEventAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProcessedEventRedisAdapter implements ProcessedEventPort {

    private final StringRedisTemplate redisTemplate;

    @Override
    public void create(List<String> aggregateIds) {
        aggregateIds.forEach(aggregateId -> {
            if (exists(aggregateId) || !add(aggregateId)) {
                throw new ProcessedEventAlreadyExistsException(aggregateId);
            }
        });
    }

    private boolean exists(String aggregateId) {
        return execute("BF.EXISTS", aggregateId);
    }

    private boolean add(String aggregateId) {
        return execute("BF.ADD", aggregateId);
    }

    private boolean execute(String command, String aggregateId) {
        Long result = redisTemplate.execute((RedisCallback<Long>) connection ->
                (Long) connection.execute(
                        command,
                        "payment-api:order-created:processed-events".getBytes(StandardCharsets.UTF_8),
                        aggregateId.getBytes(StandardCharsets.UTF_8)));

        return result == 1L;
    }
}
