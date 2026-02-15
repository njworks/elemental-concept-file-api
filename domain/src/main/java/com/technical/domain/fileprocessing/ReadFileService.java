package com.technical.domain.fileprocessing;

import com.technical.domain.fileprocessing.dto.FileDataDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class ReadFileService {
    public List<FileDataDTO> readFile(MultipartFile file) {
        List<FileDataDTO> fileDataList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || !line.contains("|")) {
                    continue;
                }

                String[] columns = line.split("\\|");

                if (columns.length >= 6) {
                    fileDataList.add(FileDataDTO.builder()
                            .name(columns[2].trim())
                            .transport(columns[4].trim())
                            .topSpeed(columns[5].trim())
                            .build());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return fileDataList;
    }
}
