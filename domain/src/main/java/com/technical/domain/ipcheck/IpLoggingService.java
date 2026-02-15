package com.technical.domain.ipcheck;

import java.time.Instant;
import java.util.UUID;

public interface IpLoggingService {
    UUID createIpLogging(String requestUri, Instant requestTimestamp, String ip);

    void finalIpLogging(UUID id, Integer status, Long requestDuration);
}
