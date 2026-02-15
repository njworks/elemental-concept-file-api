package com.technical.database.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IpLoggingDAOTest {
    @Mock
    private IpLoggingRepository ipLoggingRepository;

    private IpLoggingDAO ipLoggingDAO;

    @BeforeEach
    void setup() {
        ipLoggingDAO = new IpLoggingDAO(ipLoggingRepository);
    }

    @Test
    void createInitialIpLogging_shouldReturnsGeneratedId() {
        String uri = "/test";
        Instant timestamp = Instant.now();
        String ipAddress = "127.0.0.1";
        UUID generatedId = UUID.randomUUID();

        IpLoggingEntity mockEntity = IpLoggingEntity.builder()
                .id(generatedId)
                .requestUri(uri)
                .requestTimestamp(timestamp)
                .requestIpAddress(ipAddress)
                .build();

        when(ipLoggingRepository.save(any(IpLoggingEntity.class))).thenReturn(mockEntity);

        UUID result = ipLoggingDAO.createInitialIpLogging(uri, timestamp, ipAddress);

        assertEquals(generatedId, result);
        verify(ipLoggingRepository, times(1)).save(any(IpLoggingEntity.class));
    }

    @Test
    void updateIpLoggingWithIpDetails_shouldUpdatesEntityWhenFound() {
        UUID id = UUID.randomUUID();
        String countryCode = "UK";
        String ipProvider = "Test ISP";

        IpLoggingEntity mockEntity = new IpLoggingEntity();
        when(ipLoggingRepository.findById(id)).thenReturn(Optional.of(mockEntity));

        ipLoggingDAO.updateIpLoggingWithIpDetails(id, countryCode, ipProvider);

        assertEquals(countryCode, mockEntity.getRequestCountryCode());
        assertEquals(ipProvider, mockEntity.getRequestIpProvider());
        verify(ipLoggingRepository, times(1)).save(mockEntity);
    }

    @Test
    void updateIpLoggingWithIpDetails_ShouldDoNothingWhenEntityNotFound() {
        UUID id = UUID.randomUUID();
        when(ipLoggingRepository.findById(id)).thenReturn(Optional.empty());

        ipLoggingDAO.updateIpLoggingWithIpDetails(id, "UK", "Test ISP");

        verify(ipLoggingRepository, never()).save(any(IpLoggingEntity.class));
    }

    @Test
    void updateFinalIpLogging_ShouldUpdateEntityWhenFound() {
        UUID id = UUID.randomUUID();
        Integer status = 200;
        Long timeTakenMs = 500L;

        IpLoggingEntity mockEntity = new IpLoggingEntity();
        mockEntity.setRequestTimestamp(Instant.now());
        when(ipLoggingRepository.findById(id)).thenReturn(Optional.of(mockEntity));

        ipLoggingDAO.updateFinalIpLogging(id, status, timeTakenMs);

        assertEquals(status, mockEntity.getResponseStatus());
        assertNotNull(mockEntity.getTimeTakenMs());
        verify(ipLoggingRepository, times(1)).save(mockEntity);
    }

    @Test
    void updateFinalIpLogging_shouldDoNothingWhenEntityNotFound() {
        UUID id = UUID.randomUUID();
        when(ipLoggingRepository.findById(id)).thenReturn(Optional.empty());

        ipLoggingDAO.updateFinalIpLogging(id, 200, 500L);

        verify(ipLoggingRepository, never()).save(any(IpLoggingEntity.class));
    }

}
