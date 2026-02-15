package com.technical.domain.ipcheck;

import com.technical.client.IpApiClient;
import com.technical.client.dto.IpApiResponse;
import com.technical.client.exception.IpApiRateLimiterException;
import com.technical.database.entity.IpLoggingDAO;
import com.technical.domain.exception.RateLimiterException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class IpCheckServiceImpl implements IpCheckService {
    @Value("${security.blocked.countries}")
    private List<String> blockedCountries;

    @Value("${security.blocked.isps}")
    private List<String> blockedIsps;

    private final IpApiClient ipApiClient;
    private final IpLoggingDAO ipLoggingDAO;

    @Override
    public boolean blockIp(String ip, UUID ipLoggingId) {
        if (ip == null || ip.isBlank()) {
            return true;
        }

        try {
            IpApiResponse ipInfo = ipApiClient.getIpInfo(ip);
            if (ipInfo == null) {
                return true;
            }
            if (ipInfo.countryCode() != null && ipInfo.isp() != null) {
                log.info("IP: {}, Country: {}, ISP: {}", ip, ipInfo.countryCode(), ipInfo.isp());
                ipLoggingDAO.updateIpLoggingWithIpDetails(ipLoggingId, ipInfo.countryCode(), ipInfo.isp());
            }

            log.info("Blocked Countries: {}, Blocked ISPs: {}", blockedCountries, blockedIsps);
            log.info("Blocked Countries: {}, Blocked ISPs: {}", blockedCountries.contains(ipInfo.countryCode()), blockedIsps.contains(ipInfo.isp()));
            return blockedCountries.contains(ipInfo.countryCode()) || blockedIsps.contains(ipInfo.isp());
        } catch (IpApiRateLimiterException e) {
            throw new RateLimiterException(e.getMessage());
        }
    }
}
