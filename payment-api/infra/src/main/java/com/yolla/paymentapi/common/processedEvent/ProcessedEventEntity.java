package com.yolla.paymentapi.common.processedEvent;

import com.yolla.paymentapi.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;

@Getter
@Setter
@Entity
@Table(name = "processed_events")
@Where(clause = "status <> -1")
public class ProcessedEventEntity extends BaseEntity {

    String aggregateId;
}
