package com.technical.database.entity;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@Service
public class IpLoggingDAO {
    private final IpLoggingRepository ipLoggingRepository;

    public UUID createInitialIpLogging(String uri, Instant timestamp, String ipAddress) {
        IpLoggingEntity entity = IpLoggingEntity.builder()
                .requestUri(uri)
                .requestTimestamp(timestamp)
                .requestIpAddress(ipAddress)
                .build();

        return ipLoggingRepository.save(entity).getId();
    }

    public void updateIpLoggingWithIpDetails(UUID id, String countryCode, String ipProvider) {
        ipLoggingRepository.findById(id)
                .ifPresent(entity ->
                {
                    entity.setRequestCountryCode(countryCode);
                    entity.setRequestIpProvider(ipProvider);
                    ipLoggingRepository.save(entity);
                });
    }

    public void updateFinalIpLogging(UUID id, Integer status, Long timeTakenMs) {
        ipLoggingRepository.findById(id)
                .ifPresent(entity ->
                {
                    entity.setResponseStatus(status);
                    entity.setTimeTakenMs(entity.getRequestTimestamp().plusMillis(timeTakenMs).toEpochMilli());
                    ipLoggingRepository.save(entity);
                });
    }
}
