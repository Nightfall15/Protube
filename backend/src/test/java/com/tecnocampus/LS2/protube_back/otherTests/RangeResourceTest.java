package com.tecnocampus.LS2.protube_back.otherTests;

import com.tecnocampus.LS2.protube_back.models.RangeResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class RangeResourceTest {

    @TempDir
    Path tempDir;

    private File testFile;
    private Resource baseResource;
    private static final String TEST_CONTENT = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    @BeforeEach
    void setUp() throws IOException {
        testFile = tempDir.resolve("test.txt").toFile();
        Files.writeString(testFile.toPath(), TEST_CONTENT);
        baseResource = new FileSystemResource(testFile);
    }

    @Test
    void getInputStream_shouldReturnCorrectRange() throws IOException {
        RangeResource rangeResource = new RangeResource(baseResource, 10, 10);

        try (InputStream is = rangeResource.getInputStream()) {
            String content = new String(is.readAllBytes());
            assertEquals("ABCDEFGHIJ", content);
        }
    }

    @Test
    void getInputStream_fromStart_shouldReturnCorrectRange() throws IOException {
        RangeResource rangeResource = new RangeResource(baseResource, 0, 5);

        try (InputStream is = rangeResource.getInputStream()) {
            String content = new String(is.readAllBytes());
            assertEquals("01234", content);
        }
    }

    @Test
    void getInputStream_toEnd_shouldReturnCorrectRange() throws IOException {
        long start = 30;
        long length = TEST_CONTENT.length() - start;
        RangeResource rangeResource = new RangeResource(baseResource, start, length);

        try (InputStream is = rangeResource.getInputStream()) {
            String content = new String(is.readAllBytes());
            assertEquals("UVWXYZ", content);
        }
    }

    @Test
    void getInputStream_fullContent_shouldReturnAllContent() throws IOException {
        RangeResource rangeResource = new RangeResource(baseResource, 0, TEST_CONTENT.length());

        try (InputStream is = rangeResource.getInputStream()) {
            String content = new String(is.readAllBytes());
            assertEquals(TEST_CONTENT, content);
        }
    }

    @Test
    void exists_shouldReturnTrue() {
        RangeResource rangeResource = new RangeResource(baseResource, 0, 10);
        assertTrue(rangeResource.exists());
    }

    @Test
    void contentLength_shouldReturnRangeLength() throws IOException {
        RangeResource rangeResource = new RangeResource(baseResource, 5, 15);
        assertEquals(15, rangeResource.contentLength());
    }

    @Test
    void getURL_shouldReturnBaseResourceURL() throws IOException {
        RangeResource rangeResource = new RangeResource(baseResource, 0, 10);
        assertEquals(baseResource.getURL(), rangeResource.getURL());
    }

    @Test
    void getURI_shouldReturnBaseResourceURI() throws IOException {
        RangeResource rangeResource = new RangeResource(baseResource, 0, 10);
        assertEquals(baseResource.getURI(), rangeResource.getURI());
    }

    @Test
    void getFile_shouldReturnBaseResourceFile() throws IOException {
        RangeResource rangeResource = new RangeResource(baseResource, 0, 10);
        assertEquals(baseResource.getFile(), rangeResource.getFile());
    }

    @Test
    void lastModified_shouldReturnBaseResourceLastModified() throws IOException {
        RangeResource rangeResource = new RangeResource(baseResource, 0, 10);
        assertEquals(baseResource.lastModified(), rangeResource.lastModified());
    }

    @Test
    void getFilename_shouldReturnBaseResourceFilename() {
        RangeResource rangeResource = new RangeResource(baseResource, 0, 10);
        assertEquals(baseResource.getFilename(), rangeResource.getFilename());
    }

    @Test
    void getDescription_shouldReturnBaseResourceDescription() {
        RangeResource rangeResource = new RangeResource(baseResource, 0, 10);
        assertEquals(baseResource.getDescription(), rangeResource.getDescription());
    }

    @Test
    void createRelative_shouldDelegateToBaseResource() throws IOException {
        RangeResource rangeResource = new RangeResource(baseResource, 0, 10);
        Resource relative = rangeResource.createRelative("relative.txt");

        assertNotNull(relative);
        assertEquals(baseResource.createRelative("relative.txt").getFilename(),
                relative.getFilename());
    }
}