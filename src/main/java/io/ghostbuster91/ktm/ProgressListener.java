package io.ghostbuster91.ktm;

public interface ProgressListener {
    void update(long bytesRead, long contentLength);
}