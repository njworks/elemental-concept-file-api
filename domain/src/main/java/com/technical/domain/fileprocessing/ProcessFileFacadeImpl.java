package com.technical.domain.fileprocessing;

import com.technical.domain.fileprocessing.dto.FileDataDTO;
import com.technical.domain.fileprocessing.validation.ValidateFile;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@AllArgsConstructor
@Service
public class ProcessFileFacadeImpl implements ProcessFileFacade {
    private final ValidateFile validateFile;
    private final ReadFileService readFileService;
    private final TransformToJSONFileService transformToJSONFileService;

    @Override
    public File processFile(MultipartFile file) {
        validateFile.validateFile(file);

        List<FileDataDTO> fileDataDTOS = readFileService.readFile(file);

        return transformToJSONFileService.transformToJSONFile(fileDataDTOS);
    }
}
