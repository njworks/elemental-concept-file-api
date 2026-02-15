package com.technical.domain.ipcheck;

import java.util.UUID;

public interface IpCheckService {
    boolean blockIp(String clientIp, UUID ipLoggingId);
}
