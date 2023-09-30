package com.argus.service;

import com.argus.entity.AppDocument;
import com.argus.entity.AppPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface FileService {
    AppDocument processDoc(Message telegramMessage);

    AppPhoto processPhoto(Message telegramlMessage);
}
