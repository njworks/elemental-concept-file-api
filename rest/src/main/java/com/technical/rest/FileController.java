package com.technical.rest;

import com.technical.domain.fileprocessing.ProcessFileFacade;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/file")
public class FileController {
    private final ProcessFileFacade processFileFacade;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Resource> uploadFile(@RequestPart MultipartFile file) {
        log.info("Received file: {}, ContentType: {}", file.getOriginalFilename(), file.getContentType());

        File processedFile = processFileFacade.processFile(file);

        Resource resource = new FileSystemResource(processedFile);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + processedFile.getName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(processedFile.length())
                .body(resource);
    }
}
