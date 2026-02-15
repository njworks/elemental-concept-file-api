package com.technical.client;

import com.technical.client.dto.IpApiResponse;
import com.technical.client.exception.IpApiClientException;
import com.technical.client.exception.IpApiRateLimiterException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IpApiClientImplTest {
    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private RestClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    private IpApiClientImpl ipApiClient;

    @BeforeEach
    void setup(){
        ipApiClient = new IpApiClientImpl(restClient);
    }
    private void mockRestClientCall(ResponseEntity<IpApiResponse> responseEntity) {
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(IpApiResponse.class)).thenReturn(responseEntity);
    }


    @Test
    void getIpInfo_shouldReturnIpApiResponse_whenApiCallIsSuccessful() {
        String ip = "127.0.0.1";
        IpApiResponse mockResponse = new IpApiResponse("UK", "Test ISP");

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Ttl", "10");
        headers.add("X-Rl", "1");

        ResponseEntity<IpApiResponse> responseEntity = new ResponseEntity<>(mockResponse, headers, HttpStatus.OK);

        mockRestClientCall(responseEntity);

        IpApiResponse response = ipApiClient.getIpInfo(ip);

        assertNotNull(response);
        assertEquals("Test ISP", response.isp());
        assertEquals("UK", response.countryCode());
    }

    @Test
    void getIpInfo_shouldThrowIpApiRateLimiterException_whenRateLimitReached() {
        String ip = "127.0.0.1";
        IpApiResponse mockResponse = new IpApiResponse("UK", "Test ISP");

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Ttl", "5");
        headers.add("X-Rl", "0");

        ResponseEntity<IpApiResponse> responseEntity = new ResponseEntity<>(mockResponse, headers, HttpStatus.OK);

        mockRestClientCall(responseEntity);

        ipApiClient.getIpInfo(ip);

        IpApiRateLimiterException callAgainException = assertThrows(IpApiRateLimiterException.class, () -> ipApiClient.getIpInfo(ip));

        assertEquals("Please wait for 10 seconds before making another request", callAgainException.getMessage());
    }

    @Test
    void getIpInfo_shouldThrowIpApiClientException_whenApiReturnsErrorStatusCode() {
        String ip = "127.0.0.1";

        ResponseEntity<IpApiResponse> responseEntity = new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        mockRestClientCall(responseEntity);

        IpApiClientException exception = assertThrows(IpApiClientException.class, () -> ipApiClient.getIpInfo(ip));
        assertEquals("Failed to retrieve IP information", exception.getMessage());
    }

    @Test
    void getIpInfo_shouldHandleInvalidRateLimitHeadersGracefully() {
        String ip = "127.0.0.1";
        IpApiResponse mockResponse = new IpApiResponse("UK", "Test ISP");

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Ttl", "not-a-number");

        ResponseEntity<IpApiResponse> responseEntity = new ResponseEntity<>(mockResponse, headers, HttpStatus.OK);

        mockRestClientCall(responseEntity);

        IpApiRateLimiterException exception = assertThrows(IpApiRateLimiterException.class, () -> ipApiClient.getIpInfo(ip));
        assertEquals("Invalid rate limit reset time received from API: not-a-number", exception.getMessage());
    }
}