package com.yolla.orderingapi.common.outbox.jpa.entity;


import com.yolla.orderingapi.common.outbox.model.Outbox;
import com.yolla.orderingapi.common.outbox.model.OutboxStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity(name = "outbox_data")
@Table(name = "outbox_data")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Where(clause = "status <> -1")
@EntityListeners(AuditingEntityListener.class)
@ToString
public class OutboxDataEntity {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "idate", nullable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name = "udate")
    private LocalDateTime updatedDate;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Column(nullable = false)
    private String topic;


    @Column(nullable = false, name = "event_group")
    private String group;

    @Column(nullable = false)
    private OutboxStatus status;

    public Outbox toModel() {
        return Outbox.builder()
                .outboxId(id)
                .topic(topic)
                .group(group)
                .payload(payload)
                .build();
    }
}
