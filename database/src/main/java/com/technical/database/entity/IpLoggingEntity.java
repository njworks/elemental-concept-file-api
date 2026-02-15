package com.technical.database.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Builder
@Data
@Entity
@Table(name = "ip_logging")
@NoArgsConstructor
@AllArgsConstructor
public class IpLoggingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String requestUri;
    private Instant requestTimestamp;
    private Integer responseStatus;
    private String requestIpAddress;
    private String requestCountryCode;
    private String requestIpProvider;
    private Long timeTakenMs;
}
