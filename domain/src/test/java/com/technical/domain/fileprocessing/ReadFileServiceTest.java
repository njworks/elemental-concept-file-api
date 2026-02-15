package com.technical.domain.fileprocessing;

import com.technical.domain.fileprocessing.dto.FileDataDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReadFileServiceTest {

    private ReadFileService readFileService;

    @BeforeEach
    void setup() {
        readFileService = new ReadFileService();
    }

    @Test
    void readFile_ShouldParseValidFileCorrectly() throws IOException {
        MultipartFile mockFile = mock(MultipartFile.class);
        String fileContent = "1|2|Car|4|Land|200\n3|4|Bike|6|Air|150";
        when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream(fileContent.getBytes(StandardCharsets.UTF_8)));

        List<FileDataDTO> result = readFileService.readFile(mockFile);

        assertEquals(2, result.size());
        assertEquals("Car", result.getFirst().getName());
        assertEquals("Land", result.getFirst().getTransport());
        assertEquals("200", result.getFirst().getTopSpeed());
        assertEquals("Bike", result.getLast().getName());
        assertEquals("Air", result.getLast().getTransport());
        assertEquals("150", result.getLast().getTopSpeed());
    }

    @Test
    void readFile_ShouldSkipEmptyLinesAndInvalidRows() throws IOException {
        MultipartFile mockFile = mock(MultipartFile.class);
        String fileContent = "1|2|Car|4|Land|200\n\nInvalidRow\n3|4|Bike|6|Air|150";
        when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream(fileContent.getBytes(StandardCharsets.UTF_8)));

        List<FileDataDTO> result = readFileService.readFile(mockFile);

        assertEquals(2, result.size());
        assertEquals("Car", result.getFirst().getName());
        assertEquals("Bike", result.get(1).getName());
    }

    @Test
    void readFile_ShouldReturnEmptyListForEmptyFile() throws IOException {
        MultipartFile mockFile = mock(MultipartFile.class);
        String fileContent = "";
        when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream(fileContent.getBytes(StandardCharsets.UTF_8)));

        List<FileDataDTO> result = readFileService.readFile(mockFile);

        assertTrue(result.isEmpty());
    }

    @Test
    void readFile_ShouldThrowRuntimeExceptionForIOException() throws IOException {
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getInputStream()).thenThrow(new IOException("File read error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> readFileService.readFile(mockFile));

        assertEquals("java.io.IOException: File read error", exception.getMessage());
    }
}