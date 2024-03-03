package com.yolla.paymentapi.common.lock;

public interface LockPort {

    void lock(String aggregateId);

    void unlock(String aggregateId);

}
