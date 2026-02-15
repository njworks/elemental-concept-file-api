package com.technical.domain.fileprocessing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.technical.domain.fileprocessing.dto.FileDataDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;

@AllArgsConstructor
@Component
public class TransformToJSONFileService {
    private final ObjectMapper objectMapper;

    public File transformToJSONFile(List<FileDataDTO> fileDataDTOS) {
        File json = new File("output.json");
        try {
            objectMapper.writeValue(json, fileDataDTOS);
            return json;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
