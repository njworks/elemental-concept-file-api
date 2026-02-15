package com.technical.domain.fileprocessing.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
public class ValidateFile {
    @Value("${file.validation.bypass:false}")
    private boolean bypassFileValidation;

    private static final String ALLOWED_CONTENT_TYPE = "text/plain";

    public void validateFile(MultipartFile file) {
        if (!bypassFileValidation) {
            log.info("Received file contentType: {}", file.getContentType());

            if (!ALLOWED_CONTENT_TYPE.equals(file.getContentType())) {
                throw new IllegalArgumentException("Invalid file type. Only text files are allowed.");
            }
        }
    }
}
