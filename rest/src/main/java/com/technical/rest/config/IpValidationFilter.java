package com.technical.rest.config;

import com.technical.domain.ipcheck.IpCheckService;
import com.technical.domain.ipcheck.IpLoggingService;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class IpValidationFilter implements Filter {
    private final IpCheckService ipCheckService;
    private final IpLoggingService ipLoggingService;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        String clientIp = httpRequest.getRemoteAddr();
        String requestUri = httpRequest.getRequestURI();
        Instant requestTimestamp = Instant.now();

        UUID ipLoggingId = ipLoggingService.createIpLogging(requestUri, requestTimestamp, clientIp);

        try {
            if (ipCheckService.blockIp(clientIp, ipLoggingId)) {
                HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
                log.error("Blocked IP: {}, Request URI: {}, Timestamp: {}", clientIp, requestUri, requestTimestamp);
                return;
            }

            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            ipLoggingService.finalIpLogging(ipLoggingId, ((HttpServletResponse) servletResponse).getStatus(), requestTimestamp.toEpochMilli());
        }
    }
}
