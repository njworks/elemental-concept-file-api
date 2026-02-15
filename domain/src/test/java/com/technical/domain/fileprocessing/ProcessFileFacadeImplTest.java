package com.technical.domain.fileprocessing;

import com.technical.domain.fileprocessing.dto.FileDataDTO;
import com.technical.domain.fileprocessing.validation.ValidateFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProcessFileFacadeImplTest {

    @Mock
    private ValidateFile validateFile;

    @Mock
    private ReadFileService readFileService;

    @Mock
    private TransformToJSONFileService transformToJSONFileService;

    private ProcessFileFacadeImpl processFileFacade;

    @BeforeEach
    void setup() {
        processFileFacade = new ProcessFileFacadeImpl(validateFile, readFileService, transformToJSONFileService);
    }

    @Test
    void processFile_ShouldReturnTransformedFileWhenValidFileProvided() {
        MultipartFile mockFile = mock(MultipartFile.class);
        List<FileDataDTO> mockFileData = List.of(FileDataDTO.builder().name("data name").transport("data transport").topSpeed("data top speed").build(),
                FileDataDTO.builder().name("data2 name").transport("data2 transport").topSpeed("data2 top speed").build());
        File mockTransformedFile = new File("transformed.json");

        doNothing().when(validateFile).validateFile(mockFile);
        when(readFileService.readFile(mockFile)).thenReturn(mockFileData);
        when(transformToJSONFileService.transformToJSONFile(mockFileData)).thenReturn(mockTransformedFile);

        File result = processFileFacade.processFile(mockFile);

        assertEquals(mockTransformedFile, result);
        verify(validateFile, times(1)).validateFile(mockFile);
        verify(readFileService, times(1)).readFile(mockFile);
        verify(transformToJSONFileService, times(1)).transformToJSONFile(mockFileData);
    }

    @Test
    void processFile_ShouldThrowExceptionWhenValidationFails() {
        MultipartFile mockFile = mock(MultipartFile.class);

        doThrow(new IllegalArgumentException("Invalid file")).when(validateFile).validateFile(mockFile);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> processFileFacade.processFile(mockFile));

        assertEquals("Invalid file", exception.getMessage());
        verify(validateFile, times(1)).validateFile(mockFile);
        verifyNoInteractions(readFileService, transformToJSONFileService);
    }

    @Test
    void processFile_ShouldThrowExceptionWhenReadFileFails() {
        MultipartFile mockFile = mock(MultipartFile.class);

        doNothing().when(validateFile).validateFile(mockFile);
        when(readFileService.readFile(mockFile)).thenThrow(new RuntimeException("Read file error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> processFileFacade.processFile(mockFile));

        assertEquals("Read file error", exception.getMessage());
        verify(validateFile, times(1)).validateFile(mockFile);
        verify(readFileService, times(1)).readFile(mockFile);
        verifyNoInteractions(transformToJSONFileService);
    }

    @Test
    void processFile_ShouldThrowExceptionWhenTransformToJSONFails() {
        MultipartFile mockFile = mock(MultipartFile.class);
        List<FileDataDTO> mockFileData = List.of(FileDataDTO.builder().name("data name").transport("data transport").topSpeed("data top speed").build());

        doNothing().when(validateFile).validateFile(mockFile);
        when(readFileService.readFile(mockFile)).thenReturn(mockFileData);
        when(transformToJSONFileService.transformToJSONFile(mockFileData)).thenThrow(new RuntimeException("Transformation error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> processFileFacade.processFile(mockFile));

        assertEquals("Transformation error", exception.getMessage());
        verify(validateFile, times(1)).validateFile(mockFile);
        verify(readFileService, times(1)).readFile(mockFile);
        verify(transformToJSONFileService, times(1)).transformToJSONFile(mockFileData);
    }
}