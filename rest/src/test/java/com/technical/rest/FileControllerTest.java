package com.technical.rest;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.technical.domain.fileprocessing.ProcessFileFacade;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.wiremock.spring.EnableWireMock;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = TestConfig.class,
        properties = {
                "ip.api.base.url=http://localhost:${wiremock.server.port}",
                "security.blocked.countries=CN,ES",
                "security.blocked.isps=AWS,GCP,Azure"
        })
@EnableWireMock
class FileControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ProcessFileFacade processFileFacade;

    @TempDir
    Path tempDir;

    @Test
    void uploadFile_ShouldReturnProcessedFileWhenIpIsAllowed() throws IOException {
        stubFor(WireMock.get(urlPathMatching("/*."))
                .withQueryParam("fields", WireMock.equalTo("isp,countryCode"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"countryCode\": \"UK\", \"isp\": \"GoodISP\"}")));

        File inputFile = tempDir.resolve("EntryFile.txt").toFile();
        Files.write(inputFile.toPath(), ("18148426-89e1-11ee-b9d1-0242ac120002|1X1D14|John Smith|Likes Apricots|Rides A Bike|6.2|12.1" +
                "\n" +
                "3ce2d17b-e66a-4c1e-bca3-40eb1c9222c7|2X2D24|Mike Smith|Likes Grape|Drives an SUV|35.0|95.5" +
                "\n" +
                "1afb6f5d-a7c2-4311-a92d-974f3180ff5e|3X3D35|Jenny Walters|Likes Avocados|Rides A Scooter|8.5|15.3"
        ).getBytes());

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(inputFile));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<byte[]> response = restTemplate.postForEntity("/file", requestEntity, byte[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        String actualResponse = new String(response.getBody());
        String expectedResponse = "[{\"name\":\"John Smith\",\"transport\":\"Rides A Bike\",\"topSpeed\":\"6.2\"}" +
                ",{\"name\":\"Mike Smith\",\"transport\":\"Drives an SUV\",\"topSpeed\":\"35.0\"}," +
                "{\"name\":\"Jenny Walters\",\"transport\":\"Rides A Scooter\",\"topSpeed\":\"8.5\"}]";

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void uploadFile_ShouldReturnForbiddenWhenCountryIsBlocked() throws IOException {
        stubFor(WireMock.get(urlPathMatching("/.*"))
                .withQueryParam("fields", WireMock.equalTo("isp,countryCode"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"countryCode\": \"ES\", \"isp\": \"GoodISP\"}")));

        File inputFile = tempDir.resolve("test-input-blocked-country.txt").toFile();
        Files.write(inputFile.toPath(), "valid content".getBytes());

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(inputFile));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("/file", requestEntity, String.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void uploadFile_ShouldReturnForbiddenWhenIspIsBlocked() throws IOException {
        stubFor(WireMock.get(urlPathMatching("/*."))
                .withQueryParam("fields", WireMock.equalTo("isp,countryCode"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"countryCode\": \"UK\", \"isp\": \"AWS\"}")));

        File inputFile = tempDir.resolve("test-input-blocked-isp.txt").toFile();
        Files.write(inputFile.toPath(), "valid content".getBytes());

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(inputFile));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("/file", requestEntity, String.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }
}
