package com.tecnocampus.LS2.protube_back.models;

import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

public class RangeResource implements Resource {
    private final Resource resource;
    private final long start;
    private final long length;

    public RangeResource(Resource resource, long start, long length) {
        this.resource = resource;
        this.start = start;
        this.length = length;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        InputStream is = resource.getInputStream();
        is.skip(start);
        return new BoundedInputStream(is, length);
    }

    @Override
    public boolean exists() {
        return resource.exists();
    }

    @Override
    public URL getURL() throws IOException {
        return resource.getURL();
    }

    @Override
    public URI getURI() throws IOException {
        return resource.getURI();
    }

    @Override
    public File getFile() throws IOException {
        return resource.getFile();
    }

    @Override
    public long contentLength() throws IOException {
        return length;
    }

    @Override
    public long lastModified() throws IOException {
        return resource.lastModified();
    }

    @Override
    public Resource createRelative(String relativePath) throws IOException {
        return resource.createRelative(relativePath);
    }

    @Override
    public String getFilename() {
        return resource.getFilename();
    }

    @Override
    public String getDescription() {
        return resource.getDescription();
    }
}

// Simple bounded input stream to limit read length
class BoundedInputStream extends InputStream {
    private final InputStream delegate;
    private long remaining;

    public BoundedInputStream(InputStream delegate, long length) {
        this.delegate = delegate;
        this.remaining = length;
    }

    @Override
    public int read() throws IOException {
        if (remaining <= 0) return -1;
        int b = delegate.read();
        if (b != -1) remaining--;
        return b;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (remaining <= 0) return -1;
        int toRead = (int) Math.min(len, remaining);
        int read = delegate.read(b, off, toRead);
        if (read > 0) remaining -= read;
        return read;
    }

    @Override
    public void close() throws IOException {
        delegate.close();
    }
}
