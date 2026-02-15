package com.technical.domain.ipcheck;

import com.technical.database.entity.IpLoggingDAO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
@Service
public class IpLoggingServiceImpl implements IpLoggingService {
    private final IpLoggingDAO ipLoggingDAO;

    @Override
    public UUID createIpLogging(String requestUri, Instant requestTimestamp, String ip) {
        log.info("createIpLogging");
        return ipLoggingDAO.createInitialIpLogging(requestUri, requestTimestamp, ip);
    }

    @Override
    public void finalIpLogging(UUID id, Integer status, Long requestDuration) {
        log.info("finalIpLogging");
        ipLoggingDAO.updateFinalIpLogging(id, status, requestDuration);
    }
}
