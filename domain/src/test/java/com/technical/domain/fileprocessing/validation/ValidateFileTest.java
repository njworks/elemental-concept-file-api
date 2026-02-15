package com.technical.domain.fileprocessing.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidateFileTest {

    @Mock
    private MultipartFile mockFile;

    private ValidateFile validateFile;

    @BeforeEach
    void setup() {
        validateFile = new ValidateFile();
    }

    @Test
    void validateFile_ShouldAllowValidTextFileWhenValidationNotBypassed() {
        ReflectionTestUtils.setField(validateFile, "bypassFileValidation", false);
        when(mockFile.getContentType()).thenReturn("text/plain");

        assertDoesNotThrow(() -> validateFile.validateFile(mockFile));
    }

    @Test
    void validateFile_ShouldThrowExceptionForInvalidFileTypeWhenValidationNotBypassed() {
        ReflectionTestUtils.setField(validateFile, "bypassFileValidation", false);
        when(mockFile.getContentType()).thenReturn("application/json");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> validateFile.validateFile(mockFile));
        assertEquals("Invalid file type. Only text files are allowed.", exception.getMessage());
    }

    @Test
    void validateFile_ShouldNotValidateFileTypeWhenValidationBypassed() {
        ReflectionTestUtils.setField(validateFile, "bypassFileValidation", true);

        verify(mockFile, never()).getContentType();
    }
}