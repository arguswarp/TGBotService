package com.argus.service;

import com.argus.entity.AppDocument;
import com.argus.entity.AppPhoto;
import com.argus.entity.BinaryContent;
import org.springframework.core.io.FileSystemResource;

public interface FileService {
    AppDocument getDocument(String id);

    AppPhoto getPhoto(String id);

}
