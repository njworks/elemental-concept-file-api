package com.technical.client;

import com.technical.client.dto.IpApiResponse;

public interface IpApiClient {
    IpApiResponse getIpInfo(String ip);
}
