package com.technical.domain.fileprocessing;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface ProcessFileFacade {
    File processFile(MultipartFile file);
}
