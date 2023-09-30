package com.argus.service.exceptions;

public class UploadFileException extends RuntimeException {
    public UploadFileException(String s) {
        super(s);
    }

    public UploadFileException(Throwable cause) {
        super(cause);
    }

    public UploadFileException(String s, Throwable e) {
        super(s, e);
    }
}
