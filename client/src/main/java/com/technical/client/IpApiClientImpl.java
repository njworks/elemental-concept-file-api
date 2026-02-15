package com.technical.client;

import com.technical.client.dto.IpApiResponse;
import com.technical.client.exception.IpApiClientException;
import com.technical.client.exception.IpApiRateLimiterException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component
@RequiredArgsConstructor
public class IpApiClientImpl implements IpApiClient {
    private static final String ENDPOINT = "/";
    private static final String SECONDS_TILL_RESET_HEADER = "X-Ttl";
    private static final String REMAINING_REQUESTS_HEADER = "X-Rl";
    private static final String ZERO = "0";
    private static final Long BUFFER_TIME_IN_SECONDS = 5L;

    private final AtomicReference<Instant> resetTime = new AtomicReference<>();
    private final AtomicBoolean isRateLimitedBlocked = new AtomicBoolean(false);
    private final RestClient restClient;

    @Override
    public IpApiResponse getIpInfo(String ip) {
        Instant now = Instant.now();
        Instant currentResetTime = resetTime.get();

        if (isRateLimitedBlocked.get()) {
            if (now.isBefore(currentResetTime)) {
                long waitSeconds = Duration.between(now, currentResetTime).toSeconds();
                throw new IpApiRateLimiterException(
                        "Please wait for %d seconds before making another request".formatted(Math.max(1, waitSeconds))
                );
            } else {
                isRateLimitedBlocked.set(false);
            }
        }

        ResponseEntity<IpApiResponse> response = restClient.get()
                .uri(uri -> uri.path(ENDPOINT)
                        .queryParam("fields", "isp,countryCode")
                        .build(ip))
                .retrieve()
                .toEntity(IpApiResponse.class);

        log.info("Received response for IP {}: status={}, headers={}", ip, response.getStatusCode(), response.getHeaders());

        String resetSeconds = response.getHeaders().getFirst(SECONDS_TILL_RESET_HEADER);
        String remainingRequests = response.getHeaders().getFirst(REMAINING_REQUESTS_HEADER);

        if (resetSeconds != null) {
            updateRateLimitInfo(resetSeconds);
        }

        if (ZERO.equals(remainingRequests)) {
            isRateLimitedBlocked.set(true);
        }

        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        }

        throw new IpApiClientException("Failed to retrieve IP information");
    }

    private void updateRateLimitInfo(String resetSeconds) {
        try {
            long resetTimeInSeconds = Long.parseLong(resetSeconds);

            resetTime.set(Instant.now().plusSeconds(resetTimeInSeconds + BUFFER_TIME_IN_SECONDS));
        } catch (NumberFormatException e) {
            throw new IpApiRateLimiterException("Invalid rate limit reset time received from API: %s".formatted(resetSeconds), e);
        }
    }
}
