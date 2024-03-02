package com.yolla.paymentapi.common.processedEvent;

import java.util.List;

public interface ProcessedEventPort {

    void create(List<String> aggregateIds);
}
