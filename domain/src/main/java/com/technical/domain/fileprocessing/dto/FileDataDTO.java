package com.technical.domain.fileprocessing.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class FileDataDTO {
    private final String name;
    private final String transport;
    private final String topSpeed;
}
