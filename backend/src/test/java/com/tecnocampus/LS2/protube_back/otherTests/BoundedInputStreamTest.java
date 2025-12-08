package com.tecnocampus.LS2.protube_back.otherTests;

import org.apache.commons.io.input.BoundedInputStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BoundedInputStreamTest {

    @Test
    void read_shouldReturnBytesWithinLimit() throws IOException {
        byte[] data = "0123456789".getBytes();
        InputStream delegate = new ByteArrayInputStream(data);
        BoundedInputStream boundedStream = new BoundedInputStream(delegate, 5);

        for (int i = 0; i < 5; i++) {
            assertEquals('0' + i, boundedStream.read());
        }
        assertEquals(-1, boundedStream.read());
    }

    @Test
    void readArray_shouldReturnBytesWithinLimit() throws IOException {
        byte[] data = "0123456789ABCDEFGHIJ".getBytes();
        InputStream delegate = new ByteArrayInputStream(data);
        BoundedInputStream boundedStream = new BoundedInputStream(delegate, 10);

        byte[] buffer = new byte[20];
        int bytesRead = boundedStream.read(buffer, 0, buffer.length);

        assertEquals(10, bytesRead);
        assertEquals("0123456789", new String(buffer, 0, bytesRead));
    }

    @Test
    void readArray_multipleCalls_shouldRespectLimit() throws IOException {
        byte[] data = "0123456789ABCDEFGHIJ".getBytes();
        InputStream delegate = new ByteArrayInputStream(data);
        BoundedInputStream boundedStream = new BoundedInputStream(delegate, 15);

        byte[] buffer1 = new byte[10];
        int read1 = boundedStream.read(buffer1, 0, buffer1.length);
        assertEquals(10, read1);

        byte[] buffer2 = new byte[10];
        int read2 = boundedStream.read(buffer2, 0, buffer2.length);
        assertEquals(5, read2);

        int read3 = boundedStream.read(buffer2, 0, buffer2.length);
        assertEquals(-1, read3);
    }

    @Test
    void read_afterLimit_shouldReturnMinusOne() throws IOException {
        byte[] data = "ABC".getBytes();
        InputStream delegate = new ByteArrayInputStream(data);
        BoundedInputStream boundedStream = new BoundedInputStream(delegate, 2);

        boundedStream.read();
        boundedStream.read();
        assertEquals(-1, boundedStream.read());
    }

    @Test
    void readArray_withZeroLimit_shouldReturnMinusOne() throws IOException {
        byte[] data = "0123456789".getBytes();
        InputStream delegate = new ByteArrayInputStream(data);
        BoundedInputStream boundedStream = new BoundedInputStream(delegate, 0);

        byte[] buffer = new byte[10];
        assertEquals(-1, boundedStream.read(buffer, 0, buffer.length));
    }

    @Test
    void close_shouldCloseDelegate() throws IOException {
        InputStream mockDelegate = mock(InputStream.class);
        BoundedInputStream boundedStream = new BoundedInputStream(mockDelegate, 2);

        boundedStream.close();

        verify(mockDelegate).close();
    }
}